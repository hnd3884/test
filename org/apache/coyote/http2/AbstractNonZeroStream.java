package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

abstract class AbstractNonZeroStream extends AbstractStream
{
    private static final Log log;
    private static final StringManager sm;
    protected static final ByteBuffer ZERO_LENGTH_BYTEBUFFER;
    protected final StreamStateMachine state;
    private volatile int weight;
    
    AbstractNonZeroStream(final String connectionId, final Integer identifier) {
        super(identifier);
        this.weight = 16;
        this.state = new StreamStateMachine(connectionId, this.getIdAsString());
    }
    
    AbstractNonZeroStream(final Integer identifier, final StreamStateMachine state) {
        super(identifier);
        this.weight = 16;
        this.state = state;
    }
    
    @Override
    final int getWeight() {
        return this.weight;
    }
    
    final void rePrioritise(final AbstractStream parent, final boolean exclusive, final int weight) {
        if (AbstractNonZeroStream.log.isDebugEnabled()) {
            AbstractNonZeroStream.log.debug((Object)AbstractNonZeroStream.sm.getString("stream.reprioritisation.debug", new Object[] { this.getConnectionId(), this.getIdAsString(), Boolean.toString(exclusive), parent.getIdAsString(), Integer.toString(weight) }));
        }
        if (this.isDescendant(parent)) {
            parent.detachFromParent();
            this.getParentStream().addChild((AbstractNonZeroStream)parent);
        }
        if (exclusive) {
            final Iterator<AbstractNonZeroStream> parentsChildren = parent.getChildStreams().iterator();
            while (parentsChildren.hasNext()) {
                final AbstractNonZeroStream parentsChild = parentsChildren.next();
                parentsChildren.remove();
                this.addChild(parentsChild);
            }
        }
        this.detachFromParent();
        parent.addChild(this);
        this.weight = weight;
    }
    
    final void rePrioritise(final AbstractStream parent, final int weight) {
        if (AbstractNonZeroStream.log.isDebugEnabled()) {
            AbstractNonZeroStream.log.debug((Object)AbstractNonZeroStream.sm.getString("stream.reprioritisation.debug", new Object[] { this.getConnectionId(), this.getIdAsString(), Boolean.FALSE, parent.getIdAsString(), Integer.toString(weight) }));
        }
        parent.addChild(this);
        this.weight = weight;
    }
    
    void replaceStream(final AbstractNonZeroStream replacement) {
        this.getParentStream().addChild(replacement);
        this.detachFromParent();
        for (final AbstractNonZeroStream child : this.getChildStreams()) {
            replacement.addChild(child);
        }
        this.getChildStreams().clear();
        replacement.weight = this.weight;
    }
    
    final boolean isClosedFinal() {
        return this.state.isClosedFinal();
    }
    
    final void checkState(final FrameType frameType) throws Http2Exception {
        this.state.checkFrameType(frameType);
    }
    
    abstract ByteBuffer getInputByteBuffer();
    
    abstract void receivedData(final int p0) throws Http2Exception;
    
    static {
        log = LogFactory.getLog((Class)AbstractNonZeroStream.class);
        sm = StringManager.getManager((Class)AbstractNonZeroStream.class);
        ZERO_LENGTH_BYTEBUFFER = ByteBuffer.allocate(0);
    }
}
