package com.example.widgetcollection.View.ChartView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.widgetcollection.R;
import com.example.widgetcollection.Utils.GetDpSpUtils;

//折线图和柱状图的基类,主要完成了获取参数配置,以及横纵坐标轴的绘制
public class BaseChartView extends View {

    //设置动画的时间
    protected int duration = 1000;
    //设置横坐标分段数量
    protected int xSection = 10;
    //设置纵坐标分段数量
    protected int ySection = 10;
    //true表示只有折线或者只有柱子,
    //false表示既有折线又有柱子(默认左边的纵坐标表示折线,右边的纵坐标表示柱子)
    protected boolean singleType = true;
    //是否显示纵向分割线及单位间距,由实现类绘制,基于section和最大最小值计算
    protected boolean showXDivider = false;
    //是否显示点在坐标轴的位置投影,由实现类绘制,基于点计算
    protected boolean showPosition = false;
    //是否显示横向分割线及单位间距,由实现类绘制,基于section和最大最小值计算
    protected boolean showYDivider = false;
    //是否设置断层
    protected boolean containFault = false;

    protected TypedArray typedArray;
    //用户不额外设置布局时使用的默认值
    protected float MarginTop = 100;

    protected float MarginBottom = 100;

    protected float MarginLeft = 100;

    protected float MarginRight = 100;

    protected float mYLabelSize = 50;

    protected float mXLabelSize = 35;

    protected float mXUnitTextSize;

    protected float mYUnitTextSize;


    // 圆半径
    protected int circleRadius = 8;

    protected int lineStrokeWidth = 3;

    protected int dataStrokeWidth = 3;

    // 绘制X轴总长度,根据布局计算得来
    protected float xLength;
    // 绘制Y轴总长度,根据布局计算得来
    protected float yLength;
    // 断层时横坐标偏移量
    protected float xFault;
    // 断层时横坐标偏移量
    protected float yFault;

    // 柱状图柱子宽度
    protected float barWidth;

    // X轴第1个节点的偏移位置
    protected float xFirstPointOffset;

    // 坐标轴颜色
    protected int lineColor;
    // 单位颜色
    protected int unitColor;
    //背景颜色
    private int background = Color.BLACK;
    // 原点坐标
    protected float xPoint;
    protected float yPoint;
    //绘制横纵轴的paint
    private Paint linePaint;
    //动画相关
    private Path xDstPath, yDstPath, anotherYDstPath;
    private PathMeasure xMeasure, yMeasure, anotherYMeasure;
    private ValueAnimator coordinateValueAnimator;
    protected float mCurrentValue = 0;

    //只设置点列表时的构造方法
    public BaseChartView(Context context) {
        this(context, null);
        init();
    }

