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
package dong.lan.tuyi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.push.Pushable;
import com.umeng.comm.core.sdkmanager.PushSDKManager;
import com.umeng.comm.core.utils.CommonUtils;

import applib.controller.HXSDKHelper;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.DemoHXSDKModel;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.basic.Welcome;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.Lock;

/**
 * 设置界面
 *
 * @author Administrator
 */
public class SettingsFragment extends Fragment implements OnClickListener {

    /**
     * 设置新消息通知布局
     */
    private RelativeLayout rl_switch_notification;
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 设置震动布局
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * 设置扬声器布局
     */

    /**
     * 打开新消息通知imageView
     */
    private ImageView iv_switch_open_notification;
    /**
     * 关闭新消息通知imageview
     */
    private ImageView iv_switch_close_notification;
    /**
     * 打开声音提示imageview
     */
    private ImageView iv_switch_open_sound;
    /**
     * 关闭声音提示imageview
     */
    private ImageView iv_switch_close_sound;
    /**
     * 打开消息震动提示
     */
    private ImageView iv_switch_open_vibrate;
    /**
     * 关闭消息震动提示
     */
    private ImageView iv_switch_close_vibrate;

    /**
     * 声音和震动中间的那条线
     */
    private TextView textview1, textview2, addShortcut,reset_lock;

    private LinearLayout blacklistContainer;

    /**
     * 退出按钮
     */
    private Button logoutBtn;

    private LinearLayout parent;
    private TextView offlineMap;
    private EMChatOptions chatOptions;
    private CheckBox check, lock;
    private boolean isPublic = false;
    DemoHXSDKModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        isPublic = Config.getUserPointOpenConfig(getActivity());
        rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);

        iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
        iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
        iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);
        iv_switch_open_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_open_vibrate);
        iv_switch_close_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_close_vibrate);


        parent = (LinearLayout) getView().findViewById(R.id.setting_layout);
        logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
        if (!TextUtils.isEmpty(EMChatManager.getInstance().getCurrentUser())) {
            logoutBtn.setText("退出登录");
        }

        textview1 = (TextView) getView().findViewById(R.id.textview1);
        textview2 = (TextView) getView().findViewById(R.id.textview2);
        offlineMap = (TextView) getView().findViewById(R.id.offline_map_tv);
        blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);

        addShortcut = (TextView) getView().findViewById(R.id.add_shortcut);

        check = (CheckBox) getView().findViewById(R.id.open_check);
        lock = (CheckBox) getView().findViewById(R.id.lock_check);
        reset_lock = (TextView) getView().findViewById(R.id.reset_lock);
        check.setChecked(isPublic);
        reset_lock.setOnClickListener(this);
        addShortcut.setOnClickListener(this);
        offlineMap.setOnClickListener(this);
        blacklistContainer.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        chatOptions = EMChatManager.getInstance().getChatOptions();

        model = (DemoHXSDKModel) HXSDKHelper.getInstance().getModel();


        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePointStatus();
            }
        });

        getView().findViewById(R.id.feedback).setOnClickListener(this);

        lock.setChecked(Config.isSetLock(getActivity()));
        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Config.isSetLock(getActivity())) {
                        Show("已开启应用锁");
                    } else {
                        Lock.locking(getActivity(),parent,Lock.SETLOCK);
                    }
                } else {
                    Lock.locking(getActivity(),parent,Lock.OFFLOCK);
                }
            }
        });
        // 震动和声音总开关，来消息时，是否允许此开关打开
        // the vibrate and sound notification are allowed or not?
        if (model.getSettingMsgNotification()) {
            iv_switch_open_notification.setVisibility(View.VISIBLE);
            iv_switch_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_notification.setVisibility(View.INVISIBLE);
            iv_switch_close_notification.setVisibility(View.VISIBLE);
        }

        // 是否打开声音
        // sound notification is switched on or not?
        if (model.getSettingMsgSound()) {
            iv_switch_open_sound.setVisibility(View.VISIBLE);
            iv_switch_close_sound.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_sound.setVisibility(View.INVISIBLE);
            iv_switch_close_sound.setVisibility(View.VISIBLE);
        }

        // 是否打开震动
        // vibrate notification is switched on or not?
        if (model.getSettingMsgVibrate()) {
            iv_switch_open_vibrate.setVisibility(View.VISIBLE);
            iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
            iv_switch_close_vibrate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback:
                startActivity(new Intent(getActivity(),FeedBackActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                break;
            case R.id.reset_lock:
                if(Config.isSetLock(getActivity()))
                Lock.locking(getActivity(),parent,Lock.RESETLOCK);
                else
                Show("未设置应用锁");
                break;
            case R.id.add_shortcut:
                Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                Parcelable ioc = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.logo);
                Intent clickIntent = new Intent(getActivity(), Welcome.class);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ioc);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
                getActivity().sendBroadcast(addIntent);
                Show("添加桌面快捷方式成功");
                break;
            case R.id.rl_switch_notification:
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);
                    chatOptions.setNotificationEnable(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);

                    HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
                    chatOptions.setNotificationEnable(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound:
                if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
                    iv_switch_open_sound.setVisibility(View.INVISIBLE);
                    iv_switch_close_sound.setVisibility(View.VISIBLE);
                    chatOptions.setNoticeBySound(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
                } else {
                    iv_switch_open_sound.setVisibility(View.VISIBLE);
                    iv_switch_close_sound.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticeBySound(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.VISIBLE);
                    chatOptions.setNoticedByVibrate(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
                } else {
                    iv_switch_open_vibrate.setVisibility(View.VISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticedByVibrate(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
                }
                break;

            case R.id.btn_logout: //退出登陆
                logout();
                break;
            case R.id.ll_black_list:
                startActivity(new Intent(getActivity(), BlacklistActivity.class));
                break;
            case R.id.offline_map_tv:
                startActivity(new Intent(getActivity(), OfflineMapActivity.class));
                break;
            default:
                break;
        }

    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        TuApplication.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                MyUmengCommunityLogin.getInstance().logout(getActivity(), new LoginListener() {
                    @Override
                    public void onStart() {
                        System.out.print("友盟退出登录 start");
                    }

                    @Override
                    public void onComplete(int i, CommUser commUser) {
                        System.out.print("友盟退出登录");
                        CommonUtils.logout();
                        CommConfig.getConfig().loginedUser = new CommUser();
                        Pushable var3 = PushSDKManager.getInstance().getCurrentSDK();
                        var3.disable();
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // 重新显示登陆页面
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    /*
    更新用户是否公开每次登陆的坐标的状态
     */
    private void updatePointStatus() {
        if (Config.tUser == null)
            Show("用户更新未完成");
        if (check.isChecked() == Config.getUserPointOpenConfig(getActivity()))
            return;
        TUser tUser = new TUser();
        tUser.setPublicMyPoint(check.isChecked());
        tUser.update(getActivity(), Config.tUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Config.updateIsPointOpen(getActivity(), check.isChecked());
            }

            @Override
            public void onFailure(int i, String s) {
                Config.print(s);
            }
        });
    }



    private void Show(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
