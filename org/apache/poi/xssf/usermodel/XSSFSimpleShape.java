package org.apache.poi.xssf.usermodel;

import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVertOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import java.util.Locale;
import java.util.Iterator;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xddf.usermodel.text.XDDFTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.XDDFColorRgbBinary;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import java.util.ArrayList;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import java.util.List;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.ss.usermodel.SimpleShape;

public class XSSFSimpleShape extends XSSFShape implements Iterable<XSSFTextParagraph>, SimpleShape, TextContainer
{
    private final XDDFTextBody _textBody;
    private final List<XSSFTextParagraph> _paragraphs;
    private static CTShape prototype;
    private CTShape ctShape;
    private static String[] _romanChars;
    private static int[] _romanAlphaValues;
    
    protected XSSFSimpleShape(final XSSFDrawing drawing, final CTShape ctShape) {
        this.drawing = drawing;
        this.ctShape = ctShape;
        this._paragraphs = new ArrayList<XSSFTextParagraph>();
        final CTTextBody body = ctShape.getTxBody();
        if (body == null) {
            this._textBody = null;
        }
        else {
            this._textBody = new XDDFTextBody(this, body);
            for (int i = 0; i < body.sizeOfPArray(); ++i) {
                this._paragraphs.add(new XSSFTextParagraph(body.getPArray(i), ctShape));
            }
        }
    }
    
    protected static CTShape prototype() {
        if (XSSFSimpleShape.prototype == null) {
            final CTShape shape = CTShape.Factory.newInstance();
            final CTShapeNonVisual nv = shape.addNewNvSpPr();
            final CTNonVisualDrawingProps nvp = nv.addNewCNvPr();
            nvp.setId(1L);
            nvp.setName("Shape 1");
            nv.addNewCNvSpPr();
            final CTShapeProperties sp = shape.addNewSpPr();
            final CTTransform2D t2d = sp.addNewXfrm();
            final CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            final CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            final CTPresetGeometry2D geom = sp.addNewPrstGeom();
            geom.setPrst(STShapeType.RECT);
            geom.addNewAvLst();
            final XDDFTextBody body = new XDDFTextBody(null, shape.addNewTxBody());
            final XDDFTextParagraph p3 = body.initialize();
            final XDDFRunProperties rp = p3.getAfterLastRunProperties();
            final XDDFColor black = new XDDFColorRgbBinary(new byte[] { 0, 0, 0 });
            final XDDFFillProperties fp = new XDDFSolidFillProperties(black);
            rp.setFillProperties(fp);
            XSSFSimpleShape.prototype = shape;
        }
        return XSSFSimpleShape.prototype;
    }
    
    @Internal
    public CTShape getCTShape() {
        return this.ctShape;
    }
    
    public XDDFTextBody getTextBody() {
        return this._textBody;
    }
    
    protected void setXfrm(final CTTransform2D t2d) {
        this.ctShape.getSpPr().setXfrm(t2d);
    }
    
    @Override
    public Iterator<XSSFTextParagraph> iterator() {
        return this._paragraphs.iterator();
    }
    
    public String getText() {
        final int MAX_LEVELS = 9;
        final StringBuilder out = new StringBuilder();
        final List<Integer> levelCount = new ArrayList<Integer>(9);
        for (int k = 0; k < 9; ++k) {
            levelCount.add(0);
        }
        for (int i = 0; i < this._paragraphs.size(); ++i) {
            if (out.length() > 0) {
                out.append('\n');
            }
            final XSSFTextParagraph p = this._paragraphs.get(i);
            if (p.isBullet() && p.getText().length() > 0) {
                final int level = Math.min(p.getLevel(), 8);
                if (p.isBulletAutoNumber()) {
                    i = this.processAutoNumGroup(i, level, levelCount, out);
                }
                else {
                    for (int j = 0; j < level; ++j) {
                        out.append('\t');
                    }
                    final String character = p.getBulletCharacter();
                    out.append((character.length() > 0) ? (character + " ") : "- ");
                    out.append(p.getText());
                }
            }
            else {
                out.append(p.getText());
                for (int l = 0; l < 9; ++l) {
                    levelCount.set(l, 0);
                }
            }
        }
        return out.toString();
    }
    
