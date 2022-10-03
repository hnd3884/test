package com.adventnet.ds.query.util;

import java.util.Comparator;
import java.util.Collections;
import org.json.JSONObject;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.LocaleColumn;
import java.util.Collection;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Set;
import com.adventnet.ds.query.CaseExpression;
import java.util.Map;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Arrays;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.text.SimpleDateFormat;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.xml.XmlDoUtil;
import java.util.StringTokenizer;
import java.math.BigDecimal;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.Operation;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.ds.query.Range;
import java.lang.reflect.Array;
import java.util.Iterator;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.zoho.conf.AppResources;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryToJsonConverter;
import com.adventnet.persistence.cache.CacheRepository;
import java.util.List;
import java.util.logging.Logger;

public class QueryUtil
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private static final int VALTYPE_EXPRESSION = -9999;
    private static final int VALTYPE_COLUMN = -9998;
    private static final int VALTYPE_CRITERIA = -9997;
    private static final int VALTYPE_RESERVED_PARAM = -9996;
    public static List<String> selectQueryTableList;
    private static final String[] DELIMITERS_FOR_IN;
    private static SASCachePlugin queryCache;
    private static CacheRepository cacheRepository;
    private static final int INT_ARRAY = 1;
    private static final int LONG_ARRAY = 2;
    private static final int STRING_ARRAY = 3;
    private static final int OBJECT_ARRAY = 4;
    private static final int FLOAT_ARRAY = 5;
    private static final int DOUBLE_ARRAY = 6;
    private static final String[] QUERY_ID;
    private static final String[] QUERYID_TABLEALIAS;
    private static final String[] QID_TALIAS_CINDEX;
    private static final String[] QID_TALIAS_SINDEX;
    private static final String[] QID_CID_RCID;
    private static SortedObjectComparator sortedObjectComparator;
    private static final QueryToJsonConverter CONVERTER;
    
    public static Row addSelectQueryIntoDO(final SelectQuery select, final DataObject dataObject) throws DataAccessException {
        return addSelectQueryWithQueryID(select, dataObject, null);
    }
    
    private static Row addSelectQueryWithQueryID(final SelectQuery select, final DataObject dataObject, final Row selectRow) throws DataAccessException {
        if (AppResources.getString("transform.criteria.value.on.persist", "false").equalsIgnoreCase("true")) {
            setDataType(select);
        }
        final List<Join> joins = select.getJoins();
        final Criteria criteria = select.getCriteria();
        final List<SortColumn> sortCols = select.getSortColumns();
        Row row = null;
        if (selectRow != null) {
            row = selectRow;
        }
        else {
            row = getSelectQueryRow(select);
            dataObject.addRow(row);
        }
        addSelectTables(dataObject, select.getTableList(), row);
        addSelectColRows(dataObject, select.getSelectColumns(), row);
        if (joins != null && joins.size() > 0) {
            addJoinRows(dataObject, joins, row);
        }
        if (sortCols != null && sortCols.size() > 0) {
            addSortColRows(dataObject, sortCols, row);
        }
        if (criteria != null) {
            addCriteriaRows(dataObject, criteria, row, CriteriaType.WHERE);
        }
        if (select.getGroupByClause() != null) {
            throw new UnsupportedOperationException("SelectQuery containing GroupByClause cannot be stored in the DB :: [" + select + "]");
        }
        return row;
    }
    
    private static void initQueryCache() {
        if (PersistenceInitializer.onSAS()) {
            QueryUtil.queryCache = SASCachePlugin.getSASCachePluginImpl();
        }
        else {
            QueryUtil.cacheRepository = CacheManager.getCacheRepository();
        }
    }
    
    public static SelectQuery getSelectQuery(final long queryID) throws DataAccessException {
        SelectQuery sq = null;
        try {
            if (PersistenceInitializer.onSAS()) {
                if (getQueryCache() != null) {
                    sq = (SelectQuery)QueryUtil.queryCache.get(queryID);
                    if (sq == null) {
                        sq = getQuery(queryID);
                        QueryUtil.queryCache.put(queryID, sq);
                    }
                    sq = (SelectQuery)sq.clone();
                }
            }
            else if (getCacheRepo() != null) {
                final String cacheKey = "SelectQueryObject_" + queryID;
                sq = (SelectQuery)QueryUtil.cacheRepository.getFromCache(cacheKey, null, true);
                if (sq == null) {
                    sq = getQuery(queryID);
                    QueryUtil.cacheRepository.addToCache(cacheKey, sq, QueryUtil.selectQueryTableList);
                }
                sq = (SelectQuery)sq.clone();
            }
            if (null == sq) {
                sq = getQuery(queryID);
            }
        }
        catch (final Exception exc) {
            throw new DataAccessException(exc);
        }
        return sq;
    }
    
    private static SelectQuery getQuery(final long queryID) throws DataAccessException {
        SelectQuery[] sqs = null;
        final DataObject dObj = getSelectQueryDO(queryID);
        sqs = getSelectQueryFromDO(dObj);
        return sqs[0];
    }
    
    public static DataObject getSelectQueryDO(final Criteria criteria) throws DataAccessException {
        final SelectQuery sQuery = getSelectQueryForSelectQueryRetrieval(criteria);
        return getSelectQueryDOFromDB(sQuery);
    }
    
    private static DataObject getSelectQueryDOFromDB(final SelectQuery select) throws DataAccessException {
        final DataObject sqDO = DataAccess.get(select);
        QueryUtil.OUT.log(Level.FINE, "sqDO :: {0}", sqDO);
        final List sqTableNames = sqDO.getTableNames();
        if (sqTableNames.contains("SelCol_Expression") || sqTableNames.contains("SortCol_Expression") || sqTableNames.contains("RelCri_Expression") || sqTableNames.contains("JoinRelCri_Expression")) {
            final List<String> tableNames = new ArrayList<String>();
            tableNames.add("SelCol_Expression");
            tableNames.add("RelCri_Expression");
            tableNames.add("JoinRelCri_Expression");
            tableNames.add("SortCol_Expression");
            final List<Long> exprIDs = new ArrayList<Long>();
            for (final String tableName : tableNames) {
                final Iterator iterator = sqDO.get(tableName, "EXPRESSION_ID");
                while (iterator.hasNext()) {
                    final Long exprID = iterator.next();
                    exprIDs.add(exprID);
                }
            }
            QueryUtil.OUT.log(Level.FINE, "exprIDs :: {0}", exprIDs);
            final SelectQuery exprSQ = new SelectQueryImpl(Table.getTable("Expression"));
            exprSQ.addSelectColumn(Column.getColumn(null, "*"));
            exprSQ.addJoin(new Join("Expression", "FunctionParams", new String[] { "EXPRESSION_ID" }, new String[] { "EXPRESSION_ID" }, 1));
            exprSQ.addJoin(new Join("Expression", "OperationParams", new String[] { "EXPRESSION_ID" }, new String[] { "EXPRESSION_ID" }, 1));
            exprSQ.addSortColumn(new SortColumn(Column.getColumn("FunctionParams", "PARAM_INDEX"), true));
            final Criteria c = new Criteria(Column.getColumn("Expression", "ROOT_EXPRESSION_ID"), exprIDs.toArray(), 8);
            exprSQ.setCriteria(c);
            final DataObject exprDO = DataAccess.get(exprSQ);
            QueryUtil.OUT.log(Level.FINE, "exprDO :: [{0}]", exprDO);
            sqDO.append(exprDO);
            QueryUtil.OUT.log(Level.FINE, "dataObject :: [{0}]", sqDO);
        }
        return sqDO;
    }
    
    private static SelectQuery getSelectQueryForSelectQueryRetrieval(final Criteria criteria) {
        final SelectQuery sq = getSelectQueryObject();
        final Criteria cri = getRelationalCriteria(criteria);
        sq.setCriteria(cri);
        return sq;
    }
    
    private static Criteria getRelationalCriteria(final Criteria criteria) {
        Criteria leftCriteria = criteria.getLeftCriteria();
        Criteria rightCriteria = criteria.getRightCriteria();
        Object value = criteria.getValue();
        final int comparator = criteria.getComparator();
        switch (comparator) {
            case 8:
            case 9: {
                value = getValuesStringForIN(comparator, value);
                break;
            }
            case 14:
            case 15: {
                value = getValuesStringForBetween(value);
                break;
            }
        }
        if (leftCriteria == null && rightCriteria == null) {
            final Column column = criteria.getColumn();
            final String tableAlias = column.getTableAlias();
            final String columnName = column.getColumnName();
            final boolean caseSensitive = criteria.isCaseSensitive();
            Criteria relCri = new Criteria(Column.getColumn("RelationalCriteria", "TABLEALIAS"), tableAlias, 0);
            relCri = relCri.and(Column.getColumn("RelationalCriteria", "COLUMNNAME"), columnName, 0);
            relCri = relCri.and(Column.getColumn("RelationalCriteria", "COMPARATOR"), comparator, 0);
            relCri = relCri.and(Column.getColumn("RelationalCriteria", "VALUE"), "*" + value + "*", 2);
            relCri = relCri.and(Column.getColumn("RelationalCriteria", "CASESENSITIVE"), caseSensitive, 0);
            return relCri;
        }
        final String operator = criteria.getOperator();
        if (leftCriteria != null) {
            leftCriteria = getRelationalCriteria(leftCriteria);
        }
        if (rightCriteria != null) {
            rightCriteria = getRelationalCriteria(rightCriteria);
        }
        if (" AND ".equals(operator)) {
            return leftCriteria.and(rightCriteria);
        }
        return leftCriteria.or(rightCriteria);
    }
    
    private static Object getValuesStringForBetween(Object value) {
        final String pattern = " :&: ";
        if (value instanceof Object[]) {
            value = getString((Object[])value, pattern);
        }
        else if (value instanceof int[]) {
            value = getString((int[])value, pattern);
        }
        else if (value instanceof long[]) {
            value = getString((long[])value, pattern);
        }
        else if (value instanceof float[]) {
            value = getString((float[])value, pattern);
        }
        else if (value instanceof double[]) {
            value = getString((double[])value, pattern);
        }
        return value;
    }
    
    private static String getValuesStringForIN(final int comparator, final Object value) {
        final int arrayType = getArrayType(value);
        int arraylength = -1;
        try {
            arraylength = Array.getLength(value);
        }
        catch (final IllegalArgumentException excp) {
            throw new IllegalArgumentException("Value for IN/NOT_IN comparator is not an array");
        }
        String s = null;
        int delimiter_type = 0;
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < arraylength; ++index) {
            s = getString(value, index, arrayType);
            if (arrayType == 3 && s.indexOf(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]) >= 0) {
                final int new_delimiter_type = delimiter_type + 1;
                sb.replace(0, sb.length(), sb.toString().replaceAll(QueryUtil.DELIMITERS_FOR_IN[delimiter_type], QueryUtil.DELIMITERS_FOR_IN[new_delimiter_type]));
                delimiter_type = new_delimiter_type;
            }
            sb.append(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]);
            sb.append(s);
            if (sb.length() == 290) {
                return sb.toString();
            }
        }
        return sb.toString();
    }
    
    private static int getArrayType(final Object value) {
        if (value instanceof String[]) {
            return 3;
        }
        if (value instanceof int[]) {
            return 1;
        }
        if (value instanceof long[]) {
            return 2;
        }
        if (value instanceof float[]) {
            return 5;
        }
        if (value instanceof double[]) {
            return 6;
        }
        return 4;
    }
    
    private static SelectQuery getSelectQueryObject() {
        final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("SelectQuery"));
        sQuery.addSelectColumn(Column.getColumn(null, "*"));
        sQuery.addJoin(new Join("SelectQuery", "SelectTable", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 2));
        sQuery.addJoin(new Join("SelectTable", "SelectColumn", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sQuery.addJoin(new Join(Table.getTable("SelectColumn"), Table.getTable("SelCol_Expression"), QueryUtil.QID_TALIAS_CINDEX, QueryUtil.QID_TALIAS_CINDEX, 1));
        sQuery.addJoin(new Join("SelectTable", "JoinTable", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sQuery.addJoin(new Join("JoinTable", "JoinColumns", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sQuery.addJoin(new Join("SelectQuery", "Criteria", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 1));
        sQuery.addJoin(new Join("Criteria", "RelationalCriteria", new String[] { "QUERYID", "CRITERIAID" }, new String[] { "QUERYID", "CRITERIAID" }, 1));
        sQuery.addJoin(new Join(Table.getTable("RelationalCriteria"), Table.getTable("RelCri_Expression"), QueryUtil.QID_CID_RCID, QueryUtil.QID_CID_RCID, 1));
        sQuery.addJoin(new Join("SelectTable", "SortColumn", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sQuery.addJoin(new Join(Table.getTable("SortColumn"), Table.getTable("SortCol_Expression"), QueryUtil.QID_TALIAS_SINDEX, QueryUtil.QID_TALIAS_SINDEX, 1));
        sQuery.addJoin(new Join("SelectQuery", "JoinCriteria", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 1));
        sQuery.addJoin(new Join("JoinCriteria", "JoinRelCriteria", new String[] { "QUERYID", "CRITERIAID" }, new String[] { "QUERYID", "CRITERIAID" }, 1));
        sQuery.addJoin(new Join(Table.getTable("JoinRelCriteria"), Table.getTable("JoinRelCri_Expression"), QueryUtil.QID_CID_RCID, QueryUtil.QID_CID_RCID, 1));
        sQuery.addSortColumn(new SortColumn(Column.getColumn("SelectColumn", "COLUMNINDEX"), true));
        sQuery.addSortColumn(new SortColumn(Column.getColumn("SortColumn", "SORTINDEX"), true));
        sQuery.addSortColumn(new SortColumn(Column.getColumn("RelationalCriteria", "RELATIONALCRITERIAID"), true));
        sQuery.addSortColumn(new SortColumn(Column.getColumn("JoinRelCriteria", "RELATIONALCRITERIAID"), true));
        return sQuery;
    }
    
    public static DataObject getSelectQueryDO(final long queryID) throws DataAccessException {
        final Long[] queryIDs = { queryID };
        return getSelectQueryDO(queryIDs);
    }
    
    public static DataObject getSelectQueryDO(final Long[] queryIDs) throws DataAccessException {
        final SelectQuery select = getSelectQueryForSelectQueryRetrieval(queryIDs);
        return getSelectQueryDOFromDB(select);
    }
    
    public static SelectQuery[] getSelectQueryFromDO(final DataObject dataObject) throws DataAccessException {
        final List<SelectQuery> selectQueries = new ArrayList<SelectQuery>();
        final Iterator itr = dataObject.getRows("SelectQuery");
        while (itr.hasNext()) {
            SelectQuery select = null;
            final List joins = new ArrayList();
            final List tabList = new ArrayList();
            final List tabRowsList = new ArrayList();
            List selectCols = new ArrayList();
            List sortCols = new ArrayList();
            final Row selectRow = itr.next();
            Range range = null;
            if (selectRow.get("STARTINDEX") != null && selectRow.get("NUMOFOBJECTS") != null) {
                final int startIndex = (int)selectRow.get("STARTINDEX");
                final int numOfObjects = (int)selectRow.get("NUMOFOBJECTS");
                if (startIndex != 0 || numOfObjects >= 1) {
                    range = new Range(startIndex, numOfObjects);
                }
            }
            final Iterator selectTabItr = dataObject.getRows("SelectTable", selectRow);
            while (selectTabItr.hasNext()) {
                final Row selectTabRow = selectTabItr.next();
                final String tabAlias = (String)selectTabRow.get(2);
                if (tabAlias.equals("<<EXPRESSION>>")) {
                    continue;
                }
                tabRowsList.add(selectTabRow);
                final Table table = Table.getTable((String)selectTabRow.get("TABLENAME"), (String)selectTabRow.get("TABLEALIAS"));
                if (!dataObject.containsTable("JoinTable")) {
                    if (select == null) {
                        select = new SelectQueryImpl(table);
                        if (range != null) {
                            select.setRange(range);
                        }
                    }
                    tabList.add(table);
                }
                else {
                    final Iterator tempItr = dataObject.getRows("JoinTable", selectTabRow);
                    if (!tempItr.hasNext()) {
                        select = new SelectQueryImpl(table);
                        select.setRange(range);
                    }
                    tabList.add(table);
                }
            }
            for (int tabsSize = tabRowsList.size(), i = 0; i < tabsSize; ++i) {
                final Row selectTab = tabRowsList.get(i);
                final Table refTable = getTable((String)selectTab.get("TABLEALIAS"), tabList);
                if (dataObject.containsTable("JoinTable")) {
                    final Iterator joinItr = dataObject.getRows("JoinTable", selectTab);
                    while (joinItr.hasNext()) {
                        final Row joinTabRow = joinItr.next();
                        final Table baseTable = getTable((String)joinTabRow.get("REFERENCEDTABLE"), tabList);
                        final Join join = formJoin(dataObject, joinTabRow, baseTable, refTable, tabList);
                        joins.add(join);
                    }
                }
            }
            iterateSelectColumns(dataObject, selectCols);
            iterateSortColumns(dataObject, sortCols);
            selectCols = SortedObject.getSortedListOfObjects(selectCols);
            sortCols = SortedObject.getSortedListOfObjects(sortCols);
            addJoins(select, select.getTableList().get(0), joins);
            select.addSelectColumns(selectCols);
            addCriteria(dataObject, select, selectRow, tabList);
            final Criteria crit = select.getCriteria();
            if (crit != null) {
                setTypeForCriteria(crit, tabList);
            }
            select.addSortColumns(sortCols);
            selectQueries.add(select);
        }
        if (selectQueries.size() == 0) {
            throw new DataAccessException("No SelectQuery present for this queryID");
        }
        return selectQueries.toArray(new SelectQuery[selectQueries.size()]);
    }
    
    public static void updateDOWithSelectQuery(final DataObject dataObject, final SelectQuery select, final long queryID) throws Exception {
        final Long objQueryID = new Long(queryID);
        final Row row = new Row("SelectQuery");
        row.set(1, new Long(queryID));
        final Row selectQueryRow = dataObject.getFirstRow("SelectQuery", row);
        final List tableNames = PersonalityConfigurationUtil.getConstituentTables("SelectQuery");
        final DataObject sqDO = dataObject.getDataObject(tableNames, row);
        final Row selectRow = getSelectQueryRow(select);
        selectRow.set("QUERYID", objQueryID);
        final DataObject dbo = DataAccess.constructDataObject();
        final Row newRow = addSelectQueryWithQueryID(select, dbo, selectRow);
        dbo.addRow(newRow);
        final DataObject diffDo = sqDO.diff(dbo);
        dataObject.merge(diffDo);
        if (getQueryCache() != null) {
            QueryUtil.queryCache.invalidate(queryID);
        }
        if (getCacheRepo() != null) {
            final String cacheKey = "SelectQueryObject_" + queryID;
            QueryUtil.cacheRepository.removeCachedData(cacheKey);
        }
    }
    
    private static void deleteFromTable(final DataObject dataObject, final Row condition) throws DataAccessException {
        final Iterator itr = dataObject.getRows("SelectTable", condition);
        while (itr.hasNext()) {
            final Row row = itr.next();
            itr.remove();
        }
    }
    
    private static void deleteFromJoinTable(final DataObject dataObject, final Row condition) throws DataAccessException {
        final Iterator itr = dataObject.getRows("JoinTable", condition);
        while (itr.hasNext()) {
            final Row row = itr.next();
            deleteFromJoinColumns(dataObject, row);
        }
        dataObject.deleteRows("JoinTable", condition);
    }
    
    private static void deleteFromJoinColumns(final DataObject dataObject, final Row condition) throws DataAccessException {
        dataObject.deleteRows("JoinColumns", condition);
    }
    
    private static void deleteFromJoinCriteria(final DataObject dataObject, final Row condition) throws DataAccessException {
        final Iterator itr = dataObject.getRows("JoinCriteria", condition);
        while (itr.hasNext()) {
            final Row row = itr.next();
            dataObject.deleteRows("JoinRelCriteria", row);
        }
        dataObject.deleteRows("JoinCriteria", condition);
    }
    
    private static void deleteFromSelectColumn(final DataObject dataObject, final Row condition) throws DataAccessException {
        dataObject.deleteRows("SelectColumn", condition);
    }
    
    private static void deleteFromSortColumn(final DataObject dataObject, final Row condition) throws DataAccessException {
        dataObject.deleteRows("SortColumn", condition);
    }
    
    private static void deleteFromCriteria(final DataObject dataObject, final Row condition) throws DataAccessException {
        final Iterator itr = dataObject.getRows("Criteria", condition);
        while (itr.hasNext()) {
            final Row row = itr.next();
            deleteFromRelCriteria(dataObject, row);
        }
        dataObject.deleteRows("Criteria", condition);
    }
    
    private static void deleteFromRelCriteria(final DataObject dataObject, final Row condition) throws DataAccessException {
        dataObject.deleteRows("RelationalCriteria", condition);
    }
    
    private static void iterateSelectColumns(final DataObject sqDO, final List selectCols) throws DataAccessException {
        final Iterator<Row> selectColItr = sqDO.getRows("SelectColumn");
        while (selectColItr.hasNext()) {
            final Row selectRow = selectColItr.next();
            final Integer indexObj = (Integer)selectRow.get(2);
            int index = 0;
            if (indexObj != null) {
                index = indexObj;
            }
            final Row selColExprRow = sqDO.getRow("SelCol_Expression", selectRow);
            Column col = null;
            if (selColExprRow != null) {
                col = getColumn(sqDO, selColExprRow);
                final String colAlias = (String)selectRow.get(3);
                if (colAlias != null) {
                    col.setColumnAlias(colAlias);
                }
            }
            else {
                col = Column.getColumn((String)selectRow.get(5), (String)selectRow.get("COLUMNNAME"), (String)selectRow.get(3));
            }
            selectCols.add(new SortedObject(index, col));
        }
    }
    
    private static Column getColumn(final DataObject sqDO, final Row rel_ExprRow) throws DataAccessException {
        QueryUtil.OUT.log(Level.FINE, "Entering :: [{0}]", rel_ExprRow);
        final Join join = new Join("Expression", rel_ExprRow.getTableName(), new String[] { "EXPRESSION_ID" }, new String[] { "EXPRESSION_ID" }, 2);
        final Row exprRow = sqDO.getRow("Expression", rel_ExprRow, join);
        QueryUtil.OUT.log(Level.FINE, "exprRow :: [{0}]", exprRow);
        final Column retCol = getExpression(exprRow, sqDO);
        if (retCol == null) {
            throw new IllegalArgumentException("No expression defined for rel_ExprRow :: " + rel_ExprRow);
        }
        return retCol;
    }
    
    private static Column getExpression(final Row exprRow, final DataObject sqDO) throws DataAccessException {
        final Row operRow = sqDO.getRow("OperationParams", exprRow);
        Column c = null;
        if (operRow != null) {
            c = getOperation(exprRow, sqDO);
        }
        else {
            c = getFunction(exprRow, sqDO);
        }
        c.setType((int)exprRow.get("VALUE_TYPE"));
        return c;
    }
    
    private static Column getFunction(final Row exprRow, final DataObject sqDO) throws DataAccessException {
        final String funcName = (String)exprRow.get(3);
        final Iterator<Row> iterator = sqDO.getRows("FunctionParams", exprRow);
        if (iterator.hasNext()) {
            final List<Object> args = new ArrayList<Object>();
            while (iterator.hasNext()) {
                final Row fpRow = iterator.next();
                final int valueType = (int)fpRow.get(4);
                args.add(getValueAsObject(fpRow, "PARAM_VALUE", valueType, sqDO));
            }
            return Column.createFunction(funcName, args.toArray());
        }
        return Column.createFunction(funcName, new Object[0]);
    }
    
    private static Column getOperation(final Row exprRow, final DataObject sqDO) throws DataAccessException {
        final Operation.operationType operationType = Operation.getOperationFor((String)exprRow.get(3));
        final Row operParamsRow = sqDO.getRow("OperationParams", exprRow);
        final int lValueType = (int)operParamsRow.get(3);
        final int rValueType = (int)operParamsRow.get(5);
        final Column lValue = (Column)getValueAsObject(operParamsRow, "LVALUE", lValueType, sqDO);
        final Object rValue = getValueAsObject(operParamsRow, "RVALUE", rValueType, sqDO);
        return Column.createOperation(operationType, lValue, rValue);
    }
    
    private static Object getValueAsObject(final Row row, final String valColName, final int type, final DataObject sqDO) throws DataAccessException {
        final String value = (String)row.get(valColName);
        switch (type) {
            case 4: {
                return Integer.parseInt(value);
            }
            case -5: {
                return Long.parseLong(value);
            }
            case 1: {
                return value;
            }
            case -9999: {
                final Row r = new Row("Expression");
                r.set(1, Long.parseLong(value));
                return getExpression(sqDO.getRow("Expression", r), sqDO);
            }
            case -9998: {
                final String[] s = value.split("\\.");
                return Column.getColumn(s[0], s[1], s[2]);
            }
            case -9996: {
                return new Function.ReservedParameter(value);
            }
            case -9997: {
                final String lr = sqDO.getRow("Criteria").get("LOGICALREPRESENTATION").toString();
                final char[] delimArray = new char[lr.length()];
                int k = 0;
                for (int i = 0; i < lr.length(); ++i) {
                    if (lr.charAt(i) == '&' || lr.charAt(i) == '|') {
                        delimArray[k++] = lr.charAt(i);
                    }
                }
                k = 0;
                Object obj = null;
                final Iterator itr = sqDO.getRows("RelationalCriteria");
                Row rcRow = itr.next();
                String criValue = rcRow.get("VALUE").toString();
                String tableName = rcRow.get("TABLEALIAS").toString();
                String colName = rcRow.get("COLUMNNAME").toString();
                int comparator = (int)rcRow.get("COMPARATOR");
                String dataType = (String)rcRow.get("VALUE_DATATYPE");
                try {
                    obj = getObject(criValue, comparator, dataType);
                }
                catch (final Exception ex) {}
                Criteria cri = new Criteria(Column.getColumn(tableName, colName), obj, comparator);
                while (itr.hasNext()) {
                    rcRow = itr.next();
                    criValue = rcRow.get("VALUE").toString();
                    tableName = rcRow.get("TABLEALIAS").toString();
                    colName = rcRow.get("COLUMNNAME").toString();
                    comparator = (int)rcRow.get("COMPARATOR");
                    dataType = (String)rcRow.get("VALUE_DATATYPE");
                    try {
                        obj = getObject(criValue, comparator, dataType);
                        if (delimArray[k++] == '&') {
                            cri = cri.and(Column.getColumn(tableName, colName), obj, comparator);
                        }
                        else {
                            cri = cri.or(Column.getColumn(tableName, colName), obj, comparator);
                        }
                    }
                    catch (final Exception ex2) {}
                }
                return cri;
            }
            case 16: {
                return Boolean.parseBoolean(value);
            }
            case 3: {
                return new BigDecimal(value);
            }
            case 6: {
                return Float.parseFloat(value);
            }
            case 8: {
                return Double.parseDouble(value);
            }
            case 91:
            case 92:
            case 93: {
                return null;
            }
            default: {
                throw new IllegalArgumentException("Unknown type received [" + type + "] for getValueAsObject method for column [" + valColName + "]");
            }
        }
    }
    
    private static void iterateSortColumns(final DataObject sqDO, final List sortCols) throws DataAccessException {
        final Iterator sortColItr = sqDO.getRows("SortColumn");
        while (sortColItr.hasNext()) {
            final Row sortRow = sortColItr.next();
            final Row sortToExpr_RelRow = sqDO.getRow("SortCol_Expression", sortRow);
            final String sortColumnAlias = (String)sortRow.get(5);
            final String sortColumnName = (String)sortRow.get(4);
            final String sortTableAlias = (String)sortRow.get(2);
            final boolean sortOrder = (boolean)sortRow.get(6);
            final Boolean isNullsFirst = (Boolean)sortRow.get(7);
            Column col = null;
            if (sortToExpr_RelRow == null) {
                col = Column.getColumn(sortTableAlias, sortColumnName, sortColumnAlias);
            }
            else {
                col = getColumn(sqDO, sortToExpr_RelRow);
                col.setColumnAlias(sortColumnAlias);
            }
            final SortColumn sortcol = new SortColumn(col, sortOrder, false, isNullsFirst);
            final Integer indexObj = (Integer)sortRow.get(3);
            int index = 0;
            if (indexObj != null) {
                index = indexObj;
            }
            sortCols.add(new SortedObject(index, sortcol));
        }
    }
    
    private static void addCriteria(final DataObject dataObject, final SelectQuery select, final Row selectRow, final List<Table> tableList) throws DataAccessException {
        final Iterator critItr = dataObject.getRows("Criteria", selectRow);
        while (critItr.hasNext()) {
            final Row critRow = critItr.next();
            if (critRow.get("IS_EXPRESSION_PARAM")) {
                continue;
            }
            final Criteria crit = formCriteria(dataObject, critRow, dataObject.getRows("RelationalCriteria", critRow), tableList);
            if (crit == null) {
                continue;
            }
            select.setCriteria(crit);
        }
    }
    
    public static Criteria formCriteria(final DataObject criteriaDO, final List<Table> tableList) throws DataAccessException {
        final Row critRow = criteriaDO.getRow("Criteria");
        final Iterator relCritItr = criteriaDO.getRows("RelationalCriteria", critRow);
        return formCriteria(critRow, relCritItr, tableList);
    }
    
    public static Criteria formCriteria(final Row critRow, final Iterator relCritItr, final List<Table> tableList) throws DataAccessException {
        return formCriteria(null, critRow, relCritItr, tableList);
    }
    
    public static Criteria formCriteria(final DataObject selectQueryDO, final Row critRow, final Iterator relCritItr, final List<Table> tableList) throws DataAccessException {
        final String logicalRep = (String)critRow.get("LOGICALREPRESENTATION");
        if (logicalRep == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(logicalRep, "()&|", false);
        final long[] mainRelCriIDs = new long[tok.countTokens()];
        int index = 0;
        while (tok.hasMoreTokens()) {
            mainRelCriIDs[index++] = new Long(tok.nextToken());
        }
        tok = new StringTokenizer(logicalRep, "()&|", true);
        final List relCritList = new ArrayList();
        while (relCritItr.hasNext()) {
            final Row relCritRow = relCritItr.next();
            relCritList.add(relCritRow);
        }
        return formCriteria(selectQueryDO, tok, relCritList, mainRelCriIDs);
    }
    
    private static Criteria formCriteria(final DataObject selectQueryDO, final StringTokenizer tok, final List relCritList, final long[] mainRelCriIDs) throws DataAccessException {
        if (!tok.hasMoreTokens()) {
            return null;
        }
        final String firstTok = tok.nextToken();
        if (!firstTok.equals("(")) {
            return getCriteria(selectQueryDO, firstTok, relCritList, mainRelCriIDs);
        }
        final Criteria leftCriteria = formCriteria(selectQueryDO, tok, relCritList, mainRelCriIDs);
        final String operator = tok.nextToken();
        final Criteria rightCriteria = formCriteria(selectQueryDO, tok, relCritList, mainRelCriIDs);
        final String lastTok = tok.nextToken();
        if (operator.equals("&")) {
            return leftCriteria.and(rightCriteria);
        }
        return leftCriteria.or(rightCriteria);
    }
    
    public static Object getObject(Object value, final int comparator, final String dataType) throws Exception {
        if (value == null) {
            return null;
        }
        if (value instanceof Column) {
            return value;
        }
        if (dataType != null && dataType.equals("DERIVED_COLUMN")) {
            return value;
        }
        final String strValue = (String)value;
        if (strValue.matches(".*\\$\\{.+}.*")) {
            return value;
        }
        Object origValues = null;
        if (comparator == 8 || comparator == 9) {
            String delimiter = null;
            if (value.toString().startsWith("(") && value.toString().endsWith(")")) {
                String str = (String)value;
                str = str.substring(1, str.length() - 1);
                final StringTokenizer tok = new StringTokenizer(str, ",");
                final String[] vals = new String[tok.countTokens()];
                int i = 0;
                while (tok.hasMoreTokens()) {
                    vals[i++] = tok.nextToken();
                }
                value = vals;
            }
            else if (value.toString().startsWith(QueryUtil.DELIMITERS_FOR_IN[0]) || value.toString().startsWith(QueryUtil.DELIMITERS_FOR_IN[1]) || value.toString().startsWith(QueryUtil.DELIMITERS_FOR_IN[2]) || value.toString().startsWith(QueryUtil.DELIMITERS_FOR_IN[3]) || value.toString().startsWith(QueryUtil.DELIMITERS_FOR_IN[4])) {
                delimiter = value.toString().substring(0, 4);
                value = value.toString().substring(4).split(delimiter);
            }
            else if (value.toString().startsWith("::")) {
                delimiter = value.toString().substring(0, 2);
                value = value.toString().substring(2).split(delimiter);
            }
            else {
                value = value.toString().split("::");
            }
            origValues = getArray((String[])value, dataType);
        }
        else if (comparator == 14 || comparator == 15) {
            value = value.toString().split(" :&: ");
            origValues = getArray((String[])value, dataType);
        }
        else {
            if (dataType != null && dataType.equals("COLUMN")) {
                final String newValue = (String)value;
                final int index = newValue.indexOf(46);
                final Column column = new Column(newValue.substring(0, index), newValue.substring(index + 1));
                return column;
            }
            if (dataType == null) {
                return value;
            }
            try {
                origValues = value;
                origValues = XmlDoUtil.convert((String)value, dataType);
            }
            catch (final Exception ex) {}
        }
        return origValues;
    }
    
    public static Object getArray(final String[] values, final String dataType) throws Exception {
        if (dataType == null) {
            return values;
        }
        if (dataType.equals("COLUMN")) {
            final Column[] temp = new Column[values.length];
            for (int i = 0; i < values.length; ++i) {
                final String newValue = values[i];
                final int index = newValue.indexOf(46);
                final Column column = Column.getColumn(newValue.substring(0, index), newValue.substring(index + 1));
                temp[i] = column;
            }
            return temp;
        }
        if (!AppResources.getString("transform.criteria.value.on.persist", "false").equalsIgnoreCase("false")) {
            return MetaDataUtil.convertArray(values, dataType);
        }
        if (dataType.equals("BIGINT")) {
            checkNullExistsForPrimitive(values, dataType);
            final long[] temp2 = new long[values.length];
            for (int i = 0; i < values.length; ++i) {
                temp2[i] = new Long(values[i]);
            }
            return temp2;
        }
        if (dataType.equals("INTEGER") || dataType.equals("TINYINT")) {
            checkNullExistsForPrimitive(values, dataType);
            final int[] temp3 = new int[values.length];
            for (int i = 0; i < values.length; ++i) {
                temp3[i] = new Integer(values[i]);
            }
            return temp3;
        }
        if (dataType.equals("FLOAT")) {
            checkNullExistsForPrimitive(values, dataType);
            final float[] temp4 = new float[values.length];
            for (int i = 0; i < values.length; ++i) {
                temp4[i] = new Float(values[i]);
            }
            return temp4;
        }
        if (dataType.equals("DOUBLE")) {
            checkNullExistsForPrimitive(values, dataType);
            final double[] temp5 = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                temp5[i] = new Double(values[i]);
            }
            return temp5;
        }
        if (dataType.equals("DECIMAL")) {
            final BigDecimal[] temp6 = new BigDecimal[values.length];
            for (int i = 0; i < values.length; ++i) {
                temp6[i] = ((values[i] == null) ? null : new BigDecimal(values[i]));
            }
            return temp6;
        }
        if (dataType.equals("DATE")) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final Date[] temp7 = new Date[values.length];
            for (int j = 0; j < values.length; ++j) {
                temp7[j] = ((values[j] == null) ? null : new Date(sdf.parse(values[j]).getTime()));
            }
            return temp7;
        }
        if (dataType.equals("TIME")) {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Time[] temp8 = new Time[values.length];
            for (int j = 0; j < values.length; ++j) {
                temp8[j] = ((values[j] == null) ? null : new Time(sdf.parse(values[j]).getTime()));
            }
            return temp8;
        }
        if (dataType.equals("TIMESTAMP") || dataType.equals("DATETIME")) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            final Timestamp[] temp9 = new Timestamp[values.length];
            for (int j = 0; j < values.length; ++j) {
                temp9[j] = ((values[j] == null) ? null : new Timestamp(sdf.parse(values[j]).getTime()));
            }
            return temp9;
        }
        return values;
    }
    
    private static void checkNullExistsForPrimitive(final String[] values, final String dataType) {
        for (final String value : values) {
            if (value == null) {
                QueryUtil.OUT.log(Level.WARNING, "Cant provide null as value for primitive datatype {0}. Set transform.criteria.value.on.persist as [true]", dataType);
                throw new IllegalArgumentException("Cant provide null as value for primitive datatype");
            }
        }
    }
    
    private static Criteria getCriteria(final DataObject selectQueryDO, final String singleCriteria, final List relCritList, final long[] mainRelCriIDs) throws DataAccessException {
        final long rcID = Long.parseLong(singleCriteria);
        final StringBuilder sb = new StringBuilder();
        for (int relCritSize = relCritList.size(), i = 0; i < relCritSize; ++i) {
            Row relCrit = relCritList.get((int)rcID - 1);
            String tableAlias = null;
            String columnName = null;
            if ((long)relCrit.get("RELATIONALCRITERIAID") == rcID) {
                Column criCol = null;
                final String relCri_ExprTableName = relCrit.getTableName().equals("RelationalCriteria") ? "RelCri_Expression" : "JoinRelCri_Expression";
                final Row relCri_ExprRow = selectQueryDO.getRow(relCri_ExprTableName, relCrit);
                if (relCri_ExprRow != null) {
                    criCol = getColumn(selectQueryDO, relCri_ExprRow);
                }
                else {
                    tableAlias = (String)relCrit.get("TABLEALIAS");
                    columnName = (String)relCrit.get("COLUMNNAME");
                    criCol = Column.getColumn(tableAlias, columnName);
                }
                final int comparator = (int)relCrit.get("COMPARATOR");
                Object value = relCrit.get("VALUE");
                if (null != value) {
                    final String strValue = value.toString();
                    if (strValue.startsWith("column:{") && strValue.endsWith("}")) {
                        final String tableName = strValue.substring(strValue.indexOf(123) + 1, strValue.indexOf(46));
                        columnName = strValue.substring(strValue.indexOf(46) + 1, strValue.indexOf(125));
                        final Column colValue = (Column)(value = Column.getColumn(tableName, columnName));
                    }
                }
                String dataType = (String)relCrit.get("VALUE_DATATYPE");
                final Row selTableRow = selectQueryDO.getRow("SelectTable", relCrit);
                final String tableName2 = (String)selTableRow.get("TABLENAME");
                if (criCol.getType() == 1111 && dataType != null && !dataType.equals("DERIVED_COLUMN") && !(value instanceof Column) && !dataType.equalsIgnoreCase("Column")) {
                    criCol.setType(MetaDataUtil.getJavaSQLType(dataType.trim()));
                }
                else if (dataType == null && value != null && !value.toString().matches(".*\\$\\{.+}.*")) {
                    try {
                        if (selectQueryDO == null) {
                            return new Criteria(Column.getColumn(tableAlias, columnName), value, comparator, (boolean)relCrit.get("CASESENSITIVE"));
                        }
                        if (selTableRow == null && relCri_ExprRow == null) {
                            if (comparator == 8 || comparator == 9 || comparator == 14 || comparator == 15) {
                                throw new DataAccessException("Not found suitable SelectTable Row, while converting criteriaValue to corresponding dataType for criteria using IN & BETWEEN operators.");
                            }
                            return new Criteria(criCol, value, comparator, (boolean)relCrit.get("CASESENSITIVE"));
                        }
                        else {
                            if (selTableRow == null) {
                                return new Criteria(criCol, value, comparator, (boolean)relCrit.get("CASESENSITIVE"));
                            }
                            ColumnDefinition colDef = null;
                            try {
                                colDef = MetaDataUtil.getTableDefinitionByName(tableName2).getColumnDefinitionByName(columnName);
                            }
                            catch (final MetaDataException e) {
                                throw new DataAccessException("Cannot able to find suitable tableDefinition for tableAlias [" + tableAlias + "]");
                            }
                            dataType = colDef.getDataType();
                            criCol.setType(DataTypeUtil.getJavaSQLType(dataType.trim()));
                        }
                    }
                    catch (final Exception e2) {
                        QueryUtil.OUT.warning(e2.getMessage());
                        QueryUtil.OUT.warning("Unable to resolve datatype for [" + tableAlias + "." + columnName + "]. Hence returning the string value.");
                    }
                }
                if ((comparator == 8 || comparator == 9) && !value.toString().startsWith("(")) {
                    final int pos = Arrays.binarySearch(mainRelCriIDs, rcID);
                    final long nextRCID = (pos == mainRelCriIDs.length - 1) ? -1L : mainRelCriIDs[pos + 1];
                    sb.append((value != null) ? value.toString().trim() : null);
                    int index = (int)rcID;
                    while (true) {
                        if (nextRCID == -1L) {
                            if (index >= relCritList.size()) {
                                break;
                            }
                        }
                        else if (index >= nextRCID - 1L) {
                            break;
                        }
                        relCrit = relCritList.get(index);
                        value = relCrit.get("VALUE");
                        if (value != null) {
                            sb.append(value.toString().trim());
                        }
                        ++index;
                    }
                }
                Object origValues = null;
                try {
                    if (sb.length() == 0 && value == null) {
                        origValues = getObject(null, comparator, dataType);
                    }
                    else if (sb.length() == 0 && value != null) {
                        origValues = getObject(value, comparator, dataType);
                    }
                    else if (sb.length() != 0 && value != null) {
                        origValues = getObject(sb.toString(), comparator, dataType);
                    }
                }
                catch (final Exception e3) {
                    throw new DataAccessException(e3);
                }
                Criteria retCriteria = null;
                if (criCol.getType() == 16 && value != null && (comparator == 2 || comparator == 3)) {
                    retCriteria = new Criteria(criCol, removeWildCardCharacters(value.toString()), comparator, (boolean)relCrit.get("CASESENSITIVE"));
                }
                else {
                    retCriteria = new Criteria(criCol, origValues, comparator, (boolean)relCrit.get("CASESENSITIVE"));
                }
                if (criCol.getTableAlias() != null && !(origValues instanceof Column)) {
                    final List<Table> tableList = new ArrayList<Table>();
                    tableList.add(Table.getTable(tableName2, tableAlias));
                    setTypeForCriteria(retCriteria, tableList);
                }
                if (AppResources.getProperty("transform.criteria.value.on.persist", "false").equalsIgnoreCase("true")) {
                    retCriteria.transformValue();
                }
                return retCriteria;
            }
        }
        return null;
    }
    
    private static String removeWildCardCharacters(String valueStr) {
        valueStr = valueStr.replaceAll("\\*", "");
        valueStr = valueStr.replaceAll("\\?", "");
        return valueStr;
    }
    
    private static void addJoins(final SelectQuery select, final Table table, final List joins) {
        for (int joinsSize = joins.size(), i = 0; i < joinsSize; ++i) {
            final Join join = joins.get(i);
            if (join.getBaseTableAlias().equals(table.getTableAlias())) {
                select.addJoin(join);
                addJoins(select, Table.getTable(join.getReferencedTableName(), join.getReferencedTableAlias()), joins);
            }
        }
    }
    
    private static Join formJoin(final DataObject dataObject, final Row joinTabRow, final Table baseTable, final Table refTable, final List<Table> tableList) throws DataAccessException {
        final List baseTableCols = new ArrayList();
        final List refTableColumns = new ArrayList();
        final Row critRow = dataObject.getRow("JoinCriteria", new Criteria(Column.getColumn("JoinCriteria", "QUERYID"), joinTabRow.get("QUERYID"), 0));
        final Iterator joinColIterator = dataObject.getRows("JoinColumns", joinTabRow);
        if (joinColIterator.hasNext()) {
            while (joinColIterator.hasNext()) {
                final Row joinCol = joinColIterator.next();
                baseTableCols.add(joinCol.get("BASETABLECOLUMN"));
                refTableColumns.add(joinCol.get("REFERENCEDTABLECOLUMN"));
            }
            return new Join(baseTable, refTable, baseTableCols.toArray(new String[baseTableCols.size()]), refTableColumns.toArray(new String[refTableColumns.size()]), (int)joinTabRow.get("JOINTYPE"));
        }
        if (critRow != null) {
            final Criteria criteria = formCriteria(dataObject, critRow, dataObject.getRows("JoinRelCriteria", critRow), tableList);
            if (criteria != null) {
                return new Join(baseTable, refTable, criteria, (int)joinTabRow.get("JOINTYPE"));
            }
        }
        return null;
    }
    
    private static Row getSelectQueryRow(final SelectQuery select) throws DataAccessException {
        int startIndex = 0;
        int numOfObjects = 0;
        final Range range = select.getRange();
        if (range != null) {
            startIndex = range.getStartIndex();
            numOfObjects = range.getNumberOfObjects();
        }
        final Row row = new Row("SelectQuery");
        row.set("STARTINDEX", new Integer(startIndex));
        row.set("NUMOFOBJECTS", new Integer(numOfObjects));
        return row;
    }
    
    private static void addSelectTables(final DataObject dataObject, final List tables, final Row selectRow) throws DataAccessException {
        for (int tabSize = tables.size(), i = 0; i < tabSize; ++i) {
            final Table table = tables.get(i);
            final Row row = new Row("SelectTable");
            row.set("QUERYID", selectRow.get("QUERYID"));
            row.set("TABLEALIAS", table.getTableAlias());
            row.set("TABLENAME", table.getTableName());
            dataObject.addRow(row);
        }
    }
    
    private static void addJoinRows(final DataObject dataObject, final List joins, final Row selectRow) throws DataAccessException {
        final int joinSize = joins.size();
        final Object queryID = selectRow.get("QUERYID");
        for (int i = 0; i < joinSize; ++i) {
            final Join join = joins.get(i);
            final Row joinTabRow = new Row("JoinTable");
            joinTabRow.set("QUERYID", queryID);
            joinTabRow.set("TABLEALIAS", join.getReferencedTableAlias());
            joinTabRow.set("REFERENCEDTABLE", join.getBaseTableAlias());
            joinTabRow.set("JOINTYPE", new Integer(join.getJoinType()));
            dataObject.addRow(joinTabRow);
            final Criteria joinCriteria = join.getCriteria();
            if (joinCriteria != null) {
                addCriteriaRows(dataObject, joinCriteria, queryID, "JoinCriteria", "JoinRelCriteria", CriteriaType.WHERE);
            }
            else {
                for (int joinColsLength = join.getNumberOfColumns(), j = 0; j < joinColsLength; ++j) {
                    final Row joinColRow = new Row("JoinColumns");
                    joinColRow.set("QUERYID", queryID);
                    joinColRow.set("BASETABLECOLUMN", join.getBaseTableColumn(j));
                    joinColRow.set("REFERENCEDTABLECOLUMN", join.getReferencedTableColumn(j));
                    joinColRow.set("TABLEALIAS", join.getReferencedTableAlias());
                    dataObject.addRow(joinColRow);
                }
            }
        }
    }
    
    private static List<Row> addSelectColRows(final DataObject dataObject, final List<Column> selectCols, final Row selectRow) throws DataAccessException {
        final List<Row> addedSelColRows = new ArrayList<Row>();
        int index = 1;
        for (final Column col : selectCols) {
            addedSelColRows.add(addSelectColRow(dataObject, col, selectRow, index++));
        }
        return addedSelColRows;
    }
    
    private static Row addSelectColRow(final DataObject dataObject, final Column selectCol, final Row selectQueryRow, final int columnIndex) throws DataAccessException {
        if (selectCol instanceof DerivedColumn) {
            throw new UnsupportedOperationException("SelectQuery containing DerivedColumn as a SelectColumn cannot be stored in the DB :: [" + selectCol + "]");
        }
        final Row row = new Row("SelectColumn");
        row.set(1, selectQueryRow.get("QUERYID"));
        row.set(3, selectCol.getColumnAlias());
        row.set(2, columnIndex);
        if (selectCol instanceof Function || selectCol instanceof Operation) {
            addSelectTableRow(dataObject, selectQueryRow.get("QUERYID"));
            row.set(5, "<<EXPRESSION>>");
            dataObject.addRow(row);
            Object exprID = null;
            if (selectCol instanceof Function) {
                exprID = addFuncParamRows((Function)selectCol, dataObject, null);
            }
            else {
                exprID = addOperParamRows((Operation)selectCol, dataObject, null);
            }
            if (selectCol.getColumnAlias() != null) {
                row.set(3, selectCol.getColumnAlias());
            }
            final Row selColExprRow = new Row("SelCol_Expression");
            selColExprRow.set(1, selectQueryRow.get(1));
            selColExprRow.set("COLUMNINDEX", columnIndex);
            selColExprRow.set(2, "<<EXPRESSION>>");
            selColExprRow.set(4, exprID);
            dataObject.addRow(selColExprRow);
        }
        else if (selectCol instanceof Column) {
            row.set(4, selectCol.getColumnName());
            row.set(5, selectCol.getTableAlias());
            dataObject.addRow(row);
        }
        return row;
    }
    
    private static void addSortColRows(final DataObject dataObject, final List sortCols, final Row selectRow) throws DataAccessException {
        for (int sortSize = sortCols.size(), i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = sortCols.get(i);
            if (sortCol == SortColumn.NULL_COLUMN) {
                throw new IllegalArgumentException("SelectQuery with NULL_COLUMN as sortColumn cannot be processed.");
            }
            final Row row = new Row("SortColumn");
            row.set("QUERYID", selectRow.get("QUERYID"));
            row.set("COLUMNNAME", sortCol.getColumnName());
            row.set("COLUMNALIAS", sortCol.getColumnAlias());
            row.set("ASCENDING_ORDER", sortCol.isAscending());
            row.set("SORTINDEX", new Integer(i));
            row.set("TABLEALIAS", sortCol.getTableAlias());
            row.set("ISNULLSFIRST", sortCol.isNullsFirst());
            final Column col = sortCol.getColumn();
            if (col instanceof Function || col instanceof Operation) {
                addSelectTableRow(dataObject, selectRow.get("QUERYID"));
                row.set(2, "<<EXPRESSION>>");
                dataObject.addRow(row);
                Object exprID = null;
                if (col instanceof Function) {
                    exprID = addFuncParamRows((Function)col, dataObject, null);
                }
                else {
                    exprID = addOperParamRows((Operation)col, dataObject, null);
                }
                final Row sortCol_ExprRow = new Row("SortCol_Expression");
                sortCol_ExprRow.set("QUERYID", selectRow.get("QUERYID"));
                sortCol_ExprRow.set(2, "<<EXPRESSION>>");
                sortCol_ExprRow.set(3, i);
                sortCol_ExprRow.set(4, exprID);
                dataObject.addRow(sortCol_ExprRow);
            }
            else {
                row.set(2, sortCol.getTableAlias());
                row.set(4, sortCol.getColumnName());
                dataObject.addRow(row);
            }
        }
    }
    
    public static void fillSQDOWithCriteria(final DataObject dataObject, final Criteria criteria) throws DataAccessException {
        fillSQDOWithCriteria(dataObject, criteria, CriteriaType.WHERE);
    }
    
    private static void fillSQDOWithCriteria(final DataObject dataObject, final Criteria criteria, final CriteriaType criteriaType) throws DataAccessException {
        final int startIndex = 0;
        final int numOfObjects = 0;
        if (dataObject.isEmpty() || dataObject.getRow("SelectQuery") == null) {
            final Row row = new Row("SelectQuery");
            row.set("STARTINDEX", new Integer(startIndex));
            row.set("NUMOFOBJECTS", new Integer(numOfObjects));
            dataObject.addRow(row);
        }
        final Row selectQueryRow = dataObject.getRow("SelectQuery");
        addCriteriaRows(dataObject, criteria, selectQueryRow, criteriaType);
    }
    
    private static void addCriteriaRows(final DataObject dataObject, final Criteria criteria, final Row selectRow, final CriteriaType criteriaType) throws DataAccessException {
        addCriteriaRows(dataObject, criteria, selectRow.get("QUERYID"), "Criteria", "RelationalCriteria", criteriaType);
    }
    
    public static Object addCriteriaRows(final DataObject dataObject, final Criteria criteria, final Object queryID, final String criteriaTableName, final String relCriteriaTableName) throws DataAccessException {
        return addCriteriaRows(dataObject, criteria, queryID, criteriaTableName, relCriteriaTableName, CriteriaType.WHERE);
    }
    
    public static Object addCriteriaRows(final DataObject dataObject, Criteria criteria, final Object queryID, final String criteriaTableName, final String relCriteriaTableName, final CriteriaType criteriaType) throws DataAccessException {
        final List relCriteriaList = new ArrayList();
        final List criteriaList = new ArrayList();
        final Row critRow = new Row(criteriaTableName);
        if (queryID != null) {
            critRow.set("QUERYID", queryID);
        }
        critRow.set("LOGICALREPRESENTATION", getLogicalRepresentationAndFillRelCriRows(dataObject, criteria, criteriaList, relCriteriaList, critRow, relCriteriaTableName));
        if (criteriaType == CriteriaType.EXPRESSION_PARAM) {
            critRow.set("IS_EXPRESSION_PARAM", true);
        }
        dataObject.addRow(critRow);
        for (int i = 0; i < criteriaList.size(); ++i) {
            criteria = criteriaList.get(i);
            if (AppResources.getString("transform.criteria.value.on.persist", "false").equalsIgnoreCase("true")) {
                criteria.transformValueAsTypeSpecific();
                criteria.validateInput();
            }
        }
        for (int relCritSize = relCriteriaList.size(), j = 0; j < relCritSize; ++j) {
            final Row row = relCriteriaList.get(j);
            dataObject.addRow(row);
        }
        return critRow.get("CRITERIAID");
    }
    
    private static Object addFuncParamRows(final Function col, final DataObject dataObject, Object rootExprID) throws DataAccessException {
        final Row exprRow = new Row("Expression");
        exprRow.set(3, col.getFunctionName());
        dataObject.addRow(exprRow);
        final Object exprID = exprRow.get(1);
        if (rootExprID == null) {
            rootExprID = exprID;
        }
        exprRow.set(2, rootExprID);
        exprRow.set("VALUE_TYPE", col.getType());
        Row funcParamsRow = null;
        int index = 0;
        final Object[] functionArguments;
        final Object[] funcArgs = functionArguments = col.getFunctionArguments();
        for (final Object arg : functionArguments) {
            funcParamsRow = new Row("FunctionParams");
            funcParamsRow.set(1, exprID);
            funcParamsRow.set(2, index++);
            if (arg instanceof Function) {
                final Object functionID = addFuncParamRows((Function)arg, dataObject, rootExprID);
                funcParamsRow.set(3, functionID);
                funcParamsRow.set(4, -9999);
            }
            else if (arg instanceof Operation) {
                final Object operationID = addOperParamRows((Operation)arg, dataObject, rootExprID);
                funcParamsRow.set(3, operationID);
                funcParamsRow.set(4, -9999);
            }
            else if (arg instanceof Column) {
                final Column c = (Column)arg;
                if (c.getFunction() == 0) {
                    funcParamsRow.set(3, c.getTableAlias() + "." + c.getColumnName() + "." + c.getColumnAlias());
                }
                else {
                    funcParamsRow.set(3, c.getColumnAlias());
                }
                funcParamsRow.set(4, -9998);
            }
            else if (arg instanceof Criteria) {
                final Criteria cri = (Criteria)arg;
                if (cri.getColumn() instanceof Function || cri.getColumn() instanceof Operation) {
                    throw new UnsupportedOperationException("Nested Function/Operation cannot be stored in the DB :: [" + arg + "]");
                }
                fillSQDOWithCriteria(dataObject, cri, CriteriaType.EXPRESSION_PARAM);
                funcParamsRow.set(3, dataObject.getRow("Criteria").get("CRITERIAID"));
                funcParamsRow.set(4, -9997);
            }
            else if (arg instanceof Function.ReservedParameter) {
                final Function.ReservedParameter reservedParam = (Function.ReservedParameter)arg;
                funcParamsRow.set(3, reservedParam.getParamValue());
                funcParamsRow.set(4, -9996);
            }
            else {
                setValueAndTypeInRow(funcParamsRow, "PARAM_VALUE", "VALUE_TYPE", arg);
            }
            dataObject.addRow(funcParamsRow);
        }
        return exprID;
    }
    
    private static Object addOperParamRows(final Operation col, final DataObject dataObject, Object rootExprID) throws DataAccessException {
        final Row exprRow = new Row("Expression");
        exprRow.set(3, col.getOperation().toString().trim());
        dataObject.addRow(exprRow);
        final Object exprID = exprRow.get(1);
        if (rootExprID == null) {
            rootExprID = exprID;
        }
        exprRow.set(2, rootExprID);
        exprRow.set("VALUE_TYPE", col.getType());
        final Row operParamsRow = new Row("OperationParams");
        operParamsRow.set(1, exprID);
        final Object lhsArg = col.getLHSArgument();
        final Object rhsArg = col.getRHSArgument();
        if (lhsArg instanceof Function) {
            final Object functionID = addFuncParamRows((Function)lhsArg, dataObject, rootExprID);
            operParamsRow.set(2, functionID);
            operParamsRow.set(3, -9999);
        }
        else if (lhsArg instanceof Operation) {
            final Object operationID = addOperParamRows((Operation)lhsArg, dataObject, rootExprID);
            operParamsRow.set(2, operationID);
            operParamsRow.set(3, -9999);
        }
        else if (lhsArg instanceof Column) {
            final Column lhsCol = (Column)lhsArg;
            if (lhsCol.getFunction() == 0) {
                operParamsRow.set(2, lhsCol.getTableAlias() + "." + lhsCol.getColumnName() + "." + lhsCol.getColumnAlias());
            }
            else {
                operParamsRow.set(2, lhsCol.getColumnAlias());
            }
            operParamsRow.set(3, -9998);
        }
        if (rhsArg instanceof Function) {
            final Object functionID = addFuncParamRows((Function)rhsArg, dataObject, rootExprID);
            operParamsRow.set(4, functionID);
            operParamsRow.set(5, -9999);
        }
        else if (rhsArg instanceof Operation) {
            final Object operationID = addOperParamRows((Operation)rhsArg, dataObject, rootExprID);
            operParamsRow.set(4, operationID);
            operParamsRow.set(5, -9999);
        }
        else if (rhsArg instanceof Column) {
            final Column c = (Column)rhsArg;
            if (c.getFunction() == 0) {
                operParamsRow.set(4, c.getTableAlias() + "." + c.getColumnName() + "." + c.getColumnAlias());
            }
            else {
                operParamsRow.set(4, c.getColumnAlias());
            }
            operParamsRow.set(5, -9998);
        }
        else {
            setValueAndTypeInRow(operParamsRow, "RVALUE", "RVALUE_TYPE", rhsArg);
        }
        dataObject.addRow(operParamsRow);
        return exprID;
    }
    
    private static void setValueAndTypeInRow(final Row row, final String valueColName, final String typeColName, final Object arg) {
        if (arg instanceof String) {
            row.set(valueColName, arg);
            row.set(typeColName, 1);
        }
        else if (arg instanceof Long) {
            row.set(valueColName, arg);
            row.set(typeColName, -5);
        }
        else if (arg instanceof Integer) {
            row.set(valueColName, arg.toString());
            row.set(typeColName, 4);
        }
        else if (arg instanceof Double) {
            row.set(valueColName, arg.toString());
            row.set(typeColName, 8);
        }
        else if (arg instanceof BigDecimal) {
            row.set(valueColName, arg.toString());
            row.set(typeColName, 3);
        }
        else if (arg instanceof Boolean) {
            row.set(valueColName, arg.toString());
            row.set(typeColName, 16);
        }
        else if (arg instanceof Float) {
            row.set(valueColName, arg.toString());
            row.set(typeColName, 6);
        }
        else if (arg instanceof Date) {
            row.set(valueColName, arg);
            row.set(typeColName, 91);
        }
        else if (arg instanceof Timestamp) {
            row.set(valueColName, arg);
            row.set(typeColName, 93);
        }
        else {
            if (!(arg instanceof Time)) {
                throw new IllegalArgumentException("The type of the argument [" + arg + "] set in the function parameters is unknown.");
            }
            row.set(valueColName, arg);
            row.set(typeColName, 92);
        }
    }
    
    private static void addSelectTableRow(final DataObject dataObject, final Object queryID) throws DataAccessException {
        final Row row = new Row("SelectTable");
        row.set(1, queryID);
        row.set(2, "<<EXPRESSION>>");
        row.set(3, "<<EXPRESSION>>");
        if (dataObject.getRow("SelectTable", row) == null) {
            dataObject.addRow(row);
        }
    }
    
    public static String getString(Object value, final int comparator) {
        if (value == null) {
            return null;
        }
        if (value instanceof Column && (!(value instanceof Function) || value instanceof Operation)) {
            value = "column:{" + ((Column)value).getTableAlias() + "." + ((Column)value).getColumnName() + "}";
        }
        if (comparator == 8 || comparator == 9) {
            final String pattern = "::";
            if (value instanceof Object[]) {
                value = getString((Object[])value, pattern);
            }
            else if (value instanceof int[]) {
                value = getString((int[])value, pattern);
            }
            else if (value instanceof long[]) {
                value = getString((long[])value, pattern);
            }
            else if (value instanceof double[]) {
                value = getString((double[])value, pattern);
            }
            else if (value instanceof float[]) {
                value = getString((float[])value, pattern);
            }
        }
        else if (comparator == 14 || comparator == 15) {
            final String pattern = " :&: ";
            if (value instanceof Object[]) {
                value = getString((Object[])value, pattern);
            }
            else if (value instanceof int[]) {
                value = getString((int[])value, pattern);
            }
            else if (value instanceof long[]) {
                value = getString((long[])value, pattern);
            }
            else if (value instanceof double[]) {
                value = getString((double[])value, pattern);
            }
            else if (value instanceof float[]) {
                value = getString((float[])value, pattern);
            }
        }
        return value.toString();
    }
    
    private static String getString(final Object[] value, final String pattern) {
        final StringBuffer buff = new StringBuffer(value.length);
        for (int i = 0; i < value.length; ++i) {
            if (i > 0) {
                buff.append(pattern);
            }
            buff.append("" + value[i]);
        }
        return buff.toString();
    }
    
    private static String getString(final int[] value, final String pattern) {
        final StringBuffer buff = new StringBuffer(value.length);
        for (int i = 0; i < value.length; ++i) {
            if (i > 0) {
                buff.append(pattern);
            }
            buff.append("" + value[i]);
        }
        return buff.toString();
    }
    
    private static String getString(final long[] value, final String pattern) {
        final StringBuffer buff = new StringBuffer(value.length);
        for (int i = 0; i < value.length; ++i) {
            if (i > 0) {
                buff.append(pattern);
            }
            buff.append("" + value[i]);
        }
        return buff.toString();
    }
    
    private static String getString(final double[] value, final String pattern) {
        final StringBuffer buff = new StringBuffer(value.length);
        for (int i = 0; i < value.length; ++i) {
            if (i > 0) {
                buff.append(pattern);
            }
            buff.append("").append(value[i]);
        }
        return buff.toString();
    }
    
    private static String getString(final float[] value, final String pattern) {
        final StringBuffer buff = new StringBuffer(value.length);
        for (int i = 0; i < value.length; ++i) {
            if (i > 0) {
                buff.append(pattern);
            }
            buff.append("").append(value[i]);
        }
        return buff.toString();
    }
    
    public static void setTypeForUpdateColumns(final List tabList, final Map values, final Criteria cri) {
        setTypeForCriteria(cri, tabList);
        final Set set = values.keySet();
        final Iterator itr = set.iterator();
        final String tableName = tabList.get(0).getTableName();
        final TableDefinition tableDefinition = getTableDefinition(tableName);
        if (tableDefinition == null) {
            throw new IllegalArgumentException("Unknown table name specified " + tableName);
        }
        while (itr.hasNext()) {
            final Column col = itr.next();
            final Object value = values.get(col);
            if (tableDefinition.getPhysicalColumns().contains(col.getColumnName())) {
                col.setType(1111);
            }
            else {
                setType(tableName, col, tabList);
            }
            if (value instanceof Column) {
                final Column valColumn = (Column)value;
                if (valColumn instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)valColumn;
                    final SelectQuery subQuery = (SelectQuery)dc.getSubQuery();
                    final Column sqColumn = subQuery.getSelectColumns().get(0);
                    setDataType(subQuery);
                    if (valColumn.getType() == 1111) {
                        valColumn.setType(sqColumn.getType());
                    }
                    return;
                }
                if (valColumn instanceof Function) {
                    final Function function = (Function)valColumn;
                    for (final Object arg : function.getFunctionArguments()) {
                        if (arg instanceof Criteria) {
                            setTypeForCriteria((Criteria)arg, tabList);
                        }
                        else if (!(arg instanceof Function.ReservedParameter)) {
                            if (arg instanceof Column) {
                                setType(((Column)arg).getTableAlias(), (Column)arg, tabList);
                            }
                        }
                    }
                }
                else if (valColumn instanceof Operation) {
                    final Operation operation = (Operation)valColumn;
                    if (operation.getLHSArgument() instanceof Column) {
                        setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument(), tabList);
                    }
                    if (!(operation.getRHSArgument() instanceof Column)) {
                        continue;
                    }
                    setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument(), tabList);
                }
                else if (valColumn instanceof CaseExpression) {
                    final CaseExpression ce = (CaseExpression)valColumn;
                    for (final CaseExpression.WhenExpr we : ce.getWhenExpressions()) {
                        if (we.getExpr() instanceof Criteria) {
                            setTypeForCriteria((Criteria)we.getExpr(), tabList);
                        }
                        else if (we.getExpr() instanceof Column) {
                            final Column modifiedColumn = (Column)we.getExpr();
                            setType(modifiedColumn.getTableAlias(), modifiedColumn, tabList);
                        }
                        if (we.getValue() instanceof Column) {
                            final Column modifiedColumn = (Column)we.getValue();
                            setType(modifiedColumn.getTableAlias(), modifiedColumn, tabList);
                        }
                    }
                    if (ce.getElseVal() != null && ce.getElseVal() instanceof Column) {
                        final Column modifiedColumn2 = (Column)ce.getElseVal();
                        setType(modifiedColumn2.getTableAlias(), modifiedColumn2, tabList);
                    }
                    if (valColumn.getTableAlias() == null) {
                        continue;
                    }
                    final Table table = getTable(valColumn.getTableAlias(), tabList);
                    setType(table.getTableName(), valColumn);
                }
                else {
                    if (!(valColumn instanceof Column)) {
                        continue;
                    }
                    final TableDefinition tabDefinition = getTableDefinition(tableName);
                    if (tabDefinition == null) {
                        throw new IllegalArgumentException("Unknown table name specified " + valColumn.getTableAlias());
                    }
                    if (tabDefinition.getPhysicalColumns().contains(valColumn.getColumnName())) {
                        valColumn.setType(1111);
                    }
                    else {
                        setType(valColumn.getTableAlias(), valColumn, tabList);
                    }
                }
            }
        }
    }
    
    public static void setDataTypeForUpdateQuery(final UpdateQuery query) {
        final List tabList = query.getTableList();
        final Map newValues = query.getUpdateColumns();
        final List joins = query.getJoins();
        final Criteria criteria = query.getCriteria();
        setTypeForUpdateColumns(tabList, newValues, criteria);
        if (joins != null && joins.size() > 0) {
            for (int i = 0; i < joins.size(); ++i) {
                final Join join = joins.get(i);
                final Criteria joinCriteria = join.getCriteria();
                if (joinCriteria != null) {
                    setTypeForCriteria(joinCriteria, tabList);
                }
            }
        }
    }
    
    public static void setDataTypeForDeleteQuery(final DeleteQuery query) {
        final List tableList = query.getTableList();
        final List sortColumns = query.getSortColumns();
        final int sortSize = sortColumns.size();
        final List joins = query.getJoins();
        if (joins != null && joins.size() > 0) {
            for (int i = 0; i < joins.size(); ++i) {
                final Join join = joins.get(i);
                final Criteria joinCriteria = join.getCriteria();
                if (joinCriteria != null) {
                    setTypeForCriteria(joinCriteria, tableList);
                }
            }
        }
        if (query.getCriteria() != null) {
            setTypeForCriteria(query.getCriteria(), tableList);
        }
        for (int i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = sortColumns.get(i);
            if (sortCol != SortColumn.NULL_COLUMN) {
                final Column col = sortCol.getColumn();
                if (col instanceof Function) {
                    final Function function = (Function)col;
                    for (final Object arg : function.getFunctionArguments()) {
                        if (arg instanceof Criteria) {
                            setTypeForCriteria((Criteria)arg, tableList);
                        }
                        else if (!(arg instanceof Function.ReservedParameter)) {
                            if (arg instanceof Column) {
                                setType(((Column)arg).getTableAlias(), (Column)arg);
                            }
                        }
                    }
                }
                else if (col instanceof Operation) {
                    final Operation operation = (Operation)col;
                    if (operation.getLHSArgument() instanceof Column) {
                        setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument());
                    }
                    if (operation.getRHSArgument() instanceof Column) {
                        setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument());
                    }
                }
                if (col.getType() == 1111) {
                    final int colFunction = col.getFunction();
                    if (colFunction == 0) {
                        final Table sortTable = getTable(col.getTableAlias(), tableList);
                        if (sortTable instanceof DerivedTable) {
                            setDerivedTablesColumnType((DerivedTable)sortTable, col);
                        }
                        else {
                            setType(sortTable.getTableName(), col);
                        }
                    }
                }
            }
        }
    }
    
    private static String getLogicalRepresentationAndFillRelCriRows(final DataObject dataObject, final Criteria criteria, final List critList, final List relCriteriaList, final Row criteriaRow, final String relCriteriaTableName) throws DataAccessException {
        final StringBuffer logicalRepresentationBuf = new StringBuffer();
        if (criteria.getLeftCriteria() == null) {
            critList.add(criteria);
            final List<Row> rcRows = getRelationalCriteriaRows(dataObject, criteria, criteriaRow, relCriteriaList.size() + 1, relCriteriaTableName);
            if (null != rcRows) {
                final Long relCriteriaID = (Long)rcRows.get(0).get(3);
                relCriteriaList.addAll(rcRows);
                logicalRepresentationBuf.append(relCriteriaID);
            }
            return logicalRepresentationBuf.toString();
        }
        logicalRepresentationBuf.insert(0, "(");
        logicalRepresentationBuf.append(getLogicalRepresentationAndFillRelCriRows(dataObject, criteria.getLeftCriteria(), critList, relCriteriaList, criteriaRow, relCriteriaTableName));
        if (criteria.getOperator().equals(" AND ")) {
            logicalRepresentationBuf.append("&");
        }
        else {
            logicalRepresentationBuf.append("|");
        }
        logicalRepresentationBuf.append(getLogicalRepresentationAndFillRelCriRows(dataObject, criteria.getRightCriteria(), critList, relCriteriaList, criteriaRow, relCriteriaTableName));
        logicalRepresentationBuf.append(")");
        return logicalRepresentationBuf.toString();
    }
    
    private static List<Row> getRelationalCriteriaRows(final DataObject dataObject, final Criteria relCriteria, final Row criteriaRow, int relCriteriaID, final String relCriteriaTableName) throws DataAccessException {
        final List<Row> rowList = new ArrayList<Row>();
        Row relCritRow = new Row(relCriteriaTableName);
        relCritRow.set(1, criteriaRow.get(1));
        relCritRow.set(2, criteriaRow.get(2));
        relCritRow.set(3, new Long(relCriteriaID));
        Column relCriColumn = relCriteria.getColumn();
        if (relCriColumn instanceof LocaleColumn) {
            final LocaleColumn lc = (LocaleColumn)relCriColumn;
            relCriColumn = lc.getColumn();
        }
        if (relCriColumn instanceof DerivedColumn) {
            throw new UnsupportedOperationException("SelectQuery containing DerivedColumn in the Criteria cannot be stored in the DB :: [" + relCriteria + "]");
        }
        if (relCriColumn instanceof Function || relCriColumn instanceof Operation) {
            addSelectTableRow(dataObject, criteriaRow.get(1));
            relCritRow.set("TABLEALIAS", "<<EXPRESSION>>");
            Object exprID = null;
            if (relCriColumn instanceof Function) {
                exprID = addFuncParamRows((Function)relCriColumn, dataObject, null);
            }
            else {
                exprID = addOperParamRows((Operation)relCriColumn, dataObject, null);
            }
            Row relCri_ExprRow = null;
            if (relCriteriaTableName.equals("RelationalCriteria")) {
                relCri_ExprRow = new Row("RelCri_Expression");
            }
            else {
                relCri_ExprRow = new Row("JoinRelCri_Expression");
            }
            relCri_ExprRow.set(1, criteriaRow.get(1));
            relCri_ExprRow.set(2, criteriaRow.get("CRITERIAID"));
            relCri_ExprRow.set(3, new Long(relCriteriaID));
            relCri_ExprRow.set(4, exprID);
            dataObject.addRow(relCri_ExprRow);
        }
        else {
            relCritRow.set(4, relCriColumn.getTableAlias());
            relCritRow.set(5, relCriColumn.getColumnName());
            Row selectTableRow = dataObject.getRow("SelectTable", relCritRow);
            if (selectTableRow == null && criteriaRow.get(1) != null) {
                selectTableRow = new Row("SelectTable");
                selectTableRow.set(1, criteriaRow.get(1));
                selectTableRow.set(2, relCriteria.getColumn().getTableAlias());
                selectTableRow.set(3, relCriteria.getColumn().getTableAlias());
                dataObject.addRow(selectTableRow);
            }
        }
        final int comparator = relCriteria.getComparator();
        relCritRow.set(6, new Integer(comparator));
        relCritRow.set(8, relCriteria.isCaseSensitive());
        Object value = relCriteria.getValue();
        if (value instanceof String && value.toString().startsWith("column:{") && value.toString().endsWith("}")) {
            throw new IllegalArgumentException("The pattern 'column:{*}' has a special meaning used by Mickey. Hence this cannot be used in criteria value part while persisting SelectQuery.");
        }
        if (value instanceof Column && !(value instanceof Function) && !(value instanceof Operation)) {
            value = "column:{" + ((Column)value).getTableAlias() + "." + ((Column)value).getColumnName() + "}";
        }
        if (value != null && (value instanceof Function || value instanceof Operation)) {
            throw new UnsupportedOperationException("SelectQuery containing Function/Operation in the Criteria value part cannot be stored in the DB :: [" + value + "]");
        }
        if (value != null && !value.toString().matches(".*\\$\\{.+}.*")) {
            if (relCriteria.getValue() instanceof DerivedColumn || relCriteria.getValue() instanceof DerivedColumn[]) {
                relCritRow.set("VALUE_DATATYPE", "DERIVED_COLUMN");
            }
            else if (relCriteria.getValue() instanceof Column || relCriteria.getValue() instanceof Column[]) {
                relCritRow.set("VALUE_DATATYPE", "COLUMN");
            }
            else if (relCriteria.getColumn().getDefinition() != null) {
                relCritRow.set("VALUE_DATATYPE", relCriteria.getColumn().getDefinition().getDataType());
            }
        }
        if ((comparator == 8 || comparator == 9) && value != null && !value.toString().matches(".*\\$\\{.+}.*")) {
            final int arrayType = getArrayType(value);
            int arraylength = -1;
            try {
                arraylength = Array.getLength(value);
            }
            catch (final IllegalArgumentException excp) {
                throw new DataAccessException("Value for IN/NOT_IN comparator is not an array");
            }
            QueryUtil.OUT.log(Level.FINE, "arrayType :: [{0}] arraylength :: [{1}]", new Object[] { arrayType, arraylength });
            String s = null;
            int total_length = 0;
            int length = 0;
            int delimiter_type = 0;
            final StringBuilder sb = new StringBuilder();
            final StringBuilder fullSB = new StringBuilder();
            for (int index = 0; index < arraylength; ++index) {
                s = getString(value, index, arrayType);
                if (arrayType == 3 && s.indexOf(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]) >= 0) {
                    final int new_delimiter_type = getNextDelimiter(delimiter_type, s, fullSB.toString(), rowList);
                    sb.replace(0, sb.length(), sb.toString().replaceAll(QueryUtil.DELIMITERS_FOR_IN[delimiter_type], QueryUtil.DELIMITERS_FOR_IN[new_delimiter_type]));
                    for (final Row r : rowList) {
                        r.set(7, r.get(7).toString().replaceAll(QueryUtil.DELIMITERS_FOR_IN[delimiter_type], QueryUtil.DELIMITERS_FOR_IN[new_delimiter_type]));
                    }
                    delimiter_type = new_delimiter_type;
                }
                length = s.length();
                if (length > 290) {
                    throw new DataAccessException("Size of an String in WHERE clause inside IN cannot be more than 290 characters but this string [" + s + "] has " + length + " characters.");
                }
                QueryUtil.OUT.log(Level.FINE, "index :: [{0}] length ::  [{1}]", new Object[] { index, length });
                if (total_length + length + 4 <= 290) {
                    total_length += length + 4;
                    sb.append(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]);
                    sb.append(s);
                    fullSB.append(s);
                    fullSB.append(",");
                }
                QueryUtil.OUT.log(Level.FINE, "total_length :: [{0}]", total_length);
                length = ((index + 1 == arraylength) ? 0 : getString(value, index + 1, arrayType).length());
                if (total_length + length + 4 >= 290 || length == 0) {
                    relCritRow.set(7, sb.toString());
                    rowList.add(relCritRow);
                    total_length = 0;
                    if (index + 1 < arraylength) {
                        final Row row = new Row("RelationalCriteria");
                        sb.delete(0, sb.length());
                        row.setAll(relCritRow.getValues());
                        relCritRow = row;
                        relCritRow.set(7, null);
                        relCritRow.set(3, new Long(++relCriteriaID));
                    }
                }
            }
        }
        else if (comparator == 14 || comparator == 15) {
            relCritRow.set(7, getValuesStringForBetween(value));
            rowList.add(relCritRow);
        }
        else {
            value = ((null == value) ? null : value.toString());
            relCritRow.set(7, value);
            rowList.add(relCritRow);
        }
        return rowList;
    }
    
    private static String getString(final Object value, final int index, final int arrayType) {
        switch (arrayType) {
            case 1: {
                return String.valueOf(((int[])value)[index]);
            }
            case 2: {
                return String.valueOf(((long[])value)[index]);
            }
            case 5: {
                return String.valueOf(((float[])value)[index]);
            }
            case 6: {
                return String.valueOf(((double[])value)[index]);
            }
            case 3: {
                return String.valueOf(((String[])value)[index]);
            }
            case 4: {
                return String.valueOf(((Object[])value)[index]);
            }
            default: {
                return null;
            }
        }
    }
    
    private static int getNextDelimiter(int delimiter_type, final String s, final String fullStr, final List<Row> rowList) {
        if (++delimiter_type > 4) {
            throw new IllegalArgumentException("Value part of this String Array contains all the pre-defined delimiters, so query with this criteria cannot be persisted.");
        }
        boolean patternFound = s.indexOf(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]) >= 0;
        if (!patternFound) {
            patternFound = (fullStr.indexOf(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]) >= 0);
        }
        if (!patternFound) {
            for (final Row relCriRow : rowList) {
                final String value = (String)relCriRow.get(7);
                if (value.indexOf(QueryUtil.DELIMITERS_FOR_IN[delimiter_type]) >= 0) {
                    patternFound = true;
                    break;
                }
            }
        }
        if (patternFound) {
            return getNextDelimiter(delimiter_type, s, fullStr, rowList);
        }
        return delimiter_type;
    }
    
    public static void setDataType(final Query query) {
        if (query instanceof UnionQuery) {
            final UnionQuery current = (UnionQuery)query;
            if (current.getLeftQuery() != null) {
                setDataType(current.getLeftQuery());
            }
            if (current.getRightQuery() != null) {
                setDataType(current.getRightQuery());
            }
        }
        else if (query instanceof SelectQuery) {
            final SelectQuery select = (SelectQuery)query;
            final List colList = select.getSelectColumns();
            final List tableList = new ArrayList();
            if (null != ((SelectQuery)query).getParent()) {
                for (SelectQuery queryRecurssion = ((SelectQuery)query).getParent(); null != queryRecurssion; queryRecurssion = queryRecurssion.getParent()) {
                    tableList.addAll(queryRecurssion.getTableList());
                }
                tableList.addAll(select.getTableList());
                QueryUtil.OUT.fine("Total tableList from query : " + tableList);
            }
            else {
                tableList.addAll(select.getTableList());
            }
            for (int i = 0; i < tableList.size(); ++i) {
                final Table selectTable = tableList.get(i);
                if (selectTable instanceof DerivedTable) {
                    final DerivedTable subQTable = (DerivedTable)selectTable;
                    setDataType(subQTable.getSubQuery());
                }
            }
            final List<SortColumn> sortColumns = select.getSortColumns();
            final int selectSize = colList.size();
            final int sortSize = sortColumns.size();
            for (int j = 0; j < selectSize; ++j) {
                final Column column = colList.get(j);
                if (column instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)column;
                    final SelectQuery subQuery = (SelectQuery)dc.getSubQuery();
                    final Column sqColumn = subQuery.getSelectColumns().get(0);
                    setDataType(subQuery);
                    if (column.getType() == 1111) {
                        column.setType(sqColumn.getType());
                    }
                }
                else {
                    setType(column.getTableAlias(), column, tableList);
                }
            }
            final List joins = select.getJoins();
            if (joins != null && joins.size() > 0) {
                for (int k = 0; k < joins.size(); ++k) {
                    final Join join = joins.get(k);
                    final Criteria joinCriteria = join.getCriteria();
                    if (joinCriteria != null) {
                        setTypeForCriteria(joinCriteria, tableList);
                    }
                }
            }
            if (select.getCriteria() != null) {
                setTypeForCriteria(select.getCriteria(), tableList);
            }
            for (int k = 0; k < sortSize; ++k) {
                final SortColumn sortCol = sortColumns.get(k);
                if (sortCol != SortColumn.NULL_COLUMN) {
                    Column col = sortCol.getColumn();
                    if (col instanceof LocaleColumn) {
                        col = ((LocaleColumn)col).getColumn();
                    }
                    setType(col.getTableAlias(), col, tableList);
                }
            }
            final GroupByClause groupbyclause = select.getGroupByClause();
            if (groupbyclause != null) {
                final List groupbycolumns = groupbyclause.getGroupByColumns();
                final int groupSize = groupbycolumns.size();
                final Criteria groupbyCriteria = groupbyclause.getCriteriaForHavingClause();
                Column col2 = null;
                if (groupbyCriteria != null) {
                    setTypeForHavingCriteria(groupbyCriteria, tableList);
                }
                for (int l = 0; l < groupSize; ++l) {
                    if (groupbycolumns.get(l) instanceof GroupByColumn) {
                        final GroupByColumn groupCol = groupbycolumns.get(l);
                        col2 = groupCol.getGroupByColumn();
                    }
                    if (groupbycolumns.get(l) instanceof Column) {
                        col2 = groupbycolumns.get(l);
                    }
                    setType(col2.getTableAlias(), col2, tableList);
                }
            }
        }
    }
    
    private static void setDerivedTablesColumnType(final DerivedTable dt, final Column column) {
        final Query subQuery = dt.getSubQuery();
        setDataType(subQuery);
        List<Column> selectColumns = null;
        if (subQuery instanceof SelectQuery) {
            final SelectQuery sQuery = (SelectQuery)subQuery;
            selectColumns = sQuery.getSelectColumns();
        }
        else if (subQuery instanceof UnionQuery) {
            final UnionQuery uQuery = (UnionQuery)subQuery;
            selectColumns = uQuery.getSelectColumns();
        }
        for (int j = 0; j < selectColumns.size() && column.getType() == 1111; ++j) {
            final Column subQueryColumn = selectColumns.get(j);
            String columnName = subQueryColumn.getColumnAlias();
            if (columnName == null) {
                final Column fnColumn = subQueryColumn.getColumn();
                if (fnColumn != null) {
                    columnName = fnColumn.getColumnAlias();
                    if (columnName == null) {
                        columnName = fnColumn.getColumnName();
                    }
                }
                else {
                    columnName = subQueryColumn.getColumnName();
                }
            }
            if (columnName.equals(column.getColumnName())) {
                column.setType(subQueryColumn.getType());
                column.setDataType(subQueryColumn.getDataType());
            }
        }
    }
    
    private static Table getTable(final String tableAlias, final List tableList) {
        if (null != tableList) {
            for (int tableSize = tableList.size(), i = 0; i < tableSize; ++i) {
                final Table table = tableList.get(i);
                if (table.getTableAlias().equals(tableAlias)) {
                    return table;
                }
            }
        }
        return null;
    }
    
    public static void setType(String tableName, final Column column) {
        final ColumnDefinition colDef = column.getDefinition();
        if (column.getType() != 1111 && colDef != null && colDef.getColumnID() == null) {
            return;
        }
        String columnName = column.getColumnName();
        int columnIndex = column.getColumnIndex();
        Column modifiedColumn = column;
        if (modifiedColumn.getColumn() != null) {
            modifiedColumn = modifiedColumn.getColumn();
            tableName = modifiedColumn.getTableAlias();
        }
        final TableDefinition tableDefinition = getTableDefinition(tableName);
        if (tableDefinition == null) {
            throw new IllegalArgumentException("Unknown table name specified " + tableName);
        }
        if (columnName == null) {
            columnName = tableDefinition.getColumnNames().get(columnIndex - 1);
            modifiedColumn.setColumnName(columnName);
        }
        else if (columnIndex == -1) {
            columnIndex = tableDefinition.getColumnIndex(columnName);
            modifiedColumn.setColumnIndex(columnIndex);
        }
        final ColumnDefinition cd = tableDefinition.getColumnDefinitionByName(modifiedColumn.getColumnName().trim());
        if (cd != null) {
            modifiedColumn.setDefinition(cd);
            if (modifiedColumn.getType() == 1111) {
                modifiedColumn.setType(getSQLType(tableName, modifiedColumn.getColumnName()));
            }
            if (modifiedColumn.getDataType() == null) {
                if (cd != null) {
                    modifiedColumn.setDataType(cd.getDataType());
                }
                else {
                    modifiedColumn.setDataType("CHAR");
                }
            }
            return;
        }
        throw new IllegalArgumentException("Unknown Column " + modifiedColumn.getColumnName() + " specified");
    }
    
    public static int getSQLType(final String tableName, final String columnName) {
        final TableDefinition tabDefn = getTableDefinition(tableName);
        if (tabDefn == null) {
            throw new IllegalArgumentException("Check whether the tablename " + tableName + " is correct or the respective data-dictionary has been loaded.");
        }
        final ColumnDefinition colDefn = tabDefn.getColumnDefinitionByName(columnName);
        final String dataType = (colDefn != null) ? colDefn.getDataType() : "CHAR";
        return getJavaSQLType(dataType);
    }
    
    public static int getJavaSQLType(final String dataType) {
        return MetaDataUtil.getJavaSQLType(dataType);
    }
    
    private static void setFuncColType(final Column column, final int type) {
        final int function = column.getFunction();
        if (function == 2) {
            column.setType(4);
        }
        else if (column.getType() == 1111) {
            column.setType(type);
        }
    }
    
    @Deprecated
    public static String getSQLTypeAsString(final int sqlTypeVal) throws IllegalArgumentException {
        return MetaDataUtil.getSQLTypeAsString(sqlTypeVal);
    }
    
    public static void syncForDataType(final SelectQuery selectQuery) throws DataAccessException {
        if (selectQuery == null) {
            throw new DataAccessException("Select passed is Null");
        }
        final List tableList = selectQuery.getTableList();
        try {
            final Criteria toChange = selectQuery.getCriteria();
            if (toChange != null) {
                final Criteria complete = getNewCriteria(toChange, tableList);
                setTypeForCriteria(complete, tableList);
                selectQuery.setCriteria(complete);
            }
        }
        catch (final MetaDataException metaExp) {
            throw new DataAccessException(metaExp);
        }
    }
    
    public static Criteria syncForDataType(final Criteria criteria) throws DataAccessException {
        return syncForDataType(criteria, null);
    }
    
    public static Criteria syncForDataType(final Criteria criteria, DataObject dataObject) throws DataAccessException {
        if (criteria == null) {
            throw new DataAccessException("Criteria passed for syncForDataType in Criteria is Null");
        }
        if (dataObject == null) {
            dataObject = DataAccess.constructDataObject();
        }
        final List tableList = new ArrayList();
        getTableList(criteria, tableList, dataObject);
        QueryUtil.OUT.log(Level.FINE, "Tables in passed Criteria are {0}  ", new Object[] { tableList });
        if (tableList.size() == 0) {
            return criteria;
        }
        Criteria returnCriteria = null;
        try {
            returnCriteria = getNewCriteria(criteria, tableList);
            QueryUtil.OUT.log(Level.FINE, "New Criteria returned is {0}  ", new Object[] { returnCriteria });
            setTypeForCriteria(returnCriteria, tableList);
            QueryUtil.OUT.log(Level.FINE, " Successfully set the Data Type for the Columns in the Criteria ");
        }
        catch (final MetaDataException metaExp) {
            QueryUtil.OUT.log(Level.WARNING, "Exception occured while setting the exact DataType in the Criteria ", metaExp);
            returnCriteria = criteria;
        }
        return returnCriteria;
    }
    
    private static void getTableList(final Criteria old, final List tableList, final DataObject dataObject) {
        Criteria old_rightCriteria = null;
        final Criteria old_leftCriteria = old.getLeftCriteria();
        if (old_leftCriteria == null) {
            final Column column = old.getColumn();
            String tableName;
            String tableAlias = tableName = column.getTableAlias();
            if (dataObject instanceof WritableDataObject) {
                tableName = ((WritableDataObject)dataObject).getOrigTableName(tableAlias);
            }
            final Table table = Table.getTable(tableName, tableAlias);
            tableList.add(table);
            if (old.getValue() instanceof Column) {
                tableAlias = (tableName = ((Column)old.getValue()).getTableAlias());
                if (dataObject instanceof WritableDataObject) {
                    tableName = ((WritableDataObject)dataObject).getOrigTableName(tableAlias);
                }
                tableList.add(Table.getTable(tableName, tableAlias));
            }
            return;
        }
        old_rightCriteria = old.getRightCriteria();
        getTableList(old_leftCriteria, tableList, dataObject);
        if (old_rightCriteria != null) {
            getTableList(old_rightCriteria, tableList, dataObject);
        }
    }
    
    private static Criteria getNewCriteria(final Criteria old, final List tableList) throws MetaDataException {
        Criteria newCriteria = null;
        Criteria old_rightCriteria = null;
        final Criteria old_leftCriteria = old.getLeftCriteria();
        if (old_leftCriteria == null) {
            final Object value = old.getValue();
            final Column column = old.getColumn();
            final Table table = getTable(column.getTableAlias(), tableList);
            final Object toSet = (value instanceof Column) ? value : convertValue(table.getTableName(), column.getColumnName(), value);
            newCriteria = new Criteria(column, toSet, old.getComparator());
            if (old.isNegate()) {
                newCriteria = newCriteria.negate();
            }
            return newCriteria;
        }
        old_rightCriteria = old.getRightCriteria();
        newCriteria = getNewCriteria(old_leftCriteria, tableList);
        if (old_rightCriteria != null) {
            final Criteria newRightCriteria = getNewCriteria(old_rightCriteria, tableList);
            if (" AND ".equals(old.getOperator())) {
                newCriteria = newCriteria.and(newRightCriteria);
            }
            else {
                newCriteria = newCriteria.or(newRightCriteria);
            }
            if (old.isNegate()) {
                newCriteria = newCriteria.negate();
            }
        }
        return newCriteria;
    }
    
    public static boolean setType(final String tableAlias, final Column column, final List<Table> tableList) {
        if (tableList == null || tableList.size() == 0) {
            setType(tableAlias, column);
            return true;
        }
        if (column instanceof Function) {
            if (column.getColumn() == null) {
                final Function function = (Function)column;
                for (final Object arg : function.getFunctionArguments()) {
                    if (arg instanceof Criteria) {
                        setTypeForCriteria((Criteria)arg, tableList);
                    }
                    else if (!(arg instanceof Function.ReservedParameter)) {
                        if (arg instanceof Column) {
                            setType(((Column)arg).getTableAlias(), (Column)arg, tableList);
                        }
                    }
                }
            }
            else {
                setType(column.getColumn().getTableAlias(), column.getColumn(), tableList);
            }
            return true;
        }
        if (column instanceof Operation) {
            final Operation operation = (Operation)column;
            if (operation.getLHSArgument() instanceof Column) {
                setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument(), tableList);
            }
            if (operation.getRHSArgument() instanceof Column) {
                setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument(), tableList);
            }
            return true;
        }
        if (column instanceof CaseExpression) {
            final CaseExpression ce = (CaseExpression)column;
            ce.setType(1);
            for (final CaseExpression.WhenExpr we : ce.getWhenExpressions()) {
                if (we.getExpr() instanceof Criteria) {
                    setTypeForCriteria((Criteria)we.getExpr(), tableList);
                }
                else if (we.getExpr() instanceof Column) {
                    final Column modifiedColumn = (Column)we.getExpr();
                    setType(modifiedColumn.getTableAlias(), modifiedColumn, tableList);
                }
                if (we.getValue() instanceof Column) {
                    final Column modifiedColumn = (Column)we.getValue();
                    setType(modifiedColumn.getTableAlias(), modifiedColumn, tableList);
                }
            }
            if (ce.getElseVal() != null && ce.getElseVal() instanceof Column) {
                final Column modifiedColumn2 = (Column)ce.getElseVal();
                setType(modifiedColumn2.getTableAlias(), modifiedColumn2, tableList);
            }
            if (column.getTableAlias() != null) {
                final Table table = getTable(column.getTableAlias(), tableList);
                if (table instanceof DerivedTable) {
                    setDerivedTablesColumnType((DerivedTable)table, column);
                }
                else {
                    setType(table.getTableName(), column);
                }
            }
            return true;
        }
        if (column.getColumn() != null) {
            final Column modifiedColumn3 = column.getColumn();
            setFuncColType(column, modifiedColumn3.getType());
            setType(modifiedColumn3.getTableAlias(), modifiedColumn3, tableList);
            return true;
        }
        if (column.getTableAlias() != null && (column.getColumnName() == null || !column.getColumnName().equals("*"))) {
            final Table table2 = getTable(column.getTableAlias(), tableList);
            if (null != table2) {
                if (!(table2 instanceof DerivedTable)) {
                    setType(table2.getTableName(), column);
                    return true;
                }
                setDerivedTablesColumnType((DerivedTable)table2, column);
            }
        }
        return false;
    }
    
    public static void setTypeForCriteria(final Criteria criteria) {
        setTypeForCriteria(criteria, null);
    }
    
    private static boolean setType(final String tableAlias, final Column column, final List tableList, final SelectQuery sq) {
        final boolean typeSet = setType(column.getTableAlias(), column, tableList);
        return typeSet || (null != sq && null != sq.getParent() && setType(tableAlias, column, sq.getParent().getTableList(), sq.getParent()));
    }
    
    public static void setTypeForCriteria(final Criteria criteria, final List<Table> tableList) {
        setTypeForCriteria(criteria, tableList, null);
    }
    
    public static void setTypeForCriteria(final Criteria criteria, final List tableList, final SelectQuery sq) {
        if (criteria != null) {
            final Criteria leftCriteria = criteria.getLeftCriteria();
            final Criteria rightCriteria = criteria.getRightCriteria();
            if (leftCriteria == null && rightCriteria == null) {
                Column column = criteria.getColumn();
                if (column instanceof LocaleColumn) {
                    column = ((LocaleColumn)column).getColumn();
                }
                if (column instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)column;
                    final SelectQuery subQuery = (SelectQuery)dc.getSubQuery();
                    final Column sqColumn = subQuery.getSelectColumns().get(0);
                    setDataType(subQuery);
                    if (dc.getType() == 1111) {
                        dc.setType(sqColumn.getType());
                    }
                    return;
                }
                if (column instanceof Function) {
                    final Function function = (Function)column;
                    for (final Object arg : function.getFunctionArguments()) {
                        if (arg instanceof Criteria) {
                            setTypeForCriteria((Criteria)arg, tableList);
                        }
                        else if (!(arg instanceof Function.ReservedParameter)) {
                            if (arg instanceof Column) {
                                setType(((Column)arg).getTableAlias(), (Column)arg, tableList);
                            }
                        }
                    }
                }
                else if (column instanceof Operation) {
                    final Operation operation = (Operation)column;
                    if (operation.getLHSArgument() instanceof Column) {
                        setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument(), tableList);
                    }
                    if (operation.getRHSArgument() instanceof Column) {
                        setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument(), tableList);
                    }
                }
                else {
                    final Table table = getTable(column.getTableAlias(), tableList);
                    if (null != tableList && table instanceof DerivedTable) {
                        setDerivedTablesColumnType((DerivedTable)table, column);
                    }
                    else if (!setType(column.getTableAlias(), column, tableList, sq)) {
                        throw new IllegalArgumentException("Invalid Table alias " + column.getTableAlias() + " specified ");
                    }
                }
                if (criteria.getValue() instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)criteria.getValue();
                    final SelectQuery subQuery = (SelectQuery)dc.getSubQuery();
                    final Column sqColumn = subQuery.getSelectColumns().get(0);
                    setDataType(subQuery);
                    if (dc.getType() == 1111) {
                        dc.setType(sqColumn.getType());
                    }
                    return;
                }
                if (criteria.getValue() instanceof Function) {
                    final Function function = (Function)criteria.getValue();
                    for (final Object arg : function.getFunctionArguments()) {
                        if (arg instanceof Criteria) {
                            setTypeForCriteria((Criteria)arg, tableList);
                        }
                        else if (!(arg instanceof Function.ReservedParameter)) {
                            if (arg instanceof Column) {
                                setType(((Column)arg).getTableAlias(), (Column)arg, tableList);
                            }
                        }
                    }
                }
                else if (criteria.getValue() instanceof Operation) {
                    final Operation operation = (Operation)criteria.getValue();
                    if (operation.getLHSArgument() instanceof Column) {
                        setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument(), tableList);
                    }
                    if (operation.getRHSArgument() instanceof Column) {
                        setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument(), tableList);
                    }
                }
                else if (criteria.getValue() instanceof Column) {
                    final Table table = getTable(((Column)criteria.getValue()).getTableAlias(), tableList);
                    if (table instanceof DerivedTable) {
                        setDerivedTablesColumnType((DerivedTable)table, (Column)criteria.getValue());
                    }
                    else {
                        final Column valCol = (Column)criteria.getValue();
                        setType(valCol.getTableAlias(), valCol, tableList);
                    }
                }
                else {
                    final String strValue = (criteria.getValue() == null) ? "" : criteria.getValue().toString();
                    if (!strValue.startsWith("${") && strValue.endsWith("}")) {
                        criteria.transformValue();
                    }
                }
                if (DataTypeUtil.isUDT(column.getType())) {
                    DataTypeManager.getDataTypeDefinition(column.getDataType()).getMeta().validateCriteriaInput(column, criteria.getValue(), criteria.getComparator(), criteria.isCaseSensitive());
                }
            }
            else {
                if (leftCriteria != null) {
                    setTypeForCriteria(leftCriteria, tableList);
                }
                if (rightCriteria != null) {
                    setTypeForCriteria(rightCriteria, tableList);
                }
            }
        }
    }
    
    public static Object convertValue(final String tableName, final String columnName, final Object value) throws MetaDataException {
        final TableDefinition tDef = getTableDefinition(tableName);
        if (tDef == null) {
            QueryUtil.OUT.log(Level.WARNING, "TableDefinition is Null for Table {0} ", tableName);
            return null;
        }
        final ColumnDefinition colDef = tDef.getColumnDefinitionByName(columnName);
        if (colDef == null) {
            QueryUtil.OUT.log(Level.WARNING, "ColumnDefinition is Null for Column {0} in Table {1}", new Object[] { columnName, tableName });
            return null;
        }
        final String dataType = colDef.getDataType();
        Object returnValue = null;
        try {
            returnValue = convert(value, dataType);
        }
        catch (final MetaDataException metaExp) {
            QueryUtil.OUT.log(Level.WARNING, "Exception while converting the value to corresponding data type ", metaExp);
            throw metaExp;
        }
        QueryUtil.OUT.log(Level.FINE, "Value to returned from convertValue is {0} of type {1} for Column {2} in Table {3} ", new Object[] { returnValue, dataType, columnName, tableName });
        return returnValue;
    }
    
    public static Object convert(final Object value, final String dataType) throws MetaDataException {
        if (value == null) {
            return null;
        }
        Object retVal;
        if (dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR") || dataType.equals("BLOB") || dataType.equals("SBLOB")) {
            if (value instanceof String || value.getClass().isArray()) {
                retVal = value;
            }
            else {
                retVal = String.valueOf(value);
            }
        }
        else {
            Label_0326: {
                if (!dataType.equals("INTEGER")) {
                    if (!dataType.equals("TINYINT")) {
                        break Label_0326;
                    }
                }
                try {
                    if (value.getClass().isArray()) {
                        if (value instanceof Integer[]) {
                            retVal = value;
                        }
                        else if (value instanceof int[]) {
                            final Integer[] intarray = new Integer[((int[])value).length];
                            for (int i = 0; i < ((int[])value).length; ++i) {
                                intarray[i] = Integer.valueOf(String.valueOf(((int[])value)[i]));
                            }
                            retVal = intarray;
                        }
                        else {
                            final Integer[] intarray = new Integer[((Object[])value).length];
                            for (int i = 0; i < ((Object[])value).length; ++i) {
                                intarray[i] = Integer.valueOf(String.valueOf(((Object[])value)[i]));
                            }
                            retVal = intarray;
                        }
                    }
                    else if (value instanceof Integer) {
                        retVal = value;
                    }
                    else {
                        retVal = new Integer(String.valueOf(value));
                    }
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value (" + String.valueOf(value) + ") specified for an INTEGER column: " + String.valueOf(value), nfe);
                }
            }
            if (dataType.equals("BIGINT")) {
                try {
                    if (value.getClass().isArray()) {
                        if (value instanceof Long[]) {
                            retVal = value;
                        }
                        else if (value instanceof long[]) {
                            final Long[] intarray2 = new Long[((long[])value).length];
                            for (int i = 0; i < ((long[])value).length; ++i) {
                                intarray2[i] = Long.valueOf(String.valueOf(((long[])value)[i]));
                            }
                            retVal = intarray2;
                        }
                        else {
                            final Long[] intarray2 = new Long[((Object[])value).length];
                            for (int i = 0; i < ((Object[])value).length; ++i) {
                                intarray2[i] = Long.valueOf(String.valueOf(((Object[])value)[i]));
                            }
                            retVal = intarray2;
                        }
                    }
                    else if (value instanceof Long) {
                        retVal = value;
                    }
                    else {
                        retVal = new Long(String.valueOf(value));
                    }
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value specified for a BIGINT column: " + value, nfe);
                }
            }
            if (!dataType.equals("BOOLEAN")) {
                if (dataType.equals("DOUBLE")) {
                    try {
                        if (value.getClass().isArray()) {
                            if (value instanceof Double[]) {
                                retVal = value;
                            }
                            else if (value instanceof double[]) {
                                final Double[] intarray3 = new Double[((double[])value).length];
                                for (int i = 0; i < ((double[])value).length; ++i) {
                                    intarray3[i] = Double.valueOf(String.valueOf(((double[])value)[i]));
                                }
                                retVal = intarray3;
                            }
                            else {
                                final Double[] intarray3 = new Double[((Object[])value).length];
                                for (int i = 0; i < ((Object[])value).length; ++i) {
                                    intarray3[i] = Double.valueOf(String.valueOf(((Object[])value)[i]));
                                }
                                retVal = intarray3;
                            }
                        }
                        else if (value instanceof Double) {
                            retVal = value;
                        }
                        else {
                            retVal = new Double(String.valueOf(value));
                        }
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a DOUBLE column: " + value, nfe);
                    }
                }
                if (dataType.equals("FLOAT")) {
                    try {
                        if (value.getClass().isArray()) {
                            if (value instanceof Float[]) {
                                retVal = value;
                            }
                            else if (value instanceof float[]) {
                                final Float[] intarray4 = new Float[((float[])value).length];
                                for (int i = 0; i < ((float[])value).length; ++i) {
                                    intarray4[i] = Float.valueOf(String.valueOf(((float[])value)[i]));
                                }
                                retVal = intarray4;
                            }
                            else {
                                final Float[] intarray4 = new Float[((Object[])value).length];
                                for (int i = 0; i < ((Object[])value).length; ++i) {
                                    intarray4[i] = Float.valueOf(String.valueOf(((Object[])value)[i]));
                                }
                                retVal = intarray4;
                            }
                        }
                        else if (value instanceof Float) {
                            retVal = value;
                        }
                        else {
                            retVal = new Float(String.valueOf(value));
                        }
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a FLOAT column: " + value, nfe);
                    }
                }
                if (dataType.equals("DECIMAL")) {
                    try {
                        if (value.getClass().isArray()) {
                            if (value instanceof BigDecimal[]) {
                                retVal = value;
                            }
                            else {
                                final BigDecimal[] intarray5 = new BigDecimal[((Object[])value).length];
                                for (int i = 0; i < ((Object[])value).length; ++i) {
                                    intarray5[i] = new BigDecimal(String.valueOf(((Object[])value)[i]));
                                }
                                retVal = intarray5;
                            }
                        }
                        else if (value instanceof BigDecimal) {
                            retVal = value;
                        }
                        else {
                            retVal = new BigDecimal(String.valueOf(value));
                        }
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a DECIMAL column: " + value, nfe);
                    }
                }
                if (dataType.equals("DATE")) {
                    try {
                        if (value.getClass().isArray()) {
                            if (value instanceof Date[]) {
                                retVal = value;
                            }
                            else {
                                final Date[] intarray6 = new Date[((Object[])value).length];
                                for (int i = 0; i < ((Object[])value).length; ++i) {
                                    intarray6[i] = Date.valueOf(String.valueOf(((Object[])value)[i]));
                                }
                                retVal = intarray6;
                            }
                        }
                        else if (value instanceof Date) {
                            retVal = value;
                        }
                        else {
                            retVal = Date.valueOf(String.valueOf(value));
                        }
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a DATE column: " + value, nfe);
                    }
                }
                if (dataType.equals("TIME")) {
                    try {
                        if (value.getClass().isArray()) {
                            if (value instanceof Time[]) {
                                retVal = value;
                            }
                            else {
                                final Time[] intarray7 = new Time[((Object[])value).length];
                                for (int i = 0; i < ((Object[])value).length; ++i) {
                                    intarray7[i] = Time.valueOf(String.valueOf(((Object[])value)[i]));
                                }
                                retVal = intarray7;
                            }
                        }
                        else if (value instanceof Time) {
                            retVal = value;
                        }
                        else {
                            retVal = Time.valueOf(String.valueOf(value));
                        }
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a TIME column: " + value, nfe);
                    }
                }
                if (!dataType.equals("TIMESTAMP")) {
                    if (!dataType.equals("DATETIME")) {
                        throw new MetaDataException("Unknown data type: " + dataType);
                    }
                }
                try {
                    if (value.getClass().isArray()) {
                        if (value instanceof Timestamp[]) {
                            retVal = value;
                        }
                        else {
                            final Timestamp[] intarray8 = new Timestamp[((Object[])value).length];
                            for (int i = 0; i < ((Object[])value).length; ++i) {
                                intarray8[i] = Timestamp.valueOf(String.valueOf(((Object[])value)[i]));
                            }
                            retVal = intarray8;
                        }
                    }
                    else if (value instanceof Timestamp) {
                        retVal = value;
                    }
                    else {
                        retVal = Timestamp.valueOf(String.valueOf(value));
                    }
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value specified for a TIMESTAMP column: " + value, nfe);
                }
                throw new MetaDataException("Unknown data type: " + dataType);
            }
            if (value instanceof Boolean) {
                retVal = value;
            }
            else {
                retVal = Boolean.valueOf(String.valueOf(value));
            }
        }
        return retVal;
    }
    
    private static TableDefinition getTableDefinition(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new RuntimeException(mde.getMessage(), mde);
        }
    }
    
    public static Criteria getTemplateReplacedCriteria(final Criteria criteria, final Map valuesForTemplates) throws QueryConstructionException {
        return getTemplateReplacedCriteria(criteria, new GenericTemplateHandler(null, valuesForTemplates));
    }
    
    public static Criteria getTemplateReplacedCriteria(final Criteria criteria, final DataObject dObj) throws QueryConstructionException {
        return getTemplateReplacedCriteria(criteria, new DOTemplateHandler(dObj));
    }
    
    public static Criteria getTemplateReplacedCriteria(final Criteria criteria, final DataObject dObj, final Map valuesForTemplates) throws QueryConstructionException {
        return getTemplateReplacedCriteria(criteria, new GenericTemplateHandler(dObj, valuesForTemplates));
    }
    
    private static Criteria getTemplateReplacedCriteria(final Criteria criteria, final TemplateHandler handler) throws QueryConstructionException {
        return getTemplateReplacedCriteria(criteria, handler, null);
    }
    
    public static Criteria getTemplateReplacedCriteria(final Criteria templateCriteria, final Object handler, final Object handlerContext) throws QueryConstructionException {
        final Criteria leftCriteria = templateCriteria.getLeftCriteria();
        final Criteria rightCriteria = templateCriteria.getRightCriteria();
        if (leftCriteria == null) {
            final Object value = templateCriteria.getValue();
            if (value instanceof String) {
                final String trimedValue = ((String)value).trim();
                final int startIndex = trimedValue.indexOf("${");
                final int endIndex = trimedValue.indexOf("}");
                if (startIndex >= 0 && endIndex >= 0 && startIndex < endIndex) {
                    QueryUtil.OUT.log(Level.FINEST, "Trimmed value is : {0}", trimedValue);
                    final int len = trimedValue.length();
                    final String template = trimedValue.substring(startIndex + 2, endIndex);
                    QueryUtil.OUT.log(Level.FINEST, "Template : {0}", template);
                    Object actualValue = null;
                    if (handler instanceof TemplateHandler) {
                        actualValue = ((TemplateHandler)handler).getValue(template);
                    }
                    else if (handler instanceof TemplateVariableHandler) {
                        actualValue = ((TemplateVariableHandler)handler).getValue(template, handlerContext);
                    }
                    if (actualValue instanceof String) {
                        actualValue = trimedValue.replaceAll("\\$\\{" + template + "\\}", (String)actualValue);
                    }
                    QueryUtil.OUT.log(Level.FINEST, "Actual value is : {0} ", actualValue);
                    final Criteria newRelCriteria = new Criteria((Column)templateCriteria.getColumn().clone(), actualValue, templateCriteria.getComparator(), templateCriteria.isCaseSensitive());
                    if (newRelCriteria.isNegate()) {
                        newRelCriteria.negate();
                    }
                    return newRelCriteria;
                }
            }
            return templateCriteria;
        }
        final Criteria newLeftCriteria = getTemplateReplacedCriteria(leftCriteria, handler, handlerContext);
        final Criteria newRightCriteria = getTemplateReplacedCriteria(rightCriteria, handler, handlerContext);
        final String operator = templateCriteria.getOperator();
        final Criteria newLogicalCriteria = operator.equals(" AND ") ? newLeftCriteria.and(newRightCriteria) : newLeftCriteria.or(newRightCriteria);
        return newLogicalCriteria;
    }
    
    private static SelectQuery getSelectQueryForSelectQueryRetrieval(final Long[] queryIDs) {
        final SelectQuery sqForQueryID = new SelectQueryImpl(Table.getTable("SelectQuery"));
        sqForQueryID.addSelectColumn(Column.getColumn(null, "*"));
        sqForQueryID.addJoin(new Join("SelectQuery", "SelectTable", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 2));
        sqForQueryID.addJoin(new Join("SelectTable", "SelectColumn", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join(Table.getTable("SelectColumn"), Table.getTable("SelCol_Expression"), QueryUtil.QID_TALIAS_CINDEX, QueryUtil.QID_TALIAS_CINDEX, 1));
        sqForQueryID.addJoin(new Join("SelectTable", "JoinTable", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("JoinTable", "JoinColumns", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("SelectQuery", "Criteria", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 1));
        sqForQueryID.addJoin(new Join("Criteria", "RelationalCriteria", new String[] { "QUERYID", "CRITERIAID" }, new String[] { "QUERYID", "CRITERIAID" }, 1));
        sqForQueryID.addJoin(new Join(Table.getTable("RelationalCriteria"), Table.getTable("RelCri_Expression"), QueryUtil.QID_CID_RCID, QueryUtil.QID_CID_RCID, 1));
        sqForQueryID.addJoin(new Join("SelectTable", "SortColumn", QueryUtil.QUERYID_TABLEALIAS, QueryUtil.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join(Table.getTable("SortColumn"), Table.getTable("SortCol_Expression"), QueryUtil.QID_TALIAS_SINDEX, QueryUtil.QID_TALIAS_SINDEX, 1));
        sqForQueryID.addJoin(new Join("SelectQuery", "JoinCriteria", QueryUtil.QUERY_ID, QueryUtil.QUERY_ID, 1));
        sqForQueryID.addJoin(new Join("JoinCriteria", "JoinRelCriteria", new String[] { "QUERYID", "CRITERIAID" }, new String[] { "QUERYID", "CRITERIAID" }, 1));
        sqForQueryID.addJoin(new Join(Table.getTable("JoinRelCriteria"), Table.getTable("JoinRelCri_Expression"), QueryUtil.QID_CID_RCID, QueryUtil.QID_CID_RCID, 1));
        final Criteria sqCriteria = new Criteria(Column.getColumn("SelectQuery", "QUERYID"), queryIDs, 8);
        sqForQueryID.setCriteria(sqCriteria);
        sqForQueryID.addSortColumn(new SortColumn(Column.getColumn("SelectColumn", "COLUMNINDEX"), true));
        sqForQueryID.addSortColumn(new SortColumn(Column.getColumn("SortColumn", "SORTINDEX"), true));
        sqForQueryID.addSortColumn(new SortColumn(Column.getColumn("RelationalCriteria", "RELATIONALCRITERIAID"), true));
        sqForQueryID.addSortColumn(new SortColumn(Column.getColumn("JoinRelCriteria", "RELATIONALCRITERIAID"), true));
        return sqForQueryID;
    }
    
    public static boolean compareList(final List list1, final List list2, final boolean ignoreOrder) {
        boolean equals = true;
        boolean isEquals = true;
        if (list1 == null) {
            equals = (list2 == null);
        }
        else if (list2 == null) {
            equals = false;
        }
        else {
            final int size = list1.size();
            if (list2.size() != size) {
                equals = false;
            }
            else {
                for (int i = 0; i < size; ++i) {
                    final Object sq = list1.get(i);
                    isEquals = (ignoreOrder ? (!list2.contains(sq)) : (!list2.get(i).equals(sq)));
                    if (isEquals) {
                        equals = false;
                        break;
                    }
                }
            }
        }
        return equals;
    }
    
    public static void setTypeForHavingCriteria(final Criteria criteria, final List tableList) {
        if (criteria != null) {
            final Criteria leftCriteria = criteria.getLeftCriteria();
            final Criteria rightCriteria = criteria.getRightCriteria();
            Table table = null;
            if (leftCriteria == null && rightCriteria == null) {
                final Column column = criteria.getColumn();
                if (column instanceof Function) {
                    final Function function = (Function)column;
                    for (final Object arg : function.getFunctionArguments()) {
                        if (arg instanceof Criteria) {
                            setTypeForCriteria((Criteria)arg, tableList);
                        }
                        else if (!(arg instanceof Function.ReservedParameter)) {
                            if (arg instanceof Column) {
                                setType(((Column)arg).getTableAlias(), (Column)arg);
                            }
                        }
                    }
                }
                else if (column instanceof Operation) {
                    final Operation operation = (Operation)column;
                    if (operation.getLHSArgument() instanceof Column) {
                        setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument());
                    }
                    if (operation.getRHSArgument() instanceof Column) {
                        setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument());
                    }
                }
                else if (column instanceof CaseExpression) {
                    final CaseExpression ce = (CaseExpression)column;
                    ce.setType(1);
                    for (final CaseExpression.WhenExpr we : ce.getWhenExpressions()) {
                        if (we.getExpr() instanceof Criteria) {
                            setTypeForCriteria((Criteria)we.getExpr(), tableList);
                        }
                        else if (we.getExpr() instanceof Column) {
                            final Column modifiedColumn = (Column)we.getExpr();
                            setType(modifiedColumn.getTableAlias(), modifiedColumn, tableList);
                        }
                        if (we.getValue() instanceof Column) {
                            final Column modifiedColumn = (Column)we.getValue();
                            setType(modifiedColumn.getTableAlias(), modifiedColumn, tableList);
                        }
                    }
                    if (ce.getElseVal() != null && ce.getElseVal() instanceof Column) {
                        final Column modifiedColumn2 = (Column)ce.getElseVal();
                        setType(modifiedColumn2.getTableAlias(), modifiedColumn2, tableList);
                    }
                    if (column.getTableAlias() != null) {
                        table = getTable(column.getTableAlias(), tableList);
                        setType(table.getTableName(), column);
                    }
                }
                else if (column.getColumn() != null && column.getFunction() == 2) {
                    column.setType(4);
                }
                else {
                    Column modifiedColumn3 = column;
                    if (modifiedColumn3.getColumn() != null) {
                        modifiedColumn3 = modifiedColumn3.getColumn();
                    }
                    if (modifiedColumn3.getTableAlias() != null && (modifiedColumn3.getColumnName() == null || !modifiedColumn3.getColumnName().equals("*"))) {
                        table = getTable(modifiedColumn3.getTableAlias(), tableList);
                        if (table instanceof DerivedTable) {
                            setDerivedTablesColumnType((DerivedTable)table, column);
                        }
                        else {
                            setType(table.getTableName(), modifiedColumn3);
                        }
                    }
                }
                if (criteria.getValue() instanceof DerivedColumn) {
                    return;
                }
                if (criteria.getValue() instanceof Column) {
                    if (((Column)criteria.getValue()).getColumn() != null && ((Column)criteria.getValue()).getFunction() == 2) {
                        ((Column)criteria.getValue()).setType(4);
                    }
                    if (criteria.getValue() instanceof Function) {
                        final Function function = (Function)criteria.getValue();
                        for (final Object arg : function.getFunctionArguments()) {
                            if (arg instanceof Criteria) {
                                setTypeForCriteria((Criteria)arg, tableList);
                            }
                            else if (!(arg instanceof Function.ReservedParameter)) {
                                if (arg instanceof Column) {
                                    setType(((Column)arg).getTableAlias(), (Column)arg);
                                }
                            }
                        }
                    }
                    else if (criteria.getValue() instanceof Operation) {
                        final Operation operation = (Operation)criteria.getValue();
                        if (operation.getLHSArgument() instanceof Column) {
                            setType(((Column)operation.getLHSArgument()).getTableAlias(), (Column)operation.getLHSArgument());
                        }
                        if (operation.getRHSArgument() instanceof Column) {
                            setType(((Column)operation.getRHSArgument()).getTableAlias(), (Column)operation.getRHSArgument());
                        }
                    }
                    else {
                        Column modifiedColumn3 = (Column)criteria.getValue();
                        if (modifiedColumn3.getColumn() != null) {
                            modifiedColumn3 = modifiedColumn3.getColumn();
                        }
                        if (modifiedColumn3.getTableAlias() != null && (modifiedColumn3.getColumnName() == null || !modifiedColumn3.getColumnName().equals("*"))) {
                            table = getTable(modifiedColumn3.getTableAlias(), tableList);
                            if (table instanceof DerivedTable) {
                                setDerivedTablesColumnType((DerivedTable)table, column);
                            }
                            else {
                                setType(table.getTableName(), modifiedColumn3);
                            }
                        }
                    }
                }
                else {
                    criteria.transformValue();
                }
            }
            else {
                if (leftCriteria != null) {
                    setTypeForHavingCriteria(leftCriteria, tableList);
                }
                if (rightCriteria != null) {
                    setTypeForHavingCriteria(rightCriteria, tableList);
                }
            }
        }
    }
    
    static SASCachePlugin getQueryCache() {
        if (null == QueryUtil.queryCache) {
            QueryUtil.queryCache = SASCachePlugin.getSASCachePluginImpl();
        }
        return QueryUtil.queryCache;
    }
    
    static CacheRepository getCacheRepo() {
        if (null == QueryUtil.cacheRepository) {
            QueryUtil.cacheRepository = CacheManager.getCacheRepository();
        }
        return QueryUtil.cacheRepository;
    }
    
    public static JSONObject queryToJson(final Query query) {
        return QueryUtil.CONVERTER.fromQuery(query);
    }
    
    public static Query jsonToQuery(final JSONObject jsonObject) {
        return QueryUtil.CONVERTER.toQuery(jsonObject);
    }
    
    public static JSONObject criteriaToJson(final Criteria criteria) {
        return QueryUtil.CONVERTER.fromCriteria(criteria);
    }
    
    public static Criteria jsonToCriteria(final JSONObject jsonObject) {
        return QueryUtil.CONVERTER.toCriteria(jsonObject);
    }
    
    static {
        CLASS_NAME = QueryUtil.class.getName();
        OUT = Logger.getLogger(QueryUtil.CLASS_NAME);
        QueryUtil.selectQueryTableList = null;
        DELIMITERS_FOR_IN = new String[] { "!%%!", "#@@#", ":`@:", "`%#`", "@#%`", "::" };
        (QueryUtil.selectQueryTableList = new ArrayList<String>()).add("SelectQuery");
        QueryUtil.selectQueryTableList.add("SelectTable");
        QueryUtil.selectQueryTableList.add("SelectColumn");
        QueryUtil.selectQueryTableList.add("JoinTable");
        QueryUtil.selectQueryTableList.add("JoinColumns");
        QueryUtil.selectQueryTableList.add("JoinCriteria");
        QueryUtil.selectQueryTableList.add("JoinRelCriteria");
        QueryUtil.selectQueryTableList.add("Criteria");
        QueryUtil.selectQueryTableList.add("RelationalCriteria");
        QueryUtil.selectQueryTableList.add("SortColumn");
        QueryUtil.selectQueryTableList = Collections.unmodifiableList((List<? extends String>)QueryUtil.selectQueryTableList);
        initQueryCache();
        QueryUtil.queryCache = null;
        QueryUtil.cacheRepository = null;
        QUERY_ID = new String[] { "QUERYID" };
        QUERYID_TABLEALIAS = new String[] { "QUERYID", "TABLEALIAS" };
        QID_TALIAS_CINDEX = new String[] { "QUERYID", "TABLEALIAS", "COLUMNINDEX" };
        QID_TALIAS_SINDEX = new String[] { "QUERYID", "TABLEALIAS", "SORTINDEX" };
        QID_CID_RCID = new String[] { "QUERYID", "CRITERIAID", "RELATIONALCRITERIAID" };
        QueryUtil.sortedObjectComparator = new SortedObjectComparator();
        CONVERTER = QueryToJsonConverter.createNewQueryToJsonConverter();
    }
    
    private enum CriteriaType
    {
        WHERE, 
        EXPRESSION_PARAM;
    }
    
    private static class GenericTemplateHandler implements TemplateHandler
    {
        private DataObject dObj;
        private Map valuesForTemplates;
        TemplateHandler[] handlers;
        int len;
        
        GenericTemplateHandler(final DataObject dObj, final Map valuesForTemplates) {
            this.handlers = null;
            this.len = 3;
            (this.handlers = new TemplateHandler[this.len])[0] = new DOTemplateHandler(dObj);
            this.handlers[1] = new ColumnObjKeyTemplateHandler(valuesForTemplates);
            this.handlers[2] = new MapTemplateHandler(valuesForTemplates);
        }
        
        @Override
        public Object getValue(final String templateStr) throws QueryConstructionException {
            QueryUtil.OUT.entering(QueryUtil.CLASS_NAME, "getValue", templateStr);
            for (int i = 0; i < this.len; ++i) {
                final Object value = this.handlers[i].getValue(templateStr);
                final Object[] params = { this.handlers[i], value };
                QueryUtil.OUT.log(Level.FINER, "Value returned is  {1} using the handler {0} ", params);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }
    
    private abstract static class TableColumnBasedTemplateHandler implements TemplateHandler
    {
        @Override
        public Object getValue(final String template) throws QueryConstructionException {
            final int indexOfDot = template.indexOf(".");
            final int len = template.length();
            if (indexOfDot > 0) {
                final String tableName = template.substring(0, indexOfDot);
                final String colName = template.substring(indexOfDot + 1, len);
                return this.getValue(tableName, colName);
            }
            return null;
        }
        
        protected abstract Object getValue(final String p0, final String p1) throws QueryConstructionException;
    }
    
    private static class DOTemplateHandler extends TableColumnBasedTemplateHandler
    {
        private DataObject dObj;
        
        DOTemplateHandler(final DataObject dObj) {
            this.dObj = dObj;
        }
        
        @Override
        protected Object getValue(final String tableName, final String columnName) throws QueryConstructionException {
            final Object[] params = { tableName, columnName };
            QueryUtil.OUT.entering(QueryUtil.CLASS_NAME, "The tableName and columnName received in DOTemplateHandler getValue method is {0} , {1} ", params);
            if (this.dObj == null) {
                return null;
            }
            try {
                final Row row = this.dObj.getFirstRow(tableName);
                return row.get(columnName);
            }
            catch (final DataAccessException dae) {
                throw new QueryConstructionException(dae.toString(), dae);
            }
        }
    }
    
    private static class ColumnObjKeyTemplateHandler extends TableColumnBasedTemplateHandler
    {
        private Map valuesForTemplates;
        
        ColumnObjKeyTemplateHandler(final Map valuesForTemplates) {
            this.valuesForTemplates = valuesForTemplates;
        }
        
        @Override
        protected Object getValue(final String tableName, final String columnName) {
            if (this.valuesForTemplates == null) {
                return null;
            }
            final Column column = Column.getColumn(tableName, columnName);
            return this.valuesForTemplates.get(column);
        }
    }
    
    private static class MapTemplateHandler implements TemplateHandler
    {
        private Map valuesForTemplates;
        
        MapTemplateHandler(final Map valuesForTemplates) {
            this.valuesForTemplates = valuesForTemplates;
        }
        
        @Override
        public Object getValue(final String template) {
            return this.valuesForTemplates.get(template);
        }
    }
    
    private static class SortedObject
    {
        int index;
        Object obj;
        
        SortedObject(final int index, final Object obj) {
            this.index = index;
            this.obj = obj;
        }
        
        int getIndex() {
            return this.index;
        }
        
        Object getObject() {
            return this.obj;
        }
        
        @Override
        public String toString() {
            return this.obj + "@" + this.index;
        }
        
        static List getSortedListOfObjects(final List sortedObjectList) {
            if (sortedObjectList == null) {
                return null;
            }
            QueryUtil.OUT.log(Level.FINEST, "Before sorting..{0}", sortedObjectList);
            Collections.sort((List<Object>)sortedObjectList, QueryUtil.sortedObjectComparator);
            final int size = sortedObjectList.size();
            QueryUtil.OUT.log(Level.FINEST, "After sorting..{0}", sortedObjectList);
            final ArrayList list = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final SortedObject so = sortedObjectList.get(i);
                list.add(so.getObject());
            }
            return list;
        }
    }
    
    private static class SortedObjectComparator implements Comparator
    {
        @Override
        public int compare(final Object vectObj1, final Object vectObj2) {
            final SortedObject so1 = (SortedObject)vectObj1;
            final SortedObject so2 = (SortedObject)vectObj2;
            final int index1 = so1.getIndex();
            final int index2 = so2.getIndex();
            return index1 - index2;
        }
    }
    
    private interface TemplateHandler
    {
        Object getValue(final String p0) throws QueryConstructionException;
    }
    
    public interface TemplateVariableHandler
    {
        String getValue(final String p0, final Object p1);
    }
}
