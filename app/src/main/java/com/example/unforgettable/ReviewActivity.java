package com.example.unforgettable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;


public class ReviewActivity extends AppCompatActivity {
    // 前端相关变量
    private Spinner spinner;
    private Button fileButton;
    private Button EditButton;
    private Button StarButton;
    private TextView typeText;
    private TextView headingText;
    private TextView detailText;
    private TextView contentText;
    private TextView passDayText;
    private TextView forgetDayText;
    private Button passButton;
    private Button forgetButton;
    private TextView remindText;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private List<MemoryCardsList> reciteCardList;    //背诵卡片列表
    private MemoryCardsList recentCard;
    private int cardIndex = 0;  // 背诵卡片index
    private String heading; // 正面 标题
    private boolean like;   // 收藏

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
        setContentView(R.layout.activity_review);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LitePal.initialize(this);   // 初始化

        setID();    // 设置id
        init();  // 初始化背诵列表&初始界面
    }

    //设置Id
    private void setID(){
        spinner = (Spinner) findViewById(R.id.spinner);
        fileButton = (Button)findViewById(R.id.fileButton);
        EditButton = (Button)findViewById(R.id.EditButton);
        StarButton = (Button)findViewById(R.id.StarButton);
        typeText = (TextView) findViewById(R.id.typeText);
        headingText = (TextView) findViewById(R.id.headingText);
        detailText = (TextView) findViewById(R.id.detailText);
        contentText = (TextView) findViewById(R.id.contentText);
        passDayText = (TextView)findViewById(R.id.passDayText);
        forgetDayText = (TextView)findViewById(R.id.forgetDayText);
        passButton = (Button)findViewById(R.id.passButton);
        forgetButton = (Button)findViewById(R.id.forgetButton);
        remindText = (TextView) findViewById(R.id.remindText);
    }

    // 初始化背诵列表 & 初始界面
    private void init() {
        // TODO: 没有背诵卡片时显示无卡片

        reciteCardList = dbhelper.getReciteCards();
        if (reciteCardList.size() == 0) {
            headingText.setText("无背诵卡片");
            // 隐藏内容部分
            fileButton.setVisibility(View.INVISIBLE);
            EditButton.setVisibility(View.INVISIBLE);
            StarButton.setVisibility(View.INVISIBLE);
            contentText.setVisibility(View.INVISIBLE);
            detailText.setVisibility(View.INVISIBLE);
            passDayText.setVisibility(View.INVISIBLE);
            forgetDayText.setVisibility(View.INVISIBLE);
            passButton.setVisibility(View.INVISIBLE);
            forgetButton.setVisibility(View.INVISIBLE);
        }
        else {
            recentCard = reciteCardList.get(cardIndex);
            showHeading();
        }



    }

    // 显示卡片正面
    private void showHeading() {
        headingText.setText(recentCard.getHeading());
        // 隐藏内容部分
        fileButton.setVisibility(View.INVISIBLE);
        EditButton.setVisibility(View.INVISIBLE);
        StarButton.setVisibility(View.INVISIBLE);
        contentText.setVisibility(View.INVISIBLE);
        detailText.setVisibility(View.INVISIBLE);
        passDayText.setVisibility(View.INVISIBLE);
        forgetDayText.setVisibility(View.INVISIBLE);
        passButton.setVisibility(View.INVISIBLE);
        forgetButton.setVisibility(View.INVISIBLE);
    }

    // 显示卡片背面
    private void showContent() {
        // 显示内容部分
        fileButton.setVisibility(View.VISIBLE);
        EditButton.setVisibility(View.VISIBLE);
        StarButton.setVisibility(View.VISIBLE);
        contentText.setVisibility(View.VISIBLE);
        detailText.setVisibility(View.VISIBLE);
        passDayText.setVisibility(View.VISIBLE);
        forgetDayText.setVisibility(View.VISIBLE);
        passButton.setVisibility(View.VISIBLE);
        forgetButton.setVisibility(View.VISIBLE);

        contentText.setText(recentCard.getContent());
        String cardDetail = "记录于"+ recentCard.getRecordDate() + " 第" + recentCard.getStage() + "此重复";
        detailText.setText(cardDetail);
        // TODO: 设置passDayText&forgetDayText的文本
    }


    // 按键监听
    private void buttonListener(){
        // 收藏按钮监听
        StarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                heading = (String)headingText.getText();
                dbhelper.changeLike(heading);
                // TODO: 改按键颜色状态
            }
        });
        // 显示答案
        remindText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showContent();
            }
        });
    }
}
