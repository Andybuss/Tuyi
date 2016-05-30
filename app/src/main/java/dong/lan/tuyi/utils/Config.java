package dong.lan.tuyi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.Interested;
import dong.lan.tuyi.bean.TUser;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/7/19  13:08.
 * Email: 760625325@qq.com
 */
public class Config {

    public static String USER_HEAD = "user_head_";
    public static final String LOAD_MODE = "load_mode";
    public static final int MY = 1;
    public static final int NEAR = 2;
    public static final String prefName = "Tuyi";
    public static SharedPreferences preferences;
    public static TUser tUser;
    public static Interested INTERESTED;
    public static String Tusername;
    public static final String pointOpen = "pointOpen";
    public static String prefTUserName = "TUserName";
    public static com.umeng.comm.core.beans.CommUser CommUser;

    //获取屏幕信息
    public static int[] getPPI(Context context) {
        int wh[] = new int[2];
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        wh[0]=metrics.widthPixels;
        wh[1]=metrics.heightPixels;
        return wh;
    }


    //自定义的Toast
    public static void Show(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }



    //获取sharepreference
    public static SharedPreferences getSharePreference(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return preferences;
    }

    public static boolean isSetLock(Context context)
    {
        return getSharePreference(context).getBoolean(TuApplication.getInstance().getUserName()+"HasLock",false);
    }
    public static void saveLock(Context context,boolean lock)
    {
        getSharePreference(context).edit().remove(TuApplication.getInstance().getUserName()+"HasLock").apply();
        getSharePreference(context).edit().putBoolean(TuApplication.getInstance().getUserName()+"HasLock",lock).apply();
    }


    public static void saveLockKey(Context context,String key)
    {
        getSharePreference(context).edit().remove(TuApplication.getInstance().getUserName()+"HasLock").apply();
        getSharePreference(context).edit().putBoolean(TuApplication.getInstance().getUserName()+"HasLock",true).apply();
        getSharePreference(context).edit().remove(TuApplication.getInstance().getUserName()+"LOCK_KEY").apply();
        getSharePreference(context).edit().putString(TuApplication.getInstance().getUserName() + "LOCK_KEY", key).apply();
    }

    public static String getLockKey(Context context)
    {
       return getSharePreference(context).getString(TuApplication.getInstance().getUserName()+"LOCK_KEY","0000");
    }
    public static void print(String s) {
            System.out.println(s);
    }

    public static boolean getUserPointOpenConfig(Context context)
    {
       return getSharePreference(context).getBoolean(TuApplication.getInstance().getUserName() + "_" + pointOpen, false);
    }
    public static void updateIsPointOpen(Context context, boolean isOpen) {
        getSharePreference(context).edit().remove(TuApplication.getInstance().getUserName()+"_"+ pointOpen).apply();
        getSharePreference(context).edit().putBoolean(TuApplication.getInstance().getUserName()+"_"+ pointOpen, isOpen).apply();
    }

    public static void setGuide1(Context context,boolean isGuide)
    {
        getSharePreference(context).edit().remove(TuApplication.getInstance().getUserName()+"_GUIDE1").apply();
        getSharePreference(context).edit().putBoolean(TuApplication.getInstance().getUserName()+"_GUIDE1", isGuide).apply();
    }
    public static boolean getGuide1(Context context)
    {
        return getSharePreference(context).getBoolean(TuApplication.getInstance().getUserName() + "_GUIDE1", false);
    }

    public static final double EARTH_RADIUS = 6378137;

    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     */
    public static double DistanceOfTwoPoints(double lat1,double lng1,double lat2,double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    public static void updateTUser( Context context,TUser user, LatLng latLng) {
        TUser tUser = new TUser();
        tUser.setObjectId(user.getObjectId());
        tUser.setDes(user.getDes());
        BmobGeoPoint p = new BmobGeoPoint(latLng.longitude, latLng.latitude);
        tUser.setLoginPoint(p);
        tUser.update(context, new UpdateListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
                System.out.println(i + "  " + s);
            }
        });
    }


    public static void updateS(final Context context,Interested interested,final String tag)
    {
        switch (tag) {
            case "乐玩":
                interested.setLewan(Config.INTERESTED.getLewan() + 1);
                break;
            case "杂记":
                interested.setZaji(Config.INTERESTED.getZaji() + 1);
                break;
            case "人情":
                interested.setRenqing(Config.INTERESTED.getRenqing() + 1);
                break;
            case "美食":
                interested.setMeishi(Config.INTERESTED.getMeishi() + 1);
                break;
            case "美景":
                interested.setMeijing(Config.INTERESTED.getMeijing() + 1);
                break;

        }
        interested.update(context, new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {
                updateStatus(context, tag);
            }
        });
    }

    public static void updateStatus(final Context context,final String tag) {
        if (Config.INTERESTED == null) {
            BmobQuery<Interested> query = new BmobQuery<>();
            query.addWhereEqualTo("user", Config.tUser);
            query.findObjects(context, new FindListener<Interested>() {
                @Override
                public void onSuccess(List<Interested> list) {
                    if (!list.isEmpty()) {
                        Config.INTERESTED = list.get(0);
                        updateS(context,list.get(0),tag);
                    }

                }

                @Override
                public void onError(int i, String s) {

                }
            });
        } else {
            updateS(context,Config.INTERESTED,tag);
        }
    }
}
