package org.apache.taglibs.standard.tag.common.core;

import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletResponse;
import org.apache.taglibs.standard.util.UrlUtil;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class UrlSupport extends BodyTagSupport implements ParamParent
{
    protected String value;
    protected String context;
    private String var;
    private int scope;
    private ParamSupport.ParamManager params;
    
    public UrlSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.var = s;
        this.value = s;
        this.params = null;
        this.context = null;
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
        final String baseUrl = resolveUrl(this.value, this.context, this.pageContext);
        String result = this.params.aggregateParams(baseUrl);
        if (!UrlUtil.isAbsoluteUrl(result)) {
            final HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
            result = response.encodeURL(result);
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)result, this.scope);
        }
        else {
            try {
                this.pageContext.getOut().print(result);
            }
            catch (final IOException ex) {
                throw new JspTagException(ex.toString(), (Throwable)ex);
            }
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
    
    public static String resolveUrl(final String url, final String context, final PageContext pageContext) throws JspException {
        if (UrlUtil.isAbsoluteUrl(url)) {
            return url;
        }
        final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        if (context == null) {
            if (url.startsWith("/")) {
                return request.getContextPath() + url;
            }
            return url;
        }
        else {
            if (!context.startsWith("/") || !url.startsWith("/")) {
                throw new JspTagException(Resources.getMessage("IMPORT_BAD_RELATIVE"));
            }
            if (context.endsWith("/") && url.startsWith("/")) {
                return context.substring(0, context.length() - 1) + url;
            }
            return context + url;
        }
    }
}
