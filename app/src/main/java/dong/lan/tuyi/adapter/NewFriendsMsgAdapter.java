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
package dong.lan.tuyi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.db.InviteMessgeDao;
import dong.lan.tuyi.domain.InviteMessage;
import dong.lan.tuyi.utils.AES;
import dong.lan.tuyi.utils.Config;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

    private Context context;
    private InviteMessgeDao messgeDao;

    public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        messgeDao = new InviteMessgeDao(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_invite_msg, null);
            holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
            holder.reason = (TextView) convertView.findViewById(R.id.message);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.user_state);
            holder.groupContainer = (LinearLayout) convertView.findViewById(R.id.ll_group);
            holder.groupname = (TextView) convertView.findViewById(R.id.tv_groupName);
             holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
        String str2 = context.getResources().getString(R.string.agree);

        String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
        String str4 = context.getResources().getString(R.string.Apply_to_the_group_of);
        String str5 = context.getResources().getString(R.string.Has_agreed_to);
        String str6 = context.getResources().getString(R.string.Has_refused_to);
        final InviteMessage msg = getItem(position);
        if (msg != null) {
            if (msg.getGroupId() != null) { // 显示群聊提示
                holder.groupContainer.setVisibility(View.VISIBLE);
                holder.groupname.setText(msg.getGroupName());
            } else {
                holder.groupContainer.setVisibility(View.GONE);
            }

            holder.reason.setText(msg.getReason());
            holder.name.setText(AES.decode(msg.getFrom()) );
             holder.time.setText(DateUtils.getTimestampString(new
                     Date(msg.getTime())));
            if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAGREED) {
                holder.status.setVisibility(View.INVISIBLE);
                holder.reason.setText(str1);
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setEnabled(true);
                holder.status.setText(str2);
                if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {
                    if (msg.getReason() == null) {
                        // 如果没写理由
                        holder.reason.setText(str3);
                    }
                } else { //入群申请
                    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.reason.setText(str4 + msg.getGroupName());
                    }
                }
                // 设置点击事件
                holder.status.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        if (Config.tUser == null) {
                            Config.Show(context, "用户数据没有更新，请检查网络");
                            return;
                        }
                        acceptInvitation(holder.status, msg);
                    }
                });
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.AGREED) {
                holder.status.setText(str5);
                holder.status.setEnabled(false);
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.REFUSED) {
                holder.status.setText(str6);
                holder.status.setEnabled(false);
            }

            // 设置用户头像
        }

        return convertView;
    }

    /**
     * 同意好友请求或者群申请
     *
     * @param button
     */
    private void acceptInvitation(final TextView button, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) //同意好友请求
                    {
                        BmobQuery<TUser> query = new BmobQuery<TUser>();
                        query.addWhereEqualTo("username", AES.decode(msg.getFrom()));
                        query.findObjects(context, new FindListener<TUser>() {
                            @Override
                            public void onSuccess(final List<TUser> list) {
                                if (list.isEmpty()) {
                                    Config.Show(context, "没有此用户");
                                    TUser user = new TUser();
                                    user.setUsername(msg.getFrom());
                                    user.setSex(true);
                                    user.save(context);
                                } else {
                                    BmobRelation relation = new BmobRelation();
                                    relation.add(list.get(0));
                                    Config.tUser.setFriends(relation);
                                    Config.tUser.update(context, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            try {
                                                EMChatManager.getInstance().acceptInvitation(msg.getFrom());
                                                CommUser user = new CommUser("id" + list.get(0).getUsername().hashCode());
                                                user.name = list.get(0).getUsername();
                                                user.source = Source.SELF_ACCOUNT;
                                                TuApplication.communitySDK.followUser(user, new Listeners.SimpleFetchListener<Response>() {
                                                    @Override
                                                    public void onComplete(Response response) {

                                                    }
                                                });
                                            } catch (EaseMobException e) {
                                                e.printStackTrace();
                                                Config.Show(context, "同意好友请求失败");
                                            }
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Config.Show(context, "同意好友请求失败 " + s);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    } else //同意加群申请
                        EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            button.setText(str2);
                            msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                            button.setBackgroundDrawable(null);
                            button.setEnabled(false);

                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private static class ViewHolder {
        ImageView avator;
        TextView name;
        TextView reason;
        TextView status;
        LinearLayout groupContainer;
        TextView groupname;
        TextView time;
    }

}
