package com.sun.org.apache.xml.internal.security.transforms.implementations;

import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformBase64Decode extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#base64";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#base64";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws IOException, CanonicalizationException, TransformationException {
        return this.enginePerformTransform(xmlSignatureInput, null, transform);
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws IOException, CanonicalizationException, TransformationException {
        if (xmlSignatureInput.isElement()) {
            Node node = xmlSignatureInput.getSubNode();
            if (xmlSignatureInput.getSubNode().getNodeType() == 3) {
                node = node.getParentNode();
            }
            final StringBuilder sb = new StringBuilder();
            this.traverseElement((Element)node, sb);
            if (outputStream == null) {
                final XMLSignatureInput xmlSignatureInput2 = new XMLSignatureInput(XMLUtils.decode(sb.toString()));
                xmlSignatureInput2.setSecureValidation(this.secureValidation);
                return xmlSignatureInput2;
            }
            outputStream.write(XMLUtils.decode(sb.toString()));
            final XMLSignatureInput xmlSignatureInput3 = new XMLSignatureInput((byte[])null);
            xmlSignatureInput3.setSecureValidation(this.secureValidation);
            xmlSignatureInput3.setOutputStream(outputStream);
            return xmlSignatureInput3;
        }
        else if (xmlSignatureInput.isOctetStream() || xmlSignatureInput.isNodeSet()) {
            if (outputStream == null) {
                final XMLSignatureInput xmlSignatureInput4 = new XMLSignatureInput(XMLUtils.decode(xmlSignatureInput.getBytes()));
                xmlSignatureInput4.setSecureValidation(this.secureValidation);
                return xmlSignatureInput4;
            }
            if (xmlSignatureInput.isByteArray() || xmlSignatureInput.isNodeSet()) {
                outputStream.write(XMLUtils.decode(xmlSignatureInput.getBytes()));
            }
            else {
                outputStream.write(XMLUtils.decode(JavaUtils.getBytesFromStream(xmlSignatureInput.getOctetStreamReal())));
            }
            final XMLSignatureInput xmlSignatureInput5 = new XMLSignatureInput((byte[])null);
            xmlSignatureInput5.setSecureValidation(this.secureValidation);
            xmlSignatureInput5.setOutputStream(outputStream);
            return xmlSignatureInput5;
        }
        else {
            try {
                final Element documentElement = XMLUtils.createDocumentBuilder(false, this.secureValidation).parse(xmlSignatureInput.getOctetStream()).getDocumentElement();
                final StringBuilder sb2 = new StringBuilder();
                this.traverseElement(documentElement, sb2);
                final XMLSignatureInput xmlSignatureInput6 = new XMLSignatureInput(XMLUtils.decode(sb2.toString()));
                xmlSignatureInput6.setSecureValidation(this.secureValidation);
                return xmlSignatureInput6;
            }
            catch (final ParserConfigurationException ex) {
                throw new TransformationException(ex, "c14n.Canonicalizer.Exception");
            }
            catch (final SAXException ex2) {
                throw new TransformationException(ex2, "SAX exception");
            }
        }
    }
    
    void traverseElement(final Element element, final StringBuilder sb) {
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
