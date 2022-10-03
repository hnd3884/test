package com.btr.proxy.search;

import com.btr.proxy.util.ProxyException;
import java.net.ProxySelector;

public interface ProxySearchStrategy
{
    ProxySelector getProxySelector() throws ProxyException;
}
