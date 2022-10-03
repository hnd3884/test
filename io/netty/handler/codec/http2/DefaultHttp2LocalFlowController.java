package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;

public class DefaultHttp2LocalFlowController implements Http2LocalFlowController
{
    public static final float DEFAULT_WINDOW_UPDATE_RATIO = 0.5f;
    private final Http2Connection connection;
    private final Http2Connection.PropertyKey stateKey;
    private Http2FrameWriter frameWriter;
    private ChannelHandlerContext ctx;
    private float windowUpdateRatio;
    private int initialWindowSize;
    private static final FlowState REDUCED_FLOW_STATE;
    
    public DefaultHttp2LocalFlowController(final Http2Connection connection) {
        this(connection, 0.5f, false);
    }
    
    public DefaultHttp2LocalFlowController(final Http2Connection connection, final float windowUpdateRatio, final boolean autoRefillConnectionWindow) {
        this.initialWindowSize = 65535;
        this.connection = ObjectUtil.checkNotNull(connection, "connection");
        this.windowUpdateRatio(windowUpdateRatio);
        this.stateKey = connection.newKey();
        final FlowState connectionState = autoRefillConnectionWindow ? new AutoRefillState(connection.connectionStream(), this.initialWindowSize) : new DefaultState(connection.connectionStream(), this.initialWindowSize);
        connection.connectionStream().setProperty(this.stateKey, connectionState);
        connection.addListener(new Http2ConnectionAdapter() {
            @Override
            public void onStreamAdded(final Http2Stream stream) {
                stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, DefaultHttp2LocalFlowController.REDUCED_FLOW_STATE);
            }
            
            @Override
            public void onStreamActive(final Http2Stream stream) {
                stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, new DefaultState(stream, DefaultHttp2LocalFlowController.this.initialWindowSize));
            }
            
            @Override
            public void onStreamClosed(final Http2Stream stream) {
                try {
                    final FlowState state = DefaultHttp2LocalFlowController.this.state(stream);
                    final int unconsumedBytes = state.unconsumedBytes();
                    if (DefaultHttp2LocalFlowController.this.ctx != null && unconsumedBytes > 0 && DefaultHttp2LocalFlowController.this.consumeAllBytes(state, unconsumedBytes)) {
                        DefaultHttp2LocalFlowController.this.ctx.flush();
                    }
                }
                catch (final Http2Exception e) {
                    PlatformDependent.throwException(e);
                }
                finally {
                    stream.setProperty(DefaultHttp2LocalFlowController.this.stateKey, DefaultHttp2LocalFlowController.REDUCED_FLOW_STATE);
                }
            }
        });
    }
    
    @Override
    public DefaultHttp2LocalFlowController frameWriter(final Http2FrameWriter frameWriter) {
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        return this;
    }
    
    @Override
    public void channelHandlerContext(final ChannelHandlerContext ctx) {
        this.ctx = ObjectUtil.checkNotNull(ctx, "ctx");
    }
    
    @Override
    public void initialWindowSize(final int newWindowSize) throws Http2Exception {
        assert !(!this.ctx.executor().inEventLoop());
        final int delta = newWindowSize - this.initialWindowSize;
        this.initialWindowSize = newWindowSize;
        final WindowUpdateVisitor visitor = new WindowUpdateVisitor(delta);
        this.connection.forEachActiveStream(visitor);
        visitor.throwIfError();
    }
    
    @Override
    public int initialWindowSize() {
        return this.initialWindowSize;
    }
    
    @Override
    public int windowSize(final Http2Stream stream) {
        return this.state(stream).windowSize();
    }
    
    @Override
    public int initialWindowSize(final Http2Stream stream) {
        return this.state(stream).initialWindowSize();
    }
    
    @Override
    public void incrementWindowSize(final Http2Stream stream, final int delta) throws Http2Exception {
        assert this.ctx != null && this.ctx.executor().inEventLoop();
        final FlowState state = this.state(stream);
        state.incrementInitialStreamWindow(delta);
        state.writeWindowUpdateIfNeeded();
    }
    
    @Override
    public boolean consumeBytes(final Http2Stream stream, final int numBytes) throws Http2Exception {
        assert this.ctx != null && this.ctx.executor().inEventLoop();
        ObjectUtil.checkPositiveOrZero(numBytes, "numBytes");
        if (numBytes == 0) {
            return false;
        }
        if (stream == null || isClosed(stream)) {
            return false;
        }
        if (stream.id() == 0) {
            throw new UnsupportedOperationException("Returning bytes for the connection window is not supported");
        }
        return this.consumeAllBytes(this.state(stream), numBytes);
    }
    
    private boolean consumeAllBytes(final FlowState state, final int numBytes) throws Http2Exception {
        return this.connectionState().consumeBytes(numBytes) | state.consumeBytes(numBytes);
    }
    
    @Override
    public int unconsumedBytes(final Http2Stream stream) {
        return this.state(stream).unconsumedBytes();
    }
    
    private static void checkValidRatio(final float ratio) {
        if (Double.compare(ratio, 0.0) <= 0 || Double.compare(ratio, 1.0) >= 0) {
            throw new IllegalArgumentException("Invalid ratio: " + ratio);
        }
    }
    
    public void windowUpdateRatio(final float ratio) {
        assert !(!this.ctx.executor().inEventLoop());
        checkValidRatio(ratio);
        this.windowUpdateRatio = ratio;
    }
    
    public float windowUpdateRatio() {
        return this.windowUpdateRatio;
    }
    
    public void windowUpdateRatio(final Http2Stream stream, final float ratio) throws Http2Exception {
        assert this.ctx != null && this.ctx.executor().inEventLoop();
        checkValidRatio(ratio);
        final FlowState state = this.state(stream);
        state.windowUpdateRatio(ratio);
        state.writeWindowUpdateIfNeeded();
    }
    
    public float windowUpdateRatio(final Http2Stream stream) throws Http2Exception {
        return this.state(stream).windowUpdateRatio();
    }
    
    @Override
    public void receiveFlowControlledFrame(final Http2Stream stream, final ByteBuf data, final int padding, final boolean endOfStream) throws Http2Exception {
        assert this.ctx != null && this.ctx.executor().inEventLoop();
        final int dataLength = data.readableBytes() + padding;
        final FlowState connectionState = this.connectionState();
        connectionState.receiveFlowControlledFrame(dataLength);
        if (stream != null && !isClosed(stream)) {
            final FlowState state = this.state(stream);
            state.endOfStream(endOfStream);
            state.receiveFlowControlledFrame(dataLength);
        }
        else if (dataLength > 0) {
            connectionState.consumeBytes(dataLength);
        }
    }
    
    private FlowState connectionState() {
        return this.connection.connectionStream().getProperty(this.stateKey);
    }
    
    private FlowState state(final Http2Stream stream) {
        return stream.getProperty(this.stateKey);
    }
    
    private static boolean isClosed(final Http2Stream stream) {
        return stream.state() == Http2Stream.State.CLOSED;
    }
    
    static {
        REDUCED_FLOW_STATE = new FlowState() {
            @Override
            public int windowSize() {
                return 0;
            }
            
            @Override
            public int initialWindowSize() {
                return 0;
            }
            
            @Override
            public void window(final int initialWindowSize) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void incrementInitialStreamWindow(final int delta) {
            }
            
            @Override
            public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean consumeBytes(final int numBytes) throws Http2Exception {
                return false;
            }
            
            @Override
            public int unconsumedBytes() {
                return 0;
            }
            
            @Override
            public float windowUpdateRatio() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void windowUpdateRatio(final float ratio) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void receiveFlowControlledFrame(final int dataLength) throws Http2Exception {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void incrementFlowControlWindows(final int delta) throws Http2Exception {
            }
            
            @Override
            public void endOfStream(final boolean endOfStream) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private final class AutoRefillState extends DefaultState
    {
        AutoRefillState(final Http2Stream stream, final int initialWindowSize) {
            super(stream, initialWindowSize);
        }
        
        @Override
        public void receiveFlowControlledFrame(final int dataLength) throws Http2Exception {
            super.receiveFlowControlledFrame(dataLength);
            super.consumeBytes(dataLength);
        }
        
        @Override
        public boolean consumeBytes(final int numBytes) throws Http2Exception {
            return false;
        }
    }
    
    private class DefaultState implements FlowState
    {
        private final Http2Stream stream;
        private int window;
        private int processedWindow;
        private int initialStreamWindowSize;
        private float streamWindowUpdateRatio;
        private int lowerBound;
        private boolean endOfStream;
        
        DefaultState(final Http2Stream stream, final int initialWindowSize) {
            this.stream = stream;
            this.window(initialWindowSize);
            this.streamWindowUpdateRatio = DefaultHttp2LocalFlowController.this.windowUpdateRatio;
        }
        
        @Override
        public void window(final int initialWindowSize) {
            assert !(!DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop());
            this.initialStreamWindowSize = initialWindowSize;
            this.processedWindow = initialWindowSize;
            this.window = initialWindowSize;
        }
        
        @Override
        public int windowSize() {
            return this.window;
        }
        
        @Override
        public int initialWindowSize() {
            return this.initialStreamWindowSize;
        }
        
        @Override
        public void endOfStream(final boolean endOfStream) {
            this.endOfStream = endOfStream;
        }
        
        @Override
        public float windowUpdateRatio() {
            return this.streamWindowUpdateRatio;
        }
        
        @Override
        public void windowUpdateRatio(final float ratio) {
            assert !(!DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop());
            this.streamWindowUpdateRatio = ratio;
        }
        
        @Override
        public void incrementInitialStreamWindow(int delta) {
            final int newValue = (int)Math.min(2147483647L, Math.max(0L, this.initialStreamWindowSize + (long)delta));
            delta = newValue - this.initialStreamWindowSize;
            this.initialStreamWindowSize += delta;
        }
        
        @Override
        public void incrementFlowControlWindows(final int delta) throws Http2Exception {
            if (delta > 0 && this.window > Integer.MAX_VALUE - delta) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window overflowed for stream: %d", this.stream.id());
            }
            this.window += delta;
            this.processedWindow += delta;
            this.lowerBound = Math.min(delta, 0);
        }
        
        @Override
        public void receiveFlowControlledFrame(final int dataLength) throws Http2Exception {
            assert dataLength >= 0;
            this.window -= dataLength;
            if (this.window < this.lowerBound) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window exceeded for stream: %d", this.stream.id());
            }
        }
        
        private void returnProcessedBytes(final int delta) throws Http2Exception {
            if (this.processedWindow - delta < this.window) {
                throw Http2Exception.streamError(this.stream.id(), Http2Error.INTERNAL_ERROR, "Attempting to return too many bytes for stream %d", this.stream.id());
            }
            this.processedWindow -= delta;
        }
        
        @Override
        public boolean consumeBytes(final int numBytes) throws Http2Exception {
            this.returnProcessedBytes(numBytes);
            return this.writeWindowUpdateIfNeeded();
        }
        
        @Override
        public int unconsumedBytes() {
            return this.processedWindow - this.window;
        }
        
        @Override
        public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
            if (this.endOfStream || this.initialStreamWindowSize <= 0 || isClosed(this.stream)) {
                return false;
            }
            final int threshold = (int)(this.initialStreamWindowSize * this.streamWindowUpdateRatio);
            if (this.processedWindow <= threshold) {
                this.writeWindowUpdate();
                return true;
            }
            return false;
        }
        
        private void writeWindowUpdate() throws Http2Exception {
            final int deltaWindowSize = this.initialStreamWindowSize - this.processedWindow;
            try {
                this.incrementFlowControlWindows(deltaWindowSize);
            }
            catch (final Throwable t) {
                throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, t, "Attempting to return too many bytes for stream %d", this.stream.id());
            }
            DefaultHttp2LocalFlowController.this.frameWriter.writeWindowUpdate(DefaultHttp2LocalFlowController.this.ctx, this.stream.id(), deltaWindowSize, DefaultHttp2LocalFlowController.this.ctx.newPromise());
        }
    }
    
    private final class WindowUpdateVisitor implements Http2StreamVisitor
    {
        private Http2Exception.CompositeStreamException compositeException;
        private final int delta;
        
        WindowUpdateVisitor(final int delta) {
            this.delta = delta;
        }
        
        @Override
        public boolean visit(final Http2Stream stream) throws Http2Exception {
            try {
                final FlowState state = DefaultHttp2LocalFlowController.this.state(stream);
                state.incrementFlowControlWindows(this.delta);
                state.incrementInitialStreamWindow(this.delta);
            }
            catch (final Http2Exception.StreamException e) {
                if (this.compositeException == null) {
                    this.compositeException = new Http2Exception.CompositeStreamException(e.error(), 4);
                }
                this.compositeException.add(e);
            }
            return true;
        }
        
        public void throwIfError() throws Http2Exception.CompositeStreamException {
            if (this.compositeException != null) {
                throw this.compositeException;
            }
        }
    }
    
    private interface FlowState
    {
        int windowSize();
        
        int initialWindowSize();
        
        void window(final int p0);
        
        void incrementInitialStreamWindow(final int p0);
        
        boolean writeWindowUpdateIfNeeded() throws Http2Exception;
        
        boolean consumeBytes(final int p0) throws Http2Exception;
        
        int unconsumedBytes();
        
        float windowUpdateRatio();
        
        void windowUpdateRatio(final float p0);
        
        void receiveFlowControlledFrame(final int p0) throws Http2Exception;
        
        void incrementFlowControlWindows(final int p0) throws Http2Exception;
        
        void endOfStream(final boolean p0);
    }
}
