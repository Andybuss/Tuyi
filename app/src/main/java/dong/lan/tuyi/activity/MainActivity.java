/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dong.lan.tuyi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;
import com.umeng.simplify.ui.fragments.CommunityMainFragment;
import com.umeng.simplify.ui.presenter.impl.LoginSuccessStrategory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import dong.lan.tuyi.BuildConfig;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.DemoHelper;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.Interested;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.db.InviteMessgeDao;
import dong.lan.tuyi.db.TUserDao;
import dong.lan.tuyi.db.UserDao;
import dong.lan.tuyi.domain.InviteMessage;
import dong.lan.tuyi.util.PhotoUtil;
import dong.lan.tuyi.utils.CircleTransformation;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.Lock;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;
import dong.lan.tuyi.utils.Weather;

public class MainActivity extends dong.lan.tuyi.basic.BaseMainActivity implements EMEventListener, View.OnClickListener, BDLocationListener {

    LocationClient mLocClient;  //定位的操作管理client
    public static String username;
    protected static final String TAG = "MainActivity";
    private TextView unreadLabel;
    private TextView unreadAddressLable;

    private ImageView head;

    private TextView net_tip;
    private TextView loc_des;
    private TextView tip;
    private LinearLayout tipLayout;
    private Button[] mTabs;
    private ContactListFragment contactListFragment;
    private ConversationListFragment conversationListFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    public boolean isConflict = false;
    private static boolean isFirstLoc = true;
    private boolean isCurrentAccountRemoved = false;

    private LocationClientOption option;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
    private Toolbar toolbar;


    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = Config.Tusername = TuApplication.getInstance().getUserName();
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            TuApplication.getInstance().logout(null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.main_slide);
        Lock.canPop = false;



        if(Build.VERSION.SDK_INT>=23 && !Settings.System.canWrite(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 601);
        }

        initView();

        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE};
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(mPermissionList,100);
        }
        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);
        // 这个fragment只显示好友和群组的聊天记录
        // 显示所有人消息记录的fragment
        contactListFragment = new ContactListFragment();
        conversationListFragment = new ConversationListFragment();
        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(this);
        mCommSDK.initSDK(this);
        CommunityMainFragment mFeedsFragment = new CommunityMainFragment();
        mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);
        CommConfig.getConfig().setLoginResultStrategy(new LoginSuccessStrategory());

        FragmentTuMap fragmentTuMap = new FragmentTuMap();
        fragments = new Fragment[]{conversationListFragment, contactListFragment, mFeedsFragment, fragmentTuMap};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentTuMap).add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(fragmentTuMap)
                .commit();
        init();
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);
        option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

        BmobUpdateAgent.update(this);
    }


    private void init() {
        initTuser();
        // 注册群组和联系人监听
        DemoHelper.getInstance().registerGroupAndContactListener();
        registerBroadcastReceiver();
    }


    private void updateCurrentUser() {
        boolean exits = DemoDBManager.getInstance().isTUserExits(Config.tUser.getUsername());
        if (exits) {
            ContentValues values = new ContentValues();
            BmobGeoPoint point = Config.tUser.getLoginPoint();
            if (point != null) {
                values.put(TUserDao.COLUMN_LAT, point.getLatitude() + "");
                values.put(TUserDao.COLUMN_LONG, point.getLongitude() + "");
            }
            values.put(TUserDao.COLUMN_NAME_AVATAR, Config.tUser.getHead());
            if (Config.tUser.isPublicMyPoint())
                values.put(TUserDao.COLUMN_IS_OPEN, "1");
            else
                values.put(TUserDao.COLUMN_IS_OPEN, "0");
            values.put(TUserDao.COLUMN_TIME, new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
            DemoDBManager.getInstance().updateTuser(Config.tUser.getUsername(), values);
        } else {
            DemoDBManager.getInstance().saveOneTuser(Config.tUser);
        }
    }

    private Toolbar.OnMenuItemClickListener itemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.footstep:
                    startActivity(new Intent(MainActivity.this, MyFootStepActivity.class).putExtra(MyFootStepActivity.INTENT_TAG, MyFootStepActivity.FROM_USER));
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
                case R.id.addFriend:
                    startActivity(new Intent(MainActivity.this, AddContactActivity.class));
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
            }
            return true;
        }
    };

    /**
     * 初始化组件
     */
    private void initView() {
        loc_des = (TextView) findViewById(R.id.user_des);
        loc_des.setOnClickListener(this);
        tip = (TextView) findViewById(R.id.tip);
        tipLayout = (LinearLayout) findViewById(R.id.tip_layout);
        if (Config.tUser == null) {
            tipLayout.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的图忆");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(itemClickListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);


        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        net_tip = (TextView) findViewById(R.id.net_tip);
        net_tip.setOnClickListener(net_error);
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_conversation);
        mTabs[1] = (Button) findViewById(R.id.btn_address_list);
        mTabs[2] = (Button) findViewById(R.id.btn_setting);
        mTabs[3] = (Button) findViewById(R.id.btn_user_main);
        mTabs[3].setSelected(true);
        currentTabIndex = 3;
        TextView user_center = (TextView) findViewById(R.id.user_center);
        findViewById(R.id.my_interested).setOnClickListener(this);
        findViewById(R.id.user_albums).setOnClickListener(this);
        TextView user_setting = (TextView) findViewById(R.id.user_setting);
        findViewById(R.id.my_Favorite).setOnClickListener(this);
        head = (ImageView) findViewById(R.id.user_head);
        TextView offlineTuyi = (TextView) findViewById(R.id.offline_tuyi);
        findViewById(R.id.toolbar_community).setOnClickListener(this);
        findViewById(R.id.toolbar_tuyi).setOnClickListener(this);
        findViewById(R.id.user_community).setOnClickListener(this);
        offlineTuyi.setOnClickListener(this);
        head.setOnClickListener(this);
        loc_des.setOnClickListener(this);
        user_setting.setOnClickListener(this);
        user_center.setOnClickListener(this);
        registerForContextMenu(mTabs[1]);
        if (Config.isSetLock(this)) {
            loc_des.post(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Lock.canPop = true;
                            Lock.locking(MainActivity.this, findViewById(R.id.slide_Main), Lock.UNLOCK);
                        }
                    });
                }
            });
        }
