package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicationSummaryReplica implements Serializable
{
    private static final long serialVersionUID = 5967001261856109688L;
    private final Date oldestBacklogChangeDate;
    private final Long ldapServerPort;
    private final Long replicationBacklog;
    private final Long peakUpdateRate;
    private final Long recentUpdateRate;
    private final String generationID;
    private final String ldapServerAddress;
    private final String replicaID;
    private final String replicationServerID;
    private final String stringRepresentation;
    
    public ReplicationSummaryReplica(final String value) {
        this.stringRepresentation = value;
        this.replicaID = getElementValue(value, "replica-id");
        this.replicationServerID = getElementValue(value, "connected-to");
        this.generationID = getElementValue(value, "generation-id");
        final String hostPort = getElementValue(value, "ldap-server");
        if (hostPort == null) {
            this.ldapServerAddress = null;
            this.ldapServerPort = null;
        }
        else {
            String a;
            Long p;
            try {
                final int colonPos = hostPort.indexOf(58);
                a = hostPort.substring(0, colonPos);
                p = Long.parseLong(hostPort.substring(colonPos + 1));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                a = null;
                p = null;
            }
            this.ldapServerAddress = a;
            this.ldapServerPort = p;
        }
        String replicationBacklogStr = getElementValue(value, "replication-backlog");
        if (replicationBacklogStr == null) {
            replicationBacklogStr = getElementValue(value, "missing-changes");
        }
        if (replicationBacklogStr == null) {
            this.replicationBacklog = null;
        }
        else {
            Long mc;
            try {
                mc = Long.parseLong(replicationBacklogStr);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                mc = null;
            }
            this.replicationBacklog = mc;
        }
        String rateStr = getElementValue(value, "recent-update-rate");
        if (rateStr == null) {
            this.recentUpdateRate = null;
        }
        else {
            Long r;
            try {
                final int slashPos = rateStr.indexOf(47);
                r = Long.parseLong(rateStr.substring(0, slashPos));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                r = null;
            }
            this.recentUpdateRate = r;
        }
        rateStr = getElementValue(value, "peak-update-rate");
        if (rateStr == null) {
            this.peakUpdateRate = null;
        }
        else {
            Long r;
            try {
                final int slashPos = rateStr.indexOf(47);
                r = Long.parseLong(rateStr.substring(0, slashPos));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                r = null;
            }
            this.peakUpdateRate = r;
        }
        String dateStr = getElementValue(value, "age-of-oldest-backlog-change");
        if (dateStr == null) {
            dateStr = getElementValue(value, "age-of-oldest-missing-change");
        }
        if (dateStr == null) {
            this.oldestBacklogChangeDate = null;
        }
        else {
            Date d;
            try {
                final int spacePos = dateStr.indexOf(32);
                d = StaticUtils.decodeGeneralizedTime(dateStr.substring(0, spacePos));
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                d = null;
            }
            this.oldestBacklogChangeDate = d;
        }
    }
    
    private static String getElementValue(final String s, final String n) {
        final String nPlusEQ = n + "=\"";
        int pos = s.indexOf(nPlusEQ);
        if (pos < 0) {
            return null;
        }
        pos += nPlusEQ.length();
        final int closePos = s.indexOf(34, pos);
        if (closePos <= pos) {
            return null;
        }
        return s.substring(pos, closePos);
    }
    
    public String getReplicaID() {
        return this.replicaID;
    }
    
    public String getLDAPServerAddress() {
        return this.ldapServerAddress;
    }
    
    public Long getLDAPServerPort() {
        return this.ldapServerPort;
    }
    
    public String getReplicationServerID() {
        return this.replicationServerID;
    }
    
    public String getGenerationID() {
        return this.generationID;
    }
    
    public Long getRecentUpdateRate() {
        return this.recentUpdateRate;
    }
    
    public Long getPeakUpdateRate() {
        return this.peakUpdateRate;
    }
    
    @Deprecated
    public Long getMissingChanges() {
        return this.getReplicationBacklog();
    }
    
    public Long getReplicationBacklog() {
        return this.replicationBacklog;
    }
    
    @Deprecated
    public Date getOldestMissingChangeDate() {
        return this.getOldestBacklogChangeDate();
    }
    
    public Date getOldestBacklogChangeDate() {
        return this.oldestBacklogChangeDate;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
}
