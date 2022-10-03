package org.apache.commons.math3.stat;

import java.util.Collection;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.Iterator;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.SortedMap;
import java.io.Serializable;

public class Frequency implements Serializable
{
    private static final long serialVersionUID = -3845586908418844111L;
    private final SortedMap<Comparable<?>, Long> freqTable;
    
    public Frequency() {
        this.freqTable = new TreeMap<Comparable<?>, Long>();
    }
    
    public Frequency(final Comparator<?> comparator) {
        this.freqTable = new TreeMap<Comparable<?>, Long>((Comparator<? super Comparable<?>>)comparator);
    }
    
    @Override
    public String toString() {
        final NumberFormat nf = NumberFormat.getPercentInstance();
        final StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
        for (final Comparable<?> value : this.freqTable.keySet()) {
            outBuffer.append(value);
            outBuffer.append('\t');
            outBuffer.append(this.getCount(value));
            outBuffer.append('\t');
            outBuffer.append(nf.format(this.getPct(value)));
            outBuffer.append('\t');
            outBuffer.append(nf.format(this.getCumPct(value)));
            outBuffer.append('\n');
        }
        return outBuffer.toString();
    }
    
    public void addValue(final Comparable<?> v) throws MathIllegalArgumentException {
        this.incrementValue(v, 1L);
    }
    
    public void addValue(final int v) throws MathIllegalArgumentException {
        this.addValue((Comparable<?>)(long)v);
    }
    
    public void addValue(final long v) throws MathIllegalArgumentException {
        this.addValue((Comparable<?>)v);
    }
    
    public void addValue(final char v) throws MathIllegalArgumentException {
        this.addValue((Comparable<?>)v);
    }
    
