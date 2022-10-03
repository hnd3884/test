package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.util.Collections;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import java.util.function.Consumer;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import java.util.function.Function;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import java.math.BigInteger;
import java.util.Iterator;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import java.util.List;
import java.util.HashMap;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import java.util.EnumMap;

public class XWPFTable implements IBodyElement, ISDTContents
{
    public static final String REGEX_PERCENTAGE = "[0-9]+(\\.[0-9]+)?%";
    public static final String DEFAULT_PERCENTAGE_WIDTH = "100%";
    static final String NS_OOXML_WP_MAIN = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    public static final String REGEX_WIDTH_VALUE = "auto|[0-9]+|[0-9]+(\\.[0-9]+)?%";
    private static final EnumMap<XWPFBorderType, STBorder.Enum> xwpfBorderTypeMap;
    private static final HashMap<Integer, XWPFBorderType> stBorderTypeMap;
    protected StringBuilder text;
    protected final List<XWPFTableRow> tableRows;
    protected IBody part;
    private CTTbl ctTbl;
    
    public XWPFTable(final CTTbl table, final IBody part, final int row, final int col) {
        this(table, part);
        for (int i = 0; i < row; ++i) {
            final XWPFTableRow tabRow = (this.getRow(i) == null) ? this.createRow() : this.getRow(i);
            for (int k = 0; k < col; ++k) {
                if (tabRow.getCell(k) == null) {
                    tabRow.createCell();
                }
            }
        }
    }
    
    public XWPFTable(final CTTbl table, final IBody part) {
        this.text = new StringBuilder(64);
        this.tableRows = new ArrayList<XWPFTableRow>();
        this.part = part;
        this.ctTbl = table;
        if (table.sizeOfTrArray() == 0) {
            this.createEmptyTable(table);
        }
        for (final CTRow row : table.getTrList()) {
            final StringBuilder rowText = new StringBuilder();
            final XWPFTableRow tabRow = new XWPFTableRow(row, this);
            this.tableRows.add(tabRow);
            for (final CTTc cell : row.getTcList()) {
                for (final CTP ctp : cell.getPList()) {
                    final XWPFParagraph p = new XWPFParagraph(ctp, part);
                    if (rowText.length() > 0) {
                        rowText.append('\t');
                    }
                    rowText.append(p.getText());
                }
            }
            if (rowText.length() > 0) {
                this.text.append((CharSequence)rowText);
                this.text.append('\n');
            }
        }
    }
    
