package com.adventnet.db.adapter.postgres;

import java.sql.Statement;
import java.util.logging.Level;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import com.adventnet.db.api.RelationalAPI;
import java.sql.Connection;
import java.io.IOException;
import com.adventnet.db.adapter.DBAdapter;
import com.zoho.mickey.tools.postgres.CreatePostgresDBUser;
import com.adventnet.persistence.PersistenceUtil;
import java.sql.DriverManager;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.util.logging.Logger;

public class DMPostgresDBAdapter extends PostgresDBAdapter
{
    private static Logger out;
    
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        final boolean retVal = super.createDB(connectionURL, userName, password);
        String superUserPassword = this.getDBProps().getProperty("superuser_pass");
        final String rUserName = this.getDBProps().getProperty("r_username", "medc");
        if (superUserPassword != null && !superUserPassword.isEmpty()) {
            superUserPassword = CryptoUtil.decrypt(superUserPassword);
            try (final Connection connection = DriverManager.getConnection(connectionURL, "postgres", superUserPassword)) {
                if (!this.isRole(connection, rUserName)) {
                    superUserPassword = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getPassword((Object)superUserPassword);
                    String rPassWord = this.getDBProps().getProperty("r_password");
                    rPassWord = ((rPassWord == null) ? rUserName : PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)rPassWord));
                    new CreatePostgresDBUser().createUser((DBAdapter)this, connectionURL, "postgres", superUserPassword, rUserName, rPassWord, "readonly");
                    this.setDBConnectionLimit(connection, rUserName);
                }
            }
            catch (final Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        return retVal;
    }
    
    private boolean isRole(final Connection conn, String roleName) throws SQLException, QueryConstructionException {
        DataSet ds = null;
        if (roleName == null || roleName.isEmpty()) {
            roleName = "current_user";
        }
        else {
            roleName = "'" + roleName + "'";
        }
        final String query = "SELECT 1 FROM pg_roles WHERE rolname = " + roleName;
        try {
            ds = RelationalAPI.getInstance().executeQuery(query, conn);
            if (ds.next()) {
                return true;
            }
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
        return false;
    }
    
    private void setDBConnectionLimit(final Connection connection, final String rUserName) {
        final String connectionLimitQuery = "alter user " + rUserName + " with CONNECTION LIMIT ";
        Statement statement = null;
        try {
            final String connectionLimit = this.getDBProps().getProperty("r_maxsize", "5");
            DMPostgresDBAdapter.out.info("DB connection limit is " + connectionLimit);
            statement = connection.createStatement();
            statement.execute(connectionLimitQuery + connectionLimit);
            DMPostgresDBAdapter.out.info("DB connection limit is set for " + rUserName);
        }
        catch (final Exception ex) {
            DMPostgresDBAdapter.out.log(Level.WARNING, "Exception while setting connection limit for " + rUserName, ex);
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex) {
                DMPostgresDBAdapter.out.log(Level.WARNING, "Exception while closing the statement", ex);
            }
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex2) {
                DMPostgresDBAdapter.out.log(Level.WARNING, "Exception while closing the statement", ex2);
            }
        }
    }
    
    static {
        DMPostgresDBAdapter.out = Logger.getLogger(DMPostgresDBAdapter.class.getName());
    }
}
