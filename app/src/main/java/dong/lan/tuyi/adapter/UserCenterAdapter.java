package dong.lan.tuyi.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.TuyiInfoActivity;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.db.Tuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.MyImageAsyn;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by Dooze on 2015/9/12.
 */
public class UserCenterAdapter extends BaseListAdapter<UserTuyi> {
    private boolean isMe;
    public UserCenterAdapter(Context context, List<UserTuyi> list,boolean isMe) {
        super(context, list);
        this.isMe =isMe;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_community, null);
        }
        final UserTuyi tuyi = getList().get(position);
        if (tuyi == null) {
            return null;
        }

        viewHolder.pic = (ImageView) convertView.findViewById(R.id.item_tuyi_pic);
        viewHolder.time = (TextView) convertView.findViewById(R.id.item_tuyi_time);
        viewHolder.content = (TextView) convertView.findViewById(R.id.item_tuyi_content);
        viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
        viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
        viewHolder.rankIcon = (TextView) convertView.findViewById(R.id.rang_icon);
        viewHolder.popular = (TextView) convertView.findViewById(R.id.popular);
        itemClick(viewHolder.itemLayout, position);
        viewHolder.rankIcon.setVisibility(View.GONE);
        viewHolder.popular.setVisibility(View.GONE);
        viewHolder.delete.setVisibility(View.VISIBLE);
        if(isMe)
            viewHolder.delete.setText("保密");
        else
            viewHolder.delete.setText("收藏");
        String time = tuyi.getTime();
        long currentTime;
        if (time == null) {
            time = tuyi.getCreatedAt();
            if (time != null)
                currentTime = TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
            else
                currentTime = 1;
        } else {
            currentTime = TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        }
        viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        String url = tuyi.gettPic();
        if (url != null)
            new MyImageAsyn(viewHolder.pic, 1).execute(url);
        else
            viewHolder.pic.setImageResource(R.drawable.logo);
        viewHolder.content.setText(tuyi.gettContent());
        setPrivate(convertView, viewHolder.delete, position);
        return convertView;
    }

    //个人主页将公开图忆设置成私人图忆的侧滑点击
    private void setPrivate(final View parent, TextView v, final int pos) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe) {
                    parent.getLayoutParams().height = 0;
                    parent.requestLayout();
                    list.get(pos).setIsPublic(false);
                    list.get(pos).update(mContext, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    ContentValues values = new ContentValues();
                                    values.put(Tuyi.COLUMN_NAME_IS_PUBLIC, "0");
                                    DemoDBManager.getInstance().updateTuyi(list.get(pos).getObjectId(), values);
                                    list.remove(pos);
                                    UserCenterAdapter.this.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Config.Show(mContext, "设置失败");
                                }
                            }
                    );
                }else
                {
                    BmobRelation relation = new BmobRelation();
                    relation.add(list.get(pos));
                    Config.tUser.setFavoraite(relation);
                    Config.tUser.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(mContext,"收藏成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(mContext,"收藏失败 T _ T",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //用点击事件模拟onItemClick

    private void itemClick(View view, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, TuyiInfoActivity.class).putExtra("TUYI", list.get(pos)));
            }
        });
    }

    public static class ViewHolder {
        LinearLayout itemLayout;
        ImageView pic;
        TextView time;
        TextView content;
        TextView delete;
        TextView popular;
        TextView rankIcon;
    }
}
