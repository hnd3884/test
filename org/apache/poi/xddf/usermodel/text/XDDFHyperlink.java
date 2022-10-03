package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.xddf.usermodel.XDDFExtensionList;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;

public class XDDFHyperlink
{
    private CTHyperlink link;
    
    public XDDFHyperlink(final String id) {
        this(CTHyperlink.Factory.newInstance());
        this.link.setId(id);
    }
    
    public XDDFHyperlink(final String id, final String action) {
        this(id);
        this.link.setAction(action);
    }
    
    @Internal
    protected XDDFHyperlink(final CTHyperlink link) {
        this.link = link;
    }
    
    @Internal
    protected CTHyperlink getXmlObject() {
        return this.link;
    }
    
    public String getAction() {
        if (this.link.isSetAction()) {
            return this.link.getAction();
        }
        return null;
    }
    
    public String getId() {
        if (this.link.isSetId()) {
            return this.link.getId();
        }
        return null;
    }
    
    public Boolean getEndSound() {
        if (this.link.isSetEndSnd()) {
            return this.link.getEndSnd();
        }
        return null;
    }
    
    public void setEndSound(final Boolean ends) {
        if (ends == null) {
            if (this.link.isSetEndSnd()) {
                this.link.unsetEndSnd();
            }
        }
        else {
            this.link.setEndSnd((boolean)ends);
        }
    }
    
    public Boolean getHighlightClick() {
        if (this.link.isSetHighlightClick()) {
            return this.link.getHighlightClick();
        }
        return null;
    }
    
    public void setHighlightClick(final Boolean highlights) {
        if (highlights == null) {
            if (this.link.isSetHighlightClick()) {
                this.link.unsetHighlightClick();
            }
        }
        else {
            this.link.setHighlightClick((boolean)highlights);
        }
    }
    
    public Boolean getHistory() {
        if (this.link.isSetHistory()) {
            return this.link.getHistory();
        }
        return null;
    }
    
    public void setHistory(final Boolean history) {
        if (history == null) {
            if (this.link.isSetHistory()) {
                this.link.unsetHistory();
            }
        }
        else {
            this.link.setHistory((boolean)history);
        }
    }
    
    public String getInvalidURL() {
        if (this.link.isSetInvalidUrl()) {
            return this.link.getInvalidUrl();
        }
        return null;
    }
    
    public void setInvalidURL(final String invalid) {
        if (invalid == null) {
            if (this.link.isSetInvalidUrl()) {
                this.link.unsetInvalidUrl();
            }
        }
        else {
            this.link.setInvalidUrl(invalid);
        }
    }
    
    public String getTargetFrame() {
        if (this.link.isSetTgtFrame()) {
            return this.link.getTgtFrame();
        }
        return null;
    }
    
    public void setTargetFrame(final String frame) {
        if (frame == null) {
            if (this.link.isSetTgtFrame()) {
                this.link.unsetTgtFrame();
            }
        }
        else {
            this.link.setTgtFrame(frame);
        }
    }
    
    public String getTooltip() {
        if (this.link.isSetTooltip()) {
            return this.link.getTooltip();
        }
        return null;
    }
    
    public void setTooltip(final String tooltip) {
        if (tooltip == null) {
            if (this.link.isSetTooltip()) {
                this.link.unsetTooltip();
            }
        }
        else {
            this.link.setTooltip(tooltip);
        }
    }
    
    public XDDFExtensionList getExtensionList() {
        if (this.link.isSetExtLst()) {
            return new XDDFExtensionList(this.link.getExtLst());
        }
        return null;
    }
    
    public void setExtensionList(final XDDFExtensionList list) {
        if (list == null) {
            if (this.link.isSetExtLst()) {
                this.link.unsetExtLst();
            }
        }
        else {
            this.link.setExtLst(list.getXmlObject());
        }
    }
}
