package org.jcp.xml.dsig.internal.dom;

import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import javax.xml.crypto.dsig.TransformException;
import org.apache.xml.security.c14n.Canonicalizer;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMExcC14NMethod extends ApacheCanonicalizer
{
    public void init(final TransformParameterSpec transformParameterSpec) throws InvalidAlgorithmParameterException {
        if (transformParameterSpec != null) {
            if (!(transformParameterSpec instanceof ExcC14NParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type ExcC14NParameterSpec");
            }
            super.params = (C14NMethodParameterSpec)transformParameterSpec;
        }
    }
    
    public void init(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws InvalidAlgorithmParameterException {
        super.init(xmlStructure, xmlCryptoContext);
        final Element firstChildElement = DOMUtils.getFirstChildElement(super.transformElem);
        if (firstChildElement == null) {
            super.params = null;
            super.inclusiveNamespaces = null;
            return;
        }
        this.unmarshalParams(firstChildElement);
    }
    
    private void unmarshalParams(final Element element) {
        final String attributeNS = element.getAttributeNS(null, "PrefixList");
        super.inclusiveNamespaces = attributeNS;
        int n = 0;
        int i = attributeNS.indexOf(32);
        final ArrayList list = new ArrayList();
        while (i != -1) {
            list.add(attributeNS.substring(n, i));
            n = i + 1;
            i = attributeNS.indexOf(32, n);
        }
        if (n <= attributeNS.length()) {
            list.add(attributeNS.substring(n));
        }
        super.params = new ExcC14NParameterSpec(list);
    }
    
    public void marshalParams(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        super.marshalParams(xmlStructure, xmlCryptoContext);
        final AlgorithmParameterSpec parameterSpec = this.getParameterSpec();
        if (parameterSpec == null) {
            return;
        }
        final String nsPrefix = DOMUtils.getNSPrefix(xmlCryptoContext, "http://www.w3.org/2001/10/xml-exc-c14n#");
        final Element element = DOMUtils.createElement(super.ownerDoc, "InclusiveNamespaces", "http://www.w3.org/2001/10/xml-exc-c14n#", nsPrefix);
        if (nsPrefix == null) {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2001/10/xml-exc-c14n#");
        }
        else {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + nsPrefix, "http://www.w3.org/2001/10/xml-exc-c14n#");
        }
        final ExcC14NParameterSpec excC14NParameterSpec = (ExcC14NParameterSpec)parameterSpec;
        final StringBuffer sb = new StringBuffer("");
        final List prefixList = excC14NParameterSpec.getPrefixList();
        for (int i = 0, size = prefixList.size(); i < size; ++i) {
            sb.append((String)prefixList.get(i));
            if (i < size - 1) {
                sb.append(" ");
            }
        }
        DOMUtils.setAttribute(element, "PrefixList", sb.toString());
        super.inclusiveNamespaces = sb.toString();
        super.transformElem.appendChild(element);
    }
    
    public String getParamsNSURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        if (data instanceof DOMSubTreeData && ((DOMSubTreeData)data).excludeComments()) {
            try {
                super.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2001/10/xml-exc-c14n#");
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2001/10/xml-exc-c14n#: " + ex.getMessage(), ex);
            }
        }
        return this.canonicalize(data, xmlCryptoContext);
    }
}
