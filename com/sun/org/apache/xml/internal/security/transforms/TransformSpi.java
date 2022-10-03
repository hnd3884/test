package com.sun.org.apache.xml.internal.security.transforms;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;

public abstract class TransformSpi
{
    protected boolean secureValidation;
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        throw new UnsupportedOperationException();
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        return this.enginePerformTransform(xmlSignatureInput, null);
    }
    
    protected abstract String engineGetURI();
}
