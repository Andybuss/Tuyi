package dong.lan.tuyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.easemob.util.NetUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ValueEventListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.adapter.RadarUserAdapter;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.InputTools;

/**
 * Created by Dooze on 2015/9/17.
 */
public class MyRadarActivity extends BaseActivity implements BDLocationListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener, RadarUserAdapter.itemClickListener {


    public static final int GET_MAP = 0x12;
    public static final int GET_CONTAINER = 0x13;
    private List<TUser> map = new ArrayList<TUser>();
    private List<Marker> mapUser = new ArrayList<Marker>();
    private List<String> ids = new ArrayList<String>();
    private List<String> tags = new ArrayList<String>();
    private List<TUser> users = new ArrayList<TUser>(4);
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    BitmapDescriptor marker = BitmapDescriptorFactory
            .fromResource(R.drawable.location_mark);
    private MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;
    private LocationClient mLocClient;
    private boolean first = true;
    private boolean init =false;
    private LatLng loc;
    BmobRealTimeData data = new BmobRealTimeData(); //Bmob的实时数据监听
    private MyHandler handler = new MyHandler();   //负责操作监控数据与UI线程的交互
    private EditText desEt;
    private EditText seachEt;
    private RecyclerView recyclerView;
    private RadarUserAdapter adapter;
    private String des = " ";
    private String s="";
    private BmobQuery<TUser> query ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_radar);
        initView();

    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
        recyclerView = (RecyclerView) findViewById(R.id.radar_user_list);

       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RadarUserAdapter(this, users);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        desEt = (EditText) findViewById(R.id.add_des_et);
        seachEt = (EditText) findViewById(R.id.radar_search_et);
        findViewById(R.id.add_des_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!desEt.getText().toString().equals("")) {
                    des = desEt.getText().toString();
                    desEt.setText("");
                    InputTools.hideSoftKeyboard(MyRadarActivity.this);
                }
            }
        });
        findViewById(R.id.radar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.radar_search_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = seachEt.getText().toString();
                if (s.length() > 0) {
                    Show("寻找用户");
                    if (query == null)
                        query = new BmobQuery<TUser>();
                    query.addWhereContains("username", s);
                    query.findObjects(getBaseContext(), new FindListener<TUser>() {
                        @Override
                        public void onSuccess(List<TUser> list) {
                            if (list.isEmpty()) {
                                Show("没有找到相关用户");
                            } else {
                                TUser user = null;
                                Show("找到 " + list.size() + " 个用户");
                                adapter.replaceUser(list);
                                adapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                }
            }
        });
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationPoiList(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);

    }

    private void startBmobRealTimeDate() {
        data.start(this, new ValueEventListener() {
            @Override
            public void onConnectCompleted() {
                if (data.isConnected()) {
                    data.subTableUpdate("TUser");
                    init =true;
                }

            }

            @Override
            public void onDataChange(JSONObject arg0) {
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    if (data == null)
                        return;
                    JSONObject point = data.optJSONObject("loginPoint");


                    TUser user = new TUser(data.optString("username"),
                            data.optString("objectId"), data.optString("head"), data.optString("des"), point.optString("latitude"),
                            point.optString("longitude"));
                    if (!Config.tUser.getUsername().equals(user.getUsername())) {
                        if (map.isEmpty()) {
                            map.add(user);
                            ids.add(user.getUsername());
                            handler.sendEmptyMessage(GET_MAP);
                        } else {
                            if (ids.contains(user.getUsername())) {
                                map.set(ids.indexOf(user.getUsername()), user);
                            } else {
                                map.add(user);
                                ids.add(user.getUsername());
                            }
                            handler.sendEmptyMessage(GET_MAP);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null || mMapView == null)
            return;
        loc = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (first) {
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(loc);
            mBaiduMap.animateMapStatus(u);
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, BitmapDescriptorFactory
                    .fromResource(R.drawable.geo_icon)));
            if(NetUtils.hasNetwork(getBaseContext()))
            startBmobRealTimeDate();
            first = false;
        }
        if(Config.tUser!=null) {
            Config.tUser.setDes(des);
            Config.updateTUser(getBaseContext(), Config.tUser, loc);
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(init) {
            data.unsubTableUpdate("TUser");
        }
        isBack =true;
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        marker.recycle();


    }

    private int patchMarker(Marker marker) {
        for (int i = mapUser.size() - 1; i > -1; i--) {
            if (marker == mapUser.get(i))
                return i;
        }
        return -1;
    }




    private void markerPopUp(int tag) {
        if(!users.isEmpty())
            users.clear();
        users.add(map.get(tag));
        adapter.replaceUser(users);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int tag = -1;
        if ((tag = patchMarker(marker)) != -1) {
            markerPopUp(tag);
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    boolean isBack =false;

    @Override
    public void onitemClik(TUser user) {
        recyclerView.setVisibility(View.GONE);
        startActivity(new Intent(MyRadarActivity.this, UserCenter.class).putExtra("USER", user));
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case GET_MAP:
                    if (!isBack && mBaiduMap != null) {
                        mBaiduMap.clear();
                        for (int i = 0; i < map.size(); i++) {
                            TUser user = map.get(i);
                            OverlayOptions ooA = new MarkerOptions().position(new LatLng(user.getLoginPoint().getLatitude(),
                                    user.getLoginPoint().getLongitude())).icon(marker)
                                    .zIndex(9).draggable(false);
                            Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                            marker.setTitle(user.getUsername());
                            Bundle date = new Bundle();
                            date.putString("DES", user.getDes());
                            date.putString("HEAD", user.getHead());
                            marker.setExtraInfo(date);
                            if (mapUser.isEmpty()) {
                                mapUser.add(marker);
                                tags.add(marker.getTitle());
                            } else if (tags.contains(marker.getTitle())) {
                                mapUser.set(tags.indexOf(marker.getTitle()),marker);
                            } else {
                                mapUser.add(marker);
                                tags.add(marker.getTitle());
                            }
                        }
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBack =false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBack =false;
    }
}
