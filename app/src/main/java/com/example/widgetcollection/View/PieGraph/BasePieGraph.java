package com.example.widgetcollection.View.PieGraph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.widgetcollection.R;
import com.example.widgetcollection.Utils.GetDpSpUtils;

//饼图的基类,主要完成获取参数配置,计算圆心坐标
public class BasePieGraph extends View {

    //设置动画的时间
    protected int duration = 1000;
    //设置内半径
    protected float innerRadius = 100;
    //设置外半径
    protected float outerRadius = 200;
    //数据是否显示在饼图中
    protected boolean dataInGraph = true;
    //是否开启动画
    protected boolean showAnimation = true;
    //是否是百分比模式
    protected boolean percentageMode = false;
    //设置数据字体大小
    protected float dataTextSize = 20;
    //设置中心字体大小
    protected float centerTextSize = 20;


    protected TypedArray typedArray;
    //用户不额外设置布局时使用的默认值
    protected float MarginTop = 100;

    protected float MarginBottom = 100;

    protected float MarginLeft = 100;

    protected float  MarginRight = 100;

    //背景颜色
    protected int background = Color.WHITE;
    //字体颜色
    protected int dataTextColor = Color.BLACK;
    // 原点坐标
    protected float xPoint;
    protected float yPoint;

    public BasePieGraph(Context context) {
        super(context);
        init();
    }

    public BasePieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePieGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePieGraph);
        setAttrbutesValue();
        init();
        typedArray.recycle();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    //根据attribute设置属性
    private void setAttrbutesValue() {
        float MarginTopPx = typedArray.getDimension(
                R.styleable.BasePieGraph_margin_top, 0);
        float MarginBottomPx = typedArray.getDimension(
                R.styleable.BasePieGraph_margin_bottom, 0);
        float MarginLeftPx = typedArray.getDimension(
                R.styleable.BasePieGraph_margin_left, 0);
        float MarginRightPx = typedArray.getDimension(
                R.styleable.BasePieGraph_margin_right, 0);

        float outerRadiusPx = typedArray.getDimension(
                R.styleable.BasePieGraph_outer_radius, 2000);
        float innerRadiusPx = typedArray.getDimension(
                R.styleable.BasePieGraph_inner_radius, 1000);
        float dataTextSizePx = typedArray.getDimension(
                R.styleable.BasePieGraph_data_text_size, 50);
        float centerTextSizePx = typedArray.getDimension(
                R.styleable.BasePieGraph_center_text_size, 60);


        duration = typedArray.getInt(R.styleable.BasePieGraph_duration, 1000);

        MarginTop = GetDpSpUtils.px2dip(getContext(), MarginTopPx);
        MarginBottom = GetDpSpUtils.px2dip(getContext(), MarginBottomPx);
        MarginLeft = GetDpSpUtils.px2dip(getContext(), MarginLeftPx);
        MarginRight = GetDpSpUtils.px2dip(getContext(), MarginRightPx);

        outerRadius = GetDpSpUtils.px2dip(getContext(), outerRadiusPx);
        innerRadius = GetDpSpUtils.px2dip(getContext(), innerRadiusPx);

        dataTextSize = GetDpSpUtils.px2dip(getContext(), dataTextSizePx);
        centerTextSize = GetDpSpUtils.px2dip(getContext(), centerTextSizePx);

        showAnimation = typedArray.getBoolean(R.styleable.BasePieGraph_show_animation, false);
        dataInGraph = typedArray.getBoolean(R.styleable.BasePieGraph_data_in_graph, true);
        percentageMode = typedArray.getBoolean(R.styleable.BasePieGraph_percentage_mode, false);

        background = typedArray.getColor(R.styleable.BasePieGraph_background_color,
                getResources().getColor(R.color.saswell_white));
        dataTextColor = typedArray.getColor(R.styleable.BasePieGraph_data_text_color,
                getResources().getColor(R.color.saswell_black));

        //用户将外半径设置比内半径小时
        if(outerRadius <= innerRadius){
            outerRadius = innerRadius + 100;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xPoint = (MeasureSpec.getSize(widthMeasureSpec) - MarginRight - MarginLeft) / 2;
        yPoint = (MeasureSpec.getSize(heightMeasureSpec) - MarginBottom - MarginTop) / 2;
    }
}
