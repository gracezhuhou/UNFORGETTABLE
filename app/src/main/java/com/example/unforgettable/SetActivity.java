package com.example.unforgettable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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

import java.util.Calendar;
import java.util.Locale;
import cn.bmob.v3.BmobUser;

public class SetActivity extends Fragment {

    private Button logoutButton;
    private Button snycButton;
    private ImageButton userPic;
    private TextView userName;

    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    Bmobhelper bmobhelper = new Bmobhelper();

    private Button cancel;
    private Button no;
    private Button accept;
    private TextView set_time;
    private RelativeLayout relativeLayout;
    private TimePickerDialog timePickerDialog;
    private TimePicker timePicker;
    private AlertDialog dialog;
    final Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private String mHour ;
    private String mMinute;
    private String time = "不提醒";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_set, container, false);

        logoutButton = view.findViewById(R.id.logout);
        snycButton = view.findViewById(R.id.snycButton);
        userPic = view.findViewById(R.id.userPic);
        userName = view.findViewById(R.id.userName);

        showUser(); // 显示当前用户

        //初始时隐藏，当switch状态为开时View显示
        relativeLayout = view.findViewById(R.id.relativelayout);

        set_time = view.findViewById(R.id.time);
        set_time.setText(time);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 登出按钮监听
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 登出
                loginRepository.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        // 数据同步按钮
        snycButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择框 上传or下载
                final String[] strArray = new String[]{"上传本机数据","同步云端数据"};  //初始化字符串数组
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());   //实例化builder
//                builder.setIcon(R.mipmap.ic_launcher);    //设置图标
//                builder.setTitle("简单列表");   //设置标题
                //设置列表
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
                builder.create().show();    //创建并显示对话框
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v){
                //Toast.makeText(SetActivity.this.getActivity(),"布局被点击",Toast.LENGTH_SHORT).show();
//                timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        Log.d("测试：小时", Integer.toString(hourOfDay));
//                        Log.d("测试：分钟", Integer.toString(minute));
//                        mHour = Integer.toString(hourOfDay);
//                        mMinute = Integer.toString(minute);
//                        time = mHour + ":" + mMinute;
//                        set_time.setText(time);
//                    }
//                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
//                timePickerDialog.show();
//                timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                dialog = new AlertDialog.Builder(getActivity()).create();//实例化一个AlertDialog
                dialog.show();    //把AlertDialog初始化
                Window window = dialog.getWindow(); //实例化一个窗口
                window.setContentView(R.layout.activity_dialog);//调用自定义的XML放到AlertDialog中展示
                Display display = window.getWindowManager().getDefaultDisplay();
                WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
                // 设置高度和宽度
//                p.height = (int) (display.getHeight() * 0.65); // 高度设置为屏幕的0.6
                p.width = (int) (display.getWidth() * 0.85); // 宽度设置为屏幕的0.65
                p.gravity = Gravity.CENTER;//设置位置
                window.setAttributes(p);

                cancel = window.findViewById(R.id.cancel);
                no = window.findViewById(R.id.no);
                accept = window.findViewById(R.id.accept);
                timePicker = window.findViewById(R.id.timepicker);//获取自定义XML的控件
                timePicker.setIs24HourView(true);

                accept.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        mHour = Integer.toString(timePicker.getHour());
                        mMinute = Integer.toString(timePicker.getMinute());
                        if(timePicker.getMinute() < 10) {
                            time = mHour + ":" +  "0" + mMinute;
                        } else {
                            time = mHour + ":" + mMinute;
                        }
                        set_time.setText(time);

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
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
    }

    // 显示当前用户
    private void showUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        userName.setText(myUser.getNickname());
    }
}
