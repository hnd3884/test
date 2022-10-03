package org.apache.taglibs.standard.tag.common.xml;

import javax.xml.transform.SourceLocator;
import javax.servlet.jsp.JspException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.objects.XObject;
import javax.xml.transform.TransformerException;
import javax.servlet.jsp.JspTagException;
import org.apache.xml.utils.PrefixResolver;
import javax.servlet.jsp.tagext.Tag;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

public class ForEachTag extends LoopTagSupport
{
    private XPath select;
    private XPathContext context;
    
    public void release() {
        super.release();
        this.select = null;
        this.context = null;
    }
    
    protected void prepare() throws JspTagException {
        this.context = XalanUtil.getContext((Tag)this, this.pageContext);
        try {
            final XObject nodes = this.select.execute(this.context, this.context.getCurrentNode(), (PrefixResolver)null);
            final DTMIterator iterator = nodes.iter();
            this.context.pushContextNodeList(iterator);
        }
        catch (final TransformerException e) {
            throw new JspTagException((Throwable)e);
        }
    }
    
    protected boolean hasNext() throws JspTagException {
        final DTMIterator iterator = this.context.getContextNodeList();
        return iterator.getCurrentPos() < iterator.getLength();
    }
    
    protected Object next() throws JspTagException {
        final DTMIterator iterator = this.context.getContextNodeList();
        final int next = iterator.nextNode();
        this.context.pushCurrentNode(next);
        return iterator.getDTM(next).getNode(next);
    }
    
    public int doAfterBody() throws JspException {
        this.context.popCurrentNode();
        return super.doAfterBody();
    }
    
    public void doFinally() {
        if (this.context != null) {
            this.context.popContextNodeList();
            this.context = null;
        }
        super.doFinally();
    }
    
    public void setSelect(final String select) {
        try {
            this.select = new XPath(select, (SourceLocator)null, (PrefixResolver)null, 0);
        }
        catch (final TransformerException e) {
            throw new AssertionError();
        }
    }
    
    public void setBegin(final int begin) throws JspTagException {
        this.beginSpecified = true;
        this.begin = begin;
        this.validateBegin();
    }
    
    public void setEnd(final int end) throws JspTagException {
        this.endSpecified = true;
        this.end = end;
        this.validateEnd();
    }
    
    public void setStep(final int step) throws JspTagException {
        this.stepSpecified = true;
        this.step = step;
        this.validateStep();
    }
    
    XPathContext getContext() {
        return this.context;
    }
}
