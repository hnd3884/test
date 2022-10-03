package javax.jms;

import java.util.Enumeration;

public interface ConnectionMetaData
{
    int getJMSMajorVersion() throws JMSException;
    
    int getJMSMinorVersion() throws JMSException;
    
    String getJMSProviderName() throws JMSException;
    
    String getJMSVersion() throws JMSException;
    
    Enumeration getJMSXPropertyNames() throws JMSException;
    
    int getProviderMajorVersion() throws JMSException;
    
    int getProviderMinorVersion() throws JMSException;
    
    String getProviderVersion() throws JMSException;
}
