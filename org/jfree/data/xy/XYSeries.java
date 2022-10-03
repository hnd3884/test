package org.jfree.data.xy;

import org.jfree.util.ObjectUtilities;
import org.jfree.data.general.SeriesException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.data.general.Series;

public class XYSeries extends Series implements Cloneable, Serializable
{
    static final long serialVersionUID = -5908509288197150436L;
    protected List data;
    private int maximumItemCount;
    private boolean autoSort;
    private boolean allowDuplicateXValues;
    
    public XYSeries(final Comparable key) {
        this(key, true, true);
    }
    
    public XYSeries(final Comparable key, final boolean autoSort) {
        this(key, autoSort, true);
    }
    
    public XYSeries(final Comparable key, final boolean autoSort, final boolean allowDuplicateXValues) {
        super(key);
        this.maximumItemCount = Integer.MAX_VALUE;
        this.data = new ArrayList();
        this.autoSort = autoSort;
        this.allowDuplicateXValues = allowDuplicateXValues;
    }
    
    public boolean getAutoSort() {
        return this.autoSort;
    }
    
    public boolean getAllowDuplicateXValues() {
        return this.allowDuplicateXValues;
    }
    
    public int getItemCount() {
        return this.data.size();
    }
    
    public List getItems() {
        return Collections.unmodifiableList((List<?>)this.data);
    }
    
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }
    
    public void setMaximumItemCount(final int maximum) {
        this.maximumItemCount = maximum;
        boolean dataRemoved = false;
        while (this.data.size() > maximum) {
            this.data.remove(0);
            dataRemoved = true;
        }
        if (dataRemoved) {
            this.fireSeriesChanged();
        }
    }
    
    public void add(final XYDataItem item) {
        this.add(item, true);
    }
    
    public void add(final double x, final double y) {
        this.add(new Double(x), new Double(y), true);
    }
    
    public void add(final double x, final double y, final boolean notify) {
        this.add(new Double(x), new Double(y), notify);
    }
    
    public void add(final double x, final Number y) {
        this.add(new Double(x), y);
    }
    
    public void add(final double x, final Number y, final boolean notify) {
        this.add(new Double(x), y, notify);
    }
    
    public void add(final Number x, final Number y) {
        this.add(x, y, true);
    }
    
    public void add(final Number x, final Number y, final boolean notify) {
        final XYDataItem item = new XYDataItem(x, y);
        this.add(item, notify);
    }
    
    public void add(final XYDataItem item, final boolean notify) {
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        if (this.autoSort) {
            int index = Collections.binarySearch(this.data, item);
            if (index < 0) {
                this.data.add(-index - 1, item);
            }
            else {
                if (!this.allowDuplicateXValues) {
                    throw new SeriesException("X-value already exists.");
                }
                for (int size = this.data.size(); index < size && item.compareTo(this.data.get(index)) == 0; ++index) {}
                if (index < this.data.size()) {
                    this.data.add(index, item);
                }
                else {
                    this.data.add(item);
                }
            }
        }
        else {
            if (!this.allowDuplicateXValues) {
                final int index = this.indexOf(item.getX());
                if (index >= 0) {
                    throw new SeriesException("X-value already exists.");
                }
            }
            this.data.add(item);
        }
        if (this.getItemCount() > this.maximumItemCount) {
            this.data.remove(0);
        }
        if (notify) {
            this.fireSeriesChanged();
        }
    }
    
    public void delete(final int start, final int end) {
        for (int i = start; i <= end; ++i) {
            this.data.remove(start);
        }
        this.fireSeriesChanged();
    }
    
    public XYDataItem remove(final int index) {
        final XYDataItem result = this.data.remove(index);
        this.fireSeriesChanged();
        return result;
    }
    
    public XYDataItem remove(final Number x) {
        return this.remove(this.indexOf(x));
    }
    
    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            this.fireSeriesChanged();
        }
    }
    
    public XYDataItem getDataItem(final int index) {
        return this.data.get(index);
    }
    
    public Number getX(final int index) {
        return this.getDataItem(index).getX();
    }
    
    public Number getY(final int index) {
        return this.getDataItem(index).getY();
    }
    
    public void update(final int index, final Number y) {
        final XYDataItem item = this.getDataItem(index);
        item.setY(y);
        this.fireSeriesChanged();
    }
    
    public void updateByIndex(final int index, final Number y) {
        this.update(index, y);
    }
    
    public void update(final Number x, final Number y) {
        final int index = this.indexOf(x);
        if (index < 0) {
            throw new SeriesException("No observation for x = " + x);
        }
        final XYDataItem item = this.getDataItem(index);
        item.setY(y);
        this.fireSeriesChanged();
    }
    
    public XYDataItem addOrUpdate(final Number x, final Number y) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        XYDataItem overwritten = null;
        final int index = this.indexOf(x);
        if (index >= 0) {
            final XYDataItem existing = this.data.get(index);
            try {
                overwritten = (XYDataItem)existing.clone();
            }
            catch (final CloneNotSupportedException e) {
                throw new SeriesException("Couldn't clone XYDataItem!");
            }
            existing.setY(y);
        }
        else {
            if (this.autoSort) {
                this.data.add(-index - 1, new XYDataItem(x, y));
            }
            else {
                this.data.add(new XYDataItem(x, y));
            }
            if (this.getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
        }
        this.fireSeriesChanged();
        return overwritten;
    }
    
    public int indexOf(final Number x) {
        if (this.autoSort) {
            return Collections.binarySearch(this.data, new XYDataItem(x, null));
        }
        for (int i = 0; i < this.data.size(); ++i) {
            final XYDataItem item = this.data.get(i);
            if (item.getX().equals(x)) {
                return i;
            }
        }
        return -1;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Object clone = this.createCopy(0, this.getItemCount() - 1);
        return clone;
    }
    
    public XYSeries createCopy(final int start, final int end) throws CloneNotSupportedException {
        final XYSeries copy = (XYSeries)super.clone();
        copy.data = new ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; ++index) {
                final XYDataItem item = this.data.get(index);
                final XYDataItem clone = (XYDataItem)item.clone();
                try {
                    copy.add(clone);
                }
                catch (final SeriesException e) {
                    System.err.println("Unable to add cloned data item.");
                }
            }
        }
        return copy;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final XYSeries that = (XYSeries)obj;
        return this.maximumItemCount == that.maximumItemCount && this.autoSort == that.autoSort && this.allowDuplicateXValues == that.allowDuplicateXValues && ObjectUtilities.equal((Object)this.data, (Object)that.data);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + ((this.data != null) ? this.data.hashCode() : 0);
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (this.autoSort ? 1 : 0);
        result = 29 * result + (this.allowDuplicateXValues ? 1 : 0);
        return result;
    }
}
