package com.adventnet.ds;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Map;
import com.adventnet.db.adapter.DBAdapter;
import java.util.Properties;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.Connection;
import javax.sql.DataSource;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.adapter.DataSourceAdapter;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.sql.SQLException;
import com.adventnet.ds.adapter.MDSDataSet;
import java.util.logging.Level;
import com.adventnet.ds.adapter.DataSourceException;
import java.util.List;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.adapter.MDSContext;
import java.util.logging.Logger;
import java.util.Hashtable;

public class DataSourceManager
{
    private static DataSourceManager dsManager;
    private static Hashtable dsVsHandler;
    private static Hashtable dsVsDataSource;
    private static Hashtable dsVsDo;
    private static Hashtable dsNameVsPlugIn;
    private static final Logger LOGGER;
    
    private DataSourceManager() {
    }
    
    public static DataSourceManager getInstance() {
        if (DataSourceManager.dsManager == null) {
            DataSourceManager.dsManager = new DataSourceManager();
        }
        return DataSourceManager.dsManager;
    }
    
    public DataSet executeQuery(final MDSContext context, final SelectQuery query) throws DataSourceException {
        return this.executeQuery(context, query, null);
    }
    
    public DataSet executeQuery(final MDSContext context, final SelectQuery query, List dataSources) throws DataSourceException {
        try {
            List tables = null;
            if (dataSources == null) {
                tables = query.getTableList();
                dataSources = this.getDataSources(tables);
            }
            final Hashtable adapterMaps = this.getAdapterMaps(dataSources);
            DataSourceManager.LOGGER.log(Level.FINER, "Tables are {0} datasources {1}  adapterMaps {2}", new Object[] { dataSources, adapterMaps });
            return new MDSDataSet(context, query, adapterMaps);
        }
        catch (final SQLException excp) {
            throw new DataSourceException(excp.getMessage());
        }
    }
    
    private List getDataSources(final List tables) throws DataSourceException {
        DataObject tableDSDo = null;
        try {
            tableDSDo = DataAccess.get("TableDSMap", (Criteria)null);
        }
        catch (final DataAccessException e) {
            throw new DataSourceException(e.getMessage());
        }
        final int size = tableDSDo.size("TableDSMap");
        if (size == -1) {
            final ArrayList retList = new ArrayList();
            retList.add("default");
            return retList;
        }
        final ArrayList retDataSources = new ArrayList();
        final HashMap tabDSMap = this.getHashMapFromDo(tableDSDo);
        for (int tabSize = tables.size(), i = 0; i < tabSize; ++i) {
            final Table tab = tables.get(i);
            final List dataSources = tabDSMap.get(tab.getTableName());
            if (dataSources != null && dataSources.size() > 0) {
                if (retDataSources.size() == 0) {
                    retDataSources.addAll(dataSources);
                }
                else {
                    final boolean allExist = this.doesAllExist(retDataSources, dataSources);
                    if (!allExist) {
                        throw new DataSourceException("Tables mentioned in SelectQuery are present in conflicting DataSources. This is not supported.");
                    }
                }
            }
        }
        return retDataSources;
    }
    
    private boolean doesAllExist(final List startUp, final List current) {
        if (startUp.size() != current.size()) {
            return false;
        }
        for (int size = current.size(), i = 0; i < size; ++i) {
            final String dataSource = current.get(i);
            if (!startUp.contains(dataSource)) {
                return false;
            }
        }
        return true;
    }
    
    private HashMap getHashMapFromDo(final DataObject tabDsDo) {
        final HashMap map = new HashMap();
        try {
            final Iterator dsIt = tabDsDo.getRows("TableDSMap");
            while (dsIt.hasNext()) {
                final Row row = dsIt.next();
                final String tableName = (String)row.get("TABLENAME");
                final String dsName = (String)row.get("DSNAME");
                List dataSources = map.get(tableName);
                if (dataSources == null) {
                    dataSources = new ArrayList();
                }
                dataSources.add(dsName);
                map.put(tableName, dataSources);
            }
        }
        catch (final DataAccessException e) {
            DataSourceManager.LOGGER.log(Level.SEVERE, "Exception while getting hashmap", e);
        }
        return map;
    }
    
    private Hashtable getAdapterMaps(final List dataSources) {
        final Hashtable hash = new Hashtable();
        final int dsSize = dataSources.size();
        DataSourceManager.LOGGER.log(Level.FINE, "List of DataSources are {0}", dataSources);
        for (int i = 0; i < dsSize; ++i) {
            final String dsName = dataSources.get(i);
            if (isDataSourceActive(dsName)) {
                final DataSourceAdapter adapter = getDSAdapter(dsName);
                DataSourceManager.LOGGER.log(Level.FINE, "Adapter for {0} is --> {1}", new Object[] { dsName, adapter });
                hash.put(dsName, adapter);
            }
        }
        return hash;
    }
    
    public DataSet executeQuery(final UnionQuery query) throws DataSourceException {
        return this.executeQuery(query, null);
    }
    
