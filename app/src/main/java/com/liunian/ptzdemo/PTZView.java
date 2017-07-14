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

public class PTZView extends RelativeLayout {
    RelativeLayout parentView;
    RelativeLayout P;
    Context context;
    ImageView touchIv;

    public PTZView(Context context) {
        super(context);
        init(context);
        this.context = context;
    }

    public PTZView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.context = context;
        mDragger = ViewDragHelper.create(P, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.i("lmly","tryCaptureView");
                if (child.getId() == touchIv.getId()) {
                    return true;
                }
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - touchIv.getWidth();
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - touchIv.getHeight();
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                mDragger.settleCapturedViewAt(autoBackViewOriginLeft, autoBackViewOriginTop);
                invalidate();
            }
        });
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    int autoBackViewOriginLeft;
    int autoBackViewOriginTop;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        autoBackViewOriginLeft = touchIv.getLeft();
        autoBackViewOriginTop = touchIv.getTop();
    }

    int width,p_c_x;
    int height,p_c_y;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public PTZView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.context = context;
    }

    private void init(Context context) {
        parentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.ptz_view, this, true);
        touchIv = (ImageView) parentView.findViewById(R.id.touchIv);
        P = (RelativeLayout) parentView.findViewById(R.id.P);
        ViewTreeObserver vto2 = touchIv.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                touchIv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                width=touchIv.getWidth();
                height=touchIv.getHeight();
                p_c_x= P.getWidth()/2;
                p_c_y=P.getHeight()/2;
                Log.i("lmly","++++"+ width+" "+height+" "+p_c_x+" "+p_c_y);
            }
        });
    }

    private ViewDragHelper mDragger;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case  MotionEvent.ACTION_MOVE:
                float x=touchIv.getX();
                float y=touchIv.getY();
                float cX=x+width/2;
                float cY=y+height/2;
                float temp1=Math.abs(p_c_x-cX)*Math.abs(p_c_x-cX)+Math.abs(p_c_y-cY)*Math.abs(p_c_y-cY);
                float temp2=(p_c_x-width/2)* (p_c_x-width/2);
                if(temp1<temp2){
                    mDragger.processTouchEvent(event);
                }
                break;
            default:
                mDragger.processTouchEvent(event);
                break;
        }
        return true;
    }

}
