package jdk.jfr.internal.instrument;

import jdk.jfr.events.SocketWriteEvent;
import java.io.IOException;
import jdk.jfr.events.SocketReadEvent;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;

@JIInstrumentationTarget("sun.nio.ch.SocketChannelImpl")
final class SocketChannelImplInstrumentor
{
    private InetSocketAddress remoteAddress;
    
    private SocketChannelImplInstrumentor() {
    }
    
    @JIInstrumentationMethod
    public int read(final ByteBuffer byteBuffer) throws IOException {
        final SocketReadEvent socketReadEvent = SocketReadEvent.EVENT.get();
        if (!socketReadEvent.isEnabled()) {
            return this.read(byteBuffer);
        }
        int read = 0;
        try {
            socketReadEvent.begin();
            read = this.read(byteBuffer);
        }
        finally {
            socketReadEvent.end();
            if (socketReadEvent.shouldCommit()) {
                final String string = this.remoteAddress.getAddress().toString();
                final int lastIndex = string.lastIndexOf(47);
                socketReadEvent.host = string.substring(0, lastIndex);
                socketReadEvent.address = string.substring(lastIndex + 1);
                socketReadEvent.port = this.remoteAddress.getPort();
                if (read < 0) {
                    socketReadEvent.endOfStream = true;
                }
                else {
                    socketReadEvent.bytesRead = read;
                }
                socketReadEvent.timeout = 0L;
                socketReadEvent.commit();
                socketReadEvent.reset();
            }
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public long read(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        final SocketReadEvent socketReadEvent = SocketReadEvent.EVENT.get();
        if (!socketReadEvent.isEnabled()) {
            return this.read(array, n, n2);
        }
        long read = 0L;
        try {
            socketReadEvent.begin();
            read = this.read(array, n, n2);
        }
        finally {
            socketReadEvent.end();
            if (socketReadEvent.shouldCommit()) {
                final String string = this.remoteAddress.getAddress().toString();
                final int lastIndex = string.lastIndexOf(47);
                socketReadEvent.host = string.substring(0, lastIndex);
                socketReadEvent.address = string.substring(lastIndex + 1);
                socketReadEvent.port = this.remoteAddress.getPort();
                if (read < 0L) {
                    socketReadEvent.endOfStream = true;
                }
                else {
                    socketReadEvent.bytesRead = read;
                }
                socketReadEvent.timeout = 0L;
                socketReadEvent.commit();
                socketReadEvent.reset();
            }
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public int write(final ByteBuffer byteBuffer) throws IOException {
        final SocketWriteEvent socketWriteEvent = SocketWriteEvent.EVENT.get();
        if (!socketWriteEvent.isEnabled()) {
            return this.write(byteBuffer);
        }
        int write = 0;
        try {
            socketWriteEvent.begin();
            write = this.write(byteBuffer);
        }
        finally {
            socketWriteEvent.end();
            if (socketWriteEvent.shouldCommit()) {
                final String string = this.remoteAddress.getAddress().toString();
                final int lastIndex = string.lastIndexOf(47);
                socketWriteEvent.host = string.substring(0, lastIndex);
                socketWriteEvent.address = string.substring(lastIndex + 1);
                socketWriteEvent.port = this.remoteAddress.getPort();
                socketWriteEvent.bytesWritten = ((write < 0) ? 0L : write);
                socketWriteEvent.commit();
                socketWriteEvent.reset();
            }
        }
        return write;
    }
    
    @JIInstrumentationMethod
    public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        final SocketWriteEvent socketWriteEvent = SocketWriteEvent.EVENT.get();
        if (!socketWriteEvent.isEnabled()) {
            return this.write(array, n, n2);
        }
        long write = 0L;
        try {
            socketWriteEvent.begin();
            write = this.write(array, n, n2);
        }
        finally {
            socketWriteEvent.end();
            if (socketWriteEvent.shouldCommit()) {
                final String string = this.remoteAddress.getAddress().toString();
                final int lastIndex = string.lastIndexOf(47);
                socketWriteEvent.host = string.substring(0, lastIndex);
                socketWriteEvent.address = string.substring(lastIndex + 1);
                socketWriteEvent.port = this.remoteAddress.getPort();
                socketWriteEvent.bytesWritten = ((write < 0L) ? 0L : write);
                socketWriteEvent.commit();
                socketWriteEvent.reset();
            }
        }
        return write;
    }
}
