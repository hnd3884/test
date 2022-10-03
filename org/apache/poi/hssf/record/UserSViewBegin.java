package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.HexDump;
import java.util.Locale;
import org.apache.poi.util.LittleEndianOutput;

public final class UserSViewBegin extends StandardRecord
{
    public static final short sid = 426;
    private byte[] _rawData;
    
    public UserSViewBegin(final UserSViewBegin other) {
        super(other);
        this._rawData = (byte[])((other._rawData == null) ? null : ((byte[])other._rawData.clone()));
    }
    
    public UserSViewBegin(final byte[] data) {
        this._rawData = data;
    }
    
    public UserSViewBegin(final RecordInputStream in) {
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
        return 426;
    }
    
    public byte[] getGuid() {
        final byte[] guid = new byte[16];
        System.arraycopy(this._rawData, 0, guid, 0, guid.length);
        return guid;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[").append("USERSVIEWBEGIN").append("] (0x");
        sb.append(Integer.toHexString(426).toUpperCase(Locale.ROOT)).append(")\n");
        sb.append("  rawData=").append(HexDump.toHex(this._rawData)).append("\n");
        sb.append("[/").append("USERSVIEWBEGIN").append("]\n");
        return sb.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public UserSViewBegin clone() {
        return this.copy();
    }
    
    @Override
    public UserSViewBegin copy() {
        return new UserSViewBegin(this);
    }
}
