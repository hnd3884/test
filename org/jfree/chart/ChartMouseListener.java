package org.jfree.chart;

import java.util.EventListener;

public interface ChartMouseListener extends EventListener
{
    void chartMouseClicked(final ChartMouseEvent p0);
    
    void chartMouseMoved(final ChartMouseEvent p0);
}
