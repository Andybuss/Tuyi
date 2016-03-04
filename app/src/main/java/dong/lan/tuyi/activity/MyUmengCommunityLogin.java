package dong.lan.tuyi.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;

import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.utils.Config;

/**
 * Created by 桂栋 on 2015/8/4.
 */
public class MyUmengCommunityLogin implements Loginable {

    private boolean isLogin = false;
    CommUser user;
    private static MyUmengCommunityLogin instance;

    public static MyUmengCommunityLogin getInstance() {
        if (instance == null)
            instance = new MyUmengCommunityLogin();
        return instance;
    }

    public MyUmengCommunityLogin() {
    }

    @Override
    public void login(Context context, LoginListener loginListener) {
        if (!isLogin) {
            if (Config.tUser == null)
                return;
            // 注意用户id、昵称、source是必填项
            user = new CommUser("id" + Config.tUser.getUsername().hashCode());
            // 登录的来源.可使用值参考文档中SOURCE的取值说明
            user.source = Source.SELF_ACCOUNT;
            user.name = TuApplication.getInstance().getUserName();
            // 如果没有头像地址，可不传递
            user.iconUrl = Config.tUser.getHead();
            // 性别
            if (Config.tUser.isSex())
                user.gender = CommUser.Gender.MALE;
            else
                user.gender = CommUser.Gender.FEMALE;
            // 登录完成回调给社区SDK
            isLogin = true;

            Config.CommUser = user;
            loginListener.onComplete(200, user);
        } else {
            loginListener.onComplete(200, Config.CommUser);
        }
    }

    @Override
    public void logout(Context context, LoginListener loginListener) {
        isLogin = false;
        loginListener.onComplete(200, null);
    }

    @Override
    public boolean isLogined(Context context) {
        SharedPreferences preferences = Config.getSharePreference(context);
        preferences.edit().remove("UMENG_LOGIN").apply();
        preferences.edit().putBoolean("UMENG_LOGIN", isLogin).apply();
        return isLogin;
    }
}
