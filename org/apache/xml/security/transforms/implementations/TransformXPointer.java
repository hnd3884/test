package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformXPointer extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/2001/WD-xptr-20010108";
    
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/WD-xptr-20010108";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws TransformationException {
        throw new TransformationException("signature.Transform.NotYetImplemented", new Object[] { "http://www.w3.org/TR/2001/WD-xptr-20010108" });
    }
}
