package com.example.unforgettable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BarChartView extends View {

    private boolean isLoadNewData = false;
    private float itemWidth = 310;
    private float itemHeight = 310;

    private float barWidth = 20; //默认柱子的宽度
    private float barHeight = 50; //柱子的高度
    private float cellHeigh = 1;  //按照list的最大值算出的1点的height
    private float widthSpace = 10; //默认柱子的间距
    private float viewMargin = 20; //整个view的外边距

    private float textHeight; //显示文字高度

    private Paint colorBarPaint; //柱状图画笔
    private TextPaint textPaint; //文字画笔

    //柱状图的柱形矩形
    private RectF barRectf;

    private List<List<BarGraphInfo>> mList;
    private List<List<BarGraphInfo>> mDefaultList;

    //共有多少个柱状图
    private int barCount;

    private float startX;
    private float startY;

    private float maxBarHeight;//最大的柱状图高度

    //数据中最小的柱子的高度 ，用来判断虚线显示位置的
    private float minBarHeight = 99999;


    //不同分辨率的比例外边框相对默认比例
    private float scaleMargin = 1;
    //    private float scaleBarWidth = 1;
    //默认在这个标准下的宽度和高度图形的字符和其他正常
    private int defualtWidth = 480;
    private int defualtHeight = 440;
//    private int defualtBarWidth = 44; //柱状图个默认44宽

    public BarChartView(Context context) {
        this(context,null);
    }
    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //获取不同屏幕的比例大小
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        scaleWidth = width / 1920;
//        scaleHeight = height / 1080;
        init();
    }
    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init() {
        //初始化画笔
        colorBarPaint = new Paint();
        colorBarPaint.setAntiAlias(true);
        //设置矩形
        barRectf = new RectF();

        textPaint = new TextPaint();

        //默认初始化数据
        mDefaultList = new ArrayList<>();

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());


        List<BarGraphInfo> listOne = new ArrayList<>();
        listOne.add(new BarGraphInfo(Color.DKGRAY,"主布A",100));
        listOne.add(new BarGraphInfo(Color.YELLOW,"主布B",200));
        List<BarGraphInfo> listTwo = new ArrayList<>();
        listTwo.add(new BarGraphInfo(Color.DKGRAY,"主布C",200));
        listTwo.add(new BarGraphInfo(Color.YELLOW,"主布D",400));
        List<BarGraphInfo> listThree = new ArrayList<>();
        listThree.add(new BarGraphInfo(Color.DKGRAY,"主布E",300));
        listThree.add(new BarGraphInfo(Color.YELLOW,"主布F",100));
        List<BarGraphInfo> listFour = new ArrayList<>();
        listFour.add(new BarGraphInfo(Color.DKGRAY,"主布G",200));
        listFour.add(new BarGraphInfo(Color.YELLOW,"主布H",100));

        mDefaultList.add(listOne);
        mDefaultList.add(listTwo);
        mDefaultList.add(listThree);
        mDefaultList.add(listFour);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);




//        if(widthMode == MeasureSpec.EXACTLY){
//
//            scaleMargin = ((widthSize/defualtWidth)+(heightSize/defualtHeight))/2;
//
//            //根据各个手机分辨率不同算出margin的间距大小
//            viewMargin = scaleMargin * viewMargin;
//            if(viewMargin == 0){
//                viewMargin = 20;
//                startY = 20;
//            }
//            itemWidth = widthSize - (2 * viewMargin); //减去外边距
//            startX = viewMargin; //起始位置定位到默认的
//        }
//
//        if(heightMode == MeasureSpec.EXACTLY){
//            //根据各个手机分辨率不同算出margin的间距大小
//            viewMargin = scaleMargin * viewMargin;
//            if(viewMargin == 0){
//                viewMargin = 20;
//                startX = 20;
//            }
//            itemHeight = heightSize - (2 * viewMargin);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

