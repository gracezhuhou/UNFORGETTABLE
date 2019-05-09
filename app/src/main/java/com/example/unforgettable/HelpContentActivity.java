package com.example.unforgettable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class HelpContentActivity extends AppCompatActivity {
    private Button backButton;
    private Intent intent;
    private int Linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        Linear = intent.getIntExtra("Linear",0);

        if(Linear == 1){
            setContentView(R.layout.activity_help_content);
        } else if (Linear == 2) {
            setContentView(R.layout.activity_help_2);
        }
        backButton = findViewById(R.id.backButton);
        setListener();
    }

    void setListener(){

        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
