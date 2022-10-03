package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Logger;

public abstract class DOMSHADigestMethod extends DOMDigestMethod
{
    private static Logger log;
    
    protected DOMSHADigestMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        super(algorithmParameterSpec);
    }
    
    protected DOMSHADigestMethod(final Element element) throws MarshalException {
        super(element);
    }
    
    protected void checkParams(final DigestMethodParameterSpec digestMethodParameterSpec) throws InvalidAlgorithmParameterException {
        if (digestMethodParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + this.getName() + " DigestMethod algorithm");
        }
    }
    
    protected DigestMethodParameterSpec unmarshalParams(final Element element) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getName() + " DigestMethod algorithm");
    }
    
    protected void marshalParams(final Element element, final String s) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getName() + " DigestMethod algorithm");
    }
    
    abstract String getName();
    
    static final DOMSHADigestMethod SHA1(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        return new DOMSHA1DigestMethod(algorithmParameterSpec);
    }
    
    static final DOMSHADigestMethod SHA1(final Element element) throws MarshalException {
        return new DOMSHA1DigestMethod(element);
    }
    
    static final DOMSHADigestMethod SHA256(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        return new DOMSHA256DigestMethod(algorithmParameterSpec);
    }
    
    static final DOMSHADigestMethod SHA256(final Element element) throws MarshalException {
        return new DOMSHA256DigestMethod(element);
    }
    
    static final DOMSHADigestMethod SHA512(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        return new DOMSHA512DigestMethod(algorithmParameterSpec);
    }
    
    static final DOMSHADigestMethod SHA512(final Element element) throws MarshalException {
        return new DOMSHA512DigestMethod(element);
    }
    
    static {
        DOMSHADigestMethod.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
    
    private static final class DOMSHA1DigestMethod extends DOMSHADigestMethod
    {
        DOMSHA1DigestMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
            super(algorithmParameterSpec);
        }
        
        DOMSHA1DigestMethod(final Element element) throws MarshalException {
            super(element);
        }
        
        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#sha1";
        }
        
        String getMessageDigestAlgorithm() {
            return "SHA";
        }
        
        String getName() {
            return "SHA1";
        }
    }
    
    private static final class DOMSHA256DigestMethod extends DOMSHADigestMethod
    {
        DOMSHA256DigestMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
            super(algorithmParameterSpec);
        }
        
        DOMSHA256DigestMethod(final Element element) throws MarshalException {
            super(element);
        }
        
        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }
        
        String getMessageDigestAlgorithm() {
            return "SHA-256";
        }
        
        String getName() {
            return "SHA256";
        }
    }
    
    private static final class DOMSHA512DigestMethod extends DOMSHADigestMethod
    {
        DOMSHA512DigestMethod(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
            super(algorithmParameterSpec);
        }
        
        DOMSHA512DigestMethod(final Element element) throws MarshalException {
            super(element);
        }
        
        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha512";
        }
        
        String getMessageDigestAlgorithm() {
            return "SHA-512";
        }
        
        String getName() {
            return "SHA512";
        }
    }
}
