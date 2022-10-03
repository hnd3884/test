package org.apache.taglibs.standard.tag.common.core;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.util.UrlUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class RedirectSupport extends BodyTagSupport implements ParamParent
{
    protected String url;
    protected String context;
    private String var;
    private int scope;
    private ParamSupport.ParamManager params;
    
    public RedirectSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.var = s;
        this.url = s;
        this.params = null;
        this.scope = 1;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void addParameter(final String name, final String value) {
        this.params.addParameter(name, value);
    }
    
    public int doStartTag() throws JspException {
        this.params = new ParamSupport.ParamManager();
        return 2;
    }
    
    public int doEndTag() throws JspException {
        final String baseUrl = UrlSupport.resolveUrl(this.url, this.context, this.pageContext);
        String result = this.params.aggregateParams(baseUrl);
        final HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
        if (!UrlUtil.isAbsoluteUrl(result)) {
            result = response.encodeRedirectURL(result);
        }
        try {
            response.sendRedirect(result);
        }
        catch (final IOException ex) {
            throw new JspTagException(ex.toString(), (Throwable)ex);
        }
        return 5;
    }
    
    public void release() {
        this.init();
    }
}
