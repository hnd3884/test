package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.text.SimpleDateFormat;
import com.unboundid.util.Debug;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplicationSummaryReplicationServer implements Serializable
{
    private static final long serialVersionUID = -3021672478708746554L;
    private final Date replicationServerLastConnected;
    private final Date replicationServerLastFailed;
    private final Long replicationServerFailedAttempts;
    private final Long replicationServerPort;
    private final String generationID;
    private final String replicationServerAddress;
    private final String replicationServerID;
    private final String replicationServerStatus;
    private final String stringRepresentation;
    
    public ReplicationSummaryReplicationServer(final String value) {
        this.stringRepresentation = value;
        this.generationID = getElementValue(value, "generation-id");
        this.replicationServerID = getElementValue(value, "server-id");
        final String hostPort = getElementValue(value, "server");
        if (hostPort == null) {
            this.replicationServerAddress = null;
            this.replicationServerPort = null;
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
            this.replicationServerAddress = a;
            this.replicationServerPort = p;
        }
        this.replicationServerStatus = getElementValue(value, "status");
        this.replicationServerLastConnected = getElementDateValue(value, "last-connected");
        this.replicationServerLastFailed = getElementDateValue(value, "last-failed");
        this.replicationServerFailedAttempts = getElementLongValue(value, "failed-attempts");
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
    
    private static Date getElementDateValue(final String s, final String n) {
        final String stringValue = getElementValue(s, n);
        if (stringValue == null) {
            return null;
        }
        try {
            final SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return f.parse(stringValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    private static Long getElementLongValue(final String s, final String n) {
        final String stringValue = getElementValue(s, n);
        if (stringValue == null) {
            return null;
        }
        try {
            return Long.valueOf(stringValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public String getReplicationServerID() {
        return this.replicationServerID;
    }
    
    public String getReplicationServerAddress() {
        return this.replicationServerAddress;
    }
    
    public Long getReplicationServerPort() {
        return this.replicationServerPort;
    }
    
    public String getGenerationID() {
        return this.generationID;
    }
    
    public String getReplicationServerStatus() {
        return this.replicationServerStatus;
    }
    
    public Date getReplicationServerLastConnected() {
        return this.replicationServerLastConnected;
    }
    
    public Date getReplicationServerLastFailed() {
        return this.replicationServerLastFailed;
    }
    
    public Long getReplicationServerFailedAttempts() {
        return this.replicationServerFailedAttempts;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
}
