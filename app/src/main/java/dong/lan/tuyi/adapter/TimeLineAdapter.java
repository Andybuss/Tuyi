package dong.lan.tuyi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;

/**
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  5/12/2016  19:10.
 * Email: 760625325@qq.com
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private Context context;
    private List<UserTuyi> tuyis;
    private TimeLineItemClickListener itemClickListener;
    public TimeLineAdapter(Context context, List<UserTuyi> tuyis){
        this.context =context;
        this.tuyis = tuyis;
    }

    public void setItemClickListener(TimeLineItemClickListener listener)
    {
        this.itemClickListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_time_line,null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final UserTuyi tuyi = tuyis.get(viewHolder.getPosition());
        viewHolder.time.setText(tuyi.getTime());
        viewHolder.content.setText(tuyi.gettContent());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onTimeLineItemClick(tuyi,viewHolder.getPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tuyis.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView  time;
        public  TextView content;
        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.item_line_time);
            content = (TextView) itemView.findViewById(R.id.item_time_line_content);
        }
    }


    public interface TimeLineItemClickListener {
        void onTimeLineItemClick(UserTuyi tuyi,int pos);
    }
}
