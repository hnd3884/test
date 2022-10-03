package com.btr.proxy.selector.fixed;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class FixedSocksSelector extends FixedProxySelector
{
    public FixedSocksSelector(final String proxyHost, final int proxyPort) {
        super(new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
    }
}
