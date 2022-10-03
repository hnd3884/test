package com.zoho.db.model;

import java.util.Iterator;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import com.zoho.db.scanner.SchemaScanner;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;

class DataBaseBuilderBase<GeneratorT extends DataBaseBuilderBase<GeneratorT>>
{
    private DataBase instance;
    private DataSource dataSource;
    private static final Logger LOGGER;
    private List<String> tableNames;
    
    protected DataBaseBuilderBase(final DataBase aInstance) {
        this.tableNames = new ArrayList<String>();
        this.instance = aInstance;
    }
    
    protected DataBase getInstance() {
        return this.instance;
    }
    
    private DataSource getDataSource() {
        return this.dataSource;
    }
    
    private void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void forTables(final List<String> names) {
        this.tableNames = names;
    }
    
    public DataBase scan() throws Exception {
        if (this.getDataSource() == null) {
            throw new IllegalAccessException("scan() cannot be invoked without setting datasource.");
        }
        List<String> allTableNames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = this.getDataSource().getConnection();
            DataBaseBuilderBase.LOGGER.info("Database schema scanning started.");
            final DatabaseMetaData metaData = connection.getMetaData();
            this.getInstance().setIdentifierQuoteString(metaData.getIdentifierQuoteString());
            this.getInstance().setCatalog(connection.getCatalog());
            this.getInstance().setDBProductName(metaData.getDatabaseProductName());
            if (this.tableNames.size() > 0) {
                for (final String tableName : this.tableNames) {
                    if (SchemaScanner.isTablePresentInDB(connection, this.getInstance().getSchema(), tableName)) {
                        allTableNames.add(tableName);
                    }
                }
            }
            else {
                allTableNames = SchemaScanner.getAllTableNames(this.instance.getCatalog(), this.instance.getSchema(), connection);
            }
            DataBaseBuilderBase.LOGGER.info("No of tables in " + this.getInstance().getDbLable() + "." + this.getInstance().getDbName() + " :: " + allTableNames.size());
            for (final String tableName : allTableNames) {
                this.withTables(TableBuilder.table().withTableName(tableName).build());
            }
            SchemaScanner.scan(this.getInstance(), this.getDataSource());
            DataBaseBuilderBase.LOGGER.info("Database schema scanning completed.");
            return this.getInstance();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public GeneratorT usingDataSource(final DataSource dataSource) {
        this.setDataSource(dataSource);
        return (GeneratorT)this;
    }
    
    public GeneratorT withSchema(final String schema) {
        this.getInstance().setSchema(schema);
        return (GeneratorT)this;
    }
    
    public GeneratorT withTables(final Table... tables) {
        for (final Table table : tables) {
            this.withTable(table);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withTables(final List<Table> tables) {
        for (final Table table : tables) {
            this.withTable(table);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withTable(final Table table) {
        this.instance.addTable(table);
        return (GeneratorT)this;
    }
    
    private GeneratorT withKeyWord(final String keyWords) {
        if (keyWords != null) {
            for (final String keyWord : keyWords.split(",")) {
                this.instance.addKeyWord(keyWord);
            }
        }
        return (GeneratorT)this;
    }
    
    static {
        LOGGER = Logger.getLogger(DataBaseBuilder.class.getName());
    }
}
