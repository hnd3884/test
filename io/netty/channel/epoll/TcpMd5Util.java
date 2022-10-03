package io.netty.channel.epoll;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;
import java.net.InetAddress;
import java.util.Collection;

final class TcpMd5Util
{
    static Collection<InetAddress> newTcpMd5Sigs(final AbstractEpollChannel channel, final Collection<InetAddress> current, final Map<InetAddress, byte[]> newKeys) throws IOException {
        ObjectUtil.checkNotNull(channel, "channel");
        ObjectUtil.checkNotNull(current, "current");
        ObjectUtil.checkNotNull(newKeys, "newKeys");
        for (final Map.Entry<InetAddress, byte[]> e : newKeys.entrySet()) {
            final byte[] key = e.getValue();
            ObjectUtil.checkNotNullWithIAE(e.getKey(), "e.getKey");
            ObjectUtil.checkNonEmpty(key, e.getKey().toString());
            if (key.length > Native.TCP_MD5SIG_MAXKEYLEN) {
                throw new IllegalArgumentException("newKeys[" + e.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')');
            }
        }
        for (final InetAddress addr : current) {
            if (!newKeys.containsKey(addr)) {
                channel.socket.setTcpMd5Sig(addr, null);
            }
        }
        if (newKeys.isEmpty()) {
            return (Collection<InetAddress>)Collections.emptySet();
        }
        final Collection<InetAddress> addresses = new ArrayList<InetAddress>(newKeys.size());
        for (final Map.Entry<InetAddress, byte[]> e2 : newKeys.entrySet()) {
            channel.socket.setTcpMd5Sig(e2.getKey(), e2.getValue());
            addresses.add(e2.getKey());
        }
        return addresses;
    }
    
    private TcpMd5Util() {
    }
}
