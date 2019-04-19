package com.example.unforgettable;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private boolean like;   // 收藏
    private String[] tab;     // 标签

    private TextView mTextMessage;

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_review:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_list:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_record:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//                case R.id.navigation_statisitc:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//                case R.id.navigation_personal:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LitePal.initialize(this.getActivity());   // 初始化数据库
        //setContentView(R.layout.activity_record);
        View view = inflater.inflate(R.layout.activity_record, container, false);

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationViewHelper.disableShiftMode(navigation);
//
//        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch(item.getItemId())
//                {
//                    case R.id.navigation_list:
//                        Intent intent2 = new Intent(RecordActivity.this, MemoryCardsList.class);
//                        //intent2.putExtra("user_phone", myPhone);
//                        startActivity(intent2);
//                        break;
//                    case R.id.navigation_review:
//                        Intent intent3 = new Intent(RecordActivity.this, ReviewActivity.class);
//                        //intent3.putExtra("user_phone", myPhone);
//                        startActivity(intent3);
//                        break;
//                    case R.id.navigation_statisitc:
//                        Intent intent4 = new Intent(RecordActivity.this, StatisticActivity.class);
//                        //intent4.putExtra("user_phone", myPhone);
//                        startActivity(intent4);
//                        break;
//                    case R.id.navigation_personal:
//                        Intent intent5 = new Intent(RecordActivity.this, SetActivity.class);
//                        //intent5.putExtra("user_phone", myPhone);
//                        startActivity(intent5);
//                        break;
//                }
//                return true;
//            }
//        });
        //setID();    //设置id
        submitButton = (Button) view.findViewById(R.id.submitButton);
        sourceInput = (EditText)view.findViewById(R.id.sourceInput);
        authorInput = (EditText)view.findViewById(R.id.authorInput);
        headingInput = (EditText)view.findViewById(R.id.headingInput);
        typeButton = (Button) view.findViewById(R.id.typeButton);
        cameraButton = (Button) view.findViewById(R.id.cameraButton);
        soundButton = (Button) view.findViewById(R.id.soundButton);
        starButton = (Button) view.findViewById(R.id.starButton);
        contentInput = (EditText)view.findViewById(R.id.contentInput);
        //submitListener();   //按下确认键

        return view;
    }

    //设置Id
    private void setID(){
//        submitButton = (Button) getActivity().findViewById(R.id.submitButton);
//        sourceInput = (EditText)getActivity().findViewById(R.id.sourceInput);
//        authorInput = (EditText)getActivity().findViewById(R.id.authorInput);
//        headingInput = (EditText)getActivity().findViewById(R.id.headingInput);
//        typeButton = (Button) getActivity().findViewById(R.id.typeButton);
//        cameraButton = (Button) getActivity().findViewById(R.id.cameraButton);
//        soundButton = (Button) getActivity().findViewById(R.id.soundButton);
//        starButton = (Button) getActivity().findViewById(R.id.starButton);
//        contentInput = (EditText)getActivity().findViewById(R.id.contentInput);
    }

    //确认键监听
    //控件的点击事件写在onActivityCreated中
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput(); //获取用户输入内容

                // TODO: 是否收藏 & 选择标签（标签最多选择5个）
                like = false;   //暂时
                tab = new String[]{"计网", "英语"}; //暂时

                dbhelper.addCard(source, author, heading, content, like, tab);  //添加记录
                // TODO: 清空页面
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