    private int processAutoNumGroup(int index, final int level, final List<Integer> levelCount, final StringBuilder out) {
        final XSSFTextParagraph p = this._paragraphs.get(index);
        final int startAt = p.getBulletAutoNumberStart();
        final ListAutoNumber scheme = p.getBulletAutoNumberScheme();
        if (levelCount.get(level) == 0) {
            levelCount.set(level, (startAt == 0) ? 1 : startAt);
        }
        for (int j = 0; j < level; ++j) {
            out.append('\t');
        }
        if (p.getText().length() > 0) {
            out.append(this.getBulletPrefix(scheme, levelCount.get(level)));
            out.append(p.getText());
        }
        while (true) {
            final XSSFTextParagraph nextp = (index + 1 == this._paragraphs.size()) ? null : this._paragraphs.get(index + 1);
            if (nextp == null) {
                break;
            }
            if (!nextp.isBullet()) {
                break;
            }
            if (!p.isBulletAutoNumber()) {
                break;
            }
            if (nextp.getLevel() > level) {
                if (out.length() > 0) {
                    out.append('\n');
                }
                index = this.processAutoNumGroup(index + 1, nextp.getLevel(), levelCount, out);
            }
            else {
                if (nextp.getLevel() < level) {
                    break;
                }
                final ListAutoNumber nextScheme = nextp.getBulletAutoNumberScheme();
                final int nextStartAt = nextp.getBulletAutoNumberStart();
                if (nextScheme != scheme || nextStartAt != startAt) {
                    break;
                }
                ++index;
                if (out.length() > 0) {
                    out.append('\n');
                }
                for (int i = 0; i < level; ++i) {
                    out.append('\t');
                }
                if (nextp.getText().length() <= 0) {
                    continue;
                }
                levelCount.set(level, levelCount.get(level) + 1);
                out.append(this.getBulletPrefix(nextScheme, levelCount.get(level)));
                out.append(nextp.getText());
            }
        }
        levelCount.set(level, 0);
        return index;
    }
    
