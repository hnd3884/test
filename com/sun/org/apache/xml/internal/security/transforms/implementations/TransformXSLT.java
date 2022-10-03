package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.IOException;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.TransformerFactory;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformXSLT extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
    static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
    static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET = "stylesheet";
    private static final Logger LOG;
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xslt-19991116";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, TransformationException {
        try {
            final Element element = transform.getElement();
            Element n = XMLUtils.selectNode(element.getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
            if (n == null) {
                n = XMLUtils.selectNode(element.getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "transform", 0);
            }
            if (n == null) {
                throw new TransformationException("xml.WrongContent", new Object[] { "xslt:stylesheet", "Transform" });
            }
            final TransformerFactory instance = TransformerFactory.newInstance();
            instance.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            StreamSource streamSource;
            try (final ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream()) {
                instance.newTransformer().transform(new DOMSource(n), new StreamResult(outputStream2));
                streamSource = new StreamSource(new ByteArrayInputStream(outputStream2.toByteArray()));
            }
            final Transformer transformer = instance.newTransformer(streamSource);
            try {
                transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
            }
            catch (final Exception ex) {
                TransformXSLT.LOG.warn("Unable to set Xalan line-separator property: " + ex.getMessage());
            }
            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlSignatureInput.getBytes())) {
                final StreamSource streamSource2 = new StreamSource(inputStream);
                if (outputStream == null) {
                    try (final ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream()) {
                        transformer.transform(streamSource2, new StreamResult(outputStream3));
                        final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(outputStream3.toByteArray());
                        xmlSignatureInput2.setSecureValidation(this.secureValidation);
                        return xmlSignatureInput2;
                    }
                }
                transformer.transform(streamSource2, new StreamResult(outputStream));
            }
            final XMLSignatureInput xmlSignatureInput3 = new XMLSignatureInput((byte[])null);
            xmlSignatureInput3.setSecureValidation(this.secureValidation);
            xmlSignatureInput3.setOutputStream(outputStream);
            return xmlSignatureInput3;
        }
        catch (final XMLSecurityException ex2) {
            throw new TransformationException(ex2);
        }
        catch (final TransformerConfigurationException ex3) {
            throw new TransformationException(ex3);
        }
        catch (final TransformerException ex4) {
            throw new TransformationException(ex4);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransformXSLT.class);
    }
}
