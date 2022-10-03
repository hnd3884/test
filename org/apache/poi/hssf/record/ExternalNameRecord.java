package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.constant.ConstantValueParser;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ss.formula.Formula;

public final class ExternalNameRecord extends StandardRecord
{
    public static final short sid = 35;
    private static final int OPT_BUILTIN_NAME = 1;
    private static final int OPT_AUTOMATIC_LINK = 2;
    private static final int OPT_PICTURE_LINK = 4;
    private static final int OPT_STD_DOCUMENT_NAME = 8;
    private static final int OPT_OLE_LINK = 16;
    private static final int OPT_ICONIFIED_PICTURE_LINK = 32768;
    private short field_1_option_flag;
    private short field_2_ixals;
    private short field_3_not_used;
    private String field_4_name;
    private Formula field_5_name_definition;
    private Object[] _ddeValues;
    private int _nColumns;
    private int _nRows;
    
    public ExternalNameRecord() {
        this.field_2_ixals = 0;
    }
    
    public ExternalNameRecord(final ExternalNameRecord other) {
        super(other);
        this.field_1_option_flag = other.field_1_option_flag;
        this.field_2_ixals = other.field_2_ixals;
        this.field_3_not_used = other.field_3_not_used;
        this.field_4_name = other.field_4_name;
        this.field_5_name_definition = ((other.field_5_name_definition == null) ? null : other.field_5_name_definition.copy());
        this._ddeValues = (Object[])((other._ddeValues == null) ? null : ((Object[])other._ddeValues.clone()));
        this._nColumns = other._nColumns;
        this._nRows = other._nRows;
    }
    
    public ExternalNameRecord(final RecordInputStream in) {
        this.field_1_option_flag = in.readShort();
        this.field_2_ixals = in.readShort();
        this.field_3_not_used = in.readShort();
        final int numChars = in.readUByte();
        this.field_4_name = StringUtil.readUnicodeString(in, numChars);
        if (!this.isOLELink() && !this.isStdDocumentNameIdentifier()) {
            if (this.isAutomaticLink()) {
                if (in.available() > 0) {
                    final int nColumns = in.readUByte() + 1;
                    final int nRows = in.readShort() + 1;
                    final int totalCount = nRows * nColumns;
                    this._ddeValues = ConstantValueParser.parse(in, totalCount);
                    this._nColumns = nColumns;
                    this._nRows = nRows;
                }
            }
            else {
                final int formulaLen = in.readUShort();
                this.field_5_name_definition = Formula.read(formulaLen, in);
            }
        }
    }
    
    public boolean isBuiltInName() {
        return (this.field_1_option_flag & 0x1) != 0x0;
    }
    
    public boolean isAutomaticLink() {
        return (this.field_1_option_flag & 0x2) != 0x0;
    }
    
    public boolean isPicureLink() {
        return (this.field_1_option_flag & 0x4) != 0x0;
    }
    
    public boolean isStdDocumentNameIdentifier() {
        return (this.field_1_option_flag & 0x8) != 0x0;
    }
    
    public boolean isOLELink() {
        return (this.field_1_option_flag & 0x10) != 0x0;
    }
    
    public boolean isIconifiedPictureLink() {
        return (this.field_1_option_flag & 0x8000) != 0x0;
    }
    
    public String getText() {
        return this.field_4_name;
    }
    
    public void setText(final String str) {
        this.field_4_name = str;
    }
    
    public short getIx() {
        return this.field_2_ixals;
    }
    
    public void setIx(final short ix) {
        this.field_2_ixals = ix;
    }
    
    public Ptg[] getParsedExpression() {
        return Formula.getTokens(this.field_5_name_definition);
    }
    
    public void setParsedExpression(final Ptg[] ptgs) {
        this.field_5_name_definition = Formula.create(ptgs);
    }
    
    @Override
    protected int getDataSize() {
        int result = 6;
        result += StringUtil.getEncodedSize(this.field_4_name) - 1;
        if (!this.isOLELink() && !this.isStdDocumentNameIdentifier()) {
            if (this.isAutomaticLink()) {
                if (this._ddeValues != null) {
                    result += 3;
                    result += ConstantValueParser.getEncodedSize(this._ddeValues);
                }
            }
            else {
                result += this.field_5_name_definition.getEncodedSize();
            }
        }
        return result;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_option_flag);
        out.writeShort(this.field_2_ixals);
        out.writeShort(this.field_3_not_used);
        out.writeByte(this.field_4_name.length());
        StringUtil.writeUnicodeStringFlagAndData(out, this.field_4_name);
        if (!this.isOLELink() && !this.isStdDocumentNameIdentifier()) {
            if (this.isAutomaticLink()) {
                if (this._ddeValues != null) {
                    out.writeByte(this._nColumns - 1);
                    out.writeShort(this._nRows - 1);
                    ConstantValueParser.encode(out, this._ddeValues);
                }
            }
            else {
                this.field_5_name_definition.serialize(out);
            }
        }
    }
    
    @Override
    public short getSid() {
        return 35;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[EXTERNALNAME]\n");
        sb.append("    .options = ").append(this.field_1_option_flag).append("\n");
        sb.append("    .ix      = ").append(this.field_2_ixals).append("\n");
        sb.append("    .name    = ").append(this.field_4_name).append("\n");
        if (this.field_5_name_definition != null) {
            final Ptg[] tokens;
            final Ptg[] ptgs = tokens = this.field_5_name_definition.getTokens();
            for (final Ptg ptg : tokens) {
                sb.append("    .namedef = ").append(ptg).append(ptg.getRVAType()).append("\n");
            }
        }
        sb.append("[/EXTERNALNAME]\n");
        return sb.toString();
    }
    
    @Override
    public ExternalNameRecord copy() {
        return new ExternalNameRecord(this);
    }
}
