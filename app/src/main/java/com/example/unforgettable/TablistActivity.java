package com.example.unforgettable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.unforgettable.LitepalTable.tabList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class TablistActivity extends AppCompatActivity {
    // 前端相关变量
    private ListView listView;
    private Button tab_add;
    private Button backButton;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();

    private ArrayAdapter<String> adapter;
    ArrayList<String> tab;   // 标签数组
    private SharedPreferences pref;
    protected int position_int;
    int index = 0;// 长按删除指定数据的索引

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
        setContentView(R.layout.activity_tablist);
        LitePal.initialize(this);   // 初始化

        listView = findViewById(R.id.listView);
        tab_add = findViewById(R.id.tab_add);
        backButton = findViewById(R.id.backButton);

        // 获取所有标签
        List<tabList> tabList = dbhelper.getTabList();
        int size = tabList.size();
        tab = new ArrayList<String>();
        for (int i = 0; i < size; ++i){
            tab.add(tabList.get(i).getTabName());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , tab);  //创建一个数组适配器
        listView.setAdapter(adapter);


        setListener();

        // 设置ListView单项选择监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3)
            {
                String str = tab.get(position);
                //updateText(str);
                // 将点击的位置参数传递给全局变量
                //position_int = position;
                Intent intent = new Intent();
                intent.putExtra("tabname",str);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                if (tab.get(arg2).equals("无标签")) {
                    return false;
                }
                new AlertDialog.Builder(TablistActivity.this)
                        .setTitle("删除标签")
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        index = arg2;
                                        String deleteText = tab.get(index);
                                        dbhelper.deltab(deleteText);

                                        // 标签列表中动态删除标签
                                        adapter.remove(deleteText);
                                        adapter.notifyDataSetChanged();
                                    }
                                })

                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                return true;

            }

        });
    }

    void setListener(){
        //添加标签按钮
        tab_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog_show();
            }
        });
        // 返回按钮响应
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    //添加标签dialog
    protected void dialog_show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.activity_tab_add, null);

        //builder.setTitle("添加标签");
        builder.setView(textEntryView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText tabText =  textEntryView.findViewById(R.id.ettab);
                //showDialog("标签 ："  + tabText.getText().toString() );
                dbhelper.addTab(tabText.getText().toString());

                // 标签列表中动态添加新标签
                adapter.add(tabText.getText().toString());
                adapter.notifyDataSetChanged();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showDialog(String str) {
        new AlertDialog.Builder(this)
                .setMessage(str)
                .show();
    }

}
