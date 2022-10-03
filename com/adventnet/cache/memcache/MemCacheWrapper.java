package com.adventnet.cache.memcache;

import com.adventnet.persistence.PersistenceInitializer;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import com.danga.MemCached.SockIOPool;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import com.zoho.conf.Configuration;
import java.util.Properties;
import com.adventnet.cache.Cache;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MemCacheWrapper
{
    private static boolean isInitialized;
    private static final Logger LOG;
    private static boolean binary;
    private static Map<String, Boolean> protocolPoolMap;
    private static String memcacheWrapperClass;
    private static Boolean memcache_instrument_disabled;
    private static final List<String> POOLNAMES;
    
    public static Cache getCache(final String poolName) {
        final Boolean tempBinary = MemCacheWrapper.protocolPoolMap.get(poolName);
        if (tempBinary != null) {
            return createMemCacheImpl(poolName, tempBinary);
        }
        return createMemCacheImpl(poolName, MemCacheWrapper.binary);
    }
    
    public static boolean isPoolAvailable(final String poolName) {
        return MemCacheWrapper.POOLNAMES.contains(poolName);
    }
    
    public static void initialize() {
        if (MemCacheWrapper.isInitialized) {
            return;
        }
        MemCacheWrapper.isInitialized = true;
        Properties prop = null;
        DataSet poolObj = null;
        Connection conn = null;
        try {
            prop = new Properties();
            prop.load(new FileInputStream(Configuration.getString("app.home") + "/conf/Cache/memcache/details.conf"));
            MemCacheWrapper.LOG.log(Level.FINE, "prop == " + prop);
            final String isBinaryStr = prop.getProperty("binary");
            if (isBinaryStr != null) {
                MemCacheWrapper.binary = Boolean.parseBoolean(isBinaryStr);
            }
            final SelectQuery sq1 = (SelectQuery)new SelectQueryImpl(new Table("ServerDetails"));
            sq1.addSelectColumn(new Column("ServerDetails", "POOLNAME"));
            sq1.setDistinct(true);
            conn = RelationalAPI.getInstance().getConnection();
            poolObj = RelationalAPI.getInstance().executeQuery((Query)sq1, conn);
            MemCacheWrapper.LOG.log(Level.FINE, "poolObj" + poolObj);
            final SelectQuery sq2 = QueryConstructor.get("ServerDetails", (Criteria)null);
            sq2.addSortColumn(new SortColumn(new Column("ServerDetails", "POOLNAME"), true));
            sq2.addSortColumn(new SortColumn(new Column("ServerDetails", "SERVERORDER"), true));
            final DataObject dObj = DataAccess.get(sq2);
            MemCacheWrapper.LOG.log(Level.FINE, "dObj" + dObj);
            String[] serverArr = null;
            Integer[] weightArr = null;
            String poolName = null;
            Iterator<Row> rowIter = null;
            List<String> serverList = null;
            List<Integer> weightList = null;
            Row row = null;
            while (poolObj.next()) {
                poolName = (String)poolObj.getValue("POOLNAME");
                MemCacheWrapper.LOG.log(Level.FINE, "poolName : " + poolName);
                serverList = new ArrayList<String>();
                weightList = new ArrayList<Integer>();
                rowIter = dObj.getRows("ServerDetails", new Criteria(new Column("ServerDetails", "POOLNAME"), (Object)poolName, 0));
                while (rowIter.hasNext()) {
                    row = rowIter.next();
                    serverList.add((String)row.get("SERVERNAME"));
                    weightList.add((Integer)row.get("WEIGHT"));
                }
                serverArr = new String[serverList.size()];
                serverArr = serverList.toArray(serverArr);
                weightArr = new Integer[weightList.size()];
                weightArr = weightList.toArray(weightArr);
                initializePool(poolName, serverArr, weightArr, prop);
            }
        }
        catch (final Exception exp) {
            MemCacheWrapper.LOG.log(Level.SEVERE, "Exception while initialize", exp);
            try {
                if (poolObj != null) {
                    poolObj.close();
                }
            }
            catch (final Exception exp) {
                MemCacheWrapper.LOG.log(Level.SEVERE, "Exception while initialize", exp);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp) {
                MemCacheWrapper.LOG.log(Level.SEVERE, "Exception while initialize", exp);
            }
        }
        finally {
            try {
                if (poolObj != null) {
                    poolObj.close();
                }
            }
            catch (final Exception exp2) {
                MemCacheWrapper.LOG.log(Level.SEVERE, "Exception while initialize", exp2);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp2) {
                MemCacheWrapper.LOG.log(Level.SEVERE, "Exception while initialize", exp2);
            }
        }
    }
    
    public static void initialize(final Properties prop) {
        if (prop != null) {
            final String isBinaryStr = prop.getProperty("binary");
            if (isBinaryStr != null) {
                MemCacheWrapper.binary = Boolean.parseBoolean(isBinaryStr);
            }
            final String poolNames = prop.getProperty("pools");
            if (poolNames != null) {
                final String[] pools = poolNames.split(",");
                String[] serverArr = null;
                Integer[] weightArr = null;
                String[] weightStrArr = null;
                String serversStr = null;
                String weightStr = null;
                for (final String poolName : pools) {
                    serversStr = prop.getProperty(poolName + ".servers");
                    if (serversStr != null) {
                        serverArr = serversStr.split(",");
                        weightArr = new Integer[serverArr.length];
                        weightStr = prop.getProperty(poolName + ".weights");
                        if (weightStr != null) {
                            weightStrArr = weightStr.split(",");
                            if (weightStrArr.length != serverArr.length) {
                                throw new IllegalArgumentException("weights should be equal to servers");
                            }
                            for (int i = 0; i < serverArr.length; ++i) {
                                weightArr[i] = Integer.parseInt(weightStrArr[i]);
                                MemCacheWrapper.LOG.log(Level.INFO, "weight == {0}", weightArr[i]);
                            }
                        }
                        else {
                            for (int i = 0; i < serverArr.length; ++i) {
                                weightArr[i] = 1;
                            }
                        }
                        initializePool(poolName, serverArr, weightArr, prop);
                    }
                }
            }
        }
    }
    
    private static void initializePool(final String poolName, final String[] serverArr, final Integer[] weightArr, final Properties prop) {
        if (MemCacheWrapper.POOLNAMES.contains(poolName)) {
            return;
        }
        MemCacheWrapper.POOLNAMES.add(poolName);
        String tempStr = null;
        String tempStr2 = null;
        final SockIOPool pool = SockIOPool.getInstance(poolName);
        pool.setServers(serverArr);
        pool.setWeights(weightArr);
        tempStr = poolName + ".binary";
        final String isBinaryStr = prop.getProperty(tempStr);
        if (isBinaryStr != null) {
            MemCacheWrapper.protocolPoolMap.put(poolName, Boolean.valueOf(isBinaryStr));
        }
        tempStr = poolName + ".initConn";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("initConn"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting initConn    " + tempStr2);
            pool.setInitConn(Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".minConn";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("minConn"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting minConn" + tempStr2);
            pool.setMinConn(Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".maxConn";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("maxConn"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting maxConn" + tempStr2);
            pool.setMaxConn(Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".maxIdle";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("maxIdle"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting maxIdle" + tempStr2);
            pool.setMaxIdle((long)Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".maxBusyTime";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("maxBusyTime"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting maxBusyTime" + tempStr2);
            pool.setMaxBusyTime((long)Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".maintSleep";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("maintSleep"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting maintSleep" + tempStr2);
            pool.setMaintSleep((long)Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".socketTO";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("socketTO"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting socketTO" + tempStr2);
            pool.setSocketTO(Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".socketConnectTO";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("socketConnectTO"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting socketConnectTO" + tempStr2);
            pool.setSocketConnectTO(Integer.parseInt(tempStr2));
        }
        tempStr = poolName + ".aliveCheck";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("aliveCheck"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting aliveCheck" + tempStr2);
            pool.setAliveCheck(Boolean.parseBoolean(tempStr2));
        }
        tempStr = poolName + ".failover";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("failover"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting failover" + tempStr2);
            pool.setFailover(Boolean.parseBoolean(tempStr2));
        }
        tempStr = poolName + ".failback";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("failback"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting failback" + tempStr2);
            pool.setFailback(Boolean.parseBoolean(tempStr2));
        }
        tempStr = poolName + ".nagle";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("nagle"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting nagle" + tempStr2);
            pool.setNagle(Boolean.parseBoolean(tempStr2));
        }
        tempStr = poolName + ".hashingAlg";
        tempStr2 = ((prop.getProperty(tempStr) != null) ? prop.getProperty(tempStr) : prop.getProperty("hashingAlg"));
        if (tempStr2 != null) {
            MemCacheWrapper.LOG.log(Level.FINE, "setting hashingAlg" + tempStr2);
            if (tempStr2.equals("CONSISTENT_HASH")) {
                pool.setHashingAlg(3);
            }
            else if (tempStr2.equals("OLD_COMPAT_HASH")) {
                pool.setHashingAlg(1);
            }
            else if (tempStr2.equals("NEW_COMPAT_HASH")) {
                pool.setHashingAlg(2);
            }
            else {
                pool.setHashingAlg(0);
            }
        }
        pool.initialize();
        MemCacheWrapper.LOG.log(Level.INFO, "Successfully Initialized {0}", poolName);
    }
    
    private static Cache createMemCacheImpl(final String poolName, final boolean binary) {
        Cache wrappedCache = null;
        if (MemCacheWrapper.memcacheWrapperClass == null || MemCacheWrapper.memcache_instrument_disabled.equals(true)) {
            return new MemcacheImpl(poolName, binary);
        }
        try {
            final Constructor cons = Class.forName(MemCacheWrapper.memcacheWrapperClass).getConstructor(String.class, Boolean.class);
            wrappedCache = cons.newInstance(poolName, binary);
            MemCacheWrapper.LOG.log(Level.INFO, "MemCache interface wrapped with instrument impl");
        }
        catch (final Throwable exc) {
            MemCacheWrapper.LOG.log(Level.INFO, "Exception while wrapping instrument cache", exc);
            return new MemcacheImpl(poolName, binary);
        }
        return wrappedCache;
    }
    
    static {
        MemCacheWrapper.isInitialized = false;
        LOG = Logger.getLogger(MemCacheWrapper.class.getName());
        MemCacheWrapper.binary = false;
        MemCacheWrapper.protocolPoolMap = new HashMap<String, Boolean>(10);
        MemCacheWrapper.memcacheWrapperClass = PersistenceInitializer.getConfigurationValue("MemcacheWrapperImpl");
        MemCacheWrapper.memcache_instrument_disabled = Boolean.getBoolean("memcache.disableinstrument");
        POOLNAMES = new ArrayList<String>(10);
        MemCacheWrapper.LOG.log(Level.FINE, "Inside Static block of MemcacheWrapper");
    }
}
