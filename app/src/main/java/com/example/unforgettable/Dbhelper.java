package com.example.unforgettable;

import org.litepal.LitePal;

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

        return card;
    }

    //更新（修改）
    //点入修改界面时，oldHeading=此时的标题，再跳转页面
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

    //删除
    public void deleteCard(String heading){
        LitePal.deleteAll(MemoryCardsList.class, "heading = ?", heading);
    }

    //获取列表
    public List<MemoryCardsList> getCardList(){
        List<MemoryCardsList> cardList = LitePal.order("id").find(MemoryCardsList.class);
        return cardList;
    }

    //查找
    public MemoryCardsList findToDo(String heading){
        List<MemoryCardsList> cardList = LitePal.where("heading = ?", heading).find(MemoryCardsList.class);
        MemoryCardsList card = cardList.get(0);
        return card;
    }
}
