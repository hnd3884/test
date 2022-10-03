package com.adventnet.db.migration.handler;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.net.InetAddress;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.sql.Statement;
import com.zoho.net.handshake.HandShakePacket;
import com.zoho.net.handshake.HandShakeClient;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import java.io.IOException;
import com.zoho.net.handshake.HandShakeUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Logger;

public class DBMServerCheckPreHandler implements DBMigrationPrePostHandler
{
    private static final Logger LOGGER;
    
    @Override
    public void preHandle() throws Exception {
        final TableDefinition statusTableDef = this.getStatusTableDef();
        Connection srcConnection = null;
        try {
            srcConnection = DBMigrationUtil.getSrcConnection();
            if (DBMigrationUtil.getSrcDBAdapter().isTablePresentInDB(srcConnection, null, statusTableDef.getTableName())) {
                DBMServerCheckPreHandler.LOGGER.info(statusTableDef.getTableName() + " exists in source database. Trying to PING HandShakeServer.");
                this.addStatusTableDefToMetaData(statusTableDef);
                final SelectQuery selectQuery = QueryConstructor.get(statusTableDef.getTableName(), (Criteria)null);
                final DataSet statusTable = RelationalAPI.getInstance().executeQuery(DBMigrationUtil.getSrcDBAdapter().getSQLGenerator().getSQLForSelect(selectQuery), srcConnection);
                boolean isHandShakeServerFound = Boolean.FALSE;
                try {
                    while (statusTable.next()) {
                        final HandShakeClient handShakeClient = HandShakeUtil.getHandShakeClient(statusTable.getString("HOST_NAME"), statusTable.getInt("LISTEN_PORT"));
                        if (handShakeClient != null) {
                            isHandShakeServerFound = Boolean.TRUE;
                            final HandShakePacket pingMessagePacket = handShakeClient.getPingMessageAndExit("PING");
                            if (!pingMessagePacket.getMessage().equals("ALIVE") || HandShakeUtil.getHandShakeServerID() == pingMessagePacket.getHandShakeServerID()) {
                                DBMServerCheckPreHandler.LOGGER.warning("HandShakeServer started by current JVM, hence ignoring error.");
                                return;
                            }
                            this.throwException(pingMessagePacket);
                        }
                        else {
                            DBMServerCheckPreHandler.LOGGER.info("HandShakePacket is null, hence starting HandShakeServer");
                        }
                    }
                }
                catch (final IOException ioe) {
                    DBMServerCheckPreHandler.LOGGER.warning("Unknown HandShakeServer. Starting new instance" + ioe.getCause().getMessage());
                    isHandShakeServerFound = Boolean.FALSE;
                }
                finally {
                    statusTable.close();
                }
                if (!isHandShakeServerFound) {
                    this.updateStatusTable(statusTableDef);
                }
            }
            else {
                final HandShakeClient handShakeClient2 = HandShakeUtil.getHandShakeClient();
                if (handShakeClient2 == null) {
                    if (DBMigrationUtil.isDBMigrationRunning()) {
                        Statement statement = null;
                        try {
                            statement = srcConnection.createStatement();
                            DBMigrationUtil.getSrcDBAdapter().createTable(statement, statusTableDef, null);
                            this.addStatusTableDefToMetaData(statusTableDef);
                            this.updateStatusTable(statusTableDef);
                        }
                        finally {
                            if (statement != null) {
                                statement.close();
                            }
                        }
                    }
                }
                else {
                    final HandShakePacket pingPacket = handShakeClient2.getPingMessageAndExit("PING");
                    if (HandShakeUtil.getHandShakeServerID() == pingPacket.getHandShakeServerID()) {
                        DBMServerCheckPreHandler.LOGGER.warning("HandShakeServer started by current JVM, hence ignoring error.");
                        return;
                    }
                    this.throwException(pingPacket);
                }
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
            throw new Exception(t);
        }
        finally {
            if (srcConnection != null) {
                srcConnection.close();
            }
        }
    }
    
    private void addStatusTableDefToMetaData(final TableDefinition tableDef) throws MetaDataException {
        if (MetaDataUtil.getTableDefinitionByName(tableDef.getTableName()) == null) {
            MetaDataUtil.addTableDefinition("Persistence", tableDef);
        }
        else {
            DBMServerCheckPreHandler.LOGGER.warning(tableDef.getTableName() + " already loaded. Hence skipping");
        }
    }
    
    private void throwException(final HandShakePacket pingMessagePacket) throws Exception {
        DBMigrationUtil.getHandlerFactory().getProgressNotifier().printMessage("\nAlready Server seems to be running.\n");
        DBMigrationUtil.getHandlerFactory().getProgressNotifier().printMessage(pingMessagePacket.toString());
        DBMigrationUtil.getHandlerFactory().getProgressNotifier().printMessage("\nPlease shutdown/stop the server and try DB migration once again.");
        throw new Exception("Already Server seems to be running");
    }
    
    protected void updateStatusTable(final TableDefinition td) throws IOException, DataAccessException {
        HandShakeUtil.startHandShakeServer();
        DataAccess.delete(td.getTableName(), (Criteria)null);
        final DataObject statusData = DataAccess.constructDataObject();
        final Row statusRow = new Row(td.getTableName());
        statusRow.set("HOST_NAME", (System.getProperty("bindaddress") != null) ? System.getProperty("bindaddress") : InetAddress.getLoopbackAddress().getHostName());
        statusRow.set("LISTEN_PORT", HandShakeUtil.getServerListeningPort());
        statusRow.set("START_TIME", HandShakeUtil.getServerStartedTime());
        statusData.addRow(statusRow);
        DBMServerCheckPreHandler.LOGGER.info("Adding server status row in DBMStatus table.");
        DataAccess.add(statusData);
    }
    
    @Override
    public void postHandle() throws Exception {
        Connection dstConnection = null;
        Statement statement = null;
        try {
            dstConnection = DBMigrationUtil.getDestConnection();
            statement = dstConnection.createStatement();
            DBMServerCheckPreHandler.LOGGER.info("Creating DBMStatus table in destination database...");
            DBMigrationUtil.getDestDBAdapter().createTable(statement, this.getStatusTableDef(), null);
        }
        finally {
            if (statement != null) {
                statement.close();
            }
            if (dstConnection != null) {
                dstConnection.close();
            }
        }
    }
    
    private TableDefinition getStatusTableDef() {
        final TableDefinition td = new TableDefinition();
        final PrimaryKeyDefinition pk = new PrimaryKeyDefinition();
        td.setTableName("DBMStatus");
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnName("HOST_NAME");
        colDef.setDataType("CHAR");
        colDef.setMaxLength(255);
        td.addColumnDefinition(colDef);
        pk.setTableName(td.getTableName());
        pk.setName(td.getTableName() + "_PK");
        pk.addColumnName(colDef.getColumnName());
        td.setPrimaryKey(pk);
        final ColumnDefinition c1 = new ColumnDefinition();
        c1.setColumnName("LISTEN_PORT");
        c1.setDataType("INTEGER");
        c1.setNullable(false);
        td.addColumnDefinition(c1);
        final ColumnDefinition c2 = new ColumnDefinition();
        c2.setColumnName("START_TIME");
        c2.setDataType("BIGINT");
        c2.setNullable(false);
        td.addColumnDefinition(c2);
        td.setModuleName("Persitence");
        return td;
    }
    
    static {
        LOGGER = Logger.getLogger(DBMServerCheckPreHandler.class.getName());
    }
}
