package org.apache.commons.net.tftp;

import org.apache.commons.net.io.ToNetASCIIInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.io.IOException;
import org.apache.commons.net.io.FromNetASCIIOutputStream;
import java.net.InetAddress;
import java.io.OutputStream;

public class TFTPClient extends TFTP
{
    public static final int DEFAULT_MAX_TIMEOUTS = 5;
    private int __maxTimeouts;
    private long totalBytesReceived;
    private long totalBytesSent;
    
    public TFTPClient() {
        this.totalBytesReceived = 0L;
        this.totalBytesSent = 0L;
        this.__maxTimeouts = 5;
    }
    
    public void setMaxTimeouts(final int numTimeouts) {
        if (numTimeouts < 1) {
            this.__maxTimeouts = 1;
        }
        else {
            this.__maxTimeouts = numTimeouts;
        }
    }
    
    public int getMaxTimeouts() {
        return this.__maxTimeouts;
    }
    
    public long getTotalBytesReceived() {
        return this.totalBytesReceived;
    }
    
    public long getTotalBytesSent() {
        return this.totalBytesSent;
    }
    
    public int receiveFile(final String filename, final int mode, OutputStream output, InetAddress host, final int port) throws IOException {
        int bytesRead = 0;
        int lastBlock = 0;
        int block = 1;
        int hostPort = 0;
        int dataLength = 0;
        this.totalBytesReceived = 0L;
        if (mode == 0) {
            output = new FromNetASCIIOutputStream(output);
        }
        TFTPPacket sent = new TFTPReadRequestPacket(host, port, filename, mode);
        final TFTPAckPacket ack = new TFTPAckPacket(host, port, 0);
        this.beginBufferedOps();
        boolean justStarted = true;
        try {
            do {
                this.bufferedSend(sent);
                boolean wantReply = true;
                int timeouts = 0;
                do {
                    try {
                        final TFTPPacket received = this.bufferedReceive();
                        final int recdPort = received.getPort();
                        final InetAddress recdAddress = received.getAddress();
                        if (justStarted) {
                            justStarted = false;
                            if (recdPort == port) {
                                final TFTPErrorPacket error = new TFTPErrorPacket(recdAddress, recdPort, 5, "INCORRECT SOURCE PORT");
                                this.bufferedSend(error);
                                throw new IOException("Incorrect source port (" + recdPort + ") in request reply.");
                            }
                            hostPort = recdPort;
                            ack.setPort(hostPort);
                            if (!host.equals(recdAddress)) {
                                host = recdAddress;
                                ack.setAddress(host);
                                sent.setAddress(host);
                            }
                        }
                        if (host.equals(recdAddress) && recdPort == hostPort) {
                            switch (received.getType()) {
                                case 5: {
                                    final TFTPErrorPacket error = (TFTPErrorPacket)received;
                                    throw new IOException("Error code " + error.getError() + " received: " + error.getMessage());
                                }
                                case 3: {
                                    final TFTPDataPacket data = (TFTPDataPacket)received;
                                    dataLength = data.getDataLength();
                                    lastBlock = data.getBlockNumber();
                                    if (lastBlock == block) {
                                        try {
                                            output.write(data.getData(), data.getDataOffset(), dataLength);
                                        }
                                        catch (final IOException e) {
                                            final TFTPErrorPacket error = new TFTPErrorPacket(host, hostPort, 3, "File write failed.");
                                            this.bufferedSend(error);
                                            throw e;
                                        }
                                        if (++block > 65535) {
                                            block = 0;
                                        }
                                        wantReply = false;
                                    }
                                    else {
                                        this.discardPackets();
                                        if (lastBlock == ((block == 0) ? 65535 : (block - 1))) {
                                            wantReply = false;
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    throw new IOException("Received unexpected packet type (" + received.getType() + ")");
                                }
                            }
                        }
                        else {
                            final TFTPErrorPacket error = new TFTPErrorPacket(recdAddress, recdPort, 5, "Unexpected host or port.");
                            this.bufferedSend(error);
                        }
                    }
                    catch (final SocketException e2) {
                        if (++timeouts >= this.__maxTimeouts) {
                            throw new IOException("Connection timed out.");
                        }
                    }
                    catch (final InterruptedIOException e3) {
                        if (++timeouts >= this.__maxTimeouts) {
                            throw new IOException("Connection timed out.");
                        }
                    }
                    catch (final TFTPPacketException e4) {
                        throw new IOException("Bad packet: " + e4.getMessage());
                    }
                } while (wantReply);
                ack.setBlockNumber(lastBlock);
                sent = ack;
                bytesRead += dataLength;
                this.totalBytesReceived += dataLength;
            } while (dataLength == 512);
            this.bufferedSend(sent);
        }
        finally {
            this.endBufferedOps();
        }
        return bytesRead;
    }
    
    public int receiveFile(final String filename, final int mode, final OutputStream output, final String hostname, final int port) throws UnknownHostException, IOException {
        return this.receiveFile(filename, mode, output, InetAddress.getByName(hostname), port);
    }
    
    public int receiveFile(final String filename, final int mode, final OutputStream output, final InetAddress host) throws IOException {
        return this.receiveFile(filename, mode, output, host, 69);
    }
    
    public int receiveFile(final String filename, final int mode, final OutputStream output, final String hostname) throws UnknownHostException, IOException {
        return this.receiveFile(filename, mode, output, InetAddress.getByName(hostname), 69);
    }
    
    public void sendFile(final String filename, final int mode, InputStream input, InetAddress host, final int port) throws IOException {
        int block = 0;
        int hostPort = 0;
        boolean justStarted = true;
        boolean lastAckWait = false;
        this.totalBytesSent = 0L;
        if (mode == 0) {
            input = new ToNetASCIIInputStream(input);
        }
        TFTPPacket sent = new TFTPWriteRequestPacket(host, port, filename, mode);
        final TFTPDataPacket data = new TFTPDataPacket(host, port, 0, this._sendBuffer, 4, 0);
        this.beginBufferedOps();
        try {
            while (true) {
                this.bufferedSend(sent);
                boolean wantReply = true;
                int timeouts = 0;
                do {
                    try {
                        final TFTPPacket received = this.bufferedReceive();
                        final InetAddress recdAddress = received.getAddress();
                        final int recdPort = received.getPort();
                        if (justStarted) {
                            justStarted = false;
                            if (recdPort == port) {
                                final TFTPErrorPacket error = new TFTPErrorPacket(recdAddress, recdPort, 5, "INCORRECT SOURCE PORT");
                                this.bufferedSend(error);
                                throw new IOException("Incorrect source port (" + recdPort + ") in request reply.");
                            }
                            hostPort = recdPort;
                            data.setPort(hostPort);
                            if (!host.equals(recdAddress)) {
                                host = recdAddress;
                                data.setAddress(host);
                                sent.setAddress(host);
                            }
                        }
                        if (host.equals(recdAddress) && recdPort == hostPort) {
                            switch (received.getType()) {
                                case 5: {
                                    final TFTPErrorPacket error = (TFTPErrorPacket)received;
                                    throw new IOException("Error code " + error.getError() + " received: " + error.getMessage());
                                }
                                case 4: {
                                    final int lastBlock = ((TFTPAckPacket)received).getBlockNumber();
                                    if (lastBlock == block) {
                                        if (++block > 65535) {
                                            block = 0;
                                        }
                                        wantReply = false;
                                    }
                                    else {
                                        this.discardPackets();
                                    }
                                    break;
                                }
                                default: {
                                    throw new IOException("Received unexpected packet type.");
                                }
                            }
                        }
                        else {
                            final TFTPErrorPacket error = new TFTPErrorPacket(recdAddress, recdPort, 5, "Unexpected host or port.");
                            this.bufferedSend(error);
                        }
                    }
                    catch (final SocketException e) {
                        if (++timeouts >= this.__maxTimeouts) {
                            throw new IOException("Connection timed out.");
                        }
                    }
                    catch (final InterruptedIOException e2) {
                        if (++timeouts >= this.__maxTimeouts) {
                            throw new IOException("Connection timed out.");
                        }
                    }
                    catch (final TFTPPacketException e3) {
                        throw new IOException("Bad packet: " + e3.getMessage());
                    }
                } while (wantReply);
                if (lastAckWait) {
                    break;
                }
                int dataLength = 512;
                int offset = 4;
                int totalThisPacket = 0;
                for (int bytesRead = 0; dataLength > 0 && (bytesRead = input.read(this._sendBuffer, offset, dataLength)) > 0; offset += bytesRead, dataLength -= bytesRead, totalThisPacket += bytesRead) {}
                if (totalThisPacket < 512) {
                    lastAckWait = true;
                }
                data.setBlockNumber(block);
                data.setData(this._sendBuffer, 4, totalThisPacket);
                sent = data;
                this.totalBytesSent += totalThisPacket;
            }
        }
        finally {
            this.endBufferedOps();
        }
    }
    
    public void sendFile(final String filename, final int mode, final InputStream input, final String hostname, final int port) throws UnknownHostException, IOException {
        this.sendFile(filename, mode, input, InetAddress.getByName(hostname), port);
    }
    
    public void sendFile(final String filename, final int mode, final InputStream input, final InetAddress host) throws IOException {
        this.sendFile(filename, mode, input, host, 69);
    }
    
    public void sendFile(final String filename, final int mode, final InputStream input, final String hostname) throws UnknownHostException, IOException {
        this.sendFile(filename, mode, input, InetAddress.getByName(hostname), 69);
    }
}
