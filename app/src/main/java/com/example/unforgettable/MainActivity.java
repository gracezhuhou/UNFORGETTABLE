package com.example.unforgettable;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.unforgettable.Bmob.MyUser;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private BottomViewAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;

    private int lastIndex; //记录上一个fragment
    List<Fragment> mFragments;

    private int hour;
    private int minute;
    private Calendar instance;
    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pi;
    private String temp;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bmob初始化
        Bmob.initialize(this, "fff6417ec19cdbd68fa74e7d3860ad8c");


        initData();
        initBottomNavigation();

        if(!temp.equals("不提醒")) {
            alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            instance = Calendar.getInstance();
            instance.set(Calendar.HOUR_OF_DAY, hour);
            instance.set(Calendar.MINUTE, minute);
            instance.set(Calendar.SECOND, 0);

            intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("NOTIFICATION");
            pi = PendingIntent.getBroadcast(this, 0, intent, 0);

            int type = AlarmManager.RTC_WAKEUP;
            alarmManager.set(type, instance.getTimeInMillis(), pi);
        }

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
                        setFragmentPosition(4);
                        break;
                    default:
                        break;
                }
                // 这里注意返回true,否则点击失效
                return true;
            }
        });
    }

//    public void setTime() {
//        TODO:temp=time
//        if(!temp.equals("不提醒")) {
//            String[] ptr = str.split(":");
//            hour = Integer.parseInt(ptr[0]);
//            minute = Integer.parseInt(ptr[1]);
//        }
//    }

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
