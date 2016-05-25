package dong.lan.tuyi.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by 桂栋 on 2015/7/13.
 */
public class FileUilt {
    private static final String TAG = "FileUtil";

    private FileUilt() {
    }

    public static File getCacheFile(String imageUri){
        File cacheFile = null;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                String fileName = getFileName(imageUri);
                File dir = new File(sdCardDir.getCanonicalPath()
                        + AsynImageLoader.CACHE_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                cacheFile = new File(dir, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getCacheFileError:" + e.getMessage());
        }

        return cacheFile;
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}
