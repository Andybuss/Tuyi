package dong.lan.tuyi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.PicassoHelper;

/**
 * Created by Dooze on 2015/9/26.
 */
public class ClickPopRecycleAdapter extends RecyclerView.Adapter <ClickPopRecycleAdapter.PopHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<UserTuyi> tuyiList;

    public interface onPopItemClickListener
    {
        void onPopItemClick(UserTuyi tuyi,int pos);
    }

    public void setPopItemClickListenner(onPopItemClickListener listenner)
    {
        this.popItemClickListenner = listenner;
    }
    onPopItemClickListener popItemClickListenner;

    public ClickPopRecycleAdapter(Context context,List<UserTuyi> list)
    {
        this.context =context;
        this.tuyiList = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public PopHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_click_pop,viewGroup,false);
        return new PopHolder(view);
    }

    @Override
    public void onBindViewHolder(final PopHolder popHolder, final int pos) {
        popHolder.content.setText(tuyiList.get(pos).gettContent());
        PicassoHelper.load(context,tuyiList.get(pos).gettPic())
                .placeholder(R.drawable.gallery)
                .into(popHolder.img);
        popHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popItemClickListenner!=null)
                    popItemClickListenner.onPopItemClick(tuyiList.get(pos),pos);
                }

        });
    }

    @Override
    public int getItemCount() {
        return tuyiList.size();
    }

    public class PopHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView content;
        public PopHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_click_image);
            content = (TextView) itemView.findViewById(R.id.item_click_content);
        }

    }
}
