package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPSearchConstraints extends LDAPConstraints
{
    private static final long serialVersionUID = -487551577157782460L;
    private int batchSize;
    private int derefPolicy;
    private int sizeLimit;
    private int timeLimit;
    
    public LDAPSearchConstraints() {
        this.batchSize = 1;
        this.derefPolicy = LDAPConnection.DEREF_NEVER;
        this.sizeLimit = 1000;
        this.timeLimit = 0;
    }
    
    public LDAPSearchConstraints(final int msLimit, final int dereference, final int maxResults, final boolean doReferrals, final int batchSize, final LDAPRebind rebindProc, final int hopLimit) {
        this();
        this.derefPolicy = dereference;
        this.sizeLimit = maxResults;
        this.batchSize = batchSize;
        this.setTimeLimit(msLimit);
        this.setReferrals(doReferrals);
        this.setRebindProc(rebindProc);
        this.setHopLimit(hopLimit);
    }
    
    public LDAPSearchConstraints(final int msLimit, final int timeLimit, final int dereference, final int maxResults, final boolean doReferrals, final int batchSize, final LDAPRebind rebindProc, final int hopLimit) {
        this();
        this.derefPolicy = dereference;
        this.sizeLimit = maxResults;
        this.timeLimit = timeLimit;
        this.batchSize = batchSize;
        this.setTimeLimit(msLimit);
        this.setReferrals(doReferrals);
        this.setRebindProc(rebindProc);
        this.setHopLimit(hopLimit);
    }
    
    public LDAPSearchConstraints(final int msLimit, final int timeLimit, final int dereference, final int maxResults, final boolean doReferrals, final int batchSize, final LDAPBind bindProc, final int hopLimit) {
        this();
        this.derefPolicy = dereference;
        this.sizeLimit = maxResults;
        this.timeLimit = timeLimit;
        this.batchSize = batchSize;
        this.setTimeLimit(msLimit);
        this.setReferrals(doReferrals);
        this.setBindProc(bindProc);
        this.setHopLimit(hopLimit);
    }
    
    public int getBatchSize() {
        return this.batchSize;
    }
    
    public void setBatchSize(final int batchSize) {
        if (batchSize < 1) {
            this.batchSize = 1;
        }
        else {
            this.batchSize = batchSize;
        }
    }
    
    public int getDereference() {
        return this.derefPolicy;
    }
    
    public void setDereference(final int dereference) {
        this.derefPolicy = dereference;
    }
    
    public int getMaxResults() {
        return this.sizeLimit;
    }
    
    public void setMaxResults(final int maxResults) {
        if (maxResults < 0) {
            this.sizeLimit = 0;
        }
        else {
            this.sizeLimit = maxResults;
        }
    }
    
    public int getServerTimeLimit() {
        return this.timeLimit;
    }
    
    public void setServerTimeLimit(final int limit) {
        if (limit < 0) {
            this.timeLimit = 0;
        }
        else {
            this.timeLimit = limit;
        }
    }
    
    @Override
    public LDAPSearchConstraints duplicate() {
        final LDAPSearchConstraints c = new LDAPSearchConstraints();
        c.batchSize = this.batchSize;
        c.derefPolicy = this.derefPolicy;
        c.sizeLimit = this.sizeLimit;
        c.timeLimit = this.timeLimit;
        c.setBindProc(this.getBindProc());
        c.setClientControls(this.getClientControls());
        c.setReferrals(this.getReferrals());
        c.setHopLimit(this.getHopLimit());
        c.setRebindProc(this.getRebindProc());
        c.setServerControls(this.getServerControls());
        c.setTimeLimit(this.getTimeLimit());
        return c;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("LDAPSearchConstraints(constraints=");
        buffer.append(super.toString());
        buffer.append(", batchSize=");
        buffer.append(this.batchSize);
        buffer.append(", derefPolicy=");
        buffer.append(this.derefPolicy);
        buffer.append(", maxResults=");
        buffer.append(this.sizeLimit);
        buffer.append(", serverTimeLimit=");
        buffer.append(this.timeLimit);
        buffer.append(')');
        return buffer.toString();
    }
}
