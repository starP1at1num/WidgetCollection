package com.example.widgetcollection.View.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;

import com.example.widgetcollection.JavaBean.Chart.ChartMapVO;
import com.example.widgetcollection.JavaBean.Chart.ChartVO;
import com.example.widgetcollection.JavaBean.Chart.PointVO;

import java.util.ArrayList;
import java.util.List;

public class LineBarChartView extends BaseChartView {

    private ChartMapVO chartMap;

    //画笔
    private Paint paint;

    //横纵坐标集合
    private List<Float> xCoordinateList, yCoordinateList, anotherYCoordinateList;

    //dst路径集合
    private List<Path> dstList;
    //PathMeasure集合
    private List<PathMeasure> measureList;
    //折线图集合
    private List<ChartVO> lineChartList = new ArrayList<>();
    //柱状图集合
    private List<ChartVO> barChartList = new ArrayList<>();

    public LineBarChartView(Context context, ChartMapVO chartMap) {
        this(context, null, chartMap);
    }

    public LineBarChartView(Context context, AttributeSet attrs, ChartMapVO chartMap) {
        super(context, attrs);
        this.chartMap = chartMap;
        init();
    }

    public LineBarChartView(Context context) {
        super(context);
    }

    public LineBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 给控件添加图表,参数是图表vo和key,key不能重复,添加成功返回true
     */
    public boolean addChart(ChartVO chartVO, String key) {
        if (chartMap.addChart(chartVO, key)) {
            init();
            initLinePath();
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * 给控件添加一组图表,参数是ChartMapVO,添加成功返回true
     */
    public boolean addChartMap(ChartMapVO chartMap) {
        if (null != chartMap && null != chartMap.getKeyList()) {
            this.chartMap = chartMap;
            init();
            initLinePath();
            invalidate();
            return true;
        }
        return false;
    }

    private void init() {
        initPaint();
        lineChartList = chartMap.getLineChart();
        barChartList = chartMap.getBarChart();
        initXCoordinate();
        initYCoordinate();
        if (!chartMap.isSingleType()) {
            initAnotherYCoordinate();
        }
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        //设置抖动,显示效果较好
        paint.setDither(true);
        paint.setStrokeWidth(dataStrokeWidth);
    }

    /**
     * 初始化X轴数据,多个图表时,相同的横坐标合并,不同的添加
     */
    private void initXCoordinate() {
        xCoordinateList = chartMap.getXCoordinate();
    }

    /**
     * 初始化Y轴数据,多个图表时,相同的纵坐标合并,不同的添加
     */
    private void initYCoordinate() {
        yCoordinateList = chartMap.getYCoordinate();
    }

    /**
     * 初始化另一条Y轴数据,多个图表时,相同的纵坐标合并,不同的添加
     */
    private void initAnotherYCoordinate() {
        anotherYCoordinateList = chartMap.getAnotherYCoordinate();
    }

    /**
     * 画单位
     */
    private void drawUnit(Canvas canvas) {
        paint.setColor(unitColor);
        float textWidth;

        paint.setTextSize(mYUnitTextSize);
        if (!chartMap.isSingleType()) {
            textWidth = paint.measureText(chartMap.getAnotherYUnitText());
            canvas.drawText(chartMap.getAnotherYUnitText(), this.getWidth()
                            - textWidth,
                    yPoint - yLength - mYLabelSize - mYLabelSize
                            / 5, paint);
        }
        textWidth = paint.measureText(chartMap.getyUnitText());
        canvas.drawText(chartMap.getyUnitText(), MarginLeft / 2 - textWidth / 2,
                yPoint - yLength - mYLabelSize - mYLabelSize
                        / 5, paint);
        paint.setTextSize(mXUnitTextSize);
        textWidth = paint.measureText(chartMap.getxUnitText());
        canvas.drawText(chartMap.getxUnitText(), (int) (this.getWidth() / 2 - textWidth / 2), yPoint
                + mXLabelSize * 3 + mXLabelSize / 5, paint);
    }

    /**
     * 画坐标轴的数据
     */
    private void drawCoordinator(Canvas canvas) {
        paint.setTextSize(mXLabelSize);

        paint.setColor(0xff008577);
        //设置x轴单位间距,纵向分割线
        if (showXDivider) {
            if (!containFault) {
                for (int i = 1; i <= xSection; i++) {
                    canvas.drawLine(xPoint + xLength * i / xSection, yPoint, xPoint + xLength * i / xSection, yPoint - yLength, paint);
                }
            } else {
                canvas.drawLine(xPoint + xFault, yPoint, xFault + xPoint, yPoint - yLength - yFault, paint);
                for (int i = 1; i <= xSection; i++) {
                    canvas.drawLine(xPoint + xFault + xLength * i / xSection, yPoint, xPoint + xFault + xLength * i / xSection, yPoint - yLength - yFault, paint);
                }
            }
        }

        //设置y轴单位间距,横向分割线
        if (showYDivider) {
            if (!containFault) {
                for (int i = 1; i <= ySection; i++) {
                    canvas.drawLine(xPoint, yPoint - yLength * i / ySection, xPoint + xLength, yPoint - yLength * i / ySection, paint);
                }
            } else {
                canvas.drawLine(xPoint, yPoint + yFault, xLength + xPoint + xFault, yPoint + yFault, paint);
                for (int i = 1; i <= ySection; i++) {
                    canvas.drawLine(xPoint, yPoint - yFault - yLength * i / ySection, xPoint + xLength + xFault, yPoint - yFault - yLength * i / ySection, paint);
                }
            }
        }

        paint.setColor(lineColor);
        //横轴
        //显示位置时
        if (showPosition) {
            for (float x : xCoordinateList) {
                canvas.drawText(x + "", getPosition(x, 0)[0] - mXLabelSize / 3,
                        yPoint + mXLabelSize * 3 / 2, paint);
            }
        } else {
            //不包含断层时只显示最大数据
            if (!containFault) {
                float endX = xCoordinateList.get(xCoordinateList.size() - 1);
                canvas.drawText(endX + "", getPosition(endX, 0)[0] - mXLabelSize / 3,
                        yPoint + mXLabelSize * 3 / 2, paint);
            } else {
                //包含断层时显示最大和最小数据
                float startX = xCoordinateList.get(0);
                float endX = xCoordinateList.get(xCoordinateList.size() - 1);
                canvas.drawText(startX + "", getPosition(startX, 0)[0] - mXLabelSize / 3,
                        yPoint + mXLabelSize * 3 / 2, paint);
                canvas.drawText(endX + "", getPosition(endX, 0)[0] - mXLabelSize / 3,
                        yPoint + mXLabelSize * 3 / 2, paint);
            }
        }

        //纵轴
        //显示位置时
        if (showPosition) {
            for (float y : yCoordinateList) {
                canvas.drawText(y + "", MarginLeft / 4,
                        getPosition(0, y)[1] + mYLabelSize / 3, paint);
            }
            if (!chartMap.isSingleType()) {
                for (float y : anotherYCoordinateList) {
                    canvas.drawText(y + "", this.getWidth() - MarginLeft / 2,
                            getAnotherYPosition(y) + mYLabelSize / 3, paint);
                }
            }
        } else {
            //不包含断层时只显示最大数据
            if (!containFault) {
                float endY = yCoordinateList.get(yCoordinateList.size() - 1);
                canvas.drawText(endY + "", MarginLeft / 4,
                        getPosition(0, endY)[1] - mXLabelSize / 3, paint);
                if (!chartMap.isSingleType()) {
                    float endAnotherY = anotherYCoordinateList.get(anotherYCoordinateList.size() - 1);
                    canvas.drawText(endAnotherY + "", this.getWidth() - MarginLeft,
                            getPosition(0, endAnotherY)[1] + mYLabelSize / 3, paint);

                }
            } else {
                //包含断层时显示最大和最小数据
                float startY = yCoordinateList.get(0);
                float endY = yCoordinateList.get(yCoordinateList.size() - 1);
                canvas.drawText(startY + "", MarginLeft / 4,
                        getPosition(0, endY)[1] + mYLabelSize / 3, paint);
                canvas.drawText(endY + "", MarginLeft / 4,
                        getPosition(0, endY)[1] + mYLabelSize / 3, paint);
                if (!chartMap.isSingleType()) {
                    float startAnotherY = anotherYCoordinateList.get(0);
                    float endAnotherY = anotherYCoordinateList.get(anotherYCoordinateList.size() - 1);
                    canvas.drawText(startAnotherY + "", this.getWidth() - MarginLeft,
                            getAnotherYPosition(startAnotherY) + mYLabelSize / 3, paint);
                    canvas.drawText(endAnotherY + "", this.getWidth() - MarginLeft,
                            getAnotherYPosition(endAnotherY) + mYLabelSize / 3, paint);

                }
            }
        }
    }

    /**
     * 画折线图的圆,
     * 新增:位置投影的显示
     */
    private void drawLineCircle(Canvas canvas) {
        for (ChartVO chartVO : lineChartList) {
            for (PointVO pointVO : chartVO.getPointList()) {
                float[] position = getPosition(pointVO.getX_coordinate(), pointVO.getY_coordinate());
                if (showPosition) {
                    paint.setColor(0xff008577);
                    canvas.drawLine(position[0], position[1], xPoint, position[1], paint);
                    canvas.drawLine(position[0], position[1], position[0], yPoint, paint);
                }
                paint.setColor(chartVO.getColor());
                canvas.drawCircle(position[0], position[1], circleRadius, paint);
            }
        }
    }

    /**
     * 获取数据坐标
     */
    private float[] getPosition(float x, float y) {
        float xPosition;
        float yPosition;
        if (!containFault) {
            xPosition = xPoint + xLength * x / xCoordinateList.get(xCoordinateList.size() - 1);
            yPosition = yPoint - yLength * y / yCoordinateList.get(yCoordinateList.size() - 1);
        } else {
            xPosition = xPoint + xFault + xLength * (x - xCoordinateList.get(0)) / (xCoordinateList.get(xCoordinateList.size() - 1) - xCoordinateList.get(0));
            yPosition = yPoint - yFault - yLength * (y - yCoordinateList.get(0)) / (yCoordinateList.get(yCoordinateList.size() - 1) - xCoordinateList.get(0));
        }
        return new float[]{xPosition, yPosition};
    }

    /**
     * 获取另一个Y轴数据坐标
     */
    private float getAnotherYPosition(float y) {
        float yPosition;
        if (!containFault) {
            yPosition = yPoint - yLength * y / anotherYCoordinateList.get(anotherYCoordinateList.size() - 1);
        } else {
            yPosition = yPoint - yFault - yLength * (y - anotherYCoordinateList.get(0)) / (anotherYCoordinateList.get(anotherYCoordinateList.size() - 1) - anotherYCoordinateList.get(0));
        }
        return yPosition;
    }

    /**
     * 初始化折线图的路径数据
     */
    private void initLinePath() {
        dstList = new ArrayList<>();
        measureList = new ArrayList<>();
        Path linePath = new Path();
        Path dstPath = new Path();
        PointVO startPoint;
        float[] startPosition;
        float[] position;
        for (ChartVO chartVO : lineChartList) {
            startPoint = chartVO.getStartPoint();
            startPosition = getPosition(startPoint.getX_coordinate(), startPoint.getY_coordinate());
            linePath.reset();
            linePath.moveTo(startPosition[0], startPosition[1]);
            for (PointVO pointVO : chartVO.getRestList()) {
                position = getPosition(pointVO.getX_coordinate(), pointVO.getY_coordinate());
                linePath.lineTo(position[0], position[1]);
            }
            PathMeasure pathMeasure = new PathMeasure(linePath, true);
            dstList.add(dstPath);
            measureList.add(pathMeasure);
        }
    }

    /**
     * 绘制柱状图
     */
    private void drawBarChart(Canvas canvas) {
        float left;
        float top;
        float right;
        float bottom;
        //当前绘制的是第几个柱状图
        int currentBarChart = 0;
        //获取需要绘制的柱状图总数
        int size = barChartList.size();
        paint.setStyle(Paint.Style.FILL);

        for (ChartVO chartVO : barChartList) {
            for (PointVO pointVO : chartVO.getPointList()) {
                paint.setColor(chartVO.getColor());
                if (chartMap.isSingleType()) {
                    //单一类型时柱状图按左边的Y轴
                    top = getPosition(pointVO.getX_coordinate(), pointVO.getY_coordinate())[1] + lineStrokeWidth;
                } else {
                    //混合类型时柱状图按右边的Y轴
                    top = getAnotherYPosition(pointVO.getY_coordinate()) + lineStrokeWidth;
                }
                left = getPosition(pointVO.getX_coordinate(), 0)[0] - ((size / 2) - currentBarChart) * barWidth;
                right = left + barWidth;
                bottom = yPoint;
                // 每次valueAnimator更新时重绘最新top值
                canvas.drawRect(left, yPoint - (yPoint - top) * mCurrentValue, right, bottom, paint);
            }
            currentBarChart++;
        }
    }

    /**
     * 画折线图
     */
    private void drawLineChart(Canvas canvas, Paint p) {
        p.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < measureList.size() || i < dstList.size(); i++) {
            float stop = measureList.get(i).getLength() * mCurrentValue;
            dstList.get(i).reset();
            measureList.get(i).getSegment(0, stop, dstList.get(i), true);
            p.setColor(lineChartList.get(i).getColor());
            canvas.drawPath(dstList.get(i), p);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnit(canvas);
        drawCoordinator(canvas);
        drawLineCircle(canvas);
        drawLineChart(canvas, paint);
        drawBarChart(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (containFault) {
            xLength -= xFault;
            yLength -= yFault;
        }
        initLinePath();
    }
}
