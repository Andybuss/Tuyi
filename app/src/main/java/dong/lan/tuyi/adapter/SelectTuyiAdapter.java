package dong.lan.tuyi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.utils.MyImageAsyn;

/**
 * Created by 梁桂栋 on 2015/11/10.
 */
public class SelectTuyiAdapter extends RecyclerView.Adapter<SelectHolder> {
    private LayoutInflater inflater;
    private List<UserTuyi> tuyis;
    private HashSet<Integer> set= new HashSet<>();
    public SelectTuyiAdapter(Context context,List<UserTuyi> tuyis)
    {
        this.tuyis =tuyis;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public SelectHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SelectHolder(inflater.inflate(R.layout.item_select_tuyi,null));
    }

    @Override
    public void onBindViewHolder(SelectHolder holder, final int i) {
        StringBuffer s =new StringBuffer();
        s.append("<html><body><h3>");
        s.append(tuyis.get(i).getTime().substring(0,10));
        s.append("</h3><p>");
        s.append(tuyis.get(i).gettContent());
        s.append("</p></body></html>");
        holder.content.setText(Html.fromHtml(s.toString()));
        new MyImageAsyn(holder.pic,MyImageAsyn.THUMNAIL).execute(tuyis.get(i).gettPic());
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    set.add(i);
                }else if(set.contains(i))
                {
                    set.remove(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tuyis.size();
    }
    public HashSet<Integer> getSet()
    {
        return set;
    }
    public List<UserTuyi> getTuyis()
    {
        List<UserTuyi> t = new ArrayList<>();
        for (Integer aSet : set) {
            t.add(tuyis.get(aSet));
        }
        return t;
    }
}

class SelectHolder extends RecyclerView.ViewHolder
{
    public TextView content;
    public ImageView pic;
    public CheckBox check;
    public SelectHolder(View v) {
        super(v);
        content = (TextView) v.findViewById(R.id.item_select_content);
        pic = (ImageView) v.findViewById(R.id.item_select_pic);
        check = (CheckBox) v.findViewById(R.id.item_select_check);
    }
}
