package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

public class LegendItemEntity extends ChartEntity implements Cloneable, Serializable
{
    private static final long serialVersionUID = -7435683933545666702L;
    private int seriesIndex;
    
    public LegendItemEntity(final Shape area) {
        super(area);
    }
    
    public int getSeriesIndex() {
        return this.seriesIndex;
    }
    
    public void setSeriesIndex(final int index) {
        this.seriesIndex = index;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LegendItemEntity && super.equals(obj)) {
            final LegendItemEntity e = (LegendItemEntity)obj;
            return this.seriesIndex == e.seriesIndex;
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
