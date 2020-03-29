package com.example.widgetcollection;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.widgetcollection.JavaBean.Chart.ChartMapVO;
import com.example.widgetcollection.JavaBean.Chart.ChartVO;
import com.example.widgetcollection.JavaBean.Chart.PointVO;
import com.example.widgetcollection.View.ChartView.LineBarChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LineBarChartView lineBarChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        lineBarChartView = findViewById(R.id.line_bar_chart_view);
        List<PointVO> pointList1 = new ArrayList<>();
        List<PointVO> pointList2 = new ArrayList<>();
        List<PointVO> pointList3 = new ArrayList<>();
        for (int i = 10; i < 15; i++) {
            pointList1.add(new PointVO(i, i));
            pointList2.add(new PointVO(15-i,15-i));
            pointList3.add(new PointVO(i*2, i*2));
        }

        ChartVO chart1 = new ChartVO(pointList1, "line", 0xFFFF9500);
        ChartVO chart2 = new ChartVO(pointList2, "line", 0xFFFF9500);
        ChartVO chart3 = new ChartVO(pointList3, "bar", 0xFF95FF00);
        HashMap<String, ChartVO> hashMap = new HashMap<>();
        hashMap.put("key1", chart1);
        hashMap.put("key2", chart2);
        hashMap.put("key3", chart3);
        ChartMapVO chartMapVO = new ChartMapVO("单位1", "单位2","单位3", hashMap);
        lineBarChartView.addChartMap(chartMapVO);
    }
}
