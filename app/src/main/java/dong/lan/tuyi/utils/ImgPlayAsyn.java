package dong.lan.tuyi.utils;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dong.lan.tuyi.util.FileUilt;
import dong.lan.tuyi.util.PhotoUtil;

/**
 * Created by 梁桂栋 on 2015/11/8.
 */
public class ImgPlayAsyn extends AsyncTask<String,Integer,Bitmap> {

    private ImageView img;
    private TextView text;
    private String s;
    public ImgPlayAsyn(ImageView imageView,TextView textView,String s)
    {
        img =imageView;
        text = textView;
        this.s = s;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null)
        {
            text.setText(s);
            img.setImageBitmap(bitmap);
            ObjectAnimator.ofFloat(img,"scaleX",1f,1.3f,1f).setDuration(2000).start();
            ObjectAnimator.ofFloat(img,"scaleY",1f,1.3f,1f).setDuration(2000).start();
            ObjectAnimator.ofFloat(text,"translationX",1000f,1f).setDuration(1500).start();
            ObjectAnimator.ofFloat(text,"translationY",1f,-100f).setDuration(1500).start();
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if(params[0]==null ||params[0].equals("") )
            return null;
        Bitmap bitmap=null;
        File cacheFile = FileUilt.getCacheFile(params[0]);
        if (cacheFile.exists()) {
            try {
                return PhotoUtil.getImageThumbnail(cacheFile.getCanonicalPath(),720,640);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // 显示网络上的图片
                URL myFileUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                cacheFile = FileUilt.getCacheFile(params[0]);
                BufferedOutputStream bos = null;
                bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
                byte[] buf = new byte[1024];
                int len = 0;
                // 将网络上的图片存储到本地
                while ((len = is.read(buf)) > 0) {
                    bos.write(buf, 0, len);
                }

                is.close();
                bos.close();
                // 从本地加载图片
                bitmap = BitmapFactory.decodeFile(cacheFile.getCanonicalPath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
