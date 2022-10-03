package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.BitField;

public final class WSBoolRecord extends StandardRecord
{
    public static final short sid = 129;
    private static final BitField autobreaks;
    private static final BitField dialog;
    private static final BitField applystyles;
    private static final BitField rowsumsbelow;
    private static final BitField rowsumsright;
    private static final BitField fittopage;
    private static final BitField displayguts;
    private static final BitField alternateexpression;
    private static final BitField alternateformula;
    private byte field_1_wsbool;
    private byte field_2_wsbool;
    
    public WSBoolRecord() {
    }
    
    public WSBoolRecord(final WSBoolRecord other) {
        super(other);
        this.field_1_wsbool = other.field_1_wsbool;
        this.field_2_wsbool = other.field_2_wsbool;
    }
    
    public WSBoolRecord(final RecordInputStream in) {
        final byte[] data = in.readRemainder();
        this.field_1_wsbool = data[1];
        this.field_2_wsbool = data[0];
    }
    
    public void setWSBool1(final byte bool1) {
        this.field_1_wsbool = bool1;
    }
    
    public void setAutobreaks(final boolean ab) {
        this.field_1_wsbool = WSBoolRecord.autobreaks.setByteBoolean(this.field_1_wsbool, ab);
    }
    
    public void setDialog(final boolean isDialog) {
        this.field_1_wsbool = WSBoolRecord.dialog.setByteBoolean(this.field_1_wsbool, isDialog);
    }
    
    public void setRowSumsBelow(final boolean below) {
        this.field_1_wsbool = WSBoolRecord.rowsumsbelow.setByteBoolean(this.field_1_wsbool, below);
    }
    
    public void setRowSumsRight(final boolean right) {
        this.field_1_wsbool = WSBoolRecord.rowsumsright.setByteBoolean(this.field_1_wsbool, right);
    }
    
    public void setWSBool2(final byte bool2) {
        this.field_2_wsbool = bool2;
    }
    
    public void setFitToPage(final boolean fit2page) {
        this.field_2_wsbool = WSBoolRecord.fittopage.setByteBoolean(this.field_2_wsbool, fit2page);
    }
    
    public void setDisplayGuts(final boolean guts) {
        this.field_2_wsbool = WSBoolRecord.displayguts.setByteBoolean(this.field_2_wsbool, guts);
    }
    
    public void setAlternateExpression(final boolean altexp) {
        this.field_2_wsbool = WSBoolRecord.alternateexpression.setByteBoolean(this.field_2_wsbool, altexp);
    }
    
    public void setAlternateFormula(final boolean formula) {
        this.field_2_wsbool = WSBoolRecord.alternateformula.setByteBoolean(this.field_2_wsbool, formula);
    }
    
    public byte getWSBool1() {
        return this.field_1_wsbool;
    }
    
    public boolean getAutobreaks() {
        return WSBoolRecord.autobreaks.isSet(this.field_1_wsbool);
    }
    
    public boolean getDialog() {
        return WSBoolRecord.dialog.isSet(this.field_1_wsbool);
    }
    
    public boolean getRowSumsBelow() {
        return WSBoolRecord.rowsumsbelow.isSet(this.field_1_wsbool);
    }
    
    public boolean getRowSumsRight() {
        return WSBoolRecord.rowsumsright.isSet(this.field_1_wsbool);
    }
    
    public byte getWSBool2() {
        return this.field_2_wsbool;
    }
    
    public boolean getFitToPage() {
        return WSBoolRecord.fittopage.isSet(this.field_2_wsbool);
    }
    
    public boolean getDisplayGuts() {
        return WSBoolRecord.displayguts.isSet(this.field_2_wsbool);
    }
    
    public boolean getAlternateExpression() {
        return WSBoolRecord.alternateexpression.isSet(this.field_2_wsbool);
    }
    
    public boolean getAlternateFormula() {
        return WSBoolRecord.alternateformula.isSet(this.field_2_wsbool);
    }
    
    @Override
    public String toString() {
        return "[WSBOOL]\n    .wsbool1        = " + Integer.toHexString(this.getWSBool1()) + "\n        .autobreaks = " + this.getAutobreaks() + "\n        .dialog     = " + this.getDialog() + "\n        .rowsumsbelw= " + this.getRowSumsBelow() + "\n        .rowsumsrigt= " + this.getRowSumsRight() + "\n    .wsbool2        = " + Integer.toHexString(this.getWSBool2()) + "\n        .fittopage  = " + this.getFitToPage() + "\n        .displayguts= " + this.getDisplayGuts() + "\n        .alternateex= " + this.getAlternateExpression() + "\n        .alternatefo= " + this.getAlternateFormula() + "\n[/WSBOOL]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeByte(this.getWSBool2());
        out.writeByte(this.getWSBool1());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 129;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public WSBoolRecord clone() {
        return this.copy();
    }
    
    @Override
    public WSBoolRecord copy() {
        return new WSBoolRecord(this);
    }
    
    static {
        autobreaks = BitFieldFactory.getInstance(1);
        dialog = BitFieldFactory.getInstance(16);
        applystyles = BitFieldFactory.getInstance(32);
        rowsumsbelow = BitFieldFactory.getInstance(64);
        rowsumsright = BitFieldFactory.getInstance(128);
        fittopage = BitFieldFactory.getInstance(1);
        displayguts = BitFieldFactory.getInstance(6);
        alternateexpression = BitFieldFactory.getInstance(64);
        alternateformula = BitFieldFactory.getInstance(128);
    }
}
