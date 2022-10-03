package com.adventnet.client.components.table.web;

import javax.servlet.jsp.PageContext;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.io.Writer;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class CellTag extends BodyTagSupport implements TableConstants
{
    private String type;
    ViewContext viewContext;
    
    public CellTag() {
        this.type = "Cell";
        this.viewContext = null;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
        }
        catch (final IOException e) {
            throw new JspTagException(e.getMessage());
        }
        this.type = "Cell";
        return 6;
    }
    
    public int doStartTag() throws JspTagException {
        this.bodyContent = null;
        if (this.viewContext == null) {
            final PageContext pageContext = this.pageContext;
            final String s = "VIEW_CTX";
            final PageContext pageContext2 = this.pageContext;
            this.viewContext = (ViewContext)pageContext.getAttribute(s, 2);
        }
        final String uniqueId = this.viewContext.getUniqueId();
        final TableViewModel viewModel = (TableViewModel)this.viewContext.getViewModel();
        try {
            viewModel.getTableIterator().initTransCtxForCurrentCell(this.type);
        }
        catch (final Exception e) {
            throw new JspTagException(e.getMessage(), e.getCause());
        }
        return 2;
    }
    
    public void setViewContext(final ViewContext vc) {
        this.viewContext = vc;
    }
    
    public ViewContext getViewContext() {
        return this.viewContext;
    }
    
    public void release() {
        this.viewContext = null;
    }
}
