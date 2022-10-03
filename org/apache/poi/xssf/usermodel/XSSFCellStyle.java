package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.ReadingOrder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.ss.usermodel.Font;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.ss.usermodel.CellStyle;

public class XSSFCellStyle implements CellStyle, Duplicatable
{
    private int _cellXfId;
    private final StylesTable _stylesSource;
    private CTXf _cellXf;
    private final CTXf _cellStyleXf;
    private XSSFFont _font;
    private XSSFCellAlignment _cellAlignment;
    private ThemesTable _theme;
    
    public XSSFCellStyle(final int cellXfId, final int cellStyleXfId, final StylesTable stylesSource, final ThemesTable theme) {
        this._cellXfId = cellXfId;
        this._stylesSource = stylesSource;
        this._cellXf = stylesSource.getCellXfAt(this._cellXfId);
        this._cellStyleXf = ((cellStyleXfId == -1) ? null : stylesSource.getCellStyleXfAt(cellStyleXfId));
        this._theme = theme;
    }
    
    @Internal
    public CTXf getCoreXf() {
        return this._cellXf;
    }
    
    @Internal
    public CTXf getStyleXf() {
        return this._cellStyleXf;
    }
    
    public XSSFCellStyle(final StylesTable stylesSource) {
        this._stylesSource = stylesSource;
        this._cellXf = CTXf.Factory.newInstance();
        this._cellStyleXf = null;
    }
    
    public void verifyBelongsToStylesSource(final StylesTable src) {
        if (this._stylesSource != src) {
            throw new IllegalArgumentException("This Style does not belong to the supplied Workbook Styles Source. Are you trying to assign a style from one workbook to the cell of a different workbook?");
        }
    }
    
