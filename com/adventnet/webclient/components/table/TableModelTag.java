package com.adventnet.webclient.components.table;

import java.util.logging.Level;
import javax.servlet.jsp.JspTagException;
import java.util.logging.Logger;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;

public class TableModelTag extends BaseTableModelTag
{
    private TableNavigatorModel tableModel;
    private ViewColumn[] viewColumns;
    private Logger logger;
    
    public TableModelTag() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public int doStartTag() throws JspTagException {
        this.tableModel = null;
        super.doStartTag();
        return 2;
    }
    
    public void setModelData() throws JspTagException {
        final Object model = this.getDataModel();
        if (!(model instanceof TableNavigatorModel)) {
            throw new JspTagException("Model not an instance of TableNavigatorModel for dataSource " + this.dataSource);
        }
        this.tableModel = (TableNavigatorModel)model;
        this.logger.log(Level.FINEST, "Data Model for " + this.dataSource + " is " + this.tableModel);
        if (this.tableModel == null) {
            throw new JspTagException("Model null for dataSource " + this.dataSource);
        }
        final int columnCount = this.tableModel.getColumnCount();
        this.logger.log(Level.FINEST, "Column Count of Table Model for " + this.dataSource + " is " + columnCount);
        this.viewColumns = new ViewColumn[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            final String columnName = this.tableModel.getColumnName(i);
            this.viewColumns[i] = new ViewColumn(columnName, i, null, null);
        }
        this.logger.log(Level.FINEST, "View Columns for " + this.dataSource + " is " + this.viewColumns);
    }
    
    public TableNavigatorModel getTableModel() throws JspTagException {
        if (this.tableModel == null) {
            this.setModelData();
        }
        return this.tableModel;
    }
    
    public ViewColumn[] getViewColumns() throws JspTagException {
        return this.viewColumns;
    }
}
