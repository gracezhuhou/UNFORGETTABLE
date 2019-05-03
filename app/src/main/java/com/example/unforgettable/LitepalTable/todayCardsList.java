package com.example.unforgettable.LitepalTable;

import org.litepal.crud.LitePalSupport;

public class todayCardsList extends LitePalSupport {
    private int id;
    private String heading; // 正面 标题
    private int firstReciteStatus;   // 是否第一次背的状态

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getFirstReciteStatus() {
        return firstReciteStatus;
    }

    public void setFirstReciteStatus(int firstReciteStatus) {
        this.firstReciteStatus = firstReciteStatus;
    }
}
