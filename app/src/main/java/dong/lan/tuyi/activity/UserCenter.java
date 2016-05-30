package dong.lan.tuyi.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.UserCenterAdapter;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.db.TUserDao;
import dong.lan.tuyi.utils.AES;
import dong.lan.tuyi.utils.CircleTransformation;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.LoopImagesHandler;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.xlist.XListView;

/**
 * Created by 桂栋 on 2015/7/18.
 */
public class UserCenter extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private ImageView head;
    private TextView click;
    private TextView username;
    private ImageView imageSwitcher;
    private TUser user;
    private XListView mListView;
    private List<String> tuyilist = new ArrayList<>();
    List<BmobQuery<UserTuyi>> queries;//Bmob的复合查询条件
    private UserCenterAdapter adapter;
    private static final int limit = 8;
    private int skip = 0;
    private int wheel = 0;
    private int CLICK_STATUS;
    public static final int ISFRIEND = 1;
    private static final int INVITE = 2;
    private ProgressDialog progressDialog;
    private LinearLayout headLayout;
    private LoopImagesHandler handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.user_center);
        if (getIntent().hasExtra("USER"))
            user = (TUser) getIntent().getSerializableExtra("USER");
        else
            finish();
        initView();
        if (user.getObjectId() == null || user.getObjectId().equals("")) {
            BmobQuery<TUser> query = new BmobQuery<>();
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.addWhereEqualTo("username", user.getUsername());
            query.findObjects(this, new FindListener<TUser>() {
                @Override
                public void onSuccess(List<TUser> list) {
                    if (!list.isEmpty()) {
                        user = list.get(0);
                        initDate();
                        getUserTuyi();
                    } else {
                        Show(getString(R.string.can_not_get_user_data));
                        dismiss();
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Show("获取用户数据失败 " + s);
                    dismiss();
                }
            });
        } else {
            initDate();
            getUserTuyi();
        }

        handle = new LoopImagesHandler(this,imageSwitcher,tuyilist);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (imageSwitcher != null) {
                        if (tuyilist == null || tuyilist.isEmpty()) {
                            sleep(5000);
                        } else {
                            if (wheel > tuyilist.size() - 1)
                                wheel = 0;
                            handle.sendEmptyMessage(wheel);
                            sleep(5000);
                            wheel++;
                        }
                    } else
                        sleep(2000);
                }
            }
        });
        thread.start();
    }

    private void sleep(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        head = (ImageView) findViewById(R.id.user_center_head);
        mListView = (XListView) findViewById(R.id.user_centre_list);
        click = (TextView) findViewById(R.id.user_center_buttonClick);
        username = (TextView) findViewById(R.id.user_center_username);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.pullRefreshing();
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CLICK_STATUS == 0) {
                    return;
                } else {
                    if (CLICK_STATUS == ISFRIEND) {
                        startActivity(new Intent(UserCenter.this, ChatActivity.class).putExtra("userId", user.getUsername()));
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    } else if (CLICK_STATUS == -1) {
                        finish();
                    } else if (CLICK_STATUS == INVITE) {
                        addFriends();
                    }
                }
            }
        });
        imageSwitcher = (ImageView) findViewById(R.id.wall_image_switcher);
        headLayout = (LinearLayout) findViewById(R.id.head_layout);
        showProgress("努力加载用户数据中", false);
    }

    /*
    获取用户的公开的图忆
     */
    boolean isFirst = true;
    boolean hasLoad = true;  //避免每次刷新都更新用户信息

    private void getUserTuyi() {
        skip = 0;
        BmobQuery<UserTuyi> query = new BmobQuery<>();
        query.setSkip(skip);
        query.and(queries);
        query.setLimit(limit);
        query.order("createAt");
        if (hasLoad)
            query.include("tUser");
        query.findObjects(this, new FindListener<UserTuyi>() {
            @Override
            public void onSuccess(List<UserTuyi> list) {
                dismiss();
                if (list.isEmpty()) {
                    if (isFirst) {
                        Show(user.getUsername() + " 没有公开的图忆");
                        isFirst = false;
                    }

                } else {
                    if (hasLoad && list.get(0).gettUser() != null) {
                        ContentValues values = new ContentValues();
                        values.put(TUserDao.COLUMN_IS_OPEN, list.get(0).gettUser().isPublicMyPoint() ? "1" : "0");
                        values.put(TUserDao.COLUMN_NAME_AVATAR, list.get(0).gettUser().getHead());
                        values.put(TUserDao.COLUMN_LAT, list.get(0).gettUser().getLoginPoint().getLatitude());
                        values.put(TUserDao.COLUMN_LONG, list.get(0).gettUser().getLoginPoint().getLongitude());
                        DemoDBManager.getInstance().updateTuser(list.get(0).gettUser().getUsername(), values);
                        hasLoad = false;
                    }
                    for (int i = 0; i < list.size(); i++)
                        tuyilist.add(list.get(i).gettPic());
                    if (CLICK_STATUS != -1)
                        adapter = new UserCenterAdapter(UserCenter.this, list, false);
                    else adapter = new UserCenterAdapter(UserCenter.this, list, true);
                    skip = list.size();
                    if (skip < limit)
                        mListView.setPullLoadEnable(false);
                    mListView.setAdapter(adapter);
                    mListView.setPullLoadEnable(true);
                }
                refreshPull();
            }

            @Override
            public void onError(int i, String s) {
                dismiss();
            }
        });
    }

    private void getMoreUserTuyi() {
        BmobQuery<UserTuyi> query = new BmobQuery<>();
        query.setSkip(skip);
        query.setLimit(limit);
        query.order("createAt");
        query.and(queries);
        query.findObjects(this, new FindListener<UserTuyi>() {
            @Override
            public void onSuccess(List<UserTuyi> list) {
                if (list.isEmpty()) {
                    Show("没有更多啦");
                    mListView.setPullLoadEnable(false);
                } else {
                    adapter.addAll(list);
                    mListView.setAdapter(adapter);
                    mListView.setPullLoadEnable(true);
                    mListView.setSelection(skip);

                    skip += list.size();
                    for (int i = 0; i < list.size(); i++)
                        tuyilist.add(list.get(i).gettPic());
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh() {
        getUserTuyi();
        refreshPull();
    }

    @Override
    public void onLoadMore() {
        getMoreUserTuyi();
        refreshLoad();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(UserCenter.this, TuyiInfoActivity.class).putExtra("TUYI", adapter.getList().get(position - 1)));
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
    }

    public void IntentClick(View view) {
        if (CLICK_STATUS == 0) {
            return;
        } else {
            if (CLICK_STATUS == ISFRIEND) {
                startActivity(new Intent(UserCenter.this, ChatActivity.class).putExtra("userId", user.getUsername()));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            } else if (CLICK_STATUS == -1) {
                finish();
            } else if (CLICK_STATUS == INVITE) {
                addFriends();
            }
        }
    }

    private void initDate() {

        if (Config.preferences == null)
            Config.preferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);


        username.setText(user.getUsername());
        BmobQuery<UserTuyi> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("tUser", user);
        BmobQuery<UserTuyi> q2 = new BmobQuery<>();
        q2.addWhereEqualTo("isPublic", true);

        queries = new ArrayList<>();
        queries.add(q1);
        queries.add(q2);
        if (TuApplication.getInstance().getUserName().equals(user.getUsername())) {
            click.setBackgroundResource(R.drawable.circle_red);
            click.setText(getString(R.string.back));
            CLICK_STATUS = -1;
        } else if (TuApplication.getInstance().getContactList().containsKey(AES.encode(user.getUsername()))) {
            click.setBackgroundResource(R.drawable.circle_green);
            click.setText(getString(R.string.robot_chat));
            CLICK_STATUS = ISFRIEND;
        } else {
            click.setBackgroundResource(R.drawable.new_friends_icon);
            CLICK_STATUS = INVITE;
        }


        String headStr = Config.preferences.getString(Config.USER_HEAD + user.getUsername(), "");
        PicassoHelper.load(this, headStr)
                .resize(100, 100)
                .transform(new CircleTransformation(50))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.loding_pic)
                .into(head);
    }


    AlertDialog dialog;

    public void addFriends() {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_friend_reason_dialog, null);
        final Button done = (Button) view.findViewById(R.id.add_done);
        final EditText reason = (EditText) view.findViewById(R.id.add_reason);
        reason.requestFocus();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason.getText().length() < 5) {
                    Show("朋友需要真心对待，马虎几字怎么行");
                    return;
                }
                dialog.dismiss();
                progressDialog = new ProgressDialog(UserCenter.this);
                String stri = getResources().getString(R.string.Is_sending_a_request);
                progressDialog.setMessage(stri);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String s = reason.getText().toString();
                            EMContactManager.getInstance().addContact(user.getUsername(), s);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s1 = getResources().getString(R.string.send_successful);
                                    Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                    Config.print(e.getMessage());
                                    Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        builder.setView(view);
        dialog = builder.show();

    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    int lastIndex = -1;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 4) {
            if (firstVisibleItem > 1 && firstVisibleItem != lastIndex) {
                ObjectAnimator.ofFloat(headLayout, "scaleY", 1f, 0f).setDuration(200).start();
                lastIndex = firstVisibleItem;
                ViewGroup.LayoutParams layoutParams = headLayout.getLayoutParams();
                layoutParams.height = 0;
                headLayout.setLayoutParams(layoutParams);
            }
            if (firstVisibleItem < 1 && lastIndex != firstVisibleItem) {
                ObjectAnimator.ofFloat(headLayout, "scaleY", 0f, 1f).setDuration(200).start();
                lastIndex = firstVisibleItem;
                ViewGroup.LayoutParams layoutParams = headLayout.getLayoutParams();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                headLayout.setLayoutParams(layoutParams);
            }
        }
    }


}


