package io.netty.handler.pcap;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import io.netty.buffer.ByteBuf;
import java.io.OutputStream;
import io.netty.util.internal.logging.InternalLogger;
import java.io.Closeable;

final class PcapWriter implements Closeable
{
    private static final InternalLogger logger;
    private final OutputStream outputStream;
    private boolean isClosed;
    
    PcapWriter(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    PcapWriter(final OutputStream outputStream, final ByteBuf byteBuf) throws IOException {
        this.outputStream = outputStream;
        PcapHeaders.writeGlobalHeader(byteBuf);
        byteBuf.readBytes(outputStream, byteBuf.readableBytes());
    }
    
    void writePacket(final ByteBuf packetHeaderBuf, final ByteBuf packet) throws IOException {
        if (this.isClosed) {
            PcapWriter.logger.debug("Pcap Write attempted on closed PcapWriter");
        }
        final long timestamp = System.currentTimeMillis();
        PcapHeaders.writePacketHeader(packetHeaderBuf, (int)(timestamp / 1000L), (int)(timestamp % 1000L * 1000L), packet.readableBytes(), packet.readableBytes());
        packetHeaderBuf.readBytes(this.outputStream, packetHeaderBuf.readableBytes());
        packet.readBytes(this.outputStream, packet.readableBytes());
    }
    
    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            PcapWriter.logger.debug("PcapWriter is already closed");
        }
        else {
            this.isClosed = true;
            this.outputStream.flush();
            this.outputStream.close();
            PcapWriter.logger.debug("PcapWriter is now closed");
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PcapWriter.class);
    }
}
