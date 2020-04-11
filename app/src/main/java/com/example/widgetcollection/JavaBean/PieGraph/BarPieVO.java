package com.example.widgetcollection.JavaBean.PieGraph;

import android.graphics.Bitmap;
import android.text.TextUtils;

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

    private float sectorArcHeight;

    private int sectorPictureCutHeight;

    private int sectorType;
    //边缘是矩形
    public static final int RECT = 1;
    //边缘是扇形
    public static final int ARC = 2;
    //边缘是正常缩放的bitmap
    public static final int NORMAL_BITMAP = 3;
    //边缘是部分缩放的bitmap
    public static final int CUT_BITMAP = 4;

    //支持覆盖配置
    public static class Builder {
        //中心部分的配置
        String centerText;
        int centerTextColor;
        Bitmap centerPicture;
        List<SectorVO> sectorVOList;
        boolean centerIsPicture;
        //扇形部分的配置
        Bitmap sectorPicture;
        float sectorRectWidth;
        float sectorRectHeight;
        float sectorArcHeight;
        int sectorPictureCutHeight;
        int sectorType;

        public Builder setCenter(String centerText, int centerTextColor) {
            if (centerPicture != null) {
                centerPicture = null;
            }
            this.centerText = centerText;
            this.centerTextColor = centerTextColor;
            centerIsPicture = false;
            return this;
        }

        public Builder setCenter(Bitmap centerPicture) {
            if (centerText != null || centerTextColor != 0) {
                centerText = "";
                centerTextColor = 0;
            }
            this.centerPicture = centerPicture;
            centerIsPicture = true;
            return this;
        }

        public Builder setSector(float sectorRectWidth, float sectorRectHeight) {
            if (sectorType != 0) {
                sectorPicture = null;
                sectorRectWidth = 0;
                sectorRectHeight = 0;
                sectorArcHeight = 0;
                sectorPictureCutHeight = 0;
            }
            this.sectorRectHeight = sectorRectHeight;
            this.sectorRectWidth = sectorRectWidth;
            sectorType = RECT;
            return this;
        }

        public Builder setSector(float sectorArcHeight) {
            if (sectorType != 0) {
                sectorPicture = null;
                sectorRectWidth = 0;
                sectorRectHeight = 0;
                sectorArcHeight = 0;
                sectorPictureCutHeight = 0;
            }
            this.sectorArcHeight = sectorArcHeight;
            sectorType = ARC;
            return this;
        }

        public Builder setSector(Bitmap sectorPicture) {
            if (sectorType != 0) {
                sectorPicture = null;
                sectorRectWidth = 0;
                sectorRectHeight = 0;
                sectorArcHeight = 0;
                sectorPictureCutHeight = 0;
            }
            this.sectorPicture = sectorPicture;
            sectorType = NORMAL_BITMAP;
            return this;
        }

        public Builder setSector(Bitmap sectorPicture, int sectorPictureCutHeight) {
            if (sectorType != 0) {
                sectorPicture = null;
                sectorRectWidth = 0;
                sectorRectHeight = 0;
                sectorArcHeight = 0;
                sectorPictureCutHeight = 0;
            }
            this.sectorPicture = sectorPicture;
            this.sectorPictureCutHeight = sectorPictureCutHeight;
            sectorType = CUT_BITMAP;
            return this;
        }

        public Builder setSectorList(List<SectorVO> sectorVOList) {
            this.sectorVOList = sectorVOList;
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
            return this;
        }

        void applyConfig(BarPieVO barPieVO) {
            switch (this.sectorType) {
                case RECT:
                    barPieVO.sectorRectHeight = this.sectorRectHeight;
                    barPieVO.sectorRectWidth = this.sectorRectWidth;
                    break;
                case ARC:
                    barPieVO.sectorArcHeight = this.sectorArcHeight;
                    break;
                case NORMAL_BITMAP:
                    barPieVO.sectorPicture = this.sectorPicture;
                    break;
                case CUT_BITMAP:
                    barPieVO.sectorPicture = this.sectorPicture;
                    barPieVO.sectorPictureCutHeight = this.sectorPictureCutHeight;
                    break;
                case 0:
                    return;
            }
            if(null != this.sectorVOList){
                Collections.sort(this.sectorVOList, new Comparator<SectorVO>() {
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
                barPieVO.sectorVOList = this.sectorVOList;
            }else {
                return;
            }
            barPieVO.centerIsPicture = this.centerIsPicture;
            barPieVO.sectorType = this.sectorType;
            if (centerIsPicture) {
                barPieVO.centerPicture = this.centerPicture;
            } else {
                barPieVO.centerText = this.centerText;
                barPieVO.centerTextColor = this.centerTextColor;
            }
        }

        public BarPieVO create() {
            if ((null == centerPicture && centerIsPicture)
                    || (!centerIsPicture && TextUtils.isEmpty(centerText) && centerTextColor == 0)
                    || sectorType == 0) {
                //初始化失败时返回空
                return null;
            } else {

                BarPieVO barPieVO = new BarPieVO();
                applyConfig(barPieVO);
                return barPieVO;
            }
        }
    }

    public String getCenterText() {
        if (!centerIsPicture) {
            return centerText;
        } else {
            return null;
        }
    }

    public int getCenterTextColor() {
        if (!centerIsPicture) {
            return centerTextColor;
        } else {
            return 0;
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
        if (sectorType == NORMAL_BITMAP || sectorType == CUT_BITMAP) {
            return sectorPicture;
        } else {
            return null;
        }
    }

    public float getSectorRectWidth() {
        if (sectorType == RECT) {
            return sectorRectWidth;
        } else {
            return 0;
        }
    }

    public float getSectorRectHeight() {
        if (sectorType == RECT) {
            return sectorRectHeight;
        } else {
            return 0;
        }
    }

    public float getSectorArcHeight() {
        if (sectorType == ARC) {
            return sectorArcHeight;
        } else {
            return 0;
        }
    }

    public int getSectorPictureCutHeight() {
        if (sectorType == CUT_BITMAP) {
            return sectorPictureCutHeight;
        } else {
            return 0;
        }
    }

    public int getSectorType() {
        return sectorType;
    }
}
