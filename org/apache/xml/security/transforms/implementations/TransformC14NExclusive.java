package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.OutputStream;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformC14NExclusive extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#";
    
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws CanonicalizationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws CanonicalizationException {
        try {
            String inclusiveNamespaces = null;
            if (transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                inclusiveNamespaces = new InclusiveNamespaces(XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), transform.getBaseURI()).getInclusiveNamespaces();
            }
            final Canonicalizer20010315ExclOmitComments canonicalizer20010315ExclOmitComments = new Canonicalizer20010315ExclOmitComments();
            if (outputStream != null) {
                canonicalizer20010315ExclOmitComments.setWriter(outputStream);
            }
            final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(canonicalizer20010315ExclOmitComments.engineCanonicalize(xmlSignatureInput, inclusiveNamespaces));
            if (outputStream != null) {
                xmlSignatureInput2.setOutputStream(outputStream);
            }
            return xmlSignatureInput2;
        }
        catch (final XMLSecurityException ex) {
            throw new CanonicalizationException("empty", ex);
        }
    }
}
