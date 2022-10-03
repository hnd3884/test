package com.me.tools.zcutil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;

public class RunSelectQuery
{
    private static Logger logger;
    
    public String getOneValue(final String qry) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String theValue = null;
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ps = conn.prepareStatement(qry);
            rs = ps.executeQuery();
            while (rs.next()) {
                theValue = rs.getString(1);
            }
        }
        catch (final SQLException etn) {
            RunSelectQuery.logger.log(Level.INFO, "Exception in METrack run select query : ", etn.getMessage());
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ef) {
                RunSelectQuery.logger.log(Level.INFO, "Exception in METrack run select query : ", ef.getMessage());
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ef2) {
                RunSelectQuery.logger.log(Level.INFO, "Exception in METrack run select query : ", ef2.getMessage());
            }
        }
        return theValue;
    }
    
    public String getDBName() {
        String dbName = "UNKNOWN";
        Connection conn = null;
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            dbName = conn.getMetaData().getDatabaseProductName();
        }
        catch (final Exception etn) {
            RunSelectQuery.logger.log(Level.INFO, "Exception in METrack run select query getDBName: ", etn.getMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ef) {
                ef.printStackTrace();
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ef2) {
                ef2.printStackTrace();
            }
        }
        return dbName;
    }
    
    static {
        RunSelectQuery.logger = Logger.getLogger(RunSelectQuery.class.getName());
    }
}
