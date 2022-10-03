package com.adventnet.db.schema.analyze;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.Map;
import java.util.List;
import javax.sql.DataSource;
import com.adventnet.db.adapter.DBAdapter;

public class SchemaComparatorObject
{
    private DBAdapter srcDBAdapter;
    private DBAdapter destDBAdapter;
    private DataSource srcDataSource;
    private DataSource destDataSource;
    private SchemaAnalyzerUtil.DBType srcDBType;
    private SchemaAnalyzerUtil.DBType destDBType;
    private SchemaComparator.ComparatorType type;
    private SchemaComparatorHandler handler;
    private List<String> tableNames;
    private Map<String, JSONArray> tableVsdiff;
    private boolean exitOnFirstDiff;
    
    public SchemaComparatorObject() {
        this.tableNames = new ArrayList<String>();
        this.tableVsdiff = new ConcurrentHashMap<String, JSONArray>();
        this.exitOnFirstDiff = false;
    }
    
    public void setDBAdapter(final DBAdapter adapter) {
        this.srcDBAdapter = adapter;
    }
    
    public DBAdapter getDBAdapter() {
        return this.srcDBAdapter;
    }
    
    public void setDataSource(final DataSource ds) {
        this.srcDataSource = ds;
    }
    
    public DataSource getDataSource() {
        return this.srcDataSource;
    }
    
    public void setDBType(final SchemaAnalyzerUtil.DBType type) {
        this.srcDBType = type;
    }
    
    public SchemaAnalyzerUtil.DBType getDBType() {
        return this.srcDBType;
    }
    
    public void setSrcDBAdapter(final DBAdapter adapter) {
        this.srcDBAdapter = adapter;
    }
    
    public void setDestDBAdapter(final DBAdapter adapter) {
        this.destDBAdapter = adapter;
    }
    
    public DBAdapter getSrcDBAdapter() {
        return this.srcDBAdapter;
    }
    
    public DBAdapter getDestDBAdapter() {
        return this.destDBAdapter;
    }
    
    public void setSrcDataSource(final DataSource ds) {
        this.srcDataSource = ds;
    }
    
    public void setDestDataSource(final DataSource ds) {
        this.destDataSource = ds;
    }
    
    public DataSource getSrcDataSource() {
        return this.srcDataSource;
    }
    
    public DataSource getDestDataSource() {
        return this.destDataSource;
    }
    
    public void setSrcDBType(final SchemaAnalyzerUtil.DBType type) {
        this.srcDBType = type;
    }
    
    public void setDestDBType(final SchemaAnalyzerUtil.DBType type) {
        this.destDBType = type;
    }
    
    public SchemaAnalyzerUtil.DBType getSrcDBType() {
        return this.srcDBType;
    }
    
    public SchemaAnalyzerUtil.DBType getDestDBType() {
        return this.destDBType;
    }
    
    public void setComparatorType(final SchemaComparator.ComparatorType ctype) {
        this.type = ctype;
    }
    
    public SchemaComparator.ComparatorType getComparatorType() {
        return this.type;
    }
    
    public void setComparatorHandler(final SchemaComparatorHandler shandler) {
        this.handler = shandler;
    }
    
    public SchemaComparatorHandler getComparatorHandler() {
        return this.handler;
    }
    
    public void setTableNames(final List<String> list) {
        this.tableNames = list;
    }
    
    public List<String> getTableNames() {
        return this.tableNames;
    }
    
    public Map<String, JSONArray> getTableVsDiffMap() {
        return this.tableVsdiff;
    }
    
    public void exitOnFirstDiff(final boolean exit) {
        this.exitOnFirstDiff = exit;
    }
    
    public boolean whetherToexitOnFirstDiff() {
        return this.exitOnFirstDiff;
    }
    
    public void setTableVsDiffMap(final Map<String, JSONArray> map) {
        this.tableVsdiff = map;
    }
}
