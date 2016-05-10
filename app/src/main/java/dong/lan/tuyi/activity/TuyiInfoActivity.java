package dong.lan.tuyi.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.adapter.CommentAdapter;
import dong.lan.tuyi.bean.TuyiComment;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.xlist.XListView;

/**
 * Created by 桂栋 on 2015/7/22.
 */
public class TuyiInfoActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, XListView.IXListViewListener, CompoundButton.OnCheckedChangeListener {

    private UserTuyi tuyi;
    private EditText com_content;

    private CheckBox like;
    private LinearLayout layoutShow;

    private XListView mListView;
    private int count;
    private static final int limit = 10;
    private CommentAdapter adapter;
    private TextView like_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyi_info_activity);
        if (getIntent().hasExtra("TUYI")) {
            tuyi = (UserTuyi) getIntent().getSerializableExtra("TUYI");
        } else
            finish();
        initView();
    }

    TextView bar_left;
    TextView bar_center;

    private void initView() {
        bar_left = (TextView) findViewById(R.id.bar_left);
        bar_center = (TextView) findViewById(R.id.bar_center);
        TextView content = (TextView) findViewById(R.id.info_content);
        ImageView pic = (ImageView) findViewById(R.id.info_head);
        TextView addCom = (TextView) findViewById(R.id.comment_tuyi);
        TextView locDes = (TextView) findViewById(R.id.locDes);
        TextView tuyiTag = (TextView) findViewById(R.id.tuyiTag);
        addCom.setOnClickListener(this);
        String c = tuyi.gettContent();
        if (c == null)
            c = "Error";
        bar_center.setText("图忆详情");
        bar_left.setOnClickListener(this);
        like = (CheckBox) findViewById(R.id.like_checkBox);
        like.setOnCheckedChangeListener(this);
        like_count = (TextView) findViewById(R.id.like_count);
        pic.setOnClickListener(this);
        if (tuyi == null) {
            content.setText("数据传递出错，请返回重试");
            content.setTextSize(30);
            content.setBackgroundResource(R.drawable.pop_bg);
            content.setGravity(Gravity.CENTER);
            content.setHeight(ActionBar.LayoutParams.MATCH_PARENT);
            content.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        } else {
            mListView = (XListView) findViewById(R.id.comment_xlist);
            mListView.setOnItemClickListener(this);
            mListView.setPullLoadEnable(true);
            mListView.setPullRefreshEnable(true);
            mListView.setXListViewListener(this);
            mListView.pullRefreshing();
            mListView.setOnItemClickListener(this);
            TextView done = (TextView) findViewById(R.id.comment_done);
            layoutShow = (LinearLayout) findViewById(R.id.comment_layout_show);
            com_content = (EditText) findViewById(R.id.comment_edit_text);
            TextView cancel = (TextView) findViewById(R.id.comment_cancel);
            cancel.setOnClickListener(this);
            done.setOnClickListener(this);
            String url = tuyi.gettPic();
            if (url == null)
                pic.setImageResource(R.drawable.signin_local_gallry);
            else
                PicassoHelper.load(this, url)
                        .placeholder(R.drawable.load_pic)
                        .into(pic);

            if (tuyi.getLocDes() == null || tuyi.getLocDes().equals(""))
                locDes.setVisibility(View.GONE);
            else
                locDes.setText(tuyi.getLocDes());

            like_count.setText(String.valueOf(tuyi.getZan()));
            fresh();
        }
        content.setText(c);
        tuyiTag.setText(tuyi.getTAG());
    }

    /*
    更新图忆的喜欢状态
     */
    private void updateLike() {
        if (Config.tUser != null) {
            if (like.isChecked()) {
                UserTuyi userTuyi = new UserTuyi();
                userTuyi.setObjectId(tuyi.getObjectId());
                if (tuyi.getZan() == null)
                    userTuyi.setZan(1);
                else
                    userTuyi.setZan(tuyi.getZan() + 1);
                BmobRelation relation = new BmobRelation();
                relation.add(Config.tUser);
                userTuyi.setLikes(relation);
                userTuyi.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_head:
                if (tuyi.gettPic() != null)
                    startActivity(new Intent(TuyiInfoActivity.this, BigImage.class).putExtra("PIC", tuyi.gettPic()));
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                break;
            case R.id.bar_left:
                finish();
                break;
            case R.id.comment_done:
                if (com_content.getText().toString().equals("")) {
                    Config.Show(TuyiInfoActivity.this, "评论内容不能为空");
                    return;
                }
                final TuyiComment comment = new TuyiComment();
                comment.setComInfo(com_content.getText().toString());
                comment.setUserTuyi(tuyi);
                comment.setComUser(Config.tUser);
                comment.save(TuyiInfoActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        if (adapter == null) {
                            adapter = new CommentAdapter(TuyiInfoActivity.this, new ArrayList<TuyiComment>());
                            mListView.setAdapter(adapter);
                        }
                        adapter.add(comment);
                        adapter.notifyDataSetChanged();
                        Config.Show(TuyiInfoActivity.this, "评论成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                if (layoutShow != null)
                    layoutShow.setVisibility(View.GONE);
                break;
            case R.id.comment_tuyi:
                if (layoutShow != null)
                    layoutShow.setVisibility(View.VISIBLE);
                break;
            case R.id.comment_cancel:
                if (layoutShow != null)
                    layoutShow.setVisibility(View.GONE);
                break;
        }
    }

    /*
    跳转到用户展示中心
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(TuyiInfoActivity.this, UserCenter.class).putExtra("USER", adapter.getList().get(position - 1).getComUser()));
        overridePendingTransition(R.anim.slide_out_to_left, R.anim.slide_in_from_right);
    }

    /*
    从bmob获取最新的十条评论
     */
    private void fresh() {
        count = 0;
        BmobQuery<TuyiComment> query = new BmobQuery<TuyiComment>();
        query.addWhereEqualTo("userTuyi", new BmobPointer(tuyi));
        query.include("comUser");
        query.setSkip(count);
        query.setLimit(limit);
        query.order("-createAt");
        query.findObjects(this, new FindListener<TuyiComment>() {
            @Override
            public void onSuccess(List<TuyiComment> list) {
                if (list.isEmpty()) {
                    Show("木有人评论");
                    refreshPull();
                    mListView.setPullLoadEnable(false);
                } else {
                    mListView.setPullLoadEnable(true);
                    count += list.size();
                    adapter = new CommentAdapter(TuyiInfoActivity.this, list);
                    mListView.setAdapter(adapter);
                    refreshPull();
                    if (list.size() < limit)
                        mListView.setPullLoadEnable(false);
                    else
                        mListView.setPullLoadEnable(true);
                }
            }

            @Override
            public void onError(int i, String s) {
                Show(s);
                refreshPull();
            }
        });
    }

    /*
    加载更多评论
     */
    private void loadMore() {
        BmobQuery<TuyiComment> query = new BmobQuery<TuyiComment>();
        query.addWhereEqualTo("userTuyi", new BmobPointer(tuyi));
        query.include("comUser");
        query.setSkip(count);
        query.setLimit(limit);
        query.order("-createAt");
        query.findObjects(this, new FindListener<TuyiComment>() {
            @Override
            public void onSuccess(List<TuyiComment> list) {
                if (list.isEmpty()) {
                    Config.Show(getBaseContext(), "木有更多了");
                    mListView.setPullLoadEnable(false);
                    refreshLoad();
                } else {
                    count += list.size();
                    adapter.addAll(list);
                    mListView.setAdapter(adapter);
                    mListView.setSelection(count - list.size());
                    refreshLoad();
                    if (list.size() < limit)
                        mListView.setPullLoadEnable(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                refreshLoad();
            }
        });
    }

    @Override
    public void onRefresh() {
        fresh();
    }

    @Override
    public void onLoadMore() {
        loadMore();
    }

    /*
    刷新加载更多的状态
     */
    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    /*
    刷新下拉刷新的状态
     */
    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    public void ImgClick(View view) {
        if (tuyi.gettPic() != null)
            startActivity(new Intent(TuyiInfoActivity.this, BigImage.class).putExtra("PIC", tuyi.gettPic()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateLike();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            like_count.setText(String.valueOf(tuyi.getZan() + 1));
        } else {
            like_count.setText(String.valueOf(tuyi.getZan()));
        }
    }
}
