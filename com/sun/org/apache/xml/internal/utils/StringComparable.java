package com.sun.org.apache.xml.internal.utils;

import java.text.CollationElementIterator;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;

public class StringComparable implements Comparable
{
    public static final int UNKNOWN_CASE = -1;
    public static final int UPPER_CASE = 1;
    public static final int LOWER_CASE = 2;
    private String m_text;
    private Locale m_locale;
    private RuleBasedCollator m_collator;
    private String m_caseOrder;
    private int m_mask;
    
    public StringComparable(final String text, final Locale locale, final Collator collator, final String caseOrder) {
        this.m_mask = -1;
        this.m_text = text;
        this.m_locale = locale;
        this.m_collator = (RuleBasedCollator)collator;
        this.m_caseOrder = caseOrder;
        this.m_mask = getMask(this.m_collator.getStrength());
    }
    
    public static final Comparable getComparator(final String text, final Locale locale, final Collator collator, final String caseOrder) {
        if (caseOrder == null || caseOrder.length() == 0) {
            return ((RuleBasedCollator)collator).getCollationKey(text);
        }
        return new StringComparable(text, locale, collator, caseOrder);
    }
    
    @Override
    public final String toString() {
        return this.m_text;
    }
    
    @Override
    public int compareTo(final Object o) {
        final String pattern = ((StringComparable)o).toString();
        if (this.m_text.equals(pattern)) {
            return 0;
        }
        final int savedStrength = this.m_collator.getStrength();
        int comp = 0;
        if (savedStrength == 0 || savedStrength == 1) {
            comp = this.m_collator.compare(this.m_text, pattern);
        }
        else {
            this.m_collator.setStrength(1);
            comp = this.m_collator.compare(this.m_text, pattern);
            this.m_collator.setStrength(savedStrength);
        }
        if (comp != 0) {
            return comp;
        }
        comp = this.getCaseDiff(this.m_text, pattern);
        if (comp != 0) {
            return comp;
        }
        return this.m_collator.compare(this.m_text, pattern);
    }
    
    private final int getCaseDiff(final String text, final String pattern) {
        final int savedStrength = this.m_collator.getStrength();
        final int savedDecomposition = this.m_collator.getDecomposition();
        this.m_collator.setStrength(2);
        this.m_collator.setDecomposition(1);
        final int[] diff = this.getFirstCaseDiff(text, pattern, this.m_locale);
        this.m_collator.setStrength(savedStrength);
        this.m_collator.setDecomposition(savedDecomposition);
        if (diff == null) {
            return 0;
        }
        if (this.m_caseOrder.equals("upper-first")) {
            if (diff[0] == 1) {
                return -1;
            }
            return 1;
        }
        else {
            if (diff[0] == 2) {
                return -1;
            }
            return 1;
        }
    }
    
    private final int[] getFirstCaseDiff(final String text, final String pattern, final Locale locale) {
        final CollationElementIterator targIter = this.m_collator.getCollationElementIterator(text);
        final CollationElementIterator patIter = this.m_collator.getCollationElementIterator(pattern);
        int startTarg = -1;
        int endTarg = -1;
        int startPatt = -1;
        int endPatt = -1;
        final int done = this.getElement(-1);
        int patternElement = 0;
        int targetElement = 0;
        boolean getPattern = true;
        boolean getTarget = true;
        while (true) {
            if (getPattern) {
                startPatt = patIter.getOffset();
                patternElement = this.getElement(patIter.next());
                endPatt = patIter.getOffset();
            }
            if (getTarget) {
                startTarg = targIter.getOffset();
                targetElement = this.getElement(targIter.next());
                endTarg = targIter.getOffset();
            }
            getPattern = (getTarget = true);
            if (patternElement == done || targetElement == done) {
                return null;
            }
            if (targetElement == 0) {
                getPattern = false;
            }
            else if (patternElement == 0) {
                getTarget = false;
            }
            else {
                if (targetElement == patternElement || startPatt >= endPatt || startTarg >= endTarg) {
                    continue;
                }
                final String subText = text.substring(startTarg, endTarg);
                final String subPatt = pattern.substring(startPatt, endPatt);
                final String subTextUp = subText.toUpperCase(locale);
                final String subPattUp = subPatt.toUpperCase(locale);
                if (this.m_collator.compare(subTextUp, subPattUp) != 0) {
                    continue;
                }
                final int[] diff = { -1, -1 };
                if (this.m_collator.compare(subText, subTextUp) == 0) {
                    diff[0] = 1;
                }
                else if (this.m_collator.compare(subText, subText.toLowerCase(locale)) == 0) {
                    diff[0] = 2;
                }
                if (this.m_collator.compare(subPatt, subPattUp) == 0) {
                    diff[1] = 1;
                }
                else if (this.m_collator.compare(subPatt, subPatt.toLowerCase(locale)) == 0) {
                    diff[1] = 2;
                }
                if ((diff[0] == 1 && diff[1] == 2) || (diff[0] == 2 && diff[1] == 1)) {
                    return diff;
                }
                continue;
            }
        }
    }
    
    private static final int getMask(final int strength) {
        switch (strength) {
            case 0: {
                return -65536;
            }
            case 1: {
                return -256;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int getElement(final int maxStrengthElement) {
        return maxStrengthElement & this.m_mask;
    }
}
