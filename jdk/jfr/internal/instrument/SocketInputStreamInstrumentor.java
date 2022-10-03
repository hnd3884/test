package jdk.jfr.internal.instrument;

import java.net.InetAddress;
import java.io.IOException;
import jdk.jfr.events.SocketReadEvent;

@JIInstrumentationTarget("java.net.SocketInputStream")
@JITypeMapping(from = "jdk.jfr.internal.instrument.SocketInputStreamInstrumentor$AbstractPlainSocketImpl", to = "java.net.AbstractPlainSocketImpl")
final class SocketInputStreamInstrumentor
{
    private AbstractPlainSocketImpl impl;
    
    private SocketInputStreamInstrumentor() {
        this.impl = null;
    }
    
    @JIInstrumentationMethod
    int read(final byte[] array, final int n, final int n2, final int n3) throws IOException {
        final SocketReadEvent socketReadEvent = SocketReadEvent.EVENT.get();
        if (!socketReadEvent.isEnabled()) {
            return this.read(array, n, n2, n3);
        }
        int read = 0;
        try {
            socketReadEvent.begin();
            read = this.read(array, n, n2, n3);
        }
        finally {
            socketReadEvent.end();
            if (socketReadEvent.shouldCommit()) {
                final String string = this.impl.address.toString();
                final int lastIndex = string.lastIndexOf(47);
                socketReadEvent.host = string.substring(0, lastIndex);
                socketReadEvent.address = string.substring(lastIndex + 1);
                socketReadEvent.port = this.impl.port;
                if (read < 0) {
                    socketReadEvent.endOfStream = true;
                }
                else {
                    socketReadEvent.bytesRead = read;
                }
                socketReadEvent.timeout = n3;
                socketReadEvent.commit();
                socketReadEvent.reset();
            }
        }
        return read;
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
