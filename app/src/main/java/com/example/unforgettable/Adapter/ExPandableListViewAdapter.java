package com.example.unforgettable.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unforgettable.helpData.FatherData;

import java.util.ArrayList;

public class ExPandableListViewAdapter extends BaseExpandableListAdapter {
    // 定义一个Context
    private Context context;
    // 定义一个LayoutInflater
    private LayoutInflater mInflater;
    // 定义一个List来保存列表数据
    private ArrayList<FatherData> data_list = new ArrayList<>();

    // 定义一个构造方法
    public ExPandableListViewAdapter(Context context, ArrayList<FatherData> datas) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data_list = datas;
    }

    // 刷新数据
    public void flashData(ArrayList<FatherData> datas) {
        this.data_list = datas;
        this.notifyDataSetChanged();
    }

    // 获取二级列表的内容
    @Override
    public Object getChild(int arg0, int arg1) {
        return data_list.get(arg0).getList().get(arg1);
    }

    // 获取二级列表的ID
    @Override
    public long getChildId(int arg0, int arg1) {
        return arg1;
    }

    // 定义二级列表中的数据
    @Override
    public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
        // 定义一个二级列表的视图类
        HolderView childrenView;
        if (arg3 == null) {
            childrenView = new HolderView();
            // 获取子视图的布局文件
            arg3 = mInflater.inflate(com.example.unforgettable.R.layout.item_help_child, arg4, false);
            childrenView.contentView = arg3.findViewById(com.example.unforgettable.R.id.contentText);
            // 这个函数是用来将holderview设置标签,相当于缓存在view当中
            arg3.setTag(childrenView);
        } else {
            childrenView = (HolderView) arg3.getTag();
        }

        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        childrenView.contentView.setText(data_list.get(arg0).getList().get(arg1).getContent());

        Drawable drawable = data_list.get(arg0).getList().get(arg1).getPic();
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        childrenView.contentView.setCompoundDrawables(null,null,null, drawable);

//        // 设置副标题上的文本信息
//        childrenView.descView.setText(data_list.get(arg0).getList().get(arg1).getDesc());
        return arg3;
    }


    // 保存二级列表的视图类
    private class HolderView {
        TextView contentView;
    }

    // 获取二级列表的数量
    @Override
    public int getChildrenCount(int arg0) {
        return data_list.get(arg0).getList().size();
    }

    // 获取一级列表的数据
    @Override
    public Object getGroup(int arg0) {
        return data_list.get(arg0);
    }

    // 获取一级列表的个数
    @Override
    public int getGroupCount() {
        return data_list.size();
    }

    // 获取一级列表的ID
    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    // 设置一级列表的view
    @Override
    public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
        HodlerViewFather hodlerViewFather;
        if (arg2 == null) {
            hodlerViewFather = new HodlerViewFather();
            arg2 = mInflater.inflate(com.example.unforgettable.R.layout.item_help_father, arg3, false);
            hodlerViewFather.helpHeadText = arg2.findViewById(com.example.unforgettable.R.id.helpHeadText);
            // 新建一个TextView对象，用来显示一级标签上的大体描述的信息
//            hodlerViewFather.group_state = (ImageView) arg2.findViewById(R.id.group_state);
            arg2.setTag(hodlerViewFather);
        } else {
            hodlerViewFather = (HodlerViewFather) arg2.getTag();
        }
        // 一级列表右侧判断箭头显示方向
//        if (arg1) {
//            hodlerViewFather.group_state.setImageResource(R.drawable.group_down);
//        } else {
//            hodlerViewFather.group_state.setImageResource(R.drawable.group_up);
//        }
        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        hodlerViewFather.helpHeadText.setText(data_list.get(arg0).getTitle());

        // 返回一个布局对象
        return arg2;
    }

    // 定义一个 一级列表的view类
    private class HodlerViewFather {
        TextView helpHeadText;
//        ImageView group_state;
    }

    /**
     * 指定位置相应的组视图
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 当选择子节点的时候，调用该方法(点击二级列表)
     */
    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }




}
