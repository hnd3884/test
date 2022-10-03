package ua_parser;

import org.apache.commons.collections4.map.LRUMap;
import java.io.InputStream;
import java.util.Map;

public class CachingParser extends Parser
{
    private static final int CACHE_SIZE = 1000;
    private Map<String, Client> cacheClient;
    private Map<String, UserAgent> cacheUserAgent;
    private Map<String, Device> cacheDevice;
    private Map<String, OS> cacheOS;
    
    public CachingParser() {
        this.cacheClient = null;
        this.cacheUserAgent = null;
        this.cacheDevice = null;
        this.cacheOS = null;
    }
    
    public CachingParser(final InputStream regexYaml) {
        super(regexYaml);
        this.cacheClient = null;
        this.cacheUserAgent = null;
        this.cacheDevice = null;
        this.cacheOS = null;
    }
    
    @Override
    public Client parse(final String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheClient == null) {
            this.cacheClient = (Map<String, Client>)new LRUMap(1000);
        }
        Client client = this.cacheClient.get(agentString);
        if (client != null) {
            return client;
        }
        client = super.parse(agentString);
        this.cacheClient.put(agentString, client);
        return client;
    }
    
    @Override
    public UserAgent parseUserAgent(final String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheUserAgent == null) {
            this.cacheUserAgent = (Map<String, UserAgent>)new LRUMap(1000);
        }
        UserAgent userAgent = this.cacheUserAgent.get(agentString);
        if (userAgent != null) {
            return userAgent;
        }
        userAgent = super.parseUserAgent(agentString);
        this.cacheUserAgent.put(agentString, userAgent);
        return userAgent;
    }
    
    @Override
    public Device parseDevice(final String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheDevice == null) {
            this.cacheDevice = (Map<String, Device>)new LRUMap(1000);
        }
        Device device = this.cacheDevice.get(agentString);
        if (device != null) {
            return device;
        }
        device = super.parseDevice(agentString);
        this.cacheDevice.put(agentString, device);
        return device;
    }
    
    @Override
    public OS parseOS(final String agentString) {
        if (agentString == null) {
            return null;
        }
        if (this.cacheOS == null) {
            this.cacheOS = (Map<String, OS>)new LRUMap(1000);
        }
        OS os = this.cacheOS.get(agentString);
        if (os != null) {
            return os;
        }
        os = super.parseOS(agentString);
        this.cacheOS.put(agentString, os);
        return os;
    }
}
