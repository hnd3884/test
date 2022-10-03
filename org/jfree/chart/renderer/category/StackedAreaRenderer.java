package org.jfree.chart.renderer.category;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import java.awt.Shape;
import java.awt.Polygon;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StackedAreaRenderer extends AreaRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -3595635038460823663L;
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        return DatasetUtilities.findStackedRangeBounds(dataset);
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }
        final double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double y1 = 0.0;
        double y1Untranslated = value.doubleValue();
        g2.setPaint(this.getItemPaint(row, column));
        g2.setStroke(this.getItemStroke(row, column));
        if (column != 0) {
            final Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {
                final double x2 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double y0Untranslated = previousValue.doubleValue();
                final double previousHeightx0Untranslated = this.getPreviousHeight(dataset, row, column - 1);
                final double previousHeightx1Untranslated = this.getPreviousHeight(dataset, row, column);
                y0Untranslated += previousHeightx0Untranslated;
                y1Untranslated += previousHeightx1Untranslated;
                final RectangleEdge location = plot.getRangeAxisEdge();
                final double previousHeightx0 = rangeAxis.valueToJava2D(previousHeightx0Untranslated, dataArea, location);
                final double previousHeightx2 = rangeAxis.valueToJava2D(previousHeightx1Untranslated, dataArea, location);
                final double y2 = rangeAxis.valueToJava2D(y0Untranslated, dataArea, location);
                y1 = rangeAxis.valueToJava2D(y1Untranslated, dataArea, location);
                Polygon p = null;
                final PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    p = new Polygon();
                    p.addPoint((int)y2, (int)x2);
                    p.addPoint((int)y1, (int)x1);
                    p.addPoint((int)previousHeightx2, (int)x1);
                    p.addPoint((int)previousHeightx0, (int)x2);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    p = new Polygon();
                    p.addPoint((int)x2, (int)y2);
                    p.addPoint((int)x1, (int)y1);
                    p.addPoint((int)x1, (int)previousHeightx2);
                    p.addPoint((int)x2, (int)previousHeightx0);
                }
                g2.setPaint(this.getItemPaint(row, column));
                g2.setStroke(this.getItemStroke(row, column));
                g2.fill(p);
            }
        }
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            final Shape shape = new Rectangle2D.Double(x1 - 3.0, y1 - 3.0, 6.0, 6.0);
            this.addItemEntity(entities, dataset, row, column, shape);
        }
    }
    
    protected double getPreviousHeight(final CategoryDataset data, final int series, final int category) {
        double result = 0.0;
        for (int i = 0; i < series; ++i) {
            final Number tmp = data.getValue(i, category);
            if (tmp != null) {
                result += tmp.doubleValue();
            }
        }
        return result;
    }
}
