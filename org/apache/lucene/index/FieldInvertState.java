package org.apache.lucene.index;

import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

public final class FieldInvertState
{
    String name;
    int position;
    int length;
    int numOverlap;
    int offset;
    int maxTermFrequency;
    int uniqueTermCount;
    float boost;
    int lastStartOffset;
    int lastPosition;
    AttributeSource attributeSource;
    OffsetAttribute offsetAttribute;
    PositionIncrementAttribute posIncrAttribute;
    PayloadAttribute payloadAttribute;
    TermToBytesRefAttribute termAttribute;
    
    public FieldInvertState(final String name) {
        this.lastStartOffset = 0;
        this.lastPosition = 0;
        this.name = name;
    }
    
    public FieldInvertState(final String name, final int position, final int length, final int numOverlap, final int offset, final float boost) {
        this.lastStartOffset = 0;
        this.lastPosition = 0;
        this.name = name;
        this.position = position;
        this.length = length;
        this.numOverlap = numOverlap;
        this.offset = offset;
        this.boost = boost;
    }
    
    void reset() {
        this.position = -1;
        this.length = 0;
        this.numOverlap = 0;
        this.offset = 0;
        this.maxTermFrequency = 0;
        this.uniqueTermCount = 0;
        this.boost = 1.0f;
        this.lastStartOffset = 0;
        this.lastPosition = 0;
    }
    
    void setAttributeSource(final AttributeSource attributeSource) {
        if (this.attributeSource != attributeSource) {
            this.attributeSource = attributeSource;
            this.termAttribute = attributeSource.getAttribute(TermToBytesRefAttribute.class);
            this.posIncrAttribute = attributeSource.addAttribute(PositionIncrementAttribute.class);
            this.offsetAttribute = attributeSource.addAttribute(OffsetAttribute.class);
            this.payloadAttribute = attributeSource.getAttribute(PayloadAttribute.class);
        }
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public int getNumOverlap() {
        return this.numOverlap;
    }
    
    public void setNumOverlap(final int numOverlap) {
        this.numOverlap = numOverlap;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public float getBoost() {
        return this.boost;
    }
    
    public void setBoost(final float boost) {
        this.boost = boost;
    }
    
    public int getMaxTermFrequency() {
        return this.maxTermFrequency;
    }
    
    public int getUniqueTermCount() {
        return this.uniqueTermCount;
    }
    
    public AttributeSource getAttributeSource() {
        return this.attributeSource;
    }
    
    public String getName() {
        return this.name;
    }
}
