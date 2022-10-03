package com.adventnet.cache.memcache.admin;

import java.util.HashMap;
import java.util.Map;
import com.adventnet.cache.Cache;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.cache.memcache.MemCacheWrapper;
import com.zoho.conf.Configuration;
import java.util.logging.Logger;
import com.adventnet.cache.CacheFactory;
import java.util.List;

public final class MemcacheAdmin
{
    private final List<String> poolNames;
    private final CacheFactory cFac;
    private static MemcacheAdmin memcacheAdmin;
    private static final Logger LOG;
    
    private MemcacheAdmin() {
        if ("/grid".equalsIgnoreCase(Configuration.getString("app.context"))) {
            MemCacheWrapper.initialize();
        }
        this.cFac = CacheFactory.getInstance();
        this.poolNames = new ArrayList<String>();
        final SelectQuery sq1 = (SelectQuery)new SelectQueryImpl(new Table("ServerDetails"));
        sq1.addSelectColumn(new Column("ServerDetails", "POOLNAME"));
        sq1.setDistinct(true);
        DataSet poolObj = null;
        Connection conn = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            poolObj = RelationalAPI.getInstance().executeQuery((Query)sq1, conn);
            MemcacheAdmin.LOG.log(Level.FINE, "poolObj" + poolObj);
            String poolName = null;
            while (poolObj.next()) {
                poolName = (String)poolObj.getValue("POOLNAME");
                this.cFac.createCache(poolName, 2, poolName);
                this.poolNames.add(poolName);
            }
        }
        catch (final Exception ex) {
            MemcacheAdmin.LOG.log(Level.SEVERE, null, ex);
            try {
                if (poolObj != null) {
                    poolObj.close();
                }
            }
            catch (final Exception exp) {
                MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at constructor initialize", exp);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp) {
                MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at constructor initialize", exp);
            }
        }
        finally {
            try {
                if (poolObj != null) {
                    poolObj.close();
                }
            }
            catch (final Exception exp2) {
                MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at constructor initialize", exp2);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp2) {
                MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at constructor initialize", exp2);
            }
        }
    }
    
    public static MemcacheAdmin getInstance() {
        if (MemcacheAdmin.memcacheAdmin == null) {
            MemcacheAdmin.memcacheAdmin = new MemcacheAdmin();
        }
        return MemcacheAdmin.memcacheAdmin;
    }
    
    public List<String> getPoolNames() {
        return this.poolNames;
    }
    
    public boolean purgeCache(final String poolName) {
        MemcacheAdmin.LOG.info("purgeCache called......." + poolName);
        final Cache ch = this.cFac.getCache(poolName);
        if (ch != null) {
            try {
                ch.purgeCache();
            }
            catch (final Exception exp) {
                MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at purgeCache", exp);
                return false;
            }
        }
        return true;
    }
    
    public boolean purgeAllCache() {
        MemcacheAdmin.LOG.info("purgeAllCache called.......");
        boolean retBool = true;
        boolean tempBool = true;
        if (this.poolNames != null) {
            for (int i = 0, j = this.poolNames.size(); i < j; ++i) {
                tempBool = this.purgeCache(this.poolNames.get(i));
                if (!tempBool) {
                    retBool = tempBool;
                }
            }
        }
        return retBool;
    }
    
    public boolean deleteFromCache(final String key, final String poolName) {
        if (key == null) {
            return false;
        }
        if (poolName != null) {
            final Cache ch = this.cFac.getCache(poolName);
            if (ch != null) {
                try {
                    ch.remove(key);
                }
                catch (final Exception exp) {
                    MemcacheAdmin.LOG.log(Level.SEVERE, "Exception at deleteFromCache", exp);
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean deleteFromCacheRepo(final String key) {
        if (this.poolNames != null) {
            for (int i = 0, j = this.poolNames.size(); i < j; ++i) {
                if (this.deleteFromCache(key, this.poolNames.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Map fetchStats(final String poolName) {
        if (poolName != null) {
            final Cache ch = this.cFac.getCache(poolName);
            if (ch != null) {
                try {
                    return ch.getStats();
                }
                catch (final Exception exp) {
                    MemcacheAdmin.LOG.log(Level.SEVERE, "Exception while Fetching stats", exp);
                }
            }
        }
        return null;
    }
    
    public Map<String, Map> fetchAllStats() {
        if (this.poolNames != null) {
            final Map<String, Map> retMap = new HashMap<String, Map>();
            String tempPoolName = null;
            Map tempMap = null;
            for (int i = 0, j = this.poolNames.size(); i < j; ++i) {
                tempPoolName = this.poolNames.get(i);
                tempMap = this.fetchStats(tempPoolName);
                if (tempMap != null) {
                    retMap.put(tempPoolName, tempMap);
                }
            }
            return retMap;
        }
        return null;
    }
    
    public Map<String, Map> fetchStats(final String[] poolNamesArr) {
        if (poolNamesArr != null) {
            final Map<String, Map> retMap = new HashMap<String, Map>();
            String tempPoolName = null;
            Map tempMap = null;
            for (int i = 0, j = poolNamesArr.length; i < j; ++i) {
                tempPoolName = poolNamesArr[i];
                tempMap = this.fetchStats(tempPoolName);
                if (tempMap != null) {
                    retMap.put(tempPoolName, tempMap);
                }
            }
            return retMap;
        }
        return null;
    }
    
    public String getFormattedUpTime(final long uptimeIn) {
        final long sec = uptimeIn % 60L;
        long uptime = uptimeIn / 60L;
        final long min = uptime % 60L;
        uptime /= 60L;
        final long hr = uptime % 24L;
        final long days;
        uptime = (days = uptime / 24L);
        final StringBuffer buff = new StringBuffer().append(days).append(":").append(hr).append(":").append(min).append(":").append(sec);
        MemcacheAdmin.LOG.info(buff.toString());
        return buff.toString();
    }
    
    public String getInMB(final String bytesStrIn) {
        final String bytesStr = bytesStrIn.trim();
        if (bytesStr == null) {
            return null;
        }
        long bytes = Long.parseLong(bytesStr);
        bytes /= 1024L;
        bytes /= 1024L;
        return String.valueOf(bytes);
    }
    
    static {
        LOG = Logger.getLogger(MemcacheAdmin.class.getName());
    }
}
