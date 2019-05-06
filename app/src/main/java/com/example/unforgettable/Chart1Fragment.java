//记忆持久度图表
package com.example.unforgettable;

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

import com.example.unforgettable.LitepalTable.stageList;
import com.example.unforgettable.LitepalTable.tabList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class Chart1Fragment extends Fragment {

    private LineChartView lineChart;
    private List<PointValue> mPointValues0 = new ArrayList<PointValue>();
    private List<PointValue> mPointValues1 = new ArrayList<PointValue>();
    private List<PointValue> mPointValues2 = new ArrayList<PointValue>();
    private List<PointValue> mPointValues3 = new ArrayList<PointValue>();
    private List<PointValue> mPointValues4 = new ArrayList<PointValue>();

    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private int index;//标签所在下标
    private String label = "全部";
    private Spinner spinner;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();
    private int [][] memory = new int [5][60];
    private List<tabList> tabList = dBhelper.getTabList();    //背诵卡片列表

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart1, container, false);

        spinner = (Spinner)view.findViewById(R.id.spinner);
        spinner.bringToFront();
        lineChart = (LineChartView)view.findViewById(R.id.chart);
        // TODO: @陈独秀

        // 图表
        initLineChart();//初始化

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @DaiSmallEast  这里写监听事件
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
                //Toast.makeText(getActivity(), "你点击的是:"+label, Toast.LENGTH_LONG).show();
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
            Log.v("记忆持久度", "页面隐藏");
        } else {
            dBhelper = new Dbhelper();
            initLineChart();
            //now visible to user
            Log.v("记忆持久度", "刷新页面");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        dBhelper = new Dbhelper();
        initLineChart();
    }


    //图表
    //LineChart

    //设置X 轴的显示
    private void getAxisXLables() {
        for (int i = 56; i >= 0; i--) {
            mAxisXValues.add(new AxisValue(i).setLabel((59-i)+"天前"));
        }
        mAxisXValues.add(new AxisValue(59).setLabel("今天"));
        mAxisXValues.add(new AxisValue(58).setLabel("昨天"));
        mAxisXValues.add(new AxisValue(57).setLabel("前天"));
    }

    //图表的每个点的显示

    private void getAxisPoints() {

        //memory数组初始化
        for(int i =0;i<5;i++){
            for(int j=0;j<60;j++){
                memory[i][j] = 0;
            }
        }

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        Calendar date = Calendar.getInstance();
        date.setTime(today);

        //获取已加入记忆规划的全部背卡片数量,stage=0
        //memory[0]记录已加入记忆规划的卡片数量
        //List<StageList> stageList0 = LitePal.where("stage = ? ", "0").order("date").find(StageList.class);
        //判断标签
//        date.add(Calendar.DATE,-0);
//        Date stat = date.getTime();

        if(label.equals("全部")){
            int i=0;
            for(; i<60;i++){
//                List<StageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(StageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                int temp = 59-i;
                date.setTime(today);
                date.add(Calendar.DATE, -temp);//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[0][i] = memory[0][i]+statistic.getStage0()+statistic.getStage1()+statistic.getStage2()+statistic.getStage3()
                    +statistic.getStage4()+statistic.getStage5()+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                }
            }
        }
        else{
            int i=0;
            for(; i<60;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[0][i] = memory[0][i]+statistic.getStage0()+statistic.getStage1()+statistic.getStage2()+statistic.getStage3()
                                +statistic.getStage4()+statistic.getStage5()+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                    }
                }
            }
        }

        //获取记忆持久度>10天的全部背卡片数量,stage=3
        //memory[1]记录记忆持久度>10天的卡片数量
        //List<StageList> stageList1 = LitePal.where("stage = ? ", "3").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            int i=0;
            for(; i<60;i++){
//                List<StageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(StageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[1][i] = memory[1][i]+statistic.getStage3()+statistic.getStage4()+statistic.getStage5()
                            +statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                }
            }
        }
        else{
            int i=0;
            for(; i<60;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[1][i] = memory[1][i]+statistic.getStage3()+statistic.getStage4()+statistic.getStage5()
                                +statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                    }
                }
            }
        }
        //获取记忆持久度>30天的全部背卡片数量,stage=5
        //memory[2]记录记忆持久度>30天的卡片数量
        //List<StageList> stageList2 = LitePal.where("stage = ? ", "5").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            int i=0;
            for(; i<60;i++){
//                List<StageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(StageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[2][i] = memory[2][i]+statistic.getStage5()+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                }
            }
        }
        else{
            int i=0;
            for(i=59; i<60;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[2][i] = memory[2][i]+statistic.getStage5()+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                    }
                }
            }
        }
        //获取记忆持久度>60天的全部背卡片数量,stage=6
        //memory[3]记录记忆持久度>60天的卡片数量
        //List<StageList> stageList3 = LitePal.where("stage = ? ", "6").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            int i=0;
            for(; i<60;i++){
//                List<StageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(StageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[3][i] = memory[3][i]+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                }
            }
        }
        else{
            int i=0;
            for(; i<60;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[3][i] = memory[3][i]+statistic.getStage6()+statistic.getStage7()+statistic.getStage8();
                    }
                }
            }
        }

        //获取记忆持久度>90天的全部背卡片数量,stage=7
        //memory[4]记录记忆持久度>90天的卡片数量
        //List<StageList> stageList4 = LitePal.where("stage = ? ", "7").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            int i=0;
            for(; i<60;i++){
//                List<StageList> stageList = LitePal.where("date = ?", getOldDate(-i)).find(StageList.class);
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    memory[4][i] = memory[4][i]+statistic.getStage7()+statistic.getStage8();
                }
            }
        }
        else{
            int i=0;
            for(; i<60;i++){
                List<stageList> stageList = dBhelper.getStageList();
                date.setTime(today);
                date.add(Calendar.DATE, -(59-i));//59-i天前的日期
                Date statisticDate = date.getTime();
                for (int m = 0; m < stageList.size(); m++) {
                    if (stageList.get(m).getDate().compareTo(statisticDate) != 0) {
                        stageList.remove(m);
                        m--;
                    }
                }
                if(stageList.isEmpty()){continue;}
                for(int j=0;j<stageList.size();j++){
                    stageList statistic = stageList.get(j);
                    if(statistic.getTab().equals(label)){
                        memory[4][i] = memory[4][i]+statistic.getStage7()+statistic.getStage8();
                    }
                }
            }
        }
        for (int i = 0; i < 60; i++) {
            mPointValues0.add(new PointValue(i, memory[0][i]));//已加入记忆规划的全部卡片
            mPointValues1.add(new PointValue(i, memory[1][i]));//记忆持久度>10天的卡片数量
            mPointValues2.add(new PointValue(i, memory[2][i]));//记忆持久度>30天的卡片数量
            mPointValues3.add(new PointValue(i, memory[3][i]));//记忆持久度>60天的卡片数量
            mPointValues4.add(new PointValue(i, memory[4][i]));//记忆持久度>90天的卡片数量
        }
    }

    private void initLineChart() {
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点

        List<Line> lines = new ArrayList<Line>();
        if(!mPointValues0.isEmpty()){
            Line line0 = new Line(mPointValues0).setColor(Color.parseColor("#FF7F50"));  //折线的颜色（靛青色）
            line0.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line0.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line0.setFilled(false);//是否填充曲线的面积
            line0.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line0.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line0.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line0);
        }

        if(!mPointValues1.isEmpty()){
            Line line1 = new Line(mPointValues1).setColor(Color.parseColor("#ffe4e1"));  //折线的颜色（浅玫瑰色）
            line1.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line1.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line1.setFilled(false);//是否填充曲线的面积
            line1.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line1.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line1.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line1);
        }

        if(!mPointValues2.isEmpty()){
            Line line2 = new Line(mPointValues2).setColor(Color.parseColor("#f0ffff"));  //折线的颜色（天蓝色）
            line2.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line2.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line2.setFilled(false);//是否填充曲线的面积
            line2.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line2.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line2.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line2);

        }

        if(!mPointValues3.isEmpty()){
            Line line3 = new Line(mPointValues3).setColor(Color.parseColor("#f08080"));  //折线的颜色（珊瑚色）
            line3.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line3.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line3.setFilled(false);//是否填充曲线的面积
            line3.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line3.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line3.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line3);
        }

        if(!mPointValues4.isEmpty()){
            Line line4 = new Line(mPointValues4).setColor(Color.parseColor("#e6e6fa"));  //折线的颜色（淡紫色）
            line4.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line4.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line4.setFilled(false);//是否填充曲线的面积
            line4.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line4.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line4.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line4);

        }

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("记忆持久度");  //表格名称
        axisX.setTextSize(5);//设置字体大小
        //axisX.setMaxLabelChars(60); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("数量");//y轴标注
        axisY.setTextSize(5);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        //lineChart.setLineChartData(data);
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。
         Viewport v = new Viewport(lineChart.getMaximumViewport());
         v.left = 0;
         v.right = 3;
         lineChart.setCurrentViewport(v);*/
    }

    /**
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    //-distanceDay代表要获取当前N天前的日期，
    //+distanceDay代表要获取当前N天后的日期
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

}
