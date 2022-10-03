package com.adventnet.client.tooltip.web;

import java.io.Writer;
import com.adventnet.client.tpl.TemplateAPI;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ToolTipTag extends BodyTagSupport implements WebConstants, JavaScriptConstants
{
    private String tooltiptype;
    private String tooltipid;
    private String linkid;
    private String title;
    
    public ToolTipTag() {
        this.tooltiptype = null;
        this.tooltipid = null;
        this.linkid = null;
        this.title = null;
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
            final String starthtml = TemplateAPI.givehtml(this.tooltiptype + "_Prefix", null, new Object[][] { { "TOOLTIP_ID", this.getTooltipid() }, { "TITLE", this.getTitle() } });
            this.pageContext.getOut().println(starthtml);
        }
        catch (final Exception ioe) {
            throw new JspTagException((Throwable)ioe);
        }
    }
    
    public int doAfterBody() throws JspTagException {
        try {
            final String endhtml = TemplateAPI.givehtml(this.tooltiptype + "_Suffix", null, new Object[][] { { "LINK_ID", this.getLinkid() }, { "TOOLTIP_ID", this.getTooltipid() } });
            this.pageContext.getOut().println(endhtml);
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
            catch (final Exception e) {
                throw new JspTagException(e.getMessage());
            }
        }
        return 6;
    }
    
    public String getTooltiptype() {
        return this.tooltiptype;
    }
    
    public void setTooltiptype(final String tooltiptype) {
        this.tooltiptype = tooltiptype;
    }
    
    public String getLinkid() {
        return this.linkid;
    }
    
    public void setLinkid(final String linkid) {
        this.linkid = linkid;
    }
    
    public String getTooltipid() {
        return this.tooltipid;
    }
    
    public void setTooltipid(final String tooltipid) {
        this.tooltipid = tooltipid;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
}
