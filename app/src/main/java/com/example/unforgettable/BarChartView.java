package com.example.unforgettable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BarChartView extends View {
    Paint textPaint; //文字画笔
    Paint barPaint; //第二柱形图画笔
    Paint barPaint1;//第一柱形图画笔

    int textColor = 0xDDFFFFFF;//文字颜色
    int barColor = 0xDDFF9246; //第二柱形图画笔颜色
    int barColor1 = 0xDDFFAC72;//第一柱形图画笔颜色

    float dataFirst; //第一柱形图数据
    float dataSecond; //第二柱形图数据

    float yMax;//y轴最大数据

    int textMagin = 5; //文字与柱形图距离
    int textSize = 18; //文字大小
    String text; //文字

    public BarChartView(Context context) {
        this(context,null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        //获取屏幕宽度
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        //以分辨率为720*1080准，计算宽高比值,根据屏幕比例计算文字大小
        float ratioWidth = (float) mScreenWidth / 720;
        float ratioHeight = (float) mScreenHeight / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        int textSize = Math.round(this.textSize * ratioMetrics);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        barPaint = new Paint();
        barPaint.setAntiAlias(true);
        barPaint.setColor(barColor);

        barPaint1 = new Paint();
        barPaint1.setAntiAlias(true);
        barPaint1.setColor(barColor1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取每个柱的宽度
        int barWidth = getRight() - getLeft();
        //获取总高度
        int totalHeight = canvas.getHeight();
        //画第二柱
        float secondBarHeight = (dataSecond / yMax )* totalHeight;
        float y = totalHeight - secondBarHeight;
        float x = getLeft();
        canvas.drawRect(x,y,x+barWidth,totalHeight,barPaint1);
        //画第一柱
        float firstBarHeght = (dataFirst / yMax )* totalHeight;
        float firstY = totalHeight - firstBarHeght;
        canvas.drawRect(x,firstY,x+barWidth,y,barPaint);
        //画文字
        Rect rect = new Rect();
        textPaint.getTextBounds(String.valueOf(text), 0, String.valueOf(text).length(), rect);
        int w = rect.width();
        int h = rect.height();
        canvas.drawText(String.valueOf(text),(x+barWidth)/2 - w/2,firstY - h/2 - textMagin,textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置柱型数据
     * @param dataFirst 第一柱的数据值
     * @param dataSecond 第二柱的数据值
     * @param yMax Y轴的最大值
     * @param text 柱上显示的文字
     */
    public void setData(float dataFirst,float dataSecond,float yMax,String text) {
        this.dataSecond = dataSecond;
        this.yMax = yMax;
        this.dataFirst = dataFirst;
        this.text = text;
        invalidate();
    }


    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public void setBarColor1(int barColor1) {
        this.barColor1 = barColor1;
    }

    public void setTextMagin(int textMagin) {
        this.textMagin = textMagin;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}