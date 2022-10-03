package org.apache.poi.xssf.streaming;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.ChildAnchor;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import java.io.IOException;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Row;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import java.awt.Dimension;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Picture;

public final class SXSSFPicture implements Picture
{
    private static final POILogger logger;
    private static float DEFAULT_COLUMN_WIDTH;
    private final SXSSFWorkbook _wb;
    private final XSSFPicture _picture;
    
    SXSSFPicture(final SXSSFWorkbook _wb, final XSSFPicture _picture) {
        this._wb = _wb;
        this._picture = _picture;
    }
    
    @Internal
    public CTPicture getCTPicture() {
        return this._picture.getCTPicture();
    }
    
    public void resize() {
        this.resize(1.0);
    }
    
    public void resize(final double scale) {
        final XSSFClientAnchor anchor = this.getClientAnchor();
        final XSSFClientAnchor pref = this.getPreferredSize(scale);
        if (anchor == null || pref == null) {
            SXSSFPicture.logger.log(5, new Object[] { "picture is not anchored via client anchor - ignoring resize call" });
            return;
        }
        final int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        final int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2(col2);
        anchor.setDx1(0);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy1(0);
        anchor.setDy2(pref.getDy2());
    }
    
    public XSSFClientAnchor getPreferredSize() {
        return this.getPreferredSize(1.0);
    }
    
    public XSSFClientAnchor getPreferredSize(final double scale) {
        final XSSFClientAnchor anchor = this.getClientAnchor();
        if (anchor == null) {
            SXSSFPicture.logger.log(5, new Object[] { "picture is not anchored via client anchor - ignoring resize call" });
            return null;
        }
        final XSSFPictureData data = this.getPictureData();
        final Dimension size = getImageDimension(data.getPackagePart(), data.getPictureType());
        final double scaledWidth = size.getWidth() * scale;
        final double scaledHeight = size.getHeight() * scale;
        float w;
        int col2;
        for (w = 0.0f, col2 = anchor.getCol1() - 1; w <= scaledWidth; w += this.getColumnWidthInPixels(++col2)) {}
        assert w > scaledWidth;
        final double cw = this.getColumnWidthInPixels(col2);
        final double deltaW = w - scaledWidth;
        final int dx2 = (int)(9525.0 * (cw - deltaW));
        anchor.setCol2(col2);
        anchor.setDx2(dx2);
        double h;
        int row2;
        for (h = 0.0, row2 = anchor.getRow1() - 1; h <= scaledHeight; h += this.getRowHeightInPixels(++row2)) {}
        assert h > scaledHeight;
        final double ch = this.getRowHeightInPixels(row2);
        final double deltaH = h - scaledHeight;
        final int dy2 = (int)(9525.0 * (ch - deltaH));
        anchor.setRow2(row2);
        anchor.setDy2(dy2);
        final CTPositiveSize2D size2d = this.getCTPicture().getSpPr().getXfrm().getExt();
        size2d.setCx((long)(scaledWidth * 9525.0));
        size2d.setCy((long)(scaledHeight * 9525.0));
        return anchor;
    }
    
    private float getColumnWidthInPixels(final int columnIndex) {
        final XSSFSheet sheet = this.getSheet();
        final CTCol col = sheet.getColumnHelper().getColumn(columnIndex, false);
        final double numChars = (col == null || !col.isSetWidth()) ? SXSSFPicture.DEFAULT_COLUMN_WIDTH : col.getWidth();
        return (float)numChars * 7.0017f;
    }
    
    private float getRowHeightInPixels(final int rowIndex) {
        final XSSFSheet xssfSheet = this.getSheet();
        final SXSSFSheet sheet = this._wb.getSXSSFSheet(xssfSheet);
        final Row row = (Row)sheet.getRow(rowIndex);
        final float height = (row != null) ? row.getHeightInPoints() : sheet.getDefaultRowHeightInPoints();
        return height * 96.0f / 72.0f;
    }
    
    protected static Dimension getImageDimension(final PackagePart part, final int type) {
        try {
            return ImageUtils.getImageDimension(part.getInputStream(), type);
        }
        catch (final IOException e) {
            SXSSFPicture.logger.log(5, new Object[] { e });
            return new Dimension();
        }
    }
    
    public XSSFPictureData getPictureData() {
        return this._picture.getPictureData();
    }
    
    protected CTShapeProperties getShapeProperties() {
        return this.getCTPicture().getSpPr();
    }
    
    public XSSFAnchor getAnchor() {
        return this._picture.getAnchor();
    }
    
    public void resize(final double scaleX, final double scaleY) {
        this._picture.resize(scaleX, scaleY);
    }
    
    public XSSFClientAnchor getPreferredSize(final double scaleX, final double scaleY) {
        return this._picture.getPreferredSize(scaleX, scaleY);
    }
    
    public Dimension getImageDimension() {
        return this._picture.getImageDimension();
    }
    
    public XSSFClientAnchor getClientAnchor() {
        final XSSFAnchor a = this.getAnchor();
        return (a instanceof XSSFClientAnchor) ? ((XSSFClientAnchor)a) : null;
    }
    
    public XSSFDrawing getDrawing() {
        return this._picture.getDrawing();
    }
    
    public XSSFSheet getSheet() {
        return this._picture.getSheet();
    }
    
    public String getShapeName() {
        return this._picture.getShapeName();
    }
    
    public Shape getParent() {
        return (Shape)this._picture.getParent();
    }
    
    public boolean isNoFill() {
        return this._picture.isNoFill();
    }
    
    public void setNoFill(final boolean noFill) {
        this._picture.setNoFill(noFill);
    }
    
    public void setFillColor(final int red, final int green, final int blue) {
        this._picture.setFillColor(red, green, blue);
    }
    
    public void setLineStyleColor(final int red, final int green, final int blue) {
        this._picture.setLineStyleColor(red, green, blue);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)SXSSFPicture.class);
        SXSSFPicture.DEFAULT_COLUMN_WIDTH = 9.140625f;
    }
}
