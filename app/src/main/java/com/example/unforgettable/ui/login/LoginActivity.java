package com.example.unforgettable.ui.login;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unforgettable.Bmob.Bmobhelper;
import com.example.unforgettable.MainActivity;
import com.example.unforgettable.Bmob.MyUser;
import com.example.unforgettable.R;
import com.example.unforgettable.RegisterActivity;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("Alert", MODE_PRIVATE);
        int mode = pref.getInt("background", -1);
        if (mode == -1) {
            setTheme(R.style.AppTheme_Base_Base);
        }
        else {
            setTheme(mode);
        }
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button registerButton = findViewById(R.id.registerButton);

        // bmob初始化
        Bmob.initialize(this, "fff6417ec19cdbd68fa74e7d3860ad8c");

        // 直接进入已登录状态
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        if(user != null){
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);

            // 更新登陆状态
            user.login(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                    if (e == null) {
                        Log.v("Bmob", myUser.getNickname() + "登录成功");

                        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
                            @Override
                            public void done(BmobUser user, BmobException e) {
                                if (e == null) {
                                    //final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
                                    Log.v("Bmob", "更新用户本地缓存信息成功");
                                } else {
                                    Log.e("BmobError","更新用户本地缓存信息失败：" + e.getMessage());
                                }
                            }
                        });

                    }
                    else {
                        Log.e("Bmob", "登录失败，原因: ", e);
                    }
                }
            });
            finish();
        }

        // 注册
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                v.getContext().startActivity(intent);
            }
        });


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                MyUser userlogin = new MyUser();
                userlogin.setUsername(usernameEditText.getText().toString());
                userlogin.setPassword(passwordEditText.getText().toString());
                userlogin.login(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e == null) {
                            Log.v("Bmob", myUser.getNickname() + "登录成功");
                            // 邮箱验证
                            if (!myUser.getEmailVerified()) {
                                Toast.makeText(getApplicationContext(),"请至" + myUser.getEmail() + "邮箱中进行激活",Toast.LENGTH_LONG).show();
                                return;
                            }

                            // 删除前登陆用户的数据
                            String cardPicPath = Environment.getExternalStorageDirectory().getPath() + "/cardPic.jpg";
                            String userPicPath = Environment.getExternalStorageDirectory().getPath() + "/userPic.jpg";
                            String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.3gp";
                            String databasePath = "data/data/com.example.unforgettable/databases/MemoryCards.db";
                            File cardPicFile = new File(cardPicPath);
                            File audioFile = new File(audioPath);
                            File userPicFile = new File(userPicPath);
                            File databaseFile = new File(databasePath);
                            if (cardPicFile.exists()){
                                cardPicFile.delete();
                            }
                            if (audioFile.exists()){
                                audioFile.delete();
                            }
                            if (userPicFile.exists()){
                                userPicFile.delete();
                            }
                            if (databaseFile.exists()){
                                databaseFile.delete();
                                LitePal.getDatabase();
                                //动态创建数据库 避免SD卡删除数据库文件 造成的CRUD报错
                                LitePalDB litePalDB = LitePalDB.fromDefault("MemoryCards");
                                LitePal.use(litePalDB);
                            }

                            // 下载database
                            Bmobhelper bmobhelper = new Bmobhelper();
                            bmobhelper.download();  // 同步云端数据

                            // 登录
                            loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                        }
                        else {
                            Log.e("Bmob", "登录失败，原因: ", e);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            usernameEditText.setText("");
                            passwordEditText.setText("");
                        }
                    }
                });
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = "欢迎~" + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

        // 跳转界面
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
