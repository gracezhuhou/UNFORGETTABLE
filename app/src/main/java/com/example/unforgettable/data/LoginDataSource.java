package com.example.unforgettable.data;

import android.util.Log;

import com.example.unforgettable.data.model.LoggedInUser;
import com.example.unforgettable.Bmob.MyUser;

import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        // handle loggedInUser authentication

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

        // 邮箱验证
//        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
//        if (!myUser.getEmailVerified()) {
//            Toast.makeText(getApplicationContext(),"请至" + myUser.getEmail() + "邮箱中进行激活",Toast.LENGTH_LONG).show();
//            return new Result.Error(new IOException("Error logging in Bmob"));
//        }

        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser.getUsername().equals(username)) {
            try {
                LoggedInUser user =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                userlogin.getNickname());
                return new Result.Success<>(user);
            }
            catch (Exception e) {
                return new Result.Error(new IOException("Error logging in", e));
            }
        }
       else {
            return new Result.Error(new IOException("Error logging in Bmob"));
       }
    }

    public void logout() {
        // TODO: revoke authentication
        MyUser.logOut();   //清除缓存用户对象
        MyUser currentUser = MyUser.getCurrentUser(MyUser.class); //现在的currentUser是null
    }
}
