package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleTextStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.apache.poi.sl.usermodel.TextShape;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleCellStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTablePartStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import java.awt.Color;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;
import org.openxmlformats.schemas.drawingml.x2006.main.STPenAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellProperties;
import org.apache.poi.sl.usermodel.TableCell;

public class XSLFTableCell extends XSLFTextShape implements TableCell<XSLFShape, XSLFTextParagraph>
{
    private CTTableCellProperties _tcPr;
    private final XSLFTable table;
    private int row;
    private int col;
    private Rectangle2D anchor;
    
    XSLFTableCell(final CTTableCell cell, final XSLFTable table) {
        super((XmlObject)cell, table.getSheet());
        this.table = table;
    }
    
    @Override
    protected CTTextBody getTextBody(final boolean create) {
        final CTTableCell cell = this.getCell();
        CTTextBody txBody = cell.getTxBody();
        if (txBody == null && create) {
            final XDDFTextBody body = new XDDFTextBody(this);
            cell.setTxBody(body.getXmlObject());
            txBody = cell.getTxBody();
        }
        return txBody;
    }
    
    static CTTableCell prototype() {
        final CTTableCell cell = CTTableCell.Factory.newInstance();
        final CTTableCellProperties pr = cell.addNewTcPr();
        pr.addNewLnL().addNewNoFill();
        pr.addNewLnR().addNewNoFill();
        pr.addNewLnT().addNewNoFill();
        pr.addNewLnB().addNewNoFill();
        return cell;
    }
    
    protected CTTableCellProperties getCellProperties(final boolean create) {
        if (this._tcPr == null) {
            final CTTableCell cell = this.getCell();
            this._tcPr = cell.getTcPr();
            if (this._tcPr == null && create) {
                this._tcPr = cell.addNewTcPr();
            }
        }
        return this._tcPr;
    }
    
    @Override
    public void setLeftInset(final double margin) {
        final CTTableCellProperties pr = this.getCellProperties(true);
        pr.setMarL(Units.toEMU(margin));
    }
    
    @Override
    public void setRightInset(final double margin) {
        final CTTableCellProperties pr = this.getCellProperties(true);
        pr.setMarR(Units.toEMU(margin));
    }
    
    @Override
    public void setTopInset(final double margin) {
        final CTTableCellProperties pr = this.getCellProperties(true);
        pr.setMarT(Units.toEMU(margin));
    }
    
    @Override
    public void setBottomInset(final double margin) {
        final CTTableCellProperties pr = this.getCellProperties(true);
        pr.setMarB(Units.toEMU(margin));
    }
    
    private CTLineProperties getCTLine(final TableCell.BorderEdge edge, final boolean create) {
        if (edge == null) {
            throw new IllegalArgumentException("BorderEdge needs to be specified.");
        }
        final CTTableCellProperties pr = this.getCellProperties(create);
        if (pr == null) {
            return null;
        }
        switch (edge) {
            case bottom: {
                return pr.isSetLnB() ? pr.getLnB() : (create ? pr.addNewLnB() : null);
            }
            case left: {
                return pr.isSetLnL() ? pr.getLnL() : (create ? pr.addNewLnL() : null);
            }
            case top: {
                return pr.isSetLnT() ? pr.getLnT() : (create ? pr.addNewLnT() : null);
            }
            case right: {
                return pr.isSetLnR() ? pr.getLnR() : (create ? pr.addNewLnR() : null);
            }
            default: {
                return null;
            }
        }
    }
    
