package dong.lan.tuyi.activity;

import android.os.Bundle;

import dong.lan.tuyi.R;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/8/4  12:34.
 * Email: 760625325@qq.com
 */
public class SettingActivity extends dong.lan.tuyi.basic.BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyihome);

        SettingsFragment settingFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, settingFragment).commit();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
