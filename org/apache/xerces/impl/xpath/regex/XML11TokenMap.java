package org.apache.xerces.impl.xpath.regex;

import java.util.HashMap;

final class XML11TokenMap implements RangeTokenMap
{
    private HashMap ranges;
    private HashMap ranges2;
    
    private XML11TokenMap() {
        this.createRanges();
    }
    
    static RangeTokenMap instance() {
        return new XML11TokenMap();
    }
    
    private void createRanges() {
        this.ranges = new HashMap();
        this.ranges2 = new HashMap();
        final RangeToken range = Token.createRange();
        REUtil.setupRange(range, "\t\n\r\r  ");
        this.ranges.put("xml:isSpace", range);
        this.ranges2.put("xml:isSpace", Token.complementRanges(range));
        final RangeToken range2 = Token.createRange();
        REUtil.setupRange(range2, REConstants.DIGITS_INTS);
        this.ranges.put("xml:isDigit", range2);
        this.ranges2.put("xml:isDigit", Token.complementRanges(range2));
        final RangeToken range3 = Token.createRange();
        range3.mergeRanges(Token.getRange("P", true));
        range3.mergeRanges(Token.getRange("Z", true));
        range3.mergeRanges(Token.getRange("C", true));
        this.ranges2.put("xml:isWord", range3);
        this.ranges.put("xml:isWord", Token.complementRanges(range3));
        final RangeToken range4 = Token.createRange();
        REUtil.setupRange(range4, REConstants.NAMECHARS11_INTS);
        this.ranges.put("xml:isNameChar", range4);
        this.ranges2.put("xml:isNameChar", Token.complementRanges(range4));
        final RangeToken range5 = Token.createRange();
        REUtil.setupRange(range5, REConstants.NAMESTARTCHARS11_INTS);
        this.ranges.put("xml:isInitialNameChar", range5);
        this.ranges2.put("xml:isInitialNameChar", Token.complementRanges(range5));
    }
    
    public RangeToken get(final String s, final boolean b) {
        return b ? this.ranges.get(s) : this.ranges2.get(s);
    }
}
