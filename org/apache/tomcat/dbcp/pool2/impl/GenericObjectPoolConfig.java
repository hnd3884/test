package org.apache.tomcat.dbcp.pool2.impl;

public class GenericObjectPoolConfig<T> extends BaseObjectPoolConfig<T>
{
    public static final int DEFAULT_MAX_TOTAL = 8;
    public static final int DEFAULT_MAX_IDLE = 8;
    public static final int DEFAULT_MIN_IDLE = 0;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    
    public GenericObjectPoolConfig() {
        this.maxTotal = 8;
        this.maxIdle = 8;
        this.minIdle = 0;
    }
    
    public int getMaxTotal() {
        return this.maxTotal;
    }
    
    public void setMaxTotal(final int maxTotal) {
        this.maxTotal = maxTotal;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
    }
    
    public int getMinIdle() {
        return this.minIdle;
    }
    
    public void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
    }
    
    public GenericObjectPoolConfig<T> clone() {
        try {
            return (GenericObjectPoolConfig)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    @Override
    protected void toStringAppendFields(final StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", maxTotal=");
        builder.append(this.maxTotal);
        builder.append(", maxIdle=");
        builder.append(this.maxIdle);
        builder.append(", minIdle=");
        builder.append(this.minIdle);
    }
}
