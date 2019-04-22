package com.example.unforgettable;

import org.litepal.crud.LitePalSupport;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.Date;

public class TodayCardsList extends LitePalSupport {
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
