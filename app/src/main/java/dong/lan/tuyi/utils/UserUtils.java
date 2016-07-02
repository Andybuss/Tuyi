package dong.lan.tuyi.utils;

import android.content.ContentValues;
import android.content.Context;
import android.widget.ImageView;

import com.easemob.easeui.domain.EaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.db.TUserDao;


public class   UserUtils {
    private UserUtils() {
    }

    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(final String username){
        EaseUser user = TuApplication.getInstance().getContactList().get(username);
        if(user == null){
            user = new EaseUser(username);
        }
        boolean isUser = isColumnName(username);
        final boolean exits = DemoDBManager.getInstance().isTUserExits(username);
        if(!exits && isUser) {
            BmobQuery<TUser> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username);
            query.findObjects(new FindListener<TUser>() {
                @Override
                public void done(List<TUser> list, BmobException e) {
                    if(e==null){
                        if (!list.isEmpty()) {
                            if (exits) {
                                ContentValues values = new ContentValues();
                                BmobGeoPoint point;
                                point = list.get(0).getLoginPoint();
                                if (point != null) {
                                    values.put(TUserDao.COLUMN_LAT, list.get(0).getLoginPoint().getLatitude());
                                    values.put(TUserDao.COLUMN_LONG, list.get(0).getLoginPoint().getLongitude());
                                }
                                values.put(TUserDao.COLUMN_NAME_ID, list.get(0).getUsername());
                                values.put(TUserDao.COLUMN_NAME_AVATAR, list.get(0).getHead());
                                values.put(TUserDao.COLUMN_USER_ID, list.get(0).getObjectId());
//                            values.put(TUserDao.COLUMN_LONGIN_ID, list.get(0).getLoginID());
                                if (list.get(0).isPublicMyPoint())
                                    values.put(TUserDao.COLUMN_IS_OPEN, "1");
                                else
                                    values.put(TUserDao.COLUMN_IS_OPEN, "0");
                                values.put(TUserDao.COLUMN_TIME, new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
                                DemoDBManager.getInstance().updateTuser(username, values);
                            } else {
                                DemoDBManager.getInstance().saveOneTuser(list.get(0));
                            }
                        }
                    }
                }
            });
        }
        if(user != null){
            if(isUser) {
                TUser tUser = new TUser();
                tUser = DemoDBManager.getInstance().getTUserByName(username);
                if (tUser != null) {
                    String nick = tUser.getNick();
                    if (nick == null || nick.equals(""))
                        nick = tUser.getUsername();
                    user.setNick(nick);
                    user.setAvatar(tUser.getHead());
                } else {
                    user.setNick(username);
                    //user.setAvatar("http://downloads.easemob.com/downloads/57.png");
                }
            }
            else
                user.setNick(username);
        }
        return user;
    }

    public static boolean isColumnName(String  name)
    {
        boolean isUser = true;
        name=name.toLowerCase();
        if(name.equals("item_chatroom")||name.equals("item_new_friends") || name.equals("item_robots") || name.equals("item_groups"))
            isUser=false;
        return isUser;
    }
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
        EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar()!=null){
            PicassoHelper.load(context,user.getAvatar())
                    .resize(100,100)
                    .transform(new CircleTransformation(50))
                    .placeholder(R.drawable.default_avatar)
                    .into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    
}
