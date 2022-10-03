package com.adventnet.persistence.migration;

import java.util.HashMap;
import com.zoho.conf.Configuration;
import java.util.ListIterator;
import com.adventnet.persistence.xml.ConfigurationPopulator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.db.persistence.metadata.ElementTransformer;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.ArrayList;
import java.io.File;
import com.zoho.dddiff.DataDictionaryDiff;
import com.zoho.dddiff.DataDictionaryAggregator;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.adapter.SQLGenerator;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Iterator;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.ds.query.AlterOperation;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Map;
import java.util.logging.Logger;

public class MigrationUtil
{
    private static final Logger OUT;
    private static DDChangeListener ddChangeListener;
    private static final Map<Integer, Integer> INSTALLVSREVERT;
    private static boolean queryExecuted;
    private static boolean isMigrationRunnning;
    private static String server_home;
    
    private MigrationUtil() {
    }
    
    public static void applyDDLChanges(final DDLChanges ddlChanges, final DDChangeListener changeListener) throws Exception {
        applyDDLChanges(ddlChanges, changeListener, true);
    }
    
    private static void applyDDLChanges(final DDLChanges ddlChanges, final DDChangeListener changeListener, final boolean useDataAccess) throws Exception {
        try {
            MigrationUtil.ddChangeListener = changeListener;
            MigrationUtil.isMigrationRunnning = true;
            final List[] installQueries = ddlChanges.getQueries(DDLChanges.DDLOperationType.INSTALL);
            final List[] revertQueries = ddlChanges.getQueries(DDLChanges.DDLOperationType.REVERT);
            for (int i = 0; i < installQueries.length; ++i) {
                final List queries = installQueries[i];
                for (int j = 0; j < queries.size(); ++j) {
                    if (i == DDLChanges.DDLOperation.ADD_UK.ordinal() && queries.get(j) instanceof AlterTableQuery) {
                        final AlterTableQuery revertQuery = setUniqueKeyName(queries.get(j));
                        if (revertQuery != null) {
                            revertQueries[MigrationUtil.INSTALLVSREVERT.get(i)].add(j, revertQuery);
                        }
                    }
                    try {
                        MigrationUtil.queryExecuted = false;
                        final Object revertQuery2 = revertQueries[MigrationUtil.INSTALLVSREVERT.get(i)].get(j);
                        executeQuery(queries.get(j), revertQuery2, useDataAccess);
                    }
                    catch (final Exception e) {
                        MigrationUtil.OUT.log(Level.INFO, "Exception occured while executing...");
                        if (MigrationUtil.ddChangeListener != null) {
                            if (MigrationUtil.ddChangeListener.getMigrationType() == DDChangeListener.MigrationType.INSTALL) {
                                MigrationUtil.ddChangeListener.setMigrationType(DDChangeListener.MigrationType.INSTALL_FAILURE);
                            }
                            else {
                                MigrationUtil.ddChangeListener.setMigrationType(DDChangeListener.MigrationType.UNINSTALL_FAILURE);
                            }
                        }
                        e.printStackTrace();
                        boolean revertStarted = false;
                        for (int k = i; k >= 0; --k) {
                            final List revertQuery3 = revertQueries[MigrationUtil.INSTALLVSREVERT.get(k)];
                            if (k == i) {
                                for (int l = MigrationUtil.queryExecuted ? j : (j - 1); l >= 0; --l) {
                                    if (!revertStarted) {
                                        MigrationUtil.OUT.log(Level.INFO, "\nGoing to revert\n");
                                        revertStarted = true;
                                    }
                                    executeQuery(revertQuery3.get(l), installQueries[k].get(l), useDataAccess);
                                }
                            }
                            else {
                                for (int m = revertQuery3.size() - 1; m >= 0; --m) {
                                    if (!revertStarted) {
                                        MigrationUtil.OUT.log(Level.INFO, "\nGoing to revert\n");
                                        revertStarted = true;
                                    }
                                    executeQuery(revertQuery3.get(m), installQueries[k].get(m), useDataAccess);
                                }
                            }
                        }
                        if (revertStarted) {
                            if (MigrationUtil.ddChangeListener != null) {
                                if (MigrationUtil.ddChangeListener.getMigrationType() == DDChangeListener.MigrationType.INSTALL_FAILURE) {
                                    MigrationUtil.OUT.log(Level.INFO, "Installation revert completed.");
                                }
                                else {
                                    MigrationUtil.OUT.log(Level.INFO, "Uninstallation revert completed.");
                                }
                            }
                            else {
                                MigrationUtil.OUT.log(Level.INFO, "Migration failure revert completed.");
                            }
                        }
                        throw e;
                    }
                }
            }
            if (MigrationUtil.ddChangeListener != null) {
                if (MigrationUtil.ddChangeListener.getMigrationType() == DDChangeListener.MigrationType.INSTALL) {
                    MigrationUtil.OUT.log(Level.INFO, "Installation completed.");
                }
                else {
                    MigrationUtil.OUT.log(Level.INFO, "Uninstallation completed.");
                }
            }
            else {
                MigrationUtil.OUT.log(Level.INFO, "Migration completed.");
            }
        }
        finally {
            MigrationUtil.isMigrationRunnning = false;
        }
    }
    
    public static void applyDDLChanges(final URL[] oldDataDictionariesPath, final URL[] newDataDictionariesPath, final DDChangeListener changeListener) throws Exception {
        final DDLChanges ddlChanges = generateDDDiff(oldDataDictionariesPath, newDataDictionariesPath);
        applyDDLChanges(ddlChanges, changeListener);
    }
    
    public static void applyDDLChanges(final DataDictionariesInfo dataDictionariesInfo, final DDChangeListener changeListener) throws Exception {
        final DDLChanges ddlChanges = generateDDDiff(dataDictionariesInfo);
        applyDDLChanges(ddlChanges, changeListener);
    }
    
    public static void applyExtendedDDChanges(final DDChangeListener ddListener, final boolean ignoreMaxSize) throws Exception {
        MigrationUtil.ddChangeListener = ddListener;
        final ExtendedDDDiff diff = ExtendedDDAggregator.generateChanges(ddListener.getMigrationType(), ignoreMaxSize);
        diff.generateQueries();
        final List[] queries = diff.getQueries();
        executeExtendedDDQueries(queries);
    }
    
