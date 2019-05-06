package com.example.unforgettable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.unforgettable.LitepalTable.tabList;

import org.litepal.LitePal;

import java.util.List;

public class TablistActivity extends AppCompatActivity {
    // 前端相关变量
    private ListView listView;
    private Button tab_add;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();

    private ArrayAdapter<String> adapter;
    String[] tab;   // 标签数组
    protected int position_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablist);
        LitePal.initialize(this);   // 初始化

        listView = findViewById(R.id.listView);
        tab_add = findViewById(R.id.tab_add);

        // 获取所有标签
        List<tabList> tapList = dbhelper.getTabList();
        int size = tapList.size();
        tab = new String[size];
        for (int i = 0; i < size; ++i){
            tab[i] = tapList.get(i).getTabName();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item , tab);  //创建一个数组适配器
        listView.setAdapter(adapter);


        setListener();

        // 设置ListView单项选择监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3)
            {
                String str = tab[position];
                //updateText(str);
                // 将点击的位置参数传递给全局变量
                //position_int = position;
                Intent intent = new Intent();
                intent.putExtra("tabname",str);
                setResult(RESULT_OK,intent);
                finish();
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
        
    }

    //添加标签框
    protected void dialog_show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.activity_tab_add, null);

        builder.setTitle("添加标签");
        builder.setView(textEntryView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText tabText =  textEntryView.findViewById(R.id.ettab);
                showDialog("标签 ："  + tabText.getText().toString() );
                dbhelper.addTab(tabText.getText().toString());

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
