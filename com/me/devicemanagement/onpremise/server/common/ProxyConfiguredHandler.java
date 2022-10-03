package com.me.devicemanagement.onpremise.server.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ProxyConfiguredHandler
{
    private static Logger logger;
    private static ProxyConfiguredHandler proxyHandler;
    private static List<ProxyConfiguredListener> proxyListenerList;
    private static List urlList;
    
    public static ProxyConfiguredHandler getInstance() {
        if (ProxyConfiguredHandler.proxyHandler == null) {
            ProxyConfiguredHandler.proxyHandler = new ProxyConfiguredHandler();
        }
        return ProxyConfiguredHandler.proxyHandler;
    }
    
    public void addProxyConfiguredListener(final ProxyConfiguredListener deviceListener) {
        ProxyConfiguredHandler.proxyListenerList.add(deviceListener);
        deviceListener.addUrlsForDomainValidation();
        ProxyConfiguredHandler.logger.log(Level.INFO, "Proxy Listener Added :{0}", deviceListener.getClass().getName());
    }
    
    public void removeProxyConfiguredListener(final ProxyConfiguredListener deviceListener) {
        ProxyConfiguredHandler.proxyListenerList.remove(deviceListener);
        ProxyConfiguredHandler.logger.log(Level.INFO, "Proxy Listener Removed :{0}", deviceListener.getClass().getName());
    }
    
    public void invokeProxyListeners(final Properties proxyProps) {
        for (final ProxyConfiguredListener listener : ProxyConfiguredHandler.proxyListenerList) {
            listener.proxyConfigured(proxyProps);
            ProxyConfiguredHandler.logger.log(Level.INFO, "Proxy Listener Called :{0}", listener.getClass().getName());
        }
    }
    
    public void addUrlTypeForDomainValidation(final String urlType) {
        ProxyConfiguredHandler.urlList.add(urlType);
        ProxyConfiguredHandler.logger.log(Level.INFO, "URL type for Domain Validation Added :{0}", urlType);
    }
    
    public List getUrlTypeList() {
        return ProxyConfiguredHandler.urlList;
    }
    
    static {
        ProxyConfiguredHandler.logger = Logger.getLogger(ProxyConfiguredHandler.class.getName());
        ProxyConfiguredHandler.proxyHandler = null;
        ProxyConfiguredHandler.proxyListenerList = new ArrayList<ProxyConfiguredListener>();
        ProxyConfiguredHandler.urlList = new ArrayList();
    }
}
