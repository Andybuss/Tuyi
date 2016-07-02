package dong.lan.tuyi.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 项目：  XViewPager
 * 作者：  梁桂栋
 * 日期：  6/9/2016  14:46.
 * Email: 760625325@qq.com
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) {
            page.setAlpha(0);
        } else if(position<=0){
            page.setAlpha(0);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        }
        else if (position <= 1) {
            page.setAlpha(1-position);
            page.setTranslationX(pageWidth* -position);

            float scaleFactor = MIN_SCALE + (1-MIN_SCALE)*(1- Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        } else {
            page.setAlpha(0);
        }
    }
}
