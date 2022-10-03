package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class BasePersistenceInterceptor implements PersistenceInterceptor
{
    PersistenceInterceptor nextPI;
    private static final String CLASS_NAME;
    private static final Logger IOUT;
    
    public BasePersistenceInterceptor() {
        this.nextPI = null;
    }
    
    @Override
    public void setBeanConfiguration(final DataObject beanDO) {
    }
    
    @Override
    public String getInterceptorName() {
        return "BasePersistenceInterceptor";
    }
    
    @Override
    public Object process(final PersistenceRequest prequest) throws DataAccessException {
        BasePersistenceInterceptor.IOUT.log(Level.FINER, "delegating to next interceptor in basepersistenceinterceptor ... ");
        return this.nextPI.process(prequest);
    }
    
    @Override
    public void setNextInterceptor(final PersistenceInterceptor pi) {
        this.nextPI = pi;
    }
    
    @Override
    public void cleanup() {
        BasePersistenceInterceptor.IOUT.log(Level.FINER, "Clean up not implemented in BasePersistenceInterceptor");
    }
    
    static {
        CLASS_NAME = BasePersistenceInterceptor.class.getName();
        IOUT = Logger.getLogger(BasePersistenceInterceptor.CLASS_NAME);
    }
}
