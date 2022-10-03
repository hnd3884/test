package javax.xml.crypto.dsig;

import java.io.InputStream;
import javax.xml.crypto.Data;
import java.util.List;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.URIReference;

public interface Reference extends URIReference, XMLStructure
{
    List getTransforms();
    
    DigestMethod getDigestMethod();
    
    String getId();
    
    byte[] getDigestValue();
    
    byte[] getCalculatedDigestValue();
    
    boolean validate(final XMLValidateContext p0) throws XMLSignatureException;
    
    Data getDereferencedData();
    
    InputStream getDigestInputStream();
}
