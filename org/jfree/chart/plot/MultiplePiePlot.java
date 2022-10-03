package org.jfree.chart.plot;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.data.category.CategoryToPieDataset;
import java.awt.Rectangle;
import org.jfree.data.general.DatasetUtilities;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import java.util.HashMap;
import java.awt.Color;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.title.TextTitle;
import java.awt.Font;
import org.jfree.data.general.PieDataset;
import java.util.Map;
import java.awt.Paint;
import org.jfree.util.TableOrder;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.JFreeChart;
import java.io.Serializable;

public class MultiplePiePlot extends Plot implements Cloneable, Serializable
{
    private static final long serialVersionUID = -355377800470807389L;
    private JFreeChart pieChart;
    private CategoryDataset dataset;
    private TableOrder dataExtractOrder;
    private double limit;
    private Comparable aggregatedItemsKey;
    private transient Paint aggregatedItemsPaint;
    private transient Map sectionPaints;
    
    public MultiplePiePlot() {
        this(null);
    }
    
    public MultiplePiePlot(final CategoryDataset dataset) {
        this.limit = 0.0;
        this.dataset = dataset;
        final PiePlot piePlot = new PiePlot(null);
        (this.pieChart = new JFreeChart(piePlot)).removeLegend();
        this.dataExtractOrder = TableOrder.BY_COLUMN;
        this.pieChart.setBackgroundPaint(null);
        final TextTitle seriesTitle = new TextTitle("Series Title", new Font("SansSerif", 1, 12));
        seriesTitle.setPosition(RectangleEdge.BOTTOM);
        this.pieChart.setTitle(seriesTitle);
        this.aggregatedItemsKey = "Other";
        this.aggregatedItemsPaint = Color.lightGray;
        this.sectionPaints = new HashMap();
    }
    
