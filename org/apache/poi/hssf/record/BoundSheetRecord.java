package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;

public final class BoundSheetRecord extends StandardRecord
{
    public static final short sid = 133;
    private static final BitField hiddenFlag;
    private static final BitField veryHiddenFlag;
    private int field_1_position_of_BOF;
    private int field_2_option_flags;
    private int field_4_isMultibyteUnicode;
    private String field_5_sheetname;
    
    public BoundSheetRecord(final String sheetname) {
        this.field_2_option_flags = 0;
        this.setSheetname(sheetname);
    }
    
    public BoundSheetRecord(final BoundSheetRecord other) {
        super(other);
        this.field_1_position_of_BOF = other.field_1_position_of_BOF;
        this.field_2_option_flags = other.field_2_option_flags;
        this.field_4_isMultibyteUnicode = other.field_4_isMultibyteUnicode;
        this.field_5_sheetname = other.field_5_sheetname;
    }
    
    public BoundSheetRecord(final RecordInputStream in) {
        final byte[] buf = new byte[4];
        in.readPlain(buf, 0, buf.length);
        this.field_1_position_of_BOF = LittleEndian.getInt(buf);
        this.field_2_option_flags = in.readUShort();
        final int field_3_sheetname_length = in.readUByte();
        this.field_4_isMultibyteUnicode = in.readByte();
        if (this.isMultibyte()) {
            this.field_5_sheetname = in.readUnicodeLEString(field_3_sheetname_length);
        }
        else {
            this.field_5_sheetname = in.readCompressedUnicode(field_3_sheetname_length);
        }
    }
    
    public void setPositionOfBof(final int pos) {
        this.field_1_position_of_BOF = pos;
    }
    
    public void setSheetname(final String sheetName) {
        WorkbookUtil.validateSheetName(sheetName);
        this.field_5_sheetname = sheetName;
        this.field_4_isMultibyteUnicode = (StringUtil.hasMultibyte(sheetName) ? 1 : 0);
    }
    
    public int getPositionOfBof() {
        return this.field_1_position_of_BOF;
    }
    
    private boolean isMultibyte() {
        return (this.field_4_isMultibyteUnicode & 0x1) != 0x0;
    }
    
    public String getSheetname() {
        return this.field_5_sheetname;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[BOUNDSHEET]\n");
        buffer.append("    .bof        = ").append(HexDump.intToHex(this.getPositionOfBof())).append("\n");
        buffer.append("    .options    = ").append(HexDump.shortToHex(this.field_2_option_flags)).append("\n");
        buffer.append("    .unicodeflag= ").append(HexDump.byteToHex(this.field_4_isMultibyteUnicode)).append("\n");
        buffer.append("    .sheetname  = ").append(this.field_5_sheetname).append("\n");
        buffer.append("[/BOUNDSHEET]\n");
        return buffer.toString();
    }
    
    @Override
    protected int getDataSize() {
        return 8 + this.field_5_sheetname.length() * (this.isMultibyte() ? 2 : 1);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.getPositionOfBof());
        out.writeShort(this.field_2_option_flags);
        final String name = this.field_5_sheetname;
        out.writeByte(name.length());
        out.writeByte(this.field_4_isMultibyteUnicode);
        if (this.isMultibyte()) {
            StringUtil.putUnicodeLE(name, out);
        }
        else {
            StringUtil.putCompressedUnicode(name, out);
        }
    }
    
    @Override
    public short getSid() {
        return 133;
    }
    
    public boolean isHidden() {
        return BoundSheetRecord.hiddenFlag.isSet(this.field_2_option_flags);
    }
    
    public void setHidden(final boolean hidden) {
        this.field_2_option_flags = BoundSheetRecord.hiddenFlag.setBoolean(this.field_2_option_flags, hidden);
    }
    
    public boolean isVeryHidden() {
        return BoundSheetRecord.veryHiddenFlag.isSet(this.field_2_option_flags);
    }
    
    public void setVeryHidden(final boolean veryHidden) {
        this.field_2_option_flags = BoundSheetRecord.veryHiddenFlag.setBoolean(this.field_2_option_flags, veryHidden);
    }
    
    public static BoundSheetRecord[] orderByBofPosition(final List<BoundSheetRecord> boundSheetRecords) {
        final BoundSheetRecord[] bsrs = new BoundSheetRecord[boundSheetRecords.size()];
        boundSheetRecords.toArray(bsrs);
        Arrays.sort(bsrs, BoundSheetRecord::compareRecords);
        return bsrs;
    }
    
    private static int compareRecords(final BoundSheetRecord bsr1, final BoundSheetRecord bsr2) {
        return bsr1.getPositionOfBof() - bsr2.getPositionOfBof();
    }
    
    @Override
    public BoundSheetRecord copy() {
        return new BoundSheetRecord(this);
    }
    
    static {
        hiddenFlag = BitFieldFactory.getInstance(1);
        veryHiddenFlag = BitFieldFactory.getInstance(2);
    }
}
