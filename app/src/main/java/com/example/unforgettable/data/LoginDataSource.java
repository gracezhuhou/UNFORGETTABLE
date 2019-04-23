package com.example.unforgettable.data;

import android.util.Log;
import android.widget.Toast;

import com.example.unforgettable.data.model.LoggedInUser;
import com.example.unforgettable.ui.login.MyUser;

import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            MyUser userlogin = new MyUser();
            userlogin.setUsername(username);
            userlogin.setPassword(password);
            userlogin.login(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                    if(e == null){
                        Log.v("登录界面", myUser.getNickname()+"登录成功");

                    }else {
                        Log.e("登录失败", "原因: ", e);
                    }
                }
            });

            // TODO: handle loggedInUser authentication
            LoggedInUser user =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            userlogin.getNickname());
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
        MyUser.logOut();   //清除缓存用户对象
        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class); //现在的currentUser是null
    }
}
