package com.xcc.horizontalrefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/4/16.
 */
public class MoreView extends LinearLayout {
    public MoreView(Context context) {
        super(context);
        init(context);
    }

    public MoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_load_more, null);
        LayoutParams params = new LayoutParams(-1, -1);
        addView(inflate, params);
        ivRefreshArrow = inflate.findViewById(R.id.ivRefreshArrow);
        textView = (TextView) inflate.findViewById(R.id.tvMoreText);

        setWillNotDraw(false);
        setGravity(Gravity.CENTER);


        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        path = new Path();
    }


    public void setIsStats(int isStats) {
        this.isStats = isStats;
        if (isStats == 1) {
            inflate.removeView(ivRefreshArrow);
            inflate.addView(ivRefreshArrow);
        }
    }

    //显示状态:0空闲,1拖动中,2拖到极限未松手
    public void setShowStats(int showStats) {
        if (this.showStats == showStats) return;
        this.showStats = showStats;
        RotateAnimation animation;
        switch (showStats) {
            case 0:
                break;
            case 1:
//                ViewHelper.setRotation(ivRefreshArrow, 0);
                if (isStats == 1)
                    animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                else
                    animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(3);
                animation.setFillAfter(true);
                ivRefreshArrow.startAnimation(animation);
                textView.setText(text1);
                break;
            case 2:
                if (isStats == 1)
                    animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                else
                    animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(300);
                animation.setFillAfter(true);
                ivRefreshArrow.startAnimation(animation);
                textView.setText(text2);
                break;
        }
    }

    private ViewGroup inflate;
    private int isStats;//位置:1左边，2右边
    private int showStats;//显示状态:0空闲,1拖动中,2拖到极限未松手
    private String text1 = "滑动加载", text2 = "松开加载";
    private Paint paint;
    private Path path;
    private int bgColor = 0xFFff00FF;//刷新颜色
    private View ivRefreshArrow;
    private TextView textView;

    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;
//        Log.v("--width--", "" + width);
//        Log.v("--height--", "" + height);
        //float textx;
        paint.setColor(bgColor);
        path.reset();
        if (isStats == 1) {
            path.moveTo(0, 0);
            //textx = abs / 2.0f;
            path.lineTo(width / 2.0f, 0);
            path.rQuadTo(width / 2.0f, height / 2.0f, 0, height);
            path.lineTo(0, height);
        } else {
            path.moveTo(width, 0);
            //textx = width - abs / 2.0f;
            path.lineTo(width / 2.0f, 0);
            path.rQuadTo(-width / 2.0f, height / 2.0f, 0, height);
            path.lineTo(width, height);
        }
        canvas.drawPath(path, paint);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setText(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
    }
}
