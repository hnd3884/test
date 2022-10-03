package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformXPointer extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/2001/WD-xptr-20010108";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/WD-xptr-20010108";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws TransformationException {
        throw new TransformationException("signature.Transform.NotYetImplemented", new Object[] { "http://www.w3.org/TR/2001/WD-xptr-20010108" });
    }
}
