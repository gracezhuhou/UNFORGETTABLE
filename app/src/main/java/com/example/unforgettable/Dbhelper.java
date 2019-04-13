package com.example.unforgettable;

import org.litepal.LitePal;

// 用于数据库增删改查等操作

public class Dbhelper {
    public Dbhelper(){
        LitePal.getDatabase();
    }
}
