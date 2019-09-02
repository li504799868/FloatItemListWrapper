package com.lzp.app1.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzp.app1.R;
import com.lzp.app1.ScreenUtils;
import com.lzp.floatitemlistwrapper.FindItemFilter;
import com.lzp.floatitemlistwrapper.OnFindItemListener;
import com.lzp.floatitemlistwrapper.recycler.FindItemRecyclerViewWrapper;

import org.jetbrains.annotations.NotNull;

/**
 * Created by li.zhipeng on 2018/11/21.
 * <p>
 * 显示浮层的Fragment，内部使用FloatItemRecyclerView，用于ViewPager中
 */
public class RecyclerViewFragment extends BaseFragment implements FindItemFilter,
        OnFindItemListener {

    private FindItemRecyclerViewWrapper wrapper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycler;
    }

    @Override
    protected void onVisible() {
        if (wrapper == null){
            return;
        }
        wrapper.getRecyclerView().post(new Runnable() {
            @Override
            public void run() {
                wrapper.findItem();
            }
        });
    }

    @Override
    protected void onInVisible() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        wrapper = new FindItemRecyclerViewWrapper(recyclerView);
        wrapper.setAutoFind(false);
        wrapper.setFindItemFilter(this);
        wrapper.setOnFindItemListener(this);

        if (isVisible){
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    wrapper.findItem();
                }
            });
        }

        // 如果创建Fragment的时候，已经对用户可见了，直接添加FloatView
        recyclerView.setAdapter(new MyAdapter());
    }

    @Override
    public boolean onFindItemFilter(@NotNull View child, int i) {
        return child.getTop() >= 0
                && child.getBottom() < ScreenUtils.getScreenHeight(requireContext());
    }

    @Override
    public void onFindItem(View convertView, View child, int position) {
        Toast.makeText(requireActivity(), "显示FloatView", Toast.LENGTH_SHORT).show();
        FloatViewController.getInstance().remove();
        ((ViewGroup) convertView).addView(FloatViewController.getInstance().getFloatView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onFindItemHide(View convertView, View child) {
        Toast.makeText(requireActivity(), "隐藏FloatView", Toast.LENGTH_SHORT).show();
        FloatViewController.getInstance().remove();
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
            return new MyViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_view, viewGroup, false));
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