    public void removeBorder(final TableCell.BorderEdge edge) {
        final CTTableCellProperties pr = this.getCellProperties(false);
        if (pr == null) {
            return;
        }
        switch (edge) {
            case bottom: {
                if (pr.isSetLnB()) {
                    pr.unsetLnB();
                    break;
                }
                break;
            }
            case left: {
                if (pr.isSetLnL()) {
                    pr.unsetLnL();
                    break;
                }
                break;
            }
            case top: {
                if (pr.isSetLnT()) {
                    pr.unsetLnT();
                    break;
                }
                break;
            }
            case right: {
                if (pr.isSetLnR()) {
                    pr.unsetLnR();
                    break;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public StrokeStyle getBorderStyle(final TableCell.BorderEdge edge) {
        final Double width = this.getBorderWidth(edge);
        return (StrokeStyle)((width == null) ? null : new StrokeStyle() {
            public PaintStyle getPaint() {
                return (PaintStyle)DrawPaint.createSolidPaint(XSLFTableCell.this.getBorderColor(edge));
            }
            
            public StrokeStyle.LineCap getLineCap() {
                return XSLFTableCell.this.getBorderCap(edge);
            }
            
            public StrokeStyle.LineDash getLineDash() {
                return XSLFTableCell.this.getBorderDash(edge);
            }
            
            public StrokeStyle.LineCompound getLineCompound() {
                return XSLFTableCell.this.getBorderCompound(edge);
            }
            
            public double getLineWidth() {
                return width;
            }
        });
    }
    
    public void setBorderStyle(final TableCell.BorderEdge edge, final StrokeStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("StrokeStyle needs to be specified.");
        }
        final StrokeStyle.LineCap cap = style.getLineCap();
        if (cap != null) {
            this.setBorderCap(edge, cap);
        }
        final StrokeStyle.LineCompound compound = style.getLineCompound();
        if (compound != null) {
            this.setBorderCompound(edge, compound);
        }
        final StrokeStyle.LineDash dash = style.getLineDash();
        if (dash != null) {
            this.setBorderDash(edge, dash);
        }
        final double width = style.getLineWidth();
        this.setBorderWidth(edge, width);
    }
    
    public Double getBorderWidth(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, false);
        return (ln == null || !ln.isSetW()) ? null : Double.valueOf(Units.toPoints((long)ln.getW()));
    }
    
    public void setBorderWidth(final TableCell.BorderEdge edge, final double width) {
        final CTLineProperties ln = this.getCTLine(edge, true);
        if (ln == null) {
            return;
        }
        ln.setW(Units.toEMU(width));
    }
    
    private CTLineProperties setBorderDefaults(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, true);
        if (ln == null) {
            throw new IllegalStateException("CTLineProperties couldn't be initialized");
        }
        if (ln.isSetNoFill()) {
            ln.unsetNoFill();
        }
        if (!ln.isSetPrstDash()) {
            ln.addNewPrstDash().setVal(STPresetLineDashVal.SOLID);
        }
        if (!ln.isSetCmpd()) {
            ln.setCmpd(STCompoundLine.SNG);
        }
        if (!ln.isSetAlgn()) {
            ln.setAlgn(STPenAlignment.CTR);
        }
        if (!ln.isSetCap()) {
            ln.setCap(STLineCap.FLAT);
        }
        if (!ln.isSetRound()) {
            ln.addNewRound();
        }
        if (!ln.isSetHeadEnd()) {
            final CTLineEndProperties hd = ln.addNewHeadEnd();
            hd.setType(STLineEndType.NONE);
            hd.setW(STLineEndWidth.MED);
            hd.setLen(STLineEndLength.MED);
        }
        if (!ln.isSetTailEnd()) {
            final CTLineEndProperties tl = ln.addNewTailEnd();
            tl.setType(STLineEndType.NONE);
            tl.setW(STLineEndWidth.MED);
            tl.setLen(STLineEndLength.MED);
        }
        return ln;
    }
    
    public void setBorderColor(final TableCell.BorderEdge edge, final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Colors need to be specified.");
        }
        final CTLineProperties ln = this.setBorderDefaults(edge);
        final CTSolidColorFillProperties fill = ln.addNewSolidFill();
        final XSLFColor c = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
        c.setColor(color);
    }
    
