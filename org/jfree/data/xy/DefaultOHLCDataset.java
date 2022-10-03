package org.jfree.data.xy;

import java.util.Arrays;
import java.util.Date;

public class DefaultOHLCDataset extends AbstractXYDataset implements OHLCDataset
{
    private Comparable key;
    private OHLCDataItem[] data;
    
    public DefaultOHLCDataset(final Comparable key, final OHLCDataItem[] data) {
        this.key = key;
        this.data = data;
    }
    
    public Comparable getSeriesKey(final int series) {
        return this.key;
    }
    
    public Number getX(final int series, final int item) {
        return new Long(this.data[item].getDate().getTime());
    }
    
    public Date getXDate(final int series, final int item) {
        return this.data[item].getDate();
    }
    
    public Number getY(final int series, final int item) {
        return this.getClose(series, item);
    }
    
    public Number getHigh(final int series, final int item) {
        return this.data[item].getHigh();
    }
    
    public double getHighValue(final int series, final int item) {
        double result = Double.NaN;
        final Number high = this.getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();
        }
        return result;
    }
    
    public Number getLow(final int series, final int item) {
        return this.data[item].getLow();
    }
    
    public double getLowValue(final int series, final int item) {
        double result = Double.NaN;
        final Number low = this.getLow(series, item);
        if (low != null) {
            result = low.doubleValue();
        }
        return result;
    }
    
    public Number getOpen(final int series, final int item) {
        return this.data[item].getOpen();
    }
    
    public double getOpenValue(final int series, final int item) {
        double result = Double.NaN;
        final Number open = this.getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();
        }
        return result;
    }
    
    public Number getClose(final int series, final int item) {
        return this.data[item].getClose();
    }
    
    public double getCloseValue(final int series, final int item) {
        double result = Double.NaN;
        final Number close = this.getClose(series, item);
        if (close != null) {
            result = close.doubleValue();
        }
        return result;
    }
    
    public Number getVolume(final int series, final int item) {
        return this.data[item].getVolume();
    }
    
    public double getVolumeValue(final int series, final int item) {
        double result = Double.NaN;
        final Number volume = this.getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();
        }
        return result;
    }
    
    public int getSeriesCount() {
        return 1;
    }
    
    public int getItemCount(final int series) {
        return this.data.length;
    }
    
    public void sortDataByDate() {
        Arrays.sort(this.data);
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultOHLCDataset)) {
            return false;
        }
        final DefaultOHLCDataset that = (DefaultOHLCDataset)obj;
        return this.key.equals(that.key) && Arrays.equals(this.data, that.data);
    }
}
