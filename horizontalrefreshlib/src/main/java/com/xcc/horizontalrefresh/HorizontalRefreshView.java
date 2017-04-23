package com.xcc.horizontalrefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Administrator on 2017/4/8.
 * 横向刷新
 */
public class HorizontalRefreshView extends FrameLayout {
    public HorizontalRefreshView(Context context) {
        super(context);
        init(context);
    }

    public HorizontalRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init(context);
    }

    private AttributeSet attrs;

    private void init(Context context) {
        String text1 = "滑动加载", text2 = "松开加载";
        int bgColor = 0;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalRefreshView);
            maxW = a.getDimension(R.styleable.HorizontalRefreshView_maxW, 100);
            text1 = a.getString(R.styleable.HorizontalRefreshView_text1);
            text2 = a.getString(R.styleable.HorizontalRefreshView_text2);
            bgColor = a.getColor(R.styleable.HorizontalRefreshView_bgColor, 0);
            if (TextUtils.isEmpty(text1)) text1 = "滑动加载";
            if (TextUtils.isEmpty(text2)) text2 = "松开加载";
        }
        leftView = new MoreView(context);
        leftView.setId(leftViewId);
        leftView.setIsStats(1);
        leftView.setText(text1, text2);
        leftView.setBgColor(bgColor);//设置拖动出来的颜色

        rightView = new MoreView(context);
        rightView.setId(rightViewId);
        rightView.setIsStats(2);
        rightView.setText(text1, text2);
        rightView.setBgColor(bgColor);

        LayoutParams params = new LayoutParams((int) maxW, -1);
        addView(leftView, params);
        params = new LayoutParams((int) maxW, -1);
        addView(rightView, params);
    }

    private View mChildView;
    private int canRefresh = 3;//可以刷新的位置，1左，2右
    private MoreView leftView, rightView;
    private int leftViewId = 11, rightViewId = 12;
    private int scrollStats = 0;//滚动状态 0可以滚，1向左边不可以，2向右边不可以
    private float scrollW;//手指滑动宽度
    private float maxW = 200;//贝塞尔曲线最大范围
    private OnHorizontalRefresh onHorizontalRefresh;
    private int r, b;

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        r = r - l;
        this.r = r;
        b = b - t;
        this.b = b;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getId() == leftViewId) {//左边
                childAt.layout(0, 0, 0, b);//将控件宽度设置为0
            } else if (childAt.getId() == rightViewId) {
                childAt.layout(r, 0, 0, b);
            } else {
                mChildView = childAt;//获取其中要滚动的view，可以是listview
            }
        }
    }


    private float ox;

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ox = event.getX();//按下时的位置
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                x = ox - x;//计算移动手指的偏移
                if (x > 0 && !canScrollLeft()) {//判断是否可以向左滑
                    scrollStats = 2;
                    return true;
                } else if (x < 0 && !canScrollRight()) {//判断是否可以向右滑
                    scrollStats = 1;
                    return true;
                }
        }
        return super.onInterceptTouchEvent(event);
    }

    private float oldX;

    public boolean onTouchEvent(MotionEvent event) {
        if (scrollStats != 0)
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    if (oldX == 0) oldX = x;//第一次监听到移动
                    else {//计算移动偏移
                        scrollW = oldX - x;
                        ViewHelper.setTranslationX(mChildView, -scrollW);//平移控件
                        Log.v("--scrollW--", "" + scrollW);
                        Log.v("--scrollStats--", "" + scrollStats);
                        float scrollW = Math.abs(this.scrollW);
                        //显示左右的提示控件
                        if (scrollStats == 1) {
                            leftView.layout(0, 0, (int) (scrollW > maxW ? maxW : scrollW), b);
                            leftView.setShowStats(scrollW > maxW ? 2 : 1);
                        } else if (scrollStats == 2) {
                            rightView.layout((int) (r - (scrollW > maxW ? maxW : scrollW)), 0, r, b);
                            rightView.setShowStats(scrollW > maxW ? 2 : 1);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //结束滑动
                    oldX = 0;
                    //开启动画
                    startAnim();
                    Log.v("--ACTION_UP--", "ACTION_UP");
                    //触发监听事件
                    float scrollW = Math.abs(this.scrollW);
                    if (onHorizontalRefresh != null && scrollW > maxW) {
                        if (scrollStats == 1) onHorizontalRefresh.OnLeftRefresh(this);
                        else onHorizontalRefresh.OnRightRefresh(this);
                    }
                    return true;
            }
        return super.onTouchEvent(event);
    }

    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(scrollW, 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scrollW = (float) valueAnimator.getAnimatedValue();
                ViewHelper.setTranslationX(mChildView, -scrollW);
                float scrollW = Math.abs(HorizontalRefreshView.this.scrollW);
                if (scrollStats == 1) {
                    leftView.layout(0, 0, (int) (scrollW > maxW ? maxW : scrollW), b);
                } else if (scrollStats == 2) {
                    rightView.layout((int) (r - (scrollW > maxW ? maxW : scrollW)), 0, r, b);
                }
                //invalidate();
            }
        });
        animator.start();
    }

    private boolean canScrollRight() {//向右
        if (mChildView == null || (canRefresh & 1) == 0) {
            return true;
        }
        return ViewCompat.canScrollHorizontally(mChildView, -1);
    }

    private boolean canScrollLeft() {//向左
        if (mChildView == null || (canRefresh & 2) == 0) {
            return true;
        }
        return ViewCompat.canScrollHorizontally(mChildView, 1);
    }

    public void setOnHorizontalRefresh(OnHorizontalRefresh onHorizontalRefresh) {
        this.onHorizontalRefresh = onHorizontalRefresh;
    }

    public interface OnHorizontalRefresh {
        void OnRightRefresh(HorizontalRefreshView view);

        void OnLeftRefresh(HorizontalRefreshView view);
    }
}
