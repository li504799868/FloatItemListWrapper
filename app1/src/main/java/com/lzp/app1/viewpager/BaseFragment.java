package com.lzp.app1.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lzp.app1.R;

/**
 * Created by li.zhipeng on 2018/11/21.
 */
public abstract class BaseFragment extends Fragment {

    private View rootView;

    protected boolean isVisible;

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        if (FloatViewController.getInstance().getFloatView() == null) {
            FloatViewController.getInstance().setFloatView(inflater.inflate(R.layout.float_view, container, false));
        }
        return rootView;
    }

    /**
     * 懒加载
     *
     * @param isVisibleToUser 是否对用户可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisibleToUser) {
            if (rootView != null) {
                onVisible();
            }
        } else {
            if (rootView != null) {
                onInVisible();
            }
        }
    }

    protected abstract void onVisible();

    protected abstract void onInVisible();
}
