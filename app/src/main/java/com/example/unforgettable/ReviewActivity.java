package com.example.unforgettable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.LitePal;


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
        LitePal.initialize(this);   // 初始化
        setContentView(R.layout.activity_review);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setID();    //设置id
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
    }

}
