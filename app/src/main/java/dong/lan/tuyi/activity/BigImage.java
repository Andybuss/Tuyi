package dong.lan.tuyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import dong.lan.tuyi.R;
import dong.lan.tuyi.utils.Lock;
import dong.lan.tuyi.utils.MyImageAsyn;

/**
 * Created by 桂栋 on 2015/8/15.
 */
public class BigImage extends Activity {
    private ImageView bigImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_big_image);
        String url = getIntent().getStringExtra("PIC");
        bigImage = (ImageView) findViewById(R.id.bigImage);
        new MyImageAsyn(bigImage,MyImageAsyn.NORMAL).execute(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Lock.canPop = false;
    }
}
