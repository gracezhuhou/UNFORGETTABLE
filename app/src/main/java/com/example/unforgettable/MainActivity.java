package com.example.unforgettable;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.example.unforgettable.Adapter.BottomViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
    private String temp;
    private SharedPreferences pref;
    private int para;
    private int height;
    private int bn_height;
    private int shortof;

    private int position;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("Alert", MODE_PRIVATE);
        int mode = pref.getInt("background", -1);
        if (mode == -1) {
            setTheme(R.style.AppTheme_Base_Base);
        }
        else {
            setTheme(mode);
        }
        setContentView(R.layout.activity_main);

        // bmob初始化
        Bmob.initialize(this, "fff6417ec19cdbd68fa74e7d3860ad8c");


        Intent intent2 = getIntent();
        para = intent2.getIntExtra("para",0);

        //获取屏幕尺寸
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;

        initData();
        initBottomNavigation();

        shortof = height - bn_height;
        setTime();

        if(!temp.equals("")) {
            Calendar instance = Calendar.getInstance();
            //是设置日历的时间，主要是让日历的年月日和当前同步
            instance.setTimeInMillis(System.currentTimeMillis());
            // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
            instance.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            long systemTime = System.currentTimeMillis();
            instance.set(Calendar.HOUR_OF_DAY, hour);
            instance.set(Calendar.MINUTE, minute);
            instance.set(Calendar.SECOND, 0);
            instance.set(Calendar.MILLISECOND, 0);
            long selectTime = instance.getTimeInMillis();

            // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
            if(systemTime > selectTime) {
                instance.add(Calendar.DAY_OF_MONTH, 1);
            }

            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("NOTIFICATION");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

            int type = AlarmManager.RTC_WAKEUP;
            alarmManager.set(type, instance.getTimeInMillis(), pi);
        }

        // 获取权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CHANGE_NETWORK_STATE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
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
        // 初始化展示MessageFragment
        if(para == 0) {
            position = 0;
        } else if(para == 1) {
            position = 4;
        }
        mBottomNavigationView.setSelectedItemId(mBottomNavigationView.getMenu().getItem(position).getItemId());
        position = 0;
    }

    public void setTime() {
        pref = getSharedPreferences("Alert", MODE_PRIVATE);
        temp = pref.getString("alertTime", "");
        if(!temp.equals("")) {
            String[] ptr = temp.split(":");
            hour = Integer.parseInt(ptr[0]);
            minute = Integer.parseInt(ptr[1]);
        }
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
        mFragments.add(new CardlistActivity());
        mFragments.add(new RecordActivity());
        mFragments.add(new StatisticActivity());
        mFragments.add(new SetActivity());
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
