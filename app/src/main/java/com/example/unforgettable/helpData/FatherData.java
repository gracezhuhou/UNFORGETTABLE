package com.example.unforgettable.helpData;

import java.util.ArrayList;

public class FatherData {
    private String title;
    private ArrayList<ChildrenData> list;// 二级列表数据
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ArrayList<ChildrenData> getList() {
        return list;
    }
    public void setList(ArrayList<ChildrenData> list) {
        this.list = list;
    }
}
