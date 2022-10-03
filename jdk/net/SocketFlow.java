package jdk.net;

import jdk.Exported;

@Exported
public class SocketFlow
{
    private static final int UNSET = -1;
    public static final int NORMAL_PRIORITY = 1;
    public static final int HIGH_PRIORITY = 2;
    private int priority;
    private long bandwidth;
    private Status status;
    
    private SocketFlow() {
        this.priority = 1;
        this.bandwidth = -1L;
        this.status = Status.NO_STATUS;
    }
    
    public static SocketFlow create() {
        return new SocketFlow();
    }
    
    public SocketFlow priority(final int priority) {
        if (priority != 1 && priority != 2) {
            throw new IllegalArgumentException("invalid priority");
        }
        this.priority = priority;
        return this;
    }
    
    public SocketFlow bandwidth(final long bandwidth) {
        if (bandwidth < 0L) {
            throw new IllegalArgumentException("invalid bandwidth");
        }
        this.bandwidth = bandwidth;
        return this;
    }
    
    public int priority() {
        return this.priority;
    }
    
    public long bandwidth() {
        return this.bandwidth;
    }
    
    public Status status() {
        return this.status;
    }
    
    @Exported
    public enum Status
    {
        NO_STATUS, 
        OK, 
        NO_PERMISSION, 
        NOT_CONNECTED, 
        NOT_SUPPORTED, 
        ALREADY_CREATED, 
        IN_PROGRESS, 
        OTHER;
    }
}
