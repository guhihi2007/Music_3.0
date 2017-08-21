package com.gpp.music_30;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/18.
 */

public class TabLayout extends LinearLayout implements View.OnClickListener {
    private ArrayList<TabItem> tabButtons;
    private OnTabClickListener listener;

    public TabLayout(Context context) {
        super(context);
        init();
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void initData(ArrayList<TabItem> tabItems, OnTabClickListener listener) {
        this.tabButtons = tabItems;
        this.listener = listener;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        if (tabItems != null && tabItems.size() > 0) {
            TabView tabView;
            for (int i = 0; i < tabItems.size(); i++) {
                tabView = new TabView(getContext());
                tabView.setTag(tabItems.get(i));
                tabView.initData(tabItems.get(i));
                tabView.setOnClickListener(this);
                addView(tabView, params);
            }
        } else {
            throw new IllegalArgumentException("tabItems is Empty");
        }
    }

    @Override
    public void onClick(View view) {
        listener.OnClick(view, (TabItem) view.getTag());
    }

    interface OnTabClickListener {
        void OnClick(View view, TabItem tabItem);
    }
}
