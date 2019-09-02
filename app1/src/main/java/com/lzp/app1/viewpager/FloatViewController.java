package com.lzp.app1.viewpager;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by li.zhipeng on 2018/11/21.
 * <p>
 * 管理浮层的Controller
 */
public class FloatViewController {

    private static FloatViewController instance;

    public synchronized static FloatViewController getInstance() {
        if (instance == null) {
            instance = new FloatViewController();
        }
        return instance;
    }

    private View floatView;

    private FloatViewController() {

    }

    public View getFloatView() {
        return floatView;
    }

    public void setFloatView(View floatView) {
        this.floatView = floatView;
    }

    public void remove(){
        if (floatView.getParent() != null){
            ((ViewGroup)floatView.getParent()).removeView(floatView);
        }
    }

}
