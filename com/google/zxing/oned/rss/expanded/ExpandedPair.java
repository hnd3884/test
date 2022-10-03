package com.google.zxing.oned.rss.expanded;

import com.google.zxing.oned.rss.FinderPattern;
import com.google.zxing.oned.rss.DataCharacter;

final class ExpandedPair
{
    private final boolean mayBeLast;
    private final DataCharacter leftChar;
    private final DataCharacter rightChar;
    private final FinderPattern finderPattern;
    
    ExpandedPair(final DataCharacter leftChar, final DataCharacter rightChar, final FinderPattern finderPattern, final boolean mayBeLast) {
        this.leftChar = leftChar;
        this.rightChar = rightChar;
        this.finderPattern = finderPattern;
        this.mayBeLast = mayBeLast;
    }
    
    boolean mayBeLast() {
        return this.mayBeLast;
    }
    
    DataCharacter getLeftChar() {
        return this.leftChar;
    }
    
    DataCharacter getRightChar() {
        return this.rightChar;
    }
    
    FinderPattern getFinderPattern() {
        return this.finderPattern;
    }
    
    public boolean mustBeLast() {
        return this.rightChar == null;
    }
}
