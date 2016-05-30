package dong.lan.tuyi.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.listener.SaveListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.Album;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.ImgPlayAsyn;

/**
 * Created by 梁桂栋 on 2015/11/5.
 */
public class TuyiPlayActivity extends BaseActivity implements View.OnClickListener,SelectTuyiActivity.onTuyiSelectListener {

    private ImageView img;
    private TextView palyText;
    private Button prePlay,addPaly,savePlay;
    private Timer timer;
    private ArrayList<UserTuyi> tuyis = new ArrayList<>();
    private MediaPlayer player;
    private int index;
    private int size;
    private int tag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuyi_play);
        tuyis = (ArrayList<UserTuyi>) DemoDBManager.getInstance().getUserAllTuyi(TuApplication.getInstance().getUserName());
        initView();
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.playImg);
        palyText = (TextView) findViewById(R.id.playText);
        prePlay = (Button) findViewById(R.id.prePlay);
        addPaly= (Button) findViewById(R.id.addPlay);
        savePlay = (Button) findViewById(R.id.savePlay);
        prePlay.setOnClickListener(this);
        addPaly.setOnClickListener(this);
        savePlay.setOnClickListener(this);
        SelectTuyiActivity.setTuyiSelectListener(this);
    }

    private void addPlay()
    {
        startActivity(new Intent(this,SelectTuyiActivity.class));
        overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_out_bottom);
    }

    private void savePlay()
    {
        savePlay.setEnabled(false);
        Album album = new Album();
        album.setMusicType("0");
        album.setUser(Config.tUser);
        album.setTuyis(tuyis);
        album.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Show("哈哈~保存成功");
                savePlay.setEnabled(true);
            }

            @Override
            public void onFailure(int i, String s) {
                Show("保存失败");
                savePlay.setEnabled(true);
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addPlay:
                addPlay();
                break;
            case R.id.savePlay:
                savePlay();
                break;
            case R.id.prePlay:
                if(tuyis==null)
                    return;
                if(tag==0) {
                    if(player==null) {
                        player = MediaPlayer.create(getBaseContext(), R.raw.happy);
                        player.setLooping(true);
                    }
                    prePlay.setText("预览");
                    player.start();
                    size = tuyis.size();
                    timer = new Timer(true);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (index >= size)
                                index = 0;
                            new ImgPlayAsyn(img, palyText, tuyis.get(index).gettContent()).execute(tuyis.get(index).gettPic());
                            index++;
                        }
                    }, 0, 2000);
                    tag=1;
                }else if(tag==1)
                {
                    timer.cancel();
                    player.pause();
                    prePlay.setText("停止");
                    tag=0;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null)
        timer.cancel();
        if(player!=null)
        player.stop();
    }

    @Override
    public void onTuyiSelected(List<UserTuyi> Tuyis) {
     this.tuyis = (ArrayList<UserTuyi>) Tuyis;
    }
}
