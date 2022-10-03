package org.jfree.chart.plot;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.data.Range;
import java.awt.geom.Line2D;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.general.DatasetUtilities;
import java.util.Iterator;
import org.jfree.chart.axis.AxisCollection;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Shape;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.ChartRenderingInfo;
import java.awt.geom.Point2D;
import org.jfree.chart.axis.Axis;
import java.awt.Graphics2D;
import org.jfree.chart.annotations.CategoryAnnotation;
import java.util.Collections;
import java.util.Collection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.event.PlotChangeEvent;
import java.util.ArrayList;
import org.jfree.ui.Layer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import java.util.List;
import java.util.Map;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.util.SortOrder;
import org.jfree.util.ObjectList;
import org.jfree.ui.RectangleInsets;
import java.util.ResourceBundle;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;
import org.jfree.chart.event.RendererChangeListener;

public class CategoryPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -3537691700434728188L;
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Font DEFAULT_VALUE_LABEL_FONT;
    protected static ResourceBundle localizationResources;
    private PlotOrientation orientation;
    private RectangleInsets axisOffset;
    private ObjectList domainAxes;
    private ObjectList domainAxisLocations;
    private boolean drawSharedDomainAxis;
    private ObjectList rangeAxes;
    private ObjectList rangeAxisLocations;
    private ObjectList datasets;
    private ObjectList datasetToDomainAxisMap;
    private ObjectList datasetToRangeAxisMap;
    private ObjectList renderers;
    private DatasetRenderingOrder renderingOrder;
    private SortOrder columnRenderingOrder;
    private SortOrder rowRenderingOrder;
    private boolean domainGridlinesVisible;
    private CategoryAnchor domainGridlinePosition;
    private transient Stroke domainGridlineStroke;
    private transient Paint domainGridlinePaint;
    private boolean rangeGridlinesVisible;
    private transient Stroke rangeGridlineStroke;
    private transient Paint rangeGridlinePaint;
    private double anchorValue;
    private boolean rangeCrosshairVisible;
    private double rangeCrosshairValue;
    private transient Stroke rangeCrosshairStroke;
    private transient Paint rangeCrosshairPaint;
    private boolean rangeCrosshairLockedOnData;
    private Map foregroundDomainMarkers;
    private Map backgroundDomainMarkers;
    private Map foregroundRangeMarkers;
    private Map backgroundRangeMarkers;
    private List annotations;
    private int weight;
    private AxisSpace fixedDomainAxisSpace;
    private AxisSpace fixedRangeAxisSpace;
    private LegendItemCollection fixedLegendItems;
    
    public CategoryPlot() {
        this(null, null, null, null);
    }
    
    public CategoryPlot(final CategoryDataset dataset, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryItemRenderer renderer) {
        this.renderingOrder = DatasetRenderingOrder.REVERSE;
        this.columnRenderingOrder = SortOrder.ASCENDING;
        this.rowRenderingOrder = SortOrder.ASCENDING;
        this.rangeCrosshairLockedOnData = true;
        this.orientation = PlotOrientation.VERTICAL;
        this.domainAxes = new ObjectList();
        this.domainAxisLocations = new ObjectList();
        this.rangeAxes = new ObjectList();
        this.rangeAxisLocations = new ObjectList();
        this.datasetToDomainAxisMap = new ObjectList();
        this.datasetToRangeAxisMap = new ObjectList();
        this.renderers = new ObjectList();
        (this.datasets = new ObjectList()).set(0, (Object)dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT, false);
        this.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, false);
        this.renderers.set(0, (Object)renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.domainAxes.set(0, (Object)domainAxis);
        this.mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.drawSharedDomainAxis = false;
        this.rangeAxes.set(0, (Object)rangeAxis);
        this.mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.configureDomainAxes();
        this.configureRangeAxes();
        this.domainGridlinesVisible = false;
        this.domainGridlinePosition = CategoryAnchor.MIDDLE;
        this.domainGridlineStroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        this.foregroundDomainMarkers = new HashMap();
        this.backgroundDomainMarkers = new HashMap();
        this.foregroundRangeMarkers = new HashMap();
        this.backgroundRangeMarkers = new HashMap();
        final Marker baseline = new ValueMarker(0.0, new Color(0.8f, 0.8f, 0.8f, 0.5f), new BasicStroke(1.0f), new Color(0.85f, 0.85f, 0.95f, 0.5f), new BasicStroke(1.0f), 0.6f);
        this.addRangeMarker(baseline, Layer.BACKGROUND);
        this.anchorValue = 0.0;
        this.annotations = new ArrayList();
    }
    
    public String getPlotType() {
        return CategoryPlot.localizationResources.getString("Category_Plot");
    }
    
    public PlotOrientation getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        this.orientation = orientation;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }
    
    public void setAxisOffset(final RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.axisOffset = offset;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public CategoryAxis getDomainAxis() {
        return this.getDomainAxis(0);
    }
    
    public CategoryAxis getDomainAxis(final int index) {
        CategoryAxis result = null;
        if (index < this.domainAxes.size()) {
            result = (CategoryAxis)this.domainAxes.get(index);
        }
        if (result == null) {
            final Plot parent = this.getParent();
            if (parent instanceof CategoryPlot) {
                final CategoryPlot cp = (CategoryPlot)parent;
                result = cp.getDomainAxis(index);
            }
        }
        return result;
    }
    
    public void setDomainAxis(final CategoryAxis axis) {
        this.setDomainAxis(0, axis);
    }
    
    public void setDomainAxis(final int index, final CategoryAxis axis) {
        this.setDomainAxis(index, axis, true);
    }
    
    public void setDomainAxis(final int index, final CategoryAxis axis, final boolean notify) {
        final CategoryAxis existing = (CategoryAxis)this.domainAxes.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.set(index, (Object)axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setDomainAxes(final CategoryAxis[] axes) {
        for (int i = 0; i < axes.length; ++i) {
            this.setDomainAxis(i, axes[i], false);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public AxisLocation getDomainAxisLocation() {
        return this.getDomainAxisLocation(0);
    }
    
    public AxisLocation getDomainAxisLocation(final int index) {
        AxisLocation result = null;
        if (index < this.domainAxisLocations.size()) {
            result = (AxisLocation)this.domainAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(this.getDomainAxisLocation(0));
        }
        return result;
    }
    
    public void setDomainAxisLocation(final AxisLocation location) {
        this.setDomainAxisLocation(location, true);
    }
    
    public void setDomainAxisLocation(final AxisLocation location, final boolean notify) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        this.setDomainAxisLocation(0, location);
    }
    
    public void setDomainAxisLocation(final int index, final AxisLocation location) {
        this.domainAxisLocations.set(index, (Object)location);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public RectangleEdge getDomainAxisEdge() {
        return this.getDomainAxisEdge(0);
    }
    
    public RectangleEdge getDomainAxisEdge(final int index) {
        RectangleEdge result = null;
        final AxisLocation location = this.getDomainAxisLocation(index);
        if (location != null) {
            result = Plot.resolveDomainAxisLocation(location, this.orientation);
        }
        else {
            result = RectangleEdge.opposite(this.getDomainAxisEdge(0));
        }
        return result;
    }
    
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }
    
    public void clearDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            final CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void configureDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            final CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }
    
    public ValueAxis getRangeAxis() {
        return this.getRangeAxis(0);
    }
    
    public ValueAxis getRangeAxis(final int index) {
        ValueAxis result = null;
        if (index < this.rangeAxes.size()) {
            result = (ValueAxis)this.rangeAxes.get(index);
        }
        if (result == null) {
            final Plot parent = this.getParent();
            if (parent instanceof CategoryPlot) {
                final CategoryPlot cp = (CategoryPlot)parent;
                result = cp.getRangeAxis(index);
            }
        }
        return result;
    }
    
    public void setRangeAxis(final ValueAxis axis) {
        this.setRangeAxis(0, axis);
    }
    
    public void setRangeAxis(final int index, final ValueAxis axis) {
        this.setRangeAxis(index, axis, true);
    }
    
    public void setRangeAxis(final int index, final ValueAxis axis, final boolean notify) {
        final ValueAxis existing = (ValueAxis)this.rangeAxes.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.set(index, (Object)axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setRangeAxes(final ValueAxis[] axes) {
        for (int i = 0; i < axes.length; ++i) {
            this.setRangeAxis(i, axes[i], false);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public AxisLocation getRangeAxisLocation() {
        return this.getRangeAxisLocation(0);
    }
    
    public AxisLocation getRangeAxisLocation(final int index) {
        AxisLocation result = null;
        if (index < this.rangeAxisLocations.size()) {
            result = (AxisLocation)this.rangeAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(this.getRangeAxisLocation(0));
        }
        return result;
    }
    
    public void setRangeAxisLocation(final AxisLocation location) {
        this.setRangeAxisLocation(location, true);
    }
    
    public void setRangeAxisLocation(final AxisLocation location, final boolean notify) {
        this.setRangeAxisLocation(0, location, notify);
    }
    
    public void setRangeAxisLocation(final int index, final AxisLocation location) {
        this.setRangeAxisLocation(index, location, true);
    }
    
    public void setRangeAxisLocation(final int index, final AxisLocation location, final boolean notify) {
        this.rangeAxisLocations.set(index, (Object)location);
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public RectangleEdge getRangeAxisEdge() {
        return this.getRangeAxisEdge(0);
    }
    
    public RectangleEdge getRangeAxisEdge(final int index) {
        final AxisLocation location = this.getRangeAxisLocation(index);
        RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(this.getRangeAxisEdge(0));
        }
        return result;
    }
    
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }
    
    public void clearRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void configureRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }
    
    public CategoryDataset getDataset() {
        return this.getDataset(0);
    }
    
    public CategoryDataset getDataset(final int index) {
        CategoryDataset result = null;
        if (this.datasets.size() > index) {
            result = (CategoryDataset)this.datasets.get(index);
        }
        return result;
    }
    
    public void setDataset(final CategoryDataset dataset) {
        this.setDataset(0, dataset);
    }
    
    public void setDataset(final int index, final CategoryDataset dataset) {
        final CategoryDataset existing = (CategoryDataset)this.datasets.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, (Object)dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        final DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        this.datasetChanged(event);
    }
    
    public int getDatasetCount() {
        return this.datasets.size();
    }
    
    public void mapDatasetToDomainAxis(final int index, final int axisIndex) {
        this.datasetToDomainAxisMap.set(index, (Object)new Integer(axisIndex));
        this.datasetChanged(new DatasetChangeEvent(this, this.getDataset(index)));
    }
    
    public CategoryAxis getDomainAxisForDataset(final int index) {
        CategoryAxis result = this.getDomainAxis();
        final Integer axisIndex = (Integer)this.datasetToDomainAxisMap.get(index);
        if (axisIndex != null) {
            result = this.getDomainAxis(axisIndex);
        }
        return result;
    }
    
    public void mapDatasetToRangeAxis(final int index, final int axisIndex) {
        this.datasetToRangeAxisMap.set(index, (Object)new Integer(axisIndex));
        this.datasetChanged(new DatasetChangeEvent(this, this.getDataset(index)));
    }
    
    public ValueAxis getRangeAxisForDataset(final int index) {
        ValueAxis result = this.getRangeAxis();
        final Integer axisIndex = (Integer)this.datasetToRangeAxisMap.get(index);
        if (axisIndex != null) {
            result = this.getRangeAxis(axisIndex);
        }
        return result;
    }
    
    public CategoryItemRenderer getRenderer() {
        return this.getRenderer(0);
    }
    
    public CategoryItemRenderer getRenderer(final int index) {
        CategoryItemRenderer result = null;
        if (this.renderers.size() > index) {
            result = (CategoryItemRenderer)this.renderers.get(index);
        }
        return result;
    }
    
    public void setRenderer(final CategoryItemRenderer renderer) {
        this.setRenderer(0, renderer, true);
    }
    
    public void setRenderer(final CategoryItemRenderer renderer, final boolean notify) {
        this.setRenderer(0, renderer, notify);
    }
    
    public void setRenderer(final int index, final CategoryItemRenderer renderer) {
        this.setRenderer(index, renderer, true);
    }
    
    public void setRenderer(final int index, final CategoryItemRenderer renderer, final boolean notify) {
        final CategoryItemRenderer existing = (CategoryItemRenderer)this.renderers.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.set(index, (Object)renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.configureDomainAxes();
        this.configureRangeAxes();
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setRenderers(final CategoryItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; ++i) {
            this.setRenderer(i, renderers[i], false);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public CategoryItemRenderer getRendererForDataset(final CategoryDataset dataset) {
        CategoryItemRenderer result = null;
        for (int i = 0; i < this.datasets.size(); ++i) {
            if (this.datasets.get(i) == dataset) {
                result = (CategoryItemRenderer)this.renderers.get(i);
                break;
            }
        }
        return result;
    }
    
    public int getIndexOf(final CategoryItemRenderer renderer) {
        return this.renderers.indexOf((Object)renderer);
    }
    
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.renderingOrder;
    }
    
    public void setDatasetRenderingOrder(final DatasetRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.renderingOrder = order;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public SortOrder getColumnRenderingOrder() {
        return this.columnRenderingOrder;
    }
    
    public void setColumnRenderingOrder(final SortOrder order) {
        this.columnRenderingOrder = order;
    }
    
    public SortOrder getRowRenderingOrder() {
        return this.rowRenderingOrder;
    }
    
    public void setRowRenderingOrder(final SortOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.rowRenderingOrder = order;
    }
    
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }
    
    public void setDomainGridlinesVisible(final boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }
    
    public void setDomainGridlinePosition(final CategoryAnchor position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.domainGridlinePosition = position;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }
    
    public void setDomainGridlineStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' not permitted.");
        }
        this.domainGridlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }
    
    public void setDomainGridlinePaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }
    
    public void setRangeGridlinesVisible(final boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }
    
    public void setRangeGridlineStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeGridlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }
    
    public void setRangeGridlinePaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }
    
    public void setFixedLegendItems(final LegendItemCollection items) {
        this.fixedLegendItems = items;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = this.fixedLegendItems;
        if (result == null) {
            result = new LegendItemCollection();
            for (int count = this.datasets.size(), datasetIndex = 0; datasetIndex < count; ++datasetIndex) {
                final CategoryDataset dataset = this.getDataset(datasetIndex);
                if (dataset != null) {
                    final CategoryItemRenderer renderer = this.getRenderer(datasetIndex);
                    if (renderer != null) {
                        for (int seriesCount = dataset.getRowCount(), i = 0; i < seriesCount; ++i) {
                            final LegendItem item = renderer.getLegendItem(datasetIndex, i);
                            if (item != null) {
                                result.add(item);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public void handleClick(final int x, final int y, final PlotRenderingInfo info) {
        final Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            double java2D = 0.0;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                java2D = x;
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                java2D = y;
            }
            final RectangleEdge edge = Plot.resolveRangeAxisLocation(this.getRangeAxisLocation(), this.orientation);
            final double value = this.getRangeAxis().java2DToValue(java2D, info.getDataArea(), edge);
            this.setAnchorValue(value);
            this.setRangeCrosshairValue(value);
        }
    }
    
    public void zoom(final double percent) {
        if (percent > 0.0) {
            final double range = this.getRangeAxis().getRange().getLength();
            final double scaledRange = range * percent;
            this.getRangeAxis().setRange(this.anchorValue - scaledRange / 2.0, this.anchorValue + scaledRange / 2.0);
        }
        else {
            this.getRangeAxis().setAutoRange(true);
        }
    }
    
    public void datasetChanged(final DatasetChangeEvent event) {
        for (int count = this.rangeAxes.size(), axisIndex = 0; axisIndex < count; ++axisIndex) {
            final ValueAxis yAxis = this.getRangeAxis(axisIndex);
            if (yAxis != null) {
                yAxis.configure();
            }
        }
        if (this.getParent() != null) {
            this.getParent().datasetChanged(event);
        }
        else {
            final PlotChangeEvent e = new PlotChangeEvent(this);
            e.setType(ChartChangeEventType.DATASET_UPDATED);
            this.notifyListeners(e);
        }
    }
    
    public void rendererChanged(final RendererChangeEvent event) {
        final Plot parent = this.getParent();
        if (parent != null) {
            if (!(parent instanceof RendererChangeListener)) {
                throw new RuntimeException("The renderer has changed and I don't know what to do!");
            }
            final RendererChangeListener rcl = (RendererChangeListener)parent;
            rcl.rendererChanged(event);
        }
        else {
            this.configureRangeAxes();
            final PlotChangeEvent e = new PlotChangeEvent(this);
            this.notifyListeners(e);
        }
    }
    
    public void addDomainMarker(final CategoryMarker marker) {
        this.addDomainMarker(marker, Layer.FOREGROUND);
    }
    
    public void addDomainMarker(final CategoryMarker marker, final Layer layer) {
        this.addDomainMarker(0, marker, layer);
    }
    
    public void addDomainMarker(final int index, final CategoryMarker marker, final Layer layer) {
        if (layer == Layer.FOREGROUND) {
            Collection markers = this.foregroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.foregroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            Collection markers = this.backgroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.backgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void clearDomainMarkers() {
        if (this.backgroundDomainMarkers != null) {
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            this.foregroundDomainMarkers.clear();
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Collection getDomainMarkers(final Layer layer) {
        return this.getDomainMarkers(0, layer);
    }
    
    public Collection getDomainMarkers(final int index, final Layer layer) {
        Collection result = null;
        final Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundDomainMarkers.get(key);
        }
        else if (layer == Layer.BACKGROUND) {
            result = this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection((Collection<?>)result);
        }
        return result;
    }
    
    public void clearDomainMarkers(final int index) {
        final Integer key = new Integer(index);
        if (this.backgroundDomainMarkers != null) {
            final Collection markers = this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                markers.clear();
            }
        }
        if (this.foregroundDomainMarkers != null) {
            final Collection markers = this.foregroundDomainMarkers.get(key);
            if (markers != null) {
                markers.clear();
            }
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void addRangeMarker(final Marker marker) {
        this.addRangeMarker(marker, Layer.FOREGROUND);
    }
    
    public void addRangeMarker(final Marker marker, final Layer layer) {
        this.addRangeMarker(0, marker, layer);
    }
    
    public void addRangeMarker(final int index, final Marker marker, final Layer layer) {
        if (layer == Layer.FOREGROUND) {
            Collection markers = this.foregroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.foregroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            Collection markers = this.backgroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.backgroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void clearRangeMarkers() {
        if (this.backgroundRangeMarkers != null) {
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            this.foregroundRangeMarkers.clear();
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Collection getRangeMarkers(final Layer layer) {
        return this.getRangeMarkers(0, layer);
    }
    
    public Collection getRangeMarkers(final int index, final Layer layer) {
        Collection result = null;
        final Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundRangeMarkers.get(key);
        }
        else if (layer == Layer.BACKGROUND) {
            result = this.backgroundRangeMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection((Collection<?>)result);
        }
        return result;
    }
    
    public void clearRangeMarkers(final int index) {
        final Integer key = new Integer(index);
        if (this.backgroundRangeMarkers != null) {
            final Collection markers = this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            final Collection markers = this.foregroundRangeMarkers.get(key);
            if (markers != null) {
                markers.clear();
            }
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }
    
    public void setRangeCrosshairVisible(final boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }
    
    public void setRangeCrosshairLockedOnData(final boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }
    
    public void setRangeCrosshairValue(final double value) {
        this.setRangeCrosshairValue(value, true);
    }
    
    public void setRangeCrosshairValue(final double value, final boolean notify) {
        this.rangeCrosshairValue = value;
        if (this.isRangeCrosshairVisible() && notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }
    
    public void setRangeCrosshairStroke(final Stroke stroke) {
        this.rangeCrosshairStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }
    
    public void setRangeCrosshairPaint(final Paint paint) {
        this.rangeCrosshairPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public List getAnnotations() {
        return this.annotations;
    }
    
    public void addAnnotation(final CategoryAnnotation annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        this.annotations.add(annotation);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean removeAnnotation(final CategoryAnnotation annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        final boolean removed = this.annotations.remove(annotation);
        if (removed) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
        return removed;
    }
    
    public void clearAnnotations() {
        this.annotations.clear();
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    protected AxisSpace calculateDomainAxisSpace(final Graphics2D g2, final Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (this.fixedDomainAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
        }
        else {
            final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(this.getDomainAxisLocation(), this.orientation);
            if (this.drawSharedDomainAxis) {
                space = this.getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space);
            }
            for (int i = 0; i < this.domainAxes.size(); ++i) {
                final Axis xAxis = (Axis)this.domainAxes.get(i);
                if (xAxis != null) {
                    final RectangleEdge edge = this.getDomainAxisEdge(i);
                    space = xAxis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;
    }
    
    protected AxisSpace calculateRangeAxisSpace(final Graphics2D g2, final Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (this.fixedRangeAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
        }
        else {
            for (int i = 0; i < this.rangeAxes.size(); ++i) {
                final Axis yAxis = (Axis)this.rangeAxes.get(i);
                if (yAxis != null) {
                    final RectangleEdge edge = this.getRangeAxisEdge(i);
                    space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;
    }
    
    protected AxisSpace calculateAxisSpace(final Graphics2D g2, final Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = this.calculateRangeAxisSpace(g2, plotArea, space);
        space = this.calculateDomainAxisSpace(g2, plotArea, space);
        return space;
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor, final PlotState parentState, PlotRenderingInfo state) {
        final boolean b1 = area.getWidth() <= 10.0;
        final boolean b2 = area.getHeight() <= 10.0;
        if (b1 || b2) {
            return;
        }
        if (state == null) {
            state = new PlotRenderingInfo(null);
        }
        state.setPlotArea(area);
        final RectangleInsets insets = this.getInsets();
        insets.trim(area);
        final AxisSpace space = this.calculateAxisSpace(g2, area);
        final Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        if (state != null) {
            state.setDataArea(dataArea);
        }
        if (this.getRenderer() != null) {
            this.getRenderer().drawBackground(g2, this, dataArea);
        }
        else {
            this.drawBackground(g2, dataArea);
        }
        final Map axisStateMap = this.drawAxes(g2, area, dataArea, state);
        this.drawDomainGridlines(g2, dataArea);
        AxisState rangeAxisState = axisStateMap.get(this.getRangeAxis());
        if (rangeAxisState == null && parentState != null) {
            rangeAxisState = parentState.getSharedAxisStates().get(this.getRangeAxis());
        }
        if (rangeAxisState != null) {
            this.drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
        }
        for (int i = 0; i < this.renderers.size(); ++i) {
            this.drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        for (int i = 0; i < this.renderers.size(); ++i) {
            this.drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        boolean foundData = false;
        final Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        final Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        final DatasetRenderingOrder order = this.getDatasetRenderingOrder();
        if (order == DatasetRenderingOrder.FORWARD) {
            for (int j = 0; j < this.datasets.size(); ++j) {
                foundData = (this.render(g2, dataArea, j, state) || foundData);
            }
        }
        else {
            for (int j = this.datasets.size() - 1; j >= 0; --j) {
                foundData = (this.render(g2, dataArea, j, state) || foundData);
            }
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        if (!foundData) {
            this.drawNoDataMessage(g2, dataArea);
        }
        if (this.isRangeCrosshairVisible()) {
            this.drawRangeLine(g2, dataArea, this.getRangeCrosshairValue(), this.getRangeCrosshairStroke(), this.getRangeCrosshairPaint());
        }
        for (int j = 0; j < this.renderers.size(); ++j) {
            this.drawDomainMarkers(g2, dataArea, j, Layer.FOREGROUND);
        }
        for (int j = 0; j < this.renderers.size(); ++j) {
            this.drawRangeMarkers(g2, dataArea, j, Layer.FOREGROUND);
        }
        this.drawAnnotations(g2, dataArea);
        if (this.getRenderer() != null) {
            this.getRenderer().drawOutline(g2, this, dataArea);
        }
        else {
            this.drawOutline(g2, dataArea);
        }
    }
    
    protected Map drawAxes(final Graphics2D g2, final Rectangle2D plotArea, final Rectangle2D dataArea, final PlotRenderingInfo plotState) {
        final AxisCollection axisCollection = new AxisCollection();
        for (int index = 0; index < this.domainAxes.size(); ++index) {
            final CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(index);
            if (xAxis != null) {
                axisCollection.add(xAxis, this.getDomainAxisEdge(index));
            }
        }
        for (int index = 0; index < this.rangeAxes.size(); ++index) {
            final ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(index);
            if (yAxis != null) {
                axisCollection.add(yAxis, this.getRangeAxisEdge(index));
            }
        }
        final Map axisStateMap = new HashMap();
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        Iterator iterator = axisCollection.getAxesAtTop().iterator();
        while (iterator.hasNext()) {
            final Axis axis = iterator.next();
            if (axis != null) {
                final AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        iterator = axisCollection.getAxesAtBottom().iterator();
        while (iterator.hasNext()) {
            final Axis axis = iterator.next();
            if (axis != null) {
                final AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtLeft().iterator();
        while (iterator.hasNext()) {
            final Axis axis = iterator.next();
            if (axis != null) {
                final AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtRight().iterator();
        while (iterator.hasNext()) {
            final Axis axis = iterator.next();
            if (axis != null) {
                final AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        return axisStateMap;
    }
    
    public boolean render(final Graphics2D g2, final Rectangle2D dataArea, final int index, final PlotRenderingInfo info) {
        boolean foundData = false;
        final CategoryDataset currentDataset = this.getDataset(index);
        final CategoryItemRenderer renderer = this.getRenderer(index);
        final CategoryAxis domainAxis = this.getDomainAxisForDataset(index);
        final ValueAxis rangeAxis = this.getRangeAxisForDataset(index);
        final boolean hasData = !DatasetUtilities.isEmptyOrNull(currentDataset);
        if (hasData && renderer != null) {
            foundData = true;
            final CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
            final int columnCount = currentDataset.getColumnCount();
            final int rowCount = currentDataset.getRowCount();
            for (int passCount = renderer.getPassCount(), pass = 0; pass < passCount; ++pass) {
                if (this.columnRenderingOrder == SortOrder.ASCENDING) {
                    for (int column = 0; column < columnCount; ++column) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; ++row) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                        else {
                            for (int row = rowCount - 1; row >= 0; --row) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                }
                else {
                    for (int column = columnCount - 1; column >= 0; --column) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; ++row) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                        else {
                            for (int row = rowCount - 1; row >= 0; --row) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;
    }
    
    protected void drawDomainGridlines(final Graphics2D g2, final Rectangle2D dataArea) {
        if (this.isDomainGridlinesVisible()) {
            final CategoryAnchor anchor = this.getDomainGridlinePosition();
            final RectangleEdge domainAxisEdge = this.getDomainAxisEdge();
            final Stroke gridStroke = this.getDomainGridlineStroke();
            final Paint gridPaint = this.getDomainGridlinePaint();
            if (gridStroke != null && gridPaint != null) {
                final CategoryDataset data = this.getDataset();
                if (data != null) {
                    final CategoryAxis axis = this.getDomainAxis();
                    if (axis != null) {
                        for (int columnCount = data.getColumnCount(), c = 0; c < columnCount; ++c) {
                            final double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
                            final CategoryItemRenderer renderer1 = this.getRenderer();
                            if (renderer1 != null) {
                                renderer1.drawDomainGridline(g2, this, dataArea, xx);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void drawRangeGridlines(final Graphics2D g2, final Rectangle2D dataArea, final List ticks) {
        if (this.isRangeGridlinesVisible()) {
            final Stroke gridStroke = this.getRangeGridlineStroke();
            final Paint gridPaint = this.getRangeGridlinePaint();
            if (gridStroke != null && gridPaint != null) {
                final ValueAxis axis = this.getRangeAxis();
                if (axis != null) {
                    final Iterator iterator = ticks.iterator();
                    while (iterator.hasNext()) {
                        final ValueTick tick = iterator.next();
                        final CategoryItemRenderer renderer1 = this.getRenderer();
                        if (renderer1 != null) {
                            renderer1.drawRangeGridline(g2, this, this.getRangeAxis(), dataArea, tick.getValue());
                        }
                    }
                }
            }
        }
    }
    
    protected void drawAnnotations(final Graphics2D g2, final Rectangle2D dataArea) {
        if (this.getAnnotations() != null) {
            final Iterator iterator = this.getAnnotations().iterator();
            while (iterator.hasNext()) {
                final CategoryAnnotation annotation = iterator.next();
                annotation.draw(g2, this, dataArea, this.getDomainAxis(), this.getRangeAxis());
            }
        }
    }
    
    protected void drawDomainMarkers(final Graphics2D g2, final Rectangle2D dataArea, final int index, final Layer layer) {
        final CategoryItemRenderer r = this.getRenderer(index);
        if (r == null) {
            return;
        }
        final Collection markers = this.getDomainMarkers(index, layer);
        final CategoryAxis axis = this.getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            final Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                final CategoryMarker marker = iterator.next();
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }
    
    protected void drawRangeMarkers(final Graphics2D g2, final Rectangle2D dataArea, final int index, final Layer layer) {
        final CategoryItemRenderer r = this.getRenderer(index);
        if (r == null) {
            return;
        }
        final Collection markers = this.getRangeMarkers(index, layer);
        final ValueAxis axis = this.getRangeAxisForDataset(index);
        if (markers != null && axis != null) {
            final Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                final Marker marker = iterator.next();
                r.drawRangeMarker(g2, this, axis, marker, dataArea);
            }
        }
    }
    
    protected void drawRangeLine(final Graphics2D g2, final Rectangle2D dataArea, final double value, final Stroke stroke, final Paint paint) {
        final double java2D = this.getRangeAxis().valueToJava2D(value, dataArea, this.getRangeAxisEdge());
        Line2D line = null;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        }
        else if (this.orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }
    
    public Range getDataRange(final ValueAxis axis) {
        Range result = null;
        final List mappedDatasets = new ArrayList();
        final int rangeIndex = this.rangeAxes.indexOf((Object)axis);
        if (rangeIndex >= 0) {
            mappedDatasets.addAll(this.datasetsMappedToRangeAxis(rangeIndex));
        }
        else if (axis == this.getRangeAxis()) {
            mappedDatasets.addAll(this.datasetsMappedToRangeAxis(0));
        }
        final Iterator iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            final CategoryDataset d = iterator.next();
            final CategoryItemRenderer r = this.getRendererForDataset(d);
            if (r != null) {
                result = Range.combine(result, r.findRangeBounds(d));
            }
        }
        return result;
    }
    
    private List datasetsMappedToRangeAxis(final int index) {
        final List result = new ArrayList();
        for (int i = 0; i < this.datasets.size(); ++i) {
            final Object dataset = this.datasets.get(i);
            if (dataset != null) {
                final Integer m = (Integer)this.datasetToRangeAxisMap.get(i);
                if (m == null) {
                    if (index == 0) {
                        result.add(dataset);
                    }
                }
                else if (m == index) {
                    result.add(dataset);
                }
            }
        }
        return result;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public void setWeight(final int weight) {
        this.weight = weight;
    }
    
    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }
    
    public void setFixedDomainAxisSpace(final AxisSpace space) {
        this.fixedDomainAxisSpace = space;
    }
    
    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }
    
    public void setFixedRangeAxisSpace(final AxisSpace space) {
        this.fixedRangeAxisSpace = space;
    }
    
    public List getCategories() {
        List result = null;
        if (this.getDataset() != null) {
            result = Collections.unmodifiableList((List<?>)this.getDataset().getColumnKeys());
        }
        return result;
    }
    
    public boolean getDrawSharedDomainAxis() {
        return this.drawSharedDomainAxis;
    }
    
    public void setDrawSharedDomainAxis(final boolean draw) {
        this.drawSharedDomainAxis = draw;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isDomainZoomable() {
        return false;
    }
    
    public boolean isRangeZoomable() {
        return true;
    }
    
    public void zoomDomainAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomDomainAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomRangeAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
            if (rangeAxis != null) {
                rangeAxis.resizeRange(factor);
            }
        }
    }
    
    public void zoomRangeAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
            if (rangeAxis != null) {
                rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }
    
    public double getAnchorValue() {
        return this.anchorValue;
    }
    
    public void setAnchorValue(final double value) {
        this.setAnchorValue(value, true);
    }
    
    public void setAnchorValue(final double value, final boolean notify) {
        this.anchorValue = value;
        if (notify) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CategoryPlot that = (CategoryPlot)obj;
        return this.orientation == that.orientation && ObjectUtilities.equal((Object)this.axisOffset, (Object)that.axisOffset) && this.domainAxes.equals((Object)that.domainAxes) && this.domainAxisLocations.equals((Object)that.domainAxisLocations) && this.drawSharedDomainAxis == that.drawSharedDomainAxis && this.rangeAxes.equals((Object)that.rangeAxes) && this.rangeAxisLocations.equals((Object)that.rangeAxisLocations) && ObjectUtilities.equal((Object)this.datasetToDomainAxisMap, (Object)that.datasetToDomainAxisMap) && ObjectUtilities.equal((Object)this.datasetToRangeAxisMap, (Object)that.datasetToRangeAxisMap) && ObjectUtilities.equal((Object)this.renderers, (Object)that.renderers) && this.renderingOrder == that.renderingOrder && this.columnRenderingOrder == that.columnRenderingOrder && this.rowRenderingOrder == that.rowRenderingOrder && this.domainGridlinesVisible == that.domainGridlinesVisible && this.domainGridlinePosition == that.domainGridlinePosition && ObjectUtilities.equal((Object)this.domainGridlineStroke, (Object)that.domainGridlineStroke) && PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint) && this.rangeGridlinesVisible == that.rangeGridlinesVisible && ObjectUtilities.equal((Object)this.rangeGridlineStroke, (Object)that.rangeGridlineStroke) && PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint) && this.anchorValue == that.anchorValue && this.rangeCrosshairVisible == that.rangeCrosshairVisible && this.rangeCrosshairValue == that.rangeCrosshairValue && ObjectUtilities.equal((Object)this.rangeCrosshairStroke, (Object)that.rangeCrosshairStroke) && PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint) && this.rangeCrosshairLockedOnData == that.rangeCrosshairLockedOnData && ObjectUtilities.equal((Object)this.foregroundRangeMarkers, (Object)that.foregroundRangeMarkers) && ObjectUtilities.equal((Object)this.backgroundRangeMarkers, (Object)that.backgroundRangeMarkers) && ObjectUtilities.equal((Object)this.annotations, (Object)that.annotations) && this.weight == that.weight && ObjectUtilities.equal((Object)this.fixedDomainAxisSpace, (Object)that.fixedDomainAxisSpace) && ObjectUtilities.equal((Object)this.fixedRangeAxisSpace, (Object)that.fixedRangeAxisSpace);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final CategoryPlot clone = (CategoryPlot)super.clone();
        clone.domainAxes = new ObjectList();
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            final CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
            if (xAxis != null) {
                final CategoryAxis clonedAxis = (CategoryAxis)xAxis.clone();
                clone.setDomainAxis(i, clonedAxis);
            }
        }
        clone.domainAxisLocations = (ObjectList)this.domainAxisLocations.clone();
        clone.rangeAxes = new ObjectList();
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(i);
            if (yAxis != null) {
                final ValueAxis clonedAxis2 = (ValueAxis)yAxis.clone();
                clone.setRangeAxis(i, clonedAxis2);
            }
        }
        clone.rangeAxisLocations = (ObjectList)this.rangeAxisLocations.clone();
        clone.datasets = (ObjectList)this.datasets.clone();
        for (int i = 0; i < clone.datasets.size(); ++i) {
            final CategoryDataset dataset = clone.getDataset(i);
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxisMap = (ObjectList)this.datasetToDomainAxisMap.clone();
        clone.datasetToRangeAxisMap = (ObjectList)this.datasetToRangeAxisMap.clone();
        clone.renderers = (ObjectList)this.renderers.clone();
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = (AxisSpace)ObjectUtilities.clone((Object)this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = (AxisSpace)ObjectUtilities.clone((Object)this.fixedRangeAxisSpace);
        }
        return clone;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        for (int i = 0; i < this.domainAxes.size(); ++i) {
            final CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
            if (xAxis != null) {
                xAxis.setPlot(this);
                xAxis.addChangeListener(this);
            }
        }
        for (int i = 0; i < this.rangeAxes.size(); ++i) {
            final ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(i);
            if (yAxis != null) {
                yAxis.setPlot(this);
                yAxis.addChangeListener(this);
            }
        }
        for (int datasetCount = this.datasets.size(), j = 0; j < datasetCount; ++j) {
            final Dataset dataset = (Dataset)this.datasets.get(j);
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (int rendererCount = this.renderers.size(), k = 0; k < rendererCount; ++k) {
            final CategoryItemRenderer renderer = (CategoryItemRenderer)this.renderers.get(k);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }
    
    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, 0, 2, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.lightGray;
        DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
        CategoryPlot.localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }
}
