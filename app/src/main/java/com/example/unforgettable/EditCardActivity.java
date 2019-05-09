package com.example.unforgettable;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.Bmob;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.baidu.ocr.sdk.utils.ImageUtil.calculateInSampleSize;
import static org.litepal.LitePalApplication.getContext;

public class EditCardActivity extends AppCompatActivity {
    // 前端相关变量
    private Button submitButton;
    private EditText sourceInput;
    private EditText authorInput;
    private EditText headingInput;
    private Button typeButton;
    private Button cameraButton;
    private Button soundButton;
    private ImageButton starButton;
    private Button playButton;
    private EditText contentInput;
    private Button clearButton;
    private Button backButton;
    private ProgressBar loading;
    private ImageView cardPic;
    private ImageButton btdel;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private String oldheading; // 原标题
    private String source;  // 来源
    private String author;  // 作者
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like = false;   // 收藏
    private String tab;     // 标签

    //录音相关变量
    private boolean mIsRecordingState = false;// 是否是录音状态
    private boolean mIsPlayState = false;// 是否是播放状态
    private MediaRecorder mRecorder = null;// 录音操作对象
    private MediaPlayer mPlayer = null;// 媒体播放器对象
    private String mFileName = null;// 录音存储路径
    private String TAG = getClass().getSimpleName();
    private boolean isAudio = false;
    private boolean isReadFinish = true;


    //拍照相关变量
    private static final int TAKE_PHOTO = 11;// 拍照
    private static final int CROP_PHOTO = 12;// 裁剪图片
    private static final int LOCAL_CROP = 13;// 本地图库
    private static final int GET_TAB = 1;

    private String path = "";
    private Uri imageUri;// 拍照时的图片uri
    //调用照相机返回图片文件
    private File tempFile;
    //调用图库返回图片文件
    private File file;

    // 创建文件保存拍照的图片
    File takePhotoImage = new File(Environment.getExternalStorageDirectory(), "cardPic.jpg");

    //图片转文字相关变量
    private TextView txt;
    private boolean hasGotToken = false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("Alert", MODE_PRIVATE);
        int mode = pref.getInt("background", -1);
        if (mode == -1) {
            setTheme(R.style.AppTheme_Base_Base);
        }
        else {
            setTheme(mode);
        }
        setContentView(R.layout.activity_editcard);

        //初始化SDK
        // 5cc9274e为申请的 APPID
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5cc9274e");

        //设置id
        submitButton = findViewById(R.id.submitButton);
        sourceInput = findViewById(R.id.sourceInput);
        authorInput = findViewById(R.id.authorInput);
        headingInput = findViewById(R.id.headingInput);
        typeButton = findViewById(R.id.typeButton);
        cameraButton = findViewById(R.id.cameraButton);
        soundButton = findViewById(R.id.soundButton);
        starButton = findViewById(R.id.starButton);
        contentInput = findViewById(R.id.contentInput);
        backButton = findViewById(R.id.backButton);
        playButton = findViewById(R.id.playbutton);
        cardPic = findViewById(R.id.card_pic);
        btdel = findViewById(R.id.bt_del);
        loading = findViewById(R.id.loading);
        clearButton = findViewById(R.id.clearButton);

        init();     // 显示原卡片内容
        setListener();
    }

    // 初始化界面，显示原卡片内容
    public void init(){
        Intent intent = getIntent();
        oldheading = intent.getStringExtra("heading_extra");
        memoryCardsList card = dbhelper.findCard(oldheading);
        sourceInput.setText(card.getSource());
        authorInput.setText(card.getAuthor());
        headingInput.setText(oldheading);
        contentInput.setText(card.getContent());
        // 标签
        if (card.getTab().equals(""))
            typeButton.setText("标签");
        else
            typeButton.setText(card.getTab());
        // 改收藏按键颜色状态
        if (card.isLike()) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
            starButton.setImageDrawable(drawable);
            like = true;
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
            starButton.setImageDrawable(drawable);
            like = false;
        }


        // 显示图片
        byte[] images = card.getPicture();
        if (images != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
            cardPic.setImageBitmap(bitmap);
        }
        else {
            btdel.setVisibility(View.INVISIBLE);
        }

        // 音频
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + oldheading + ".3gp";
        File audioFile = new File(mFileName);
        if (!audioFile.exists())
            playButton.setVisibility(View.INVISIBLE);

