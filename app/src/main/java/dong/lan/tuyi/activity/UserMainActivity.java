package dong.lan.tuyi.activity;

import android.os.Bundle;

import dong.lan.tuyi.R;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  7/2/2016  13:22.
 * Email: 760625325@qq.com
 */
public class UserMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyihome);
        UserMainFragment userMainFragment = new UserMainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, userMainFragment).commit();
    }
}
