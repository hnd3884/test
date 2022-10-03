package com.adventnet.client.components.chart.table;

import org.jfree.chart.ChartColor;
import com.adventnet.persistence.DataAccessException;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.util.Iterator;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import com.adventnet.client.components.chart.web.ChartConstants;
import com.adventnet.client.components.chart.util.ChartUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.logging.Level;
import java.awt.Paint;
import java.util.List;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import org.jfree.chart.plot.DefaultDrawingSupplier;

public class ChartDrawingSupplier extends DefaultDrawingSupplier
{
    private Logger logger;
    private DataObject prop;
    private ViewContext vc;
    private HashMap cValues;
    private int paintCounter;
    private int cLength;
    private MCKDataset mt;
    private List availableColorArray;
    private static final Paint[] CHART_PAINT_SEQUENCE;
    
    public ChartDrawingSupplier(final ViewContext vc, final MCKDataset mt, final String graphType) throws DataAccessException {
        super(ChartDrawingSupplier.CHART_PAINT_SEQUENCE, ChartDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, ChartDrawingSupplier.DEFAULT_STROKE_SEQUENCE, ChartDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, ChartDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        this.logger = Logger.getLogger(ChartDrawingSupplier.class.getName());
        this.prop = null;
        this.vc = null;
        this.cValues = null;
        this.paintCounter = 0;
        this.cLength = -1;
        this.mt = null;
        this.availableColorArray = null;
        this.vc = vc;
        this.mt = mt;
        this.logger.log(Level.FINE, "XCATEGORIES graphType {0}", graphType);
        final int graphTypeHCode = graphType.hashCode();
        final DataObject data = vc.getModel().getViewConfiguration();
        this.availableColorArray = new ArrayList();
        if (data.size("YSeriesColumn") > 0) {
            this.cValues = new HashMap();
            final Iterator iterator = data.getRows("YSeriesColumn");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String strClr = (String)row.get("COLORINHEX");
                if (strClr != null) {
                    final String seriesName = (String)row.get("DATACOLUMN");
                    final Color clrValue = ChartUtil.getColorFromHex(strClr.trim());
                    this.cValues.put(seriesName, clrValue);
                }
            }
            this.cLength = this.cValues.size();
        }
        else {
            final Object viewName = data.getFirstValue("ChartViewConfig", "VIEWNAME");
            final Object type = "COLOR";
            Row pkRow = new Row("AxisColumn");
            pkRow.set("VIEWNAME", viewName);
            pkRow.set("COLUMNTYPE", type);
            final Row tcRow = data.getRow("AxisColumn", pkRow);
            if (tcRow != null) {
                final GraphData graphData = (GraphData)vc.getTransientState("GRAPHCCDATA");
                pkRow = new Row("AxisColumn");
                pkRow.set("VIEWNAME", viewName);
                pkRow.set("COLUMNTYPE", (Object)"SERIES");
                Row seriesRow = data.getRow("AxisColumn", pkRow);
                this.logger.log(Level.FINE, "seriesRow {0}", seriesRow);
                Row dataRow = null;
                if (seriesRow == null && (graphTypeHCode == ChartConstants.PIE || graphTypeHCode == ChartConstants.PIE3D)) {
                    pkRow = new Row("AxisColumn");
                    pkRow.set("VIEWNAME", viewName);
                    pkRow.set("COLUMNTYPE", (Object)"XAXIS");
                    seriesRow = data.getRow("AxisColumn", pkRow);
                    this.logger.log(Level.FINE, "XCATEGORIES seriesRow {0}", seriesRow);
                    dataRow = new Row("AxisColumn");
                    dataRow.set("VIEWNAME", viewName);
                    dataRow.set("COLUMNTYPE", (Object)"YAXIS");
                    dataRow = data.getRow("AxisColumn", dataRow);
                }
                if (seriesRow != null) {
                    final TableModel tm = graphData.getData();
                    final String colCol = (String)tcRow.get("DATACOLUMN");
                    final int colIndex = FilterUtil.getFirstColumnIndex(tm, colCol);
                    final String seriesCol = (String)seriesRow.get("DATACOLUMN");
                    final int seriesIdx = FilterUtil.getFirstColumnIndex(tm, seriesCol);
                    if (colIndex <= -1 || seriesIdx <= -1) {
                        throw new RuntimeException("Unknown column " + colCol + " is specified for color column");
                    }
                    final int rowCount = tm.getRowCount();
                    final HashMap values = new HashMap();
                    for (int i = 0; i < rowCount; ++i) {
                        final Object seriesName2 = tm.getValueAt(i, seriesIdx);
                        Number dataVal = 1;
                        if (dataRow != null) {
                            final int dataColIndex = FilterUtil.getFirstColumnIndex(tm, (String)dataRow.get("DATACOLUMN"));
                            dataVal = (Number)tm.getValueAt(i, dataColIndex);
                        }
                        if (seriesName2 != null && !values.containsKey(seriesName2)) {
                            final Object strValue = tm.getValueAt(i, colIndex);
                            if (strValue == null) {
                                throw new RuntimeException("Color value cannot be null");
                            }
                            final Color clrValue2 = ChartUtil.getColorFromHex(strValue.toString().trim());
                            values.put(seriesName2, clrValue2);
                            if (dataRow != null && dataVal != null && dataVal.doubleValue() > 0.0) {
                                this.availableColorArray.add(clrValue2);
                            }
                        }
                    }
                    this.cValues = values;
                    this.cLength = ((this.availableColorArray.size() > 0) ? this.availableColorArray.size() : this.cValues.size());
                }
            }
        }
        this.logger.log(Level.FINE, "color values {0}", this.cValues);
        this.logger.log(Level.FINE, "cLength {0}", this.cLength);
    }
    
