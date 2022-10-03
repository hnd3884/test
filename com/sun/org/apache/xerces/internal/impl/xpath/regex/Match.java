package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public class Match implements Cloneable
{
    int[] beginpos;
    int[] endpos;
    int nofgroups;
    CharacterIterator ciSource;
    String strSource;
    char[] charSource;
    
    public Match() {
        this.beginpos = null;
        this.endpos = null;
        this.nofgroups = 0;
        this.ciSource = null;
        this.strSource = null;
        this.charSource = null;
    }
    
    public synchronized Object clone() {
        final Match ma = new Match();
        if (this.nofgroups > 0) {
            ma.setNumberOfGroups(this.nofgroups);
            if (this.ciSource != null) {
                ma.setSource(this.ciSource);
            }
            if (this.strSource != null) {
                ma.setSource(this.strSource);
            }
            for (int i = 0; i < this.nofgroups; ++i) {
                ma.setBeginning(i, this.getBeginning(i));
                ma.setEnd(i, this.getEnd(i));
            }
        }
        return ma;
    }
    
    protected void setNumberOfGroups(final int n) {
        final int oldn = this.nofgroups;
        this.nofgroups = n;
        if (oldn <= 0 || oldn < n || n * 2 < oldn) {
            this.beginpos = new int[n];
            this.endpos = new int[n];
        }
        for (int i = 0; i < n; ++i) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }
    
    protected void setSource(final CharacterIterator ci) {
        this.ciSource = ci;
        this.strSource = null;
        this.charSource = null;
    }
    
    protected void setSource(final String str) {
        this.ciSource = null;
        this.strSource = str;
        this.charSource = null;
    }
    
    protected void setSource(final char[] chars) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = chars;
    }
    
    protected void setBeginning(final int index, final int v) {
        this.beginpos[index] = v;
    }
    
    protected void setEnd(final int index, final int v) {
        this.endpos[index] = v;
    }
    
    public int getNumberOfGroups() {
        if (this.nofgroups <= 0) {
            throw new IllegalStateException("A result is not set.");
        }
        return this.nofgroups;
    }
    
    public int getBeginning(final int index) {
        if (this.beginpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        return this.beginpos[index];
    }
    
    public int getEnd(final int index) {
        if (this.endpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        return this.endpos[index];
    }
    
    public String getCapturedText(final int index) {
        if (this.beginpos == null) {
            throw new IllegalStateException("match() has never been called.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        final int begin = this.beginpos[index];
        final int end = this.endpos[index];
        if (begin < 0 || end < 0) {
            return null;
        }
        String ret;
        if (this.ciSource != null) {
            ret = REUtil.substring(this.ciSource, begin, end);
        }
        else if (this.strSource != null) {
            ret = this.strSource.substring(begin, end);
        }
        else {
            ret = new String(this.charSource, begin, end - begin);
        }
        return ret;
    }
}
