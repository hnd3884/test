package javax.xml.crypto.dsig.keyinfo;

import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import java.util.List;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.URIReference;

public interface RetrievalMethod extends URIReference, XMLStructure
{
    List getTransforms();
    
    String getURI();
    
    Data dereference(final XMLCryptoContext p0) throws URIReferenceException;
}
