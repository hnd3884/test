package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import java.nio.ByteBuffer;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class BufferManagerReadGrow implements BufferManagerRead, MarkAndResetHandler
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private Object streamMemento;
    private RestorableInputStream inputStream;
    private boolean markEngaged;
    
    BufferManagerReadGrow(final ORB orb) {
        this.markEngaged = false;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
    }
    
    @Override
    public void processFragment(final ByteBuffer byteBuffer, final FragmentMessage fragmentMessage) {
    }
    
    @Override
    public void init(final Message message) {
    }
    
    @Override
    public ByteBufferWithInfo underflow(final ByteBufferWithInfo byteBufferWithInfo) {
        throw this.wrapper.unexpectedEof();
    }
    
    @Override
    public void cancelProcessing(final int n) {
    }
    
    @Override
    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }
    
    @Override
    public void mark(final RestorableInputStream inputStream) {
        this.markEngaged = true;
        this.inputStream = inputStream;
        this.streamMemento = this.inputStream.createStreamMemento();
    }
    
    @Override
    public void fragmentationOccured(final ByteBufferWithInfo byteBufferWithInfo) {
    }
    
    @Override
    public void reset() {
        if (!this.markEngaged) {
            return;
        }
        this.markEngaged = false;
        this.inputStream.restoreInternalState(this.streamMemento);
        this.streamMemento = null;
    }
    
    @Override
    public void close(final ByteBufferWithInfo byteBufferWithInfo) {
    }
}
