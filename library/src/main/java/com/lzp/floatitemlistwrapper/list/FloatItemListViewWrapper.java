package com.lzp.floatitemlistwrapper.list;

import android.database.DataSetObserver;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lzp.floatitemlistwrapper.FindItemFilter;
import com.lzp.floatitemlistwrapper.OnFindItemListener;

import java.util.ArrayList;


/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * RecyclerView包装类
 */
public class FloatItemListViewWrapper {

    public FloatItemListViewWrapper(){}

    public FloatItemListViewWrapper(ListView listView){
        bindListView(listView);
    }

    private boolean autoFind = true;

    /**
     * listView
     */
    private ListView listView;

    /**
     * 当前的滑动状态
     */
    private int currentState = -1;

    private View findItem = null;

    /**
     * 显示浮层的child的位置
     */
    private int findItemPosition = -1;

    /**
     * 悬浮View的显示状态监听器
     */
    private OnFindItemListener onFindItemListener;

    /**
     * 控制每一个item是否要显示floatView
     */
    private FindItemFilter findItemFilter;

    private BaseAdapter adapter;

    /**
     * 滚动监听列表，ListView不支持设置多个滚动监听，这里做一个扩展
     */
    private ArrayList<AbsListView.OnScrollListener> onScrollListeners = new ArrayList<>();

    public void bindListView(ListView listView){
        this.listView = listView;
        // 设置滚动监听
        initOnScrollListener();
        // 设置布局监听，当adapter数据发生改变的时候，需要做一些处理
        initOnLayoutChangedListener();
    }

    private void initOnScrollListener() {
        AbsListView.OnScrollListener myScrollerListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentState = scrollState;
                switch (scrollState) {
                    // 停止滑动
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 对正在显示的浮层的child做个副本，为了判断显示浮层的child是否发现了变化
                        View tempFloatChild = findItem;
                        // 更新浮层的位置，覆盖child
                        updateFloatScrollStopTranslateY();
                        // 如果firstChild没有发生变化，回调floatView滑动停止的监听
                        if (tempFloatChild == findItem) {
                            if (onFindItemListener != null) {
                                onFindItemListener.onFindItemScrollStop(findItem);
                            }
                        }
                        break;
                    // 开始滑动
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        // 保存第一个child
                        // 更新浮层的位置
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    // 这里有一个bug，如果手指在屏幕上快速滑动，但是手指并未离开，仍然有可能触发Fling
                    // 所以这里不对Fling状态进行处理
//                    case 2:
//                        hideFloatView();
//                        break;
                }
                // 回调滚动监听器
                if (onScrollListeners.size() > 0) {
                    for (AbsListView.OnScrollListener listener : onScrollListeners) {
                        listener.onScrollStateChanged(view, scrollState);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断显示浮层的child是否已经划出屏幕
                if (findItem != null) {
                    if (needFloatInScreen(firstVisibleItem, visibleItemCount)) {
                        clearFloatChild();
                    }
                }

                switch (currentState) {
                    // 停止滑动
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        updateFloatScrollStartTranslateY();
                        if (onFindItemListener != null) {
                            onFindItemListener.onFindItemScrollFling(findItem);
                        }

                        break;
                }
                // 回调滚动监听器
                if (onScrollListeners.size() > 0) {
                    for (AbsListView.OnScrollListener listener : onScrollListeners) {
                        listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    }
                }
            }
        };
        listView.setOnScrollListener(myScrollerListener);
    }

    private boolean needFloatInScreen(int firstVisibleItem, int visibleItemCount) {
        // 别忘了visibleItemCount包含了第一个可见的item，所以要减一
        return findItemPosition < firstVisibleItem || findItemPosition > firstVisibleItem + visibleItemCount - 1;
    }

    private void initOnLayoutChangedListener() {
        // 设置OnLayoutChangeListener监听，会在设置adapter和adapter.notifyXXX的时候回调
        // 所以我们要这里做一些处理
//        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (listView.getAdapter() == null) {
//                    return;
//                }
//                // 数据已经刷新，找到需要显示悬浮的Item
//                clearFloatChild();
//                // 找到第一个child
//                getFirstChild();
//                updateFloatScrollStartTranslateY();
//            }
//        });
    }

