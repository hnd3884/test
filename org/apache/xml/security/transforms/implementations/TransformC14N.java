package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.OutputStream;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformC14N extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws CanonicalizationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws CanonicalizationException {
        final Canonicalizer20010315OmitComments canonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
        if (outputStream != null) {
            canonicalizer20010315OmitComments.setWriter(outputStream);
        }
        final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(canonicalizer20010315OmitComments.engineCanonicalize(xmlSignatureInput));
        if (outputStream != null) {
            xmlSignatureInput2.setOutputStream(outputStream);
        }
        return xmlSignatureInput2;
    }
}
