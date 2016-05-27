package dong.lan.tuyi.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageSwitcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.util.FileUilt;

/**
 * Created by 桂栋 on 2015/7/26.
 */
public class MyImageDownload extends AsyncTask<String,Integer,Integer> {
    private List<Uri> uriList = new ArrayList<>();
    private ImageSwitcher switcher;

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer result) {
        for(int i = 0;i<uriList.size();i++) {
            if(uriList.get(i)==null)
                switcher.setImageResource(R.drawable.pic3);
            else
            switcher.setImageURI(uriList.get(i));
        }
    }

    @Override
    protected Integer doInBackground(String... params) {

        if(params[0]==null ||params[0].equals("") )
            return null;
        File cacheFile = FileUilt.getCacheFile(params[0]);
        if (cacheFile.exists()) {
            Uri uri = Uri.fromFile(cacheFile);
            uriList.add(uri);
            return 1;
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
                uriList.add(uri);
                Config.print(uri.toString());
                // 从本地加载图片
//                bitmap = BitmapFactory.decodeFile(cacheFile.getCanonicalPath());
                //String name = MD5Util.MD5(imageUri);

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
        return 1;
    }
}
