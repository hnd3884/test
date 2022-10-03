package org.apache.taglibs.standard.tag.common.core;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.util.EscapeXML;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class OutSupport extends BodyTagSupport
{
    private Object output;
    
    public void release() {
        this.output = null;
        super.release();
    }
    
    public int doStartTag() throws JspException {
        this.bodyContent = null;
        this.output = this.evalValue();
        if (this.output != null) {
            return 0;
        }
        this.output = this.evalDefault();
        if (this.output != null) {
            return 0;
        }
        this.output = "";
        return 2;
    }
    
    protected abstract Object evalValue() throws JspException;
    
    protected abstract String evalDefault() throws JspException;
    
    protected abstract boolean evalEscapeXml() throws JspException;
    
    public int doAfterBody() throws JspException {
        this.output = this.bodyContent.getString().trim();
        return 0;
    }
    
    public int doEndTag() throws JspException {
        try {
            final boolean escapeXml = this.evalEscapeXml();
            EscapeXML.emit(this.output, escapeXml, this.pageContext.getOut());
        }
        catch (final IOException e) {
            throw new JspTagException((Throwable)e);
        }
        finally {
            this.output = null;
        }
        return 6;
    }
}
