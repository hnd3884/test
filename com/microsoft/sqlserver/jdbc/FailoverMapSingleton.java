package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.HashMap;

final class FailoverMapSingleton
{
    private static int initialHashmapSize;
    private static HashMap<String, FailoverInfo> failoverMap;
    
    private FailoverMapSingleton() {
    }
    
    private static String concatPrimaryDatabase(final String primary, final String instance, final String database) {
        final StringBuilder buf = new StringBuilder();
        buf.append(primary);
        if (null != instance) {
            buf.append("\\");
            buf.append(instance);
        }
        buf.append(";");
        buf.append(database);
        return buf.toString();
    }
    
    static FailoverInfo getFailoverInfo(final SQLServerConnection connection, final String primaryServer, final String instance, final String database) {
        synchronized (FailoverMapSingleton.class) {
            if (FailoverMapSingleton.failoverMap.isEmpty()) {
                return null;
            }
            final String mapKey = concatPrimaryDatabase(primaryServer, instance, database);
            if (connection.getConnectionLogger().isLoggable(Level.FINER)) {
                connection.getConnectionLogger().finer(connection.toString() + " Looking up info in the map using key: " + mapKey);
            }
            final FailoverInfo fo = FailoverMapSingleton.failoverMap.get(mapKey);
            if (null != fo) {
                fo.log(connection);
            }
            return fo;
        }
    }
    
    static void putFailoverInfo(final SQLServerConnection connection, final String primaryServer, final String instance, final String database, final FailoverInfo actualFailoverInfo, final boolean actualuseFailover, final String failoverPartner) throws SQLServerException {
        synchronized (FailoverMapSingleton.class) {
            final FailoverInfo fo;
            if (null == (fo = getFailoverInfo(connection, primaryServer, instance, database))) {
                if (connection.getConnectionLogger().isLoggable(Level.FINE)) {
                    connection.getConnectionLogger().fine(connection.toString() + " Failover map add server: " + primaryServer + "; database:" + database + "; Mirror:" + failoverPartner);
                }
                FailoverMapSingleton.failoverMap.put(concatPrimaryDatabase(primaryServer, instance, database), actualFailoverInfo);
            }
            else {
                fo.failoverAdd(connection, actualuseFailover, failoverPartner);
            }
        }
    }
    
    static {
        FailoverMapSingleton.initialHashmapSize = 5;
        FailoverMapSingleton.failoverMap = new HashMap<String, FailoverInfo>(FailoverMapSingleton.initialHashmapSize);
    }
}
