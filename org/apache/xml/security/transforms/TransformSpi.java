package org.apache.xml.security.transforms;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.signature.XMLSignatureInput;

public abstract class TransformSpi
{
    protected Transform _transformObject;
    
    public TransformSpi() {
        this._transformObject = null;
    }
    
    protected void setTransform(final Transform transformObject) {
        this._transformObject = transformObject;
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        return this.enginePerformTransform(xmlSignatureInput, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        try {
            final TransformSpi transformSpi = (TransformSpi)this.getClass().newInstance();
            transformSpi.setTransform(transform);
            return transformSpi.enginePerformTransform(xmlSignatureInput);
        }
        catch (final InstantiationException ex) {
            throw new TransformationException("", ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new TransformationException("", ex2);
        }
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
        throw new UnsupportedOperationException();
    }
    
    protected abstract String engineGetURI();
}
