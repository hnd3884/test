package com.adventnet.mfw.service;

import com.adventnet.persistence.DataObject;

public interface Service
{
    void create(final DataObject p0) throws Exception;
    
    void start() throws Exception;
    
    void stop() throws Exception;
    
    void destroy() throws Exception;
}
