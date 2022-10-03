package com.me.devicemanagement.framework.server.util;

import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class DMReadOnlyDataSetWrapper
{
    private static final Logger LOGGER;
    
    public static DMDataSetWrapper executeQuery(final SelectQuery query) throws Exception {
        Connection connection = null;
        try {
            DMReadOnlyDataSetWrapper.LOGGER.log(Level.INFO, "Inside DMReadOnlyDataSetWrapper - executeQuery");
            connection = SyMUtil.getReadOnlyConnection();
            final DMDataSetWrapper ddsw = DMDataSetWrapper.executeQuery(connection, query);
            return ddsw;
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DMReadOnlyDataSetWrapper.class.getName());
    }
}
