package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.hssf.record.cont.ContinuableRecord;

public final class NameRecord extends ContinuableRecord
{
    public static final short sid = 24;
    public static final byte BUILTIN_CONSOLIDATE_AREA = 1;
    public static final byte BUILTIN_AUTO_OPEN = 2;
    public static final byte BUILTIN_AUTO_CLOSE = 3;
    public static final byte BUILTIN_DATABASE = 4;
    public static final byte BUILTIN_CRITERIA = 5;
    public static final byte BUILTIN_PRINT_AREA = 6;
    public static final byte BUILTIN_PRINT_TITLE = 7;
    public static final byte BUILTIN_RECORDER = 8;
    public static final byte BUILTIN_DATA_FORM = 9;
    public static final byte BUILTIN_AUTO_ACTIVATE = 10;
    public static final byte BUILTIN_AUTO_DEACTIVATE = 11;
    public static final byte BUILTIN_SHEET_TITLE = 12;
    public static final byte BUILTIN_FILTER_DB = 13;
    private short field_1_option_flag;
    private byte field_2_keyboard_shortcut;
    private short field_5_externSheetIndex_plus1;
    private int field_6_sheetNumber;
    private boolean field_11_nameIsMultibyte;
    private byte field_12_built_in_code;
    private String field_12_name_text;
    private Formula field_13_name_definition;
    private String field_14_custom_menu_text;
    private String field_15_description_text;
    private String field_16_help_topic_text;
    private String field_17_status_bar_text;
    
    public NameRecord() {
        this.field_13_name_definition = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        this.field_12_name_text = "";
        this.field_14_custom_menu_text = "";
        this.field_15_description_text = "";
        this.field_16_help_topic_text = "";
        this.field_17_status_bar_text = "";
    }
    
    public NameRecord(final NameRecord other) {
        super(other);
        this.field_1_option_flag = other.field_1_option_flag;
        this.field_2_keyboard_shortcut = other.field_2_keyboard_shortcut;
        this.field_5_externSheetIndex_plus1 = other.field_5_externSheetIndex_plus1;
        this.field_6_sheetNumber = other.field_6_sheetNumber;
        this.field_11_nameIsMultibyte = other.field_11_nameIsMultibyte;
        this.field_12_built_in_code = other.field_12_built_in_code;
        this.field_12_name_text = other.field_12_name_text;
        this.field_13_name_definition = other.field_13_name_definition;
        this.field_14_custom_menu_text = other.field_14_custom_menu_text;
        this.field_15_description_text = other.field_15_description_text;
        this.field_16_help_topic_text = other.field_16_help_topic_text;
        this.field_17_status_bar_text = other.field_17_status_bar_text;
    }
    
    public NameRecord(final byte builtin, final int sheetNumber) {
        this();
        this.field_12_built_in_code = builtin;
        this.setOptionFlag((short)(this.field_1_option_flag | 0x20));
        this.field_6_sheetNumber = sheetNumber;
    }
    
    public void setOptionFlag(final short flag) {
        this.field_1_option_flag = flag;
    }
    
    public void setKeyboardShortcut(final byte shortcut) {
        this.field_2_keyboard_shortcut = shortcut;
    }
    
    public int getSheetNumber() {
        return this.field_6_sheetNumber;
    }
    
    public byte getFnGroup() {
        final int masked = this.field_1_option_flag & 0xFC0;
        return (byte)(masked >> 4);
    }
    
    public void setSheetNumber(final int value) {
        this.field_6_sheetNumber = value;
    }
    
    public void setNameText(final String name) {
        this.field_12_name_text = name;
        this.field_11_nameIsMultibyte = StringUtil.hasMultibyte(name);
    }
    
    public void setCustomMenuText(final String text) {
        this.field_14_custom_menu_text = text;
    }
    
    public void setDescriptionText(final String text) {
        this.field_15_description_text = text;
    }
    
    public void setHelpTopicText(final String text) {
        this.field_16_help_topic_text = text;
    }
    
    public void setStatusBarText(final String text) {
        this.field_17_status_bar_text = text;
    }
    
    public short getOptionFlag() {
        return this.field_1_option_flag;
    }
    
    public byte getKeyboardShortcut() {
        return this.field_2_keyboard_shortcut;
    }
    
    private int getNameTextLength() {
        if (this.isBuiltInName()) {
            return 1;
        }
        return this.field_12_name_text.length();
    }
    
    public boolean isHiddenName() {
        return (this.field_1_option_flag & 0x1) != 0x0;
    }
    
    public void setHidden(final boolean b) {
        if (b) {
            this.field_1_option_flag |= 0x1;
        }
        else {
            this.field_1_option_flag &= 0xFFFFFFFE;
        }
    }
    
    public boolean isFunctionName() {
        return (this.field_1_option_flag & 0x2) != 0x0;
    }
    
