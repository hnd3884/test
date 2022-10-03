package com.sun.org.apache.xml.internal.security.transforms.implementations;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.DOMException;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import org.w3c.dom.NodeList;
import java.util.List;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import com.sun.org.apache.xml.internal.security.transforms.params.XPath2FilterContainer;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.ArrayList;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformXPath2Filter extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2002/06/xmldsig-filter2";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2002/06/xmldsig-filter2";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws TransformationException {
        try {
            final ArrayList list = new ArrayList();
            final ArrayList list2 = new ArrayList();
            final ArrayList list3 = new ArrayList();
            final Element[] selectNodes = XMLUtils.selectNodes(transform.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath");
            if (selectNodes.length == 0) {
                throw new TransformationException("xml.WrongContent", new Object[] { "http://www.w3.org/2002/06/xmldsig-filter2", "XPath" });
            }
            Document document;
            if (xmlSignatureInput.getSubNode() != null) {
                document = XMLUtils.getOwnerDocument(xmlSignatureInput.getSubNode());
            }
            else {
                document = XMLUtils.getOwnerDocument(xmlSignatureInput.getNodeSet());
            }
            for (int i = 0; i < selectNodes.length; ++i) {
                final XPath2FilterContainer instance = XPath2FilterContainer.newInstance(selectNodes[i], xmlSignatureInput.getSourceURI());
                final NodeList selectNodeList = XPathFactory.newInstance().newXPathAPI().selectNodeList(document, instance.getXPathFilterTextNode(), XMLUtils.getStrFromNode(instance.getXPathFilterTextNode()), instance.getElement());
                if (instance.isIntersect()) {
                    list3.add(selectNodeList);
                }
                else if (instance.isSubtract()) {
                    list2.add(selectNodeList);
                }
                else if (instance.isUnion()) {
                    list.add(selectNodeList);
                }
            }
            xmlSignatureInput.addNodeFilter(new XPath2NodeFilter(list, list2, list3));
            xmlSignatureInput.setNodeSet(true);
            return xmlSignatureInput;
        }
        catch (final TransformerException ex) {
            throw new TransformationException(ex);
        }
        catch (final DOMException ex2) {
            throw new TransformationException(ex2);
        }
        catch (final CanonicalizationException ex3) {
            throw new TransformationException(ex3);
        }
        catch (final InvalidCanonicalizerException ex4) {
            throw new TransformationException(ex4);
        }
        catch (final XMLSecurityException ex5) {
            throw new TransformationException(ex5);
        }
        catch (final SAXException ex6) {
            throw new TransformationException(ex6);
        }
        catch (final IOException ex7) {
            throw new TransformationException(ex7);
        }
        catch (final ParserConfigurationException ex8) {
            throw new TransformationException(ex8);
        }
    }
}
