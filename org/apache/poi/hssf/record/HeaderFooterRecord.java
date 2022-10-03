package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.HexDump;
import java.util.Locale;
import java.util.Arrays;
import org.apache.poi.util.LittleEndianOutput;

public final class HeaderFooterRecord extends StandardRecord
{
    public static final short sid = 2204;
    private static final byte[] BLANK_GUID;
    private byte[] _rawData;
    
    public HeaderFooterRecord(final byte[] data) {
        this._rawData = data;
    }
    
    public HeaderFooterRecord(final HeaderFooterRecord other) {
        super(other);
        this._rawData = (byte[])((other._rawData == null) ? null : ((byte[])other._rawData.clone()));
    }
    
    public HeaderFooterRecord(final RecordInputStream in) {
        this._rawData = in.readRemainder();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.write(this._rawData);
    }
    
    @Override
    protected int getDataSize() {
        return this._rawData.length;
    }
    
    @Override
    public short getSid() {
        return 2204;
    }
    
    public byte[] getGuid() {
        final byte[] guid = new byte[16];
        System.arraycopy(this._rawData, 12, guid, 0, guid.length);
        return guid;
    }
    
    public boolean isCurrentSheet() {
        return Arrays.equals(this.getGuid(), HeaderFooterRecord.BLANK_GUID);
    }
    
    @Override
    public String toString() {
        return "[HEADERFOOTER] (0x" + Integer.toHexString(2204).toUpperCase(Locale.ROOT) + ")\n  rawData=" + HexDump.toHex(this._rawData) + "\n[/HEADERFOOTER]\n";
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public HeaderFooterRecord clone() {
        return this.copy();
    }
    
    @Override
    public HeaderFooterRecord copy() {
        return new HeaderFooterRecord(this);
    }
    
    static {
        BLANK_GUID = new byte[16];
    }
}
