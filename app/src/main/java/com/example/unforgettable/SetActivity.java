package com.example.unforgettable;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.unforgettable.Bmob.Bmobhelper;
import com.example.unforgettable.Bmob.MyUser;
import com.example.unforgettable.data.LoginDataSource;
import com.example.unforgettable.data.LoginRepository;
import com.example.unforgettable.ui.login.LoginActivity;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import cn.bmob.v3.BmobUser;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.ALARM_SERVICE;


public class SetActivity extends Fragment {
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    Bmobhelper bmobhelper = new Bmobhelper();
    private SharedPreferences.Editor editor;

    private Button logoutButton;
    private Button snycButton;
    private ImageButton userPic;
    private TextView userName;
    private Button cancel;
    private Button no;
    private Button accept;
    private TextView set_time;
    private RelativeLayout relativeLayout;
    private TimePickerDialog timePickerDialog;
    private TimePicker timePicker;
    private AlertDialog dialog;
    final Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private int hour=10;
    private int minute=57;
    private String mHour ;
    private String mMinute;
    private String time;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    // There are hardcoding only for show it's just strings
    String name = "my_package_channel";
    String id = "my_package_channel_1"; // The user-visible name of the channel.
    String description = "my_package_first_channel"; // The user-visible description of the channel.
    private static final int NOTIFY_ID = 1000;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_set, container, false);

        logoutButton = view.findViewById(R.id.logout);
        snycButton = view.findViewById(R.id.snycButton);
        userPic = view.findViewById(R.id.userPic);
        userName = view.findViewById(R.id.userName);

        showUser(); // 显示当前用户

        // 初始时隐藏，当switch状态为开时View显示
        relativeLayout = view.findViewById(R.id.relativelayout);

        set_time = view.findViewById(R.id.time);
        set_time.setText("不提醒");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
         * 登出按钮监听
         */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登出
                loginRepository.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        /*
         * 数据同步按钮
         */
        snycButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择框 上传or下载
                final String[] strArray = new String[]{"上传本机数据","同步云端数据"};  // 初始化字符串数组
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());   // 实例化builder
//                builder.setIcon(R.mipmap.ic_launcher);    // 设置图标
//                builder.setTitle("简单列表");   // 设置标题
                // 设置列表
                builder.setItems(strArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
//                        Toast.makeText(getActivity(),strArray[index], Toast.LENGTH_SHORT).show();
                        if (index == 0) {
                            // 上传本机数据
                            bmobhelper.upload();
                        }
                        else if (index == 1) {
                            // 同步云端数据
                            bmobhelper.download();
                        }
                    }
                });
                builder.create().show();    // 创建并显示对话框
            }
        });
        /*
         * 设置提醒时间
         */
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v){
                dialog = new AlertDialog.Builder(getActivity()).create();   // 实例化一个AlertDialog
                dialog.show();  // 把AlertDialog初始化
                Window window = dialog.getWindow(); // 实例化一个窗口
                window.setContentView(R.layout.activity_dialog);    // 调用自定义的XML放到AlertDialog中展示
                Display display = window.getWindowManager().getDefaultDisplay();
                WindowManager.LayoutParams p = window.getAttributes();  // 获取对话框当前的参数值
                // 设置高度和宽度
//                p.height = (int) (display.getHeight() * 0.65); // 高度设置为屏幕的0.6
                p.width = (int) (display.getWidth() * 0.85);    // 宽度设置为屏幕的0.65
                p.gravity = Gravity.CENTER; // 设置位置
                window.setAttributes(p);

                cancel = window.findViewById(R.id.cancel);
                no = window.findViewById(R.id.no);
                accept = window.findViewById(R.id.accept);
                timePicker = window.findViewById(R.id.timepicker);// 获取自定义XML的控件
                timePicker.setIs24HourView(true);

                accept.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        hour = timePicker.getHour();
                        minute = timePicker.getMinute();
                        mHour = Integer.toString(hour);
                        mMinute = Integer.toString(minute);
                        if(timePicker.getMinute() < 10) {
                            time = mHour + ":" +  "0" + mMinute;
                        } else {
                            time = mHour + ":" + mMinute;
                        }
                        set_time.setText(time);

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        // 储存提醒时间
                        editor = getActivity().getSharedPreferences("Alert", MODE_PRIVATE).edit();
                        editor.putString("alertTime", time);
                        editor.apply();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        // time = "不提醒";
                        set_time.setText(time);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        /*
         * 换头像
         */
        userPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
    }

    /*
     * 显示当前用户
     */
    private void showUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        userName.setText(myUser.getNickname());
    }


    /*
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setTitle("添加图片");
        String[] items = { "选择本地照片", "拍照" };

        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent (Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        // 用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/test/" + System.currentTimeMillis() + ".jpg");
                        file.getParentFile().mkdirs();

                        // 改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
                        tempUri = FileProvider.getUriForFile(getActivity(), "com.example.litepaltest.fileprovider", file);

                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }

        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /*
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        // com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /*
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            userPic.setImageBitmap(mBitmap);    // 显示图片

            // 保存
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("picture",MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
//            String imageBase64 = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);
//            editor.putString("pic",imageBase64 );
//            editor.apply();
        }
    }

}
