package com.sun.nio.sctp;

import sun.nio.ch.sctp.SctpStdSocketOption;
import java.net.SocketAddress;
import jdk.Exported;

@Exported
public class SctpStandardSocketOptions
{
    public static final SctpSocketOption<Boolean> SCTP_DISABLE_FRAGMENTS;
    public static final SctpSocketOption<Boolean> SCTP_EXPLICIT_COMPLETE;
    public static final SctpSocketOption<Integer> SCTP_FRAGMENT_INTERLEAVE;
    public static final SctpSocketOption<InitMaxStreams> SCTP_INIT_MAXSTREAMS;
    public static final SctpSocketOption<Boolean> SCTP_NODELAY;
    public static final SctpSocketOption<SocketAddress> SCTP_PRIMARY_ADDR;
    public static final SctpSocketOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR;
    public static final SctpSocketOption<Integer> SO_SNDBUF;
    public static final SctpSocketOption<Integer> SO_RCVBUF;
    public static final SctpSocketOption<Integer> SO_LINGER;
    
    private SctpStandardSocketOptions() {
    }
    
    static {
        SCTP_DISABLE_FRAGMENTS = new SctpStdSocketOption<Boolean>("SCTP_DISABLE_FRAGMENTS", Boolean.class, 1);
        SCTP_EXPLICIT_COMPLETE = new SctpStdSocketOption<Boolean>("SCTP_EXPLICIT_COMPLETE", Boolean.class, 2);
        SCTP_FRAGMENT_INTERLEAVE = new SctpStdSocketOption<Integer>("SCTP_FRAGMENT_INTERLEAVE", Integer.class, 3);
        SCTP_INIT_MAXSTREAMS = new SctpStdSocketOption<InitMaxStreams>("SCTP_INIT_MAXSTREAMS", InitMaxStreams.class);
        SCTP_NODELAY = new SctpStdSocketOption<Boolean>("SCTP_NODELAY", Boolean.class, 4);
        SCTP_PRIMARY_ADDR = new SctpStdSocketOption<SocketAddress>("SCTP_PRIMARY_ADDR", SocketAddress.class);
        SCTP_SET_PEER_PRIMARY_ADDR = new SctpStdSocketOption<SocketAddress>("SCTP_SET_PEER_PRIMARY_ADDR", SocketAddress.class);
        SO_SNDBUF = new SctpStdSocketOption<Integer>("SO_SNDBUF", Integer.class, 5);
        SO_RCVBUF = new SctpStdSocketOption<Integer>("SO_RCVBUF", Integer.class, 6);
        SO_LINGER = new SctpStdSocketOption<Integer>("SO_LINGER", Integer.class, 7);
    }
    
    @Exported
    public static class InitMaxStreams
    {
        private int maxInStreams;
        private int maxOutStreams;
        
        private InitMaxStreams(final int maxInStreams, final int maxOutStreams) {
            this.maxInStreams = maxInStreams;
            this.maxOutStreams = maxOutStreams;
        }
        
        public static InitMaxStreams create(final int n, final int n2) {
            if (n2 < 0 || n2 > 65535) {
                throw new IllegalArgumentException("Invalid maxOutStreams value");
            }
            if (n < 0 || n > 65535) {
                throw new IllegalArgumentException("Invalid maxInStreams value");
            }
            return new InitMaxStreams(n, n2);
        }
        
        public int maxInStreams() {
            return this.maxInStreams;
        }
        
        public int maxOutStreams() {
            return this.maxOutStreams;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(super.toString()).append(" [");
            sb.append("maxInStreams:").append(this.maxInStreams);
            sb.append("maxOutStreams:").append(this.maxOutStreams).append("]");
            return sb.toString();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o != null && o instanceof InitMaxStreams) {
                final InitMaxStreams initMaxStreams = (InitMaxStreams)o;
                if (this.maxInStreams == initMaxStreams.maxInStreams && this.maxOutStreams == initMaxStreams.maxOutStreams) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return 0x7 ^ this.maxInStreams ^ this.maxOutStreams;
        }
    }
}