    private void createEmptyTable(final CTTbl table) {
        table.addNewTr().addNewTc().addNewP();
        final CTTblPr tblpro = table.addNewTblPr();
        tblpro.addNewTblW().setW(new BigInteger("0"));
        tblpro.getTblW().setType(STTblWidth.AUTO);
        final CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);
    }
    
    @Internal
    public CTTbl getCTTbl() {
        return this.ctTbl;
    }
    
    public String getText() {
        return this.text.toString();
    }
    
    @Deprecated
    @Removal
    @NotImplemented
    public void addNewRowBetween(final int start, final int end) {
        throw new UnsupportedOperationException("XWPFTable#addNewRowBetween(int, int) not implemented");
    }
    
    public void addNewCol() {
        if (this.ctTbl.sizeOfTrArray() == 0) {
            this.createRow();
        }
        for (int i = 0; i < this.ctTbl.sizeOfTrArray(); ++i) {
            final XWPFTableRow tabRow = new XWPFTableRow(this.ctTbl.getTrArray(i), this);
            tabRow.createCell();
        }
    }
    
    public XWPFTableRow createRow() {
        final int sizeCol = (this.ctTbl.sizeOfTrArray() > 0) ? this.ctTbl.getTrArray(0).sizeOfTcArray() : 0;
        final XWPFTableRow tabRow = new XWPFTableRow(this.ctTbl.addNewTr(), this);
        this.addColumn(tabRow, sizeCol);
        this.tableRows.add(tabRow);
        return tabRow;
    }
    
    public XWPFTableRow getRow(final int pos) {
        if (pos >= 0 && pos < this.ctTbl.sizeOfTrArray()) {
            return this.getRows().get(pos);
        }
        return null;
    }
    
    public int getWidth() {
        final CTTblPr tblPr = this.getTblPr();
        return tblPr.isSetTblW() ? tblPr.getTblW().getW().intValue() : -1;
    }
    
    public void setWidth(final int width) {
        final CTTblPr tblPr = this.getTblPr();
        final CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(new BigInteger(Integer.toString(width)));
        tblWidth.setType(STTblWidth.DXA);
    }
    
    public int getNumberOfRows() {
        return this.ctTbl.sizeOfTrArray();
    }
    
    private CTTblPr getTblPr() {
        return this.getTblPr(true);
    }
    
    private CTTblPr getTblPr(final boolean force) {
        return (this.ctTbl.getTblPr() != null) ? this.ctTbl.getTblPr() : (force ? this.ctTbl.addNewTblPr() : null);
    }
    
    private CTTblBorders getTblBorders(final boolean force) {
        final CTTblPr tblPr = this.getTblPr(force);
        return (tblPr == null) ? null : (tblPr.isSetTblBorders() ? tblPr.getTblBorders() : (force ? tblPr.addNewTblBorders() : null));
    }
    
    private CTBorder getTblBorder(final boolean force, final Border border) {
        switch (border) {
            case INSIDE_V: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetInsideV;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getInsideV;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewInsideV;
                break;
            }
            case INSIDE_H: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetInsideH;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getInsideH;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewInsideH;
                break;
            }
            case LEFT: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetLeft;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getLeft;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewLeft;
                break;
            }
            case TOP: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetTop;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getTop;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewTop;
                break;
            }
            case RIGHT: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetRight;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getRight;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewRight;
                break;
            }
            case BOTTOM: {
                final Function<CTTblBorders, Boolean> isSet = CTTblBorders::isSetBottom;
                final Function<CTTblBorders, CTBorder> get = CTTblBorders::getBottom;
                final Function<CTTblBorders, CTBorder> addNew = CTTblBorders::addNewBottom;
                break;
            }
            default: {
                return null;
            }
        }
        final CTTblBorders ctb = this.getTblBorders(force);
        Function<CTTblBorders, Boolean> isSet;
        Function<CTTblBorders, CTBorder> get;
        Function<CTTblBorders, CTBorder> addNew;
        return (ctb == null) ? null : (isSet.apply(ctb) ? get.apply(ctb) : (force ? addNew.apply(ctb) : null));
    }
    
    public TableRowAlign getTableAlignment() {
        final CTTblPr tPr = this.getTblPr(false);
        return (tPr == null) ? null : (tPr.isSetJc() ? TableRowAlign.valueOf(tPr.getJc().getVal().intValue()) : null);
    }
    
    public void setTableAlignment(final TableRowAlign tra) {
        final CTTblPr tPr = this.getTblPr(true);
        final CTJc jc = tPr.isSetJc() ? tPr.getJc() : tPr.addNewJc();
        jc.setVal(STJc.Enum.forInt(tra.getValue()));
    }
    
    public void removeTableAlignment() {
        final CTTblPr tPr = this.getTblPr(false);
        if (tPr != null && tPr.isSetJc()) {
            tPr.unsetJc();
        }
    }
    
    private void addColumn(final XWPFTableRow tabRow, final int sizeCol) {
        if (sizeCol > 0) {
            for (int i = 0; i < sizeCol; ++i) {
                tabRow.createCell();
            }
        }
    }
    
    public String getStyleID() {
        String styleId = null;
        final CTTblPr tblPr = this.ctTbl.getTblPr();
        if (tblPr != null) {
            final CTString styleStr = tblPr.getTblStyle();
            if (styleStr != null) {
                styleId = styleStr.getVal();
            }
        }
        return styleId;
    }
    
    public void setStyleID(final String styleName) {
        final CTTblPr tblPr = this.getTblPr();
        CTString styleStr = tblPr.getTblStyle();
        if (styleStr == null) {
            styleStr = tblPr.addNewTblStyle();
        }
        styleStr.setVal(styleName);
    }
    
    public XWPFBorderType getInsideHBorderType() {
        return this.getBorderType(Border.INSIDE_H);
    }
    
    public int getInsideHBorderSize() {
        return this.getBorderSize(Border.INSIDE_H);
    }
    
    public int getInsideHBorderSpace() {
        return this.getBorderSpace(Border.INSIDE_H);
    }
    
    public String getInsideHBorderColor() {
        return this.getBorderColor(Border.INSIDE_H);
    }
    
    public XWPFBorderType getInsideVBorderType() {
        return this.getBorderType(Border.INSIDE_V);
    }
    
    public int getInsideVBorderSize() {
        return this.getBorderSize(Border.INSIDE_V);
    }
    
    public int getInsideVBorderSpace() {
        return this.getBorderSpace(Border.INSIDE_V);
    }
    
    public String getInsideVBorderColor() {
        return this.getBorderColor(Border.INSIDE_V);
    }
    
    public XWPFBorderType getTopBorderType() {
        return this.getBorderType(Border.TOP);
    }
    
    public int getTopBorderSize() {
        return this.getBorderSize(Border.TOP);
    }
    
    public int getTopBorderSpace() {
        return this.getBorderSpace(Border.TOP);
    }
    
    public String getTopBorderColor() {
        return this.getBorderColor(Border.TOP);
    }
    
    public XWPFBorderType getBottomBorderType() {
        return this.getBorderType(Border.BOTTOM);
    }
    
    public int getBottomBorderSize() {
        return this.getBorderSize(Border.BOTTOM);
    }
    
    public int getBottomBorderSpace() {
        return this.getBorderSpace(Border.BOTTOM);
    }
    
    public String getBottomBorderColor() {
        return this.getBorderColor(Border.BOTTOM);
    }
    
    public XWPFBorderType getLeftBorderType() {
        return this.getBorderType(Border.LEFT);
    }
    
    public int getLeftBorderSize() {
        return this.getBorderSize(Border.LEFT);
    }
    
    public int getLeftBorderSpace() {
        return this.getBorderSpace(Border.LEFT);
    }
    
    public String getLeftBorderColor() {
        return this.getBorderColor(Border.LEFT);
    }
    
    public XWPFBorderType getRightBorderType() {
        return this.getBorderType(Border.RIGHT);
    }
    
    public int getRightBorderSize() {
        return this.getBorderSize(Border.RIGHT);
    }
    
    public int getRightBorderSpace() {
        return this.getBorderSpace(Border.RIGHT);
    }
    
    public String getRightBorderColor() {
        return this.getBorderColor(Border.RIGHT);
    }
    
    private XWPFBorderType getBorderType(final Border border) {
        final CTBorder b = this.getTblBorder(false, border);
        return (b != null) ? XWPFTable.stBorderTypeMap.get(b.getVal().intValue()) : null;
    }
    
    private int getBorderSize(final Border border) {
        final CTBorder b = this.getTblBorder(false, border);
        return (b != null) ? (b.isSetSz() ? b.getSz().intValue() : -1) : -1;
    }
    
    private int getBorderSpace(final Border border) {
        final CTBorder b = this.getTblBorder(false, border);
        return (b != null) ? (b.isSetSpace() ? b.getSpace().intValue() : -1) : -1;
    }
    
    private String getBorderColor(final Border border) {
        final CTBorder b = this.getTblBorder(false, border);
        return (b != null) ? (b.isSetColor() ? b.xgetColor().getStringValue() : null) : null;
    }
    
    public int getRowBandSize() {
        int size = 0;
        final CTTblPr tblPr = this.getTblPr();
        if (tblPr.isSetTblStyleRowBandSize()) {
            final CTDecimalNumber rowSize = tblPr.getTblStyleRowBandSize();
            size = rowSize.getVal().intValue();
        }
        return size;
    }
    
    public void setRowBandSize(final int size) {
        final CTTblPr tblPr = this.getTblPr();
        final CTDecimalNumber rowSize = tblPr.isSetTblStyleRowBandSize() ? tblPr.getTblStyleRowBandSize() : tblPr.addNewTblStyleRowBandSize();
        rowSize.setVal(BigInteger.valueOf(size));
    }
    
    public int getColBandSize() {
        int size = 0;
        final CTTblPr tblPr = this.getTblPr();
        if (tblPr.isSetTblStyleColBandSize()) {
            final CTDecimalNumber colSize = tblPr.getTblStyleColBandSize();
            size = colSize.getVal().intValue();
        }
        return size;
    }
    
    public void setColBandSize(final int size) {
        final CTTblPr tblPr = this.getTblPr();
        final CTDecimalNumber colSize = tblPr.isSetTblStyleColBandSize() ? tblPr.getTblStyleColBandSize() : tblPr.addNewTblStyleColBandSize();
        colSize.setVal(BigInteger.valueOf(size));
    }
    
    public void setInsideHBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.INSIDE_H, type, size, space, rgbColor);
    }
    
    public void setInsideVBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.INSIDE_V, type, size, space, rgbColor);
    }
    
    public void setTopBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.TOP, type, size, space, rgbColor);
    }
    
    public void setBottomBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.BOTTOM, type, size, space, rgbColor);
    }
    
    public void setLeftBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.LEFT, type, size, space, rgbColor);
    }
    
    public void setRightBorder(final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        this.setBorder(Border.RIGHT, type, size, space, rgbColor);
    }
    
    private void setBorder(final Border border, final XWPFBorderType type, final int size, final int space, final String rgbColor) {
        final CTBorder b = this.getTblBorder(true, border);
        assert b != null;
        b.setVal((STBorder.Enum)XWPFTable.xwpfBorderTypeMap.get(type));
        b.setSz(BigInteger.valueOf(size));
        b.setSpace(BigInteger.valueOf(space));
        b.setColor((Object)rgbColor);
    }
    
    public void removeInsideHBorder() {
        this.removeBorder(Border.INSIDE_H);
    }
    
    public void removeInsideVBorder() {
        this.removeBorder(Border.INSIDE_V);
    }
    
    public void removeTopBorder() {
        this.removeBorder(Border.TOP);
    }
    
    public void removeBottomBorder() {
        this.removeBorder(Border.BOTTOM);
    }
    
    public void removeLeftBorder() {
        this.removeBorder(Border.LEFT);
    }
    
    public void removeRightBorder() {
        this.removeBorder(Border.RIGHT);
    }
    
    public void removeBorders() {
        final CTTblPr pr = this.getTblPr(false);
        if (pr != null && pr.isSetTblBorders()) {
            pr.unsetTblBorders();
        }
    }
    
    private void removeBorder(final Border border) {
        Function<CTTblBorders, Boolean> isSet = null;
        Consumer<CTTblBorders> unSet = null;
        switch (border) {
            case INSIDE_H: {
                isSet = CTTblBorders::isSetInsideH;
                unSet = CTTblBorders::unsetInsideH;
                break;
            }
            case INSIDE_V: {
                isSet = CTTblBorders::isSetInsideV;
                unSet = CTTblBorders::unsetInsideV;
                break;
            }
            case LEFT: {
                isSet = CTTblBorders::isSetLeft;
                unSet = CTTblBorders::unsetLeft;
                break;
            }
            case TOP: {
                isSet = CTTblBorders::isSetTop;
                unSet = CTTblBorders::unsetTop;
                break;
            }
            case RIGHT: {
                isSet = CTTblBorders::isSetRight;
                unSet = CTTblBorders::unsetRight;
                break;
            }
            case BOTTOM: {
                isSet = CTTblBorders::isSetBottom;
                unSet = CTTblBorders::unsetBottom;
                break;
            }
            default: {
                return;
            }
        }
        final CTTblBorders tbl = this.getTblBorders(false);
        if (tbl != null && isSet.apply(tbl)) {
            unSet.accept(tbl);
            this.cleanupTblBorders();
        }
    }
    
    private void cleanupTblBorders() {
        final CTTblPr pr = this.getTblPr(false);
        if (pr != null && pr.isSetTblBorders()) {
            final CTTblBorders b = pr.getTblBorders();
            if (!b.isSetInsideH() && !b.isSetInsideV() && !b.isSetTop() && !b.isSetBottom() && !b.isSetLeft() && !b.isSetRight()) {
                pr.unsetTblBorders();
            }
        }
    }
    
    public int getCellMarginTop() {
        return this.getCellMargin(CTTblCellMar::getTop);
    }
    
    public int getCellMarginLeft() {
        return this.getCellMargin(CTTblCellMar::getLeft);
    }
    
    public int getCellMarginBottom() {
        return this.getCellMargin(CTTblCellMar::getBottom);
    }
    
    public int getCellMarginRight() {
        return this.getCellMargin(CTTblCellMar::getRight);
    }
    
    private int getCellMargin(final Function<CTTblCellMar, CTTblWidth> margin) {
        final CTTblPr tblPr = this.getTblPr();
        final CTTblCellMar tcm = tblPr.getTblCellMar();
        if (tcm != null) {
            final CTTblWidth tw = margin.apply(tcm);
            if (tw != null) {
                return tw.getW().intValue();
            }
        }
        return 0;
    }
    
    public void setCellMargins(final int top, final int left, final int bottom, final int right) {
        final CTTblPr tblPr = this.getTblPr();
        final CTTblCellMar tcm = tblPr.isSetTblCellMar() ? tblPr.getTblCellMar() : tblPr.addNewTblCellMar();
        this.setCellMargin(tcm, CTTblCellMar::isSetTop, CTTblCellMar::getTop, CTTblCellMar::addNewTop, CTTblCellMar::unsetTop, top);
        this.setCellMargin(tcm, CTTblCellMar::isSetLeft, CTTblCellMar::getLeft, CTTblCellMar::addNewLeft, CTTblCellMar::unsetLeft, left);
        this.setCellMargin(tcm, CTTblCellMar::isSetBottom, CTTblCellMar::getBottom, CTTblCellMar::addNewBottom, CTTblCellMar::unsetBottom, bottom);
        this.setCellMargin(tcm, CTTblCellMar::isSetRight, CTTblCellMar::getRight, CTTblCellMar::addNewRight, CTTblCellMar::unsetRight, right);
    }
    
    private void setCellMargin(final CTTblCellMar tcm, final Function<CTTblCellMar, Boolean> isSet, final Function<CTTblCellMar, CTTblWidth> get, final Function<CTTblCellMar, CTTblWidth> addNew, final Consumer<CTTblCellMar> unSet, final int margin) {
        if (margin == 0) {
            if (isSet.apply(tcm)) {
                unSet.accept(tcm);
            }
        }
        else {
            final CTTblWidth tw = (isSet.apply(tcm) ? get : addNew).apply(tcm);
            tw.setType(STTblWidth.DXA);
            tw.setW(BigInteger.valueOf(margin));
        }
    }
    
    public void addRow(final XWPFTableRow row) {
        this.ctTbl.addNewTr();
        this.ctTbl.setTrArray(this.getNumberOfRows() - 1, row.getCtRow());
        this.tableRows.add(row);
    }
    
    public boolean addRow(final XWPFTableRow row, final int pos) {
        if (pos >= 0 && pos <= this.tableRows.size()) {
            this.ctTbl.insertNewTr(pos);
            this.ctTbl.setTrArray(pos, row.getCtRow());
            this.tableRows.add(pos, row);
            return true;
        }
        return false;
    }
    
    public XWPFTableRow insertNewTableRow(final int pos) {
        if (pos >= 0 && pos <= this.tableRows.size()) {
            final CTRow row = this.ctTbl.insertNewTr(pos);
            final XWPFTableRow tableRow = new XWPFTableRow(row, this);
            this.tableRows.add(pos, tableRow);
            return tableRow;
        }
        return null;
    }
    
    public boolean removeRow(final int pos) throws IndexOutOfBoundsException {
        if (pos >= 0 && pos < this.tableRows.size()) {
            if (this.ctTbl.sizeOfTrArray() > 0) {
                this.ctTbl.removeTr(pos);
            }
            this.tableRows.remove(pos);
            return true;
        }
        return false;
    }
    
    public List<XWPFTableRow> getRows() {
        return Collections.unmodifiableList((List<? extends XWPFTableRow>)this.tableRows);
    }
    
    @Override
    public BodyElementType getElementType() {
        return BodyElementType.TABLE;
    }
    
    @Override
    public IBody getBody() {
        return this.part;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        if (this.part != null) {
            return this.part.getPart();
        }
        return null;
    }
    
    @Override
    public BodyType getPartType() {
        return this.part.getPartType();
    }
    
    public XWPFTableRow getRow(final CTRow row) {
        for (int i = 0; i < this.getRows().size(); ++i) {
            if (this.getRows().get(i).getCtRow() == row) {
                return this.getRow(i);
            }
        }
        return null;
    }
    
    public double getWidthDecimal() {
        return getWidthDecimal(this.getTblPr().getTblW());
    }
    
    protected static double getWidthDecimal(final CTTblWidth ctWidth) {
        double result = 0.0;
        final STTblWidth.Enum typeValue = ctWidth.getType();
        if (typeValue == STTblWidth.DXA || typeValue == STTblWidth.AUTO || typeValue == STTblWidth.NIL) {
            result = 0.0 + ctWidth.getW().intValue();
        }
        else if (typeValue == STTblWidth.PCT) {
            result = ctWidth.getW().intValue() / 50.0;
        }
        return result;
    }
    
    public TableWidthType getWidthType() {
        return getWidthType(this.getTblPr().getTblW());
    }
    
    protected static TableWidthType getWidthType(final CTTblWidth ctWidth) {
        STTblWidth.Enum typeValue = ctWidth.getType();
        if (typeValue == null) {
            typeValue = STTblWidth.NIL;
            ctWidth.setType(typeValue);
        }
        switch (typeValue.intValue()) {
            case 1: {
                return TableWidthType.NIL;
            }
            case 4: {
                return TableWidthType.AUTO;
            }
            case 3: {
                return TableWidthType.DXA;
            }
            case 2: {
                return TableWidthType.PCT;
            }
            default: {
                return TableWidthType.AUTO;
            }
        }
    }
    
    public void setWidth(final String widthValue) {
        setWidthValue(widthValue, this.getTblPr().getTblW());
    }
    
    protected static void setWidthValue(final String widthValue, final CTTblWidth ctWidth) {
        if (!widthValue.matches("auto|[0-9]+|[0-9]+(\\.[0-9]+)?%")) {
            throw new RuntimeException("Table width value \"" + widthValue + "\" must match regular expression \"" + "auto|[0-9]+|[0-9]+(\\.[0-9]+)?%" + "\".");
        }
        if (widthValue.matches("auto")) {
            ctWidth.setType(STTblWidth.AUTO);
            ctWidth.setW(BigInteger.ZERO);
        }
        else if (widthValue.matches("[0-9]+(\\.[0-9]+)?%")) {
            setWidthPercentage(ctWidth, widthValue);
        }
        else {
            ctWidth.setW(new BigInteger(widthValue));
            ctWidth.setType(STTblWidth.DXA);
        }
    }
    
    protected static void setWidthPercentage(final CTTblWidth ctWidth, final String widthValue) {
        ctWidth.setType(STTblWidth.PCT);
        if (widthValue.matches("[0-9]+(\\.[0-9]+)?%")) {
            final String numberPart = widthValue.substring(0, widthValue.length() - 1);
            final double percentage = Double.parseDouble(numberPart) * 50.0;
            final long intValue = Math.round(percentage);
            ctWidth.setW(BigInteger.valueOf(intValue));
        }
        else {
            if (!widthValue.matches("[0-9]+")) {
                throw new RuntimeException("setWidthPercentage(): Width value must be a percentage (\"33.3%\" or an integer, was \"" + widthValue + "\"");
            }
            ctWidth.setW(new BigInteger(widthValue));
        }
    }
    
    public void setWidthType(final TableWidthType widthType) {
        setWidthType(widthType, this.getTblPr().getTblW());
    }
    
    protected static void setWidthType(final TableWidthType widthType, final CTTblWidth ctWidth) {
        final TableWidthType currentType = getWidthType(ctWidth);
        if (!currentType.equals(widthType)) {
            final STTblWidth.Enum stWidthType = widthType.getSTWidthType();
            ctWidth.setType(stWidthType);
            switch (stWidthType.intValue()) {
                case 2: {
                    setWidthPercentage(ctWidth, "100%");
                    break;
                }
                default: {
                    ctWidth.setW(BigInteger.ZERO);
                    break;
                }
            }
        }
    }
    
    static {
        (xwpfBorderTypeMap = new EnumMap<XWPFBorderType, STBorder.Enum>(XWPFBorderType.class)).put(XWPFBorderType.NIL, STBorder.Enum.forInt(1));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.NONE, STBorder.Enum.forInt(2));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.SINGLE, STBorder.Enum.forInt(3));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THICK, STBorder.Enum.forInt(4));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DOUBLE, STBorder.Enum.forInt(5));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DOTTED, STBorder.Enum.forInt(6));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DASHED, STBorder.Enum.forInt(7));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DOT_DASH, STBorder.Enum.forInt(8));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DOT_DOT_DASH, STBorder.Enum.forInt(9));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.TRIPLE, STBorder.Enum.forInt(10));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_SMALL_GAP, STBorder.Enum.forInt(11));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THICK_THIN_SMALL_GAP, STBorder.Enum.forInt(12));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_THIN_SMALL_GAP, STBorder.Enum.forInt(13));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_MEDIUM_GAP, STBorder.Enum.forInt(14));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THICK_THIN_MEDIUM_GAP, STBorder.Enum.forInt(15));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_THIN_MEDIUM_GAP, STBorder.Enum.forInt(16));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_LARGE_GAP, STBorder.Enum.forInt(17));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THICK_THIN_LARGE_GAP, STBorder.Enum.forInt(18));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THIN_THICK_THIN_LARGE_GAP, STBorder.Enum.forInt(19));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.WAVE, STBorder.Enum.forInt(20));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DOUBLE_WAVE, STBorder.Enum.forInt(21));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DASH_SMALL_GAP, STBorder.Enum.forInt(22));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.DASH_DOT_STROKED, STBorder.Enum.forInt(23));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THREE_D_EMBOSS, STBorder.Enum.forInt(24));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.THREE_D_ENGRAVE, STBorder.Enum.forInt(25));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.OUTSET, STBorder.Enum.forInt(26));
        XWPFTable.xwpfBorderTypeMap.put(XWPFBorderType.INSET, STBorder.Enum.forInt(27));
        (stBorderTypeMap = new HashMap<Integer, XWPFBorderType>()).put(1, XWPFBorderType.NIL);
        XWPFTable.stBorderTypeMap.put(2, XWPFBorderType.NONE);
        XWPFTable.stBorderTypeMap.put(3, XWPFBorderType.SINGLE);
        XWPFTable.stBorderTypeMap.put(4, XWPFBorderType.THICK);
        XWPFTable.stBorderTypeMap.put(5, XWPFBorderType.DOUBLE);
        XWPFTable.stBorderTypeMap.put(6, XWPFBorderType.DOTTED);
        XWPFTable.stBorderTypeMap.put(7, XWPFBorderType.DASHED);
        XWPFTable.stBorderTypeMap.put(8, XWPFBorderType.DOT_DASH);
        XWPFTable.stBorderTypeMap.put(9, XWPFBorderType.DOT_DOT_DASH);
        XWPFTable.stBorderTypeMap.put(10, XWPFBorderType.TRIPLE);
        XWPFTable.stBorderTypeMap.put(11, XWPFBorderType.THIN_THICK_SMALL_GAP);
        XWPFTable.stBorderTypeMap.put(12, XWPFBorderType.THICK_THIN_SMALL_GAP);
        XWPFTable.stBorderTypeMap.put(13, XWPFBorderType.THIN_THICK_THIN_SMALL_GAP);
        XWPFTable.stBorderTypeMap.put(14, XWPFBorderType.THIN_THICK_MEDIUM_GAP);
        XWPFTable.stBorderTypeMap.put(15, XWPFBorderType.THICK_THIN_MEDIUM_GAP);
        XWPFTable.stBorderTypeMap.put(16, XWPFBorderType.THIN_THICK_THIN_MEDIUM_GAP);
        XWPFTable.stBorderTypeMap.put(17, XWPFBorderType.THIN_THICK_LARGE_GAP);
        XWPFTable.stBorderTypeMap.put(18, XWPFBorderType.THICK_THIN_LARGE_GAP);
        XWPFTable.stBorderTypeMap.put(19, XWPFBorderType.THIN_THICK_THIN_LARGE_GAP);
        XWPFTable.stBorderTypeMap.put(20, XWPFBorderType.WAVE);
        XWPFTable.stBorderTypeMap.put(21, XWPFBorderType.DOUBLE_WAVE);
        XWPFTable.stBorderTypeMap.put(22, XWPFBorderType.DASH_SMALL_GAP);
        XWPFTable.stBorderTypeMap.put(23, XWPFBorderType.DASH_DOT_STROKED);
        XWPFTable.stBorderTypeMap.put(24, XWPFBorderType.THREE_D_EMBOSS);
        XWPFTable.stBorderTypeMap.put(25, XWPFBorderType.THREE_D_ENGRAVE);
        XWPFTable.stBorderTypeMap.put(26, XWPFBorderType.OUTSET);
        XWPFTable.stBorderTypeMap.put(27, XWPFBorderType.INSET);
    }
    
    public enum XWPFBorderType
    {
        NIL, 
        NONE, 
        SINGLE, 
        THICK, 
        DOUBLE, 
        DOTTED, 
        DASHED, 
        DOT_DASH, 
        DOT_DOT_DASH, 
        TRIPLE, 
        THIN_THICK_SMALL_GAP, 
        THICK_THIN_SMALL_GAP, 
        THIN_THICK_THIN_SMALL_GAP, 
        THIN_THICK_MEDIUM_GAP, 
        THICK_THIN_MEDIUM_GAP, 
        THIN_THICK_THIN_MEDIUM_GAP, 
        THIN_THICK_LARGE_GAP, 
        THICK_THIN_LARGE_GAP, 
        THIN_THICK_THIN_LARGE_GAP, 
        WAVE, 
        DOUBLE_WAVE, 
        DASH_SMALL_GAP, 
        DASH_DOT_STROKED, 
        THREE_D_EMBOSS, 
        THREE_D_ENGRAVE, 
        OUTSET, 
        INSET;
    }
    
    private enum Border
    {
        INSIDE_V, 
        INSIDE_H, 
        LEFT, 
        TOP, 
        BOTTOM, 
        RIGHT;
    }
}
