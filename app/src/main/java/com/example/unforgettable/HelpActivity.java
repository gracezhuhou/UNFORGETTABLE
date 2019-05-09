package com.example.unforgettable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.unforgettable.Adapter.ExPandableListViewAdapter;
import com.example.unforgettable.helpData.ChildrenData;
import com.example.unforgettable.helpData.FatherData;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {
    private Button backButton;

    private ExpandableListView myExpandableListView;
    private ExPandableListViewAdapter adapter;
    private ArrayList<FatherData> datas;

    private int lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), datas.get(arg2).getTitle(), Toast.LENGTH_LONG).show();
                int count =  adapter.getGroupCount();
                for (int i = 0; i < 12; ++i) {
                    if (myExpandableListView.isGroupExpanded(i) && i != arg2) {
                        myExpandableListView.collapseGroup(i);
                    }
                }
                myExpandableListView.expandGroup(arg2);
                return true;
            }


        });

        // 设置二级item点击的监听器，同时在Adapter中设置isChildSelectable返回值true，同时二级列表布局中控件不能设置点击效果
        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4) {
                // TODO Auto-generated method stub
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
//        // 一级列表中的数据
//        FatherData fatherData1 = new FatherData();
//        fatherData1.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData2 = new FatherData();
//        fatherData2.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData3 = new FatherData();
//        fatherData3.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData4 = new FatherData();
//        fatherData4.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData5 = new FatherData();
//        fatherData5.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData6 = new FatherData();
//        fatherData6.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData7 = new FatherData();
//        fatherData7.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData8 = new FatherData();
//        fatherData8.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData9 = new FatherData();
//        fatherData9.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData10 = new FatherData();
//        fatherData10.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData11 = new FatherData();
//        fatherData11.setTitle("用户名和邮箱有什么不同？" );
//        FatherData fatherData12 = new FatherData();
//        fatherData12.setTitle("用户名和邮箱有什么不同？" );
//
//        // 二级列表中的数据
//        ArrayList<ChildrenData> itemList = new ArrayList<>();
//        ChildrenData childrenData1 = new ChildrenData();
//        childrenData1.setContent("内容");
//        ChildrenData childrenData2 = new ChildrenData();
//        childrenData2.setContent("内容");
//        ChildrenData childrenData3 = new ChildrenData();
//        childrenData3.setContent("内容");
//        ChildrenData childrenData4 = new ChildrenData();
//        childrenData4.setContent("内容");
//        ChildrenData childrenData5 = new ChildrenData();
//        childrenData5.setContent("内容");
//        ChildrenData childrenData6 = new ChildrenData();
//        childrenData6.setContent("内容");
//        ChildrenData childrenData7 = new ChildrenData();
//        childrenData7.setContent("内容");
//        ChildrenData childrenData8 = new ChildrenData();
//        childrenData8.setContent("内容");
//        ChildrenData childrenData9 = new ChildrenData();
//        childrenData9.setContent("内容");
//        ChildrenData childrenData10 = new ChildrenData();
//        childrenData10.setContent("内容");
//        ChildrenData childrenData11 = new ChildrenData();
//        childrenData11.setContent("内容");
//        ChildrenData childrenData12 = new ChildrenData();
//        childrenData12.setContent("内容");



        // 一级列表中的数据
        for (int i = 0; i < 12; i++) {
            FatherData fatherData = new FatherData();
            fatherData.setTitle("标题" + i);
            // 二级列表中的数据
            ArrayList<ChildrenData> itemList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                ChildrenData childrenData = new ChildrenData();
                childrenData.setContent("内容" + j);
                Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
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
