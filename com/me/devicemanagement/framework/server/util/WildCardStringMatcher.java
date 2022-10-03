package com.me.devicemanagement.framework.server.util;

import java.util.Vector;

public class WildCardStringMatcher
{
    private String wildCardPatternString;
    private int wildCardPatternLength;
    private boolean ignoreWildCards;
    private boolean hasLeadingStar;
    private boolean hasTrailingStar;
    private String[] charSegments;
    private int charBound;
    
    public WildCardStringMatcher() {
        this.charBound = 0;
        this.ignoreWildCards = false;
    }
    
    public boolean isStringMatching(String actualString, String wildCardString) {
        actualString = actualString.toLowerCase();
        wildCardString = wildCardString.toLowerCase();
        this.wildCardPatternString = wildCardString;
        this.wildCardPatternLength = wildCardString.length();
        this.setWildCards();
        return this.doesMatch(actualString, 0, actualString.length());
    }
    
    private void setWildCards() {
        if (this.wildCardPatternString.startsWith("*")) {
            this.hasLeadingStar = true;
        }
        if (this.wildCardPatternString.endsWith("*") && this.wildCardPatternLength > 1) {
            this.hasTrailingStar = true;
        }
        final Vector<String> temp = new Vector<String>();
        int pos = 0;
        final StringBuffer buf = new StringBuffer();
        while (pos < this.wildCardPatternLength) {
            final char c = this.wildCardPatternString.charAt(pos++);
            switch (c) {
                case '*': {
                    if (buf.length() > 0) {
                        temp.addElement(buf.toString());
                        this.charBound += buf.length();
                        buf.setLength(0);
                        continue;
                    }
                    continue;
                }
                case '?': {
                    buf.append('\0');
                    continue;
                }
                default: {
                    buf.append(c);
                    continue;
                }
            }
        }
        if (buf.length() > 0) {
            temp.addElement(buf.toString());
            this.charBound += buf.length();
        }
        temp.copyInto(this.charSegments = new String[temp.size()]);
    }
    
    private final boolean doesMatch(final String text, int startPoint, int endPoint) {
        final int textLength = text.length();
        if (startPoint > endPoint) {
            return false;
        }
        if (this.ignoreWildCards) {
            return endPoint - startPoint == this.wildCardPatternLength && this.wildCardPatternString.regionMatches(false, 0, text, startPoint, this.wildCardPatternLength);
        }
        final int charCount = this.charSegments.length;
        if (charCount == 0 && (this.hasLeadingStar || this.hasTrailingStar)) {
            return true;
        }
        if (startPoint == endPoint) {
            return this.wildCardPatternLength == 0;
        }
        if (this.wildCardPatternLength == 0) {
            return startPoint == endPoint;
        }
        if (startPoint < 0) {
            startPoint = 0;
        }
        if (endPoint > textLength) {
            endPoint = textLength;
        }
        int currPosition = startPoint;
        final int bound = endPoint - this.charBound;
        if (bound < 0) {
            return false;
        }
        int i = 0;
        String currString = this.charSegments[i];
        final int currStringLength = currString.length();
        if (!this.hasLeadingStar) {
            if (!this.isExpressionMatching(text, startPoint, currString, 0, currStringLength)) {
                return false;
            }
            ++i;
            currPosition += currStringLength;
        }
        if (this.charSegments.length == 1 && !this.hasLeadingStar && !this.hasTrailingStar) {
            return currPosition == endPoint;
        }
        while (i < charCount) {
            currString = this.charSegments[i];
            final int k = currString.indexOf(0);
            final int currentMatch = this.getTextPosition(text, currPosition, endPoint, currString);
            if (k < 0 && currentMatch < 0) {
                return false;
            }
            currPosition = currentMatch + currString.length();
            ++i;
        }
        if (!this.hasTrailingStar && currPosition != endPoint) {
            final int clen = currString.length();
            return this.isExpressionMatching(text, endPoint - clen, currString, 0, clen);
        }
        return i == charCount;
    }
    
    private final int getTextPosition(final String textString, final int start, final int end, final String posString) {
        final int plen = posString.length();
        final int max = end - plen;
        int position = -1;
        final int i = textString.indexOf(posString, start);
        if (posString.equals(".")) {
            position = 1;
        }
        if (i == -1 || i > max) {
            position = -1;
        }
        else {
            position = i;
        }
        return position;
    }
    
    private boolean isExpressionMatching(final String textString, int stringStartIndex, final String patternString, int patternStartIndex, int length) {
        while (length-- > 0) {
            final char textChar = textString.charAt(stringStartIndex++);
            final char patternChar = patternString.charAt(patternStartIndex++);
            if ((this.ignoreWildCards || patternChar != '\0') && patternChar != textChar && textChar != patternChar && textChar != patternChar) {
                return false;
            }
        }
        return true;
    }
}
