package com.example.unforgettable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    // 前端相关变量
    private Button submitButton;
    private EditText sourceInput;
    private EditText authorInput;
    private EditText headingInput;
    private Button typeButton;
    private Button cameraButton;
    private Button soundButton;
    private Button starButton;
    private EditText contentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //设置id
        submitButton = (Button)findViewById(R.id.submitButton);
        sourceInput = (EditText)findViewById(R.id.sourceInput);
        authorInput = (EditText)findViewById(R.id.authorInput);
        headingInput = (EditText)findViewById(R.id.headingInput);
        typeButton = (Button) findViewById(R.id.typeButton);
        cameraButton = (Button)findViewById(R.id.cameraButton);
        soundButton = (Button)findViewById(R.id.soundButton);
        starButton = (Button)findViewById(R.id.starButton);
        contentInput = (EditText)findViewById(R.id.contentInput);

        init();
    }

    public void init(){

    }
}
