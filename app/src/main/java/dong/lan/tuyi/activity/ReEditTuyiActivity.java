package dong.lan.tuyi.activity;

import android.content.ContentValues;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.db.OfflineTuyi;
import dong.lan.tuyi.db.Tuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.InputTools;
import dong.lan.tuyi.utils.PicassoHelper;

/**
 * Created by 桂栋 on 2015/8/8.
 */
public class ReEditTuyiActivity extends BaseActivity implements View.OnClickListener, BDLocationListener ,OnGetGeoCoderResultListener{

    private UserTuyi tuyi;
    private EditText content;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker cur_Marker;
    private RelativeLayout markOffLayout;
    private EditText geoText;

    private int from;
    public static final int NORMAL = 1;
    public static final int OFFLINE = 2;


    BitmapDescriptor location_mark = BitmapDescriptorFactory.fromResource(R.drawable.location_mark);
    private boolean isFirst = true;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reedit_tuyi);
        if (getIntent().hasExtra("TUYI")) {
            tuyi = (UserTuyi) getIntent().getSerializableExtra("TUYI");
            from = NORMAL;
        } else if (getIntent().hasExtra("OFFLINE_TUYI")) {
            tuyi = (UserTuyi) getIntent().getSerializableExtra("OFFLINE_TUYI");
            from = OFFLINE;
        }
        if (tuyi == null) {
            Show("数据出错");
            finish();
        }
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        initView();

    }

    TextView bar_center;
    TextView bar_left;
    TextView bar_right;
    ImageView pic;
    CheckBox isOpen;
    long contentHas;
    GeoCoder mSearch;
    String locDes="";

    private void initView() {
        bar_left = (TextView) findViewById(R.id.bar_left);
        bar_center = (TextView) findViewById(R.id.bar_center);
        bar_right = (TextView) findViewById(R.id.bar_right);
        bar_right.setOnClickListener(this);
        pic = (ImageView) findViewById(R.id.reedit_pic);
        content = (EditText) findViewById(R.id.et_reedit);
        isOpen = (CheckBox) findViewById(R.id.reedit_isOpen_check);
        geoText = (EditText) findViewById(R.id.geoResultText);
        bar_right.setText("完成");
        if (from == NORMAL) {
            content.requestFocus();
            bar_center.setText("修改图忆");
            PicassoHelper.load(this,tuyi.gettPic())
                    .placeholder(R.drawable.gallery)
                    .into(pic);
        } else if (from == OFFLINE) {
            bar_center.setText("离线图忆");
            pic.setImageBitmap(BitmapFactory.decodeFile(tuyi.gettUri()));
            TextView add_mark = (TextView) findViewById(R.id.add_mark);
            add_mark.setOnClickListener(this);
            add_mark.setVisibility(View.VISIBLE);
            markOffLayout = (RelativeLayout) findViewById(R.id.markOffLayout);
            mMapView = (MapView) findViewById(R.id.bmapView);
            TextView mark_done = (TextView) findViewById(R.id.mark_done);
            mark_done.setOnClickListener(this);
            mBaiduMap = mMapView.getMap();
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
            mBaiduMap.setMapStatus(msu);
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
            LocationClient mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(this);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            option.setIsNeedAddress(true);
            option.setTimeOut(3000);
            mLocClient.setLocOption(option);
            mLocClient.start();
            mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (latLng != null) {
                        cur_Marker.setPosition(latLng);
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                                .location(latLng));
                    }
                }
            });
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(Marker marker) {
                    if (marker == cur_Marker)
                        cur_Marker.setPosition(marker.getPosition());
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    if (marker == cur_Marker) {
                        cur_Marker.setPosition(marker.getPosition());
                    }
                }

                @Override
                public void onMarkerDragStart(Marker marker) {

                }
            });
        }
        initData();

    }

    private void initData() {
        content.setText(tuyi.gettContent());
        contentHas = tuyi.gettContent().hashCode();
        if (tuyi.isPublic())
            isOpen.setChecked(true);
        else
            isOpen.setChecked(false);
    }

    private void done()
    {
        if (from==NORMAL && contentHas == content.getText().toString().hashCode() && isOpen.isChecked() == tuyi.isPublic() ) {
            Show("还没有修改呢");
        } else {
            if (from == NORMAL) {
                UserTuyi t = new UserTuyi();
                t.settContent(content.getText().toString());
                if (isOpen.isChecked())
                    t.setIsPublic(true);
                else
                    t.setIsPublic(false);
                t.update(ReEditTuyiActivity.this, tuyi.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ContentValues values = new ContentValues();
                        values.put(Tuyi.COLUME_NAME_CONTENT, content.getText().toString());
                        if (isOpen.isChecked())
                            values.put(Tuyi.COLUMN_NAME_IS_PUBLIC, "1");
                        else
                            values.put(Tuyi.COLUMN_NAME_IS_PUBLIC, "0");
                        DemoDBManager.getInstance().updateTuyi(tuyi.getObjectId(), values);
                        UserMainFragment.isChange = true;
                        Show("修改图忆成功");
                        finish();

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Show("修改失败：" + s);
                    }
                });
            } else if (from == OFFLINE) {

                if(geoText.getText().toString().equals(""))
                {
                    Show("请在屏幕底下输入位置信息");
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(OfflineTuyi.COLUMN_LAT, lat);
                values.put(OfflineTuyi.COLUMN_LNG, lng);
                values.put(OfflineTuyi.COLUMN_LOCDES,locDes);
                values.put(OfflineTuyi.COLUMN_CONTENT, content.getText().toString());
                if (isOpen.isChecked())
                    values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "1");
                else
                    values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "0");
                DemoDBManager.getInstance().updateOfflineTuyi(values, tuyi.getTime());
                Show("更改离线图忆完成");
                UploadOfflineTuyiFragment.hasChange=true;
            }
        }
    }

    public void  BarLeftClick(View view)
    {
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_right:
                InputTools.HideKeyboard(content);
                done();
                break;
            case R.id.bar_left:
                finish();
                break;
            case R.id.add_mark:
                Show("长按即可标记位置");
                InputTools.HideKeyboard(content);
                markOffLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.mark_done:
                //保存离线图忆的位置信息
                lat = cur_Marker.getPosition().latitude;
                lng = cur_Marker.getPosition().longitude;
                markOffLayout.setVisibility(View.GONE);
                InputTools.HideKeyboard(content);
                break;
        }
    }

    private void saveTuyi(final String url) {
        tuyi.settPic(url);
        tuyi.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Show("离线图忆保存成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Show("离线图忆保存失败 : " + s);
                saveTuyi(url);
                UploadOfflineTuyiFragment.hasChange=true;
            }
        });
    }

    String url;

    public void UploadTuyi(View view) {
        final BmobFile bmobFile = new BmobFile(new File(tuyi.gettUri()));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                url = bmobFile.getUrl();
                saveTuyi(url);
            }

            @Override
            public void onFailure(int i, String s) {
                Show("上传图片失败：" + s);
            }
        });
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        // map view 销毁后不在处理新接收的位置
        if (bdLocation == null || mMapView == null)
            return;
        if (isFirst) {
            geoText.setText(bdLocation.getAddrStr());
            isFirst = false;
            LatLng loc = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(loc);
            mBaiduMap.animateMapStatus(u);
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, BitmapDescriptorFactory
                    .fromResource(R.drawable.geo_icon)));
            OverlayOptions ooA = new MarkerOptions().position(loc).icon(location_mark)
                    .zIndex(9).draggable(true);
            cur_Marker = (Marker) (mBaiduMap.addOverlay(ooA));
            cur_Marker.setPerspective(true);
            cur_Marker.setDraggable(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (from == OFFLINE)
            mMapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (from == OFFLINE)
            mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        location_mark.recycle();
        if (from == OFFLINE)
            mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        Config.print("addr "+result.getAddress());
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            geoText.setText("抱歉，未能找到结果");
            locDes = "";
        }
        else {
            geoText.setText((locDes=result.getAddress()));
        }
    }
}
