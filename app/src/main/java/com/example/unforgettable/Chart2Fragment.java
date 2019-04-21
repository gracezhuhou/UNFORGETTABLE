package com.example.unforgettable;

import android.content.Context;
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
    private BarChartView chartView1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);

        // TODO: @陈独秀
//        chartView1 =  view.findViewById(R.id.bar_view1);
//        chartView1.setData(1000,800,5000,"1000");

        spinner = view.findViewById(R.id.spinner);
        //chartView1 = (BarChartView)view.findViewById(R.id.bar_view1);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @陈独秀  这里写监听事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] labels = getResources().getStringArray(R.array.reviewtType);
                label = labels[pos];
                Log.v("label","选择的标签是" + labels[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

}
