package com.example.widgetcollection.JavaBean.PieGraph;

import android.graphics.Bitmap;

import java.util.List;

public class PieVO {

    //通过构造函数区分中心是文字还是图片
    //注意回收bitmap

    private String centerText;

    private int centerTextColor;

    private boolean centerIsPicture;

    private Bitmap centerPicture;

    private List<SectorVO> sectorVOList;

    private float sum;

    //有两个构造方法,传入中心文字的构造方法和传入中心图片的构造方法
    public PieVO(String centerText, int centerTextColor, List<SectorVO> sectorVOList) {
        this.centerText = centerText;
        this.sectorVOList = sectorVOList;
        this.centerTextColor = centerTextColor;
        for (SectorVO sectorVO : sectorVOList) {
            sum += sectorVO.getProportion();
        }
        centerIsPicture = false;
    }

    public PieVO(Bitmap centerPicture, List<SectorVO> sectorVOList) {
        this.centerPicture = centerPicture;
        this.sectorVOList = sectorVOList;
        for (SectorVO sectorVO : sectorVOList) {
            sum += sectorVO.getProportion();
        }
        centerIsPicture = true;
    }

    public float getSum() {
        return sum;
    }

    public String getCenterText() {
        if (!centerIsPicture) {
            return centerText;
        } else {
            return null;
        }
    }

    public void setCenterText(String centerText) {
        if (!centerIsPicture) {
            this.centerText = centerText;
        }
    }

    public int getCenterTextColor() {
        if (!centerIsPicture) {
            return centerTextColor;
        } else {
            return 0;
        }

    }

    public void setCenterTextColor(int centerTextColor) {
        if (!centerIsPicture) {
            this.centerTextColor = centerTextColor;
        }
    }

    public void setCenterPicture(Bitmap centerPicture) {
        if (centerIsPicture) {
            this.centerPicture = centerPicture;
        }
    }

    public boolean isCenterIsPicture() {
        return centerIsPicture;
    }

    public Bitmap getCenterPicture() {
        if (centerIsPicture) {
            return centerPicture;
        } else {
            return null;
        }
    }

    //获取bitmap的宽高比
    public float getAspectRatio() {
        if (centerIsPicture && null != centerPicture) {
            return centerPicture.getWidth() / centerPicture.getHeight();
        }
        return -1;
    }

    public List<SectorVO> getSectorVOList() {
        return sectorVOList;
    }

    public void setSectorVOList(List<SectorVO> sectorVOList) {
        this.sectorVOList = sectorVOList;
    }
}
