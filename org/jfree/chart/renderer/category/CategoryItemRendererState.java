package org.jfree.chart.renderer.category;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.RendererState;

public class CategoryItemRendererState extends RendererState
{
    private double barWidth;
    private double seriesRunningTotal;
    
    public CategoryItemRendererState(final PlotRenderingInfo info) {
        super(info);
        this.barWidth = 0.0;
        this.seriesRunningTotal = 0.0;
    }
    
    public double getBarWidth() {
        return this.barWidth;
    }
    
    public void setBarWidth(final double width) {
        this.barWidth = width;
    }
    
    public double getSeriesRunningTotal() {
        return this.seriesRunningTotal;
    }
    
    void setSeriesRunningTotal(final double total) {
        this.seriesRunningTotal = total;
    }
}
