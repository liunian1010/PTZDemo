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

    //中心图片原始的位置
    int autoBackViewOriginLeft;
    int autoBackViewOriginTop;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        autoBackViewOriginLeft = touchIv.getLeft();
        autoBackViewOriginTop = touchIv.getTop();
    }

    int width,height;
    int p_c_x,p_c_y;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public PTZView2(Context context, AttributeSet attrs, int defStyleAttr) {
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
                neiCircleW=Math.abs(p_c_x-width/2);
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


    float neiCircleW;
    boolean isSelect=false;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()){
            case  MotionEvent.ACTION_MOVE:
                if(isSelect){//已选中可滑动小圆
                    final float temp1=Math.abs(x-p_c_x)*Math.abs(x-p_c_x)+Math.abs(y-p_c_y)*Math.abs(y-p_c_y);
                    final float temp2=neiCircleW*neiCircleW;
                    if(temp1>temp2){//手指在大圆外
                        //通过勾股定理算出小圆沿着外圈滚动时，圆心的坐标点。
                        final double final_y=Math.sqrt(Math.abs(y-p_c_y)*Math.abs(y-p_c_y)*temp2/temp1);
                        final double final_x=Math.sqrt(Math.abs(x-p_c_x)*Math.abs(x-p_c_x)*temp2/temp1);
                        if(x>p_c_x){
                            touchIv.setX(neiCircleW+(float) final_x);
                        }else{
                            touchIv.setX(neiCircleW-(float) final_x);
                        }
                        if(y>p_c_y){
                            touchIv.setY((neiCircleW+(float) final_y));
                        }else{
                            touchIv.setY((neiCircleW-(float) final_y));
                        }
                    }else{//手指在大圆内
                        touchIv.setX(x-width/2);
                        touchIv.setY(y-height/2);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                final View toCapture = findTopChildUnder((int) x, (int) y);
                if(toCapture!=null){
                    isSelect=true;
                }else{
                    isSelect=false;
                }
                break;
            case  MotionEvent.ACTION_UP:
                isSelect=false;
                touchIv.setX(autoBackViewOriginLeft);
                touchIv.setY(autoBackViewOriginTop);
                break;
            default:
                break;
        }
        return true;
    }
}
