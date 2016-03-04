package dong.lan.tuyi.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import cn.bmob.v3.Bmob;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.DemoHXSDKHelper;
import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.LoginActivity;
import dong.lan.tuyi.activity.MainActivity;
import dong.lan.tuyi.activity.OfflineTuyiActivity;
import dong.lan.tuyi.activity.TuMapActivity;
import dong.lan.tuyi.utils.Config;

/**
 * Created by 桂栋 on 2015/7/14.
 */
public class Welcome extends Activity implements GestureDetector.OnGestureListener {

    @ViewInject(R.id.viewflit_activity)
    private ViewFlipper mVFActivity;
    private GestureDetector mGestureDetector;
    @ViewInject(R.id.pic3)
    private ImageView go;
    @ViewInject(R.id.pic1)
    private ImageView pic1;
    private static final int sleepTime = 2000;

    private boolean isFirst = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        Config.preferences = Config.getSharePreference(this);
        //初始化Bmob SDK
        Bmob.initialize(Welcome.this, Constant.BmonAppID);
        AnalyticsConfig.enableEncrypt(true); //友盟统计日志加密
        MobclickAgent.updateOnlineConfig(getApplicationContext());//友盟统计回调数据频率
        ViewUtils.inject(this);
        mVFActivity.setAutoStart(true);
        mVFActivity.setFlipInterval(4000);
        initView();
    }


    private void login() {

        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(Welcome.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    finish();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isFirst) {
            login();
        }
    }

    private void initView() {
        Config.preferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
        isFirst = Config.preferences.getBoolean(Config.LOAD_MODE, true);
        mGestureDetector = new GestureDetector(this);
        if (isFirst) {
            go.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    login();
                }
            });
            Config.preferences.edit().putBoolean(Config.LOAD_MODE, false).apply();
        } else {
            pic1.setImageResource(R.drawable.load_pic);
            AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
            animation.setDuration(1500);
            mVFActivity.startAnimation(animation);
            if(getIntent().hasExtra("OFFLINE") && getIntent().getBooleanExtra("OFFLINE",false))
            {

                startActivity(new Intent(this, OfflineTuyiActivity.class).putExtra("FROM_DESK",true));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
            }
            if(getIntent().hasExtra("ADD_TUYI") && getIntent().getBooleanExtra("ADD_TUYI",false))
            {

                startActivity(new Intent(this, TuMapActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
            }
        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {


        if (motionEvent.getX() > motionEvent1.getX()) {
            mVFActivity.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_left));
            mVFActivity.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_left));
            mVFActivity.showNext();
        } else if (motionEvent.getX() < motionEvent1.getX()) {
            mVFActivity.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_right));
            mVFActivity.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_right));
            mVFActivity.showPrevious();
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
