package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.common.Duplicatable;

public abstract class SubRecord implements Duplicatable
{
    private static final int MAX_RECORD_LENGTH = 1000000;
    
    protected SubRecord() {
    }
    
    protected SubRecord(final SubRecord other) {
    }
    
    public static SubRecord createSubRecord(final LittleEndianInput in, final int cmoOt) {
        final int sid = in.readUShort();
        final int secondUShort = in.readUShort();
        switch (sid) {
            case 21: {
                return new CommonObjectDataSubRecord(in, secondUShort);
            }
            case 9: {
                return new EmbeddedObjectRefSubRecord(in, secondUShort);
            }
            case 6: {
                return new GroupMarkerSubRecord(in, secondUShort);
            }
            case 0: {
                return new EndSubRecord(in, secondUShort);
            }
            case 13: {
                return new NoteStructureSubRecord(in, secondUShort);
            }
            case 19: {
                return new LbsDataSubRecord(in, secondUShort, cmoOt);
            }
            case 12: {
                return new FtCblsSubRecord(in, secondUShort);
            }
            case 8: {
                return new FtPioGrbitSubRecord(in, secondUShort);
            }
            case 7: {
                return new FtCfSubRecord(in, secondUShort);
            }
            default: {
                return new UnknownSubRecord(in, sid, secondUShort);
            }
        }
    }
    
    protected abstract int getDataSize();
    
    public byte[] serialize() {
        final int size = this.getDataSize() + 4;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        this.serialize(new LittleEndianOutputStream(baos));
        if (baos.size() != size) {
            throw new RuntimeException("write size mismatch");
        }
        return baos.toByteArray();
    }
    
    public abstract void serialize(final LittleEndianOutput p0);
    
    public boolean isTerminating() {
        return false;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public abstract SubRecord clone();
    
    @Override
    public abstract SubRecord copy();
    
    private static final class UnknownSubRecord extends SubRecord
    {
        private final int _sid;
        private final byte[] _data;
        
        public UnknownSubRecord(final LittleEndianInput in, final int sid, final int size) {
            this._sid = sid;
            final byte[] buf = IOUtils.safelyAllocate(size, 1000000);
            in.readFully(buf);
            this._data = buf;
        }
        
        @Override
        protected int getDataSize() {
            return this._data.length;
        }
        
        @Override
        public void serialize(final LittleEndianOutput out) {
            out.writeShort(this._sid);
            out.writeShort(this._data.length);
            out.write(this._data);
        }
        
        @Deprecated
        @Removal(version = "5.0.0")
        @Override
        public UnknownSubRecord clone() {
            return this.copy();
        }
        
        @Override
        public UnknownSubRecord copy() {
            return this;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append("sid=").append(HexDump.shortToHex(this._sid));
            sb.append(" size=").append(this._data.length);
            sb.append(" : ").append(HexDump.toHex(this._data));
            sb.append("]\n");
            return sb.toString();
        }
    }
}
