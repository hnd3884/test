package org.apache.xerces.stax;

import javax.xml.stream.Location;

public class ImmutableLocation implements Location
{
    private final int fCharacterOffset;
    private final int fColumnNumber;
    private final int fLineNumber;
    private final String fPublicId;
    private final String fSystemId;
    
    public ImmutableLocation(final Location location) {
        this(location.getCharacterOffset(), location.getColumnNumber(), location.getLineNumber(), location.getPublicId(), location.getSystemId());
    }
    
    public ImmutableLocation(final int fCharacterOffset, final int fColumnNumber, final int fLineNumber, final String fPublicId, final String fSystemId) {
        this.fCharacterOffset = fCharacterOffset;
        this.fColumnNumber = fColumnNumber;
        this.fLineNumber = fLineNumber;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
    }
    
    public int getCharacterOffset() {
        return this.fCharacterOffset;
    }
    
    public int getColumnNumber() {
        return this.fColumnNumber;
    }
    
    public int getLineNumber() {
        return this.fLineNumber;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
}
