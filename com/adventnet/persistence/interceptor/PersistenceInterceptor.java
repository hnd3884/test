package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;

public interface PersistenceInterceptor
{
    String getInterceptorName();
    
    Object process(final PersistenceRequest p0) throws DataAccessException;
    
    void setNextInterceptor(final PersistenceInterceptor p0);
    
    void cleanup();
    
    void setBeanConfiguration(final DataObject p0) throws DataAccessException;
}