//        // 设置音频sdcard的路径
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//            mFileName += "/audio.3gp";
//        }

        Log.v("卡片编辑界面","初始化页面完成");
    }

    // 事件响应
    public void setListener(){
        // 提交按钮监听
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 60}, -1);

                getInput(); //获取用户输入内容

                // 选择标签
                if (typeButton.getText() == "标签") {
                    tab = "";
                }
                else tab = typeButton.getText().toString();

                //添加记录
                if (dbhelper.updateCard(oldheading, source, author, heading, content, like, tab)) {
                    if (isAudio) {
                        File audioFile = new File(mFileName);
                        String fileName = heading + ".3gp";
                        File newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
                        // 文件重命名
                        audioFile.renameTo(newfile);

                        // 删除旧文件
                        String oldFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + oldheading + ".3gp";
                        File oldFileName = new File(oldFile);
                        if (oldFileName.exists()){
                            oldFileName.delete();
                        }
                    }
                    // 没有更改录音，将原录音重新命名
                    else if (!oldheading.equals(heading)){
                        String oldFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + oldheading + ".3gp";
                        File oldFileName = new File(oldFile);
                        if (oldFileName.exists()){
                            String fileName = heading + ".3gp";
                            File newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
                            oldFileName.renameTo(newfile);
                        }
                    }

                    Toast.makeText(getApplicationContext(), "新建卡片成功", Toast.LENGTH_SHORT).show();
                    finish();   //返回复习界面
                }
                Log.v("卡片编辑界面","提交按钮点击事件");

            }
        });
        // 收藏按钮响应
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);

                // 改收藏按键颜色状态
                Drawable.ConstantState drawableState = starButton.getDrawable().getConstantState();
                Drawable.ConstantState drawableState_yel = getResources().getDrawable(R.drawable.ic_star_yel).getConstantState();
                if (!drawableState.equals(drawableState_yel)) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
                    // 这一步必须要做,否则不会显示.
                    starButton.setImageDrawable(drawable);
                    like = true;
                }
                else {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
                    // 这一步必须要做,否则不会显示.
                    starButton.setImageDrawable(drawable);
                    like = false;
                }
                Log.v("卡片编辑界面","收藏按钮点击事件");
            }
        });
        // 返回按钮响应
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        //录音按钮监听
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.RECORD_AUDIO},1);
                }
                initSpeech(getContext());
            }
        });

        //播放按钮监听
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);

                // 判断播放按钮的状态，根据相应的状态处理事务
                playButton.setText(R.string.wait_for);
                playButton.setEnabled(false);
                if (mIsPlayState) {
                    stopPlay();
                    playButton.setText(R.string.start_play);
                } else {
                    startPlay();
                    playButton.setText(R.string.stop_play);
                }
                mIsPlayState = !mIsPlayState;
                playButton.setEnabled(true);
            }
        });

        // 拍照按钮监听
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.CAMERA},1);
                }
                //初始化orc,获取token
                takePhotoOrSelectPicture();// 拍照或者调用图库
            }
        });

        cardPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);
