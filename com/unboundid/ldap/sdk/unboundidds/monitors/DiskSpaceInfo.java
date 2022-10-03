package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DiskSpaceInfo implements Serializable
{
    private static final long serialVersionUID = -7798824641501237274L;
    private final Long totalBytes;
    private final Long usableBytes;
    private final Long usablePercent;
    private final String consumerName;
    private final String path;
    
    public DiskSpaceInfo(final String consumerName, final String path, final Long totalBytes, final Long usableBytes, final long usablePercent) {
        this.consumerName = consumerName;
        this.path = path;
        this.totalBytes = totalBytes;
        this.usableBytes = usableBytes;
        this.usablePercent = usablePercent;
    }
    
    public String getConsumerName() {
        return this.consumerName;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public Long getTotalBytes() {
        return this.totalBytes;
    }
    
    public Long getUsableBytes() {
        return this.usableBytes;
    }
    
    public Long getUsablePercent() {
        return this.usablePercent;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("DiskSpaceInfo(consumerName='");
        buffer.append(this.consumerName);
        buffer.append("', path='");
        buffer.append(this.path);
        buffer.append("', totalBytes=");
        buffer.append(this.totalBytes);
        buffer.append(", usableBytes=");
        buffer.append(this.usableBytes);
        buffer.append(", usablePercent=");
        buffer.append(this.usablePercent);
        buffer.append(')');
    }
}
