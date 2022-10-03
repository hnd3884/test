package com.me.devicemanagement.onpremise.tools.dbmigration.utils;

import com.me.devicemanagement.onpremise.tools.dbmigration.gui.ChangeDBServerGUI;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangeMasterKeyUtil
{
    private static final String MASTERKEY_PASSWORD = "masterkey.password";
    public static int sqlAlterErrorCode;
    private static String masterKey;
    private static Logger logger;
    
    public static void changeMasterKeyPassword() {
        try {
            final String password = "Password123";
            ChangeMasterKeyUtil.logger.log(Level.INFO, "Going to get DB name");
            if (DBMigrationUtils.getDBName().equalsIgnoreCase("mssql")) {
                final NodeList propertyList = DBMigrationUtils.writePersistenceConfiguration(null);
                final boolean isMasterKeySet = checkMasterKeyProperty(propertyList);
                if (!isMasterKeySet) {
                    ChangeMasterKeyUtil.logger.log(Level.INFO, "Craete dynamic masterkey");
                    alterMasterKey(ChangeMasterKeyUtil.masterKey = DBMigrationUtils.createDynamicMasterKey(password));
                    if (ChangeMasterKeyUtil.sqlAlterErrorCode == 1000) {
                        DBMigrationUtils.writePersistenceConfiguration(ChangeMasterKeyUtil.masterKey);
                    }
                    else {
                        DBMigrationUtils.writePersistenceConfiguration(password);
                    }
                }
            }
        }
        catch (final Exception e) {
            ChangeMasterKeyUtil.logger.log(Level.WARNING, "Exception while changing master key", e);
        }
    }
    
    private static boolean checkMasterKeyProperty(final NodeList propertyList) {
        boolean isMasterKeySet = false;
        if (propertyList != null && propertyList.getLength() > 0) {
            final Node propertyEl = propertyList.item(0);
            if (propertyEl.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("masterkey.password")) {
                isMasterKeySet = true;
            }
        }
        return isMasterKeySet;
    }
    
    private static void alterMasterKey(final String masterKey) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            stmt = conn.createStatement();
            final String sql = "ALTER MASTER KEY REGENERATE WITH ENCRYPTION BY PASSWORD ='" + masterKey + "'";
            stmt.execute(sql);
            ChangeMasterKeyUtil.logger.log(Level.INFO, "Master key has been altered successfully");
            ChangeMasterKeyUtil.sqlAlterErrorCode = 1000;
        }
        catch (final SQLException e) {
            ChangeMasterKeyUtil.sqlAlterErrorCode = e.getErrorCode();
            ChangeMasterKeyUtil.logger.log(Level.SEVERE, "Exception while alter the Master key", e);
        }
        catch (final Exception ex) {
            ChangeMasterKeyUtil.logger.log(Level.SEVERE, "Exception while alter the Master key", ex);
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    static {
        ChangeMasterKeyUtil.sqlAlterErrorCode = 0;
        ChangeMasterKeyUtil.logger = Logger.getLogger(ChangeDBServerGUI.class.getName());
    }
}
