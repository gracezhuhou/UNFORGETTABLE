package com.example.unforgettable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.unforgettable.Adapter.CardsRecyclerAdapter;
import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.example.unforgettable.LitepalTable.tabList;

import org.litepal.LitePal;

import java.util.List;

public class CardlistActivity extends Fragment {
    // 前端相关变量
    private Spinner spinner;
    private Button card_edit;
    private Button tab_add;
    private CardsRecyclerAdapter recyclerAdapter;
    private RecyclerView cardsRecyclerView;
    private LinearLayoutManager layoutManager;
    private TextView headline;
    private TextView content_text;
    private TextView detail_text;
    private int bt_height;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like;   // 收藏

    private List<memoryCardsList> MemoryCardsList;
    String[] tab;   // 下拉菜单中的标签数组

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_cardlist, container, false);
        LitePal.initialize(this.getActivity());   // 初始化

        spinner = view.findViewById(R.id.spinner);
        setSpinner();

        MemoryCardsList = dbhelper.getCardList();   //列表
        //card_edit = view.findViewById(R.id.card_edit);
        cardsRecyclerView = view.findViewById(R.id.cardsRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        cardsRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new CardsRecyclerAdapter(MemoryCardsList);
        cardsRecyclerView.setAdapter(recyclerAdapter);
        cardsRecyclerView.setHasFixedSize(true);
        //tab_add = view.findViewById(R.id.tab_add);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //now invisible to user

        } else {
            //now visible to user
            MemoryCardsList = dbhelper.getCardList();   //列表
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            cardsRecyclerView.setLayoutManager(layoutManager);
            recyclerAdapter = new CardsRecyclerAdapter(MemoryCardsList);
            cardsRecyclerView.setAdapter(recyclerAdapter);
            //recyclerAdapter.notifyDataSetChanged();
            cardsRecyclerView.setHasFixedSize(true);
            setSpinner();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        MemoryCardsList = dbhelper.getCardList();   //列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        cardsRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new CardsRecyclerAdapter(MemoryCardsList);
        cardsRecyclerView.setAdapter(recyclerAdapter);
        cardsRecyclerView.setHasFixedSize(true);
        //recyclerAdapter.notifyDataSetChanged();
        setSpinner();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 下拉菜单点击
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                MemoryCardsList = dbhelper.getAllTabCards(tab[pos]);   //获得该标签下卡片
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                cardsRecyclerView.setLayoutManager(layoutManager);
                recyclerAdapter = new CardsRecyclerAdapter(MemoryCardsList);
                cardsRecyclerView.setAdapter(recyclerAdapter);
                //recyclerAdapter.notifyDataSetChanged();
                cardsRecyclerView.setHasFixedSize(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

//        //添加标签按钮
//        tab_add.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                dialog_show();
//            }
//        });
    }

    // 设置标签下拉菜单
    private void setSpinner() {
        // 获取所有标签
        List<tabList> tapList = dbhelper.getTabList();
        int size = tapList.size();
        tab = new String[size + 3];
        tab[0] = "全部";
        tab[1] = "收藏";
        tab[2] = "归档";
        for (int i = 0; i < size; ++i){
            tab[i + 3] = tapList.get(i).getTabName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item , tab);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        spinner.setAdapter(adapter);
    }

}