    /**
     * 手动计算应该播放视频的child
     */
    public void findItem() {
        if (findItem == null) {
            updateFloatScrollStopTranslateY();
            // 回调显示状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItem(findItem, findItem,
                        getChildAdapterPosition(findItem));
            }
            return;
        }
        // 获取fistChild在列表中的位置
        int position = getChildAdapterPosition(findItem);
        // 判断是否允许播放
        if (findItemFilter.onFindItemFilter(findItem, position)) {
            updateFloatScrollStartTranslateY();
            // 回调显示状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItem(findItem, findItem,
                        getChildAdapterPosition(findItem));
            }
        } else {
            // 回调隐藏状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItemHide(findItem, findItem);
            }
        }
    }

    private void autoFindItem(){
        if (autoFind){
            listView.post(new Runnable() {
                @Override
                public void run() {
                    findItem();
                }
            });
        }
    }

    /**
     * 找到第一个要显示悬浮item的
     */
    private void getFirstChild() {
        if (findItem != null) {
            return;
        }
        int childPos = calculateShowFloatViewPosition();
        if (childPos != -1) {
            findItem = listView.getChildAt(childPos);
            // 记录显示浮层child的位置
            findItemPosition = getChildAdapterPosition(findItem);
            // 回调显示状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItem(findItem, findItem,
                        getChildAdapterPosition(findItem));
            }
        }
    }

    /**
     * 计算需要显示floatView的位置
     *
     * @return 如果找到RecyclerView中对应的child，返回child的位置，否则发挥-1，表示没有要显示浮层的child
     */
    private int calculateShowFloatViewPosition() {
        // 如果没有设置floatViewShowHook，默认返回第一个Child
        if (findItemFilter == null) {
            return 0;
        }
        // 获取fistChild在列表中的位置
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int childCount = listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = listView.getChildAt(i);
            // 判断这个child是否需要显示
            if (child != null && findItemFilter.onFindItemFilter(child, firstVisiblePosition + i)) {
                return i;
            }
        }
        // -1 表示没有需要显示floatView的item
        return -1;
    }

    private void updateFloatScrollStartTranslateY() {
        if (findItem != null) {
            if (onFindItemListener != null) {
                onFindItemListener.onFindItemScroll(findItem);
            }
        }
    }

    private void updateFloatScrollStopTranslateY() {
        if (findItem == null) {
            getFirstChild();
        }
        updateFloatScrollStartTranslateY();
    }

    public ListView getListView() {
        return listView;
    }

    /**
     * 清除floatView依赖的item，并隐藏floatView
     */
    public void clearFloatChild() {
        // 回调隐藏状态的监听器
        if (onFindItemListener != null) {
            onFindItemListener.onFindItemHide(findItem, findItem);
        }
        findItem = null;
    }

    /**
     * 获取child在列表中的位置
     */
    private int getChildAdapterPosition(View item) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int childCount = listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = listView.getChildAt(i);
            // 判断是否是当前的item
            if (child == item) {
                return firstVisiblePosition + i;
            }
        }
        return -1;
    }

    public void setAdapter(BaseAdapter adapter){
        if (this.adapter != null){
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        listView.setAdapter(adapter);
        this.adapter = adapter;
        if (this.adapter != null){
            this.adapter.registerDataSetObserver(dataSetObserver);
        }
        autoFindItem();
    }

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            autoFindItem();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            autoFindItem();
        }
    };

    public void setOnFindItemListener(OnFindItemListener onFindItemListener) {
        this.onFindItemListener = onFindItemListener;
    }

    public void addOnScrollListener(AbsListView.OnScrollListener listener) {
        if (!onScrollListeners.contains(listener)) {
            onScrollListeners.add(listener);
        }
    }

    public void removeOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListeners.remove(listener);
    }

    public void clearOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListeners.clear();
    }

    public void setFindItemFilter(FindItemFilter findItemFilter) {
        this.findItemFilter = findItemFilter;
    }

    public boolean isAutoFind() {
        return autoFind;
    }

    public void setAutoFind(boolean autoFind) {
        this.autoFind = autoFind;
    }
}
