package dong.lan.tuyi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.easemob.easeui.ui.EaseChatFragment;

import dong.lan.tuyi.R;

public class ChatActivity extends BaseActivity {
    public static final int RESULT_CODE_COPY = 11;
    public static final int RESULT_CODE_DELETE = 12;
    public static final int RESULT_CODE_OPEN = 13;
    public static final int RESULT_CODE_DWONLOAD = 14;
    public static final int RESULT_CODE_TO_CLOUD = 15;
    public static final int RESULT_CODE_FORWARD = 16;
    public static final int CHATTYPE_GROUP = 17;
    public static final int CHATTYPE_CHATROOM = 18;
    public static final int CHATTYPE_SINGLE = 19;
    public static final String COPY_IMAGE = "";
    public static final int REQUEST_CODE_COPY_AND_PASTE = 20;
    public static ChatActivity activityInstance;
    public static int resendPos;
    private EaseChatFragment chatFragment;
    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString("userId");
        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO
            }, 1);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername() {
        return toChatUsername;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