//
        scaleMargin = ((getMeasuredWidth()/defualtWidth)+(getMeasuredHeight()/defualtHeight))/2;

        //根据各个手机分辨率不同算出margin的间距大小
        viewMargin = scaleMargin * viewMargin;

        itemWidth = getMeasuredWidth() - (2 * viewMargin); //减去外边距
        startX = viewMargin; //起始位置定位到默认的

        itemHeight = getMeasuredHeight() - (2 * viewMargin);
        startY = viewMargin;

        Log.i("tag","itemWidth = " + itemWidth);
        Log.i("tag","itemHeight = " + itemHeight);
        Log.i("tag","viewMargin = " + viewMargin);
        Log.i("tag","onLayout()");
        if(!isLoadNewData) {
            isLoadNewData = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setList(mDefaultList);
                }
            },3000);

        }

    }

    private int totalTime = 50; //总绘制次数，用来控制绘制速度
    private int currentTime = 0; //当前已绘制的次数
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        currentTime++;
        float left = startX + widthSpace;
        //画矩形
        for (int i = 0;i < barCount;i++){

            List<BarGraphInfo> list = mList.get(i);
            float itemHeightSum = 0;
            for(int j = 0; j < list.size();j++){
                //计算出高度
                barHeight = cellHeigh * list.get(j).num;
                barHeight = barHeight/totalTime*currentTime;

                itemHeightSum += barHeight;

                //设置颜色
                colorBarPaint.setColor(list.get(j).color);
                barRectf = new RectF();

                float barLeft = left + i * barWidth;
                float top = (startY + textHeight + maxBarHeight) - itemHeightSum;
                float right = left + (i + 1) * barWidth;
                float bottom = startY + textHeight + maxBarHeight - (itemHeightSum - barHeight) ;
                barRectf.set(barLeft,top,right,bottom);
                canvas.drawRect(barRectf,colorBarPaint);

                //画num
//                RectF numRectF = new RectF();
//                float numLeft = left + i * barWidth;
//                float numTop = (startY + textHeight + maxBarHeight) - barHeight - textHeight;
//                float numRight = left + (i + 1) * barWidth;
//                float numBottom = (startY + textHeight + maxBarHeight) - barHeight;
//                numRectF.set(numLeft,numTop,numRight,numBottom);
                drawText(canvas,barRectf,String.valueOf(list.get(j).num == 0? 0 : list.get(j).num),Color.RED,false);
            }

            //画subTitle
            RectF textRectF = new RectF();
            float subTitleLeft = left + i * barWidth;
            float subTitleTop = startY + textHeight + maxBarHeight;
            float subTitleRight = left + (i + 1) * barWidth;
            float subTitleBottom = startY + textHeight + maxBarHeight + textHeight;
            textRectF.set(subTitleLeft,subTitleTop,subTitleRight,subTitleBottom);
            drawText(canvas,textRectF,"配套总览",list!= null && list.size()>0?list.get(0).color:Color.DKGRAY,true);



//            //画num
//            RectF numRectF = new RectF();
//            float numLeft = left + i * barWidth;
//            float numTop = (startY + textHeight + maxBarHeight) - barHeight - textHeight;
//            float numRight = left + (i + 1) * barWidth;
//            float numBottom = (startY + textHeight + maxBarHeight) - barHeight;
//            numRectF.set(numLeft,numTop,numRight,numBottom);
//            drawText(canvas,numRectF,String.valueOf(mList.get(i).num == 0? 0 : mList.get(i).num),mList.get(i).color,false);

            left += barWidth;
        }


        //用来刷新数据的
        if (currentTime < totalTime) {
            postInvalidate();
        }
    }



    //画文字居中
    private void drawText(Canvas canvas,RectF rectF,String text,int color,boolean isTitle){
        if(color == 0) {
            textPaint.setColor(Color.BLACK);
        }else{
            textPaint.setColor(color);
        }
        float textSize = barWidth/44 * 18 > 23 ? 23 : 18 * (barWidth / 44);
        textSize = textSize < 15 ? 15 : textSize;
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rectF.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式

        if(isTitle) {
            //换行 拆开换行
            int group = (int) (4 * (barWidth / 44));
            if (text.length() > group) {
                int count = text.length() % group >= 1 ? text.length() / group + 1 : text.length() / group;
                for (int i = 0; i < count; i++) {
                    if ((i + 1) * group < text.length()) {
                        canvas.drawText(text.substring(i * group, (i + 1) * group), rectF.centerX(), baseLineY + (i * rectF.height() / 2), textPaint);
                    } else {
                        canvas.drawText(text.substring(i * group, text.length()), rectF.centerX(), baseLineY + (i * rectF.height() / 2), textPaint);
                    }
                }
            } else {
                canvas.drawText(text, rectF.centerX(), baseLineY, textPaint);
            }
        }else{
            canvas.drawText(text, rectF.centerX(), baseLineY, textPaint);
        }
    }


    public static class BarGraphInfo{
        private int color;
        private String subTitle;
        private float num;
        private String keyInfo; //记录关键信息

        public BarGraphInfo(){}

        public BarGraphInfo(int color,String subTitle,float num){
            this.color = color;
            this.subTitle = subTitle;
            this.num = num;
        }

        public BarGraphInfo(int color,String subTitle,float num,String keyInfo){
            this.color = color;
            this.subTitle = subTitle;
            this.num = num;
            this.keyInfo = keyInfo;
        }

        public String getKeyInfo(){
            return keyInfo;
        }
    }


    /**
     * 设置数据源
     * @param list
     */
    public void setList(List<List<BarGraphInfo>> list){
        setList(list,0);
    }

    /**
     * 设置数据源
     * @param list  数据源
     * @param maxNum 设置默认最大数据值
     */
    public void setList(List<List<BarGraphInfo>> list,float maxNum){

        currentTime = 0;
        if(list == null || list.size() <= 0){
            mList = mDefaultList;
        }else{
            mList = list;
        }
        barCount = mList.size();//多少个柱状图

        float avgWidth = itemWidth / barCount;
        barWidth = avgWidth / 2;  //获取状图的宽度
        widthSpace = avgWidth / 4;  //获取间隙

        for(List<BarGraphInfo> subList : list){
            int subSum = 0;
            for(BarGraphInfo info : subList){
                subSum += info.num;
            }
            if(subSum > maxNum){
                maxNum = subSum;
            }
        }
        if(maxNum == 0){
            maxNum = 100;
        }
        //获取柱状图最大显示区域  平分高度10份，6份用来显示最大柱状图，2份高度用来显示下面的文字,2份用来显示柱状图上面的文字
        maxBarHeight = (itemHeight/10) * maxBarHeightWt;
        textHeight = (itemHeight/10) * textHeightWt; //文字RectF高度
        cellHeigh = maxBarHeight/maxNum;  //平均每num = 1 时的高度
        postInvalidate();
    }
    //最大柱状图的比重 高度相对于控件总高度的比重
    private int maxBarHeightWt = 8;
    //文字高度相对于控件总高度的比重
    private int textHeightWt = 1;
}
