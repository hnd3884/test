package org.apache.coyote.http11.upgrade;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.modeler.BaseModelMBean;

public class UpgradeGroupInfo extends BaseModelMBean
{
    private final List<UpgradeInfo> upgradeInfos;
    private long deadBytesReceived;
    private long deadBytesSent;
    private long deadMsgsReceived;
    private long deadMsgsSent;
    
    public UpgradeGroupInfo() {
        this.upgradeInfos = new ArrayList<UpgradeInfo>();
        this.deadBytesReceived = 0L;
        this.deadBytesSent = 0L;
        this.deadMsgsReceived = 0L;
        this.deadMsgsSent = 0L;
    }
    
    public synchronized void addUpgradeInfo(final UpgradeInfo ui) {
        this.upgradeInfos.add(ui);
    }
    
    public synchronized void removeUpgradeInfo(final UpgradeInfo ui) {
        if (ui != null) {
            this.deadBytesReceived += ui.getBytesReceived();
            this.deadBytesSent += ui.getBytesSent();
            this.deadMsgsReceived += ui.getMsgsReceived();
            this.deadMsgsSent += ui.getMsgsSent();
            this.upgradeInfos.remove(ui);
        }
    }
    
    public synchronized long getBytesReceived() {
        long bytes = this.deadBytesReceived;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesReceived();
        }
        return bytes;
    }
    
    public synchronized void setBytesReceived(final long bytesReceived) {
        this.deadBytesReceived = bytesReceived;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesReceived(bytesReceived);
        }
    }
    
    public synchronized long getBytesSent() {
        long bytes = this.deadBytesSent;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            bytes += ui.getBytesSent();
        }
        return bytes;
    }
    
    public synchronized void setBytesSent(final long bytesSent) {
        this.deadBytesSent = bytesSent;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            ui.setBytesSent(bytesSent);
        }
    }
    
    public synchronized long getMsgsReceived() {
        long msgs = this.deadMsgsReceived;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsReceived();
        }
        return msgs;
    }
    
    public synchronized void setMsgsReceived(final long msgsReceived) {
        this.deadMsgsReceived = msgsReceived;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsReceived(msgsReceived);
        }
    }
    
    public synchronized long getMsgsSent() {
        long msgs = this.deadMsgsSent;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            msgs += ui.getMsgsSent();
        }
        return msgs;
    }
    
    public synchronized void setMsgsSent(final long msgsSent) {
        this.deadMsgsSent = msgsSent;
        for (final UpgradeInfo ui : this.upgradeInfos) {
            ui.setMsgsSent(msgsSent);
        }
    }
    
    public void resetCounters() {
        this.setBytesReceived(0L);
        this.setBytesSent(0L);
        this.setMsgsReceived(0L);
        this.setMsgsSent(0L);
    }
}
