package com.example.unforgettable.loginData;

import com.example.unforgettable.loginData.model.LoggedInUser;
import com.example.unforgettable.Bmob.MyUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        // handle loggedInUser authentication

        try {
            MyUser myUser = MyUser.getCurrentUser(MyUser.class);
            LoggedInUser user =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // revoke authentication
        MyUser.logOut();   //清除缓存用户对象
        //MyUser currentUser = MyUser.getCurrentUser(MyUser.class); //现在的currentUser是null
    }
}
