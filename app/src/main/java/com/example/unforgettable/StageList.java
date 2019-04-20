package com.example.unforgettable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

// 数据库 - 表名&列名
public class StageList extends LitePalSupport {
    //运用注解来为字段添加index标签

    private int id;
    //忽略即是不在数据库中创建该属性对应的字段
    //不为空
    @Column(nullable = false)
    private String[] tab;     // 标签, 最多选择5个
    private int remember = 0;
    private int forget = 0;


    // TODO: 日期
    private Date date = new Date(System.currentTimeMillis());

    private int stage = 0;  // 背诵阶段
//    private int stage1 = 0;
//    private int stage2 = 0;
//    private int stage3 = 0;
//    private int stage4 = 0;
//    private int stage5 = 0;
//    private int stage6 = 0;
//    private int stage7 = 0;
//    private int stage8 = 0;

    //记得添加所有字段的getter和setter方法

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String[] getTab() { return tab; }

    public void setTab(String[] tab) {
        this.tab = tab;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) { this.date = date; }

    public int getStage0() {
        return stage;
    }

    public void setStage0(int stage) {
        this.stage = stage;
    }

    public int getRemember() { return remember; }

    public void setRemember(int remember) {
        this.remember = remember;
    }

    public int getForget() { return forget; }

    public void setForget(int forget) { this.forget = forget; }

//    public int getStage1() {
//        return stage1;
//    }
//
//    public void setStage1(int number) {
//        this.stage1 = number;
//    }
//
//    public int getStage2() {
//        return stage2;
//    }
//
//    public void setStage2(int number) {
//        this.stage2 = number;
//    }
//
//    public int getStage3() {
//        return stage3;
//    }
//
//    public void setStage3(int number) {
//        this.stage3 = number;
//    }
//
//    public int getStage4() {
//        return stage4;
//    }
//
//    public void setStage4(int number) {
//        this.stage4 = number;
//    }
//
//    public int getStage5() {
//        return stage5;
//    }
//
//    public void setStage5(int number) {
//        this.stage5 = number;
//    }
//
//    public int getStage6() {
//        return stage6;
//    }
//
//    public void setStage6(int number) {
//        this.stage6 = number;
//    }
//
//    public int getStage7() {
//        return stage7;
//    }
//
//    public void setStage7(int number) {
//        this.stage7 = number;
//    }
//
//
}
