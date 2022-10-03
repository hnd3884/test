package io.netty.resolver.dns;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class RotationalDnsServerAddresses extends DefaultDnsServerAddresses
{
    private static final AtomicIntegerFieldUpdater<RotationalDnsServerAddresses> startIdxUpdater;
    private volatile int startIdx;
    
    RotationalDnsServerAddresses(final List<InetSocketAddress> addresses) {
        super("rotational", addresses);
    }
    
    @Override
    public DnsServerAddressStream stream() {
        int curStartIdx;
        int nextStartIdx;
        do {
            curStartIdx = this.startIdx;
            nextStartIdx = curStartIdx + 1;
            if (nextStartIdx >= this.addresses.size()) {
                nextStartIdx = 0;
            }
        } while (!RotationalDnsServerAddresses.startIdxUpdater.compareAndSet(this, curStartIdx, nextStartIdx));
        return new SequentialDnsServerAddressStream(this.addresses, curStartIdx);
    }
    
    static {
        startIdxUpdater = AtomicIntegerFieldUpdater.newUpdater(RotationalDnsServerAddresses.class, "startIdx");
    }
}
