package com.turo.pushy.apns.proxy;

import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import java.net.SocketAddress;

public class Socks5ProxyHandlerFactory implements ProxyHandlerFactory
{
    private final SocketAddress proxyAddress;
    private final String username;
    private final String password;
    
    public Socks5ProxyHandlerFactory(final SocketAddress proxyAddress) {
        this(proxyAddress, null, null);
    }
    
    public Socks5ProxyHandlerFactory(final SocketAddress proxyAddress, final String username, final String password) {
        this.proxyAddress = proxyAddress;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public ProxyHandler createProxyHandler() {
        return (ProxyHandler)new Socks5ProxyHandler(this.proxyAddress, this.username, this.password);
    }
}
