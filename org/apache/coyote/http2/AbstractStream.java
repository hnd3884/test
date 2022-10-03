package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

abstract class AbstractStream
{
    private static final Log log;
    private static final StringManager sm;
    private final Integer identifier;
    private final String idAsString;
    private volatile AbstractStream parentStream;
    private final Set<AbstractNonZeroStream> childStreams;
    private long windowSize;
    private volatile int connectionAllocationRequested;
    private volatile int connectionAllocationMade;
    
    AbstractStream(final Integer identifier) {
        this.parentStream = null;
        this.childStreams = Collections.newSetFromMap(new ConcurrentHashMap<AbstractNonZeroStream, Boolean>());
        this.windowSize = 65535L;
        this.connectionAllocationRequested = 0;
        this.connectionAllocationMade = 0;
        this.identifier = identifier;
        this.idAsString = identifier.toString();
    }
    
    final Integer getIdentifier() {
        return this.identifier;
    }
    
    final String getIdAsString() {
        return this.idAsString;
    }
    
    final int getIdAsInt() {
        return this.identifier;
    }
    
    final void detachFromParent() {
        if (this.parentStream != null) {
            this.parentStream.getChildStreams().remove(this);
            this.parentStream = null;
        }
    }
    
    final void addChild(final AbstractNonZeroStream child) {
        child.setParentStream(this);
        this.childStreams.add(child);
    }
    
    final boolean isDescendant(final AbstractStream stream) {
        AbstractStream parent;
        for (parent = stream.getParentStream(); parent != null && parent != this; parent = parent.getParentStream()) {}
        return parent != null;
    }
    
    final AbstractStream getParentStream() {
        return this.parentStream;
    }
    
    final void setParentStream(final AbstractStream parentStream) {
        this.parentStream = parentStream;
    }
    
    final Set<AbstractNonZeroStream> getChildStreams() {
        return this.childStreams;
    }
    
    final synchronized void setWindowSize(final long windowSize) {
        this.windowSize = windowSize;
    }
    
    final synchronized long getWindowSize() {
        return this.windowSize;
    }
    
    synchronized void incrementWindowSize(final int increment) throws Http2Exception {
        this.windowSize += increment;
        if (AbstractStream.log.isDebugEnabled()) {
            AbstractStream.log.debug((Object)AbstractStream.sm.getString("abstractStream.windowSizeInc", new Object[] { this.getConnectionId(), this.getIdAsString(), Integer.toString(increment), Long.toString(this.windowSize) }));
        }
        if (this.windowSize <= 2147483647L) {
            return;
        }
        final String msg = AbstractStream.sm.getString("abstractStream.windowSizeTooBig", new Object[] { this.getConnectionId(), this.identifier, Integer.toString(increment), Long.toString(this.windowSize) });
        if (this.identifier == 0) {
            throw new ConnectionException(msg, Http2Error.FLOW_CONTROL_ERROR);
        }
        throw new StreamException(msg, Http2Error.FLOW_CONTROL_ERROR, this.identifier);
    }
    
    final synchronized void decrementWindowSize(final int decrement) {
        this.windowSize -= decrement;
        if (AbstractStream.log.isDebugEnabled()) {
            AbstractStream.log.debug((Object)AbstractStream.sm.getString("abstractStream.windowSizeDec", new Object[] { this.getConnectionId(), this.getIdAsString(), Integer.toString(decrement), Long.toString(this.windowSize) }));
        }
    }
    
    final int getConnectionAllocationRequested() {
        return this.connectionAllocationRequested;
    }
    
    final void setConnectionAllocationRequested(final int connectionAllocationRequested) {
        AbstractStream.log.debug((Object)AbstractStream.sm.getString("abstractStream.setConnectionAllocationRequested", new Object[] { this.getConnectionId(), this.getIdAsString(), Integer.toString(this.connectionAllocationRequested), Integer.toString(connectionAllocationRequested) }));
        this.connectionAllocationRequested = connectionAllocationRequested;
    }
    
    final int getConnectionAllocationMade() {
        return this.connectionAllocationMade;
    }
    
    final void setConnectionAllocationMade(final int connectionAllocationMade) {
        AbstractStream.log.debug((Object)AbstractStream.sm.getString("abstractStream.setConnectionAllocationMade", new Object[] { this.getConnectionId(), this.getIdAsString(), Integer.toString(this.connectionAllocationMade), Integer.toString(connectionAllocationMade) }));
        this.connectionAllocationMade = connectionAllocationMade;
    }
    
    abstract String getConnectionId();
    
    abstract int getWeight();
    
    static {
        log = LogFactory.getLog((Class)AbstractStream.class);
        sm = StringManager.getManager((Class)AbstractStream.class);
    }
}
