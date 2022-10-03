package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformC14NWithComments extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws CanonicalizationException {
        final Canonicalizer20010315WithComments canonicalizer20010315WithComments = new Canonicalizer20010315WithComments();
        canonicalizer20010315WithComments.setSecureValidation(this.secureValidation);
        if (outputStream != null) {
            canonicalizer20010315WithComments.setWriter(outputStream);
        }
        final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(canonicalizer20010315WithComments.engineCanonicalize(xmlSignatureInput));
        xmlSignatureInput2.setSecureValidation(this.secureValidation);
        if (outputStream != null) {
            xmlSignatureInput2.setOutputStream(outputStream);
        }
        return xmlSignatureInput2;
    }
}
