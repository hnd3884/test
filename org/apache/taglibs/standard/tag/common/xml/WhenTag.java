package org.apache.taglibs.standard.tag.common.xml;

import javax.xml.transform.SourceLocator;
import org.apache.xpath.XPathContext;
import javax.xml.transform.TransformerException;
import javax.servlet.jsp.JspTagException;
import org.apache.xml.utils.PrefixResolver;
import javax.servlet.jsp.tagext.Tag;
import org.apache.xpath.XPath;
import org.apache.taglibs.standard.tag.common.core.WhenTagSupport;

public class WhenTag extends WhenTagSupport
{
    private XPath select;
    
    public void release() {
        super.release();
        this.select = null;
    }
    
    protected boolean condition() throws JspTagException {
        final XPathContext context = XalanUtil.getContext((Tag)this, this.pageContext);
        try {
            return this.select.bool(context, context.getCurrentNode(), (PrefixResolver)null);
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
}
