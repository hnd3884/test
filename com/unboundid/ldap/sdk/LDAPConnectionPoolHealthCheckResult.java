package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPConnectionPoolHealthCheckResult implements Serializable
{
    private static final long serialVersionUID = -7312002973891471180L;
    private final int numDefunct;
    private final int numExamined;
    private final int numExpired;
    
    LDAPConnectionPoolHealthCheckResult(final int numExamined, final int numExpired, final int numDefunct) {
        this.numExamined = numExamined;
        this.numExpired = numExpired;
        this.numDefunct = numDefunct;
    }
    
    public int getNumExamined() {
        return this.numExamined;
    }
    
    public int getNumExpired() {
        return this.numExpired;
    }
    
    public int getNumDefunct() {
        return this.numDefunct;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnectionPoolHealthCheckResult(numExamined=");
        buffer.append(this.numExamined);
        buffer.append(", numExpired=");
        buffer.append(this.numExpired);
        buffer.append(", numDefunct=");
        buffer.append(this.numDefunct);
        buffer.append(')');
    }
}
