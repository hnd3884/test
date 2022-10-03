package com.adventnet.persistence.interceptor;

import javax.transaction.SystemException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAEInterceptor implements PersistenceInterceptor
{
    private static final Logger LOGGER;
    PersistenceInterceptor nextPI;
    
    public DAEInterceptor() {
        this.nextPI = null;
        DAEInterceptor.LOGGER.log(Level.FINEST, "initializing DAEInterceptor...");
    }
    
    @Override
    public String getInterceptorName() {
        return "DAEInterceptor";
    }
    
    @Override
    public void setBeanConfiguration(final DataObject beanDO) {
    }
    
    @Override
    public void setNextInterceptor(final PersistenceInterceptor pi) {
        this.nextPI = pi;
    }
    
    @Override
    public void cleanup() {
    }
    
    @Override
    public Object process(final PersistenceRequest request) throws DataAccessException {
        Object toreturn = null;
        try {
            toreturn = this.nextPI.process(request);
        }
        catch (final DataAccessException dae) {
            DAEInterceptor.LOGGER.log(Level.FINEST, "DataAccessException caught in DAEInterceptor, enable roleback flag", dae);
            try {
                DataAccess.getTransactionManager().setRollbackOnly();
            }
            catch (final SystemException se) {
                throw new DataAccessException("SystemException occured while trying to set rollback", (Throwable)se);
            }
            throw dae;
        }
        return toreturn;
    }
    
    static {
        LOGGER = Logger.getLogger(DAEInterceptor.class.getName());
    }
}
