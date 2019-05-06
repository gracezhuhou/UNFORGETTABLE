package com.example.unforgettable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.unforgettable.Bmob.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class AppearanceChange extends AppCompatActivity {
    Button backButton;
    LinearLayout sampleBase;
    LinearLayout sampleBlue;
    LinearLayout sampleGradient;
    LinearLayout samplePink;
    LinearLayout sampleCartoon;

    SharedPreferences pref;

    //private SharedPreferences pref;
    private SharedPreferences.Editor editor;

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

        setContentView(R.layout.activity_appearance_change);

        backButton = findViewById(R.id.backButton);

        sampleBase = findViewById(R.id.sample_base);
        sampleBlue = findViewById(R.id.sample_blue);
        sampleGradient = findViewById(R.id.sample_gradient);
        samplePink = findViewById(R.id.sample_pink);
        sampleCartoon = findViewById(R.id.sample_cartoon);

        setListener();

        editor = getSharedPreferences("Alert", MODE_PRIVATE).edit();
        editor.apply();
    }

    void setListener(){
        sampleBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_Base;
                editor.putInt("background", mode);
                editor.apply();

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("para", 1);
                v.getContext().startActivity(intent);
                finish();
            }
        });
        sampleBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_Blue;
                editor.putInt("background", mode);
                editor.apply();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("para", 1);
                v.getContext().startActivity(intent);
                finish();
            }
        });
        sampleGradient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_Gradient;
                editor.putInt("background", mode);
                editor.apply();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("para", 1);
                v.getContext().startActivity(intent);
                finish();
            }
        });
        samplePink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_Pink;
                editor.putInt("background", mode);
                editor.apply();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("para", 1);
                v.getContext().startActivity(intent);
                finish();
            }
        });
        sampleCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_Cartoon;
                editor.putInt("background", mode);
                editor.apply();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("para", 1);
                v.getContext().startActivity(intent);
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
