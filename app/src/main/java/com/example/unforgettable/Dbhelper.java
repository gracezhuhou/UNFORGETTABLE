package com.example.unforgettable;

import android.util.Log;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;


// 用于数据库增删改查等操作

public class Dbhelper {
    public Dbhelper(){
        LitePal.getDatabase();
    }


    // 增加
    public MemoryCardsList addCard(String source, String author, String heading, String content, boolean like, String[] tab){
        //不可重复Heading
        if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0){
            return null;
        }

        MemoryCardsList card = new MemoryCardsList();
        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setLike(like);
        card.setTab(tab);
        card.save();

        Log.v("Dbhelper","Add Card");
        return card;
    }

    // 更新（修改）
    // 点入修改界面时，oldHeading=此时的标题，再跳转页面
    public boolean updateCard(String oldHeading, String source, String author, String heading, String content, boolean like, String[] tab){
        //检测重名,不可重复Heading
        if (!oldHeading.equals(heading))
            if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0)
                return false;

        MemoryCardsList card = findToDo(oldHeading);

        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setLike(like);
        card.setTab(tab);
        card.updateAll("heading = ?", oldHeading);
        return true;
    }

    // 删除
    public void deleteCard(String heading){
        LitePal.deleteAll(MemoryCardsList.class, "heading = ?", heading);
    }

    // 获取列表
    public List<MemoryCardsList> getCardList(){
        List<MemoryCardsList> cardList = LitePal.order("id").find(MemoryCardsList.class);
        return cardList;
    }

    // 根据heading查找唯一卡片
    public MemoryCardsList findToDo(String heading){
        List<MemoryCardsList> cardList = LitePal.where("heading = ?", heading).find(MemoryCardsList.class);
        MemoryCardsList card = cardList.get(0);
        return card;
    }

    // 获取应背列表
    public List<MemoryCardsList> getReciteCards() {
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate(), 23, 59, 59);
        List<MemoryCardsList> reciteCardList = LitePal.where("finish = ?", "false").order("reciteDate").find(MemoryCardsList.class);
//

        // TODO: 移去今日之后的card
        int size = reciteCardList.size();
        for (int i = 0; i < size; i++) {
            Date reciteDate = reciteCardList.get(i).getReciteDate();
            if (reciteCardList.get(i).getReciteDate().compareTo(today) == 1) {
                reciteCardList.remove(i);
                i--;
            }
        }
        return reciteCardList;
    }

    // 更改是否为收藏
    public void changeLike(String heading){
        MemoryCardsList card = findToDo(heading);
        boolean like = card.isLike();
        card.setLike(!like);
        card.updateAll("heading = ?", heading);
    }
}
