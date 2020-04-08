package com.example.widgetcollection;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.example.widgetcollection.JavaBean.Chart.ChartMapVO;
import com.example.widgetcollection.JavaBean.Chart.ChartVO;
import com.example.widgetcollection.JavaBean.Chart.PointVO;
import com.example.widgetcollection.JavaBean.PieGraph.PieVO;
import com.example.widgetcollection.JavaBean.PieGraph.SectorVO;
import com.example.widgetcollection.View.ChartView.LineBarChartView;
import com.example.widgetcollection.View.PieGraph.PieGraphView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //    private LineBarChartView lineBarChartView;
    private PieGraphView pieGraphView;
    private Bitmap test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
//        lineBarChartView = findViewById(R.id.line_bar_chart_view);
//        List<PointVO> pointList1 = new ArrayList<>();
//        List<PointVO> pointList2 = new ArrayList<>();
//        List<PointVO> pointList3 = new ArrayList<>();
//        List<PointVO> pointList4 = new ArrayList<>();
//        for (int i = 10; i < 15; i++) {
//            pointList1.add(new PointVO(i, i));
//            pointList2.add(new PointVO(15-i,15-i));
//            pointList3.add(new PointVO(i*2, i*2));
//            pointList4.add(new PointVO(i, i));
//            pointList4.add(new PointVO(i*2, i*2));
//        }
//
//        ChartVO chart1 = new ChartVO(pointList1, "line", 0xFFFF9500);
//        ChartVO chart2 = new ChartVO(pointList2, "line", 0xFFFF9500);
//        ChartVO chart3 = new ChartVO(pointList3, "bar", 0xFF95FF00);
//        ChartVO chart4 = new ChartVO(pointList4, "bar", 0xFFFF9500);
//        HashMap<String, ChartVO> hashMap = new HashMap<>();
//        hashMap.put("key1", chart1);
//        hashMap.put("key2", chart2);
//        hashMap.put("key3", chart3);
//        hashMap.put("key4", chart4);
//        ChartMapVO chartMapVO = new ChartMapVO("单位1", "单位2","单位3", hashMap);
//        lineBarChartView.addChartMap(chartMapVO);
        test = BitmapFactory.decodeResource(getResources(), R.mipmap.gongjvren1hao);
        pieGraphView = findViewById(R.id.pie_graph);
        ArrayList<SectorVO> list = new ArrayList<>();
        list.add(new SectorVO("数据1", 50, 0xFFFF9500));
        list.add(new SectorVO("数据2", 60, 0xFF104E8B));
        list.add(new SectorVO("数据3", 70, 0xFF6B8E23));
        list.add(new SectorVO("数据4", 50, 0xFF8EE5EE));
        list.add(new SectorVO("数据5", 60, 0xFFCD3333));
        list.add(new SectorVO("数据6", 70, 0xFF495c6f));
//        PieVO pieVO = new PieVO("总数据",0xFF3B3B3B, list);
        PieVO pieVO = new PieVO(test, list);
        pieGraphView.setData(pieVO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (test != null) {
            test.recycle();
            System.gc();
        }
    }
}
