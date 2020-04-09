package com.example.widgetcollection.JavaBean.PieGraph;

import android.graphics.Bitmap;

import com.example.widgetcollection.JavaBean.Chart.PointVO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BarPieVO {

    //注意回收bitmap
    private String centerText;

    private int centerTextColor;

    private boolean centerIsPicture;

    private Bitmap centerPicture;

    private List<SectorVO> sectorVOList;

    private Bitmap sectorPicture;

    private float sectorRectWidth;

    private float sectorRectHeight;

    private boolean sectorIsPicture;
    //有四个构造方法,中心传入文字/图片,边缘是普通矩形/图片


    public BarPieVO(String centerText, int centerTextColor, List<SectorVO> sectorVOList, Bitmap sectorPicture) {
        this.centerText = centerText;
        this.centerTextColor = centerTextColor;
        this.sectorVOList = sectorVOList;
        this.sectorPicture = sectorPicture;
        this.centerIsPicture = false;
        this.sectorIsPicture = true;
        Collections.sort(sectorVOList, new Comparator<SectorVO>() {
            @Override
            public int compare(SectorVO o1, SectorVO o2) {
                if (o1.getProportion() <= o2.getProportion()) {
                    return -1;
                } else if (o1.getProportion() == o2.getProportion()) {
                    return 0;
                }
                return 1;
            }
        });
    }

    public BarPieVO(String centerText, int centerTextColor, List<SectorVO> sectorVOList, float sectorRectWidth, float sectorRectHeight) {
        this.centerText = centerText;
        this.centerTextColor = centerTextColor;
        this.sectorVOList = sectorVOList;
        this.sectorRectWidth = sectorRectWidth;
        this.sectorRectHeight = sectorRectHeight;
        this.centerIsPicture = false;
        this.sectorIsPicture = false;
        Collections.sort(sectorVOList, new Comparator<SectorVO>() {
            @Override
            public int compare(SectorVO o1, SectorVO o2) {
                if (o1.getProportion() <= o2.getProportion()) {
                    return -1;
                } else if (o1.getProportion() == o2.getProportion()) {
                    return 0;
                }
                return 1;
            }
        });

    }

    public BarPieVO(Bitmap centerPicture, List<SectorVO> sectorVOList, Bitmap sectorPicture) {
        this.centerPicture = centerPicture;
        this.sectorVOList = sectorVOList;
        this.sectorPicture = sectorPicture;
        this.centerIsPicture = true;
        this.sectorIsPicture = true;
        Collections.sort(sectorVOList, new Comparator<SectorVO>() {
            @Override
            public int compare(SectorVO o1, SectorVO o2) {
                if (o1.getProportion() <= o2.getProportion()) {
                    return -1;
                } else if (o1.getProportion() == o2.getProportion()) {
                    return 0;
                }
                return 1;
            }
        });

    }

    public BarPieVO(Bitmap centerPicture, List<SectorVO> sectorVOList, float sectorRectWidth, float sectorRectHeight) {
        this.centerPicture = centerPicture;
        this.sectorVOList = sectorVOList;
        this.sectorRectWidth = sectorRectWidth;
        this.sectorRectHeight = sectorRectHeight;
        this.centerIsPicture = true;
        this.sectorIsPicture = false;
        Collections.sort(sectorVOList, new Comparator<SectorVO>() {
            @Override
            public int compare(SectorVO o1, SectorVO o2) {
                if (o1.getProportion() <= o2.getProportion()) {
                    return -1;
                } else if (o1.getProportion() == o2.getProportion()) {
                    return 0;
                }
                return 1;
            }
        });

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

    public Bitmap getSectorPicture() {
        if (sectorIsPicture) {
            return sectorPicture;
        } else {
            return null;
        }
    }

    public float getSectorRectWidth() {
        if (!sectorIsPicture) {
            return sectorRectWidth;
        } else {
            return 0;
        }
    }

    public float getSectorRectHeight() {
        if (!sectorIsPicture) {
            return sectorRectHeight;
        } else {
            return 0;
        }
    }

    public boolean isSectorIsPicture() {
        return sectorIsPicture;
    }

    public void setSectorPicture(Bitmap sectorPicture) {
        if (sectorIsPicture) {
            this.sectorPicture = sectorPicture;
        }
    }

    public void setSectorRectWidth(float sectorRectWidth) {
        if (!sectorIsPicture) {
            this.sectorRectWidth = sectorRectWidth;
        }
    }

    public void setSectorRectHeight(float sectorRectHeight) {
        if (!sectorIsPicture) {
            this.sectorRectHeight = sectorRectHeight;
        }
    }

}
