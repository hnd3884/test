package org.apache.commons.httpclient.util;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.commons.httpclient.HttpConnection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;

public class IdleConnectionHandler
{
    private static final Log LOG;
    private Map connectionToAdded;
    
    public IdleConnectionHandler() {
        this.connectionToAdded = new HashMap();
    }
    
    public void add(final HttpConnection connection) {
        final Long timeAdded = new Long(System.currentTimeMillis());
        if (IdleConnectionHandler.LOG.isDebugEnabled()) {
            IdleConnectionHandler.LOG.debug((Object)("Adding connection at: " + timeAdded));
        }
        this.connectionToAdded.put(connection, timeAdded);
    }
    
    public void remove(final HttpConnection connection) {
        this.connectionToAdded.remove(connection);
    }
    
    public void removeAll() {
        this.connectionToAdded.clear();
    }
    
    public void closeIdleConnections(final long idleTime) {
        final long idleTimeout = System.currentTimeMillis() - idleTime;
        if (IdleConnectionHandler.LOG.isDebugEnabled()) {
            IdleConnectionHandler.LOG.debug((Object)("Checking for connections, idleTimeout: " + idleTimeout));
        }
        final Iterator connectionIter = this.connectionToAdded.keySet().iterator();
        while (connectionIter.hasNext()) {
            final HttpConnection conn = connectionIter.next();
            final Long connectionTime = this.connectionToAdded.get(conn);
            if (connectionTime <= idleTimeout) {
                if (IdleConnectionHandler.LOG.isDebugEnabled()) {
                    IdleConnectionHandler.LOG.debug((Object)("Closing connection, connection time: " + connectionTime));
                }
                connectionIter.remove();
                conn.close();
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(IdleConnectionHandler.class);
    }
}
