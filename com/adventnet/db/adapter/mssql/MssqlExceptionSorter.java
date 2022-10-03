package com.adventnet.db.adapter.mssql;

import java.util.logging.Level;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.adventnet.db.adapter.BaseExceptionSorter;

public class MssqlExceptionSorter extends BaseExceptionSorter
{
    private static Logger logger;
    
    @Override
    public boolean isDBAlive(final SQLException e) {
        final String sqlState = e.getSQLState();
        final boolean returnValue = e.getErrorCode() == 0 && ("08S01".equalsIgnoreCase(sqlState) || "HY010".equalsIgnoreCase(sqlState));
        MssqlExceptionSorter.logger.log(Level.SEVERE, "Returning [{0}] from isDBAlive method", !returnValue);
        return !returnValue && super.isDBAlive(e);
    }
    
    static {
        MssqlExceptionSorter.logger = Logger.getLogger(MssqlExceptionSorter.class.getName());
    }
}
