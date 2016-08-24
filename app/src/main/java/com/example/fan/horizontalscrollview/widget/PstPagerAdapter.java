package com.example.fan.horizontalscrollview.widget;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Object is Item
 */
public class PstPagerAdapter extends PagerAdapter {
    protected List<Item> mEntries = new ArrayList<Item>();

    public void addItem(View view, int title, int icon) {
        // 这个地方要修改一下
        String _title = view.getContext().getResources().getString(title);
        Item entry = new Item(view, _title, icon);
        mEntries.add(entry);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mEntries.get(position).mTitle;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(((Item) arg2).mView);
    }

    private View getView(int index) {
        return mEntries.get(index).mView;
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(getView(arg1), 0);
        return mEntries.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((Item) arg1).mView;
    }


    protected class Item {
        public View mView;
        public String mTitle;
        public int mIconId;

        public Item(View mView, String mTitle, int mIconId) {
            super();
            this.mView = mView;
            this.mTitle = mTitle;
            this.mIconId = mIconId;
        }

    }
}