package com.adventnet.db.schema.analyze;

import org.json.JSONException;
import java.sql.SQLException;
import com.adventnet.db.adapter.postgres.PostgresDBAdapter;
import com.zoho.db.model.Table;
import java.util.Iterator;
import com.zoho.db.model.DataBase;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.ArrayList;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;
import com.zoho.db.model.DataBaseBuilder;
import java.util.concurrent.ConcurrentHashMap;
import com.adventnet.db.schema.analyze.notifier.SchemaAnalyzerProgressNotifier;
import org.json.JSONArray;
import java.util.Map;
import java.util.logging.Logger;

public class SchemaAnalyzer
{
    private static final Logger LOGGER;
    private static Map<String, JSONArray> tableVsdiff;
    private static SchemaAnalyzerProgressNotifier notifier;
    
    public Map<String, JSONArray> analyzeSchema(final SchemaComparatorObject compObj) throws Exception {
        if (compObj.getComparatorType() == SchemaComparator.ComparatorType.DATABASE_VS_DATABASE) {
            return this.compareDatabaseSchemas(compObj);
        }
        return this.compareTableVsTableDefinition(compObj);
    }
    
    public Map<String, JSONArray> compareDatabaseSchemas(final SchemaComparatorObject compObj) throws Exception {
        SchemaAnalyzer.LOGGER.info("\nAnalysing source database.....");
        String srcSchema = null;
        compObj.setTableVsDiffMap(SchemaAnalyzer.tableVsdiff = new ConcurrentHashMap<String, JSONArray>());
        final DataSource srcDataSource = compObj.getSrcDataSource();
        final DBAdapter srcDBAdapter = compObj.getSrcDBAdapter();
        final DataSource destDataSource = compObj.getDestDataSource();
        final DBAdapter destDBAdapter = compObj.getDestDBAdapter();
        final SchemaAnalyzerUtil.DBType srcDBType = compObj.getSrcDBType();
        final SchemaAnalyzerUtil.DBType destDBType = compObj.getDestDBType();
        SchemaAnalyzer.notifier = SchemaAnalyzerUtil.getConfiguration().getProgressNotifier();
        try (final Connection srcConnection = srcDataSource.getConnection()) {
            srcSchema = SchemaAnalyzerUtil.getSchema(srcDBAdapter.getSQLGenerator().getSchemaQuery(), srcConnection);
        }
        SchemaAnalyzerUtil.setSrcDB(((DataBaseBuilder)((DataBaseBuilder)DataBaseBuilder.dataBase().withSchema(srcSchema)).usingDataSource(srcDataSource)).scan());
        try (final Connection srcConnection = srcDataSource.getConnection()) {
            this.handleDBSpecificChanges(srcDBType, srcDBAdapter, srcConnection, SchemaAnalyzerUtil.getSrcDB());
            SchemaAnalyzerUtil.getSrcDB().setSchema(SchemaAnalyzerUtil.getSchema(srcDBAdapter.getSQLGenerator().getSchemaQuery(), srcConnection));
            SchemaAnalyzer.LOGGER.info("No of tables in " + srcDBType + " database ::: " + SchemaAnalyzerUtil.getSrcDB().getAllTables().size());
        }
        String destSchema = null;
        try (final Connection destConnection = destDataSource.getConnection()) {
            destSchema = SchemaAnalyzerUtil.getSchema(destDBAdapter.getSQLGenerator().getSchemaQuery(), destConnection);
        }
        SchemaAnalyzerUtil.setDestDB(((DataBaseBuilder)((DataBaseBuilder)DataBaseBuilder.dataBase().withSchema(destSchema)).usingDataSource(destDataSource)).scan());
        try (final Connection destConnection = destDataSource.getConnection()) {
            SchemaAnalyzer.LOGGER.info("\nAnalysing destination database.");
            this.handleDBSpecificChanges(destDBType, destDBAdapter, destConnection, SchemaAnalyzerUtil.getDestDB());
            SchemaAnalyzerUtil.getDestDB().setSchema(SchemaAnalyzerUtil.getSchema(destDBAdapter.getSQLGenerator().getSchemaQuery(), destConnection));
            SchemaAnalyzer.LOGGER.info("No of tables in " + destDBType + " database ::: " + SchemaAnalyzerUtil.getDestDB().getAllTables().size());
        }
        final SchemaComparator comparator = new SchemaComparator(compObj, SchemaAnalyzerUtil.getSrcDB(), SchemaAnalyzerUtil.getDestDB(), this.getComparatorPoolSize(srcDBType, destDBType), SchemaAnalyzer.tableVsdiff, SchemaAnalyzer.notifier);
        SchemaAnalyzer.LOGGER.info("Validating schemas");
        SchemaAnalyzer.LOGGER.info("\nValidating destination database schema");
        comparator.validateSchemas();
        this.printDiffInLog(SchemaAnalyzer.tableVsdiff);
        return SchemaAnalyzer.tableVsdiff;
    }
    
