package com.lzp.app1.viewpager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.lzp.app1.R;

/**
 * Created by li.zhipeng on 2018/10/10.
 *
 * ViewPager中使用FloatItemRecyclerView优化方案演示demo
 *
 */
public class ViewPagerDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return new RecyclerViewFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Fragment" + position;
            }
        });

        SlidingTabLayout tabLayout = findViewById(R.id.sliding_tab_layout);
        tabLayout.setViewPager(viewPager);

    }

}
