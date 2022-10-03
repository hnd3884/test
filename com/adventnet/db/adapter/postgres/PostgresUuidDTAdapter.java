package com.adventnet.db.adapter.postgres;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.sql.Connection;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DTAdapter;

public class PostgresUuidDTAdapter implements DTAdapter
{
    private static final Logger LOGGER;
    
    @Override
    public void validateVersion(final Connection conn) {
        try {
            final DatabaseMetaData dbm = conn.getMetaData();
            final String version = dbm.getDatabaseProductVersion();
            PostgresUuidDTAdapter.LOGGER.log(Level.INFO, "Postgres version : {0}", version);
            final Float dbVer = new Float(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion());
            if (dbVer < new Float("9.4")) {
                throw new UnsupportedOperationException("This version of Postgres :: " + version + " is not supported for UUID");
            }
            PostgresUuidDTAdapter.LOGGER.log(Level.INFO, "This version of Postgres :: {0} is supported for UUID", version);
        }
        catch (final SQLException ex) {
            PostgresUuidDTAdapter.LOGGER.log(Level.INFO, "Unable to validate version for UUID");
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
        LOGGER = Logger.getLogger(PostgresUuidDTAdapter.class.getName());
    }
}
