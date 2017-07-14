package com.liunian.ptzdemo;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * <p>类说明</p>
 *
 * @author liumeiliya 2017/7/13 13:51
 * @version V1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2017/7/13
 * @modify by reason:{方法名}:{原因}
 */

public class PTZView2 extends RelativeLayout {
    RelativeLayout parentView;
    RelativeLayout P;
    Context context;
    ImageView touchIv;
    OnWheelTouchListener onWheelTouchListener;
    OnDirectionLsn onDirectionLsn;

    public PTZView2(Context context) {
        super(context);
        init(context);
        this.context = context;
    }

    public PTZView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.context = context;
    }

    public void setOnWheelTouchListener(OnWheelTouchListener onWheelTouchListener) {
        this.onWheelTouchListener = onWheelTouchListener;
    }

    public void setOnDirectionLsn(OnDirectionLsn onDirectionLsn) {
        this.onDirectionLsn = onDirectionLsn;
    }

    //中心图片原始的位置
    int autoBackViewOriginLeft;
    int autoBackViewOriginTop;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        autoBackViewOriginLeft = touchIv.getLeft();
        autoBackViewOriginTop = touchIv.getTop();
    }

    public PTZView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.context = context;
    }

    //圆盘的直径（2个值其实一样）
    int width, height;
    //圆心的坐标
    int p_c_x, p_c_y;

    private void init(Context context) {
        parentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.ptz_view, this, true);
        touchIv = (ImageView) parentView.findViewById(R.id.touchIv);
        P = (RelativeLayout) parentView.findViewById(R.id.P);
        ViewTreeObserver vto2 = touchIv.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                touchIv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                width = touchIv.getWidth();
                height = touchIv.getHeight();
                p_c_x = P.getWidth() / 2;
                p_c_y = P.getHeight() / 2;
                neiCircleW = Math.abs(p_c_x - width / 2);
            }
        });
    }

    //判断现在手指触摸的是否是需要滑动的小圆。
    public View findTopChildUnder(int x, int y) {
        final int childCount = P.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = P.getChildAt(0);
            if (x >= child.getLeft() && x < child.getRight()
                    && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    /**
     * 设置是否支持第一次点击时，圆盘就移动到随手指点击的地方
     *
     * @param isSupport
     */
    public void setIsFromFingerWithClick(boolean isSupport) {
        this.isSupport = isSupport;
    }

    /**
     * 内圈半径
     */
    float neiCircleW;
    /**
     * 是否选中圆盘
     */
    boolean isSelect = false;
    /**
     * 是否支持第一次点击时，圆盘就移动到随手指点击的地方
     */
    boolean isSupport = false;
    /**
     * 滑动生效距离。
     */
    float enableDistance = 0;

    /**
     * 设置滑动生效距离，就是圆盘圆心离中心点的距离多少时，触发滑动生效。
     */
    public void setEnableDistance(float enableDistance) {
        this.enableDistance = enableDistance;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {//已选中可滑动小圆
                    moveSteeringWheel(x, y, ev);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                final View toCapture = findTopChildUnder((int) x, (int) y);
                if (toCapture != null) {
                    isSelect = true;
                } else {
                    isSelect = false;
                }
                if (isSupport) {
                    isSelect = true;
                    moveSteeringWheel(x, y, ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                isSelect = false;
                touchIv.setX(autoBackViewOriginLeft);
                touchIv.setY(autoBackViewOriginTop);
                if (this.onWheelTouchListener != null) {
                    onWheelTouchListener.onRelease();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 移动方向盘
     *
     * @param x 手指点击的x轴坐标
     * @param y 手指点击的y轴坐标
     */
    private void moveSteeringWheel(float x, float y, MotionEvent ev) {
        final float temp1 = Math.abs(x - p_c_x) * Math.abs(x - p_c_x) + Math.abs(y - p_c_y) * Math.abs(y - p_c_y);
        final float temp2 = neiCircleW * neiCircleW;
        if (temp1 > temp2) {//手指在大圆外
            //通过勾股定理算出小圆沿着外圈滚动时，圆心的坐标点。
            final double final_y = Math.sqrt(Math.abs(y - p_c_y) * Math.abs(y - p_c_y) * temp2 / temp1);
            final double final_x = Math.sqrt(Math.abs(x - p_c_x) * Math.abs(x - p_c_x) * temp2 / temp1);
            if (x > p_c_x) {
                touchIv.setX(neiCircleW + (float) final_x);
            } else {
                touchIv.setX(neiCircleW - (float) final_x);
            }
            if (y > p_c_y) {
                touchIv.setY((neiCircleW + (float) final_y));
            } else {
                touchIv.setY((neiCircleW - (float) final_y));
            }

            if (temp1 >= (enableDistance * enableDistance)) {
                if (this.onWheelTouchListener != null) {
                    onWheelTouchListener.onEnableDistance(ev);
                }
                if (this.onDirectionLsn != null) {
                    final double tempY = touchIv.getY() + width / 2 - p_c_x;
                    final double tempX = touchIv.getX() + width / 2 - p_c_x;
//                    Log.i("lmly","tempY:"+tempY+"  tempX:"+tempX);
                    //为了方便起见，在45度角及其倍数的情况下，不作特殊处理。
                    if (tempY > 0 && tempX > 0) {
                        if (tempY > tempX) {
                            onDirectionLsn.bottom();
                        } else {
                            onDirectionLsn.right();
                        }
                        return;
                    } else if (tempY < 0 && tempX < 0) {
                        if (Math.abs(tempY) > Math.abs(tempX)) {
                            onDirectionLsn.top();
                        } else {
                            onDirectionLsn.left();
                        }
                        return;
                    } else if (tempY > 0 && tempX < 0) {
                        if (Math.abs(tempY) > Math.abs(tempX)) {
                            onDirectionLsn.bottom();
                        } else {
                            onDirectionLsn.left();
                        }
                        return;
                    } else if (tempY < 0 && tempX > 0) {
                        if (Math.abs(tempY) > Math.abs(tempX)) {
                            onDirectionLsn.top();
                        } else {
                            onDirectionLsn.right();
                        }
                        return;
                    }
                }
            }
        } else {//手指在大圆内
            touchIv.setX(x - width / 2);
            touchIv.setY(y - height / 2);
            if (this.onDirectionLsn != null) {
                //@TODO 为什么我都要加上一个width / 2呢，因为你圆盘的getY()和getX()方法其实获取到的都是圆盘的左上角的坐标，而我们需要的是圆心坐标，所以你必需加上这个圆盘的半径，才能得到圆心
                final double tempY = touchIv.getY() + width / 2 - p_c_x;
                final double tempX = touchIv.getX() + width / 2 - p_c_x;
                //为了方便起见，在45度角及其倍数的情况下，不作特殊处理。
                if (tempY > 0 && tempX > 0) {
                    if (tempY > tempX) {
                        onDirectionLsn.bottom();
                    } else {
                        onDirectionLsn.right();
                    }
                    return;
                } else if (tempY < 0 && tempX < 0) {
                    if (Math.abs(tempY) > Math.abs(tempX)) {
                        onDirectionLsn.top();
                    } else {
                        onDirectionLsn.left();
                    }
                    return;
                } else if (tempY > 0 && tempX < 0) {
                    if (Math.abs(tempY) > Math.abs(tempX)) {
                        onDirectionLsn.bottom();
                    } else {
                        onDirectionLsn.left();
                    }
                    return;
                } else if (tempY < 0 && tempX > 0) {
                    if (Math.abs(tempY) > Math.abs(tempX)) {
                        onDirectionLsn.top();
                    } else {
                        onDirectionLsn.right();
                    }
                    return;
                }
            }
        }
    }
}
