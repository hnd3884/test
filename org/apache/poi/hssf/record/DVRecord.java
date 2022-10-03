package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.BitField;
import org.apache.poi.hssf.record.common.UnicodeString;

public final class DVRecord extends StandardRecord
{
    public static final short sid = 446;
    private static final UnicodeString NULL_TEXT_STRING;
    private static final BitField opt_data_type;
    private static final BitField opt_error_style;
    private static final BitField opt_string_list_formula;
    private static final BitField opt_empty_cell_allowed;
    private static final BitField opt_suppress_dropdown_arrow;
    private static final BitField opt_show_prompt_on_cell_selected;
    private static final BitField opt_show_error_on_invalid_value;
    private static final BitField opt_condition_operator;
    private int _option_flags;
    private final UnicodeString _promptTitle;
    private final UnicodeString _errorTitle;
    private final UnicodeString _promptText;
    private final UnicodeString _errorText;
    private short _not_used_1;
    private final Formula _formula1;
    private short _not_used_2;
    private final Formula _formula2;
    private final CellRangeAddressList _regions;
    
    public DVRecord(final DVRecord other) {
        super(other);
        this._not_used_1 = 16352;
        this._not_used_2 = 0;
        this._option_flags = other._option_flags;
        this._promptTitle = other._promptTitle.copy();
        this._errorTitle = other._errorTitle.copy();
        this._promptText = other._promptText.copy();
        this._errorText = other._errorText.copy();
        this._not_used_1 = other._not_used_1;
        this._formula1 = ((other._formula1 == null) ? null : other._formula1.copy());
        this._not_used_2 = other._not_used_2;
        this._formula2 = ((other._formula2 == null) ? null : other._formula2.copy());
        this._regions = ((other._regions == null) ? null : other._regions.copy());
    }
    
    public DVRecord(final int validationType, final int operator, final int errorStyle, final boolean emptyCellAllowed, final boolean suppressDropDownArrow, final boolean isExplicitList, final boolean showPromptBox, final String promptTitle, final String promptText, final boolean showErrorBox, final String errorTitle, final String errorText, final Ptg[] formula1, final Ptg[] formula2, final CellRangeAddressList regions) {
        this._not_used_1 = 16352;
        this._not_used_2 = 0;
        if (promptTitle != null && promptTitle.length() > 32) {
            throw new IllegalStateException("Prompt-title cannot be longer than 32 characters, but had: " + promptTitle);
        }
        if (promptText != null && promptText.length() > 255) {
            throw new IllegalStateException("Prompt-text cannot be longer than 255 characters, but had: " + promptText);
        }
        if (errorTitle != null && errorTitle.length() > 32) {
            throw new IllegalStateException("Error-title cannot be longer than 32 characters, but had: " + errorTitle);
        }
        if (errorText != null && errorText.length() > 255) {
            throw new IllegalStateException("Error-text cannot be longer than 255 characters, but had: " + errorText);
        }
        int flags = 0;
        flags = DVRecord.opt_data_type.setValue(flags, validationType);
        flags = DVRecord.opt_condition_operator.setValue(flags, operator);
        flags = DVRecord.opt_error_style.setValue(flags, errorStyle);
        flags = DVRecord.opt_empty_cell_allowed.setBoolean(flags, emptyCellAllowed);
        flags = DVRecord.opt_suppress_dropdown_arrow.setBoolean(flags, suppressDropDownArrow);
        flags = DVRecord.opt_string_list_formula.setBoolean(flags, isExplicitList);
        flags = DVRecord.opt_show_prompt_on_cell_selected.setBoolean(flags, showPromptBox);
        flags = DVRecord.opt_show_error_on_invalid_value.setBoolean(flags, showErrorBox);
        this._option_flags = flags;
        this._promptTitle = resolveTitleText(promptTitle);
        this._promptText = resolveTitleText(promptText);
        this._errorTitle = resolveTitleText(errorTitle);
        this._errorText = resolveTitleText(errorText);
        this._formula1 = Formula.create(formula1);
        this._formula2 = Formula.create(formula2);
        this._regions = regions;
    }
    
    public DVRecord(final RecordInputStream in) {
        this._not_used_1 = 16352;
        this._not_used_2 = 0;
        this._option_flags = in.readInt();
        this._promptTitle = readUnicodeString(in);
        this._errorTitle = readUnicodeString(in);
        this._promptText = readUnicodeString(in);
        this._errorText = readUnicodeString(in);
        final int field_size_first_formula = in.readUShort();
        this._not_used_1 = in.readShort();
        this._formula1 = Formula.read(field_size_first_formula, in);
        final int field_size_sec_formula = in.readUShort();
        this._not_used_2 = in.readShort();
        this._formula2 = Formula.read(field_size_sec_formula, in);
        this._regions = new CellRangeAddressList(in);
    }
    
    public int getDataType() {
        return DVRecord.opt_data_type.getValue(this._option_flags);
    }
    
    public int getErrorStyle() {
        return DVRecord.opt_error_style.getValue(this._option_flags);
    }
    
    public boolean getListExplicitFormula() {
        return DVRecord.opt_string_list_formula.isSet(this._option_flags);
    }
    
    public boolean getEmptyCellAllowed() {
        return DVRecord.opt_empty_cell_allowed.isSet(this._option_flags);
    }
    
