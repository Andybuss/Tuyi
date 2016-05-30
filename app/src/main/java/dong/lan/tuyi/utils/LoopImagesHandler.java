package dong.lan.tuyi.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.List;

import dong.lan.tuyi.R;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  5/10/2016  18:37.
 * Email: 760625325@qq.com
 */
public class LoopImagesHandler extends Handler {
    private Context context;
    private List<String> UrlList;
    private ImageView imageSwitcher;
    public LoopImagesHandler(Context context,ImageView imageSwitcher, List<String> list){
        this.context = context;
        this.UrlList = list;
        this.imageSwitcher = imageSwitcher;

    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what>=0){
            PicassoHelper.load(context,UrlList.get(msg.what))
                    .placeholder(R.drawable.gallery)
                    .into(imageSwitcher);
        }
    }
}
