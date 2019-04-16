package com.example.unforgettable;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.LitePal;

public class RecordActivity extends AppCompatActivity {
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
    private boolean like;   // 收藏
    private String tab;     // 标签

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_review:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_list:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_record:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_statisitc:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_personal:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);   // 初始化数据库
        setContentView(R.layout.activity_record);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        setID();    //设置id
        submitListener();   //按下确认键
    }

    //设置Id
    private void setID(){
        submitButton = (Button) findViewById(R.id.submitButton);
        sourceInput = (EditText)findViewById(R.id.sourceInput);
        authorInput = (EditText)findViewById(R.id.authorInput);
        headingInput = (EditText)findViewById(R.id.headingInput);
        typeButton = (Button) findViewById(R.id.typeButton);
        cameraButton = (Button) findViewById(R.id.cameraButton);
        soundButton = (Button) findViewById(R.id.soundButton);
        starButton = (Button) findViewById(R.id.starButton);
        contentInput = (EditText)findViewById(R.id.contentInput);
    }

    //确认键监听
    private void submitListener(){
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getInput(); //获取用户输入内容
                //暂时：
                like = false;
                tab = "计网";
                dbhelper.addCard(source, author, heading, content, like, tab);  //添加记录
                //清空页面
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
