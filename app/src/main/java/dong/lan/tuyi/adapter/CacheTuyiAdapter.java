package dong.lan.tuyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by Dooze on 2015/9/19.
 */
public class CacheTuyiAdapter extends BaseListAdapter<UserTuyi> {
    float x=0;
    float y = 0;
    public CacheTuyiAdapter(Context context, List<UserTuyi> list) {
        super(context, list);

    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cache,null);
            holder = new ViewHolder();
            holder.cache_tittle = (TextView) convertView.findViewById(R.id.cache_tittle);
            holder.cache_content = (TextView) convertView.findViewById(R.id.cache_content);
            holder.time = (TextView) convertView.findViewById(R.id.cache_time);
            holder.parent = (LinearLayout) convertView.findViewById(R.id.cache_item_layout);
            convertView.setTag(holder);
        }else
        holder = (ViewHolder) convertView.getTag();

        holder.cache_content.setText(list.get(position).gettContent());
        String time = list.get(position).getTime();
        long currentTime;
        if (time == null) {
            time = list.get(position).getCreatedAt();
            if (time != null)
                currentTime = TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
            else
                currentTime = 1;
        } else {
            currentTime = TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        }
        holder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        return convertView;
    }

    private static class ViewHolder
    {
        TextView cache_tittle;
        TextView cache_content;
        LinearLayout parent;
        TextView time;
    }
}
