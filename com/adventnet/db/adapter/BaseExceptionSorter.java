package com.adventnet.db.adapter;

import com.zoho.conf.AppResources;
import com.adventnet.db.api.RelationalAPI;
import java.util.Locale;
import java.util.logging.Level;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.HashSet;
import java.io.Serializable;
import com.zoho.cp.ExceptionSorter;

public class BaseExceptionSorter implements ExceptionSorter, Serializable
{
    private static HashSet<String> sqlStates;
    private static Logger logger;
    
    public boolean isDBAlive(final SQLException e) {
        final String ss = e.getSQLState();
        BaseExceptionSorter.logger.log(Level.SEVERE, "SQLState :: [{0}], Error Code:: [{1}]", new Object[] { ss, e.getErrorCode() });
        final boolean returnValue = ss == null || !BaseExceptionSorter.sqlStates.contains(ss.toUpperCase(Locale.US));
        BaseExceptionSorter.logger.log(Level.SEVERE, "Returning [{0}] from isDBAlive method", returnValue);
        return returnValue;
    }
    
    public boolean isExceptionFatal(final SQLException e) {
        if (!this.isDBAlive(e)) {
            BaseExceptionSorter.logger.log(Level.SEVERE, "Going to Halt the JVM");
            RelationalAPI.getInstance().logAndHalt(AppResources.getInteger("check.dbcrash.delay", Integer.valueOf(10)));
            return true;
        }
        return false;
    }
    
    static {
        (BaseExceptionSorter.sqlStates = new HashSet<String>()).add("08S01");
        BaseExceptionSorter.sqlStates.add("08000");
        BaseExceptionSorter.sqlStates.add("08001");
        BaseExceptionSorter.sqlStates.add("08002");
        BaseExceptionSorter.sqlStates.add("08003");
        BaseExceptionSorter.sqlStates.add("08004");
        BaseExceptionSorter.sqlStates.add("08007");
        BaseExceptionSorter.sqlStates.add("08900");
        BaseExceptionSorter.logger = Logger.getLogger(BaseExceptionSorter.class.getName());
    }
}
