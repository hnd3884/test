package org.apache.axiom.attachments;

import org.apache.axiom.om.OMException;

public abstract class IncomingAttachmentStreams
{
    protected boolean _readyToGetNextStream;
    
    public IncomingAttachmentStreams() {
        this._readyToGetNextStream = true;
    }
    
    public final boolean isReadyToGetNextStream() {
        return this._readyToGetNextStream;
    }
    
    protected final void setReadyToGetNextStream(final boolean ready) {
        this._readyToGetNextStream = ready;
    }
    
    public abstract IncomingAttachmentInputStream getNextStream() throws OMException;
}
