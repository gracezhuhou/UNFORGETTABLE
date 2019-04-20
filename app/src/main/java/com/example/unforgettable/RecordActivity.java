package com.example.unforgettable;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.LitePal;

public class RecordActivity extends Fragment {
    // 前端相关变量
    private Button submitButton;
    private EditText sourceInput;
    private EditText authorInput;
    private EditText headingInput;
    private Button typeButton;
    private Button cameraButton;
    private Button soundButton;
    private Button starButton;
    private EditText contentInput;


    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private String source;  // 来源
    private String author;  // 作者
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like = false;   // 收藏
    private String[] tab;     // 标签

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_record, container, false);
        LitePal.initialize(this.getActivity());   // 初始化数据库

        //设置id
        submitButton = (Button) view.findViewById(R.id.submitButton);
        sourceInput = (EditText)view.findViewById(R.id.sourceInput);
        authorInput = (EditText)view.findViewById(R.id.authorInput);
        headingInput = (EditText)view.findViewById(R.id.headingInput);
        typeButton = (Button) view.findViewById(R.id.typeButton);
        cameraButton = (Button) view.findViewById(R.id.cameraButton);
        soundButton = (Button) view.findViewById(R.id.soundButton);
        starButton = (Button) view.findViewById(R.id.starButton);
        contentInput = (EditText)view.findViewById(R.id.contentInput);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //now invisible to user
            Log.v("记录界面", "页面隐藏");
        } else {
            //now visible to user
            Log.v("记录界面", "刷新页面");
        }
    }

    //确认键监听
    //控件的点击事件写在onActivityCreated中
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 提交按钮监听
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput(); //获取用户输入内容

                // TODO: 选择标签（标签最多选择5个）
                tab = new String[]{"计网", "英语"}; //暂时

                dbhelper.addCard(source, author, heading, content, like, tab);  //添加记录
                // TODO: 清空页面

            }
        });
        // 收藏按钮响应
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO: 改收藏按键颜色状态  @大冬瓜 @母后
                int starColor = starButton.getCurrentTextColor();
                if (starColor == R.color.bottom_navigation_selected) {
                    starButton.setTextColor(getResources().getColor(R.color.pink));
                    like = true;
                }
                else {
                    starButton.setTextColor(getResources().getColor(R.color.bottom_navigation_selected));
                    like = false;
                }
            }
        });
    }


    //获取用户输入内容
    private void getInput(){
        source = sourceInput.getText().toString();
        author = authorInput.getText().toString();
        heading = headingInput.getText().toString();
        content = contentInput.getText().toString();
    }

}
