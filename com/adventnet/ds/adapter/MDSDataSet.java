package com.adventnet.ds.adapter;

import java.util.HashSet;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.UnionQuery;
import java.util.Collection;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.DataSourceManager;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Enumeration;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.adapter.mds.DBThreadLocal;
import java.sql.SQLException;
import com.adventnet.ds.query.Range;
import java.util.ArrayList;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;

public class MDSDataSet extends DataSet
{
    private SelectQuery query;
    private SelectQuery modified;
    SortHandler sortHandler;
    List rows;
    List currentRow;
    boolean functionExist;
    List functionHandlers;
    List unionResult;
    DataSet[] dataSets;
    private int startIndex;
    private int noOfObjects;
    int rowsProcessed;
    private int checkStart;
    private MDSContext context;
    private static final Logger OUT;
    
    public MDSDataSet(final MDSContext context, final SelectQuery query, final Hashtable dsAdapterMaps) throws DataSourceException, SQLException {
        super(null, query.getSelectColumns());
        this.query = null;
        this.modified = null;
        this.sortHandler = null;
        this.rows = null;
        this.currentRow = null;
        this.functionExist = false;
        this.functionHandlers = new ArrayList();
        this.unionResult = null;
        this.dataSets = null;
        this.startIndex = -1;
        this.noOfObjects = 0;
        this.rowsProcessed = 0;
        final int size = dsAdapterMaps.size();
        if (size > 0) {
            this.query = query;
            this.context = context;
            this.modified = (SelectQuery)query.clone();
            this.dataSets = new DataSet[size];
            final String[] dsNames = new String[size];
            final Range range = query.getRange();
            if (size > 1) {
                if (range != null) {
                    this.startIndex = range.getStartIndex();
                    this.noOfObjects = range.getNumberOfObjects();
                }
                if (this.startIndex > 1) {
                    this.modified.setRange(new Range(1, this.noOfObjects + this.startIndex - 1));
                }
            }
            SortHandler.doPreProcessing(this.modified);
            this.processCommon(dsAdapterMaps, this.dataSets, dsNames, this.modified);
        }
    }
    
    private void executeAndGetResult(final Hashtable dsAdapterMaps, final DataSet[] dataSets, final String[] dsNames, final SelectQuery modified) throws DataSourceException {
        final Enumeration enu = dsAdapterMaps.keys();
        int i = 0;
        final HashMap dsProps = DBThreadLocal.get();
        try {
            while (enu.hasMoreElements()) {
                final String dsName = enu.nextElement();
                DBThreadLocal.set(dsName);
                try {
                    final SelectQuery clonedQuery = (SelectQuery)modified.clone();
                    final List selCols = RelationalAPI.getSelectColumns(clonedQuery);
                    for (int size = clonedQuery.getSelectColumns().size(), j = 0; j < size; ++j) {
                        clonedQuery.removeSelectColumn(0);
                    }
                    clonedQuery.addSelectColumns(selCols);
                    final DataSourceAdapter adapter = dsAdapterMaps.get(dsName);
                    (dataSets[i] = adapter.executeQuery(this.context, clonedQuery)).fillColumnInfo();
                    QueryUtil.setDataType(clonedQuery);
                }
                catch (final QueryConstructionException e) {
                    throw new DataSourceException(e);
                }
                catch (final SQLException e2) {
                    throw new DataSourceException(e2);
                }
                dsNames[i] = dsName;
                ++i;
            }
        }
        finally {
            DBThreadLocal.set(dsProps);
        }
    }
    
    private void createFunctionHandlers(final String[] dsNames) throws DataSourceException {
        final List selCols = this.query.getSelectColumns();
        for (int selSize = selCols.size(), i = 0; i < selSize; ++i) {
            final Column col = selCols.get(i);
            if (col.getColumn() != null) {
                if (dsNames.length > 1 && col.getFunction() == 2 && col.getColumn().getFunction() == 1) {
                    throw new DataSourceException("Count(Distinct()) is not supported as of now");
                }
                final int functionName = col.getFunction();
                final String function = this.getStrFunction(functionName);
                final FunctionHandler funcHandler = this.getFunctionHandler(function);
                funcHandler.init(this.query, this.modified);
                this.functionHandlers.add(funcHandler);
            }
        }
    }
    
