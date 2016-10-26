package dong.lan.tuyi.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.Album;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.ImgPlayAsyn;
import dong.lan.tuyi.utils.PicassoHelper;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by 梁桂栋 on 2015/11/5 ： 下午12:45.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: Tuyi
 */
public class AddTuyiPlayActivity extends BaseActivity implements View.OnClickListener, SelectTuyiActivity.onTuyiSelectListener {

    private ImageView img;
    private TextView playText;
    EditText add_desc_et;
    private Button prePlay, addPaly, savePlay;
    private Timer timer;
    private List<UserTuyi> tuyis = new ArrayList<>();
    private MediaPlayer player;
    private int index;
    private int size;
    private int tag = 0;
    private Button addDescBt;
    private boolean isAdd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuyi_play);
        tuyis = DemoDBManager.getInstance().getUserAllTuyi(TuApplication.getInstance().getUserName());
        initView();
        if (getIntent().hasExtra("album")) {
            isAdd = false;
            Album album = (Album) getIntent().getSerializableExtra("album");
            if (album != null) {
                tuyis.clear();
                tuyis.addAll(album.getTuyis());
                PicassoHelper.load(this, tuyis.get(0).gettPic()).into(img);
                playText.setText(tuyis.get(0).gettContent());
                setTitle("我的途忆\t1/" + tuyis.size());
            }
        }else
            isAdd=true;
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.playImg);
        playText = (TextView) findViewById(R.id.playText);
        prePlay = (Button) findViewById(R.id.prePlay);
        addPaly = (Button) findViewById(R.id.addPlay);
        savePlay = (Button) findViewById(R.id.savePlay);
        addDescBt = (Button) findViewById(R.id.addDescription);
        add_desc_et = (EditText) findViewById(R.id.play_desc_et);
        addDescBt.setOnClickListener(this);
        prePlay.setOnClickListener(this);
        addPaly.setOnClickListener(this);
        savePlay.setOnClickListener(this);
        SelectTuyiActivity.setTuyiSelectListener(this);
    }

    private void addPlay() {
        startActivity(new Intent(this, SelectTuyiActivity.class));
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
    }

    private void savePlay() {
        if(isAdd)
            return;
        if (tuyis == null || tuyis.size() == 0) {
            Show("没有选择图忆呢");
            return;
        }
        if(add_desc_et.getText().length()==0){
            Show("点击屏幕下方的“说明”来添加个描述吧");
            return;
        }
        savePlay.setEnabled(false);
        Album album = new Album();
        album.setMusicType("0");
        album.setDescription(add_desc_et.getText().toString());
        album.setUser(Config.tUser);
        album.addAll("tuyis", tuyis);
        album.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Show("哈哈~保存成功");
                    savePlay.setEnabled(true);
                } else {
                    Show("保存失败");
                    savePlay.setEnabled(true);
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDescription:
                if(add_desc_et.getVisibility()==View.GONE){
                    add_desc_et.setVisibility(View.VISIBLE);
                    addDescBt.setText("完成");
                    add_desc_et.animate()
                            .translationY(0)
                            .setDuration(400)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                }else{
                    addDescBt.setText("说明");
                    add_desc_et.animate()
                            .translationY(-2000)
                            .setDuration(400)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    add_desc_et.setVisibility(View.GONE);
                }
                break;
            case R.id.addPlay:
                addPlay();
                break;
            case R.id.savePlay:
                savePlay();
                break;
            case R.id.prePlay:
                if (tuyis == null)
                    return;
                if (tag == 0) {
                    if (player == null) {
                        player = MediaPlayer.create(getBaseContext(), R.raw.happy);
                        player.setLooping(true);
                    }
                    prePlay.setText("停止");
                    player.start();
                    size = tuyis.size();
                    timer = new Timer(true);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (index >= size) {
                                index = 0;
                                player.reset();
                                player.stop();
                                timer.cancel();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        prePlay.setText("预览");
                                    }
                                });
                            }
                            new ImgPlayAsyn(img,
                                    playText,
                                    tuyis.get(index).gettContent()
                            ).execute(tuyis.get(index).gettPic());
                            index++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setTitle("我的途忆\t" + index + "/" + size);
                                }
                            });
                        }
                    }, 0, 3000);
                    tag = 1;
                } else if (tag == 1) {
                    timer.cancel();
                    player.pause();
                    prePlay.setText("预览");
                    tag = 0;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        if (player != null)
            player.stop();
    }

    @Override
    public void onTuyiSelected(List<UserTuyi> Tuyis) {
        this.tuyis = Tuyis;
    }
}
