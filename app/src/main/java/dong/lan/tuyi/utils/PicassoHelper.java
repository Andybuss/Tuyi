package dong.lan.tuyi.utils;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import dong.lan.tuyi.R;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  5/10/2016  18:10.
 * Email: 760625325@qq.com
 *
 * 对Picasso做的简单封装，避免某些情况下传入空字符的报错
 *
 */
public class PicassoHelper {
    public static RequestCreator load(Context context,String path) {
        if(path!=null && path.trim().length() == 0) {
            return Picasso.with(context).load(R.drawable.logo);
        } else {
            return Picasso.with(context).load(path);
        }
    }
}
