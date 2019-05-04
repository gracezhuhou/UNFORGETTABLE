package com.example.unforgettable;

import android.util.Log;

import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.example.unforgettable.LitepalTable.stageList;
import com.example.unforgettable.LitepalTable.statusSumList;
import com.example.unforgettable.LitepalTable.tabList;
import com.example.unforgettable.LitepalTable.todayCardsList;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


// 用于数据库增删改查等操作

public class Dbhelper {
    Dbhelper(){
        LitePal.getDatabase();

//        for (int i = 0; i < 22; ++i) {
//            statusSumList statusRow = new statusSumList();
//            statusRow.setSpan(i);
//            statusRow.save();
//        }
//        for (int i = 1; i < 4; ++i) {
//            statusSumList statusRow = new statusSumList();
//            statusRow.setSpan(i * 30);
//            statusRow.save();
//        }
//        for (int i = 1; i < 3; ++i) {
//            statusSumList statusRow = new statusSumList();
//            statusRow.setSpan(i * 180);
//            statusRow.save();
//        }
    }

    /*
    *
     记忆卡片 memoryCardsList
    *
    */

    // 增加
    boolean addCard(String source, String author, String heading, String content, boolean like, String tab){
        // 不可重复Heading
        if (LitePal.where("heading = ?", heading).find(memoryCardsList.class).size() != 0){
            return false;
        };
        // Heading不可为空
        if (heading.equals("") || content.equals(""))  return false;

        memoryCardsList card = new memoryCardsList();
        card.setSource(source);
        card.setAuthor(author);
        card.setHeading(heading);
        card.setContent(content);
        card.setLike(like);
        card.setTab(tab);
        card.setReciteDate(today());
        card.save();

        // 更新stageList
        addNewStage(tab);

        Log.v("数据库","添加卡片--" + heading);
        return true;
    }

    // 更新（修改）
    boolean updateCard(String oldHeading, String source, String author, String heading, String content, boolean like, String tab){
        //检测重名,不可重复Heading
        if (!oldHeading.equals(heading))
            if (LitePal.where("heading = ?", heading).find(memoryCardsList.class).size() != 0)
                return false;
        // 不可为空
        if (heading.equals("") || content.equals(""))  return false;

        memoryCardsList card = findCard(oldHeading);

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
        LitePal.deleteAll(memoryCardsList.class, "heading = ?", heading);
        Log.v("数据库","删除卡片--" + heading);
    }

    // 获取列表
    List<memoryCardsList> getCardList(){
        List<memoryCardsList> cardList = LitePal.order("id").find(memoryCardsList.class);
        Log.v("数据库","获取卡片列表" + cardList.size() + "张");
        return cardList;
    }

    // 根据heading查找唯一卡片
    memoryCardsList findCard(String heading){
        List<memoryCardsList> cardList = LitePal.where("heading = ?", heading).find(memoryCardsList.class);
        memoryCardsList card = cardList.get(0);
        return card;
    }

