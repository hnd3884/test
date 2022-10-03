package com.adventnet.webclient.components.table;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import java.util.logging.Level;
import javax.servlet.jsp.tagext.Tag;
import java.util.logging.Logger;
import java.util.Vector;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TableIteratorTag extends BodyTagSupport
{
    private Vector tableColumns;
    private int currentColumn;
    private int totalColumns;
    private ViewColumn[] viewColumns;
    private Logger logger;
    
    public TableIteratorTag() {
        this.tableColumns = null;
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public int doStartTag() throws JspTagException {
        final Class baseTableModelClass = BaseTableModelTag.class;
        final BaseTableModelTag table = (BaseTableModelTag)findAncestorWithClass((Tag)this, baseTableModelClass);
        final TableNavigatorModel model = table.getTableModel();
        this.viewColumns = table.getUserViewColumns();
        this.currentColumn = 0;
        this.totalColumns = 0;
        this.totalColumns = this.viewColumns.length;
        this.logger.log(Level.FINE, "About to iterate for " + new Integer(this.totalColumns) + " columns");
        return 2;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
        }
        catch (final IOException e) {
            throw new JspTagException(e.getMessage());
        }
        this.pageContext.removeAttribute("EXECUTE_HEADER");
        this.pageContext.removeAttribute("EXECUTE_COLUMN");
        this.pageContext.removeAttribute("COLUMN_INDEX");
        return 6;
    }
    
    public void doInitBody() {
        this.evalBody();
    }
    
    public int evalBody() {
        if (this.currentColumn >= this.totalColumns) {
            return 6;
        }
        if (this.viewColumns[this.currentColumn] == null) {
            return 6;
        }
        final int index = this.viewColumns[this.currentColumn].getIndex();
        this.setValue("VIEW_COLUMN", (Object)this.viewColumns[this.currentColumn]);
        this.logger.log(Level.FINE, "Setting value {0}", this.viewColumns[this.currentColumn]);
        this.pageContext.setAttribute("COLUMN_INDEX", (Object)new Integer(index));
        this.pageContext.setAttribute("EXECUTE_COLUMN", (Object)new Boolean(true));
        this.pageContext.setAttribute("EXECUTE_HEADER", (Object)new Boolean(true));
        return 2;
    }
    
    public int doAfterBody() {
        if (this.currentColumn < this.totalColumns) {
            ++this.currentColumn;
            return this.evalBody();
        }
        return 6;
    }
}