    private String getBulletPrefix(final ListAutoNumber scheme, final int value) {
        final StringBuilder out = new StringBuilder();
        switch (scheme) {
            case ALPHA_LC_PARENT_BOTH:
            case ALPHA_LC_PARENT_R: {
                if (scheme == ListAutoNumber.ALPHA_LC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(this.valueToAlpha(value).toLowerCase(Locale.ROOT));
                out.append(')');
                break;
            }
            case ALPHA_UC_PARENT_BOTH:
            case ALPHA_UC_PARENT_R: {
                if (scheme == ListAutoNumber.ALPHA_UC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(this.valueToAlpha(value));
                out.append(')');
                break;
            }
            case ALPHA_LC_PERIOD: {
                out.append(this.valueToAlpha(value).toLowerCase(Locale.ROOT));
                out.append('.');
                break;
            }
            case ALPHA_UC_PERIOD: {
                out.append(this.valueToAlpha(value));
                out.append('.');
                break;
            }
            case ARABIC_PARENT_BOTH:
            case ARABIC_PARENT_R: {
                if (scheme == ListAutoNumber.ARABIC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(value);
                out.append(')');
                break;
            }
            case ARABIC_PERIOD: {
                out.append(value);
                out.append('.');
                break;
            }
            case ARABIC_PLAIN: {
                out.append(value);
                break;
            }
            case ROMAN_LC_PARENT_BOTH:
            case ROMAN_LC_PARENT_R: {
                if (scheme == ListAutoNumber.ROMAN_LC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(this.valueToRoman(value).toLowerCase(Locale.ROOT));
                out.append(')');
                break;
            }
            case ROMAN_UC_PARENT_BOTH:
            case ROMAN_UC_PARENT_R: {
                if (scheme == ListAutoNumber.ROMAN_UC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(this.valueToRoman(value));
                out.append(')');
                break;
            }
            case ROMAN_LC_PERIOD: {
                out.append(this.valueToRoman(value).toLowerCase(Locale.ROOT));
                out.append('.');
                break;
            }
            case ROMAN_UC_PERIOD: {
                out.append(this.valueToRoman(value));
                out.append('.');
                break;
            }
            default: {
                out.append('\u2022');
                break;
            }
        }
        out.append(" ");
        return out.toString();
    }
    
    private String valueToAlpha(int value) {
        String alpha = "";
        while (value > 0) {
            final int modulo = (value - 1) % 26;
            alpha = (char)(65 + modulo) + alpha;
            value = (value - modulo) / 26;
        }
        return alpha;
    }
    
    private String valueToRoman(int value) {
        final StringBuilder out = new StringBuilder();
        for (int i = 0; value > 0 && i < XSSFSimpleShape._romanChars.length; ++i) {
            while (XSSFSimpleShape._romanAlphaValues[i] <= value) {
                out.append(XSSFSimpleShape._romanChars[i]);
                value -= XSSFSimpleShape._romanAlphaValues[i];
            }
        }
        return out.toString();
    }
    
    public void clearText() {
        this._paragraphs.clear();
        final CTTextBody txBody = this.ctShape.getTxBody();
        txBody.setPArray((CTTextParagraph[])null);
    }
    
    public void setText(final String text) {
        this.clearText();
        this.addNewTextParagraph().addNewTextRun().setText(text);
    }
    
    public void setText(final XSSFRichTextString str) {
        final XSSFWorkbook wb = (XSSFWorkbook)this.getDrawing().getParent().getParent();
        str.setStylesTableReference(wb.getStylesSource());
        final CTTextParagraph p = CTTextParagraph.Factory.newInstance();
        if (str.numFormattingRuns() == 0) {
            final CTRegularTextRun r = p.addNewR();
            final CTTextCharacterProperties rPr = r.addNewRPr();
            rPr.setLang("en-US");
            rPr.setSz(1100);
            r.setT(str.getString());
        }
        else {
            for (int i = 0; i < str.getCTRst().sizeOfRArray(); ++i) {
                final CTRElt lt = str.getCTRst().getRArray(i);
                CTRPrElt ltPr = lt.getRPr();
                if (ltPr == null) {
                    ltPr = lt.addNewRPr();
                }
                final CTRegularTextRun r2 = p.addNewR();
                final CTTextCharacterProperties rPr2 = r2.addNewRPr();
                rPr2.setLang("en-US");
                applyAttributes(ltPr, rPr2);
                r2.setT(lt.getT());
            }
        }
        this.clearText();
        this.ctShape.getTxBody().setPArray(new CTTextParagraph[] { p });
        this._paragraphs.add(new XSSFTextParagraph(this.ctShape.getTxBody().getPArray(0), this.ctShape));
    }
    
    public List<XSSFTextParagraph> getTextParagraphs() {
        return this._paragraphs;
    }
    
    public XSSFTextParagraph addNewTextParagraph() {
        final CTTextBody txBody = this.ctShape.getTxBody();
        final CTTextParagraph p = txBody.addNewP();
        final XSSFTextParagraph paragraph = new XSSFTextParagraph(p, this.ctShape);
        this._paragraphs.add(paragraph);
        return paragraph;
    }
    
    public XSSFTextParagraph addNewTextParagraph(final String text) {
        final XSSFTextParagraph paragraph = this.addNewTextParagraph();
        paragraph.addNewTextRun().setText(text);
        return paragraph;
    }
    
    public XSSFTextParagraph addNewTextParagraph(final XSSFRichTextString str) {
        final CTTextBody txBody = this.ctShape.getTxBody();
        final CTTextParagraph p = txBody.addNewP();
        if (str.numFormattingRuns() == 0) {
            final CTRegularTextRun r = p.addNewR();
            final CTTextCharacterProperties rPr = r.addNewRPr();
            rPr.setLang("en-US");
            rPr.setSz(1100);
            r.setT(str.getString());
        }
        else {
            for (int i = 0; i < str.getCTRst().sizeOfRArray(); ++i) {
                final CTRElt lt = str.getCTRst().getRArray(i);
                CTRPrElt ltPr = lt.getRPr();
                if (ltPr == null) {
                    ltPr = lt.addNewRPr();
                }
                final CTRegularTextRun r2 = p.addNewR();
                final CTTextCharacterProperties rPr2 = r2.addNewRPr();
                rPr2.setLang("en-US");
                applyAttributes(ltPr, rPr2);
                r2.setT(lt.getT());
            }
        }
        final XSSFTextParagraph paragraph = new XSSFTextParagraph(p, this.ctShape);
        this._paragraphs.add(paragraph);
        return paragraph;
    }
    
    public void setTextHorizontalOverflow(final TextHorizontalOverflow overflow) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (overflow == null) {
                if (bodyPr.isSetHorzOverflow()) {
                    bodyPr.unsetHorzOverflow();
                }
            }
            else {
                bodyPr.setHorzOverflow(STTextHorzOverflowType.Enum.forInt(overflow.ordinal() + 1));
            }
        }
    }
    
    public TextHorizontalOverflow getTextHorizontalOverflow() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetHorzOverflow()) {
            return TextHorizontalOverflow.values()[bodyPr.getHorzOverflow().intValue() - 1];
        }
        return TextHorizontalOverflow.OVERFLOW;
    }
    
    public void setTextVerticalOverflow(final TextVerticalOverflow overflow) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (overflow == null) {
                if (bodyPr.isSetVertOverflow()) {
                    bodyPr.unsetVertOverflow();
                }
            }
            else {
                bodyPr.setVertOverflow(STTextVertOverflowType.Enum.forInt(overflow.ordinal() + 1));
            }
        }
    }
    
    public TextVerticalOverflow getTextVerticalOverflow() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetVertOverflow()) {
            return TextVerticalOverflow.values()[bodyPr.getVertOverflow().intValue() - 1];
        }
        return TextVerticalOverflow.OVERFLOW;
    }
    
    public void setVerticalAlignment(final VerticalAlignment anchor) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (anchor == null) {
                if (bodyPr.isSetAnchor()) {
                    bodyPr.unsetAnchor();
                }
            }
            else {
                bodyPr.setAnchor(STTextAnchoringType.Enum.forInt(anchor.ordinal() + 1));
            }
        }
    }
    
    public VerticalAlignment getVerticalAlignment() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetAnchor()) {
            return VerticalAlignment.values()[bodyPr.getAnchor().intValue() - 1];
        }
        return VerticalAlignment.TOP;
    }
    
    public void setTextDirection(final TextDirection orientation) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (orientation == null) {
                if (bodyPr.isSetVert()) {
                    bodyPr.unsetVert();
                }
            }
            else {
                bodyPr.setVert(STTextVerticalType.Enum.forInt(orientation.ordinal() + 1));
            }
        }
    }
    
    public TextDirection getTextDirection() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            final STTextVerticalType.Enum val = bodyPr.getVert();
            if (val != null) {
                return TextDirection.values()[val.intValue() - 1];
            }
        }
        return TextDirection.HORIZONTAL;
    }
    
    public double getBottomInset() {
        final Double inset = this._textBody.getBodyProperties().getBottomInset();
        if (inset == null) {
            return 3.6;
        }
        return inset;
    }
    
    public double getLeftInset() {
        final Double inset = this._textBody.getBodyProperties().getLeftInset();
        if (inset == null) {
            return 3.6;
        }
        return inset;
    }
    
    public double getRightInset() {
        final Double inset = this._textBody.getBodyProperties().getRightInset();
        if (inset == null) {
            return 3.6;
        }
        return inset;
    }
    
    public double getTopInset() {
        final Double inset = this._textBody.getBodyProperties().getTopInset();
        if (inset == null) {
            return 3.6;
        }
        return inset;
    }
    
    public void setBottomInset(final double margin) {
        if (margin == -1.0) {
            this._textBody.getBodyProperties().setBottomInset(null);
        }
        else {
            this._textBody.getBodyProperties().setBottomInset(margin);
        }
    }
    
    public void setLeftInset(final double margin) {
        if (margin == -1.0) {
            this._textBody.getBodyProperties().setLeftInset(null);
        }
        else {
            this._textBody.getBodyProperties().setLeftInset(margin);
        }
    }
    
    public void setRightInset(final double margin) {
        if (margin == -1.0) {
            this._textBody.getBodyProperties().setRightInset(null);
        }
        else {
            this._textBody.getBodyProperties().setRightInset(margin);
        }
    }
    
    public void setTopInset(final double margin) {
        if (margin == -1.0) {
            this._textBody.getBodyProperties().setTopInset(null);
        }
        else {
            this._textBody.getBodyProperties().setTopInset(margin);
        }
    }
    
    public boolean getWordWrap() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        return bodyPr == null || !bodyPr.isSetWrap() || bodyPr.getWrap() == STTextWrappingType.SQUARE;
    }
    
    public void setWordWrap(final boolean wrap) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            bodyPr.setWrap(wrap ? STTextWrappingType.SQUARE : STTextWrappingType.NONE);
        }
    }
    
    public void setTextAutofit(final TextAutofit value) {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (bodyPr.isSetSpAutoFit()) {
                bodyPr.unsetSpAutoFit();
            }
            if (bodyPr.isSetNoAutofit()) {
                bodyPr.unsetNoAutofit();
            }
            if (bodyPr.isSetNormAutofit()) {
                bodyPr.unsetNormAutofit();
            }
            switch (value) {
                case NONE: {
                    bodyPr.addNewNoAutofit();
                    break;
                }
                case NORMAL: {
                    bodyPr.addNewNormAutofit();
                    break;
                }
                case SHAPE: {
                    bodyPr.addNewSpAutoFit();
                    break;
                }
            }
        }
    }
    
    public TextAutofit getTextAutofit() {
        final CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (bodyPr.isSetNoAutofit()) {
                return TextAutofit.NONE;
            }
            if (bodyPr.isSetNormAutofit()) {
                return TextAutofit.NORMAL;
            }
            if (bodyPr.isSetSpAutoFit()) {
                return TextAutofit.SHAPE;
            }
        }
        return TextAutofit.NORMAL;
    }
    
    public int getShapeType() {
        return this.ctShape.getSpPr().getPrstGeom().getPrst().intValue();
    }
    
    public void setShapeType(final int type) {
        this.ctShape.getSpPr().getPrstGeom().setPrst(STShapeType.Enum.forInt(type));
    }
    
    @Override
    protected CTShapeProperties getShapeProperties() {
        return this.ctShape.getSpPr();
    }
    
    private static void applyAttributes(final CTRPrElt pr, final CTTextCharacterProperties rPr) {
        if (pr.sizeOfBArray() > 0) {
            rPr.setB(pr.getBArray(0).getVal());
        }
        if (pr.sizeOfUArray() > 0) {
            final STUnderlineValues.Enum u1 = pr.getUArray(0).getVal();
            if (u1 == STUnderlineValues.SINGLE) {
                rPr.setU(STTextUnderlineType.SNG);
            }
            else if (u1 == STUnderlineValues.DOUBLE) {
                rPr.setU(STTextUnderlineType.DBL);
            }
            else if (u1 == STUnderlineValues.NONE) {
                rPr.setU(STTextUnderlineType.NONE);
            }
        }
        if (pr.sizeOfIArray() > 0) {
            rPr.setI(pr.getIArray(0).getVal());
        }
        if (pr.sizeOfRFontArray() > 0) {
            final CTTextFont rFont = rPr.isSetLatin() ? rPr.getLatin() : rPr.addNewLatin();
            rFont.setTypeface(pr.getRFontArray(0).getVal());
        }
        if (pr.sizeOfSzArray() > 0) {
            final int sz = (int)(pr.getSzArray(0).getVal() * 100.0);
            rPr.setSz(sz);
        }
        if (pr.sizeOfColorArray() > 0) {
            final CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
            final CTColor xlsColor = pr.getColorArray(0);
            if (xlsColor.isSetRgb()) {
                final CTSRgbColor clr = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
                clr.setVal(xlsColor.getRgb());
            }
            else if (xlsColor.isSetIndexed()) {
                final HSSFColor indexed = HSSFColor.getIndexHash().get((int)xlsColor.getIndexed());
                if (indexed != null) {
                    final byte[] rgb = { (byte)indexed.getTriplet()[0], (byte)indexed.getTriplet()[1], (byte)indexed.getTriplet()[2] };
                    final CTSRgbColor clr2 = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
                    clr2.setVal(rgb);
                }
            }
        }
    }
    
    public String getShapeName() {
        return this.ctShape.getNvSpPr().getCNvPr().getName();
    }
    
    public int getShapeId() {
        return (int)this.ctShape.getNvSpPr().getCNvPr().getId();
    }
    
    public <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter) {
        return Optional.empty();
    }
    
    public <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        return Optional.empty();
    }
    
    static {
        XSSFSimpleShape._romanChars = new String[] { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
        XSSFSimpleShape._romanAlphaValues = new int[] { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    }
}