    public CategoryDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final CategoryDataset dataset) {
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        if ((this.dataset = dataset) != null) {
            this.setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        this.datasetChanged(new DatasetChangeEvent(this, dataset));
    }
    
    public JFreeChart getPieChart() {
        return this.pieChart;
    }
    
    public void setPieChart(final JFreeChart pieChart) {
        this.pieChart = pieChart;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }
    
    public void setDataExtractOrder(final TableOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument");
        }
        this.dataExtractOrder = order;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getLimit() {
        return this.limit;
    }
    
    public void setLimit(final double limit) {
        this.limit = limit;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Comparable getAggregatedItemsKey() {
        return this.aggregatedItemsKey;
    }
    
    public void setAggregatedItemsKey(final Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.aggregatedItemsKey = key;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getAggregatedItemsPaint() {
        return this.aggregatedItemsPaint;
    }
    
    public void setAggregatedItemsPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.aggregatedItemsPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public String getPlotType() {
        return "Multiple Pie Plot";
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor, final PlotState parentState, final PlotRenderingInfo info) {
        final RectangleInsets insets = this.getInsets();
        insets.trim(area);
        this.drawBackground(g2, area);
        this.drawOutline(g2, area);
        if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
            this.drawNoDataMessage(g2, area);
            return;
        }
        int pieCount = 0;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            pieCount = this.dataset.getRowCount();
        }
        else {
            pieCount = this.dataset.getColumnCount();
        }
        int displayCols = (int)Math.ceil(Math.sqrt(pieCount));
        int displayRows = (int)Math.ceil(pieCount / (double)displayCols);
        if (displayCols > displayRows && area.getWidth() < area.getHeight()) {
            final int temp = displayCols;
            displayCols = displayRows;
            displayRows = temp;
        }
        this.prefetchSectionPaints();
        final int x = (int)area.getX();
        final int y = (int)area.getY();
        final int width = (int)area.getWidth() / displayCols;
        final int height = (int)area.getHeight() / displayRows;
        int row = 0;
        int column = 0;
        final int diff = displayRows * displayCols - pieCount;
        int xoffset = 0;
        final Rectangle rect = new Rectangle();
        for (int pieIndex = 0; pieIndex < pieCount; ++pieIndex) {
            rect.setBounds(x + xoffset + width * column, y + height * row, width, height);
            String title = null;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                title = this.dataset.getRowKey(pieIndex).toString();
            }
            else {
                title = this.dataset.getColumnKey(pieIndex).toString();
            }
            this.pieChart.setTitle(title);
            PieDataset piedataset = null;
            final PieDataset dd = new CategoryToPieDataset(this.dataset, this.dataExtractOrder, pieIndex);
            if (this.limit > 0.0) {
                piedataset = DatasetUtilities.createConsolidatedPieDataset(dd, this.aggregatedItemsKey, this.limit);
            }
            else {
                piedataset = dd;
            }
            final PiePlot piePlot = (PiePlot)this.pieChart.getPlot();
            piePlot.setDataset(piedataset);
            piePlot.setPieIndex(pieIndex);
            for (int i = 0; i < piedataset.getItemCount(); ++i) {
                final Comparable key = piedataset.getKey(i);
                Paint p;
                if (key.equals(this.aggregatedItemsKey)) {
                    p = this.aggregatedItemsPaint;
                }
                else {
                    p = this.sectionPaints.get(key);
                }
                piePlot.setSectionPaint(i, p);
            }
            ChartRenderingInfo subinfo = null;
            if (info != null) {
                subinfo = new ChartRenderingInfo();
            }
            this.pieChart.draw(g2, rect, subinfo);
            if (info != null) {
                info.getOwner().getEntityCollection().addAll(subinfo.getEntityCollection());
                info.addSubplotInfo(subinfo.getPlotInfo());
            }
            if (++column == displayCols) {
                column = 0;
                if (++row == displayRows - 1 && diff != 0) {
                    xoffset = diff * width / 2;
                }
            }
        }
    }
    
    private void prefetchSectionPaints() {
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            for (int c = 0; c < this.dataset.getColumnCount(); ++c) {
                final Comparable key = this.dataset.getColumnKey(c);
                final Paint p = this.sectionPaints.get(key);
                if (p == null) {
                    this.sectionPaints.put(key, this.getDrawingSupplier().getNextPaint());
                }
            }
        }
        else {
            for (int r = 0; r < this.dataset.getRowCount(); ++r) {
                final Comparable key = this.dataset.getRowKey(r);
                final Paint p = this.sectionPaints.get(key);
                if (p == null) {
                    this.sectionPaints.put(key, this.getDrawingSupplier().getNextPaint());
                }
            }
        }
    }
    
    public LegendItemCollection getLegendItems() {
        final LegendItemCollection result = new LegendItemCollection();
        if (this.dataset != null) {
            List keys = null;
            this.prefetchSectionPaints();
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                keys = this.dataset.getColumnKeys();
            }
            else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
                keys = this.dataset.getRowKeys();
            }
            if (keys != null) {
                int section = 0;
                final Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    final Comparable key = iterator.next();
                    final String description;
                    final String label = description = key.toString();
                    final Paint paint = this.sectionPaints.get(key);
                    final LegendItem item = new LegendItem(label, description, null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, paint, Plot.DEFAULT_OUTLINE_STROKE, paint);
                    result.add(item);
                    ++section;
                }
            }
            if (this.limit > 0.0) {
                result.add(new LegendItem(this.aggregatedItemsKey.toString(), this.aggregatedItemsKey.toString(), null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, this.aggregatedItemsPaint, Plot.DEFAULT_OUTLINE_STROKE, this.aggregatedItemsPaint));
            }
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MultiplePiePlot)) {
            return false;
        }
        final MultiplePiePlot that = (MultiplePiePlot)obj;
        return this.dataExtractOrder == that.dataExtractOrder && this.limit == that.limit && this.aggregatedItemsKey.equals(that.aggregatedItemsKey) && PaintUtilities.equal(this.aggregatedItemsPaint, that.aggregatedItemsPaint) && ObjectUtilities.equal((Object)this.pieChart, (Object)that.pieChart) && super.equals(obj);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.aggregatedItemsPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.aggregatedItemsPaint = SerialUtilities.readPaint(stream);
        this.sectionPaints = new HashMap();
    }
}
