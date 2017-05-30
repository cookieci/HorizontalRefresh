package com.qxlibrary.widget.horizontalrefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.qxlibrary.R;
import com.qxlibrary.Sysout;

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
        leftView.setBgColor(bgColor);
//        leftView.
        rightView = new MoreView(context);
        rightView.setId(rightViewId);
        rightView.setIsStats(2);
        rightView.setText(text1, text2);
        rightView.setBgColor(bgColor);

        LayoutParams params = new LayoutParams((int) maxW, -1);
        addView(leftView, params);
        params = new LayoutParams((int) maxW, -1);
        addView(rightView, params);

        //setWillNotDraw(false);
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setTextSize(20);
//        path = new Path();
    }

    //    private Paint paint;
//    private Path path;
    private View mChildView;
    private int canRefresh = 3;//可以刷新的位置，1左，2右
    private MoreView leftView, rightView;
    private int leftViewId = 11, rightViewId = 12;
    //private int rereshColor = 0xFFffFF00;//刷新颜色
    //private int textColor = 0xFF000000;//刷新颜色
    //private String refreshTextL = "加载刷新";//刷新文字
    //private String refreshTextR = "加载刷新";//刷新文字
    //private int refreshDist = 200;//刷新距离
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
                childAt.layout(0, 0, 0, b);
            } else if (childAt.getId() == rightViewId) {
                childAt.layout(r, 0, 0, b);
            } else {
                mChildView = childAt;
                //childAt.layout(l, t, r, b);
            }
        }
    }


//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //Sysout.v("--onDraw--", "super.onDraw(canvas);");
//        if (scrollStats == 0) return;
//        paint.setColor(rereshColor);
//        path.reset();
//        int width = getWidth();
//        int height = getHeight();
//        float abs = Math.abs(scrollW);
//        if (abs > maxW) abs = maxW;
//        float textx;
//        if (scrollStats == 1) {
//            path.moveTo(0, 0);
//            textx = abs / 2.0f;
//            path.lineTo(abs / 2.0f, 0);
//            path.rQuadTo(abs, height / 2.0f, 0, height);
//            path.lineTo(0, height);
//        } else {
//            path.moveTo(width, 0);
//            textx = width - abs / 2.0f;
//            path.lineTo(width - abs / 2.0f, 0);
//            path.rQuadTo(-abs, height / 2.0f, /*width - 10*/0, height);
//            path.lineTo(width, height);
//        }
//        canvas.drawPath(path, paint);
//        paint.setColor(textColor);
//        canvas.drawText("加载刷新", textx, height / 2.0f, paint);
//    }

    private float ox;

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ox = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                x = ox - x;
                if (x > 0 && !canScrollLeft()) {
                    scrollStats = 2;
                    return true;
                } else if (x < 0 && !canScrollRight()) {
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
                    if (oldX == 0) oldX = x;
                    else {
                        scrollW = oldX - x;
                        //scrollW = Math.abs(scrollW);
                        Sysout.v("--scrollW--", "" + scrollW);
                        Sysout.v("--scrollStats--", "" + scrollStats);
                        //invalidate();
                        float scrollW = 0;
                        if (scrollStats == 1) {
                            if (this.scrollW > 0) this.scrollW = 0;
                            else scrollW = Math.abs(this.scrollW);
                            ViewHelper.setTranslationX(mChildView, scrollW);
                            leftView.layout(0, 0, (int) (scrollW > maxW ? maxW : scrollW), b);
//                            leftView.invalidate();
                            leftView.setShowStats(scrollW > maxW ? 2 : 1);
                        } else if (scrollStats == 2) {
                            if (this.scrollW < 0) this.scrollW = 0;
                            else scrollW = Math.abs(this.scrollW);
                            ViewHelper.setTranslationX(mChildView, -scrollW);
                            rightView.layout((int) (r - (scrollW > maxW ? maxW : scrollW)), 0, r, b);
                            rightView.setShowStats(scrollW > maxW ? 2 : 1);
//                            rightView.invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    oldX = 0;
                    startAnim();
                    Sysout.v("--ACTION_UP--", "ACTION_UP");
                    //触发监听事件
                    float scrollW = Math.abs(this.scrollW);
                    if (onHorizontalRefresh != null && scrollW > maxW) {
                        if (scrollStats == 1) onHorizontalRefresh.OnLeftRefresh(this);
                        else onHorizontalRefresh.OnRightRefresh(this);
                    }
                    //开启动画
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

    public void setCanRefresh(int canRefresh) {
        this.canRefresh = canRefresh;
    }

    public void setOnHorizontalRefresh(OnHorizontalRefresh onHorizontalRefresh) {
        this.onHorizontalRefresh = onHorizontalRefresh;
    }

    public interface OnHorizontalRefresh {
        void OnRightRefresh(HorizontalRefreshView view);

        void OnLeftRefresh(HorizontalRefreshView view);
    }
}
