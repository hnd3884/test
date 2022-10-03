package org.apache.xml.security.transforms.implementations;

import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Element;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformBase64Decode extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#base64";
    
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#base64";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws IOException, CanonicalizationException, TransformationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, CanonicalizationException, TransformationException {
        try {
            if (xmlSignatureInput.isElement()) {
                Node node = xmlSignatureInput.getSubNode();
                if (xmlSignatureInput.getSubNode().getNodeType() == 3) {
                    node = node.getParentNode();
                }
                final StringBuffer sb = new StringBuffer();
                this.traverseElement((Element)node, sb);
                if (outputStream == null) {
                    return new XMLSignatureInput(Base64.decode(sb.toString()));
                }
                Base64.decode(sb.toString(), outputStream);
                final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput((byte[])null);
                xmlSignatureInput2.setOutputStream(outputStream);
                return xmlSignatureInput2;
            }
            else if (xmlSignatureInput.isOctetStream() || xmlSignatureInput.isNodeSet()) {
                if (outputStream == null) {
                    return new XMLSignatureInput(Base64.decode(xmlSignatureInput.getBytes()));
                }
                if (xmlSignatureInput.isByteArray() || xmlSignatureInput.isNodeSet()) {
                    Base64.decode(xmlSignatureInput.getBytes(), outputStream);
                }
                else {
                    Base64.decode(new BufferedInputStream(xmlSignatureInput.getOctetStreamReal()), outputStream);
                }
                final XMLSignatureInput xmlSignatureInput3 = new XMLSignatureInput((byte[])null);
                xmlSignatureInput3.setOutputStream(outputStream);
                return xmlSignatureInput3;
            }
            else {
                try {
                    final Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlSignatureInput.getOctetStream()).getDocumentElement();
                    final StringBuffer sb2 = new StringBuffer();
                    this.traverseElement(documentElement, sb2);
                    return new XMLSignatureInput(Base64.decode(sb2.toString()));
                }
                catch (final ParserConfigurationException ex) {
                    throw new TransformationException("c14n.Canonicalizer.Exception", ex);
                }
                catch (final SAXException ex2) {
                    throw new TransformationException("SAX exception", ex2);
                }
            }
        }
        catch (final Base64DecodingException ex3) {
            throw new TransformationException("Base64Decoding", ex3);
        }
    }
    
    void traverseElement(final Element element, final StringBuffer sb) {
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case 1: {
                    this.traverseElement((Element)node, sb);
                    break;
                }
                case 3: {
                    sb.append(((Text)node).getData());
                    break;
                }
            }
        }
    }
}
