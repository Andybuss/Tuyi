package dong.lan.tuyi.utils;

import android.net.Uri;
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

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/7/26  14:55.
 * Email: 760625325@qq.com
 */
public class MyImageDownload extends AsyncTask<String,Integer,Uri> {

    private ImageView imageView;
    public MyImageDownload(ImageView imageView){
        this.imageView = imageView;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Uri result) {
        if(result==null)
            imageView.setImageResource(R.drawable.default_image);
        else{
            imageView.setTag(result);
            imageView.setImageURI(result);
        }
    }

    @Override
    protected Uri doInBackground(String... params) {

        if(params[0]==null ||params[0].equals("") )
            return null;
        File cacheFile = FileUilt.getCacheFile(params[0]);
        if (cacheFile.exists()) {
            return Uri.fromFile(cacheFile);
        } else {
            InputStream is = null;
            BufferedOutputStream bos = null;
            try {
                // 显示网络上的图片
                URL myFileUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();

                is = conn.getInputStream();
                cacheFile = FileUilt.getCacheFile(params[0]);
                bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
                byte[] buf = new byte[1024];
                int len = 0;
                // 将网络上的图片存储到本地
                while ((len = is.read(buf)) > 0) {
                    bos.write(buf, 0, len);
                }

                Uri uri = Uri.fromFile(cacheFile);
                Config.print(uri.toString());
                return uri;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (bos != null)
                        bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
