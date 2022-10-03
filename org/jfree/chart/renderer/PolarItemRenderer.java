package org.jfree.chart.renderer;

import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import java.util.List;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

public interface PolarItemRenderer
{
    void drawSeries(final Graphics2D p0, final Rectangle2D p1, final PlotRenderingInfo p2, final PolarPlot p3, final XYDataset p4, final int p5);
    
    void drawAngularGridLines(final Graphics2D p0, final PolarPlot p1, final List p2, final Rectangle2D p3);
    
    void drawRadialGridLines(final Graphics2D p0, final PolarPlot p1, final ValueAxis p2, final List p3, final Rectangle2D p4);
    
    LegendItem getLegendItem(final int p0);
    
    PolarPlot getPlot();
    
    void setPlot(final PolarPlot p0);
    
    void addChangeListener(final RendererChangeListener p0);
    
    void removeChangeListener(final RendererChangeListener p0);
}
