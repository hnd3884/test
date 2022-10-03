package org.apache.poi.xslf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.Removal;
import org.apache.poi.common.usermodel.HyperlinkType;
import java.net.URI;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.apache.poi.sl.usermodel.Hyperlink;

public class XSLFHyperlink implements Hyperlink<XSLFShape, XSLFTextParagraph>
{
    private final XSLFSheet _sheet;
    private final CTHyperlink _link;
    
    XSLFHyperlink(final CTHyperlink link, final XSLFSheet sheet) {
        this._sheet = sheet;
        this._link = link;
    }
    
    @Internal
    public CTHyperlink getXmlObject() {
        return this._link;
    }
    
    public void setAddress(final String address) {
        this.linkToUrl(address);
    }
    
    public String getAddress() {
        final String id = this._link.getId();
        if (id == null || id.isEmpty()) {
            return this._link.getAction();
        }
        final PackageRelationship rel = this._sheet.getPackagePart().getRelationship(id);
        if (rel == null) {
            return null;
        }
        final URI targetURI = rel.getTargetURI();
        return (targetURI == null) ? null : targetURI.toASCIIString();
    }
    
    public String getLabel() {
        return this._link.getTooltip();
    }
    
    public void setLabel(final String label) {
        this._link.setTooltip(label);
    }
    
    public HyperlinkType getType() {
        String action = this._link.getAction();
        if (action == null) {
            action = "";
        }
        if (action.equals("ppaction://hlinksldjump") || action.startsWith("ppaction://hlinkshowjump")) {
            return HyperlinkType.DOCUMENT;
        }
        String address = this.getAddress();
        if (address == null) {
            address = "";
        }
        if (address.startsWith("mailto:")) {
            return HyperlinkType.EMAIL;
        }
        return HyperlinkType.URL;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public HyperlinkType getTypeEnum() {
        return this.getType();
    }
    
    public void linkToEmail(final String emailAddress) {
        this.linkToExternal("mailto:" + emailAddress);
        this.setLabel(emailAddress);
    }
    
    public void linkToUrl(final String url) {
        this.linkToExternal(url);
        this.setLabel(url);
    }
    
    private void linkToExternal(final String url) {
        final PackagePart thisPP = this._sheet.getPackagePart();
        if (this._link.isSetId() && !this._link.getId().isEmpty()) {
            thisPP.removeRelationship(this._link.getId());
        }
        final PackageRelationship rel = thisPP.addExternalRelationship(url, XSLFRelation.HYPERLINK.getRelation());
        this._link.setId(rel.getId());
        if (this._link.isSetAction()) {
            this._link.unsetAction();
        }
    }
    
    public void linkToSlide(final Slide<XSLFShape, XSLFTextParagraph> slide) {
        if (this._link.isSetId() && !this._link.getId().isEmpty()) {
            this._sheet.getPackagePart().removeRelationship(this._link.getId());
        }
        final POIXMLDocumentPart.RelationPart rp = this._sheet.addRelation(null, XSLFRelation.SLIDE, (POIXMLDocumentPart)slide);
        this._link.setId(rp.getRelationship().getId());
        this._link.setAction("ppaction://hlinksldjump");
    }
    
    public void linkToNextSlide() {
        this.linkToRelativeSlide("nextslide");
    }
    
    public void linkToPreviousSlide() {
        this.linkToRelativeSlide("previousslide");
    }
    
    public void linkToFirstSlide() {
        this.linkToRelativeSlide("firstslide");
    }
    
    public void linkToLastSlide() {
        this.linkToRelativeSlide("lastslide");
    }
    
    void copy(final XSLFHyperlink src) {
        switch (src.getType()) {
            case EMAIL:
            case URL: {
                this.linkToExternal(src.getAddress());
                break;
            }
            case DOCUMENT: {
                final String idSrc = src._link.getId();
                if (idSrc == null || idSrc.isEmpty()) {
                    this.linkToRelativeSlide(src.getAddress());
                    break;
                }
                final POIXMLDocumentPart pp = src._sheet.getRelationById(idSrc);
                if (pp != null) {
                    final POIXMLDocumentPart.RelationPart rp = this._sheet.addRelation(null, XSLFRelation.SLIDE, pp);
                    this._link.setId(rp.getRelationship().getId());
                    this._link.setAction(src._link.getAction());
                }
                break;
            }
            default: {
                return;
            }
        }
        this.setLabel(src.getLabel());
    }
    
    private void linkToRelativeSlide(final String jump) {
        final PackagePart thisPP = this._sheet.getPackagePart();
        if (this._link.isSetId() && !this._link.getId().isEmpty()) {
            thisPP.removeRelationship(this._link.getId());
        }
        this._link.setId("");
        this._link.setAction((jump.startsWith("ppaction") ? "" : "ppaction://hlinkshowjump?jump=") + jump);
    }
}
