package com.example.unforgettable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;


public class ReviewActivity extends Fragment{
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
    private TextView dimDayText;
    private TextView forgetDayText;
    private Button passButton;
    private Button dimButton;
    private Button forgetButton;
    private Button remindButton;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private List<MemoryCardsList> reciteCardList;    //背诵卡片列表
    private String heading; // 正面 标题
    private boolean like;   // 收藏

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_review, container, false);

        LitePal.initialize(this.getActivity());   // 初始化

        // 设置id
        spinner = (Spinner)view.findViewById(R.id.spinner);
        fileButton = (Button)view.findViewById(R.id.fileButton);
        editButton = (Button)view.findViewById(R.id.editButton);
        starButton = (Button)view.findViewById(R.id.starButton);
        typeText = (TextView)view.findViewById(R.id.typeText);
        headingText = (TextView)view.findViewById(R.id.headingText);
        detailText = (TextView)view.findViewById(R.id.detailText);
        contentText = (TextView)view.findViewById(R.id.contentText);
        passDayText = (TextView)view.findViewById(R.id.passDayText);
        dimDayText = (TextView)view.findViewById(R.id.dimDayText);
        forgetDayText = (TextView)view.findViewById(R.id.forgetDayText);
        passButton = (Button)view.findViewById(R.id.passButton);
        dimButton = (Button)view.findViewById(R.id.dimButton);
        forgetButton = (Button)view.findViewById(R.id.forgetButton);
        remindButton = (Button)view.findViewById(R.id.remindButton);

        init();  // 初始化背诵列表&初始界面

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //now invisible to user
            Log.v("复习界面", "页面隐藏");
        } else {
            init();
            //now visible to user
            Log.v("复习界面", "刷新页面");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }

    // 按键监听
    //控件的点击事件写在onActivityCreated中
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 收藏按钮监听
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                like = dbhelper.changeLike((String)headingText.getText());

                // TODO: 改按键颜色状态    @大冬瓜 @母后
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
                dbhelper.updateReciteDate((String)headingText.getText(), 1);
                reciteCardList.remove(0);
                showHeading();
                Log.v("复习界面","记住按钮点击事件");
            }
        });
        // 模糊按钮监听
        dimButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dbhelper.updateReciteDate((String)headingText.getText(), 0);
                MemoryCardsList forgetCard = reciteCardList.get(0);
                reciteCardList.remove(0);
                reciteCardList.add(forgetCard);
                showHeading();
                Log.v("复习界面","模糊按钮点击事件");
            }
        });
        // 忘记按钮监听
        forgetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dbhelper.updateReciteDate((String)headingText.getText(), -1);
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
        // 编辑按钮点击
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.v("复习界面","编辑按钮点击事件");
                String heading = (String)headingText.getText();
                Intent intent = new Intent(v.getContext(), EditCardActivity.class);
                intent.putExtra("heading_extra", heading);
                v.getContext().startActivity(intent);
            }
        });

        Log.v("复习界面","按钮监听完成");
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
            // TODO: 改按键颜色状态    @大冬瓜 @母后
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
        dimDayText.setVisibility(View.INVISIBLE);
        forgetDayText.setVisibility(View.INVISIBLE);
        passButton.setVisibility(View.INVISIBLE);
        dimButton.setVisibility(View.INVISIBLE);
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
        dimDayText.setVisibility(View.VISIBLE);
        forgetDayText.setVisibility(View.VISIBLE);
        passButton.setVisibility(View.VISIBLE);
        dimButton.setVisibility(View.VISIBLE);
        forgetButton.setVisibility(View.VISIBLE);
        // 隐藏
        remindButton.setVisibility(View.INVISIBLE);

        MemoryCardsList recentCard = dbhelper.findCard((String)headingText.getText());
        int stage = recentCard.getStage();
        contentText.setText(recentCard.getContent());
        String cardDetail = "记录于"+ recentCard.getRecordDate() + " 第" + stage + "次重复";
        detailText.setText(cardDetail);
        String addDay[] = new String[]{"+1天", "+2天", "+4天", "+7天", "+15天", "+1个月", "+3个月", "+6个月", "+1年"};
        passDayText.setText(addDay[stage]);
        Log.v("复习界面","卡片背面显示");
    }

}
