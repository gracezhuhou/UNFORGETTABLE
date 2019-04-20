package com.example.unforgettable;

import android.util.Log;

import org.litepal.LitePal;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


// 用于数据库增删改查等操作

public class Dbhelper {
    Dbhelper(){
        LitePal.getDatabase();
    }

    /*
    *
     记忆卡片
    *
    */

    // 增加
    MemoryCardsList addCard(String source, String author, String heading, String content, boolean like, String[] tab){
        //不可重复Heading
        if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0){
            return null;
        };

        if (heading.equals("") || content.equals(""))  return null;    // 不可为空

        MemoryCardsList card = new MemoryCardsList();
        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setLike(like);
        card.setTab(tab);
        Date today = new Date(System.currentTimeMillis());
        Date reciteDate = new Date(today.getYear(), today.getMonth(), today.getDate());
        card.setReciteDate(reciteDate);
        card.save();

        Log.v("数据库","添加卡片--" + heading);
        return card;
    }

    // 更新（修改）
    // 点入修改界面时，oldHeading=此时的标题，再跳转页面
    boolean updateCard(String oldHeading, String source, String author, String heading, String content, boolean like, String[] tab){
        //检测重名,不可重复Heading
        if (!oldHeading.equals(heading))
            if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0)
                return false;

        if (heading.equals("") || content.equals(""))  return false;    // 不可为空

        MemoryCardsList card = findCard(oldHeading);

        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setLike(like);
        card.setTab(tab);
        card.updateAll("heading = ?", oldHeading);
        Log.v("数据库","更改卡片--" + heading);
        return true;
    }

    // 删除
    void deleteCard(String heading){
        LitePal.deleteAll(MemoryCardsList.class, "heading = ?", heading);
        Log.v("数据库","删除卡片--" + heading);
    }

    // 获取列表
    List<MemoryCardsList> getCardList(){
        List<MemoryCardsList> cardList = LitePal.order("id").find(MemoryCardsList.class);
        Log.v("数据库","获取卡片列表" + cardList.size() + "张");
        return cardList;
    }

    // 根据heading查找唯一卡片
    MemoryCardsList findCard(String heading){
        List<MemoryCardsList> cardList = LitePal.where("heading = ?", heading).find(MemoryCardsList.class);
        MemoryCardsList card = cardList.get(0);
        return card;
    }

    // 获取应背列表
    List<MemoryCardsList> getReciteCards() {
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate(), 23, 59, 59);
        List<MemoryCardsList> reciteCardList = LitePal.where("finish = ?", "0").order("reciteDate").find(MemoryCardsList.class);

        for (int i = 0; i < reciteCardList.size(); i++) {
            Date reciteDate = reciteCardList.get(i).getReciteDate();
            if (reciteCardList.get(i).getReciteDate().compareTo(today) == 1) {
                reciteCardList.remove(i);
                i--;
            }
        }
        Log.v("数据库","获取今日应背卡片" + reciteCardList.size()+"张");
        return reciteCardList;
    }

    // 更改是否为收藏
    boolean changeLike(String heading){
        MemoryCardsList card = findCard(heading);
        boolean like = !card.isLike();
        if (like) {
            card.setLike(like);
            Log.v("数据库","收藏卡片" + heading);
        }
        else {
            card.setToDefault("like");
            Log.v("数据库","取消收藏卡片" + heading);
        }
        card.updateAll("heading = ?", heading);
        return like;
    }

    // 更新下一次背诵时间
    void updateReciteDate(String heading, boolean isPass) {
        MemoryCardsList card = findCard(heading);
        int stage = card.getStage();

        // 记住单词
        if (isPass){
            // 设定下次背诵时间
            Calendar date = Calendar.getInstance();
            date.setTime(card.getReciteDate());

            switch (stage) {
                case 0:
                    date.add(Calendar.DATE, 1);
                    break;
                case 1:
                    date.add(Calendar.DATE, 2);
                    break;
                case 2:
                    date.add(Calendar.DATE, 4);
                    break;
                case 3:
                    date.add(Calendar.DATE, 7);
                    break;
                case 4:
                    date.add(Calendar.DATE, 15);
                    break;
                case 5:
                    date.add(Calendar.MONTH, 1);
                    break;
                case 6:
                    date.add(Calendar.MONTH, 3);
                    break;
                case 7:
                    date.add(Calendar.MONTH, 6);
                    break;
                case 8:
                    date.add(Calendar.YEAR, 1);
                    break;
                // TODO: 背完
            }

            // 更新数据库
            Date reciteDate = new Date(date.getTime().getYear(), date.getTime().getMonth(), date.getTime().getDate());
            card.setReciteDate(reciteDate);
            card.setStage(stage + 1);
            card.updateAll("heading = ?", heading);
            Log.v("数据库","更新已记住卡片的背诵时间至" + reciteDate);
        }
        // 忘记单词
        else {
            // 更新数据库
            Date reciteDate = new Date(System.currentTimeMillis());
            card.setReciteDate(reciteDate);
            card.setStage(0);
            card.updateAll("heading = ?", heading);
            Log.v("数据库","更新未记住卡片的背诵时间至" + reciteDate);
        }
    }

    // 更新归档单词
    void finishCard(String heading) {
        MemoryCardsList card = findCard(heading);
        card.setFinish(true);
        card.updateAll("heading = ?", heading);
        Log.v("数据库","归档卡片--" + heading);

        // TODO: 归档的撤销
    }
}
