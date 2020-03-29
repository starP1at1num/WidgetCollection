package com.example.widgetcollection.JavaBean.Chart;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//折线图、柱状图的基本数据
public class ChartVO {
    //点的列表
    private List<PointVO> pointList;
    //点的数量
    private int size;
    //支持的类型,目前仅支持折线和柱状(line/bar)
    private String type;
    //颜色
    private int color;

    public ChartVO(List<PointVO> pointList, String type, int color) {
        if (pointList.isEmpty() || pointList.size() < 2) {
            return;
        }
        this.color = color;
        this.type = type;
        this.pointList = pointList;
    }

    public List<PointVO> getPointList() {
        //将数据按横坐标大小排序返回
        sortList();
        return pointList;
    }

    public void setPointList(List<PointVO> pointList) {
        this.pointList = pointList;
    }

    public int getSize() {
        return pointList.size();
    }

    //切换或创建点列表时,计算横纵坐标的起始值,并将列表按照横坐标排序
    private void sortList() {
        Collections.sort(pointList, new Comparator<PointVO>() {
            @Override
            public int compare(PointVO o1, PointVO o2) {
                if (o1.getX_coordinate() <= o2.getX_coordinate()) {
                    return -1;
                } else if (o1.getX_coordinate() == o2.getX_coordinate()) {
                    return 0;
                }
                return 1;
            }
        });
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    //获取横坐标列表
    public List<Float> getXList() {
        ArrayList<Float> xList = new ArrayList<>();
        Collections.sort(pointList, new Comparator<PointVO>() {
            @Override
            public int compare(PointVO o1, PointVO o2) {
                if (o1.getX_coordinate() <= o2.getX_coordinate()) {
                    return -1;
                } else if (o1.getX_coordinate() == o2.getX_coordinate()) {
                    return 0;
                }
                return 1;
            }
        });
        for (PointVO pointVO : pointList) {
            xList.add(pointVO.getX_coordinate());
        }
        return xList;
    }

    //获取纵坐标列表
    public List<Float> getYList() {
        ArrayList<Float> yList = new ArrayList<>();
        Collections.sort(pointList, new Comparator<PointVO>() {
            @Override
            public int compare(PointVO o1, PointVO o2) {
                if (o1.getY_coordinate() <= o2.getY_coordinate()) {
                    return -1;
                } else if (o1.getY_coordinate() == o2.getY_coordinate()) {
                    return 0;
                }
                return 1;
            }
        });
        for (PointVO pointVO : pointList) {
            yList.add(pointVO.getY_coordinate());
        }
        return yList;
    }

    public PointVO getStartPoint() {
        sortList();
        return pointList.get(0);
    }

    //获取除第一个点之外剩下的点的集合
    public List<PointVO> getRestList() {
        //将数据按横坐标大小排序返回
        sortList();
        return pointList.subList(1, pointList.size());
    }
}
