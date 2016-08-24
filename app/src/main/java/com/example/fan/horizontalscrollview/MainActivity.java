package com.example.fan.horizontalscrollview;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.example.fan.horizontalscrollview.system.DeviceUtils;
import com.example.fan.horizontalscrollview.widget.PstRedDotPagerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity implements View.OnClickListener{
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mPstTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DeviceUtils.hasSmartBar() && Build.VERSION.SDK_INT >= 11) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
            setTheme(android.R.style.Theme_Holo);
            Method method;
            try {
                method = Class.forName("android.app.ActionBar").getMethod(
                        "setActionBarViewCollapsable", new Class[]{boolean.class});
                try {
                    method.invoke(getActionBar(), true);
                    getActionBar().setDisplayOptions(0);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        findView();
        setOnClickListener();
        initView();
    }

    private void findView() {
        mViewPager = (ViewPager) findViewById(R.id.vPager);
        mPstTab = (PagerSlidingTabStrip) findViewById(R.id.pst_indicator);
    }

    private void setOnClickListener() {
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view1 = layoutInflater.inflate(R.layout.item_view1, null);
        View view2 = layoutInflater.inflate(R.layout.item_view2, null);
        View view3 = layoutInflater.inflate(R.layout.item_view3, null);
//
        PstRedDotPagerAdapter pstAdapter = new PstRedDotPagerAdapter();
        pstAdapter.addItem(view1, R.string.asus_low_battery_summarize_card_1, -1);
        pstAdapter.addItem(view2, R.string.asus_low_battery_summarize_card_2, -1);
        pstAdapter.addItem(view3, R.string.asus_low_battery_summarize_card_3, -1);
        mViewPager.setAdapter(pstAdapter);

        mPstTab.setViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {

    }
}
