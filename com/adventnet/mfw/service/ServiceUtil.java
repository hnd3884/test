package com.adventnet.mfw.service;

import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class ServiceUtil
{
    private static final Logger OUT;
    
    public static Service lookup(final String serviceName) throws Exception {
        final HashMap map = ServiceStarter.serviceMap;
        final Service service = map.get(serviceName);
        return service;
    }
    
    public static void addServices(final DataObject serviceDO) throws Exception {
        ServiceStarter.addServices(serviceDO);
    }
    
    static {
        OUT = Logger.getLogger(ServiceUtil.class.getName());
    }
}
