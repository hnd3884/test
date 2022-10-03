package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class Association
{
    private final int associationID;
    private final int maxInStreams;
    private final int maxOutStreams;
    
    protected Association(final int associationID, final int maxInStreams, final int maxOutStreams) {
        this.associationID = associationID;
        this.maxInStreams = maxInStreams;
        this.maxOutStreams = maxOutStreams;
    }
    
    public final int associationID() {
        return this.associationID;
    }
    
    public final int maxInboundStreams() {
        return this.maxInStreams;
    }
    
    public final int maxOutboundStreams() {
        return this.maxOutStreams;
    }
}
