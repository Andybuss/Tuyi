package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.adapter.FeedBackAdapter;
import dong.lan.tuyi.bean.FeedBack;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.xlist.XListView;

/**
 * Created by Dooze on 2015/9/29.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {

    private XListView mListView;
    private FeedBackAdapter adapter;
    private EditText feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViewById(R.id.feed_back).setOnClickListener(this);
        mListView= (XListView) findViewById(R.id.feed_list);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(this);
        mListView.setAdapter(adapter);
        feed= (EditText) findViewById(R.id.feed_edit);
        findViewById(R.id.feed_send).setOnClickListener(this);
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.feed_back:
                finish();
                break;
            case R.id.feed_send:
                if(feed.getText().toString().equals(""))
                {
                    Show("没有写反馈信息呢~");
                    return;
                }
                final FeedBack feedBack = new FeedBack();
                feedBack.setUser(Config.tUser);
                feedBack.setFeek(feed.getText().toString());
                feedBack.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Show("感谢您的反馈");
                            if (adapter == null) {
                                List<FeedBack> feedBacks = new ArrayList<>();
                                feedBacks.add(feedBack);
                                adapter = new FeedBackAdapter(FeedBackActivity.this, feedBacks);
                                mListView.setAdapter(adapter);
                            } else {
                                adapter.add(feedBack);
                                mListView.deferNotifyDataSetChanged();
                            }
                        }else{
                            Show("反馈失败：" + e.getMessage());
                        }
                    }
                });
                break;
        }
    }

    private void initData()
    {
        BmobQuery<FeedBack> query = new BmobQuery<>();
        query.addWhereEqualTo("user", Config.tUser);
        query.setLimit(30);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.order("-updateAt");
        query.findObjects(new FindListener<FeedBack>() {
            @Override
            public void done(List<FeedBack> list, BmobException e) {
                if(e==null){
                    if (list.isEmpty()) {
                        Show("没有反馈信息~");
                    } else {
                        adapter = new FeedBackAdapter(FeedBackActivity.this, list);
                        mListView.setAdapter(adapter);
                        mListView.setSelection(list.size());
                    }
                }else{
                    Show(e.getMessage());
                }
            }
        });
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }
    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {

    }
}
