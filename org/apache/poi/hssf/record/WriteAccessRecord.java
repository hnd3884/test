package org.apache.poi.hssf.record;

import java.util.Arrays;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public final class WriteAccessRecord extends StandardRecord
{
    public static final short sid = 92;
    private static final byte PAD_CHAR = 32;
    private static final int DATA_SIZE = 112;
    private static final byte[] PADDING;
    private String field_1_username;
    
    public WriteAccessRecord() {
        this.setUsername("");
    }
    
    public WriteAccessRecord(final WriteAccessRecord other) {
        super(other);
        this.field_1_username = other.field_1_username;
    }
    
    public WriteAccessRecord(final RecordInputStream in) {
        if (in.remaining() > 112) {
            throw new RecordFormatException("Expected data size (112) but got (" + in.remaining() + ")");
        }
        final int nChars = in.readUShort();
        final int is16BitFlag = in.readUByte();
        if (nChars > 112 || (is16BitFlag & 0xFE) != 0x0) {
            final byte[] data = new byte[3 + in.remaining()];
            LittleEndian.putUShort(data, 0, nChars);
            LittleEndian.putByte(data, 2, is16BitFlag);
            in.readFully(data, 3, data.length - 3);
            final String rawValue = new String(data, StringUtil.UTF8);
            this.setUsername(rawValue.trim());
            return;
        }
        String rawText;
        if ((is16BitFlag & 0x1) == 0x0) {
            rawText = StringUtil.readCompressedUnicode(in, nChars);
        }
        else {
            rawText = StringUtil.readUnicodeLE(in, nChars);
        }
        this.field_1_username = rawText.trim();
        for (int padSize = in.remaining(); padSize > 0; --padSize) {
            in.readUByte();
        }
    }
    
    public void setUsername(final String username) {
        final boolean is16bit = StringUtil.hasMultibyte(username);
        final int encodedByteCount = 3 + username.length() * (is16bit ? 2 : 1);
        final int paddingSize = 112 - encodedByteCount;
        if (paddingSize < 0) {
            throw new IllegalArgumentException("Name is too long: " + username);
        }
        this.field_1_username = username;
    }
    
    public String getUsername() {
        return this.field_1_username;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[WRITEACCESS]\n");
        buffer.append("    .name = ").append(this.field_1_username).append("\n");
        buffer.append("[/WRITEACCESS]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        final String username = this.getUsername();
        final boolean is16bit = StringUtil.hasMultibyte(username);
        out.writeShort(username.length());
        out.writeByte(is16bit ? 1 : 0);
        if (is16bit) {
            StringUtil.putUnicodeLE(username, out);
        }
        else {
            StringUtil.putCompressedUnicode(username, out);
        }
        final int encodedByteCount = 3 + username.length() * (is16bit ? 2 : 1);
        final int paddingSize = 112 - encodedByteCount;
        out.write(WriteAccessRecord.PADDING, 0, paddingSize);
    }
    
    @Override
    protected int getDataSize() {
        return 112;
    }
    
    @Override
    public short getSid() {
        return 92;
    }
    
    @Override
    public WriteAccessRecord copy() {
        return new WriteAccessRecord(this);
    }
    
    static {
        Arrays.fill(PADDING = new byte[112], (byte)32);
    }
}
