package dong.lan.tuyi.utils;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dong.lan.tuyi.R;
import dong.lan.tuyi.util.FileUilt;
import dong.lan.tuyi.util.PhotoUtil;

/**
 * Created by 桂栋 on 2015/7/26.
 */
public class MyImageAsyn extends AsyncTask<String,Integer,Bitmap> {
    private ImageView imageView;
    public static final int HEAD =0;
    public static final int NORMAL =1;
    public static final int USER_CENTER =2;
    public static final int THUMNAIL =3;
    private int style=1;
    public static final float down[]=new float[]{1f,0.9f,0.85f,0.8f,0.7f,0.65f,0.6f,0.5f,0.45f,0.4f,0.3f,0.32f,0.27f,0.2f,0.1f,
            0.15f,0.2f,0.26f,0.333f,0.39f,0.45f,0.53f,0.6f,0.68f,7.4f,0.8f,0.85f,0.9f,0.95f,1f};
    public MyImageAsyn(ImageView imageView,int style)
    {
        this.style =style;
        this.imageView = imageView;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap==null)
        {
            if(style==HEAD)
                imageView.setImageResource(R.drawable.default_avatar);
            else
            imageView.setImageResource(R.drawable.add_pic);
            if(style==USER_CENTER)
            ObjectAnimator.ofFloat(imageView,"alpha",down).setDuration(1000).start();
        }else {
            if (style == HEAD) {
                imageView.setImageBitmap(PhotoUtil.toRoundBitmap(bitmap));
            }
            else {
                imageView.setImageBitmap(bitmap);
            }
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
                if(style==HEAD)
                return PhotoUtil.getImageThumbnail(cacheFile.getCanonicalPath(),100,100);
                else if(style == THUMNAIL)
                    return PhotoUtil.getImageThumbnail(cacheFile.getCanonicalPath(),200,200);
                else
                    return PhotoUtil.getImageThumbnail(cacheFile.getCanonicalPath(),960,720);
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
                BufferedOutputStream bos;
                bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
                byte[] buf = new byte[1024];
                int len;
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
