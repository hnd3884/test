package org.jfree.chart.editor;

import org.jfree.chart.JFreeChart;

public class ChartEditorManager
{
    static ChartEditorFactory factory;
    
    private ChartEditorManager() {
    }
    
    public static ChartEditorFactory getChartEditorFactory() {
        return ChartEditorManager.factory;
    }
    
    public static void setChartEditorFactory(final ChartEditorFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");
        }
        ChartEditorManager.factory = f;
    }
    
    public static ChartEditor getChartEditor(final JFreeChart chart) {
        return ChartEditorManager.factory.createEditor(chart);
    }
    
    static {
        ChartEditorManager.factory = new DefaultChartEditorFactory();
    }
}
