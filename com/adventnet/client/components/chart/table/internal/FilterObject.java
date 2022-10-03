package com.adventnet.client.components.chart.table.internal;

public class FilterObject implements Comparable
{
    Object[] data;
    String temp;
    
    public FilterObject(final Object[] data) {
        this.data = null;
        this.temp = null;
        this.data = data;
    }
    
    public Object[] getData() {
        return this.data;
    }
    
    @Override
    public boolean equals(final Object o) {
        final FilterObject toCompared = (FilterObject)o;
        final Object[] cmpData = toCompared.getData();
        if (cmpData.length == this.data.length) {
            for (int length = this.data.length, i = 0; i < length; ++i) {
                if (!this.data[i].equals(cmpData[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(final Object o) {
        final FilterObject toCompared = (FilterObject)o;
        return this.toString().compareTo(toCompared.toString());
    }
    
    @Override
    public String toString() {
        if (this.temp == null) {
            final StringBuffer sbf = new StringBuffer();
            for (int i = 0; i < this.data.length; ++i) {
                if (i + 1 != this.data.length) {
                    sbf.append(this.data[i].toString() + "_");
                }
                else {
                    sbf.append(this.data[i].toString());
                }
            }
            this.temp = sbf.toString();
        }
        return this.temp;
    }
    
    @Override
    public int hashCode() {
        final int len = this.data.length;
        int has = 0;
        for (int i = 0; i < len; ++i) {
            has += this.data[i].hashCode();
        }
        return has;
    }
}
