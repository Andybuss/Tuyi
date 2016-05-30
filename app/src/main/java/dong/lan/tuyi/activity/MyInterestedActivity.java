package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.Interested;
import dong.lan.tuyi.utils.Config;

/**
 * Created by 梁桂栋 on 2015/11/10.
 */
public class MyInterestedActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox zj;
    private CheckBox lw;
    private CheckBox rq;
    private CheckBox ms;
    private CheckBox mj;
    private TextView status;
    private boolean tag[] = new boolean[]{false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interedted);
        initView();
    }

    private void initView() {
        zj = (CheckBox) findViewById(R.id.interested_zaji);
        lw = (CheckBox) findViewById(R.id.interested_lewan);
        rq = (CheckBox) findViewById(R.id.interested_renqing);
        ms = (CheckBox) findViewById(R.id.interested_meishi);
        mj = (CheckBox) findViewById(R.id.interested_meijing);
        status = (TextView) findViewById(R.id.interested_status);
        ((TextView)findViewById(R.id.bar_center)).setText("兴趣标签");
        findViewById(R.id.bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        zj.setOnCheckedChangeListener(this);
        lw.setOnCheckedChangeListener(this);
        rq.setOnCheckedChangeListener(this);
        ms.setOnCheckedChangeListener(this);
        mj.setOnCheckedChangeListener(this);
        initData();
    }

    private void initData()
    {
        BmobQuery<Interested> query = new BmobQuery<>();
        query.addWhereEqualTo("user", Config.tUser);
        query.findObjects(this, new FindListener<Interested>() {
            @Override
            public void onSuccess(List<Interested> list) {
                if (list.isEmpty()) {
                    Show("赶紧你的兴趣标签吧");
                } else {
                    Interested interested =list.get(0);
                    zj.setChecked(interested.getZj());
                    lw.setChecked(interested.getLw());
                    rq.setChecked(interested.getRq());
                    ms.setChecked(interested.getMs());
                    mj.setChecked(interested.getMj());
                    StringBuffer s = new StringBuffer();
                    s.append("<html><body>");
                    s.append("<h3> 您的标签记录统计</h3>");
                    s.append("<h5>  杂记记录： ");
                    s.append(interested.getZaji());
                    s.append(" 个</h5>");
                    s.append("<h5>  乐玩记录： ");
                    s.append(interested.getLewan());
                    s.append(" 个</h5>");
                    s.append("<h5>  人情记录： ");
                    s.append(interested.getRenqing());
                    s.append(" 个</h5>");
                    s.append("<h5>  美食记录： ");
                    s.append(interested.getMeishi());
                    s.append(" 个</h5>");
                    s.append("<h5>  美景记录： ");
                    s.append(interested.getMeijing());
                    s.append(" 个</h5>");
                    s.append("</body></html>");
                    status.setText(Html.fromHtml(s.toString()));
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.interested_zaji:
                if (isChecked) {
                    zj.setText("+杂记");
                    tag[0] = true;
                } else {
                    tag[0] = false;
                    zj.setText("杂记");
                }
                break;
            case R.id.interested_lewan:
                if (isChecked) {
                    lw.setText("+乐玩");
                    tag[1] = true;
                } else {
                    tag[1] = false;
                    lw.setText("乐玩");
                }
                break;
            case R.id.interested_renqing:
                if (isChecked) {
                    rq.setText("+人情");
                    tag[2] = true;
                } else {
                    tag[2] = false;
                    rq.setText("人情");
                }
                break;
            case R.id.interested_meijing:
                if (isChecked) {
                    mj.setText("+美景");
                    tag[3] = true;
                } else {
                    tag[3] = false;
                    mj.setText("美景");
                }
                break;
            case R.id.interested_meishi:
                if (isChecked) {
                    ms.setText("+美食");
                    tag[4] = true;
                } else {
                    tag[4] = false;
                    ms.setText("美食");
                }
                break;

        }
    }

    private void updateStatus()
    {
       if(Config.tUser!=null)
       {
           BmobQuery<Interested> query = new BmobQuery<>();
           query.addWhereEqualTo("user", Config.tUser);
           query.findObjects(this, new FindListener<Interested>() {
               @Override
               public void onSuccess(List<Interested> list) {
                   if (list.isEmpty()) {
                       Interested interested = new Interested();
                       interested.setUser(Config.tUser);
                       interested.setZj(tag[0]);
                       interested.setLw(tag[1]);
                       interested.setRq(tag[2]);
                       interested.setMs(tag[4]);
                       interested.setMj(tag[3]);
                       interested.setZaji(0);
                       interested.setLewan(0);
                       interested.setRenqing(0);
                       interested.setMeishi(0);
                       interested.setMeijing(0);
                       interested.save(getBaseContext(), new SaveListener() {
                           @Override
                           public void onSuccess() {
                               Show("保存成功");
                           }

                           @Override
                           public void onFailure(int i, String s) {

                           }
                       });
                   } else {
                       Interested interested = list.get(0);
                       interested.setZj(tag[0]);
                       interested.setLw(tag[1]);
                       interested.setRq(tag[2]);
                       interested.setMs(tag[4]);
                       interested.setMj(tag[3]);
                       interested.update(getBaseContext(), new UpdateListener() {
                           @Override
                           public void onSuccess() {
                           }

                           @Override
                           public void onFailure(int i, String s) {

                           }
                       });
                   }
               }

               @Override
               public void onError(int i, String s) {

               }
           });

       }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            updateStatus();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
