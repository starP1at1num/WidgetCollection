package com.example.widgetcollection.View.PieGraph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.widgetcollection.JavaBean.PieGraph.BarPieVO;
import com.example.widgetcollection.JavaBean.PieGraph.SectorVO;
import com.example.widgetcollection.R;
import com.example.widgetcollection.Utils.GetDpSpUtils;

import java.util.ArrayList;


//柱状饼图
public class BarPieGraph extends View {

    //设置动画的时间
    private int duration = 1000;
    //是否开启动画
    private boolean showAnimation = true;
    //动画种类,旋转或者延伸
    private int animationType;
    private static int rotate = 1;
    private static int extend = 2;
    //设置数据字体大小
    private float dataTextSize = 20;

    //中心画笔
    private Paint centerPaint;
    //矩形画笔
    private Paint rectPaint;
    //数据画笔
    private Paint dataPaint;

    //待绘制的矩形
    private Rect rect;
    //数据用的辅助矩形
    private Rect dataRect;
    //数据用的辅助矩形
    private Rect dataTextRect;
    //类型是扇形时用的辅助矩形
    private RectF arcRect;

    protected TypedArray typedArray;
    //用户不额外设置布局时使用的默认值
    private float MarginTop = 100;

    private float MarginBottom = 100;

    private float MarginLeft = 100;

    private float MarginRight = 100;

    private float radius = 100;

    //数据颜色
    private int dataTextColor = Color.BLACK;
    //圆内背景颜色
    private int innerCircleColor = Color.BLACK;
    // 原点坐标
    private float xPoint;
    private float yPoint;

    private int width;
    private int height;

    //动画相关
    private ValueAnimator coordinateValueAnimator;
    private float mCurrentValue;

    //计算起始角度
    private ArrayList<Float> startAngleList;
    //每个矩形所占角度
    private float proportion;

    //数据集合
    private BarPieVO barPieVO;

    float startHeight, startProportion;
    float left, right, bottom;
    int sectorPictureCutHeight;
    Bitmap src;

    public BarPieGraph(Context context) {
        super(context);
        init();
    }

