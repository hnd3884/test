package com.me.devicemanagement.framework.server.search;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AdvSearchSuggestionQuery implements SuggestQueryIfc
{
    private static Logger advSearchErrorLogger;
    
    @Override
    public List getSuggestData(String searchString, final String searchParamId) {
        final List dataList = new ArrayList();
        String viewName = "";
        String tableName = "";
        String columnName = "";
        DataObject dObj = null;
        DataObject dataObject = (DataObject)new WritableDataObject();
        Properties dataProperty = null;
        int paramType = -1;
        try {
            dObj = this.getSuggestionDO(searchString, searchParamId);
            if (!dObj.isEmpty()) {
                Row row = dObj.getFirstRow("SearchCriteria");
                viewName = (String)row.get("VIEW_NAME");
                tableName = (String)row.get("TABLE_NAME");
                columnName = (String)row.get("COLUMN_NAME");
                row = dObj.getFirstRow("SearchParams");
                paramType = (int)row.get("PARAM_TYPE");
            }
            if (viewName != null && tableName != null && columnName != null) {
                tableName = tableName.trim();
                columnName = columnName.trim();
                searchString += "*";
                SelectQuery suggestionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
                suggestionQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                final Criteria suggestCri = new Criteria(Column.getColumn(tableName, columnName), (Object)searchString, 2, false);
                suggestionQuery.setCriteria(suggestCri);
                final List groupList = new ArrayList();
                suggestionQuery = this.addSuggestionCriteria(suggestionQuery, Long.valueOf(searchParamId), groupList, paramType);
                suggestionQuery.setDistinct(true);
                dataObject = SyMUtil.getPersistence().get(suggestionQuery);
            }
            final Iterator itr = dataObject.getRows(tableName);
            int i = 0;
            while (itr.hasNext()) {
                final Row row2 = itr.next();
                dataProperty = new Properties();
                final String dataValue = (String)row2.get(columnName);
                ((Hashtable<String, String>)dataProperty).put("dataValue", dataValue);
                ((Hashtable<String, Integer>)dataProperty).put("dataId", ++i);
                dataList.add(dataProperty);
            }
        }
        catch (final Exception ex) {
            AdvSearchSuggestionQuery.advSearchErrorLogger.log(Level.WARNING, "AdvSearchSuggestionQuery : Exception occurred - getSuggestData() :  ", ex);
        }
        return dataList;
    }
    
    @Override
    public List getSuggestDataAPI(String searchString, final String searchParamId) {
        final List dataList = new ArrayList();
        String viewName = "";
        String tableName = "";
        String columnName = "";
        DataObject dObj = null;
        DataObject dataObject = (DataObject)new WritableDataObject();
        int paramType = -1;
        try {
            dObj = this.getSuggestionDO(searchString, searchParamId);
            if (!dObj.isEmpty()) {
                Row row = dObj.getFirstRow("SearchCriteria");
                viewName = (String)row.get("VIEW_NAME");
                tableName = (String)row.get("TABLE_NAME");
                columnName = (String)row.get("COLUMN_NAME");
                row = dObj.getFirstRow("SearchParams");
                paramType = (int)row.get("PARAM_TYPE");
            }
            if (viewName != null && tableName != null && columnName != null) {
                tableName = tableName.trim();
                columnName = columnName.trim();
                searchString += "*";
                SelectQuery suggestionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
                suggestionQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                final Criteria suggestCri = new Criteria(Column.getColumn(tableName, columnName), (Object)searchString, 2, false);
                suggestionQuery.setCriteria(suggestCri);
                final List groupList = new ArrayList();
                suggestionQuery = this.addSuggestionCriteria(suggestionQuery, Long.valueOf(searchParamId), groupList, paramType);
                suggestionQuery.setDistinct(true);
                dataObject = SyMUtil.getPersistence().get(suggestionQuery);
            }
            final Iterator itr = dataObject.getRows(tableName);
            while (itr.hasNext()) {
                final Row row2 = itr.next();
                final String dataValue = (String)row2.get(columnName);
                if (!dataList.contains(dataValue)) {
                    dataList.add(dataValue);
                }
                if (dataList.size() >= 10) {
                    break;
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchSuggestionQuery.advSearchErrorLogger.log(Level.WARNING, "AdvSearchSuggestionQuery : Exception occurred - getSuggestData() :  ", ex);
        }
        return dataList;
    }
    
    public SelectQuery addSuggestionCriteria(final SelectQuery suggestionQuery, final Long searchParamId, final List groupList, final int paramType) {
        try {
            final Criteria criparam = new Criteria(Column.getColumn("SearchParams", "PARAM_ID"), (Object)searchParamId, 0);
            final DataObject dObj = SyMUtil.getPersistence().get("SearchParams", criparam);
            final String paramName = (String)dObj.getValue("SearchParams", "PARAM_NAME", criparam);
            ApiFactoryProvider.getSearchSuggestion().addSuggestionCriteria(suggestionQuery, paramName, paramType);
        }
        catch (final Exception ex) {
            AdvSearchSuggestionQuery.advSearchErrorLogger.log(Level.WARNING, "AdvSearchSuggestionQuery : Exception occurred - addSuggestionCriteria() :  ", ex);
        }
        return suggestionQuery;
    }
    
    private DataObject getSuggestionDO(final String searchString, final String searchParamId) throws Exception {
        DataObject dObj = null;
        final Criteria criSearch = new Criteria(Column.getColumn("SearchCriteria", "PARAM_ID"), (Object)searchParamId, 0);
        final boolean isComputerorDevice = AdvSearchUtil.getInstance().getSearchProductSpecificHandler().isComputerOrDevice(Long.valueOf(searchParamId));
        if (isComputerorDevice) {
            dObj = AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getCompDeviceDetailParam(Long.valueOf(searchParamId));
        }
        else {
            final SelectQuery searchQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchCriteria"));
            searchQuery.addSelectColumn(Column.getColumn("SearchCriteria", "CRITERIA_ID"));
            searchQuery.addSelectColumn(Column.getColumn("SearchCriteria", "VIEW_NAME"));
            searchQuery.addSelectColumn(Column.getColumn("SearchCriteria", "TABLE_NAME"));
            searchQuery.addSelectColumn(Column.getColumn("SearchCriteria", "COLUMN_NAME"));
            searchQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            searchQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_TYPE"));
            searchQuery.setCriteria(criSearch);
            final Join joinCri = new Join("SearchCriteria", "SearchParams", new String[] { "PARAM_ID" }, new String[] { "PARAM_ID" }, 2);
            searchQuery.addJoin(joinCri);
            dObj = SyMUtil.getPersistence().get(searchQuery);
        }
        return dObj;
    }
    
    static {
        AdvSearchSuggestionQuery.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
