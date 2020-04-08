package com.example.widgetcollection.JavaBean.PieGraph;

//饼图的每个扇形
public class SectorVO {
    private String description;

    private float proportion;

    private int color;

    public SectorVO(String description, float proportion, int color) {
        this.description = description;
        this.proportion = proportion;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getProportion() {
        return proportion;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
