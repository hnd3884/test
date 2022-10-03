package com.adventnet.client.view.web;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ViewInitializerTag extends BodyTagSupport implements WebConstants
{
    private static Logger logger;
    protected ViewContext viewCtx;
    
    public int doStartTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            this.viewCtx = (ViewContext)WebClientUtil.getRequiredAttribute("VIEW_CTX", request);
            this.pageContext.setAttribute("VIEWCONFIG", (Object)this.viewCtx.getModel().getViewConfiguration());
            return 2;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspException((Throwable)ex);
        }
    }
    
    public int doEndTag() throws JspTagException {
        try {
            if (this.bodyContent != null) {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
        }
        catch (final Exception e) {
            throw new JspTagException((Throwable)e);
        }
        return 6;
    }
    
    static {
        ViewInitializerTag.logger = Logger.getLogger(TagSupport.class.getName());
    }
}