    public void cloneStyleFrom(final CellStyle source) {
        if (source instanceof XSSFCellStyle) {
            final XSSFCellStyle src = (XSSFCellStyle)source;
            if (src._stylesSource == this._stylesSource) {
                this._cellXf.set((XmlObject)src.getCoreXf());
                this._cellStyleXf.set((XmlObject)src.getStyleXf());
            }
            else {
                try {
                    if (this._cellXf.isSetAlignment()) {
                        this._cellXf.unsetAlignment();
                    }
                    if (this._cellXf.isSetExtLst()) {
                        this._cellXf.unsetExtLst();
                    }
                    this._cellXf = CTXf.Factory.parse(src.getCoreXf().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    final CTFill fill = CTFill.Factory.parse(src.getCTFill().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    this.addFill(fill);
                    final CTBorder border = CTBorder.Factory.parse(src.getCTBorder().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    this.addBorder(border);
                    this._stylesSource.replaceCellXfAt(this._cellXfId, this._cellXf);
                }
                catch (final XmlException e) {
                    throw new POIXMLException((Throwable)e);
                }
                final String fmt = src.getDataFormatString();
                this.setDataFormat(new XSSFDataFormat(this._stylesSource).getFormat(fmt));
                try {
                    final CTFont ctFont = CTFont.Factory.parse(src.getFont().getCTFont().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    final XSSFFont font = new XSSFFont(ctFont);
                    font.registerTo(this._stylesSource);
                    this.setFont((Font)font);
                }
                catch (final XmlException e2) {
                    throw new POIXMLException((Throwable)e2);
                }
            }
            this._font = null;
            this._cellAlignment = null;
            return;
        }
        throw new IllegalArgumentException("Can only clone from one XSSFCellStyle to another, not between HSSFCellStyle and XSSFCellStyle");
    }
    
    private void addFill(final CTFill fill) {
        final int idx = this._stylesSource.putFill(new XSSFCellFill(fill, this._stylesSource.getIndexedColors()));
        this._cellXf.setFillId((long)idx);
        this._cellXf.setApplyFill(true);
    }
    
    private void addBorder(final CTBorder border) {
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(border, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public HorizontalAlignment getAlignment() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        if (align != null && align.isSetHorizontal()) {
            return HorizontalAlignment.forInt(align.getHorizontal().intValue() - 1);
        }
        return HorizontalAlignment.GENERAL;
    }
    
    public HorizontalAlignment getAlignmentEnum() {
        return this.getAlignment();
    }
    
    public BorderStyle getBorderBottom() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        final STBorderStyle.Enum ptrn = ct.isSetBottom() ? ct.getBottom().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short)(ptrn.intValue() - 1));
    }
    
    public BorderStyle getBorderBottomEnum() {
        return this.getBorderBottom();
    }
    
    public BorderStyle getBorderLeft() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        final STBorderStyle.Enum ptrn = ct.isSetLeft() ? ct.getLeft().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short)(ptrn.intValue() - 1));
    }
    
    public BorderStyle getBorderLeftEnum() {
        return this.getBorderLeft();
    }
    
    public BorderStyle getBorderRight() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        final STBorderStyle.Enum ptrn = ct.isSetRight() ? ct.getRight().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short)(ptrn.intValue() - 1));
    }
    
    public BorderStyle getBorderRightEnum() {
        return this.getBorderRight();
    }
    
    public BorderStyle getBorderTop() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        final STBorderStyle.Enum ptrn = ct.isSetTop() ? ct.getTop().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short)(ptrn.intValue() - 1));
    }
    
    public BorderStyle getBorderTopEnum() {
        return this.getBorderTop();
    }
    
    public short getBottomBorderColor() {
        final XSSFColor clr = this.getBottomBorderXSSFColor();
        return (clr == null) ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getBottomBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.BOTTOM);
    }
    
    public short getDataFormat() {
        return (short)this._cellXf.getNumFmtId();
    }
    
    public String getDataFormatString() {
        final int idx = this.getDataFormat();
        return new XSSFDataFormat(this._stylesSource).getFormat((short)idx);
    }
    
    public short getFillBackgroundColor() {
        final XSSFColor clr = this.getFillBackgroundXSSFColor();
        return (clr == null) ? IndexedColors.AUTOMATIC.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getFillBackgroundColorColor() {
        return this.getFillBackgroundXSSFColor();
    }
    
    public XSSFColor getFillBackgroundXSSFColor() {
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return null;
        }
        final int fillIndex = (int)this._cellXf.getFillId();
        final XSSFCellFill fg = this._stylesSource.getFillAt(fillIndex);
        final XSSFColor fillBackgroundColor = fg.getFillBackgroundColor();
        if (fillBackgroundColor != null && this._theme != null) {
            this._theme.inheritFromThemeAsRequired(fillBackgroundColor);
        }
        return fillBackgroundColor;
    }
    
    public short getFillForegroundColor() {
        final XSSFColor clr = this.getFillForegroundXSSFColor();
        return (clr == null) ? IndexedColors.AUTOMATIC.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getFillForegroundColorColor() {
        return this.getFillForegroundXSSFColor();
    }
    
    public XSSFColor getFillForegroundXSSFColor() {
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return null;
        }
        final int fillIndex = (int)this._cellXf.getFillId();
        final XSSFCellFill fg = this._stylesSource.getFillAt(fillIndex);
        final XSSFColor fillForegroundColor = fg.getFillForegroundColor();
        if (fillForegroundColor != null && this._theme != null) {
            this._theme.inheritFromThemeAsRequired(fillForegroundColor);
        }
        return fillForegroundColor;
    }
    
    public FillPatternType getFillPattern() {
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return FillPatternType.NO_FILL;
        }
        final int fillIndex = (int)this._cellXf.getFillId();
        final XSSFCellFill fill = this._stylesSource.getFillAt(fillIndex);
        final STPatternType.Enum ptrn = fill.getPatternType();
        if (ptrn == null) {
            return FillPatternType.NO_FILL;
        }
        return FillPatternType.forInt(ptrn.intValue() - 1);
    }
    
    public FillPatternType getFillPatternEnum() {
        return this.getFillPattern();
    }
    
    public XSSFFont getFont() {
        if (this._font == null) {
            this._font = this._stylesSource.getFontAt(this.getFontId());
        }
        return this._font;
    }
    
    @Deprecated
    public short getFontIndex() {
        return (short)this.getFontId();
    }
    
    public int getFontIndexAsInt() {
        return this.getFontId();
    }
    
    public boolean getHidden() {
        return this._cellXf.isSetProtection() && this._cellXf.getProtection().isSetHidden() && this._cellXf.getProtection().getHidden();
    }
    
    public short getIndention() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        return (short)((align == null) ? 0L : align.getIndent());
    }
    
    public short getIndex() {
        return (short)this._cellXfId;
    }
    
    protected int getUIndex() {
        return this._cellXfId;
    }
    
    public short getLeftBorderColor() {
        final XSSFColor clr = this.getLeftBorderXSSFColor();
        return (clr == null) ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getLeftBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.LEFT);
    }
    
    public boolean getLocked() {
        return !this._cellXf.isSetProtection() || !this._cellXf.getProtection().isSetLocked() || this._cellXf.getProtection().getLocked();
    }
    
    public boolean getQuotePrefixed() {
        return this._cellXf.getQuotePrefix();
    }
    
    public short getRightBorderColor() {
        final XSSFColor clr = this.getRightBorderXSSFColor();
        return (clr == null) ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getRightBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.RIGHT);
    }
    
    public short getRotation() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        return (short)((align == null) ? 0L : align.getTextRotation());
    }
    
    public boolean getShrinkToFit() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        return align != null && align.getShrinkToFit();
    }
    
    public short getTopBorderColor() {
        final XSSFColor clr = this.getTopBorderXSSFColor();
        return (clr == null) ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }
    
    public XSSFColor getTopBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        final int idx = Math.toIntExact(this._cellXf.getBorderId());
        final XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.TOP);
    }
    
    public VerticalAlignment getVerticalAlignment() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        if (align != null && align.isSetVertical()) {
            return VerticalAlignment.forInt(align.getVertical().intValue() - 1);
        }
        return VerticalAlignment.BOTTOM;
    }
    
    public VerticalAlignment getVerticalAlignmentEnum() {
        return this.getVerticalAlignment();
    }
    
    public boolean getWrapText() {
        final CTCellAlignment align = this._cellXf.getAlignment();
        return align != null && align.getWrapText();
    }
    
    public void setAlignment(final HorizontalAlignment align) {
        this.getCellAlignment().setHorizontal(align);
    }
    
    public void setBorderBottom(final BorderStyle border) {
        final CTBorder ct = this.getCTBorder();
        final CTBorderPr pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
        if (border == BorderStyle.NONE) {
            ct.unsetBottom();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setBorderLeft(final BorderStyle border) {
        final CTBorder ct = this.getCTBorder();
        final CTBorderPr pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
        if (border == BorderStyle.NONE) {
            ct.unsetLeft();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setBorderRight(final BorderStyle border) {
        final CTBorder ct = this.getCTBorder();
        final CTBorderPr pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
        if (border == BorderStyle.NONE) {
            ct.unsetRight();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setBorderTop(final BorderStyle border) {
        final CTBorder ct = this.getCTBorder();
        final CTBorderPr pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
        if (border == BorderStyle.NONE) {
            ct.unsetTop();
        }
        else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setBottomBorderColor(final short color) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(color);
        this.setBottomBorderColor(clr);
    }
    
    public void setBottomBorderColor(final XSSFColor color) {
        final CTBorder ct = this.getCTBorder();
        if (color == null && !ct.isSetBottom()) {
            return;
        }
        final CTBorderPr pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
        if (color != null) {
            pr.setColor(color.getCTColor());
        }
        else {
            pr.unsetColor();
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setDataFormat(final short fmt) {
        this.setDataFormat(fmt & 0xFFFF);
    }
    
    public void setDataFormat(final int fmt) {
        this._cellXf.setApplyNumberFormat(true);
        this._cellXf.setNumFmtId((long)fmt);
    }
    
    public void setFillBackgroundColor(final XSSFColor color) {
        final CTFill ct = this.getCTFill();
        CTPatternFill ptrn = ct.getPatternFill();
        if (color == null) {
            if (ptrn != null && ptrn.isSetBgColor()) {
                ptrn.unsetBgColor();
            }
        }
        else {
            if (ptrn == null) {
                ptrn = ct.addNewPatternFill();
            }
            ptrn.setBgColor(color.getCTColor());
        }
        this.addFill(ct);
    }
    
    public void setFillBackgroundColor(final short bg) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(bg);
        this.setFillBackgroundColor(clr);
    }
    
    public void setFillForegroundColor(final XSSFColor color) {
        final CTFill ct = this.getCTFill();
        CTPatternFill ptrn = ct.getPatternFill();
        if (color == null) {
            if (ptrn != null && ptrn.isSetFgColor()) {
                ptrn.unsetFgColor();
            }
        }
        else {
            if (ptrn == null) {
                ptrn = ct.addNewPatternFill();
            }
            ptrn.setFgColor(color.getCTColor());
        }
        this.addFill(ct);
    }
    
    public void setFillForegroundColor(final short fg) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(fg);
        this.setFillForegroundColor(clr);
    }
    
    private CTFill getCTFill() {
        CTFill ct;
        if (!this._cellXf.isSetApplyFill() || this._cellXf.getApplyFill()) {
            final int fillIndex = (int)this._cellXf.getFillId();
            final XSSFCellFill cf = this._stylesSource.getFillAt(fillIndex);
            ct = (CTFill)cf.getCTFill().copy();
        }
        else {
            ct = CTFill.Factory.newInstance();
        }
        return ct;
    }
    
    public void setReadingOrder(final ReadingOrder order) {
        this.getCellAlignment().setReadingOrder(order);
    }
    
    public ReadingOrder getReadingOrder() {
        return this.getCellAlignment().getReadingOrder();
    }
    
    private CTBorder getCTBorder() {
        CTBorder ct;
        if (this._cellXf.getApplyBorder()) {
            final int idx = Math.toIntExact(this._cellXf.getBorderId());
            final XSSFCellBorder cf = this._stylesSource.getBorderAt(idx);
            ct = (CTBorder)cf.getCTBorder().copy();
        }
        else {
            ct = CTBorder.Factory.newInstance();
        }
        return ct;
    }
    
    public void setFillPattern(final FillPatternType pattern) {
        final CTFill ct = this.getCTFill();
        final CTPatternFill ctptrn = ct.isSetPatternFill() ? ct.getPatternFill() : ct.addNewPatternFill();
        if (pattern == FillPatternType.NO_FILL && ctptrn.isSetPatternType()) {
            ctptrn.unsetPatternType();
        }
        else {
            ctptrn.setPatternType(STPatternType.Enum.forInt(pattern.getCode() + 1));
        }
        this.addFill(ct);
    }
    
    public void setFont(final Font font) {
        if (font != null) {
            final long index = font.getIndexAsInt();
            this._cellXf.setFontId(index);
            this._cellXf.setApplyFont(true);
        }
        else {
            this._cellXf.setApplyFont(false);
        }
    }
    
    public void setHidden(final boolean hidden) {
        if (!this._cellXf.isSetProtection()) {
            this._cellXf.addNewProtection();
        }
        this._cellXf.getProtection().setHidden(hidden);
    }
    
    public void setIndention(final short indent) {
        this.getCellAlignment().setIndent(indent);
    }
    
    public void setLeftBorderColor(final short color) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(color);
        this.setLeftBorderColor(clr);
    }
    
    public void setLeftBorderColor(final XSSFColor color) {
        final CTBorder ct = this.getCTBorder();
        if (color == null && !ct.isSetLeft()) {
            return;
        }
        final CTBorderPr pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
        if (color != null) {
            pr.setColor(color.getCTColor());
        }
        else {
            pr.unsetColor();
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setLocked(final boolean locked) {
        if (!this._cellXf.isSetProtection()) {
            this._cellXf.addNewProtection();
        }
        this._cellXf.getProtection().setLocked(locked);
    }
    
    public void setQuotePrefixed(final boolean quotePrefix) {
        this._cellXf.setQuotePrefix(quotePrefix);
    }
    
    public void setRightBorderColor(final short color) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(color);
        this.setRightBorderColor(clr);
    }
    
    public void setRightBorderColor(final XSSFColor color) {
        final CTBorder ct = this.getCTBorder();
        if (color == null && !ct.isSetRight()) {
            return;
        }
        final CTBorderPr pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
        if (color != null) {
            pr.setColor(color.getCTColor());
        }
        else {
            pr.unsetColor();
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setRotation(final short rotation) {
        this.getCellAlignment().setTextRotation(rotation);
    }
    
    public void setTopBorderColor(final short color) {
        final XSSFColor clr = XSSFColor.from(CTColor.Factory.newInstance(), this._stylesSource.getIndexedColors());
        clr.setIndexed(color);
        this.setTopBorderColor(clr);
    }
    
    public void setTopBorderColor(final XSSFColor color) {
        final CTBorder ct = this.getCTBorder();
        if (color == null && !ct.isSetTop()) {
            return;
        }
        final CTBorderPr pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
        if (color != null) {
            pr.setColor(color.getCTColor());
        }
        else {
            pr.unsetColor();
        }
        final int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId((long)idx);
        this._cellXf.setApplyBorder(true);
    }
    
    public void setVerticalAlignment(final VerticalAlignment align) {
        this.getCellAlignment().setVertical(align);
    }
    
    public void setWrapText(final boolean wrapped) {
        this.getCellAlignment().setWrapText(wrapped);
    }
    
    public XSSFColor getBorderColor(final XSSFCellBorder.BorderSide side) {
        switch (side) {
            case BOTTOM: {
                return this.getBottomBorderXSSFColor();
            }
            case RIGHT: {
                return this.getRightBorderXSSFColor();
            }
            case TOP: {
                return this.getTopBorderXSSFColor();
            }
            case LEFT: {
                return this.getLeftBorderXSSFColor();
            }
            default: {
                throw new IllegalArgumentException("Unknown border: " + side);
            }
        }
    }
    
    public void setBorderColor(final XSSFCellBorder.BorderSide side, final XSSFColor color) {
        switch (side) {
            case BOTTOM: {
                this.setBottomBorderColor(color);
                break;
            }
            case RIGHT: {
                this.setRightBorderColor(color);
                break;
            }
            case TOP: {
                this.setTopBorderColor(color);
                break;
            }
            case LEFT: {
                this.setLeftBorderColor(color);
                break;
            }
        }
    }
    
    public void setShrinkToFit(final boolean shrinkToFit) {
        this.getCellAlignment().setShrinkToFit(shrinkToFit);
    }
    
    private int getFontId() {
        if (this._cellXf.isSetFontId()) {
            return (int)this._cellXf.getFontId();
        }
        return (int)this._cellStyleXf.getFontId();
    }
    
    protected XSSFCellAlignment getCellAlignment() {
        if (this._cellAlignment == null) {
            this._cellAlignment = new XSSFCellAlignment(this.getCTCellAlignment());
        }
        return this._cellAlignment;
    }
    
    private CTCellAlignment getCTCellAlignment() {
        if (this._cellXf.getAlignment() == null) {
            this._cellXf.setAlignment(CTCellAlignment.Factory.newInstance());
        }
        return this._cellXf.getAlignment();
    }
    
    @Override
    public int hashCode() {
        return this._cellXf.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof XSSFCellStyle)) {
            return false;
        }
        final XSSFCellStyle cf = (XSSFCellStyle)o;
        return this._cellXf.toString().equals(cf.getCoreXf().toString());
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public XSSFCellStyle clone() {
        return this.copy();
    }
    
    public XSSFCellStyle copy() {
        final CTXf xf = (CTXf)this._cellXf.copy();
        final int xfSize = this._stylesSource._getStyleXfsSize();
        final int indexXf = this._stylesSource.putCellXf(xf);
        return new XSSFCellStyle(indexXf - 1, xfSize - 1, this._stylesSource, this._theme);
    }
}
