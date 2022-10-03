package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellRangeAddressList;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.ss.util.CellRangeAddress;

public final class MergeCellsRecord extends StandardRecord
{
    public static final short sid = 229;
    private final CellRangeAddress[] _regions;
    private final int _startIndex;
    private final int _numberOfRegions;
    
    public MergeCellsRecord(final MergeCellsRecord other) {
        super(other);
        this._regions = (CellRangeAddress[])((other._regions == null) ? null : ((CellRangeAddress[])Stream.of(other._regions).map((Function<? super CellRangeAddress, ?>)CellRangeAddress::copy).toArray(CellRangeAddress[]::new)));
        this._startIndex = other._startIndex;
        this._numberOfRegions = other._numberOfRegions;
    }
    
    public MergeCellsRecord(final CellRangeAddress[] regions, final int startIndex, final int numberOfRegions) {
        this._regions = regions;
        this._startIndex = startIndex;
        this._numberOfRegions = numberOfRegions;
    }
    
    public MergeCellsRecord(final RecordInputStream in) {
        final int nRegions = in.readUShort();
        final CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        for (int i = 0; i < nRegions; ++i) {
            cras[i] = new CellRangeAddress(in);
        }
        this._numberOfRegions = nRegions;
        this._startIndex = 0;
        this._regions = cras;
    }
    
    public short getNumAreas() {
        return (short)this._numberOfRegions;
    }
    
    public CellRangeAddress getAreaAt(final int index) {
        return this._regions[this._startIndex + index];
    }
    
    @Override
    protected int getDataSize() {
        return CellRangeAddressList.getEncodedSize(this._numberOfRegions);
    }
    
    @Override
    public short getSid() {
        return 229;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._numberOfRegions);
        for (int i = 0; i < this._numberOfRegions; ++i) {
            this._regions[this._startIndex + i].serialize(out);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder retval = new StringBuilder();
        retval.append("[MERGEDCELLS]").append("\n");
        retval.append("     .numregions =").append(this.getNumAreas()).append("\n");
        for (int k = 0; k < this._numberOfRegions; ++k) {
            final CellRangeAddress r = this._regions[this._startIndex + k];
            retval.append("     .rowfrom =").append(r.getFirstRow()).append("\n");
            retval.append("     .rowto   =").append(r.getLastRow()).append("\n");
            retval.append("     .colfrom =").append(r.getFirstColumn()).append("\n");
            retval.append("     .colto   =").append(r.getLastColumn()).append("\n");
        }
        retval.append("[MERGEDCELLS]").append("\n");
        return retval.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public MergeCellsRecord clone() {
        return this.copy();
    }
    
    @Override
    public MergeCellsRecord copy() {
        return new MergeCellsRecord(this);
    }
}