    private static void executeExtendedDDQueries(final List[] queries) throws Exception {
        final List installQueries = queries[0];
        final List revertQueries = queries[1];
        for (int i = 0; i < installQueries.size(); ++i) {
            try {
                MigrationUtil.queryExecuted = false;
                executeQuery(installQueries.get(i), revertQueries.get(i), true);
            }
            catch (final Exception e) {
                e.printStackTrace();
                MigrationUtil.OUT.severe("Exception occurred.. Hence reverting the changes");
                for (int j = MigrationUtil.queryExecuted ? i : (i - 1); j >= 0; --j) {
                    executeQuery(revertQueries.get(j), installQueries.get(j), true);
                }
                throw e;
            }
        }
    }
    
    private static AlterTableQuery setUniqueKeyName(final AlterTableQuery atq) {
        AlterTableQuery revertQuery = null;
        try {
            for (final AlterOperation ao : atq.getAlterOperations()) {
                if (ao.getOperationType() == 4) {
                    final UniqueKeyDefinition ukd = (UniqueKeyDefinition)ao.getAlterObject();
                    if (ukd.getName() != null && ukd.getName().length() != 0) {
                        continue;
                    }
                    final String tableName = atq.getTableName();
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                    final List<UniqueKeyDefinition> ukDefs = td.getUniqueKeys();
                    int uks;
                    String ukName;
                    for (uks = ((ukDefs == null) ? 0 : ukDefs.size()), ukName = tableName + "_UK"; td.getUniqueKeyDefinitionByName(ukName + uks) != null; ++uks) {}
                    ukName += uks;
                    ukd.setName(ukName);
                    revertQuery = new AlterTableQueryImpl(tableName);
                    revertQuery.removeUniqueKey(ukName);
                }
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
        return revertQuery;
    }
    
    private static void executeQuery(final Object query, final Object revertQuery, final boolean useDataAccess) throws Exception {
        final RelationalAPI relApi = RelationalAPI.getInstance();
        final SQLGenerator generator = relApi.getDBAdapter().getSQLGenerator();
        if (query instanceof AlterTableQuery) {
            final AlterTableQuery aq = (AlterTableQuery)query;
            if (aq == null) {
                return;
            }
            String queryToBeExecuted = generator.getSQLForAlterTable(aq);
            queryToBeExecuted = generator.getSQLWithHiddenEncryptionKey(queryToBeExecuted);
            MigrationUtil.OUT.log(Level.INFO, "Going to execute query :: {0}, isExecutable :: {1}", new Object[] { queryToBeExecuted, aq.isExecutable() });
            boolean proceed = true;
            if (MigrationUtil.ddChangeListener != null) {
                MigrationUtil.OUT.log(Level.INFO, "Invoking PreListener");
                proceed = MigrationUtil.ddChangeListener.preInvokeForAlterTable(aq);
                MigrationUtil.OUT.log(Level.INFO, "PreListener reteurns " + proceed);
            }
            if (proceed) {
                try {
                    if (useDataAccess) {
                        DataAccess.alterTable(aq);
                    }
                    else {
                        if (!aq.isValid()) {
                            aq.validate();
                        }
                        MetaDataUtil.validateAlterTableQuery(aq);
                        if (aq.isExecutable()) {
                            relApi.alterTable(aq);
                        }
                        MetaDataUtil.alterTableDefinition(aq);
                    }
                    MigrationUtil.queryExecuted = true;
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    if (MigrationUtil.ddChangeListener == null) {
                        throw e;
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Invoking handleException");
                    MigrationUtil.ddChangeListener.handleExceptionForAlterTable(aq, e);
                }
                if (MigrationUtil.ddChangeListener != null) {
                    MigrationUtil.OUT.log(Level.INFO, "Invoking PostListener");
                    MigrationUtil.ddChangeListener.postInvokeForAlterTable(aq);
                }
            }
        }
        else if (query instanceof TableDefinition) {
            final TableDefinition td = (TableDefinition)query;
            MigrationUtil.OUT.log(Level.INFO, "Going to execute query :: {0}", new Object[] { generator.getSQLForCreateTable(td) });
            boolean proceed2 = true;
            if (MigrationUtil.ddChangeListener != null) {
                MigrationUtil.OUT.log(Level.INFO, "Invoking PreListener");
                proceed2 = MigrationUtil.ddChangeListener.preInvokeForCreateTable(td);
                MigrationUtil.OUT.log(Level.INFO, "PreListener reteurns " + proceed2);
            }
            if (proceed2) {
                try {
                    if (useDataAccess) {
                        DataAccess.createTable(td.getModuleName(), td);
                    }
                    else {
                        MetaDataUtil.validateTableDefinition(td);
                        relApi.createTable(td, null);
                        MetaDataUtil.addTableDefinition(td.getModuleName(), td);
                    }
                    MigrationUtil.queryExecuted = true;
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                    if (MigrationUtil.ddChangeListener == null) {
                        throw e2;
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Invoking handleException");
                    MigrationUtil.ddChangeListener.handleExceptionForCreateTable(td, e2);
                }
                if (MigrationUtil.ddChangeListener != null) {
                    MigrationUtil.OUT.log(Level.INFO, "Invoking PostListener");
                    MigrationUtil.ddChangeListener.postInvokeForCreateTable(td);
                }
            }
        }
        else if (query instanceof String) {
            final String tableName = (String)query;
            TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tableDefinition == null) {
                tableDefinition = (TableDefinition)revertQuery;
            }
            MigrationUtil.OUT.log(Level.INFO, "Going to execute query :: {0}", new Object[] { generator.getSQLForDrop(tableName, true) });
            boolean proceed = true;
            if (MigrationUtil.ddChangeListener != null) {
                MigrationUtil.OUT.log(Level.INFO, "Invoking PreListener");
                proceed = MigrationUtil.ddChangeListener.preInvokeForDropTable(tableDefinition);
                MigrationUtil.OUT.log(Level.INFO, "PreListener reteurns " + proceed);
            }
            if (proceed) {
                if (tableDefinition != null && !tableDefinition.isTemplate()) {
                    drop_PIDX_Table(tableName);
                }
                try {
                    if (useDataAccess) {
                        DataAccess.dropTable(tableName);
                    }
                    else {
                        validateDropTable(tableName);
                        relApi.dropTable(tableName, true, null);
                        MetaDataUtil.removeTableDefinition(tableName);
                    }
                    MigrationUtil.queryExecuted = true;
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    if (MigrationUtil.ddChangeListener == null) {
                        throw e;
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Invoking handleException");
                    MigrationUtil.ddChangeListener.handleExceptionForDropTable(tableDefinition, e);
                }
                if (MigrationUtil.ddChangeListener != null) {
                    MigrationUtil.OUT.log(Level.INFO, "Invoking PostListener");
                    MigrationUtil.ddChangeListener.postInvokeForDropTable(tableDefinition);
                }
            }
        }
        else if (query instanceof UpdateQuery) {
            final UpdateQuery uq = (UpdateQuery)query;
            if (uq.getTableName().equals("SB_Applications")) {
                MigrationUtil.OUT.log(Level.INFO, "Going to execute query :: {0}", new Object[] { generator.getSQLForUpdate(uq.getTableName(), uq.getUpdateColumns(), uq.getCriteria()) });
                final Map<Column, Object> values = uq.getUpdateColumns();
                final String ddName = (String)uq.getCriteria().getValue();
                final DataDictionary dd = MetaDataUtil.getDataDictionary(ddName);
                for (final Column col : values.keySet()) {
                    if (col.getColumnName().equals("DC_TYPE")) {
                        final String oldDcType = dd.getDynamicColumnType();
                        final String newDcType = values.get(col);
                        if (oldDcType != null && newDcType != null && !oldDcType.equals(newDcType)) {
                            throw new MetaDataException("The dc-type of data-dictionary \"" + ddName + "\" cannot be changed from \"" + oldDcType + "\" to \"" + newDcType + "\".");
                        }
                        continue;
                    }
                }
                if (useDataAccess) {
                    DataAccess.update(uq);
                }
                for (final Column col : values.keySet()) {
                    if (col.getColumnName().equals("APPL_DESC")) {
                        dd.setDescription(values.get(col));
                    }
                    else if (col.getColumnName().equals("TEMPLATE_META_HANDLER")) {
                        final String newTemplateHandler = values.get(col);
                        dd.setTemplateMetaHandler(newTemplateHandler);
                    }
                    else {
                        if (!col.getColumnName().equals("DC_TYPE")) {
                            continue;
                        }
                        dd.setDynamicColumnType(values.get(col));
                    }
                }
                MigrationUtil.queryExecuted = true;
            }
        }
    }
    
    private static void drop_PIDX_Table(final String tableName) throws Exception {
        final String pidxTableName = tableName + "_PIDX";
        final TableDefinition pidxTableDefinition = MetaDataUtil.getTableDefinitionByName(pidxTableName);
        if (pidxTableDefinition != null) {
            MigrationUtil.OUT.log(Level.INFO, "Going to drop " + tableName + "'s respective PIDX table " + pidxTableName);
            DataAccess.dropTable(pidxTableName);
        }
    }
    
    private static void validateDropTable(final String tableName) throws MetaDataException {
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
        if (td == null) {
            throw new IllegalArgumentException("The specified table [" + tableName + "] doesn't exists");
        }
        if (td.isTemplate() && !tableName.equals(td.getTableName())) {
            throw new UnsupportedOperationException("Not Applicable.[" + tableName + "] is a template-instance");
        }
    }
    
    public static DDLChanges generateDDDiff(final URL[] oldDataDictionariesPath, final URL[] newDataDictionariesPath) throws Exception {
        final DataDictionariesInfo info = new DataDictionariesInfo(oldDataDictionariesPath, newDataDictionariesPath);
        return generateDDDiff(info);
    }
    
    public static DDLChanges generateDDDiff(final DataDictionariesInfo dataDictionariesInfo) throws Exception {
        final DataDictionaryAggregator oldAggregator = getAggregator(dataDictionariesInfo.getOldDataDictionaries());
        final DataDictionaryAggregator newAggregator = getAggregator(dataDictionariesInfo.getNewDataDictionaries());
        final DataDictionaryDiff installDiff = oldAggregator.diff(newAggregator);
        final DDLChanges ddlChanges = new DDLChanges(installDiff);
        if (dataDictionariesInfo.ignoreMaxSizeReduction()) {
            ddlChanges.ignoreMaxSizeReduction();
        }
        ddlChanges.setOperationType(dataDictionariesInfo.getDDLOperationType());
        ddlChanges.generateDDLQueries();
        return ddlChanges;
    }
    
    private static DataDictionaryAggregator getAggregator(final URL[] dataDictionariesPath) {
        final DataDictionaryAggregator aggregator = new DataDictionaryAggregator();
        for (int i = 0; i < dataDictionariesPath.length; ++i) {
            aggregator.addFile(dataDictionariesPath[i]);
        }
        return aggregator;
    }
    
    public static void applyDDLChanges(final String backupModuleDir, final String existingModuleDir, final DDChangeListener changeListener) throws Exception {
        validateDirectoryExists(backupModuleDir, existingModuleDir);
        String existingDDFilesPath = existingModuleDir + File.separator + "dd-files.xml";
        String backupDDFilesPath = backupModuleDir + File.separator + "dd-files.xml";
        File existingDDFile = new File(existingDDFilesPath);
        File backupDDFile = new File(backupDDFilesPath);
        MigrationUtil.OUT.log(Level.INFO, "existingddFilePath :: {0}---> {1}", new String[] { existingDDFilesPath, "" + existingDDFile.exists() });
        MigrationUtil.OUT.log(Level.INFO, "backupddFilePath :: {0}---> {1}", new String[] { backupDDFilesPath, "" + backupDDFile.exists() });
        if (existingDDFile.exists() || backupDDFile.exists()) {
            final List<URL> oldDDs = new ArrayList<URL>();
            final List<URL> newDDs = new ArrayList<URL>();
            if (existingDDFile.exists()) {
                final DataObject ddFileDO = Xml2DoConverter.transform(existingDDFile.toURI().toURL());
                final Iterator ddFiles = ddFileDO.getRows("ConfFile");
                while (ddFiles.hasNext()) {
                    final Row ddEntry = ddFiles.next();
                    final String ddFileName = (String)ddEntry.get("URL");
                    final String oldDDFilePath = backupModuleDir + File.separator + ddFileName;
                    final String newDDFilePath = existingModuleDir + File.separator + ddFileName;
                    final File oldDDFile = new File(oldDDFilePath);
                    final File newDDFile = new File(newDDFilePath);
                    if (oldDDFile.exists() && newDDFile.exists()) {
                        MigrationUtil.OUT.log(Level.INFO, "Going to check whether modified :: :: {0}, {1}", new String[] { oldDDFile.getCanonicalPath(), newDDFile.getCanonicalPath() });
                        oldDDs.add(oldDDFile.toURI().toURL());
                        newDDs.add(newDDFile.toURI().toURL());
                    }
                }
            }
            if (backupDDFile.exists()) {
                final DataObject oldDDFileDO = Xml2DoConverter.transform(backupDDFile.toURI().toURL());
                final DataObject newDDFileDO = Xml2DoConverter.transform(existingDDFile.toURI().toURL());
                Iterator ddFiles2 = oldDDFileDO.getRows("ConfFile");
                while (ddFiles2.hasNext()) {
                    final Row ddEntry2 = ddFiles2.next();
                    final String ddFileName2 = (String)ddEntry2.get("URL");
                    if (getConfFileRow(newDDFileDO, ddFileName2) == null) {
                        File oldDD = new File(backupModuleDir + File.separator + ddFileName2);
                        if (!oldDD.exists()) {
                            oldDD = new File(existingModuleDir + File.separator + ddFileName2);
                        }
                        MigrationUtil.OUT.log(Level.INFO, "Going to removed xml :: :: {0}", new String[] { oldDD.getCanonicalPath() });
                        oldDDs.add(oldDD.toURI().toURL());
                    }
                }
                ddFiles2 = newDDFileDO.getRows("ConfFile");
                while (ddFiles2.hasNext()) {
                    final Row ddEntry2 = ddFiles2.next();
                    final String ddFileName2 = (String)ddEntry2.get("URL");
                    if (getConfFileRow(oldDDFileDO, ddFileName2) == null) {
                        final File newDD = new File(existingModuleDir + File.separator + ddFileName2);
                        MigrationUtil.OUT.log(Level.INFO, "Going to add xml :: :: {0}", new String[] { newDD.getCanonicalPath() });
                        newDDs.add(newDD.toURI().toURL());
                    }
                }
            }
            if (oldDDs.size() > 0 || newDDs.size() > 0) {
                final URL[] oldArray = new URL[oldDDs.size()];
                final URL[] newArray = new URL[newDDs.size()];
                for (int i = 0; i < oldDDs.size(); ++i) {
                    final URL url = oldDDs.get(i);
                    oldArray[i] = url;
                }
                for (int i = 0; i < newDDs.size(); ++i) {
                    final URL url = newDDs.get(i);
                    newArray[i] = url;
                }
                applyDDLChanges(oldArray, newArray, changeListener);
            }
        }
        else {
            existingDDFilesPath = existingModuleDir + File.separator + "data-dictionary.xml";
            existingDDFile = new File(existingDDFilesPath);
            backupDDFilesPath = backupModuleDir + File.separator + "data-dictionary.xml";
            backupDDFile = new File(backupDDFilesPath);
            if (backupDDFile.exists() && existingDDFile.exists()) {
                MigrationUtil.OUT.log(Level.INFO, "Going to check whether modified :: :: {0}, {1}", new String[] { existingDDFile.getCanonicalPath(), backupDDFile.getCanonicalPath() });
                applyDDLChanges(new URL[] { backupDDFile.toURI().toURL() }, new URL[] { existingDDFile.toURI().toURL() }, changeListener);
            }
        }
    }
    
    public static void applyMetaDDChanges(final String oldPersistenceJarPath, final String newPersistenceJarPath, final DDChangeListener metaDDListener, final boolean isInstall) throws Exception {
        final File existingJarFile = new File(newPersistenceJarPath);
        final File backUpJarFile = new File(oldPersistenceJarPath);
        final String metaDDEntry = "com/adventnet/db/persistence/metadata/conf/meta-dd.xml";
        final String oldDDPath = "jar:file:" + backUpJarFile.getCanonicalPath() + "!/" + metaDDEntry;
        final String newDDPath = "jar:file:" + existingJarFile.getCanonicalPath() + "!/" + metaDDEntry;
        final URL oldURL = new URL(oldDDPath);
        final URL newURL = new URL(newDDPath);
        DataDictionariesInfo ddInfo;
        if (isInstall) {
            ddInfo = new DataDictionariesInfo(new URL[] { oldURL }, new URL[] { newURL });
        }
        else {
            ddInfo = new DataDictionariesInfo(new URL[] { newURL }, new URL[] { oldURL });
            metaDDListener.setMigrationType(DDChangeListener.MigrationType.UNINSTALL);
            ddInfo.setDDLOperationType(DDLChanges.DDLOperationType.REVERT);
        }
        ddInfo.ignoreMaxSizeReductionOnRevert();
        final DDLChanges ddlChanges = generateDDDiff(ddInfo);
        if (isInstall) {
            applyDDLChanges(ddlChanges, metaDDListener, false);
            MigrationUtil.OUT.log(Level.INFO, "Applied meta-dd.xml changes in DB.");
            final DataDictionary dd = ElementTransformer.getDataDictionary(newURL);
            MetaDataUtil.removeDataDictionaryConfiguration(dd.getName());
            MetaDataUtil.addDataDictionaryConfiguration(dd);
            MigrationUtil.OUT.log(Level.INFO, "Updating meta-dd.xml changes in MetaPersistence tables started.");
            updateMetaDataEntriesForDDLChanges(ddlChanges.getQueries(DDLChanges.DDLOperationType.INSTALL));
            MigrationUtil.OUT.log(Level.INFO, "Updating meta-dd.xml changes in MetaPersistence completed.");
        }
        else {
            MigrationUtil.OUT.log(Level.INFO, "Updating meta-dd.xml changes in MetaPersistence tables started.");
            updateMetaDataEntriesForDDLChanges(ddlChanges.getQueries(DDLChanges.DDLOperationType.INSTALL));
            MigrationUtil.OUT.log(Level.INFO, "Updating meta-dd.xml changes in MetaPersistence completed.");
            applyDDLChanges(ddlChanges, metaDDListener, false);
            MigrationUtil.OUT.log(Level.INFO, "Applied meta-dd.xml changes in DB.");
        }
        MigrationUtil.OUT.log(Level.INFO, "Updating meta-dd.xml changes completed.....");
    }
    
    private static void updateMetaDataEntriesForDDLChanges(final List[] completeQueries) throws DataAccessException, MetaDataException {
        for (int i = 0; i < completeQueries.length; ++i) {
            final List queries = completeQueries[i];
            for (int j = 0; j < queries.size(); ++j) {
                final Object query = queries.get(j);
                if (query instanceof AlterTableQuery) {
                    final AlterTableQuery atq = (AlterTableQuery)query;
                    final DataObject alterDO = SchemaBrowserUtil.alterTableDefinition(atq);
                    if (!alterDO.isEmpty()) {
                        DataAccess.fillGeneratedValues(alterDO);
                        DataAccess.update(alterDO);
                    }
                }
                else if (query instanceof TableDefinition) {
                    final TableDefinition td = (TableDefinition)query;
                    final DataObject addedData = DataAccess.constructDataObject();
                    SchemaBrowserUtil.addTableDefinitionInDO(td.getModuleName(), td, addedData);
                    SchemaBrowserUtil.fillTableOrderInDO(addedData);
                    DataAccess.add(addedData);
                }
                else if (query instanceof String) {
                    final String tableName = (String)query;
                    final Criteria cri = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0);
                    DataAccess.delete("TableDetails", cri);
                }
                else if (query instanceof UpdateQuery) {
                    final UpdateQuery uq = (UpdateQuery)query;
                    DataAccess.update(uq);
                }
            }
        }
    }
    
    private static void validateDirectoryExists(final String oldDir, final String newDir) {
        if (!new File(oldDir).isDirectory()) {
            throw new IllegalArgumentException("No such directory \"" + oldDir + "\" exists.");
        }
        if (!new File(newDir).isDirectory()) {
            throw new IllegalArgumentException("No such directory \"" + newDir + "\" exists.");
        }
    }
    
    private static Row getConfFileRow(final DataObject dataObject, final String confFileURL) throws DataAccessException {
        final Criteria cri = new Criteria(Column.getColumn("ConfFile", "URL"), confFileURL, 0);
        return dataObject.getRow("ConfFile", cri);
    }
    
    private static List<URL> getDDFiles(final String moduleDir) throws Exception {
        final List<URL> ddFiles = new ArrayList<URL>();
        String ddPath = moduleDir + File.separator + "dd-files.xml";
        File ddFile = new File(ddPath);
        if (ddFile.exists()) {
            final DataObject ddFilesDO = Xml2DoConverter.transform(ddFile.toURI().toURL());
            final Iterator ddFilesEntries = ddFilesDO.getRows("ConfFile");
            while (ddFilesEntries.hasNext()) {
                final Row ddEntry = ddFilesEntries.next();
                final String ddName = (String)ddEntry.get("URL");
                final String ddFilePath = ddFile.getParent() + File.separator + ddName;
                final File DDFile = new File(ddFilePath);
                ddFiles.add(DDFile.toURI().toURL());
            }
        }
        else {
            ddPath = moduleDir + File.separator + "data-dictionary.xml";
            ddFile = new File(ddPath);
            if (ddFile.exists()) {
                ddFiles.add(ddFile.toURI().toURL());
            }
        }
        return ddFiles;
    }
    
    public static DataDictionaryAggregator getAllDataDictionaries(final String confDir) throws Exception {
        final DataDictionaryAggregator aggregator = new DataDictionaryAggregator();
        final String moduleXMLPath = confDir + File.separator + "module.xml";
        final File moduleFile = new File(moduleXMLPath);
        final DataObject moduleDO = Xml2DoConverter.transform(moduleFile.toURI().toURL());
        final Iterator modules = moduleDO.getRows("Module");
        while (modules.hasNext()) {
            final Row moduleRow = modules.next();
            final String moduleName = (String)moduleRow.get("MODULENAME");
            if (moduleName.equals("Persistence")) {
                continue;
            }
            final String moduleDir = confDir + File.separator + moduleName;
            final List<URL> dds = getDDFiles(moduleDir);
            for (final URL url : dds) {
                aggregator.addFile(url);
            }
        }
        return aggregator;
    }
    
    public static DataDictionaryDiff generateDDDiff(final String backupConfDir, final String existingConfDir) throws Exception {
        final DataDictionaryAggregator oldAggregator = getAllDataDictionaries(backupConfDir);
        final DataDictionaryAggregator newAggregator = getAllDataDictionaries(existingConfDir);
        return oldAggregator.diff(newAggregator);
    }
    
    public static void applyDOChanges(final String moduleName, final String backupModuleDir, final String existingModuleDir, final DOXMLChangeListener dOXMLChangeListener) throws Exception {
        MigrationUtil.OUT.log(Level.INFO, "Backup Module Dir :: {0}", backupModuleDir);
        MigrationUtil.OUT.log(Level.INFO, "Existing Module Dir :: {0}", existingModuleDir);
        final String serverHome = new File(MigrationUtil.server_home).getCanonicalPath();
        final String existingConfFilePath = existingModuleDir + File.separator + "conf-files.xml";
        final File existingConfFile = new File(existingConfFilePath);
        final String backupConfFilePath = backupModuleDir + File.separator + "conf-files.xml";
        final File backupConfFile = new File(backupConfFilePath);
        DataObject existingConfFileDO = null;
        DataObject backupConfFileDO = null;
        if (backupConfFile.exists()) {
            backupConfFileDO = Xml2DoConverter.transform(backupConfFile.toURI().toURL());
        }
        if (existingConfFile.exists()) {
            existingConfFileDO = Xml2DoConverter.transform(existingConfFile.toURI().toURL());
            final Iterator existingConfFileEntries = existingConfFileDO.getRows("ConfFile");
            while (existingConfFileEntries.hasNext()) {
                final Row confFileRow = existingConfFileEntries.next();
                final String fileName = (String)confFileRow.get("URL");
                final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                final String backupDOFilePath = backupModuleDir + File.separator + fileName;
                final File existingDOFile = new File(existingDOFilePath);
                final File backupDOFile = new File(backupDOFilePath);
                boolean isExistInBackupConfFile = true;
                if (backupConfFile.exists() && getConfFileRow(backupConfFileDO, fileName) == null) {
                    isExistInBackupConfFile = false;
                }
                if (existingDOFile.exists() && backupDOFile.exists() && isExistInBackupConfFile) {
                    String filePath = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for modified xml [{0}]", filePath);
                        continue;
                    }
                    final String populator = (String)confFileRow.get("POPULATORCLASS");
                    ConfigurationPopulator configurationPopulator = null;
                    if (populator != null && populator.length() > 0) {
                        configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Going to check whether modified :: :: {0}, {1}", new String[] { backupDOFile.getCanonicalPath(), existingDOFile.getCanonicalPath() });
                    XML2DOUpdater.diffXml(existingDOFile, backupDOFile, moduleName, dOXMLChangeListener, configurationPopulator, confFileRow);
                }
                if (existingDOFile.exists() && !isExistInBackupConfFile) {
                    String filePath = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for added xml [{0}]", filePath);
                    }
                    else {
                        final String populator = (String)confFileRow.get("POPULATORCLASS");
                        ConfigurationPopulator configurationPopulator = null;
                        if (populator != null && populator.length() > 0) {
                            configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                        }
                        MigrationUtil.OUT.log(Level.INFO, "Going to add xml :: :: {0}", new String[] { existingDOFile.getCanonicalPath() });
                        XML2DOUpdater.addXml(existingDOFile, backupDOFile, moduleName, confFileRow, dOXMLChangeListener, configurationPopulator);
                    }
                }
            }
        }
        if (backupConfFile.exists()) {
            final ListIterator backupConfFileEntries = getListIterator(backupConfFileDO.getRows("ConfFile"));
            while (backupConfFileEntries.hasPrevious()) {
                final Row confFileRow = backupConfFileEntries.previous();
                final String fileName = (String)confFileRow.get("URL");
                if (!existingConfFile.exists() || getConfFileRow(existingConfFileDO, fileName) == null) {
                    final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                    final String backupDOFilePath = backupModuleDir + File.separator + fileName;
                    final File existingDOFile = new File(existingDOFilePath);
                    final File backupDOFile = new File(backupDOFilePath);
                    String filePath2 = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath2 = filePath2.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath2)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for removed xml [{0}]", filePath2);
                    }
                    else {
                        MigrationUtil.OUT.log(Level.INFO, "Going to removed xml :: :: {0}", new String[] { filePath2 });
                        XML2DOUpdater.removeXml(existingDOFile, backupDOFile, dOXMLChangeListener);
                    }
                }
            }
        }
    }
    
