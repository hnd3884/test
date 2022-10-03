package com.google.zxing.oned.rss;

final class Pair extends DataCharacter
{
    private final FinderPattern finderPattern;
    private int count;
    
    Pair(final int value, final int checksumPortion, final FinderPattern finderPattern) {
        super(value, checksumPortion);
        this.finderPattern = finderPattern;
    }
    
    FinderPattern getFinderPattern() {
        return this.finderPattern;
    }
    
    int getCount() {
        return this.count;
    }
    
    void incrementCount() {
        ++this.count;
    }
}
