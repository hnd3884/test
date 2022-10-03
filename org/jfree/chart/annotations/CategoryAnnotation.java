package org.jfree.chart.annotations;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.Graphics2D;

public interface CategoryAnnotation
{
    void draw(final Graphics2D p0, final CategoryPlot p1, final Rectangle2D p2, final CategoryAxis p3, final ValueAxis p4);
}
