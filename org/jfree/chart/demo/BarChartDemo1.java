package org.jfree.chart.demo;

import java.awt.Window;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.axis.CategoryLabelPositions;
import java.awt.GradientPaint;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.NumberAxis;
import java.awt.Paint;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import java.awt.Container;
import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.ui.ApplicationFrame;

public class BarChartDemo1 extends ApplicationFrame
{
    public BarChartDemo1(final String title) {
        super(title);
        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        this.setContentPane((Container)chartPanel);
    }
    
    private static CategoryDataset createDataset() {
        final String series1 = "First";
        final String series2 = "Second";
        final String series3 = "Third";
        final String category1 = "Category 1";
        final String category2 = "Category 2";
        final String category3 = "Category 3";
        final String category4 = "Category 4";
        final String category5 = "Category 5";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);
        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);
        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);
        return dataset;
    }
    
    private static JFreeChart createChart(final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createBarChart("Bar Chart Demo 1", "Category", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        final BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);
        final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, new Color(0, 64, 0));
        final GradientPaint gp3 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp2);
        renderer.setSeriesPaint(2, gp3);
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235987755982988));
        return chart;
    }
    
    public static void main(final String[] args) {
        final BarChartDemo1 demo = new BarChartDemo1("Bar Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen((Window)demo);
        demo.setVisible(true);
    }
}
