package com.adventnet.webclient.components.table;

import java.util.Enumeration;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import javax.servlet.jsp.JspTagException;
import com.adventnet.webclient.util.ValueRetriever;
import com.adventnet.webclient.util.FrameWorkUtil;
import javax.servlet.jsp.tagext.Tag;
import java.util.logging.Logger;
import java.util.Properties;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class HeaderDataTag extends BodyTagSupport
{
    private String dataSource;
    private TableNavigatorModel tableModel;
    private TableRenderer tableRenderer;
    private String headerName;
    private Properties headerProperties;
    boolean execute;
    boolean writeBody;
    private boolean jspDriven;
    private String hName;
    private ViewColumn viewColumn;
    private Logger logger;
    
    public HeaderDataTag() {
        this.dataSource = null;
        this.headerName = null;
        this.headerProperties = null;
        this.execute = true;
        this.writeBody = false;
        this.jspDriven = false;
        this.hName = null;
        this.viewColumn = null;
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public void setHeaderName(final String value) {
        this.headerName = value;
    }
    
    public int doStartTag() throws JspTagException {
        this.writeBody = false;
        final Class tabClass = BaseTableModelTag.class;
        final Class iteClass = TableIteratorTag.class;
        final BaseTableModelTag table = (BaseTableModelTag)findAncestorWithClass((Tag)this, tabClass);
        final TableIteratorTag iterator = (TableIteratorTag)findAncestorWithClass((Tag)this, iteClass);
        this.tableModel = table.getTableModel();
        final ViewColumn[] viewColumns = table.getUserViewColumns();
        this.tableRenderer = table.getTableRenderer();
        final int length = viewColumns.length;
        boolean exists = false;
        if (iterator != null) {
            this.viewColumn = (ViewColumn)iterator.getValue("VIEW_COLUMN");
            this.hName = this.viewColumn.getColumnName();
            final String headerRenderer = this.viewColumn.getRendererClass();
            if (headerRenderer != null) {
                try {
                    (this.tableRenderer = (TableRenderer)FrameWorkUtil.getInstance().createInstance(headerRenderer)).setValueRetriever(new ValueRetriever(this.pageContext));
                }
                catch (final Exception ex) {
                    throw new JspTagException();
                }
            }
            this.execute = (boolean)this.pageContext.getAttribute("EXECUTE_HEADER");
            for (int i = 0; i < length; ++i) {
                final String name = viewColumns[i].getColumnName();
                if (name.equals(this.headerName) || this.headerName.equals("*")) {
                    exists = true;
                    break;
                }
            }
        }
        else {
            this.hName = this.headerName;
            this.jspDriven = true;
            for (int j = 0; j < length; ++j) {
                final String name2 = viewColumns[j].getColumnName();
                if (name2.equals(this.hName)) {
                    exists = true;
                    this.viewColumn = viewColumns[j];
                    break;
                }
            }
        }
        if (!exists) {
            this.logger.log(Level.FINE, "The headerName " + this.headerName + " is not present in the dataSource " + this.dataSource);
            return 0;
        }
        if (this.jspDriven && this.headerName.equals("*")) {
            return 0;
        }
        if (this.execute && (this.headerName.equals(this.hName) || this.headerName.equals("*"))) {
            return 2;
        }
        return 0;
    }
    
    public int doEndTag() throws JspTagException {
        if (this.headerProperties != null) {
            final Enumeration enum1 = this.headerProperties.propertyNames();
            while (enum1.hasMoreElements()) {
                final String propName = enum1.nextElement();
                this.pageContext.removeAttribute(propName);
            }
        }
        if (this.writeBody) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (final IOException e) {
                throw new JspTagException(e.getMessage());
            }
        }
        return 6;
    }
    
    public void doInitBody() {
        this.writeBody = true;
        this.headerProperties = this.tableRenderer.renderHeader(this.viewColumn);
        if (this.headerProperties != null) {
            final Enumeration enum1 = this.headerProperties.propertyNames();
            while (enum1.hasMoreElements()) {
                final String propName = enum1.nextElement();
                final String propValue = this.headerProperties.getProperty(propName, "");
                this.pageContext.setAttribute(propName, (Object)propValue);
            }
        }
        if (this.jspDriven) {
            this.pageContext.setAttribute("EXECUTE_HEADER", (Object)new Boolean(true));
        }
        else {
            this.pageContext.setAttribute("EXECUTE_HEADER", (Object)new Boolean(false));
        }
    }
}
