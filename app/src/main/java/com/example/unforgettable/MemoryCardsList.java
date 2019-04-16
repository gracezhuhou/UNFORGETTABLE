package com.example.unforgettable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class MemoryCardsList extends LitePalSupport {
    //运用注解来为字段添加index标签

    //name是唯一的，且默认值为unknown
    @Column(unique = true)
    private int id;

    //忽略即是不在数据库中创建该属性对应的字段
    @Column(ignore = true)
    private String source;  // 来源
    private String author;  // 作者

    //不为空
    @Column(nullable = false)
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like;   // 收藏
    private String[] tab;     // 标签, 最多选择5个
    private boolean finish = false; // 归档
    // TODO: 背诵时间

    //记得添加所有字段的getter和setter方法
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String[] getTab() {
        return tab;
    }

    public void setTab(String[] tab) {
        this.tab = tab;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}
