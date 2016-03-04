package dong.lan.tuyi.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Dooze on 2015/10/10.
 */
public class MyScrollView extends HorizontalScrollView {
    public MyScrollView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context,new XScrollDetector());
        canScroll =true;
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context,new XScrollDetector());
        canScroll =true;
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public static boolean Scrollable =true;
    private boolean canScroll;
    private GestureDetector gestureDetector ;
    private OnTouchListener touchListener;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_UP)
            canScroll =true;
        return super.onInterceptTouchEvent(ev)&&gestureDetector.onTouchEvent(ev);
    }

    public class XScrollDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Config.print("e1:"+e1.getAction()+"  "+e1.getRawX()+" e2:"+e2.getAction()+"  "+e2.getRawX());
        if(Scrollable) {
           canScroll =true;
        }else
        canScroll =false;
            return canScroll;
        }
    }

}
