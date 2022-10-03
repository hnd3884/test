package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.StringTokenizer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SRVRecord implements Serializable
{
    private static final long serialVersionUID = -5505867807717870889L;
    private final int port;
    private final long priority;
    private final long weight;
    private final String address;
    private final String recordString;
    
    SRVRecord(final String recordString) throws LDAPException {
        this.recordString = recordString;
        try {
            final StringTokenizer tokenizer = new StringTokenizer(recordString, " ");
            this.priority = Long.parseLong(tokenizer.nextToken());
            this.weight = Long.parseLong(tokenizer.nextToken());
            this.port = Integer.parseInt(tokenizer.nextToken());
            final String addrString = tokenizer.nextToken();
            if (addrString.endsWith(".")) {
                this.address = addrString.substring(0, addrString.length() - 1);
            }
            else {
                this.address = addrString;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SRV_RECORD_MALFORMED_STRING.get(recordString, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public long getPriority() {
        return this.priority;
    }
    
    public long getWeight() {
        return this.weight;
    }
    
    @Override
    public String toString() {
        return this.recordString;
    }
}
