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
import com.example.unforgettable.helpData.AnimatedExpandableListView;
import com.example.unforgettable.helpData.ChildrenData;
import com.example.unforgettable.helpData.FatherData;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {
    private Button backButton;

    private AnimatedExpandableListView myExpandableListView;
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
                // Auto-generated method stub
                //Toast.makeText(getApplicationContext(), datas.get(arg2).getTitle(), Toast.LENGTH_LONG).show();
                if (myExpandableListView.isGroupExpanded(arg2)) {
                    myExpandableListView.collapseGroupWithAnimation(arg2);
                }
                else {
                    int count = adapter.getGroupCount();
                    for (int i = 0; i < count; ++i) {
                        if (myExpandableListView.isGroupExpanded(i) && i != arg2) {
                            myExpandableListView.collapseGroupWithAnimation(i);
                        }
                    }
                    myExpandableListView.expandGroupWithAnimation(arg2);
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
                "1",
                "1",
                "333",
                "33333",
                "333"};// 12个标题

        String[][] child = new String[][] {
                {"1","2"},
                {"1","2"},
                {"1","2"},
                {"1","2"},
                {"2"}};// 12组内容

        int[][] pic = new int[][] {
                {R.drawable.ic_star_yel, R.drawable.ic_star_yel, R.drawable.ic_star_yel},
                {R.drawable.ic_star_yel, R.drawable.ic_star_yel},
                {R.drawable.ic_star_yel, R.drawable.ic_star_yel},
                {R.drawable.ic_star_yel, R.drawable.ic_star_yel},
                {R.drawable.ic_star_yel}
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
