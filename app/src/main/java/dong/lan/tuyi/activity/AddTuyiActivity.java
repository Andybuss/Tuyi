package dong.lan.tuyi.activity;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.tuyi.Constant;
import dong.lan.tuyi.R;
import dong.lan.tuyi.TuApplication;
import dong.lan.tuyi.adapter.CacheTuyiAdapter;
import dong.lan.tuyi.bean.TUser;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.util.PhotoUtil;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.ImageHelper;
import dong.lan.tuyi.utils.InputTools;
import dong.lan.tuyi.utils.TimeUtil;
import dong.lan.tuyi.xlist.XListView;

public class AddTuyiActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    public static final int SET_TUYI =0x16;
    public static int pos =-1;
    private EditText  contetnt, addr;
    private LinearLayout dlLayout;
    private ImageView pic;
    private RadioButton rbPublic, rbPrivate;
    private RadioGroup radioGroup;
    private RadioButton radioButton[] = new RadioButton[5];
    private int rbId[] = new int[]{R.id.za,R.id.shi,R.id.jing,R.id.qing,R.id.wan};
    public static String[] checkStr = new String[]{"杂记","美食","美景","人情","乐玩"};
    private String addrStr;
    private double lat, lng;
    private TextView downloadTip;
    private TextView tempSave;
    private FrameLayout cachePopLayout;
    XListView mListView;
    List<UserTuyi> caches = new ArrayList<>();
    CacheTuyiAdapter adapter =null;
    boolean isFirst =true;
    String filePath = "";
    boolean isFromCamera = false;// 区分拍照旋转
    private boolean hasLoc = true;
    int degree = 0;
    String path = "";
    private String Tusername;
    private int checkIndex = 0;
    private float statu =1;
    private float hue=0;
    private float lum=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pop);
        addrStr = getIntent().getStringExtra("ADDRESS");
        lat = getIntent().getDoubleExtra("LAT", 0);
        lng = getIntent().getDoubleExtra("LNG", 0);
        Tusername = TuApplication.getInstance().getUserName();
        initView();
        if (lat == 0.0) {
            Show("没有位置信息，会保存到离线图忆");
            hasLoc = false;
        }
        addr.setText("(点击可以修改)"+addrStr);
    }
    private void initView() {
        findViewById(R.id.add_cancel).setOnClickListener(this);
        findViewById(R.id.add_done).setOnClickListener(this);
        findViewById(R.id.added_pic).setOnClickListener(this);
        tempSave = (TextView) findViewById(R.id.temp_save);
        tempSave.setOnClickListener(this);
        tempSave.setOnLongClickListener(this);

        dlLayout = (LinearLayout) findViewById(R.id.upLoad_progress_layout);
        pic = (ImageView) findViewById(R.id.show_img);
        rbPrivate = (RadioButton) findViewById(R.id.isPrivate);
        rbPublic = (RadioButton) findViewById(R.id.isPublic);
        contetnt = (EditText) findViewById(R.id.add_content);
        addr = (EditText) findViewById(R.id.add_locDes);
        downloadTip = (TextView) findViewById(R.id.download_tip);
        addr.setText(addr+"(点击修改地址)");
        addr.setOnClickListener(this);
        pic.setOnClickListener(this);
        mListView = (XListView) findViewById(R.id.cache_list);
        cachePopLayout = (FrameLayout) findViewById(R.id.CachePopLayout);
        radioGroup = (RadioGroup) findViewById(R.id.label_layout);
        for(int i =0;i<5;i++)
        {
            radioButton[i] = (RadioButton) findViewById(rbId[i]);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i =0;i<5;i++)
                {
                    if(checkedId==rbId[i])
                    {
                        checkIndex =i;
                        break;
                    }
                }
            }
        });

    }

    PopupWindow popupWindow ;
    Bitmap bitmap;
    ImageView img;
    EditText waterText;
    private void handleImgPop()
    {
        bitmap = PhotoUtil.drawableToBitmap(pic.getDrawable());
        View view = LayoutInflater.from(this)
                .inflate(R.layout.pop_handle_image,null);
        TextView save = (TextView) view.findViewById(R.id.handle_save);
        TextView back = (TextView) view.findViewById(R.id.handle_back);
        TextView waterTextDone = (TextView) view.findViewById(R.id.waterTextDone);
        img = (ImageView) view.findViewById(R.id.handle_image);
        waterText = (EditText) view.findViewById(R.id.waterText);
        final SeekBar sta = (SeekBar) view.findViewById(R.id.satura);
        final SeekBar hue = (SeekBar) view.findViewById(R.id.hue);
        final SeekBar lum = (SeekBar) view.findViewById(R.id.lum);
        sta.setProgress(127);
        hue.setProgress(127);
        lum.setProgress(127);
        waterTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageBitmap(ImageHelper.handleImageEffect(bitmap,(hue.getProgress()-127)/127*180f,sta.getProgress()/127f,lum.getProgress()/127f,waterText.getText().toString()));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic.setImageDrawable(img.getDrawable());
                popupWindow.dismiss();
            }
        });
        img.setImageBitmap(bitmap);
        sta.setOnSeekBarChangeListener(this);
        hue.setOnSeekBarChangeListener(this);
        lum.setOnSeekBarChangeListener(this);
        popupWindow= new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,true);
        popupWindow.setAnimationStyle(R.style.Animations_PopAnim);
        if(popupWindow!=null && !popupWindow.isShowing())
            popupWindow.showAtLocation(findViewById(R.id.add_pop_parent), Gravity.CENTER,0,0);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_img:
                //addWaterMark();
                handleImgPop();
                break;
            case R.id.add_locDes:
                addr.setText("" + addrStr);
                addr.setSelection(addrStr.length());
                break;
            case R.id.temp_save:
                //保存未完成的图忆到本地，方便下次直接编辑
                saveToCache();
                break;
            case R.id.add_cancel:
                finish();
                break;
            case R.id.add_done:
                addDone();
                break;
            case R.id.added_pic:
                showDialog();
                break;

        }

    }

    private void saveToCache() {
        if (contetnt.getText().toString().equals("")) {
            Show("内容空空哒");
            return;
        }
        final UserTuyi tuyi = new UserTuyi();
        if(path.equals(""))
        {
            Show("没有保存图片");
        }
        if(addrStr.equals(""))
        {
            Show("没有位置信息");
        }
        TUser user = new TUser();
        if (Config.tUser == null) {
            user = DemoDBManager.getInstance().getTUserByName(Tusername);
            if (user == null) {
                Show("请等待更新用户数据完成");
                return;
            }
            tuyi.settUser(user);
        } else
            user =Config.tUser;
        tuyi.setIsPublic(isForAll);
        tuyi.settContent(contetnt.getText().toString());
        tuyi.settUri(path);
        tuyi.setLocDes(addr.getText().toString());
        tuyi.setTAG(checkStr[checkIndex]);
        tuyi.setTime(new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
        if(user!=null)
        {
            tuyi.setOfflineNmae(user.getUsername());
            DemoDBManager.getInstance().saveCacheTuyi(tuyi);
            Show("缓存成功");
            isFirst = true;
        }
        else {
            final android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(this,R.style.DialogMDStyle);
            builder .setTitle("请输入正确的用户名");
            View view = LayoutInflater.from(this).inflate(R.layout.offline_get_name_dialog,null);
            final EditText editText = (EditText) view.findViewById(R.id.offline_dialog_get_name);
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(editText.getText().toString().equals(""))
                    {
                        Show("请输入正确用户名");
                        return;
                    }
                    tuyi.setOfflineNmae(editText.getText().toString());
                    DemoDBManager.getInstance().saveAOfflineTuyi(tuyi);
                    Show("保存为离线成功");
                }
            });
            builder.show();
        }


    }

    private boolean isForAll;

    private void addDone() {
        if (contetnt.getText().toString().equals("")) {
            Show("内容空空哒");
            return;
        }
        if(path==null || path.equals(""))
        {
            Show("请添加图片");
        }
        isForAll = !(rbPrivate.isChecked() && !rbPublic.isChecked());
        InputTools.HideKeyboard(addr);
        InputTools.HideKeyboard(contetnt);
        TUser user = new TUser();
        user = DemoDBManager.getInstance().getTUserByName(Tusername);
        if (user == null) {
            Show("请等待更新用户数据完成");
            return;
        }
        uploadPic();
    }

    Dialog dialog;

    private void showDialog() {
        dialog = new android.app.AlertDialog.Builder(this).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_set_head_img);
        TextView msg = (TextView) dialog.findViewById(R.id.dialog_msg);
        msg.setText("上传新头像");
        TextView dialog_left = (TextView) dialog.findViewById(R.id.dialog_left);
        TextView dialog_right = (TextView) dialog.findViewById(R.id.dialog_right);
        dialog_left.setText("相机");
        dialog_right.setText("相册");
        dialog_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                File dir = new File(Constant.PICTURE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, "Tu_" + Config.Tusername +
                        new SimpleDateFormat(TimeUtil.FORMAT_NORMAL).format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        Constant.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        dialog_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });
    }


    String FileUrl = "";

    private void uploadPic() {
        String filename = "Tu_" + Config.Tusername +
                new SimpleDateFormat(TimeUtil.FORMAT_NORMAL).format(new Date()) + ".png";
        path = Constant.PICTURE_PATH + filename;
        PhotoUtil.saveBitmap(Constant.PICTURE_PATH, filename,
                PhotoUtil.drawableToBitmap(pic.getDrawable()), true);
        if (path.equals("")) {
            Show("没有可上传的图片，请重新选择图片");
            if (dlLayout != null)
                dlLayout.setVisibility(View.GONE);

        } else {
            if(hasLoc) {
                if (dlLayout != null)
                    dlLayout.setVisibility(View.VISIBLE);
                BmobProFile.getInstance(this).upload(path, new UploadListener() {
                    @Override
                    public void onSuccess(String s, String s1, BmobFile bmobFile) {
                        FileUrl = bmobFile.getUrl();
                        saveInfo(FileUrl, new LatLng(lat, lng), contetnt.getText().toString());
                        if (dlLayout != null)
                            dlLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(int i) {
                        downloadTip.setText("上传完成 " + i + " %");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Show("图片上传失败：" + s);
                        if (dlLayout != null)
                            dlLayout.setVisibility(View.GONE);
                    }
                });
            }
            else
            {
                Show("没有位置信息，自动保存到离线图忆");
                saveInfo(path, null, contetnt.getText().toString());
            }
        }
    }

    private void saveInfo(final String url, LatLng position, String content) {
        final UserTuyi userTuyi = new UserTuyi();
        TUser user = new TUser();
        if (Config.tUser == null) {
            user = DemoDBManager.getInstance().getTUserByName(Tusername);
            if (user == null) {
                Show("请等待更新用户数据完成");
                return;
            }
            userTuyi.settUser(user);
        } else
            user =Config.tUser;

        if(position!=null) {
            userTuyi.settUser(user);
            userTuyi.setIsPublic(isForAll);
            userTuyi.settPic(url);
            userTuyi.settUri(path);
            userTuyi.setZan(0);
            userTuyi.setTAG(checkStr[checkIndex]);
            userTuyi.setTime(new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1)
                    .format(new Date()));
            userTuyi.settContent(content);
            userTuyi.settPoint(new BmobGeoPoint(lng,lat));
            userTuyi.setLocDes(addr.getText().toString());
            userTuyi.save(AddTuyiActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    Show("嚯嚯~保存成功");
                    Config.updateStatus(getBaseContext() ,checkStr[checkIndex]);
                    DemoDBManager.getInstance().saveTuyi(userTuyi);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    if (dlLayout != null)
                        dlLayout.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            userTuyi.setIsPublic(isForAll);
            userTuyi.settContent(contetnt.getText().toString());
            userTuyi.settUri(path);
            userTuyi.setTAG(checkStr[checkIndex]);
            userTuyi.setTime(new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
            if(user!=null)
            {
                userTuyi.setOfflineNmae(user.getUsername());
                DemoDBManager.getInstance().saveAOfflineTuyi(userTuyi);
                Show("保存成功");
                UploadOfflineTuyiFragment.hasChange = true;
            }
            else {
                final android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(this,R.style.DialogMDStyle);
                builder .setTitle("请输入正确的用户名");
                View view = LayoutInflater.from(this).inflate(R.layout.offline_get_name_dialog,null);
                final EditText editText = (EditText) view.findViewById(R.id.offline_dialog_get_name);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().equals("")) {
                            Show("请输入正确用户名");
                            return;
                        }
                        UserTuyi offTuyi = new UserTuyi();
                        offTuyi.setOfflineNmae(editText.getText().toString());
                        DemoDBManager.getInstance().saveAOfflineTuyi(offTuyi);
                        Show("保存为离线成功");
                        UploadOfflineTuyiFragment.hasChange = true;
                    }
                });
                builder.show();
            }

        }
        }


    private void saveCropAvator(String filePath) {


        Bitmap bitmap;
        if (filePath != null && !filePath.equals("")) {
            path = filePath;
            bitmap = PhotoUtil.getImageThumbnail(filePath, 720, 540);
            if (isFromCamera && degree != 0) {
                bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
            }
            ViewGroup.LayoutParams params =pic.getLayoutParams();
            if(bitmap.getWidth()<Config.getPPI(this)[0])
            {
                params.height=bitmap.getHeight() *(Config.getPPI(this)[0]/bitmap.getHeight());
                params.width =Config.getPPI(this)[0];
            }
            else if(bitmap.getWidth()>Config.getPPI(this)[0])
            {
                params.height=bitmap.getHeight() * Config.getPPI(this)[0]/bitmap.getWidth();
                params.width =Config.getPPI(this)[0];
            }
            else
            {
               params.width = bitmap.getWidth();
                params.height = bitmap.getHeight();
            }
            pic.setLayoutParams(params);
            pic.setImageBitmap(bitmap);
            //draw=pic.getDrawable();

            if (bitmap != null && bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)

        {
            case Constant.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Show("SD不可用");
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    saveCropAvator(file.getAbsolutePath());
                }
                break;
            case Constant.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                if(data!=null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = new String[]{MediaStore.Images.Media.DATA};
                    Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    path = c.getString(columnIndex);
                    c.close();

                    if (null != path) {
                        saveCropAvator(path);
                    }
                }
                break;
            default:
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bitmap!=null)
        bitmap.recycle();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.temp_save)
        {
            if(caches == null)
            {
                Show("  没有缓存图忆  ");
            }
            else {
                ObjectAnimator.ofFloat(cachePopLayout, "translationY", 1000f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(cachePopLayout, "translationX", -500f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(cachePopLayout, "scaleX", 0f, 1f).setDuration(500).start();
                ObjectAnimator.ofFloat(cachePopLayout, "scaleY", 0f, 1f).setDuration(500).start();
                if (isFirst) {
                    caches = DemoDBManager.getInstance().getAllCacheTuyi();
                    if (caches != null) {
                        cachePopLayout.setVisibility(View.VISIBLE);
                        adapter = new CacheTuyiAdapter(this, caches);
                        mListView.setPullLoadEnable(false);
                        mListView.setPullRefreshEnable(false);
                        mListView.pullRefreshing();
                        mListView.setAdapter(adapter);
                        mListView.setOnItemClickListener(this);
                        isFirst = false;
                    }
                    else
                    {
                        Show("没有缓存的图忆");
                    }
                }
                else
                {
                    cachePopLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        return true;
    }



    private void setCacheToView(UserTuyi tuyi) {
        contetnt.setText(tuyi.gettContent());
        addr.setText(tuyi.getLocDes());
        if(tuyi.gettPoint()!=null) {
            lat = tuyi.gettPoint().getLatitude();
            lng = tuyi.gettPoint().getLongitude();
        }
        if(tuyi.gettUri()!=null && !tuyi.gettUri().equals("") )
        {
            path =tuyi.gettUri();
            pic.setImageBitmap(PhotoUtil.getImageThumbnail(path,960,720));
        }
        if(tuyi.isPublic())
        {
            rbPublic.setChecked(true);
            rbPrivate.setChecked(false);
            isForAll =true;
        }
        for(int i =0;i<5;i++)
        {
            if(checkStr[i].equals(tuyi.getTAG()))
            {
                radioButton[i].setChecked(true);
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(cachePopLayout!=null && cachePopLayout.getVisibility()==View.VISIBLE) {
                cachePopLayout.setVisibility(View.GONE);
                return  true;
            }
            if(popupWindow!=null && popupWindow.isShowing())
            {
                popupWindow.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setCacheToView(adapter.getList().get(position - 1));
        ObjectAnimator.ofFloat(cachePopLayout, "translationY", 1f,1000f).setDuration(500).start();
        ObjectAnimator.ofFloat(cachePopLayout, "translationX", 1f,-500f).setDuration(500).start();
        ObjectAnimator.ofFloat(cachePopLayout, "scaleX",1f, 0f).setDuration(500).start();
        ObjectAnimator.ofFloat(cachePopLayout, "scaleY", 1f,0f).setDuration(500).start();
        cachePopLayout.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId())
        {
            case R.id.satura:
                statu = progress * 1.0f / 127;
                break;
            case R.id.hue:
                hue = (progress - 127) * 1.0f / 127 * 180;
                break;
            case R.id.lum:
                lum = progress * 1.0f / 127;
                break;
        }
        img.setImageBitmap(ImageHelper.handleImageEffect(bitmap, hue, statu, lum,waterText.getText().toString()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



}
