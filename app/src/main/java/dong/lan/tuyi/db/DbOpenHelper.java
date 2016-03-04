/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dong.lan.tuyi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import applib.controller.HXSDKHelper;


public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private static final String CREATE_TUYI_TABLE = "create table "
            + Tuyi.TABLE_NAME + " ("
            + Tuyi.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Tuyi.COLUMN_NAME_ID + " text, "
            + Tuyi.COLUMN_OBJECT_ID + " text, "
            + Tuyi.COLUMN_TAG + " text, "
            + Tuyi.COLUMN_NAME_AVATAR + " text, "
            + Tuyi.COLUMN_USER_ID + " text, "
            + Tuyi.COLUMN_NAME_LOCDES + " text, "
            + Tuyi.COLUME_NAME_CONTENT + " text, "
            + Tuyi.COLUMN_NAME_LAT + " text, "
            + Tuyi.COLUMN_NAME_LGT + " text, "
            + Tuyi.COLUMN_NAME_URI + " text, "
            + Tuyi.COLUMN_NAME_URL + " text, "
            + Tuyi.COLUMN_NAME_ZAN + " text, "
            + Tuyi.COLUMN_NAME_IS_PUBLIC + " text, "
            + Tuyi.COLUMN_NAME_TIME + " text);";

    private static final String CREATE_TUSERDAO_TABLE = "create table "
            + TUserDao.TABLE_NAME + " ("
            + TUserDao.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TUserDao.COLUMN_NAME_ID + " text, "
            + TUserDao.COLUMN_NAME_AVATAR + " text, "
            + TUserDao.COLUMN_USER_ID + " text, "
            + TUserDao.COLUMN_LAT + " text, "
            + TUserDao.COLUMN_LONG + " text, "
            + TUserDao.COLUMN_LONGIN_ID + " text, "
            + TUserDao.COLUMN_IS_OPEN + " text default 0, "
            + TUserDao.COLUMN_IS_FRIEND + " text, "
            + TUserDao.COLUMN_TIME + " text);";

    private static final String CREATE_OFFLINETUYI_TABLE = "create table "
            + OfflineTuyi.COLUMN_TABLENAME + " ("
            + OfflineTuyi.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OfflineTuyi.COLUMN_USERNAME + " text, "
            + OfflineTuyi.COLUMN_CONTENT + " text, "
            + OfflineTuyi.COLUMN_LOCDES + " text, "
            + OfflineTuyi.COLUMN_LAT + " text, "
            + OfflineTuyi.COLUMN_LNG + " text, "
            + OfflineTuyi.COLUMN_TAG + " text, "
            + OfflineTuyi.COLUMN_TIME + " text, "
            + OfflineTuyi.COLUMN_PIC_URL + " text, "
            + OfflineTuyi.COLUMN_IS_PUBLIC + " text);";

    private static final String CREATE_CACHETUYI_TABLE = "create table "
            + OfflineTuyi.COLUMN_CACHE + " ("
            + OfflineTuyi.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OfflineTuyi.COLUMN_USERNAME + " text, "
            + OfflineTuyi.COLUMN_CONTENT + " text, "
            + OfflineTuyi.COLUMN_LOCDES + " text, "
            + OfflineTuyi.COLUMN_LAT + " text, "
            + OfflineTuyi.COLUMN_LNG + " text, "
            + OfflineTuyi.COLUMN_TAG + " text, "
            + OfflineTuyi.COLUMN_TIME + " text, "
            + OfflineTuyi.COLUMN_PIC_URL + " text, "
            + OfflineTuyi.COLUMN_IS_PUBLIC + " text);";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return HXSDKHelper.getInstance().getHXId() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);
        db.execSQL(CREATE_TUYI_TABLE);
        db.execSQL(CREATE_TUSERDAO_TABLE);
        db.execSQL(CREATE_OFFLINETUYI_TABLE);
        db.execSQL(CREATE_CACHETUYI_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }

        if (oldVersion < 3) {
            db.execSQL(CREATE_PREF_TABLE);
        }
        if (oldVersion < 4) {
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if (oldVersion < 5) {
            db.execSQL(CREATE_TUYI_TABLE);
            db.execSQL(CREATE_TUSERDAO_TABLE);
            db.execSQL(CREATE_CACHETUYI_TABLE);
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
