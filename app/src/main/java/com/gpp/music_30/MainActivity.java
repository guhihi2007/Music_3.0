package com.gpp.music_30;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabClickListener {
    private TabLayout tabLayout;
    private ArrayList<TabItem> tabItems;
    private DrawerLayout drawerLayout;
    private LinearLayout left;
    private NavigationView navigationView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ArrayList<Fragment> fragments;
    private ViewPager viewPager;
    private Fragment_Home fragmentHome;
    private Fragment_Favour fragmentFavour;
    private Fragment_Search fragmentSearch;
    private Fragment_Set fragmentSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        findView();
        init();
        setViewPager();
    }

    private void findView() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        left = (LinearLayout) findViewById(R.layout.left_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void init() {
        tabItems = new ArrayList<>();
        fragments = new ArrayList<>();
        tabItems.add(new TabItem(R.drawable.home_2, R.string.home));
        tabItems.add(new TabItem(R.drawable.favour_2, R.string.favour));
        tabItems.add(new TabItem(R.drawable.search_2, R.string.search));
        tabItems.add(new TabItem(R.drawable.set_2, R.string.set));
        tabLayout.initData(tabItems, this);
        tabLayout.getChildAt(0).setBackgroundColor(R.color.tabSelectedColor);
        ((TabView) (tabLayout.getChildAt(0))).textView.setTextColor(Color.WHITE);
        ((TabView) (tabLayout.getChildAt(0))).imageView.setImageResource(R.drawable.home_1);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        fragmentHome = new Fragment_Home();
        fragmentFavour = new Fragment_Favour();
        fragmentSearch = new Fragment_Search();
        fragmentSet = new Fragment_Set();
        fragments.add(fragmentHome);
        fragments.add(fragmentFavour);
        fragments.add(fragmentSearch);
        fragments.add(fragmentSet);
    }

    @Override
    public void OnClick(View view, TabItem tabItem) {
        switch (tabItem.textResid) {
            case R.string.home:
                setTabImage(0);
                break;
            case R.string.favour:
                setTabImage(1);
                break;
            case R.string.search:
                setTabImage(2);
                break;
            case R.string.set:
                setTabImage(3);
                break;
        }
    }

    private void setViewPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabImage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTabImage(int position) {
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            if (i == position) {
                tabLayout.getChildAt(position).setBackgroundColor(R.color.tabSelectedColor);
                ((TabView) (tabLayout.getChildAt(position))).textView.setTextColor(Color.WHITE);
                viewPager.setCurrentItem(position);
            } else {
                tabLayout.getChildAt(i).setBackgroundColor(Color.WHITE);
                ((TabView) (tabLayout.getChildAt(i))).textView.setTextColor(R.color.textDefaultColor);
            }
        }
        switch (position) {
            case 0:
                ((TabView) (tabLayout.getChildAt(0))).imageView.setImageResource(R.drawable.home_1);
                ((TabView) (tabLayout.getChildAt(1))).imageView.setImageResource(R.drawable.favour_2);
                ((TabView) (tabLayout.getChildAt(2))).imageView.setImageResource(R.drawable.search_2);
                ((TabView) (tabLayout.getChildAt(3))).imageView.setImageResource(R.drawable.set_2);
                break;
            case 1:
                ((TabView) (tabLayout.getChildAt(1))).imageView.setImageResource(R.drawable.favour_1);
                ((TabView) (tabLayout.getChildAt(0))).imageView.setImageResource(R.drawable.home_2);
                ((TabView) (tabLayout.getChildAt(2))).imageView.setImageResource(R.drawable.search_2);
                ((TabView) (tabLayout.getChildAt(3))).imageView.setImageResource(R.drawable.set_2);
                break;
            case 2:
                ((TabView) (tabLayout.getChildAt(2))).imageView.setImageResource(R.drawable.search_1);
                ((TabView) (tabLayout.getChildAt(0))).imageView.setImageResource(R.drawable.home_2);
                ((TabView) (tabLayout.getChildAt(1))).imageView.setImageResource(R.drawable.favour_2);
                ((TabView) (tabLayout.getChildAt(3))).imageView.setImageResource(R.drawable.set_2);
                break;
            case 3:
                ((TabView) (tabLayout.getChildAt(3))).imageView.setImageResource(R.drawable.set_1);
                ((TabView) (tabLayout.getChildAt(0))).imageView.setImageResource(R.drawable.home_2);
                ((TabView) (tabLayout.getChildAt(1))).imageView.setImageResource(R.drawable.favour_2);
                ((TabView) (tabLayout.getChildAt(2))).imageView.setImageResource(R.drawable.search_2);
                break;
        }
    }
}
