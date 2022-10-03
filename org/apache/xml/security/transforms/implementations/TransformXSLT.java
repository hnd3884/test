package org.apache.xml.security.transforms.implementations;

import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import javax.xml.transform.TransformerFactory;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.transforms.TransformationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformXSLT extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
    static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
    static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET = "stylesheet";
    
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xslt-19991116";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws IOException, TransformationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, TransformationException {
        try {
            final Element selectNode = XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
            if (selectNode == null) {
                throw new TransformationException("xml.WrongContent", new Object[] { "xslt:stylesheet", "Transform" });
            }
            final TransformerFactory instance = TransformerFactory.newInstance();
            final StreamSource streamSource = new StreamSource(new ByteArrayInputStream(xmlSignatureInput.getBytes()));
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            instance.newTransformer().transform(new DOMSource(selectNode), new StreamResult(byteArrayOutputStream));
            final Transformer transformer = instance.newTransformer(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            if (outputStream == null) {
                final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                transformer.transform(streamSource, new StreamResult(byteArrayOutputStream2));
                return new XMLSignatureInput(byteArrayOutputStream2.toByteArray());
            }
            transformer.transform(streamSource, new StreamResult(outputStream));
            final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput((byte[])null);
            xmlSignatureInput2.setOutputStream(outputStream);
            return xmlSignatureInput2;
        }
        catch (final XMLSecurityException ex) {
            throw new TransformationException("generic.EmptyMessage", new Object[] { ex.getMessage() }, ex);
        }
        catch (final TransformerConfigurationException ex2) {
            throw new TransformationException("generic.EmptyMessage", new Object[] { ex2.getMessage() }, ex2);
        }
        catch (final TransformerException ex3) {
            throw new TransformationException("generic.EmptyMessage", new Object[] { ex3.getMessage() }, ex3);
        }
    }
}
