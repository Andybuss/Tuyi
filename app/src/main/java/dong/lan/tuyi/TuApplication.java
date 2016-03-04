/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dong.lan.tuyi;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.comm.core.sdkmanager.PushSDKManager;
import com.umeng.comm.push.UmengPushImpl;
import com.umeng.comm.ui.location.DefaultLocationImpl;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dong.lan.tuyi.activity.MyUmengCommunityLogin;
import dong.lan.tuyi.domain.User;
import dong.lan.tuyi.utils.AES;

public class TuApplication extends MultiDexApplication {


	public static Context applicationContext;
	public  static  Context appContext;
	private static TuApplication instance;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();


	//百度地图定位
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	public TextView mLocationResult,logMsg;
	public TextView trigger,exit;
	public Vibrator mVibrator;


	public static  CommunitySDK communitySDK;


	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";

	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
        instance = this;
		appContext = this;
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
// 如果app启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

		if (processAppName == null ||!processAppName.equalsIgnoreCase("dong.lan.tuyi")) {
			//"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}
		EMChat.getInstance().init(applicationContext);
		communitySDK = CommunityFactory.getCommSDK(this);
		communitySDK.initSDK(this);
		AES.init();
		LoginSDKManager.getInstance().addAndUse(MyUmengCommunityLogin.getInstance());
		LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl()) ;
		PushSDKManager.getInstance().addImpl("my_push", new UmengPushImpl());
		PushSDKManager.getInstance().useThis("my_push");
		PushAgent.getInstance(this).enable();
		PushAgent.getInstance(this).setDebugMode(true);
		PushAgent.getInstance(this).setMessageHandler(new UmengMessageHandler() {
			@Override
			public void dealWithNotificationMessage(Context arg0, UMessage msg) {
				super.dealWithNotificationMessage(arg0, msg);
				Log.e("", "### 自行处理推送消息");

			}
		});


		SDKInitializer.initialize(this);// 百度地图 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);


/**
 * debugMode == true 时为打开，sdk 会在log里输入调试信息
 * 在做代码混淆的时候需要设置成false
 */
		//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		hxSDKHelper.onInit(applicationContext);
	}


	public static TuApplication getInstance() {
		return instance;
	}
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}


	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}


	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			logMsg(sb.toString());
		}

		/**
		 * 显示请求字符串
		 */
		public void logMsg(String str) {
			try {
				if (mLocationResult != null)
					mLocationResult.setText(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
