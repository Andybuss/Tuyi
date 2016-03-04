package dong.lan.tuyi.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import dong.lan.tuyi.utils.Config;

/**
 * Created by Dooze on 2015/8/28.
 */
public class SwipLayout extends HorizontalScrollView implements View.OnTouchListener {
    private LinearLayout myWrapper;
    private ViewGroup mMain;
    private ViewGroup mContent;
    private int mSreenWidth;
    private int swipLength = 100;
    public static int swipRightLength = 100;
    private boolean once = false;
    private  boolean isOpen = false;

    public SwipLayout(Context context) {
        super(context);
        this.setHorizontalScrollBarEnabled(false);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mSreenWidth = metrics.widthPixels;
        swipLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, swipRightLength, context.getResources().getDisplayMetrics());
    }

    public SwipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setHorizontalScrollBarEnabled(false);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mSreenWidth = metrics.widthPixels;
        swipLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, swipRightLength, context.getResources().getDisplayMetrics());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            myWrapper = (LinearLayout) getChildAt(0);
            mMain = (ViewGroup) myWrapper.getChildAt(0);
            //mMain.setOnTouchListener(this);
            mContent = (ViewGroup) myWrapper.getChildAt(1);
            mMain.getLayoutParams().width = mSreenWidth;
            mContent.getLayoutParams().width = swipLength;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.scrollTo(0, 0);
    }


    public void close() {
        this.smoothScrollTo(0, 0);
    }

    public void open() {
        this.smoothScrollTo(swipLength, 0);
    }

    float x = 0;
    float y = 0;

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getRawX();
                y = ev.getRawY();
                MyScrollView.Scrollable =false;
                break;
            case MotionEvent.ACTION_CANCEL:
                x = x - ev.getRawX();
                y = y - ev.getRawY();
                if (x >= 25 && Math.abs(y) < 30) {
                    if (isOpen) {
                        this.smoothScrollTo(0, 0);
                        MyScrollView.Scrollable =true;
                        isOpen = false;
                    } else {
                        this.smoothScrollTo(swipLength, 0);
                        isOpen = true;
                    }
                } else if (x <= -25 && Math.abs(y) < 30) {
                    this.smoothScrollTo(0, 0);
                    MyScrollView.Scrollable =true;
                    isOpen = false;
                }else
                {
                    MyScrollView.Scrollable =true;
                }
                Config.print("x: "+x+" y: "+y+"   "+MyScrollView.Scrollable);
                return true;
        }
        return false;
    }



}
