package com.me.devicemanagement.framework.server.dcViewFilter;

import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import org.json.JSONException;
import org.json.JSONArray;
import com.adventnet.client.view.web.WebViewAPI;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONObject;
import java.util.Map;
import java.util.logging.Level;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.server.customreport.CRConstantValues;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import java.util.List;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import com.adventnet.client.view.web.ViewContext;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.logging.Logger;

public class DCViewFilterUtil
{
    private Logger viewFilterLogger;
    protected static DCViewFilterUtil dcViewFilterUtil;
    private static DCEventLogUtil eventLogUtil;
    
    public DCViewFilterUtil() {
        this.viewFilterLogger = Logger.getLogger(this.getClass().getName());
    }
    
    public static DCViewFilterUtil getInstance() {
        if (DCViewFilterUtil.dcViewFilterUtil == null) {
            DCViewFilterUtil.dcViewFilterUtil = new DCViewFilterUtil();
        }
        return DCViewFilterUtil.dcViewFilterUtil;
    }
    
    public void initialiseFilterComponents(final HttpServletRequest request) {
        String isSchedule = (String)request.getAttribute("isSchedule");
        isSchedule = ((isSchedule != null) ? "true" : "false");
        this.getCriteriaList(request, isSchedule);
        final String viewIdStr = (String)request.getAttribute("reportId");
        final String pageIdStr = (String)request.getAttribute("pageId");
        this.viewFilterLogger.fine("DCViewFilter Initialised for view:" + viewIdStr + "of Page :" + pageIdStr);
        final Long viewId = Long.valueOf(viewIdStr);
        final Long pageId = Long.valueOf(pageIdStr);
        final Long loginId = SYMClientUtil.getLoginId(request);
        request.setAttribute("savedFilterList", (Object)this.getSavedViewFilterList(viewId, pageId, loginId));
    }
    
    public void deleteDCViewFilterCriteriaDetails(final Long filterId, final Boolean deleteFilter, final String loginName) throws DataAccessException {
        this.viewFilterLogger.info("Inside deleteCriteriaColumnDetails to delete Criteria for Filter:" + filterId);
        Object remarksArgs = null;
        final ArrayList criteriaColumnIdList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterCriteria"));
        selectQuery.addJoin(new Join("DCViewFilterCriteria", "DCViewFilterDetails", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria filterColumnRelCrit = new Criteria(new Column("DCViewFilterCriteria", "FILTER_ID"), (Object)filterId, 0);
        selectQuery.setCriteria(filterColumnRelCrit);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row filterNameRow = dataObject.getFirstRow("DCViewFilterDetails");
                final String filterName = (String)filterNameRow.get("FILTER_NAME");
                final Iterator criteriaColumnItr = dataObject.getRows("DCViewFilterCriteria");
                while (criteriaColumnItr.hasNext()) {
                    final Row row = criteriaColumnItr.next();
                    final Long criteriaColumnId = (Long)row.get("CRITERIA_COLUMN_ID");
                    criteriaColumnIdList.add(criteriaColumnId);
                }
                final Criteria criteriaColumnCrit = new Criteria(new Column("CriteriaColumnDetails", "CRITERIA_COLUMN_ID"), (Object)criteriaColumnIdList.toArray(), 8);
                SyMUtil.getPersistence().delete(criteriaColumnCrit);
                if (deleteFilter) {
                    final Criteria criteria = new Criteria(new Column("DCViewFilterDetails", "FILTER_ID"), (Object)filterId, 0);
                    SyMUtil.getPersistence().delete(criteria);
                    remarksArgs = filterName + "@@@" + I18N.getMsg("desktopcentral.webclient.tools.deleted", new Object[0]);
                    DCViewFilterUtil.eventLogUtil.addEvent(6002, loginName, null, "dc.common.viewFilter.Filter_log_success_message", remarksArgs, true);
                }
            }
            this.viewFilterLogger.info("Deleted Criteria Details for Filter:" + filterId);
        }
        catch (final Exception ex) {
            try {
                if (deleteFilter) {
                    DCViewFilterUtil.eventLogUtil.addEvent(6003, loginName, null, "dc.common.viewFilter.Filter_log_failure_message", remarksArgs, true);
                }
            }
            catch (final Exception ex2) {
                this.viewFilterLogger.severe("Exception occurred while deleting Filter:" + filterId + ex.getLocalizedMessage());
            }
            this.viewFilterLogger.severe("Exception occurred while deleting Filter:" + filterId + ex.getLocalizedMessage());
            if (ex instanceof DataAccessException) {
                final DataAccessException exc = (DataAccessException)ex;
                throw exc;
            }
        }
    }
    
