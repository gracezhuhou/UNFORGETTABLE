package com.example.unforgettable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView set_time;
    private Switch aSwitch;
    private RelativeLayout relativeLayout;
    private RelativeLayout remind_time;
    private TimePickerDialog timePickerDialog;
    final Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private String mHour ;
    private String mMinute;
    private  String time = "20:00";

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
        relativeLayout = view.findViewById(R.id.remind_time);
        relativeLayout.setVisibility(View.GONE);

        set_time = view.findViewById(R.id.time);
        set_time.setText(time);
        aSwitch = view.findViewById(R.id.if_remind);

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
            @Override
            public void onClick(View v){
                //Toast.makeText(SetActivity.this.getActivity(),"布局被点击",Toast.LENGTH_SHORT).show();
                timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("测试：小时", Integer.toString(hourOfDay));
                        Log.d("测试：分钟", Integer.toString(minute));
                        mHour = Integer.toString(hourOfDay);
                        mMinute = Integer.toString(minute);
                        time = mHour + ":" + mMinute;
                        set_time.setText(time);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
                timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked) {
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    // 显示当前用户
    private void showUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        userName.setText(myUser.getNickname());
    }
}
