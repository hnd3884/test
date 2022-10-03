package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPageOrder;
import org.apache.poi.ss.usermodel.PrintCellComments;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellComments;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.PageOrder;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.PaperSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.poi.ss.usermodel.PrintSetup;

public class XSSFPrintSetup implements PrintSetup
{
    private CTWorksheet ctWorksheet;
    private CTPageSetup pageSetup;
    private CTPageMargins pageMargins;
    
    protected XSSFPrintSetup(final CTWorksheet worksheet) {
        this.ctWorksheet = worksheet;
        if (this.ctWorksheet.isSetPageSetup()) {
            this.pageSetup = this.ctWorksheet.getPageSetup();
        }
        else {
            this.pageSetup = this.ctWorksheet.addNewPageSetup();
        }
        if (this.ctWorksheet.isSetPageMargins()) {
            this.pageMargins = this.ctWorksheet.getPageMargins();
        }
        else {
            this.pageMargins = this.ctWorksheet.addNewPageMargins();
        }
    }
    
    public void setPaperSize(final short size) {
        this.pageSetup.setPaperSize((long)size);
    }
    
    public void setPaperSize(final PaperSize size) {
        this.setPaperSize((short)(size.ordinal() + 1));
    }
    
    public void setScale(final short scale) {
        if (scale < 10 || scale > 400) {
            throw new POIXMLException("Scale value not accepted: you must choose a value between 10 and 400.");
        }
        this.pageSetup.setScale((long)scale);
    }
    
    public void setPageStart(final short start) {
        this.pageSetup.setFirstPageNumber((long)start);
    }
    
    public void setFitWidth(final short width) {
        this.pageSetup.setFitToWidth((long)width);
    }
    
    public void setFitHeight(final short height) {
        this.pageSetup.setFitToHeight((long)height);
    }
    
    public void setLeftToRight(final boolean leftToRight) {
        if (leftToRight) {
            this.setPageOrder(PageOrder.OVER_THEN_DOWN);
        }
        else {
            this.setPageOrder(PageOrder.DOWN_THEN_OVER);
        }
    }
    
    public void setLandscape(final boolean ls) {
        if (ls) {
            this.setOrientation(PrintOrientation.LANDSCAPE);
        }
        else {
            this.setOrientation(PrintOrientation.PORTRAIT);
        }
    }
    
    public void setValidSettings(final boolean valid) {
        this.pageSetup.setUsePrinterDefaults(valid);
    }
    
    public void setNoColor(final boolean mono) {
        this.pageSetup.setBlackAndWhite(mono);
    }
    
    public void setDraft(final boolean d) {
        this.pageSetup.setDraft(d);
    }
    
    public void setNotes(final boolean printNotes) {
        if (printNotes) {
            this.pageSetup.setCellComments(STCellComments.AS_DISPLAYED);
        }
    }
    
    public void setNoOrientation(final boolean orientation) {
        if (orientation) {
            this.setOrientation(PrintOrientation.DEFAULT);
        }
    }
    
    public void setUsePage(final boolean page) {
        this.pageSetup.setUseFirstPageNumber(page);
    }
    
    public void setHResolution(final short resolution) {
        this.pageSetup.setHorizontalDpi((long)resolution);
    }
    
    public void setVResolution(final short resolution) {
        this.pageSetup.setVerticalDpi((long)resolution);
    }
    
    public void setHeaderMargin(final double headerMargin) {
        this.pageMargins.setHeader(headerMargin);
    }
    
    public void setFooterMargin(final double footerMargin) {
        this.pageMargins.setFooter(footerMargin);
    }
    
    public void setCopies(final short copies) {
        this.pageSetup.setCopies((long)copies);
    }
    
    public void setOrientation(final PrintOrientation orientation) {
        final STOrientation.Enum v = STOrientation.Enum.forInt(orientation.getValue());
        this.pageSetup.setOrientation(v);
    }
    
    public PrintOrientation getOrientation() {
        final STOrientation.Enum val = this.pageSetup.getOrientation();
        return (val == null) ? PrintOrientation.DEFAULT : PrintOrientation.valueOf(val.intValue());
    }
    
    public PrintCellComments getCellComment() {
        final STCellComments.Enum val = this.pageSetup.getCellComments();
        return (val == null) ? PrintCellComments.NONE : PrintCellComments.valueOf(val.intValue());
    }
    
    public void setPageOrder(final PageOrder pageOrder) {
        final STPageOrder.Enum v = STPageOrder.Enum.forInt(pageOrder.getValue());
        this.pageSetup.setPageOrder(v);
    }
    
    public PageOrder getPageOrder() {
        return (this.pageSetup.getPageOrder() == null) ? null : PageOrder.valueOf(this.pageSetup.getPageOrder().intValue());
    }
    
    public short getPaperSize() {
        return (short)this.pageSetup.getPaperSize();
    }
    
    public PaperSize getPaperSizeEnum() {
        return PaperSize.values()[this.getPaperSize() - 1];
    }
    
    public short getScale() {
        return (short)this.pageSetup.getScale();
    }
    
    public short getPageStart() {
        return (short)this.pageSetup.getFirstPageNumber();
    }
    
    public short getFitWidth() {
        return (short)this.pageSetup.getFitToWidth();
    }
    
    public short getFitHeight() {
        return (short)this.pageSetup.getFitToHeight();
    }
    
    public boolean getLeftToRight() {
        return this.getPageOrder() == PageOrder.OVER_THEN_DOWN;
    }
    
    public boolean getLandscape() {
        return this.getOrientation() == PrintOrientation.LANDSCAPE;
    }
    
    public boolean getValidSettings() {
        return this.pageSetup.getUsePrinterDefaults();
    }
    
    public boolean getNoColor() {
        return this.pageSetup.getBlackAndWhite();
    }
    
    public boolean getDraft() {
        return this.pageSetup.getDraft();
    }
    
    public boolean getNotes() {
        return this.getCellComment() == PrintCellComments.AS_DISPLAYED;
    }
    
    public boolean getNoOrientation() {
        return this.getOrientation() == PrintOrientation.DEFAULT;
    }
    
    public boolean getUsePage() {
        return this.pageSetup.getUseFirstPageNumber();
    }
    
    public short getHResolution() {
        return (short)this.pageSetup.getHorizontalDpi();
    }
    
    public short getVResolution() {
        return (short)this.pageSetup.getVerticalDpi();
    }
    
    public double getHeaderMargin() {
        return this.pageMargins.getHeader();
    }
    
    public double getFooterMargin() {
        return this.pageMargins.getFooter();
    }
    
    public short getCopies() {
        return (short)this.pageSetup.getCopies();
    }
    
    public void setTopMargin(final double topMargin) {
        this.pageMargins.setTop(topMargin);
    }
    
    public double getTopMargin() {
        return this.pageMargins.getTop();
    }
    
    public void setBottomMargin(final double bottomMargin) {
        this.pageMargins.setBottom(bottomMargin);
    }
    
    public double getBottomMargin() {
        return this.pageMargins.getBottom();
    }
    
    public void setLeftMargin(final double leftMargin) {
        this.pageMargins.setLeft(leftMargin);
    }
    
    public double getLeftMargin() {
        return this.pageMargins.getLeft();
    }
    
    public void setRightMargin(final double rightMargin) {
        this.pageMargins.setRight(rightMargin);
    }
    
    public double getRightMargin() {
        return this.pageMargins.getRight();
    }
}
