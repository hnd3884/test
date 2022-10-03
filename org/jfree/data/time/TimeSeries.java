package org.jfree.data.time;

import org.jfree.util.ObjectUtilities;
import org.jfree.data.general.SeriesException;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.data.general.Series;

public class TimeSeries extends Series implements Cloneable, Serializable
{
    private static final long serialVersionUID = -5032960206869675528L;
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
    private String domain;
    private String range;
    protected Class timePeriodClass;
    protected List data;
    private int maximumItemCount;
    private long maximumItemAge;
    
    public TimeSeries(final String name) {
        this(name, "Time", "Value", Day.class);
    }
    
    public TimeSeries(final String name, final Class timePeriodClass) {
        this(name, "Time", "Value", timePeriodClass);
    }
    
    public TimeSeries(final String name, final String domain, final String range, final Class timePeriodClass) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        this.data = new ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
    }
    
    public String getDomainDescription() {
        return this.domain;
    }
    
    public void setDomainDescription(final String description) {
        final String old = this.domain;
        this.firePropertyChange("Domain", old, this.domain = description);
    }
    
    public String getRangeDescription() {
        return this.range;
    }
    
    public void setRangeDescription(final String description) {
        final String old = this.range;
        this.firePropertyChange("Range", old, this.range = description);
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
        if (maximum < 0) {
            throw new IllegalArgumentException("Negative 'maximum' argument.");
        }
        this.maximumItemCount = maximum;
        final int count = this.data.size();
        if (count > maximum) {
            this.delete(0, count - maximum - 1);
        }
    }
    
    public long getMaximumItemAge() {
        return this.maximumItemAge;
    }
    
    public void setMaximumItemAge(final long periods) {
        if (periods < 0L) {
            throw new IllegalArgumentException("Negative 'periods' argument.");
        }
        this.maximumItemAge = periods;
        this.removeAgedItems(true);
    }
    
    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }
    
    public TimeSeriesDataItem getDataItem(final int index) {
        return this.data.get(index);
    }
    
    public TimeSeriesDataItem getDataItem(final RegularTimePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument");
        }
        final TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, -2.147483648E9);
        final int index = Collections.binarySearch(this.data, dummy);
        if (index >= 0) {
            return this.data.get(index);
        }
        return null;
    }
    
    public RegularTimePeriod getTimePeriod(final int index) {
        return this.getDataItem(index).getPeriod();
    }
    
    public RegularTimePeriod getNextTimePeriod() {
        final RegularTimePeriod last = this.getTimePeriod(this.getItemCount() - 1);
        return last.next();
    }
    
    public Collection getTimePeriods() {
        final Collection result = new ArrayList();
        for (int i = 0; i < this.getItemCount(); ++i) {
            result.add(this.getTimePeriod(i));
        }
        return result;
    }
    
    public Collection getTimePeriodsUniqueToOtherSeries(final TimeSeries series) {
        final Collection result = new ArrayList();
        for (int i = 0; i < series.getItemCount(); ++i) {
            final RegularTimePeriod period = series.getTimePeriod(i);
            final int index = this.getIndex(period);
            if (index < 0) {
                result.add(period);
            }
        }
        return result;
    }
    
    public int getIndex(final RegularTimePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        final TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, -2.147483648E9);
        return Collections.binarySearch(this.data, dummy);
    }
    
    public Number getValue(final int index) {
        return this.getDataItem(index).getValue();
    }
    
    public Number getValue(final RegularTimePeriod period) {
        final int index = this.getIndex(period);
        if (index >= 0) {
            return this.getValue(index);
        }
        return null;
    }
    
    public void add(final TimeSeriesDataItem item) {
        this.add(item, true);
    }
    
    public void add(final TimeSeriesDataItem item, final boolean notify) {
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        if (!item.getPeriod().getClass().equals(this.timePeriodClass)) {
            final StringBuffer b = new StringBuffer();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }
        boolean added = false;
        final int count = this.getItemCount();
        if (count == 0) {
            this.data.add(item);
            added = true;
        }
        else {
            final RegularTimePeriod last = this.getTimePeriod(this.getItemCount() - 1);
            if (item.getPeriod().compareTo(last) > 0) {
                this.data.add(item);
                added = true;
            }
            else {
                final int index = Collections.binarySearch(this.data, item);
                if (index >= 0) {
                    final StringBuffer b2 = new StringBuffer();
                    b2.append("You are attempting to add an observation for ");
                    b2.append("the time period ");
                    b2.append(item.getPeriod().toString());
                    b2.append(" but the series already contains an observation");
                    b2.append(" for that time period. Duplicates are not ");
                    b2.append("permitted.  Try using the addOrUpdate() method.");
                    throw new SeriesException(b2.toString());
                }
                this.data.add(-index - 1, item);
                added = true;
            }
        }
        if (added) {
            if (this.getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
            this.removeAgedItems(false);
            if (notify) {
                this.fireSeriesChanged();
            }
        }
    }
    
    public void add(final RegularTimePeriod period, final double value) {
        this.add(period, value, true);
    }
    
    public void add(final RegularTimePeriod period, final double value, final boolean notify) {
        final TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        this.add(item, notify);
    }
    
    public void add(final RegularTimePeriod period, final Number value) {
        this.add(period, value, true);
    }
    
    public void add(final RegularTimePeriod period, final Number value, final boolean notify) {
        final TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        this.add(item, notify);
    }
    
    public void update(final RegularTimePeriod period, final Number value) {
        final TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
        final int index = Collections.binarySearch(this.data, temp);
        if (index >= 0) {
            final TimeSeriesDataItem pair = this.data.get(index);
            pair.setValue(value);
            this.fireSeriesChanged();
            return;
        }
        throw new SeriesException("TimeSeries.update(TimePeriod, Number):  period does not exist.");
    }
    
    public void update(final int index, final Number value) {
        final TimeSeriesDataItem item = this.getDataItem(index);
        item.setValue(value);
        this.fireSeriesChanged();
    }
    
    public TimeSeries addAndOrUpdate(final TimeSeries series) {
        final TimeSeries overwritten = new TimeSeries("Overwritten values from: " + this.getKey(), series.getTimePeriodClass());
        for (int i = 0; i < series.getItemCount(); ++i) {
            final TimeSeriesDataItem item = series.getDataItem(i);
            final TimeSeriesDataItem oldItem = this.addOrUpdate(item.getPeriod(), item.getValue());
            if (oldItem != null) {
                overwritten.add(oldItem);
            }
        }
        return overwritten;
    }
    
    public TimeSeriesDataItem addOrUpdate(final RegularTimePeriod period, final double value) {
        return this.addOrUpdate(period, new Double(value));
    }
    
    public TimeSeriesDataItem addOrUpdate(final RegularTimePeriod period, final Number value) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        TimeSeriesDataItem overwritten = null;
        final TimeSeriesDataItem key = new TimeSeriesDataItem(period, value);
        final int index = Collections.binarySearch(this.data, key);
        if (index >= 0) {
            final TimeSeriesDataItem existing = this.data.get(index);
            overwritten = (TimeSeriesDataItem)existing.clone();
            existing.setValue(value);
            this.removeAgedItems(false);
            this.fireSeriesChanged();
        }
        else {
            this.data.add(-index - 1, new TimeSeriesDataItem(period, value));
            if (this.getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
            this.removeAgedItems(false);
            this.fireSeriesChanged();
        }
        return overwritten;
    }
    
    public void removeAgedItems(final boolean notify) {
        if (this.getItemCount() > 1) {
            final long latest = this.getTimePeriod(this.getItemCount() - 1).getSerialIndex();
            boolean removed = false;
            while (latest - this.getTimePeriod(0).getSerialIndex() >= this.maximumItemAge) {
                this.data.remove(0);
                removed = true;
            }
            if (removed && notify) {
                this.fireSeriesChanged();
            }
        }
    }
    
    public void removeAgedItems(final long latest, final boolean notify) {
        if (this.getItemCount() > 1) {
            while (latest - this.getTimePeriod(0).getSerialIndex() >= this.maximumItemAge) {
                this.data.remove(0);
            }
        }
    }
    
    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            this.fireSeriesChanged();
        }
    }
    
    public void delete(final RegularTimePeriod period) {
        final int index = this.getIndex(period);
        if (index >= 0) {
            this.data.remove(index);
            this.fireSeriesChanged();
        }
    }
    
    public void delete(final int start, final int end) {
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        for (int i = 0; i <= end - start; ++i) {
            this.data.remove(start);
        }
        this.fireSeriesChanged();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Object clone = this.createCopy(0, this.getItemCount() - 1);
        return clone;
    }
    
    public TimeSeries createCopy(final int start, final int end) throws CloneNotSupportedException {
        if (start < 0) {
            throw new IllegalArgumentException("Requires start >= 0.");
        }
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        final TimeSeries copy = (TimeSeries)super.clone();
        copy.data = new ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; ++index) {
                final TimeSeriesDataItem item = this.data.get(index);
                final TimeSeriesDataItem clone = (TimeSeriesDataItem)item.clone();
                try {
                    copy.add(clone);
                }
                catch (final SeriesException e) {
                    e.printStackTrace();
                }
            }
        }
        return copy;
    }
    
    public TimeSeries createCopy(final RegularTimePeriod start, final RegularTimePeriod end) throws CloneNotSupportedException {
        if (start == null) {
            throw new IllegalArgumentException("Null 'start' argument.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Null 'end' argument.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Requires start on or before end.");
        }
        boolean emptyRange = false;
        int startIndex = this.getIndex(start);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
            if (startIndex == this.data.size()) {
                emptyRange = true;
            }
        }
        int endIndex = this.getIndex(end);
        if (endIndex < 0) {
            endIndex = -(endIndex + 1);
            --endIndex;
        }
        if (endIndex < 0) {
            emptyRange = true;
        }
        if (emptyRange) {
            final TimeSeries copy = (TimeSeries)super.clone();
            copy.data = new ArrayList();
            return copy;
        }
        return this.createCopy(startIndex, endIndex);
    }
    
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimeSeries) || !super.equals(object)) {
            return false;
        }
        final TimeSeries s = (TimeSeries)object;
        if (!ObjectUtilities.equal((Object)this.getDomainDescription(), (Object)s.getDomainDescription())) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.getRangeDescription(), (Object)s.getRangeDescription())) {
            return false;
        }
        if (!this.getClass().equals(s.getClass())) {
            return false;
        }
        if (this.getMaximumItemAge() != s.getMaximumItemAge()) {
            return false;
        }
        if (this.getMaximumItemCount() != s.getMaximumItemCount()) {
            return false;
        }
        final int count = this.getItemCount();
        if (count != s.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            if (!this.getDataItem(i).equals(s.getDataItem(i))) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        int result = (this.domain != null) ? this.domain.hashCode() : 0;
        result = 29 * result + ((this.range != null) ? this.range.hashCode() : 0);
        result = 29 * result + ((this.timePeriodClass != null) ? this.timePeriodClass.hashCode() : 0);
        result = 29 * result + this.data.hashCode();
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (int)this.maximumItemAge;
        return result;
    }
}
