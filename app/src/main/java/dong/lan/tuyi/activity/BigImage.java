package dong.lan.tuyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import dong.lan.tuyi.R;
import dong.lan.tuyi.utils.Lock;
import dong.lan.tuyi.utils.PicassoHelper;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/8/15  18:27.
 * Email: 760625325@qq.com
 */
public class BigImage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_big_image);
        String url = getIntent().getStringExtra("PIC");
        ImageView bigImage = (ImageView) findViewById(R.id.bigImage);
        PicassoHelper.load(this, url)
                .placeholder(R.drawable.gallery)
                .into(bigImage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
