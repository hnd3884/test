package redis.clients.util;

import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.HostAndPort;

public class ClusterNodeInformation
{
    private HostAndPort node;
    private List<Integer> availableSlots;
    private List<Integer> slotsBeingImported;
    private List<Integer> slotsBeingMigrated;
    
    public ClusterNodeInformation(final HostAndPort node) {
        this.node = node;
        this.availableSlots = new ArrayList<Integer>();
        this.slotsBeingImported = new ArrayList<Integer>();
        this.slotsBeingMigrated = new ArrayList<Integer>();
    }
    
    public void addAvailableSlot(final int slot) {
        this.availableSlots.add(slot);
    }
    
    public void addSlotBeingImported(final int slot) {
        this.slotsBeingImported.add(slot);
    }
    
    public void addSlotBeingMigrated(final int slot) {
        this.slotsBeingMigrated.add(slot);
    }
    
    public HostAndPort getNode() {
        return this.node;
    }
    
    public List<Integer> getAvailableSlots() {
        return this.availableSlots;
    }
    
    public List<Integer> getSlotsBeingImported() {
        return this.slotsBeingImported;
    }
    
    public List<Integer> getSlotsBeingMigrated() {
        return this.slotsBeingMigrated;
    }
}
