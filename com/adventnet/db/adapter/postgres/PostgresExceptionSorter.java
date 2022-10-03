package com.adventnet.db.adapter.postgres;

import java.util.logging.Level;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.io.Serializable;
import com.adventnet.db.adapter.BaseExceptionSorter;

public class PostgresExceptionSorter extends BaseExceptionSorter implements Serializable
{
    private static Logger logger;
    
    @Override
    public boolean isDBAlive(final SQLException e) {
        final int eCode = e.getErrorCode();
        final String eState = e.getSQLState();
        final boolean retValue = 0 == eCode && ("57P01".equalsIgnoreCase(eState) || "57P02".equalsIgnoreCase(eState) || "08006".equals(eState));
        PostgresExceptionSorter.logger.log(Level.SEVERE, "Returning [{0}] from isDBAlive method ErrorCode: {1} SqlState: {2}", new Object[] { !retValue, eCode, eState });
        return !retValue && super.isDBAlive(e);
    }
    
    static {
        PostgresExceptionSorter.logger = Logger.getLogger(PostgresExceptionSorter.class.getName());
    }
}
