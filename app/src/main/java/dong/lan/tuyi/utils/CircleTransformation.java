package dong.lan.tuyi.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  5/10/2016  11:44.
 * Email: 760625325@qq.com
 */
public class CircleTransformation implements Transformation {
    private  float radius = 0f;

    public CircleTransformation(float radius) {
        this.radius = Resources.getSystem().getDisplayMetrics().density * radius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        squaredBitmap.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return "CircleTransformation";
    }
}