    private String getStrFunction(final int functionName) {
        String function = null;
        if (functionName == 2) {
            function = "COUNT";
        }
        else if (functionName == 5) {
            function = "SUM";
        }
        else if (functionName == 3) {
            function = "MIN";
        }
        else if (functionName == 4) {
            function = "MAX";
        }
        else if (functionName == 6) {
            function = "AVG";
        }
        else if (functionName == 1) {
            function = "DISTINCT";
        }
        return function;
    }
    
    private FunctionHandler getFunctionHandler(final String functionName) throws DataSourceException {
        FunctionHandler funcHandler = null;
        final DataObject dsDo = DataSourceManager.getInstance().getDataSourceInfo("default");
        try {
            final Iterator fnsIt = dsDo.getRows("FunctionHandler");
            while (fnsIt.hasNext()) {
                final Row fnRow = fnsIt.next();
                final String function = (String)fnRow.get("FUNCTIONNAME");
                if (function.equals(functionName)) {
                    final String handlerClass = (String)fnRow.get("HANDLERCLASS");
                    try {
                        funcHandler = (FunctionHandler)Thread.currentThread().getContextClassLoader().loadClass(handlerClass).newInstance();
                        break;
                    }
                    catch (final Exception excp) {
                        throw new DataSourceException(excp.getMessage());
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            MDSDataSet.OUT.log(Level.SEVERE, "Exception : ", e);
        }
        return funcHandler;
    }
    
    @Override
    public boolean next() throws SQLException {
        MDSDataSet.OUT.log(Level.FINER, "NUM OBJECTS {0} and rowsProcessed {1}", new Object[] { new Integer(this.noOfObjects), new Integer(this.rowsProcessed) });
        if (this.noOfObjects != 0 && this.rowsProcessed == this.noOfObjects) {
            return false;
        }
        MDSDataSet.OUT.log(Level.FINER, "BEFORE WHILE checkStart {0} and startIndex {1}", new Object[] { new Integer(this.checkStart), new Integer(this.startIndex) });
        boolean valid = false;
        while (this.checkStart < this.startIndex - 1) {
            valid = this.processNext();
            if (!valid) {
                return false;
            }
            ++this.checkStart;
        }
        valid = this.processNext();
        if (valid) {
            ++this.rowsProcessed;
        }
        return valid;
    }
    
    private boolean processNext() throws SQLException {
        try {
            this.currentRow = null;
            if (this.unionResult != null && this.unionResult.size() > 0) {
                this.currentRow = this.unionResult.remove(0);
                return true;
            }
            if (this.rows != null && this.rows.size() > 0) {
                if (this.rows.size() > 0) {
                    this.currentRow = this.rows.remove(0);
                    return true;
                }
                this.rows = null;
            }
            if (this.sortHandler != null) {
                this.rows = this.sortHandler.getNextRows();
            }
            if (this.functionExist) {
                if (this.rows == null) {
                    this.processDataSets();
                }
                if (this.rows != null) {
                    this.currentRow = this.callFunctionHandlers();
                }
                this.rows = null;
            }
            else {
                if (this.sortHandler == null) {
                    this.processDataSets();
                }
                if (this.rows != null && this.rows.size() > 0) {
                    this.currentRow = this.rows.remove(0);
                }
            }
            MDSDataSet.OUT.log(Level.FINER, "CROW {0} ", this.currentRow);
            if (this.currentRow != null) {
                return true;
            }
        }
        catch (final DataSourceException excp) {
            MDSDataSet.OUT.log(Level.SEVERE, "Exception : ", excp);
        }
        return false;
    }
    
    private void processDataSets() throws SQLException {
        for (int i = 0; this.dataSets != null && i < this.dataSets.length; ++i) {
            final DataSet ds = this.dataSets[i];
            if (!ds.isClosed()) {
                final int cnt = ds.getColumnCount();
                if (ds.next()) {
                    if (this.rows == null) {
                        this.rows = new ArrayList();
                    }
                    final List row = new ArrayList();
                    for (int j = 0; j < cnt; ++j) {
                        row.add(ds.getValue(j + 1));
                    }
                    this.rows.add(row);
                }
            }
        }
    }
    
    @Override
    public Object getValue(final int columnIndex) {
        return this.currentRow.get(columnIndex - 1);
    }
    
    @Override
    public Object getValue(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getValue(columnIndex);
    }
    
    @Override
    public int findColumn(final String columnName) throws SQLException {
        this.checkClosed();
        final List columnNames = this.query.getSelectColumns();
        for (int i = 0; i < columnNames.size(); ++i) {
            final Column col = columnNames.get(i);
            if (col.getColumnName().equals(columnName)) {
                return i + 1;
            }
        }
        throw new SQLException("Column Not Found:" + columnName);
    }
    
    @Override
    public void close() throws SQLException {
        if (this.closed) {
            return;
        }
        if (this.dataSets != null) {
            for (int size = this.dataSets.length, i = 0; i < size; ++i) {
                try (final DataSet dataSet = this.dataSets[i]) {}
            }
        }
        super.close();
    }
    
    private List callFunctionHandlers() throws DataSourceException {
        final List dataList = new ArrayList();
        dataList.addAll(this.query.getSelectColumns());
        for (int handSize = this.functionHandlers.size(), i = 0; i < handSize; ++i) {
            final FunctionHandler functionHandler = this.functionHandlers.get(i);
            functionHandler.processNextRow(this.rows, dataList);
        }
        return dataList;
    }
    
    private boolean doesFunctionExist(final SelectQuery query) {
        final List selectCols = query.getSelectColumns();
        for (int selSize = selectCols.size(), i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null) {
                return true;
            }
        }
        return false;
    }
    
    public MDSDataSet(final UnionQuery query, final Hashtable adapterMaps) throws DataSourceException, SQLException {
        super(null, getSelectCols(query));
        this.query = null;
        this.modified = null;
        this.sortHandler = null;
        this.rows = null;
        this.currentRow = null;
        this.functionExist = false;
        this.functionHandlers = new ArrayList();
        this.unionResult = null;
        this.dataSets = null;
        this.startIndex = -1;
        this.noOfObjects = 0;
        this.rowsProcessed = 0;
        this.unionResult = this.processUnion(query, adapterMaps);
    }
    
    private List processUnion(final UnionQuery query, final Hashtable adapterMaps) throws DataSourceException {
        if (query == null) {
            return null;
        }
        final Query leftQuery = query.getLeftQuery();
        final Query rightQuery = query.getRightQuery();
        final boolean retainDuplicates = query.isRetainDuplicateRows();
        List leftResult = null;
        List rightResult = null;
        if (leftQuery == null || rightQuery == null) {
            return null;
        }
        if (leftQuery instanceof UnionQuery) {
            this.processUnion((UnionQuery)leftQuery, adapterMaps);
        }
        else {
            leftResult = this.processQueryResult((SelectQuery)leftQuery, adapterMaps);
        }
        if (rightQuery instanceof UnionQuery) {
            this.processUnion((UnionQuery)rightQuery, adapterMaps);
        }
        else {
            rightResult = this.processQueryResult((SelectQuery)rightQuery, adapterMaps);
        }
        if (retainDuplicates) {
            leftResult.addAll(rightResult);
            return leftResult;
        }
        return this.mergeResults(leftResult, rightResult);
    }
    
    private List processQueryResult(final SelectQuery query, final Hashtable dsAdapterMaps) throws DataSourceException {
        final List rowList = new ArrayList();
        final int size = dsAdapterMaps.size();
        final DataSet[] dataSets = new DataSet[size];
        final String[] dsNames = new String[size];
        final SelectQuery modified = (SelectQuery)query.clone();
        final SortHandler sortHandler = this.sortHandler;
        SortHandler.doPreProcessing(modified);
        this.processCommon(dsAdapterMaps, dataSets, dsNames, modified);
        List unionRow = new ArrayList();
        boolean exist = true;
        while (exist) {
            if (this.sortHandler != null) {
                this.rows = this.sortHandler.getNextRows();
            }
            if (this.rows != null && this.rows.size() != 0) {
                if (this.functionExist) {
                    unionRow = this.callFunctionHandlers();
                    rowList.add(unionRow);
                }
                else {
                    rowList.addAll(this.rows);
                }
                this.rows = null;
            }
            else {
                exist = false;
            }
        }
        return rowList;
    }
    
    private void processCommon(final Hashtable dsAdapterMaps, final DataSet[] dataSets, final String[] dsNames, final SelectQuery modified) throws DataSourceException {
        if (this.query == null) {
            this.query = modified;
        }
        this.executeAndGetResult(dsAdapterMaps, dataSets, dsNames, modified);
        final List<SortColumn> sortCols = modified.getSortColumns();
        if (sortCols.size() != 0 && dataSets.length > 1) {
            this.sortHandler = new SortHandler(dataSets, this.query);
        }
    }
    
    private void checkSupportedFunction(final String[] dsNames, final Query query) throws DataSourceException {
        List selectCols = null;
        if (query instanceof SelectQuery) {
            selectCols = ((SelectQuery)query).getSelectColumns();
        }
        else {
            selectCols = getSelectCols((UnionQuery)query);
        }
        for (int selSize = selectCols.size(), i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null) {
                final int function = col.getFunction();
                final String strFunction = this.getStrFunction(function);
                this.checkFunctionSupport(dsNames, strFunction);
            }
        }
    }
    
    private void checkFunctionSupport(final String[] dsNames, final String function) throws DataSourceException {
        boolean supported = false;
        final String dsName = null;
        MDSDataSet.OUT.log(Level.FINER, "dsNames size {0}", new Integer(dsNames.length));
        for (int i = 0; i < dsNames.length; ++i) {
            final DataObject dsDo = DataSourceManager.getInstance().getDataSourceInfo(dsNames[i]);
            MDSDataSet.OUT.log(Level.FINER, "checkFunctionSupport with dsName {0} DataSource {1}", new Object[] { dsNames[i], dsDo });
            try {
                final Iterator fnsIt = dsDo.getRows("FunctionHandler");
                while (fnsIt.hasNext()) {
                    final Row fnRow = fnsIt.next();
                    final String functionName = (String)fnRow.get("FUNCTIONNAME");
                    if (function.equals(functionName)) {
                        supported = true;
                        break;
                    }
                }
            }
            catch (final DataAccessException e) {
                MDSDataSet.OUT.log(Level.SEVERE, "Exception ", e);
            }
        }
        if (!supported) {
            throw new DataSourceException("Function " + function + " is not supported in DataSource " + dsName);
        }
    }
    
    private List mergeResults(final List leftResult, final List rightResult) {
        final HashSet hashSet = new HashSet(leftResult);
        hashSet.addAll(rightResult);
        return new ArrayList(hashSet);
    }
    
    private static List getSelectCols(final UnionQuery query) {
        Query current;
        for (current = query; current != null && !(current instanceof SelectQuery); current = ((UnionQuery)current).getLeftQuery()) {}
        final List selectCols = ((SelectQuery)current).getSelectColumns();
        return selectCols;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<MDSDataSet>\n");
        final String currentDs = super.toString();
        sb.append(currentDs);
        final int size = this.dataSets.length;
        if (size > 0) {
            sb.append("<DataSets>\n");
            for (int i = 0; i < size; ++i) {
                final DataSet dataSet = this.dataSets[i];
                if (dataSet != null) {
                    final String temp = dataSet.toString();
                    sb.append(temp);
                }
            }
            sb.append("\n </DataSets>");
        }
        sb.append("\n </MDSDataSet>");
        return sb.toString();
    }
    
    static {
        OUT = Logger.getLogger(MDSDataSet.class.getName());
    }
}
