package com.liunian.ptzdemo;

import android.view.MotionEvent;

/**
 * <p>类说明</p>
 *
 * @author liumeiliya 2017/7/14 14:11
 * @version V1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2017/7/14
 * @modify by reason:{方法名}:{原因}
 */

public interface OnWheelTouchListener {
    void onEnableDistance(MotionEvent ev);
    void onRelease();
}
