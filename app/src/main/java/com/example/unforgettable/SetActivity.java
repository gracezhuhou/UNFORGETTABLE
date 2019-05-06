package com.example.unforgettable;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.unforgettable.Bmob.Bmobhelper;
import com.example.unforgettable.Bmob.MyUser;
import com.example.unforgettable.data.LoginDataSource;
import com.example.unforgettable.data.LoginRepository;
import com.example.unforgettable.ui.login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.baidu.ocr.sdk.utils.ImageUtil.calculateInSampleSize;


public class SetActivity extends Fragment {
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    Bmobhelper bmobhelper = new Bmobhelper();
    private SharedPreferences.Editor editor;

    private Button logoutButton;
    private Button snycButton;
    private ImageView userPic;
    private TextView userName;
    private Button cancel;
    private Button no;
    private Button accept;
    private TextView set_time;
    private RelativeLayout relativeLayout;
    private TimePickerDialog timePickerDialog;
    private TimePicker timePicker;
    private AlertDialog dialog;
    private Button appearanceButton;
    final Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private int hour=10;
    private int minute=57;
    private String mHour ;
    private String mMinute;
    private String time;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private String temp;
    private SharedPreferences pref;

//    private Bitmap mBitmap;
//    protected static final int CHOOSE_PICTURE = 0;
//    protected static final int TAKE_PICTURE = 1;
//    protected static Uri imageUri;
//    private static final int CROP_SMALL_PICTURE = 2;

    // 创建文件保存拍照的图片
    File takePhotoImage = new File(Environment.getExternalStorageDirectory(), "userPic.jpg");
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
        appearanceButton = view.findViewById(R.id.change_appearance);

        showUser(); // 显示当前用户

        // 初始时隐藏，当switch状态为开时View显示
        relativeLayout = view.findViewById(R.id.relativelayout);

        set_time = view.findViewById(R.id.time);

        pref = getActivity().getSharedPreferences("Alert", MODE_PRIVATE);
        temp = pref.getString("alertTime", "");
        if (temp.equals("")) {
            set_time.setText("不提醒");
        } else {
            set_time.setText(temp);
        }

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
                // 上传云端
                //bmobhelper.logout();

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

                        if(!temp.equals("")) {
                            Calendar instance = Calendar.getInstance();
                            //是设置日历的时间，主要是让日历的年月日和当前同步
                            instance.setTimeInMillis(System.currentTimeMillis());
                            // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
                            instance.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                            long systemTime = System.currentTimeMillis();
                            instance.set(Calendar.HOUR_OF_DAY, hour);
                            instance.set(Calendar.MINUTE, minute);
                            instance.set(Calendar.SECOND, 0);
                            instance.set(Calendar.MILLISECOND, 0);
                            long selectTime = instance.getTimeInMillis();

                            // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
                            if(systemTime > selectTime) {
                                instance.add(Calendar.DAY_OF_MONTH, 1);
                            }

                            AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                            intent.setAction("NOTIFICATION");
                            PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

                            int type = AlarmManager.RTC_WAKEUP;
                            alarmManager.set(type, instance.getTimeInMillis(), pi);
                        }
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        //time = "不提醒";
                        set_time.setText("不提醒");
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

        /*
        * 编辑用户信息
        */
        userName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditUserActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        /*
         * 换肤
         */
        appearanceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AppearanceChange.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void setTime() {
        pref = getActivity().getSharedPreferences("Alert", MODE_PRIVATE);
        temp = pref.getString("alertTime", "");
        if(!temp.equals("")) {
            String[] ptr = temp.split(":");
            hour = Integer.parseInt(ptr[0]);
            minute = Integer.parseInt(ptr[1]);
        }
    }

