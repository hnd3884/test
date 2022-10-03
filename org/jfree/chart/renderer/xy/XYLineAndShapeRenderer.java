package org.jfree.chart.renderer.xy;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import org.jfree.util.ObjectUtilities;
import java.awt.Stroke;
import java.awt.Paint;
import org.jfree.chart.LegendItem;
import org.jfree.util.ShapeUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.axis.ValueAxis;
import java.awt.geom.GeneralPath;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.XYPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.util.BooleanUtilities;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.geom.Line2D;
import java.awt.Shape;
import org.jfree.util.BooleanList;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYLineAndShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -7435246895986425885L;
    private Boolean linesVisible;
    private BooleanList seriesLinesVisible;
    private boolean baseLinesVisible;
    private transient Shape legendLine;
    private Boolean shapesVisible;
    private BooleanList seriesShapesVisible;
    private boolean baseShapesVisible;
    private Boolean shapesFilled;
    private BooleanList seriesShapesFilled;
    private boolean baseShapesFilled;
    private boolean drawOutlines;
    private boolean useFillPaint;
    private boolean useOutlinePaint;
    private boolean drawSeriesLineAsPath;
    
    public XYLineAndShapeRenderer() {
        this(true, true);
    }
    
    public XYLineAndShapeRenderer(final boolean lines, final boolean shapes) {
        this.linesVisible = null;
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.shapesFilled = null;
        this.useFillPaint = false;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.drawSeriesLineAsPath = false;
    }
    
    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }
    
    public void setDrawSeriesLineAsPath(final boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public int getPassCount() {
        return 2;
    }
    
    public boolean getItemLineVisible(final int series, final int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = this.getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseLinesVisible;
    }
    
    public Boolean getLinesVisible() {
        return this.linesVisible;
    }
    
    public void setLinesVisible(final Boolean visible) {
        this.linesVisible = visible;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void setLinesVisible(final boolean visible) {
        this.setLinesVisible(BooleanUtilities.valueOf(visible));
    }
    
    public Boolean getSeriesLinesVisible(final int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }
    
    public void setSeriesLinesVisible(final int series, final Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void setSeriesLinesVisible(final int series, final boolean visible) {
        this.setSeriesLinesVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }
    
    public void setBaseLinesVisible(final boolean flag) {
        this.baseLinesVisible = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Shape getLegendLine() {
        return this.legendLine;
    }
    
    public void setLegendLine(final Shape line) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        this.legendLine = line;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getItemShapeVisible(final int series, final int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = this.getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseShapesVisible;
    }
    
    public Boolean getShapesVisible() {
        return this.shapesVisible;
    }
    
    public void setShapesVisible(final Boolean visible) {
        this.shapesVisible = visible;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void setShapesVisible(final boolean visible) {
        this.setShapesVisible(BooleanUtilities.valueOf(visible));
    }
    
    public Boolean getSeriesShapesVisible(final int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }
    
    public void setSeriesShapesVisible(final int series, final boolean visible) {
        this.setSeriesShapesVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    public void setSeriesShapesVisible(final int series, final Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }
    
    public void setBaseShapesVisible(final boolean flag) {
        this.baseShapesVisible = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getItemShapeFilled(final int series, final int item) {
        Boolean flag = this.shapesFilled;
        if (flag == null) {
            flag = this.getSeriesShapesFilled(series);
        }
        if (flag != null) {
            return flag;
        }
        return this.baseShapesFilled;
    }
    
    public void setShapesFilled(final boolean filled) {
        this.setShapesFilled(BooleanUtilities.valueOf(filled));
    }
    
    public void setShapesFilled(final Boolean filled) {
        this.shapesFilled = filled;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Boolean getSeriesShapesFilled(final int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }
    
    public void setSeriesShapesFilled(final int series, final boolean flag) {
        this.setSeriesShapesFilled(series, BooleanUtilities.valueOf(flag));
    }
    
    public void setSeriesShapesFilled(final int series, final Boolean flag) {
        this.seriesShapesFilled.setBoolean(series, flag);
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }
    
    public void setBaseShapesFilled(final boolean flag) {
        this.baseShapesFilled = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }
    
    public void setDrawOutlines(final boolean flag) {
        this.drawOutlines = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }
    
    public void setUseFillPaint(final boolean flag) {
        this.useFillPaint = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }
    
    public void setUseOutlinePaint(final boolean flag) {
        this.useOutlinePaint = flag;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot, final XYDataset data, final PlotRenderingInfo info) {
        final State state = new State(info);
        state.seriesPath = new GeneralPath();
        return state;
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        if (this.isLinePass(pass)) {
            if (item == 0 && this.drawSeriesLineAsPath) {
                final State s = (State)state;
                s.seriesPath.reset();
                s.lastPointGood = false;
            }
            if (this.getItemLineVisible(series, item)) {
                if (this.drawSeriesLineAsPath) {
                    this.drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
                else {
                    this.drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
            }
        }
        else if (this.isItemPass(pass)) {
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }
            this.drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
        }
    }
    
    protected boolean isLinePass(final int pass) {
        return pass == 0;
    }
    
    protected boolean isItemPass(final int pass) {
        return pass == 1;
    }
    
    protected void drawPrimaryLine(final XYItemRendererState state, final Graphics2D g2, final XYPlot plot, final XYDataset dataset, final int pass, final int series, final int item, final ValueAxis domainAxis, final ValueAxis rangeAxis, final Rectangle2D dataArea) {
        if (item == 0) {
            return;
        }
        final double x1 = dataset.getXValue(series, item);
        final double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        final double x2 = dataset.getXValue(series, item - 1);
        final double y2 = dataset.getYValue(series, item - 1);
        if (Double.isNaN(y2) || Double.isNaN(x2)) {
            return;
        }
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double transX0 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
        final double transY0 = rangeAxis.valueToJava2D(y2, dataArea, yAxisLocation);
        final double transX2 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        final double transY2 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX2) || Double.isNaN(transY2)) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY2, transX2);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX2, transY2);
        }
        if (state.workingLine.intersects(dataArea)) {
            this.drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }
    
    protected void drawFirstPassShape(final Graphics2D g2, final int pass, final int series, final int item, final Shape shape) {
        g2.setStroke(this.getItemStroke(series, item));
        g2.setPaint(this.getItemPaint(series, item));
        g2.draw(shape);
    }
    
    protected void drawPrimaryLineAsPath(final XYItemRendererState state, final Graphics2D g2, final XYPlot plot, final XYDataset dataset, final int pass, final int series, final int item, final ValueAxis domainAxis, final ValueAxis rangeAxis, final Rectangle2D dataArea) {
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double x1 = dataset.getXValue(series, item);
        final double y1 = dataset.getYValue(series, item);
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        final State s = (State)state;
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x2 = (float)transX1;
            float y2 = (float)transY1;
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x2 = (float)transY1;
                y2 = (float)transX1;
            }
            if (s.isLastPointGood()) {
                s.seriesPath.lineTo(x2, y2);
            }
            else {
                s.seriesPath.moveTo(x2, y2);
            }
            s.setLastPointGood(true);
        }
        else {
            s.setLastPointGood(false);
        }
        if (item == dataset.getItemCount(series) - 1) {
            this.drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }
    
    protected void drawSecondaryPass(final Graphics2D g2, final XYPlot plot, final XYDataset dataset, final int pass, final int series, final int item, final ValueAxis domainAxis, final Rectangle2D dataArea, final ValueAxis rangeAxis, final CrosshairState crosshairState, final EntityCollection entities) {
        Shape entityArea = null;
        final double x1 = dataset.getXValue(series, item);
        final double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (this.getItemShapeVisible(series, item)) {
            Shape shape = this.getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
            }
            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (this.getItemShapeFilled(series, item)) {
                    if (this.useFillPaint) {
                        g2.setPaint(this.getItemFillPaint(series, item));
                    }
                    else {
                        g2.setPaint(this.getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (this.getUseOutlinePaint()) {
                        g2.setPaint(this.getItemOutlinePaint(series, item));
                    }
                    else {
                        g2.setPaint(this.getItemPaint(series, item));
                    }
                    g2.setStroke(this.getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
            }
        }
        if (this.isItemLabelVisible(series, item)) {
            double xx = transX1;
            double yy = transY1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                xx = transY1;
                yy = transX1;
            }
            this.drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0);
        }
        this.updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, plot.getOrientation());
        if (entities != null) {
            this.addEntity(entities, entityArea, dataset, series, item, transX1, transY1);
        }
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        final XYPlot plot = this.getPlot();
        if (plot == null) {
            return null;
        }
        LegendItem result = null;
        final XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null && this.getItemVisible(series, 0)) {
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
            final boolean shapeIsVisible = this.getItemShapeVisible(series, 0);
            final Shape shape = this.getSeriesShape(series);
            final boolean shapeIsFilled = this.getItemShapeFilled(series, 0);
            final Paint fillPaint = this.useFillPaint ? this.getSeriesFillPaint(series) : this.getSeriesPaint(series);
            final boolean shapeOutlineVisible = this.drawOutlines;
            final Paint outlinePaint = this.useOutlinePaint ? this.getSeriesOutlinePaint(series) : this.getSeriesPaint(series);
            final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
            final boolean lineVisible = this.getItemLineVisible(series, 0);
            final Stroke lineStroke = this.getSeriesStroke(series);
            final Paint linePaint = this.getSeriesPaint(series);
            result = new LegendItem(label, description, toolTipText, urlText, shapeIsVisible, shape, shapeIsFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, lineVisible, this.legendLine, lineStroke, linePaint);
            result.setSeriesIndex(series);
            result.setDatasetIndex(datasetIndex);
        }
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final XYLineAndShapeRenderer that = (XYLineAndShapeRenderer)obj;
        return ObjectUtilities.equal((Object)this.linesVisible, (Object)that.linesVisible) && ObjectUtilities.equal((Object)this.seriesLinesVisible, (Object)that.seriesLinesVisible) && this.baseLinesVisible == that.baseLinesVisible && ShapeUtilities.equal(this.legendLine, that.legendLine) && ObjectUtilities.equal((Object)this.shapesVisible, (Object)that.shapesVisible) && ObjectUtilities.equal((Object)this.seriesShapesVisible, (Object)that.seriesShapesVisible) && this.baseShapesVisible == that.baseShapesVisible && ObjectUtilities.equal((Object)this.shapesFilled, (Object)that.shapesFilled) && ObjectUtilities.equal((Object)this.seriesShapesFilled, (Object)that.seriesShapesFilled) && this.baseShapesFilled == that.baseShapesFilled && this.drawOutlines == that.drawOutlines && this.useOutlinePaint == that.useOutlinePaint;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
    }
    
    public static class State extends XYItemRendererState
    {
        public GeneralPath seriesPath;
        private boolean lastPointGood;
        
        public State(final PlotRenderingInfo info) {
            super(info);
        }
        
        public boolean isLastPointGood() {
            return this.lastPointGood;
        }
        
        public void setLastPointGood(final boolean good) {
            this.lastPointGood = good;
        }
    }
}
