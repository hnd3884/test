package com.adventnet.db.persistence.metadata;

import java.util.Iterator;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transaction;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.db.util.CreateSchema;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DeploymentNotificationInfo;
import java.io.File;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import java.net.URL;
import java.util.logging.Logger;

public class MetaDataAccess
{
    private static final Logger LOGGER;
    public static final int CREATE_TABLE = 1;
    public static final int ALTER_TABLE = 2;
    public static final int DROP_TABLE = 3;
    public static final int CREATE_TABLES = 4;
    public static final int DROP_TABLES = 5;
    public static final int CREATE_TEMPLATE_INSTANCE = 6;
    public static final int DROP_TEMPLATE_INSTANCE = 7;
    
    public static DataDictionary loadDataDictionary(final URL url, final boolean createTables) throws MetaDataException {
        return loadDataDictionary(url, createTables, null);
    }
    
    public static DataDictionary loadDataDictionary(final URL url, final boolean createTables, String moduleName) throws MetaDataException {
        try {
            final DataDictionary dataDictionary = DataDictionaryParser.getDataDictionary(url);
            if (moduleName == null) {
                moduleName = dataDictionary.getName();
            }
            else if (!moduleName.equals(dataDictionary.getName())) {
                throw new MetaDataException("The name specified in data-dictionary \"" + dataDictionary.getName() + "\" should be the same as the module name " + moduleName);
            }
            MetaDataUtil.addDataDictionaryConfiguration(dataDictionary);
            Transaction oldTransaction = null;
            try {
                if (createTables) {
                    try {
                        final File f = new File(url.getFile());
                        if (f.exists() && f.isFile()) {
                            final File parentDir = f.getParentFile();
                            if (parentDir != null) {
                                oldTransaction = suspendTransaction();
                                final DeploymentNotificationInfo depInfo = new DeploymentNotificationInfo(null, moduleName, parentDir.toURL(), false);
                                final CreateSchema cschema = new CreateSchema(RelationalAPI.getInstance().getDataSource());
                                final String dbServerName = RelationalAPI.getInstance().getDBAdapter().getName();
                                cschema.createDBObjects(depInfo, dbServerName, false, false);
                            }
                        }
                    }
                    catch (final Exception ex) {
                        throw new MetaDataException(ex.getMessage(), ex);
                    }
                    finally {
                        resumeTransaction(oldTransaction);
                    }
                    MetaDataAccess.LOGGER.log(Level.FINEST, "creating tables for module " + moduleName);
                    DataAccess.createTables(dataDictionary.getName());
                }
            }
            catch (final Exception exc) {
                throw new MetaDataException(exc.getMessage(), exc);
            }
            if (SchemaBrowserUtil.isReady()) {
                try {
                    DataAccess.addDataDictionary(dataDictionary);
                }
                catch (final Exception e) {
                    throw new MetaDataException("Error while persisting table data dictionary definition", e);
                }
            }
            return dataDictionary;
        }
        catch (final Exception exc2) {
            if (exc2 instanceof MetaDataException) {
                throw (MetaDataException)exc2;
            }
            throw new MetaDataException("Exception occured while parsing the data-dictionary [" + url.getFile() + "] of module [" + moduleName + "]", exc2);
        }
    }
    
    private static List getTables() throws SQLException {
        final List tablesPresent = RelationalAPI.getInstance().getTables();
        final List tables = new ArrayList();
        for (int size = tablesPresent.size(), i = 0; i < size; ++i) {
            tables.add(tablesPresent.get(i).toLowerCase());
        }
        MetaDataAccess.LOGGER.log(Level.FINEST, "Tables present in the database are {0}", tablesPresent);
        return tables;
    }
    
    private static Transaction suspendTransaction() throws PersistenceException {
        try {
            final Transaction tx = DataAccess.getTransactionManager().getTransaction();
            if (tx != null) {
                return DataAccess.getTransactionManager().suspend();
            }
        }
        catch (final Exception exc) {
            MetaDataAccess.LOGGER.log(Level.WARNING, exc.getMessage(), exc);
            throw new PersistenceException(exc.getMessage());
        }
        return null;
    }
    
    private static void resumeTransaction(final Transaction oldTransaction) throws PersistenceException {
        if (oldTransaction != null) {
            try {
                DataAccess.getTransactionManager().resume(oldTransaction);
            }
            catch (final Exception exc) {
                MetaDataAccess.LOGGER.log(Level.WARNING, exc.getMessage(), exc);
                throw new PersistenceException(exc.getMessage());
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MetaDataAccess.class.getName());
        final TableStateHandler tsh = new TableStateHandler();
        DataAccess.registerForMetaDataChanges(tsh);
    }
    
    class TableStateHandler implements MetaDataChangeListener
    {
        @Override
        public void preMetaDataChange(final MetaDataPreChangeEvent preMDCEvent) {
        }
        
        @Override
        public void metaDataChanged(final MetaDataChangeEvent mdcEvent) {
            final int opType = mdcEvent.getOperationType();
            TableDefinition td = null;
            switch (opType) {
                case 2: {
                    final AlterTableQuery alterTableQuery = (AlterTableQuery)mdcEvent.getObject();
                    final String tableName = alterTableQuery.getTableName();
                    try {
                        td = MetaDataUtil.getTableDefinitionByName(tableName);
                    }
                    catch (final Exception e) {
                        MetaDataAccess.LOGGER.log(Level.WARNING, "unable to get table definition to change state fori the table " + tableName);
                    }
                    break;
                }
                case 3: {
                    td = (TableDefinition)mdcEvent.getObject();
                    break;
                }
                case 5: {
                    final List tables = (List)mdcEvent.getObject();
                    for (final TableDefinition tableDefinition : tables) {
                        final TableDefinition tabDef = tableDefinition;
                        ++tableDefinition.tableState;
                    }
                    break;
                }
            }
            if (td != null) {
                final TableDefinition tableDefinition2 = td;
                ++tableDefinition2.tableState;
            }
        }
    }
}
