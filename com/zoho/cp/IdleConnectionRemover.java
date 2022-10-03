package com.zoho.cp;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class IdleConnectionRemover
{
    private static ConcurrentHashMap<ConnectionPool, String> connPoolList;
    public static int connectionRemoverThreadinterval;
    public static long threadTurnCounter;
    private static final Logger LOGGER;
    
    private static void init() {
        IdleConnectionRemover.connPoolList = new ConcurrentHashMap<ConnectionPool, String>();
        final Timer timer = new Timer("IdleConnectionRemover");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                incThreadTurnCounter();
                removeIdleConnection();
            }
        }, TimeUnit.MINUTES.toMillis(IdleConnectionRemover.connectionRemoverThreadinterval), TimeUnit.MINUTES.toMillis(IdleConnectionRemover.connectionRemoverThreadinterval));
    }
    
    public static void register(final ConnectionPool connPool) {
        IdleConnectionRemover.connPoolList.put(connPool, "");
    }
    
    private static void removeIdleConnection() {
        try {
            for (final ConnectionPool connPool : IdleConnectionRemover.connPoolList.keySet()) {
                connPool.removeTimedOutConnection();
            }
        }
        catch (final Exception exc) {
            IdleConnectionRemover.LOGGER.log(Level.INFO, "Exception while removing idle connection", exc);
        }
    }
    
    private static void incThreadTurnCounter() {
        ++IdleConnectionRemover.threadTurnCounter;
    }
    
    public static long getThreadTurnCounter() {
        return IdleConnectionRemover.threadTurnCounter;
    }
    
    public static void removeResources(final DataSource ds) {
        final TxDataSource tx = (TxDataSource)ds;
        IdleConnectionRemover.connPoolList.remove(tx.connPool);
    }
    
    static {
        IdleConnectionRemover.connectionRemoverThreadinterval = 5;
        IdleConnectionRemover.threadTurnCounter = 0L;
        LOGGER = Logger.getLogger(IdleConnectionRemover.class.getName());
        init();
    }
}
