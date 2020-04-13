package com.example.widgetcollection.View.loading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.widgetcollection.R;
import com.example.widgetcollection.Utils.GetDpSpUtils;

//加载view的基类用于获取配置参数
public class BaseLoadingView extends View {

    private TypedArray typedArray;

    //动画的时间
    protected int duration = 1000;
    //字体颜色
    protected int textColor = Color.BLACK;
    //字体大小
    protected float textSize = 20;
    //字体
    protected String text = "加载中...";

    //view宽度高度
    protected float width;
    protected float height;

    //动画相关
    private ValueAnimator coordinateValueAnimator;
    protected float mCurrentValue;

    public BaseLoadingView(Context context) {
        super(context);
        initAnimation();
    }

    public BaseLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseLoadingView);
        setAttributesValue();
        typedArray.recycle();
        initAnimation();
    }

    private void setAttributesValue() {
        float textSizePx = typedArray.getDimension(
                R.styleable.BaseLoadingView_data_text_size, 50);

        duration = typedArray.getInt(R.styleable.BaseLoadingView_duration, 1000);

        textSize = GetDpSpUtils.px2dip(getContext(), textSizePx);

        textColor = typedArray.getColor(R.styleable.BaseLoadingView_data_text_color,
                getResources().getColor(R.color.saswell_black));

        text = typedArray.getString(R.styleable.BaseLoadingView_text);
        if (null == text || TextUtils.isEmpty(text)) {
            text = "加载中...";
        }
    }

    private void initAnimation() {
        if (coordinateValueAnimator != null) {
            coordinateValueAnimator.cancel();
        }
        coordinateValueAnimator = ValueAnimator.ofFloat(0,
                1);
        coordinateValueAnimator.setDuration(duration);
        // 减速插值器
        coordinateValueAnimator.setInterpolator(new DecelerateInterpolator());
        coordinateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        coordinateValueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }
}