    public Paint getNextPaint() {
        if (this.cLength > -1) {
            final int index = this.paintCounter % this.cLength;
            ++this.paintCounter;
            final String seriesName = this.mt.getSeriesOrPieName(index);
            Color color = null;
            if (this.availableColorArray.size() > 0) {
                color = this.availableColorArray.get(index);
            }
            else {
                color = this.cValues.get(seriesName);
            }
            this.logger.log(Level.FINE, "index  {0} seriesName {1} color {2}", new Object[] { index + "", seriesName, color });
            if (color != null) {
                return color;
            }
        }
        return super.getNextPaint();
    }
    
    public static Paint[] createDefaultColorArray() {
        return new Paint[] { Color.blue, Color.green, Color.yellow, Color.orange, Color.magenta, Color.cyan, Color.pink, Color.gray, ChartColor.DARK_BLUE, ChartColor.DARK_GREEN, ChartColor.DARK_YELLOW, ChartColor.DARK_MAGENTA, ChartColor.DARK_CYAN, Color.darkGray, ChartColor.LIGHT_RED, ChartColor.LIGHT_BLUE, ChartColor.LIGHT_GREEN, ChartColor.LIGHT_YELLOW, ChartColor.LIGHT_MAGENTA, ChartColor.LIGHT_CYAN, Color.lightGray, ChartColor.VERY_DARK_RED, ChartColor.VERY_DARK_BLUE, ChartColor.VERY_DARK_GREEN, ChartColor.VERY_DARK_YELLOW, ChartColor.VERY_DARK_MAGENTA, ChartColor.VERY_DARK_CYAN, ChartColor.VERY_LIGHT_RED, ChartColor.VERY_LIGHT_BLUE, ChartColor.VERY_LIGHT_GREEN, ChartColor.VERY_LIGHT_YELLOW, ChartColor.VERY_LIGHT_MAGENTA, ChartColor.VERY_LIGHT_CYAN };
    }
    
    static {
        CHART_PAINT_SEQUENCE = createDefaultColorArray();
    }
}
