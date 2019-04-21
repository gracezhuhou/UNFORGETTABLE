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
    private Date date;
    private String tab;     // 标签
    private int remember = 0;
    private int dim = 0;
    private int forget = 0;
    private int stage0 = 0;  // 背诵阶段
    private int stage1 = 0;
    private int stage2 = 0;
    private int stage3 = 0;
    private int stage4 = 0;
    private int stage5 = 0;
    private int stage6 = 0;
    private int stage7 = 0;
    private int stage8 = 0;
    private int stage9 = 0;

    //记得添加所有字段的getter和setter方法

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTab() { return tab; }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) { this.date = date; }

    public int getRemember() { return remember; }

    public void setRemember(int remember) {
        this.remember = remember;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getForget() { return forget; }

    public void setForget(int forget) { this.forget = forget; }

    public int getStage0() {
        return stage0;
    }

    public void setStage0(int stage0) {
        this.stage0 = stage0;
    }

    public int getStage1() {
        return stage1;
    }

    public void setStage1(int number) {
        this.stage1 = number;
    }

    public int getStage2() {
        return stage2;
    }

    public void setStage2(int number) {
        this.stage2 = number;
    }

    public int getStage3() {
        return stage3;
    }

    public void setStage3(int number) {
        this.stage3 = number;
    }

    public int getStage4() {
        return stage4;
    }

    public void setStage4(int number) {
        this.stage4 = number;
    }

    public int getStage5() {
        return stage5;
    }

    public void setStage5(int number) {
        this.stage5 = number;
    }

    public int getStage6() {
        return stage6;
    }

    public void setStage6(int number) {
        this.stage6 = number;
    }

    public int getStage7() {
        return stage7;
    }

    public void setStage7(int number) {
        this.stage7 = number;
    }

    public int getStage8() {
        return stage6;
    }

    public void setStage8(int number) {
        this.stage6 = number;
    }

    public int getStage9() {
        return stage7;
    }

    public void setStage9(int number) {
        this.stage7 = number;
    }

    public int getStage(int index) {
        switch(index){
            case 0: return stage0;
            case 1: return stage1;
            case 2: return stage2;
            case 3: return stage3;
            case 4: return stage4;
            case 5: return stage5;
            case 6: return stage6;
            case 7: return stage7;
            case 8: return stage8;
            case 9: return stage9;
            default: return 0;
        }
    }

    public void setStage(int index, int number) {
        switch(index){
            case 0: this.stage0 = number; break;
            case 1: this.stage1 = number; break;
            case 2: this.stage2 = number; break;
            case 3: this.stage3 = number; break;
            case 4: this.stage4 = number; break;
            case 5: this.stage5 = number; break;
            case 6: this.stage6 = number; break;
            case 7: this.stage7 = number; break;
            case 8: this.stage8 = number; break;
            case 9: this.stage9 = number;
        }
    }
}
