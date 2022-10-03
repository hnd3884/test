package javax.jms;

import javax.transaction.xa.XAResource;

public interface XASession extends Session
{
    XAResource getXAResource();
    
    boolean getTransacted() throws JMSException;
    
    void commit() throws JMSException;
    
    void rollback() throws JMSException;
}
