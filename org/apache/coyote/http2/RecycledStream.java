package org.apache.coyote.http2;

import java.nio.ByteBuffer;

class RecycledStream extends AbstractNonZeroStream
{
    private final String connectionId;
    private int remainingFlowControlWindow;
    
    RecycledStream(final String connectionId, final Integer identifier, final StreamStateMachine state, final int remainingFlowControlWindow) {
        super(identifier, state);
        this.connectionId = connectionId;
        this.remainingFlowControlWindow = remainingFlowControlWindow;
    }
    
    @Override
    String getConnectionId() {
        return this.connectionId;
    }
    
    @Override
    void incrementWindowSize(final int increment) throws Http2Exception {
    }
    
    @Override
    void receivedData(final int payloadSize) throws ConnectionException {
        this.remainingFlowControlWindow -= payloadSize;
    }
    
    @Override
    ByteBuffer getInputByteBuffer() {
        if (this.remainingFlowControlWindow < 0) {
            return RecycledStream.ZERO_LENGTH_BYTEBUFFER;
        }
        return null;
    }
}
