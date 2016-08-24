/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.fan.horizontalscrollview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.*;
import com.example.fan.horizontalscrollview.system.DeviceUtils;
import com.example.fan.horizontalscrollview.system.DimenUtils;
import com.example.fan.horizontalscrollview.widget.PstRedDotPagerAdapter;

import java.util.Locale;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private static final boolean DEBUG = false;

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // 专用的Cm有图标有文字的Tab
    public interface CmIconTextProvider {
        public int getPageIconResId(int position);
    }

    public interface WarningTextProvider {
        public int getWarningIconId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor, android.R.attr.textStyle};
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;
    private int picksPosition = -1;  //当前值只用来标记picks的position

    private Paint rectPaint;
    private Paint dividerPaint;
    private Paint underLinePaint;

    private boolean checkedTabWidths = false;
    private static final int COLOR_UNDER_LINE = 0xFFD9D9D9;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;
    private float underlineLengthPercentage = 1.0f;

    //private boolean shouldExpand = false;
    private boolean textAllCaps = false;

    private int scrollOffset = 52;
    private int indicatorHeight = 6;
    private int underlineHeight = 1;
    private int dividerPadding = 12;
    private int tabPadding = 12;
    private int dividerWidth = 1;
    private boolean halfLastTab = false;

    //	private int tabTextSize = 12;
