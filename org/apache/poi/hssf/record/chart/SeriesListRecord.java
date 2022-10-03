package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import java.util.Arrays;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class SeriesListRecord extends StandardRecord
{
    public static final short sid = 4118;
    private short[] field_1_seriesNumbers;
    
    public SeriesListRecord(final SeriesListRecord other) {
        super(other);
        this.field_1_seriesNumbers = (short[])((other.field_1_seriesNumbers == null) ? null : ((short[])other.field_1_seriesNumbers.clone()));
    }
    
    public SeriesListRecord(final short[] seriesNumbers) {
        this.field_1_seriesNumbers = (short[])((seriesNumbers == null) ? null : ((short[])seriesNumbers.clone()));
    }
    
    public SeriesListRecord(final RecordInputStream in) {
        final int nItems = in.readUShort();
        final short[] ss = new short[nItems];
        for (int i = 0; i < nItems; ++i) {
            ss[i] = in.readShort();
        }
        this.field_1_seriesNumbers = ss;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SERIESLIST]\n");
        buffer.append("    .seriesNumbers= ").append(" (").append(Arrays.toString(this.getSeriesNumbers())).append(" )");
        buffer.append("\n");
        buffer.append("[/SERIESLIST]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        final int nItems = this.field_1_seriesNumbers.length;
        out.writeShort(nItems);
        for (int i = 0; i < nItems; ++i) {
            out.writeShort(this.field_1_seriesNumbers[i]);
        }
    }
    
    @Override
    protected int getDataSize() {
        return this.field_1_seriesNumbers.length * 2 + 2;
    }
    
    @Override
    public short getSid() {
        return 4118;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SeriesListRecord clone() {
        return this.copy();
    }
    
    @Override
    public SeriesListRecord copy() {
        return new SeriesListRecord(this);
    }
    
    public short[] getSeriesNumbers() {
        return this.field_1_seriesNumbers;
    }
}
