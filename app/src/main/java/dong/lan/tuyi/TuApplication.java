/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dong.lan.tuyi;


import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.easeui.domain.EaseUser;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;

import java.util.Map;

import cn.bmob.v3.Bmob;
import dong.lan.tuyi.activity.MyUmengCommunityLogin;

public class TuApplication extends MultiDexApplication {


    public static Context applicationContext;
    private static TuApplication instance;

    public Vibrator mVibrator;

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;

        //MultiDex.install(this);
        DemoHelper.getInstance().init(applicationContext);

        Bmob.initialize(this, Constant.BmonAppID);
        LoginSDKManager.getInstance().addAndUse(MyUmengCommunityLogin.getInstance());

        SDKInitializer.initialize(this);
        mVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        initShare();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
    public static TuApplication getInstance() {
        return instance;
    }


    public Map<String, EaseUser> getContactList() {
        return DemoHelper.getInstance().getContactList();
    }


    /**
     * 设置好友user list到内存中
     */
    public void setContactList(Map<String, EaseUser> contactList) {
        DemoHelper.getInstance().setContactList(contactList);
    }

    /**
     * 获取当前登陆用户名
     */
    public String getUserName() {
        return DemoHelper.getInstance().getCurrentUsernName();
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return DemoHelper.getInstance().getPassword();
    }

    /**
     * 设置用户名
     *
     */
    public void setUserName(String username) {
        DemoHelper.getInstance().setCurrentUserName(username);
    }

    public void setPassword(String pwd) {
        DemoHelper.getInstance().setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        DemoHelper.getInstance().logout(true,emCallBack);
    }

    private void initShare(){
        //PlatformConfig.setSinaWeibo("275392174", "d96fb6b323c60a42ed9f74bfab1b4f7a");
    }



}
