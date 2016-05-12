package dong.lan.tuyi.activity;

import android.content.Context;
import android.support.annotation.Keep;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;

import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.utils.Config;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/8/4  09:36.
 * Email: 760625325@qq.com
 */

@Keep
public class MyUmengCommunityLogin extends AbsLoginImpl {

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
    protected void onLogin(Context context, LoginListener loginListener) {

        // 注意用户id、昵称、source是必填项
        CommUser user = new CommUser("id" + Config.tUser.getUsername().hashCode()); // 用户id
        user.name = TuApplication.getInstance().getUserName(); // 用户昵称

        user.iconUrl=Config.tUser.getHead();
        if (Config.tUser.isSex())
            user.gender = CommUser.Gender.MALE;
        else
            user.gender = CommUser.Gender.FEMALE;
        Config.CommUser = user;
        loginListener.onComplete(200, user);
    }



    @Override
    protected void onLogout(Context context, LoginListener loginListener) {
        super.onLogout(context, loginListener);
    }
}
