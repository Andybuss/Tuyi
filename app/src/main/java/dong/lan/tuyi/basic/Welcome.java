package dong.lan.tuyi.basic;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.BuildConfig;
import com.easemob.redpacketsdk.RedPacket;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import dong.lan.tuyi.DemoHelper;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.activity.LoginActivity;
import dong.lan.tuyi.activity.MainActivity;
import dong.lan.tuyi.activity.OfflineTuyiActivity;
import dong.lan.tuyi.activity.TuMapActivity;
import dong.lan.tuyi.utils.Config;

/**
 * Created by 梁桂栋 on 2015/7/14 ： 下午2:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: Tuyi
 */
public class Welcome extends Activity implements GestureDetector.OnGestureListener {

    @ViewInject(R.id.viewflit_activity)
    private ViewFlipper flipper;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        Config.preferences = Config.getSharePreference(this);
        ViewUtils.inject(this);
        initView();
    }

    private void jump() {
        if (DemoHelper.getInstance().isLoggedIn()) {
            long start = System.currentTimeMillis();
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            long costTime = System.currentTimeMillis() - start;
            if (sleepTime - costTime > 0) {
                try {
                    Thread.sleep(sleepTime - costTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (BuildConfig.DEBUG) Log.d("Welcome", "mainTu");
            //进入主页面
            TuApplication.getInstance().setUserName(DemoHelper.getInstance().getCurrentUsernName());
            startActivity(new Intent(Welcome.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (BuildConfig.DEBUG) Log.d("Welcome", "loginAc");
            startActivity(new Intent(Welcome.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
        finish();
    }

    private void login() {
        new Thread(new Runnable() {
            public void run() {
                if (BuildConfig.DEBUG) Log.d("Welcome", "login");
                jump();
            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT<23)
        {
            RedPacket.getInstance().initContext(TuApplication.applicationContext);
            RedPacket.getInstance().setDebugMode(true);
            Log.d("TAG", "onStart: ");
            if (!isFirst)
                login();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && ((ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission
                .WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission
                        .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission
                        .WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Welcome.this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_SECURE_SETTINGS
            }, 1);
        } else {
            RedPacket.getInstance().initContext(TuApplication.applicationContext);
            RedPacket.getInstance().setDebugMode(true);
            Log.d("TAG", "onStart: ");
            if (!isFirst)
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
            if (BuildConfig.DEBUG) Log.d("Welcome", "isFirst");
        } else {
            pic1.setImageResource(R.drawable.load_pic);
            AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
            animation.setDuration(1500);
            //flipper.startAnimation(animation);
            if (getIntent().hasExtra("OFFLINE") && getIntent().getBooleanExtra("OFFLINE", false)) {

                startActivity(new Intent(this, OfflineTuyiActivity.class).putExtra("FROM_DESK", true));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            } else if (getIntent().hasExtra("ADD_TUYI") && getIntent().getBooleanExtra("ADD_TUYI", false)) {

                startActivity(new Intent(this, TuMapActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            } else {
                startActivity(new Intent(Welcome.this, MainActivity.class));
            }
            finish();

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
            flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_right));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_left));
            flipper.showNext();
        } else if (motionEvent.getX() < motionEvent1.getX()) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_left));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_right));
            flipper.showPrevious();
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission
                .READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {
            RedPacket.getInstance().initContext(TuApplication.applicationContext);
            RedPacket.getInstance().setDebugMode(true);
        } else {
            new AlertDialog.Builder(Welcome.this)
                    .setMessage("为了使用红包功能我们需要读取您手机的状态，以获取唯一的设备ID。")
                    .setPositiveButton("请求权限", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Welcome.this, new String[]{
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.WRITE_SECURE_SETTINGS
                            }, 1);
                        }
                    }).show();
        }
    }
}