//	private ColorStateList tabTextColor;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.pst_tab_background;

    private Locale locale;
    private boolean mAliquots;
    private boolean mTextChangeable;

    private float mUnderLineShrinkPercent = 0;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }


    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = context.getResources().getColor(R.color.yongchao_blue);//a.getColor(R.styleable.PagerSlidingTabStrip_indicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_underlineColor, underlineColor);
        underlineLengthPercentage = a.getFloat(R.styleable.PagerSlidingTabStrip_underlineLongPercentage,
                underlineLengthPercentage);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_dividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_indicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_underlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_dividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_tabBackground, tabBackgroundResId);
        //shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_shouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_scrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_textAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.STROKE);
        rectPaint.setStrokeWidth(indicatorHeight);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        underLinePaint = new Paint();
        underLinePaint.setColor(COLOR_UNDER_LINE);
        underLinePaint.setStrokeWidth(2);
        underLinePaint.setStyle(Style.STROKE);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));

            } else if (pager.getAdapter() instanceof CmIconTextProvider) {
                addCmTab(i, pager.getAdapter().getPageTitle(i).toString(),
                        ((CmIconTextProvider) pager.getAdapter()).getPageIconResId(i));

            } else if (pager.getAdapter() instanceof PstRedDotPagerAdapter) {
                addRedDotTab(i, pager.getAdapter().getPageTitle(i).toString(), ((PstRedDotPagerAdapter) pager.getAdapter()).getRedDotID(i));

            } else if (pager.getAdapter() instanceof WarningTextProvider) {
                addWarningTab(i, pager.getAdapter().getPageTitle(i).toString(), ((WarningTextProvider) pager.getAdapter()).getWarningIconId(i));

            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        checkedTabWidths = false;

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                // 这里有个deprecated的方法，由于我的build SDK 是14，暂时屏蔽
                // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                // getViewTreeObserver().removeGlobalOnLayoutListener(this);
                // } else {
                // getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    public interface OnPageClickedLisener {
        public void onPageClicked(int position);
    }

    protected OnPageClickedLisener mOnPageClickedLisener;

    public TextView getTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setTextColor(getResources().getColorStateList(mTextChangeable ?
                R.color.pst_tab_changeable_text_selector : R.color.pst_tab_text_selector));
        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPageClickedLisener) {
                    mOnPageClickedLisener.onPageClicked(position);
                }
                pager.setCurrentItem(position);
            }
        });

        return tab;
    }

    public void addTextTab(int position, String title) {
        tabsContainer.addView(getTextTab(position, title));
    }

    private void addCmTab(final int position, String title, int resId) {
        RelativeLayout tab = (RelativeLayout) inflate(getContext(), R.layout.cm_pst__tab, null);
        ImageView iv = (ImageView) tab.findViewById(R.id.pst_iv_tab);
        iv.setImageResource(resId);
        iv.setVisibility(View.VISIBLE);
        TextView tv = (TextView) tab.findViewById(R.id.pst_tv_tab);
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPageClickedLisener) {
                    mOnPageClickedLisener.onPageClicked(position);
                }
                pager.setCurrentItem(position);
            }
        });

        tabsContainer.addView(tab);
    }

    private void addRedDotTab(final int position, String title, int resId) {
        LinearLayout tabLayout = new LinearLayout(getContext());
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLayout.setGravity(Gravity.CENTER);
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);

        tab.setSingleLine();
        tab.setTextColor(getResources().getColorStateList(R.color.pst_tab_text_selector));
        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        tabLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPageClickedLisener) {
                    mOnPageClickedLisener.onPageClicked(position);
                }
                pager.setCurrentItem(position);
            }
        });

        tabLayout.addView(tab, 0);
        ImageView tabImg = new ImageView(getContext());
        LinearLayout.LayoutParams tabImgParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tabImgParams.setMargins(DeviceUtils.dip2px(getContext(), 5), DeviceUtils.dip2px(getContext(), 10), 0, 0);
        tabImgParams.gravity = Gravity.TOP;
        tabImg.setLayoutParams(tabImgParams);

        tabLayout.addView(tabImg, 1);
        tabsContainer.addView(tabLayout);
    }

    private void addWarningTab(final int position, String title, int resId) {
        LinearLayout tabLayout = new LinearLayout(getContext());
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLayout.setGravity(Gravity.CENTER);

        ImageView tabImg = new ImageView(getContext());
        int width = DimenUtils.dp2px(getContext(), 18);
        LinearLayout.LayoutParams tabImgParams = new LinearLayout.LayoutParams(width, width);
        tabImgParams.setMargins(0, 0, DimenUtils.dp2px(getContext(), 10), 0);
        tabImgParams.gravity = Gravity.CENTER;
        tabImg.setLayoutParams(tabImgParams);
        if (resId != -1) {
            tabImg.setBackgroundResource(resId);
            tabImg.setVisibility(View.VISIBLE);
        } else {
            tabImg.setVisibility(View.GONE);
        }
        tabLayout.addView(tabImg, 0);

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setTextColor(getResources().getColorStateList(mTextChangeable ?
                R.color.pst_tab_changeable_text_selector : R.color.pst_tab_text_selector));
        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        tabLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPageClickedLisener) {
                    mOnPageClickedLisener.onPageClicked(position);
                }
                pager.setCurrentItem(position);
            }
        });
        tabLayout.addView(tab, 1);

        tabsContainer.addView(tabLayout);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnPageClickedLisener) {
                    mOnPageClickedLisener.onPageClicked(position);
                }
                pager.setCurrentItem(position);
            }
        });

        tabsContainer.addView(tab);

    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setLayoutParams(defaultTabLayoutParams);
            v.setBackgroundResource(tabBackgroundResId);
            v.setPadding(tabPadding, 0, tabPadding, 0);

            if (v instanceof TextView || v instanceof RelativeLayout) {
                TextView tab = null;
                if (v instanceof TextView)
                    tab = (TextView) v;
                else
                    tab = (TextView) v.findViewById(R.id.pst_tv_tab);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return;
        }

        if (checkedTabWidths) {
            return;
        }

        int childWidth = 0;
        for (int i = 0; i < tabCount; i++) {
            childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
        }

        int myWidth = getMeasuredWidth();
        if (childWidth > 0 && myWidth > 0) {

            if (mAliquots) {
                for (int i = 0; i < tabCount; i++) {
                    tabsContainer.getChildAt(i).setLayoutParams(expandedTabLayoutParams);
                }

            } else {

                // 如果 TAB 需要展示的宽度大于可展示宽度就使用自适应 defaultTabLayoutParams，
                // 否则的话就把多余的宽度平均到每个 TAB
                if (childWidth <= myWidth) {
                    int extras = myWidth - childWidth;
                    if (extras > 0) {
                        int extrasPadding = extras / tabCount;
                        tabPadding += extrasPadding / 2;

                        for (int i = 0; i < tabCount; i++) {
                            LinearLayout.LayoutParams itemLayout = new LinearLayout.LayoutParams(
                                    tabsContainer.getChildAt(i).getMeasuredWidth() + extrasPadding, LayoutParams.MATCH_PARENT);
                            tabsContainer.getChildAt(i).setLayoutParams(itemLayout);
                        }
                    }
                } else {
                    for (int i = 0; i < tabCount; i++) {
                        tabsContainer.getChildAt(i).setLayoutParams(defaultTabLayoutParams);
                    }
                }

            }

            checkedTabWidths = true;
        }
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
        // 变换Icon状态
        for (int i = 0; i < tabCount; i++) {
            tabsContainer.getChildAt(i).setSelected(i == position);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (isInEditMode() || tabCount == 0) {
            return result;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (underlineLengthPercentage > 1.0f || underlineLengthPercentage < 0.0f)
            underlineLengthPercentage = 1.0f;

        float temp = 0;
        if (mUnderLineShrinkPercent > 0 && mUnderLineShrinkPercent < 1.0) {
            temp = (lineRight - lineLeft) * (1 - mUnderLineShrinkPercent) / 2;
        }

        canvas.drawLine(0, height, getWidth() * 2, height, underLinePaint);
        canvas.drawLine(lineLeft + temp, height, lineRight - temp, height, rectPaint);
        return result;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (DEBUG)
                Log.d("PZC", "positionOffsetPixels:" + positionOffsetPixels);
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}