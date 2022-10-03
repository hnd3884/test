package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.StandardRecord;

public class ChartTitleFormatRecord extends StandardRecord
{
    public static final short sid = 4176;
    private final CTFormat[] _formats;
    
    public ChartTitleFormatRecord(final ChartTitleFormatRecord other) {
        super(other);
        this._formats = Stream.of(other._formats).map((Function<? super CTFormat, ?>)CTFormat::new).toArray(CTFormat[]::new);
    }
    
    public ChartTitleFormatRecord(final RecordInputStream in) {
        final int nRecs = in.readUShort();
        this._formats = new CTFormat[nRecs];
        for (int i = 0; i < nRecs; ++i) {
            this._formats[i] = new CTFormat(in);
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._formats.length);
        for (int i = 0; i < this._formats.length; ++i) {
            this._formats[i].serialize(out);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 2 + 4 * this._formats.length;
    }
    
    @Override
    public short getSid() {
        return 4176;
    }
    
    public int getFormatCount() {
        return this._formats.length;
    }
    
    public void modifyFormatRun(final short oldPos, final short newLen) {
        int shift = 0;
        for (int i = 0; i < this._formats.length; ++i) {
            final CTFormat ctf = this._formats[i];
            if (shift != 0) {
                ctf.setOffset(ctf.getOffset() + shift);
            }
            else if (oldPos == ctf.getOffset() && i < this._formats.length - 1) {
                final CTFormat nextCTF = this._formats[i + 1];
                shift = newLen - (nextCTF.getOffset() - ctf.getOffset());
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CHARTTITLEFORMAT]\n");
        buffer.append("    .format_runs       = ").append(this._formats.length).append("\n");
        for (int i = 0; i < this._formats.length; ++i) {
            final CTFormat ctf = this._formats[i];
            buffer.append("       .char_offset= ").append(ctf.getOffset());
            buffer.append(",.fontidx= ").append(ctf.getFontIndex());
            buffer.append("\n");
        }
        buffer.append("[/CHARTTITLEFORMAT]\n");
        return buffer.toString();
    }
    
    @Override
    public ChartTitleFormatRecord copy() {
        return new ChartTitleFormatRecord(this);
    }
    
    private static final class CTFormat
    {
        public static final int ENCODED_SIZE = 4;
        private int _offset;
        private int _fontIndex;
        
        public CTFormat(final CTFormat other) {
            this._offset = other._offset;
            this._fontIndex = other._fontIndex;
        }
        
        public CTFormat(final RecordInputStream in) {
            this._offset = in.readShort();
            this._fontIndex = in.readShort();
        }
        
        public int getOffset() {
            return this._offset;
        }
        
        public void setOffset(final int newOff) {
            this._offset = newOff;
        }
        
        public int getFontIndex() {
            return this._fontIndex;
        }
        
        public void serialize(final LittleEndianOutput out) {
            out.writeShort(this._offset);
            out.writeShort(this._fontIndex);
        }
    }
}
