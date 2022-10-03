package javax.ejb;

import javax.transaction.UserTransaction;
import java.util.Properties;
import java.security.Principal;
import java.security.Identity;

public interface EJBContext
{
    Identity getCallerIdentity();
    
    Principal getCallerPrincipal();
    
    EJBHome getEJBHome() throws IllegalStateException;
    
    EJBLocalHome getEJBLocalHome() throws IllegalStateException;
    
    Properties getEnvironment();
    
    boolean getRollbackOnly() throws IllegalStateException;
    
    UserTransaction getUserTransaction() throws IllegalStateException;
    
    boolean isCallerInRole(final String p0);
    
    boolean isCallerInRole(final Identity p0);
    
    void setRollbackOnly() throws IllegalStateException;
}
