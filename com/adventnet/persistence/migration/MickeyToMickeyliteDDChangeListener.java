package com.adventnet.persistence.migration;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.logging.Logger;

public class MickeyToMickeyliteDDChangeListener extends DefaultDDChangeListener
{
    private static final Logger LOGGER;
    
    @Override
    public boolean preInvokeForAlterTable(final AlterTableQuery atq) throws Exception {
        final AlterOperation ao = atq.getAlterOperations().get(0);
        final String dbType = PersistenceInitializer.getConfigurationValue("DBName");
        String tableName = null;
        if (dbType.equals("postgres")) {
            tableName = atq.getTableName().toLowerCase();
        }
        else {
            tableName = atq.getTableName();
        }
        if (ao.getOperationType() == 7) {
            final String fkName = (String)ao.getAlterObject();
            if (!this.isFKExistsInDB(tableName, fkName)) {
                MickeyToMickeyliteDDChangeListener.LOGGER.info("Going to Skip Delete_Foreign_Key operation in DB for the table  :: " + atq.getTableName());
                atq.setIsExecutable(false);
            }
        }
        return super.preInvokeForAlterTable(atq);
    }
    
    private boolean isFKExistsInDB(final String tableName, final String fkName) throws Exception {
        Connection conn = null;
        ResultSet rs = null;
        boolean isExists = false;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            final DatabaseMetaData dbMeta = conn.getMetaData();
            rs = dbMeta.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                final String fetchedFKName = rs.getString("FK_NAME");
                MickeyToMickeyliteDDChangeListener.LOGGER.info("fetchedFKName :: " + fetchedFKName);
                if (fetchedFKName.equalsIgnoreCase(fkName)) {
                    isExists = true;
                }
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return isExists;
    }
    
    static {
        LOGGER = Logger.getLogger(MickeyToMickeyliteDDChangeListener.class.getName());
    }
}
