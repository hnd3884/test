package com.adventnet.client.box.web;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class BoxTag extends BodyTagSupport implements WebConstants, JavaScriptConstants
{
    private String boxType;
    private BoxCreator boxCreator;
    private boolean isOpen;
    public String title;
    public String boxId;
    private String showMethod;
    private String hideMethod;
    private String effectDuration;
    
    public BoxTag() {
        this.boxType = null;
        this.boxCreator = null;
        this.isOpen = true;
        this.title = "";
        this.boxId = "";
        this.showMethod = "Effect.SlideDown";
        this.hideMethod = "Effect.SlideUp";
        this.effectDuration = "0.5";
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public void setBoxId(final String boxId) {
        this.boxId = boxId;
    }
    
    public void setBoxType(final String boxType) {
        this.boxType = boxType;
    }
    
    public void setIsOpen(final boolean isOpen) {
        this.isOpen = isOpen;
    }
    
    public String getBoxType() {
        return this.boxType;
    }
    
    public int doStartTag() throws JspTagException {
        try {
            return 2;
        }
        catch (final Exception e) {
            throw new JspTagException((Throwable)e);
        }
    }
    
    public void doInitBody() throws JspTagException {
        try {
            final String key = this.boxType + "_Prefix";
            final HashMap options = new HashMap();
            options.put("SHOWMETHOD", this.showMethod);
            options.put("HIDEMETHOD", this.hideMethod);
            options.put("EFFECTDURATION", this.effectDuration);
            this.pageContext.getOut().println(BoxAPI.getHtml(key, this.boxId, this.title, this.pageContext, this.isOpen, options) + "<div>");
        }
        catch (final Exception ioe) {
            throw new JspTagException((Throwable)ioe);
        }
    }
    
    public int doAfterBody() throws JspTagException {
        try {
            final String key = this.boxType + "_Suffix";
            this.pageContext.getOut().println("</div>" + BoxAPI.getHtml(key, this.boxId, this.title, this.pageContext, this.isOpen, null));
        }
        catch (final Exception ioe) {
            throw new JspTagException((Throwable)ioe);
        }
        return 6;
    }
    
    public int doEndTag() throws JspTagException {
        if (this.bodyContent != null) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (final IOException e) {
                throw new JspTagException(e.getMessage());
            }
        }
        return 6;
    }
    
    public String getEffectDuration() {
        return this.effectDuration;
    }
    
    public void setEffectDuration(final String effectDuration) {
        this.effectDuration = effectDuration;
    }
    
    public String getHideMethod() {
        return this.hideMethod;
    }
    
    public void setHideMethod(final String hideMethod) {
        this.hideMethod = hideMethod;
    }
    
    public String getShowMethod() {
        return this.showMethod;
    }
    
    public void setShowMethod(final String showMethod) {
        this.showMethod = showMethod;
    }
}
