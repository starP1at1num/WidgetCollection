package com.example.widgetcollection.View.PieGraph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.widgetcollection.JavaBean.PieGraph.PieVO;
import com.example.widgetcollection.JavaBean.PieGraph.SectorVO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PieGraphView extends BasePieGraph {
    //中心画笔
    private Paint centerPaint;
    //扇形画笔
    private Paint arcPaint;
    //数据画笔
    private Paint dataPaint;

    private PieVO pieVO;

    private float radius = (outerRadius + innerRadius) / 2;

    /**
     * 斜线长度
     */
    private float SLASH_LINE_OFFSET = outerRadius - innerRadius;

    /**
     * 横线长度
     */
    private float HOR_LINE_LENGTH = SLASH_LINE_OFFSET * 1.5f;

    /**
     * 横线上文字的横向偏移量
     */
    private float X_OFFSET = HOR_LINE_LENGTH * 0.1f;

    /**
     * 横线上文字的纵向偏移量
     */
    private float Y_OFFSET = X_OFFSET * 0.4f;

    //用于辅助计算中心图片/文字的大小
    private Rect centerBound, centerSumBound;

    //用于辅助计算扇形上图片/文字的大小
    private Rect dataTextBound;

    //扇形外接矩形
    private RectF rect;

    //各数据占比列表
    private List<Float> percentageList = new ArrayList<>();

    //动画相关
    private ValueAnimator coordinateValueAnimator;
    private float mCurrentValue;

    //计算饼图各扇形角度占比
    ArrayList<Float> ratioList;
    //计算饼图各扇形起始角度
    ArrayList<Float> startAngleList;

    public PieGraphView(Context context) {
        super(context);
    }

    public PieGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieGraphView(Context context, PieVO pieVO) {
        this(context, null, pieVO);
    }

    public PieGraphView(Context context, AttributeSet attrs, PieVO pieVO) {
        super(context, attrs);
        if (null != pieVO) {
            this.pieVO = pieVO;
            init();
        }
    }

    //设置pieVo的数据
    public void setData(PieVO pieVO) {
        if (null != pieVO) {
            this.pieVO = pieVO;
            init();
            postInvalidate();
        }
    }

    private void init() {
        //初始化画笔
        //圆环画笔
        arcPaint = new Paint();
        //圆环宽度
        arcPaint.setStrokeWidth(outerRadius - innerRadius);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);

        //中心画笔
        centerPaint = new Paint();
        centerPaint.setTextSize(centerTextSize);
        centerPaint.setAntiAlias(true);
        if (!pieVO.isCenterIsPicture()) {
            centerPaint.setColor(pieVO.getCenterTextColor());
        }

        //数据画笔
        dataPaint = new Paint();
        dataPaint.setStrokeWidth(1);
        dataPaint.setTextSize(dataTextSize);
        dataPaint.setAntiAlias(true);
        if (!pieVO.isCenterIsPicture()) {
            dataPaint.setColor(pieVO.getCenterTextColor());
        } else {
            dataPaint.setColor(dataTextColor);
        }


        //计算各扇面所占百分比
        for (SectorVO sectorVO : pieVO.getSectorVOList()) {
            percentageList.add(sectorVO.getProportion() / pieVO.getSum());
        }

        dataTextBound = new Rect();

        if (showAnimation) {
            if (coordinateValueAnimator != null) {
                coordinateValueAnimator.cancel();
            }
            coordinateValueAnimator = ValueAnimator.ofFloat(0,
                    360);
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

            ratioList = new ArrayList<>();
            startAngleList = new ArrayList<>();
            for (int i = 0; i < pieVO.getSectorVOList().size(); i++) {
                ratioList.add(pieVO.getSectorVOList().get(i).getProportion() / pieVO.getSum());
                if (i == 0) {
                    startAngleList.add(0f);
                } else {
                    startAngleList.add(startAngleList.get(i - 1) + ratioList.get(i - 1) * 360);
                }
            }
        }
    }

    private void initParams() {
        //初始化圆心图片/文字参数
        if (!pieVO.isCenterIsPicture()) {
            //文字就计算文字的大小
            centerBound = new Rect();
            centerPaint.getTextBounds(pieVO.getCenterText(), 0, pieVO.getCenterText().length(), centerBound);
            centerSumBound = new Rect();
            centerPaint.getTextBounds(pieVO.getSum() + "", 0, (pieVO.getSum() + "").length(), centerSumBound);
        } else {
            //图片就计算内圆可容下的图片的大小
            //高度约等于0.707乘以半径
            //宽度按照bitmap的宽高比基于高度计算出来
            int heightOffset;
            int widthOffset;
            if (pieVO.getAspectRatio() < 1) {
                //宽高比小于1时,以长度为基准
                heightOffset = (int) (0.707 * innerRadius);
                widthOffset = (int) (heightOffset * pieVO.getAspectRatio());
            } else {
                //宽高比大于1时,以宽3度为基准
                widthOffset = (int) (0.707 * innerRadius);
                heightOffset = (int) (widthOffset * pieVO.getAspectRatio());
            }
            centerBound = new Rect((int) xPoint - widthOffset, (int) yPoint - heightOffset, (int) xPoint + widthOffset, (int) yPoint + heightOffset);
        }

        //设置扇形外接矩形,即圆形区域
        rect = new RectF(xPoint - radius, yPoint - radius,
                xPoint + radius, yPoint + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showAnimation) {
            drawArc(canvas);
            drawData(canvas);
            drawCenter(canvas);
        } else {
            drawAnimatorArc(canvas, getCurrentArc());
            if (mCurrentValue == 360) {
                drawData(canvas);
                drawCenter(canvas);
            }
        }

    }

    /**
     * 获取mCurrentValue在第几个扇形里面
     */
    private int getCurrentArc() {
        for (int i = 0; i < pieVO.getSectorVOList().size(); i++) {
            if (mCurrentValue >= startAngleList.get(i) && mCurrentValue < startAngleList.get(i) + ratioList.get(i) * 360) {
                return i;
            }
        }
        return -1;
    }

    private void drawAnimatorArc(Canvas canvas, int n) {
        if (n != -1) {
            for (int i = 0; i <= n; i++) {
                arcPaint.setColor(pieVO.getSectorVOList().get(i).getColor());
                canvas.drawArc(rect, startAngleList.get(i), mCurrentValue - startAngleList.get(i), false, arcPaint);
            }
        } else {
            for (int i = 0; i < pieVO.getSectorVOList().size(); i++) {
                arcPaint.setColor(pieVO.getSectorVOList().get(i).getColor());
                canvas.drawArc(rect, startAngleList.get(i), 360 - startAngleList.get(i), false, arcPaint);
            }
        }
    }

    /**
     * 绘制圆心文字/图片
     */
    private void drawCenter(Canvas canvas) {
        if (!pieVO.isCenterIsPicture()) {
            //绘制中心文字
            canvas.drawText(pieVO.getCenterText(), xPoint - centerBound.width() / 2.0f, yPoint - centerBound.height() / 2.0f, centerPaint);
            canvas.drawText(pieVO.getSum() + "", xPoint - centerSumBound.width() / 2.0f, yPoint + centerSumBound.height() * 1.5f, centerPaint);
        } else {
            //绘制中心图片
            Bitmap src = pieVO.getCenterPicture();
            Matrix m = new Matrix();
            m.setScale((float)(centerBound.right - centerBound.left) / src.getWidth(), (float)(centerBound.bottom - centerBound.top) / src.getHeight());
            Bitmap target = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
            canvas.drawBitmap(target, centerBound.left, centerBound.top, centerPaint);
            target.recycle();
        }
    }

    /**
     * 绘制扇形
     */
    private void drawArc(Canvas canvas) {
        int startAngle = 0;
        int i = 0;
        float angle;
        for (SectorVO sectorVO : pieVO.getSectorVOList()) {
            arcPaint.setColor(sectorVO.getColor());
            if (i == pieVO.getSectorVOList().size() - 1) {//保证所有度数加起来等于360
                angle = 360 - startAngle;
            } else {
                angle = (float) Math.ceil(percentageList.get(i) * 360);
            }
            //绘制第i段扇形
            canvas.drawArc(rect, startAngle, angle, false, arcPaint);
            startAngle += angle;
            i++;
        }
    }

    /**
     * 绘制扇形上面的数据
     */
    private void drawData(Canvas canvas) {
        int startAngle = 0;
        int i = 0;
        float angle;
        //当前扇形弧线相对于纵轴的中心点度数,由于扇形的绘制是从三点钟方向开始，所以加90
        float arcCenterDegree;
        //斜线结束坐标
        float endX = 0;
        float endY = 0;
        //横线结束坐标
        float horEndX = 0;
        float horEndY = 0;
        //数字开始坐标
        float numberStartX = 0;
        float numberStartY = 0;
        //文本开始坐标
        float textStartX = 0;
        float textStartY = 0;
        //弧度中心坐标
        float startX, startY;
        for (SectorVO sectorVO : pieVO.getSectorVOList()) {
            if (i == pieVO.getSectorVOList().size() - 1) {//保证所有度数加起来等于360
                angle = 360 - startAngle;
            } else {
                angle = (float) Math.ceil(percentageList.get(i) * 360);
            }
            //+0.5是为了让每个扇形之间没有间隙
            if (angle != 0) {
                angle += 0.5f;
            }
            startAngle += angle;
            arcCenterDegree = 90 + startAngle - angle / 2;

            //获取名称文本大小
            dataPaint.getTextBounds(sectorVO.getDescription(), 0, sectorVO.getDescription().length(), dataTextBound);
            //根据每个弧度的中心点坐标绘制数据
            startX = calculatePosition(arcCenterDegree)[0];
            startY = calculatePosition(arcCenterDegree)[1];
            if (dataInGraph) {
                //绘制名称数据，20为纵坐标偏移量
                canvas.drawText(sectorVO.getDescription(),
                        startX - dataTextBound.width() / 2.0f,
                        startY - dataTextBound.height() / 2.0f,
                        dataPaint);
                String data;
                if (percentageMode) {
                    //绘制百分比数据
                    data = floatToPercent(percentageList.get(i));
                } else {
                    //绘制非百分比数据
                    data = percentageList.get(i) + "";
                }
                dataPaint.getTextBounds(data, 0, data.length(), dataTextBound);
                //绘制百分比数据，20为纵坐标偏移量
                canvas.drawText(data,
                        startX - dataTextBound.width() / 2.0f,
                        startY + dataTextBound.height() * 1.5f,
                        dataPaint);
            } else {
                //数据和描述在扇形外面时
                //根据角度判断象限，并且计算各个坐标点
                if (arcCenterDegree > 90 && arcCenterDegree < 180) {//二象限
                    endX = startX + SLASH_LINE_OFFSET;
                    endY = startY + SLASH_LINE_OFFSET;

                    horEndX = endX + HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;
                } else if (arcCenterDegree == 180) {
                    startX = xPoint;
                    startY = yPoint + radius;
                    endX = startX + SLASH_LINE_OFFSET;
                    endY = startY + SLASH_LINE_OFFSET;


                    horEndX = endX + HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;
                } else if (arcCenterDegree > 180 && arcCenterDegree < 270) {//三象限
                    endX = startX - SLASH_LINE_OFFSET;
                    endY = startY + SLASH_LINE_OFFSET;

                    horEndX = endX - HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;
                } else if (arcCenterDegree == 270) {
                    startX = xPoint - radius;
                    startY = yPoint;
                    endX = startX - SLASH_LINE_OFFSET;
                    endY = startY - SLASH_LINE_OFFSET;

                    horEndX = endX - HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;
                } else if (arcCenterDegree > 270 && arcCenterDegree < 360) {//四象限
                    endX = startX - SLASH_LINE_OFFSET;
                    endY = startY - SLASH_LINE_OFFSET;

                    horEndX = endX - HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;
                } else if (arcCenterDegree == 360) {
                    startX = xPoint;
                    startY = yPoint - radius;
                    endX = startX - SLASH_LINE_OFFSET;
                    endY = startY - SLASH_LINE_OFFSET;

                    horEndX = endX - HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX - HOR_LINE_LENGTH + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;

                } else if (arcCenterDegree > 360) {//一象限
                    endX = startX + SLASH_LINE_OFFSET;
                    endY = startY - SLASH_LINE_OFFSET;

                    horEndX = endX + HOR_LINE_LENGTH;
                    horEndY = endY;

                    numberStartX = endX + X_OFFSET;
                    numberStartY = endY - Y_OFFSET;

                    textStartX = endX + X_OFFSET;
                    textStartY = endY + dataTextBound.height() + Y_OFFSET / 2;

                }
                //绘制折线
                canvas.drawLine(startX, startY, endX, endY, dataPaint);
                //绘制横线
                canvas.drawLine(endX, endY, horEndX, horEndY, dataPaint);
                //绘制数字
                if (percentageMode) {
                    //百分比模式
                    canvas.drawText(floatToPercent(percentageList.get(i)), numberStartX, numberStartY, dataPaint);
                } else {
                    //非百分比模式
                    canvas.drawText(sectorVO.getProportion() + "", numberStartX, numberStartY, dataPaint);
                }
                //绘制文字
                canvas.drawText(sectorVO.getDescription() + "", textStartX, textStartY, dataPaint);
            }
            i++;
        }
    }

    /**
     * 计算每段弧度的中心坐标
     *
     * @param degree 当前扇形中心度数
     */
    private float[] calculatePosition(float degree) {
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

    public static String floatToPercent(Float num) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2); //最大小数位数
        return percentFormat.format(num);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != pieVO) {
            initParams();
        }
    }

}
