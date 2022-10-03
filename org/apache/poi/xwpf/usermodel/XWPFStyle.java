package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;

public class XWPFStyle
{
    protected XWPFStyles styles;
    private CTStyle ctStyle;
    
    public XWPFStyle(final CTStyle style) {
        this(style, null);
    }
    
    public XWPFStyle(final CTStyle style, final XWPFStyles styles) {
        this.ctStyle = style;
        this.styles = styles;
    }
    
    public String getStyleId() {
        return this.ctStyle.getStyleId();
    }
    
    public void setStyleId(final String styleId) {
        this.ctStyle.setStyleId(styleId);
    }
    
    public STStyleType.Enum getType() {
        return this.ctStyle.getType();
    }
    
    public void setType(final STStyleType.Enum type) {
        this.ctStyle.setType(type);
    }
    
    public void setStyle(final CTStyle style) {
        this.ctStyle = style;
    }
    
    public CTStyle getCTStyle() {
        return this.ctStyle;
    }
    
    public XWPFStyles getStyles() {
        return this.styles;
    }
    
    public String getBasisStyleID() {
        if (this.ctStyle.getBasedOn() != null) {
            return this.ctStyle.getBasedOn().getVal();
        }
        return null;
    }
    
    public String getLinkStyleID() {
        if (this.ctStyle.getLink() != null) {
            return this.ctStyle.getLink().getVal();
        }
        return null;
    }
    
    public String getNextStyleID() {
        if (this.ctStyle.getNext() != null) {
            return this.ctStyle.getNext().getVal();
        }
        return null;
    }
    
    public String getName() {
        if (this.ctStyle.isSetName()) {
            return this.ctStyle.getName().getVal();
        }
        return null;
    }
    
    public boolean hasSameName(final XWPFStyle compStyle) {
        final CTStyle ctCompStyle = compStyle.getCTStyle();
        final String name = ctCompStyle.getName().getVal();
        return name.equals(this.ctStyle.getName().getVal());
    }
}
