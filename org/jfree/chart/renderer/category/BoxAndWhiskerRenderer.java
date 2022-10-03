package org.jfree.chart.renderer.category;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import java.awt.geom.Point2D;
import java.util.Iterator;
import org.jfree.chart.renderer.OutlierList;
import java.util.List;
import java.util.Collections;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierListCollection;
import java.util.ArrayList;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Shape;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class BoxAndWhiskerRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 632027470694481177L;
    private transient Paint artifactPaint;
    private boolean fillBox;
    private double itemMargin;
    
    public BoxAndWhiskerRenderer() {
        this.artifactPaint = Color.black;
        this.fillBox = true;
        this.itemMargin = 0.2;
    }
    
    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }
    
    public void setArtifactPaint(final Paint paint) {
        this.artifactPaint = paint;
    }
    
    public boolean getFillBox() {
        return this.fillBox;
    }
    
    public void setFillBox(final boolean flag) {
        this.fillBox = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getItemMargin() {
        return this.itemMargin;
    }
    
    public void setItemMargin(final double margin) {
        this.itemMargin = margin;
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        final CategoryPlot cp = this.getPlot();
        if (cp == null) {
            return null;
        }
        final CategoryDataset dataset = cp.getDataset(datasetIndex);
        final String description;
        final String label = description = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
        String toolTipText = null;
        if (this.getLegendItemToolTipGenerator() != null) {
            toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.getLegendItemURLGenerator() != null) {
            urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        final Shape shape = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
        final Paint paint = this.getSeriesPaint(series);
        final Paint outlinePaint = this.getSeriesOutlinePaint(series);
        final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
        return new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
    }
    
    public CategoryItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final CategoryPlot plot, final int rendererIndex, final PlotRenderingInfo info) {
        final CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        final CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        final CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            final int columns = dataset.getColumnCount();
            final int rows = dataset.getRowCount();
            double space = 0.0;
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = this.getItemMargin();
            }
            final double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(used / (dataset.getColumnCount() * dataset.getRowCount()));
            }
            else {
                state.setBarWidth(used);
            }
        }
        return state;
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        if (!(dataset instanceof BoxAndWhiskerCategoryDataset)) {
            throw new IllegalArgumentException("BoxAndWhiskerRenderer.drawItem() : the data should be of type BoxAndWhiskerCategoryDataset only.");
        }
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            this.drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            this.drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
    }
    
    public void drawHorizontalItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column) {
        final BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset)dataset;
        final double categoryEnd = domainAxis.getCategoryEnd(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double categoryStart = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double categoryWidth = Math.abs(categoryEnd - categoryStart);
        double yy = categoryStart;
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = dataArea.getWidth() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            final double usedWidth = state.getBarWidth() * seriesCount + seriesGap * (seriesCount - 1);
            final double offset = (categoryWidth - usedWidth) / 2.0;
            yy = yy + offset + row * (state.getBarWidth() + seriesGap);
        }
        else {
            final double offset2 = (categoryWidth - state.getBarWidth()) / 2.0;
            yy += offset2;
        }
        final Paint p = this.getItemPaint(row, column);
        if (p != null) {
            g2.setPaint(p);
        }
        final Stroke s = this.getItemStroke(row, column);
        g2.setStroke(s);
        final RectangleEdge location = plot.getRangeAxisEdge();
        final Number xQ1 = bawDataset.getQ1Value(row, column);
        final Number xQ2 = bawDataset.getQ3Value(row, column);
        final Number xMax = bawDataset.getMaxRegularValue(row, column);
        final Number xMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (xQ1 != null && xQ2 != null && xMax != null && xMin != null) {
            final double xxQ1 = rangeAxis.valueToJava2D(xQ1.doubleValue(), dataArea, location);
            final double xxQ2 = rangeAxis.valueToJava2D(xQ2.doubleValue(), dataArea, location);
            final double xxMax = rangeAxis.valueToJava2D(xMax.doubleValue(), dataArea, location);
            final double xxMin = rangeAxis.valueToJava2D(xMin.doubleValue(), dataArea, location);
            final double yymid = yy + state.getBarWidth() / 2.0;
            g2.draw(new Line2D.Double(xxMax, yymid, xxQ2, yymid));
            g2.draw(new Line2D.Double(xxMax, yy, xxMax, yy + state.getBarWidth()));
            g2.draw(new Line2D.Double(xxMin, yymid, xxQ1, yymid));
            g2.draw(new Line2D.Double(xxMin, yy, xxMin, yy + state.getBarWidth()));
            box = new Rectangle2D.Double(Math.min(xxQ1, xxQ2), yy, Math.abs(xxQ1 - xxQ2), state.getBarWidth());
            if (this.fillBox) {
                g2.fill(box);
            }
            g2.draw(box);
        }
        g2.setPaint(this.artifactPaint);
        double aRadius = 0.0;
        final Number xMean = bawDataset.getMeanValue(row, column);
        if (xMean != null) {
            final double xxMean = rangeAxis.valueToJava2D(xMean.doubleValue(), dataArea, location);
            aRadius = state.getBarWidth() / 4.0;
            final Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xxMean - aRadius, yy + aRadius, aRadius * 2.0, aRadius * 2.0);
            g2.fill(avgEllipse);
            g2.draw(avgEllipse);
        }
        final Number xMedian = bawDataset.getMedianValue(row, column);
        if (xMedian != null) {
            final double xxMedian = rangeAxis.valueToJava2D(xMedian.doubleValue(), dataArea, location);
            g2.draw(new Line2D.Double(xxMedian, yy, xxMedian, yy + state.getBarWidth()));
        }
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                String tip = null;
                final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (this.getItemURLGenerator(row, column) != null) {
                    url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                final CategoryItemEntity entity = new CategoryItemEntity(box, tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
    
    public void drawVerticalItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column) {
        final BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset)dataset;
        final double categoryEnd = domainAxis.getCategoryEnd(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double categoryStart = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double categoryWidth = categoryEnd - categoryStart;
        double xx = categoryStart;
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = dataArea.getWidth() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            final double usedWidth = state.getBarWidth() * seriesCount + seriesGap * (seriesCount - 1);
            final double offset = (categoryWidth - usedWidth) / 2.0;
            xx = xx + offset + row * (state.getBarWidth() + seriesGap);
        }
        else {
            final double offset2 = (categoryWidth - state.getBarWidth()) / 2.0;
            xx += offset2;
        }
        double yyAverage = 0.0;
        final Paint p = this.getItemPaint(row, column);
        if (p != null) {
            g2.setPaint(p);
        }
        final Stroke s = this.getItemStroke(row, column);
        g2.setStroke(s);
        double aRadius = 0.0;
        final RectangleEdge location = plot.getRangeAxisEdge();
        final Number yQ1 = bawDataset.getQ1Value(row, column);
        final Number yQ2 = bawDataset.getQ3Value(row, column);
        final Number yMax = bawDataset.getMaxRegularValue(row, column);
        final Number yMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (yQ1 != null && yQ2 != null && yMax != null && yMin != null) {
            final double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
            final double yyQ2 = rangeAxis.valueToJava2D(yQ2.doubleValue(), dataArea, location);
            final double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
            final double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
            final double xxmid = xx + state.getBarWidth() / 2.0;
            g2.draw(new Line2D.Double(xxmid, yyMax, xxmid, yyQ2));
            g2.draw(new Line2D.Double(xx, yyMax, xx + state.getBarWidth(), yyMax));
            g2.draw(new Line2D.Double(xxmid, yyMin, xxmid, yyQ1));
            g2.draw(new Line2D.Double(xx, yyMin, xx + state.getBarWidth(), yyMin));
            box = new Rectangle2D.Double(xx, Math.min(yyQ1, yyQ2), state.getBarWidth(), Math.abs(yyQ1 - yyQ2));
            if (this.fillBox) {
                g2.fill(box);
            }
            g2.draw(box);
        }
        g2.setPaint(this.artifactPaint);
        final Number yMean = bawDataset.getMeanValue(row, column);
        if (yMean != null) {
            yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
            aRadius = state.getBarWidth() / 4.0;
            final Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage - aRadius, aRadius * 2.0, aRadius * 2.0);
            g2.fill(avgEllipse);
            g2.draw(avgEllipse);
        }
        final Number yMedian = bawDataset.getMedianValue(row, column);
        if (yMedian != null) {
            final double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
            g2.draw(new Line2D.Double(xx, yyMedian, xx + state.getBarWidth(), yyMedian));
        }
        final double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
        final double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;
        g2.setPaint(p);
        final double oRadius = state.getBarWidth() / 3.0;
        final List outliers = new ArrayList();
        final OutlierListCollection outlierListCollection = new OutlierListCollection();
        final List yOutliers = bawDataset.getOutliers(row, column);
        if (yOutliers != null) {
            for (int i = 0; i < yOutliers.size(); ++i) {
                final double outlier = yOutliers.get(i).doubleValue();
                final Number minOutlier = bawDataset.getMinOutlier(row, column);
                final Number maxOutlier = bawDataset.getMaxOutlier(row, column);
                final Number minRegular = bawDataset.getMinRegularValue(row, column);
                final Number maxRegular = bawDataset.getMaxRegularValue(row, column);
                if (outlier > maxOutlier.doubleValue()) {
                    outlierListCollection.setHighFarOut(true);
                }
                else if (outlier < minOutlier.doubleValue()) {
                    outlierListCollection.setLowFarOut(true);
                }
                else if (outlier > maxRegular.doubleValue()) {
                    final double yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
                }
                else if (outlier < minRegular.doubleValue()) {
                    final double yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
                }
                Collections.sort((List<Comparable>)outliers);
            }
            Iterator iterator = outliers.iterator();
            while (iterator.hasNext()) {
                final Outlier outlier2 = iterator.next();
                outlierListCollection.add(outlier2);
            }
            iterator = outlierListCollection.iterator();
            while (iterator.hasNext()) {
                final OutlierList list = iterator.next();
                final Outlier outlier3 = list.getAveragedOutlier();
                final Point2D point = outlier3.getPoint();
                if (list.isMultiple()) {
                    this.drawMultipleEllipse(point, state.getBarWidth(), oRadius, g2);
                }
                else {
                    this.drawEllipse(point, oRadius, g2);
                }
            }
            if (outlierListCollection.isHighFarOut()) {
                this.drawHighFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, maxAxisValue);
            }
            if (outlierListCollection.isLowFarOut()) {
                this.drawLowFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, minAxisValue);
            }
        }
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                String tip = null;
                final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (this.getItemURLGenerator(row, column) != null) {
                    url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                final CategoryItemEntity entity = new CategoryItemEntity(box, tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
    
    private void drawEllipse(final Point2D point, final double oRadius, final Graphics2D g2) {
        final Ellipse2D dot = new Ellipse2D.Double(point.getX() + oRadius / 2.0, point.getY(), oRadius, oRadius);
        g2.draw(dot);
    }
    
    private void drawMultipleEllipse(final Point2D point, final double boxWidth, final double oRadius, final Graphics2D g2) {
        final Ellipse2D dot1 = new Ellipse2D.Double(point.getX() - boxWidth / 2.0 + oRadius, point.getY(), oRadius, oRadius);
        final Ellipse2D dot2 = new Ellipse2D.Double(point.getX() + boxWidth / 2.0, point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }
    
    private void drawHighFarOut(final double aRadius, final Graphics2D g2, final double xx, final double m) {
        final double side = aRadius * 2.0;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }
    
    private void drawLowFarOut(final double aRadius, final Graphics2D g2, final double xx, final double m) {
        final double side = aRadius * 2.0;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BoxAndWhiskerRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final BoxAndWhiskerRenderer that = (BoxAndWhiskerRenderer)obj;
        return PaintUtilities.equal(this.artifactPaint, that.artifactPaint) && this.fillBox == that.fillBox && this.itemMargin == that.itemMargin;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.artifactPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.artifactPaint = SerialUtilities.readPaint(stream);
    }
}
