package pt.unl.fct.pds.utils;

import org.jfree.chart.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LineChart {

    public LineChart() {}

    public static JFreeChart createLineChart(List<Integer> simpleBandwidth, List<Integer> advancedBandwidth) {
        XYSeries simple = new XYSeries("Simple");
        XYSeries advanced = new XYSeries("Advanced");

        for (int i = 0; i < simpleBandwidth.size(); i++) {
            simple.add(i + 1, simpleBandwidth.get(i));
        }
        for (int i = 0; i < advancedBandwidth.size(); i++) {
            advanced.add(i + 1, advancedBandwidth.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(simple);
        dataset.addSeries(advanced);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Bandwidth Comparison",
                "Circuit",
                "Bandwidth",
                dataset
        );

        return lineChart;
    }

    public static void convertPNG(JFreeChart chart, String fileName) throws IOException {
        File file = new File(fileName);
        ChartUtils.saveChartAsPNG(file, chart, 1200, 600);
    }
}
