package com.adventnet.webclient.components.table;

import java.util.HashMap;
import java.io.IOException;
import java.io.Writer;
import com.adventnet.webclient.ClientException;
import java.util.logging.Level;
import javax.servlet.jsp.JspTagException;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.webclient.components.BaseTagSupport;

public abstract class BaseTableModelTag extends BaseTagSupport
{
    private TableNavigatorModel tableModel;
    private TableRenderer tableRenderer;
    private ViewColumn[] viewColumns;
    private ViewColumn[] userViewColumns;
    private String userVar;
    private String originalVar;
    private ArrayList userList;
    private ArrayList originalList;
    private HttpSession session;
    private Logger logger;
    protected String uniqueId;
    public String viewList;
    public String renderer;
    
    public BaseTableModelTag() {
        this.userVar = null;
        this.originalVar = null;
        this.userList = null;
        this.originalList = null;
        this.session = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.viewList = null;
        this.renderer = null;
    }
    
    public void setViewList(final String viewListName) {
        this.viewList = viewListName;
    }
    
    public void setRenderer(final String renderer) {
        this.renderer = renderer;
    }
    
    public int doStartTag() throws JspTagException {
        super.doStartTag();
        this.session = this.pageContext.getSession();
        return 2;
    }
    
    public void doInitBody() throws JspTagException {
        this.tableModel = this.getTableModel();
        if (this.renderer == null) {
            this.renderer = "com.adventnet.webclient.components.table.di.DefaultTableRenderer";
            this.logger.log(Level.FINE, "No renderer specified for table. Therefore using default.");
        }
        try {
            this.tableRenderer = (TableRenderer)this.util.createInstance(this.renderer);
        }
        catch (final ClientException e) {
            throw new JspTagException(e.getMessage());
        }
        this.tableRenderer.setValueRetriever(this.retrieve);
        this.viewColumns = this.getViewColumns();
        this.setUserViewColumns();
        final long recordsCount = this.tableModel.getTotalRecordsCount();
        final long startIndex = this.tableModel.getStartIndex();
        final long endIndex = this.tableModel.getEndIndex();
        final int columnCount = this.userViewColumns.length;
        final int rowCount = this.tableModel.getRowCount();
        final long pageLength = this.tableModel.getPageLength();
        this.logger.log(Level.FINE, "TableModel contains " + recordsCount + " Records, " + rowCount + " Rows and " + columnCount + " Columns");
        this.pageContext.setAttribute("RECORDS_COUNT", (Object)new Long(recordsCount));
        this.pageContext.setAttribute("START_INDEX", (Object)new Long(startIndex));
        this.pageContext.setAttribute("END_INDEX", (Object)new Long(endIndex));
        this.pageContext.setAttribute("PAGE_LENGTH", (Object)new Long(pageLength));
        this.pageContext.setAttribute("COLUMN_COUNT", (Object)new Integer(columnCount));
        this.pageContext.setAttribute("ROW_COUNT", (Object)new Integer(rowCount));
        this.pageContext.setAttribute("USER_VAR", (Object)this.userVar);
        this.pageContext.setAttribute("ORIGINAL_VAR", (Object)this.originalVar);
    }
    
    public TableRenderer getTableRenderer() {
        return this.tableRenderer;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
        }
        catch (final IOException e) {
            throw new JspTagException(e.getMessage());
        }
        this.pageContext.removeAttribute("SET_CLASS");
        this.pageContext.removeAttribute("CSS_CLASS");
        this.pageContext.removeAttribute("CURRENT_CLASS");
        return 6;
    }
    
    public ViewColumn[] getUserViewColumns() {
        return this.userViewColumns;
    }
    
    private void setUserViewColumns() {
        String key = null;
        if (this.dataSource != null) {
            key = this.dataSource;
        }
        this.userVar = this.getKey() + "_userList";
        this.originalVar = this.getKey() + "_originalList";
        this.userList = (ArrayList)this.pageContext.findAttribute(this.userVar);
        if (this.viewList != null) {
            this.userList = (ArrayList)this.pageContext.findAttribute(this.viewList);
        }
        final HashMap originalColumnMap = new HashMap();
        if (this.userList == null || this.userList.isEmpty()) {
            this.userList = new ArrayList();
            this.originalList = new ArrayList();
            for (int i = 0; i < this.viewColumns.length; ++i) {
                this.userList.add(this.viewColumns[i].getColumnName());
                this.originalList.add(this.viewColumns[i].getColumnName());
            }
            this.session.setAttribute(this.originalVar, (Object)this.originalList);
        }
        this.session.setAttribute(this.userVar, (Object)this.userList);
        for (int i = 0; i < this.viewColumns.length; ++i) {
            originalColumnMap.put(this.viewColumns[i].getColumnName(), this.viewColumns[i]);
        }
        this.userViewColumns = new ViewColumn[this.userList.size()];
        for (int i = 0; i < this.userList.size(); ++i) {
            final String columnName = this.userList.get(i);
            this.userViewColumns[i] = originalColumnMap.get(columnName);
        }
    }
    
    public String getKey() {
        if (this.uniqueId != null) {
            return this.uniqueId;
        }
        return this.dataSource;
    }
    
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    public void setUniqueId(final String uniqueIdArg) {
        this.uniqueId = uniqueIdArg;
    }
    
    protected String getStateParameter(final String parameter) {
        if (this.uniqueId == null) {
            return this.request.getParameter(parameter);
        }
        return (String)this.session.getAttribute(this.uniqueId + "_" + parameter);
    }
    
    protected String getCurrentStateParameter(final String parameter) {
        if (this.uniqueId == null) {
            return this.request.getParameter(parameter);
        }
        final String attribute = (String)this.session.getAttribute(this.uniqueId + "_" + parameter);
        this.session.removeAttribute(this.uniqueId + "_" + parameter);
        return attribute;
    }
    
    public abstract TableNavigatorModel getTableModel() throws JspTagException;
    
    public abstract ViewColumn[] getViewColumns() throws JspTagException;
}
