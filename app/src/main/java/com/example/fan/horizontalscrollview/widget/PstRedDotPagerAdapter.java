package com.example.fan.horizontalscrollview.widget;



public class PstRedDotPagerAdapter extends PstPagerAdapter {

    public static final int NO_REDDOT = -1;

    public int getRedDotID(int pos) {
        return mEntries.get(pos).mIconId;
    }

}
