package com.liunian.ptzdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    PTZView2 ptzView2;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.fangxaing);
        ptzView2 = (PTZView2) findViewById(R.id.ptzView2);
        ptzView2.setEnableDistance(200f);
        ptzView2.setIsFromFingerWithClick(true);
        ptzView2.setOnWheelTouchListener(new OnWheelTouchListener() {
            @Override
            public void onEnableDistance(MotionEvent ev) {
//                Log.i("lmly","onEnableDistance");

            }

            @Override
            public void onRelease() {
                textView.setText("没有选择方向，请触摸紫色圆盘");
            }
        });
        ptzView2.setOnDirectionLsn(new OnDirectionLsn() {
            @Override
            public void top() {
                textView.setText("当前选中方向为：上");
            }

            @Override
            public void left() {
                textView.setText("当前选中方向为：左");
            }

            @Override
            public void bottom() {
                textView.setText("当前选中方向为：下");
            }

            @Override
            public void right() {
                textView.setText("当前选中方向为：右");
            }
        });
    }
}
