package dong.lan.tuyi.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.ReEditTuyiActivity;
import dong.lan.tuyi.activity.ShareTuyiActivity;
import dong.lan.tuyi.activity.TuMapActivity;
import dong.lan.tuyi.activity.TuyiInfoActivity;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by 桂栋 on 2015/7/22.
 */
public class UserMainAdapter extends BaseListAdapter<UserTuyi> {
    public static int FLAG = -1;
    public static final int COMMUNITY = 0;
    public static final int USERMAIN = 1;
    public static final int OFFLINE = 2;
    private int index;
    private String y;

    public UserMainAdapter(Context context, List<UserTuyi> list) {
        super(context, list);
    }

    public UserMainAdapter(Context context, List<UserTuyi> list, int community) {
        super(context, list);
        FLAG = community;
        y = TimeUtil.getYear();
    }


    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final UserTuyi tuyi = getList().get(position);
        if (tuyi == null) {
            return null;
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_community, null);
            viewHolder = new ViewHolder();
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.item_tuyi_pic);
            viewHolder.time = (TextView) convertView.findViewById(R.id.item_tuyi_time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.item_tuyi_content);
            viewHolder.rankIcon = (TextView) convertView.findViewById(R.id.rang_icon);
            viewHolder.popular = (TextView) convertView.findViewById(R.id.popular);
            viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
            viewHolder.share = (TextView) convertView.findViewById(R.id.swip_share);
            viewHolder.reEdit = (TextView) convertView.findViewById(R.id.swip_reEdit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(viewHolder);


        itemClick(viewHolder.itemLayout, position);
        viewHolder.rankIcon.setVisibility(View.GONE);
        viewHolder.popular.setVisibility(View.GONE);
        viewHolder.delete.setVisibility(View.VISIBLE);
        viewHolder.delete.setText(mContext.getString(R.string.delete));
        viewHolder.share.setVisibility(View.VISIBLE);
        viewHolder.reEdit.setVisibility(View.VISIBLE);
        viewHolder.share.setText(mContext.getString(R.string.share));
        viewHolder.reEdit.setText(mContext.getString(R.string.modefy));
        deleteItem(convertView, viewHolder.delete, position);
        swipReEdit(viewHolder.reEdit, position);
        swipShare(viewHolder.share, position);
        String time = tuyi.getTime();
        StringBuffer s = new StringBuffer();
        if (time != null) {
            s.append("<html><body><h1>");
            if (time.substring(0, 4).equals(y) && index != Integer.parseInt(y)) {
                s.append(time.substring(0, 4));
                s.append("年");
                index = Integer.parseInt(y);
            } else if (Integer.parseInt(time.substring(0, 4)) < index) {
                s.append(time.substring(0, 4));
                index = Integer.parseInt(time.substring(0, 4));
                s.append("年");
            }
            s.append("</h1>");
            s.append("<h2>");
            s.append(time.substring(5, 7));
            s.append("月</h2>");
            s.append("<h3>");
            s.append(time.substring(8, 10));
            s.append("日");
            s.append("</h3><body></html>");
            viewHolder.time.setText(Html.fromHtml(s.toString()));
        }
        String url = tuyi.gettPic();
        PicassoHelper.load(mContext, url)
                .resize(100, 100)
                .placeholder(R.drawable.gallery)
                .into(viewHolder.pic);
        s.delete(0, s.length());
        s.append("<html><head><p>");
        s.append(tuyi.gettContent());
        s.append("</p>");
        s.append("<head></html>");

        viewHolder.content.setText(Html.fromHtml(s.toString()));
        return convertView;
    }


    private void swipReEdit(TextView view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ReEditTuyiActivity.class).putExtra("TUYI", list.get(position)));
            }
        });
    }

    private void swipShare(TextView view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ShareTuyiActivity.class).putExtra("SHARE_TUYI", list.get(position)));
            }
        });
    }

    private void deleteItem(final View parent, TextView view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.getLayoutParams().height = 0;
                parent.requestLayout();
                list.get(position).delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            DemoDBManager.getInstance().deleteTuyiByObjectID(list.get(position).getObjectId());
                            list.remove(position);
                            UserMainAdapter.this.notifyDataSetChanged();
                        }else{
                            Config.Show(mContext, e.getMessage());
                        }
                    }
                });

            }
        });
    }


    //用点击事件模拟onItemClick

    private void itemClick(View view, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FLAG != OFFLINE) {
                    if (list.get(pos).gettContent().equals("无数据，下拉刷新试试看")) {
                        mContext.startActivity(new Intent(mContext, TuMapActivity.class));
                    } else {
                        mContext.startActivity(new Intent(mContext, TuyiInfoActivity.class).putExtra("TUYI", list.get(pos)));
                    }
                } else
                    mContext.startActivity(new Intent(mContext, ReEditTuyiActivity.class).putExtra("OFFLINE_TUYI", list.get(pos)));
            }
        });
    }


    public static class ViewHolder {
        LinearLayout itemLayout;
        ImageView pic;
        TextView time;
        TextView content;
        TextView popular;
        TextView rankIcon;
        TextView delete;
        TextView share;
        TextView reEdit;

    }
}