    public void setFunction(final boolean function) {
        if (function) {
            this.field_1_option_flag |= 0x2;
        }
        else {
            this.field_1_option_flag &= 0xFFFFFFFD;
        }
    }
    
    public boolean hasFormula() {
        return Option.isFormula(this.field_1_option_flag) && this.field_13_name_definition.getEncodedTokenSize() > 0;
    }
    
    public boolean isCommandName() {
        return (this.field_1_option_flag & 0x4) != 0x0;
    }
    
    public boolean isMacro() {
        return (this.field_1_option_flag & 0x8) != 0x0;
    }
    
    public boolean isComplexFunction() {
        return (this.field_1_option_flag & 0x10) != 0x0;
    }
    
    public boolean isBuiltInName() {
        return (this.field_1_option_flag & 0x20) != 0x0;
    }
    
    public String getNameText() {
        return this.isBuiltInName() ? translateBuiltInName(this.getBuiltInName()) : this.field_12_name_text;
    }
    
    public byte getBuiltInName() {
        return this.field_12_built_in_code;
    }
    
    public Ptg[] getNameDefinition() {
        return this.field_13_name_definition.getTokens();
    }
    
    public void setNameDefinition(final Ptg[] ptgs) {
        this.field_13_name_definition = Formula.create(ptgs);
    }
    
    public String getCustomMenuText() {
        return this.field_14_custom_menu_text;
    }
    
    public String getDescriptionText() {
        return this.field_15_description_text;
    }
    
    public String getHelpTopicText() {
        return this.field_16_help_topic_text;
    }
    
    public String getStatusBarText() {
        return this.field_17_status_bar_text;
    }
    
    public void serialize(final ContinuableRecordOutput out) {
        final int field_7_length_custom_menu = this.field_14_custom_menu_text.length();
        final int field_8_length_description_text = this.field_15_description_text.length();
        final int field_9_length_help_topic_text = this.field_16_help_topic_text.length();
        final int field_10_length_status_bar_text = this.field_17_status_bar_text.length();
        out.writeShort(this.getOptionFlag());
        out.writeByte(this.getKeyboardShortcut());
        out.writeByte(this.getNameTextLength());
        out.writeShort(this.field_13_name_definition.getEncodedTokenSize());
        out.writeShort(this.field_5_externSheetIndex_plus1);
        out.writeShort(this.field_6_sheetNumber);
        out.writeByte(field_7_length_custom_menu);
        out.writeByte(field_8_length_description_text);
        out.writeByte(field_9_length_help_topic_text);
        out.writeByte(field_10_length_status_bar_text);
        out.writeByte(this.field_11_nameIsMultibyte ? 1 : 0);
        if (this.isBuiltInName()) {
            out.writeByte(this.field_12_built_in_code);
        }
        else {
            final String nameText = this.field_12_name_text;
            if (this.field_11_nameIsMultibyte) {
                StringUtil.putUnicodeLE(nameText, out);
            }
            else {
                StringUtil.putCompressedUnicode(nameText, out);
            }
        }
        this.field_13_name_definition.serializeTokens(out);
        this.field_13_name_definition.serializeArrayConstantData(out);
        StringUtil.putCompressedUnicode(this.getCustomMenuText(), out);
        StringUtil.putCompressedUnicode(this.getDescriptionText(), out);
        StringUtil.putCompressedUnicode(this.getHelpTopicText(), out);
        StringUtil.putCompressedUnicode(this.getStatusBarText(), out);
    }
    
    private int getNameRawSize() {
        if (this.isBuiltInName()) {
            return 1;
        }
        final int nChars = this.field_12_name_text.length();
        if (this.field_11_nameIsMultibyte) {
            return 2 * nChars;
        }
        return nChars;
    }
    
    protected int getDataSize() {
        return 13 + this.getNameRawSize() + this.field_14_custom_menu_text.length() + this.field_15_description_text.length() + this.field_16_help_topic_text.length() + this.field_17_status_bar_text.length() + this.field_13_name_definition.getEncodedSize();
    }
    
    public int getExternSheetNumber() {
        final Ptg[] tokens = this.field_13_name_definition.getTokens();
        if (tokens.length == 0) {
            return 0;
        }
        final Ptg ptg = tokens[0];
        if (ptg.getClass() == Area3DPtg.class) {
            return ((Area3DPtg)ptg).getExternSheetIndex();
        }
        if (ptg.getClass() == Ref3DPtg.class) {
            return ((Ref3DPtg)ptg).getExternSheetIndex();
        }
        return 0;
    }
    
