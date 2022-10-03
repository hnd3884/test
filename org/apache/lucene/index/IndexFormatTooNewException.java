package org.apache.lucene.index;

import java.util.Objects;
import org.apache.lucene.store.DataInput;
import java.io.IOException;

public class IndexFormatTooNewException extends IOException
{
    private final String resourceDescription;
    private final int version;
    private final int minVersion;
    private final int maxVersion;
    
    public IndexFormatTooNewException(final String resourceDescription, final int version, final int minVersion, final int maxVersion) {
        super("Format version is not supported (resource " + resourceDescription + "): " + version + " (needs to be between " + minVersion + " and " + maxVersion + ")");
        this.resourceDescription = resourceDescription;
        this.version = version;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }
    
    public IndexFormatTooNewException(final DataInput in, final int version, final int minVersion, final int maxVersion) {
        this(Objects.toString(in), version, minVersion, maxVersion);
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getMaxVersion() {
        return this.maxVersion;
    }
    
    public int getMinVersion() {
        return this.minVersion;
    }
}
