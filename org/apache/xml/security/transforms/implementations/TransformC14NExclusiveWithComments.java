package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.OutputStream;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformC14NExclusiveWithComments extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws CanonicalizationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream writer, final Transform transform) throws CanonicalizationException {
        try {
            String inclusiveNamespaces = null;
            if (transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                inclusiveNamespaces = new InclusiveNamespaces(XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), transform.getBaseURI()).getInclusiveNamespaces();
            }
            final Canonicalizer20010315ExclWithComments canonicalizer20010315ExclWithComments = new Canonicalizer20010315ExclWithComments();
            if (writer != null) {
                canonicalizer20010315ExclWithComments.setWriter(writer);
            }
            return new XMLSignatureInput(canonicalizer20010315ExclWithComments.engineCanonicalize(xmlSignatureInput, inclusiveNamespaces));
        }
        catch (final XMLSecurityException ex) {
            throw new CanonicalizationException("empty", ex);
        }
    }
}