    public SelectQuery checkAndAddDCViewFilterCriteria(SelectQuery selectQuery, final ViewContext viewContext) {
        this.viewFilterLogger.fine("Inside checkAndAddDCViewFilterCriteria");
        final HttpServletRequest request = viewContext.getRequest();
        String criteriaJSONString = request.getParameter("criteriaJSON");
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        criteriaJSONString = (String)((criteriaJSONString == null && isDCViewFilterReset == null) ? request.getAttribute("criteriaJSON") : criteriaJSONString);
        criteriaJSONString = (String)((criteriaJSONString == null && isDCViewFilterReset == null) ? viewContext.getStateParameter("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString != null) {
            try {
                final DCViewFilter dcViewFilter = DCViewFilter.dcViewFilterMapper(criteriaJSONString);
                selectQuery = this.getDCViewFilterCriteria(selectQuery, dcViewFilter.getDcViewFilterCriteriaList());
            }
            catch (final Exception ex) {
                this.viewFilterLogger.severe("Exception while checking and Adding DCViewFilterCriteria" + ex);
            }
        }
        return selectQuery;
    }
    
    public SelectQuery getDCViewFilterCriteria(final SelectQuery selectQuery, final List<DCViewFilterCriteria> dcViewFilterCriteriaList) {
        this.viewFilterLogger.fine("Inside getDCViewFilterCriteria to construct Criteria for the JSON:" + dcViewFilterCriteriaList);
        final ArrayList tableList = (ArrayList)selectQuery.getTableList();
        final ArrayList tableNameList = this.fetchTableNamesFromList(tableList);
        Criteria viewFilterCriteria = null;
        Criteria baseCriteria = selectQuery.getCriteria();
        try {
            for (int i = 0; i < dcViewFilterCriteriaList.size(); ++i) {
                final DCViewFilterCriteria dcViewFilterCriteria = dcViewFilterCriteriaList.get(i);
                String searchValue1 = null;
                String searchValue2 = null;
                final Long columnID = dcViewFilterCriteria.getColumnID();
                final String logicalOperator = dcViewFilterCriteria.getLogicalOperator();
                final String comparator = dcViewFilterCriteria.getComparator();
                final String customComparator = dcViewFilterCriteria.getCustomComparator();
                final List value = dcViewFilterCriteria.getSearchValue();
                if (value != null && !value.isEmpty()) {
                    searchValue1 = value.get(0);
                    if (dcViewFilterCriteria.getComparator().equalsIgnoreCase("between")) {
                        searchValue2 = value.get(1);
                    }
                    else {
                        searchValue1 = (String)value.stream().collect(Collectors.joining("$@$"));
                    }
                }
                if (columnID != null) {
                    final SelectQuery columnDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
                    columnDetailsSelect.addSelectColumn(new Column((String)null, "*"));
                    final Criteria columnDetailsCrit = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)columnID, 0);
                    columnDetailsSelect.setCriteria(columnDetailsCrit);
                    final DataObject columnDetailsDO = SyMUtil.getPersistence().get(columnDetailsSelect);
                    if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                        final Row columnRow = columnDetailsDO.getFirstRow("CRColumns");
                        final String tableName = (String)columnRow.get("TABLE_NAME_ALIAS");
                        final String columnName = (String)columnRow.get("COLUMN_NAME_ALIAS");
                        final String dataType = (String)columnRow.get("DATA_TYPE");
                        final Long subModuleId = (Long)columnRow.get("SUB_MODULE_ID");
                        final Criteria subModuleCriteria = new Criteria(Column.getColumn("CRJoinRelation", "SUB_MODULE_ID"), (Object)subModuleId, 0);
                        final DataObject joinDO = this.getJoinDO(subModuleCriteria);
                        final DataObject joinCriteriaDO = this.getjoinCriteriaDO(subModuleCriteria);
                        final ArrayList joinList = new ArrayList();
                        this.checkAndAddJoin(selectQuery, tableNameList, joinList, tableName, joinDO, joinCriteriaDO);
                        final String handlerClass = this.getDCViewFilterHandler(columnID);
                        Criteria criteria;
                        if (handlerClass != null) {
                            final DCViewFilterHandler dcViewFilterHandler = (DCViewFilterHandler)Class.forName(handlerClass).newInstance();
                            criteria = dcViewFilterHandler.getDCViewFilterCriteria(dcViewFilterCriteria);
                        }
                        else {
                            criteria = this.getDCViewFilterCriteria(dataType, tableName, columnName, comparator, customComparator, searchValue1, searchValue2);
                        }
                        if (viewFilterCriteria == null && criteria != null) {
                            viewFilterCriteria = criteria;
                        }
                        else if (criteria != null) {
                            viewFilterCriteria = (logicalOperator.equalsIgnoreCase("AND") ? viewFilterCriteria.and(criteria) : viewFilterCriteria.or(criteria));
                        }
                    }
                }
            }
            this.viewFilterLogger.fine("Constructed Criteria is:" + viewFilterCriteria);
        }
        catch (final Exception e) {
            this.viewFilterLogger.severe("Exception while Constructing Criteria:" + e.getLocalizedMessage());
        }
        if (baseCriteria != null && viewFilterCriteria != null) {
            baseCriteria = baseCriteria.and(viewFilterCriteria);
            selectQuery.setCriteria(baseCriteria);
        }
        else if (viewFilterCriteria != null) {
            selectQuery.setCriteria(viewFilterCriteria);
        }
        return selectQuery;
    }
    
    public Object alterSearchvalue(Object searchString, final String operatorValue) {
        if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres") && (operatorValue.equalsIgnoreCase("like") || operatorValue.equalsIgnoreCase("not like") || operatorValue.equalsIgnoreCase("starts with") || operatorValue.equalsIgnoreCase("ends with") || operatorValue.equalsIgnoreCase("contains"))) {
            searchString = String.valueOf(searchString).replaceAll("(?<![\\\\])[\\\\](?![\\\\\\/\\}])", "\\\\\\\\");
        }
        return searchString;
    }
    
    public DCViewFilterCriteria alterSearchValue(final DCViewFilterCriteria dcViewFilterCriteria) {
        final String operatorValue = dcViewFilterCriteria.getComparator();
        final List<String> searchvalue = dcViewFilterCriteria.getSearchValue();
        final List<String> modifiedSearchValue = new ArrayList<String>();
        if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            for (String searchString : searchvalue) {
                searchString = (String)this.alterSearchvalue(searchString, operatorValue);
                modifiedSearchValue.add(searchString);
            }
            dcViewFilterCriteria.setSearchValue((List)modifiedSearchValue);
        }
        return dcViewFilterCriteria;
    }
    
    public Criteria getEqualNotEqualCriteria(final String dataType, final String tableName, final String columnName, String comparator, String value, final String searchValue2) {
        final String[] valueArray = value.split(Pattern.quote("$@$"));
        Criteria criteria;
        if (valueArray.length > 1) {
            comparator = ((comparator.equalsIgnoreCase("equal") || comparator.equalsIgnoreCase("not equal")) ? (comparator.equalsIgnoreCase("equal") ? "in" : "not in") : comparator);
            criteria = new Criteria(new Column(tableName, columnName), (Object)valueArray, CRConstantValues.getOperatorValue(comparator), false);
        }
        else {
            if (value.equalsIgnoreCase("null")) {
                value = (dataType.equalsIgnoreCase("CHAR") ? null : "0");
            }
            final Object objectValue = (dataType.equalsIgnoreCase("BIGINT") || dataType.equalsIgnoreCase("INT")) ? Long.valueOf(dataType.equalsIgnoreCase("BIGINT") ? Long.valueOf(value) : ((long)(int)Integer.valueOf(value))) : value;
            criteria = new Criteria(new Column(tableName, columnName), objectValue, CRConstantValues.getOperatorValue(comparator), false);
        }
        return criteria;
    }
    
    public Criteria getDCViewFilterCriteria(String dataType, final String tableName, final String columnName, final String comparator, final String customComparator, String value, final String searchValue2) {
        Criteria criteria = null;
        try {
            final String upperCase;
            dataType = (upperCase = dataType.toUpperCase());
            switch (upperCase) {
                case "DATE_RESTRICTED":
                case "DATE": {
                    criteria = this.getDateSearchString(columnName, tableName, comparator, customComparator, value, searchValue2);
                    break;
                }
                case "DATETIME": {
                    criteria = this.getDateTimeSearchString(columnName, tableName, comparator, customComparator, value, searchValue2);
                    break;
                }
                case "BOOLEAN": {
                    final Object searchObj = value.equalsIgnoreCase("true") ? new String[] { "true", "1" } : new String[] { "false", "0" };
                    criteria = new Criteria(new Column(tableName, columnName), searchObj, CRConstantValues.getOperatorValue("in"), false);
                    break;
                }
                case "BIGINT":
                case "INT": {
                    criteria = this.getEqualNotEqualCriteria(dataType, tableName, columnName, comparator, value, searchValue2);
                    break;
                }
                default: {
                    if (comparator.equalsIgnoreCase("empty") || comparator.equalsIgnoreCase("not empty")) {
                        final int operatorValue = CRConstantValues.getOperatorValue(comparator);
                        final Criteria emptyCriteria = new Criteria(new Column(tableName, columnName), (Object)"", operatorValue, false);
                        final Criteria nullCriteria = new Criteria(new Column(tableName, columnName), (Object)null, operatorValue, false);
                        final Criteria defaultEmptyCriteria = new Criteria(new Column(tableName, columnName), (Object)"--", operatorValue, false);
                        criteria = emptyCriteria;
                        criteria = (comparator.equalsIgnoreCase("empty") ? criteria.or(nullCriteria).or(defaultEmptyCriteria) : criteria.and(nullCriteria).and(defaultEmptyCriteria));
                        break;
                    }
                    if (!comparator.equalsIgnoreCase("equal") && !comparator.equalsIgnoreCase("not equal")) {
                        value = (String)CRConstantValues.getSearchString(value, comparator);
                        value = (String)this.alterSearchvalue(value, comparator);
                        criteria = new Criteria(new Column(tableName, columnName), (Object)value, CRConstantValues.getOperatorValue(comparator), false);
                        break;
                    }
                    criteria = this.getEqualNotEqualCriteria(dataType, tableName, columnName, comparator, value, searchValue2);
                    break;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return criteria;
    }
    
    public Criteria getDateTimeSearchString(final String columnName, final String tableName, final String comparator, final String customComparator, final String value, final String searchValue2) {
        this.viewFilterLogger.fine("Inside getDateSearchString to construct Criteria for:" + columnName + " Column of " + tableName + " table" + "Comparator:" + comparator + "Search Value 2:" + searchValue2);
        final String beforeSeconds = ":59";
        final String afterSeconds = ":00";
        Criteria criteria = null;
        final String dateFormat = "MM/dd/yyyy hh:mm:ss";
        if (comparator.equalsIgnoreCase("is")) {
            if (customComparator.equalsIgnoreCase("isDate")) {
                final Long startTime = DateTimeUtil.dateInLonginUserTimeZone(searchValue2 + afterSeconds, dateFormat);
                final Long endTime = DateTimeUtil.dateInLonginUserTimeZone(searchValue2 + beforeSeconds, dateFormat);
                criteria = new Criteria(new Column(tableName, columnName), (Object)startTime, 4);
                criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime, 6));
            }
            else {
                try {
                    final String[] valueArray = value.split(Pattern.quote("$@$"));
                    if (valueArray.length > 1) {
                        Criteria multipleCriteria = null;
                        for (int i = 0; i < valueArray.length; ++i) {
                            final Criteria individualCrit = this.getDateTimeSearchString(columnName, tableName, comparator, customComparator, valueArray[i], searchValue2);
                            multipleCriteria = ((multipleCriteria != null) ? multipleCriteria.or(individualCrit) : individualCrit);
                        }
                        criteria = multipleCriteria;
                    }
                    else {
                        final Hashtable milliSecRange = Utils.getStartAndEndDates(value, true);
                        if (milliSecRange != null) {
                            final Long startTime2 = milliSecRange.get("startDate");
                            final Long endTime2 = milliSecRange.get("endDate");
                            criteria = new Criteria(new Column(tableName, columnName), (Object)startTime2, 4);
                            criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime2, 6));
                        }
                    }
                }
                catch (final Exception e) {
                    this.viewFilterLogger.severe("Exception while Constructing Criteria for Date:" + e.getLocalizedMessage());
                }
            }
        }
        else if (comparator.equalsIgnoreCase("before")) {
            final Long beforeTime = DateTimeUtil.dateInLonginUserTimeZone(value + beforeSeconds, dateFormat);
            criteria = new Criteria(new Column(tableName, columnName), (Object)beforeTime, 6);
        }
        else if (comparator.equalsIgnoreCase("after")) {
            final Long afterTime = DateTimeUtil.dateInLonginUserTimeZone(value + afterSeconds, dateFormat);
            criteria = new Criteria(new Column(tableName, columnName), (Object)afterTime, 4);
        }
        else if (comparator.equalsIgnoreCase("between")) {
            final Criteria beforeCriteria = this.getDateTimeSearchString(columnName, tableName, "before", customComparator, searchValue2, null);
            final Criteria afterCriteria = this.getDateTimeSearchString(columnName, tableName, "after", customComparator, value, null);
            criteria = beforeCriteria.and(afterCriteria);
        }
        else if (comparator.equalsIgnoreCase("last_n_days") && value != "") {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
            final Long startTime2 = dateRange.get("fromDate");
            final Long endTime2 = dateRange.get("toDate");
            criteria = new Criteria(new Column(tableName, columnName), (Object)startTime2, 4);
            criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime2, 6));
        }
        this.viewFilterLogger.fine("Criteria For Date Column is:" + criteria);
        return criteria;
    }
    
    public Criteria getDateSearchString(final String columnName, final String tableName, final String comparator, final String customComparator, final String value, final String searchValue2) {
        this.viewFilterLogger.fine("Inside getDateSearchString to construct Criteria for:" + columnName + " Column of " + tableName + " table" + "Comparator:" + comparator + "Search Value 2:" + searchValue2);
        Criteria criteria = null;
        final String format = "MM/dd/yyyy hh:mm:ss";
        if (comparator.equalsIgnoreCase("is")) {
            if (customComparator.equalsIgnoreCase("isDate")) {
                final Long startTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 00:00:00", format);
                final Long endTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 23:59:59", format);
                criteria = new Criteria(new Column(tableName, columnName), (Object)startTime, 4);
                criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime, 6));
            }
            else {
                try {
                    final String[] valueArray = value.split(Pattern.quote("$@$"));
                    if (valueArray.length > 1) {
                        Criteria multipleCriteria = null;
                        for (int i = 0; i < valueArray.length; ++i) {
                            final Criteria individualCrit = this.getDateSearchString(columnName, tableName, comparator, customComparator, valueArray[i], searchValue2);
                            multipleCriteria = ((multipleCriteria != null) ? multipleCriteria.or(individualCrit) : individualCrit);
                        }
                        criteria = multipleCriteria;
                    }
                    else {
                        final Hashtable milliSecRange = Utils.getStartAndEndDates(value, true);
                        if (milliSecRange != null) {
                            final Long startTime2 = milliSecRange.get("startDate");
                            final Long endTime2 = milliSecRange.get("endDate");
                            criteria = new Criteria(new Column(tableName, columnName), (Object)startTime2, 4);
                            criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime2, 6));
                        }
                    }
                }
                catch (final Exception e) {
                    this.viewFilterLogger.severe("Exception while Constructing Criteria for Date:" + e.getLocalizedMessage());
                }
            }
        }
        else if (comparator.equalsIgnoreCase("before")) {
            final Long beforeTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 23:59:59", format);
            criteria = new Criteria(new Column(tableName, columnName), (Object)beforeTime, 6);
        }
        else if (comparator.equalsIgnoreCase("after")) {
            final Long afterTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 00:00:00", format);
            criteria = new Criteria(new Column(tableName, columnName), (Object)afterTime, 4);
        }
        else if (comparator.equalsIgnoreCase("between")) {
            final Criteria beforeCriteria = this.getDateSearchString(columnName, tableName, "before", customComparator, searchValue2, null);
            final Criteria afterCriteria = this.getDateSearchString(columnName, tableName, "after", customComparator, value, null);
            criteria = beforeCriteria.and(afterCriteria);
        }
        else if (comparator.equalsIgnoreCase("last_n_days") || comparator.equalsIgnoreCase("Last n Days")) {
            if (value != "") {
                final int noOfDays = Integer.valueOf(value);
                final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
                final Long startTime2 = dateRange.get("fromDate");
                final Long endTime2 = dateRange.get("toDate");
                criteria = new Criteria(new Column(tableName, columnName), (Object)startTime2, 4);
                criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)endTime2, 6));
            }
        }
        else if (comparator.equalsIgnoreCase("Before n Days") || comparator.equalsIgnoreCase("before_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
            final Long fromDate = dateRange.get("fromDate");
            criteria = new Criteria(new Column(tableName, columnName), (Object)fromDate, 6);
        }
        else if (comparator.equalsIgnoreCase("Next N Days") || comparator.equalsIgnoreCase("next_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long fromDate = dateRange.get("fromDate");
            final Long toDate = dateRange.get("toDate");
            criteria = new Criteria(new Column(tableName, columnName), (Object)fromDate, 4);
            criteria = criteria.and(new Criteria(new Column(tableName, columnName), (Object)toDate, 6));
        }
        else if (comparator.equalsIgnoreCase("After n Days") || comparator.equalsIgnoreCase("after_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long to_date = dateRange.get("toDate");
            criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)to_date, 4);
        }
        this.viewFilterLogger.fine("Criteria For Date Column is:" + criteria);
        return criteria;
    }
    
    public SelectQuery checkAndAddJoin(final SelectQuery selectQuery, final ArrayList tableList, final ArrayList joinList, final String tableName, final DataObject joinDO, final DataObject joinCriteriaDO) {
        this.viewFilterLogger.fine("Inside checkAndAddJoin to construct Join for:" + tableName);
        if (!tableList.contains(tableName)) {
            try {
                Row joinRow = null;
                if (joinDO != null) {
                    final Criteria criteria = new Criteria(Column.getColumn("CRJoinRelation", "CHILD_TABLE_ALIAS"), (Object)tableName, 0);
                    joinRow = joinDO.getRow("CRJoinRelation", criteria);
                }
                String relationID = "";
                String childTableAlias = "";
                String parentTableAlias = "";
                String joinType = "";
                String childTableName = "";
                String parentTableName = "";
                if (joinRow != null) {
                    relationID = (joinRow.get("RELATION_ID") + "").trim();
                    childTableAlias = (joinRow.get("CHILD_TABLE_ALIAS") + "").trim();
                    parentTableAlias = (joinRow.get("PARENT_TABLE_ALIAS") + "").trim();
                    childTableName = (joinRow.get("CHILD_TABLE_NAME") + "").trim();
                    parentTableName = (joinRow.get("PARENT_TABLE_NAME") + "").trim();
                    joinType = (joinRow.get("JOIN_TYPE") + "").trim();
                }
                final Criteria criteria2 = new Criteria(Column.getColumn("CRJoinColumns", "RELATION_ID"), (Object)relationID, 0);
                final Iterator iterator = joinCriteriaDO.getRows("CRJoinColumns", criteria2);
                final Criteria c1 = this.createJoinCriteria(iterator, childTableAlias, parentTableAlias);
                final Join tempJoin = new Join(parentTableName, parentTableAlias, childTableName, childTableAlias, c1, CRConstantValues.getOperatorValue(joinType));
                joinList.add(tempJoin);
                if (tableList.contains(parentTableAlias)) {
                    this.addJoin(joinList, selectQuery, tableList);
                }
                else {
                    final Criteria tempCri = new Criteria(Column.getColumn("CRJoinRelation", "CHILD_TABLE_ALIAS"), (Object)parentTableAlias, 0);
                    final Row row = joinDO.getRow("CRJoinRelation", tempCri);
                    if (row != null) {
                        final String tempChildTableAlias = (joinRow.get("CHILD_TABLE_ALIAS") + "").trim();
                        final String tempParentTableAlias = (joinRow.get("PARENT_TABLE_ALIAS") + "").trim();
                        if (!tempChildTableAlias.equalsIgnoreCase(tempParentTableAlias) && !tableList.contains(tempParentTableAlias)) {
                            this.checkAndAddJoin(selectQuery, tableList, joinList, parentTableAlias, joinDO, joinCriteriaDO);
                        }
                        else {
                            this.addJoin(joinList, selectQuery, tableList);
                        }
                    }
                }
            }
            catch (final Exception e) {
                this.viewFilterLogger.log(Level.SEVERE, "DCViewFilterUtil :Exception from checkAndAddJoin:", e);
            }
        }
        return selectQuery;
    }
    
    private String createNativeJoinCriteria(final Iterator iteratorRows, final String childTableAlias, final String parentTableAlias) {
        this.viewFilterLogger.fine("Inside createNativeJoinCriteria to construct Criteria for Joining:" + childTableAlias + " with " + parentTableAlias);
        final StringBuilder criteria = new StringBuilder();
        int i = 0;
        while (iteratorRows.hasNext()) {
            final Row row = iteratorRows.next();
            if (row != null) {
                final String childColumnAlias = (row.get("CHILD_COLUMN_ALIAS") + "").trim();
                final String parentColumnAlias = (row.get("PARENT_COLUMN_ALIAS") + "").trim();
                final String joinOperator = (row.get("JOIN_OPERATOR") + "").trim();
                String comOpetator = (row.get("COMPISITE_OPERATOR") + "").trim();
                comOpetator = CRConstantValues.getLogicalOperator(comOpetator);
                if (i != 0) {
                    criteria.append(" ");
                    criteria.append(comOpetator);
                }
                criteria.append(" ");
                criteria.append(parentTableAlias);
                criteria.append(".");
                criteria.append(parentColumnAlias);
                if (joinOperator.equalsIgnoreCase("equal")) {
                    criteria.append("=");
                }
                else {
                    criteria.append("!=");
                }
                criteria.append(childTableAlias);
                criteria.append(".");
                criteria.append(childColumnAlias);
            }
            ++i;
        }
        this.viewFilterLogger.fine("Criteria set by createNativeJoinCriteria Joining:" + criteria.toString());
        return criteria.toString();
    }
    
    private Criteria createJoinCriteria(final Iterator iteratorRows, final String childTableAlias, final String parentTableAlias) {
        this.viewFilterLogger.fine("Inside createJoinCriteria to construct Criteria for Joining:" + childTableAlias + " with " + parentTableAlias);
        Criteria criteria = null;
        final List criteriaList = new ArrayList();
        final List orderList = new ArrayList();
        int i = 0;
        while (iteratorRows.hasNext()) {
            final Row row = iteratorRows.next();
            if (row != null) {
                final String childColumnAlias = (row.get("CHILD_COLUMN_ALIAS") + "").trim();
                final String parentColumnAlias = (row.get("PARENT_COLUMN_ALIAS") + "").trim();
                final String joinOperator = (row.get("JOIN_OPERATOR") + "").trim();
                boolean parentStaicValue = false;
                parentStaicValue = (boolean)row.get("IS_PARENT_STATIC_VALUE");
                String comOpetator = (row.get("COMPISITE_OPERATOR") + "").trim();
                final int joinOrder = Integer.parseInt((row.get("JOIN_ORDER") + "").trim());
                comOpetator = CRConstantValues.getLogicalOperator(comOpetator);
                if (parentStaicValue) {
                    final Criteria c1 = new Criteria(Column.getColumn(childTableAlias, childColumnAlias), (Object)parentColumnAlias, CRConstantValues.getOperatorValue(joinOperator));
                    criteriaList.add(joinOrder, c1);
                    orderList.add(joinOrder, comOpetator);
                }
                else {
                    final Criteria c1 = new Criteria(Column.getColumn(parentTableAlias, parentColumnAlias), (Object)Column.getColumn(childTableAlias, childColumnAlias), CRConstantValues.getOperatorValue(joinOperator));
                    criteriaList.add(joinOrder, c1);
                    orderList.add(joinOrder, comOpetator);
                }
            }
            ++i;
        }
        for (i = 0; i < criteriaList.size(); ++i) {
            final Criteria c2 = criteriaList.get(i);
            if (i > 0) {
                final String compOp = orderList.get(i);
                if (compOp.equalsIgnoreCase(CRConstantValues.getLogicalOperator(" AND "))) {
                    criteria = criteria.and(c2);
                }
                else if (compOp.equalsIgnoreCase(CRConstantValues.getLogicalOperator(" OR "))) {
                    criteria = criteria.or(c2);
                }
            }
            else {
                criteria = c2;
            }
        }
        return criteria;
    }
    
    private SelectQuery addJoin(final ArrayList joinList, final SelectQuery selectQuery, final ArrayList tableList) {
        this.viewFilterLogger.fine("Inside addJoin for addling list of joins");
        for (int i = joinList.size() - 1; i >= 0; --i) {
            final Join tempJoin = joinList.get(i);
            selectQuery.addJoin(tempJoin);
            final String childTableAlias = tempJoin.getReferencedTableAlias();
            final String baseTableAlias = tempJoin.getBaseTableAlias();
            if (!tableList.contains(childTableAlias)) {
                tableList.add(childTableAlias);
            }
            if (!tableList.contains(baseTableAlias)) {
                tableList.add(baseTableAlias);
            }
        }
        this.viewFilterLogger.fine("Successfully added the Joins");
        return selectQuery;
    }
    
    public Map getSavedViewFilterList(final Long viewId, final Long pageId, final Long loginId) {
        this.viewFilterLogger.fine("Inside getSavedViewFilterList to fetch saved Filters for view: " + viewId + " of Page: " + pageId + " for user:" + loginId);
        final Map savedFilters = new HashMap();
        final List filterList = new ArrayList();
        try {
            final DataObject filterListDo = this.getSavedFiltersDO(viewId, pageId, loginId);
            if (filterListDo != null && !filterListDo.isEmpty()) {
                final Iterator filterListItr = filterListDo.getRows("DCViewFilterDetails");
                while (filterListItr.hasNext()) {
                    final JSONObject filterDetails = new JSONObject();
                    final Row filterRows = filterListItr.next();
                    final Long filterId = (Long)filterRows.get("FILTER_ID");
                    final String filterName = (String)filterRows.get("FILTER_NAME");
                    filterDetails.put("filterId", (Object)(filterId + ""));
                    filterDetails.put("filterName", (Object)filterName);
                    filterList.add(filterDetails);
                }
            }
            savedFilters.put("SavedFilters", filterList);
            this.viewFilterLogger.fine("List of Saved Filters:" + savedFilters);
        }
        catch (final Exception e) {
            this.viewFilterLogger.severe("Exception while Fetching Saved Filter List:" + e.getLocalizedMessage());
        }
        return savedFilters;
    }
    
    public DataObject getSavedFiltersDO(final Long viewId, final Long pageId, final Long loginId) throws DataAccessException {
        final SelectQuery filterListSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterMapping"));
        final Join filterDetailsJoin = new Join("DCViewFilterMapping", "DCViewFilterDetails", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
        filterListSelect.addJoin(filterDetailsJoin);
        filterListSelect.addSelectColumn(new Column((String)null, "*"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("DCViewFilterDetails", "FILTER_NAME"), true);
        filterListSelect.addSortColumn(sortColumn);
        final Criteria pageCrit = new Criteria(new Column("DCViewFilterMapping", "PAGE_ID"), (Object)pageId, 0);
        final Criteria viewCrit = new Criteria(new Column("DCViewFilterMapping", "VIEW_ID"), (Object)viewId, 0);
        final Criteria userCrit = new Criteria(new Column("DCViewFilterDetails", "LOGIN_ID"), (Object)loginId, 0);
        final Criteria anonymousFilterCrit = new Criteria(new Column("DCViewFilterDetails", "IS_ANONYMOUS"), (Object)false, 0);
        Criteria filterSelectCrit = pageCrit;
        filterSelectCrit = filterSelectCrit.and(viewCrit);
        filterSelectCrit = filterSelectCrit.and(userCrit);
        filterSelectCrit = filterSelectCrit.and(anonymousFilterCrit);
        filterListSelect.setCriteria(filterSelectCrit);
        return SyMUtil.getPersistence().get(filterListSelect);
    }
    
    public void renameDCViewFilter(final Long filterID, final String filterName, final String loginName) throws DataAccessException {
        String oldFilterName = null;
        this.viewFilterLogger.info("Entered renameDCViewFilter to rename Filter" + filterID);
        Object remarksArgs = null;
        try {
            if (filterID != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterDetails"));
                selectQuery.addSelectColumn(new Column("DCViewFilterDetails", "FILTER_ID"));
                selectQuery.addSelectColumn(new Column("DCViewFilterDetails", "FILTER_NAME"));
                final Criteria criteria = new Criteria(new Column("DCViewFilterDetails", "FILTER_ID"), (Object)filterID, 0);
                final Criteria nameCriteria = new Criteria(new Column("DCViewFilterDetails", "FILTER_NAME"), (Object)filterName, 1, false);
                selectQuery.setCriteria(criteria.and(nameCriteria));
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Row dcViewFilterRow = dataObject.getFirstRow("DCViewFilterDetails");
                    oldFilterName = (String)dcViewFilterRow.get("FILTER_NAME");
                    dcViewFilterRow.set("FILTER_NAME", (Object)filterName);
                    dataObject.updateRow(dcViewFilterRow);
                    SyMUtil.getPersistence().update(dataObject);
                    remarksArgs = oldFilterName + "@@@" + filterName;
                    DCViewFilterUtil.eventLogUtil.addEvent(6004, loginName, null, "dc.common.viewFilter.Filter_rename_success_message", remarksArgs, true);
                    this.viewFilterLogger.info("Successfully renamed Filter to " + filterName);
                }
            }
        }
        catch (final DataAccessException ex) {
            DCViewFilterUtil.eventLogUtil.addEvent(6005, loginName, null, "dc.common.viewFilter.Filter_rename_failure_message", remarksArgs, true);
            this.viewFilterLogger.severe("Exception while renaming Filter" + ex.getLocalizedMessage());
            throw ex;
        }
    }
    
    public Map saveDCViewFilter(final DCViewFilter dcViewFilter, Long filterID, final String filterName, final Long pageID, final Long viewID, final Long loginID, final String loginName, final Boolean isAnonymous) throws DataAccessException {
        final Map filterMap = new HashMap();
        Object remarksArgs = null;
        if (dcViewFilter == null || filterName == null) {
            this.viewFilterLogger.warning("Criteria Details or FilterName is not available hence Filter Cannot be saved");
            return null;
        }
        final WritableDataObject writableDataObject = new WritableDataObject();
        try {
            UniqueValueHolder filterIdUVH = null;
            if (filterID != null) {
                this.deleteDCViewFilterCriteriaDetails(filterID, false, loginName);
                remarksArgs = filterName + "@@@" + I18N.getMsg("dc.common.MODIFIED", new Object[0]);
            }
            else {
                if (loginID != null) {
                    final Row filterDetailsRow = new Row("DCViewFilterDetails");
                    filterDetailsRow.set("FILTER_NAME", (Object)filterName);
                    filterDetailsRow.set("LOGIN_ID", (Object)loginID);
                    filterDetailsRow.set("IS_ANONYMOUS", (Object)isAnonymous);
                    writableDataObject.addRow(filterDetailsRow);
                    filterIdUVH = (UniqueValueHolder)filterDetailsRow.get("FILTER_ID");
                }
                final Row viewFilterMapping = new Row("DCViewFilterMapping");
                viewFilterMapping.set("FILTER_ID", (Object)filterIdUVH);
                viewFilterMapping.set("PAGE_ID", (Object)pageID);
                viewFilterMapping.set("VIEW_ID", (Object)viewID);
                writableDataObject.addRow(viewFilterMapping);
                remarksArgs = filterName + "@@@" + I18N.getMsg("dc.rep.PowerReportAction.saved", new Object[0]);
            }
            if (filterID != null || filterIdUVH != null) {
                final List<DCViewFilterCriteria> dcViewFilterCriteriaList = dcViewFilter.getDcViewFilterCriteriaList();
                for (int i = 0; i < dcViewFilterCriteriaList.size(); ++i) {
                    final DCViewFilterCriteria dcViewFilterCriteria = dcViewFilterCriteriaList.get(i);
                    final Row criteriaColumnDetails = this.saveCriteriaColumnDetailRow(dcViewFilterCriteria, i);
                    writableDataObject.addRow(criteriaColumnDetails);
                    final UniqueValueHolder criteriaColumnId = (UniqueValueHolder)criteriaColumnDetails.get("CRITERIA_COLUMN_ID");
                    final Row viewFilterCriteriaMapping = new Row("DCViewFilterCriteria");
                    viewFilterCriteriaMapping.set("CRITERIA_COLUMN_ID", (Object)criteriaColumnId);
                    if (filterID == null) {
                        viewFilterCriteriaMapping.set("FILTER_ID", (Object)filterIdUVH);
                    }
                    else {
                        viewFilterCriteriaMapping.set("FILTER_ID", (Object)filterID);
                    }
                    writableDataObject.addRow(viewFilterCriteriaMapping);
                    SyMUtil.getPersistence().update((DataObject)writableDataObject);
                }
            }
            filterID = ((filterID != null) ? filterID : Long.valueOf(filterIdUVH.getValue().toString()));
            filterMap.put("filterID", filterID);
            filterMap.put("filterName", filterName);
            if (!isAnonymous) {
                DCViewFilterUtil.eventLogUtil.addEvent(6000, loginName, null, "dc.common.viewFilter.Filter_log_success_message", remarksArgs, true);
            }
            this.viewFilterLogger.info("Filter:" + filterName + " has been saved/updated successfully");
            return filterMap;
        }
        catch (final Exception ex) {
            try {
                if (!isAnonymous) {
                    DCViewFilterUtil.eventLogUtil.addEvent(6001, loginName, null, "dc.common.viewFilter.Filter_log_failure_message", remarksArgs, true);
                }
            }
            catch (final Exception ex2) {
                this.viewFilterLogger.log(Level.SEVERE, "Exception while Saving Filter:" + ex);
            }
            this.viewFilterLogger.log(Level.SEVERE, "Exception while Saving Filter:" + ex);
            if (ex instanceof DataAccessException) {
                throw (DataAccessException)ex;
            }
            return filterMap;
        }
    }
    
    public Row saveCriteriaColumnDetailRow(final DCViewFilterCriteria dcViewFilterCriteria, final int order) {
        String searchValue1 = null;
        String searchValue2 = null;
        Integer logicalOperator = 1;
        final String comparatorCustomType = dcViewFilterCriteria.getCustomComparator();
        final List value = dcViewFilterCriteria.getSearchValue();
        if (value != null && !value.isEmpty()) {
            searchValue1 = value.get(0);
            if (dcViewFilterCriteria.getComparator().equalsIgnoreCase("between")) {
                searchValue2 = value.get(1);
            }
            else {
                searchValue1 = (String)value.stream().collect(Collectors.joining("$@$"));
            }
        }
        if (dcViewFilterCriteria.getLogicalOperator().equalsIgnoreCase("AND")) {
            logicalOperator = 1;
        }
        else if (dcViewFilterCriteria.getLogicalOperator().equalsIgnoreCase("OR")) {
            logicalOperator = 0;
        }
        final Row criteriaColumnDetails = new Row("CriteriaColumnDetails");
        criteriaColumnDetails.set("COLUMN_ID", (Object)dcViewFilterCriteria.getColumnID());
        criteriaColumnDetails.set("COMPARATOR", (Object)dcViewFilterCriteria.getComparator());
        criteriaColumnDetails.set("LOGICAL_OPERATOR", (Object)logicalOperator);
        criteriaColumnDetails.set("SEARCH_VALUE", (Object)searchValue1);
        criteriaColumnDetails.set("CRITERIA_ORDER", (Object)order);
        if (comparatorCustomType != null && !comparatorCustomType.equalsIgnoreCase("")) {
            final int opCustomType = DCViewFilterConstants.getCustomTypes(comparatorCustomType);
            if (opCustomType != -1) {
                criteriaColumnDetails.set("OP_CUSTOM_TYPE", (Object)opCustomType);
            }
        }
        if (searchValue2 != null && !searchValue2.equalsIgnoreCase("")) {
            criteriaColumnDetails.set("SEARCH_VALUE_2", (Object)searchValue2);
        }
        return criteriaColumnDetails;
    }
    
    public DCViewFilter getCriteriaJSONForFilter(final long filterId) {
        this.viewFilterLogger.fine("Inside getCriteriaJSONFilter to fetch criteria details for Filter:" + filterId);
        DCViewFilter dcViewFilter = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CriteriaColumnDetails"));
        final Join filterCritJoin = new Join("CriteriaColumnDetails", "DCViewFilterCriteria", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 2);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria filterCriteria = new Criteria(new Column("DCViewFilterCriteria", "FILTER_ID"), (Object)filterId, 0);
        final SortColumn criteriaIndexSort = new SortColumn("CriteriaColumnDetails", "CRITERIA_ORDER", true);
        selectQuery.addSortColumn(criteriaIndexSort);
        selectQuery.addJoin(filterCritJoin);
        selectQuery.setCriteria(filterCriteria);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            dcViewFilter = this.convertToViewFilterModel(dataObject);
        }
        catch (final DataAccessException ex) {
            this.viewFilterLogger.severe("Exception while fetching Criteria Details for Filter" + ex.getLocalizedMessage());
        }
        return dcViewFilter;
    }
    
    public DCViewFilter convertToViewFilterModel(final DataObject criteriaDO) throws DataAccessException {
        DCViewFilter dcViewFilter = null;
        if (criteriaDO != null && !criteriaDO.isEmpty()) {
            dcViewFilter = new DCViewFilter();
            final Iterator filterCriteriaItr = criteriaDO.getRows("CriteriaColumnDetails");
            while (filterCriteriaItr.hasNext()) {
                final DCViewFilterCriteria dcViewFilterCriteria = new DCViewFilterCriteria();
                final Row filterCriteriaRow = filterCriteriaItr.next();
                final Long columnID = (Long)filterCriteriaRow.get("COLUMN_ID");
                final String comparator = (String)filterCriteriaRow.get("COMPARATOR");
                final String value = (String)filterCriteriaRow.get("SEARCH_VALUE");
                final String value2 = (String)filterCriteriaRow.get("SEARCH_VALUE_2");
                final Integer customType = (Integer)filterCriteriaRow.get("OP_CUSTOM_TYPE");
                final int logicalOperator = (int)filterCriteriaRow.get("LOGICAL_OPERATOR");
                final List valueList = (value != null) ? new ArrayList(Arrays.asList((String[])value.split(Pattern.quote("$@$")))) : new ArrayList();
                if (value2 != null && !value2.isEmpty()) {
                    valueList.add(value2);
                }
                final String logicalOperatorString = (logicalOperator == 1) ? "AND" : "OR";
                dcViewFilterCriteria.setColumnID(columnID);
                dcViewFilterCriteria.setComparator(comparator);
                dcViewFilterCriteria.setSearchValue(valueList);
                dcViewFilterCriteria.setLogicalOperator(logicalOperatorString);
                if (customType != null) {
                    dcViewFilterCriteria.setCustomComparator(DCViewFilterConstants.getCustomTypeValue(customType));
                }
                else {
                    dcViewFilterCriteria.setCustomComparator("--");
                }
                dcViewFilter.addDCViewFilterCriteria(dcViewFilterCriteria);
            }
            this.viewFilterLogger.fine("Criteria Details :" + dcViewFilter);
        }
        return dcViewFilter;
    }
    
    public String checkAndAddVariableValueForDCViewFilter(final ViewContext viewCtx, final String variableName) {
        String viewFilterCriteria = null;
        final HttpServletRequest request = viewCtx.getRequest();
        String viewId = request.getParameter("viewId");
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        viewId = ((viewId == null && isDCViewFilterReset == null) ? request.getParameter("viewId") : viewId);
        if (viewId == null && viewCtx.getModel() != null) {
            final String viewName = viewCtx.getUniqueId();
            viewId = WebViewAPI.getViewNameNo((Object)viewName) + "";
        }
        this.viewFilterLogger.fine("Inside checkAndAddVariableValueForDCViewFilter to add criteria details for viewId:" + viewId);
        String criteriaJSONString = request.getParameter("criteriaJSON");
        criteriaJSONString = (String)((criteriaJSONString == null && isDCViewFilterReset == null) ? viewCtx.getStateParameter("criteriaJSON") : criteriaJSONString);
        criteriaJSONString = (String)((criteriaJSONString == null && isDCViewFilterReset == null) ? request.getAttribute("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString != null && viewId != null) {
            viewFilterCriteria = this.getVariableValueForDCViewFilter(viewId, criteriaJSONString, variableName);
        }
        this.viewFilterLogger.fine("Variable Value given by checkAndAddVariableValueForDCViewFilter is:" + viewFilterCriteria);
        return viewFilterCriteria;
    }
    
    public String getVariableValueForDCViewFilter(final String viewId, final String criteriaJSONString, final String placeHolderName) {
        this.viewFilterLogger.log(Level.FINEST, "Inside appendNativeDCViewFilterCriteria to append native Criteria for JSON" + criteriaJSONString);
        String criteria = null;
        if (criteriaJSONString != null) {
            try {
                final DCViewFilter dcViewFilter = DCViewFilter.dcViewFilterMapper(criteriaJSONString);
                final List<DCViewFilterCriteria> dcViewFilterCriteriaList = dcViewFilter.getDcViewFilterCriteriaList();
                final int placeHolderFor = this.getPlaceHolderType(viewId, placeHolderName);
                if (placeHolderFor != -1 && placeHolderFor == 2) {
                    String joinString = null;
                    final ArrayList joinColumnIdList = this.getColumnIdsForPlaceHolder(viewId, placeHolderName, 2);
                    joinString = this.checkAndAddNativeJoin(joinColumnIdList, dcViewFilterCriteriaList);
                    if (joinString != null && joinString.trim().length() == 0) {
                        joinString = null;
                    }
                    return joinString;
                }
                if (placeHolderFor != -1) {
                    final ArrayList columnIdList = this.getColumnIdsForPlaceHolder(viewId, placeHolderName, 1);
                    criteria = this.getNativeDCViewFilterCriteria(columnIdList, dcViewFilterCriteriaList);
                    if (criteria != null && criteria.trim().length() == 0) {
                        criteria = null;
                    }
                }
            }
            catch (final Exception ex) {
                this.viewFilterLogger.log(Level.SEVERE, "Exception while generating criteia", ex);
                return criteria;
            }
        }
        return criteria;
    }
    
    public String checkAndAddNativeJoin(final ArrayList joinColumIdList, final List<DCViewFilterCriteria> dcViewFilterCriteriaList) {
        this.viewFilterLogger.fine("Inside checkAndAddNativeJoin to add Join");
        final StringBuilder joinString = new StringBuilder();
        try {
            final ArrayList relationIdList = new ArrayList();
            final ArrayList columnIdList = new ArrayList();
            final Long subModuleId = null;
            DataObject joinDetailsDO = null;
            DCViewFilterCriteria dcViewFilterCriteria = null;
            for (int i = 0; i < dcViewFilterCriteriaList.size(); ++i) {
                dcViewFilterCriteria = dcViewFilterCriteriaList.get(i);
                final Long columnID = dcViewFilterCriteria.getColumnID();
                if (joinColumIdList.contains(columnID)) {
                    columnIdList.add(columnID);
                }
            }
            if (!columnIdList.isEmpty()) {
                final SelectQuery columnDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRJoinRelation"));
                columnDetailsSelect.addSelectColumn(new Column((String)null, "*"));
                final Join crJoin = new Join("CRJoinRelation", "DCViewFilterJoinRel", new String[] { "RELATION_ID" }, new String[] { "RELATION_ID" }, 2);
                columnDetailsSelect.addJoin(crJoin);
                final Column joinOrderColumn = Column.getColumn("DCViewFilterJoinRel", "JOIN_ORDER");
                final SortColumn sortCol = new SortColumn(joinOrderColumn, true);
                columnDetailsSelect.addSortColumn(sortCol);
                final Criteria columnDetailsCrit = new Criteria(new Column("DCViewFilterJoinRel", "COLUMN_ID"), (Object)columnIdList.toArray(), 8);
                columnDetailsSelect.setCriteria(columnDetailsCrit);
                joinDetailsDO = SyMUtil.getPersistence().get(columnDetailsSelect);
                if (joinDetailsDO != null && !joinDetailsDO.isEmpty()) {
                    final Iterator joinDetailsItr = joinDetailsDO.getRows("CRJoinRelation");
                    while (joinDetailsItr.hasNext()) {
                        final Row columnRow = joinDetailsItr.next();
                        final Long relationId = (Long)columnRow.get("RELATION_ID");
                        relationIdList.add(relationId);
                    }
                }
            }
            if (!relationIdList.isEmpty()) {
                final Criteria joinColRelationIdCri = new Criteria(Column.getColumn("CRJoinColumns", "RELATION_ID"), (Object)relationIdList.toArray(), 8);
                final DataObject joinCriteriaDO = this.getjoinCriteriaDO(joinColRelationIdCri);
                if (joinDetailsDO != null && !joinDetailsDO.isEmpty()) {
                    final Iterator joinDOItr = joinDetailsDO.getRows("CRJoinRelation");
                    while (joinDOItr.hasNext()) {
                        final Row joinRow = joinDOItr.next();
                        String relationID = "";
                        String childTableAlias = "";
                        String parentTableAlias = "";
                        String joinType = "";
                        final String childColumnAlias = "";
                        final String parentColumnAlias = "";
                        String childTableName = "";
                        String parentTableName = "";
                        final String joinOperator = "";
                        if (joinRow != null) {
                            relationID = (joinRow.get("RELATION_ID") + "").trim();
                            childTableAlias = (joinRow.get("CHILD_TABLE_ALIAS") + "").trim();
                            parentTableAlias = (joinRow.get("PARENT_TABLE_ALIAS") + "").trim();
                            childTableName = (joinRow.get("CHILD_TABLE_NAME") + "").trim();
                            parentTableName = (joinRow.get("PARENT_TABLE_NAME") + "").trim();
                            joinType = (joinRow.get("JOIN_TYPE") + "").trim();
                        }
                        final Criteria joinColumnCriteria = new Criteria(Column.getColumn("CRJoinColumns", "RELATION_ID"), (Object)relationID, 0);
                        final Iterator iterator = joinCriteriaDO.getRows("CRJoinColumns", joinColumnCriteria);
                        final String joinToken = this.getJoinName(joinType);
                        if (joinToken != null) {
                            joinString.append(joinToken);
                            joinString.append(" ");
                            joinString.append(childTableName);
                            joinString.append(" ");
                            joinString.append(childTableAlias);
                            joinString.append(" ");
                            joinString.append("ON");
                            joinString.append(" ");
                            joinString.append(this.createNativeJoinCriteria(iterator, childTableAlias, parentTableAlias));
                            joinString.append(" ");
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.viewFilterLogger.severe("Exception in checkAndAddNativeJoin" + ex);
        }
        this.viewFilterLogger.fine("Join String provided by checkAndAddNativeJoin is:" + joinString.toString());
        return joinString.toString();
    }
    
    public String getJoinName(final String joinName) {
        if (joinName.equalsIgnoreCase("INNER_JOIN")) {
            return "INNER JOIN";
        }
        if (joinName.equalsIgnoreCase("LEFT_JOIN")) {
            return "LEFT JOIN";
        }
        return null;
    }
    
    public Integer getPlaceHolderType(final String viewId, final String placeHolderName) {
        Integer placeHolderFor = -1;
        final SelectQuery crColumnSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
        final Join viewJoin = new Join("CRColumns", "ViewToCRColumnsRel", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join placeHolderJoin = new Join("CRColumns", "PlaceHolderToCRColumnRel", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join crSubModuleJoin = new Join("CRColumns", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
        final Join crModuleJoin = new Join("CRSubModule", "CRModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        Criteria dcViewFilterCriteria = new Criteria(Column.getColumn("CRModule", "MODULE_NAME"), (Object)"FilterCriteria", 0);
        final Criteria placeHolderCriteria = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "PLACE_HOLDER"), (Object)placeHolderName, 0);
        final Criteria viewCriteria = new Criteria(Column.getColumn("ViewToCRColumnsRel", "VIEW_ID"), (Object)viewId, 0);
        crColumnSelect.addJoin(viewJoin);
        crColumnSelect.addJoin(placeHolderJoin);
        crColumnSelect.addJoin(crSubModuleJoin);
        crColumnSelect.addJoin(crModuleJoin);
        dcViewFilterCriteria = dcViewFilterCriteria.and(placeHolderCriteria);
        dcViewFilterCriteria = dcViewFilterCriteria.and(viewCriteria);
        crColumnSelect.addSelectColumn(Column.getColumn("CRColumns", "COLUMN_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("CRModule", "MODULE_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("CRSubModule", "SUB_MODULE_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("ViewToCRColumnsRel", "VIEW_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("ViewToCRColumnsRel", "COLUMN_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("PlaceHolderToCRColumnRel", "VIEW_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("PlaceHolderToCRColumnRel", "PLACE_HOLDER"));
        crColumnSelect.addSelectColumn(Column.getColumn("PlaceHolderToCRColumnRel", "COLUMN_ID"));
        crColumnSelect.addSelectColumn(Column.getColumn("PlaceHolderToCRColumnRel", "PLACEHOLDER_FOR"));
        crColumnSelect.setCriteria(dcViewFilterCriteria);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(crColumnSelect);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row placeHolderRow = dataObject.getFirstRow("PlaceHolderToCRColumnRel");
                placeHolderFor = (Integer)placeHolderRow.get("PLACEHOLDER_FOR");
            }
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception while fetching column ids associated with PlaceHolder:" + placeHolderName + " " + e);
        }
        return placeHolderFor;
    }
    
    public ArrayList getColumnIdsForPlaceHolder(final String viewId, final String placeHolderName, final int placeHolderFor) {
        final ArrayList columnIdList = new ArrayList();
        final SelectQuery crColumnSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
        final Join viewJoin = new Join("CRColumns", "ViewToCRColumnsRel", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join placeHolderJoin = new Join("CRColumns", "PlaceHolderToCRColumnRel", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join crSubModuleJoin = new Join("CRColumns", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
        final Join crModuleJoin = new Join("CRSubModule", "CRModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        Criteria dcViewFilterCriteria = new Criteria(Column.getColumn("CRModule", "MODULE_NAME"), (Object)"FilterCriteria", 0);
        final Criteria placeHolderCriteria = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "PLACE_HOLDER"), (Object)placeHolderName, 0);
        final Criteria placeHolderForCriteria = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "PLACEHOLDER_FOR"), (Object)placeHolderFor, 0);
        final Criteria viewCriteria = new Criteria(Column.getColumn("ViewToCRColumnsRel", "VIEW_ID"), (Object)viewId, 0);
        final Criteria placeHolderViewCriteria = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "VIEW_ID"), (Object)viewId, 0);
        crColumnSelect.addJoin(viewJoin);
        crColumnSelect.addJoin(placeHolderJoin);
        crColumnSelect.addJoin(crSubModuleJoin);
        crColumnSelect.addJoin(crModuleJoin);
        dcViewFilterCriteria = dcViewFilterCriteria.and(placeHolderCriteria);
        dcViewFilterCriteria = dcViewFilterCriteria.and(placeHolderForCriteria);
        dcViewFilterCriteria = dcViewFilterCriteria.and(viewCriteria);
        dcViewFilterCriteria = dcViewFilterCriteria.and(placeHolderViewCriteria);
        crColumnSelect.addSelectColumn(Column.getColumn("CRColumns", "COLUMN_ID"));
        crColumnSelect.setCriteria(dcViewFilterCriteria);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(crColumnSelect);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator columnIdItr = dataObject.getRows("CRColumns");
                while (columnIdItr.hasNext()) {
                    final Row columnId = columnIdItr.next();
                    columnIdList.add(columnId.get("COLUMN_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception while fetching column ids associated with PlaceHolder:" + placeHolderName + " " + e);
        }
        return columnIdList;
    }
    
    public String getNativeDCViewFilterCriteria(final ArrayList columnIdList, final List<DCViewFilterCriteria> dcViewFilterCriteriaList) {
        this.viewFilterLogger.log(Level.FINEST, "Inside getNativeDCViewFilterCriteria to construct native Criteria for JSON" + dcViewFilterCriteriaList);
        StringBuilder criteriaString = new StringBuilder();
        try {
            int criteriaApplied = 0;
            for (int i = 0; i < dcViewFilterCriteriaList.size(); ++i) {
                final DCViewFilterCriteria dcViewFilterCriteria = dcViewFilterCriteriaList.get(i);
                final Long columnID = dcViewFilterCriteria.getColumnID();
                Integer logicalOperator = 1;
                if (dcViewFilterCriteria.getLogicalOperator().equalsIgnoreCase("AND")) {
                    logicalOperator = 1;
                }
                else if (dcViewFilterCriteria.getLogicalOperator().equalsIgnoreCase("OR")) {
                    logicalOperator = 0;
                }
                final Integer criteriaIndex = i;
                if (columnIdList.contains(columnID)) {
                    final StringBuilder criteria = this.getCriteriaString(dcViewFilterCriteria, columnID);
                    if (criteriaApplied == 0) {
                        criteriaString.append(criteria.toString());
                    }
                    else if (criteriaApplied > 0) {
                        if (logicalOperator == 1) {
                            criteriaString.append(" ");
                            criteriaString.append("AND");
                        }
                        else if (logicalOperator == 0) {
                            criteriaString.append(" ");
                            criteriaString.append("OR");
                        }
                        criteriaString.append((CharSequence)criteria);
                    }
                    ++criteriaApplied;
                }
            }
            if (criteriaString.length() != 0) {
                final StringBuilder wrappedCriteria = new StringBuilder();
                wrappedCriteria.append("(");
                wrappedCriteria.append((CharSequence)criteriaString);
                wrappedCriteria.append(")");
                criteriaString = wrappedCriteria;
            }
            this.viewFilterLogger.log(Level.INFO, "Criteria set by user is {0}", criteriaString);
            return criteriaString.toString();
        }
        catch (final Exception ex) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception while generating criteria", ex);
            return null;
        }
    }
    
    public StringBuilder getCriteriaString(final DCViewFilterCriteria dcViewFilterCriteria, final Long columnId) {
        StringBuilder criteria = new StringBuilder();
        try {
            final String handlerClassName = this.getDCViewFilterHandler(columnId);
            if (handlerClassName != null) {
                final DCViewFilterHandler dcViewFilterHandler = (DCViewFilterHandler)Class.forName(handlerClassName).newInstance();
                criteria.append(dcViewFilterHandler.getNativeDCViewFilterCriteria(dcViewFilterCriteria));
            }
            else {
                final SelectQuery columnDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
                columnDetailsSelect.addSelectColumn(new Column((String)null, "*"));
                final Criteria columnDetailsCrit = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)columnId, 0);
                columnDetailsSelect.setCriteria(columnDetailsCrit);
                final DataObject columnDetailsDO = SyMUtil.getPersistence().get(columnDetailsSelect);
                if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                    final Row columnRow = columnDetailsDO.getFirstRow("CRColumns");
                    final String tableAlias = (String)columnRow.get("TABLE_NAME_ALIAS");
                    final String columnAlias = (String)columnRow.get("COLUMN_NAME_ALIAS");
                    final String dataType = (String)columnRow.get("DATA_TYPE");
                    criteria.append(" ");
                    criteria.append("(");
                    criteria.append(this.constructNativeCriteria(tableAlias, columnAlias, dataType, dcViewFilterCriteria));
                    criteria.append(")");
                    criteria.append(" ");
                }
            }
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.FINEST, "Criteria set by constructNativeCriteria is:" + criteria.toString());
            criteria = new StringBuilder();
        }
        return criteria;
    }
    
    public String constructNativeCriteria(final String tableAlias, final String columnAlias, final String dataType, final DCViewFilterCriteria dcViewFilterCriteria) {
        this.viewFilterLogger.log(Level.FINEST, "Inside constructNativeCriteria to construct native Criteria for column" + columnAlias);
        final StringBuilder criteria = new StringBuilder();
        try {
            String searchValue = null;
            String searchValue2 = null;
            String comparator = dcViewFilterCriteria.getComparator();
            final String customComparator = dcViewFilterCriteria.getCustomComparator();
            final List value = dcViewFilterCriteria.getSearchValue();
            if (value != null && !value.isEmpty()) {
                searchValue = value.get(0);
                if (dcViewFilterCriteria.getComparator().equalsIgnoreCase("between")) {
                    searchValue2 = value.get(1);
                }
                else {
                    searchValue = (String)value.stream().collect(Collectors.joining("$@$"));
                }
            }
            if (dataType.equalsIgnoreCase("DATE")) {
                criteria.append(this.constructDateCriteria(tableAlias, columnAlias, comparator, customComparator, searchValue, searchValue2));
            }
            else if (comparator.equalsIgnoreCase("empty") || comparator.equalsIgnoreCase("not empty")) {
                criteria.append(this.getEmptyCriteriaSearchString(tableAlias, columnAlias, comparator));
            }
            else {
                criteria.append(tableAlias);
                criteria.append(".");
                criteria.append(columnAlias);
                criteria.append(" ");
                if (dataType.equalsIgnoreCase("BOOLEAN")) {
                    final String trueValue = "true$@$1";
                    final String falseValue = "false$@$0";
                    comparator = "in";
                    searchValue = (searchValue.equalsIgnoreCase("true") ? trueValue : falseValue);
                }
                final String[] valueArray = searchValue.split(Pattern.quote("$@$"));
                if (valueArray.length > 1) {
                    if (comparator.equalsIgnoreCase("equal")) {
                        comparator = "in";
                    }
                    else if (comparator.equalsIgnoreCase("not equal")) {
                        comparator = "not in";
                    }
                }
                criteria.append(this.getOperatorToken(comparator));
                criteria.append(this.getSearchString(searchValue, comparator, dataType));
            }
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.SEVERE, "constructNativeCriteria: Exception while constructing criteia", e);
            return null;
        }
        this.viewFilterLogger.log(Level.FINEST, "Criteria set by constructNativeCriteria is:" + criteria.toString());
        return criteria.toString();
    }
    
    public String constructDateCriteria(final String tableAlias, final String columnAlias, final String comparator, final String customComparator, final String value, final String searchValue2) {
        this.viewFilterLogger.fine("Inside getDateSearchString to construct Criteria for:" + columnAlias + "Comparator:" + comparator + " Value: " + value + "Search Value 2:" + searchValue2);
        StringBuilder criteria = new StringBuilder();
        final StringBuilder columnBuilder = new StringBuilder();
        columnBuilder.append(tableAlias);
        columnBuilder.append(".");
        columnBuilder.append(columnAlias);
        columnBuilder.append(" ");
        final String dateFormat = "MM/dd/yyyy hh:mm:ss";
        if (comparator.equalsIgnoreCase("is")) {
            if (customComparator.equalsIgnoreCase("isDate")) {
                final Long startTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 00:00:00", dateFormat);
                final Long endTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 23:59:59", dateFormat);
                criteria.append(this.getDateRangeCriteriaString(tableAlias, columnAlias, startTime, endTime));
            }
            else {
                try {
                    final String[] valueArray = value.split(Pattern.quote("$@$"));
                    if (valueArray.length > 1) {
                        final StringBuilder multipleCriteria = new StringBuilder();
                        for (int i = 0; i < valueArray.length; ++i) {
                            multipleCriteria.append("(");
                            multipleCriteria.append(" ");
                            multipleCriteria.append(this.constructDateCriteria(tableAlias, columnAlias, comparator, customComparator, valueArray[i], searchValue2));
                            multipleCriteria.append(" ");
                            multipleCriteria.append(")");
                            if (i != valueArray.length - 1) {
                                multipleCriteria.append("OR");
                            }
                        }
                        criteria = new StringBuilder(multipleCriteria);
                    }
                    else {
                        final Hashtable milliSecRange = Utils.getStartAndEndDates(value, true);
                        if (milliSecRange != null) {
                            final Long startTime2 = milliSecRange.get("startDate");
                            final Long endTime2 = milliSecRange.get("endDate");
                            criteria.append(this.getDateRangeCriteriaString(tableAlias, columnAlias, startTime2, endTime2));
                        }
                    }
                }
                catch (final Exception e) {
                    this.viewFilterLogger.severe("Exception while Constructing Criteria for Date:" + e.getLocalizedMessage());
                }
            }
        }
        else if (comparator.equalsIgnoreCase("before")) {
            final Long beforeTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 23:59:59", dateFormat);
            criteria = new StringBuilder(columnBuilder);
            criteria.append("<=");
            criteria.append(" ");
            criteria.append(beforeTime);
            criteria.append(" ");
        }
        else if (comparator.equalsIgnoreCase("after")) {
            final Long afterTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 00:00:00", dateFormat);
            criteria = new StringBuilder(columnBuilder);
            criteria.append(">=");
            criteria.append(" ");
            criteria.append(afterTime);
            criteria.append(" ");
        }
        else if (comparator.equalsIgnoreCase("between")) {
            final Long fromTime = DateTimeUtil.dateInLonginUserTimeZone(value + " 00:00:00", dateFormat);
            final Long toTime = DateTimeUtil.dateInLonginUserTimeZone(searchValue2 + " 23:59:59", dateFormat);
            criteria.append(this.getDateRangeCriteriaString(tableAlias, columnAlias, fromTime, toTime));
        }
        else if (comparator.equalsIgnoreCase("last_n_days") || comparator.equalsIgnoreCase("Last n Days")) {
            if (value != "") {
                final int noOfDays = Integer.valueOf(value);
                final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
                final Long startTime2 = dateRange.get("fromDate");
                final Long endTime2 = dateRange.get("toDate");
                criteria.append(this.getDateRangeCriteriaString(tableAlias, columnAlias, startTime2, endTime2));
            }
        }
        else if (comparator.equalsIgnoreCase("Before n Days") || comparator.equalsIgnoreCase("before_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
            final Long fromDate = dateRange.get("fromDate");
            criteria = new StringBuilder(columnBuilder);
            criteria.append("<=");
            criteria.append(" ");
            criteria.append(fromDate);
            criteria.append(" ");
        }
        else if (comparator.equalsIgnoreCase("Next N Days") || comparator.equalsIgnoreCase("next_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long fromDate = dateRange.get("fromDate");
            final Long toDate = dateRange.get("toDate");
            criteria.append(this.getDateRangeCriteriaString(tableAlias, columnAlias, fromDate, toDate));
        }
        else if (comparator.equalsIgnoreCase("After n Days") || comparator.equalsIgnoreCase("after_n_days")) {
            final int noOfDays = Integer.valueOf(value);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long to_date = dateRange.get("toDate");
            criteria.append(new Criteria(Column.getColumn(tableAlias, columnAlias), (Object)to_date, 4));
        }
        this.viewFilterLogger.fine("Criteria For Date Column is:" + (Object)criteria);
        return criteria.toString();
    }
    
    public String getDateRangeCriteriaString(final String tableAlias, final String columnAlias, final Long fromTime, final Long toTime) {
        this.viewFilterLogger.fine("Inside getDateRangeCriteriaString to construct Criteria for:" + columnAlias + "From Time:" + fromTime + " To Time: " + toTime);
        final StringBuilder criteria = new StringBuilder();
        criteria.append("(");
        criteria.append(" ");
        final StringBuilder columnBuilder = new StringBuilder();
        columnBuilder.append(tableAlias);
        columnBuilder.append(".");
        columnBuilder.append(columnAlias);
        columnBuilder.append(" ");
        final StringBuilder greaterThanCriteria = new StringBuilder(columnBuilder);
        greaterThanCriteria.append(">=");
        greaterThanCriteria.append(" ");
        greaterThanCriteria.append(fromTime);
        greaterThanCriteria.append(" ");
        final StringBuilder lessThanCriteria = new StringBuilder(columnBuilder);
        lessThanCriteria.append("<=");
        lessThanCriteria.append(" ");
        lessThanCriteria.append(toTime);
        lessThanCriteria.append(" ");
        criteria.append((CharSequence)greaterThanCriteria);
        criteria.append(" ");
        criteria.append("AND");
        criteria.append(" ");
        criteria.append((CharSequence)lessThanCriteria);
        criteria.append(" ");
        criteria.append(")");
        this.viewFilterLogger.fine("Criteria constructed by getDateRangeCriteriaString is:" + criteria.toString());
        return criteria.toString();
    }
    
    public String getEmptyCriteriaSearchString(final String tableAlias, final String columnAlias, final String operatorValue) {
        this.viewFilterLogger.fine("Inside getEmptyCriteriaSearchString to construct Empty Criteria for:" + columnAlias);
        final StringBuilder emptyCriteriaString = new StringBuilder("(");
        emptyCriteriaString.append(" ");
        final StringBuilder columnNameBuilder = new StringBuilder(tableAlias);
        columnNameBuilder.append(".");
        columnNameBuilder.append(columnAlias);
        columnNameBuilder.append(" ");
        final String operatorToken = this.getOperatorToken(operatorValue);
        final StringBuilder defaultCriteria = new StringBuilder(columnNameBuilder);
        defaultCriteria.append(operatorToken);
        defaultCriteria.append(" ");
        defaultCriteria.append("'");
        defaultCriteria.append("--");
        defaultCriteria.append("'");
        final StringBuilder emptyCriteria = new StringBuilder(columnNameBuilder);
        emptyCriteria.append(operatorToken);
        emptyCriteria.append(" ");
        emptyCriteria.append("'");
        emptyCriteria.append("'");
        final StringBuilder nullCriteria = new StringBuilder(columnNameBuilder);
        if (operatorValue.equalsIgnoreCase("empty")) {
            nullCriteria.append("is");
            nullCriteria.append(" ");
            nullCriteria.append("null");
            emptyCriteriaString.append((CharSequence)defaultCriteria);
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append("OR");
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append((CharSequence)emptyCriteria);
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append("OR");
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append((CharSequence)nullCriteria);
        }
        else {
            nullCriteria.append("is not");
            nullCriteria.append(" ");
            nullCriteria.append("null");
            emptyCriteriaString.append((CharSequence)defaultCriteria);
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append("AND");
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append((CharSequence)emptyCriteria);
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append("AND");
            emptyCriteriaString.append(" ");
            emptyCriteriaString.append((CharSequence)nullCriteria);
        }
        emptyCriteriaString.append(" ");
        emptyCriteriaString.append(")");
        return emptyCriteriaString.toString();
    }
    
    public Object getSearchString(Object searchString, String operatorValue, final String dataType) {
        boolean isMssql = false;
        if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
            isMssql = true;
        }
        this.viewFilterLogger.finest("Inside getSearchString");
        operatorValue = operatorValue.trim();
        if (operatorValue.equalsIgnoreCase("like")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("not like")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("starts with")) {
            searchString = "'" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("ends with")) {
            searchString = "'%" + searchString + "'";
        }
        else if (operatorValue.equalsIgnoreCase("contains")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("in") || operatorValue.equalsIgnoreCase("not in")) {
            final String[] searchValues = searchString.toString().split(Pattern.quote("$@$"));
            final StringBuilder multipleSearchValues = new StringBuilder();
            multipleSearchValues.append("(");
            for (int i = 0; i < searchValues.length; ++i) {
                if (isMssql) {
                    multipleSearchValues.append(" N");
                }
                multipleSearchValues.append("'");
                multipleSearchValues.append(searchValues[i].trim());
                multipleSearchValues.append("'");
                if (i != searchValues.length - 1) {
                    multipleSearchValues.append(",");
                }
            }
            multipleSearchValues.append(")");
            searchString = multipleSearchValues;
        }
        else {
            searchString = "'" + searchString + "'";
        }
        if (isMssql && !operatorValue.equalsIgnoreCase("in") && !operatorValue.equalsIgnoreCase("not in")) {
            searchString = " N" + searchString;
        }
        this.viewFilterLogger.finest("Value returned from getSearchString is" + searchString);
        return searchString;
    }
    
    public String getOperatorToken(String value) {
        this.viewFilterLogger.fine("Inside getOperatorToken");
        value = value.trim();
        if (value.equalsIgnoreCase("equal")) {
            return "=";
        }
        if (value.equalsIgnoreCase("empty")) {
            return "=";
        }
        if (value.equalsIgnoreCase("not equal")) {
            return "!=";
        }
        if (value.equalsIgnoreCase("not empty")) {
            return "!=";
        }
        if (value.equalsIgnoreCase("greater than")) {
            return ">";
        }
        if (value.equalsIgnoreCase("greater or equal")) {
            return ">=";
        }
        if (value.equalsIgnoreCase("less than")) {
            return "<";
        }
        if (value.equalsIgnoreCase("less or equal")) {
            return "<=";
        }
        if (value.equalsIgnoreCase("like")) {
            return "like";
        }
        if (value.equalsIgnoreCase("contains")) {
            return "like";
        }
        if (value.equalsIgnoreCase("not like")) {
            return "not like";
        }
        if (value.equalsIgnoreCase("starts with")) {
            return "like";
        }
        if (value.equalsIgnoreCase("ends with")) {
            return "like";
        }
        if (value.equalsIgnoreCase("in")) {
            return "in";
        }
        if (value.equalsIgnoreCase("not in")) {
            return "not in";
        }
        return "";
    }
    
    public JSONObject ViewFilterComparatorJSON() {
        final JSONObject viewFilterComparatorJSON = new JSONObject();
        try {
            viewFilterComparatorJSON.put("charlist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.CHATOPERATORLIST)));
            viewFilterComparatorJSON.put("intlist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.INTOPERATORLIST)));
            viewFilterComparatorJSON.put("i18nlist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.I18N_OPERATOR_LIST)));
            viewFilterComparatorJSON.put("dcViewFilterDatelist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.DCVIEWFILTERDATELIST)));
            viewFilterComparatorJSON.put("booleanOperatorList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.BOOLEANOPERATORLIST)));
            viewFilterComparatorJSON.put("equalOnlyList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.EQUALOPERATORLIST)));
            viewFilterComparatorJSON.put("similarOperatorList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.NOEQUALCHARTOPERATORLIST)));
            viewFilterComparatorJSON.put("equalLikeList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.EQUALLIKEOPERATORLIST)));
            viewFilterComparatorJSON.put("equalLikeOnlyList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.EQUALLIKEONLYOPERATORLIST)));
            viewFilterComparatorJSON.put("likeOnlyList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.LIKEONLYOPERATORLIST)));
            viewFilterComparatorJSON.put("dateRestrictedList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.DATERESTRICTEDLIST)));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterComparatorJSON;
    }
    
    public JSONObject ViewFilterComparatorJSONi18N() {
        final JSONObject viewFilterComparatorI18NJSON = new JSONObject();
        try {
            viewFilterComparatorI18NJSON.put("charlist_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.CHATOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("intlist_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.INTOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("dcViewFilterDatelist_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.DCVIEWFILTERDATELIST_I18N))));
            viewFilterComparatorI18NJSON.put("booleanOperatorList_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.BOOLEANOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("equalOnlyList_i18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.EQUALOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("i18nList_I18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.I18N_OPERATOR_LIST_I18N))));
            viewFilterComparatorI18NJSON.put("similarOperator_i18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.NOEQUALCHATOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("equalLike_i18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.EQUALLIKEOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("equalLikeOnlyList_i18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.EQUALLIKEONLYOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("likeOnlyList_i18n", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.LIKEONLYOPERATORLIST_I18N))));
            viewFilterComparatorI18NJSON.put("dateRestrictedList_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.DATERESTRICTEDLIST_I18N))));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterComparatorI18NJSON;
    }
    
    public JSONObject viewFilterCompValueJSON() {
        final JSONObject viewFilterCompValueJSON = new JSONObject();
        try {
            viewFilterCompValueJSON.put("dcViewFilterDateValuelist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.PATCHDATEOPERATORLIST)));
            viewFilterCompValueJSON.put("booleanValueList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.BOOLEANVALUELIST)));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterCompValueJSON;
    }
    
    public JSONObject viewFilterCompValueI18NJSON() {
        final JSONObject viewFilterCompValueI18NJSON = new JSONObject();
        try {
            viewFilterCompValueI18NJSON.put("dcViewFilterDateValuelist_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.PATCHDATEOPERATORLIST_I18N))));
            viewFilterCompValueI18NJSON.put("booleanValueList_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.BOOLEANVALUELIST_I18N))));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterCompValueI18NJSON;
    }
    
    public JSONObject viewFilterCompValueJSONForScheduleReport() {
        final JSONObject viewFilterCompValueJSON = new JSONObject();
        try {
            viewFilterCompValueJSON.put("dcViewFilterDateValuelist", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.SCHEDULEDATEOPERATORLIST)));
            viewFilterCompValueJSON.put("booleanValueList", (Object)new JSONArray((Collection)Arrays.asList(CRConstantValues.BOOLEANVALUELIST)));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterCompValueJSON;
    }
    
    public JSONObject viewFilterCompValueI18NJSONForScheduleReport() {
        final JSONObject viewFilterCompValueI18NJSON = new JSONObject();
        try {
            viewFilterCompValueI18NJSON.put("dcViewFilterDateValuelist_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.SCHEDULEDATEOPERATORLIST_I18N))));
            viewFilterCompValueI18NJSON.put("booleanValueList_I18N", (Object)new JSONArray((Collection)Arrays.asList(this.getI18Nlist(CRConstantValues.BOOLEANVALUELIST_I18N))));
        }
        catch (final JSONException e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception in getCriteriaList:" + e);
        }
        return viewFilterCompValueI18NJSON;
    }
    
    public void getCriteriaList(final HttpServletRequest request, final String isScheduleReport) {
        this.viewFilterLogger.log(Level.FINEST, "Inside getCriteriaList");
        String reportId = request.getParameter("reportId");
        final String viewName = (String)request.getAttribute("viewName");
        if (reportId == null) {
            reportId = (String)request.getAttribute("reportId");
        }
        JSONObject viewFilterComparatorJSON = new JSONObject();
        JSONObject viewFilterComparatorI18NJSON = new JSONObject();
        JSONObject viewFilterCompValueJSON = new JSONObject();
        JSONObject viewFilterCompValueI18NJSON = new JSONObject();
        viewFilterComparatorJSON = getInstance().ViewFilterComparatorJSON();
        viewFilterComparatorI18NJSON = getInstance().ViewFilterComparatorJSONi18N();
        if (isScheduleReport.equalsIgnoreCase("false")) {
            viewFilterCompValueJSON = getInstance().viewFilterCompValueJSON();
            viewFilterCompValueI18NJSON = getInstance().viewFilterCompValueI18NJSON();
        }
        else {
            viewFilterCompValueJSON = getInstance().viewFilterCompValueJSONForScheduleReport();
            viewFilterCompValueI18NJSON = getInstance().viewFilterCompValueI18NJSONForScheduleReport();
        }
        request.setAttribute("viewFilterComparatorJSON", (Object)viewFilterComparatorJSON);
        request.setAttribute("viewFilterComparatorI18NJSON", (Object)viewFilterComparatorI18NJSON);
        request.setAttribute("viewFilterCompValueJSON", (Object)viewFilterCompValueJSON);
        request.setAttribute("viewFilterCompValueI18NJSON", (Object)viewFilterCompValueI18NJSON);
        JSONArray criteriaCols = new JSONArray();
        criteriaCols = this.buildColumnListJson(reportId);
        request.setAttribute("criteriaCols", (Object)criteriaCols);
        request.setAttribute("reportId", (Object)reportId);
        request.setAttribute("viewName", (Object)viewName);
    }
    
    private String[] getI18Nlist(final String[] filterList) {
        this.viewFilterLogger.finest("Inside getI18Nlist");
        final String[] i18nedList = new String[filterList.length];
        try {
            for (int i = 0; i < filterList.length; ++i) {
                i18nedList[i] = I18N.getMsg(filterList[i], new Object[0]);
            }
        }
        catch (final Exception ex) {
            this.viewFilterLogger.severe("Exception while getting I18N value for filter value: " + ex);
        }
        return i18nedList;
    }
    
    public JSONArray buildColumnListJson(final String viewId) {
        this.viewFilterLogger.log(Level.FINEST, "Inside buildColumnListJson");
        final JSONArray criteriaCols = new JSONArray();
        try {
            this.viewFilterLogger.log(Level.FINE, "Going to generate criteria for view id: {0}", viewId);
            final DataObject dataObject = this.getCRColumnDetailsDO(viewId);
            final ArrayList categories = this.getColumnCategoriesList(viewId);
            if (dataObject != null && !dataObject.isEmpty()) {
                for (int i = 0; i < categories.size(); ++i) {
                    final JSONObject categoryDetails = new JSONObject();
                    final JSONArray columnsList = new JSONArray();
                    final Object value = categories.get(i);
                    categoryDetails.put("category", (Object)I18N.getMsg((String)value, new Object[0]));
                    final Criteria categoryCriteria = new Criteria(new Column("CRColumns", "COLUMN_CATEGORY"), value, 0);
                    final Iterator iter = dataObject.getRows("CRColumns", categoryCriteria);
                    while (iter.hasNext()) {
                        final JSONObject columnDetails = new JSONObject();
                        final Row tableRows = iter.next();
                        final String colname = (String)tableRows.get("DISPLAY_NAME");
                        final String dataType = (String)tableRows.get("DATA_TYPE");
                        final String colId = String.valueOf(tableRows.get("COLUMN_ID"));
                        final String searchEnabled = String.valueOf(tableRows.get("SEARCH_ENABLED"));
                        final String displayName = I18N.getMsg(colname, new Object[0]);
                        columnDetails.put("displayString", (Object)displayName);
                        columnDetails.put("dataType", (Object)dataType);
                        columnDetails.put("columnId", (Object)colId);
                        columnDetails.put("searchEnabled", (Object)searchEnabled);
                        columnsList.put((Object)columnDetails);
                    }
                    categoryDetails.put("columns", (Object)columnsList);
                    criteriaCols.put((Object)categoryDetails);
                }
                this.viewFilterLogger.fine("Criteria columns for " + viewId + " is " + criteriaCols.toString());
                return criteriaCols;
            }
            return null;
        }
        catch (final Exception e) {
            this.viewFilterLogger.severe("Exception in fetching criteria status" + e);
            return null;
        }
    }
    
    public DataObject getCRColumnDetailsDO(final String viewID) throws DataAccessException {
        final Column viewCol = Column.getColumn("ViewToCRColumnsRel", "VIEW_ID");
        Criteria cri = new Criteria(viewCol, (Object)viewID, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewToCRColumnsRel"));
        final Join join = new Join("ViewToCRColumnsRel", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join subModuleJoin = new Join("CRColumns", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
        final Join moduleJoin = new Join("CRSubModule", "CRModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        final Criteria moduleCriteria = new Criteria(new Column("CRModule", "MODULE_NAME"), (Object)"FilterCriteria", 0);
        cri = cri.and(moduleCriteria);
        final Column displayOrderCol = Column.getColumn("CRColumns", "DISPLAY_ORDER");
        final SortColumn sortCol = new SortColumn(displayOrderCol, true);
        query.addSortColumn(sortCol);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(join);
        query.addJoin(subModuleJoin);
        query.addJoin(moduleJoin);
        query.setCriteria(cri);
        return SyMUtil.getPersistence().get(query);
    }
    
    public ArrayList getColumnCategoriesList(final String viewId) {
        this.viewFilterLogger.finest("Inside getColumnCategoriesList");
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet categories = null;
        final ArrayList categoriesList = new ArrayList();
        try {
            conn = relApi.getConnection();
            final Column viewCol = Column.getColumn("ViewToCRColumnsRel", "VIEW_ID");
            final Column categoryCol = Column.getColumn("CRColumns", "COLUMN_CATEGORY");
            Criteria cri = new Criteria(viewCol, (Object)viewId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewToCRColumnsRel"));
            final Join join = new Join("ViewToCRColumnsRel", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final Join subModuleJoin = new Join("CRColumns", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
            final Join moduleJoin = new Join("CRSubModule", "CRModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
            final Column categoryDispOrderCol = Column.getColumn("CRColumns", "CATEGORY_ORDER");
            final SortColumn sortCol = new SortColumn(categoryDispOrderCol, true);
            query.addSortColumn(sortCol);
            final Criteria moduleCriteria = new Criteria(new Column("CRModule", "MODULE_NAME"), (Object)"FilterCriteria", 0);
            cri = cri.and(moduleCriteria);
            query.addJoin(join);
            query.addJoin(subModuleJoin);
            query.addJoin(moduleJoin);
            query.addSelectColumn(categoryCol);
            query.addSelectColumn(categoryDispOrderCol);
            query.setDistinct(true);
            query.setCriteria(cri);
            categories = relApi.executeQuery((Query)query, conn);
            while (categories.next()) {
                categoriesList.add(categories.getValue(1));
            }
            this.viewFilterLogger.fine("getColumnCategoriesList: search values query string:" + RelationalAPI.getInstance().getSelectSQL((Query)query));
        }
        catch (final Exception ex) {
            this.viewFilterLogger.severe("Exception in fetching column category list" + ex);
            try {
                categories.close();
            }
            catch (final SQLException ex2) {
                this.viewFilterLogger.severe("Exception while closing column categories data object" + ex2);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex2) {
                this.viewFilterLogger.severe("Exception in closing DB connection" + ex2);
            }
        }
        finally {
            try {
                categories.close();
            }
            catch (final SQLException ex3) {
                this.viewFilterLogger.severe("Exception while closing column categories data object" + ex3);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex3) {
                this.viewFilterLogger.severe("Exception in closing DB connection" + ex3);
            }
        }
        return categoriesList;
    }
    
    public ArrayList fetchTableNamesFromList(final ArrayList list) {
        final ArrayList name = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            final Table table = list.get(i);
            name.add(table.getTableAlias());
        }
        return name;
    }
    
    public DataObject getJoinDO(final Criteria criteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRJoinRelation"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            final Column nameCol = Column.getColumn("CRJoinRelation", "RELATION_ID");
            final SortColumn sortCol = new SortColumn(nameCol, true);
            selectQuery.addSortColumn(sortCol);
            final DataObject data = DataAccess.get(selectQuery);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            this.viewFilterLogger.log(Level.FINE, "SQL Query for Join  " + actualSQL);
            return data;
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.WARNING, "DCViewFilterUtil :Exception from getJoinDO:", e);
            return null;
        }
    }
    
    public DataObject getjoinCriteriaDO(final Criteria criteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRJoinRelation"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join(Table.getTable("CRJoinRelation"), Table.getTable("CRJoinColumns"), new String[] { "RELATION_ID" }, new String[] { "RELATION_ID" }, 2);
            selectQuery.addJoin(join);
            final Column joinOrderColumn = Column.getColumn("CRJoinColumns", "JOIN_ORDER");
            final SortColumn sortCol = new SortColumn(joinOrderColumn, true);
            selectQuery.addSortColumn(sortCol);
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            final DataObject data = DataAccess.get(selectQuery);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            this.viewFilterLogger.log(Level.FINE, "SQL Query for joinCriteria " + actualSQL);
            return data;
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.WARNING, "DCViewFilterUtil :Exception from getJoinCriteriaDO:", e);
            return null;
        }
    }
    
    public String getDCViewFilterHandler(final Long columnId) {
        String handlerClassName = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterColumnHandler"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Join join = new Join(Table.getTable("DCViewFilterColumnHandler"), Table.getTable("CRColumns"), new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        selectQuery.addJoin(join);
        final Criteria criteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)columnId, 0);
        selectQuery.setCriteria(criteria);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row handlerRow = dataObject.getRow("DCViewFilterColumnHandler");
                handlerClassName = (String)handlerRow.get("Handler");
            }
        }
        catch (final Exception ex) {
            this.viewFilterLogger.severe("Exception while fetching Handler for column:" + columnId + " Trace: " + ex);
        }
        return handlerClassName;
    }
    
    public synchronized boolean filterNameValidationCheck(final String filteName, final Long pageId, final Long viewId, final Long loginID, final Long filterID) throws DataAccessException {
        boolean nameExists = true;
        final Table table = Table.getTable("DCViewFilterDetails");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        Criteria criteria = new Criteria(Column.getColumn("DCViewFilterDetails", "FILTER_NAME"), (Object)filteName, 0, false);
        final Criteria pageIdCrit = new Criteria(Column.getColumn("DCViewFilterMapping", "PAGE_ID"), (Object)pageId, 0);
        final Criteria viewIdCrit = new Criteria(Column.getColumn("DCViewFilterMapping", "VIEW_ID"), (Object)viewId, 0);
        final Criteria loginCrit = new Criteria(Column.getColumn("DCViewFilterDetails", "LOGIN_ID"), (Object)loginID, 0);
        Criteria filterIDCrit = null;
        if (filterID != null) {
            filterIDCrit = new Criteria(Column.getColumn("DCViewFilterDetails", "FILTER_ID"), (Object)filterID, 1, false);
        }
        final Join filterViewMapping = new Join("DCViewFilterDetails", "DCViewFilterMapping", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
        selectQuery.addSelectColumn(Column.getColumn("DCViewFilterDetails", "FILTER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DCViewFilterDetails", "FILTER_NAME"));
        selectQuery.addJoin(filterViewMapping);
        criteria = criteria.and(pageIdCrit.and(viewIdCrit).and(loginCrit).and(filterIDCrit));
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (dataObject == null || dataObject.isEmpty()) {
            nameExists = false;
        }
        return nameExists;
    }
    
    public boolean isFilterAccessible(final Long filterId, final Long loginId) {
        try {
            final Criteria filterCriteria = new Criteria(Column.getColumn("DCViewFilterDetails", "FILTER_ID"), (Object)filterId, 0);
            final Criteria loginUserCriteria = new Criteria(Column.getColumn("DCViewFilterDetails", "LOGIN_ID"), (Object)loginId, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("DCViewFilterDetails", filterCriteria.and(loginUserCriteria));
            if (dataObject != null && !dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            this.viewFilterLogger.log(Level.SEVERE, "Exception occurred while authorizing filter " + filterId + " against login user " + loginId, e);
        }
        return false;
    }
    
    public synchronized Map getFilterDetails(final Long filterID, final Long loginID) throws DataAccessException {
        final Map map = new HashMap();
        boolean isFilterMappedToUser = false;
        boolean isFilterExists = false;
        Long pageID = null;
        Long viewID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterDetails"));
        selectQuery.addJoin(new Join("DCViewFilterDetails", "DCViewFilterMapping", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("DCViewFilterDetails", "FILTER_ID"), (Object)filterID, 0, false);
        final Criteria loginCriteria = new Criteria(Column.getColumn("DCViewFilterDetails", "LOGIN_ID"), (Object)loginID, 0);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            isFilterExists = true;
            final Row row = dataObject.getRow("DCViewFilterDetails", loginCriteria);
            if (row != null) {
                isFilterMappedToUser = true;
            }
            final Row filterRow = dataObject.getFirstRow("DCViewFilterMapping");
            pageID = (Long)filterRow.get("PAGE_ID");
            viewID = (long)(int)filterRow.get("VIEW_ID");
        }
        map.put("isFilterNameExists", isFilterExists);
        map.put("isFilterMappedToUser", isFilterMappedToUser);
        map.put("pageID", pageID);
        map.put("viewID", viewID);
        return map;
    }
    
    static {
        DCViewFilterUtil.dcViewFilterUtil = null;
        DCViewFilterUtil.eventLogUtil = DCEventLogUtil.getInstance();
    }
}
