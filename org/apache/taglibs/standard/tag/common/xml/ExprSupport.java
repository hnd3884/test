package org.apache.taglibs.standard.tag.common.xml;

import javax.xml.transform.SourceLocator;
import javax.servlet.jsp.JspException;
import org.apache.xpath.XPathContext;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.util.EscapeXML;
import org.apache.xml.utils.PrefixResolver;
import javax.servlet.jsp.tagext.Tag;
import org.apache.xpath.XPath;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class ExprSupport extends TagSupport
{
    private XPath select;
    protected boolean escapeXml;
    
    public ExprSupport() {
        this.escapeXml = true;
    }
    
    public void release() {
        super.release();
        this.select = null;
    }
    
    public int doStartTag() throws JspException {
        try {
            final XPathContext context = XalanUtil.getContext((Tag)this, this.pageContext);
            final String result = this.select.execute(context, context.getCurrentNode(), (PrefixResolver)null).str();
            EscapeXML.emit(result, this.escapeXml, this.pageContext.getOut());
            return 0;
        }
        catch (final IOException ex) {
            throw new JspTagException(ex.toString(), (Throwable)ex);
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
