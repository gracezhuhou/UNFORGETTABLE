package com.example.unforgettable;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;
    private String[] mValues;

    public MyAxisValueFormatter(String[] values) {
        //mFormat = new DecimalFormat("###,###,###,##0.0");
        mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        //return mFormat.format(value) + " $";
        // "value" represents the position of the label on the axis (x or y)
        Log.d(TAG, "----->getFormattedValue: "+value);
        return mValues[(int) value];
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}

