package dong.lan.tuyi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.TuyiComment;
import dong.lan.tuyi.utils.CircleTransformation;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by 桂栋 on 2015/7/24.
 */
public class CommentAdapter extends BaseListAdapter<TuyiComment> {
    public CommentAdapter(Context context, List<TuyiComment> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comment_item, null);
        }
        final TuyiComment comment = getList().get(position);
        ViewHolder viewHolder =new ViewHolder();
        viewHolder.head = (ImageView) convertView.findViewById(R.id.comment_item_head);
        viewHolder.content  = (TextView) convertView.findViewById(R.id.comment_item_content);
        viewHolder.time = (TextView) convertView.findViewById(R.id.comment_item_time);
        String url = comment.getComUser().getHead();
        if(url==null)
            viewHolder.head.setImageResource(R.drawable.default_avatar);
        else
            PicassoHelper.load(mContext,url)
                    .resize(100,100)
                    .transform(new CircleTransformation(50))
                    .placeholder(R.drawable.gallery)
                    .into(viewHolder.head);
        String con =comment.getComInfo();
        if(con==null)
            con="Nothing on this!";
        viewHolder.content.setText(con);

        String time = comment.getCreatedAt();
        long currentTime;
        currentTime= TimeUtil.stringToLong(time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        return convertView;
    }
    public static class ViewHolder
    {
        ImageView head;
        TextView time;
        TextView content;
    }
}
