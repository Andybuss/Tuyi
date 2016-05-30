package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.RecyclerViewAdapter;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;

/**
 * Created by Dooze on 2015/8/28.
 */
public class MyFootStepActivity extends BaseActivity {

    public static final int FROM_LIKE = 1;
    public static final int FROM_USER = 2;
    public static final String INTENT_TAG = "Like_Tag";
    private List<UserTuyi> likes = new ArrayList<>();
    private TextView left,center,right;
    private static final int limit = 10;
    private int skip = 0;
    private int from;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_tuyi);
        if (getIntent().hasExtra(INTENT_TAG)) {
            from = getIntent().getIntExtra(INTENT_TAG, 0);
            if (from == 0)
                finish();
        } else {
            finish();
        }
        initView();
    }

    private void initView() {
        left= (TextView) findViewById(R.id.bar_left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        center= (TextView) findViewById(R.id.bar_center);
        right= (TextView) findViewById(R.id.bar_right);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        initData();
    }

    private void initData() {
        if (from == FROM_USER) {
            center.setText("Mine足迹");
            likes = DemoDBManager.getInstance().getUserAllTuyi(TuApplication.getInstance().getUserName());
            if (likes == null) {
                Show("用户没有图忆");
                finish();
            }
            else {
                adapter=new RecyclerViewAdapter(this,likes);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        }
    }



}
