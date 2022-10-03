package org.jcp.xml.dsig.internal.dom;

import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import javax.xml.crypto.dsig.TransformException;
import org.apache.xml.security.c14n.Canonicalizer;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.Data;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMCanonicalXMLC14NMethod extends ApacheCanonicalizer
{
    public void init(final TransformParameterSpec transformParameterSpec) throws InvalidAlgorithmParameterException {
        if (transformParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for Canonical XML C14N algorithm");
        }
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        if (data instanceof DOMSubTreeData && ((DOMSubTreeData)data).excludeComments()) {
            try {
                super.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/TR/2001/REC-xml-c14n-20010315: " + ex.getMessage(), ex);
            }
        }
        return this.canonicalize(data, xmlCryptoContext);
    }
}