//        head.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        if (!Config.getGuide1(getBaseContext())) {
//            guidePop();
//        }
    }

    PopupWindow p = null;

    private void guidePop() {
        View view = LayoutInflater.from(this).inflate(R.layout.guide_pop1, null);
        view.findViewById(R.id.guide_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.dismiss();
                Config.setGuide1(getBaseContext(), true);
            }
        });
        p = new PopupWindow(view, LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, true);
        p.showAtLocation(findViewById(R.id.slide_Main), Gravity.CENTER, 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateTUser(TUser user, LatLng latLng) {
        TUser tUser = new TUser();
        tUser.setObjectId(user.getObjectId());
        BmobGeoPoint p = new BmobGeoPoint(latLng.longitude, latLng.latitude);
        tUser.setLoginPoint(p);
        tUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    isFirstLoc = false;
                } else {
                    isFirstLoc = true;
                }
            }
        });
    }

    /*
    每次进入主界面保存TUser
     */
    Thread thread;
    boolean run = true;
    int count = 0;


    private void initUmengCommunity() {
        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(MainActivity.this);
        mCommSDK.login(MainActivity.this, new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int stCode, CommUser userInfo) {
                // 这里处理你自己的逻辑,stCode为200时表示登录成功
                if (stCode == 200)
                    Show("登陆成功");
            }
        });


    }

    private void saveInterested() {
        Interested interested = new Interested();
        interested.setZaji(0);
        interested.setLewan(0);
        interested.setRenqing(0);
        interested.setMeishi(0);
        interested.setMeijing(0);
        interested.setZj(false);
        interested.setLw(false);
        interested.setRq(false);
        interested.setMs(false);
        interested.setMj(false);
        interested.setUser(Config.tUser);
        interested.save(new SaveListener<String>() {
            @Override
            public void done(String id, BmobException e) {
                if (e != null) {
                    saveInterested();
                }
            }
        });
    }

    private void initStatus() {
        BmobQuery<Interested> query = new BmobQuery<>();
        query.addWhereEqualTo("user", Config.tUser);
        query.findObjects(new FindListener<Interested>() {
            @Override
            public void done(List<Interested> list, BmobException e) {
                if (e == null) {
                    if (list.isEmpty()) {
                        saveInterested();
                    } else {
                        Config.INTERESTED = list.get(0);
                    }
                } else {
                    initStatus();
                }
            }
        });
    }

    private void initTuser() {
        System.out.println(TuApplication.getInstance().getUserName());
        TUser tUser = DemoDBManager.getInstance().getTUserByName(TuApplication.getInstance().getUserName());

        if (tUser != null)
            PicassoHelper.load(this, tUser.getHead())
                    .resize(100, 100).transform(new CircleTransformation(50))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(head);
        tip.setText("更新用户数据中...");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Config.tUser == null) {
                    count++;
                    BmobQuery<TUser> query = new BmobQuery<TUser>();
                    query.addWhereEqualTo("username", Config.Tusername);
                    query.findObjects(new FindListener<TUser>() {
                        @Override
                        public void done(List<TUser> list, BmobException e) {
                            if (e == null) {
                                if (BuildConfig.DEBUG) Log.d("DDDDDDDDDDDD", list.toString());
                                if (!list.isEmpty()) {
                                    Config.tUser = list.get(0);
                                    Show("更新用户数据成功");
                                    initStatus();
                                    updateCurrentUser();
                                    tipLayout.setVisibility(View.GONE);
                                    Config.updateIsPointOpen(MainActivity.this, Config.tUser.isPublicMyPoint());
                                    initUmengCommunity();
                                    if (list.get(0).getHead() == null || list.get(0).getHead().equals("")) {
                                        head.setImageResource(R.drawable.default_avatar);
                                    } else {
                                        PicassoHelper.load(MainActivity.this, list.get(0).getHead())
                                                .resize(100, 100)
                                                .transform(new CircleTransformation(50))
                                                .placeholder(R.drawable.default_avatar)
                                                .error(R.drawable.default_avatar)
                                                .into(head);
                                    }
                                }
                            } else {
                                tip.setText("再次更新用户数据中...");
                                initTuser();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_user_main:
                index = 3;
                toolbar.setTitle("我的图忆");
                break;
            case R.id.btn_conversation:
                index = 0;
                toolbar.setTitle("会话");
                break;
            case R.id.btn_address_list:
                index = 1;
                toolbar.setTitle("通讯录");
                break;
            case R.id.btn_setting:
                index = 2;
                toolbar.setTitle("图忆社区");
                break;

        }
        if (index == 2) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    toolbar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            toolbar.setAnimation(alphaAnimation);

        } else if (toolbar.getVisibility() == View.GONE) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(500);
            toolbar.setAnimation(alphaAnimation);
            toolbar.setVisibility(View.VISIBLE);
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
                EMMessage message = (EMMessage) event.getData();
                // 提示新消息
                DemoHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUIWithMessage();
                break;
            case EventOfflineMessage: {
                refreshUIWithMessage();
                break;
            }

            case EventConversationListChanged: {
                refreshUIWithMessage();
                break;
            }
            case EventNewCMDMessage:
                EMMessage cmdMessage = (EMMessage) event.getData();
                //获取消息body
                CmdMessageBody cmdMsgBody = (CmdMessageBody) cmdMessage.getBody();
                final String action = cmdMsgBody.action;//获取自定义action
                if (action.equals(EaseConstant.EASE_ATTR_REVOKE)) {
                    EaseCommonUtils.receiveRevokeMessage(this, cmdMessage);
                }
                //red packet code : 处理红包回执透传消息
                if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    RedPacketUtil.receiveRedPacketAckMessage(cmdMessage);
                }
                //end of red packet code
                refreshUIWithMessage();
                break;
            case EventReadAck:
                // TODO 这里当此消息未加载到内存中时，ackMessage会为null，消息的删除会失败
                EMMessage ackMessage = (EMMessage) event.getData();
                EMConversation conversation = EMChatManager.getInstance().getConversation(ackMessage.getTo());
                // 判断接收到ack的这条消息是不是阅后即焚的消息，如果是，则说明对方看过消息了，对方会销毁，这边也删除(现在只有txt iamge file三种消息支持 )
                if (ackMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
                        && (ackMessage.getType() == Type.TXT
                        || ackMessage.getType() == Type.VOICE
                        || ackMessage.getType() == Type.IMAGE)) {
                    // 判断当前会话是不是只有一条消息，如果只有一条消息，并且这条消息也是阅后即焚类型，当对方阅读后，这边要删除，会话会被过滤掉，因此要加载上一条消息
                    if (conversation.getAllMessages().size() == 1 && conversation.getLastMessage().getMsgId().equals(ackMessage.getMsgId())) {
                        if (ackMessage.getChatType() == ChatType.Chat) {
                            conversation.loadMoreMsgFromDB(ackMessage.getMsgId(), 1);
                        } else {
                            conversation.loadMoreGroupMsgFromDB(ackMessage.getMsgId(), 1);
                        }
                    }
                    conversation.removeMessage(ackMessage.getMsgId());
                }
                refreshUIWithMessage();
                break;
            default:
                break;
        }
    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (currentTabIndex == 0) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        });
    }


    @Override
    public void back(View view) {
        super.back(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
        unregisterBroadcastReceiver();

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    private static long firstTime;

    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(false);
            super.onBackPressed();
        } else {
            Show("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
//					unreadAddressLable.setText(String.valueOf(count));
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    private InviteMessgeDao inviteMessgeDao;

    private UserDao userDao;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_community:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.toolbar_tuyi:
                startActivity(new Intent(MainActivity.this, UserMainActivity.class));
                break;
            case R.id.my_Favorite:
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.user_des:
                isGetWeather = true;
                break;
            case R.id.offline_tuyi:
                startActivityForResult(new Intent(MainActivity.this, OfflineTuyiActivity.class), 100);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.user_community:
                Lock.canPop = false;
                startActivityForResult(new Intent(MainActivity.this, TuyiCommunityActivity.class), 100);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.user_head:
                if (Config.tUser == null) {
                    Show("请等待更新用户数据完成后再试");
                    return;
                }
                Lock.canPop = false;
                final Dialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setCancelable(true);
                dialog.show();
                dialog.getWindow().setContentView(R.layout.dialog_set_head_img);
                TextView msg = (TextView) dialog.findViewById(R.id.dialog_msg);
                msg.setText("上传新头像");
                TextView dialog_left = (TextView) dialog.findViewById(R.id.dialog_left);
                TextView dialog_right = (TextView) dialog.findViewById(R.id.dialog_right);
                dialog_left.setText("相机");
                dialog_right.setText("相册");
                dialog_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        File dir = new File(Constant.PICTURE_PATH);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        // 原图
                        File file = new File(dir, username + "_head_" + new SimpleDateFormat(TimeUtil.FORMAT_NORMAL).format(new Date()));
                        filePath = file.getAbsolutePath();// 获取相片的保存路径
                        Uri imageUri = Uri.fromFile(file);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent,
                                Constant.REQUESTCODE_UPLOADAVATAR_CAMERA);
                    }
                });
                dialog_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent,
                                Constant.REQUESTCODE_UPLOADAVATAR_LOCATION);
                    }
                });

                break;
            case R.id.user_center:
                if (Config.tUser != null) {
                    Lock.canPop = false;
                    startActivityForResult(new Intent(MainActivity.this, UserCenter.class).putExtra("USER", Config.tUser), 100);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
                break;
            case R.id.user_albums:
                startActivityForResult(new Intent(MainActivity.this, TuyiPlayActivity.class), 100);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.user_setting:
                startActivityForResult(new Intent(MainActivity.this, MyRadarActivity.class), 100);
                break;
            case R.id.my_interested:
                startActivityForResult(new Intent(MainActivity.this, MyInterestedActivity.class), 100);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;


        }
    }


    String city = "";
    public static boolean isGetWeather = false;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation != null) {

            if (isFirstLoc && Config.tUser != null) {
                isFirstLoc = false;
                updateTUser(Config.tUser, new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
            }
            if (!isGetWeather && (bdLocation.hasAddr())) {

                isGetWeather = true;
                if (bdLocation.getCity() != null)
                    city = bdLocation.getCity().substring(0, bdLocation.getCity().length() - 1);
                if (bdLocation.getProvince() != null && city == null) {
                    city = bdLocation.getProvince().substring(0, bdLocation.getProvince().length() - 1);
                }
                if (bdLocation.getDistrict() != null && city == null) {
                    city = bdLocation.getDistrict().substring(0, bdLocation.getDistrict().length() - 1);
                }
                if (city == null || city.equals("")) {
                    loc_des.setText("获取位置信息失败，点击再试");
                } else {
                    option.setScanSpan(1000 * 60 * 10);
                    mLocClient.setLocOption(option);
                    new Weather(MainActivity.this, loc_des, Weather.NAME).execute("cityname=" + city);
                }
                print("city" + city);

            }

        }
    }



    View.OnClickListener net_error = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction("com.android.settings");
            startActivityForResult(intent, 0);
        }
    };


    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        DemoHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            contactListFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Lock.canPop && Config.isSetLock(this)) {
            Lock.canPop = false;
            Lock.locking(MainActivity.this, findViewById(R.id.slide_Main), Lock.UNLOCK);
        }

        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(this);

        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged,
                        EMNotifierEvent.Event.EventNewCMDMessage,
                        EMNotifierEvent.Event.EventReadAck
                });

        if (!EMChatManager.getInstance().isConnected() && NetUtils.hasNetwork(this)) {
            EMChatManager.getInstance().reconnect();
        }

        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

        if (tipLayout.getVisibility() == View.VISIBLE && Config.tUser != null)
            tipLayout.setVisibility(View.GONE);

    }


    @Override
    protected void onStop() {
        Lock.unLock();
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.popActivity(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        TuApplication.getInstance().logout(null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        TuApplication.getInstance().logout(null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.context_tab_contact, menu);
    }


    String filePath = "";
    String path = "";
    boolean isFromCamera;
    int degree = 0;
    LinearLayout popLayout;


    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 601:
                if (Build.VERSION.SDK_INT>=23 && !Settings.System.canWrite(this)) {
                    new android.app.AlertDialog.Builder(this)
                            .setMessage("不能读写系统设置。应用将不能正常运行")
                            .show();
                }
                break;
            case 100:
                Lock.canPop = false;
                break;
            case Constant.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                Lock.canPop = false;
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Show("SD不可用");
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    Show("file :" + file.getAbsolutePath());
                    Log.i("life", "拍照后的角度：" + degree);
                    startImageAction(Uri.fromFile(file), 200, 200,
                            Constant.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case Constant.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                Lock.canPop = false;
                if (popLayout != null) {
                    popLayout.setVisibility(View.GONE);
                }
                Uri uri;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Show("SD不可用");
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200,
                            Constant.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    Show("照片获取失败");
                }

                break;
            case Constant.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                if (popLayout != null) {
                    popLayout.setVisibility(View.GONE);
                }
                if (data == null) {
                    return;
                } else {
                    saveCropAvator(data);
                }
                // 初始化文件路径
                //filePath = "";
                // 上传头像
                uploadAvatar();
                break;
            default:
                break;

        }
    }

    private void uploadAvatar() {
        Show("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateUserAvatar(bmobFile.getUrl());
                } else {
                    Show("头像上传失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                // 保存图片
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                String filename = username + "_head_" + new SimpleDateFormat(TimeUtil.FORMAT_NORMAL).format(new Date()) + ".png";
                path = Constant.PICTURE_PATH + filename;
                Config.preferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
                Config.preferences.edit().remove(Config.USER_HEAD + username).apply();
                Config.preferences.edit().putString(Config.USER_HEAD + username, path).apply();
                PhotoUtil.saveBitmap(Constant.PICTURE_PATH, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    private void updateUserAvatar(String url) {
        String id;
        if (Config.tUser != null)
            id = Config.tUser.getObjectId();
        else {
            Config.preferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
            id = Config.preferences.getString(username + "_ID", "");
        }
        if (id.equals("")) {
            Show("登陆用户数据出错！！！");
            return;
        }
        TUser tUser = new TUser();
        tUser.setHead(url);
        tUser.setObjectId(id);
        tUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Show("头像更新成功！");
                    refreshAvatar(path);
                    CommunityFactory.getCommSDK(MainActivity.this).updateUserProtrait(BitmapFactory.decodeFile(path), new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
                        @Override
                        public void onComplete(PortraitUploadResponse portraitUploadResponse) {
                        }
                    });
                } else {
                    Show("头像更新失败：" + e.toString());
                }
            }
        });
    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            final Bitmap bitmap = PhotoUtil.toRoundBitmap(BitmapFactory.decodeFile(avatar));
            ((ImageView) findViewById(R.id.user_head)).setImageBitmap(bitmap);
            CommunityFactory.getCommSDK(MainActivity.this).updateUserProtrait(bitmap, new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
                @Override
                public void onComplete(PortraitUploadResponse portraitUploadResponse) {
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DemoHelper.getInstance().getUserProfileManager().uploadUserAvatar(PhotoUtil.Bitmap2Bytes(bitmap));
                }
            }).start();
        } else {
            ((ImageView) findViewById(R.id.user_head)).setImageResource(R.drawable.default_avatar);
        }
    }


    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        intentFilter.addAction(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if (contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }
                String action = intent.getAction();
                if (action.equals(Constant.ACTION_GROUP_CHANAGED)) {
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
                //red packet code : 会话列表页面刷新红包回执消息
                if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
                //end of red packet code
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
