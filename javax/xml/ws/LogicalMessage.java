package javax.xml.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

public interface LogicalMessage
{
    Source getPayload();
    
    void setPayload(final Source p0);
    
    Object getPayload(final JAXBContext p0);
    
    void setPayload(final Object p0, final JAXBContext p1);
}
