package com.example.unforgettable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unforgettable.ui.login.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    Button backButton;
    EditText nickNameInput;
    EditText passwordInput;
    EditText passwordInputAgain;
    EditText emailInput;
    Button emailButton;
    TextView authenticText;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // bmob初始化
        Bmob.initialize(this, "fff6417ec19cdbd68fa74e7d3860ad8c");

        backButton = findViewById(R.id.backButton);
        nickNameInput = findViewById(R.id.nickNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordInputAgain = findViewById(R.id.passwordInputAgain);
        emailInput = findViewById(R.id.emailInput);
        registerButton = findViewById(R.id.registerButton);

        setListener();
    }

    void setListener(){
        // 注册按钮
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.v("注册界面","注册按钮点击事件");
                String nickName = nickNameInput.getText().toString();
                // TODO: 密码格式
                String password = passwordInput.getText().toString();
                String passwordAgain = passwordInputAgain.getText().toString();
                String email = emailInput.getText().toString();

                // 两次密码输入不一致
                if (!password.equals(passwordAgain)) {
                    Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_LONG).show();
                    return;
                }

                // 新建bmob用户
                MyUser user =new MyUser();
                user.setUsername(email);
                user.setPassword(password);
                user.setEmail(email);
                user.setNickname(nickName);
                user.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser user, BmobException e) {
                        if(e == null)
                        {
                            Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                            finish();   // 返回
                        }
                        else
                        {
                            Log.e("注册失败", "原因: ",e );
                        }
                    }
                });

            }
        });
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