    public DataSet executeQuery(final UnionQuery query, final List dataSources) throws DataSourceException {
        try {
            final Hashtable adapterMaps = this.getAdapterMaps(dataSources);
            return new MDSDataSet(query, adapterMaps);
        }
        catch (final SQLException excp) {
            throw new DataSourceException(excp.getMessage());
        }
    }
    
    public static MDSContext getContext() throws DataSourceException {
        return getContextFromAdapaters(null);
    }
    
    private static MDSContext getContextFromAdapaters(final List dataSources) throws DataSourceException {
        final MDSContext context = new MDSContext();
        for (final String dsName : DataSourceManager.dsVsHandler.keySet()) {
            if (!dsName.equalsIgnoreCase("RelationalAPI") && isDataSourceActive(dsName)) {
                final DataSourceAdapter adapter = DataSourceManager.dsVsHandler.get(dsName);
                adapter.initForExecution(context);
            }
        }
        return context;
    }
    
    public static void cleanUp(final MDSContext context) throws DataSourceException {
        for (final String dsName : DataSourceManager.dsVsHandler.keySet()) {
            if (!dsName.equals("RelationalAPI") && isDataSourceActive(dsName)) {
                final DataSourceAdapter adapter = DataSourceManager.dsVsHandler.get(dsName);
                DataSourceManager.LOGGER.log(Level.FINE, "Going to invoke cleanup if context is not null ::  dsName :: [{0}]    context :: [{1}]", new Object[] { dsName, context });
                if (context == null) {
                    continue;
                }
                adapter.cleanUp(context);
            }
        }
    }
    
    public DataObject getDataSourceInfo(final String dsName) {
        final DataObject dsDo = DataSourceManager.dsVsDo.get(dsName);
        if (dsDo == null) {
            getDSAdapter(dsName);
        }
        return DataSourceManager.dsVsDo.get(dsName);
    }
    
    public static void removeDataSource(final String dsName) throws Exception {
        DataSourceManager.dsVsHandler.remove(dsName);
        DataSourceManager.dsVsDataSource.remove(dsName);
    }
    
    public static void addTableDSMapping(final DataObject tdsDo) {
        try {
            DataAccess.add(tdsDo);
        }
        catch (final Exception e) {
            DataSourceManager.LOGGER.log(Level.SEVERE, "Exception while adding table", e);
        }
    }
    
    public static DataSourceAdapter getDSAdapter(final String dsName) {
        return DataSourceManager.dsVsHandler.get(dsName);
    }
    
    public static DataSource getDataSource(final String dsName) {
        return DataSourceManager.dsVsDataSource.get(dsName);
    }
    
    @Deprecated
    public static void addDataSource(final String dsName, final DataSource ds, final DataSourceAdapter dsAdapter) {
        if (!dsName.equals("RelationalAPI") && !dsName.equals("default")) {
            try (final Connection c = ds.getConnection()) {
                updateDSStatus(dsName, true);
            }
            catch (final Exception e) {
                try {
                    updateDSStatus(dsName, false);
                }
                catch (final DataAccessException ex) {
                    DataSourceManager.LOGGER.log(Level.SEVERE, "Exception while adding datasource", ex);
                }
                throw new IllegalArgumentException("Invalid parameters specified for datasource");
            }
        }
        DataSourceManager.dsVsDataSource.put(dsName, ds);
        DataSourceManager.dsVsHandler.put(dsName, dsAdapter);
    }
    
    public static boolean addDataSource(final DataObject addedDataSourceDO) throws Exception {
        if (PersistenceInitializer.onSAS()) {
            final Row dsRow = addedDataSourceDO.getRow("DataSource");
            final Properties props = PersistenceInitializer.getDBProps(addedDataSourceDO);
            final DataSource dataSource = PersistenceInitializer.createDataSource(props);
            final DBAdapter dbAdapter = PersistenceInitializer.createDBAdapter(props);
            final String dsName = (String)dsRow.get(2);
            boolean isActive = (boolean)dsRow.get("ISACTIVE");
            try (final Connection c = dataSource.getConnection()) {
                if (!isActive) {
                    isActive = true;
                    updateDSStatus(dsName, true);
                }
            }
            catch (final Exception e) {
                DataSourceManager.LOGGER.fine("[WARNING] Invalid parameters specified for datasource " + dsRow.get("DSNAME"));
                if (isActive) {
                    isActive = false;
                    updateDSStatus(dsName, false);
                }
                return isActive;
            }
            DataSourceManager.dsVsDataSource.put(dsName, dataSource);
            DataSourceManager.dsVsHandler.put(dsName, dbAdapter);
            dbAdapter.initialize(props);
            return isActive;
        }
        final Properties props2 = PersistenceInitializer.getDBProps(addedDataSourceDO);
        return addDataSource(props2);
    }
    
