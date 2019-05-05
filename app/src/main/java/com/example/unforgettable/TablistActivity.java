package com.example.unforgettable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.unforgettable.LitepalTable.tabList;

import org.litepal.LitePal;

import java.util.List;

public class TablistActivity extends Fragment {
    // 前端相关变量
    private ListView listView;
    private Button tab_add;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();

    String[] tab;   // 标签数组

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_cardlist, container, false);
        LitePal.initialize(this.getActivity());   // 初始化

        listView = view.findViewById(R.id.listView);
        tab_add = view.findViewById(R.id.tab_add);

        // 获取所有标签
        List<tabList> tapList = dbhelper.getTabList();
        int size = tapList.size();
        tab = new String[size + 1];
        tab[0] = "全部";
        for (int i = 0; i < size; ++i){
            tab[i + 1] = tapList.get(i).getTabName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item , tab);  //创建一个数组适配器
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.activity_tab_add, null);

        builder.setTitle("添加标签");
        builder.setView(textEntryView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText tab =  textEntryView.findViewById(R.id.ettab);
                showDialog("标签 ："  + tab.getText().toString() );
                dbhelper.addTab(tab.getText().toString());
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
        new AlertDialog.Builder(getActivity())
                .setMessage(str)
                .show();
    }
}
