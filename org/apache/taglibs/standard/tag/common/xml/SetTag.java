package org.apache.taglibs.standard.tag.common.xml;

import org.apache.taglibs.standard.tag.common.core.Util;
import javax.xml.transform.SourceLocator;
import javax.servlet.jsp.JspException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import javax.xml.transform.TransformerException;
import javax.servlet.jsp.JspTagException;
import org.apache.xml.utils.PrefixResolver;
import javax.servlet.jsp.tagext.Tag;
import org.apache.xpath.XPath;
import javax.servlet.jsp.tagext.TagSupport;

public class SetTag extends TagSupport
{
    private XPath select;
    private String var;
    private int scope;
    
    public SetTag() {
        this.scope = 1;
    }
    
    public void release() {
        super.release();
        this.select = null;
        this.var = null;
    }
    
    public int doStartTag() throws JspException {
        try {
            final XPathContext context = XalanUtil.getContext((Tag)this, this.pageContext);
            final XObject result = this.select.execute(context, context.getCurrentNode(), (PrefixResolver)null);
            this.pageContext.setAttribute(this.var, XalanUtil.coerceToJava(result), this.scope);
            return 0;
        }
        catch (final TransformerException e) {
            throw new JspTagException((Throwable)e);
        }
    }
    
    public void setSelect(final String select) {
        try {
            this.select = new XPath(select, (SourceLocator)null, (PrefixResolver)null, 0);
        }
        catch (final TransformerException e) {
            throw new AssertionError();
        }
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
}
