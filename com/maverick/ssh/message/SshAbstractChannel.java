package com.maverick.ssh.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshChannel;

public abstract class SshAbstractChannel implements SshChannel
{
    public static final int CHANNEL_UNINITIALIZED = 1;
    public static final int CHANNEL_OPEN = 2;
    public static final int CHANNEL_CLOSED = 3;
    protected int channelid;
    protected int state;
    protected SshMessageRouter manager;
    protected SshMessageStore ms;
    
    public SshAbstractChannel() {
        this.channelid = -1;
        this.state = 1;
    }
    
    protected SshMessageStore getMessageStore() throws SshException {
        if (this.ms == null) {
            throw new SshException("Channel is not initialized!", 5);
        }
        return this.ms;
    }
    
    public int getChannelId() {
        return this.channelid;
    }
    
    public SshMessageRouter getMessageRouter() {
        return this.manager;
    }
    
    protected void init(final SshMessageRouter manager, final int channelid) {
        this.channelid = channelid;
        this.manager = manager;
        this.ms = new SshMessageStore(manager, this, this.getStickyMessageIds());
    }
    
    protected abstract MessageObserver getStickyMessageIds();
    
    public boolean isClosed() {
        return this.state == 3;
    }
    
    public void idle() {
    }
    
    protected abstract boolean processChannelMessage(final SshChannelMessage p0) throws SshException;
}
