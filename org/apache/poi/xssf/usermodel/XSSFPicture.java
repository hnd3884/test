package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.awt.Dimension;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Picture;

public final class XSSFPicture extends XSSFShape implements Picture
{
    private static final POILogger logger;
    private static CTPicture prototype;
    private CTPicture ctPicture;
    
    protected XSSFPicture(final XSSFDrawing drawing, final CTPicture ctPicture) {
        this.drawing = drawing;
        this.ctPicture = ctPicture;
    }
    
    protected static CTPicture prototype() {
        if (XSSFPicture.prototype == null) {
            final CTPicture pic = CTPicture.Factory.newInstance();
            final CTPictureNonVisual nvpr = pic.addNewNvPicPr();
            final CTNonVisualDrawingProps nvProps = nvpr.addNewCNvPr();
            nvProps.setId(1L);
            nvProps.setName("Picture 1");
            nvProps.setDescr("Picture");
            final CTNonVisualPictureProperties nvPicProps = nvpr.addNewCNvPicPr();
            nvPicProps.addNewPicLocks().setNoChangeAspect(true);
            final CTBlipFillProperties blip = pic.addNewBlipFill();
            blip.addNewBlip().setEmbed("");
            blip.addNewStretch().addNewFillRect();
            final CTShapeProperties sppr = pic.addNewSpPr();
            final CTTransform2D t2d = sppr.addNewXfrm();
            final CTPositiveSize2D ext = t2d.addNewExt();
            ext.setCx(0L);
            ext.setCy(0L);
            final CTPoint2D off = t2d.addNewOff();
            off.setX(0L);
            off.setY(0L);
            final CTPresetGeometry2D prstGeom = sppr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();
            XSSFPicture.prototype = pic;
        }
        return XSSFPicture.prototype;
    }
    
    protected void setPictureReference(final PackageRelationship rel) {
        this.ctPicture.getBlipFill().getBlip().setEmbed(rel.getId());
    }
    
    @Internal
    public CTPicture getCTPicture() {
        return this.ctPicture;
    }
    
    public void resize() {
        this.resize(Double.MAX_VALUE);
    }
    
    public void resize(final double scale) {
        this.resize(scale, scale);
    }
    
    public void resize(final double scaleX, final double scaleY) {
        final XSSFClientAnchor anchor = this.getClientAnchor();
        final XSSFClientAnchor pref = this.getPreferredSize(scaleX, scaleY);
        if (anchor == null || pref == null) {
            XSSFPicture.logger.log(5, new Object[] { "picture is not anchored via client anchor - ignoring resize call" });
            return;
        }
        final int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        final int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2(col2);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy2(pref.getDy2());
    }
    
    public XSSFClientAnchor getPreferredSize() {
        return this.getPreferredSize(1.0);
    }
    
    public XSSFClientAnchor getPreferredSize(final double scale) {
        return this.getPreferredSize(scale, scale);
    }
    
    public XSSFClientAnchor getPreferredSize(final double scaleX, final double scaleY) {
        final Dimension dim = ImageUtils.setPreferredSize((Picture)this, scaleX, scaleY);
        final CTPositiveSize2D size2d = this.ctPicture.getSpPr().getXfrm().getExt();
        size2d.setCx((long)(int)dim.getWidth());
        size2d.setCy((long)(int)dim.getHeight());
        return this.getClientAnchor();
    }
    
    protected static Dimension getImageDimension(final PackagePart part, final int type) {
        try {
            return ImageUtils.getImageDimension(part.getInputStream(), type);
        }
        catch (final IOException e) {
            XSSFPicture.logger.log(5, new Object[] { e });
            return new Dimension();
        }
    }
    
    public Dimension getImageDimension() {
        final XSSFPictureData picData = this.getPictureData();
        return getImageDimension(picData.getPackagePart(), picData.getPictureType());
    }
    
    public XSSFPictureData getPictureData() {
        final String blipId = this.ctPicture.getBlipFill().getBlip().getEmbed();
        return (XSSFPictureData)this.getDrawing().getRelationById(blipId);
    }
    
    @Override
    protected CTShapeProperties getShapeProperties() {
        return this.ctPicture.getSpPr();
    }
    
    public XSSFClientAnchor getClientAnchor() {
        final XSSFAnchor a = this.getAnchor();
        return (a instanceof XSSFClientAnchor) ? ((XSSFClientAnchor)a) : null;
    }
    
    public XSSFSheet getSheet() {
        return (XSSFSheet)this.getDrawing().getParent();
    }
    
    public String getShapeName() {
        return this.ctPicture.getNvPicPr().getCNvPr().getName();
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFPicture.class);
    }
}
