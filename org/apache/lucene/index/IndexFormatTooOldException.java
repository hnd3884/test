package org.apache.lucene.index;

import java.util.Objects;
import org.apache.lucene.store.DataInput;
import java.io.IOException;

public class IndexFormatTooOldException extends IOException
{
    private final String resourceDescription;
    private final String reason;
    private final Integer version;
    private final Integer minVersion;
    private final Integer maxVersion;
    
    public IndexFormatTooOldException(final String resourceDescription, final String reason) {
        super("Format version is not supported (resource " + resourceDescription + "): " + reason + ". This version of Lucene only supports indexes created with release 4.0 and later.");
        this.resourceDescription = resourceDescription;
        this.reason = reason;
        this.version = null;
        this.minVersion = null;
        this.maxVersion = null;
    }
    
    public IndexFormatTooOldException(final DataInput in, final String reason) {
        this(Objects.toString(in), reason);
    }
    
    public IndexFormatTooOldException(final String resourceDescription, final int version, final int minVersion, final int maxVersion) {
        super("Format version is not supported (resource " + resourceDescription + "): " + version + " (needs to be between " + minVersion + " and " + maxVersion + "). This version of Lucene only supports indexes created with release 4.0 and later.");
        this.resourceDescription = resourceDescription;
        this.version = version;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.reason = null;
    }
    
    public IndexFormatTooOldException(final DataInput in, final int version, final int minVersion, final int maxVersion) {
        this(Objects.toString(in), version, minVersion, maxVersion);
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public Integer getVersion() {
        return this.version;
    }
    
    public Integer getMaxVersion() {
        return this.maxVersion;
    }
    
    public Integer getMinVersion() {
        return this.minVersion;
    }
}
