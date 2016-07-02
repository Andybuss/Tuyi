package dong.lan.tuyi.adapter;

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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.TuyiInfoActivity;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by Dooze on 2015/9/23.
 */
public class MyFavoriteAdapter extends BaseListAdapter<UserTuyi> {
    public MyFavoriteAdapter(Context context, List<UserTuyi> list) {
        super(context, list);
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
        viewHolder.delete.setText("取消收藏");
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
            PicassoHelper.load(mContext,url)
                    .placeholder(R.drawable.gallery)
                    .into(viewHolder.pic);
        else
            viewHolder.pic.setImageResource(R.drawable.logo);
        viewHolder.content.setText(tuyi.gettContent());
        deleteFavorite(convertView, viewHolder.delete, position);
        return convertView;
    }

    private void deleteFavorite(final View parent, TextView v, final int pos) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.getLayoutParams().height = 0;
                parent.requestLayout();
                BmobRelation relation = new BmobRelation();
                relation.remove(list.get(pos));
                Config.tUser.setFavoraite(relation);
                Config.tUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(mContext, "移除收藏成功", Toast.LENGTH_SHORT).show();
                            list.remove(pos);
                            MyFavoriteAdapter.this.notifyDataSetChanged();
                        }else{
                            Toast.makeText(mContext, "移除收藏失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

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
