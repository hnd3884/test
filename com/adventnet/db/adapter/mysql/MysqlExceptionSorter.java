package com.adventnet.db.adapter.mysql;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.io.Serializable;
import com.adventnet.db.adapter.BaseExceptionSorter;

public class MysqlExceptionSorter extends BaseExceptionSorter implements Serializable
{
    private static Logger logger;
    
    @Override
    public boolean isDBAlive(final SQLException e) {
        return super.isDBAlive(e);
    }
    
    static {
        MysqlExceptionSorter.logger = Logger.getLogger(MysqlExceptionSorter.class.getName());
    }
}
