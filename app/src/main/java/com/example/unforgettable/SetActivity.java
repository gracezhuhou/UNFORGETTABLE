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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import cn.bmob.v3.BmobUser;

import static android.app.Activity.RESULT_OK;
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

//    private Bitmap mBitmap;
//    protected static final int CHOOSE_PICTURE = 0;
//    protected static final int TAKE_PICTURE = 1;
//    protected static Uri imageUri;
//    private static final int CROP_SMALL_PICTURE = 2;

    // 创建文件保存拍照的图片
    File takePhotoImage = new File(Environment.getExternalStorageDirectory(), "user_image.jpg");
    private Uri imageUri;// 拍照时的图片uri
    //拍照相关变量
    private static final int TAKE_PHOTO = 11;// 拍照
    private static final int CROP_PHOTO = 12;// 裁剪图片
    private static final int LOCAL_CROP = 13;// 本地图库

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
                takePhotoOrSelectPicture();
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


    /**
     *拍照or图库实现
     */
    private void takePhotoOrSelectPicture() {
        CharSequence[] items = {"拍照","图库"};// 裁剪items选项

        // 弹出对话框提示用户拍照或者是通过本地图库选择图片
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            // 选择了拍照
                            case 0:
                                try {
                                    // 文件存在，删除文件
                                    if(takePhotoImage.exists()){
                                        takePhotoImage.delete();
                                    }
                                    // 根据路径名自动的创建一个新的空文件
                                    takePhotoImage.createNewFile();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(Build.VERSION.SDK_INT >= 24){
                                    imageUri = FileProvider.getUriForFile(getActivity(),"com.example.unforgettable.fileprovider", takePhotoImage);
                                }else {
                                    imageUri = Uri.fromFile(takePhotoImage);
                                }

                                // 创建Intent，用于启动手机的照相机拍照
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                // 指定输出到文件uri中
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                // 启动intent开始拍照
                                startActivityForResult(intent, TAKE_PHOTO);
                                //getPicFromCamera();//调用相机
                                break;

                            // 调用系统图库
                            case 1:
                                // 创建Intent，用于打开手机本地图库选择图片
                                Intent intent1 = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // 启动intent打开本地图库
                                startActivityForResult(intent1,LOCAL_CROP);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 回调接口
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:// 拍照
                if(resultCode == RESULT_OK){
                    // 创建intent用于裁剪图片
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    // 设置数据为文件uri，类型为图片格式
                    intent.setDataAndType(imageUri,"image/*");
                    // 允许裁剪
                    intent.putExtra("scale",true);
                    // 指定输出到文件uri中
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    // 启动intent，开始裁剪
                    startActivityForResult(intent, CROP_PHOTO);

                    // 用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(getActivity(),"com.example.unforgettable.fileprovider", takePhotoImage);
                        cropPhoto(imageUri);//裁剪图片
                    } else {
                        cropPhoto(Uri.fromFile(takePhotoImage));//裁剪图片
                    }

                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        userPic.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e){
                        e.printStackTrace();
                    }


                }
                break;
            case LOCAL_CROP:// 系统图库

                if(resultCode == RESULT_OK){
                    // 创建intent用于裁剪图片
                    Intent intent1 = new Intent("com.android.camera.action.CROP");
                    // 获取图库所选图片的uri
                    Uri uri = data.getData();
                    intent1.setDataAndType(uri,"image/*");
                    //  设置裁剪图片的宽高
                    intent1.putExtra("outputX", 300);
                    intent1.putExtra("outputY", 300);
                    // 裁剪后返回数据
                    intent1.putExtra("return-data", true);
                    // 启动intent，开始裁剪
                    startActivityForResult(intent1, CROP_PHOTO);
                }

                break;
            case CROP_PHOTO:// 裁剪后展示图片
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    //设置到ImageView上
                    userPic.setImageBitmap(image);
                    //也可以进行一些保存、压缩等操作后上传
                    String path = saveImage("userPic", image);
                    File file = new File(path);
                }
//                if (resultCode == RESULT_OK) {
//                    try {
//                        bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
//                        Currency();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
        }
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_PHOTO);
    }

    /**
     * 保存图片到本地
     *
     * @param name
     * @param bmp
     * @return
     */
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
