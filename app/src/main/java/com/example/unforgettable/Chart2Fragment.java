package com.example.unforgettable;

//学习情况表

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.example.unforgettable.LitepalTable.stageList;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Chart2Fragment extends Fragment implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String label = "全部";
    private Spinner spinner;
    private BarChart mBarChart;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();

    //每天记忆，模糊，忘记,需复习
    // 往前30天，往后10天+今天
    private int [][] memory = new int [4][41];
    private List<com.example.unforgettable.LitepalTable.tabList> tabList = dBhelper.getTabList();    //背诵卡片列表
    //private BarChartView chartView1;

    private String [] values = new String[41];


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);

        // TODO: @陈独秀

        //chartView1 = view.findViewById(R.id.bar_view1);
        spinner = view.findViewById(R.id.spinner);
        //堆叠条形图
        mBarChart = view.findViewById(R.id.mBarChart);

        // 图表
        initView();//初始化

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //now invisible to user
            Log.v("学习情况", "页面隐藏");
        } else {
            dBhelper = new Dbhelper();
            initView();
            //now visible to user
            Log.v("学习情况", "刷新页面");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        dBhelper = new Dbhelper();
        initView();
    }


    private void initView() {

        mBarChart.setOnChartValueSelectedListener(this);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setMaxVisibleValueCount(40);

        // 扩展现在只能分别在x轴和y轴
        mBarChart.setPinchZoom(false);
        //不显示图表网格
        mBarChart.setDrawGridBackground(false);
        //背景阴影
        mBarChart.setDrawBarShadow(false);
        mBarChart.setHighlightFullBarEnabled(false);

        mBarChart.setDrawValueAboveBar(false);

        //显示边框
        mBarChart.setDrawBorders(false);

        //设置动画效果
//        barChart.animateY(1000,);
//        barChart.animateX(1000,);

        /***XY轴的设置***/
        //X轴设置显示位置在底部
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//x轴位置
        mBarChart.getXAxis().setDrawLabels(true);
        mBarChart.getAxisRight().setEnabled(false);


        //自定义X轴
        for(int i = 0;i < 28; i++){
            int temp = 30-i;
            values[i] = temp + "天前";
        }
        values[28] = "前天";
        values[29] = "昨天";
        values[30] = "今天";
        values[31] = "明天";
        values[32] = "后天";
        for(int i = 33; i<41;i++){
            int temp = i-30;
            values[i] = temp+"天后";
        }

        MyAxisValueFormatter formatter = new MyAxisValueFormatter(values);
        //mBarChart.getXAxis().setLabelCount(20);
        mBarChart.getXAxis().setValueFormatter(formatter);

//        xAxis.setAxisMinimum(0f);
//        xAxis.setGranularity(1f);

        // 改变y标签的位置
        YAxis leftAxis = mBarChart.getAxisLeft();
//        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f);



        Legend l = mBarChart.getLegend();
//        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextSize(11f);
        //显示位置
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        //是否绘制在图表里面
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        setData();
    }

    //初始化
    private void setData() {

        //设置柱状图数据
        //memory数组初始化
        for(int i =0;i<4;i++){
            for(int j=0;j<41;j++){
                memory[i][j] = 0;
            }
        }

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        Calendar date = Calendar.getInstance();
        date.setTime(today);


        if(label.equals("全部")){
            for(int i=0; i<30;i++){
//          List<stageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(stageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(30-i));//30-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[0][i] = memory[0][i]+statistic.getRemember();
                    memory[1][i] = memory[1][i]+statistic.getDim();
                    memory[2][i] = memory[2][i]+statistic.getForget();
                }
            }
        }
        else{
            for(int i=0; i<30;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(30-i));//30-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[0][i] = memory[0][i]+statistic.getRemember();
                        memory[1][i] = memory[1][i]+statistic.getDim();
                        memory[2][i] = memory[2][i]+statistic.getForget();
                    }
                }
            }
        }

        //今天之后各天需背卡片数量

        //提取今天当前标签下的应复习卡片
        List<stageList> todaystage = dBhelper.getStageList();
        date.setTime(today);
        for (int m = 0; m < todaystage.size(); m++) {
            if (todaystage.get(m).getDate().compareTo(today) != 0 || todaystage.get(m).getTab().compareTo(label)!= 0 ) {
                todaystage.remove(m);
                m--;
            }
        }

        for(int i=0;i<todaystage.size();i++){
            if(todaystage.get(i).getRemember()!=0 || todaystage.get(i).getDim()!=0 || todaystage.get(i).getForget() != 0){
                //今日卡片已经开始背诵
                memory[0][30] = memory[0][30]+ todaystage.get(i).getRemember();
                memory[1][30] = memory[1][30]+ todaystage.get(i).getDim();
                memory[2][30] = memory[2][30]+ todaystage.get(i).getForget();
            }
            else if(i==todaystage.size()-1 && memory[0][30] == 0 && memory[1][30] == 0 && memory[2][30] == 0){
                //如果今天的卡片还未开始背诵，获取今天应背卡片个数
                memory[3][30] = dBhelper.getReciteCards().size();
            }
        }

        date.setTime(today);

        for(int i=1;i<10;i++){
            List<memoryCardsList> memoryList = dBhelper.getCardList();
            date.setTime(today);
            date.add(Calendar.DATE, i);//i天后的日期
            Date statisticDate = date.getTime();
            //获取i天后当前标签应背卡片
            for (int m = 0; m < memoryList.size(); m++) {
                if (memoryList.get(m).getReciteDate().compareTo(statisticDate) != 0 || memoryList.get(m).getTab().compareTo(label) !=0 ) {
                    memoryList.remove(m);
                    m--;
                }
            }
            memory[3][30+i] = memoryList.size();
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        //录入今天前每天的学习数据
        for (int i = 0; i < 30; i++) {
            float val1 = (float) memory[0][i];
            float val2 = (float) memory[1][i];
            float val3 = (float) memory[2][i];
//            float mult = (50 + 1);
//            float val1 = (float) (Math.random() * mult) + mult / 3;
//            float val2 = (float) (Math.random() * mult) + mult / 3;
//            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(i, new float[]{val1, val2, val3}));
        }

        //录入今天的学习情况
        if(memory[3][30]!=0){
            //如果今天还未开始背诵
            float val1 = (float) memory[3][30];
            float val2 = (float) 0;
            float val3 = (float) 0;
            yVals1.add(new BarEntry(30, new float[]{val1, val2, val3}));

        }
        else {
            //如果今天已经开始背诵
            float val1 = (float) memory[0][30];
            float val2 = (float) memory[1][30];
            float val3 = (float) memory[2][30];
            yVals1.add(new BarEntry(30, new float[]{val1, val2, val3}));
        }

        //录入今天之后的学习情况
        for (int i = 31; i < 41; i++) {
            float val1 = (float) memory[3][i];
            float val2 = (float) 0;
            float val3 = (float) 0;
//            float mult = (50 + 1);
//            float val1 = (float) (Math.random() * mult) + mult / 3;
//            float val2 = (float) (Math.random() * mult) + mult / 3;
//            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(i, new float[]{val1, val2, val3}));
        }

        BarDataSet set1;

        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "学习情况");
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"忘记", "模糊", "记得"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new MyValueFormatter());
            data.setValueTextColor(Color.WHITE);

            mBarChart.setData(data);
        }
        mBarChart.setFitBars(true);
        mBarChart.invalidate();
    }

    private int[] getColors() {
        int stacksize = 3;
        //有尽可能多的颜色每项堆栈值
        int[] colors = new int[stacksize];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }
        return colors;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

}
