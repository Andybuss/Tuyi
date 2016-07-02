package dong.lan.tuyi.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.ClickPopRecycleAdapter;
import dong.lan.tuyi.adapter.TimeLineAdapter;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.CircleTransformation;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  7/1/2016  00:10.
 * Email: 760625325@qq.com
 */
public class FragmentTuMap extends Fragment implements View.OnClickListener,
        TimeLineAdapter.TimeLineItemClickListener{

    public static final int TUYI_MARKER = 0;
    public static final int USER_MARKER = 1;
    public int clickTag = 0;
    private boolean goFetck = true;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private int topIconID[] = new int[]{R.id.switcher, R.id.add_tuyi, R.id.add_look_more, R.id.near_user,R.id.capture};
    private int nearID[] = new int[]{R.id.near_za, R.id.near_shi, R.id.near_jing, R.id.near_qing, R.id.near_wan, R.id.near_all};
    private TextView topIcon[] = new TextView[5];
    private TextView nearIcon[] = new TextView[6];
    private Button search;
    private TextView nearClose;
    private EditText searchEt;
    private ImageView openTimeLine;
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
    private static float Tra;
    public static String Tusername;
    private FrameLayout popLayout;
    private RecyclerView recyclerView;
    private RecyclerView timeLineView;
    private DrawerLayout tuyiMapDrawer;
    SensorManager sensorManager;
    View markarPopView;
    PopupWindow markarPop;
    private ClickPopRecycleAdapter adapter;
    private TimeLineAdapter timeLineAdapter;
    boolean isAnimOpen = false;
    boolean popStart = false;
    int radius = 200;
    boolean isFromOffline = false;
    UiSettings uiSettings;
    private MapStatusUpdate msu;
    private String BUNDLE_TAG="TUYI";
    boolean run = false;
    final static int RUN = 0;
    int tag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tu_map,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        Tusername = TuApplication.getInstance().getUserName();
        Tra = Config.getPPI(getActivity())[1] / 7;
        radius = Config.getPPI(getActivity())[0] / 2;
        initView();
        markarPopView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_show_click_marker, null);
        mMapView = (MapView) getView().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);
        getView().findViewById(R.id.focus_loc).setOnClickListener(this);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
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
                        adapter = new ClickPopRecycleAdapter(getActivity(), popList);
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
                        if (clickTag == TUYI_MARKER && marker.getExtraInfo() != null && marker.getExtraInfo().getSerializable(BUNDLE_TAG) != null) {
                            showClickMarkerInfo((UserTuyi) marker.getExtraInfo().getSerializable(BUNDLE_TAG));
                        }
                        if (clickTag == USER_MARKER) {
                            int tag = markerClickMatch(marker);
                            startActivity(new Intent(getActivity(), UserCenter.class).putExtra("USER", nearUsers.get(tag)));
                        }
                    }
                }
                return true;
            }
        });
    }


    private void initView() {
        q1.addWhereEqualTo("isPublic", true);


        for (int i = 0; i < 5; i++) {
            topIcon[i] = (TextView) getView().findViewById(topIconID[i]);
            topIcon[i].setOnClickListener(this);
        }
        for (int i = 0; i < 6; i++) {
            nearIcon[i] = (TextView) getView().findViewById(nearID[i]);
            nearIcon[i].setOnClickListener(this);
        }
        openTimeLine = (ImageView) getView().findViewById(R.id.open_time_line);
        openTimeLine.setOnClickListener(this);
        nearClose = (TextView) getView().findViewById(R.id.near_close);
        nearClose.setOnClickListener(this);
        tuyiMapDrawer = (DrawerLayout) getView().findViewById(R.id.tuyi_map_drawer);
        tuyiMapDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if(timeLineAdapter==null){
                    tuyiList = DemoDBManager.getInstance().getUserAllTuyi(Tusername);
                    timeLineAdapter = new TimeLineAdapter(getActivity(),tuyiList);
                    timeLineAdapter.setItemClickListener(FragmentTuMap.this);
                    timeLineView.setAdapter(timeLineAdapter);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        search = (Button) getView().findViewById(R.id.map_search_bt);
        search.setOnClickListener(this);
        searchEt = (EditText) getView().findViewById(R.id.map_search_et);
        popLayout = (FrameLayout) getView().findViewById(R.id.clickPopLayout);
        timeLineView = (RecyclerView) getView().findViewById(R.id.drawer_tuyi_map_list);
        timeLineView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView = (RecyclerView) getView().findViewById(R.id.clickPopList);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
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
        BmobQuery<UserTuyi> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.addWhereNear("tPoint", point);
        if (!nearTag.equals("")) {
            BmobQuery<UserTuyi> query1 = new BmobQuery<>();
            query1.addWhereEqualTo("TAG", nearTag);
            BmobQuery<UserTuyi> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("isPublic", true);
            List<BmobQuery<UserTuyi>> queries = new ArrayList<>();
            queries.add(query1);
            queries.add(query2);
            query.and(queries);
        } else {
            query.addWhereEqualTo("isPublic", true);
        }
        query.include("tUser");
        query.findObjects(new FindListener<UserTuyi>() {

            @Override
            public void done(List<UserTuyi> list, BmobException e) {
                if(e==null){
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
                            bundle.putSerializable(BUNDLE_TAG, list.get(i));
                            marker.setExtraInfo(bundle);
                            if (Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100) {
                                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                            }
                        }
                    }
                }else{
                    Show(e.getMessage());
                }
            }
        });
    }

    private void Show(String s) {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
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
            Config.preferences = getActivity().getSharedPreferences(Config.prefName, Context.MODE_PRIVATE);
            if (Config.tUser == null) {
                Show("请等待用户数据刷新成功后再试");
            } else {
                BmobQuery<UserTuyi> query = new BmobQuery<>();
                query.addWhereEqualTo("tUser", Config.tUser);
                query.order("-time,-createAt");
                query.include("tUser");
                query.findObjects(new FindListener<UserTuyi>() {
                    @Override
                    public void done(List<UserTuyi> list, BmobException e) {
                        if(e==null){
                            if (list.isEmpty()) {
                                Show("无数据");
                            } else {
                                showMarkerOfUser(list);
                                tuyiList = list;
                                DemoDBManager.getInstance().saveTuyiFromNet(list);
                            }
                        }else{
                            Show(e.getMessage());
                        }
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
            PicassoHelper.load(getActivity(),avatar)
                    .resize(100, 100)
                    .transform(new CircleTransformation(50))
                    .placeholder(R.drawable.default_avatar)
                    .into(head);

        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = false;
                BmobRelation relation = new BmobRelation();
                relation.add(tuyi);
                Config.tUser.setFavoraite(relation);
                Config.tUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Show("收藏成功");
                        }else{
                            Show("收藏失败 T _ T");
                        }

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
                    userTuyi.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                        }
                    });

                }
            }
        });
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = false;
                startActivity(new Intent(getActivity(), UserCenter.class).putExtra("USER", tuyi.gettUser()));
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
            PicassoHelper.load(getActivity(), url)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.chat_error_item_bg)
                    .into(pic);
        else
            Show("图片加载链接为空");

        if (markarPop != null) {
            markarPop.setAnimationStyle(R.style.Animations_PopAnim);
            markarPop.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
        }
    }

    View popView;
    ImageView popTuyiImageView;
    TextView popTuyiText;
    private void ShowPopView(UserTuyi tuyi){
        if(popView==null){
            popView =LayoutInflater.from(getActivity()).inflate(R.layout.view_tuyi_pop,null);
            popTuyiText = (TextView) popView.findViewById(R.id.pop_tuyi_text);
            popTuyiImageView = (ImageView) popView.findViewById(R.id.pop_tuyi_img);
        }
        PicassoHelper.load(getActivity(),tuyi.gettPic()).into(popTuyiImageView);
        popTuyiText.setText(tuyi.gettContent());
        InfoWindow infoWindow = new InfoWindow(popView,
                new LatLng(tuyi.gettPoint().getLatitude(),tuyi.gettPoint().getLongitude()),
                -47);
        mBaiduMap.showInfoWindow(infoWindow);
        msu = MapStatusUpdateFactory.zoomTo(20.0f);
        mBaiduMap.setMapStatus(msu);

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(tuyi.gettPoint().getLatitude(),
                tuyi.gettPoint().getLongitude()));
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, BitmapDescriptorFactory
                .fromResource(R.drawable.location_mark)));

        popTuyiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
            }
        });
    }

    private void showMarkerOfUser(List<UserTuyi> list) {
        if(timeLineAdapter==null){
            timeLineAdapter = new TimeLineAdapter(getActivity(),list);
            timeLineAdapter.setItemClickListener(this);
            timeLineView.setAdapter(timeLineAdapter);
        }
        for (int i = 0; i < list.size(); i++) {
            LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
            OverlayOptions ooA;
            if (Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100) {
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
            }
            if (list.get(i).isPublic())
                ooA = new MarkerOptions().position(p).icon(btm_public)
                        .zIndex(9).draggable(false).title(list.get(i).gettContent());
            else
                ooA = new MarkerOptions().position(p).icon(btm_private)
                        .zIndex(9).draggable(false).title(list.get(i).gettContent());
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_TAG, list.get(i));
            Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
            marker.setExtraInfo(bundle);
        }
    }

    private void showNearUser() {
        if (loc == null)
            return;
        BmobGeoPoint point = new BmobGeoPoint(loc.longitude, loc.latitude);
        BmobQuery<TUser> query = new BmobQuery<>();
        query.addWhereWithinRadians("loginPoint", point, 10000);
        query.addWhereEqualTo("publicMyPoint", true);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<TUser>() {
            @Override
            public void done(List<TUser> list, BmobException e) {
                if(e==null){
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
                }else{
                    Show(e.getMessage());
                }
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

    BmobQuery<UserTuyi> q1 = new BmobQuery<>();
    BmobQuery<UserTuyi> q2 = new BmobQuery<>();
    BmobQuery<UserTuyi> q3 = new BmobQuery<>();
    BmobQuery<UserTuyi> or = new BmobQuery<>();
    List<BmobQuery<UserTuyi>> ors = new ArrayList<>();
    List<BmobQuery<UserTuyi>> ands = new ArrayList<>();
    BmobQuery<UserTuyi> query = new BmobQuery<>();

    private void searchTuyi() {
        String s = searchEt.getText().toString();
        if (s.matches("^[\u4e00-\u9fa5]*$")) {
            Show("开始搜索附近");
            q2.addWhereContains("tContent", s);
            q3.addWhereContains("tTittle", s);
            ors.add(q2);
            ors.add(q3);
            or.or(ors);
            ands.add(q1);
            ands.add(or);
            query.and(ands);
            query.setLimit(50);
            query.order("-createAt,-zan");
            query.findObjects(new FindListener<UserTuyi>() {
                @Override
                public void done(List<UserTuyi> list, BmobException e) {
                    if(e==null){
                        if (list.isEmpty()) {
                            Show("没有相似图忆存在");
                        } else {
                            mBaiduMap.clear();
                            Show("找到" + list.size() + " 个图忆");
                            for (int i = 0; i < list.size(); i++) {
                                tuyiList = list;
                                LatLng p = new LatLng(list.get(i).gettPoint().getLatitude(), list.get(i).gettPoint().getLongitude());
                                OverlayOptions ooA = new MarkerOptions().position(p).icon(btm_near)
                                        .zIndex(9).draggable(true);
                                Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(BUNDLE_TAG, list.get(i));
                                marker.setExtraInfo(bundle);
                                if (Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100) {
                                    mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                                }
                            }
                            msu = MapStatusUpdateFactory.zoomTo(4.0f);
                            mBaiduMap.setMapStatus(msu);
                        }
                    }else{
                        Show("搜索失败"+e.getMessage());
                    }
                }
            });
        } else {
            Show("只允许输入汉字");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.near_close:
                popLayout.setVisibility(View.GONE);
                break;
            case R.id.open_time_line:
                tuyiMapDrawer.openDrawer(GravityCompat.START);
                break;
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
            case R.id.capture:
                snap();
                break;
            case R.id.near_user:
                clickTag = USER_MARKER;
                mBaiduMap.clear();
                showNearUser();
                break;
            case R.id.near_all:
                clickTag = TUYI_MARKER;
                mBaiduMap.clear();
                NearTuyi("");
                break;
            //在地图上显示自己全部的图忆
            case R.id.add_look_more:
                clickTag = TUYI_MARKER;
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

            for (int i = 1; i < 5; i++) {
                ObjectAnimator.ofFloat(topIcon[i], "translationY", 1f, Tra * i).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "rotation", 1f, 360f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleX", 1f, 1.2f).setDuration(300).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleY", 1f, 1.2f).setDuration(300).start();
            }
            isAnimOpen = true;
        } else {
            ObjectAnimator.ofFloat(topIcon[0], "scaleX", 0.7f, 1f).setDuration(500).start();
            ObjectAnimator.ofFloat(topIcon[0], "scaleY", 0.7f, 1f).setDuration(500).start();

            for (int i = 4; i > 0; i--) {
                ObjectAnimator.ofFloat(topIcon[i], "translationY", Tra * i, 1f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "rotation", 360f, 1f).setDuration(100 * i).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleX", 1.2f, 1f).setDuration(200).start();
                ObjectAnimator.ofFloat(topIcon[i], "scaleY", 1.2f, 1f).setDuration(200).start();
            }
            isAnimOpen = false;
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
                    isSnaping = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        isSnaping = true;
        Show("正在截取屏幕图片...");

    }

    boolean isSnaping = false;
    String addr = "";



    private void startAdd() {
        Intent intent = new Intent(getActivity(), AddTuyiActivity.class);
        intent.putExtra("ADDRESS", addr);
        intent.putExtra("LAT", loc.latitude);
        intent.putExtra("LNG", loc.longitude);
        startActivity(intent);
    }


    List<UserTuyi> popList = new ArrayList<>();

    /*
    侧滑抽屉中列表点击事件回调
     */
    @Override
    public void onTimeLineItemClick(UserTuyi tuyi, int pos) {
        tuyiMapDrawer.closeDrawer(GravityCompat.START);
        ShowPopView(tuyi);
        Show(tuyi.gettContent());
    }



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
            query.findObjects(new FindListener<UserTuyi>() {
                @Override
                public void done(final List<UserTuyi> list, BmobException e) {
                    if(e==null){
                        if (!list.isEmpty()) {
                            lastLoc = loc;
                            goFetck = true;
                            final Dialog dialog = new android.app.AlertDialog.Builder(getActivity()).create();
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
                                        if (Config.DistanceOfTwoPoints(loc.latitude, loc.longitude, tuyiList.get(i).gettPoint().getLatitude(), tuyiList.get(i).gettPoint().getLongitude()) < 100) {
                                            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                                        }
                                        dialog.dismiss();


                                    }
                                }
                            });
                        }
                    }else{
                        goFetck = true;
                    }
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
}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

}
