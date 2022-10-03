package org.apache.xml.security.transforms.implementations;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.w3c.dom.DOMException;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.signature.NodeFilter;
import java.util.List;
import org.w3c.dom.Node;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import java.util.ArrayList;
import org.apache.xml.security.utils.CachedXPathAPIHolder;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformXPath2Filter extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2002/06/xmldsig-filter2";
    
    protected String engineGetURI() {
        return "http://www.w3.org/2002/06/xmldsig-filter2";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws TransformationException {
        CachedXPathAPIHolder.setDoc(transform.getElement().getOwnerDocument());
        try {
            final ArrayList list = new ArrayList();
            final ArrayList list2 = new ArrayList();
            final ArrayList list3 = new ArrayList();
            final CachedXPathFuncHereAPI cachedXPathFuncHereAPI = new CachedXPathFuncHereAPI(CachedXPathAPIHolder.getCachedXPathAPI());
            final int length = XMLUtils.selectNodes(transform.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath").length;
            if (length == 0) {
                throw new TransformationException("xml.WrongContent", new Object[] { "http://www.w3.org/2002/06/xmldsig-filter2", "XPath" });
            }
            Document document;
            if (xmlSignatureInput.getSubNode() != null) {
                document = XMLUtils.getOwnerDocument(xmlSignatureInput.getSubNode());
            }
            else {
                document = XMLUtils.getOwnerDocument(xmlSignatureInput.getNodeSet());
            }
            for (int i = 0; i < length; ++i) {
                final XPath2FilterContainer instance = XPath2FilterContainer.newInstance(XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath", i), xmlSignatureInput.getSourceURI());
                final NodeList selectNodeList = cachedXPathFuncHereAPI.selectNodeList(document, instance.getXPathFilterTextNode(), CachedXPathFuncHereAPI.getStrFromNode(instance.getXPathFilterTextNode()), instance.getElement());
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
            xmlSignatureInput.addNodeFilter(new XPath2NodeFilter(convertNodeListToSet(list), convertNodeListToSet(list2), convertNodeListToSet(list3)));
            xmlSignatureInput.setNodeSet(true);
            return xmlSignatureInput;
        }
        catch (final TransformerException ex) {
            throw new TransformationException("empty", ex);
        }
        catch (final DOMException ex2) {
            throw new TransformationException("empty", ex2);
        }
        catch (final CanonicalizationException ex3) {
            throw new TransformationException("empty", ex3);
        }
        catch (final InvalidCanonicalizerException ex4) {
            throw new TransformationException("empty", ex4);
        }
        catch (final XMLSecurityException ex5) {
            throw new TransformationException("empty", ex5);
        }
        catch (final SAXException ex6) {
            throw new TransformationException("empty", ex6);
        }
        catch (final IOException ex7) {
            throw new TransformationException("empty", ex7);
        }
        catch (final ParserConfigurationException ex8) {
            throw new TransformationException("empty", ex8);
        }
    }
    
    static Set convertNodeListToSet(final List list) {
        final HashSet set = new HashSet();
        for (int i = 0; i < list.size(); ++i) {
            final NodeList list2 = list.get(i);
            for (int length = list2.getLength(), j = 0; j < length; ++j) {
                set.add(list2.item(j));
            }
        }
        return set;
    }
}
