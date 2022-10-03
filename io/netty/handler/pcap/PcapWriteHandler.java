package io.netty.handler.pcap;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import java.net.Inet6Address;
import io.netty.util.NetUtil;
import java.net.Inet4Address;
import io.netty.channel.ChannelPromise;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import java.io.IOException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import io.netty.util.internal.logging.InternalLogger;
import java.io.Closeable;
import io.netty.channel.ChannelDuplexHandler;

public final class PcapWriteHandler extends ChannelDuplexHandler implements Closeable
{
    private final InternalLogger logger;
    private PcapWriter pCapWriter;
    private final OutputStream outputStream;
    private final boolean captureZeroByte;
    private final boolean writePcapGlobalHeader;
    private int sendSegmentNumber;
    private int receiveSegmentNumber;
    private InetSocketAddress srcAddr;
    private InetSocketAddress dstAddr;
    private boolean isClosed;
    
    public PcapWriteHandler(final OutputStream outputStream) {
        this(outputStream, false, true);
    }
    
    public PcapWriteHandler(final OutputStream outputStream, final boolean captureZeroByte, final boolean writePcapGlobalHeader) {
        this.logger = InternalLoggerFactory.getInstance(PcapWriteHandler.class);
        this.sendSegmentNumber = 1;
        this.receiveSegmentNumber = 1;
        this.outputStream = ObjectUtil.checkNotNull(outputStream, "OutputStream");
        this.captureZeroByte = captureZeroByte;
        this.writePcapGlobalHeader = writePcapGlobalHeader;
    }
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final ByteBufAllocator byteBufAllocator = ctx.alloc();
        if (this.writePcapGlobalHeader) {
            final ByteBuf byteBuf = byteBufAllocator.buffer();
            try {
                this.pCapWriter = new PcapWriter(this.outputStream, byteBuf);
            }
            catch (final IOException ex) {
                ctx.channel().close();
                ctx.fireExceptionCaught((Throwable)ex);
                this.logger.error("Caught Exception While Initializing PcapWriter, Closing Channel.", ex);
            }
            finally {
                byteBuf.release();
            }
        }
        else {
            this.pCapWriter = new PcapWriter(this.outputStream);
        }
        if (ctx.channel() instanceof SocketChannel) {
            if (ctx.channel().parent() instanceof ServerSocketChannel) {
                this.srcAddr = (InetSocketAddress)ctx.channel().remoteAddress();
                this.dstAddr = (InetSocketAddress)ctx.channel().localAddress();
            }
            else {
                this.srcAddr = (InetSocketAddress)ctx.channel().localAddress();
                this.dstAddr = (InetSocketAddress)ctx.channel().remoteAddress();
            }
            this.logger.debug("Initiating Fake TCP 3-Way Handshake");
            final ByteBuf tcpBuf = byteBufAllocator.buffer();
            try {
                TCPPacket.writePacket(tcpBuf, null, 0, 0, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.SYN);
                this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
                TCPPacket.writePacket(tcpBuf, null, 0, 1, this.dstAddr.getPort(), this.srcAddr.getPort(), TCPPacket.TCPFlag.SYN, TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.dstAddr, this.srcAddr, tcpBuf, byteBufAllocator, ctx);
                TCPPacket.writePacket(tcpBuf, null, 1, 1, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
            }
            finally {
                tcpBuf.release();
            }
            this.logger.debug("Finished Fake TCP 3-Way Handshake");
        }
        else if (ctx.channel() instanceof DatagramChannel) {
            final DatagramChannel datagramChannel = (DatagramChannel)ctx.channel();
            if (datagramChannel.isConnected()) {
                this.srcAddr = (InetSocketAddress)ctx.channel().localAddress();
                this.dstAddr = (InetSocketAddress)ctx.channel().remoteAddress();
            }
        }
        super.channelActive(ctx);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!this.isClosed) {
            if (ctx.channel() instanceof SocketChannel) {
                this.handleTCP(ctx, msg, false);
            }
            else if (ctx.channel() instanceof DatagramChannel) {
                this.handleUDP(ctx, msg);
            }
            else {
                this.logger.debug("Discarding Pcap Write for Unknown Channel Type: {}", ctx.channel());
            }
        }
        super.channelRead(ctx, msg);
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (!this.isClosed) {
            if (ctx.channel() instanceof SocketChannel) {
                this.handleTCP(ctx, msg, true);
            }
            else if (ctx.channel() instanceof DatagramChannel) {
                this.handleUDP(ctx, msg);
            }
            else {
                this.logger.debug("Discarding Pcap Write for Unknown Channel Type: {}", ctx.channel());
            }
        }
        super.write(ctx, msg, promise);
    }
    
    private void handleTCP(final ChannelHandlerContext ctx, final Object msg, final boolean isWriteOperation) {
        if (msg instanceof ByteBuf) {
            if (((ByteBuf)msg).readableBytes() == 0 && !this.captureZeroByte) {
                this.logger.debug("Discarding Zero Byte TCP Packet. isWriteOperation {}", (Object)isWriteOperation);
                return;
            }
            final ByteBufAllocator byteBufAllocator = ctx.alloc();
            final ByteBuf packet = ((ByteBuf)msg).duplicate();
            final ByteBuf tcpBuf = byteBufAllocator.buffer();
            final int bytes = packet.readableBytes();
            try {
                if (isWriteOperation) {
                    TCPPacket.writePacket(tcpBuf, packet, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.ACK);
                    this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
                    this.logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr, this.dstAddr, false);
                    this.sendSegmentNumber += bytes;
                    TCPPacket.writePacket(tcpBuf, null, this.receiveSegmentNumber, this.sendSegmentNumber, this.dstAddr.getPort(), this.srcAddr.getPort(), TCPPacket.TCPFlag.ACK);
                    this.completeTCPWrite(this.dstAddr, this.srcAddr, tcpBuf, byteBufAllocator, ctx);
                    this.logTCP(true, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, this.dstAddr, this.srcAddr, true);
                }
                else {
                    TCPPacket.writePacket(tcpBuf, packet, this.receiveSegmentNumber, this.sendSegmentNumber, this.dstAddr.getPort(), this.srcAddr.getPort(), TCPPacket.TCPFlag.ACK);
                    this.completeTCPWrite(this.dstAddr, this.srcAddr, tcpBuf, byteBufAllocator, ctx);
                    this.logTCP(false, bytes, this.receiveSegmentNumber, this.sendSegmentNumber, this.dstAddr, this.srcAddr, false);
                    this.receiveSegmentNumber += bytes;
                    TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.ACK);
                    this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
                    this.logTCP(false, bytes, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr, this.dstAddr, true);
                }
            }
            finally {
                tcpBuf.release();
            }
        }
        else {
            this.logger.debug("Discarding Pcap Write for TCP Object: {}", msg);
        }
    }
    
    private void completeTCPWrite(final InetSocketAddress srcAddr, final InetSocketAddress dstAddr, final ByteBuf tcpBuf, final ByteBufAllocator byteBufAllocator, final ChannelHandlerContext ctx) {
        final ByteBuf ipBuf = byteBufAllocator.buffer();
        final ByteBuf ethernetBuf = byteBufAllocator.buffer();
        final ByteBuf pcap = byteBufAllocator.buffer();
        try {
            if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
                IPPacket.writeTCPv4(ipBuf, tcpBuf, NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress()));
                EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
            }
            else {
                if (!(srcAddr.getAddress() instanceof Inet6Address) || !(dstAddr.getAddress() instanceof Inet6Address)) {
                    this.logger.error("Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}", srcAddr.getAddress(), dstAddr.getAddress());
                    return;
                }
                IPPacket.writeTCPv6(ipBuf, tcpBuf, srcAddr.getAddress().getAddress(), dstAddr.getAddress().getAddress());
                EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
            }
            this.pCapWriter.writePacket(pcap, ethernetBuf);
        }
        catch (final IOException ex) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", ex);
            ctx.fireExceptionCaught((Throwable)ex);
        }
        finally {
            ipBuf.release();
            ethernetBuf.release();
            pcap.release();
        }
    }
    
    private void logTCP(final boolean isWriteOperation, final int bytes, final int sendSegmentNumber, final int receiveSegmentNumber, final InetSocketAddress srcAddr, final InetSocketAddress dstAddr, final boolean ackOnly) {
        if (this.logger.isDebugEnabled()) {
            if (ackOnly) {
                this.logger.debug("Writing TCP ACK, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}", isWriteOperation, sendSegmentNumber, receiveSegmentNumber, dstAddr, srcAddr);
            }
            else {
                this.logger.debug("Writing TCP Data of {} Bytes, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}", bytes, isWriteOperation, sendSegmentNumber, receiveSegmentNumber, srcAddr, dstAddr);
            }
        }
    }
    
    private void handleUDP(final ChannelHandlerContext ctx, final Object msg) {
        final ByteBuf udpBuf = ctx.alloc().buffer();
        try {
            if (msg instanceof DatagramPacket) {
                if (((DefaultAddressedEnvelope<ByteBuf, A>)msg).content().readableBytes() == 0 && !this.captureZeroByte) {
                    this.logger.debug("Discarding Zero Byte UDP Packet");
                    return;
                }
                final DatagramPacket datagramPacket = ((DatagramPacket)msg).duplicate();
                InetSocketAddress srcAddr = ((DefaultAddressedEnvelope<M, InetSocketAddress>)datagramPacket).sender();
                final InetSocketAddress dstAddr = ((DefaultAddressedEnvelope<M, InetSocketAddress>)datagramPacket).recipient();
                if (srcAddr == null) {
                    srcAddr = (InetSocketAddress)ctx.channel().localAddress();
                }
                this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", ((DefaultAddressedEnvelope<ByteBuf, A>)datagramPacket).content().readableBytes(), srcAddr, dstAddr);
                UDPPacket.writePacket(udpBuf, ((DefaultAddressedEnvelope<ByteBuf, A>)datagramPacket).content(), srcAddr.getPort(), dstAddr.getPort());
                this.completeUDPWrite(srcAddr, dstAddr, udpBuf, ctx.alloc(), ctx);
            }
            else if (msg instanceof ByteBuf && ((DatagramChannel)ctx.channel()).isConnected()) {
                if (((ByteBuf)msg).readableBytes() == 0 && !this.captureZeroByte) {
                    this.logger.debug("Discarding Zero Byte UDP Packet");
                    return;
                }
                final ByteBuf byteBuf = ((ByteBuf)msg).duplicate();
                this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", byteBuf.readableBytes(), this.srcAddr, this.dstAddr);
                UDPPacket.writePacket(udpBuf, byteBuf, this.srcAddr.getPort(), this.dstAddr.getPort());
                this.completeUDPWrite(this.srcAddr, this.dstAddr, udpBuf, ctx.alloc(), ctx);
            }
            else {
                this.logger.debug("Discarding Pcap Write for UDP Object: {}", msg);
            }
        }
        finally {
            udpBuf.release();
        }
    }
    
    private void completeUDPWrite(final InetSocketAddress srcAddr, final InetSocketAddress dstAddr, final ByteBuf udpBuf, final ByteBufAllocator byteBufAllocator, final ChannelHandlerContext ctx) {
        final ByteBuf ipBuf = byteBufAllocator.buffer();
        final ByteBuf ethernetBuf = byteBufAllocator.buffer();
        final ByteBuf pcap = byteBufAllocator.buffer();
        try {
            if (srcAddr.getAddress() instanceof Inet4Address && dstAddr.getAddress() instanceof Inet4Address) {
                IPPacket.writeUDPv4(ipBuf, udpBuf, NetUtil.ipv4AddressToInt((Inet4Address)srcAddr.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)dstAddr.getAddress()));
                EthernetPacket.writeIPv4(ethernetBuf, ipBuf);
            }
            else {
                if (!(srcAddr.getAddress() instanceof Inet6Address) || !(dstAddr.getAddress() instanceof Inet6Address)) {
                    this.logger.error("Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}", srcAddr.getAddress(), dstAddr.getAddress());
                    return;
                }
                IPPacket.writeUDPv6(ipBuf, udpBuf, srcAddr.getAddress().getAddress(), dstAddr.getAddress().getAddress());
                EthernetPacket.writeIPv6(ethernetBuf, ipBuf);
            }
            this.pCapWriter.writePacket(pcap, ethernetBuf);
        }
        catch (final IOException ex) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", ex);
            ctx.fireExceptionCaught((Throwable)ex);
        }
        finally {
            ipBuf.release();
            ethernetBuf.release();
            pcap.release();
        }
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel() instanceof SocketChannel) {
            this.logger.debug("Starting Fake TCP FIN+ACK Flow to close connection");
            final ByteBufAllocator byteBufAllocator = ctx.alloc();
            final ByteBuf tcpBuf = byteBufAllocator.buffer();
            try {
                TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.FIN, TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
                TCPPacket.writePacket(tcpBuf, null, this.receiveSegmentNumber, this.sendSegmentNumber, this.dstAddr.getPort(), this.srcAddr.getPort(), TCPPacket.TCPFlag.FIN, TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.dstAddr, this.srcAddr, tcpBuf, byteBufAllocator, ctx);
                TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber + 1, this.receiveSegmentNumber + 1, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, byteBufAllocator, ctx);
            }
            finally {
                tcpBuf.release();
            }
            this.logger.debug("Finished Fake TCP FIN+ACK Flow to close connection");
        }
        this.close();
        super.handlerRemoved(ctx);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (ctx.channel() instanceof SocketChannel) {
            final ByteBuf tcpBuf = ctx.alloc().buffer();
            try {
                TCPPacket.writePacket(tcpBuf, null, this.sendSegmentNumber, this.receiveSegmentNumber, this.srcAddr.getPort(), this.dstAddr.getPort(), TCPPacket.TCPFlag.RST, TCPPacket.TCPFlag.ACK);
                this.completeTCPWrite(this.srcAddr, this.dstAddr, tcpBuf, ctx.alloc(), ctx);
            }
            finally {
                tcpBuf.release();
            }
            this.logger.debug("Sent Fake TCP RST to close connection");
        }
        this.close();
        ctx.fireExceptionCaught(cause);
    }
    
    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            this.logger.debug("PcapWriterHandler is already closed");
        }
        else {
            this.isClosed = true;
            this.pCapWriter.close();
            this.logger.debug("PcapWriterHandler is now closed");
        }
    }
}
