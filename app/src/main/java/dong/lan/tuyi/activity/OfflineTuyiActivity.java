package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import dong.lan.tuyi.R;

/**
 * Created by 梁桂栋 on 2015/8/21 ： 下午12:20.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: Tuyi
 */
public class OfflineTuyiActivity extends dong.lan.tuyi.basic.BaseActivity {


    private Fragment[] fragments;
    private Button tab[];
    int index, curIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_tuyi);
        RecoderOfflineTuyiFragment recoderOfflineTuyiFragment = new RecoderOfflineTuyiFragment();
        UploadOfflineTuyiFragment uploadOfflineTuyiFragment = new UploadOfflineTuyiFragment();
        fragments = new Fragment[]{recoderOfflineTuyiFragment, uploadOfflineTuyiFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_offline_container, recoderOfflineTuyiFragment)
                .add(R.id.fragment_offline_container, uploadOfflineTuyiFragment)
                .hide(uploadOfflineTuyiFragment)
                .show(recoderOfflineTuyiFragment).commit();
        initView();
    }

    private void initView() {
        tab = new Button[2];
        tab[0] = (Button) findViewById(R.id.addOffTuyi);
        tab[1] = (Button) findViewById(R.id.localOffTuyi);
        tab[0].setSelected(true);
        curIndex = 0;
        index = 0;
    }


    public void AddOffLineTuyi(View view) {
        tab2Add();
    }

    public void LocalOfflineTuyi(View view) {
        tab2LocalList();
    }

    private void tab2Add()
    {
        index = 0;
        if (curIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[curIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        tab[curIndex].setSelected(false);
        // 把当前tab设为选中状态
        tab[index].setSelected(true);
        curIndex = index;
    }
    private void tab2LocalList()
    {
        index = 1;
        if (curIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[curIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        tab[curIndex].setSelected(false);
        // 把当前tab设为选中状态
        tab[index].setSelected(true);
        curIndex = index;
    }

    float x = 0;
    float y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_CANCEL:
                x = x - event.getX();
                y = y - event.getRawY();
                if(x<-50 && Math.abs(y)<50 && curIndex ==0)
                {
                    tab2LocalList();
                }
                if(x>50 && Math.abs(y)<50 && curIndex ==1)
                {
                    tab2Add();
                }
        }

        return super.onTouchEvent(event);
    }
}
