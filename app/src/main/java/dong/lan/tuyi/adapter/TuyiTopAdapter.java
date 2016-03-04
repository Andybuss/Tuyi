package dong.lan.tuyi.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
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

/**
 * Created by Dooze on 2015/9/13.
 */
public class TuyiTopAdapter extends BaseListAdapter<UserTuyi> {
    DecimalFormat format;
    public TuyiTopAdapter(Context context, List<UserTuyi> list) {
        super(context, list);
        format = new DecimalFormat(".000");
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
        viewHolder.popular.setText(position +1 +"");
        viewHolder.delete.setVisibility(View.VISIBLE);
        viewHolder.delete.setText("赞");
        Zan(viewHolder.delete, position);
        if (position == 0) {
            viewHolder.rankIcon.setBackgroundResource(R.drawable.show_event_pk_rank1);
        } else if (position == 1) {
            viewHolder.rankIcon.setBackgroundResource(R.drawable.show_event_pk_rank2);
        } else if (position == 2) {
            viewHolder.rankIcon.setBackgroundResource(R.drawable.show_event_pk_rank3);
        } else
            viewHolder.rankIcon.setVisibility(View.GONE);
        int zan =tuyi.getZan();
        if(zan<1000)
            viewHolder.time.setText(zan + "赞");
        else if(zan>1000&&zan<100000)
        viewHolder.time.setText(format.format(zan/1000.0) + "千赞");
        else
            viewHolder.time.setText(format.format(zan/10000.0) + "万赞");
        String url = tuyi.gettPic();
        if (url != null)
            new MyImageAsyn(viewHolder.pic, 1).execute(url);
        else
            viewHolder.pic.setImageResource(R.drawable.logo);
        viewHolder.content.setText(tuyi.gettContent());
        return convertView;
    }
    private void Zan(TextView view,final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobRelation relation = new BmobRelation();
                relation.add(Config.tUser);
                list.get(position).setZan(list.get(position).getZan() + 1);
                list.get(position).setLikes(relation);
                list.get(position).update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ContentValues values = new ContentValues();
                        values.put(Tuyi.COLUMN_NAME_ZAN, list.get(position).getZan());
                        DemoDBManager.getInstance().updateTuyi(list.get(position).getObjectId(), values);
                        if(position>0 && list.get(position).getZan()>list.get(position-1).getZan())
                        {

                            UserTuyi tuyi = list.get(position-1);
                            list.set(position-1,list.get(position));
                            list.set(position,tuyi);
                        }
                        TuyiTopAdapter.this.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {

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
