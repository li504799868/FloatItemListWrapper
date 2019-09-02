package com.lzp.floatitemlistwrapper

import android.view.View

/**
 * @author li.zhipeng
 *
 *      符合条件的Item的过滤器
 * */
interface FindItemFilter {

    fun onFindItemFilter(child: View, i: Int): Boolean
}