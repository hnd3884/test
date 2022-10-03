package redis.clients.jedis;

import java.util.ArrayList;
import redis.clients.util.SafeEncoder;
import java.util.Iterator;
import java.util.List;
import redis.clients.util.ClusterNodeInformation;
import java.util.HashMap;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import redis.clients.util.ClusterNodeInformationParser;

public class JedisClusterInfoCache
{
    public static final ClusterNodeInformationParser nodeInfoParser;
    private Map<String, JedisPool> nodes;
    private Map<Integer, JedisPool> slots;
    private final ReentrantReadWriteLock rwl;
    private final Lock r;
    private final Lock w;
    private final GenericObjectPoolConfig poolConfig;
    private int connectionTimeout;
    private int soTimeout;
    
    public JedisClusterInfoCache(final GenericObjectPoolConfig poolConfig, final int timeout) {
        this(poolConfig, timeout, timeout);
    }
    
    public JedisClusterInfoCache(final GenericObjectPoolConfig poolConfig, final int connectionTimeout, final int soTimeout) {
        this.nodes = new HashMap<String, JedisPool>();
        this.slots = new HashMap<Integer, JedisPool>();
        this.rwl = new ReentrantReadWriteLock();
        this.r = this.rwl.readLock();
        this.w = this.rwl.writeLock();
        this.poolConfig = poolConfig;
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
    }
    
    public void discoverClusterNodesAndSlots(final Jedis jedis) {
        this.w.lock();
        try {
            this.nodes.clear();
            this.slots.clear();
            final String localNodes = jedis.clusterNodes();
            for (final String nodeInfo : localNodes.split("\n")) {
                final ClusterNodeInformation clusterNodeInfo = JedisClusterInfoCache.nodeInfoParser.parse(nodeInfo, new HostAndPort(jedis.getClient().getHost(), jedis.getClient().getPort()));
                final HostAndPort targetNode = clusterNodeInfo.getNode();
                this.setNodeIfNotExist(targetNode);
                this.assignSlotsToNode(clusterNodeInfo.getAvailableSlots(), targetNode);
            }
        }
        finally {
            this.w.unlock();
        }
    }
    
    public void discoverClusterSlots(final Jedis jedis) {
        this.w.lock();
        try {
            this.slots.clear();
            final List<Object> slots = jedis.clusterSlots();
            for (final Object slotInfoObj : slots) {
                final List<Object> slotInfo = (List<Object>)slotInfoObj;
                if (slotInfo.size() <= 2) {
                    continue;
                }
                final List<Integer> slotNums = this.getAssignedSlotArray(slotInfo);
                final List<Object> hostInfos = slotInfo.get(2);
                if (hostInfos.size() <= 0) {
                    continue;
                }
                final HostAndPort targetNode = this.generateHostAndPort(hostInfos);
                this.setNodeIfNotExist(targetNode);
                this.assignSlotsToNode(slotNums, targetNode);
            }
        }
        finally {
            this.w.unlock();
        }
    }
    
    private HostAndPort generateHostAndPort(final List<Object> hostInfos) {
        return new HostAndPort(SafeEncoder.encode(hostInfos.get(0)), hostInfos.get(1).intValue());
    }
    
    public void setNodeIfNotExist(final HostAndPort node) {
        this.w.lock();
        try {
            final String nodeKey = getNodeKey(node);
            if (this.nodes.containsKey(nodeKey)) {
                return;
            }
            final JedisPool nodePool = new JedisPool(this.poolConfig, node.getHost(), node.getPort(), this.connectionTimeout, this.soTimeout, null, 0, null);
            this.nodes.put(nodeKey, nodePool);
        }
        finally {
            this.w.unlock();
        }
    }
    
    public void assignSlotToNode(final int slot, final HostAndPort targetNode) {
        this.w.lock();
        try {
            JedisPool targetPool = this.nodes.get(getNodeKey(targetNode));
            if (targetPool == null) {
                this.setNodeIfNotExist(targetNode);
                targetPool = this.nodes.get(getNodeKey(targetNode));
            }
            this.slots.put(slot, targetPool);
        }
        finally {
            this.w.unlock();
        }
    }
    
    public void assignSlotsToNode(final List<Integer> targetSlots, final HostAndPort targetNode) {
        this.w.lock();
        try {
            JedisPool targetPool = this.nodes.get(getNodeKey(targetNode));
            if (targetPool == null) {
                this.setNodeIfNotExist(targetNode);
                targetPool = this.nodes.get(getNodeKey(targetNode));
            }
            for (final Integer slot : targetSlots) {
                this.slots.put(slot, targetPool);
            }
        }
        finally {
            this.w.unlock();
        }
    }
    
    public JedisPool getNode(final String nodeKey) {
        this.r.lock();
        try {
            return this.nodes.get(nodeKey);
        }
        finally {
            this.r.unlock();
        }
    }
    
    public JedisPool getSlotPool(final int slot) {
        this.r.lock();
        try {
            return this.slots.get(slot);
        }
        finally {
            this.r.unlock();
        }
    }
    
    public Map<String, JedisPool> getNodes() {
        this.r.lock();
        try {
            return new HashMap<String, JedisPool>(this.nodes);
        }
        finally {
            this.r.unlock();
        }
    }
    
    public static String getNodeKey(final HostAndPort hnp) {
        return hnp.getHost() + ":" + hnp.getPort();
    }
    
    public static String getNodeKey(final Client client) {
        return client.getHost() + ":" + client.getPort();
    }
    
    public static String getNodeKey(final Jedis jedis) {
        return getNodeKey(jedis.getClient());
    }
    
    private List<Integer> getAssignedSlotArray(final List<Object> slotInfo) {
        final List<Integer> slotNums = new ArrayList<Integer>();
        for (int slot = slotInfo.get(0).intValue(); slot <= slotInfo.get(1).intValue(); ++slot) {
            slotNums.add(slot);
        }
        return slotNums;
    }
    
    static {
        nodeInfoParser = new ClusterNodeInformationParser();
    }
}
