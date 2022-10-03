package javax.jms;

import java.io.Serializable;

public interface ObjectMessage extends Message
{
    void setObject(final Serializable p0) throws JMSException;
    
    Serializable getObject() throws JMSException;
}