    public Map<String, JSONArray> compareTableVsTableDefinition(final SchemaComparatorObject compObj) throws Exception {
        Connection srcConnection = null;
        final DataSource srcDataSource = compObj.getSrcDataSource();
        final DBAdapter srcDBAdapter = compObj.getSrcDBAdapter();
        compObj.setTableVsDiffMap(SchemaAnalyzer.tableVsdiff = new ConcurrentHashMap<String, JSONArray>());
        SchemaAnalyzer.notifier = SchemaAnalyzerUtil.getConfiguration().getProgressNotifier();
        final List<String> tableNames = compObj.getTableNames();
        List<TableDefinition> tds = new ArrayList<TableDefinition>();
        try {
            srcConnection = srcDataSource.getConnection();
            final String srcSchema = SchemaAnalyzerUtil.getSchema(srcDBAdapter.getSQLGenerator().getSchemaQuery(), srcConnection);
            final DataBaseBuilder builder = (DataBaseBuilder)((DataBaseBuilder)DataBaseBuilder.dataBase().withSchema(srcSchema)).usingDataSource(srcDataSource);
            if (tableNames.size() > 0) {
                builder.forTables((List)compObj.getTableNames());
            }
            final DataBase srcDB = builder.scan();
            this.handleDBSpecificChanges(compObj.getSrcDBType(), compObj.getSrcDBAdapter(), srcConnection, srcDB);
            TableDefinition td = null;
            if (tableNames.size() > 0) {
                for (final String tableName : tableNames) {
                    td = MetaDataUtil.getTableDefinitionByName(tableName);
                    if (td != null) {
                        tds.add(td);
                    }
                }
            }
            else {
                tds = MetaDataUtil.getTableDefinitions();
            }
            final SchemaComparator comparator = new SchemaComparator(compObj, tds, srcDB, SchemaAnalyzer.tableVsdiff, SchemaAnalyzer.notifier);
            comparator.validateTableDefinitions();
            this.printDiffInLog(SchemaAnalyzer.tableVsdiff);
            return SchemaAnalyzer.tableVsdiff;
        }
        catch (final Exception exp) {
            SchemaAnalyzer.LOGGER.log(Level.SEVERE, "Error occured while analysing database..");
            exp.printStackTrace();
            throw exp;
        }
        finally {
            if (srcConnection != null) {
                srcConnection.close();
            }
        }
    }
    
    public static Map<String, JSONArray> getTableNameVsDiffMap() {
        return SchemaAnalyzer.tableVsdiff;
    }
    
    private void handleDBSpecificChanges(final SchemaAnalyzerUtil.DBType dbType, final DBAdapter dbAdapter, final Connection connection, final DataBase database) throws SQLException {
        if (dbType == SchemaAnalyzerUtil.DBType.POSTGRES) {
            for (final Table table : database.getAllTables()) {
                final Map<String, String> citextColumnVsMaxSize = ((PostgresDBAdapter)dbAdapter).getCitextColumnVsMaxSize(table.getName(), connection);
                if (!citextColumnVsMaxSize.isEmpty()) {
                    for (final String columnName : citextColumnVsMaxSize.keySet()) {
                        final String string = citextColumnVsMaxSize.get(columnName);
                        if (string != null) {
                            SchemaAnalyzer.LOGGER.fine("Updating max length of the citext column " + table.getName() + "." + columnName + ". max size:: " + string);
                            table.getColumn(columnName).setMaxSize(Integer.parseInt(string));
                        }
                    }
                }
            }
        }
    }
    
    protected int getAnalyzerPoolSize(final SchemaAnalyzerUtil.DBType dbType) throws Exception {
        return SchemaAnalyzerUtil.DBType.FIREBIRD.equals(dbType) ? 1 : SchemaAnalyzerUtil.getConfiguration().getWorkerPoolSize();
    }
    
    protected int getComparatorPoolSize(final SchemaAnalyzerUtil.DBType src, final SchemaAnalyzerUtil.DBType dest) throws Exception {
        final int s = this.getAnalyzerPoolSize(src);
        final int d = this.getAnalyzerPoolSize(dest);
        return (s < d) ? s : d;
    }
    
    private void printDiffInLog(final Map<String, JSONArray> tableVsdiff) throws JSONException {
        if (tableVsdiff.size() > 0) {
            SchemaAnalyzer.LOGGER.info("DDL sanity test :: DIFF IDENTIFIED");
            SchemaAnalyzer.LOGGER.info("Diff identified tables :: " + tableVsdiff.size());
            SchemaAnalyzer.LOGGER.info("DataBase schemas has some diff. Kindly refer logs for more details...");
            for (final String tableName : tableVsdiff.keySet()) {
                SchemaAnalyzer.LOGGER.info("Table [" + tableName + "] has following diff ::\n" + tableVsdiff.get(tableName).toString(4));
            }
            if (SchemaAnalyzer.notifier != null) {
                SchemaAnalyzer.notifier.printMessage("Diff exists.. please refer logs!!");
            }
        }
        else {
            SchemaAnalyzer.LOGGER.info("DDL sanity test :: PASSED");
            if (SchemaAnalyzer.notifier != null) {
                SchemaAnalyzer.notifier.printMessage("DDL sanity test :: PASSED");
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SchemaAnalyzer.class.getName());
        SchemaAnalyzer.tableVsdiff = null;
        SchemaAnalyzer.notifier = null;
    }
}