//               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////                 builder.setTitle("提升");
////                 builder.setMessage("");
//               builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                   @Override
//                   public void onClick(DialogInterface dialog, int which) {
//                       //删除系统缩略图
//                       getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
//                       //删除手机中图片
//                       file.delete();
//
//                       iv_show_picture.setBackgroundResource(0);
//                       //当BackgroundResource的值设置为0的时候遇有R里面没有这个值，所以默认背景图片就不会显示了
//                       iv_show_picture.setImageBitmap(bitmap);
//
//                       //判断是否删除成功
//                       if(iv_show_picture.getDrawable() == null){
//                           Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();
//                        }
//                    }
//           });
//               builder.setNegativeButton("",null);
//               builder.show();
                btdel.setVisibility(View.VISIBLE);
                btdel.bringToFront();
                return true;
            }
        });

        //删除按钮监听
        btdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 50}, -1);
                //初始化orc,获取token
                if(cardPic.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "还未添加照片噢", Toast.LENGTH_LONG).show();
                }
                else deletePic();
            }
        });

        // 标签按钮监听
        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);

                Intent intent = new Intent(getApplicationContext(),TablistActivity.class);
                startActivityForResult(intent,1);
            }
        });

        // 清空按钮
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 60}, -1);
                clearAll();
            }
        });

        initAccessTokenWithAkSk();
    }

    //获取用户输入内容
    private void getInput(){
        source = sourceInput.getText().toString();
        author = authorInput.getText().toString();
        heading = headingInput.getText().toString();
        content = contentInput.getText().toString();
    }

    public void initSpeech(final Context context) {

        //若当前录音按钮显示为“开始录音”
        if(soundButton.getText().equals("录音")){
            CharSequence[] items = {"保存录音","识别录音"};// 录音items选项

            // 弹出对话框提示用户保存录音或者是识别录音
            new AlertDialog.Builder(EditCardActivity.this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);

                            switch (which){
                                // 选择了保存录音
                                case 0:
                                    vibrator.vibrate(new long[]{0, 40}, -1);

                                    isAudio = true;
                                    playButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "正在录音，请讲话", Toast.LENGTH_LONG).show();
                                    // 判断录音按钮的状态，根据相应的状态处理事务
                                    soundButton.setText(R.string.wait_for);
                                    soundButton.setEnabled(false);
                                    if (mIsRecordingState) {
                                        stopRecording();
                                        soundButton.setText("录音");
                                    } else {
                                        startRecording();
                                        soundButton.setText(R.string.stop_recording);
                                    }
                                    mIsRecordingState = !mIsRecordingState;
                                    soundButton.setEnabled(true);

                                    break;
                                // 识别语音
                                case 1:
                                    vibrator.vibrate(new long[]{0, 40}, -1);

                                    //1.创建RecognizerDialog对象
                                    RecognizerDialog mDialog = new RecognizerDialog(context, null);
                                    //2.设置accent、language等参数
                                    mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//语种，这里可以有zh_cn和en_us
                                    mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//设置口音，这里设置的是汉语普通话
                                    mDialog.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");//设置编码类型
                                    //3.设置回调接口
                                    mDialog.setListener(new RecognizerDialogListener() {
                                        @Override
                                        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                                            if (!isLast) {
                                                //解析语音
                                                //返回的result为识别后的汉字,直接赋值到TextView上即可
                                                String result = parseVoice(recognizerResult.getResultString());
                                                content = content + result;
                                                //content.substring(4,content.length()-1);
                                                contentInput.setText(content);
                                                contentInput.getText().delete(0,4);
                                            }
                                        }

                                        @Override
                                        public void onError(SpeechError speechError) {
                                            Log.e("返回的错误码", speechError.getErrorCode() + "");
                                        }
                                    });
                                    //4.显示dialog，接收语音输入
                                    mDialog.show();

                                    break;
                            }

                        }
                    }).show();
        }
        else{
            //当前“录音”按钮显示为“停止录音”
            // 判断录音按钮的状态，根据相应的状态处理事务
            soundButton.setText(R.string.wait_for);
            soundButton.setEnabled(false);
            if (mIsRecordingState) {
                stopRecording();
                soundButton.setText("录音");
            } else {
                startRecording();
                soundButton.setText(R.string.stop_recording);
            }
            mIsRecordingState = !mIsRecordingState;
            soundButton.setEnabled(true);

        }

    }

    /**
     * 开始录音
     */
    private void startRecording() {

        mRecorder = new MediaRecorder();
        // 设置声音来源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置所录制的音视频文件的格式。(3gp)
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置录制的音频文件的保存位置。
        if (mFileName == null) {
            Toast.makeText(getApplicationContext(), R.string.no_sd, Toast.LENGTH_SHORT).show();
        } else {
            mRecorder.setOutputFile(mFileName);
            Log.d(TAG, mFileName);
        }
        // 设置所录制的声音的编码格式。
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.e_recording));
        }
        mRecorder.start();// 开始录音
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);// 设置多媒体数据来源
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, getString(R.string.e_play));
        }
        // 播放完成，改变按钮状态
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsPlayState = !mIsPlayState;
                playButton.setText(R.string.start_play);
            }
        });
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        mPlayer.release();
        mPlayer = null;
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        RecordActivity.Voice voiceBean = gson.fromJson(resultString, RecordActivity.Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<RecordActivity.Voice.WSBean> ws = voiceBean.ws;
        for (RecordActivity.Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }


    /**
     * 语音对象封装
     */
    public class Voice {

        public ArrayList<RecordActivity.Voice.WSBean> ws;

        public class WSBean {
            public ArrayList<RecordActivity.Voice.CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }

    /**
     *拍照or图库实现
     */
    private void takePhotoOrSelectPicture() {
        CharSequence[] items = {"拍照","图库"};// 裁剪items选项

        // 弹出对话框提示用户拍照或者是通过本地图库选择图片
        new AlertDialog.Builder(EditCardActivity.this)
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
                                    imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.unforgettable.fileprovider", takePhotoImage);
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
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case TAKE_PHOTO:// 拍照
                submitButton.setEnabled(false);

                if(resultCode == RESULT_OK){
                    // 创建intent用于裁剪图片
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    // 设置数据为文件uri，类型为图片格式
                    intent.setDataAndType(imageUri,"image/*");
                    // 允许裁剪
                    intent.putExtra("scale",true);
                    // 指定输出到文件uri中
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(imageUri));
                        cardPic.setImageBitmap(bitmap);
                        //btdel.setVisibility(View.VISIBLE);

                        String path = Environment.getExternalStorageDirectory().getPath()+"/cardPic.jpg";
                        bitmap = getSmallBitmap(path);
                        saveImage("cardPic", bitmap);
                        file = takePhotoImage;

                    }
                    catch (FileNotFoundException e){
                        e.printStackTrace();
                    }


                    //getSmallBitmap(path);

                    final Toast toast =  Toast.makeText(getApplicationContext(), "正在识别中，请稍等...", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    //延长土司时间
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    },1000000);

                    Currency();

//                    loading.setVisibility(View.GONE);

//                    //若识别出字符
//                    if(content!=null){
//                        //loading.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "识别成功", Toast.LENGTH_LONG).show();
//                    }
//                    else {
//                        //loading.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "无法识别", Toast.LENGTH_LONG).show();
//                    }
//                    // 启动intent，开始裁剪
//                    startActivityForResult(intent, CROP_PHOTO);

                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        imageUri = FileProvider.getUriForFile(getActivity(),"com.example.unforgettable.fileprovider", takePhotoImage);
//                        cropPhoto(imageUri);//裁剪图片
//                    } else {
//                        cropPhoto(Uri.fromFile(takePhotoImage));//裁剪图片
//                    }

//                    try{
//                        //将拍摄的照片显示出来
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
//                        iv_show_picture.setImageBitmap(bitmap);
//                    }
//                    catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }


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
                if(data != null){
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //设置到ImageView上
                        cardPic.setImageBitmap(image);
                        //btdel.setVisibility(View.VISIBLE);
                        //也可以进行一些保存、压缩等操作后上传
                        //String name = "";

                        path = saveImage("cardPic", image);

                        Toast.makeText(getApplicationContext(),"图片已保存", Toast.LENGTH_SHORT).show();
                        file = new File(path);
                        /*
                         *上传文件的额操作
                         */

//                    //解析图片进行文字识别
//                    Currency();
                    }
                }
