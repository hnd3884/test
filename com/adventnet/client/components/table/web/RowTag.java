package com.adventnet.client.components.table.web;

import com.adventnet.persistence.DataObject;
import com.adventnet.iam.xss.IAMEncoder;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.jsp.JspWriter;
import javax.swing.table.TableModel;
import com.adventnet.client.util.web.JavaScriptConstants;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class RowTag extends BodyTagSupport implements JavaScriptConstants, TableConstants
{
    private TableModel tableModel;
    private boolean javaScriptRow;
    private JspWriter writer;
    private ViewContext viewContext;
    private TableIterator iter;
    private boolean dynamicUpdate;
    
    public RowTag() {
        this.tableModel = null;
        this.javaScriptRow = false;
        this.writer = null;
        this.viewContext = null;
        this.iter = null;
        this.dynamicUpdate = false;
    }
    
    public void setJavaScriptRow(final boolean jsRow) {
        this.javaScriptRow = jsRow;
    }
    
    public boolean getJavaScriptRow() {
        return this.javaScriptRow;
    }
    
    public int doStartTag() throws JspTagException {
        this.initialize();
        if (this.viewContext == null) {
            this.viewContext = (ViewContext)this.pageContext.findAttribute("VIEW_CTX");
        }
        final TableViewModel viewModel = (TableViewModel)this.viewContext.getViewModel();
        this.tableModel = (TableModel)viewModel.getTableModel();
        (this.iter = viewModel.getTableIterator()).setCurrentRow(-1);
        if (this.iter.hasNextRow()) {
            return 2;
        }
        if (this.javaScriptRow) {
            final String uniqueId = this.viewContext.getUniqueId();
            try {
                if (!this.dynamicUpdate) {
                    this.writer.println(this.generateJS(this.tableModel, uniqueId));
                }
                else {
                    this.writer.println(this.updateJS(this.tableModel, uniqueId));
                }
            }
            catch (final Exception e) {
                throw new JspTagException((Throwable)e);
            }
        }
        return 0;
    }
    
    public void setViewContext(final ViewContext vc) {
        this.viewContext = vc;
    }
    
    public boolean isDynamicUpdate() {
        return this.dynamicUpdate;
    }
    
    public void setDynamicUpdate(final boolean isDynamicUpdate) {
        this.dynamicUpdate = isDynamicUpdate;
    }
    
    public ViewContext getViewContext() {
        return this.viewContext;
    }
    
    public void doInitBody() throws JspTagException {
        this.iter.nextRow();
    }
    
    public int doEndTag() throws JspTagException {
        if (this.bodyContent != null) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
                if (this.javaScriptRow) {
                    final String uniqueId = this.viewContext.getUniqueId();
                    if (!this.dynamicUpdate) {
                        this.writer.println(this.generateJS(this.tableModel, uniqueId));
                    }
                    else {
                        final String updateScript = this.updateJS(this.tableModel, uniqueId);
                        this.writer.println(updateScript);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new JspTagException(e.getMessage());
            }
        }
        this.cleanUp();
        return 6;
    }
    
    public int doAfterBody() throws JspTagException {
        return this.iter.nextRow() ? 2 : 6;
    }
    
    private void initialize() {
        this.writer = this.pageContext.getOut();
        this.tableModel = null;
        this.bodyContent = null;
    }
    
    public void release() {
        this.viewContext = null;
    }
    
    private void cleanUp() {
        this.javaScriptRow = false;
    }
    
    private String getColumnsJS(final TableModel model, final String uniqueId) throws Exception {
        final StringBuffer buffer = new StringBuffer();
        final int count = model.getColumnCount();
        buffer.append("[");
        for (int i = 0; i < count; ++i) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append("'");
            buffer.append(IAMEncoder.encodeJavaScript(model.getColumnName(i)));
            buffer.append("'");
        }
        buffer.append("]");
        return buffer.toString();
    }
    
    public String generateJS(final TableModel model, final String uniqueId) throws Exception {
        final StringBuffer jsBuffer = new StringBuffer();
        final DataObject tableDO = this.viewContext.getModel().getViewConfiguration();
        jsBuffer.append("<script>");
        final String rowSelection = (String)tableDO.getFirstValue("ACTableViewConfig", "ENABLEROWSELECTION");
        final TableViewModel viewModel = (TableViewModel)this.viewContext.getViewModel();
        final String columnArray = this.getColumnsJS(model, uniqueId);
        final StringBuffer buffer = new StringBuffer("_TM = new TableDOMModel('" + IAMEncoder.encodeJavaScript(uniqueId) + "'," + columnArray + ",[");
        final Object[] viewColumns = viewModel.getViewColumns();
        for (int i = 0; i < viewColumns.length; ++i) {
            buffer.append('\"').append(((Object[])viewColumns[i])[0]).append("\",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("],'").append(IAMEncoder.encodeJavaScript(rowSelection)).append("');");
        jsBuffer.append(buffer.toString());
        for (int rowCnt = model.getRowCount(), j = 0; j < rowCnt; ++j) {
            final StringBuffer colVals = new StringBuffer();
            for (int columnCount = model.getColumnCount(), count = 0; count < columnCount; ++count) {
                if (count > 0) {
                    colVals.append(",");
                }
                final Object obj = model.getValueAt(j, count);
                if (obj != null) {
                    colVals.append("'");
                    if (obj instanceof String) {
                        colVals.append(IAMEncoder.encodeJavaScript((String)obj));
                    }
                    else {
                        colVals.append(obj);
                    }
                    colVals.append("'");
                }
                else {
                    colVals.append("null");
                }
            }
            jsBuffer.append("\n_TM.add(" + colVals.toString() + ");");
        }
        jsBuffer.append("\n");
        jsBuffer.append("</script>");
        return jsBuffer.toString();
    }
    
    public String updateJS(final TableModel model, final String uniqueId) throws Exception {
        final String initialScript = "var _DTM=TableModel.getInstance('" + IAMEncoder.encodeJavaScript(this.viewContext.getUniqueId()) + "');";
        final StringBuffer jsBuffer = new StringBuffer();
        jsBuffer.append("<script>");
        jsBuffer.append(initialScript);
        for (int rowCnt = model.getRowCount(), i = 0; i < rowCnt; ++i) {
            final StringBuffer colVals = new StringBuffer();
            for (int columnCount = model.getColumnCount(), count = 0; count < columnCount; ++count) {
                if (count > 0) {
                    colVals.append(",");
                }
                final Object obj = model.getValueAt(i, count);
                if (obj != null) {
                    colVals.append("'");
                    if (obj instanceof String) {
                        colVals.append(IAMEncoder.encodeJavaScript((String)obj));
                        colVals.append("'");
                    }
                    else {
                        colVals.append(obj);
                        colVals.append("'");
                    }
                }
                else {
                    colVals.append("null");
                }
            }
            jsBuffer.append("\n");
            jsBuffer.append("_DTM.add(" + colVals.toString() + ");");
        }
        jsBuffer.append("\n");
        jsBuffer.append("</script>");
        return jsBuffer.toString();
    }
}
