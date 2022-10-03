package javax.jms;

public interface Connection
{
    String getClientID() throws JMSException;
    
    void setClientID(final String p0) throws JMSException;
    
    ConnectionMetaData getMetaData() throws JMSException;
    
    ExceptionListener getExceptionListener() throws JMSException;
    
    void setExceptionListener(final ExceptionListener p0) throws JMSException;
    
    void start() throws JMSException;
    
    void stop() throws JMSException;
    
    void close() throws JMSException;
}
