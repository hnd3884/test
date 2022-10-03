package com.sun.jndi.dns;

import sun.net.PortConfig;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.util.Objects;
import java.util.Random;
import java.net.ProtocolFamily;

class DNSDatagramSocketFactory
{
    static final int DEVIATION = 3;
    static final int THRESHOLD = 6;
    static final int BIT_DEVIATION = 2;
    static final int HISTORY = 32;
    static final int MAX_RANDOM_TRIES = 5;
    int lastport;
    int suitablePortCount;
    int unsuitablePortCount;
    final ProtocolFamily family;
    final int thresholdCount;
    final int deviation;
    final Random random;
    final PortHistory history;
    
    DNSDatagramSocketFactory() {
        this(new Random());
    }
    
    DNSDatagramSocketFactory(final Random random) {
        this(Objects.requireNonNull(random), null, 3, 6);
    }
    
    DNSDatagramSocketFactory(final Random random, final ProtocolFamily family, final int n, final int n2) {
        this.lastport = 0;
        this.random = Objects.requireNonNull(random);
        this.history = new PortHistory(32, random);
        this.family = family;
        this.deviation = Math.max(1, n);
        this.thresholdCount = Math.max(2, n2);
    }
    
    public synchronized DatagramSocket open() throws SocketException {
        int lastport = this.lastport;
        if (this.unsuitablePortCount > this.thresholdCount) {
            final DatagramSocket openRandom = this.openRandom();
            if (openRandom != null) {
                return openRandom;
            }
            this.unsuitablePortCount = 0;
            this.suitablePortCount = 0;
            lastport = 0;
        }
        final DatagramSocket openDefault = this.openDefault();
        this.lastport = openDefault.getLocalPort();
        if (lastport == 0) {
            this.history.offer(this.lastport);
            return openDefault;
        }
        final boolean b = this.suitablePortCount > this.thresholdCount;
        final boolean b2 = Integer.bitCount(lastport ^ this.lastport) > 2 && Math.abs(this.lastport - lastport) > this.deviation;
        final boolean contains = this.history.contains(this.lastport);
        final boolean b3 = b || (b2 && !contains);
        if (b3 && !contains) {
            this.history.add(this.lastport);
        }
        if (b3) {
            if (!b) {
                ++this.suitablePortCount;
            }
            else if (!b2 || contains) {
                this.unsuitablePortCount = 1;
                this.suitablePortCount = this.thresholdCount / 2;
            }
            return openDefault;
        }
        assert !b;
        final DatagramSocket openRandom2 = this.openRandom();
        if (openRandom2 == null) {
            return openDefault;
        }
        ++this.unsuitablePortCount;
        openDefault.close();
        return openRandom2;
    }
    
    private DatagramSocket openDefault() throws SocketException {
        if (this.family != null) {
            try {
                final DatagramChannel open = DatagramChannel.open(this.family);
                try {
                    final DatagramSocket socket = open.socket();
                    socket.bind(null);
                    return socket;
                }
                catch (final Throwable t) {
                    open.close();
                    throw t;
                }
            }
            catch (final SocketException ex) {
                throw ex;
            }
            catch (final IOException ex2) {
                final SocketException ex3 = new SocketException(ex2.getMessage());
                ex3.initCause(ex2);
                throw ex3;
            }
        }
        return new DatagramSocket();
    }
    
    synchronized boolean isUsingNativePortRandomization() {
        return this.unsuitablePortCount <= this.thresholdCount && this.suitablePortCount > this.thresholdCount;
    }
    
    synchronized boolean isUsingJavaPortRandomization() {
        return this.unsuitablePortCount > this.thresholdCount;
    }
    
    synchronized boolean isUndecided() {
        return !this.isUsingJavaPortRandomization() && !this.isUsingNativePortRandomization();
    }
    
    private DatagramSocket openRandom() {
        int n = 5;
        while (n-- > 0) {
            final int n2 = EphemeralPortRange.LOWER + this.random.nextInt(EphemeralPortRange.RANGE);
            try {
                if (this.family != null) {
                    final DatagramChannel open = DatagramChannel.open(this.family);
                    try {
                        final DatagramSocket socket = open.socket();
                        socket.bind(new InetSocketAddress(n2));
                        return socket;
                    }
                    catch (final Throwable t) {
                        open.close();
                        throw t;
                    }
                }
                return new DatagramSocket(n2);
            }
            catch (final IOException ex) {
                continue;
            }
            break;
        }
        return null;
    }
    
    static final class EphemeralPortRange
    {
        static final int LOWER;
        static final int UPPER;
        static final int RANGE;
        
        private EphemeralPortRange() {
        }
        
        static {
            LOWER = PortConfig.getLower();
            UPPER = PortConfig.getUpper();
            RANGE = EphemeralPortRange.UPPER - EphemeralPortRange.LOWER + 1;
        }
    }
    
    static final class PortHistory
    {
        final int capacity;
        final int[] ports;
        final Random random;
        int index;
        
        PortHistory(final int capacity, final Random random) {
            this.random = random;
            this.capacity = capacity;
            this.ports = new int[capacity];
        }
        
        public boolean contains(final int n) {
            int n2 = 0;
            for (int n3 = 0; n3 < this.capacity && (n2 = this.ports[n3]) != 0 && n2 != n; ++n3) {}
            return n2 == n;
        }
        
        public boolean add(final int n) {
            if (this.ports[this.index] != 0) {
                this.ports[this.random.nextInt(this.capacity)] = n;
            }
            else {
                this.ports[this.index] = n;
            }
            if (++this.index == this.capacity) {
                this.index = 0;
            }
            return true;
        }
        
        public boolean offer(final int n) {
            return !this.contains(n) && this.add(n);
        }
    }
}