//                if (resultCode == RESULT_OK) {
//                    try {
//                        bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
                break;

            case GET_TAB: //tab标签
                if (resultCode == RESULT_OK && data != null){
                    String returndata = data.getStringExtra("tabname");
                    typeButton.setText(returndata);
                    Log.d("RecordActivity", returndata);
                }

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

    //初始化我们的orc,获取token
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.e("TGA","token:"+token);
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                Log.e("TGA","AK，SK方式获取token失败");
//                error.printStackTrace();
//                Toast.makeText(MainActivity.this,"AK，SK方式获取token失败",Toast.LENGTH_LONG).show();

            }
        }, getApplicationContext(),  "U58ZZblInp8Txf68wiipMDGh", "16LiSrbQUSt8heyVPHUb5kkrp5fxAbxZ");
    }

    /**
     * uri 转相对路径
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    //解析图片进行文字识别
    public void Currency(){
        final StringBuffer sbPic=new StringBuffer();
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        //param.setDetectDirection(true);
        //String str = getRealFilePath(getActivity(),imageUri);
        //Log.e("TGA",str+"------str-------------");
        param.setImageFile(takePhotoImage);
        //param.setImageFile(new File(getRealFilePath(getActivity(),imageUri)));

// 调用通用文字识别服务
        OCR.getInstance(getApplicationContext()).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                for (WordSimple wordSimple : result.getWordList()) {
                    // wordSimple不包含位置信息
                    sbPic.append(wordSimple.getWords());
                    sbPic.append("\n");
                }
                content = sbPic.toString();
                contentInput.setText(sbPic.toString());

                // json格式返回字符串
//                listener.onResult(result.getJsonRes());
                Toast.makeText(getApplicationContext(),"文字识别完成", Toast.LENGTH_SHORT).show();
                submitButton.setEnabled(true); // 可以提交
                isReadFinish = true;
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                Toast.makeText(getApplicationContext(),"无法识别文字", Toast.LENGTH_SHORT).show();
                isReadFinish = true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(getApplicationContext()).release();
    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUESTCODE) {
//            //询问用户权限
//            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
//                //用户同意
//            } else {
//                //用户不同意
//            }
//        }
//    }

    //图片压缩

    //    //质量压缩
    public static void compressBmpToFile(Bitmap bmp,File file){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;
        bmp.compress(Bitmap.CompressFormat.JPEG,options,baos);
        while(baos.toByteArray().length/1024>100){
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG,options,baos);
        }
        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

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

    private void deletePic(){
        //删除系统缩略图
        getApplicationContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
        //删除手机中图片
        file.delete();

        cardPic.setBackgroundResource(0);
        //当BackgroundResource的值设置为0的时候遇有R里面没有这个值，所以默认背景图片就不会显示了
        cardPic.setImageBitmap(bitmap);

        btdel.setVisibility(View.INVISIBLE);//删除按钮隐藏

        //判断是否删除成功
//                    iv_show_picture.setDrawingCacheEnabled(true);
//                    Bitmap obmp = Bitmap.createBitmap(iv_show_picture.getDrawingCache());  //获取到Bitmap的图片
//                    if(obmp != null){
//                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();
//                    }
//                    iv_show_picture.setDrawingCacheEnabled(false);
        if(cardPic.getDrawable() == null){
            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
        }

    }

    void clearAll(){
        sourceInput.setText("");
        authorInput.setText("");
        headingInput.setText("");
        contentInput.setText("");
        cardPic.setImageBitmap(null);
        playButton.setVisibility(View.INVISIBLE);
        String picPath = Environment.getExternalStorageDirectory().getPath() + "/cardPic.jpg";
        File picFile = new File(picPath);
        if (picFile.exists()){
            picFile.delete();
        }
        isAudio = false;
        File audioFile = new File(mFileName);
        if (audioFile.exists()){
            audioFile.delete();
        }
        typeButton.setText("标签");
        Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
        starButton.setImageDrawable(drawable);
    }
}
