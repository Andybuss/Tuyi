package dong.lan.tuyi.db;

import android.content.Context;

import java.util.List;

import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.UserTuyi;

/**
 * Created by 桂栋 on 2015/7/19.
 */
public class Tuyi {

    public static final String TABLE_NAME ="Tuyi";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_USER_ID="tUserID";
    public static final String COLUMN_OBJECT_ID="objectID";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_AVATAR = "head";
    public static final String COLUME_NAME_CONTENT ="content";
    public static final String COLUMN_NAME_LAT="lat";
    public static final String COLUMN_NAME_LGT="lgt";
    public static final String COLUMN_NAME_URL="url";
    public static final String COLUMN_NAME_URI="uri";
    public static final String COLUMN_NAME_TIME ="time";
    public static final String COLUMN_NAME_ZAN="zan";
    public static final String COLUMN_NAME_LOCDES="locDes";
    public static final String COLUMN_NAME_IS_PUBLIC="isPublic";
    public static final String COLUMN_TAG ="tag";

    public Tuyi(Context context)
    {
        DemoDBManager.getInstance().onInit(context);
    }


    public void deleteTuyi(String tittle)
    {
        DemoDBManager.getInstance().deleteTuyiByTime(tittle);
    }

    public void saveTuyi(UserTuyi tuyi)
    {
        DemoDBManager.getInstance().saveTuyi(tuyi);
    }

    public List<UserTuyi> getUserAllTuyi()
    {
        return DemoDBManager.getInstance().getUserAllTuyi(TuApplication.getInstance().getUserName());
    }
    public boolean isTuyiEmpty()
    {
        return DemoDBManager.getInstance().isTuyiEmpty();
    }
}
