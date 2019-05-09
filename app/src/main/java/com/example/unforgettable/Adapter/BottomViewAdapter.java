package com.example.unforgettable.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BottomViewAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    public void setList(List<Fragment> list) {
        this.mFragmentList = list;
        notifyDataSetChanged();
    }

    public BottomViewAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        //return mFragmentList.size();
        return mFragmentList != null ? mFragmentList.size() : 0;
    }
}
