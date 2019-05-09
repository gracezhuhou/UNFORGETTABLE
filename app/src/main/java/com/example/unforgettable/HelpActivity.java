package com.example.unforgettable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HelpActivity extends AppCompatActivity {
    private Button backButton;
    private RelativeLayout linear1;
    private RelativeLayout linear2;
    private RelativeLayout linear3;
    private RelativeLayout linear4;
    private RelativeLayout linear5;
    private RelativeLayout linear6;
    private RelativeLayout linear7;
    private RelativeLayout linear8;
    private RelativeLayout linear9;
    private RelativeLayout linear10;
    private RelativeLayout linear11;
    private RelativeLayout linear12;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        backButton = findViewById(R.id.backButton);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        linear3 = findViewById(R.id.linear3);
        linear4 = findViewById(R.id.linear4);
        linear7 = findViewById(R.id.linear7);
        linear5 = findViewById(R.id.linear5);
        linear6 = findViewById(R.id.linear6);
        linear8 = findViewById(R.id.linear8);
        linear9 = findViewById(R.id.linear9);
        linear10 = findViewById(R.id.linear10);
        linear11 = findViewById(R.id.linear11);
        linear12 = findViewById(R.id.linear12);

        intent = new Intent(this,HelpContentActivity.class);

        setListener();
    }

    void setListener(){
        linear1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",1);
                v.getContext().startActivity(intent);
            }
        });
        linear2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",2);
                v.getContext().startActivity(intent);
            }
        });
        linear3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",3);
                v.getContext().startActivity(intent);
            }
        });
        linear4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",4);
                v.getContext().startActivity(intent);
            }
        });
        linear5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",5);
                v.getContext().startActivity(intent);
            }
        });
        linear6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",6);
                v.getContext().startActivity(intent);
            }
        });
        linear7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",7);
            }
        });
        linear8.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",8);
            }
        });
        linear9.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",9);
            }
        });
        linear10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",10);
            }
        });
        linear11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",11);
            }
        });
        linear12.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent.putExtra("Linear",12);
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
