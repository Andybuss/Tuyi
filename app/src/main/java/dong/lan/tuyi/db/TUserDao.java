package dong.lan.tuyi.db;

import android.content.Context;

/**
 * Created by 桂栋 on 2015/7/27.
 *
 * 用来保存所有浏览过的用户的数据库基类,使用DemoDBManager进行管理
 */
public class TUserDao  {
    public static final String TABLE_NAME ="TUSER";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_AVATAR = "head";
    public static final String COLUMN_USER_ID="objectID";
    public static final String COLUMN_IS_FRIEND="isFriend";
    public static final String COLUMN_TIME ="time";
    public static final String COLUMN_LAT="lat";
    public static final String COLUMN_LONGIN_ID="loginID";
    public static final String COLUMN_LONG="lng";
    public static final String COLUMN_IS_OPEN="isOpen";

    public TUserDao(Context context)
    {
        DemoDBManager.getInstance().onInit(context);
    }

}