    // 获取应背列表
    List<memoryCardsList> getReciteCards() {
        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate(), 23, 59, 59);
        List<memoryCardsList> reciteCardList = LitePal.where("finish = ?", "0").order("reciteDate").find(memoryCardsList.class);

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
    List<memoryCardsList> getReciteTabCards(String tabName) {
        if (tabName.equals("全部")) return getReciteCards();

        Date current = new Date(System.currentTimeMillis());
        Date today = new Date(current.getYear(), current.getMonth(), current.getDate(), 23, 59, 59);
        List<memoryCardsList> reciteCardList = LitePal.where("finish = ?", "0").order("reciteDate").find(memoryCardsList.class);

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
        memoryCardsList card = findCard(heading);
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

    // 重复次数+1
    void addRepeatTime(String heading) {
        memoryCardsList card = findCard(heading);
        int repeatTime = card.getRepeatTime();
        card.setRepeatTime(repeatTime + 1);
        card.updateAll("heading = ?", heading);
    }

    // 点击记住1 /模糊0 /遗忘-1 后
    // 更新下一次背诵时间
    void updateReciteDate(String heading, int pass) {
        memoryCardsList card = findCard(heading);
        int stage = card.getStage();

        // 记忆卡片《重复次数》列的值+1
        addRepeatTime(heading);

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

            // stage的变化 更新至StageList
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
        memoryCardsList card = findCard(heading);
        card.setFinish(true);
        card.updateAll("heading = ?", heading);
        Log.v("数据库","归档卡片--" + heading);

        // TODO: 归档的撤销
    }

    /*
    *
     标签 tabList
    *
    */
    // 添加
    void addTab(String tabName) {
        //不可重复
        if (LitePal.where("tabName = ?", tabName).find(tabList.class).size() != 0){
            return;
        };
        if (tabName.equals(""))  return;    // 不可为空

        tabList tab = new tabList();
        tab.setTabName(tabName);
        tab.save();
        Log.v("数据库","添加标签--" + tabName);
    }

    // 获取标签列表
    List<tabList> getTabList(){
        List<tabList> tabList = LitePal.order("id").find(tabList.class);
        Log.v("数据库","获取标签" + tabList.size() + "个");
        return tabList;
    }

    /*****************
    *
     状态统计 stageList
    *
    ******************/
    // 添加
    void addStageList(){
        //不可重复日期创建
        //Date current = new Date(System.currentTimeMillis());
        Date today = today();
        List<stageList> stageList = LitePal.findAll(stageList.class);
        int size = stageList.size();
        for (int i = 0; i < size; ++i){
            if (stageList.get(i).getDate().compareTo(today) == 0){
                return;
            };
        }

        List<tabList> tabList = getTabList();
        size = tabList.size();
        for (int i = 0; i< size; ++i){
            String tab = tabList.get(i).getTabName();
            stageList stageRow = new stageList();
            stageRow.setDate(today);
            stageRow.setTab(tab);
            int[] stageSum = getStageSum(tab);
            stageRow.setStage0(stageSum[0]);
            stageRow.setStage1(stageSum[1]);
            stageRow.setStage2(stageSum[2]);
            stageRow.setStage3(stageSum[3]);
            stageRow.setStage4(stageSum[4]);
            stageRow.setStage5(stageSum[5]);
            stageRow.setStage6(stageSum[6]);
            stageRow.setStage7(stageSum[7]);
            stageRow.setStage8(stageSum[8]);
            stageRow.setStage9(stageSum[9]);

            stageRow.save();
        }
        Log.v("数据库","添加统计状态行");
    }

    // 新建记忆卡片时增加stage0
    void addNewStage(String tab) {
        List<stageList> stageList =  LitePal.where("tab = ?", tab).find(stageList.class);
        Date today = today();
        for (int i = 0; i < stageList.size(); i++) {
            stageList todayStage = stageList.get(i);
            if (todayStage.getDate().compareTo(today) == 0) {
                int stageSum = todayStage.getStage0() + 1;
                todayStage.setStage0(stageSum);
                todayStage.updateAll("id = ?", Integer.toString(todayStage.getId()));
            }
        }
    }

    // 更新某类别的状态更改
    private void updateStageSum (int stage, int newStage, String tab) {
        //Date current = new Date(System.currentTimeMillis());
        Date today = today();

        List<stageList> stageList =  LitePal.where("tab = ?", tab).find(stageList.class);
        for (int i = 0; i < stageList.size(); i++) {
            stageList todayStage = stageList.get(i);
            if (todayStage.getDate().compareTo(today) == 0) {
               // if (tab.equals(todayStage.getTab())) {
                int stageSum = todayStage.getStage(stage) - 1;
                int newStageSum = todayStage.getStage(newStage) + 1;
                todayStage.setStage(stage,stageSum);
                todayStage.setStage(newStage,newStageSum);
                todayStage.updateAll("id = ?", Integer.toString(todayStage.getId()));
               // }
            }
        }
    }

    // 更新记忆状态Status 记住/模糊/忘记
    private void updateMemoryStatus(String tab, int status) {
        //Date current = new Date(System.currentTimeMillis());
        Date today = today();

        List<stageList> stageList = getStageList();
        for (int i = 0; i < stageList.size(); i++) {
            stageList todayStage = stageList.get(i);
            if (todayStage.getDate().compareTo(today) == 0) {
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
                    todayStage.updateAll("id = ?", Integer.toString(todayStage.getId()));
                }
            }
        }
    }

