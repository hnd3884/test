package org.apache.poi.xwpf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;

public class XWPFHyperlinkRun extends XWPFRun
{
    private CTHyperlink hyperlink;
    
    public XWPFHyperlinkRun(final CTHyperlink hyperlink, final CTR run, final IRunBody p) {
        super(run, p);
        this.hyperlink = hyperlink;
    }
    
    @Internal
    public CTHyperlink getCTHyperlink() {
        return this.hyperlink;
    }
    
    public String getAnchor() {
        return this.hyperlink.getAnchor();
    }
    
    public String getHyperlinkId() {
        return this.hyperlink.getId();
    }
    
    public void setHyperlinkId(final String id) {
        this.hyperlink.setId(id);
    }
    
    public XWPFHyperlink getHyperlink(final XWPFDocument document) {
        final String id = this.getHyperlinkId();
        if (id == null) {
            return null;
        }
        return document.getHyperlinkByID(id);
    }
}
