package dong.lan.tuyi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.ReEditTuyiActivity;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/9/13  08:36.
 * Email: 760625325@qq.com
 */
public class OfflineAdapter extends BaseListAdapter<UserTuyi> {
    private Context context;
    public OfflineAdapter(Context context, List<UserTuyi> list) {
        super(context, list);
        this.context = context;
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
        viewHolder.delete.setVisibility(View.VISIBLE);
        viewHolder.delete.setText(mContext.getString(R.string.delete));
        deleteItem(convertView,viewHolder.delete,position);
        if (tuyi.gettPoint() == null) {
            viewHolder.popular.setBackgroundResource(R.drawable.circle_red);
            viewHolder.popular.setText(mContext.getString(R.string.mark));
        } else {
            viewHolder.popular.setVisibility(View.GONE);
        }
        viewHolder.content.setText(tuyi.gettContent());
        String time = tuyi.getTime();
        long currentTime;
        currentTime = TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        PicassoHelper.load(context,tuyi.gettUri())
                .placeholder(R.drawable.logo)
                .resize(100,100)
                .into(viewHolder.pic);

        return convertView;
    }


    private void itemClick(View view, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, ReEditTuyiActivity.class).putExtra("OFFLINE_TUYI", list.get(pos)));
            }
        });
    }

    private void deleteItem(final View parent,TextView view,final int position)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DemoDBManager.getInstance().deleteOffTuyiByTime(list.get(position).getTime());
                list.remove(position);
                OfflineAdapter.this.notifyDataSetChanged();
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
