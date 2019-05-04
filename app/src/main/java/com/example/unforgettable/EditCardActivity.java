package com.example.unforgettable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.unforgettable.LitepalTable.memoryCardsList;

public class EditCardActivity extends AppCompatActivity {
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
    private Button backButton;
    private ImageView cardPic;

    // 数据库相关变量
    private Dbhelper dbhelper = new Dbhelper();
    private String oldheading; // 原标题
    private String source;  // 来源
    private String author;  // 作者
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like = false;   // 收藏
    private String tab;     // 标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcard);

        //设置id
        submitButton = findViewById(R.id.submitButton);
        sourceInput = findViewById(R.id.sourceInput);
        authorInput = findViewById(R.id.authorInput);
        headingInput = findViewById(R.id.headingInput);
        typeButton = findViewById(R.id.typeButton);
        cameraButton = findViewById(R.id.cameraButton);
        soundButton = findViewById(R.id.soundButton);
        starButton = findViewById(R.id.starButton);
        contentInput = findViewById(R.id.contentInput);
        backButton = findViewById(R.id.backButton);
        cardPic = findViewById(R.id.card_pic);

        init();     // 显示原卡片内容
        setListener();
    }

    // 初始化界面，显示原卡片内容
    public void init(){
        Intent intent = getIntent();
        oldheading = intent.getStringExtra("heading_extra");
        memoryCardsList card = dbhelper.findCard(oldheading);
        sourceInput.setText(card.getSource());
        authorInput.setText(card.getAuthor());
        headingInput.setText(oldheading);
        contentInput.setText(card.getContent());
        // TODO: 改按键颜色状态    @大冬瓜 @母后
        String starText = (String)starButton.getText();
        if (starText.equals("❤")) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            starButton.setCompoundDrawables(null, null, drawable, null);
            starButton.setText("已收藏"); //暂时
            starButton.setTextColor(Color.argb(0, 0, 255, 0));
            like = true;
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            starButton.setCompoundDrawables(null, null, drawable, null);
            starButton.setText("❤"); //暂时
            starButton.setTextColor(Color.argb(0, 0, 255, 0));
            like = false;
        }

        // 显示图片
        byte[] images = card.getPicture();
        if (images != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
            cardPic.setImageBitmap(bitmap);
        }

        // TODO: typeButton,cameraButton,soundButton 状态

        Log.v("卡片编辑界面","初始化页面完成");
    }

    // 事件响应
    public void setListener(){
        // 提交按钮监听
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput(); //获取用户输入内容

                // TODO: 选择标签（标签最多选择5个）
                tab = "计网"; //暂时

                dbhelper.updateCard(oldheading, source, author, heading, content, like, tab);  //添加记录
                Log.v("卡片编辑界面","提交按钮点击事件");
                finish();   //返回复习界面
            }
        });
        // 收藏按钮响应
        starButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO: 改收藏按键颜色状态  @大冬瓜 @母后
                String starText = (String)starButton.getText();
                if (starText.equals("❤")) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_yel);
                    // 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    starButton.setCompoundDrawables(null, null, drawable, null);
                    starButton.setText("已收藏"); //暂时
                    starButton.setTextColor(Color.argb(0, 0, 255, 0));
                    like = true;
                }
                else {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_star_black);
                    // 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    starButton.setCompoundDrawables(null, null, drawable, null);
                    starButton.setText("❤"); //暂时
                    starButton.setTextColor(Color.argb(0, 0, 255, 0));
                    like = false;
                }
                Log.v("卡片编辑界面","收藏按钮点击事件");
            }
        });
        // 返回按钮响应
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    //获取用户输入内容
    private void getInput(){
        source = sourceInput.getText().toString();
        author = authorInput.getText().toString();
        heading = headingInput.getText().toString();
        content = contentInput.getText().toString();
    }
}
