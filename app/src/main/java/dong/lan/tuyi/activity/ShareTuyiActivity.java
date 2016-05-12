package dong.lan.tuyi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.utils.DeviceUtils;

import java.io.File;

import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.util.FileUilt;
import dong.lan.tuyi.utils.PicassoHelper;

/**
 * Created by 桂栋 on 2015/8/13.
 */
public class ShareTuyiActivity extends BaseActivity{


    TextView bar_left;
    TextView bar_center;
    TextView bar_right;
    private TextView textCount;
    EditText contetn;
    ImageView img;
    UserTuyi tuyi;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_tuyi_avitity);
        if(getIntent().hasExtra("SHARE_TUYI"))
        {
            tuyi= (UserTuyi) getIntent().getSerializableExtra("SHARE_TUYI");
            if(tuyi==null)
                finish();
        }
        else
        finish();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("分享中...");
        initView();
    }


    String addr ="";
    Location location;
    private void initView() {
        bar_center = (TextView) findViewById(R.id.bar_center);
        bar_left = (TextView) findViewById(R.id.bar_left);
        bar_right = (TextView) findViewById(R.id.bar_right);
        contetn = (EditText) findViewById(R.id.share_content);
        img = (ImageView) findViewById(R.id.share_image);
        bar_center.setText("分享图忆");
        bar_right.setText("分享  ");
        textCount = (TextView) findViewById(R.id.textCount);
        contetn.setText(tuyi.gettContent());
        contetn.setFocusableInTouchMode(true);
        contetn.setMinimumHeight(DeviceUtils.dp2px(this, 150.0F));
        contetn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        contetn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(contetn.getText().toString().length()>300)
                {
                    textCount.setText("超出字数+"+(contetn.getText().toString().length()-300));
                }else
                {
                    textCount.setText((contetn.getText().toString().length())+"/"+300);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(contetn.getText().toString().length()>300)
                {
                    textCount.setText("超出字数+"+(contetn.getText().toString().length()-300));
                }else
                {
                    textCount.setText((contetn.getText().toString().length())+"/"+300);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        PicassoHelper.load(this,tuyi.gettPic())
                .placeholder(R.drawable.gallery)
                .into(img);
        initData();
    }
    



    private void showAtFriendsDialog() {

    }
    GeoCoder mSearch;
    boolean isGeoGet =false;
    Uri uri;
    private void initData()
    {

        final LatLng latLng = new LatLng(tuyi.gettPoint().getLatitude(), tuyi.gettPoint().getLongitude());
        mSearch = GeoCoder.newInstance();
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng));
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Show("抱歉，未能找到结果");
                    return;
                }
                addr = reverseGeoCodeResult.getAddress();
                location = new Location("gps");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                isGeoGet = true;
            }
        });
        File cacheFile = FileUilt.getCacheFile(tuyi.gettPic());
        if (cacheFile.exists()) {
            uri = Uri.fromFile(cacheFile);

        }
    }
    private void shareFeed()
    {
        if(contetn.getText().toString().length()>300)
        {
            Show("字数不要超过300哟");
            return;
        }
        progressDialog.show();
        final FeedItem feedItem = new FeedItem();
        feedItem.text = contetn.getText().toString();
        feedItem.imageUrls.add(new ImageItem(uri.getPath(), uri.getPath(), uri.getPath()));
        feedItem.location = location;
        feedItem.locationAddr = addr;
        feedItem.creator = CommConfig.getConfig().loginedUser;
        TuApplication.communitySDK.postFeed(feedItem, new Listeners.SimpleFetchListener<FeedItemResponse>() {
            @Override
            public void onComplete(FeedItemResponse feedItemResponse) {
                Show("发布成功");
                progressDialog.dismiss();
                startActivity(new Intent(ShareTuyiActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                isGeoGet = false;
                finish();
            }
        });
    }
    public void BarLeftClick(View view)
    {
        finish();
    }
    public void BarRightClick(View view) throws InterruptedException {
        shareFeed();
    }


}
