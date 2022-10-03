package org.tukaani.xz.lz;

public final class Matches
{
    public final int[] len;
    public final int[] dist;
    public int count;
    
    Matches(final int n) {
        this.count = 0;
        this.len = new int[n];
        this.dist = new int[n];
    }
}
