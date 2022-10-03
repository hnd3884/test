package org.apache.xerces.impl.xpath.regex;

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
        final Match match = new Match();
        if (this.nofgroups > 0) {
            match.setNumberOfGroups(this.nofgroups);
            if (this.ciSource != null) {
                match.setSource(this.ciSource);
            }
            if (this.strSource != null) {
                match.setSource(this.strSource);
            }
            for (int i = 0; i < this.nofgroups; ++i) {
                match.setBeginning(i, this.getBeginning(i));
                match.setEnd(i, this.getEnd(i));
            }
        }
        return match;
    }
    
    protected void setNumberOfGroups(final int nofgroups) {
        final int nofgroups2 = this.nofgroups;
        this.nofgroups = nofgroups;
        if (nofgroups2 <= 0 || nofgroups2 < nofgroups || nofgroups * 2 < nofgroups2) {
            this.beginpos = new int[nofgroups];
            this.endpos = new int[nofgroups];
        }
        for (int i = 0; i < nofgroups; ++i) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }
    
    protected void setSource(final CharacterIterator ciSource) {
        this.ciSource = ciSource;
        this.strSource = null;
        this.charSource = null;
    }
    
    protected void setSource(final String strSource) {
        this.ciSource = null;
        this.strSource = strSource;
        this.charSource = null;
    }
    
    protected void setSource(final char[] charSource) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = charSource;
    }
    
    protected void setBeginning(final int n, final int n2) {
        this.beginpos[n] = n2;
    }
    
    protected void setEnd(final int n, final int n2) {
        this.endpos[n] = n2;
    }
    
    public int getNumberOfGroups() {
        if (this.nofgroups <= 0) {
            throw new IllegalStateException("A result is not set.");
        }
        return this.nofgroups;
    }
    
    public int getBeginning(final int n) {
        if (this.beginpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        return this.beginpos[n];
    }
    
    public int getEnd(final int n) {
        if (this.endpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        return this.endpos[n];
    }
    
    public String getCapturedText(final int n) {
        if (this.beginpos == null) {
            throw new IllegalStateException("match() has never been called.");
        }
        if (n < 0 || this.nofgroups <= n) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + n);
        }
        final int n2 = this.beginpos[n];
        final int n3 = this.endpos[n];
        if (n2 < 0 || n3 < 0) {
            return null;
        }
        String s;
        if (this.ciSource != null) {
            s = REUtil.substring(this.ciSource, n2, n3);
        }
        else if (this.strSource != null) {
            s = this.strSource.substring(n2, n3);
        }
        else {
            s = new String(this.charSource, n2, n3 - n2);
        }
        return s;
    }
}
