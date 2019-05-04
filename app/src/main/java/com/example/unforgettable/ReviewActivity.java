package com.example.unforgettable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.example.unforgettable.LitepalTable.tabList;

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
    private RelativeLayout remindButton;
    private ImageView cardPic;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private List<memoryCardsList> reciteCardList;    //背诵卡片列表
    private boolean like;   // 收藏
    String[] tab;   // 下拉菜单中的标签数组

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_review, container, false);

        LitePal.initialize(this.getActivity());   // 初始化

        // 设置id
        spinner = view.findViewById(R.id.spinner);
        fileButton = view.findViewById(R.id.fileButton);
        editButton = view.findViewById(R.id.editButton);
        starButton = view.findViewById(R.id.starButton);
        typeText = view.findViewById(R.id.typeText);
        headingText = view.findViewById(R.id.headingText);
        detailText = view.findViewById(R.id.detailText);
        contentText = view.findViewById(R.id.contentText);
        passDayText = view.findViewById(R.id.passDayText);
        dimDayText = view.findViewById(R.id.dimDayText);
        forgetDayText = view.findViewById(R.id.forgetDayText);
        passButton = view.findViewById(R.id.passButton);
        dimButton = view.findViewById(R.id.dimButton);
        forgetButton = view.findViewById(R.id.forgetButton);
        remindButton = view.findViewById(R.id.remindButton);
        cardPic = view.findViewById(R.id.cardPic);

        //LitePal.deleteDatabase("MemoryCards");
        //dbhelper.addTab("英语");
        //dbhelper.addTab("高数");

        dbhelper.addStageList();
        dbhelper.deleteOldDayCards();   // 删去todayCardsList中之前的卡片
        setSpinner();   // 设置标签
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
            dbhelper = new Dbhelper();
            init();
            //now visible to user
            Log.v("复习界面", "刷新页面");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        dbhelper = new Dbhelper();
        init();
    }

    // 按键监听
    // 控件的点击事件写在onActivityCreated中
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 收藏按钮监听
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                like = dbhelper.changeLike((String)headingText.getText());

                // 改按键颜色状态
                if (like) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
                    // 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    starButton.setCompoundDrawables(null, null, drawable, null);
                    starButton.setTextColor(Color.argb(0, 0, 255, 0));
                }
                else {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
                    // 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    starButton.setCompoundDrawables(null, null, drawable, null);
                    starButton.setTextColor(Color.argb(0, 0, 255, 0));
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
                dbhelper.setReciteStatus((String)headingText.getText(), 1); // 更新todatCardsList
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
                dbhelper.setReciteStatus((String)headingText.getText(), 0);
                dbhelper.updateReciteDate((String)headingText.getText(), 0);
                memoryCardsList forgetCard = reciteCardList.get(0);
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
                dbhelper.setReciteStatus((String)headingText.getText(), -1);
                dbhelper.updateReciteDate((String)headingText.getText(), -1);
                memoryCardsList forgetCard = reciteCardList.get(0);
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
        // 下拉菜单点击
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                reciteCardList = dbhelper.getReciteTabCards(tab[pos]);
                //Toast.makeText(getActivity(), "你点击的是:"+tab[pos], Toast.LENGTH_LONG).show();
                showHeading();
                Log.v("复习界面","下拉菜单选择");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        Log.v("复习界面","按钮监听完成");
    }

    // 设置标签下拉菜单
    private void setSpinner() {
        // 获取所有标签
        List<tabList> tapList = dbhelper.getTabList();
        int size = tapList.size();
        tab = new String[size + 1];
        tab[0] = "全部";
        for (int i = 0; i < size; ++i){
            tab[i + 1] = tapList.get(i).getTabName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item , tab);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        spinner.setAdapter(adapter);
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
            typeText.setVisibility(View.INVISIBLE);
        }
        else {
            headingText.setText(reciteCardList.get(0).getHeading());    // 当前卡片标题
            typeText.setText(reciteCardList.get(0).getTab());   // 当前卡片标签
            remindButton.setVisibility(View.VISIBLE);   // 显示
            like = reciteCardList.get(0).isLike();
            // 设置初始星星颜色
            // 改按键颜色状态
            if (like) {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                starButton.setCompoundDrawables(null, null, drawable, null);
                starButton.setTextColor(Color.argb(0, 0, 255, 0));
            }
            else {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                starButton.setCompoundDrawables(null, null, drawable, null);
                starButton.setTextColor(Color.argb(0, 0, 255, 0));
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

        memoryCardsList recentCard = dbhelper.findCard((String)headingText.getText());
        int stage = recentCard.getStage();
        contentText.setText(recentCard.getContent());
        String cardDetail = "记录于"+ recentCard.getRecordDate() + " 第" + recentCard.getRepeatTime()+ "次重复";
        detailText.setText(cardDetail);
        String addDay[] = new String[]{"+1天", "+2天", "+4天", "+7天", "+15天", "+1个月", "+3个月", "+6个月", "+1年"};
        passDayText.setText(addDay[stage]);

        // 显示图片
        byte[] images = recentCard.getPicture();
        if (images != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
            cardPic.setImageBitmap(bitmap);
        }

        Log.v("复习界面","卡片背面显示");
    }

}
