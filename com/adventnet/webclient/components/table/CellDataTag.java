package com.adventnet.webclient.components.table;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Writer;
import java.util.logging.Level;
import com.adventnet.webclient.util.ValueRetriever;
import com.adventnet.webclient.util.FrameWorkUtil;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import java.util.logging.Logger;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import java.util.Properties;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class CellDataTag extends BodyTagSupport
{
    private Properties cellProperties;
    private String columnName;
    private String headerName;
    private boolean execute;
    private boolean writeBody;
    private boolean jspDriven;
    private int rowIndex;
    private int columnIndex;
    private TableNavigatorModel tableModel;
    private ViewColumn viewColumn;
    private TableRenderer tableRenderer;
    private Properties instanceCatcher;
    private Logger logger;
    
    public CellDataTag() {
        this.cellProperties = null;
        this.columnName = null;
        this.headerName = null;
        this.execute = true;
        this.writeBody = false;
        this.jspDriven = false;
        this.tableRenderer = null;
        this.instanceCatcher = new Properties();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public void setColumnName(final String value) {
        this.columnName = value;
    }
    
    public int doStartTag() throws JspTagException {
        this.writeBody = false;
        final Class iteClass = TableIteratorTag.class;
        final Class tabClass = BaseTableModelTag.class;
        final Class rowClass = RowTag.class;
        final BaseTableModelTag table = (BaseTableModelTag)findAncestorWithClass((Tag)this, tabClass);
        final TableIteratorTag iterator = (TableIteratorTag)findAncestorWithClass((Tag)this, iteClass);
        final RowTag row = (RowTag)findAncestorWithClass((Tag)this, rowClass);
        this.rowIndex = 0;
        this.columnIndex = 0;
        if (row == null) {
            throw new JspTagException("The row tag is missing. The cell tag should be enclosed by a row Tag");
        }
        this.tableModel = table.getTableModel();
        final ViewColumn[] viewColumns = table.getUserViewColumns();
        this.tableRenderer = table.getTableRenderer();
        this.rowIndex = (int)this.pageContext.getAttribute("ROW_INDEX");
        final int length = viewColumns.length;
        boolean exists = false;
        if (iterator != null) {
            this.viewColumn = (ViewColumn)iterator.getValue("VIEW_COLUMN");
            this.columnIndex = this.viewColumn.getIndex();
            final String columnRenderer = this.viewColumn.getRendererClass();
            if (columnRenderer != null) {
                this.tableRenderer = ((Hashtable<K, TableRenderer>)this.instanceCatcher).get(columnRenderer);
                if (this.tableRenderer == null) {
                    try {
                        this.tableRenderer = (TableRenderer)FrameWorkUtil.getInstance().createInstance(columnRenderer);
                        ((Hashtable<String, TableRenderer>)this.instanceCatcher).put(columnRenderer, this.tableRenderer);
                    }
                    catch (final Exception ex) {
                        throw new JspTagException();
                    }
                }
                this.tableRenderer.setValueRetriever(new ValueRetriever(this.pageContext));
            }
            this.headerName = this.viewColumn.getColumnName();
            this.pageContext.setAttribute("COLUMN_NAME", (Object)this.headerName);
            if (!(this.execute = (boolean)this.pageContext.getAttribute("EXECUTE_COLUMN"))) {
                return 0;
            }
            for (int i = 0; i < length; ++i) {
                final String name = viewColumns[i].getColumnName();
                if (name.equals(this.columnName) || this.columnName.equals("*")) {
                    exists = true;
                    break;
                }
            }
        }
        else {
            this.headerName = this.columnName;
            this.jspDriven = true;
            for (int j = 0; j < length; ++j) {
                final String name2 = viewColumns[j].getColumnName();
                if (name2.equals(this.headerName)) {
                    exists = true;
                    this.columnIndex = viewColumns[j].getIndex();
                    break;
                }
            }
        }
        if (!exists) {
            this.logger.log(Level.FINE, "The columnName " + this.columnName + " is not present in the table ");
            return 0;
        }
        if (this.jspDriven && this.headerName.equals("*")) {
            return 0;
        }
        if (this.execute && (this.columnName.equals(this.headerName) || this.columnName.equals("*"))) {
            return 2;
        }
        return 0;
    }
    
    public int doEndTag() throws JspTagException {
        if (this.cellProperties != null) {
            final Enumeration enum1 = this.cellProperties.propertyNames();
            while (enum1.hasMoreElements()) {
                final String propName = enum1.nextElement();
                this.pageContext.removeAttribute(propName);
            }
        }
        if (this.writeBody) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new JspTagException(e.getMessage());
            }
        }
        return 6;
    }
    
    public void doInitBody() {
        this.writeBody = true;
        this.cellProperties = this.tableRenderer.renderCell(this.tableModel, this.rowIndex, this.columnIndex, this.viewColumn);
        if (this.cellProperties != null) {
            final Enumeration enum1 = this.cellProperties.propertyNames();
            while (enum1.hasMoreElements()) {
                final String propName = enum1.nextElement();
                final Object propValue = ((Hashtable<K, Object>)this.cellProperties).get(propName);
                this.pageContext.setAttribute(propName, propValue);
            }
        }
        if (this.jspDriven) {
            this.pageContext.setAttribute("EXECUTE_COLUMN", (Object)new Boolean(true));
        }
        else {
            this.pageContext.setAttribute("EXECUTE_COLUMN", (Object)new Boolean(false));
        }
    }
}
