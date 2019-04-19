package com.example.unforgettable;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private BottomViewAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;

    private int lastIndex; //记录上一个fragment
    List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initBottomNavigation();
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
//        viewPager = (ViewPager) findViewById(R.id.vp);
//        //滑动
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (menuItem != null) {
//                    menuItem.setChecked(false);
//                } else {
//                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
//                }
//                menuItem = bottomNavigationView.getMenu().getItem(position);
//                menuItem.setChecked(true);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPagerAdapter = new BottomViewAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(viewPagerAdapter);
//        List<Fragment> list = new ArrayList<>();
//        list.add(ReviewActivity.getInstance());
//        list.add(ReviewActivity.getInstance());
//        list.add(RecordActivity.getInstance());
//        list.add(StatisticActivity.getInstance());
//        list.add(SetActivity.getInstance());
//        viewPagerAdapter.setList(list);

    }

    public void initBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.navigation);
        // 解决当item大于三个时，非平均布局问题
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_review:
                        setFragmentPosition(0);
                        break;
                    case R.id.navigation_list:
                        setFragmentPosition(1);
                        break;
                    case R.id.navigation_record:
                        setFragmentPosition(2);
                        break;
                    case R.id.navigation_statisitc:
                        setFragmentPosition(3);
                        break;
                    case R.id.navigation_personal:
                        setFragmentPosition(3);
                        break;
                    default:
                        break;
                }
                // 这里注意返回true,否则点击失效
                return true;
            }
        });
    }

//有滑动效果的viewpager
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            menuItem = item;
//            switch (item.getItemId()) {
//                case R.id.navigation_review:
//                    viewPager.setCurrentItem(0);
//                    return true;
//                case R.id.navigation_list:
//                    viewPager.setCurrentItem(1);
//                    return true;
//                case R.id.navigation_record:
//                    viewPager.setCurrentItem(2);
//                    return true;
//                case R.id.navigation_statisitc:
//                    viewPager.setCurrentItem(3);
//                    return true;
//                case R.id.navigation_personal:
//                    viewPager.setCurrentItem(4);
//                    return true;
//            }
//            return false;
//        }
//    };

    public void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new ReviewActivity());
        mFragments.add(new RecordActivity());
        mFragments.add(new RecordActivity());
        mFragments.add(new StatisticActivity());
        mFragments.add(new SetActivity());
        // 初始化展示MessageFragment
        setFragmentPosition(0);
    };

    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.vp, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }
}
