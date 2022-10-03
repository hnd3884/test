package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import org.apache.poi.util.RecordFormatException;

public final class OldSheetRecord
{
    private static final int MAX_RECORD_LENGTH = 100000;
    public static final short sid = 133;
    private int field_1_position_of_BOF;
    private int field_2_visibility;
    private int field_3_type;
    private byte[] field_5_sheetname;
    private CodepageRecord codepage;
    
    public OldSheetRecord(final RecordInputStream in) {
        this.field_1_position_of_BOF = in.readInt();
        this.field_2_visibility = in.readUByte();
        this.field_3_type = in.readUByte();
        final int field_4_sheetname_length = in.readUByte();
        if (field_4_sheetname_length > 0) {
            in.mark(1);
            final byte b = in.readByte();
            if (b != 0) {
                try {
                    in.reset();
                }
                catch (final IOException e) {
                    throw new RecordFormatException(e);
                }
            }
        }
        in.read(this.field_5_sheetname = IOUtils.safelyAllocate(field_4_sheetname_length, 100000), 0, field_4_sheetname_length);
    }
    
    public void setCodePage(final CodepageRecord codepage) {
        this.codepage = codepage;
    }
    
    public short getSid() {
        return 133;
    }
    
    public int getPositionOfBof() {
        return this.field_1_position_of_BOF;
    }
    
    public String getSheetname() {
        return OldStringRecord.getString(this.field_5_sheetname, this.codepage);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[BOUNDSHEET]\n");
        buffer.append("    .bof        = ").append(HexDump.intToHex(this.getPositionOfBof())).append("\n");
        buffer.append("    .visibility = ").append(HexDump.shortToHex(this.field_2_visibility)).append("\n");
        buffer.append("    .type       = ").append(HexDump.byteToHex(this.field_3_type)).append("\n");
        buffer.append("    .sheetname  = ").append(this.getSheetname()).append("\n");
        buffer.append("[/BOUNDSHEET]\n");
        return buffer.toString();
    }
}
