package org.apache.poi.ss.format;

import org.apache.poi.util.Internal;

@Internal
public class CellNumberStringMod implements Comparable<CellNumberStringMod>
{
    public static final int BEFORE = 1;
    public static final int AFTER = 2;
    public static final int REPLACE = 3;
    private final CellNumberFormatter.Special special;
    private final int op;
    private CharSequence toAdd;
    private CellNumberFormatter.Special end;
    private boolean startInclusive;
    private boolean endInclusive;
    
    public CellNumberStringMod(final CellNumberFormatter.Special special, final CharSequence toAdd, final int op) {
        this.special = special;
        this.toAdd = toAdd;
        this.op = op;
    }
    
    public CellNumberStringMod(final CellNumberFormatter.Special start, final boolean startInclusive, final CellNumberFormatter.Special end, final boolean endInclusive, final char toAdd) {
        this(start, startInclusive, end, endInclusive);
        this.toAdd = toAdd + "";
    }
    
    public CellNumberStringMod(final CellNumberFormatter.Special start, final boolean startInclusive, final CellNumberFormatter.Special end, final boolean endInclusive) {
        this.special = start;
        this.startInclusive = startInclusive;
        this.end = end;
        this.endInclusive = endInclusive;
        this.op = 3;
        this.toAdd = "";
    }
    
    @Override
    public int compareTo(final CellNumberStringMod that) {
        final int diff = this.special.pos - that.special.pos;
        return (diff != 0) ? diff : (this.op - that.op);
    }
    
    @Override
    public boolean equals(final Object that) {
        return that instanceof CellNumberStringMod && this.compareTo((CellNumberStringMod)that) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.special.hashCode() + this.op;
    }
    
    public CellNumberFormatter.Special getSpecial() {
        return this.special;
    }
    
    public int getOp() {
        return this.op;
    }
    
    public CharSequence getToAdd() {
        return this.toAdd;
    }
    
    public CellNumberFormatter.Special getEnd() {
        return this.end;
    }
    
    public boolean isStartInclusive() {
        return this.startInclusive;
    }
    
    public boolean isEndInclusive() {
        return this.endInclusive;
    }
}
