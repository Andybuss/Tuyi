package dong.lan.tuyi.utils;

import android.app.ActionBar;
import android.content.Context;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import dong.lan.tuyi.R;

/**
 * Created by 桂栋 on 2015/8/19.
 */
public class Lock {
    public static PopupWindow popupWindow;
    public static Context context;
    public static final int UNLOCK = 1;
    public static final int SETLOCK = 2;
    public static final int RESETLOCK = 3;
    public static final int OFFLOCK = 4;
    private static int style =0;
    public static int itemID[]={R.id.lock_zero,R.id.lock_one,R.id.lock_two,R.id.lock_three,
            R.id.lock_four,R.id.lock_five,R.id.lock_six,R.id.lock_seven,R.id.lock_eight,R.id.lock_nine};
    public static Button checkBox[] = new Button[10];
    public static EditText key;
    public static TextView tip;
    static String string="";
    static String pwd="";
    static String pre;
    static int loop =0;
    public static boolean canPop =false;
    static boolean fromRest = false;
    static Vibrator vibrator;
    static View.OnClickListener l =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.lock_zero:
                    Check(checkBox[0].getText().toString());
                    break;
                case R.id.lock_one:
                    Check(checkBox[1].getText().toString());
                    break;
                case R.id.lock_two:
                    Check(checkBox[2].getText().toString());
                    break;
                case R.id.lock_three:
                    Check(checkBox[3].getText().toString());
                    break;
                case R.id.lock_four:
                    Check(checkBox[4].getText().toString());
                    break;
                case R.id.lock_five:
                    Check(checkBox[5].getText().toString());
                    break;
                case R.id.lock_six:
                    Check(checkBox[6].getText().toString());
                    break;
                case R.id.lock_seven:
                    Check(checkBox[7].getText().toString());
                    break;
                case R.id.lock_eight:
                    Check(checkBox[8].getText().toString());
                    break;
                case R.id.lock_nine:
                    Check(checkBox[9].getText().toString());
                    break;
            }
        }
    };
    private static void Check(String ss)
    {
        string+=ss;
        key.append(ss);
        if(string.length()==4)
        {
            System.out.println("string "+string);
            if(style==UNLOCK) {
                if (string.equals(Config.getLockKey(context))) {
                    unLock();
                    key.setText("");
                    string = "";
                } else {
                    tip.setText("密码不正确");
                    key.setText("");
                    string = "";
                    vibrator.vibrate(500);
                }
            }
            else if(style == SETLOCK)
            {
                loop++;
                if(fromRest)
                    tip.setText("重新设置4位密码");
                if(loop==1) {
                    pre = key.getText().toString();
                    key.setText("");
                    tip.setText("确认密码");
                    string="";
                }else if(loop==2)
                {
                    if(pre.equals(string))
                    {
                        Config.saveLockKey(context, pre);
                        unLock();
                        key.setText("");
                        string = "";
                        pre = "";
                        loop=0;
                        fromRest=false;
                    }
                    else
                    {
                        tip.setText("两次密码不一致，请重新设置");
                        key.setText("");
                        string = "";
                        pre = "";
                        loop=0;
                        vibrator.vibrate(500);
                    }
                }
            }
            else if(style==RESETLOCK)
            {
                if(string.equals(Config.getLockKey(context)))
                {
                    key.setText("");
                    string = "";
                    pre = "";
                    style = SETLOCK;
                    fromRest =true;
                }
                else
                {
                    tip.setText("密码不正确");
                    key.setText("");
                    string = "";
                    pre = "";
                    vibrator.vibrate(500);
                }
            }
            else if(style==OFFLOCK)
            {
                if(string.equals(Config.getLockKey(context)))
                {
                    key.setText("");
                    string = "";
                    Config.saveLock(context, false);
                    unLock();
                }else
                {
                    tip.setText("密码不正确");
                    key.setText("");
                    string = "";
                    vibrator.vibrate(500);
                }
            }
        }else
            vibrator.vibrate(50);

    }

    public static void locking(Context c,View parent,int tag)
    {
        context=c;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        pwd  =Config.getLockKey(context);
        style = tag;
        context=c;
        View view = LayoutInflater.from(context).inflate(R.layout.lock,null);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,false);
        key= (EditText) view.findViewById(R.id.lock_key);
        tip = (TextView) view.findViewById(R.id.lock_tip);
        if(tag==SETLOCK)
            tip.setText("设置4位应用锁密码");
        else if(tag==OFFLOCK || tag==RESETLOCK)
            tip.setText("输入已设置的密码");
        else
            tip.setText("输入4位解锁密码");
        for(int i = 0;i<10;i++) {
            checkBox[i] = (Button) view.findViewById(itemID[i]);
            checkBox[i].setOnClickListener(l);
        }
        
        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);

    }

    public static void unLock()
    {
        if(popupWindow!=null && popupWindow.isShowing())
        {
            popupWindow.dismiss();
            canPop = true;
        }
    }
}
