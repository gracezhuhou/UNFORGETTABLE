package com.example.unforgettable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.unforgettable.Bmob.Bmobhelper;
import com.example.unforgettable.Bmob.MyUser;
import com.example.unforgettable.data.LoginRepository;
import com.example.unforgettable.ui.login.LoginActivity;

import cn.bmob.v3.BmobUser;

public class SetActivity extends Fragment {

    private Button logoutButton;
    private Button snycButton;
    private ImageButton userPic;
    private TextView userName;

    private LoginRepository loginRepository;
    Bmobhelper bmobhelper = new Bmobhelper();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_set, container, false);

        logoutButton = view.findViewById(R.id.logout);
        snycButton = view.findViewById(R.id.snycButton);
        userPic = view.findViewById(R.id.userPic);
        userName = view.findViewById(R.id.userName);

        showUser(); // 显示当前用户

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
    }

    // 显示当前用户
    private void showUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        userName.setText(myUser.getNickname());
    }
}