    public static void applyDOChanges(final String backupModuleDir, final String existingModuleDir, final DOXMLChangeListener dOXMLChangeListener) throws Exception {
        final String backUpDir = new File(backupModuleDir).getParentFile().getParent();
        final String moduleName = backupModuleDir.substring((backUpDir + File.separator + "conf" + File.separator).length(), backupModuleDir.length());
        applyDOChanges(moduleName, backupModuleDir, existingModuleDir, dOXMLChangeListener);
    }
    
    public static void applyDOChangesForMickeyTOMickeyLite(final String moduleName, final String existingModuleDir, final String newModuleDir, final DOXMLChangeListener dOXMLChangeListener) throws Exception {
        MigrationUtil.OUT.log(Level.INFO, "Backup Module Dir :: {0}", existingModuleDir);
        MigrationUtil.OUT.log(Level.INFO, "Existing Module Dir :: {0}", newModuleDir);
        final String serverHome = new File(Configuration.getString("server.home")).getCanonicalPath();
        final String newConfFilePath = newModuleDir + File.separator + "conf-files.xml";
        final File newConfFile = new File(newConfFilePath);
        final String existingConfFilePath = existingModuleDir + File.separator + "conf-files.xml";
        final File existingConfFile = new File(existingConfFilePath);
        if (newConfFile.exists()) {
            final DataObject newConfFileDO = Xml2DoConverter.transform(newConfFile.toURI().toURL());
            final Iterator newConfFileEntries = newConfFileDO.getRows("ConfFile");
            while (newConfFileEntries.hasNext()) {
                final Row confFileEntry = newConfFileEntries.next();
                final String fileName = (String)confFileEntry.get("URL");
                final String newDOFilePath = newModuleDir + File.separator + fileName;
                final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                final File newDOFile = new File(newDOFilePath);
                final File existingDOFile = new File(existingDOFilePath);
                boolean isExistInBackupConfFile = true;
                if (existingConfFile.exists() && getConfFileRow(Xml2DoConverter.transform(existingConfFile.toURI().toURL()), "conf/" + fileName) == null) {
                    isExistInBackupConfFile = false;
                }
                if (newDOFile.exists() && existingDOFile.exists() && isExistInBackupConfFile) {
                    String filePath = newDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for modified xml [{0}]", filePath);
                        continue;
                    }
                    final String populator = (String)confFileEntry.get("POPULATORCLASS");
                    ConfigurationPopulator configurationPopulator = null;
                    if (populator != null && populator.length() > 0) {
                        configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Going to check whether modified :: :: {0}, {1}", new String[] { existingDOFile.getCanonicalPath(), newDOFile.getCanonicalPath() });
                    XML2DOUpdater.diffXml(newDOFile, existingDOFile, moduleName, dOXMLChangeListener, configurationPopulator, confFileEntry);
                }
                if (newDOFile.exists() && !isExistInBackupConfFile) {
                    String filePath = newDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for added xml [{0}]", filePath);
                    }
                    else {
                        final String populator = (String)confFileEntry.get("POPULATORCLASS");
                        ConfigurationPopulator configurationPopulator = null;
                        if (populator != null && populator.length() > 0) {
                            configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                        }
                        if (newDOFile.getName().equals("service.xml")) {
                            continue;
                        }
                        MigrationUtil.OUT.log(Level.INFO, "Going to add xml :: :: {0}", new String[] { newDOFile.getCanonicalPath() });
                        XML2DOUpdater.addXml(newDOFile, existingDOFile, moduleName, confFileEntry, dOXMLChangeListener, configurationPopulator);
                    }
                }
            }
        }
        if (existingConfFile.exists()) {
            final DataObject existingConfFileDO = Xml2DoConverter.transform(existingConfFile.toURI().toURL());
            DataObject newConfFileDO2 = null;
            if (newConfFile.exists()) {
                newConfFileDO2 = Xml2DoConverter.transform(newConfFile.toURI().toURL());
            }
            final Iterator existingConfFileEntries = existingConfFileDO.getRows("ConfFile");
            while (existingConfFileEntries.hasNext()) {
                final Row confFileEntry2 = existingConfFileEntries.next();
                String fileName2 = (String)confFileEntry2.get("URL");
                fileName2 = fileName2.substring(fileName2.lastIndexOf("/") + 1);
                if (newConfFileDO2 == null || getConfFileRow(newConfFileDO2, fileName2) == null) {
                    final String newDOFilePath2 = newModuleDir + File.separator + fileName2;
                    final String existingDOFilePath2 = existingModuleDir + File.separator + fileName2;
                    final File newDOFile2 = new File(newDOFilePath2);
                    final File existingDOFile2 = new File(existingDOFilePath2);
                    String filePath = newDOFile2.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for removed xml [{0}]", filePath);
                    }
                    else {
                        MigrationUtil.OUT.log(Level.INFO, "Going to remove xml :: :: {0}", new String[] { filePath });
                        XML2DOUpdater.removeXml(newDOFile2, existingDOFile2, dOXMLChangeListener);
                    }
                }
            }
        }
    }
    
    private static ListIterator getListIterator(final Iterator it) {
        final List<Row> rows = new ArrayList<Row>();
        while (it.hasNext()) {
            final Row r = it.next();
            rows.add(r);
        }
        return rows.listIterator(rows.size());
    }
    
    public static void revertDOChanges(final String moduleName, final String backupModuleDir, final String existingModuleDir, final DOXMLChangeListener dOXMLChangeListener) throws Exception {
        XML2DOUpdater.isIntallProgress = false;
        MigrationUtil.OUT.log(Level.INFO, "Backup Module Dir :: {0}", backupModuleDir);
        MigrationUtil.OUT.log(Level.INFO, "Existing Module Dir :: {0}", existingModuleDir);
        final String serverHome = new File(Configuration.getString("server.home")).getCanonicalPath();
        final String existingConfFilePath = existingModuleDir + File.separator + "conf-files.xml";
        final File existingConfFile = new File(existingConfFilePath);
        final String backupConfFilePath = backupModuleDir + File.separator + "conf-files.xml";
        final File backupConfFile = new File(backupConfFilePath);
        DataObject existingConfFileDO = null;
        DataObject backupConfFileDO = null;
        if (backupConfFile.exists()) {
            backupConfFileDO = Xml2DoConverter.transform(backupConfFile.toURI().toURL());
        }
        if (existingConfFile.exists()) {
            existingConfFileDO = Xml2DoConverter.transform(existingConfFile.toURI().toURL());
            final ListIterator existingConfFileEntries = getListIterator(existingConfFileDO.getRows("ConfFile"));
            while (existingConfFileEntries.hasPrevious()) {
                final Row confFileRow = existingConfFileEntries.previous();
                final String fileName = (String)confFileRow.get("URL");
                final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                final String backupDOFilePath = backupModuleDir + File.separator + fileName;
                final File existingDOFile = new File(existingDOFilePath);
                final File backupDOFile = new File(backupDOFilePath);
                boolean isExistInBackupConfFile = true;
                if (backupConfFile.exists() && getConfFileRow(backupConfFileDO, fileName) == null) {
                    isExistInBackupConfFile = false;
                }
                if (existingDOFile.exists() && backupDOFile.exists() && isExistInBackupConfFile) {
                    String filePath = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for modified xml [{0}]", filePath);
                        continue;
                    }
                    final String populator = (String)confFileRow.get("POPULATORCLASS");
                    ConfigurationPopulator configurationPopulator = null;
                    if (populator != null && populator.length() > 0) {
                        configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                    }
                    MigrationUtil.OUT.log(Level.INFO, "Going to check whether modified :: :: {0}, {1}", new String[] { backupDOFile.getCanonicalPath(), existingDOFile.getCanonicalPath() });
                    XML2DOUpdater.diffXml(existingDOFile, backupDOFile, moduleName, dOXMLChangeListener, configurationPopulator, null);
                }
                if (existingDOFile.exists() && !isExistInBackupConfFile) {
                    String filePath = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for removed xml [{0}]", filePath);
                    }
                    else {
                        MigrationUtil.OUT.log(Level.INFO, "Going to remove xml :: :: {0}", new String[] { existingDOFile.getCanonicalPath() });
                        XML2DOUpdater.removeXml(existingDOFile, backupDOFile, dOXMLChangeListener);
                    }
                }
            }
        }
        if (backupConfFile.exists()) {
            final Iterator backupConfFileEntries = backupConfFileDO.getRows("ConfFile");
            while (backupConfFileEntries.hasNext()) {
                final Row confFileRow = backupConfFileEntries.next();
                final String fileName = (String)confFileRow.get("URL");
                if (existingConfFile.exists() && getConfFileRow(existingConfFileDO, fileName) == null) {
                    final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                    final String backupDOFilePath = backupModuleDir + File.separator + fileName;
                    final File existingDOFile = new File(existingDOFilePath);
                    final File backupDOFile = new File(backupDOFilePath);
                    String filePath2 = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                    filePath2 = filePath2.replaceAll("\\\\", "/");
                    if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath2)) {
                        MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for added xml [{0}]", filePath2);
                    }
                    else {
                        final String populator2 = (String)confFileRow.get("POPULATORCLASS");
                        ConfigurationPopulator configurationPopulator2 = null;
                        if (populator2 != null && populator2.length() > 0) {
                            configurationPopulator2 = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator2).newInstance();
                        }
                        MigrationUtil.OUT.log(Level.INFO, "Going to add xml :: :: {0}", new String[] { filePath2 });
                        XML2DOUpdater.addXml(existingDOFile, backupDOFile, moduleName, confFileRow, dOXMLChangeListener, configurationPopulator2);
                    }
                }
            }
        }
    }
    
    public static void revertDOChanges(final String backupModuleDir, final String existingModuleDir, final DOXMLChangeListener dOXMLChangeListener) throws Exception {
        final String backUpDir = new File(backupModuleDir).getParentFile().getParent();
        MigrationUtil.OUT.log(Level.INFO, "backupDir :: " + backUpDir);
        final String moduleName = backupModuleDir.substring((backUpDir + File.separator + "conf" + File.separator).length(), backupModuleDir.length());
        revertDOChanges(moduleName, backupModuleDir, existingModuleDir, dOXMLChangeListener);
    }
    
    public static void removeDOXMLFiles(final File removedModuleConfFilesXml, final DOXMLChangeListener dOXMLChangeListener, final boolean isInstall) throws Exception {
        XML2DOUpdater.isIntallProgress = isInstall;
        final String serverHome = new File(MigrationUtil.server_home).getCanonicalPath();
        if (removedModuleConfFilesXml.exists()) {
            final DataObject confFileDO = Xml2DoConverter.transform(removedModuleConfFilesXml.toURI().toURL());
            final ListIterator li = getListIterator(confFileDO.getRows("ConfFile"));
            while (li.hasPrevious()) {
                final Row confFileRow = li.previous();
                final String fileName = (String)confFileRow.get("URL");
                final String doXmlFilePath = removedModuleConfFilesXml.getParentFile().getCanonicalPath() + File.separator + fileName;
                final File doXmlFile = new File(doXmlFilePath);
                String filePath = doXmlFile.getCanonicalPath().substring(serverHome.length() + 1);
                filePath = filePath.replaceAll("\\\\", "/");
                if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                    MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for removed xml [{0}]", filePath);
                }
                else {
                    MigrationUtil.OUT.log(Level.INFO, "Going to remove xml :: :: {0}", new String[] { doXmlFile.getCanonicalPath() });
                    XML2DOUpdater.removeXml(doXmlFile, null, dOXMLChangeListener);
                }
            }
        }
        else {
            MigrationUtil.OUT.log(Level.INFO, "No conf-files.xml exists in removed module.");
        }
    }
    
    public static void removeDOXMLFiles(final String existingModuleDir, final String backupModuleDir, final DOXMLChangeListener dOXMLChangeListener, final boolean isInstall) throws Exception {
        XML2DOUpdater.isIntallProgress = isInstall;
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String backupConfFilesPath = backupModuleDir + File.separator + "conf-files.xml";
        final File backupConfFile = new File(backupConfFilesPath);
        final String existingConfFilesPath = existingModuleDir + File.separator + "conf-files.xml";
        final File existingConfFile = new File(existingConfFilesPath);
        DataObject confFileDO = null;
        if (isInstall && backupConfFile.exists()) {
            confFileDO = Xml2DoConverter.transform(backupConfFile.toURI().toURL());
        }
        else if (existingConfFile.exists()) {
            confFileDO = Xml2DoConverter.transform(existingConfFile.toURI().toURL());
        }
        if (confFileDO != null) {
            final ListIterator confFileEntries = getListIterator(confFileDO.getRows("ConfFile"));
            while (confFileEntries.hasPrevious()) {
                final Row confFileEntry = confFileEntries.previous();
                final String fileName = (String)confFileEntry.get("URL");
                final String existingDOFilePath = existingModuleDir + File.separator + fileName;
                final File existingDOFile = new File(existingDOFilePath);
                final String backupDOFilePath = backupModuleDir + File.separator + fileName;
                final File backupDOFile = new File(backupDOFilePath);
                String filePath = existingDOFile.getCanonicalPath().substring(serverHome.length() + 1);
                filePath = filePath.replaceAll("\\\\", "/");
                if (dOXMLChangeListener != null && dOXMLChangeListener.skipXML(filePath)) {
                    MigrationUtil.OUT.log(Level.INFO, "skipXML returning true for removed xml [{0}]", filePath);
                }
                else {
                    MigrationUtil.OUT.log(Level.INFO, "Going to remove xml :: :: {0}", new String[] { existingDOFile.getCanonicalPath() });
                    XML2DOUpdater.removeXml(existingDOFile, backupDOFile, dOXMLChangeListener);
                }
            }
        }
        else {
            MigrationUtil.OUT.log(Level.INFO, "No conf-files.xml exists in removed module.");
        }
    }
    
    public static void populateDOXMLFiles(final File newModuleConfFilesXml, final String moduleName, final DOXMLChangeListener doXMLListener) throws Exception {
        if (newModuleConfFilesXml.exists()) {
            final String serverHome = new File(MigrationUtil.server_home).getCanonicalPath();
            final DataObject confFileDO = Xml2DoConverter.transform(newModuleConfFilesXml.toURI().toURL());
            final Iterator confFileEntries = confFileDO.getRows("ConfFile");
            while (confFileEntries.hasNext()) {
                final Row confFileRow = confFileEntries.next();
                final String fileName = (String)confFileRow.get("URL");
                final String doXmlFilePath = newModuleConfFilesXml.getParentFile().getCanonicalPath() + File.separator + fileName;
                final File doXmlFile = new File(doXmlFilePath);
                String filePath = doXmlFile.getCanonicalPath().substring(serverHome.length() + 1);
                filePath = filePath.replaceAll("\\\\", "/");
                if (doXMLListener != null && doXMLListener.skipXML(filePath)) {
                    MigrationUtil.OUT.info("skipXML returning true for added xml " + filePath);
                }
                else {
                    final String populator = (String)confFileRow.get("POPULATORCLASS");
                    ConfigurationPopulator configurationPopulator = null;
                    if (populator != null && populator.length() > 0) {
                        configurationPopulator = (ConfigurationPopulator)Thread.currentThread().getContextClassLoader().loadClass(populator).newInstance();
                    }
                    MigrationUtil.OUT.info("Going to add xml :: " + doXmlFile.getCanonicalPath());
                    XML2DOUpdater.addXml(doXmlFile, null, moduleName, confFileRow, doXMLListener, configurationPopulator);
                }
            }
        }
        else {
            MigrationUtil.OUT.info("No conf-files.xml exists in newly added module.");
        }
    }
    
    public static boolean isMigrationRunning() {
        return MigrationUtil.isMigrationRunnning;
    }
    
    static {
        OUT = Logger.getLogger(MigrationUtil.class.getName());
        INSTALLVSREVERT = new HashMap<Integer, Integer>(17);
        MigrationUtil.isMigrationRunnning = false;
        MigrationUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.MODIFY_DD.ordinal(), DDLChanges.DDLOperation.MODIFY_DD.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_FK.ordinal(), DDLChanges.DDLOperation.ADD_FK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_UK.ordinal(), DDLChanges.DDLOperation.ADD_UK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_PK.ordinal(), DDLChanges.DDLOperation.ADD_PK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_IDX.ordinal(), DDLChanges.DDLOperation.ADD_IDX.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.ADD_COLUMN.ordinal(), DDLChanges.DDLOperation.DROP_COLUMN.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.MODIFY_COLUMN.ordinal(), DDLChanges.DDLOperation.MODIFY_COLUMN.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.MODIFY_PK.ordinal(), DDLChanges.DDLOperation.MODIFY_PK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.ADD_IDX.ordinal(), DDLChanges.DDLOperation.DROP_IDX.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.ADD_PK.ordinal(), DDLChanges.DDLOperation.DROP_PK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.ADD_UK.ordinal(), DDLChanges.DDLOperation.DROP_UK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.CREATE_TABLE.ordinal(), DDLChanges.DDLOperation.DROP_TABLE.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.MODIFY_TABLE.ordinal(), DDLChanges.DDLOperation.MODIFY_TABLE.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.MODIFY_FK.ordinal(), DDLChanges.DDLOperation.MODIFY_FK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.ADD_FK.ordinal(), DDLChanges.DDLOperation.DROP_FK.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_TABLE.ordinal(), DDLChanges.DDLOperation.CREATE_TABLE.ordinal());
        MigrationUtil.INSTALLVSREVERT.put(DDLChanges.DDLOperation.DROP_COLUMN.ordinal(), DDLChanges.DDLOperation.ADD_COLUMN.ordinal());
    }
}
