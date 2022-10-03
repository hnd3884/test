package org.apache.wml.dom;

import org.apache.wml.WMLTemplateElement;

public class WMLTemplateElementImpl extends WMLElementImpl implements WMLTemplateElement
{
    private static final long serialVersionUID = 4231732841621131049L;
    
    public WMLTemplateElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setOnTimer(final String s) {
        this.setAttribute("ontimer", s);
    }
    
    public String getOnTimer() {
        return this.getAttribute("ontimer");
    }
    
    public void setOnEnterBackward(final String s) {
        this.setAttribute("onenterbackward", s);
    }
    
    public String getOnEnterBackward() {
        return this.getAttribute("onenterbackward");
    }
    
    public void setClassName(final String s) {
        this.setAttribute("class", s);
    }
    
    public String getClassName() {
        return this.getAttribute("class");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setOnEnterForward(final String s) {
        this.setAttribute("onenterforward", s);
    }
    
    public String getOnEnterForward() {
        return this.getAttribute("onenterforward");
    }
}
