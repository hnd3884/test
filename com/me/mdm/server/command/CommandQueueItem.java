package com.me.mdm.server.command;

import java.io.Serializable;

public class CommandQueueItem implements Serializable, Comparable<CommandQueueItem>
{
    public Long commandID;
    public int priority;
    public Long addedTime;
    
    public CommandQueueItem() {
        this.addedTime = 0L;
    }
    
    @Override
    public boolean equals(final Object obj) {
        final CommandQueueItem temp = (CommandQueueItem)obj;
        return temp.commandID.equals(this.commandID) && temp.priority == this.priority;
    }
    
    @Override
    public int compareTo(final CommandQueueItem o) {
        if (o.priority == this.priority) {
            return this.addedTime.compareTo(o.addedTime);
        }
        return o.priority - this.priority;
    }
    
    @Override
    public String toString() {
        return "CommandID: " + this.commandID + "\tPriority: " + this.priority + "\t";
    }
}
