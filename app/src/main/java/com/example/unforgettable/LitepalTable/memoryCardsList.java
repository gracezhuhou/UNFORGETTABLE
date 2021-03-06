package com.example.unforgettable.LitepalTable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.Date;


// 数据库 - 表名&列名
public class memoryCardsList extends LitePalSupport {

    private int id;
    private String source;  // 来源
    private String author;  // 作者
    private String heading; // 正面 标题
    private String content; // 背面 内容
    private boolean like;   // 收藏
    private String tab = "无标签";     // 标签
    private boolean finish = false; // 归档
    private int repeatTime = 0; // 重复次数
    private byte[] picture; // 图片
    //private boolean isAudio = false;    // 是否有录音

    // 背诵时间
    private Date recordDate = new Date(System.currentTimeMillis());
    private Date reciteDate;
    private int stage = 0;  // 背诵阶段

    //添加所有字段的getter和setter方法
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

    public String getTab() { return tab; }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getReciteDate() {
        return reciteDate;
    }

    public void setReciteDate(Date reciteDate) {
        this.reciteDate = reciteDate;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

//    public boolean isAudio() {
//        return isAudio;
//    }
//
//    public void setAudio(boolean audio) {
//        isAudio = audio;
//    }
}
