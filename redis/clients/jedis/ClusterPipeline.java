package redis.clients.jedis;

import java.util.List;

public interface ClusterPipeline
{
    Response<String> clusterNodes();
    
    Response<String> clusterMeet(final String p0, final int p1);
    
    Response<String> clusterAddSlots(final int... p0);
    
    Response<String> clusterDelSlots(final int... p0);
    
    Response<String> clusterInfo();
    
    Response<List<String>> clusterGetKeysInSlot(final int p0, final int p1);
    
    Response<String> clusterSetSlotNode(final int p0, final String p1);
    
    Response<String> clusterSetSlotMigrating(final int p0, final String p1);
    
    Response<String> clusterSetSlotImporting(final int p0, final String p1);
}
