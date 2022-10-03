package org.apache.lucene.analysis.compound.hyphenation;

public class Hyphenation
{
    private int[] hyphenPoints;
    
    Hyphenation(final int[] points) {
        this.hyphenPoints = points;
    }
    
    public int length() {
        return this.hyphenPoints.length;
    }
    
    public int[] getHyphenationPoints() {
        return this.hyphenPoints;
    }
}
