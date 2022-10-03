package jdk.jfr.internal.instrument;

import java.net.InetAddress;
import java.io.IOException;
import jdk.jfr.events.SocketWriteEvent;

@JIInstrumentationTarget("java.net.SocketOutputStream")
@JITypeMapping(from = "jdk.jfr.internal.instrument.SocketOutputStreamInstrumentor$AbstractPlainSocketImpl", to = "java.net.AbstractPlainSocketImpl")
final class SocketOutputStreamInstrumentor
{
    private AbstractPlainSocketImpl impl;
    
    private SocketOutputStreamInstrumentor() {
        this.impl = null;
    }
    
    @JIInstrumentationMethod
    private void socketWrite(final byte[] array, final int n, final int n2) throws IOException {
        final SocketWriteEvent socketWriteEvent = SocketWriteEvent.EVENT.get();
        if (!socketWriteEvent.isEnabled()) {
            this.socketWrite(array, n, n2);
            return;
        }
        int n3 = 0;
        try {
            socketWriteEvent.begin();
            this.socketWrite(array, n, n2);
            n3 = n2;
        }
        finally {
            socketWriteEvent.end();
            if (socketWriteEvent.shouldCommit()) {
                final String string = this.impl.address.toString();
                final int lastIndex = string.lastIndexOf(47);
                socketWriteEvent.host = string.substring(0, lastIndex);
                socketWriteEvent.address = string.substring(lastIndex + 1);
                socketWriteEvent.port = this.impl.port;
                socketWriteEvent.bytesWritten = ((n3 < 0) ? 0L : n3);
                socketWriteEvent.commit();
                socketWriteEvent.reset();
            }
        }
    }
    
    void silenceFindBugsUnwrittenField(final InetAddress address) {
        this.impl.address = address;
    }
    
    static class AbstractPlainSocketImpl
    {
        InetAddress address;
        int port;
    }
}
