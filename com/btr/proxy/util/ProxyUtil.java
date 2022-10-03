package com.btr.proxy.util;

import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Matcher;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import java.util.regex.Pattern;
import java.net.Proxy;
import java.util.List;

public class ProxyUtil
{
    public static final int DEFAULT_PROXY_PORT = 80;
    private static List<Proxy> noProxyList;
    private static Pattern pattern;
    
    public static FixedProxySelector parseProxySettings(final String proxyVar) {
        if (proxyVar == null || proxyVar.trim().length() == 0) {
            return null;
        }
        final Matcher matcher = ProxyUtil.pattern.matcher(proxyVar);
        if (matcher.matches()) {
            final String host = matcher.group(1);
            int port;
            if (!"".equals(matcher.group(2))) {
                port = Integer.parseInt(matcher.group(2));
            }
            else {
                port = 80;
            }
            return new FixedProxySelector(host.trim(), port);
        }
        return null;
    }
    
    public static synchronized List<Proxy> noProxyList() {
        if (ProxyUtil.noProxyList == null) {
            final ArrayList<Proxy> list = new ArrayList<Proxy>(1);
            list.add(Proxy.NO_PROXY);
            ProxyUtil.noProxyList = Collections.unmodifiableList((List<? extends Proxy>)list);
        }
        return ProxyUtil.noProxyList;
    }
    
    static {
        ProxyUtil.pattern = Pattern.compile("\\w*?:?/*([^:/]+):?(\\d*)/?");
    }
}