    // 获取stage统计列表
    List<stageList> getStageList(){
        List<stageList> stageList = LitePal.order("id").find(stageList.class);
        Log.v("数据库","获取阶段列表" + stageList.size() + "个");
        return stageList;
    }


    // 某一标签的状态和
    private int[] getStageSum(String tabName){
        int[] stageSum = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        List<memoryCardsList> cardList = getCardList();
        int size = cardList.size();
        for (int i = 0; i < size; ++i) {
            String tab = cardList.get(i).getTab();
            if (tab == null) continue;

            if (tabName.equals(tab))
                stageSum[cardList.get(i).getStage()]++;
        }
        return stageSum;
    }


    /********************
    *
    todayCardsList
     *
     ********************/

    // 设置初次背诵状态
    // 并更新至StageList & statusSumList
    void setReciteStatus(String heading, int status) {
        //不可重复卡片
        if (LitePal.where("heading = ?", heading).find(todayCardsList.class).size() != 0){
            return;
        }

        // 新建今日卡片
        todayCardsList todayCard = new todayCardsList();
        todayCard.setHeading(heading);
        todayCard.setFirstReciteStatus(status);
        //Date today = new Date(System.currentTimeMillis());
        //Date date = new Date(today.getYear(), today.getMonth(), today.getDate());
        todayCard.setDate(today());
        todayCard.save();

        Log.v("数据库","添加初次背诵状态");

        memoryCardsList card = findCard(heading);
        // 更新至StageList
        updateMemoryStatus(card.getTab(), status);

        // 更新至StatusSumList
        updateStatuSumList(heading, status);
    }

    // 获取今日卡片列表
    List<todayCardsList> getTodayCardsList(){
        List<todayCardsList> todayCardsList = LitePal.findAll(com.example.unforgettable.LitepalTable.todayCardsList.class);
        Log.v("数据库","获取今日卡片" + todayCardsList.size() + "个");
        return todayCardsList;
    }

    // 删去todayCardsList中之前日期的卡片
    void deleteOldDayCards() {
        List<todayCardsList> todayCardsList = getTodayCardsList();
        int size = todayCardsList.size();

        for (int i = 0; i < size; ++i) {
            todayCardsList todayCard = todayCardsList.get(i);
            if (todayCard.getDate().compareTo(today()) == -1) {
                deleteTodayCard(todayCard.getHeading());
            }
        }
    }

    // 删除今日卡片
    void deleteTodayCard(String heading){
        LitePal.deleteAll(todayCardsList.class, "heading = ?", heading);
        Log.v("数据库todayCardsList","删除过期卡片--" + heading);
    }


    /************************
     *
     * statusSumList
     *
     * ************************/

    // 根据span查找唯一行
    statusSumList findStatusRow(int span){
        List<statusSumList> statusSumList = LitePal.where("span = ?", Integer.toString(span)).find(statusSumList.class);
        if (statusSumList.size() == 0)  return null;
        //statusSumList statusRow = statusSumList.get(0);
        return statusSumList.get(0);
    }

    // 更新statuSumList
    void updateStatuSumList(String heading, int status) {
        memoryCardsList card = findCard(heading);
        Date recordDate = card.getRecordDate();
        //Date today = new Date(System.currentTimeMillis());
        // 计算时间差
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(recordDate);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(today());
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        int span = (day2 - day1);

        statusSumList statusRow = findStatusRow(span);
        if (statusRow != null) {
            switch (status){
                case 1:{
                    int rememberSum = statusRow.getRememberSum();
                    statusRow.setRememberSum(rememberSum + 1);
                    break;
                }
                case -1:{
                    int forgetSum = statusRow.getForgetSum();
                    statusRow.setForgetSum(forgetSum + 1);
                    break;
                }
                case 0: {
                    int dimSum = statusRow.getDimSum();
                    statusRow.setDimSum(dimSum + 1);
                }
            }
            statusRow.updateAll("span = ?", Integer.toString(span));
        }
    }


    // 获取今天年月日
    private Date today() {
        Date today = new Date(System.currentTimeMillis());
        return new Date(today.getYear(), today.getMonth(), today.getDate());
    }
}
