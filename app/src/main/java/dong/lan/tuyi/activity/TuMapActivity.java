package dong.lan.tuyi.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.ClickPopRecycleAdapter;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.util.AsynImageLoader;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.MyImageAsyn;
import dong.lan.tuyi.utils.TimeUtil;


/**
 * Created by 桂栋 on 2015/7/18.
 */
public class TuMapActivity extends BaseActivity implements View.OnClickListener, SensorEventListener {


    public static final int TUYI_MARKER = 0;
    public static final int USER_MARKER = 1;
    public static final UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constant.DESCRIPTOR);
    public int clickTag = 0;
    private boolean goFetck = true;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private int topIconID[] = new int[]{R.id.switcher, R.id.add_tuyi, R.id.add_look_more, R.id.near_user};
    private int nearID[] = new int[]{R.id.near_za, R.id.near_shi, R.id.near_jing, R.id.near_qing, R.id.near_wan, R.id.near_all};
    private TextView topIcon[] = new TextView[4];
    private TextView nearIcon[] = new TextView[6];
    private Button search;
    private EditText searchEt;
    private List<Marker> userMarkers = new ArrayList<>();
    private List<TUser> nearUsers = new ArrayList<>();
    private List<UserTuyi> tuyiList = new ArrayList<>();
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;

    BitmapDescriptor btm_near = BitmapDescriptorFactory
            .fromResource(R.drawable.location_mark);
    BitmapDescriptor btm_public = BitmapDescriptorFactory
            .fromResource(R.drawable.mark_public);
    BitmapDescriptor btm_private = BitmapDescriptorFactory
            .fromResource(R.drawable.mark_private);
    BitmapDescriptor userIcon = BitmapDescriptorFactory.fromResource(R.drawable.user_icon);
    private boolean isFirstLoc = true;
    private LatLng loc;
    private LatLng lastLoc = new LatLng(89.99999, 89.99999);
    private int index = 0;
    private static float Tra;
    public static String Tusername;
    private FrameLayout popLayout,mapSearchLayout;
    private RecyclerView recyclerView;
    TextView bar_center, bar_left;
    SensorManager sensorManager;
    View markarPopView;
    PopupWindow markarPop;
    private ClickPopRecycleAdapter adapter;
    private MyHandler handler = new MyHandler();
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (run) {
                    try {
                        handler.sendEmptyMessage(RUN);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    });
    private MapStatusUpdate msu;

    private void initView() {
        q1.addWhereEqualTo("isPublic", true);
        bar_center = (TextView) findViewById(R.id.bar_center);
        bar_left = (TextView) findViewById(R.id.bar_left);
        bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bar_center.setText("图忆");
        findViewById(R.id.bar_right).setEnabled(false);
        for (int i = 0; i < 4; i++) {
            topIcon[i] = (TextView) findViewById(topIconID[i]);
            topIcon[i].setOnClickListener(this);
        }
        for (int i = 0; i < 6; i++) {
            nearIcon[i] = (TextView) findViewById(nearID[i]);
            nearIcon[i].setOnClickListener(this);
        }
        search = (Button) findViewById(R.id.map_search_bt);
        search.setOnClickListener(this);
        searchEt = (EditText) findViewById(R.id.map_search_et);
        popLayout = (FrameLayout) findViewById(R.id.clickPopLayout);
        mapSearchLayout = (FrameLayout) findViewById(R.id.map_bar);
        recyclerView = (RecyclerView) findViewById(R.id.clickPopList);
        GridLayoutManager layoutManager = new GridLayoutManager(TuMapActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
    }


    boolean isFromOffline = false;
    UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
            setContentView(mMapView);
            isFromOffline = true;
        } else {
            setContentView(R.layout.activity_tu_map);
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Tusername = TuApplication.getInstance().getUserName();
            Tra = Config.getPPI(this)[1] / 7;
            radius = Config.getPPI(this)[0] / 2;
            initView();
            markarPopView = LayoutInflater.from(this).inflate(R.layout.pop_show_click_marker, null);
            mMapView = (MapView) findViewById(R.id.bmapView);
            mBaiduMap = mMapView.getMap();
            uiSettings = mBaiduMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            msu = MapStatusUpdateFactory.zoomTo(14.0f);
            mBaiduMap.setMapStatus(msu);
            mBaiduMap.setMyLocationEnabled(true);
            findViewById(R.id.focus_loc).setOnClickListener(this);
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
            // 定位初始化
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setCoorType("bd09ll");
            option.setScanSpan(3000);
            option.setIsNeedLocationDescribe(true);
            option.setIsNeedAddress(true);
            mLocClient.setLocOption(option);
            mLocClient.start();

            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    boolean turnOn = false;
                    if (mBaiduMap.getMapStatus().zoom > 11) {
                        popList.clear();
                        for (int i = 0; i < tuyiList.size(); i++) {
                            if (Config.DistanceOfTwoPoints(marker.getPosition().latitude, marker.getPosition().longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 50) {
                                popList.add(tuyiList.get(i));
                            }
                        }
                        if (popList.size() > 5) {
                            adapter = new ClickPopRecycleAdapter(TuMapActivity.this, popList);
                            adapter.setPopItemClickListenner(new ClickPopRecycleAdapter.onPopItemClickListener() {
                                @Override
                                public void onPopItemClick(UserTuyi tuyi, int pos) {
                                    showClickMarkerInfo(tuyi);
                                    popLayout.setVisibility(View.GONE);
                                }
                            });
                            turnOn = true;
                            popLayout.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    if (!turnOn) {
                        if (marker.getExtraInfo() != null && marker.getExtraInfo().getSerializable("TU") != null) {
                            showClickMarkerInfo((UserTuyi) marker.getExtraInfo().getSerializable("TU"));
                        } else {
                            if (clickTag == TUYI_MARKER && marker.getExtraInfo() != null && marker.getExtraInfo().getSerializable("TUYI") != null) {
                                showClickMarkerInfo((UserTuyi) marker.getExtraInfo().getSerializable("TUYI"));
                            }
                            if (clickTag == USER_MARKER) {
                                int tag = markerClickMatch(marker);
                                startActivity(new Intent(TuMapActivity.this, UserCenter.class).putExtra("USER", nearUsers.get(tag)));
                                overridePendingTransition(R.anim.slide_out_to_left, R.anim.slide_in_from_right);
                            }
                        }
                    }
                    return true;
                }
            });
        }
        // 配置需要分享的相关平台
        configPlatforms();
    }


    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                Constant.QQ_APPID, Constant.QQ_APPKEY);
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, Constant.QQ_APPID, Constant.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void setShareContent(Bitmap img) {
        UMImage image = new UMImage(this, img);

        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent("来自图忆的分享");
        // 设置tencent分享内容
        mController.setShareMedia(tencent);
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                Constant.QQ_APPID, Constant.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent("我在图忆的足迹，你也来吧");
        mController.setShareImage(image);
        mController.setAppWebSite("http://tuyiapp.bmob.cn");
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("我在图忆的足迹，你也来吧");
        qzone.setTitle("图忆");
        qzone.setAppWebSite("http://tuyiapp.bmob.cn");
        qzone.setTargetUrl("http://tuyiapp.bmob.cn");
        qzone.setShareMedia(image);
        mController.setShareMedia(qzone);
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("我在图忆的足迹，你也来吧");
        qqShareContent.setTitle("图忆");
        qqShareContent.setShareMedia(image);
        qqShareContent.setAppWebSite("http://tuyiapp.bmob.cn");
        qqShareContent.setTargetUrl("http://tuyiapp.bmob.cn");
        mController.setShareMedia(qqShareContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private int markerClickMatch(Marker curMarker) {
        for (int i = 0; i < userMarkers.size(); i++) {
            if (userMarkers.get(i) == curMarker) {
                return i;
            }
        }
        return -1;
    }

    /*
    根据标签获取不同类型的图忆
     */
    private void NearTuyi(String nearTag) {
        if (loc == null) {
            Show("没有位置信息，稍后重试");
            return;
        }
        BmobGeoPoint point = new BmobGeoPoint(loc.longitude, loc.latitude);
        BmobQuery<UserTuyi> query = new BmobQuery<UserTuyi>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.addWhereNear("tPoint", point);
        if (!nearTag.equals("")) {
            BmobQuery<UserTuyi> query1 = new BmobQuery<UserTuyi>();
            query1.addWhereEqualTo("TAG", nearTag);
            BmobQuery<UserTuyi> query2 = new BmobQuery<UserTuyi>();
            query2.addWhereEqualTo("isPublic", true);
            List<BmobQuery<UserTuyi>> queries = new ArrayList<>();
            queries.add(query1);
            queries.add(query2);
            query.and(queries);
        } else {
            query.addWhereEqualTo("isPublic", true);
        }
        query.include("tUser");
        query.findObjects(TuMapActivity.this, new FindListener<UserTuyi>() {
            @Override
            public void onSuccess(List<UserTuyi> list) {
                if (!list.isEmpty()) {
                    Show("附近有 " + list.size() + " 个图忆");
                    tuyiList.clear();
                    mBaiduMap.clear();
                    for (int i = 0; i < list.size(); i++) {
                        tuyiList = list;
                        LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
                        OverlayOptions ooA = new MarkerOptions().position(p).icon(btm_near)
                                .zIndex(9).draggable(true);
                        Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("TUYI", list.get(i));
                        marker.setExtraInfo(bundle);
                        if(Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100)
                        {
                            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /*
    显示登陆用户的所有图忆到地图上
     */
    private void showMyAllTuyi() {
        if (tuyiList == null)
            tuyiList = new ArrayList<>();
        if (!tuyiList.isEmpty())
            tuyiList.clear();
        tuyiList = DemoDBManager.getInstance().getUserAllTuyi(Tusername);
        if (tuyiList == null || tuyiList.isEmpty()) {
            Show("本地记录啥纪录都没有撒，准备从网络后台获取");
            Config.preferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
            if (Config.tUser == null) {
                Show("请等待用户数据刷新成功后再试");
            } else {
                BmobQuery<UserTuyi> query = new BmobQuery<UserTuyi>();
                query.addWhereEqualTo("tUser", Config.tUser);
                query.order("-time,-createAt");
                query.include("tUser");
                query.findObjects(TuMapActivity.this, new FindListener<UserTuyi>() {
                    @Override
                    public void onSuccess(List<UserTuyi> list) {
                        if (list.isEmpty()) {
                            Show("无数据");
                        } else {
                            showMarkerOfUser(list);
                            tuyiList = list;
                            DemoDBManager.getInstance().saveTuyiFromNet(list);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        } else {
            showMarkerOfUser(tuyiList);
        }

    }


    /*
    点击地图标记后的pop
     */
    private void showClickMarkerInfo(final UserTuyi tuyi) {
        if (tuyi == null) {
            Show("用户数据加载失败");
            return;
        }
        String time = tuyi.getCreatedAt();
        String avatar = tuyi.gettUser().getHead();
        if (time == null || time.equals(""))
            time = tuyi.getTime();
        if (markarPop == null)
            markarPop = new PopupWindow(markarPopView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        markarPop.setOutsideTouchable(true);
        final ImageView head = (ImageView) markarPopView.findViewById(R.id.pop_head);
        ImageView off = (ImageView) markarPopView.findViewById(R.id.shut_down_pop);
        final ImageView pic = (ImageView) markarPopView.findViewById(R.id.pop_pic);
        TextView content = (TextView) markarPopView.findViewById(R.id.pop_content);
        TextView addFavorite = (TextView) markarPopView.findViewById(R.id.pop_add_favorite);
        TextView tag = (TextView) markarPopView.findViewById(R.id.pop_tuyiTag);
        tag.setText(tuyi.getTAG());
        content.setText(tuyi.gettContent() + "\n" + time);
        if (avatar != null && !avatar.equals(""))
            AsynImageLoader.getInstance().showImageAsyn(head, avatar, R.drawable.default_avatar, 960, 720, AsynImageLoader.ForHead);

        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = false;
                BmobRelation relation = new BmobRelation();
                relation.add(tuyi);
                Config.tUser.setFavoraite(relation);
                Config.tUser.update(TuMapActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Show("收藏成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Show("收藏失败 T _ T");
                    }
                });

                if (Config.tUser != null) {
                    UserTuyi userTuyi = new UserTuyi();
                    userTuyi.setObjectId(tuyi.getObjectId());
                    if (tuyi.getZan() == null)
                        userTuyi.setZan(1);
                    else
                        userTuyi.setZan(tuyi.getZan() + 1);
                    BmobRelation r = new BmobRelation();
                    relation.add(Config.tUser);
                    userTuyi.setLikes(r);
                    userTuyi.update(TuMapActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                }
            }
        });
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = false;
                startActivity(new Intent(TuMapActivity.this, UserCenter.class).putExtra("USER", tuyi.gettUser()));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = false;
                if (markarPop != null && markarPop.isShowing())
                    markarPop.dismiss();
            }
        });
        String url = tuyi.gettPic();
        if (url != null)
            new MyImageAsyn(pic, MyImageAsyn.NORMAL).execute(url);
        else
            Show("图片加载链接为空");

        if (markarPop != null) {
            markarPop.setAnimationStyle(R.style.Animations_PopAnim);
            markarPop.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
        }
    }

    private void showMarkerOfUser(List<UserTuyi> list) {
        for (int i = 0; i < list.size(); i++) {
            LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
            OverlayOptions ooA;
            if(Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100)
            {
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
            }
            if (list.get(i).isPublic())
                ooA = new MarkerOptions().position(p).icon(btm_public)
                        .zIndex(9).draggable(false).title(list.get(i).gettContent());
            else
                ooA = new MarkerOptions().position(p).icon(btm_private)
                        .zIndex(9).draggable(false).title(list.get(i).gettContent());
            Bundle bundle = new Bundle();
            bundle.putSerializable("TUYI", list.get(i));
            Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
            marker.setExtraInfo(bundle);
        }
    }

    boolean isAnimOpen = false;
    boolean popStart = false;
    int radius = 200;

    private void showNearUser() {
        if (loc == null)
            return;
        BmobGeoPoint point = new BmobGeoPoint(loc.longitude, loc.latitude);
        BmobQuery<TUser> query = new BmobQuery<TUser>();
        query.addWhereWithinRadians("loginPoint", point, 10000);
        query.addWhereEqualTo("publicMyPoint", true);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(this, new FindListener<TUser>() {
            @Override
            public void onSuccess(List<TUser> list) {
                if (!list.isEmpty()) {
                    nearUsers = list;
                    if (!userMarkers.isEmpty())
                        userMarkers.clear();
                    for (int i = 0; i < list.size(); i++) {
                        LatLng p = new LatLng(list.get(i).getLoginPoint().getLatitude(), list.get(i).getLoginPoint().getLongitude());
                        OverlayOptions ooA = new MarkerOptions().position(p).icon(userIcon)
                                .zIndex(9).draggable(false);
                        userMarkers.add((Marker) (mBaiduMap.addOverlay(ooA)));
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void startPop() {
        if (popStart) {
            ObjectAnimator.ofFloat(nearIcon[0], "translationX", 0, 1f).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[0], "translationY", -radius, 1f).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[0], "rotation", 360f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[0], "scaleX", 1.2f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[0], "scaleY", 1.2f, 1f).setDuration(300).start();
            for (int i = 1; i < 5; i++) {
                ObjectAnimator.ofFloat(nearIcon[i], "translationX", (float) (radius * Math.sin(Math.PI / 8 * i)), 1f).setDuration(400).start();
                ObjectAnimator.ofFloat(nearIcon[i], "translationY", -(float) (radius * Math.cos(Math.PI / 8 * i)), 1f).setDuration(400).start();
                ObjectAnimator.ofFloat(nearIcon[i], "rotation", 360f, 1f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(nearIcon[i], "scaleX", 1.2f, 1f).setDuration(300).start();
                ObjectAnimator.ofFloat(nearIcon[i], "scaleY", 1.2f, 1f).setDuration(300).start();
            }
            ObjectAnimator.ofFloat(nearIcon[5], "translationX", (float) (radius / 2 * Math.sin(Math.PI / 4)), 1f).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[5], "translationY", -(float) (radius / 2 * Math.cos(Math.PI / 4)), 1f).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[5], "rotation", 360f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[5], "scaleX", 1.2f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[5], "scaleY", 1.2f, 1f).setDuration(300).start();
            popStart = false;
        } else {
            ObjectAnimator.ofFloat(nearIcon[0], "translationX", 1f, 0).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[0], "translationY", 1f, -radius).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[0], "rotation", 1f, 360f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[0], "scaleX", 1f, 1.2f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[0], "scaleY", 1f, 1.2f).setDuration(300).start();
            for (int i = 1; i < 5; i++) {
                ObjectAnimator.ofFloat(nearIcon[i], "translationX", 1f, (float) (radius * Math.sin(Math.PI / 8 * i))).setDuration(400).start();
                ObjectAnimator.ofFloat(nearIcon[i], "translationY", 1f, -(float) (radius * Math.cos(Math.PI / 8 * i))).setDuration(400).start();
                ObjectAnimator.ofFloat(nearIcon[i], "rotation", 1f, 360f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(nearIcon[i], "scaleX", 1f, 1.2f).setDuration(300).start();
                ObjectAnimator.ofFloat(nearIcon[i], "scaleY", 1f, 1.2f).setDuration(300).start();
            }
            ObjectAnimator.ofFloat(nearIcon[5], "translationX", 1f, (float) (radius / 2 * Math.sin(Math.PI / 4))).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[5], "translationY", 1f, -(float) (radius / 2 * Math.cos(Math.PI / 4))).setDuration(400).start();
            ObjectAnimator.ofFloat(nearIcon[5], "rotation", 1f, 360f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[5], "scaleX", 1f, 1.2f).setDuration(300).start();
            ObjectAnimator.ofFloat(nearIcon[5], "scaleY", 1f, 1.2f).setDuration(300).start();
            popStart = true;
        }
    }

    BmobQuery<UserTuyi> q1 = new BmobQuery<UserTuyi>();
    BmobQuery<UserTuyi> q2 = new BmobQuery<UserTuyi>();
    BmobQuery<UserTuyi> q3 = new BmobQuery<UserTuyi>();
    BmobQuery<UserTuyi> or =new BmobQuery<UserTuyi>();
    List<BmobQuery<UserTuyi>> ors= new ArrayList<>();
    List<BmobQuery<UserTuyi>> ands= new ArrayList<>();
    BmobQuery<UserTuyi> query = new BmobQuery<UserTuyi>();
    private void searchTuyi()
    {
        String s =searchEt.getText().toString();
        if(s.matches("^[\u4e00-\u9fa5]*$"))
        {
            Show("开始搜索附近");
            q2.addWhereContains("tContent", s);
            q3.addWhereContains("tTittle",s);
            ors.add(q2);
            ors.add(q3);
            or.or(ors);
            ands.add(q1);
            ands.add(or);
            query.and(ands);
            query.setLimit(50);
            query.order("-createAt,-zan");
            query.findObjects(this, new FindListener<UserTuyi>() {
                @Override
                public void onSuccess(List<UserTuyi> list) {
                    if(list.isEmpty())
                    {
                        Show("没有相似图忆存在");
                    }else
                    {
                        mBaiduMap.clear();
                        Show("找到"+list.size()+" 个图忆");
                        for (int i = 0; i < list.size(); i++) {
                            tuyiList = list;
                            LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
                            OverlayOptions ooA = new MarkerOptions().position(p).icon(btm_near)
                                    .zIndex(9).draggable(true);
                            Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("TUYI", list.get(i));
                            marker.setExtraInfo(bundle);
                            if(Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100)
                            {
                                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                            }
                        }
                        msu = MapStatusUpdateFactory.zoomTo(4.0f);
                        mBaiduMap.setMapStatus(msu);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Show("搜索失败");
                }
            });
        }else
        {
            Show("只允许输入汉字");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_search_bt:
                searchTuyi();
                break;
            case R.id.focus_loc:
                startPop();
                break;
            case R.id.near_za:
                NearTuyi("杂记");
                startPop();
                break;
            case R.id.near_shi:
                NearTuyi("美食");
                startPop();
                break;
            case R.id.near_jing:
                NearTuyi("美景");
                startPop();
                break;
            case R.id.near_qing:
                NearTuyi("人情");
                startPop();
                break;
            case R.id.near_wan:
                NearTuyi("乐玩");
                startPop();
                break;
            case R.id.near_user:
                clickTag = USER_MARKER;
                mBaiduMap.clear();
                showNearUser();
                break;
            case R.id.near_all:
                clickTag = TUYI_MARKER;
                index = Config.NEAR;
                mBaiduMap.clear();
                NearTuyi("");
                break;
            case R.id.add_look_more:
                clickTag = TUYI_MARKER;
                index = Config.MY;
                mBaiduMap.clear();
                showMyAllTuyi();
                break;
            case R.id.add_tuyi:
                if (!mLocClient.isStarted()) {
                    mLocClient.start();
                }
                startAdd();
                break;
            case R.id.switcher:
                if (!mLocClient.isStarted()) {
                    mLocClient.start();
                }
                dropPop();
                break;
        }
    }

    private void dropPop() {
        if (!isAnimOpen) {
            ObjectAnimator.ofFloat(topIcon[0], "scaleX", 1f, 0.7f).setDuration(500).start();
            ObjectAnimator.ofFloat(topIcon[0], "scaleY", 1f, 0.7f).setDuration(500).start();

            for (int i = 1; i < 4; i++) {
                ObjectAnimator.ofFloat(topIcon[i], "translationY", 1f, Tra * i).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "rotation", 1f, 360f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleX", 1f, 1.2f).setDuration(300).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleY", 1f, 1.2f).setDuration(300).start();
            }
            isAnimOpen = true;
            mapSearchLayout.setVisibility(View.GONE);
        } else {
            ObjectAnimator.ofFloat(topIcon[0], "scaleX", 0.7f, 1f).setDuration(500).start();
            ObjectAnimator.ofFloat(topIcon[0], "scaleY", 0.7f, 1f).setDuration(500).start();

            for (int i = 3; i > 0; i--) {
                ObjectAnimator.ofFloat(topIcon[i], "translationY", Tra * i, 1f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "rotation", 360f, 1f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleX", 1.2f, 1f).setDuration(200).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleY", 1.2f, 1f).setDuration(200).start();
            }
            isAnimOpen = false;
            mapSearchLayout.setVisibility(View.VISIBLE);
        }
    }

    private void snap() {
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                @SuppressLint("SdCardPath") File file = new File("/mnt/sdcard/Tuyi/" + TimeUtil.getCurrentTime(TimeUtil.FORMAT_NORMAL) + ".png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (snapshot.compress(
                            Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                    setShareContent(BitmapFactory.decodeFile(file.toString()));
                    mController.getConfig().setPlatforms(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
                    mController.openShare(TuMapActivity.this, false);
                    isSnaping = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        isSnaping = true;
        Show("正在截取屏幕图片...");

    }

    boolean isSnaping = false;
    boolean first = true;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float values[] = event.values;
        if (!isSnaping && (Math.abs(values[0]) > 35 || Math.abs(values[1]) > 35 || Math.abs(values[2]) > 35)) {
            if (first) {
                first = false;
                thread.start();
            }
            if (run) {
                run = false;
                try {
                    thread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                run = true;
                thread.notify();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    String addr = "";

    private void startAdd() {
        Intent intent = new Intent(this, AddTuyiActivity.class);
        intent.putExtra("ADDRESS", addr);
        intent.putExtra("LAT", loc.latitude);
        intent.putExtra("LNG", loc.longitude);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

//    /*
//    点击定位的点
//     */
//    @Override
//    public boolean onMyLocationClick() {
//
//        startAdd();
//        return true;
//    }

    List<UserTuyi> popList = new ArrayList<>();

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            loc = new LatLng(location.getLatitude(),
                    location.getLongitude());
            addr = location.getAddrStr();
            MyLocationData locData = new MyLocationData.Builder()
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (goFetck && Config.DistanceOfTwoPoints(lastLoc.latitude, lastLoc.longitude, loc.latitude, loc.longitude) > 5000) {
                goFetck = false;
                BmobQuery<UserTuyi> query = new BmobQuery<>();
                query.addWhereNear("tPoint", new BmobGeoPoint(loc.longitude, loc.latitude));
                query.order("-zan,-createAt");
                query.include("tUser");
                query.setLimit(10);
                query.findObjects(TuMapActivity.this, new FindListener<UserTuyi>() {
                    @Override
                    public void onSuccess(final List<UserTuyi> list) {
                        if (!list.isEmpty()) {
                            lastLoc = loc;
                            goFetck = true;
                            final Dialog dialog = new android.app.AlertDialog.Builder(TuMapActivity.this).create();
                            dialog.setCancelable(true);
                            dialog.show();
                            dialog.getWindow().setContentView(R.layout.recomman_dialog);
                            TextView text = (TextView) dialog.findViewById(R.id.recommend_text);
                            text.setText("为你推荐了附近的" + list.size() + "个图忆哟~");
                            TextView look = (TextView) dialog.findViewById(R.id.recommend_look);
                            look.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < list.size(); i++) {
                                        tuyiList = list;
                                        LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
                                        OverlayOptions ooA = new MarkerOptions().position(p).icon(btm_near)
                                                .zIndex(11).draggable(true);
                                        Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                                        marker.setPerspective(true);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("TU", list.get(i));
                                        marker.setExtraInfo(bundle);
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.nav));
                                        if(Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100)
                                        {
                                            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                                        }
                                        dialog.dismiss();


                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        goFetck = true;
                    }
                });
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                loc = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(loc);
                mBaiduMap.animateMapStatus(u);
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, BitmapDescriptorFactory
                        .fromResource(R.drawable.geo_icon)));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(searchEt.getText().length()>0)
            {
                searchEt.getText().clear();
            }
            if (popLayout != null && popLayout.getVisibility() == View.VISIBLE) {
                popLayout.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFromOffline) {
            btm_public.recycle();
            btm_private.recycle();
            userIcon.recycle();
            btm_near.recycle();
            mLocClient.stop();
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mMapView = null;
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFromOffline)
            sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (!isFromOffline)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    boolean run = false;
    final static int RUN = 0;
    int tag = 0;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RUN:
                    if (tag >= tuyiList.size()) {
                        run = false;
                        tag = 0;
                    }
                    else
                        showClickMarkerInfo(tuyiList.get(tag++));

            }
            super.handleMessage(msg);
        }
    }
}
