package com.adventnet.client.components.table.web;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ColumnTag extends BodyTagSupport implements TableConstants
{
    private Logger logger;
    private TableTransformerContext context;
    private TableIterator iter;
    private ViewContext viewContext;
    
    public ColumnTag() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.context = null;
        this.iter = null;
    }
    
    public int doStartTag() throws JspTagException {
        this.initialize();
        if (this.viewContext == null) {
            this.viewContext = (ViewContext)this.pageContext.findAttribute("VIEW_CTX");
        }
        final String uniqueId = this.viewContext.getUniqueId();
        final TableViewModel viewModel = (TableViewModel)this.viewContext.getViewModel();
        this.context = viewModel.getTableTransformerContext();
        (this.iter = viewModel.getTableIterator()).setCurrentColumn(-1);
        return 2;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
        }
        catch (final IOException e) {
            throw new JspTagException(e.getMessage());
        }
        return 6;
    }
    
    public void doInitBody() {
        this.iter.nextColumn();
    }
    
    public int doAfterBody() {
        return this.iter.nextColumn() ? 2 : 6;
    }
    
    public void setViewContext(final ViewContext vc) {
        this.viewContext = vc;
    }
    
    public ViewContext getViewContext() {
        return this.viewContext;
    }
    
    private void initialize() {
        this.bodyContent = null;
    }
    
    public void release() {
        this.viewContext = null;
    }
}
