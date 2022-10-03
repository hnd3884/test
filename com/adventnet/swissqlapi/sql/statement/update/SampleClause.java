package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class SampleClause
{
    private String sampleBlock;
    private String sample;
    public ArrayList samplePercentList;
    
    public SampleClause() {
        this.samplePercentList = new ArrayList();
        this.sampleBlock = new String();
        this.sample = new String();
    }
    
    public void setSample(final String s) {
        this.sample = s;
    }
    
    public void setBlock(final String s) {
        this.sampleBlock = s;
    }
    
    public void setSamplePercentList(final ArrayList list) {
        this.samplePercentList = list;
    }
    
    public String getSample() {
        return this.sample;
    }
    
    public String getBlock() {
        return this.sampleBlock;
    }
    
    public ArrayList getSamplePercentList() {
        return this.samplePercentList;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(this.sample.toUpperCase());
        stringbuffer.append(this.sampleBlock);
        for (int i = 0, size = this.samplePercentList.size(); i < size; ++i) {
            stringbuffer.append(this.samplePercentList.get(i).toString());
        }
        return stringbuffer.toString();
    }
}
