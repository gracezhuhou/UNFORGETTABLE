package com.example.unforgettable.Bmob;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class MyUser extends BmobUser {
    private String nickname;
    private BmobFile picture;
    private BmobFile database;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BmobFile getDatabase() {
        return database;
    }

    public void setDatabase(BmobFile database) {
        this.database = database;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }
}
