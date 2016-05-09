package dong.lan.tuyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.adapter.TuyiTopAdapter;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.xlist.XListView;

/**
 * Created by 桂栋 on 2015/7/24.
 */
public class TuyiCommunityActivity extends Activity implements AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private XListView mListView;
    private TuyiTopAdapter adapter;
    private int count;
    private LinearLayout layout;
    private static final int limit = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyi_community);
        initView();
    }

    private void initView()
    {
        layout = (LinearLayout) findViewById(R.id.loading_layout);
        mListView = (XListView) findViewById(R.id.community_xlist);
        TextView bar_center = (TextView) findViewById(R.id.bar_center);
        TextView bar_left = (TextView) findViewById(R.id.bar_left);
        bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bar_center.setText("TOP 图忆");
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        fresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(TuyiCommunityActivity.this,TuyiInfoActivity.class);
        intent.putExtra("TUYI", adapter.getList().get(position - 1));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void fresh()
    {
        count =0;
        layout.setVisibility(View.VISIBLE);
        BmobQuery<UserTuyi> query = new BmobQuery<UserTuyi>();
        query.order("-zan");
        query.setLimit(limit);
        query.findObjects(this, new FindListener<UserTuyi>() {
            @Override
            public void onSuccess(List<UserTuyi> list) {
                layout.setVisibility(View.GONE);
                if (list.isEmpty()) {
                    Config.Show(TuyiCommunityActivity.this, "木有数据");
                    refreshPull();
                } else {
                    adapter = new TuyiTopAdapter(TuyiCommunityActivity.this, list);
                    mListView.setAdapter(adapter);
                    refreshPull();
                    if (list.size() < limit)
                        mListView.setPullLoadEnable(false);
                    else {
                        mListView.setPullLoadEnable(true);
                        mListView.setPullRefreshEnable(false);
                    }
                    count += list.size();
                }
            }

            @Override
            public void onError(int i, String s) {
                refreshPull();
                layout.setVisibility(View.GONE);
            }
        });
    }

    private void loadMore()
    {
        BmobQuery<UserTuyi> query = new BmobQuery<UserTuyi>();
        query.order("zan");
        query.setLimit(limit);
        query.setSkip(count);
        query.findObjects(this, new FindListener<UserTuyi>() {
            @Override
            public void onSuccess(List<UserTuyi> list) {
                if (list.isEmpty()) {
                    Config.Show(TuyiCommunityActivity.this, "木有数据");
                    refreshLoad();
                    mListView.setPullLoadEnable(false);
                } else {
                    adapter.addAll(list);
                    mListView.setAdapter(adapter);
                    refreshLoad();
                    if (list.size() < limit) {
                        mListView.setPullLoadEnable(false);
                        mListView.setPullRefreshEnable(true);
                    } else
                        mListView.setPullLoadEnable(true);

                    mListView.setSelection(count);
                    count += list.size();
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
}
