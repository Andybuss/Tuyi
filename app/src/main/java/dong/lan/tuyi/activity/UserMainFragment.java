package dong.lan.tuyi.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.UserMainAdapter;
import dong.lan.tuyi.basic.SwipLayout;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.xlist.XListView;

/**
 *
 * 项目：  Tuyi
 * 作者：  梁桂栋
 * 日期：  2015/7/22  21:28.
 * Email: 760625325@qq.com
 */
public class UserMainFragment extends Fragment implements AdapterView.OnItemClickListener, XListView.IXListViewListener, AdapterView.OnItemLongClickListener {


    private XListView mListView;
    private List<UserTuyi> tuyilist = new ArrayList<>();
    private LinearLayout dialog;
    private UserMainAdapter adapter;
    public static boolean isChange = false;
    private static String username;
    private java.util.List<UserTuyi> notList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_main_fragment, container, false);
    }

    //封装没有查询结果的时候，列表的默认显示内容
    private void initNotList() {
        UserTuyi u = new UserTuyi();
        u.settContent(getString(R.string.no_data_tip));
        notList.add(u);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;

        tuyilist = new ArrayList<>();
        initNotList();
        SwipLayout.swipRightLength = 150;
        username = TuApplication.getInstance().getUserName();
        mListView = (XListView) getView().findViewById(R.id.my_list);
        dialog = (LinearLayout) getView().findViewById(R.id.wait_dialog);
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.pullRefreshing();
        initDB();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isChange) {
            initDB();
            isChange = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SwipLayout.swipRightLength = 100;
    }


    /*
    获取用户的图忆事件，默认先从本地数据库获取，没用再从bmob后台获取
     */
    private void initDB() {
        tuyilist = DemoDBManager.getInstance().getUserAllTuyi(username);
        if (tuyilist == null || tuyilist.isEmpty()) {
            tuyilist = new ArrayList<>();
                if (Config.tUser == null) {
                    adapter = new UserMainAdapter(getActivity(), notList,UserMainAdapter.USERMAIN);
                    mListView.setAdapter(adapter);
                    ShowDialog(false);
                } else {
                    BmobQuery<UserTuyi> query = new BmobQuery<>();
                    query.order("-time,-createAt");
                    query.addWhereEqualTo("tUser", Config.tUser);
                    query.include("tUser");
                    query.findObjects(getActivity(), new FindListener<UserTuyi>() {
                        @Override
                        public void onSuccess(List<UserTuyi> list) {
                            if (list.isEmpty()) {
                                ShowDialog(false);
                                adapter = new UserMainAdapter(getActivity(), notList,UserMainAdapter.USERMAIN);
                                mListView.setAdapter(adapter);
                            } else {
                                tuyilist = list;
                                adapter = new UserMainAdapter(getActivity(), list,UserMainAdapter.USERMAIN);
                                mListView.setAdapter(adapter);
                                DemoDBManager.getInstance().saveTuyiFromNet(list);
                                ShowDialog(false);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            ShowDialog(false);
                            adapter = new UserMainAdapter(getActivity(), notList);
                            mListView.setAdapter(adapter);
                        }
                    });
                }
        } else {
            ShowDialog(false);
            adapter = new UserMainAdapter(getActivity(), tuyilist,UserMainAdapter.USERMAIN);
            mListView.setAdapter(adapter);
        }

    }

    private void refresh() {
        //下拉刷新功能时本地数据库有更新，直接从数据库获取更新的内容
        if (!tuyilist.isEmpty() && DemoDBManager.getInstance().getTuyiCount() > adapter.getCount()) {
            List<UserTuyi> l ;
            l = DemoDBManager.getInstance().getTuyiGreaterThanID(adapter.getCount());
            if(l!=null)
            adapter.addAll(l);
            mListView.deferNotifyDataSetChanged();
            mListView.setSelection(adapter.getCount()-1);
        }
        //内容列表为空则再次initDB（）
        if (tuyilist == null || tuyilist.isEmpty()) {
            initDB();
        }
    }

    @Override
    public void onRefresh() {
        refresh();
        refreshPull();
    }

    @Override
    public void onLoadMore() {
        refreshLoad();
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }

    }


    //控制刷新时的进度条的显示
    private void ShowDialog(boolean Show) {
        if (dialog != null) {
            if (Show) {
                dialog.setVisibility(View.VISIBLE);
                dialog.requestFocus();
            }
            else
                dialog.setVisibility(View.GONE);
        }
    }


    //列表点击事件，跳转查看详情页面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TuyiInfoActivity.class);
        intent.putExtra("TUYI", adapter.getList().get(position - 1));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    //列表项长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_set_head_img);
        TextView msg = (TextView) dialog.findViewById(R.id.dialog_msg);
        msg.setText(getString(R.string.how_to_handle_this_tuyi));
        TextView dialog_left = (TextView) dialog.findViewById(R.id.dialog_left);
        TextView dialog_right = (TextView) dialog.findViewById(R.id.dialog_right);
        dialog_left.setText(getString(R.string.modefy));
        dialog_right.setText(getString(R.string.share_to_tuyi));
        dialog_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), ReEditTuyiActivity.class).putExtra("TUYI", adapter.getList().get(position - 1)));
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
        dialog_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UserTuyi tuyi = adapter.getList().get(position - 1);
                startActivity(new Intent(getActivity(),ShareTuyiActivity.class).putExtra("SHARE_TUYI",tuyi));
            }
        });
        return true;
    }
}
