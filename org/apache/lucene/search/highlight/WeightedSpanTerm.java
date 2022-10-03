package org.apache.lucene.search.highlight;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class WeightedSpanTerm extends WeightedTerm
{
    boolean positionSensitive;
    private List<PositionSpan> positionSpans;
    
    public WeightedSpanTerm(final float weight, final String term) {
        super(weight, term);
        this.positionSpans = new ArrayList<PositionSpan>();
        this.positionSpans = new ArrayList<PositionSpan>();
    }
    
    public WeightedSpanTerm(final float weight, final String term, final boolean positionSensitive) {
        super(weight, term);
        this.positionSpans = new ArrayList<PositionSpan>();
        this.positionSensitive = positionSensitive;
    }
    
    public boolean checkPosition(final int position) {
        for (final PositionSpan posSpan : this.positionSpans) {
            if (position >= posSpan.start && position <= posSpan.end) {
                return true;
            }
        }
        return false;
    }
    
    public void addPositionSpans(final List<PositionSpan> positionSpans) {
        this.positionSpans.addAll(positionSpans);
    }
    
    public boolean isPositionSensitive() {
        return this.positionSensitive;
    }
    
    public void setPositionSensitive(final boolean positionSensitive) {
        this.positionSensitive = positionSensitive;
    }
    
    public List<PositionSpan> getPositionSpans() {
        return this.positionSpans;
    }
}
