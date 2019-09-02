package com.lzp.app1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lzp.floatitemlistwrapper.FindItemFilter;
import com.lzp.floatitemlistwrapper.OnFindItemListener;
import com.lzp.floatitemlistwrapper.list.FloatItemListViewWrapper;

import org.jetbrains.annotations.NotNull;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * FloatItemListView演示demo
 */
public class ListViewViewDemoActivity extends AppCompatActivity implements FindItemFilter, OnFindItemListener {

    private View testView;

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.list_view);
        PtrFrameLayout mPtrFrame = findViewById(R.id.ptr_frame);
        mPtrFrame.setHeaderView(new PtrClassicDefaultHeader(this));
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 2000);
            }
        });

        FloatItemListViewWrapper wrapper = new FloatItemListViewWrapper(listView);
        wrapper.setFindItemFilter(this);
        wrapper.setOnFindItemListener(this);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.header, listView, false));
        listView.setAdapter(new MyAdapter());

        testView = new View(this);
        testView.setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onFindItemFilter(@NotNull View child, int i) {
        if (i < listView.getHeaderViewsCount()) {
            return false;
        }
        return child.getTop() >= 0
                && child.getBottom() < ScreenUtils.getScreenHeight(this);
    }

    @Override
    public void onFindItem(View convertView, View child, int position) {
        Toast.makeText(this, "显示FloatView" + position, Toast.LENGTH_SHORT).show();
        if (testView.getParent() != null){
            ((ViewGroup)testView.getParent()).removeView(testView);
        }
        ((ViewGroup)convertView).addView(testView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onFindItemHide(View convertView, View child) {
        Toast.makeText(this, "隐藏FloatView", Toast.LENGTH_SHORT).show();
        if (testView.getParent() != null){
            ((ViewGroup)testView.getParent()).removeView(testView);
        }
    }

    @Override
    public void onFindItemScroll(View convertView) {

    }

    @Override
    public void onFindItemScrollFling(View convertView) {

    }

    @Override
    public void onFindItemScrollStop(View convertView) {

    }



    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ListViewViewDemoActivity.this).inflate(R.layout.item_view, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText("item" + position);
            return convertView;
        }
    }

}
