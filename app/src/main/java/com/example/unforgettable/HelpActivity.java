package com.example.unforgettable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.unforgettable.Adapter.ExPandableListViewAdapter;
import com.example.unforgettable.helpData.AnimatedExpandableListView;
import com.example.unforgettable.helpData.ChildrenData;
import com.example.unforgettable.helpData.FatherData;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {
    private Button backButton;

    private ExpandableListView myExpandableListView;
    private ExPandableListViewAdapter adapter;
    private ArrayList<FatherData> datas;

    SharedPreferences pref;
    private int lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("Alert", MODE_PRIVATE);
        int mode = pref.getInt("background", -1);
        if (mode == -1) {
            setTheme(R.style.AppTheme_Base_Base);
        }
        else {
            setTheme(mode);
        }
        setContentView(R.layout.activity_help);

        backButton = findViewById(R.id.backButton);

        initView();
        setData();
        setAdapter();
        setListener();
    }

    // 初始化控件
    private void initView() {
        myExpandableListView = findViewById(R.id.expandablelist);

        // 设置ExpandableListView的监听事件
        // 设置一级item点击的监听器
        myExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
                // Auto-generated method stub
                //Toast.makeText(getApplicationContext(), datas.get(arg2).getTitle(), Toast.LENGTH_LONG).show();
                if (myExpandableListView.isGroupExpanded(arg2)) {
                    myExpandableListView.collapseGroup(arg2);
                }
                else {
                    int count = adapter.getGroupCount();
                    for (int i = 0; i < count; ++i) {
                        if (myExpandableListView.isGroupExpanded(i) && i != arg2) {
                            myExpandableListView.collapseGroup(i);
                        }
                    }
                    myExpandableListView.expandGroup(arg2, true);

                }
                return true;
            }


        });

        // 设置二级item点击的监听器，同时在Adapter中设置isChildSelectable返回值true，同时二级列表布局中控件不能设置点击效果
        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4) {
                // Auto-generated method stub
                //Toast.makeText(getApplicationContext(), datas.get(arg2).getList().get(arg3).getContent(), Toast.LENGTH_LONG).show();
                return false;
            }

        });
    }

    /**
     * 自定义setAdapter
     */
    private void setAdapter() {
        if (adapter == null) {
            adapter = new ExPandableListViewAdapter(this, datas);
            myExpandableListView.setAdapter(adapter);
        }
//        else {
//            adapter.flashData(datas);
//        }
    }

    // 定义数据
    private void setData() {
        if (datas == null) {
            datas = new ArrayList<>();
        }

        String[] father = new String[] {
                "用户名和邮箱有什么不同？",
                "如何设置复习时间？",
                "复习功能具体是怎样的？",
                "如何修改个人信息？",
                "如何修改头像？",
                "如何进行数据同步？",
                "统计页面的功能详情?",
                "如何使用录音功能来输入记忆卡片内容？",
                "如何使用拍照功能来输入记忆卡片内容？",
                "列表有什么作用？",
                "标签功能的详情?",
                "星星标志和文档标志有什么含义？",
        };// 12个标题

        String[][] child = new String[][] {
                {"邮箱是您在系统中的唯一识别号。它用于独立登录（Email邮箱+Password密码）。\n" +
                        "【注：邮箱只支持在注册时设置一次，之后不可更改。】\n" +
                        "\n" +
                        "用户名是您在安堡中给自己起的别名，它允许多次更改。"},
                {"在【设置】->【提醒时间】，点击即可进入设置时间页。选择好您想每天提醒您的“复习时间”再点击“确定”即设置成功；如果您不需要每日提醒，点击“不提醒”即可。"},
                {"在【复习】界面点开记忆卡片“显示答案”之后，会出现三个选项——“√”，“模糊”，“忘记”。\n" +
                        "\n" +
                        "“√”对应“+X天”，表示您对该记忆卡片上的内容还有很好的记忆，不需要在今日重复提醒您学习了，可以隔天再提醒你复习它，重复频率低。\n" +
                        "\n" +
                        "“模糊”对应“今天”，表示您今日对该记忆卡片上的内容记得有点模糊、不牢靠，需要重复提醒您复习它，重复频率中。\n" +
                        "\n" +
                        "“忘记”对应“今天”，表示您今日对记忆卡片上的内容已经差不多完全忘记了，需要不断重复提醒您复习它，重复频率高。"},
                {"在【设置】界面点击头像旁边的用户昵称，即可进入修改个人信息页。\n" +
                        "\n" +
                        "在个人信息页，您可以自由修改您的用户名；您还可以修改您的登录密码，需要先正确输入“原密码”，再输“新密码”，确认之后即修改成功。\n" +
                        "\n" +
                        "【注：您的邮箱（即登录账号）是不可以修改的，注册之后就固定了。】"},
                {"在【设置】界面点击您的头像，可选择“拍照”或“图库”上传新的头像。"},
                {"在【设置】界面点击【数据同步】，会出现“上传本机数据”和“同步云端数据”两个选择。","“上传本机数据”——点击之后可以把您在该机上为您的账号增加的一些记录卡片数据上传的云端保存下来，供以后同步使用。","“同步云端数据”——点击之后可以把您之前上传到云端的记忆卡片数据同步到该机上，即使您更换过手机也可以同步以前手机上的数据（前提是登录的是用一个账号且以前账号上的数据有上传到云端）；如果您不小心删除了手机上的数据，只要上传过云端，就可以把数据同步回来。"},
                {"【统计】分成三个模块：“遗忘曲线”，“学习情况”，“记忆持久度”。\n" +
                        "\n" +
                        "【遗忘曲线】：一定天数之后的卡片遗忘率，一个是科学研究的艾宾浩斯遗忘曲线，一个是用户自己的遗忘曲线。","【学习情况】:以“今天” 为基准，往前30天每一天的记住卡片数量、模糊卡片数量、忘记卡片数量的显示；往后十天每一天的应背卡片数量。","【记忆持久度】:展示两个月内的记忆持久度情况，一共五条折线——已加入记忆规划的全部卡片；记忆持久度＞10天的卡片量；记忆持久度＞30天的卡片量；记忆持久度＞60天的卡片量；记忆持久度＞90天的卡片量。"},
                {"【记录】->【录音】->【识别录音】，说出您想要录入的记忆卡片内容，停止说话后识别器会将您说的话展示在“文本”处；之后您可以自由对已识别出来的文字进行修改、删除或增加等操作；若您还想再次语音识别，可再次点击【录音】->【识别语音】，它会将您第二次语音输入的文字展示在之前已经识别好的文字之后。","另外您还可以选择【保存录音】，它会将您所说的话直接以音频的形式保存下来，点击右下角的【播放】即可播放你所录入的音频。"},
                {"【记录】->【拍照】，出现“拍照”和“图库”两个选项，您可以任意选择一个进行操作。上传好您的图片之后，可以将它保存在记忆卡片中。"},
                {"在列表中，您可以看到您所有的记忆卡片，还能通过它们的标签分类查看。","点击每个记忆卡片，能进入另一个页面，可以对它们进行修改。","长按卡片即可以选择删除掉该记忆卡片。"},
                {"【记录】->【标签】，可进去标签页面，在此页面中，您可以看到所有的标签（如果之前添加过的话），点击标签即可为记忆卡片添加上对应标签。您可以任意添加新的标签，也可以删除旧标签。标签设置完保存之后，您可以在【列表】中通过标签分类查看您的所有记忆卡片。"},
                {"☆是添加到收藏的意思。在【记录】界面添加记忆卡片，以及在【列表】界面，您都可以看到它。选择它之后，该记忆卡片会多一个“收藏”的标签，在【列表】->【全部】下面可以看到“收藏”，打开“收藏”，您就可以看到您标记了☆的记忆卡片。\n" +
                        "\n" +
                        "✉是添加到归档的意思，与☆类似。"},
        };// 12组内容

        int[][] pic = new int[][] {
                {R.drawable.help1},//1
                {R.drawable.help2},//1
                {R.drawable.help3},//1
                {R.drawable.help4},//1
                {R.drawable.help5},//1
                {R.drawable.help6_1,R.drawable.help6_2,R.drawable.help6_3},//3
                {R.drawable.help7_1,R.drawable.help7_2,R.drawable.help7_3},//3
                {R.drawable.help8_1,R.drawable.help8_2},//2
                {R.drawable.help9},//1
                {R.drawable.help10_1,R.drawable.help10_2,R.drawable.help10_3},//3
                {R.drawable.help11},//1
                {R.drawable.help12},//1
        };


        // 一级列表中的数据
        for (int i = 0; i < father.length; i++) {
            FatherData fatherData = new FatherData();
            fatherData.setTitle(father[i]);
            // 二级列表中的数据
            ArrayList<ChildrenData> itemList = new ArrayList<>();
            for (int j = 0; j < child[i].length; j++) {
                ChildrenData childrenData = new ChildrenData();
                childrenData.setContent(child[i][j]);
                Drawable drawable = getResources().getDrawable(pic[i][j]);
                childrenData.setPic(drawable);
                itemList.add(childrenData);
            }
            fatherData.setList(itemList);
            datas.add(fatherData);
        }

    }


    void setListener(){
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
