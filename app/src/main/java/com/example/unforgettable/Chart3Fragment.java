//遗忘曲线图表
package com.example.unforgettable;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

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

public class Chart3Fragment extends Fragment {

    private LineChartView lineChart;
    private List<PointValue> mPointValues0 = new ArrayList<PointValue>();
    private List<PointValue> mPointValues1 = new ArrayList<PointValue>();


    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private int index;//标签所在下标
    private String label = "";
    private Spinner spinner;

    // 数据库相关变量
    private Dbhelper dBhelper = new Dbhelper();
    private float [][] memory = new float [2][27];
    private List<tabList> tabList = dBhelper.getTabList();    //背诵卡片列表

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chart3, container, false);

//        spinner = (Spinner)view.findViewById(R.id.spinner);
        lineChart = (LineChartView)view.findViewById(R.id.chart);
        // TODO: @陈独秀

        // 图表
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: @DaiSmallEast  这里写监听事件
//        final String []tab = new String[tabList.size()+1];
//
//        tab[0] = "全部";
//
//        for (int i=0;i<tabList.size();i++){
//            tab[i+1] = tabList.get(i).getTabName();
//        }
//
//        final ArrayAdapter<String> adpter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item ,tab);
//        adpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adpter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                label = tab[pos];
//                //Toast.makeText(getActivity(), "你点击的是:"+tab[pos], Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Another interface callback
//            }
//        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //now invisible to user
            Log.v("遗忘曲线", "页面隐藏");
        } else {
            dBhelper = new Dbhelper();
            initLineChart();
            //now visible to user
            Log.v("遗忘曲线", "刷新页面");
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
        mAxisXValues.add(new AxisValue(0).setLabel("今天"));
        mAxisXValues.add(new AxisValue(1).setLabel("明天"));
        mAxisXValues.add(new AxisValue(2).setLabel("后天"));
        for (int i = 3; i < 22; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(i+"天后"));
        }
        mAxisXValues.add(new AxisValue(22).setLabel("1个月后"));
        mAxisXValues.add(new AxisValue(23).setLabel("2个月后"));
        mAxisXValues.add(new AxisValue(24).setLabel("3个月后"));
        mAxisXValues.add(new AxisValue(25).setLabel("半年后"));
        mAxisXValues.add(new AxisValue(26).setLabel("1年后"));
    }

    //图表的每个点的显示

    private void getAxisPoints() {

        //memory数组初始化
        for(int i =0;i<2;i++){
            for(int j=0;j<27;j++){
                memory[i][j] = 1;
            }
        }

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        Calendar date = Calendar.getInstance();
        date.setTime(today);

        //艾宾浩斯遗忘曲线
        //每过i天的记忆率，设初次记忆后经过了x小时，那么记忆率y近似地满足y=1-0.56x^0.06
        for(int i=0;i<22;i++){
            int j = i*24;
            memory[0][i] = 1 - (float)0.56 * (float) Math.pow(j,0.06);
        }
        //一个月后
        memory[0][22] = 1 - (float)0.56 * (float)Math.pow(30*24,0.06);
        //2个月后
        memory[0][23] = 1 - (float)0.56 * (float)Math.pow(60*24,0.06);
        //3个月后
        memory[0][24] = 1 - (float)0.56 * (float)Math.pow(90*24,0.06);
        //6个月后
        memory[0][25] = 1 - (float)0.56 * (float)Math.pow(180*24,0.06);
        //1年后
        memory[0][26] = 1 - (float)0.56 * (float)Math.pow(360*24,0.06);

        //用户遗忘曲线
 //       memory[1][0] = 1;
        //获取用户今天的背诵记录比例
//        List<StageList> stageList = dBhelper.getStageList();
//        for(int m=0;m<stageList.size();m++){
//            if (stageList.get(m).getDate().compareTo(today) != 0) {
//                stageList.remove(m);
//                m--;
//            }
//        }
//        int remember = 0;
//        int dim = 0;
//        int forget = 0;
//        for(int j=0;j<stageList.size();j++){
//            remember = remember + stageList.get(j).getRemember();
//            dim = dim + stageList.get(j).getDim();
//            forget = forget + stageList.get(j).getForget();
//        }
//
//        float rate = (float) ((forget+0.5*dim)/(remember+dim+forget));
//        memory[1][1] = memory[1][0]-rate;
//
//        for(int i=2;i<22;i++){//1天后~21天后的遗忘曲线
//            List<MemoryCardsList> memoryList = dBhelper.getReciteCards();
//
//            //获取未来背诵日期与记录日期相隔为i天的应背卡片
//            for(int m=0;m<memoryList.size();m++){
//                if (getGapCount(memoryList.get(m).getRecordDate(),memoryList.get(m).getReciteDate()) != i) {
//                    memoryList.remove(m);
//                    m--;
//                }
//            }
//            for(int n=0;n<memoryList.size();n++){
//
//            }
//
//        }

        //用户遗忘曲线
        for(int i=0;i<27;i++){

            int remember = 0;
            int dim = 0;
            int forget = 0;
            int span = 0;

            if(i<22){span = i;}
            else if(i == 22) span = 30;
            else if(i == 23) span = 60;
            else if(i == 24) span = 90;
            else if(i == 25) span = 180;
            else span = 360;

            if(dBhelper.findStatusRow(span) == null){
                remember = 0;
                dim = 0;
                forget = 0;
            }
            else{
                remember = dBhelper.findStatusRow(span).getRememberSum();
                dim = dBhelper.findStatusRow(span).getDimSum();
                forget = dBhelper.findStatusRow(span).getForgetSum();
            }

            int sum = remember+dim+forget;
            if(sum == 0){
                //若数据库无数据明细，则用户记忆曲线默认为艾宾浩斯曲线
                memory[1][i] = memory[0][i];
            }
            else {
                float temp = (float) ((remember + (0.5 * dim)) / (remember + dim + forget));
                memory[1][i] = 1-temp;
            }

        }

        for (int i = 0; i < 27; i++) {
            mPointValues0.add(new PointValue((float) i, memory[0][i]));
            mPointValues1.add(new PointValue((float) i, memory[1][i]));
        }
    }

    private void initLineChart() {

        List<Line> lines = new ArrayList<Line>();
        if(!mPointValues0.isEmpty()){
            Line line0 = new Line(mPointValues0).setColor(Color.parseColor("#B0E0E6"));  //折线的颜色（橙色）
            line0.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line0.setCubic(true);//曲线是否平滑，即是曲线还是折线
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
            line1.setCubic(true);//曲线是否平滑，即是曲线还是折线
            line1.setFilled(false);//是否填充曲线的面积
            line1.setHasLabels(false);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line1.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line1.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）

            lines.add(line1);
        }

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("遗忘曲线");  //表格名称
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
        //axisX.setMaxLabelChars(10);
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
