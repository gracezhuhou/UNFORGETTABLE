//记忆持久度图表
package com.example.unforgettable;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.litepal.LitePal;

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
    private String label;
    private Spinner spinner;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();
    private int [][] memory = new int [5][60];
    private List<TabList> tabList;    //背诵卡片列表

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_statistic, container, false);

        spinner = view.findViewById(R.id.spinner);
        // TODO: @陈独秀

        // 图表
        memoryChart();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @DaiSmallEast  这里写监听事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] labels = getResources().getStringArray(R.array.reviewtType);
                label = labels[pos];
                index = pos;
                Log.v("label","选择的标签是" + labels[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //图表
    //LineChart
    private void memoryChart() {
        initLineChart();//初始化
    }

    //设置X 轴的显示
    private void getAxisXLables() {
        for (int i = 0; i < 60; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(i+"天前"));
        }
    }

    //图表的每个点的显示

    private void getAxisPoints() {

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        //获取已加入记忆规划的全部背卡片数量,stage=0
        //memory[0]记录已加入记忆规划的卡片数量
        List<StageList> stageList0 = LitePal.where("stage = ? ", "0").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            for(int i=0; i<60;i++){
                stageList0 = LitePal.where("stage = ? and date = ?", "0", getOldDate(-i)).order("date").find(StageList.class);
                memory[0][i] = stageList0.size();
            }
        }
        else{
            for (int i = 0; i < stageList0.size(); i++) {
                //移除标签不属于当前选择标签的卡片记录
                if (!stageList0.get(i).getTab().equals(label)){
                    stageList0.remove(i);
                    i--;//>：1；<：-1；==：0
                }
            }
            for(int i=0; i<60;i++){
                stageList0 = LitePal.where("stage = ? and date = ?", "0", getOldDate(-i)).order("date").find(StageList.class);
                memory[0][i] = stageList0.size();
            }
        }

        //获取记忆持久度>10天的全部背卡片数量,stage=3
        //memory[1]记录记忆持久度>10天的卡片数量
        List<StageList> stageList1 = LitePal.where("stage = ? ", "3").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            for(int i=0; i<60;i++){
                stageList1 = LitePal.where("stage = ? and date = ?", "3", getOldDate(-i)).order("date").find(StageList.class);
                memory[1][i] = stageList1.size();
            }
        }
        else{
            for (int i = 0; i < stageList1.size(); i++) {
                //移除标签不属于当前选择标签的卡片记录
                if (!stageList1.get(i).getTab().equals(label)){
                    stageList1.remove(i);
                    i--;//>：1；<：-1；==：0
                }
            }
            for(int i=0; i<60;i++){
                stageList1 = LitePal.where("stage = ? and date = ?", "3", getOldDate(-i)).order("date").find(StageList.class);
                memory[1][i] = stageList1.size();
            }
        }

        //获取记忆持久度>30天的全部背卡片数量,stage=5
        //memory[2]记录记忆持久度>30天的卡片数量
        List<StageList> stageList2 = LitePal.where("stage = ? ", "5").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            for(int i=0; i<60;i++){
                stageList2 = LitePal.where("stage = ? and date = ?", "5", getOldDate(-i)).order("date").find(StageList.class);
                memory[2][i] = stageList2.size();
            }
        }
        else{
            for (int i = 0; i < stageList2.size(); i++) {
                //移除标签不属于当前选择标签的卡片记录
                if (!stageList2.get(i).getTab().equals(label)){
                    stageList2.remove(i);
                    i--;//>：1；<：-1；==：0
                }
            }
            for(int i=0; i<60;i++){
                stageList2 = LitePal.where("stage = ? and date = ?", "5", getOldDate(-i)).order("date").find(StageList.class);
                memory[2][i] = stageList2.size();
            }
        }
        //获取记忆持久度>60天的全部背卡片数量,stage=6
        //memory[3]记录记忆持久度>60天的卡片数量
        List<StageList> stageList3 = LitePal.where("stage = ? ", "6").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            for(int i=0; i<60;i++){
                stageList3 = LitePal.where("stage = ? and date = ?", "3", getOldDate(-i)).order("date").find(StageList.class);
                memory[3][i] = stageList3.size();
            }
        }
        else{
            for (int i = 0; i < stageList3.size(); i++) {
                //移除标签不属于当前选择标签的卡片记录
                if (!stageList3.get(i).getTab().equals(label)){
                    stageList3.remove(i);
                    i--;//>：1；<：-1；==：0
                }
            }
            for(int i=0; i<60;i++){
                stageList3 = LitePal.where("stage = ? and date = ?", "6", getOldDate(-i)).order("date").find(StageList.class);
                memory[3][i] = stageList3.size();
            }
        }

        //获取记忆持久度>90天的全部背卡片数量,stage=7
        //memory[4]记录记忆持久度>90天的卡片数量
        List<StageList> stageList4 = LitePal.where("stage = ? ", "7").order("date").find(StageList.class);
        //判断标签
        if(label.equals("全部")){
            for(int i=0; i<60;i++){
                stageList4 = LitePal.where("stage = ? and date = ?", "7", getOldDate(-i)).order("date").find(StageList.class);
                memory[4][i] = stageList4.size();
            }
        }
        else{
            for (int i = 0; i < stageList4.size(); i++) {
                //移除标签不属于当前选择标签的卡片记录
                if (!stageList4.get(i).getTab().equals(label)){
                    stageList4.remove(i);
                    i--;//>：1；<：-1；==：0
                }
            }
            for(int i=0; i<60;i++){
                stageList4 = LitePal.where("stage = ? and date = ?", "7", getOldDate(-i)).order("date").find(StageList.class);
                memory[4][i] = stageList4.size();
            }
        }

        for (int i = 0; i < 60; i++) {
            mPointValues0.add(new PointValue(i, memory[0][i]));
            mPointValues1.add(new PointValue(i, memory[1][i]));
            mPointValues2.add(new PointValue(i, memory[2][i]));
            mPointValues3.add(new PointValue(i, memory[3][i]));
            mPointValues4.add(new PointValue(i, memory[4][i]));
        }
    }

    private void initLineChart() {
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点

        List<Line> lines = new ArrayList<Line>();
        Line line0 = new Line(mPointValues0).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        line0.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line0.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line0.setFilled(false);//是否填充曲线的面积
        line0.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line0.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line0.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        Line line1 = new Line(mPointValues0).setColor(Color.parseColor("#ffe4e1"));  //折线的颜色（浅玫瑰色）
        line1.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line1.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line1.setFilled(false);//是否填充曲线的面积
        line1.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line1.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line1.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        Line line2 = new Line(mPointValues0).setColor(Color.parseColor("#f0ffff"));  //折线的颜色（天蓝色）
        line2.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line2.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line2.setFilled(false);//是否填充曲线的面积
        line2.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line2.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line2.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        Line line3 = new Line(mPointValues0).setColor(Color.parseColor("#f08080"));  //折线的颜色（珊瑚色）
        line3.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line3.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line3.setFilled(false);//是否填充曲线的面积
        line3.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line3.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line3.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        Line line4 = new Line(mPointValues0).setColor(Color.parseColor("#e6e6fa"));  //折线的颜色（淡紫色）
        line4.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line4.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line4.setFilled(false);//是否填充曲线的面积
        line4.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line4.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line4.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

        lines.add(line0);
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("记忆持久度");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        //axisX.setMaxLabelChars(60); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("数量");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
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
