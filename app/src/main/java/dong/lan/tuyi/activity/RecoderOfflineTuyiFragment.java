package dong.lan.tuyi.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import dong.lan.tuyi.Constant;
import dong.lan.tuyi.R;
import dong.lan.tuyi.bean.UserTuyi;
import dong.lan.tuyi.db.DemoDBManager;
import dong.lan.tuyi.util.PhotoUtil;
import dong.lan.tuyi.utils.Config;
import dong.lan.tuyi.utils.ImageHelper;
import dong.lan.tuyi.utils.TimeUtil;

/**
 * Created by 桂栋 on 2015/8/22.
 */
public class RecoderOfflineTuyiFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener {

    private EditText content;
    private ImageView pic;
    private CheckBox checkBox;
    private RadioButton radioButton[] = new RadioButton[5];
    private int rbId[] = new int[]{R.id.za,R.id.shi,R.id.jing,R.id.qing,R.id.wan};
    private int checkIndex;
    private float statu = 1;
    private float hue =0;
    private float lum = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fram_offline_tuyi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        TextView bar_center = (TextView) getView().findViewById(R.id.bar_center);
        TextView bar_left = (TextView) getView().findViewById(R.id.bar_left);
        TextView bar_right = (TextView) getView().findViewById(R.id.bar_right);
        content = (EditText) getView().findViewById(R.id.offline_content);
        checkBox = (CheckBox) getView().findViewById(R.id.offline_check);
        pic = (ImageView) getView().findViewById(R.id.offline_pic);
        pic.setOnClickListener(this);
        pic.setOnLongClickListener(this);
        bar_right.setOnClickListener(this);
        bar_left.setOnClickListener(this);
        bar_center.setText("添加离线图忆");
        bar_right.setText("完成");
        RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.label_layout);
        for(int i =0;i<5;i++)
        {
            radioButton[i] = (RadioButton) getView().findViewById(rbId[i]);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < 5; i++) {
                    if (checkedId == rbId[i]) {
                        checkIndex = i;
                        break;
                    }
                }
            }
        });

    }

    int tag = 0;
    String filePath = "";
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;
    String path = "";

    private void saveCropAvator(String filePath) {
        Bitmap bitmap = null;
        if (filePath != null && !filePath.equals("")) {
            String filename = "OFF_" + Config.Tusername +
                    new SimpleDateFormat(TimeUtil.FORMAT_NORMAL).format(new Date()) + ".png";
            bitmap = PhotoUtil.getImageThumbnail(filePath, 960, 640);
            path = PhotoUtil.saveBitmapWithPath(Constant.PICTURE_PATH,filename,bitmap,true);
//            if (isFromCamera && degree != 0) {
//                bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
//            }
//            ViewGroup.LayoutParams params =pic.getLayoutParams();
//            if(bitmap.getWidth()<Config.getPPI(getActivity())[0])
//            {
//                params.height=bitmap.getHeight() *(Config.getPPI(getActivity())[0]/bitmap.getHeight());
//                params.width =Config.getPPI(getActivity())[0];
//            }
//            else if(bitmap.getWidth()>Config.getPPI(getActivity())[0])
//            {
//                params.height=bitmap.getHeight() * Config.getPPI(getActivity())[0]/bitmap.getWidth();
//                params.width =Config.getPPI(getActivity())[0];
//            }
//            else
//            {
//                params.width = bitmap.getWidth();
//                params.height = bitmap.getHeight();
            //}
            //pic.setLayoutParams(params);
            pic.setImageBitmap(bitmap);
            if (bitmap != null && bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == -1) {
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
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
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

    private void resetView()
    {
        content.setText("");
        pic.setImageResource(R.drawable.signin_local_gallry);
        path = "";
    }
    String username="";

    private void saveOfflineTuyi(String name)
    {
        UserTuyi offTuyi = new UserTuyi();
        offTuyi.setIsPublic(checkBox.isChecked());
        offTuyi.setOfflineNmae(name);
        offTuyi.settContent(content.getText().toString());
        offTuyi.settUri(path);
        offTuyi.setTAG(AddTuyiActivity.checkStr[checkIndex]);
        offTuyi.setTime(new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1).format(new Date()));
        DemoDBManager.getInstance().saveAOfflineTuyi(offTuyi);
        Show("保存成功");
        resetView();
        UploadOfflineTuyiFragment.hasChange = true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_left:
                getActivity().finish();
                break;
            case R.id.bar_right:
                if (content.getText().toString().equals("")) {
                    Show("内容不能为空");
                    return;
                }
                if(Config.tUser==null) {
                    final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogMDStyle);
                    builder.setTitle("警告,请输入用户名");
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.offline_get_name_dialog, null);
                    final EditText editText = (EditText) view.findViewById(R.id.offline_dialog_get_name);
                    builder.setView(view);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editText.getText().toString().equals("")) {
                                Show("请输入正确用户名");
                                return;
                            }
                            saveOfflineTuyi(editText.getText().toString());
                        }
                    });
                    builder.show();
                }
                else
                {
                    saveOfflineTuyi(Config.tUser.getUsername());
                }
                break;
            case R.id.offline_pic:
                final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setCancelable(true);
                dialog.show();
                dialog.getWindow().setContentView(R.layout.dialog_set_head_img);
                TextView msg = (TextView) dialog.findViewById(R.id.dialog_msg);
                msg.setText("选择图片");
                TextView dialog_left = (TextView) dialog.findViewById(R.id.dialog_left);
                TextView dialog_right = (TextView) dialog.findViewById(R.id.dialog_right);
                dialog_left.setText("相机");
                dialog_right.setText("相册");
                dialog_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        dialog.dismiss();
                    }
                });
                dialog_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, Constant.REQUESTCODE_UPLOADAVATAR_LOCATION);
                        dialog.dismiss();
                    }
                    });
        }
    }

    private void Show(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    PopupWindow popupWindow ;
    Bitmap bitmap;
    ImageView img;
    EditText waterText;
    private void handleImgPop()
    {
        bitmap = PhotoUtil.drawableToBitmap(pic.getDrawable());
        View view = LayoutInflater.from(getActivity())
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
                img.setImageBitmap(ImageHelper.handleImageEffect(bitmap, (hue.getProgress()-127)/127*180f, sta.getProgress()/127f, lum.getProgress()/127f, waterText.getText().toString()));
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
            popupWindow.showAtLocation(getView().findViewById(R.id.offlineParent), Gravity.CENTER,0,0);

    }
    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.offline_pic)
        {
            handleImgPop();
        }
        return false;
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
