package com.example.unforgettable;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unforgettable.Bmob.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetActivity extends AppCompatActivity {
    SharedPreferences pref;

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
        setContentView(R.layout.activity_forget);

        final EditText emailInput = findViewById(R.id.emailInput);
        final Button resetButton = findViewById(R.id.resetButton);


        // 注册
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String email = emailInput.getText().toString();

                /**
                 * 邮箱重置密码
                 */
                MyUser.resetPasswordByEmail(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            String str = "重置密码请求成功，请到" + email + "邮箱进行密码重置操作";
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Log.e("BMOB", e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