    public BarPieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarPieGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BarPieGraph);
        setAttributesValue();
        typedArray.recycle();
    }

    public BarPieGraph(Context context, BarPieVO barPieVO) {
        this(context, null, barPieVO);
    }

    public BarPieGraph(Context context, AttributeSet attrs, BarPieVO barPieVO) {
        this(context, attrs, 0);
        if (null != barPieVO) {
            this.barPieVO = barPieVO;
            init();
        }
    }

    //设置pieVo的数据
    public void setData(BarPieVO barPieVO) {
        if (null != barPieVO) {
            this.barPieVO = barPieVO;
            init();
            postInvalidate();
        }
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //初始化画笔
        //中心画笔
        centerPaint = new Paint();
        centerPaint.setStrokeWidth(1);
        centerPaint.setAntiAlias(true);
        centerPaint.setStyle(Paint.Style.STROKE);

        //矩形画笔
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.STROKE);

        //数据画笔
        dataPaint = new Paint();
        dataPaint.setStrokeWidth(1);
        dataPaint.setTextSize(dataTextSize);
        dataPaint.setAntiAlias(true);

        dataRect = new Rect();
        dataTextRect = new Rect();

        proportion = 360 / barPieVO.getSectorVOList().size();

        //如果开启动画
        if (showAnimation) {
            if (coordinateValueAnimator != null) {
                coordinateValueAnimator.cancel();
            }
            if (animationType == rotate) {
                coordinateValueAnimator = ValueAnimator.ofFloat(270, -90);
            } else {
                coordinateValueAnimator = ValueAnimator.ofFloat(0,
                        1);
            }
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

            startAngleList = new ArrayList<>();
            for (int i = 0; i < barPieVO.getSectorVOList().size(); i++) {
                startAngleList.add(270 - proportion * i);
            }
        }
    }

    /**
     * 根据attribute设置属性
     */
    private void setAttributesValue() {
        float MarginTopPx = typedArray.getDimension(
                R.styleable.BarPieGraph_margin_top, 0);
        float MarginBottomPx = typedArray.getDimension(
                R.styleable.BarPieGraph_margin_bottom, 0);
        float MarginLeftPx = typedArray.getDimension(
                R.styleable.BarPieGraph_margin_left, 0);
        float MarginRightPx = typedArray.getDimension(
                R.styleable.BarPieGraph_margin_right, 0);

        float dataTextSizePx = typedArray.getDimension(
                R.styleable.BarPieGraph_data_text_size, 50);

        float radiusPx = typedArray.getDimension(
                R.styleable.BarPieGraph_radius, 500);

        duration = typedArray.getInt(R.styleable.BasePieGraph_duration, 1000);

        MarginTop = GetDpSpUtils.px2dip(getContext(), MarginTopPx);
        MarginBottom = GetDpSpUtils.px2dip(getContext(), MarginBottomPx);
        MarginLeft = GetDpSpUtils.px2dip(getContext(), MarginLeftPx);
        MarginRight = GetDpSpUtils.px2dip(getContext(), MarginRightPx);
        radius = GetDpSpUtils.px2dip(getContext(), radiusPx);

        dataTextSize = GetDpSpUtils.px2dip(getContext(), dataTextSizePx);

        showAnimation = typedArray.getBoolean(R.styleable.BarPieGraph_show_animation, false);

        dataTextColor = typedArray.getColor(R.styleable.BarPieGraph_data_text_color,
                getResources().getColor(R.color.saswell_black));
        innerCircleColor = typedArray.getColor(R.styleable.BarPieGraph_inner_circle_color,
                getResources().getColor(R.color.saswell_white));

        if (showAnimation) {
            animationType = typedArray.getInt(R.styleable.BarPieGraph_animation_type, 1);
        }
    }

    private void initParams() {
        //初始化圆心图片/文字参数
        if (!barPieVO.isCenterIsPicture()) {
            //文字就计算文字的大小
            rect = new Rect();
            centerPaint.getTextBounds(barPieVO.getCenterText(), 0, barPieVO.getCenterText().length(), rect);
        } else {
            //图片就计算内圆可容下的图片的大小
            //高度约等于0.707乘以半径
            //宽度按照bitmap的宽高比基于高度计算出来
            int heightOffset;
            int widthOffset;
            if (barPieVO.getAspectRatio() < 1) {
                //宽高比小于1时,以长度为基准
                heightOffset = (int) (0.707 * radius);
                widthOffset = (int) (heightOffset * barPieVO.getAspectRatio());
            } else {
                //宽高比大于1时,以宽3度为基准
                widthOffset = (int) (0.707 * radius);
                heightOffset = (int) (widthOffset * barPieVO.getAspectRatio());
            }
            rect = new Rect((int) xPoint - widthOffset, (int) yPoint - heightOffset, (int) xPoint + widthOffset, (int) yPoint + heightOffset);
        }
        switch (barPieVO.getSectorType()) {
            case BarPieVO.RECT:
                left = xPoint - barPieVO.getSectorRectWidth() / 2;
                right = xPoint + barPieVO.getSectorRectWidth() / 2;
                startHeight = barPieVO.getSectorRectHeight();
                break;
            case BarPieVO.ARC:
                startHeight = barPieVO.getSectorArcHeight();
                break;
            case BarPieVO.NORMAL_BITMAP:
                src = barPieVO.getSectorPicture();
                left = xPoint - src.getWidth() / 2f;
                right = xPoint + src.getWidth() / 2f;
                startHeight = src.getHeight();
                break;
            case BarPieVO.CUT_BITMAP:
                src = barPieVO.getSectorPicture();
                left = xPoint - src.getWidth() / 2f;
                right = xPoint + src.getWidth() / 2f;
                startHeight = src.getHeight();
                sectorPictureCutHeight = barPieVO.getSectorPictureCutHeight();
                break;
            case 0:
                break;
        }
        startProportion = barPieVO.getSectorVOList().get(0).getProportion();
        bottom = yPoint;
    }

    /**
     * 绘制圆心文字/图片
     */
    private void drawCenter(Canvas canvas) {
        centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        centerPaint.setColor(innerCircleColor);
        canvas.drawCircle(xPoint, yPoint, radius, centerPaint);
        if (!barPieVO.isCenterIsPicture()) {
            //绘制中心文字
            centerPaint.setColor(barPieVO.getCenterTextColor());
            canvas.drawText(barPieVO.getCenterText(), xPoint - rect.width() / 2.0f, yPoint + rect.height() / 2.0f, centerPaint);
        } else {
            //绘制中心图片
            Bitmap src = barPieVO.getCenterPicture();
            Matrix m = new Matrix();
            m.setScale((float) (rect.right - rect.left) / src.getWidth(), (float) (rect.bottom - rect.top) / src.getHeight());
            Bitmap target = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
            canvas.drawBitmap(target, rect.left, rect.top, centerPaint);
            target.recycle();
        }
    }

    /**
     * 绘制扇形,从第0个到第n个
     */
    private void drawSector(Canvas canvas, int n) {
        //从y轴开始逆时针
        float top;
        Bitmap targetBitmap;
        //以矩形中心点旋转图形,先旋转一次,让最后一条数据显示在90度上
        canvas.rotate(360 - proportion, xPoint, yPoint);
        switch (barPieVO.getSectorType()) {
            case BarPieVO.RECT:
                drawSectorRect(canvas, n);
                break;
            case BarPieVO.ARC:
                drawSectorArc(canvas, n);
                break;
            case BarPieVO.NORMAL_BITMAP:
                drawSectorBitmap(canvas, n);
                break;
            case BarPieVO.CUT_BITMAP:
                drawSectorCutBitmap(canvas, n);
                break;
            case 0:
                break;
        }
    }

    /**
     * 绘制扇形矩形,从第0个到第n个
     */
    private void drawSectorRect(Canvas canvas, int n) {
        float top;
        for (int i = 0; i < n; i++) {
            rectPaint.setColor(barPieVO.getSectorVOList().get(i).getColor());
            if (i > 0) {
                //第一张图正常画,之后逆时针旋转
                //rotate是顺时针
                canvas.rotate(360 - proportion, xPoint, yPoint); //以矩形中心点旋转图形
            }
            top = yPoint - (barPieVO.getSectorVOList().get(i).getProportion() / startProportion) * startHeight;
            if (showAnimation && animationType == extend) {
                top = bottom - mCurrentValue * (bottom - top);
            }
            top -= radius;
            dataRect.set((int) left, (int) top, (int) right, (int) (bottom - radius));
            canvas.drawRect(dataRect, rectPaint);
        }
    }

    /**
     * 绘制扇形扇形,从第0个到第n个
     */
    private void drawSectorArc(Canvas canvas, int n) {
        float top, arcRadius;
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                //第一张图正常画,之后逆时针旋转
                //rotate是顺时针
                canvas.rotate(360 - proportion, xPoint, yPoint); //以矩形中心点旋转图形
            }
            top = yPoint - (barPieVO.getSectorVOList().get(i).getProportion() / startProportion) * startHeight;
            if (showAnimation && animationType == extend) {
                top = bottom - mCurrentValue * (bottom - top);
            }
            top -= radius;
            arcRadius = (yPoint - top + radius) / 2;
            //设置扇形外接矩形,即圆形区域
            arcRect = new RectF(xPoint - arcRadius, yPoint - arcRadius,
                    xPoint + arcRadius, yPoint + arcRadius);
            rectPaint.setStrokeWidth(yPoint - top - radius);
            rectPaint.setColor(barPieVO.getSectorVOList().get(i).getColor());
            canvas.drawArc(arcRect, 270, proportion, false, rectPaint);
        }
    }

    /**
     * 绘制扇形拉伸图片,从第0个到第n个
     */
    private void drawSectorBitmap(Canvas canvas, int n) {
        float top;
        Bitmap targetBitmap;
        //周围是图片时
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                //第一张图正常画,之后逆时针旋转
                //rotate是顺时针
                canvas.rotate(360 - proportion, xPoint, yPoint); //以矩形中心点旋转图形
            }
            top = yPoint - (barPieVO.getSectorVOList().get(i).getProportion() / startProportion) * startHeight;
            if (showAnimation && animationType == extend) {
                top = bottom - mCurrentValue * (bottom - top);
            }
            top -= radius;
            targetBitmap = Bitmap.createScaledBitmap(src, (int) (right - left), (int) (bottom - top), true);
            canvas.drawBitmap(targetBitmap, left, top, rectPaint);
        }
    }

    /**
     * 绘制扇形部分拉伸图片,从第0个到第n个
     */
    private void drawSectorCutBitmap(Canvas canvas, int n) {
        float top;
        //新图片由未拉伸的bitmaph和拉伸之后的bitmap组合而成
        Bitmap uncutBitmap, cuttedBitmap;
        //周围是图片时
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                //第一张图正常画,之后逆时针旋转
                //rotate是顺时针
                canvas.rotate(360 - proportion, xPoint, yPoint); //以矩形中心点旋转图形
            }
            top = yPoint - (barPieVO.getSectorVOList().get(i).getProportion() / startProportion) * startHeight;
            if (showAnimation && animationType == extend) {
                top = bottom - mCurrentValue * (bottom - top);
            }
            top -= radius;
            uncutBitmap = Bitmap.createBitmap(src, 0, 0, (int) (right - left), sectorPictureCutHeight);
            Matrix m = new Matrix();
            m.setScale(1, (yPoint - top - radius - sectorPictureCutHeight) / (startHeight - sectorPictureCutHeight - radius));
            cuttedBitmap = Bitmap.createBitmap(src, 0, sectorPictureCutHeight, (int) (right - left), (int) (startHeight - sectorPictureCutHeight - radius), m, true);
            canvas.drawBitmap(uncutBitmap, left, top, rectPaint);
            canvas.drawBitmap(cuttedBitmap, left, top + sectorPictureCutHeight, rectPaint);
        }
    }

    /**
     * 获取mCurrentValue在第几个扇形里面
     */
    private int getCurrentArc() {
        for (int i = 0; i < barPieVO.getSectorVOList().size(); i++) {
            if (mCurrentValue <= startAngleList.get(i) && mCurrentValue > startAngleList.get(i) - proportion) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据getCurrentArc返回的数量绘制扇形,用于旋转的动画效果
     */
    private void drawAnimatorArc(Canvas canvas, int n) {
        if (n != -1) {
            drawSector(canvas, n);
        } else {
            drawSector(canvas, barPieVO.getSectorVOList().size());
        }
    }

    /**
     * 绘制扇形上面的数据
     */
    private void drawData(Canvas canvas) {
        dataPaint.setColor(dataTextColor);
        int i = 0;
        //当前矩形中线相对于纵轴的中心点度数
        float centerDegree, dataHeight;
        //中心坐标
        float startX, startY;
        for (SectorVO sectorVO : barPieVO.getSectorVOList()) {
            if (barPieVO.getSectorType() != BarPieVO.ARC) {
                centerDegree = 360 - proportion * (i + 1);
            } else {
                centerDegree = 360 - proportion * (i + 0.4f);
            }
            //获取名称文本大小
            dataPaint.getTextBounds(sectorVO.getDescription(), 0, sectorVO.getDescription().length(), dataTextRect);

            dataHeight = (startHeight) * sectorVO.getProportion() / startProportion + radius + startHeight / 3;
            //根据每个弧度的中心点坐标绘制数据
            startX = calculatePosition(centerDegree, dataHeight)[0];
            startY = calculatePosition(centerDegree, dataHeight)[1];
            //绘制名称数据
            canvas.drawText(sectorVO.getDescription(),
                    startX - dataTextRect.width(),
                    startY + dataTextRect.height(),
                    dataPaint);
            String data;
            data = sectorVO.getProportion() + "";
            dataPaint.getTextBounds(data, 0, data.length(), dataTextRect);
            canvas.drawText(data,
                    startX - dataTextRect.width(),
                    startY + dataTextRect.height() * 2.5f,
                    dataPaint);
            i++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showAnimation && animationType == rotate) {
            drawAnimatorArc(canvas, getCurrentArc());
            if (mCurrentValue == -90) {
                drawCenter(canvas);
                drawData(canvas);
            }
        } else {
            drawSector(canvas, barPieVO.getSectorVOList().size());
            drawCenter(canvas);
            drawData(canvas);
        }
    }

    /**
     * 计算每段弧度的中心坐标
     *
     * @param degree 当前扇形中心度数
     */
    private float[] calculatePosition(float degree, float radius) {
        //由于Math.sin(double a)中参数a不是度数而是弧度，所以需要将度数转化为弧度
        //而Math.toRadians(degree)的作用就是将度数转化为弧度
        //sin 一二正，三四负 sin（180-a）=sin(a)
        //扇形弧线中心点距离圆心的x坐标
        float x = (float) (Math.sin(Math.toRadians(degree)) * (radius));
        //cos 一四正，二三负
        //扇形弧线中心点距离圆心的y坐标
        float y = (float) (Math.cos(Math.toRadians(degree)) * radius);

        //每段弧度的中心坐标(扇形弧线中心点相对于view的坐标)
        float startX = xPoint + x;
        float startY = yPoint - y;

        float[] position = new float[2];
        position[0] = startX;
        position[1] = startY;
        return position;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != barPieVO) {
            xPoint = (MeasureSpec.getSize(widthMeasureSpec) - MarginRight - MarginLeft) / 2;
            yPoint = (MeasureSpec.getSize(heightMeasureSpec) - MarginBottom - MarginTop) / 2;
            initParams();
        }
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }
}
