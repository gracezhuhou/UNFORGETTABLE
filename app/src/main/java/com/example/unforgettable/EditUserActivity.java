package com.example.unforgettable;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unforgettable.Bmob.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditUserActivity extends AppCompatActivity {

    Button backButton;
    EditText nickNameInput;
    EditText passwordInput;
    EditText newPasswordInput;
    EditText passwordInputAgain;
    TextView emailText;
    Button submitButton;
    SharedPreferences pref;

    final MyUser myuser = MyUser.getCurrentUser(MyUser.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("Alert", MODE_PRIVATE);
        int mode = pref.getInt("background", -1);
        if (mode == -1) {
            setTheme(R.style.AppTheme_Base_Base);
        }
        else {
            setTheme(mode);
        }
        setContentView(R.layout.activity_edit_user);

        backButton = findViewById(R.id.backButton);
        nickNameInput = findViewById(R.id.nickNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        passwordInputAgain = findViewById(R.id.passwordInputAgain);
        emailText = findViewById(R.id.emailText);
        submitButton = findViewById(R.id.submitButton);

        initText();
        setListener();
    }

    void initText() {
        nickNameInput.setText(myuser.getNickname());
        emailText.setText(myuser.getEmail());
    }

    void setListener(){
        // 注册按钮
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String nickName = nickNameInput.getText().toString();
                // TODO: 密码格式
                String oldPassword = passwordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String passwordAgain = passwordInputAgain.getText().toString();

                // 两次密码输入不一致
                if (!newPassword.equals(passwordAgain)) {
                    Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_LONG).show();
                    passwordInput.setText("");
                    newPasswordInput.setText("");
                    passwordInputAgain.setText("");
                    return;
                }

                /*
                 * 更新用户操作并同步更新本地的用户信息
                 */
                if (!myuser.getNickname().equals(nickName)) {
                    myuser.setNickname(nickName);
                    myuser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "用户名更新成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "用户名更新失败", Toast.LENGTH_LONG).show();
                                Log.e("error", e.getMessage());
                            }
                        }
                    });
                }

                /*
                 * 提供旧密码修改密码
                 */
                MyUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "密码更新成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "密码更新失败"+ e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                finish();
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
