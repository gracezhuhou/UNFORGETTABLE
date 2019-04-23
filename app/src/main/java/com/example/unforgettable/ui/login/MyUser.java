package com.example.unforgettable.ui.login;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
