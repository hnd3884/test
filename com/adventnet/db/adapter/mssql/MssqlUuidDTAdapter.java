package com.adventnet.db.adapter.mssql;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.sql.Connection;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DTAdapter;

public class MssqlUuidDTAdapter implements DTAdapter
{
    private static final Logger LOGGER;
    
    @Override
    public void validateVersion(final Connection conn) {
        try {
            final DatabaseMetaData dbm = conn.getMetaData();
            final String version = dbm.getDatabaseProductVersion();
            final String[] versionDetails = version.split("\\.");
            MssqlUuidDTAdapter.LOGGER.log(Level.INFO, "Mssql version : {0}", version);
            final int majorVersion = Integer.parseInt(versionDetails[0]);
            final int minorVersion = Integer.parseInt(versionDetails[1]);
            if (majorVersion < 9) {
                throw new UnsupportedOperationException("This version of Mssql :: " + version + " is not supported for UUID");
            }
            if (minorVersion < 0) {
                throw new UnsupportedOperationException("This version of Mssql :: " + version + " is not supported for UUID");
            }
            MssqlUuidDTAdapter.LOGGER.log(Level.INFO, "This version of Mysql :: {0} is supported for UUID", version);
        }
        catch (final SQLException ex) {
            MssqlUuidDTAdapter.LOGGER.log(Level.INFO, "Unable to validate version for UUID");
            ex.printStackTrace();
        }
    }
    
    @Override
    public byte[] getBytes(final Object value) {
        return value.toString().getBytes();
    }
    
    @Override
    public int getJavaSQLType() {
        return 1111;
    }
    
    static {
        LOGGER = Logger.getLogger(MssqlUuidDTAdapter.class.getName());
    }
}
