package com.example.unforgettable;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


public class Chart2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String label;
    private Spinner spinner;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();
    private int [][] memory = new int [5][60];
    private List<TabList> tabList = dBhelper.getTabList();    //背诵卡片列表
    private BarChartView chartView1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);
        //View rootView = View.inflate(getActivity(), R.layout.fragment_chart2, null);
        //chartView1 =  rootView.findViewById(R.id.bar_view1);

        // TODO: @陈独秀

//        chartView1.setData(1000,800,5000,"1000");
        chartView1 = view.findViewById(R.id.bar_view1);
        chartView1.setData(1000,800,5000,"1000");

        spinner = view.findViewById(R.id.spinner);
//        View rootView = View.inflate(getActivity(), R.layout.fragment_chart2, null);
        //setContentView(rootView);

        //charView = (BarChartView)rootView.findViewById(R.id.bar_view1);
//        BarChartView barChart = view.findViewById(R.id.bar_view1);
////        Canvas canvas = new Canvas();
////        barChart.draw(canvas);
        //chartView1 = (BarChartView)view.findViewById(R.id.bar_view1);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @陈独秀  这里写监听事件
        final String []tab = new String[tabList.size()+1];

        tab[0] = "全部";

        for (int i=0;i<tabList.size();i++){
            tab[i+1] = tabList.get(i).getTabName();
        }

        final ArrayAdapter<String> adpter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item ,tab);
        adpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adpter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                label = tab[pos];
                //Toast.makeText(getActivity(), "你点击的是:"+tab[pos], Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

}
