package org.apache.wml.dom;

import org.apache.wml.WMLCardElement;

public class WMLCardElementImpl extends WMLElementImpl implements WMLCardElement
{
    private static final long serialVersionUID = -3571126568344328924L;
    
    public WMLCardElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setOnTimer(final String s) {
        this.setAttribute("ontimer", s);
    }
    
    public String getOnTimer() {
        return this.getAttribute("ontimer");
    }
    
    public void setOrdered(final boolean b) {
        this.setAttribute("ordered", b);
    }
    
    public boolean getOrdered() {
        return this.getAttribute("ordered", true);
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
    
    public void setXmlLang(final String s) {
        this.setAttribute("xml:lang", s);
    }
    
    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }
    
    public void setTitle(final String s) {
        this.setAttribute("title", s);
    }
    
    public String getTitle() {
        return this.getAttribute("title");
    }
    
    public void setNewContext(final boolean b) {
        this.setAttribute("newcontext", b);
    }
    
    public boolean getNewContext() {
        return this.getAttribute("newcontext", false);
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