    public static boolean addDataSource(final Properties props) throws Exception {
        final DBAdapter dbAdapter = PersistenceInitializer.createDBAdapter(props);
        if (props.getProperty("exceptionsorterclassname") == null) {
            props.setProperty("exceptionsorterclassname", dbAdapter.getDBSpecificExceptionSorterName());
        }
        if (props.getProperty("aborthandlerclassname") == null && dbAdapter.getDBSpecificAbortHandlerName() != null) {
            props.setProperty("aborthandlerclassname", dbAdapter.getDBSpecificAbortHandlerName());
        }
        final Map map = dbAdapter.splitConnectionURL(props.getProperty("url"));
        props.setProperty("host", map.get("Server"));
        final DataSourcePlugIn dsPlugin = PersistenceInitializer.createDataSourcePlugIn(props);
        final DataSource dataSource = dsPlugin.getDataSource();
        final String dsName = props.getProperty("DSName");
        boolean isActive = Boolean.getBoolean(props.getProperty("isactive"));
        try {
            Connection c = null;
            try {
                c = dataSource.getConnection();
                DataSourceManager.dsNameVsPlugIn.put(dsName, dsPlugin);
                DataSourceManager.dsVsDataSource.put(dsName, dataSource);
                DataSourceManager.dsVsHandler.put(dsName, dbAdapter);
                dbAdapter.initialize(props);
                dbAdapter.validateVersion(c);
                dbAdapter.prepareDatabase(c);
            }
            finally {
                if (c != null) {
                    c.close();
                }
            }
            if (!isActive) {
                isActive = true;
                updateDSStatus(dsName, true);
            }
        }
        catch (final IllegalArgumentException | UnsupportedOperationException iae) {
            throw iae;
        }
        catch (final Exception e) {
            DataSourceManager.LOGGER.log(Level.WARNING, " Invalid parameters specified for datasource {0}", props.getProperty("DSName"));
            if (isActive) {
                isActive = false;
                updateDSStatus(dsName, false);
            }
            e.printStackTrace();
        }
        return isActive;
    }
    
    public static void updateDSStatus(final String dsName, final boolean isActive) throws DataAccessException {
        final UpdateQuery updateQuery = new UpdateQueryImpl("DataSource");
        updateQuery.setUpdateColumn("ISACTIVE", new Boolean(isActive));
        updateQuery.setCriteria(new Criteria(Column.getColumn("DataSource", "DSNAME"), dsName, 0));
        DataAccess.update(updateQuery);
    }
    
    public static boolean isDataSourceActive(final String dsName) {
        return isDataSourceActive(dsName, true);
    }
    
    private static boolean isDataSourceActive(final String dsName, final boolean checkAfterFlush) {
        final DataSource ds = DataSourceManager.dsVsDataSource.get(dsName);
        boolean returnValue = false;
        Connection c = null;
        try {
            c = ds.getConnection();
            final DBAdapter dbadapter = DataSourceManager.dsVsHandler.get(dsName);
            returnValue = dbadapter.isActive(c);
        }
        catch (final Exception e) {
            returnValue = false;
        }
        finally {
            if (c != null) {
                try {
                    c.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
            DataSourceManager.LOGGER.log(Level.FINE, "isDataSourceActive :: Returning [{0}] for dsName [{1}]", new Object[] { returnValue, dsName });
        }
        if (!returnValue) {
            final DataSourcePlugIn dsPlugin = DataSourceManager.dsNameVsPlugIn.get(dsName);
            try {
                dsPlugin.flush();
                if (checkAfterFlush) {
                    returnValue = isDataSourceActive(dsName, false);
                }
            }
            catch (final Exception e3) {
                e3.printStackTrace();
            }
        }
        return returnValue;
    }
    
    public static void flushDataSource(final String dsName) throws Exception {
        final DataSourcePlugIn dsPlugin = DataSourceManager.dsNameVsPlugIn.get(dsName);
        if (dsPlugin != null) {
            dsPlugin.flush();
        }
    }
    
    public static boolean reinitDataSource(final String dsName) throws Exception {
        boolean isActive = false;
        if (dsName.equalsIgnoreCase("default")) {
            throw new IllegalArgumentException("default datasource cannot be reinitialized.");
        }
        if (DataSourceManager.dsVsDataSource.get(dsName) != null) {
            DataSourceManager.dsNameVsPlugIn.remove(dsName);
            DataSourceManager.dsVsDataSource.remove(dsName);
            DataSourceManager.dsVsHandler.remove(dsName);
        }
        final DataObject mdsDO = DataAccess.getForPersonality("DataSource", new Criteria(Column.getColumn("DataSource", "DSNAME"), dsName, 0));
        if (mdsDO != null && !mdsDO.isEmpty()) {
            isActive = addDataSource(mdsDO);
        }
        return isActive;
    }
    
    static {
        DataSourceManager.dsManager = null;
        DataSourceManager.dsVsHandler = new Hashtable();
        DataSourceManager.dsVsDataSource = new Hashtable();
        DataSourceManager.dsVsDo = new Hashtable();
        DataSourceManager.dsNameVsPlugIn = new Hashtable();
        LOGGER = Logger.getLogger(DataSourceManager.class.getName());
    }
}
