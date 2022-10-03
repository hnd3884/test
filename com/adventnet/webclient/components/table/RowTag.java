package com.adventnet.webclient.components.table;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspWriter;
import com.adventnet.webclient.util.ValueRetriever;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class RowTag extends BodyTagSupport
{
    private TableNavigatorModel tableModel;
    private ValueRetriever retriever;
    private long totalRowCount;
    private int currentRow;
    private JspWriter writer;
    private String originalValuesRequired;
    
    public RowTag() {
        this.tableModel = null;
        this.totalRowCount = 0L;
        this.currentRow = 0;
        this.writer = null;
        this.originalValuesRequired = "false";
    }
    
    public void setOriginalValuesRequired(final String required) {
        this.originalValuesRequired = required;
    }
    
    public String getOriginalValuesRequired() {
        return this.originalValuesRequired;
    }
    
    public int doStartTag() throws JspTagException {
        this.writer = this.pageContext.getOut();
        final Class tableModelClass = BaseTableModelTag.class;
        final BaseTableModelTag table = (BaseTableModelTag)findAncestorWithClass((Tag)this, tableModelClass);
        this.retriever = (ValueRetriever)table.getValue("RETRIEVER");
        this.bodyContent = null;
        this.tableModel = table.getTableModel();
        this.totalRowCount = this.tableModel.getRowCount();
        if (this.totalRowCount == 0L) {
            return 0;
        }
        this.currentRow = 0;
        return 2;
    }
    
    public void doInitBody() throws JspTagException {
        this.evalBody();
    }
    
    public int doEndTag() throws JspTagException {
        if (this.bodyContent != null) {
            try {
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
            catch (final IOException e) {
                throw new JspTagException(e.getMessage());
            }
        }
        this.originalValuesRequired = "false";
        this.pageContext.removeAttribute("ROW_INDEX");
        return 6;
    }
    
    public int evalBody() throws JspTagException {
        if (this.currentRow < this.totalRowCount) {
            this.pageContext.setAttribute("ROW_INDEX", (Object)new Integer(this.currentRow));
            final int columnCount = this.tableModel.getColumnCount();
            final Properties columnProps = new Properties();
            for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
                final String columnName = this.tableModel.getColumnName(columnIndex);
                final Object value = this.tableModel.getValueAt(this.currentRow, columnIndex);
                if (columnName != null && value != null) {
                    ((Hashtable<String, Object>)columnProps).put(columnName, value);
                }
            }
            this.retriever.setDataModel(columnProps);
            this.setValue("ROW_DATA", (Object)columnProps);
            this.pageContext.setAttribute("DATA_PROPERTIES", (Object)columnProps);
            this.pageContext.setAttribute("DATA_OBJECT_NUMBER", (Object)new Integer(this.currentRow));
            if (this.originalValuesRequired.equals("true")) {
                try {
                    this.writer.println(this.getOriginalValues(columnProps));
                }
                catch (final IOException ioe) {
                    throw new JspTagException((Throwable)ioe);
                }
            }
            return 2;
        }
        return 6;
    }
    
    public int getCurrentRow() {
        return this.currentRow;
    }
    
    public int doAfterBody() throws JspTagException {
        if (this.currentRow < this.totalRowCount) {
            ++this.currentRow;
            return this.evalBody();
        }
        return 6;
    }
    
    private String getOriginalValues(final Properties dataProps) {
        if (dataProps == null) {
            return null;
        }
        final String objectName = "originalValues";
        final StringBuffer dataBuffer = new StringBuffer();
        dataBuffer.append("<Script language=\"Javascript\">\n");
        dataBuffer.append("\t");
        dataBuffer.append(objectName + this.currentRow);
        dataBuffer.append(" = ");
        dataBuffer.append("new Object();\n");
        final Enumeration enumeration = dataProps.propertyNames();
        while (enumeration.hasMoreElements()) {
            final String propName = enumeration.nextElement();
            final Object propValue = ((Hashtable<K, Object>)dataProps).get(propName);
            dataBuffer.append("\t");
            dataBuffer.append(objectName + this.currentRow);
            dataBuffer.append(".");
            dataBuffer.append(propName);
            dataBuffer.append(" = ");
            dataBuffer.append("\"");
            dataBuffer.append(propValue);
            dataBuffer.append("\"");
            dataBuffer.append(";\n\n");
        }
        dataBuffer.append("</Script>\n");
        return dataBuffer.toString();
    }
}
