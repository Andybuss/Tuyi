package dong.lan.tuyi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Dooze on 2015/9/24.
 */
public class ImageHelper {


    private ImageHelper() {
    }

    public static  Bitmap handleColorMatrixs(Bitmap bitmap,float colorMatrixs[]) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrixs);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmp;
    }
    /*
    色光三原色的调整
     */
    public static Bitmap handleImageEffect(Bitmap bitmap, float hue,float saturatin, float lum,String waterText)
    {
        //新生成一个bitmap  因为直接在原bitmap上修改会报错
        Bitmap bmp =Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);  //在画布上修改图片
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);  //抗锯齿的画笔

        ColorMatrix hueMatrix = new ColorMatrix(); //色相的调整对象
        hueMatrix.setRotate(0,hue);
        hueMatrix.setRotate(1,hue);
        hueMatrix.setRotate(2, hue);

        ColorMatrix saturationMatrix = new ColorMatrix(); //饱和度调整
        saturationMatrix.setSaturation(saturatin);

        ColorMatrix lumMatrix = new ColorMatrix(); //亮度调整
        lumMatrix.setScale(lum, lum, lum, 1);

        ColorMatrix imageMatrix = new ColorMatrix();  //融合三种matrix
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix)); //将融合后的matrix设置到paint中
        canvas.drawBitmap(bitmap,0,0,paint);
        if(waterText!=null)
        {
            String familyName ="宋体";
            Typeface font = Typeface.create(familyName,Typeface.BOLD);
            TextPaint textPaint=new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTypeface(font);
            textPaint.setTextSize(50);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(waterText,textPaint,bitmap.getWidth(), Layout.Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
            layout.draw(canvas);
            //文字就加左上角算了
            canvas.drawText(waterText,0,40,paint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bmp;

    }
}
