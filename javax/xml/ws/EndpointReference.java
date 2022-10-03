package javax.xml.ws;

import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.ws.spi.Provider;
import javax.xml.transform.Source;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class EndpointReference
{
    protected EndpointReference() {
    }
    
    public static EndpointReference readFrom(final Source eprInfoset) {
        return Provider.provider().readEndpointReference(eprInfoset);
    }
    
    public abstract void writeTo(final Result p0);
    
    public <T> T getPort(final Class<T> serviceEndpointInterface, final WebServiceFeature... features) {
        return Provider.provider().getPort(this, serviceEndpointInterface, features);
    }
    
    @Override
    public String toString() {
        final StringWriter w = new StringWriter();
        this.writeTo(new StreamResult(w));
        return w.toString();
    }
}
