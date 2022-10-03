package com.adventnet.client.components.chart.table;

import java.util.Date;
import org.jfree.data.DomainOrder;
import com.adventnet.client.components.chart.util.ChartUtil;
import javax.swing.table.TableModel;
import org.jfree.data.xy.XYZDataset;
import org.jfree.data.xy.XisSymbolic;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.AbstractXYDataset;

public class CustomXYDataset extends AbstractXYDataset implements XYDataset, TableXYDataset, XisSymbolic, XYZDataset, MCKDataset
{
    private TableModel model;
    private boolean isXSymbolic;
    private boolean isZ;
    private int xIndex;
    private int zIndex;
    private int[] seriesIDX;
    
    public CustomXYDataset(final TableModel model, final int[] seriesIDX, final int xIndex, final int zIndex, final boolean isXSymbolic) {
        this.model = null;
        this.isXSymbolic = false;
        this.isZ = true;
        this.xIndex = -1;
        this.zIndex = -1;
        this.seriesIDX = null;
        this.isXSymbolic = isXSymbolic;
        this.model = model;
        this.isZ = (zIndex > -1);
        this.xIndex = xIndex;
        this.zIndex = zIndex;
        this.seriesIDX = seriesIDX;
    }
    
    public CustomXYDataset(final TableModel model, final boolean isXSymbolic, final int xIndex, final int zIndex) {
        this.model = null;
        this.isXSymbolic = false;
        this.isZ = true;
        this.xIndex = -1;
        this.zIndex = -1;
        this.seriesIDX = null;
        this.isXSymbolic = isXSymbolic;
        this.model = model;
        this.isZ = (zIndex > -1);
        this.xIndex = xIndex;
        this.zIndex = zIndex;
        int counter = 0;
        if (zIndex > -1) {
            ++counter;
        }
        if (xIndex > -1) {
            ++counter;
        }
        final int cc = model.getColumnCount();
        final int scc = cc - counter;
        if (scc > 0) {
            this.seriesIDX = new int[scc];
            counter = -1;
            for (int i = 0; i < scc; ++i) {
                if ((++counter == xIndex || counter == zIndex) && (++counter == xIndex || counter == zIndex)) {
                    ++counter;
                }
                this.seriesIDX[i] = counter;
            }
        }
    }
    
    public int getSeriesCount() {
        return (this.seriesIDX != null) ? this.seriesIDX.length : 0;
    }
    
    public int indexOf(final Comparable seriesKey) {
        return ((ChartUtil.GraphPoint)seriesKey).getPosition();
    }
    
    public Comparable getSeriesKey(final int series) {
        return new ChartUtil.GraphPoint(this.model.getColumnName(this.seriesIDX[series]), series);
    }
    
    public String getSeriesName(final int series) {
        return this.model.getColumnName(this.seriesIDX[series]);
    }
    
    public int getItemCount(final int series) {
        return this.model.getRowCount();
    }
    
    public double getXValue(final int series, final int item) {
        double result = Double.NaN;
        final Number x = this.getX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }
        return result;
    }
    
    public double getYValue(final int series, final int item) {
        double result = Double.NaN;
        final Number y = this.getY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }
        return result;
    }
    
    public double getZValue(final int series, final int item) {
        double result = Double.NaN;
        final Number z = this.getZ(series, item);
        if (z != null) {
            result = z.doubleValue();
        }
        return result;
    }
    
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }
    
    public Number getX(final int series, final int item) {
        if (this.isXSymbolic) {
            return new Double(item);
        }
        return this.convertToNumber(this.model.getValueAt(item, this.xIndex));
    }
    
    public Number getY(final int series, final int item) {
        return this.convertToNumber(this.model.getValueAt(item, this.seriesIDX[series]));
    }
    
    public Number getZ(final int series, final int item) {
        return this.convertToNumber(this.model.getValueAt(item, this.zIndex));
    }
    
    public int getItemCount() {
        return this.model.getRowCount();
    }
    
    public String[] getXSymbolicValues() {
        final String[] sValues = new String[this.model.getRowCount()];
        for (int rc = this.model.getRowCount(), i = 0; i < rc; ++i) {
            sValues[i] = this.getXSymbol(i);
        }
        return sValues;
    }
    
    public String getXSymbolicValue(final int series, final int item) {
        return this.getXSymbol(item);
    }
    
    public String getXSymbolicValue(final Integer val) {
        return this.getXSymbol(val);
    }
    
    private String getXSymbol(final int pos) {
        final String prefix = "NULL";
        final Object value = this.model.getValueAt(pos, 0);
        String strValue = null;
        if (value != null) {
            strValue = value.toString().trim();
        }
        if (strValue == null || strValue.equals("")) {
            strValue = prefix + pos;
        }
        return strValue;
    }
    
    protected Number convertToNumber(final Object ob) {
        if (ob == null) {
            return null;
        }
        if (ob instanceof Number) {
            return (Number)ob;
        }
        if (ob instanceof Date) {
            return new Long(((Date)ob).getTime());
        }
        throw new RuntimeException("Illegal argument excpetion , it needs to be either instance of Number or java.util.Date : " + ob.getClass().getName());
    }
    
    public String getSeriesOrPieName(final int series) {
        return this.model.getColumnName(this.seriesIDX[series]);
    }
}
