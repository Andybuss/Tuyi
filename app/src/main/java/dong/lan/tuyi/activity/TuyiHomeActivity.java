package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.view.View;

import com.umeng.comm.ui.fragments.CommunityMainFragment;

import dong.lan.tuyi.R;

/**
 * Created by 桂栋 on 2015/8/4.
 */
public class TuyiHomeActivity extends dong.lan.tuyi.basic.BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyihome);

        CommunityMainFragment mFeedsFragment = new CommunityMainFragment();
        //设置Feed流页面的返回按钮不可见
        mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);
        //添加并显示Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, mFeedsFragment).commit();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