    //设置点列表和AttributeSet时的构造方法
    public BaseChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseChartView);
        setAttrbutesValue();
        init();
        typedArray.recycle();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initPaint();
        startAnimation();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(lineStrokeWidth);
    }

    /**
     * 初始化宽高比例等数据
     */
    public void initParams(int width, int height) {
        yPoint = height - MarginBottom + mYLabelSize / 3;
        xLength = width - MarginLeft - MarginRight
                - (MarginRight + MarginLeft) / 16;
        yLength = height - MarginTop - MarginBottom;
        xPoint = MarginLeft;
    }

    private void initPath() {
        //横纵坐标轴的路径
        Path xCoordinatorPath, yCoordinatorPath, anotherYCoordinatorPath;
        xCoordinatorPath = new Path();
        yCoordinatorPath = new Path();
        xDstPath = new Path();
        yDstPath = new Path();
        xCoordinatorPath.moveTo(xPoint, yPoint);
        xCoordinatorPath.lineTo(xLength + MarginLeft, yPoint);
        yCoordinatorPath.moveTo(xPoint, yPoint);
        yCoordinatorPath.lineTo(xPoint, yPoint - yLength);
        xMeasure = new PathMeasure(xCoordinatorPath, true);
        yMeasure = new PathMeasure(yCoordinatorPath, true);
        //多种类型时需要绘制两条y轴
        if (!singleType) {
            anotherYCoordinatorPath = new Path();
            anotherYDstPath = new Path();
            anotherYCoordinatorPath.moveTo(xLength + MarginLeft, yPoint);
            anotherYCoordinatorPath.lineTo(xLength + MarginLeft, yPoint - yLength);
            anotherYMeasure = new PathMeasure(anotherYCoordinatorPath, true);
        }
    }

    //根据attribute设置属性
    private void setAttrbutesValue() {
        float MarginTopPx = typedArray.getDimension(
                R.styleable.BaseChartView_margin_top, 500);
        float MarginBottomPx = typedArray.getDimension(
                R.styleable.BaseChartView_margin_bottom, 500);
        float MarginLeftPx = typedArray.getDimension(
                R.styleable.BaseChartView_margin_left, 500);
        float MarginRightPx = typedArray.getDimension(
                R.styleable.BaseChartView_margin_right, 500);

        float barWidthPx = typedArray.getDimension(
                R.styleable.BaseChartView_bar_width, 200);
        float xFaultPx = typedArray.getDimension(
                R.styleable.BaseChartView_x_fault, 500);
        float yFaultPx = typedArray.getDimension(
                R.styleable.BaseChartView_y_fault, 500);

        float yLabelSizePx = typedArray.getDimension(
                R.styleable.BaseChartView_ylabel_text_size, 30);
        float xLabelSizePx = typedArray.getDimension(
                R.styleable.BaseChartView_xlabel_text_size, 20);
        float xUnitSizePx = typedArray.getDimension(
                R.styleable.BaseChartView_x_unit_text_size, 30);
        float yUnitSizePx = typedArray.getDimension(
                R.styleable.BaseChartView_y_unit_text_size, 30);

        float xFirstPointOffsetPx = typedArray.getDimension(
                R.styleable.BaseChartView_x_first_point_offset, 30);
        float lineStrokeWidthPx = typedArray.getDimension(
                R.styleable.BaseChartView_line_stroke_width, 5);
        float dataStrokeWidthPx = typedArray.getDimension(
                R.styleable.BaseChartView_data_stroke_width, 5);
        float circleRadiusPx = typedArray.getDimension(
                R.styleable.BaseChartView_circle_radius, 6);

        duration = typedArray.getInt(R.styleable.BaseChartView_duration, 1000);
        xSection = typedArray.getInt(R.styleable.BaseChartView_x_section, 5);
        ySection = typedArray.getInt(R.styleable.BaseChartView_y_section, 5);
        singleType = typedArray.getBoolean(R.styleable.BaseChartView_single_type, true);
        showXDivider = typedArray.getBoolean(R.styleable.BaseChartView_show_x_divider, false);
        showYDivider = typedArray.getBoolean(R.styleable.BaseChartView_show_y_divider, false);
        showPosition = typedArray.getBoolean(R.styleable.BaseChartView_show_position, false);
        containFault = typedArray.getBoolean(R.styleable.BaseChartView_contain_fault, false);
        xFirstPointOffset = GetDpSpUtils.px2sp(getContext(),
                xFirstPointOffsetPx);

        MarginTop = GetDpSpUtils.px2dip(getContext(), MarginTopPx);
        MarginBottom = GetDpSpUtils.px2dip(getContext(), MarginBottomPx);
        MarginLeft = GetDpSpUtils.px2dip(getContext(), MarginLeftPx);
        MarginRight = GetDpSpUtils.px2dip(getContext(), MarginRightPx);
        barWidth = GetDpSpUtils.px2dip(getContext(), barWidthPx);

        xFault = GetDpSpUtils.px2dip(getContext(), xFaultPx);
        yFault = GetDpSpUtils.px2dip(getContext(), yFaultPx);

        mYLabelSize = GetDpSpUtils.px2sp(getContext(), yLabelSizePx);
        mXLabelSize = GetDpSpUtils.px2sp(getContext(), xLabelSizePx);

        mXUnitTextSize = GetDpSpUtils.px2sp(getContext(), xUnitSizePx);
        mYUnitTextSize = GetDpSpUtils.px2sp(getContext(), yUnitSizePx);

        lineStrokeWidth = GetDpSpUtils.px2sp(getContext(), lineStrokeWidthPx);
        dataStrokeWidth = GetDpSpUtils.px2sp(getContext(), dataStrokeWidthPx);
        circleRadius = GetDpSpUtils.px2sp(getContext(), circleRadiusPx);

        lineColor = typedArray.getColor(R.styleable.BaseChartView_line_color,
                getResources().getColor(R.color.saswell_yellow));
        background = typedArray.getColor(R.styleable.BaseChartView_background_color,
                getResources().getColor(R.color.saswell_black));
        unitColor = typedArray.getColor(R.styleable.BaseChartView_unit_color,
                getResources().getColor(R.color.saswell_light_grey));
    }


    // 画横轴
    private void drawXLine(Canvas canvas, Paint p) {
        float stop = xMeasure.getLength() * mCurrentValue;
        xDstPath.reset();
        xMeasure.getSegment(0, stop, xDstPath, true);
        p.setColor(getResources().getColor(R.color.saswell_yellow));
        canvas.drawPath(xDstPath, p);
    }

    // 画纵轴
    private void drawYLine(Canvas canvas, Paint p) {
        p.setColor(getResources().getColor(R.color.saswell_yellow));
        float stop = yMeasure.getLength() * mCurrentValue;
        yDstPath.reset();
        yMeasure.getSegment(0, stop, yDstPath, true);
        canvas.drawPath(yDstPath, p);
        //多种类型时需要绘制两条y轴
        if (!singleType) {
            stop = anotherYMeasure.getLength() * mCurrentValue;
            anotherYDstPath.reset();
            anotherYMeasure.getSegment(0, stop, anotherYDstPath, true);
            canvas.drawPath(anotherYDstPath, p);
        }
    }

    private void startAnimation() {
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(background);
        drawXLine(canvas, linePaint);
        drawYLine(canvas, linePaint);
    }

    //顶不住了,先把测量相关逻辑写在onMeasure后面,之后看看怎么优化吧
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initParams(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        initPath();
    }
}
