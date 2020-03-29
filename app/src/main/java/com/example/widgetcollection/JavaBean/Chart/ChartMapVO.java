package com.example.widgetcollection.JavaBean.Chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ChartMapVO {
    //x轴单位
    private String xUnitText;
    //y轴单位
    private String yUnitText;
    //另一条y轴的单位
    private String anotherYUnitText;
    //是否是单一类型
    private boolean singleType;
    //折线/柱状图的集合
    private HashMap<String, ChartVO> chartMap;
    //key的集合
    private Set<String> keyList;

    //包含另一条Y轴的场景
    public ChartMapVO(String xUnitText, String yUnitText, String anotherYUnitText, HashMap<String, ChartVO> chartMap) {
        this.xUnitText = xUnitText;
        this.yUnitText = yUnitText;
        this.anotherYUnitText = anotherYUnitText;
        this.singleType = false;
        this.chartMap = chartMap;
        keyList = chartMap.keySet();
    }

    //不包含另一条Y轴的场景
    public ChartMapVO(String xUnitText, String yUnitText, HashMap<String, ChartVO> chartMap) {
        this.xUnitText = xUnitText;
        this.yUnitText = yUnitText;
        this.singleType = true;
        this.chartMap = chartMap;
        keyList = chartMap.keySet();
    }

    //给控件添加图表,参数是图表vo和key,key不能重复,添加成功返回true
    public boolean addChart(ChartVO chartVO, String key) {
        if (!chartMap.containsKey(key)) {
            chartMap.put(key, chartVO);
            return true;
        }
        return false;
    }

    /**
     * 初始化X轴数据,多个图表时,相同的横坐标合并,不同的添加
     */
    public List<Float> getXCoordinate() {
        List<Float> tempList = new ArrayList<>();
        for (String key : keyList) {
            ChartVO chartVO = chartMap.get(key);
            if (tempList.isEmpty()) {
                tempList = chartVO.getXList();
            } else {
                tempList.addAll(chartVO.getXList());
            }
        }
        return sortAndRemove(tempList);
    }

    /**
     * 初始化Y轴数据,多个图表时,相同的纵坐标合并,不同的添加
     */
    public List<Float> getYCoordinate() {
        List<Float> tempList = new ArrayList<>();
        for (String key : keyList) {
            ChartVO chartVO = chartMap.get(key);
            if(isSingleType()) {
                if (tempList.isEmpty()) {
                    tempList = chartVO.getYList();
                } else {
                    tempList.addAll(chartVO.getYList());
                }
            }else {
                if("line".equals(chartVO.getType())) {
                    if (tempList.isEmpty()) {
                        tempList = chartVO.getYList();
                    } else {
                        tempList.addAll(chartVO.getYList());
                    }
                }
            }
        }
        return sortAndRemove(tempList);
    }

    /**
     * 初始化另一条Y轴数据,多个图表时,相同的纵坐标合并,不同的添加
     * 默认按照柱状图的数据为另一条Y轴数据
     */
    public List<Float> getAnotherYCoordinate() {
        //singleType为true时不支持另一条Y轴
        if(singleType){
            return null;
        }
        List<Float> tempList = new ArrayList<>();
        for (String key : keyList) {
            ChartVO chartVO = chartMap.get(key);
            if("bar".equals(chartVO.getType())) {
                if (tempList.isEmpty()) {
                    tempList = chartVO.getYList();
                } else {
                    tempList.addAll(chartVO.getYList());
                }
            }
        }
        return sortAndRemove(tempList);
    }

    /**
     *将合并的list排序并去重
     */
    private List<Float> sortAndRemove(List<Float> list){
        Collections.sort(list);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).equals(list.get(i-1))) {
                list.remove(i);
            }
        }
        return list;
    }

    /**
     *获得折线图集合
     */
    public List<ChartVO> getLineChart(){
        List<ChartVO> resultList = new ArrayList<>();
        for (String key: keyList){
            ChartVO chartVO = chartMap.get(key);
            if("line".equals(chartVO.getType())){
                resultList.add(chartVO);
            }
        }
        return resultList;
    }

    /**
     *获得柱状图集合
     */
    public List<ChartVO> getBarChart(){
        List<ChartVO> resultList = new ArrayList<>();
        for (String key: keyList){
            ChartVO chartVO = chartMap.get(key);
            if("bar".equals(chartVO.getType())){
                resultList.add(chartVO);
            }
        }
        return resultList;
    }

    /**
     *获得所有图集合
     */
    public List<ChartVO> getChartList(){
        List<ChartVO> resultList = new ArrayList<>();
        for (String key: keyList){
            ChartVO chartVO = chartMap.get(key);
            resultList.add(chartVO);
        }
        return resultList;
    }

    public Set<String> getKeyList() {
        return keyList;
    }

    public boolean isSingleType() {
        return singleType;
    }

    public String getxUnitText() {
        return xUnitText;
    }

    public String getyUnitText() {
        return yUnitText;
    }

    public String getAnotherYUnitText() {
        return anotherYUnitText;
    }
}
