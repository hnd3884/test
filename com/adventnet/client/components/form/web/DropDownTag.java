package com.adventnet.client.components.form.web;

import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewManager;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import javax.servlet.jsp.JspException;
import com.adventnet.iam.xss.IAMEncoder;
import javax.swing.table.TableModel;
import com.adventnet.customview.service.ServiceConfiguration;
import com.adventnet.customview.service.SQTemplateValuesServiceConfiguration;
import com.adventnet.customview.CustomViewRequest;
import java.util.HashMap;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import javax.servlet.jsp.tagext.TagSupport;

public class DropDownTag extends TagSupport
{
    private String dropDownName;
    private String cssClass;
    
    public DropDownTag() {
        this.dropDownName = null;
        this.cssClass = null;
    }
    
    public void setConfigName(final String configName) {
        this.dropDownName = configName;
    }
    
    public String getConfigName() {
        return this.dropDownName;
    }
    
    public void setCssClass(final String cssClass) {
        this.cssClass = cssClass;
    }
    
    public String getCssClass() {
        return this.cssClass;
    }
    
    public int doStartTag() throws JspException {
        try {
            DataObject dropDownDO = null;
            Row ddRow = new Row("ACDropDown");
            ddRow.set("NAME", (Object)this.dropDownName);
            dropDownDO = LookUpUtil.getPersistence().getForPersonality("ACDropDown", ddRow);
            ddRow = dropDownDO.getFirstRow("ACDropDown");
            final String serverColumn = (String)ddRow.get("COLUMNFORSERVER");
            final String clientColumn = (String)ddRow.get("COLUMNFORCLIENT");
            final String tableName = (String)ddRow.get("TABLENAME");
            final String cvName = (String)ddRow.get("CVNAME");
            final String jsMethod = (String)ddRow.get("ONSELECTMETHODNAME");
            final String defaultValue = (String)ddRow.get("DEFAULTVALUE");
            final String selectedKey = (String)ddRow.get("SELECTEDKEY");
            String value = null;
            if (selectedKey != null) {
                value = this.pageContext.getRequest().getParameter(selectedKey);
            }
            SelectQuery query = null;
            if (tableName != null) {
                Column column = new Column(tableName, serverColumn);
                query = (SelectQuery)new SelectQueryImpl(new Table(tableName));
                column = column.distinct();
                column.setColumnAlias(serverColumn);
                query.addSelectColumn(column);
                if (clientColumn != null) {
                    final Column ccolumn = new Column(tableName, clientColumn);
                    query.addSelectColumn(ccolumn);
                }
            }
            else {
                if (cvName == null) {
                    throw new RuntimeException("Either CVName or TableName should be specified");
                }
                final Row customViewRow = new Row("CustomViewConfiguration");
                customViewRow.set(2, (Object)cvName);
                final DataObject customViewDO = LookUpUtil.getPersistence().get("CustomViewConfiguration", customViewRow);
                final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", 3);
                query = QueryUtil.getSelectQuery(queryID);
            }
            query.setRange(new Range(1, 0));
            HashMap criteriaMap = null;
            if (dropDownDO.containsTable("ACDropDownParams")) {
                criteriaMap = new HashMap();
                final Iterator iterator = dropDownDO.getRows("ACDropDownParams");
                while (iterator.hasNext()) {
                    final Row tempRow = iterator.next();
                    final String paramName = (String)tempRow.get("PARAMNAME");
                    final String paramValue = this.pageContext.getRequest().getParameter(paramName);
                    if (paramValue != null) {
                        criteriaMap.put(paramName, paramValue);
                    }
                }
            }
            final CustomViewRequest cvRequest = new CustomViewRequest(query);
            final CustomViewManager cvMgr = LookUpUtil.getCVManagerForTable();
            if (criteriaMap != null) {
                final SQTemplateValuesServiceConfiguration serConfig = new SQTemplateValuesServiceConfiguration(criteriaMap);
                cvRequest.putServiceConfiguration((ServiceConfiguration)serConfig);
            }
            final ViewData viewData = cvMgr.getData(cvRequest);
            final TableModel tableModel = (TableModel)viewData.getModel();
            final StringBuffer buffer = new StringBuffer("<Select name='");
            buffer.append(IAMEncoder.encodeHTMLAttribute(this.dropDownName));
            buffer.append("'");
            if (jsMethod != null) {
                buffer.append(" onChange='");
                buffer.append(IAMEncoder.encodeHTMLAttribute(jsMethod));
                buffer.append("(this);'");
            }
            if (this.cssClass != null) {
                buffer.append(" class='");
                buffer.append(IAMEncoder.encodeHTMLAttribute(this.cssClass));
                buffer.append("'");
            }
            buffer.append(">");
            if (defaultValue != null) {
                buffer.append("<Option value='" + IAMEncoder.encodeHTMLAttribute(defaultValue) + "'>");
                buffer.append(IAMEncoder.encodeHTML(defaultValue));
                buffer.append("</Option>");
            }
            final int size = tableModel.getRowCount();
            final int colCount = tableModel.getColumnCount();
            final HashMap colVsIndex = new HashMap();
            for (int cnt = 0; cnt < colCount; ++cnt) {
                colVsIndex.put(tableModel.getColumnName(cnt), new Integer(cnt));
            }
            int clientColumnIndex;
            final int serverColumnIndex = clientColumnIndex = colVsIndex.get(serverColumn);
            if (clientColumn != null) {
                clientColumnIndex = colVsIndex.get(clientColumn);
            }
            for (int i = 0; i < size; ++i) {
                buffer.append("<Option value='");
                buffer.append(IAMEncoder.encodeHTMLAttribute(tableModel.getValueAt(i, serverColumnIndex).toString()));
                buffer.append("'");
                if (value != null && value.equals(tableModel.getValueAt(i, serverColumnIndex).toString())) {
                    buffer.append(" selected ");
                }
                buffer.append(">");
                buffer.append(IAMEncoder.encodeHTML(tableModel.getValueAt(i, clientColumnIndex).toString()));
                buffer.append("</Option>");
            }
            buffer.append("</Select>");
            this.pageContext.getOut().println(buffer.toString());
        }
        catch (final Exception ex) {
            throw new JspException((Throwable)ex);
        }
        return 1;
    }
}
