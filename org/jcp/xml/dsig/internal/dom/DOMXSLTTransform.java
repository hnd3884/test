package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMXSLTTransform extends ApacheTransform
{
    public void init(final TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("params are required");
        }
        if (!(params instanceof XSLTTransformParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unrecognized params");
        }
        super.params = params;
    }
    
    public void init(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws InvalidAlgorithmParameterException {
        super.init(xmlStructure, xmlCryptoContext);
        this.unmarshalParams(DOMUtils.getFirstChildElement(super.transformElem));
    }
    
    private void unmarshalParams(final Element element) {
        super.params = new XSLTTransformParameterSpec(new DOMStructure(element));
    }
    
    public void marshalParams(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        super.marshalParams(xmlStructure, xmlCryptoContext);
        DOMUtils.appendChild(super.transformElem, ((DOMStructure)((XSLTTransformParameterSpec)this.getParameterSpec()).getStylesheet()).getNode());
    }
}
