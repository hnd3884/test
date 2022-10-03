package org.jfree.chart.renderer.xy;

import java.awt.geom.Line2D;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import org.jfree.chart.entity.EntityCollection;
import java.awt.Stroke;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.util.ShapeUtilities;
import java.awt.Polygon;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.axis.ValueAxis;
import java.awt.Paint;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.LegendItem;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.XYPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.geom.GeneralPath;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import java.awt.Shape;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYAreaRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -4481971353973876747L;
    public static final int SHAPES = 1;
    public static final int LINES = 2;
    public static final int SHAPES_AND_LINES = 3;
    public static final int AREA = 4;
    public static final int AREA_AND_SHAPES = 5;
    private boolean plotShapes;
    private boolean plotLines;
    private boolean plotArea;
    private boolean showOutline;
    private transient Shape legendArea;
    
    public XYAreaRenderer() {
        this(4);
    }
    
    public XYAreaRenderer(final int type) {
        this(type, null, null);
    }
    
    public XYAreaRenderer(final int type, final XYToolTipGenerator toolTipGenerator, final XYURLGenerator urlGenerator) {
        this.setBaseToolTipGenerator(toolTipGenerator);
        this.setURLGenerator(urlGenerator);
        if (type == 1) {
            this.plotShapes = true;
        }
        if (type == 2) {
            this.plotLines = true;
        }
        if (type == 3) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        if (type == 4) {
            this.plotArea = true;
        }
        if (type == 5) {
            this.plotArea = true;
            this.plotShapes = true;
        }
        this.showOutline = false;
        final GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
    }
    
    public boolean isOutline() {
        return this.showOutline;
    }
    
    public void setOutline(final boolean show) {
        this.showOutline = show;
    }
    
    public boolean getPlotShapes() {
        return this.plotShapes;
    }
    
    public boolean getPlotLines() {
        return this.plotLines;
    }
    
    public boolean getPlotArea() {
        return this.plotArea;
    }
    
    public Shape getLegendArea() {
        return this.legendArea;
    }
    
    public void setLegendArea(final Shape area) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        this.legendArea = area;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot, final XYDataset data, final PlotRenderingInfo info) {
        final XYAreaRendererState state = new XYAreaRendererState(info);
        return state;
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        LegendItem result = null;
        final XYPlot xyplot = this.getPlot();
        if (xyplot != null) {
            final XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                final XYSeriesLabelGenerator lg = this.getLegendItemLabelGenerator();
                final String description;
                final String label = description = lg.generateLabel(dataset, series);
                String toolTipText = null;
                if (this.getLegendItemToolTipGenerator() != null) {
                    toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
                }
                String urlText = null;
                if (this.getLegendItemURLGenerator() != null) {
                    urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
                }
                final Paint paint = this.getSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, urlText, this.legendArea, paint);
            }
        }
        return result;
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        final XYAreaRendererState areaState = (XYAreaRendererState)state;
        final double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        final int itemCount = dataset.getItemCount(series);
        final double x2 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y2 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        final double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
        final double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());
        final double x3 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        double y3 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (Double.isNaN(y3)) {
            y3 = 0.0;
        }
        final double transX3 = domainAxis.valueToJava2D(x3, dataArea, plot.getDomainAxisEdge());
        final double transY3 = rangeAxis.valueToJava2D(y3, dataArea, plot.getRangeAxisEdge());
        final double transZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
        Polygon hotspot = null;
        if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            hotspot = new Polygon();
            hotspot.addPoint((int)transZero, (int)((transX2 + transX1) / 2.0));
            hotspot.addPoint((int)((transY2 + transY1) / 2.0), (int)((transX2 + transX1) / 2.0));
            hotspot.addPoint((int)transY1, (int)transX1);
            hotspot.addPoint((int)((transY1 + transY3) / 2.0), (int)((transX1 + transX3) / 2.0));
            hotspot.addPoint((int)transZero, (int)((transX1 + transX3) / 2.0));
        }
        else {
            hotspot = new Polygon();
            hotspot.addPoint((int)((transX2 + transX1) / 2.0), (int)transZero);
            hotspot.addPoint((int)((transX2 + transX1) / 2.0), (int)((transY2 + transY1) / 2.0));
            hotspot.addPoint((int)transX1, (int)transY1);
            hotspot.addPoint((int)((transX1 + transX3) / 2.0), (int)((transY1 + transY3) / 2.0));
            hotspot.addPoint((int)((transX1 + transX3) / 2.0), (int)transZero);
        }
        if (item == 0) {
            areaState.area = new Polygon();
            final double zero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
            if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                areaState.area.addPoint((int)transX1, (int)zero);
            }
            else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                areaState.area.addPoint((int)zero, (int)transX1);
            }
        }
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            areaState.area.addPoint((int)transX1, (int)transY1);
        }
        else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            areaState.area.addPoint((int)transY1, (int)transX1);
        }
        final PlotOrientation orientation = plot.getOrientation();
        final Paint paint = this.getItemPaint(series, item);
        final Stroke stroke = this.getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        Shape shape = null;
        if (this.getPlotShapes()) {
            shape = this.getItemShape(series, item);
            if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
            }
            g2.draw(shape);
        }
        if (this.getPlotLines() && item > 0) {
            if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                areaState.line.setLine(transX2, transY2, transX1, transY1);
            }
            else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                areaState.line.setLine(transY2, transX2, transY1, transX1);
            }
            g2.draw(areaState.line);
        }
        if (this.getPlotArea() && item > 0 && item == itemCount - 1) {
            if (orientation == PlotOrientation.VERTICAL) {
                areaState.area.addPoint((int)transX1, (int)transZero);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                areaState.area.addPoint((int)transZero, (int)transX1);
            }
            g2.fill(areaState.area);
            if (this.isOutline()) {
                g2.setStroke(this.getItemOutlineStroke(series, item));
                g2.setPaint(this.getItemOutlinePaint(series, item));
                g2.draw(areaState.area);
            }
        }
        this.updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, orientation);
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null && hotspot != null) {
                String tip = null;
                final XYToolTipGenerator generator = this.getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (this.getURLGenerator() != null) {
                    url = this.getURLGenerator().generateURL(dataset, series, item);
                }
                final XYItemEntity entity = new XYItemEntity(hotspot, dataset, series, item, tip, url);
                entities.add(entity);
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYAreaRenderer)) {
            return false;
        }
        final XYAreaRenderer that = (XYAreaRenderer)obj;
        return this.plotArea == that.plotArea && this.plotLines == that.plotLines && this.plotShapes == that.plotShapes && this.showOutline == that.showOutline && ShapeUtilities.equal(this.legendArea, that.legendArea);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendArea = SerialUtilities.readShape(stream);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendArea, stream);
    }
    
    static class XYAreaRendererState extends XYItemRendererState
    {
        public Polygon area;
        public Line2D line;
        
        public XYAreaRendererState(final PlotRenderingInfo info) {
            super(info);
            this.area = new Polygon();
            this.line = new Line2D.Double();
        }
    }
}
