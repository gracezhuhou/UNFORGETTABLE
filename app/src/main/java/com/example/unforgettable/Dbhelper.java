package com.example.unforgettable;

import android.support.design.widget.TabLayout;
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
     记忆卡片 MemoryCardsList
    *
    */

    // 增加
    boolean addCard(String source, String author, String heading, String content, boolean like, String tab){
        // 不可重复Heading
        if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0){
            return false;
        };
        // Heading不可为空
        if (heading.equals("") || content.equals(""))  return false;

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
        return true;
    }

    // 更新（修改）
    // 点入修改界面时，oldHeading=此时的标题，再跳转页面
    boolean updateCard(String oldHeading, String source, String author, String heading, String content, boolean like, String tab){
        //检测重名,不可重复Heading
        if (!oldHeading.equals(heading))
            if (LitePal.where("heading = ?", heading).find(MemoryCardsList.class).size() != 0)
                return false;
        // 不可为空
        if (heading.equals("") || content.equals(""))  return false;

        MemoryCardsList card = findCard(oldHeading);

        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setTab(tab);
        if (like)
            card.setLike(like);
        else
            card.setToDefault("like");
        card.updateAll("heading = ?", oldHeading);
        Log.v("数据库","更改卡片--" + heading);
        return true;
    }

    // 删除卡片
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
            if (reciteCardList.get(i).getReciteDate().compareTo(today) == 1) {
                reciteCardList.remove(i);
                i--;
            }
        }
        Log.v("数据库","获取今日应背卡片" + reciteCardList.size()+"张");
        return reciteCardList;
    }

    // 获取某一标签应背列表
    List<MemoryCardsList> getReciteTabCards(String tabName) {
        if (tabName.equals("全部")) return getReciteCards();

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate(), 23, 59, 59);
        List<MemoryCardsList> reciteCardList = LitePal.where("finish = ?", "0").order("reciteDate").find(MemoryCardsList.class);

        for (int i = 0; i < reciteCardList.size(); i++) {
            // 日期
            if (reciteCardList.get(i).getReciteDate().compareTo(today) == 1) {
                reciteCardList.remove(i);
                i--;
                continue;
            }
            // 标签
            String tab = reciteCardList.get(i).getTab();
            if (tab == null || !tab.equals(tabName)){
                reciteCardList.remove(i);
                i--;
            }
        }
        Log.v("数据库","获取今日" + tabName + "应背卡片" + reciteCardList.size()+"张");
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
    void updateReciteDate(String heading, int pass) {
        MemoryCardsList card = findCard(heading);
        int stage = card.getStage();

        // 记住单词
        if (pass == 1){
            // 设定下次背诵时间
            Date current = new Date(System.currentTimeMillis());
            Date today = new Date(current.getYear(), current.getMonth(), current.getDate());
            Calendar date = Calendar.getInstance();
            date.setTime(today);

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
                // TODO: 背完--归档
                default:
                    card.setFinish(true);
            }

            // 更新数据库
            Date reciteDate = new Date(date.getTime().getYear(), date.getTime().getMonth(), date.getTime().getDate());
            card.setReciteDate(reciteDate);
            card.setStage(stage + 1);
            card.updateAll("heading = ?", heading);
            Log.v("数据库","更新已记住卡片的背诵时间至" + reciteDate);

            // 更新至StageList
            updateStageSum(stage, stage + 1, card.getTab());
        }
        // 忘记单词
        else if (pass == -1){
            // 更新数据库
            Date reciteDate = new Date(System.currentTimeMillis());
            card.setReciteDate(reciteDate);
            card.setStage(0);
            card.updateAll("heading = ?", heading);
            Log.v("数据库","更新未记住卡片的背诵时间至" + reciteDate);

            // 更新至StageList
            updateStageSum(stage, 0, card.getTab());
        }
        // 模糊
        else {
            // 更新数据库
            Date reciteDate = new Date(System.currentTimeMillis());
            card.setReciteDate(reciteDate);
            if (stage == 0) {
                card.setStage(0);
                updateStageSum(stage, 0, card.getTab());    // 更新至StageList
            }
            else {
                card.setStage(stage - 1);
                updateStageSum(stage, stage - 1, card.getTab());    // 更新至StageList
            }
            card.updateAll("heading = ?", heading);
            Log.v("数据库","更新模糊卡片的背诵时间至" + reciteDate);
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

    /*
    *
     标签 TabList
    *
    */
    // 添加
    void addTab(String tabName) {
        //不可重复
        if (LitePal.where("tabName = ?", tabName).find(TabList.class).size() != 0){
            return;
        };
        if (tabName.equals(""))  return;    // 不可为空

        TabList tab = new TabList();
        tab.setTabName(tabName);
        tab.save();
        Log.v("数据库","添加标签--" + tabName);
    }

    // 获取标签列表
    List<TabList> getTabList(){
        List<TabList> tabList = LitePal.order("id").find(TabList.class);
        Log.v("数据库","获取标签" + tabList.size() + "个");
        return tabList;
    }

    /*
    *
     状态统计 StageList
    *
    */
    // 添加
    void addStageList(){
        //不可重复日期创建
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());
        List<StageList> stageList = LitePal.findAll(StageList.class);
        int size = stageList.size();
        for (int i = 0; i < size; ++i){
            if (stageList.get(i).getDate().compareTo(today) == 0){
                return;
            };
        }

        List<TabList> tabList = getTabList();
        size = tabList.size();
        for (int i = 0; i< size; ++i){
            String tab = tabList.get(i).getTabName();
            StageList stageRow = new StageList();
            stageRow.setDate(today);
            stageRow.setTab(tab);
            int[] stageSum = getStageSum(tab);
            stageRow.setStage0(stageSum[0]);
            stageRow.setStage0(stageSum[1]);
            stageRow.setStage0(stageSum[2]);
            stageRow.setStage0(stageSum[3]);
            stageRow.setStage0(stageSum[4]);
            stageRow.setStage0(stageSum[5]);
            stageRow.setStage0(stageSum[6]);
            stageRow.setStage0(stageSum[7]);
            stageRow.setStage0(stageSum[8]);
            stageRow.setStage0(stageSum[9]);

            stageRow.save();
        }

        Log.v("数据库","添加统计状态行");
    }

    // 更新某类别的状态更改
    private void updateStageSum (int stage, int newStage, String tab) {
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        List<StageList> stageList = getStageList();
        for (int i = 0; i < stageList.size(); i++) {
            StageList todayStage = stageList.get(i);
            if (todayStage.getDate().compareTo(today) == 1) {
                if (tab.equals(todayStage.getTab())) {
                    int stageSum = todayStage.getStage(stage) - 1;
                    int newStageSum = todayStage.getStage(newStage) + 1;
                    todayStage.setStage(stage,stageSum);
                    todayStage.setStage(newStage,newStageSum);
                    todayStage.save();
                }
            }
        }
    }

    private void updateMemoryStatus(String tab, int status) {
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate());

        List<StageList> stageList = getStageList();
        for (int i = 0; i < stageList.size(); i++) {
            StageList todayStage = stageList.get(i);
            if (todayStage.getDate().compareTo(today) > 0) {
                if (tab.equals(todayStage.getTab())) {
                    switch (status){
                        case 1:
                            todayStage.setRemember(todayStage.getRemember() + 1);
                            break;
                        case -1:
                            todayStage.setForget(todayStage.getForget() + 1);
                            break;
                        case 0:
                            todayStage.setDim(todayStage.getDim() + 1);
                    }
                    todayStage.save();
                }
            }
        }
    }

    // 获取stage统计列表
    List<StageList> getStageList(){
        List<StageList> stageList = LitePal.order("id").find(StageList.class);
        Log.v("数据库","获取阶段列表" + stageList.size() + "个");
        return stageList;
    }


    // 某一标签的状态和
    private int[] getStageSum(String tabName){
        int[] stageSum = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        List<MemoryCardsList> cardList = getCardList();
        int size = cardList.size();
        for (int i = 0; i < size; ++i) {
            String tab = cardList.get(i).getTab();
            if (tab == null) continue;

            if (tabName.equals(tab))
                stageSum[cardList.get(i).getStage()]++;
        }
        return stageSum;
    }


    /*
    *
    TodayCardsList
     *
     */

    // 设置初次背诵状态 并更新至StageList
    void setReciteStatus(String heading, int status) {
        //不可重复Heading
        if (LitePal.where("heading = ?", heading).find(TodayCardsList.class).size() != 0){
            return;
        }

        TodayCardsList todayCard = new TodayCardsList();
        todayCard.setHeading(heading);
        todayCard.setFirstReciteStatus(status);
        todayCard.save();

        Log.v("数据库","添加初次背诵状态");

        // 更新至StageList
        updateMemoryStatus(findCard(heading).getTab(), status);
    }
}
