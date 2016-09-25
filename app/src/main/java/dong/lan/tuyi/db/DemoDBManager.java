package dong.lan.tuyi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.easeui.BuildConfig;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.util.HanziToPinyin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.domain.InviteMessage;
import dong.lan.tuyi.domain.RobotUser;
import dong.lan.tuyi.utils.TimeUtil;

public class DemoDBManager {
    static private DemoDBManager dbMgr = new DemoDBManager();
    private DbOpenHelper dbHelper;
    
    private DemoDBManager(){
        dbHelper = DbOpenHelper.getInstance(TuApplication.getInstance().getApplicationContext());
    }
    
    public static synchronized DemoDBManager getInstance(){
        if(dbMgr == null){
            dbMgr = new DemoDBManager();
        }
        return dbMgr;
    }
    
    /**
     * 保存好友list
     * 
     * @param contactList
     */
    synchronized public void saveContactList(List<EaseUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if(user.getNick() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                if(user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取好友list
     * 
     * @return
     */
    synchronized public Map<String, EaseUser> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, EaseUser> users = new HashMap<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                EaseUser user = new EaseUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }
                
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM)|| username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else if (Character.isDigit(headerName.charAt(0))) {
                    user.setInitialLetter("#");
                } else {
                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
                            .get(0).target.substring(0, 1).toUpperCase());
                    char header = user.getInitialLetter().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setInitialLetter("#");
                    }
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }
    
    /**
     * 删除一个联系人
     * @param username
     */
    synchronized public void deleteContact(String username){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }
    
    /**
     * 保存一个联系人
     * @param user
     */
    synchronized public void saveContact(EaseUser user){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if(user.getNick() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        if(user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if(db.isOpen()){
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }
    
    public void setDisabledGroups(List<String> groups){
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }
    
    public List<String>  getDisabledGroups(){       
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }
    
    public void setDisabledIds(List<String> ids){
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }
    
    public List<String> getDisabledIds(){
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }
    
    synchronized private void setList(String column, List<String> strList){
        StringBuilder strBuilder = new StringBuilder();
        
        for(String hxid:strList){
            strBuilder.append(hxid).append("$");
        }
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null,null);
        }
    }
    
    synchronized private List<String> getList(String column){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME,null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }
        
        cursor.close();
        
        String[] array = strVal.split("$");
        
        if(array != null && array.length > 0){
            List<String> list = new ArrayList<String>();
            for(String str:array){
                list.add(str);
            }
            
            return list;
        }
        
        return null;
    }
    
    /**
     * 保存message
     * @param message
     * @return  返回这条messaged在db中的id
     */
    public synchronized Integer saveMessage(InviteMessage message){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            db.insert(InviteMessgeDao.TABLE_NAME, null, values);
            
            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessgeDao.TABLE_NAME,null); 
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
            
            cursor.close();
        }
        return id;
    }
    
    /**
     * 更新message
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId,ContentValues values){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.update(InviteMessgeDao.TABLE_NAME, values, InviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }
    
    /**
     * 获取messges
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + InviteMessgeDao.TABLE_NAME + " desc",null);
            while(cursor.moveToNext()){
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
                
                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                if(status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
                else if(status == InviteMessage.InviteMesageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
                else if(status == InviteMessage.InviteMesageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
                else if(status == InviteMessage.InviteMesageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                else if(status == InviteMessage.InviteMesageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
                else if(status == InviteMessage.InviteMesageStatus.BEAPPLYED.ordinal()){
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
                }
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }
    
    /**
     * 删除要求消息
     * @param from
     */
    synchronized public void deleteMessage(String from){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }
    
    synchronized int getUnreadNotifyCount(){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select " + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessgeDao.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
         return count;
    }
    
    synchronized void setUnreadNotifyCount(int count){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessgeDao.TABLE_NAME, values, null,null);
        }
    }
    
    synchronized public void closeDB(){
        if(dbHelper != null){
            dbHelper.closeDB();
        }
        dbMgr = null;
    }
    
    
    /**
     * Save Robot list
     */
	synchronized public void saveRobotList(List<RobotUser> robotList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(UserDao.ROBOT_TABLE_NAME, null, null);
			for (RobotUser item : robotList) {
				ContentValues values = new ContentValues();
				values.put(UserDao.ROBOT_COLUMN_NAME_ID, item.getUsername());
				if (item.getNick() != null)
					values.put(UserDao.ROBOT_COLUMN_NAME_NICK, item.getNick());
				if (item.getAvatar() != null)
					values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR, item.getAvatar());
				db.replace(UserDao.ROBOT_TABLE_NAME, null, values);
			}
		}
	}
    
    /**
     * load robot list
     */
	synchronized public Map<String, RobotUser> getRobotList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, RobotUser> users = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + UserDao.ROBOT_TABLE_NAME, null);
			if(cursor.getCount()>0){
				users = new HashMap<String, RobotUser>();
			};
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_AVATAR));
				RobotUser user = new RobotUser(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				if(Character.isDigit(headerName.charAt(0))){
					user.setInitialLetter("#");
				}else{
					user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getInitialLetter().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setInitialLetter("#");
					}
				}
				
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}




    synchronized public List<UserTuyi> getAllCacheTuyi() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(OfflineTuyi.COLUMN_CACHE, null, null, null, null, null, OfflineTuyi.COLUMN_TIME + " desc");
        if (!cursor.moveToFirst()) {
            return null;
        }
        List<UserTuyi> list = new ArrayList<>();
        do {

            UserTuyi offTuyi = new UserTuyi();
            offTuyi.settContent(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_CONTENT)));
            offTuyi.setTime(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_TIME)));
            offTuyi.settUri(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_PIC_URL)));
            offTuyi.setOfflineNmae(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_USERNAME)));
            offTuyi.setLocDes(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LOCDES)));
            if (cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG)) == null ||
                    cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG)).equals(""))
                offTuyi.settPoint(null);
            else {
                BmobGeoPoint p = new BmobGeoPoint(Double.parseDouble(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG))),
                        Double.parseDouble(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LAT))));
                offTuyi.settPoint(p);
            }
            if (cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_IS_PUBLIC)).equals("1"))
                offTuyi.setIsPublic(true);
            else
                offTuyi.setIsPublic(false);
            list.add(offTuyi);
        } while (cursor.moveToNext());


        return list;
    }

    synchronized public void saveCacheTuyi(UserTuyi offTuyi) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OfflineTuyi.COLUMN_CONTENT, offTuyi.gettContent());
        values.put(OfflineTuyi.COLUMN_PIC_URL, offTuyi.gettUri());
        values.put(OfflineTuyi.COLUMN_TIME, offTuyi.getTime());
        values.put(OfflineTuyi.COLUMN_USERNAME, offTuyi.getOfflineNmae());
        values.put(OfflineTuyi.COLUMN_LOCDES, offTuyi.getLocDes());
        if (offTuyi.getTAG() == null || offTuyi.getTAG().equals(""))
            values.put(OfflineTuyi.COLUMN_TAG, "杂记");
        else
            values.put(OfflineTuyi.COLUMN_TAG, offTuyi.getTAG());
        if (offTuyi.gettPoint() == null) {
            values.put(OfflineTuyi.COLUMN_LAT, "");
            values.put(OfflineTuyi.COLUMN_LNG, "");
        } else {
            values.put(OfflineTuyi.COLUMN_LAT, offTuyi.gettPoint().getLatitude());
            values.put(OfflineTuyi.COLUMN_LNG, offTuyi.gettPoint().getLongitude());
        }
        if (offTuyi.isPublic())
            values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "1");
        else
            values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "0");
        if (db.isOpen()) {
            db.replace(OfflineTuyi.COLUMN_CACHE, null, values);
        }
    }

    synchronized public void deleteCacheTuyi(String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(OfflineTuyi.COLUMN_CACHE, OfflineTuyi.COLUMN_TIME + " = ?", new String[]{time});
        }
    }

    synchronized public void updateCacheTuyi(ContentValues values, String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(OfflineTuyi.COLUMN_CACHE, values, OfflineTuyi.COLUMN_TIME + " = ?", new String[]{time});
        }
    }

    synchronized public int getOfflineTuyiCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(id) from " + OfflineTuyi.COLUMN_TABLENAME, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /*
    保存一个离线图忆信息
     */
    synchronized public void saveAOfflineTuyi(UserTuyi offTuyi) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OfflineTuyi.COLUMN_CONTENT, offTuyi.gettContent());
        values.put(OfflineTuyi.COLUMN_PIC_URL, offTuyi.gettUri());
        values.put(OfflineTuyi.COLUMN_TIME, offTuyi.getTime());
        values.put(OfflineTuyi.COLUMN_USERNAME, offTuyi.getOfflineNmae());
        values.put(OfflineTuyi.COLUMN_LOCDES, offTuyi.getLocDes());
        if (offTuyi.getTAG() == null || offTuyi.getTAG().equals(""))
            values.put(OfflineTuyi.COLUMN_TAG, "杂记");
        else
            values.put(OfflineTuyi.COLUMN_TAG, offTuyi.getTAG());
        if (offTuyi.gettPoint() == null) {
            values.put(OfflineTuyi.COLUMN_LAT, "");
            values.put(OfflineTuyi.COLUMN_LNG, "");
        } else {
            values.put(OfflineTuyi.COLUMN_LAT, offTuyi.gettPoint().getLatitude());
            values.put(OfflineTuyi.COLUMN_LNG, offTuyi.gettPoint().getLongitude());
        }
        if (offTuyi.isPublic())
            values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "1");
        else
            values.put(OfflineTuyi.COLUMN_IS_PUBLIC, "0");
        if (db.isOpen()) {
            db.replace(OfflineTuyi.COLUMN_TABLENAME, null, values);
        }
    }

    /*
    获取本地保存到数据库的离线图忆
     */
    synchronized public List<UserTuyi> getAllOfflineTuyi() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(OfflineTuyi.COLUMN_TABLENAME, null, null, null, null, null, OfflineTuyi.COLUMN_TIME + " desc");
        if (!cursor.moveToFirst()) {
            return null;
        }
        List<UserTuyi> list = new ArrayList<>();
        do {

            UserTuyi offTuyi = new UserTuyi();
            offTuyi.setTAG(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_TAG)));
            offTuyi.settContent(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_CONTENT)));
            offTuyi.setTime(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_TIME)));
            offTuyi.settUri(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_PIC_URL)));
            offTuyi.setOfflineNmae(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_USERNAME)));
            offTuyi.setLocDes(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LOCDES)));
            if (cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG)) == null ||
                    cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG)).equals(""))
                offTuyi.settPoint(null);
            else {
                BmobGeoPoint p = new BmobGeoPoint(Double.parseDouble(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LNG))),
                        Double.parseDouble(cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_LAT))));
                offTuyi.settPoint(p);
            }
            if (cursor.getString(cursor.getColumnIndex(OfflineTuyi.COLUMN_IS_PUBLIC)).equals("1"))
                offTuyi.setIsPublic(true);
            else
                offTuyi.setIsPublic(false);
            list.add(offTuyi);
        } while (cursor.moveToNext());


        return list;
    }
    /*
    删除一个离线图忆
     */

    synchronized public void deleteOffTuyiByTime(String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(OfflineTuyi.COLUMN_TABLENAME, OfflineTuyi.COLUMN_TIME + " = ?", new String[]{time});
        }
    }

    /*
    更改一个离线图忆信息
     */

    synchronized public void updateOfflineTuyi(ContentValues values, String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(OfflineTuyi.COLUMN_TABLENAME, values, OfflineTuyi.COLUMN_TIME + " = ?", new String[]{time});
        }
    }

    /*
    更新一个用户的信息
     */
    synchronized public void updateTuser(String username, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(TUserDao.TABLE_NAME, values, TUserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /*
    判断是否用户存在数据表中
     */

    synchronized public boolean isTUserExits(String name) {
        boolean exits = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TUserDao.TABLE_NAME, new String[]{"username"}, "username=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            if (cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_NAME_ID)).equals(name))
                exits = true;
        }
        cursor.close();
        return exits;
    }

    /*
    保存一个TUser 的用户信息
     */
    synchronized public void saveOneTuser(TUser tUser) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        BmobGeoPoint point = new BmobGeoPoint();
        point = tUser.getLoginPoint();
        if (point != null) {
            values.put(TUserDao.COLUMN_LAT, tUser.getLoginPoint().getLatitude() + "");
            values.put(TUserDao.COLUMN_LONG, tUser.getLoginPoint().getLongitude() + "");
        }
        values.put(TUserDao.COLUMN_NAME_ID, tUser.getUsername());
        values.put(TUserDao.COLUMN_NAME_AVATAR, tUser.getHead());
        values.put(TUserDao.COLUMN_USER_ID, tUser.getObjectId());
//        values.put(TUserDao.COLUMN_LONGIN_ID, tUser.getLoginID());
        if (tUser.isPublicMyPoint())
            values.put(TUserDao.COLUMN_IS_OPEN, "1");
        else
            values.put(TUserDao.COLUMN_IS_OPEN, "0");
        values.put(TUserDao.COLUMN_TIME, new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
        if (db.isOpen()) {
            db.replace(TUserDao.TABLE_NAME, null, values);
        }
    }
    /*
    根据用户名查找指定用户
     */

    synchronized public TUser getTUserByName(String name) {
        if(name==null) {
            if (BuildConfig.DEBUG) Log.d("DemoDBManager", "name:" + name);
            return null;
        }
        TUser user = new TUser();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TUserDao.TABLE_NAME, null, "username=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            String la = cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_LAT));
            String lg = cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_LONG));
            if (la != null && lg != null && !la.equals("") && !lg.equals("")) {
                double lat = Double.valueOf(la);
                double lng = Double.valueOf(lg);
                BmobGeoPoint point = new BmobGeoPoint(lng, lat);
                user.setLoginPoint(point);
            }
            user.setUsername(cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_NAME_ID)));
            if (cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_IS_OPEN)).equals("1"))
                user.setPublicMyPoint(true);
            else
                user.setPublicMyPoint(false);
            user.setHead(cursor.getString(cursor.getColumnIndex(TUserDao.COLUMN_NAME_AVATAR)));
        }
        cursor.close();
        return user;
    }

    /*
    删除一个图忆
     */
    synchronized public void deleteTuyiByTime(String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(Tuyi.TABLE_NAME, Tuyi.COLUMN_NAME_TIME + " = ?", new String[]{time});
        }
    }

    /*
    删除一个图忆
     */
    synchronized public void deleteTuyiByObjectID(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(Tuyi.TABLE_NAME, Tuyi.COLUMN_OBJECT_ID + " = ?", new String[]{id});
        }
    }

    /*
    图忆表是否为空
     */
    synchronized public boolean isTuyiEmpty() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Tuyi.TABLE_NAME, null, null, null, null, null, Tuyi.COLUMN_NAME_TIME);
        cursor.close();
        return !cursor.moveToFirst();

    }

    /*
    返回用户的所有图忆
     */
    synchronized public List<UserTuyi> getUserAllTuyi(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Tuyi.TABLE_NAME, null, "username=?", new String[]{name}, null, null, Tuyi.COLUMN_NAME_TIME + " desc");
        if (!cursor.moveToFirst()) {
            return null;
        }
        List<UserTuyi> list = new ArrayList<>();
        do {
            double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_LAT)));
            double lgt = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_LGT)));
            UserTuyi userTuyi = new UserTuyi();
            BmobGeoPoint point = new BmobGeoPoint(lgt, lat);
            TUser user = new TUser();
            user = getTUserByName(name);
            userTuyi.settUser(user);
            userTuyi.settPoint(point);
            userTuyi.setTAG(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_TAG)));
            userTuyi.setObjectId(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_OBJECT_ID)));
            userTuyi.settContent(cursor.getString(cursor.getColumnIndex(Tuyi.COLUME_NAME_CONTENT)));
            userTuyi.settPic(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_URL)));
            userTuyi.settUri(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_URI)));
            userTuyi.setLocDes(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_LOCDES)));
            if (cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_ZAN)) != null)
                userTuyi.setZan(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_ZAN))));
            else
                userTuyi.setZan(0);
            String tag = cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_IS_PUBLIC));
            if (tag.equals("1"))
                userTuyi.setIsPublic(true);
            else
                userTuyi.setIsPublic(false);
            userTuyi.setTime(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_TIME)));

            list.add(userTuyi);
        } while (cursor.moveToNext());

        cursor.close();
        return list;
    }

    /*
    更新图忆
     */
    synchronized public void updateTuyi(String id, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(Tuyi.TABLE_NAME, values, Tuyi.COLUMN_OBJECT_ID + " = ?", new String[]{id});
        }
    }

    /*
    返回id大于制定id值得所有图忆
     */
    synchronized public List<UserTuyi> getTuyiGreaterThanID(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Tuyi.TABLE_NAME, null, Tuyi.COLUMN_ID + ">?", new String[]{String.valueOf(id)}, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        List<UserTuyi> list = new ArrayList<>();
        do {
            double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_LAT)));
            double lgt = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_LGT)));
            UserTuyi userTuyi = new UserTuyi();
            BmobGeoPoint point = new BmobGeoPoint(lgt, lat);
            userTuyi.settPoint(point);
            TUser user = new TUser();
            user = getTUserByName(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_ID)));
            userTuyi.settUser(user);
            userTuyi.setTAG(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_TAG)));
            userTuyi.setObjectId(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_OBJECT_ID)));
            userTuyi.settContent(cursor.getString(cursor.getColumnIndex(Tuyi.COLUME_NAME_CONTENT)));
            userTuyi.settPic(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_URL)));
            userTuyi.setZan(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_ZAN))));
            userTuyi.settUri(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_URI)));
            String tag = cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_IS_PUBLIC));
            if (tag.equals("1"))
                userTuyi.setIsPublic(true);
            else
                userTuyi.setIsPublic(false);
            userTuyi.setTime(cursor.getString(cursor.getColumnIndex(Tuyi.COLUMN_NAME_TIME)));

            list.add(userTuyi);
        } while (cursor.moveToNext());

        cursor.close();
        System.out.println(list != null ? "list is null" : list.size() + "");
        return list;
    }

    /*
    返回图忆的总数
     */
    synchronized public int getTuyiCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Tuyi.TABLE_NAME, new String[]{Tuyi.COLUMN_ID}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        System.out.println("DB COUNT " + count);
        return count;
    }

    /*
    保存用户从云后台获取的所有图忆
     */
    synchronized public void saveTuyiFromNet(List<UserTuyi> list) {
        for (int i = 0; i < list.size(); i++) {
            saveTuyi(list.get(i));
        }
    }

    /*
    保存一个图忆
     */
    synchronized public void saveTuyi(UserTuyi tuyi) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Tuyi.COLUMN_NAME_ID, tuyi.gettUser().getUsername());
        values.put(Tuyi.COLUME_NAME_CONTENT, tuyi.gettContent());
        values.put(Tuyi.COLUMN_NAME_AVATAR, tuyi.gettUser().getHead());
        values.put(Tuyi.COLUMN_OBJECT_ID, tuyi.getObjectId());
        values.put(Tuyi.COLUMN_USER_ID, tuyi.gettUser().getObjectId());
        if (tuyi.isPublic())
            values.put(Tuyi.COLUMN_NAME_IS_PUBLIC, "1");
        else
            values.put(Tuyi.COLUMN_NAME_IS_PUBLIC, "0");

        if (tuyi.getTAG() == null || tuyi.getTAG().equals(""))
            values.put(OfflineTuyi.COLUMN_TAG, "杂记");
        else
            values.put(OfflineTuyi.COLUMN_TAG, tuyi.getTAG());
        values.put(Tuyi.COLUMN_NAME_LOCDES, tuyi.getLocDes());
        values.put(Tuyi.COLUMN_NAME_LAT, tuyi.gettPoint().getLatitude() + "");
        values.put(Tuyi.COLUMN_NAME_LGT, tuyi.gettPoint().getLongitude() + "");
        values.put(Tuyi.COLUMN_NAME_URI, tuyi.gettUri());
        values.put(Tuyi.COLUMN_NAME_URL, tuyi.gettPic());
        values.put(Tuyi.COLUMN_NAME_TIME, tuyi.getTime());
        values.put(Tuyi.COLUMN_NAME_ZAN, tuyi.getZan());
        if (db.isOpen()) {
            db.replace(Tuyi.TABLE_NAME, null, values);
        }
    }
    
    
    
}