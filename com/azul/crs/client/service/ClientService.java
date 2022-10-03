package com.azul.crs.client.service;

import com.azul.crs.util.logging.Logger;

public interface ClientService
{
    default String serviceName() {
        return "client.service." + this.getClass().getSimpleName();
    }
    
    default Logger logger() {
        return Logger.getLogger(this.getClass());
    }
    
    void start();
    
    void stop(final long p0);
}
