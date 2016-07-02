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
package dong.lan.tuyi;

import android.os.Environment;

import com.easemob.easeui.EaseConstant;

public class Constant extends EaseConstant {


    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

    // 设置消息中 msgId 扩展的 key
    public static final String EM_ATTR_MSG_ID = "msg_id";
    // 设置自己扩展的消息类型的 key
    public static final String EM_ATTR_TYPE = "msg_type";
    public static final String EM_ATTR_TYPE_REVOKE = "revoke";

    public static final String DESCRIPTOR = "com.umeng.share";
    public static final String QQ_APPID = "1104844816";
    public static final String QQ_APPKEY = "iVoc0x1PsrLhIjPj";
   
    public static final String BmonAppID = "cc1ea39e7cfcd0d34214f2892de367f3";
    public static final String BDKey = "xz8Atj67sxQDOccGRHjUkDUD";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    public static final String PICTURE_PATH = Environment.getExternalStorageDirectory() + "/Tuyi/image/";
    /**
     * 拍照回调
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像

    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
    public static final String EXTRA_STRING = "extra_string";

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String CHAT_ROBOT = "item_robots";

}
