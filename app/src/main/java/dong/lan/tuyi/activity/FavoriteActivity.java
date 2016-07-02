package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.adapter.MyFavoriteAdapter;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.xlist.XListView;

/**
 * Created by Dooze on 2015/9/23.
 */
public class FavoriteActivity extends BaseActivity implements  XListView.IXListViewListener {

    private XListView mListView;
    private MyFavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favorite);
        findViewById(R.id.bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView barCenter = (TextView) findViewById(R.id.bar_center);
        barCenter.setText("我的收藏");
        mListView = (XListView) findViewById(R.id.favorite_list);
//        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mListView.setXListViewListener(this);
//        mListView.setOnItemClickListener(this);
//        mListView.setOnItemLongClickListener(this);
        mListView.pullRefreshing();
        initData();
    }

    private void initData() {
        final BmobQuery<UserTuyi> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.addWhereRelatedTo("favoraite", new BmobPointer(Config.tUser));
        query.findObjects(new FindListener<UserTuyi>() {
            @Override
            public void done(List<UserTuyi> list, BmobException e) {
                if(e==null){
                    if (list.isEmpty()) {
                        Show("没有收藏");
                    } else {
                        adapter = new MyFavoriteAdapter(FavoriteActivity.this, list);
                        mListView.setAdapter(adapter);
                    }
                }else{
                    Show("获取收藏失败:"+e.getMessage());
                }
            }
        });
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
