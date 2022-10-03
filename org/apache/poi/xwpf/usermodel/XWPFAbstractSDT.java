package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;

public abstract class XWPFAbstractSDT implements ISDTContents
{
    private final String title;
    private final String tag;
    private final IBody part;
    
    public XWPFAbstractSDT(final CTSdtPr pr, final IBody part) {
        if (pr == null) {
            this.title = "";
            this.tag = "";
        }
        else {
            final CTString[] aliases = pr.getAliasArray();
            if (aliases != null && aliases.length > 0) {
                this.title = aliases[0].getVal();
            }
            else {
                this.title = "";
            }
            final CTString[] tags = pr.getTagArray();
            if (tags != null && tags.length > 0) {
                this.tag = tags[0].getVal();
            }
            else {
                this.tag = "";
            }
        }
        this.part = part;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public abstract ISDTContent getContent();
    
    public IBody getBody() {
        return null;
    }
    
    public POIXMLDocumentPart getPart() {
        return this.part.getPart();
    }
    
    public BodyType getPartType() {
        return BodyType.CONTENTCONTROL;
    }
    
    public BodyElementType getElementType() {
        return BodyElementType.CONTENTCONTROL;
    }
    
    public XWPFDocument getDocument() {
        return this.part.getXWPFDocument();
    }
}
