package com.example.unforgettable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private Button editButton;
    private Button starButton;
    private TextView typeText;
    private TextView headingText;
    private TextView detailText;
    private TextView contentText;
    private TextView passDayText;
    private TextView forgetDayText;
    private Button passButton;
    private Button forgetButton;
    private Button remindButton;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private List<MemoryCardsList> reciteCardList;    //背诵卡片列表
    //private int cardIndex = 0;  // 背诵卡片index
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

        buttonListener();
    }



    // 初始化背诵列表 & 初始界面
    private void init() {
        reciteCardList = dbhelper.getReciteCards();
        showHeading();
        Log.v("复习界面","初始化界面完成");
    }

    // 显示卡片正面
    private void showHeading() {
        if (reciteCardList.size() == 0) {
            headingText.setText("无背诵卡片");
            remindButton.setVisibility(View.INVISIBLE); // 隐藏
        }
        else {
            headingText.setText(reciteCardList.get(0).getHeading());    // 当前卡片标题
            remindButton.setVisibility(View.VISIBLE);   // 显示
            // TODO: 改按键颜色状态    @前端
            like = reciteCardList.get(0).isLike();
            if (like) {
                starButton.setText("已收藏"); //暂时
            }
            else {
                starButton.setText("❤"); //暂时
            }
        }
        // 隐藏
        fileButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        starButton.setVisibility(View.INVISIBLE);
        contentText.setVisibility(View.INVISIBLE);
        detailText.setVisibility(View.INVISIBLE);
        passDayText.setVisibility(View.INVISIBLE);
        forgetDayText.setVisibility(View.INVISIBLE);
        passButton.setVisibility(View.INVISIBLE);
        forgetButton.setVisibility(View.INVISIBLE);
        Log.v("复习界面","卡片正面显示");
    }

    // 显示卡片背面
    private void showContent() {
        // 显示内容部分
        fileButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        starButton.setVisibility(View.VISIBLE);
        contentText.setVisibility(View.VISIBLE);
        detailText.setVisibility(View.VISIBLE);
        passDayText.setVisibility(View.VISIBLE);
        forgetDayText.setVisibility(View.VISIBLE);
        passButton.setVisibility(View.VISIBLE);
        forgetButton.setVisibility(View.VISIBLE);
        // 隐藏
        remindButton.setVisibility(View.INVISIBLE);

        MemoryCardsList recentCard = dbhelper.findCard((String)headingText.getText());
        int stage = recentCard.getStage();
        contentText.setText(recentCard.getContent());
        String cardDetail = "记录于"+ recentCard.getRecordDate() + " 第" + stage + "此重复";
        detailText.setText(cardDetail);
        String addDay[] = new String[]{"+1天", "+2天", "+4天", "+7天", "+15天", "+1个月", "+3个月", "+6个月", "+1年"};
        passDayText.setText(addDay[stage]);
        Log.v("复习界面","卡片背面显示");
    }


    // 按键监听
    private void buttonListener(){
        // 收藏按钮监听
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                like = dbhelper.changeLike((String)headingText.getText());

                // TODO: 改按键颜色状态    @前端
                if (like) {
                    starButton.setText("已收藏"); //暂时
                }
                else {
                    starButton.setText("❤"); //暂时
                }
                Log.v("复习界面","收藏按钮点击事件" + like);
            }
        });
        // 显示答案
        remindButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showContent();
            }
        });
        // 记住按钮监听
        passButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dbhelper.updateReciteDate((String)headingText.getText(), true);
                reciteCardList.remove(0);
                showHeading();
                Log.v("复习界面","记住按钮点击事件");
            }
        });
        // 忘记按钮监听
        forgetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dbhelper.updateReciteDate((String)headingText.getText(), false);
                MemoryCardsList forgetCard = reciteCardList.get(0);
                reciteCardList.remove(0);
                reciteCardList.add(forgetCard);
                showHeading();
                Log.v("复习界面","忘记按钮点击事件");
            }
        });
        // 归档按钮监听
        fileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dbhelper.finishCard((String)headingText.getText());
                reciteCardList.remove(0);
                showHeading();
                Log.v("复习界面","归档按钮点击事件");
            }
        });

        Log.v("复习界面","按钮监听完成");
    }

    //设置Id
    private void setID(){
        spinner = (Spinner) findViewById(R.id.spinner);
        fileButton = (Button)findViewById(R.id.fileButton);
        editButton = (Button)findViewById(R.id.editButton);
        starButton = (Button)findViewById(R.id.starButton);
        typeText = (TextView) findViewById(R.id.typeText);
        headingText = (TextView) findViewById(R.id.headingText);
        detailText = (TextView) findViewById(R.id.detailText);
        contentText = (TextView) findViewById(R.id.contentText);
        passDayText = (TextView)findViewById(R.id.passDayText);
        forgetDayText = (TextView)findViewById(R.id.forgetDayText);
        passButton = (Button)findViewById(R.id.passButton);
        forgetButton = (Button)findViewById(R.id.forgetButton);
        remindButton = (Button) findViewById(R.id.remindButton);
    }
}
