package com.example.unforgettable;

//学习情况表

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.unforgettable.LitepalTable.stageList;
import com.example.unforgettable.LitepalTable.memoryCardsList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Chart2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String label;
    private Spinner spinner;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();

    //每天记忆，模糊，忘记,需复习
    // 往前30天，往后10天+今天
    private int [][] memory = new int [4][41];
    private List<com.example.unforgettable.LitepalTable.tabList> tabList = dBhelper.getTabList();    //背诵卡片列表
    private BarChartView chartView1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);

        // TODO: @陈独秀

        chartView1 = view.findViewById(R.id.bar_view1);
        spinner = view.findViewById(R.id.spinner);

        // 图表
        initLineChart();//初始化

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

    private void initLineChart() {
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

        for(int i=0; i<30;i++){
//          List<stageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(stageList.class);
            List<stageList> stageList = dBhelper.getStageList();
            date.setTime(today);
            date.add(Calendar.DATE, -i);//i天前的日期
            Date statisticDate = date.getTime();
            for (int m = 0; m < stageList.size(); m++) {
                if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                    stageList.remove(m);
                    m--;
                }
            }
            for(int j=0;j<stageList.size();j++){
                com.example.unforgettable.LitepalTable.stageList statistic = stageList.get(j);
                memory[0][i] = memory[0][i]+statistic.getRemember();
                memory[1][i] = memory[1][i]+statistic.getDim();
                memory[2][i] = memory[2][i]+statistic.getForget();
            }
        }

        //今天之后各天需背卡片数量

        //提取今天应复习卡片
        List<stageList> todaystage = dBhelper.getStageList();
        date.setTime(today);
        for (int m = 0; m < todaystage.size(); m++) {
            if (todaystage.get(m).getDate().compareTo(today) != 0) {
                todaystage.remove(m);
                m--;
            }
        }

        for(int i=0;i<todaystage.size();i++){
            if(todaystage.get(i).getRemember()!=0 || todaystage.get(i).getDim()!=0 || todaystage.get(i).getForget() != 0){
                memory[0][30] = memory[0][30]+ todaystage.get(i).getRemember();
                memory[1][30] = memory[1][30]+ todaystage.get(i).getDim();
                memory[2][30] = memory[2][30]+ todaystage.get(i).getForget();
            }
            else if(i==todaystage.size()-1 && memory[0][30] == 0 && memory[1][30] == 0 && memory[2][30] == 0){
                //获取今天应背卡片个数
                memory[3][30] = dBhelper.getReciteCards().size();
            }
        }

        date.setTime(today);
        for(int i=1;i<10;i++){
            List<memoryCardsList> memoryList = dBhelper.getCardList();
            date.setTime(today);
            date.add(Calendar.DATE, +i);//i天后的日期
            Date statisticDate = date.getTime();
            //获取i天后应背卡片
            for (int m = 0; m < memoryList.size(); m++) {
                if (memoryList.get(m).getReciteDate().compareTo(statisticDate) != 0) {
                    memoryList.remove(m);
                    m--;
                }
            }
            memory[3][30+i] = memoryList.size();
        }

        //柱形图每段高度获取
        String []text = new String[41];
        for(int i=0;i<41;i++){
            int temp = Math.max(memory[0][i]+memory[1][i]+memory[2][i],memory[3][i]);
            text[i] = String.valueOf(temp);
        }
        chartView1.SetData(memory[0],memory[1],memory[2],memory[3],10,text);
    }

}
