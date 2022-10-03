package redis.clients.jedis;

import java.util.List;

public interface ClusterCommands
{
    String clusterNodes();
    
    String clusterMeet(final String p0, final int p1);
    
    String clusterAddSlots(final int... p0);
    
    String clusterDelSlots(final int... p0);
    
    String clusterInfo();
    
    List<String> clusterGetKeysInSlot(final int p0, final int p1);
    
    String clusterSetSlotNode(final int p0, final String p1);
    
    String clusterSetSlotMigrating(final int p0, final String p1);
    
    String clusterSetSlotImporting(final int p0, final String p1);
    
    String clusterSetSlotStable(final int p0);
    
    String clusterForget(final String p0);
    
    String clusterFlushSlots();
    
    Long clusterKeySlot(final String p0);
    
    Long clusterCountKeysInSlot(final int p0);
    
    String clusterSaveConfig();
    
    String clusterReplicate(final String p0);
    
    List<String> clusterSlaves(final String p0);
    
    String clusterFailover();
    
    List<Object> clusterSlots();
    
    String clusterReset(final JedisCluster.Reset p0);
}