    public Color getBorderColor(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, false);
        if (ln == null || ln.isSetNoFill() || !ln.isSetSolidFill()) {
            return null;
        }
        final CTSolidColorFillProperties fill = ln.getSolidFill();
        final XSLFColor c = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
        return c.getColor();
    }
    
    public StrokeStyle.LineCompound getBorderCompound(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, false);
        if (ln == null || ln.isSetNoFill() || !ln.isSetSolidFill() || !ln.isSetCmpd()) {
            return null;
        }
        return StrokeStyle.LineCompound.fromOoxmlId(ln.getCmpd().intValue());
    }
    
    public void setBorderCompound(final TableCell.BorderEdge edge, final StrokeStyle.LineCompound compound) {
        if (compound == null) {
            throw new IllegalArgumentException("LineCompound need to be specified.");
        }
        final CTLineProperties ln = this.setBorderDefaults(edge);
        ln.setCmpd(STCompoundLine.Enum.forInt(compound.ooxmlId));
    }
    
    public StrokeStyle.LineDash getBorderDash(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, false);
        if (ln == null || ln.isSetNoFill() || !ln.isSetSolidFill() || !ln.isSetPrstDash()) {
            return null;
        }
        return StrokeStyle.LineDash.fromOoxmlId(ln.getPrstDash().getVal().intValue());
    }
    
    public void setBorderDash(final TableCell.BorderEdge edge, final StrokeStyle.LineDash dash) {
        if (dash == null) {
            throw new IllegalArgumentException("LineDash need to be specified.");
        }
        final CTLineProperties ln = this.setBorderDefaults(edge);
        if (!ln.isSetPrstDash()) {
            ln.addNewPrstDash();
        }
        ln.getPrstDash().setVal(STPresetLineDashVal.Enum.forInt(dash.ooxmlId));
    }
    
    public StrokeStyle.LineCap getBorderCap(final TableCell.BorderEdge edge) {
        final CTLineProperties ln = this.getCTLine(edge, false);
        if (ln == null || ln.isSetNoFill() || !ln.isSetSolidFill() || !ln.isSetCap()) {
            return null;
        }
        return StrokeStyle.LineCap.fromOoxmlId(ln.getCap().intValue());
    }
    
    public void setBorderCap(final TableCell.BorderEdge edge, final StrokeStyle.LineCap cap) {
        if (cap == null) {
            throw new IllegalArgumentException("LineCap need to be specified.");
        }
        final CTLineProperties ln = this.setBorderDefaults(edge);
        ln.setCap(STLineCap.Enum.forInt(cap.ooxmlId));
    }
    
    public void setFillColor(final Color color) {
        final CTTableCellProperties spPr = this.getCellProperties(true);
        if (color == null) {
            if (spPr.isSetSolidFill()) {
                spPr.unsetSolidFill();
            }
        }
        else {
            final CTSolidColorFillProperties fill = spPr.isSetSolidFill() ? spPr.getSolidFill() : spPr.addNewSolidFill();
            final XSLFColor c = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
            c.setColor(color);
        }
    }
    
    public Color getFillColor() {
        final PaintStyle ps = this.getFillPaint();
        if (ps instanceof PaintStyle.SolidPaint) {
            final ColorStyle cs = ((PaintStyle.SolidPaint)ps).getSolidColor();
            return DrawPaint.applyColorTransform(cs);
        }
        return null;
    }
    
    public PaintStyle getFillPaint() {
        final XSLFSheet sheet = this.getSheet();
        final XSLFTheme theme = sheet.getTheme();
        final boolean hasPlaceholder = this.getPlaceholder() != null;
        XmlObject props = (XmlObject)this.getCellProperties(false);
        XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(props);
        if (fp != null) {
            final PaintStyle paint = this.selectPaint(fp, null, sheet.getPackagePart(), theme, hasPlaceholder);
            if (paint != null) {
                return paint;
            }
        }
        CTTablePartStyle tps = this.getTablePartStyle(null);
        if (tps == null || !tps.isSetTcStyle()) {
            tps = this.getTablePartStyle(XSLFTableStyle.TablePartStyle.wholeTbl);
            if (tps == null || !tps.isSetTcStyle()) {
                return null;
            }
        }
        final XMLSlideShow slideShow = sheet.getSlideShow();
        final CTTableStyleCellStyle tcStyle = tps.getTcStyle();
        if (tcStyle.isSetFill()) {
            props = (XmlObject)tcStyle.getFill();
        }
        else {
            if (!tcStyle.isSetFillRef()) {
                return null;
            }
            props = (XmlObject)tcStyle.getFillRef();
        }
        fp = XSLFPropertiesDelegate.getFillDelegate(props);
        if (fp != null) {
            final PaintStyle paint2 = this.selectPaint(fp, null, slideShow.getPackagePart(), theme, hasPlaceholder);
            if (paint2 != null) {
                return paint2;
            }
        }
        return null;
    }
    
    private CTTablePartStyle getTablePartStyle(final XSLFTableStyle.TablePartStyle tablePartStyle) {
        final CTTable ct = this.table.getCTTable();
        if (!ct.isSetTblPr()) {
            return null;
        }
        final CTTableProperties pr = ct.getTblPr();
        final boolean bandRow = pr.isSetBandRow() && pr.getBandRow();
        final boolean firstRow = pr.isSetFirstRow() && pr.getFirstRow();
        final boolean lastRow = pr.isSetLastRow() && pr.getLastRow();
        final boolean bandCol = pr.isSetBandCol() && pr.getBandCol();
        final boolean firstCol = pr.isSetFirstCol() && pr.getFirstCol();
        final boolean lastCol = pr.isSetLastCol() && pr.getLastCol();
        XSLFTableStyle.TablePartStyle tps;
        if (tablePartStyle != null) {
            tps = tablePartStyle;
        }
        else if (this.row == 0 && firstRow) {
            tps = XSLFTableStyle.TablePartStyle.firstRow;
        }
        else if (this.row == this.table.getNumberOfRows() - 1 && lastRow) {
            tps = XSLFTableStyle.TablePartStyle.lastRow;
        }
        else if (this.col == 0 && firstCol) {
            tps = XSLFTableStyle.TablePartStyle.firstCol;
        }
        else if (this.col == this.table.getNumberOfColumns() - 1 && lastCol) {
            tps = XSLFTableStyle.TablePartStyle.lastCol;
        }
        else {
            tps = XSLFTableStyle.TablePartStyle.wholeTbl;
            final int br = this.row + (firstRow ? 1 : 0);
            final int bc = this.col + (firstCol ? 1 : 0);
            if (bandRow && (br & 0x1) == 0x0) {
                tps = XSLFTableStyle.TablePartStyle.band1H;
            }
            else if (bandCol && (bc & 0x1) == 0x0) {
                tps = XSLFTableStyle.TablePartStyle.band1V;
            }
        }
        final XSLFTableStyle tabStyle = this.table.getTableStyle();
        if (tabStyle == null) {
            return null;
        }
        final CTTablePartStyle part = tabStyle.getTablePartStyle(tps);
        return (part == null) ? tabStyle.getTablePartStyle(XSLFTableStyle.TablePartStyle.wholeTbl) : part;
    }
    
    void setGridSpan(final int gridSpan_) {
        this.getCell().setGridSpan(gridSpan_);
    }
    
    public int getGridSpan() {
        final CTTableCell c = this.getCell();
        return c.isSetGridSpan() ? c.getGridSpan() : 1;
    }
    
    void setRowSpan(final int rowSpan_) {
        this.getCell().setRowSpan(rowSpan_);
    }
    
    public int getRowSpan() {
        final CTTableCell c = this.getCell();
        return c.isSetRowSpan() ? c.getRowSpan() : 1;
    }
    
    void setHMerge() {
        this.getCell().setHMerge(true);
    }
    
    void setVMerge() {
        this.getCell().setVMerge(true);
    }
    
    @Override
    public void setVerticalAlignment(final VerticalAlignment anchor) {
        final CTTableCellProperties cellProps = this.getCellProperties(true);
        if (anchor == null) {
            if (cellProps.isSetAnchor()) {
                cellProps.unsetAnchor();
            }
        }
        else {
            cellProps.setAnchor(STTextAnchoringType.Enum.forInt(anchor.ordinal() + 1));
        }
    }
    
    @Override
    public VerticalAlignment getVerticalAlignment() {
        final CTTableCellProperties cellProps = this.getCellProperties(false);
        VerticalAlignment align = VerticalAlignment.TOP;
        if (cellProps != null && cellProps.isSetAnchor()) {
            final int ival = cellProps.getAnchor().intValue();
            align = VerticalAlignment.values()[ival - 1];
        }
        return align;
    }
    
    @Override
    public void setTextDirection(final TextShape.TextDirection orientation) {
        final CTTableCellProperties cellProps = this.getCellProperties(true);
        if (orientation == null) {
            if (cellProps.isSetVert()) {
                cellProps.unsetVert();
            }
        }
        else {
            STTextVerticalType.Enum vt = null;
            switch (orientation) {
                default: {
                    vt = STTextVerticalType.HORZ;
                    break;
                }
                case VERTICAL: {
                    vt = STTextVerticalType.VERT;
                    break;
                }
                case VERTICAL_270: {
                    vt = STTextVerticalType.VERT_270;
                    break;
                }
                case STACKED: {
                    vt = STTextVerticalType.WORD_ART_VERT;
                    break;
                }
            }
            cellProps.setVert(vt);
        }
    }
    
    @Override
    public TextShape.TextDirection getTextDirection() {
        final CTTableCellProperties cellProps = this.getCellProperties(false);
        STTextVerticalType.Enum orientation;
        if (cellProps != null && cellProps.isSetVert()) {
            orientation = cellProps.getVert();
        }
        else {
            orientation = STTextVerticalType.HORZ;
        }
        switch (orientation.intValue()) {
            default: {
                return TextShape.TextDirection.HORIZONTAL;
            }
            case 2:
            case 5:
            case 6: {
                return TextShape.TextDirection.VERTICAL;
            }
            case 3: {
                return TextShape.TextDirection.VERTICAL_270;
            }
            case 4:
            case 7: {
                return TextShape.TextDirection.STACKED;
            }
        }
    }
    
    private CTTableCell getCell() {
        return (CTTableCell)this.getXmlObject();
    }
    
    void setRowColIndex(final int row, final int col) {
        this.row = row;
        this.col = col;
    }
    
    protected CTTransform2D getXfrm() {
        final Rectangle2D anc = this.getAnchor();
        final CTTransform2D xfrm = CTTransform2D.Factory.newInstance();
        final CTPoint2D off = xfrm.addNewOff();
        off.setX((long)Units.toEMU(anc.getX()));
        off.setY((long)Units.toEMU(anc.getY()));
        final CTPositiveSize2D size = xfrm.addNewExt();
        size.setCx((long)Units.toEMU(anc.getWidth()));
        size.setCy((long)Units.toEMU(anc.getHeight()));
        return xfrm;
    }
    
    public void setAnchor(final Rectangle2D anchor) {
        if (this.anchor == null) {
            this.anchor = (Rectangle2D)anchor.clone();
        }
        else {
            this.anchor.setRect(anchor);
        }
    }
    
    public Rectangle2D getAnchor() {
        if (this.anchor == null) {
            this.table.updateCellAnchor();
        }
        assert this.anchor != null;
        return this.anchor;
    }
    
    public boolean isMerged() {
        final CTTableCell c = this.getCell();
        return (c.isSetHMerge() && c.getHMerge()) || (c.isSetVMerge() && c.getVMerge());
    }
    
    @Override
    protected XSLFCellTextParagraph newTextParagraph(final CTTextParagraph p) {
        return new XSLFCellTextParagraph(p, (XSLFTextShape)this);
    }
    
    protected XmlObject getShapeProperties() {
        return (XmlObject)this.getCellProperties(false);
    }
    
    private final class XSLFCellTextParagraph extends XSLFTextParagraph
    {
        private XSLFCellTextParagraph(final CTTextParagraph p, final XSLFTextShape shape) {
            super(p, shape);
        }
        
        @Override
        protected XSLFCellTextRun newTextRun(final XmlObject r) {
            return new XSLFCellTextRun(r, (XSLFTextParagraph)this);
        }
    }
    
    private final class XSLFCellTextRun extends XSLFTextRun
    {
        private XSLFCellTextRun(final XmlObject r, final XSLFTextParagraph p) {
            super(r, p);
        }
        
        @Override
        public PaintStyle getFontColor() {
            final CTTableStyleTextStyle txStyle = this.getTextStyle();
            if (txStyle == null) {
                return super.getFontColor();
            }
            CTSchemeColor phClr = null;
            final CTFontReference fontRef = txStyle.getFontRef();
            if (fontRef != null) {
                phClr = fontRef.getSchemeClr();
            }
            final XSLFTheme theme = XSLFTableCell.this.getSheet().getTheme();
            final XSLFColor c = new XSLFColor((XmlObject)txStyle, theme, phClr, XSLFTableCell.this.getSheet());
            return (PaintStyle)DrawPaint.createSolidPaint(c.getColorStyle());
        }
        
        @Override
        public boolean isBold() {
            final CTTableStyleTextStyle txStyle = this.getTextStyle();
            if (txStyle == null) {
                return super.isBold();
            }
            return txStyle.isSetB() && txStyle.getB().intValue() == 1;
        }
        
        @Override
        public boolean isItalic() {
            final CTTableStyleTextStyle txStyle = this.getTextStyle();
            if (txStyle == null) {
                return super.isItalic();
            }
            return txStyle.isSetI() && txStyle.getI().intValue() == 1;
        }
        
        private CTTableStyleTextStyle getTextStyle() {
            CTTablePartStyle tps = XSLFTableCell.this.getTablePartStyle(null);
            if (tps == null || !tps.isSetTcTxStyle()) {
                tps = XSLFTableCell.this.getTablePartStyle(XSLFTableStyle.TablePartStyle.wholeTbl);
            }
            return (tps == null) ? null : tps.getTcTxStyle();
        }
    }
}
