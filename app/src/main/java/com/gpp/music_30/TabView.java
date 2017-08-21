package com.gpp.music_30;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/8/18.
 */

public class TabView extends LinearLayout {
    public ImageView imageView;
    public TextView textView;

    public TabView(Context context) {
        super(context);
        init(context);
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.tab_item, this, true);
        imageView = findViewById(R.id.item_image);
        textView = findViewById(R.id.item_tv);
    }


    public void initData(TabItem tabButton) {
        imageView.setImageResource(tabButton.imageResId);
        textView.setText(tabButton.textResid);
    }
}
