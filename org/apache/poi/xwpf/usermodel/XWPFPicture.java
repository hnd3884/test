package org.apache.poi.xwpf.usermodel;

import org.apache.poi.util.Units;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;

public class XWPFPicture
{
    private CTPicture ctPic;
    private String description;
    private XWPFRun run;
    
    public XWPFPicture(final CTPicture ctPic, final XWPFRun run) {
        this.run = run;
        this.ctPic = ctPic;
        this.description = ctPic.getNvPicPr().getCNvPr().getDescr();
    }
    
    public void setPictureReference(final PackageRelationship rel) {
        this.ctPic.getBlipFill().getBlip().setEmbed(rel.getId());
    }
    
    public CTPicture getCTPicture() {
        return this.ctPic;
    }
    
    public XWPFPictureData getPictureData() {
        final CTBlipFillProperties blipProps = this.ctPic.getBlipFill();
        if (blipProps == null || !blipProps.isSetBlip()) {
            return null;
        }
        final String blipId = blipProps.getBlip().getEmbed();
        final POIXMLDocumentPart part = this.run.getParent().getPart();
        if (part != null) {
            final POIXMLDocumentPart relatedPart = part.getRelationById(blipId);
            if (relatedPart instanceof XWPFPictureData) {
                return (XWPFPictureData)relatedPart;
            }
        }
        return null;
    }
    
    public double getWidth() {
        return Units.toPoints(this.ctPic.getSpPr().getXfrm().getExt().getCx());
    }
    
    public double getDepth() {
        return Units.toPoints(this.ctPic.getSpPr().getXfrm().getExt().getCy());
    }
    
    public String getDescription() {
        return this.description;
    }
}
