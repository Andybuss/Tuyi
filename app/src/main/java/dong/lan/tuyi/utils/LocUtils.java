package dong.lan.tuyi.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by 桂栋 on 2015/8/7.
 */
public class LocUtils {

    public Context context;
    public LocationClient mLocClient;
    public LatLng loc;
    public LocListener locListener = new LocListener();
    public LocUtils() {
    }
    public LocUtils(Context context)
    {
        this.context = context;
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(locListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setTimeOut(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private class LocListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            loc = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            mLocClient.stop();
        }
    }

    public  LatLng getLoc()
    {
        boolean isNull = true;
        long start =System.currentTimeMillis();
        long cur = 0;
        while(isNull)
        {
            cur = System.currentTimeMillis();
            if((cur-start)/1000>10)
                return null;
            if(loc!=null)
                isNull = false;
        }
        return loc;
    }

}
