package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class TabIdRecord extends StandardRecord
{
    public static final short sid = 317;
    private static final short[] EMPTY_SHORT_ARRAY;
    public short[] _tabids;
    
    public TabIdRecord() {
        this._tabids = TabIdRecord.EMPTY_SHORT_ARRAY;
    }
    
    public TabIdRecord(final TabIdRecord other) {
        super(other);
        this._tabids = (short[])((other._tabids == null) ? null : ((short[])other._tabids.clone()));
    }
    
    public TabIdRecord(final RecordInputStream in) {
        final int nTabs = in.remaining() / 2;
        this._tabids = new short[nTabs];
        for (int i = 0; i < this._tabids.length; ++i) {
            this._tabids[i] = in.readShort();
        }
    }
    
    public void setTabIdArray(final short[] array) {
        this._tabids = array.clone();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[TABID]\n");
        buffer.append("    .elements        = ").append(this._tabids.length).append("\n");
        for (int i = 0; i < this._tabids.length; ++i) {
            buffer.append("    .element_").append(i).append(" = ").append(this._tabids[i]).append("\n");
        }
        buffer.append("[/TABID]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        for (final short tabid : this._tabids) {
            out.writeShort(tabid);
        }
    }
    
    @Override
    protected int getDataSize() {
        return this._tabids.length * 2;
    }
    
    @Override
    public short getSid() {
        return 317;
    }
    
    @Override
    public TabIdRecord copy() {
        return new TabIdRecord(this);
    }
    
    static {
        EMPTY_SHORT_ARRAY = new short[0];
    }
}
