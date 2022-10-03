package org.jfree.chart.entity;

import java.awt.Shape;
import org.jfree.data.general.PieDataset;
import java.io.Serializable;

public class PieSectionEntity extends ChartEntity implements Serializable
{
    private static final long serialVersionUID = 9199892576531984162L;
    private PieDataset dataset;
    private int pieIndex;
    private int sectionIndex;
    private Comparable sectionKey;
    
    public PieSectionEntity(final Shape area, final PieDataset dataset, final int pieIndex, final int sectionIndex, final Comparable sectionKey, final String toolTipText, final String urlText) {
        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.pieIndex = pieIndex;
        this.sectionIndex = sectionIndex;
        this.sectionKey = sectionKey;
    }
    
    public PieDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final PieDataset dataset) {
        this.dataset = dataset;
    }
    
    public int getPieIndex() {
        return this.pieIndex;
    }
    
    public void setPieIndex(final int index) {
        this.pieIndex = index;
    }
    
    public int getSectionIndex() {
        return this.sectionIndex;
    }
    
    public void setSectionIndex(final int index) {
        this.sectionIndex = index;
    }
    
    public Comparable getSectionKey() {
        return this.sectionKey;
    }
    
    public void setSectionKey(final Comparable key) {
        this.sectionKey = key;
    }
    
    public String toString() {
        return "PieSection: " + this.pieIndex + ", " + this.sectionIndex + "(" + this.sectionKey.toString() + ")";
    }
}
