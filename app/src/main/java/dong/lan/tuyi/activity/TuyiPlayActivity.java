package dong.lan.tuyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.Album;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.PicassoHelper;
import dong.lan.tuyi.utils.UserUtils;
import dong.lan.tuyi.widget.RecycleViewDivider;
import dong.lan.tuyi.widget.XSwipeLayout;

/**
 * Created by 梁桂栋 on 16-10-23 ： 下午2:50.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: Tuyi
 */

public class TuyiPlayActivity extends BaseActivity {

    private static final String TAG = "TuyiPlayActivity";
    List<Album> albumList;
    RecyclerView playLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuyi_play);
        playLists = (RecyclerView) findViewById(R.id.tuyi_plays);
        playLists.setLayoutManager(new GridLayoutManager(this, 1));
        playLists.addItemDecoration(new RecycleViewDivider(this, LinearLayout.VERTICAL, R.drawable.rect_divider));
        if (Config.tUser == null) {
            Show("用户信息未初始化");
            return;
        }
        albumList = new ArrayList<>();
        BmobQuery<Album> q = new BmobQuery<>();
        q.addWhereEqualTo("user", Config.tUser);
        q.order("-createdAt");
        q.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findObjects(new FindListener<Album>() {
            @Override
            public void done(List<Album> list, BmobException e) {
                if (e == null && list != null && !list.isEmpty()) {
                    if(!albumList.isEmpty())
                        albumList.clear();
                    albumList.addAll(list);
                    playLists.setAdapter(new Adapter());
                } else {
                    Show("你没有保存有图忆相册");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
            View v;
            if (getItemViewType(i) == 1) {
                final Holder holder;
                v = LayoutInflater.from(TuyiPlayActivity.this).inflate(R.layout.item_tuyi_play, null);
                holder = new Holder(v);
                holder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TuyiPlayActivity.this, AddTuyiPlayActivity.class);
                        intent.putExtra("album", albumList.get(holder.getLayoutPosition() - 1));
                        startActivity(intent);
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        albumList.remove(holder.getLayoutPosition()-1);
                        notifyItemRemoved(holder.getLayoutPosition());
                    }
                });
                return holder;
            } else {
                v = LayoutInflater.from(TuyiPlayActivity.this).inflate(R.layout.item_add_tuyi_play, null);
                RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(v) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TuyiPlayActivity.this, AddTuyiPlayActivity.class);
                        startActivity(intent);
                    }
                });
                return holder;
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder h, int i) {
            if (getItemViewType(h.getLayoutPosition()) == 1) {
                Holder holder = (Holder) h;
                Album album = albumList.get(h.getLayoutPosition() - 1);
                holder.tittle.setText(album.getDescription());
                holder.info.setText("此途忆总共有 " + album.getTuyis().size() + " 个图忆\n" + album.getCreatedAt());
                PicassoHelper.load(TuyiPlayActivity.this, album.getTuyis().get(0).gettPic()).into(holder.img);
            }
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            return 1;
        }

        @Override
        public int getItemCount() {
            return albumList == null ? 1 : albumList.size() + 1;
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView delete;
            TextView tittle;
            TextView info;
            ImageView img;
            View content;

            public Holder(View itemView) {
                super(itemView);
                delete = (TextView) itemView.findViewById(R.id.item_play_delete);
                tittle = (TextView) itemView.findViewById(R.id.item_play_tittle);
                info = (TextView) itemView.findViewById(R.id.item_play_info);
                img = (ImageView) itemView.findViewById(R.id.item_play_img);
                content = itemView.findViewById(R.id.content);
            }
        }
    }
}
