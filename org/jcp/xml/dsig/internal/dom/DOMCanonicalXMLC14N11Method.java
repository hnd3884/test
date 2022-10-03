package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import javax.xml.crypto.dsig.TransformException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.Data;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMCanonicalXMLC14N11Method extends ApacheCanonicalizer
{
    public static final String C14N_11 = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String C14N_11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    
    @Override
    public void init(final TransformParameterSpec transformParameterSpec) throws InvalidAlgorithmParameterException {
        if (transformParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for Canonical XML 1.1 algorithm");
        }
    }
    
    @Override
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        if (data instanceof DOMSubTreeData && ((DOMSubTreeData)data).excludeComments()) {
            try {
                (this.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2006/12/xml-c14n11")).setSecureValidation(Utils.secureValidation(xmlCryptoContext));
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2006/12/xml-c14n11: " + ex.getMessage(), ex);
            }
        }
        return this.canonicalize(data, xmlCryptoContext);
    }
}
