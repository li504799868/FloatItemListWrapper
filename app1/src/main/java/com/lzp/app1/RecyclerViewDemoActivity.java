package com.lzp.app1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzp.floatitemlistwrapper.FindItemFilter;
import com.lzp.floatitemlistwrapper.OnFindItemListener;
import com.lzp.floatitemlistwrapper.recycler.FindItemRecyclerViewWrapper;

import org.jetbrains.annotations.NotNull;

/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * FloatItemRecyclerView演示demo
 */
public class RecyclerViewDemoActivity extends AppCompatActivity implements FindItemFilter, OnFindItemListener {

    private View testView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final FindItemRecyclerViewWrapper wrapper = new FindItemRecyclerViewWrapper(recyclerView);
        wrapper.setFindItemFilter(this);
        wrapper.setOnFindItemListener(this);
        wrapper.setAdapter(new MyAdapter());
        testView = new View(this);
        testView.setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onFindItemFilter(@NotNull View child, int i) {
        return child.getTop() >= 0
                && child.getBottom() < ScreenUtils.getScreenHeight(this);
    }

    @Override
    public void onFindItem(View convertView, View child, int position) {
        Toast.makeText(this, "显示FloatView", Toast.LENGTH_SHORT).show();
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


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(RecyclerViewDemoActivity.this).inflate(R.layout.item_view, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
