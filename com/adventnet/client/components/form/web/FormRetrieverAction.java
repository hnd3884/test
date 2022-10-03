package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import java.util.Map;
import java.util.Enumeration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.List;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.util.LookUpUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.customview.service.ServiceConfiguration;
import com.adventnet.customview.service.SQTemplateValuesServiceConfiguration;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.ViewData;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import javax.swing.table.TableModel;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableRetrieverAction;

public class FormRetrieverAction extends TableRetrieverAction
{
    @Override
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        initializeReferences();
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        final HttpServletRequest request = viewCtx.getRequest();
        final HashMap criteriaMap = this.getCustomCriteria(tableViewDO, request);
        SelectQuery query = this.fetchAndCacheSelectQuery(viewCtx);
        query = (SelectQuery)query.clone();
        final Properties props = new Properties();
        final String type = (String)tableViewDO.getFirstValue("ACFormConfig", "FORMTYPE");
        if (type != null && "Create".equalsIgnoreCase(type)) {
            viewCtx.setViewModel((Object)props);
            return;
        }
        final ViewData viewData = this.getViewData(query, viewCtx, criteriaMap);
        final TableModel tableModel = (TableModel)viewData.getModel();
        if (tableModel.getRowCount() > 0) {
            for (int i = 0; i < tableModel.getColumnCount(); ++i) {
                final String columnName = tableModel.getColumnName(i);
                final Object value = tableModel.getValueAt(0, i);
                if (value != null) {
                    ((Hashtable<String, String>)props).put(columnName, value.toString());
                }
            }
        }
        viewCtx.setViewModel((Object)props);
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        SelectQuery query = (SelectQuery)viewCtx.getModel().getCompiledData((Object)"SELECTQUERY");
        if (query == null) {
            final Object cvId = tableViewDO.getFirstValue("ACFormConfig", "CVNAME");
            query = this.getSelectQuery(cvId, viewCtx);
            viewCtx.getModel().addCompiledData((Object)"SELECTQUERY", (Object)query);
        }
        return query;
    }
    
    @Override
    public ViewData getViewData(final SelectQuery query, final ViewContext viewCtx, final HashMap criteriaMap) throws Exception {
        ViewData viewData = null;
        final CustomViewRequest cvRequest = new CustomViewRequest(query);
        if (criteriaMap != null) {
            final SQTemplateValuesServiceConfiguration serConfig = new SQTemplateValuesServiceConfiguration(criteriaMap);
            cvRequest.putServiceConfiguration((ServiceConfiguration)serConfig);
        }
        viewData = FormRetrieverAction.cvMgr.getData(cvRequest);
        return viewData;
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        initializeReferences();
        final String create = request.getParameter("Create");
        DataObject dataObject = null;
        if (create == null) {
            dataObject = this.updateDataObject(viewCtx, request);
        }
        else {
            dataObject = this.createDataObject(viewCtx, request);
        }
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        final SelectQuery query = this.fetchAndCacheSelectQuery(viewCtx);
        final List columnsList = query.getSelectColumns();
        try {
            LookUpUtil.getPersistence().update(dataObject);
            String message = (String)tableViewDO.getFirstValue("ACFormConfig", "SUCCESSMESSAGE");
            message = this.formatMessage(message, columnsList, request);
            request.setAttribute("FORM_ACTION_RESULT", (Object)message);
        }
        catch (final Exception e) {
            e.printStackTrace();
            String message2 = (String)tableViewDO.getFirstValue("ACFormConfig", "FAILUREMESSAGE");
            message2 = this.formatMessage(message2, columnsList, request);
            request.setAttribute("FORM_ACTION_RESULT", (Object)message2);
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    public DataObject createDataObject(final ViewContext viewCtx, final HttpServletRequest request) throws Exception {
        final SelectQuery query = this.fetchAndCacheSelectQuery(viewCtx);
        QueryUtil.setDataType((Query)query);
        final HashMap aliasVsName = new HashMap();
        final List tableList = query.getTableList();
        final ArrayList tableNames = new ArrayList();
        for (int count = 0; count < tableList.size(); ++count) {
            final Table table = tableList.get(count);
            final String tableName = table.getTableName();
            aliasVsName.put(table.getTableAlias(), tableName);
            tableNames.add(tableName);
        }
        final Properties rowCollection = new Properties();
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final List columnsList = query.getSelectColumns();
        for (int count2 = 0; count2 < columnsList.size(); ++count2) {
            final Column column = columnsList.get(count2);
            final String columnAlias = column.getColumnAlias();
            if (request.getParameter(columnAlias) != null) {
                final String tableName2 = aliasVsName.get(column.getTableAlias());
                Row currentRow = ((Hashtable<K, Row>)rowCollection).get(tableName2);
                if (currentRow == null) {
                    currentRow = new Row(tableName2);
                    final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName2);
                    final List list = tableDefinition.getForeignKeyList();
                    if (list != null) {
                        for (int len = list.size(), i = 0; i < len; ++i) {
                            final ForeignKeyDefinition fkDefinition = list.get(i);
                            final String masterName = fkDefinition.getMasterTableName();
                            if (tableNames.contains(masterName)) {
                                final ColumnDefinition columnDefinition = fkDefinition.getForeignKeyColumns().get(0).getReferencedColumnDefinition();
                                final ColumnDefinition localColumnDefinition = fkDefinition.getForeignKeyColumns().get(0).getLocalColumnDefinition();
                                final String referencedColumnName = columnDefinition.getColumnName();
                                final String localColumnName = localColumnDefinition.getColumnName();
                                Row masterRow = ((Hashtable<K, Row>)rowCollection).get(masterName);
                                if (masterRow == null) {
                                    masterRow = new Row(masterName);
                                    ((Hashtable<String, Row>)rowCollection).put(masterName, masterRow);
                                }
                                if (rowCollection != null) {
                                    final Object referencedValue = masterRow.get(referencedColumnName);
                                    currentRow.set(localColumnName, referencedValue);
                                }
                            }
                        }
                    }
                    ((Hashtable<String, Row>)rowCollection).put(tableName2, currentRow);
                }
                currentRow.set(column.getColumnName(), this.convertDataToType(request.getParameter(columnAlias), column.getType()));
            }
        }
        final Enumeration en = rowCollection.propertyNames();
        while (en.hasMoreElements()) {
            final String currentTableName = en.nextElement();
            if (tableNames.contains(currentTableName)) {
                final Row currentRow2 = ((Hashtable<K, Row>)rowCollection).get(currentTableName);
                dataObject.addRow(currentRow2);
            }
        }
        return dataObject;
    }
    
    public DataObject updateDataObject(final ViewContext viewCtx, final HttpServletRequest request) throws Exception {
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        final HashMap criteriaMap = this.getCustomCriteria(tableViewDO, request);
        SelectQuery query = this.fetchAndCacheSelectQuery(viewCtx);
        query = (SelectQuery)query.clone();
        final SelectQuery originalQuery = (SelectQuery)query.clone();
        QueryUtil.setDataType((Query)originalQuery);
        if (criteriaMap != null && criteriaMap.size() > 0) {
            final Criteria criteria = QueryUtil.getTemplateReplacedCriteria(query.getCriteria(), (Map)criteriaMap);
            query.setCriteria(criteria);
        }
        final List list = query.getSelectColumns();
        final List tableList = query.getTableList();
        final HashMap aliasVsName = new HashMap();
        for (int count = 0; count < list.size(); ++count) {
            query.removeSelectColumn((Column)list.get(count));
        }
        for (int count = 0; count < tableList.size(); ++count) {
            final Table table = tableList.get(count);
            final String tableName = table.getTableName();
            aliasVsName.put(table.getTableAlias(), tableName);
            query.addSelectColumn(new Column(tableName, "*"));
        }
        final DataObject dataObject = LookUpUtil.getPersistence().get(query);
        final List columnsList = originalQuery.getSelectColumns();
        for (int count2 = 0; count2 < columnsList.size(); ++count2) {
            final Column column = columnsList.get(count2);
            final String columnAlias = column.getColumnAlias();
            if (request.getParameter(columnAlias) != null) {
                final String tableName2 = aliasVsName.get(column.getTableAlias());
                final Row row = dataObject.getFirstRow(tableName2);
                row.set(column.getColumnName(), this.convertDataToType(request.getParameter(columnAlias), column.getType()));
                dataObject.updateRow(row);
            }
        }
        return dataObject;
    }
    
    public Object convertDataToType(final String value, final int type) {
        if (type == 12) {
            return value;
        }
        if (type == -5) {
            return new Long(value);
        }
        if (type == 16) {
            return new Boolean(value);
        }
        if (type == 4) {
            return new Integer(value);
        }
        if (type == 6) {
            return new Float(value);
        }
        if (type == 8) {
            return new Double(value);
        }
        return value;
    }
    
    private String formatMessage(String message, final List columnsList, final HttpServletRequest request) {
        for (int count = 0; count < columnsList.size(); ++count) {
            final Column column = columnsList.get(count);
            final String columnAlias = column.getColumnAlias();
            if (message.indexOf("${" + columnAlias + "}") >= 0) {
                message = message.replaceAll("\\$\\{" + columnAlias + "\\}", request.getParameter(columnAlias));
            }
        }
        return message;
    }
}
