package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class ViewFieldsRecord extends StandardRecord
{
    public static final short sid = 177;
    private static final int STRING_NOT_PRESENT_LEN = 65535;
    private static final int BASE_SIZE = 10;
    private int _sxaxis;
    private int _cSub;
    private int _grbitSub;
    private int _cItm;
    private String _name;
    
    public ViewFieldsRecord(final ViewFieldsRecord other) {
        super(other);
        this._sxaxis = other._sxaxis;
        this._cSub = other._cSub;
        this._grbitSub = other._grbitSub;
        this._cItm = other._cItm;
        this._name = other._name;
    }
    
    public ViewFieldsRecord(final RecordInputStream in) {
        this._sxaxis = in.readShort();
        this._cSub = in.readShort();
        this._grbitSub = in.readShort();
        this._cItm = in.readShort();
        final int cchName = in.readUShort();
        if (cchName != 65535) {
            final int flag = in.readByte();
            if ((flag & 0x1) != 0x0) {
                this._name = in.readUnicodeLEString(cchName);
            }
            else {
                this._name = in.readCompressedUnicode(cchName);
            }
        }
    }
    
    @Override
    protected void serialize(final LittleEndianOutput out) {
        out.writeShort(this._sxaxis);
        out.writeShort(this._cSub);
        out.writeShort(this._grbitSub);
        out.writeShort(this._cItm);
        if (this._name != null) {
            StringUtil.writeUnicodeString(out, this._name);
        }
        else {
            out.writeShort(65535);
        }
    }
    
    @Override
    protected int getDataSize() {
        if (this._name == null) {
            return 10;
        }
        return 11 + this._name.length() * (StringUtil.hasMultibyte(this._name) ? 2 : 1);
    }
    
    @Override
    public short getSid() {
        return 177;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SXVD]\n");
        buffer.append("    .sxaxis    = ").append(HexDump.shortToHex(this._sxaxis)).append('\n');
        buffer.append("    .cSub      = ").append(HexDump.shortToHex(this._cSub)).append('\n');
        buffer.append("    .grbitSub  = ").append(HexDump.shortToHex(this._grbitSub)).append('\n');
        buffer.append("    .cItm      = ").append(HexDump.shortToHex(this._cItm)).append('\n');
        buffer.append("    .name      = ").append(this._name).append('\n');
        buffer.append("[/SXVD]\n");
        return buffer.toString();
    }
    
    @Override
    public ViewFieldsRecord copy() {
        return new ViewFieldsRecord(this);
    }
    
    private enum Axis
    {
        NO_AXIS(0), 
        ROW(1), 
        COLUMN(2), 
        PAGE(4), 
        DATA(8);
        
        int id;
        
        private Axis(final int id) {
            this.id = id;
        }
    }
}
