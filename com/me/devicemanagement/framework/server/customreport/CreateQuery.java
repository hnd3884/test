package com.me.devicemanagement.framework.server.customreport;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Logger;

public class CreateQuery
{
    long from_date;
    long to_date;
    boolean checkCustomDate;
    public static Logger logger;
    
    public CreateQuery() {
        this.from_date = 0L;
        this.to_date = 0L;
        this.checkCustomDate = false;
    }
    
    public DataObject getModuleDO(final Long moduleID, final Long subModuleID, final List selectList) {
        try {
            CreateQuery.logger.log(Level.INFO, "Processing getModuleDO method");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSubModule"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join(Table.getTable("CRSubModule"), Table.getTable("CRColumns"), new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
            selectQuery.addJoin(join);
            final Join inputTransformerJoin = new Join(Table.getTable("CRColumns"), Table.getTable("CRColumnTransformer"), new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1);
            selectQuery.addJoin(inputTransformerJoin);
            Criteria subModuleCriteria = new Criteria(Column.getColumn("CRSubModule", "SUB_MODULE_ID"), (Object)new Long(subModuleID), 0);
            final Criteria columnIdCriteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)selectList.toArray(), 8);
            subModuleCriteria = subModuleCriteria.and(columnIdCriteria);
            selectQuery.setCriteria(subModuleCriteria);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            CreateQuery.logger.log(Level.INFO, "SQL Query for Module" + actualSQL);
            final DataObject data = DataAccess.get(selectQuery);
            return data;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getModuleDO:", e);
            return null;
        }
    }
    
    @Deprecated
    public DataObject getCriModuleDO(final Long moduleID, final Long subModuleID, final List criteriaList) {
        CreateQuery.logger.log(Level.INFO, "Processing getCriModuleDO method");
        final Iterator criIterator = criteriaList.iterator();
        final List tempTableList = new ArrayList();
        int i = 0;
        while (criIterator.hasNext()) {
            final CRCriteria crCriteria = criIterator.next();
            tempTableList.add(crCriteria.columnID);
            ++i;
        }
        return this.getCriteriaColumnDO(subModuleID, tempTableList);
    }
    
    public DataObject getCriteriaColumnDO(final Long subModuleID, List criteriaColumnIDs) {
        try {
            criteriaColumnIDs = ((criteriaColumnIDs == null) ? new ArrayList() : criteriaColumnIDs);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSubModule"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join(Table.getTable("CRSubModule"), Table.getTable("CRColumns"), new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
            selectQuery.addJoin(join);
            final Join inputCustomizerJoin = new Join(Table.getTable("CRColumns"), Table.getTable("CRColumnInputCustomizer"), new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1);
            selectQuery.addJoin(inputCustomizerJoin);
            Criteria subModuleCriteria = new Criteria(Column.getColumn("CRSubModule", "SUB_MODULE_ID"), (Object)subModuleID, 0);
            final Criteria columnIdCriteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)criteriaColumnIDs.toArray(), 8);
            subModuleCriteria = subModuleCriteria.and(columnIdCriteria);
            selectQuery.setCriteria(subModuleCriteria);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            CreateQuery.logger.log(Level.FINE, "SQL Query for Criteria " + actualSQL);
            return DataAccess.get(selectQuery);
        }
        catch (final Exception ex) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getCriModuleDO:", ex);
            return null;
        }
    }
    
    public DataObject getJoinDO(final Long subModuleID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRJoinRelation"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria c1 = new Criteria(Column.getColumn("CRJoinRelation", "SUB_MODULE_ID"), (Object)new Long(subModuleID), 0);
            selectQuery.setCriteria(c1);
            final Column nameCol = Column.getColumn("CRJoinRelation", "RELATION_ID");
            final SortColumn sortCol = new SortColumn(nameCol, true);
            selectQuery.addSortColumn(sortCol);
            final DataObject data = DataAccess.get(selectQuery);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            CreateQuery.logger.log(Level.FINE, "SQL Query for Join  " + actualSQL);
            return data;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getJoinDO:", e);
            return null;
        }
    }
    
    public DataObject getJoinCriteriaDO(final Long subModuleID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRJoinRelation"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join(Table.getTable("CRJoinRelation"), Table.getTable("CRJoinColumns"), new String[] { "RELATION_ID" }, new String[] { "RELATION_ID" }, 2);
            selectQuery.addJoin(join);
            final SortColumn sortCol = new SortColumn(Column.getColumn("CRJoinColumns", "JOIN_ORDER"), true);
            selectQuery.addSortColumn(sortCol);
            final Criteria c1 = new Criteria(Column.getColumn("CRJoinRelation", "SUB_MODULE_ID"), (Object)new Long(subModuleID), 0);
            selectQuery.setCriteria(c1);
            final DataObject data = DataAccess.get(selectQuery);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            CreateQuery.logger.log(Level.FINE, "SQL Query for joinCriteria " + actualSQL);
            return data;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getJoinCriteriaDO:", e);
            return null;
        }
    }
    
    public SelectQuery getCRSelectQuery(final DataObject moduleDO, final DataObject joinDO, final DataObject joinCriteriaDO, final List selectList, final DataObject criModuleDO) {
        SelectQuery selectQuery = null;
        final List columnList = new ArrayList();
        final List tableList = new ArrayList();
        final List selectQueryTableList = new ArrayList();
        try {
            if (moduleDO != null) {
                final Row firstRow = moduleDO.getRow("CRSubModule");
                final String baseTable = (firstRow.get("BASE_TABLE_ALIAS") + "").trim();
                selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTable));
                selectQueryTableList.add(baseTable);
                final Iterator iterator = moduleDO.getRows("CRColumns");
                int i = 0;
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    if (row != null) {
                        final String columnName = row.get("COLUMN_NAME_ALIAS") + "";
                        final String tableName = row.get("TABLE_NAME_ALIAS") + "";
                        columnList.add(Column.getColumn(tableName, columnName, tableName + "." + columnName));
                        if (!tableList.contains(tableName)) {
                            tableList.add(tableName);
                        }
                    }
                    ++i;
                }
                selectQuery.addSelectColumns(columnList);
                this.getCriteriaTables(tableList, criModuleDO);
                final Iterator tableIterator = tableList.iterator();
                int j = 0;
                while (tableIterator.hasNext()) {
                    final String table = tableIterator.next().trim();
                    if (!baseTable.equalsIgnoreCase(table) && joinDO != null) {
                        final Criteria criteria = new Criteria(Column.getColumn("CRJoinRelation", "CHILD_TABLE_ALIAS"), (Object)table, 0);
                        final Row joinRow = joinDO.getRow("CRJoinRelation", criteria);
                        if (joinRow != null) {
                            this.createJoinList(joinCriteriaDO, joinRow, selectQuery, table, selectQueryTableList);
                            CreateQuery.logger.log(Level.INFO, "Table List " + selectQuery.getTableList());
                        }
                    }
                    ++j;
                }
                final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
                CreateQuery.logger.log(Level.INFO, "Query is " + actualSQL);
            }
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getCRSelectQuery:", e);
        }
        return selectQuery;
    }
    
    private void getCriteriaTables(final List tableList, final DataObject criModuleDO) {
        try {
            final Iterator iterator = criModuleDO.getRows("CRColumns");
            int i = 0;
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (row != null) {
                    final String tableName = row.get("TABLE_NAME_ALIAS") + "";
                    if (!tableList.contains(tableName)) {
                        tableList.add(tableName);
                    }
                }
                ++i;
            }
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getCriteriaTables:", e);
        }
    }
    
    private int createJoinList(final DataObject joinCriteriaDO, final Row joinRow, final SelectQuery sQuery, final String tableName, final List selectQueryTableList) {
        try {
            final boolean isTable = this.checkQueryContainTablName(selectQueryTableList, tableName);
            final List joinList = new ArrayList();
            if (!isTable) {
                this.createJoinList(joinRow, joinCriteriaDO, joinList, selectQueryTableList, sQuery);
            }
            return 1000;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from createJoinList:", e);
            return 1001;
        }
    }
    
    public int createJoinList(final Row joinRow, final DataObject joinCriteriaDO, final List joinList, final List selectQueryTableList, final SelectQuery sQuery) {
        try {
            String childTableAlias = "";
            String parentTableAlias = "";
            String joinType = "";
            final String childColumnAlias = "";
            final String parentColumnAlias = "";
            String childTableName = "";
            String parentTableName = "";
            final String joinOperator = "";
            if (joinRow != null) {
                final Long relationID = (Long)joinRow.get("RELATION_ID");
                childTableAlias = (String)joinRow.get("CHILD_TABLE_ALIAS");
                parentTableAlias = (String)joinRow.get("PARENT_TABLE_ALIAS");
                childTableName = (String)joinRow.get("CHILD_TABLE_NAME");
                parentTableName = (String)joinRow.get("PARENT_TABLE_NAME");
                joinType = (String)joinRow.get("JOIN_TYPE");
                final Criteria criteria = new Criteria(Column.getColumn("CRJoinColumns", "RELATION_ID"), (Object)relationID, 0);
                final Iterator iterator = joinCriteriaDO.getRows("CRJoinColumns", criteria);
                final Criteria c1 = this.createCriteria(iterator, childTableAlias, parentTableAlias);
                final Join tempJoin = new Join(parentTableName, parentTableAlias, childTableName, childTableAlias, c1, CRConstantValues.getOperatorValue(joinType));
                joinList.add(tempJoin);
                if (selectQueryTableList.contains(parentTableAlias)) {
                    this.addJoin(joinList, sQuery, selectQueryTableList);
                }
                else {
                    final Criteria tempCri = new Criteria(Column.getColumn("CRJoinRelation", "CHILD_TABLE_ALIAS"), (Object)parentTableAlias, 0);
                    final Row row = joinCriteriaDO.getRow("CRJoinRelation", tempCri);
                    if (row != null) {
                        final String tempChildTableAlias = (String)joinRow.get("CHILD_TABLE_ALIAS");
                        final String tempParentTableAlias = (String)joinRow.get("PARENT_TABLE_ALIAS");
                        if (!tempChildTableAlias.equalsIgnoreCase(tempParentTableAlias) && !selectQueryTableList.contains(tempParentTableAlias)) {
                            this.createJoinList(row, joinCriteriaDO, joinList, selectQueryTableList, sQuery);
                        }
                        else {
                            this.addJoin(joinList, sQuery, selectQueryTableList);
                        }
                    }
                }
            }
            return 1000;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from createJoinList:", e);
            return 1001;
        }
    }
    
    public boolean checkQueryContainTablName(final List selectQueryTableList, final String tableName) {
        try {
            return selectQueryTableList.contains(tableName);
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from checkQueryContainTablName:", e);
            return false;
        }
    }
    
    private void addJoin(final List joinList, final SelectQuery sQuery, final List selectQueryTableList) {
        for (int i = joinList.size() - 1; i >= 0; --i) {
            final Join tempJoin = joinList.get(i);
            sQuery.addJoin(tempJoin);
            final String childTableAlias = tempJoin.getReferencedTableAlias();
            final String baseTableAlias = tempJoin.getBaseTableAlias();
            if (!selectQueryTableList.contains(childTableAlias)) {
                selectQueryTableList.add(childTableAlias);
            }
            if (!selectQueryTableList.contains(baseTableAlias)) {
                selectQueryTableList.add(baseTableAlias);
            }
        }
    }
    
    private Criteria createCriteria(final Iterator iteratorRows, final String childTableAlias, final String parentTableAlias) {
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
                if (compOp.equalsIgnoreCase(" AND ".trim())) {
                    criteria = criteria.and(c2);
                }
                else if (compOp.equalsIgnoreCase(" OR ".trim())) {
                    criteria = criteria.or(c2);
                }
            }
            else {
                criteria = c2;
            }
        }
        return criteria;
    }
    
    public SelectQuery addCriteria(final SelectQuery sQ, final List criteriaList, final DataObject criModuleDO) {
        try {
            String logicalOperatorValue = "";
            String operatorValue = "";
            String searchValue = "";
            Criteria storeCriteria = null;
            CreateQuery.logger.log(Level.INFO, "In create Query // Add criteria " + criteriaList);
            final Iterator criIterator = criteriaList.iterator();
            final List tempTableList = new ArrayList();
            int i = 0;
            while (criIterator.hasNext()) {
                final CRCriteria crCriteria = criIterator.next();
                final Long columnID = crCriteria.columnID;
                logicalOperatorValue = crCriteria.logicalOperatorValue;
                operatorValue = crCriteria.operatorValue;
                searchValue = crCriteria.searchValue;
                final boolean caseSensitive = crCriteria.caseSensitive;
                Object searchObj = searchValue;
                if (operatorValue.equalsIgnoreCase("is") && searchValue.equalsIgnoreCase("true")) {
                    searchObj = new String[] { "true", "1" };
                }
                else if (operatorValue.equalsIgnoreCase("is") && searchValue.equalsIgnoreCase("false")) {
                    searchObj = new String[] { "false", "0" };
                }
                if (criModuleDO != null) {
                    final Criteria criteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)columnID, 0);
                    final Row columnRow = criModuleDO.getRow("CRColumns", criteria);
                    final Criteria inputClassCriteria = new Criteria(Column.getColumn("CRColumnInputCustomizer", "COLUMN_ID"), (Object)columnID, 0);
                    final Row inputClassRow = criModuleDO.getRow("CRColumnInputCustomizer", criteria);
                    if (columnRow != null) {
                        final String tableNameAlias = (columnRow.get("TABLE_NAME_ALIAS") + "").trim();
                        final String columnNameAlias = (columnRow.get("COLUMN_NAME_ALIAS") + "").trim();
                        Criteria tempCriteria = null;
                        if (searchValue != null && (operatorValue.equalsIgnoreCase("is") || operatorValue.equalsIgnoreCase("between")) && this.convertDateTemplatetoDate(searchValue)) {
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.from_date, 4, caseSensitive);
                            final Criteria tempCriteria_to = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.to_date, 6, caseSensitive);
                            tempCriteria = tempCriteria.and(tempCriteria_to);
                        }
                        else if (searchValue != null && operatorValue.equalsIgnoreCase("Before n Days")) {
                            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(new Integer(searchValue), true);
                            this.from_date = dateRange.get("fromDate");
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.from_date, 6, caseSensitive);
                        }
                        else if (searchValue != null && operatorValue.equalsIgnoreCase("Last n Days")) {
                            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(new Integer(searchValue), true);
                            this.from_date = dateRange.get("fromDate");
                            this.to_date = dateRange.get("toDate");
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.from_date, 4, caseSensitive).and(new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.to_date, 6, caseSensitive));
                        }
                        else if (searchValue != null && operatorValue.equalsIgnoreCase("Next n Days")) {
                            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(new Integer(searchValue), false);
                            this.to_date = dateRange.get("toDate");
                            this.from_date = dateRange.get("fromDate");
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.from_date, 4, caseSensitive).and(new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.to_date, 6, caseSensitive));
                        }
                        else if (searchValue != null && operatorValue.equalsIgnoreCase("After n Days")) {
                            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(new Integer(searchValue), false);
                            this.to_date = dateRange.get("toDate");
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), (Object)this.to_date, 4, caseSensitive);
                        }
                        else {
                            if (inputClassRow != null) {
                                final String className = (inputClassRow.get("CLASS_NAME") + "").trim();
                                final ColumnInputCustomizer columnCustomizer = (ColumnInputCustomizer)Class.forName(className).newInstance();
                                searchObj = columnCustomizer.customize(searchObj);
                            }
                            searchObj = CRConstantValues.getSearchString(searchObj, operatorValue);
                            searchObj = DCViewFilterUtil.getInstance().alterSearchvalue(searchObj, operatorValue);
                            tempCriteria = new Criteria(Column.getColumn(tableNameAlias, columnNameAlias), searchObj, CRConstantValues.getOperatorValue(operatorValue), caseSensitive);
                        }
                        if (i > 0) {
                            logicalOperatorValue = CRConstantValues.getLogicalOperator(logicalOperatorValue);
                            if (logicalOperatorValue.equalsIgnoreCase("AND")) {
                                storeCriteria = storeCriteria.and(tempCriteria);
                            }
                            else if (logicalOperatorValue.equalsIgnoreCase("OR")) {
                                storeCriteria = storeCriteria.or(tempCriteria);
                            }
                        }
                        else {
                            storeCriteria = tempCriteria;
                        }
                    }
                }
                ++i;
            }
            sQ.setCriteria(storeCriteria);
            return sQ;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from addCriteria:", e);
            return null;
        }
    }
    
    private boolean convertDateTemplatetoDate(final String searchValue) throws ParseException {
        CreateQuery.logger.log(Level.INFO, "CreateQuery :Entered into convertDateTemplatetoDate" + searchValue);
        boolean flag = false;
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        if (searchValue.contains("From") && searchValue.contains("To")) {
            final String[] count = searchValue.split(" ");
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(count[2]);
            this.from_date = date.getTime();
            date = format.parse(count[5]);
            this.to_date = date.getTime();
            flag = true;
        }
        else {
            final String lowerCase = searchValue.toLowerCase();
            switch (lowerCase) {
                case "today": {
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    this.to_date = System.currentTimeMillis();
                    flag = true;
                    break;
                }
                case "yesterday": {
                    startDate.add(5, -1);
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    endDate.add(5, -1);
                    endDate = this.setEndTime(endDate);
                    this.to_date = endDate.getTimeInMillis();
                    flag = true;
                    break;
                }
                case "this week": {
                    startDate.set(7, Calendar.getInstance().getActualMinimum(7));
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    this.to_date = System.currentTimeMillis();
                    flag = true;
                    break;
                }
                case "current month": {
                    startDate.set(5, Calendar.getInstance().getActualMinimum(5));
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    this.to_date = System.currentTimeMillis();
                    flag = true;
                    break;
                }
                case "last week": {
                    startDate.set(7, Calendar.getInstance().getActualMinimum(7));
                    final int week_num = startDate.get(3);
                    startDate.set(6, (week_num - 2) * 7 - 2);
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    endDate.set(7, Calendar.getInstance().getActualMinimum(7));
                    endDate.set(3, week_num);
                    endDate.set(6, (week_num - 2) * 7 - 2 + 6);
                    endDate = this.setEndTime(endDate);
                    this.to_date = endDate.getTimeInMillis();
                    flag = true;
                    break;
                }
                case "last month": {
                    startDate.set(5, Calendar.getInstance().getActualMinimum(5));
                    final int month = startDate.get(2);
                    startDate.set(2, month - 1);
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    endDate.set(5, Calendar.getInstance().getActualMinimum(5));
                    endDate.set(5, 0);
                    endDate = this.setEndTime(endDate);
                    this.to_date = endDate.getTimeInMillis();
                    flag = true;
                    break;
                }
                case "current quarter": {
                    final int month = startDate.get(2);
                    final int quarter_start = this.checkQuarter(month);
                    startDate.set(2, quarter_start);
                    startDate.set(5, 1);
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    this.to_date = System.currentTimeMillis();
                    flag = true;
                    break;
                }
                case "last quarter": {
                    final int month = startDate.get(2);
                    final int quarter_start = this.checkQuarter(month);
                    startDate.set(2, quarter_start - 3);
                    startDate.set(5, 1);
                    startDate = this.setStartTime(startDate);
                    this.from_date = startDate.getTimeInMillis();
                    endDate.set(2, quarter_start);
                    endDate.set(5, 1);
                    endDate.add(5, -1);
                    endDate = this.setEndTime(endDate);
                    this.to_date = endDate.getTimeInMillis();
                    flag = true;
                    break;
                }
                default: {
                    final String val = searchValue;
                    final DateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        final Date date2 = format2.parse(val.trim());
                        startDate = this.setStartTime(dateToCalendar(date2));
                        this.from_date = startDate.getTimeInMillis();
                        endDate = this.setEndTime(dateToCalendar(date2));
                        this.to_date = endDate.getTimeInMillis();
                        flag = true;
                    }
                    catch (final Exception e) {
                        CreateQuery.logger.log(Level.WARNING, "Exception while Date Conversion", e);
                    }
                    break;
                }
            }
        }
        return flag;
    }
    
    public static Calendar dateToCalendar(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    
    private boolean convertNDaysToDate(final String searchValue) throws ParseException {
        CreateQuery.logger.log(Level.INFO, "CreateQuery :Entered into convertNDaystoDate" + searchValue);
        final int days = Integer.parseInt(searchValue);
        Calendar startDate = Calendar.getInstance();
        final Calendar endDate = Calendar.getInstance();
        startDate.add(5, -days);
        startDate = this.setStartTime(startDate);
        this.from_date = startDate.getTimeInMillis();
        return true;
    }
    
    private int checkQuarter(final int month) {
        int value = 0;
        final int quarter = month / 3;
        switch (quarter) {
            case 0: {
                value = 0;
                break;
            }
            case 1: {
                value = 3;
                break;
            }
            case 2: {
                value = 6;
                break;
            }
            case 3: {
                value = 9;
                break;
            }
        }
        return value;
    }
    
    private Calendar setStartTime(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }
    
    private Calendar setEndTime(final Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        return calendar;
    }
    
    private SelectQuery addSortColumn(final SelectQuery sQ, final String sortColumnID, final DataObject criModuleDO) {
        try {
            if (criModuleDO != null) {
                final Criteria criteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)sortColumnID, 0);
                final Row columnRow = criModuleDO.getRow("CRColumns", criteria);
                if (columnRow != null) {
                    final String tableNameAlias = (columnRow.get("TABLE_NAME_ALIAS") + "").trim();
                    final String columnNameAlias = (columnRow.get("COLUMN_NAME_ALIAS") + "").trim();
                    final SortColumn sortColumn = new SortColumn(Column.getColumn(tableNameAlias, columnNameAlias), true);
                    sQ.addSortColumn(sortColumn);
                }
            }
            return sQ;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from addCriteria:", e);
            return null;
        }
    }
    
    public int deleteCRViewDetails(final CustomReportDetails customReportDetails) {
        try {
            CreateQuery.logger.log(Level.INFO, "entering into deleteCRViewDetails method.....");
            final long userID = customReportDetails.userID;
            final String sessionID = customReportDetails.sessionID;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRViewDetailsInfo"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria c1 = new Criteria(Column.getColumn("CRViewDetailsInfo", "SESSIONID"), (Object)sessionID, 0);
            selectQuery.setCriteria(c1);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            CreateQuery.logger.log(Level.INFO, "Delete Query .....   " + actualSQL);
            final DataObject data = DataAccess.get(selectQuery);
            CreateQuery.logger.log(Level.INFO, "CRViewDetailsInfo .....   " + data);
            if (!data.isEmpty()) {
                final Row row = data.getRow("CRViewDetailsInfo");
                if (row != null) {
                    final String queryID = row.get("QUERYID") + "";
                    final String viewID = row.get("VIEWID") + "";
                    final String viewName = row.get("VIEWNAME") + "";
                    CreateQuery.logger.log(Level.INFO, "VIEW NAME is " + viewName);
                    final Criteria deleCri = new Criteria(Column.getColumn("SelectQuery", "QUERYID"), (Object)new Long(queryID), 0);
                    DataAccess.delete(deleCri);
                    final Criteria deleCriView = new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME_NO"), (Object)new Long(viewID), 0);
                    DataAccess.delete(deleCriView);
                    final Criteria deleCri2 = new Criteria(Column.getColumn("ACColumnConfigurationList", "NAME"), (Object)viewName, 0);
                    DataAccess.delete(deleCri2);
                    CreateQuery.logger.log(Level.INFO, "View Details are deleted .................");
                }
            }
            return 1102;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from deleteCRViewDetails:", e);
            return 1103;
        }
    }
    
    static {
        CreateQuery.logger = Logger.getLogger("CustomReportLogger");
    }
}
