package com.turo.pushy.apns.proxy;

import io.netty.handler.proxy.ProxyHandler;

public interface ProxyHandlerFactory
{
    ProxyHandler createProxyHandler();
}