    public NameRecord(final RecordInputStream ris) {
        final byte[] remainder = ris.readAllContinuedRemainder();
        final LittleEndianInput in = new LittleEndianByteArrayInputStream(remainder);
        this.field_1_option_flag = in.readShort();
        this.field_2_keyboard_shortcut = in.readByte();
        final int field_3_length_name_text = in.readUByte();
        final int field_4_length_name_definition = in.readShort();
        this.field_5_externSheetIndex_plus1 = in.readShort();
        this.field_6_sheetNumber = in.readUShort();
        final int f7_customMenuLen = in.readUByte();
        final int f8_descriptionTextLen = in.readUByte();
        final int f9_helpTopicTextLen = in.readUByte();
        final int f10_statusBarTextLen = in.readUByte();
        this.field_11_nameIsMultibyte = (in.readByte() != 0);
        if (this.isBuiltInName()) {
            this.field_12_built_in_code = in.readByte();
        }
        else if (this.field_11_nameIsMultibyte) {
            this.field_12_name_text = StringUtil.readUnicodeLE(in, field_3_length_name_text);
        }
        else {
            this.field_12_name_text = StringUtil.readCompressedUnicode(in, field_3_length_name_text);
        }
        final int nBytesAvailable = in.available() - (f7_customMenuLen + f8_descriptionTextLen + f9_helpTopicTextLen + f10_statusBarTextLen);
        this.field_13_name_definition = Formula.read(field_4_length_name_definition, in, nBytesAvailable);
        this.field_14_custom_menu_text = StringUtil.readCompressedUnicode(in, f7_customMenuLen);
        this.field_15_description_text = StringUtil.readCompressedUnicode(in, f8_descriptionTextLen);
        this.field_16_help_topic_text = StringUtil.readCompressedUnicode(in, f9_helpTopicTextLen);
        this.field_17_status_bar_text = StringUtil.readCompressedUnicode(in, f10_statusBarTextLen);
    }
    
    @Override
    public short getSid() {
        return 24;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[NAME]\n");
        sb.append("    .option flags           = ").append(HexDump.shortToHex(this.field_1_option_flag)).append("\n");
        sb.append("    .keyboard shortcut      = ").append(HexDump.byteToHex(this.field_2_keyboard_shortcut)).append("\n");
        sb.append("    .length of the name     = ").append(this.getNameTextLength()).append("\n");
        sb.append("    .extSheetIx(1-based, 0=Global)= ").append(this.field_5_externSheetIndex_plus1).append("\n");
        sb.append("    .sheetTabIx             = ").append(this.field_6_sheetNumber).append("\n");
        sb.append("    .Menu text length       = ").append(this.field_14_custom_menu_text.length()).append("\n");
        sb.append("    .Description text length= ").append(this.field_15_description_text.length()).append("\n");
        sb.append("    .Help topic text length = ").append(this.field_16_help_topic_text.length()).append("\n");
        sb.append("    .Status bar text length = ").append(this.field_17_status_bar_text.length()).append("\n");
        sb.append("    .NameIsMultibyte        = ").append(this.field_11_nameIsMultibyte).append("\n");
        sb.append("    .Name (Unicode text)    = ").append(this.getNameText()).append("\n");
        final Ptg[] ptgs = this.field_13_name_definition.getTokens();
        sb.append("    .Formula (nTokens=").append(ptgs.length).append("):").append("\n");
        for (final Ptg ptg : ptgs) {
            sb.append("       ").append(ptg).append(ptg.getRVAType()).append("\n");
        }
        sb.append("    .Menu text       = ").append(this.field_14_custom_menu_text).append("\n");
        sb.append("    .Description text= ").append(this.field_15_description_text).append("\n");
        sb.append("    .Help topic text = ").append(this.field_16_help_topic_text).append("\n");
        sb.append("    .Status bar text = ").append(this.field_17_status_bar_text).append("\n");
        sb.append("[/NAME]\n");
        return sb.toString();
    }
    
    private static String translateBuiltInName(final byte name) {
        switch (name) {
            case 10: {
                return "Auto_Activate";
            }
            case 3: {
                return "Auto_Close";
            }
            case 11: {
                return "Auto_Deactivate";
            }
            case 2: {
                return "Auto_Open";
            }
            case 1: {
                return "Consolidate_Area";
            }
            case 5: {
                return "Criteria";
            }
            case 4: {
                return "Database";
            }
            case 9: {
                return "Data_Form";
            }
            case 6: {
                return "Print_Area";
            }
            case 7: {
                return "Print_Titles";
            }
            case 8: {
                return "Recorder";
            }
            case 12: {
                return "Sheet_Title";
            }
            case 13: {
                return "_FilterDatabase";
            }
            default: {
                return "Unknown";
            }
        }
    }
    
    @Override
    public NameRecord copy() {
        return new NameRecord(this);
    }
    
    private static final class Option
    {
        public static final int OPT_HIDDEN_NAME = 1;
        public static final int OPT_FUNCTION_NAME = 2;
        public static final int OPT_COMMAND_NAME = 4;
        public static final int OPT_MACRO = 8;
        public static final int OPT_COMPLEX = 16;
        public static final int OPT_BUILTIN = 32;
        public static final int OPT_BINDATA = 4096;
        
        public static boolean isFormula(final int optValue) {
            return (optValue & 0xF) == 0x0;
        }
    }
}
