package com.adventnet.customview.table;

import java.math.BigDecimal;
import com.adventnet.ds.query.Join;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import java.sql.SQLException;
import com.adventnet.model.table.internal.CVTableModelRow;
import java.util.Hashtable;
import com.adventnet.ds.query.DataSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.DerivedTable;
import java.util.HashMap;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.model.Model;
import com.adventnet.model.table.CVTableModelImpl;
import com.adventnet.ds.query.Range;
import java.util.Vector;
import com.adventnet.model.table.TableModelData;
import com.adventnet.persistence.DataAccess;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.customview.CustomViewException;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.customview.CustomViewManagerContext;
import java.util.logging.Logger;
import com.adventnet.customview.service.ServiceProvider;

public class TableModelCoreServiceProvider implements ServiceProvider
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private ServiceProvider nextServiceProvider;
    private CustomViewManagerContext customViewManagerContext;
    private String TABLEMODEL_SERVICECONFIGURATIONAME;
    protected boolean queryPIDX;
    private boolean processTotalCount;
    private boolean fetchPrevPage;
    private boolean setOldRange;
    private boolean isNoCount;
    protected ThreadLocal cvRequestThLocal;
    private CustomViewManager cvMgr;
    
    public TableModelCoreServiceProvider() {
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
        this.TABLEMODEL_SERVICECONFIGURATIONAME = "TABLEMODEL_SERVICE";
        this.queryPIDX = true;
        this.processTotalCount = true;
        this.fetchPrevPage = false;
        this.setOldRange = true;
        this.isNoCount = false;
        this.cvRequestThLocal = new ThreadLocal();
        this.cvMgr = new CustomViewManager() {
            @Override
            public ViewData getData(final CustomViewRequest customViewRequest) throws CustomViewException {
                return TableModelCoreServiceProvider.this.process(customViewRequest);
            }
            
            @Override
            public CustomViewManagerContext getCustomViewManagerContext() {
                return TableModelCoreServiceProvider.this.customViewManagerContext;
            }
        };
    }
    
    @Override
    public String getServiceName() {
        return "TABLE";
    }
    
    public ServiceProvider getNextServiceProvider() {
        return this.nextServiceProvider;
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        this.customViewManagerContext = customViewManagerContext;
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.nextServiceProvider = sp;
    }
    
    @Override
    public void cleanup() {
        TableModelCoreServiceProvider.OUT.finer(" Inside cleanup...");
        this.nextServiceProvider = null;
        this.customViewManagerContext = null;
    }
    
    private String readServiceConfiguration(final CustomViewRequest customViewRequest) throws CustomViewException {
        TableModelCoreServiceProvider.OUT.finer("TEST SOP1 , readServiceConfiguration called");
        final TableModelServiceConfiguration serviceConfigurationForTable = (TableModelServiceConfiguration)customViewRequest.getServiceConfiguration(this.TABLEMODEL_SERVICECONFIGURATIONAME);
        String baseTableName = null;
        if (serviceConfigurationForTable != null) {
            baseTableName = serviceConfigurationForTable.getBaseTableName();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "The baseTable has been set as : {0} in the TableModelServicConfiguration", baseTableName);
            if (baseTableName == null) {
                final SelectQuery sq = customViewRequest.getSelectQuery();
                final List tables = sq.getTableList();
                final Table table = tables.get(0);
                baseTableName = table.getTableName();
                TableModelCoreServiceProvider.OUT.log(Level.FINEST, "Since baseTableName is null in TableModelServicConfiguration, Taking default.{0}", baseTableName);
            }
        }
        return baseTableName;
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        try {
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "THE COMPLETE REQUEST --- {0}", customViewRequest);
            this.cvRequestThLocal.set(customViewRequest);
            DataObject cvConfigDO = customViewRequest.getCustomViewConfiguration();
            if (cvConfigDO == null) {
                try {
                    cvConfigDO = DataAccess.constructDataObject();
                }
                catch (final Exception e) {
                    throw new CustomViewException("Cannot construct DataObject", e);
                }
            }
            final SelectQuery selectQuery = customViewRequest.getSelectQuery();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " Inside process , selectQuery : {0}", selectQuery);
            final String baseTableName = this.readServiceConfiguration(customViewRequest);
            String pidxTableName = null;
            if (baseTableName != null) {
                pidxTableName = this.getPIDXTableName(baseTableName);
            }
            long oldTotal = (long)customViewRequest.getWithDefault("TOTAL", 0L);
            this.isNoCount = (oldTotal == -1L);
            final boolean fetchCountOnly = (boolean)customViewRequest.getWithDefault("fetchCountOnly", false);
            this.fetchPrevPage = (boolean)customViewRequest.getWithDefault("fetchPrevPage", false);
            TableModelData tableModelData = null;
            final int no_of_times_to_fetch_prevPage = (int)customViewRequest.getWithDefault("no_of_times_to_fetch_prevPage", 1);
            if (fetchCountOnly) {
                if (oldTotal > 0L) {
                    throw new CustomViewException("CustomView request already has total. So fetchCount request will not be processed");
                }
                tableModelData = new TableModelData(null);
                tableModelData.setTotal(this.getTotalCount(selectQuery));
            }
            else {
                tableModelData = this.getModelData(selectQuery, baseTableName, pidxTableName, oldTotal);
                int startIndex = (int)tableModelData.getStartIndex();
                if (this.fetchPrevPage && this.isNoCount && startIndex > 1) {
                    for (int iteration_count = 0; tableModelData.getTableData().size() == 0 && iteration_count <= no_of_times_to_fetch_prevPage; tableModelData = this.getModelData(selectQuery, baseTableName, pidxTableName, oldTotal), ++iteration_count) {
                        final int viewLength = selectQuery.getRange().getNumberOfObjects();
                        startIndex = Math.max(startIndex - viewLength + 1, 1);
                        selectQuery.setRange(new Range(startIndex, viewLength));
                        if (iteration_count == no_of_times_to_fetch_prevPage) {
                            oldTotal = 0L;
                        }
                    }
                }
            }
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableModelData : {0}", tableModelData);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " the getModelData method returned ----> : {0}", tableModelData);
            final CVTableModelImpl tableModel = new CVTableModelImpl(tableModelData, cvConfigDO, selectQuery);
            tableModel.setCustomViewRequest(customViewRequest);
            tableModel.setCustomViewManager(this.cvMgr);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableModel : {0}", tableModel);
            final ViewData viewData = new ViewData(cvConfigDO, tableModel);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "Dbg msg from process method, viewData to return == {0}", viewData);
            return viewData;
        }
        catch (final Exception j) {
            j.printStackTrace();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "THEEXCPMSG : {0}", j.getMessage());
            throw new CustomViewException("Exception : " + j.getMessage() + " while processing request", j);
        }
        finally {
            this.cvRequestThLocal.set(null);
        }
    }
    
    private String getPIDXTableName(final String baseTableName) throws CustomViewException {
        try {
            String pidxTableName = null;
            final String dominantTable = PersonalityConfigurationUtil.getDominantTable(baseTableName);
            if (PersonalityConfigurationUtil.isIndexed(dominantTable)) {
                pidxTableName = dominantTable + "_PIDX";
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "pidxTableName -> {0}", pidxTableName);
            }
            return pidxTableName;
        }
        catch (final DataAccessException dae) {
            throw new CustomViewException((Throwable)dae);
        }
    }
    
    private SelectQuery getSelectQueryToFindTablesList(final String pidxTableName) {
        final SelectQuery selectQueryForFetchingTablesList = (SelectQuery)new SelectQueryImpl(Table.getTable(pidxTableName));
        selectQueryForFetchingTablesList.addSelectColumn(Column.getColumn(pidxTableName, "TABLE_NAME"));
        return selectQueryForFetchingTablesList;
    }
    
    private void initializeRange(final SelectQuery selectQuery) {
        final Range range = selectQuery.getRange();
        int _SI = 0;
        int _VL = 0;
        if (range == null) {
            _SI = 1;
            _VL = 10;
        }
        else {
            _SI = selectQuery.getRange().getStartIndex();
            _VL = selectQuery.getRange().getNumberOfObjects();
            if (_SI == 0 && _VL == 0) {
                _VL = 10;
            }
            _SI = Math.max(range.getStartIndex(), 1);
        }
        selectQuery.setRange(new Range(_SI, _VL));
    }
    
    private long checkRangeBasedOnTotal(final SelectQuery selectQuery, long total) throws CustomViewException {
        this.initializeRange(selectQuery);
        final Range range = selectQuery.getRange();
        int startIndex = range.getStartIndex();
        final int viewLength = selectQuery.getRange().getNumberOfObjects();
        if (total == 0L) {
            total = this.getTotalCount(selectQuery);
            if (total == 0L) {
                startIndex = 0;
            }
            else if (total != -1L && startIndex > total) {
                final int pageLength = this.isNoCount ? (viewLength - 1) : viewLength;
                int pageNum = (int)total / pageLength;
                if ((int)total % pageLength == 0) {
                    --pageNum;
                }
                startIndex = pageNum * pageLength + 1;
            }
        }
        selectQuery.setRange(new Range(startIndex, viewLength));
        TableModelCoreServiceProvider.OUT.log(Level.FINE, "startIndex : {0} , viewLength : {1} ", new Object[] { new Integer(startIndex), new Integer(viewLength) });
        return total;
    }
    
    protected HashMap getTableAliasToPKColsMapping(final SelectQuery selectQuery) throws CustomViewException {
        final List tables = selectQuery.getTableList();
        final int tablesCount = tables.size();
        final HashMap tableAliasToPKCols = new HashMap();
        for (int i = 0; i < tablesCount; ++i) {
            final Table table = tables.get(i);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " table : {0}", table);
            if (table instanceof DerivedTable) {
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "Stumbled on a sub-query. So ignoring the processing here.");
            }
            else {
                final String tableName = table.getTableName();
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableName : {0}", tableName);
                final String tableAlias = table.getTableAlias();
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableAlias : {0}", tableAlias);
                TableDefinition tableDefinition;
                try {
                    tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
                }
                catch (final MetaDataException mde) {
                    throw new CustomViewException((Throwable)mde);
                }
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableDefinition : {0}", tableDefinition);
                final PrimaryKeyDefinition pkDefinition = tableDefinition.getPrimaryKey();
                if (pkDefinition != null) {
                    final List keyColumnNamesList = pkDefinition.getColumnList();
                    TableModelCoreServiceProvider.OUT.log(Level.FINER, " keyColumnNamesList : {0}", keyColumnNamesList);
                    tableAliasToPKCols.put(tableAlias, keyColumnNamesList);
                }
            }
        }
        return tableAliasToPKCols;
    }
    
    private Map getData(final List selectColumns, final Map tableAliasToPKCols, final String baseTableName, final boolean formTableList) {
        final Map data = new HashMap();
        final int selectColumnsSize = selectColumns.size();
        final int[] columnTypes = new int[selectColumnsSize];
        final String[] colTypes = new String[selectColumnsSize];
        final Class[] columnClasses = new Class[selectColumnsSize];
        final List keyColumnsList = new ArrayList();
        final List keyColumnIndicesList = new ArrayList();
        final List dominantTableKeyIndices = new ArrayList();
        final List dominantKeyColumnNames = new ArrayList();
        int indexLocator = 0;
        for (int i = 0; i < selectColumnsSize; ++i) {
            final Column selectColumn = selectColumns.get(i);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " selectColumn : {0}", selectColumn);
            final String tableAlias = selectColumn.getTableAlias();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " tableAlias : {0}", tableAlias);
            final String columnName = selectColumn.getColumnName();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " columnName : {0}", columnName);
            columnTypes[i] = selectColumn.getType();
            colTypes[i] = selectColumn.getDataType();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " columnType : {0}", new Integer(columnTypes[i]));
            columnClasses[i] = this.getClassForType(columnTypes[i]);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, " columnClass : {0}", columnClasses[i]);
            final List keyColumnNamesList = tableAliasToPKCols.get(tableAlias);
            if (keyColumnNamesList == null) {
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " keyColumns not found for column : {0}. Ignoring to be functional column.", selectColumn);
            }
            else if (keyColumnNamesList.contains(columnName)) {
                TableModelCoreServiceProvider.OUT.finer(" keyColumnNamesList.contains(columnName)");
                keyColumnsList.add(selectColumn);
                keyColumnIndicesList.add(new Integer(i));
                if (formTableList && tableAlias.equals(baseTableName)) {
                    dominantTableKeyIndices.add(new Integer(indexLocator));
                    dominantKeyColumnNames.add(columnName);
                }
                ++indexLocator;
            }
        }
        TableModelCoreServiceProvider.OUT.log(Level.FINER, " keyColumnsList : {0}", keyColumnsList);
        TableModelCoreServiceProvider.OUT.log(Level.FINER, " keyColumnIndicesList : {0}", keyColumnIndicesList);
        data.put("keyColumnsList", keyColumnsList);
        data.put("keyColumnIndicesList", keyColumnIndicesList);
        data.put("dominantTableKeyIndices", dominantTableKeyIndices);
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "DTKI obtained from the list of keys --- > {0}", dominantTableKeyIndices);
        data.put("dominantKeyColumnNames", dominantKeyColumnNames);
        data.put("columnClassesList", Arrays.asList((Class[])columnClasses));
        data.put("columnSQLTypes", columnTypes);
        data.put("colSQLTypes", colTypes);
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "Displaying DTKI before adding it to the keysList --->{0}", dominantTableKeyIndices);
        return data;
    }
    
    private List getKeysValueList(final int[] keyColumnIndices, final List dominantTableKeyIndices, final DataSet ds, final int numberOfKeyColumns, final int selectColumnsSize, final boolean formTableList) throws CustomViewException {
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "Displaying the DTKI obtained as input to the getKeysValueList method -> {0}", dominantTableKeyIndices);
        final Vector keys = new Vector();
        final Vector tableContents = new Vector();
        final Hashtable keyColVsValues = new Hashtable();
        final List keysValueList = new ArrayList();
        try {
            final int columnCount = selectColumnsSize;
            for (int i = 0; formTableList && i < dominantTableKeyIndices.size(); ++i) {
                keyColVsValues.put(dominantTableKeyIndices.get(i), new ArrayList());
            }
            int i = 0;
            while (ds.next()) {
                final Vector row = new Vector();
                for (int j = 0; j < columnCount; ++j) {
                    row.addElement(ds.getValue(j + 1));
                }
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "adding row[{0}] : {1}", new Object[] { new Integer(i), row });
                final Vector keyForRow = new Vector();
                for (int k = 0; k < numberOfKeyColumns; ++k) {
                    keyForRow.add(ds.getValue(keyColumnIndices[k] + 1));
                }
                if (formTableList) {
                    for (int dSize = dominantTableKeyIndices.size(), d = 0; d < dSize; ++d) {
                        final int elementID = dominantTableKeyIndices.get(d);
                        final List keyList = keyColVsValues.get(new Integer(elementID));
                        keyList.add(keyForRow.elementAt(elementID));
                    }
                }
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "adding keyForRow[{0}] : {1}", new Object[] { new Integer(i), keyForRow });
                tableContents.addElement(new CVTableModelRow(row, keyForRow));
                keys.addElement(keyForRow);
                ++i;
            }
        }
        catch (final SQLException sqle) {
            throw new CustomViewException("SQLException occured when working on the DataSet", sqle);
        }
        keysValueList.add(keys);
        keysValueList.add(tableContents);
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "DISPLAYING keyColVsValues before adding to keysList list : {0}", keyColVsValues);
        keysValueList.add(keyColVsValues);
        return keysValueList;
    }
    
    private Class[] getColumnClassesFromList(final List columnClassesList) {
        final int columnClassesSize = columnClassesList.size();
        final Class[] columnClasses = new Class[columnClassesSize];
        for (int i = 0; i < columnClassesSize; ++i) {
            columnClasses[i] = columnClassesList.get(i);
        }
        return columnClasses;
    }
    
    private void tempMeth() {
    }
    
    private void fillTableList(final List tableContents, final List dominantTableKeyIndices, final List dominantKeyColumnNames, final Hashtable keyColVsValues, final int keyListSize, final String pidxTableName) throws CustomViewException {
        try {
            final SelectQuery selectQueryForFetchingTablesList = this.getSelectQueryToFindTablesList(pidxTableName);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "keyColsVsValues list obtained as input to the getTableListDO method -> {0}", keyColVsValues);
            Criteria tableListCriteria = null;
            final int indicesListSize = dominantTableKeyIndices.size();
            final List criteriaList = new ArrayList();
            tableListCriteria = new Criteria(Column.getColumn(pidxTableName, (String)dominantKeyColumnNames.get(0)), keyColVsValues.get(dominantTableKeyIndices.get(0)).get(0), 0);
            for (int y = 1; y < indicesListSize; ++y) {
                final String dominantKeyColumnName = dominantKeyColumnNames.get(y);
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "aaa DomTableKeyColName -> {0}", dominantKeyColumnName);
                final List keyValuesList = keyColVsValues.get(dominantTableKeyIndices.get(y));
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "bbb keyValuesList -> {0}", keyValuesList);
                final Object keyValue = keyValuesList.get(0);
                TableModelCoreServiceProvider.OUT.log(Level.FINER, "ccc keyValue -> {0}", keyValue);
                tableListCriteria.and(new Criteria(Column.getColumn(pidxTableName, (String)dominantKeyColumnNames.get(y)), keyColVsValues.get(dominantTableKeyIndices.get(y)).get(0), 0));
            }
            TableModelCoreServiceProvider.OUT.log(Level.FINEST, "criteria[0] : {0}", tableListCriteria);
            criteriaList.add(tableListCriteria);
            for (int r = 1; r < keyListSize; ++r) {
                Criteria c = new Criteria(Column.getColumn(pidxTableName, (String)dominantKeyColumnNames.get(0)), keyColVsValues.get(dominantTableKeyIndices.get(0)).get(r), 0);
                for (int z = 1; z < dominantTableKeyIndices.size(); ++z) {
                    final String keyColumnName = dominantKeyColumnNames.get(z);
                    final Object keyValue2 = keyColVsValues.get(dominantTableKeyIndices.get(z)).get(r);
                    c = c.and(new Criteria(Column.getColumn(pidxTableName, keyColumnName), keyValue2, 0));
                }
                TableModelCoreServiceProvider.OUT.log(Level.FINEST, "criteria[{0}] : {1}", new Object[] { new Integer(r), c });
                criteriaList.add(c);
                tableListCriteria = tableListCriteria.or(c);
            }
            selectQueryForFetchingTablesList.setCriteria(tableListCriteria);
            for (int z2 = 0; z2 < dominantTableKeyIndices.size(); ++z2) {
                final Column groupByCol = Column.getColumn(pidxTableName, (String)dominantKeyColumnNames.get(z2));
                selectQueryForFetchingTablesList.addSelectColumn(groupByCol);
            }
            DataObject tableListDataObject = null;
            tableListDataObject = DataAccess.get(selectQueryForFetchingTablesList);
            TableModelCoreServiceProvider.OUT.log(Level.FINEST, "tableListDataObject : {0}", tableListDataObject);
            for (int criteriaListSize = criteriaList.size(), i = 0; i < criteriaListSize; ++i) {
                final Iterator iter = tableListDataObject.getRows(pidxTableName, (Criteria)criteriaList.get(i));
                final List tableList = new ArrayList();
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    final String tableName = (String)row.get("TABLE_NAME");
                    tableList.add(tableName);
                }
                TableModelCoreServiceProvider.OUT.log(Level.FINEST, "tableList[{0}] : {1}", new Object[] { new Integer(i), tableList });
                final CVTableModelRow row2 = tableContents.get(i);
                row2.setTableList(tableList);
            }
        }
        catch (final DataAccessException dae) {
            throw new CustomViewException((Throwable)dae);
        }
    }
    
    Object getContext() throws CustomViewException {
        try {
            return RelationalAPI.getInstance().getConnection();
        }
        catch (final SQLException sqlExp) {
            throw new CustomViewException(sqlExp.getMessage(), sqlExp);
        }
    }
    
    private boolean hasAggregateFunction(final SelectQuery selectQuery) {
        final List selectColumnList = selectQuery.getSelectColumns();
        for (int size = selectColumnList.size(), i = 0; i < size; ++i) {
            final Column sc = selectColumnList.get(i);
            final int function = sc.getFunction();
            if (function != 0 && function != 1) {
                return true;
            }
        }
        return false;
    }
    
    private long getTotalCount(final SelectQuery selectQuery) throws CustomViewException {
        final List groupByColumns = selectQuery.getGroupByColumns();
        if ((groupByColumns != null && groupByColumns.size() > 0) || selectQuery.getGroupByClause() != null || selectQuery.isDistinct()) {
            TableModelCoreServiceProvider.OUT.log(Level.FINEST, "group by columns found, invoking getTotalIneffiently");
            return this.getTotalIneffiently(selectQuery);
        }
        if (this.hasAggregateFunction(selectQuery)) {
            return 1L;
        }
        long total = 0L;
        final SelectQuery selectQueryForTotal = this.getSelectQueryForTotal(selectQuery);
        Object conn = null;
        DataSet ds = null;
        try {
            conn = this.getContext();
            ds = this.execute(selectQueryForTotal, conn);
            while (ds.next()) {
                total += (long)ds.getValue(1, -5);
            }
        }
        catch (final QueryConstructionException qce) {
            throw new CustomViewException("Exception occured during QueryConstruction, Query is \"" + selectQueryForTotal + "\"", (Throwable)qce);
        }
        catch (final SQLException sqle) {
            throw new CustomViewException("Exception occured when executing query, Query is \"" + selectQueryForTotal + "\"", sqle);
        }
        finally {
            this.cleanup(ds, conn);
        }
        return total;
    }
    
    private int getTotalIneffiently(final SelectQuery orgSq) throws CustomViewException {
        SelectQuery sq = (SelectQuery)((SelectQueryImpl)orgSq).clone();
        sq.setRange((Range)null);
        final List<SortColumn> sortColumns = sq.getSortColumns();
        if (sortColumns != null && !sortColumns.isEmpty()) {
            for (final SortColumn sortColumn : sortColumns) {
                sq.removeSortColumn(sortColumn);
            }
        }
        try {
            sq = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)sq);
        }
        catch (final QueryConstructionException e1) {
            e1.printStackTrace();
        }
        final String aliasName = "column";
        final List<Column> selectColumnList = sq.getSelectColumns();
        int columnIndex = 1;
        for (final Column selectColumn : selectColumnList) {
            final Column columnToBeReplaced = (Column)selectColumn.clone();
            columnToBeReplaced.setColumnAlias(aliasName + columnIndex);
            sq.removeSelectColumn(selectColumn);
            sq.addSelectColumn(columnToBeReplaced);
            ++columnIndex;
        }
        final Column count = Column.getColumn((String)null, "*").count();
        count.setColumnAlias("count");
        final SelectQuery outerQuery = (SelectQuery)new SelectQueryImpl((Table)new DerivedTable("INNER_QUERY", (Query)sq));
        outerQuery.addSelectColumn(count);
        DataSet ds = null;
        int total = 0;
        Object conn = null;
        try {
            conn = this.getContext();
            TableModelCoreServiceProvider.OUT.fine("count query for the customView ::" + RelationalAPI.getInstance().getSelectSQL((Query)outerQuery));
            ds = this.execute(outerQuery, conn);
            if (ds.next()) {
                total = (int)ds.getValue(1, 4);
            }
        }
        catch (final QueryConstructionException qce) {
            throw new CustomViewException("Exception occured during QueryConstruction, Query is \"" + sq + "\"", (Throwable)qce);
        }
        catch (final SQLException sqle) {
            throw new CustomViewException("Exception occured when executing query, Query is \"" + sq + "\"", sqle);
        }
        finally {
            this.cleanup(ds, conn);
        }
        TableModelCoreServiceProvider.OUT.log(Level.FINEST, "returning total as {0}", total);
        return total;
    }
    
    DataSet execute(final SelectQuery sql, final Object conn) throws CustomViewException, QueryConstructionException {
        try {
            return RelationalAPI.getInstance().executeQuery((Query)sql, (Connection)conn);
        }
        catch (final SQLException sqlExp) {
            final CustomViewException cusExp = new CustomViewException(sqlExp.getMessage(), sqlExp);
            throw cusExp;
        }
    }
    
    private TableModelData getModelData(final SelectQuery selectQuery, final String baseTableName, final String pidxTableName, final long oldTotal) throws CustomViewException {
        TableModelCoreServiceProvider.OUT.entering("TableModelCoreServiceProvider", "getModel", new Object[] { selectQuery });
        TableModelCoreServiceProvider.OUT.log(Level.FINER, " Inside getModelData {0}", selectQuery);
        TableModelData modelData = null;
        final long total = this.checkRangeBasedOnTotal(selectQuery, oldTotal);
        final HashMap tableAliasToPKCols = this.getTableAliasToPKColsMapping(selectQuery);
        Object conn = null;
        DataSet ds = null;
        try {
            conn = this.getContext();
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "conn : {0}", conn);
            ds = this.execute(selectQuery, conn);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "DataSet : {0}", ds);
            modelData = this.constructTableModelData(ds, selectQuery, tableAliasToPKCols, baseTableName, pidxTableName, total);
            TableModelCoreServiceProvider.OUT.finer("Returning modelData....");
            return modelData;
        }
        catch (final QueryConstructionException qce) {
            throw new CustomViewException("Exception occured during QueryConstruction, Query is \"" + selectQuery + "\"", (Throwable)qce);
        }
        finally {
            this.cleanup(ds, conn);
        }
    }
    
    public void fetchTotalCount(final boolean fetchStatus) {
        this.processTotalCount = fetchStatus;
    }
    
    private TableModelData constructTableModelData(final DataSet ds, final SelectQuery selectQuery, final Map tableAliasToPKCols, final String baseTableName, final String pidxTableName, final long total) throws CustomViewException {
        final List selectColumns = selectQuery.getSelectColumns();
        final boolean formTableList = pidxTableName != null;
        final Map data = this.getData(selectColumns, tableAliasToPKCols, baseTableName, formTableList);
        final List keyColumnsList = data.get("keyColumnsList");
        final List keyColumnIndicesList = data.get("keyColumnIndicesList");
        final List columnClassesList = data.get("columnClassesList");
        final Class[] columnClasses = this.getColumnClassesFromList(columnClassesList);
        final int[] columnTypes = data.get("columnSQLTypes");
        final String[] colTypes = data.get("colSQLTypes");
        final int numberOfKeyColumns = keyColumnsList.size();
        final int[] keyColumnIndices = new int[numberOfKeyColumns];
        for (int i = 0; i < numberOfKeyColumns; ++i) {
            keyColumnIndices[i] = keyColumnIndicesList.get(i);
        }
        List dominantTableKeyIndices = null;
        List dominantKeyColumnNames = null;
        if (formTableList) {
            dominantTableKeyIndices = data.get("dominantTableKeyIndices");
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "DTKI obtained from the list of keys --- > {0}", dominantTableKeyIndices);
            dominantKeyColumnNames = data.get("dominantKeyColumnNames");
        }
        final List keysValueList = this.getKeysValueList(keyColumnIndices, dominantTableKeyIndices, ds, numberOfKeyColumns, selectColumns.size(), formTableList);
        final Vector keys = keysValueList.get(0);
        final Vector tableContents = keysValueList.get(1);
        final int tableContentsSize = tableContents.size();
        if (formTableList && tableContentsSize > 0 && this.queryPIDX) {
            final Hashtable keyColVsValues = keysValueList.get(2);
            TableModelCoreServiceProvider.OUT.log(Level.FINER, "Got the hashTable keyValuesVsValues as follows : {0}", keyColVsValues);
            final int keyListSize = keys.size();
            this.fillTableList(tableContents, dominantTableKeyIndices, dominantKeyColumnNames, keyColVsValues, keyListSize, pidxTableName);
        }
        final TableModelData modelData = new TableModelData(tableContents);
        modelData.setKeys(keys);
        modelData.setKeyColumns(keyColumnsList);
        modelData.setKeyColumnIndices(keyColumnIndices);
        modelData.setColumnClasses(columnClasses);
        modelData.setColumnSQLTypes(columnTypes);
        modelData.setColSQLTypes(colTypes);
        int startIndex = selectQuery.getRange().getStartIndex();
        if (startIndex == 1 && tableContentsSize == 0) {
            startIndex = 0;
        }
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "startIndex : {0}", new Integer(startIndex));
        TableModelCoreServiceProvider.OUT.log(Level.FINER, "total      : {0}", new Long(total));
        modelData.setStartIndex(startIndex);
        modelData.setTotal(total);
        int endIndex = startIndex + tableContentsSize - 1;
        endIndex = Math.max(endIndex, 0);
        modelData.setEndIndex(endIndex);
        return modelData;
    }
    
    protected SelectQuery getSelectQueryForTotal(SelectQuery selectQuery) {
        final List tables = selectQuery.getTableList();
        final List joins = selectQuery.getJoins();
        final List groupByColumns = selectQuery.getGroupByColumns();
        final Criteria criteria = selectQuery.getCriteria();
        final Table baseTable = tables.remove(0);
        final List selectColumns = selectQuery.getSelectColumns();
        selectQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        for (int size = tables.size(), i = 0; i < size; ++i) {
            final Join join = joins.get(i);
            selectQuery.addJoin(join);
        }
        selectQuery.addGroupByColumns(groupByColumns);
        selectQuery.setCriteria(criteria);
        final Column pkColumn = Column.getColumn((String)null, "*");
        Column countStar = pkColumn.count();
        for (int s = selectColumns.size(), j = 0; j < s; ++j) {
            final Column sc = selectColumns.get(j);
            if (sc.getFunction() == 1) {
                countStar = sc.count();
            }
        }
        selectQuery.addSelectColumn(countStar);
        return selectQuery;
    }
    
    private Class getClassForType(final int type) {
        switch (type) {
            case 12: {
                return String.class;
            }
            case -5: {
                return Long.class;
            }
            case 4: {
                return Integer.class;
            }
            case 16: {
                return Boolean.class;
            }
            case 6: {
                return Float.class;
            }
            case 8: {
                return Double.class;
            }
            case 3: {
                return BigDecimal.class;
            }
            default: {
                return String.class;
            }
        }
    }
    
    protected void cleanup(final DataSet ds, final Object conn) {
        TableModelCoreServiceProvider.OUT.log(Level.FINER, " cleanup({0}, {1})", new Object[] { ds, conn });
        if (ds != null) {
            try {
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " closing {0}", ds);
                ds.close();
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " closed {0}", ds);
            }
            catch (final SQLException se) {
                TableModelCoreServiceProvider.OUT.log(Level.SEVERE, "Exception when cleaning up resources : {0}", se);
            }
        }
        if (conn != null) {
            try {
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " closing {0}", conn);
                ((Connection)conn).close();
                TableModelCoreServiceProvider.OUT.log(Level.FINER, " closed {0}", conn);
            }
            catch (final SQLException se) {
                TableModelCoreServiceProvider.OUT.log(Level.SEVERE, "Exception when cleaning up resources : {0}", se);
            }
        }
    }
    
    static {
        CLASS_NAME = TableModelCoreServiceProvider.class.getName();
        OUT = Logger.getLogger(TableModelCoreServiceProvider.CLASS_NAME);
    }
}
