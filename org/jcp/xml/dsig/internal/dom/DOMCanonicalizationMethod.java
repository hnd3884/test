package org.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.CanonicalizationMethod;

public class DOMCanonicalizationMethod extends DOMTransform implements CanonicalizationMethod
{
    public DOMCanonicalizationMethod(final TransformService transformService) throws InvalidAlgorithmParameterException {
        super(transformService);
    }
    
    public DOMCanonicalizationMethod(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        super(element, xmlCryptoContext, provider);
    }
    
    public Data canonicalize(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        return this.transform(data, xmlCryptoContext);
    }
    
    public Data canonicalize(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream outputStream) throws TransformException {
        return this.transform(data, xmlCryptoContext, outputStream);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CanonicalizationMethod)) {
            return false;
        }
        final CanonicalizationMethod canonicalizationMethod = (CanonicalizationMethod)o;
        return this.getAlgorithm().equals(canonicalizationMethod.getAlgorithm()) && DOMUtils.paramsEqual(this.getParameterSpec(), canonicalizationMethod.getParameterSpec());
    }
    
    public int hashCode() {
        return 42;
    }
}
