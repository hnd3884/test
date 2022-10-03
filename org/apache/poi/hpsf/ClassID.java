package org.apache.poi.hpsf;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import java.util.Arrays;
import org.apache.poi.common.Duplicatable;

public class ClassID implements Duplicatable
{
    @Deprecated
    public static final ClassID OLE10_PACKAGE;
    @Deprecated
    public static final ClassID PPT_SHOW;
    @Deprecated
    public static final ClassID XLS_WORKBOOK;
    @Deprecated
    public static final ClassID TXT_ONLY;
    @Deprecated
    public static final ClassID EXCEL_V3;
    @Deprecated
    public static final ClassID EXCEL_V3_CHART;
    @Deprecated
    public static final ClassID EXCEL_V3_MACRO;
    @Deprecated
    public static final ClassID EXCEL95;
    @Deprecated
    public static final ClassID EXCEL95_CHART;
    @Deprecated
    public static final ClassID EXCEL97;
    @Deprecated
    public static final ClassID EXCEL97_CHART;
    @Deprecated
    public static final ClassID EXCEL2003;
    @Deprecated
    public static final ClassID EXCEL2007;
    @Deprecated
    public static final ClassID EXCEL2007_MACRO;
    @Deprecated
    public static final ClassID EXCEL2007_XLSB;
    @Deprecated
    public static final ClassID EXCEL2010;
    @Deprecated
    public static final ClassID EXCEL2010_CHART;
    @Deprecated
    public static final ClassID EXCEL2010_ODS;
    @Deprecated
    public static final ClassID WORD95;
    @Deprecated
    public static final ClassID WORD97;
    @Deprecated
    public static final ClassID WORD2007;
    @Deprecated
    public static final ClassID WORD2007_MACRO;
    @Deprecated
    public static final ClassID POWERPOINT97;
    @Deprecated
    public static final ClassID POWERPOINT95;
    @Deprecated
    public static final ClassID POWERPOINT2007;
    @Deprecated
    public static final ClassID POWERPOINT2007_MACRO;
    @Deprecated
    public static final ClassID EQUATION30;
    public static final int LENGTH = 16;
    private final byte[] bytes;
    
    public ClassID(final byte[] src, final int offset) {
        this.bytes = new byte[16];
        this.read(src, offset);
    }
    
    public ClassID() {
        Arrays.fill(this.bytes = new byte[16], (byte)0);
    }
    
    public ClassID(final ClassID other) {
        this.bytes = new byte[16];
        System.arraycopy(other.bytes, 0, this.bytes, 0, this.bytes.length);
    }
    
    public ClassID(final String externalForm) {
        this.bytes = new byte[16];
        final String clsStr = externalForm.replaceAll("[{}-]", "");
        for (int i = 0; i < clsStr.length(); i += 2) {
            this.bytes[i / 2] = (byte)Integer.parseInt(clsStr.substring(i, i + 2), 16);
        }
    }
    
    public ClassID(final LittleEndianInput lei) {
        this.bytes = new byte[16];
        final byte[] buf = this.bytes.clone();
        lei.readFully(buf);
        this.read(buf, 0);
    }
    
