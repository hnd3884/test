package com.zoho.security.cache;

import java.util.Map;
import java.util.Iterator;
import com.zoho.security.util.HashUtil;
import java.util.List;
import java.util.HashSet;
import org.w3c.dom.Element;
import com.zoho.security.api.Range;
import java.util.Set;

public class CacheConfiguration
{
    private String clusterIP;
    private int clusterPort;
    private Set<String> sentinels;
    private String masterName;
    private String poolName;
    private int db;
    private Range<Long> range;
    private int maxTotal;
    private int minIdle;
    private int maxIdle;
    private int readTimeoutInMilliSeconds;
    private ConfigurationType configurationType;
    
    public CacheConfiguration(final Element cacheConfigElement) {
        this.db = 0;
        this.range = (Range<Long>)Range.createRange(Long.MIN_VALUE, Long.MAX_VALUE, null);
        this.maxTotal = 8;
        this.minIdle = 0;
        this.maxIdle = 0;
        this.readTimeoutInMilliSeconds = 5000;
        this.configurationType = ("".equals(cacheConfigElement.getAttribute(Attributes.CLUSTER_IP.getAttributeName())) ? ConfigurationType.REDIS_SENTINEL : ConfigurationType.REDIS_CLUSTER);
        if (this.configurationType == ConfigurationType.REDIS_CLUSTER) {
            this.clusterIP = cacheConfigElement.getAttribute(Attributes.CLUSTER_IP.getAttributeName());
            this.clusterPort = ("".equals(cacheConfigElement.getAttribute(Attributes.CLUSTER_PORT.getAttributeName())) ? 6379 : Integer.valueOf(cacheConfigElement.getAttribute(Attributes.CLUSTER_PORT.getAttributeName())));
        }
        else {
            this.sentinels = new HashSet<String>();
            for (final String sentinel : cacheConfigElement.getAttribute(Attributes.SENTINELS.getAttributeName()).split(",")) {
                this.sentinels.add(sentinel.trim());
            }
            this.masterName = cacheConfigElement.getAttribute(Attributes.MASTER_NAME.getAttributeName());
        }
        this.poolName = cacheConfigElement.getAttribute(Attributes.POOL_NAME.getAttributeName());
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.DB.getAttributeName()))) {
            this.db = Integer.valueOf(cacheConfigElement.getAttribute(Attributes.DB.getAttributeName()));
        }
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.MAX_TOTAL.getAttributeName()))) {
            this.maxTotal = Integer.valueOf(cacheConfigElement.getAttribute(Attributes.MAX_TOTAL.getAttributeName()));
        }
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.MIN_IDLE.getAttributeName()))) {
            this.minIdle = Integer.valueOf(cacheConfigElement.getAttribute(Attributes.MIN_IDLE.getAttributeName()));
        }
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.MAX_IDLE.getAttributeName()))) {
            this.maxIdle = Integer.valueOf(cacheConfigElement.getAttribute(Attributes.MAX_IDLE.getAttributeName()));
        }
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.READ_TIMEOUT.getAttributeName()))) {
            this.readTimeoutInMilliSeconds = Integer.valueOf(cacheConfigElement.getAttribute(Attributes.READ_TIMEOUT.getAttributeName()));
        }
        if (!"".equals(cacheConfigElement.getAttribute(Attributes.RANGES.getAttributeName()))) {
            final String[] splittedValues = cacheConfigElement.getAttribute(Attributes.RANGES.getAttributeName()).split("/");
            this.range = (Range<Long>)Range.createRange(Long.valueOf(splittedValues[0].trim()), Long.valueOf(splittedValues[1].trim()), null);
        }
    }
    
    public CacheConfiguration(final String poolName, final String clusterIP, final int clusterPort) {
        this.db = 0;
        this.range = (Range<Long>)Range.createRange(Long.MIN_VALUE, Long.MAX_VALUE, null);
        this.maxTotal = 8;
        this.minIdle = 0;
        this.maxIdle = 0;
        this.readTimeoutInMilliSeconds = 5000;
        this.poolName = poolName;
        this.clusterIP = clusterIP;
        this.clusterPort = clusterPort;
        this.configurationType = ConfigurationType.REDIS_CLUSTER;
    }
    
    public CacheConfiguration(final String poolName, final String masterName, final Set<String> sentinels) {
        this.db = 0;
        this.range = (Range<Long>)Range.createRange(Long.MIN_VALUE, Long.MAX_VALUE, null);
        this.maxTotal = 8;
        this.minIdle = 0;
        this.maxIdle = 0;
        this.readTimeoutInMilliSeconds = 5000;
        this.poolName = poolName;
        this.masterName = masterName;
        this.sentinels = sentinels;
        this.configurationType = ConfigurationType.REDIS_SENTINEL;
    }
    
    public boolean isInRange(final long hash) {
        return this.range.contains(hash);
    }
    
    public static CacheConfiguration getCacheConfiguration(final String key, final String poolName, final List<CacheConfiguration> cacheConfigurationList) {
        for (final CacheConfiguration cacheConfiguration : cacheConfigurationList) {
            if (cacheConfiguration.getPoolName().equals(poolName)) {
                final long hash = HashUtil.murmurHash(key);
                if (cacheConfiguration.isInRange(hash)) {
                    return cacheConfiguration;
                }
                continue;
            }
        }
        return null;
    }
    
    public ConfigurationType getConfigurationType() {
        return this.configurationType;
    }
    
    public String getClusterIP() {
        return this.clusterIP;
    }
    
    public int getClusterPort() {
        return this.clusterPort;
    }
    
    public String getMasterName() {
        return this.masterName;
    }
    
    public Set<String> getSentinels() {
        return this.sentinels;
    }
    
    public String getPoolName() {
        return this.poolName;
    }
    
    public void setDb(final int db) {
        this.db = db;
    }
    
    public int getDb() {
        return this.db;
    }
    
    public void setRange(final Range<Long> range) {
        this.range = range;
    }
    
    public Range<Long> getRange() {
        return this.range;
    }
    
    public void setMaxTotal(final int maxTotal) {
        this.maxTotal = maxTotal;
    }
    
    public int getMaxTotal() {
        return this.maxTotal;
    }
    
    public void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
    }
    
    public int getMinIdle() {
        return this.minIdle;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setReadTimeoutInMilliSeconds(final int readTimeoutInMilliSeconds) {
        this.readTimeoutInMilliSeconds = readTimeoutInMilliSeconds;
    }
    
    public int getReadTimeoutInMilliSeconds() {
        return this.readTimeoutInMilliSeconds;
    }
    
    public static CacheConfiguration getCacheConfiguration(final String cacheSelectionKey, final Map<String, List<CacheConfiguration>> poolNameVsCacheConfigurationMap, final String poolName) {
        final List<CacheConfiguration> cacheConfigurations = poolNameVsCacheConfigurationMap.get(poolName);
        if (cacheConfigurations != null) {
            final long hash = HashUtil.murmurHash(cacheSelectionKey);
            for (final CacheConfiguration cacheConfiguration : cacheConfigurations) {
                if (cacheConfiguration.isInRange(hash)) {
                    return cacheConfiguration;
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("CacheConfiguration:: poolName: ");
        strBuilder.append(this.poolName);
        strBuilder.append(", configurationType: ");
        strBuilder.append(this.configurationType);
        strBuilder.append(", clusterIP: ");
        strBuilder.append(this.clusterIP);
        strBuilder.append(", clusterPort: ");
        strBuilder.append(this.clusterPort);
        strBuilder.append(", masterName: ");
        strBuilder.append(this.masterName);
        strBuilder.append(", sentinels: ");
        strBuilder.append(this.sentinels);
        strBuilder.append(", db: ");
        strBuilder.append(this.db);
        strBuilder.append(", range: ");
        strBuilder.append(this.range.toString());
        strBuilder.append(", maxTotal: ");
        strBuilder.append(this.maxTotal);
        strBuilder.append(", minIdle: ");
        strBuilder.append(this.minIdle);
        strBuilder.append(", maxIdle: ");
        strBuilder.append(this.maxIdle);
        strBuilder.append(", readTimeoutInMilliSeconds: ");
        strBuilder.append(this.readTimeoutInMilliSeconds);
        return strBuilder.append(".").toString();
    }
    
    public enum ConfigurationType
    {
        REDIS_CLUSTER, 
        REDIS_SENTINEL;
    }
    
    public enum Attributes
    {
        POOL_NAME("pool-name"), 
        CLUSTER_IP("cluster-ip"), 
        CLUSTER_PORT("cluster-port"), 
        DB("db"), 
        RANGES("ranges"), 
        MAX_TOTAL("max-total"), 
        MIN_IDLE("min-idle"), 
        MAX_IDLE("max-idle"), 
        READ_TIMEOUT("read-timeout"), 
        MASTER_NAME("master-name"), 
        SENTINELS("sentinels");
        
        private String attributeName;
        
        private Attributes(final String attributeName) {
            this.attributeName = null;
            this.attributeName = attributeName;
        }
        
        public String getAttributeName() {
            return this.attributeName;
        }
    }
}
