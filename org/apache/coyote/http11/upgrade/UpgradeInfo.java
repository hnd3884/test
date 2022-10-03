package org.apache.coyote.http11.upgrade;

public class UpgradeInfo
{
    private UpgradeGroupInfo groupInfo;
    private volatile long bytesSent;
    private volatile long bytesReceived;
    private volatile long msgsSent;
    private volatile long msgsReceived;
    
    public UpgradeInfo() {
        this.groupInfo = null;
        this.bytesSent = 0L;
        this.bytesReceived = 0L;
        this.msgsSent = 0L;
        this.msgsReceived = 0L;
    }
    
    public UpgradeGroupInfo getGlobalProcessor() {
        return this.groupInfo;
    }
    
    public void setGroupInfo(final UpgradeGroupInfo groupInfo) {
        if (groupInfo == null) {
            if (this.groupInfo != null) {
                this.groupInfo.removeUpgradeInfo(this);
                this.groupInfo = null;
            }
        }
        else {
            (this.groupInfo = groupInfo).addUpgradeInfo(this);
        }
    }
    
    public long getBytesSent() {
        return this.bytesSent;
    }
    
    public void setBytesSent(final long bytesSent) {
        this.bytesSent = bytesSent;
    }
    
    public void addBytesSent(final long bytesSent) {
        this.bytesSent += bytesSent;
    }
    
    public long getBytesReceived() {
        return this.bytesReceived;
    }
    
    public void setBytesReceived(final long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }
    
    public void addBytesReceived(final long bytesReceived) {
        this.bytesReceived += bytesReceived;
    }
    
    public long getMsgsSent() {
        return this.msgsSent;
    }
    
    public void setMsgsSent(final long msgsSent) {
        this.msgsSent = msgsSent;
    }
    
    public void addMsgsSent(final long msgsSent) {
        this.msgsSent += msgsSent;
    }
    
    public long getMsgsReceived() {
        return this.msgsReceived;
    }
    
    public void setMsgsReceived(final long msgsReceived) {
        this.msgsReceived = msgsReceived;
    }
    
    public void addMsgsReceived(final long msgsReceived) {
        this.msgsReceived += msgsReceived;
    }
}