    public void incrementValue(final Comparable<?> v, final long increment) throws MathIllegalArgumentException {
        Comparable<?> obj = v;
        if (v instanceof Integer) {
            obj = v;
        }
        try {
            final Long count = this.freqTable.get(obj);
            if (count == null) {
                this.freqTable.put(obj, increment);
            }
            else {
                this.freqTable.put(obj, count + increment);
            }
        }
        catch (final ClassCastException ex) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES, new Object[] { v.getClass().getName() });
        }
    }
    
    public void incrementValue(final int v, final long increment) throws MathIllegalArgumentException {
        this.incrementValue((Comparable<?>)(long)v, increment);
    }
    
    public void incrementValue(final long v, final long increment) throws MathIllegalArgumentException {
        this.incrementValue((Comparable<?>)v, increment);
    }
    
    public void incrementValue(final char v, final long increment) throws MathIllegalArgumentException {
        this.incrementValue((Comparable<?>)v, increment);
    }
    
    public void clear() {
        this.freqTable.clear();
    }
    
    public Iterator<Comparable<?>> valuesIterator() {
        return this.freqTable.keySet().iterator();
    }
    
    public Iterator<Map.Entry<Comparable<?>, Long>> entrySetIterator() {
        return this.freqTable.entrySet().iterator();
    }
    
    public long getSumFreq() {
        long result = 0L;
        final Iterator<Long> iterator = this.freqTable.values().iterator();
        while (iterator.hasNext()) {
            result += iterator.next();
        }
        return result;
    }
    
    public long getCount(final Comparable<?> v) {
        if (v instanceof Integer) {
            return this.getCount((long)v);
        }
        long result = 0L;
        try {
            final Long count = this.freqTable.get(v);
            if (count != null) {
                result = count;
            }
        }
        catch (final ClassCastException ex) {}
        return result;
    }
    
    public long getCount(final int v) {
        return this.getCount((Comparable<?>)(long)v);
    }
    
    public long getCount(final long v) {
        return this.getCount((Comparable<?>)v);
    }
    
    public long getCount(final char v) {
        return this.getCount((Comparable<?>)v);
    }
    
    public int getUniqueCount() {
        return this.freqTable.keySet().size();
    }
    
    public double getPct(final Comparable<?> v) {
        final long sumFreq = this.getSumFreq();
        if (sumFreq == 0L) {
            return Double.NaN;
        }
        return this.getCount(v) / (double)sumFreq;
    }
    
    public double getPct(final int v) {
        return this.getPct((Comparable<?>)(long)v);
    }
    
    public double getPct(final long v) {
        return this.getPct((Comparable<?>)v);
    }
    
    public double getPct(final char v) {
        return this.getPct((Comparable<?>)v);
    }
    
    public long getCumFreq(final Comparable<?> v) {
        if (this.getSumFreq() == 0L) {
            return 0L;
        }
        if (v instanceof Integer) {
            return this.getCumFreq((long)v);
        }
        Comparator<Comparable<?>> c = (Comparator<Comparable<?>>)this.freqTable.comparator();
        if (c == null) {
            c = (Comparator<Comparable<?>>)new NaturalComparator<Object>();
        }
        long result = 0L;
        try {
            final Long value = this.freqTable.get(v);
            if (value != null) {
                result = value;
            }
        }
        catch (final ClassCastException ex) {
            return result;
        }
        if (c.compare(v, this.freqTable.firstKey()) < 0) {
            return 0L;
        }
        if (c.compare(v, this.freqTable.lastKey()) >= 0) {
            return this.getSumFreq();
        }
        final Iterator<Comparable<?>> values = this.valuesIterator();
        while (values.hasNext()) {
            final Comparable<?> nextValue = values.next();
            if (c.compare(v, nextValue) <= 0) {
                return result;
            }
            result += this.getCount(nextValue);
        }
        return result;
    }
    
    public long getCumFreq(final int v) {
        return this.getCumFreq((Comparable<?>)(long)v);
    }
    
    public long getCumFreq(final long v) {
        return this.getCumFreq((Comparable<?>)v);
    }
    
    public long getCumFreq(final char v) {
        return this.getCumFreq((Comparable<?>)v);
    }
    
    public double getCumPct(final Comparable<?> v) {
        final long sumFreq = this.getSumFreq();
        if (sumFreq == 0L) {
            return Double.NaN;
        }
        return this.getCumFreq(v) / (double)sumFreq;
    }
    
    public double getCumPct(final int v) {
        return this.getCumPct((Comparable<?>)(long)v);
    }
    
    public double getCumPct(final long v) {
        return this.getCumPct((Comparable<?>)v);
    }
    
    public double getCumPct(final char v) {
        return this.getCumPct((Comparable<?>)v);
    }
    
    public List<Comparable<?>> getMode() {
        long mostPopular = 0L;
        for (final Long l : this.freqTable.values()) {
            final long frequency = l;
            if (frequency > mostPopular) {
                mostPopular = frequency;
            }
        }
        final List<Comparable<?>> modeList = new ArrayList<Comparable<?>>();
        for (final Map.Entry<Comparable<?>, Long> ent : this.freqTable.entrySet()) {
            final long frequency2 = ent.getValue();
            if (frequency2 == mostPopular) {
                modeList.add(ent.getKey());
            }
        }
        return modeList;
    }
    
    public void merge(final Frequency other) throws NullArgumentException {
        MathUtils.checkNotNull(other, LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        final Iterator<Map.Entry<Comparable<?>, Long>> iter = other.entrySetIterator();
        while (iter.hasNext()) {
            final Map.Entry<Comparable<?>, Long> entry = iter.next();
            this.incrementValue(entry.getKey(), entry.getValue());
        }
    }
    
    public void merge(final Collection<Frequency> others) throws NullArgumentException {
        MathUtils.checkNotNull(others, LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        for (final Frequency freq : others) {
            this.merge(freq);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.freqTable == null) ? 0 : this.freqTable.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frequency)) {
            return false;
        }
        final Frequency other = (Frequency)obj;
        if (this.freqTable == null) {
            if (other.freqTable != null) {
                return false;
            }
        }
        else if (!this.freqTable.equals(other.freqTable)) {
            return false;
        }
        return true;
    }
    
    private static class NaturalComparator<T extends Comparable<T>> implements Comparator<Comparable<T>>, Serializable
    {
        private static final long serialVersionUID = -3852193713161395148L;
        
        public int compare(final Comparable<T> o1, final Comparable<T> o2) {
            return o1.compareTo((T)o2);
        }
    }
}
