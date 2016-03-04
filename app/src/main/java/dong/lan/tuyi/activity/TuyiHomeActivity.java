package dong.lan.tuyi.activity;

import android.os.Bundle;
import android.view.View;

import com.umeng.comm.ui.fragments.CommunityMainFragment;
import com.umeng.community.share.UMShareServiceFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;

import dong.lan.tuyi.Constant;
import dong.lan.tuyi.R;

/**
 * Created by 桂栋 on 2015/8/4.
 */
public class TuyiHomeActivity extends dong.lan.tuyi.basic.BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyihome);
        CommunityMainFragment mFeedsFragment = new CommunityMainFragment();
        //设置Feed流页面的返回按钮不可见
        mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);
        //添加并显示Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, mFeedsFragment).commit();
        // 添加QQ
        UMQQSsoHandler qqHandler = new UMQQSsoHandler(this, Constant.QQ_APPID,
                Constant.QQ_APPKEY);
        qqHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, Constant.QQ_APPID,
                Constant.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
        UMShareServiceFactory.getSocialService().getConfig()
                .setPlatforms(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        UMShareServiceFactory.getSocialService().getConfig()
                .setPlatformOrder(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        UMShareServiceFactory.getSocialService().getConfig().containsDeletePlatform(SHARE_MEDIA.WEIXIN);
        UMShareServiceFactory.getSocialService().getConfig().containsDeletePlatform(SHARE_MEDIA.WEIXIN_CIRCLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
