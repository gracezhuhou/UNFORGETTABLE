package com.example.unforgettable.LitepalTable;

import org.litepal.crud.LitePalSupport;

public class statusSumList extends LitePalSupport {
    private int id;
    private int span;   // 记忆时间
    private int rememberSum = 0;
    private int dimSum = 0;
    private int forgetSum = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public int getRememberSum() {
        return rememberSum;
    }

    public void setRememberSum(int rememberSum) {
        this.rememberSum = rememberSum;
    }

    public int getDimSum() {
        return dimSum;
    }

    public void setDimSum(int dimSum) {
        this.dimSum = dimSum;
    }

    public int getForgetSum() {
        return forgetSum;
    }

    public void setForgetSum(int forgetSum) {
        this.forgetSum = forgetSum;
    }
}
