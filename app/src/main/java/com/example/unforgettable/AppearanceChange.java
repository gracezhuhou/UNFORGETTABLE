package com.example.unforgettable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AppearanceChange extends AppCompatActivity {
    Button backButton;
    LinearLayout sampleBase;
    LinearLayout sampleBlue;
    LinearLayout sampleGradient;
    LinearLayout samplePink;
    LinearLayout sampleCartoon;
    LinearLayout sampleGot;

    ImageView base;
    ImageView blue;
    ImageView gradient;
    ImageView got;
    ImageView pink;
    ImageView cartoon;


    SharedPreferences pref;

    //private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Drawable sample_base;

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
        sampleGot = findViewById(R.id.sample_got);
        base = findViewById(R.id.bg_base);
        blue = findViewById(R.id.bg_blue);
        pink = findViewById(R.id.bg_pink);
        got = findViewById(R.id.bg_got);
        gradient = findViewById(R.id.bg_gradient);
        cartoon = findViewById(R.id.bg_cartoon);

        base.setImageResource(R.drawable.sample_base);
        blue.setImageResource(R.drawable.sample_blue);
        pink.setImageResource(R.drawable.sample_pink);
        got.setImageResource(R.drawable.sample_got);
        gradient.setImageResource(R.drawable.sample_gradient);
        cartoon.setImageResource(R.drawable.sample_cartoon);

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
        sampleGot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = R.style.AppTheme_Base_GoT;
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