    public int length() {
        return 16;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public void setBytes(final byte[] bytes) {
        System.arraycopy(bytes, 0, this.bytes, 0, 16);
    }
    
    public byte[] read(final byte[] src, final int offset) {
        this.bytes[0] = src[3 + offset];
        this.bytes[1] = src[2 + offset];
        this.bytes[2] = src[1 + offset];
        this.bytes[3] = src[0 + offset];
        this.bytes[4] = src[5 + offset];
        this.bytes[5] = src[4 + offset];
        this.bytes[6] = src[7 + offset];
        this.bytes[7] = src[6 + offset];
        System.arraycopy(src, 8 + offset, this.bytes, 8, 8);
        return this.bytes;
    }
    
    public void write(final byte[] dst, final int offset) throws ArrayStoreException {
        if (dst.length < 16) {
            throw new ArrayStoreException("Destination byte[] must have room for at least 16 bytes, but has a length of only " + dst.length + ".");
        }
        dst[0 + offset] = this.bytes[3];
        dst[1 + offset] = this.bytes[2];
        dst[2 + offset] = this.bytes[1];
        dst[3 + offset] = this.bytes[0];
        dst[4 + offset] = this.bytes[5];
        dst[5 + offset] = this.bytes[4];
        dst[6 + offset] = this.bytes[7];
        dst[7 + offset] = this.bytes[6];
        System.arraycopy(this.bytes, 8, dst, 8 + offset, 8);
    }
    
    public void write(final LittleEndianOutput leo) {
        final byte[] buf = this.bytes.clone();
        this.write(buf, 0);
        leo.write(buf);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ClassID && Arrays.equals(this.bytes, ((ClassID)o).bytes);
    }
    
    public boolean equalsInverted(final ClassID o) {
        return o.bytes[0] == this.bytes[3] && o.bytes[1] == this.bytes[2] && o.bytes[2] == this.bytes[1] && o.bytes[3] == this.bytes[0] && o.bytes[4] == this.bytes[5] && o.bytes[5] == this.bytes[4] && o.bytes[6] == this.bytes[7] && o.bytes[7] == this.bytes[6] && o.bytes[8] == this.bytes[8] && o.bytes[9] == this.bytes[9] && o.bytes[10] == this.bytes[10] && o.bytes[11] == this.bytes[11] && o.bytes[12] == this.bytes[12] && o.bytes[13] == this.bytes[13] && o.bytes[14] == this.bytes[14] && o.bytes[15] == this.bytes[15];
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        final String hex = Hex.encodeHexString(this.bytes, false);
        return "{" + hex.substring(0, 8) + "-" + hex.substring(8, 12) + "-" + hex.substring(12, 16) + "-" + hex.substring(16, 20) + "-" + hex.substring(20) + "}";
    }
    
    @Override
    public ClassID copy() {
        return new ClassID(this);
    }
    
    static {
        OLE10_PACKAGE = ClassIDPredefined.OLE_V1_PACKAGE.getClassID();
        PPT_SHOW = ClassIDPredefined.POWERPOINT_V8.getClassID();
        XLS_WORKBOOK = ClassIDPredefined.EXCEL_V7_WORKBOOK.getClassID();
        TXT_ONLY = ClassIDPredefined.TXT_ONLY.getClassID();
        EXCEL_V3 = ClassIDPredefined.EXCEL_V3.getClassID();
        EXCEL_V3_CHART = ClassIDPredefined.EXCEL_V3_CHART.getClassID();
        EXCEL_V3_MACRO = ClassIDPredefined.EXCEL_V3_MACRO.getClassID();
        EXCEL95 = ClassIDPredefined.EXCEL_V7.getClassID();
        EXCEL95_CHART = ClassIDPredefined.EXCEL_V7_CHART.getClassID();
        EXCEL97 = ClassIDPredefined.EXCEL_V8.getClassID();
        EXCEL97_CHART = ClassIDPredefined.EXCEL_V8_CHART.getClassID();
        EXCEL2003 = ClassIDPredefined.EXCEL_V11.getClassID();
        EXCEL2007 = ClassIDPredefined.EXCEL_V12.getClassID();
        EXCEL2007_MACRO = ClassIDPredefined.EXCEL_V12_MACRO.getClassID();
        EXCEL2007_XLSB = ClassIDPredefined.EXCEL_V12_XLSB.getClassID();
        EXCEL2010 = ClassIDPredefined.EXCEL_V14.getClassID();
        EXCEL2010_CHART = ClassIDPredefined.EXCEL_V14_CHART.getClassID();
        EXCEL2010_ODS = ClassIDPredefined.EXCEL_V14_ODS.getClassID();
        WORD95 = ClassIDPredefined.WORD_V7.getClassID();
        WORD97 = ClassIDPredefined.WORD_V8.getClassID();
        WORD2007 = ClassIDPredefined.WORD_V12.getClassID();
        WORD2007_MACRO = ClassIDPredefined.WORD_V12_MACRO.getClassID();
        POWERPOINT97 = ClassIDPredefined.POWERPOINT_V8.getClassID();
        POWERPOINT95 = ClassIDPredefined.POWERPOINT_V7.getClassID();
        POWERPOINT2007 = ClassIDPredefined.POWERPOINT_V12.getClassID();
        POWERPOINT2007_MACRO = ClassIDPredefined.POWERPOINT_V12_MACRO.getClassID();
        EQUATION30 = ClassIDPredefined.EQUATION_V3.getClassID();
    }
}
