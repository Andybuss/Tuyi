package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.SelectTuyiAdapter;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;

/**
 * Created by 梁桂栋 on 2015/11/9.
 */
public class SelectTuyiActivity extends BaseActivity {


    public interface onTuyiSelectListener
    {
        void onTuyiSelected(List<UserTuyi> Tuyis);
    }
    public static onTuyiSelectListener listener;
    public static  void setTuyiSelectListener(onTuyiSelectListener l)
    {
        listener = l;
    }

    private SelectTuyiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tuyi);
        initView();
    }

    private void initView() {
        findViewById(R.id.bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView right = (TextView) findViewById(R.id.bar_right);
        right.setText(R.string.ok);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                {
                    listener.onTuyiSelected(adapter.getTuyis());
                    finish();
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectTuyiList);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        List<UserTuyi> tuyis = DemoDBManager.getInstance().getUserAllTuyi(TuApplication.getInstance().getUserName());
        if (tuyis == null) {
            Show("您没有图忆记录");
        } else {
            adapter = new SelectTuyiAdapter(this, tuyis);
            recyclerView.setAdapter(adapter);
        }
    }
}
