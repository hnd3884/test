package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.awt.Color;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;

public class XSSFTextRun
{
    private final CTRegularTextRun _r;
    private final XSSFTextParagraph _p;
    
    XSSFTextRun(final CTRegularTextRun r, final XSSFTextParagraph p) {
        this._r = r;
        this._p = p;
    }
    
    XSSFTextParagraph getParentParagraph() {
        return this._p;
    }
    
    public String getText() {
        return this._r.getT();
    }
    
    public void setText(final String text) {
        this._r.setT(text);
    }
    
    public CTRegularTextRun getXmlObject() {
        return this._r;
    }
    
    public void setFontColor(final Color color) {
        final CTTextCharacterProperties rPr = this.getRPr();
        final CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
        final CTSRgbColor clr = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
        clr.setVal(new byte[] { (byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue() });
        if (fill.isSetHslClr()) {
            fill.unsetHslClr();
        }
        if (fill.isSetPrstClr()) {
            fill.unsetPrstClr();
        }
        if (fill.isSetSchemeClr()) {
            fill.unsetSchemeClr();
        }
        if (fill.isSetScrgbClr()) {
            fill.unsetScrgbClr();
        }
        if (fill.isSetSysClr()) {
            fill.unsetSysClr();
        }
    }
    
    public Color getFontColor() {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (rPr.isSetSolidFill()) {
            final CTSolidColorFillProperties fill = rPr.getSolidFill();
            if (fill.isSetSrgbClr()) {
                final CTSRgbColor clr = fill.getSrgbClr();
                final byte[] rgb = clr.getVal();
                return new Color(0xFF & rgb[0], 0xFF & rgb[1], 0xFF & rgb[2]);
            }
        }
        return new Color(0, 0, 0);
    }
    
    public void setFontSize(final double fontSize) {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (fontSize == -1.0) {
            if (rPr.isSetSz()) {
                rPr.unsetSz();
            }
        }
        else {
            if (fontSize < 1.0) {
                throw new IllegalArgumentException("Minimum font size is 1pt but was " + fontSize);
            }
            rPr.setSz((int)(100.0 * fontSize));
        }
    }
    
    public double getFontSize() {
        double scale = 1.0;
        double size = 11.0;
        final CTTextNormalAutofit afit = this.getParentParagraph().getParentShape().getTxBody().getBodyPr().getNormAutofit();
        if (afit != null) {
            scale = afit.getFontScale() / 100000.0;
        }
        final CTTextCharacterProperties rPr = this.getRPr();
        if (rPr.isSetSz()) {
            size = rPr.getSz() * 0.01;
        }
        return size * scale;
    }
    
    public double getCharacterSpacing() {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (rPr.isSetSpc()) {
            return rPr.getSpc() * 0.01;
        }
        return 0.0;
    }
    
    public void setCharacterSpacing(final double spc) {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (spc == 0.0) {
            if (rPr.isSetSpc()) {
                rPr.unsetSpc();
            }
        }
        else {
            rPr.setSpc((int)(100.0 * spc));
        }
    }
    
    public void setFont(final String typeface) {
        this.setFontFamily(typeface, (byte)(-1), (byte)(-1), false);
    }
    
    public void setFontFamily(final String typeface, final byte charset, final byte pictAndFamily, final boolean isSymbol) {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (typeface == null) {
            if (rPr.isSetLatin()) {
                rPr.unsetLatin();
            }
            if (rPr.isSetCs()) {
                rPr.unsetCs();
            }
            if (rPr.isSetSym()) {
                rPr.unsetSym();
            }
        }
        else if (isSymbol) {
            final CTTextFont font = rPr.isSetSym() ? rPr.getSym() : rPr.addNewSym();
            font.setTypeface(typeface);
        }
        else {
            final CTTextFont latin = rPr.isSetLatin() ? rPr.getLatin() : rPr.addNewLatin();
            latin.setTypeface(typeface);
            if (charset != -1) {
                latin.setCharset(charset);
            }
            if (pictAndFamily != -1) {
                latin.setPitchFamily(pictAndFamily);
            }
        }
    }
    
    public String getFontFamily() {
        final CTTextCharacterProperties rPr = this.getRPr();
        final CTTextFont font = rPr.getLatin();
        if (font != null) {
            return font.getTypeface();
        }
        return "Calibri";
    }
    
    public byte getPitchAndFamily() {
        final CTTextCharacterProperties rPr = this.getRPr();
        final CTTextFont font = rPr.getLatin();
        if (font != null) {
            return font.getPitchFamily();
        }
        return 0;
    }
    
    public void setStrikethrough(final boolean strike) {
        this.getRPr().setStrike(strike ? STTextStrikeType.SNG_STRIKE : STTextStrikeType.NO_STRIKE);
    }
    
    public boolean isStrikethrough() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetStrike() && rPr.getStrike() != STTextStrikeType.NO_STRIKE;
    }
    
    public boolean isSuperscript() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetBaseline() && rPr.getBaseline() > 0;
    }
    
    public void setBaselineOffset(final double baselineOffset) {
        this.getRPr().setBaseline((int)baselineOffset * 1000);
    }
    
    public void setSuperscript(final boolean flag) {
        this.setBaselineOffset(flag ? 30.0 : 0.0);
    }
    
    public void setSubscript(final boolean flag) {
        this.setBaselineOffset(flag ? -25.0 : 0.0);
    }
    
    public boolean isSubscript() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetBaseline() && rPr.getBaseline() < 0;
    }
    
    public TextCap getTextCap() {
        final CTTextCharacterProperties rPr = this.getRPr();
        if (rPr.isSetCap()) {
            return TextCap.values()[rPr.getCap().intValue() - 1];
        }
        return TextCap.NONE;
    }
    
    public void setBold(final boolean bold) {
        this.getRPr().setB(bold);
    }
    
    public boolean isBold() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetB() && rPr.getB();
    }
    
    public void setItalic(final boolean italic) {
        this.getRPr().setI(italic);
    }
    
    public boolean isItalic() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetI() && rPr.getI();
    }
    
    public void setUnderline(final boolean underline) {
        this.getRPr().setU(underline ? STTextUnderlineType.SNG : STTextUnderlineType.NONE);
    }
    
    public boolean isUnderline() {
        final CTTextCharacterProperties rPr = this.getRPr();
        return rPr.isSetU() && rPr.getU() != STTextUnderlineType.NONE;
    }
    
    protected CTTextCharacterProperties getRPr() {
        return this._r.isSetRPr() ? this._r.getRPr() : this._r.addNewRPr();
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass() + "]" + this.getText();
    }
}
