package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import java.util.Iterator;
import org.apache.poi.util.LittleEndianOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public abstract class PageBreakRecord extends StandardRecord
{
    private static final int[] EMPTY_INT_ARRAY;
    private final ArrayList<Break> _breaks;
    private final Map<Integer, Break> _breakMap;
    
    protected PageBreakRecord() {
        this._breaks = new ArrayList<Break>();
        this._breakMap = new HashMap<Integer, Break>();
    }
    
    protected PageBreakRecord(final PageBreakRecord other) {
        this._breaks = new ArrayList<Break>();
        this._breakMap = new HashMap<Integer, Break>();
        this._breaks.addAll(other._breaks);
        this.initMap();
    }
    
    public PageBreakRecord(final RecordInputStream in) {
        this._breaks = new ArrayList<Break>();
        this._breakMap = new HashMap<Integer, Break>();
        final int nBreaks = in.readShort();
        this._breaks.ensureCapacity(nBreaks + 2);
        for (int k = 0; k < nBreaks; ++k) {
            this._breaks.add(new Break(in));
        }
        this.initMap();
    }
    
    private void initMap() {
        this._breaks.forEach(br -> {
            final Break break1 = this._breakMap.put(br.main, br);
        });
    }
    
    public boolean isEmpty() {
        return this._breaks.isEmpty();
    }
    
    @Override
    protected int getDataSize() {
        return 2 + this._breaks.size() * 6;
    }
    
    public final void serialize(final LittleEndianOutput out) {
        final int nBreaks = this._breaks.size();
        out.writeShort(nBreaks);
        for (final Break aBreak : this._breaks) {
            aBreak.serialize(out);
        }
    }
    
    public int getNumBreaks() {
        return this._breaks.size();
    }
    
    public final Iterator<Break> getBreaksIterator() {
        return this._breaks.iterator();
    }
    
    @Override
    public String toString() {
        final StringBuilder retval = new StringBuilder();
        String label;
        String mainLabel;
        String subLabel;
        if (this.getSid() == 27) {
            label = "HORIZONTALPAGEBREAK";
            mainLabel = "row";
            subLabel = "col";
        }
        else {
            label = "VERTICALPAGEBREAK";
            mainLabel = "column";
            subLabel = "row";
        }
        retval.append("[" + label + "]").append("\n");
        retval.append("     .sid        =").append(this.getSid()).append("\n");
        retval.append("     .numbreaks =").append(this.getNumBreaks()).append("\n");
        final Iterator<Break> iterator = this.getBreaksIterator();
        for (int k = 0; k < this.getNumBreaks(); ++k) {
            final Break region = iterator.next();
            retval.append("     .").append(mainLabel).append(" (zero-based) =").append(region.main).append("\n");
            retval.append("     .").append(subLabel).append("From    =").append(region.subFrom).append("\n");
            retval.append("     .").append(subLabel).append("To      =").append(region.subTo).append("\n");
        }
        retval.append("[" + label + "]").append("\n");
        return retval.toString();
    }
    
    public void addBreak(final int main, final int subFrom, final int subTo) {
        final Integer key = main;
        Break region = this._breakMap.get(key);
        if (region == null) {
            region = new Break(main, subFrom, subTo);
            this._breakMap.put(key, region);
            this._breaks.add(region);
        }
        else {
            region.main = main;
            region.subFrom = subFrom;
            region.subTo = subTo;
        }
    }
    
    public final void removeBreak(final int main) {
        final Integer rowKey = main;
        final Break region = this._breakMap.get(rowKey);
        this._breaks.remove(region);
        this._breakMap.remove(rowKey);
    }
    
    public final Break getBreak(final int main) {
        final Integer rowKey = main;
        return this._breakMap.get(rowKey);
    }
    
    public final int[] getBreaks() {
        final int count = this.getNumBreaks();
        if (count < 1) {
            return PageBreakRecord.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[count];
        for (int i = 0; i < count; ++i) {
            final Break breakItem = this._breaks.get(i);
            result[i] = breakItem.main;
        }
        return result;
    }
    
    @Override
    public abstract PageBreakRecord copy();
    
    static {
        EMPTY_INT_ARRAY = new int[0];
    }
    
    public static final class Break
    {
        public static final int ENCODED_SIZE = 6;
        public int main;
        public int subFrom;
        public int subTo;
        
        public Break(final Break other) {
            this.main = other.main;
            this.subFrom = other.subFrom;
            this.subTo = other.subTo;
        }
        
        public Break(final int main, final int subFrom, final int subTo) {
            this.main = main;
            this.subFrom = subFrom;
            this.subTo = subTo;
        }
        
        public Break(final RecordInputStream in) {
            this.main = in.readUShort() - 1;
            this.subFrom = in.readUShort();
            this.subTo = in.readUShort();
        }
        
        public void serialize(final LittleEndianOutput out) {
            out.writeShort(this.main + 1);
            out.writeShort(this.subFrom);
            out.writeShort(this.subTo);
        }
    }
}
