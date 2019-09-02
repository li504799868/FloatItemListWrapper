package com.lzp.floatitemlistwrapper;


import android.view.View;

/**
 * 显示状态的回调监听器
 */
public interface OnFindItemListener {

    /**
     * FloatView被显示
     *
     * @param convertView 浮层
     * @param child     显示浮层的child
     * @param position  child在RecyclerView中的位置
     */
    void onFindItem(View convertView, View child, int position);

    /**
     * FloatView被隐藏
     */
    void onFindItemHide(View convertView, View child);

    /**
     * FloatView被移动
     */
    void onFindItemScroll(View convertView);

    /**
     * FloatView被处于Fling状态
     */
    void onFindItemScrollFling(View convertView);

    /**
     * FloatView由滚动变为静止状态
     */
    void onFindItemScrollStop(View convertView);

}
