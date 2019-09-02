package com.lzp.floatitemlistwrapper.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzp.floatitemlistwrapper.FindItemFilter;
import com.lzp.floatitemlistwrapper.OnFindItemListener;


/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 *     找到屏幕中符合条件的Item，RecyclerView包装类
 *
 */
public class FindItemRecyclerViewWrapper{

    public FindItemRecyclerViewWrapper(){}

    public FindItemRecyclerViewWrapper(RecyclerView recyclerView){
        bindRecyclerView(recyclerView);
    }

    private boolean autoFind = true;

    /**
     * recyclerView
     */
    private RecyclerView recyclerView;

    /**
     * 当前的滑动状态
     */
    private int currentState = -1;

    private View findItem = null;

    /**
     * 悬浮View的显示状态监听器
     */
    private OnFindItemListener onFindItemListener;

    /**
     * 控制每一个item是否要显示floatView
     */
    private FindItemFilter findItemFilter;

    private RecyclerView.Adapter adapter;

    /**
     * 必须设置FloatViewShowHook，完成View的初始化操作
     */
    public void setFindItemFilter(FindItemFilter findItemFilter) {
        this.findItemFilter = findItemFilter;
    }

    public void bindRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        bindRecyclerViewListener();
    }

    private void bindRecyclerViewListener() {
        // 设置滚动监听
        initOnScrollListener();
        // 设置布局监听，当adapter数据发生改变的时候，需要做一些处理
        initOnLayoutChangedListener();
        // 监听recyclerView的item滚动情况，判断正在悬浮item是否已经移出了屏幕
        initOnChildAttachStateChangeListener();
    }

    private void initOnScrollListener() {
        RecyclerView.OnScrollListener myScrollerListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentState = newState;
                switch (newState) {
                    // 停止滑动
                    case RecyclerView.SCROLL_STATE_IDLE:
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
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        // 保存第一个child
                        // 更新浮层的位置
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    // 这里有一个bug，如果手指在屏幕上快速滑动，但是手指并未离开，仍然有可能触发Fling
                    // 所以这里不对Fling状态进行处理
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        hideFloatView();
//                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                switch (currentState) {
                    // 停止滑动
                    case RecyclerView.SCROLL_STATE_IDLE:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        updateFloatScrollStartTranslateY();
                        if (onFindItemListener != null) {
                            onFindItemListener.onFindItemScrollFling(findItem);
                        }

                        break;
                }
            }
        };
        recyclerView.addOnScrollListener(myScrollerListener);
    }

    private void initOnChildAttachStateChangeListener() {
        // 监听item的移除情况
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                // 判断child是否被移除
                // 请注意：回调onChildViewDetachedFromWindow时并没有真正移除这个child
                // 所以这里增加一个判断：floatChildInScreen是否正在被adapter使用，防止浮层闪烁
                if (view == findItem && floatChildInScreen()) {
                    clearFloatChild();
                }
            }
        });
    }

    private void initOnLayoutChangedListener() {
        // 设置OnLayoutChangeListener监听，会在设置adapter和adapter.notifyXXX的时候回调
        // 所以我们要这里做一些处理
//        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (recyclerView.getAdapter() == null) {
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
                        recyclerView.getChildAdapterPosition(findItem));
            }
            return;
        }
        // 获取needFloatChild在列表中的位置
        int position = recyclerView.getChildAdapterPosition(findItem);
        // 判断是否允许播放
        if (findItemFilter.onFindItemFilter(findItem, position)) {
            updateFloatScrollStartTranslateY();
            // 回调显示状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItem(findItem, findItem,
                        recyclerView.getChildAdapterPosition(findItem));
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
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    findItem();
                }
            });
        }
    }

    /**
     * 判断item是否正在显示内容
     */
    private boolean floatChildInScreen() {
        return recyclerView.getChildAdapterPosition(findItem) != -1;
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
            findItem = recyclerView.getChildAt(childPos);
            // 回调显示状态的监听器
            if (onFindItemListener != null) {
                onFindItemListener.onFindItem(findItem, findItem,
                        recyclerView.getChildAdapterPosition(findItem));
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
        int firstVisiblePosition;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else {
            throw new IllegalArgumentException("only support LinearLayoutManager!!!");
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
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

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }


    public boolean isAutoFind() {
        return autoFind;
    }

    public void setAutoFind(boolean autoFind) {
        this.autoFind = autoFind;
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

    public void setOnFindItemListener(OnFindItemListener onFindItemListener) {
        this.onFindItemListener = onFindItemListener;
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        if (this.adapter != null){
            this.adapter.unregisterAdapterDataObserver(adapterDataObserver);
        }
        recyclerView.setAdapter(adapter);
        this.adapter = adapter;
        if (this.adapter != null){
            this.adapter.registerAdapterDataObserver(adapterDataObserver);
        }
        autoFindItem();
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            autoFindItem();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            autoFindItem();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            autoFindItem();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            autoFindItem();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            autoFindItem();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            autoFindItem();
        }
    };


}
