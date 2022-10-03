package javax.xml.crypto.dsig.keyinfo;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface KeyInfo extends XMLStructure
{
    List getContent();
    
    String getId();
    
    void marshal(final XMLStructure p0, final XMLCryptoContext p1) throws MarshalException;
}