    /*
     * 显示当前用户
     */
    private void showUser() {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        userName.setText(myUser.getNickname());
        Drawable appPic = getResources().getDrawable(R.drawable.ic_logo);
        if (myUser.getPicture() != null) {
            String picUrl = myUser.getPicture().getUrl();
            userPic.setImageDrawable(appPic);
            Picasso.get().load(picUrl).into(userPic);
        }
        else {
            String picPath = Environment.getExternalStorageDirectory().getPath() + "/cardPic.jpg";
            File picFile = new File(picPath);
            //if (picFile.exists()){
            //    Picasso.get().load(picFile).into(userPic);
            //}
            //else
                userPic.setImageDrawable(appPic);
    }
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

                                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},1);
                                    // 创建Intent，用于启动手机的照相机拍照
                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    // 指定输出到文件uri中
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                    // 启动intent开始拍照
                                    startActivityForResult(intent, TAKE_PHOTO);
                                    //getPicFromCamera();//调用相机
                                } else {
                                    // 创建Intent，用于启动手机的照相机拍照
                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    // 指定输出到文件uri中
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                    // 启动intent开始拍照
                                    startActivityForResult(intent, TAKE_PHOTO);
                                    //getPicFromCamera();//调用相机
                                }

                                break;

                            // 调用系统图库
                            case 1:
                                // 创建Intent，用于打开手机本地图库选择图片
                                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                    Intent intent1 = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                    // 启动intent打开本地图库
                                    // 根据路径名自动的创建一个新的空文件
                                    startActivityForResult(intent1,LOCAL_CROP);
                                } else {
                                    Intent intent1 = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                    // 启动intent打开本地图库
                                    startActivityForResult(intent1,LOCAL_CROP);
                                }
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
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    // 启动intent，开始裁剪
//                    startActivityForResult(intent, CROP_PHOTO);

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

                        // todo:
                        String path = Environment.getExternalStorageDirectory().getPath()+"/userPic.jpg";
                        bitmap = getSmallBitmap(path);
//                        MCompressor.from()
//                                .compressFormat(Bitmap.CompressFormat.JPEG)
//                                .quality(80)
//                                .greaterThan(500, CompressUnit.KB)
//                                .fromFilePath(path)
//                                .toFilePath(path)
//                                .compress();

//                        bitmap = compressImage(bitmap);
//                        File file = new File(path);
//                        qualityCompress(bitmap,file);


                        saveImage("userPic", bitmap);
                        bmobhelper.uploadPic();
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
                    // aspectX aspectY 是宽高的比例
                    intent1.putExtra("aspectX", 1);
                    intent1.putExtra("aspectY", 1);
                    //  设置裁剪图片的宽高
                    intent1.putExtra("outputX", 300);
                    intent1.putExtra("outputY", 300);
                    intent1.putExtra("scale", true);
                    // 裁剪后返回数据
                    intent1.putExtra("return-data", true);
                    // 启动intent，开始裁剪
                    startActivityForResult(intent1, CROP_PHOTO);
                }

                break;
            case CROP_PHOTO:// 裁剪后展示图片
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //设置到ImageView上
                        userPic.setImageBitmap(image);
                        //也可以进行一些保存、压缩等操作后上传
                        String path = saveImage("userPic", image);
                        bmobhelper.uploadPic();

                        //File file = new File(path);
                    }
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

//    //质量压缩
//    public static void compressBmpToFile(Bitmap bmp,File file){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int options = 80;
//        bmp.compress(Bitmap.CompressFormat.JPEG,options,baos);
//        while(baos.toByteArray().length/1024>100){
//            baos.reset();
//            options -= 10;
//            bmp.compress(Bitmap.CompressFormat.JPEG,options,baos);
//        }
//        try{
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(baos.toByteArray());
//            fos.flush();
//            fos.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    //尺寸压缩
//    private Bitmap sizeCompress(String path, int rqsW, int rqsH) {
//        // 用option设置返回的bitmap对象的一些属性参数
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;// 设置仅读取Bitmap的宽高而不读取内容
//        BitmapFactory.decodeFile(path, options);// 获取到图片的宽高，放在option里边
//        final int height = options.outHeight;//图片的高度放在option里的outHeight属性中
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (rqsW == 0 || rqsH == 0) {
//            options.inSampleSize = 1;
//        } else if (height > rqsH || width > rqsW) {
//            final int heightRatio = Math.round((float) height / (float) rqsH);
//            final int widthRatio = Math.round((float) width / (float) rqsW);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//            options.inSampleSize = inSampleSize;
//        }
//        return BitmapFactory.decodeFile(path, options);// 主要通过option里的inSampleSize对原图片进行按比例压缩
//    }
//
//    /**
//     * 质量压缩方法
//     *
//     * @param image
//     * @return
//     */
//    public static Bitmap compressImage(Bitmap image) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;//每次都减少10
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
//        return bitmap;
//    }


//    /**
//     * 质量压缩
//     * 设置bitmap options属性，降低图片的质量，像素不会减少
//     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
//     * 设置options 属性0-100，来实现压缩
//     *
//     * @param bmp
//     * @param file
//     */ public static void qualityCompress(Bitmap bmp, File file) {
//        // 0-100 100为不压缩
//        int quality = 20;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        // 把压缩后的数据存放到baos中
//        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(baos.toByteArray());
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


}
