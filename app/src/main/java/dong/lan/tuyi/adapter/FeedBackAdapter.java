package dong.lan.tuyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.FeedBack;

/**
 * Created by Dooze on 2015/9/29.
 */
public class FeedBackAdapter extends BaseListAdapter<FeedBack> {
    public FeedBackAdapter(Context context, List<FeedBack> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            FeedBack feedBack = new FeedBack();
            feedBack=list.get(position);
            ViewHolder holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_feed,null);
            holder.feedBackLayout = (RelativeLayout) convertView.findViewById(R.id.feedFromLayout);
            holder.feedFromContent= (TextView) convertView.findViewById(R.id.feed_from_content);
            holder.feedFromTime = (TextView) convertView.findViewById(R.id.feed_from_time);
            holder.feedHead  = (ImageView) convertView.findViewById(R.id.feed_to_head);
            holder.feedToContent= (TextView) convertView.findViewById(R.id.feed_to_content);
            holder.feedToTime= (TextView) convertView.findViewById(R.id.feed_to_time);
            if(feedBack.getReply()==null || feedBack.getReply().equals(""))
            {
                holder.feedBackLayout.setVisibility(View.GONE);
            }else
            {
                holder.feedFromTime.setText(feedBack.getUpdatedAt());
                holder.feedFromContent.setText(feedBack.getReply());
            }
            holder.feedToContent.setText(feedBack.getFeek());
            holder.feedToTime.setText(feedBack.getCreatedAt());
            convertView.setTag(holder);
        }
        return convertView;
    }
    static class ViewHolder
    {
        RelativeLayout feedBackLayout;
        TextView feedToTime;
        TextView feedToContent;
        TextView feedFromTime;
        TextView feedFromContent;
        ImageView feedHead;

    }
}
