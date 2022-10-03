package javax.xml.crypto.dsig;

import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.XMLStructure;

public interface XMLSignature extends XMLStructure
{
    public static final String XMLNS = "http://www.w3.org/2000/09/xmldsig#";
    
    boolean validate(final XMLValidateContext p0) throws XMLSignatureException;
    
    KeyInfo getKeyInfo();
    
    SignedInfo getSignedInfo();
    
    List getObjects();
    
    String getId();
    
    SignatureValue getSignatureValue();
    
    void sign(final XMLSignContext p0) throws MarshalException, XMLSignatureException;
    
    KeySelectorResult getKeySelectorResult();
    
    public interface SignatureValue extends XMLStructure
    {
        String getId();
        
        byte[] getValue();
        
        boolean validate(final XMLValidateContext p0) throws XMLSignatureException;
    }
}
