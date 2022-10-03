package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformC14NExclusiveWithComments extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream writer, final Transform transform) throws CanonicalizationException {
        try {
            String inclusiveNamespaces = null;
            if (transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                inclusiveNamespaces = new InclusiveNamespaces(XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), transform.getBaseURI()).getInclusiveNamespaces();
            }
            final Canonicalizer20010315ExclWithComments canonicalizer20010315ExclWithComments = new Canonicalizer20010315ExclWithComments();
            canonicalizer20010315ExclWithComments.setSecureValidation(this.secureValidation);
            if (writer != null) {
                canonicalizer20010315ExclWithComments.setWriter(writer);
            }
            final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(canonicalizer20010315ExclWithComments.engineCanonicalize(xmlSignatureInput, inclusiveNamespaces));
            xmlSignatureInput2.setSecureValidation(this.secureValidation);
            return xmlSignatureInput2;
        }
        catch (final XMLSecurityException ex) {
            throw new CanonicalizationException(ex);
        }
    }
}