    public boolean getSuppressDropdownArrow() {
        return DVRecord.opt_suppress_dropdown_arrow.isSet(this._option_flags);
    }
    
    public boolean getShowPromptOnCellSelected() {
        return DVRecord.opt_show_prompt_on_cell_selected.isSet(this._option_flags);
    }
    
    public boolean getShowErrorOnInvalidValue() {
        return DVRecord.opt_show_error_on_invalid_value.isSet(this._option_flags);
    }
    
    public int getConditionOperator() {
        return DVRecord.opt_condition_operator.getValue(this._option_flags);
    }
    
    public String getPromptTitle() {
        return resolveTitleString(this._promptTitle);
    }
    
    public String getErrorTitle() {
        return resolveTitleString(this._errorTitle);
    }
    
    public String getPromptText() {
        return resolveTitleString(this._promptText);
    }
    
    public String getErrorText() {
        return resolveTitleString(this._errorText);
    }
    
    public Ptg[] getFormula1() {
        return Formula.getTokens(this._formula1);
    }
    
    public Ptg[] getFormula2() {
        return Formula.getTokens(this._formula2);
    }
    
    public CellRangeAddressList getCellRangeAddress() {
        return this._regions;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[DV]\n");
        sb.append(" options=").append(Integer.toHexString(this._option_flags));
        sb.append(" title-prompt=").append(formatTextTitle(this._promptTitle));
        sb.append(" title-error=").append(formatTextTitle(this._errorTitle));
        sb.append(" text-prompt=").append(formatTextTitle(this._promptText));
        sb.append(" text-error=").append(formatTextTitle(this._errorText));
        sb.append("\n");
        appendFormula(sb, "Formula 1:", this._formula1);
        appendFormula(sb, "Formula 2:", this._formula2);
        sb.append("Regions: ");
        for (int nRegions = this._regions.countRanges(), i = 0; i < nRegions; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            final CellRangeAddress addr = this._regions.getCellRangeAddress(i);
            sb.append('(').append(addr.getFirstRow()).append(',').append(addr.getLastRow());
            sb.append(',').append(addr.getFirstColumn()).append(',').append(addr.getLastColumn()).append(')');
        }
        sb.append("\n");
        sb.append("[/DV]");
        return sb.toString();
    }
    
    private static String formatTextTitle(final UnicodeString us) {
        final String str = us.getString();
        if (str.length() == 1 && str.charAt(0) == '\0') {
            return "'\\0'";
        }
        return str;
    }
    
    private static void appendFormula(final StringBuilder sb, final String label, final Formula f) {
        sb.append(label);
        if (f == null) {
            sb.append("<empty>\n");
            return;
        }
        final Ptg[] ptgs = f.getTokens();
        sb.append('\n');
        for (final Ptg ptg : ptgs) {
            sb.append('\t').append(ptg).append('\n');
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this._option_flags);
        serializeUnicodeString(this._promptTitle, out);
        serializeUnicodeString(this._errorTitle, out);
        serializeUnicodeString(this._promptText, out);
        serializeUnicodeString(this._errorText, out);
        out.writeShort(this._formula1.getEncodedTokenSize());
        out.writeShort(this._not_used_1);
        this._formula1.serializeTokens(out);
        out.writeShort(this._formula2.getEncodedTokenSize());
        out.writeShort(this._not_used_2);
        this._formula2.serializeTokens(out);
        this._regions.serialize(out);
    }
    
    private static UnicodeString resolveTitleText(final String str) {
        if (str == null || str.length() < 1) {
            return DVRecord.NULL_TEXT_STRING;
        }
        return new UnicodeString(str);
    }
    
    private static String resolveTitleString(final UnicodeString us) {
        if (us == null || us.equals(DVRecord.NULL_TEXT_STRING)) {
            return null;
        }
        return us.getString();
    }
    
    private static UnicodeString readUnicodeString(final RecordInputStream in) {
        return new UnicodeString(in);
    }
    
    private static void serializeUnicodeString(final UnicodeString us, final LittleEndianOutput out) {
        StringUtil.writeUnicodeString(out, us.getString());
    }
    
    private static int getUnicodeStringSize(final UnicodeString us) {
        final String str = us.getString();
        return 3 + str.length() * (StringUtil.hasMultibyte(str) ? 2 : 1);
    }
    
    @Override
    protected int getDataSize() {
        int size = 12;
        size += getUnicodeStringSize(this._promptTitle);
        size += getUnicodeStringSize(this._errorTitle);
        size += getUnicodeStringSize(this._promptText);
        size += getUnicodeStringSize(this._errorText);
        size += this._formula1.getEncodedTokenSize();
        size += this._formula2.getEncodedTokenSize();
        size += this._regions.getSize();
        return size;
    }
    
    @Override
    public short getSid() {
        return 446;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DVRecord clone() {
        return this.copy();
    }
    
    @Override
    public DVRecord copy() {
        return new DVRecord(this);
    }
    
    static {
        NULL_TEXT_STRING = new UnicodeString("\u0000");
        opt_data_type = new BitField(15);
        opt_error_style = new BitField(112);
        opt_string_list_formula = new BitField(128);
        opt_empty_cell_allowed = new BitField(256);
        opt_suppress_dropdown_arrow = new BitField(512);
        opt_show_prompt_on_cell_selected = new BitField(262144);
        opt_show_error_on_invalid_value = new BitField(524288);
        opt_condition_operator = new BitField(7340032);
    }
}
