package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformC14N11_WithComments extends TransformSpi
{
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws CanonicalizationException {
        final Canonicalizer11_WithComments canonicalizer11_WithComments = new Canonicalizer11_WithComments();
        canonicalizer11_WithComments.setSecureValidation(this.secureValidation);
        if (outputStream != null) {
            canonicalizer11_WithComments.setWriter(outputStream);
        }
        final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(canonicalizer11_WithComments.engineCanonicalize(xmlSignatureInput));
        xmlSignatureInput2.setSecureValidation(this.secureValidation);
        if (outputStream != null) {
            xmlSignatureInput2.setOutputStream(outputStream);
        }
        return xmlSignatureInput2;
    }
}
