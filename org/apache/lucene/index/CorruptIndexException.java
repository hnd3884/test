package org.apache.lucene.index;

import java.util.Objects;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.DataInput;
import java.io.IOException;

public class CorruptIndexException extends IOException
{
    private final String message;
    private final String resourceDescription;
    
    public CorruptIndexException(final String message, final DataInput input) {
        this(message, input, null);
    }
    
    public CorruptIndexException(final String message, final DataOutput output) {
        this(message, output, null);
    }
    
    public CorruptIndexException(final String message, final DataInput input, final Throwable cause) {
        this(message, Objects.toString(input), cause);
    }
    
    public CorruptIndexException(final String message, final DataOutput output, final Throwable cause) {
        this(message, Objects.toString(output), cause);
    }
    
    public CorruptIndexException(final String message, final String resourceDescription) {
        this(message, resourceDescription, null);
    }
    
    public CorruptIndexException(final String message, final String resourceDescription, final Throwable cause) {
        super(Objects.toString(message) + " (resource=" + resourceDescription + ")", cause);
        this.resourceDescription = resourceDescription;
        this.message = message;
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public String getOriginalMessage() {
        return this.message;
    }
}
