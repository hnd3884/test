package org.jfree.chart.demo;

import java.awt.Window;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartPanel;
import javax.swing.JPanel;
import java.awt.Font;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import java.awt.Container;
import org.jfree.ui.ApplicationFrame;

public class PieChartDemo1 extends ApplicationFrame
{
    public PieChartDemo1(final String title) {
        super(title);
        this.setContentPane((Container)createDemoPanel());
    }
    
    private static PieDataset createDataset() {
        final DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", new Double(43.2));
        dataset.setValue("Two", new Double(10.0));
        dataset.setValue("Three", new Double(27.5));
        dataset.setValue("Four", new Double(17.5));
        dataset.setValue("Five", new Double(11.0));
        dataset.setValue("Six", new Double(19.4));
        return dataset;
    }
    
    private static JFreeChart createChart(final PieDataset dataset) {
        final JFreeChart chart = ChartFactory.createPieChart("Pie Chart Demo 1", dataset, true, true, false);
        final PiePlot plot = (PiePlot)chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setLabelFont(new Font("SansSerif", 0, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
    }
    
    public static JPanel createDemoPanel() {
        final JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }
    
    public static void main(final String[] args) {
        final PieChartDemo1 demo = new PieChartDemo1("Pie Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen((Window)demo);
        demo.setVisible(true);
    }
}
