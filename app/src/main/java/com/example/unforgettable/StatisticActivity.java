package com.example.unforgettable;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class StatisticActivity extends Fragment {
    private TabLayout tabLayout;                            //定义TabLayout
    private ViewPager vp_pager;                             //定义viewPager
    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;                                     //tab名称列表

    //定义fragment
    private Chart1Fragment f1;
    private Chart2Fragment f2;
//    private Chart3Fragment f3;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_statistic, container, false);

        // TODO: @陈独秀
        //初始化控件
        initControls(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @陈独秀  这里写监听事件
    }
    /**
     * 初始化各控件
     * @param view
     */
    private void initControls(View view) {

        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        vp_pager = (ViewPager)view.findViewById(R.id.viewPager);

        //初始化各fragment
//        hotRecommendFragment = new Find_hotRecommendFragment();
//        hotCollectionFragment = new Find_hotCollectionFragment();
//        hotMonthFragment = new Find_hotMonthFragment();
//        hotToday = new Find_hotToday();

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        f1 = new Chart1Fragment();
        f2 = new Chart2Fragment();
//        f3 = new Chart3Fragment();
        list_fragment.add(f1);
        list_fragment.add(f2);
//        list_fragment.add(f3);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("遗忘曲线");
        list_title.add("学习情况");
        list_title.add("记忆持久度");

        //设置TabLayout的模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(2)));

        fAdapter = new TabLayoutAdapter(getActivity().getSupportFragmentManager(),list_fragment,list_title);

        //viewpager加载adapter
        vp_pager.setAdapter(fAdapter);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tabLayout.setupWithViewPager(vp_pager);
        //tab_FindFragment_title.set
    }

}
