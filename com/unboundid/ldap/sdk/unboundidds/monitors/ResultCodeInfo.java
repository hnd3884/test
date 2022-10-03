package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResultCodeInfo implements Serializable
{
    private static final long serialVersionUID = 1223217954357101681L;
    private final double averageResponseTimeMillis;
    private final double percent;
    private final double totalResponseTimeMillis;
    private final int intValue;
    private final long count;
    private final OperationType operationType;
    private final String name;
    
    ResultCodeInfo(final int intValue, final String name, final OperationType operationType, final long count, final double percent, final double totalResponseTimeMillis, final double averageResponseTimeMillis) {
        this.intValue = intValue;
        this.name = name;
        this.operationType = operationType;
        this.count = count;
        this.totalResponseTimeMillis = totalResponseTimeMillis;
        this.averageResponseTimeMillis = averageResponseTimeMillis;
        this.percent = percent;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public OperationType getOperationType() {
        return this.operationType;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public double getPercent() {
        return this.percent;
    }
    
    public double getTotalResponseTimeMillis() {
        return this.totalResponseTimeMillis;
    }
    
    public double getAverageResponseTimeMillis() {
        return this.averageResponseTimeMillis;
    }
}
