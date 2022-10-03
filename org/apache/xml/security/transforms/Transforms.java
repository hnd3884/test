package org.apache.xml.security.transforms;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.DOMException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class Transforms extends SignatureElementProxy
{
    static Log log;
    public static final String TRANSFORM_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String TRANSFORM_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
    public static final String TRANSFORM_BASE64_DECODE = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    public static final String TRANSFORM_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
    public static final String TRANSFORM_XPATH2FILTER04 = "http://www.w3.org/2002/04/xmldsig-filter2";
    public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
    public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
    Element[] transforms;
    
    protected Transforms() {
    }
    
    public Transforms(final Document document) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public Transforms(final Element element, final String s) throws DOMException, XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, s);
        if (this.getLength() == 0) {
            throw new TransformationException("xml.WrongContent", new Object[] { "Transform", "Transforms" });
        }
    }
    
    public void addTransform(final String s) throws TransformationException {
        try {
            if (Transforms.log.isDebugEnabled()) {
                Transforms.log.debug((Object)("Transforms.addTransform(" + s + ")"));
            }
            this.addTransform(Transform.getInstance(super._doc, s));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }
    
    public void addTransform(final String s, final Element element) throws TransformationException {
        try {
            if (Transforms.log.isDebugEnabled()) {
                Transforms.log.debug((Object)("Transforms.addTransform(" + s + ")"));
            }
            this.addTransform(Transform.getInstance(super._doc, s, element));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }
    
    public void addTransform(final String s, final NodeList list) throws TransformationException {
        try {
            this.addTransform(Transform.getInstance(super._doc, s, list));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }
    
    private void addTransform(final Transform transform) {
        if (Transforms.log.isDebugEnabled()) {
            Transforms.log.debug((Object)("Transforms.addTransform(" + transform.getURI() + ")"));
        }
        super._constructionElement.appendChild(transform.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public XMLSignatureInput performTransforms(final XMLSignatureInput xmlSignatureInput) throws TransformationException {
        return this.performTransforms(xmlSignatureInput, null);
    }
    
    public XMLSignatureInput performTransforms(XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws TransformationException {
        try {
            final int n = this.getLength() - 1;
            for (int i = 0; i < n; ++i) {
                final Transform item = this.item(i);
                if (Transforms.log.isDebugEnabled()) {
                    Transforms.log.debug((Object)("Preform the (" + i + ")th " + item.getURI() + " transform"));
                }
                xmlSignatureInput = item.performTransform(xmlSignatureInput);
            }
            if (n >= 0) {
                xmlSignatureInput = this.item(n).performTransform(xmlSignatureInput, outputStream);
            }
            return xmlSignatureInput;
        }
        catch (final IOException ex) {
            throw new TransformationException("empty", ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new TransformationException("empty", ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new TransformationException("empty", ex3);
        }
    }
    
    public int getLength() {
        if (this.transforms == null) {
            this.transforms = XMLUtils.selectDsNodes(super._constructionElement.getFirstChild(), "Transform");
        }
        return this.transforms.length;
    }
    
    public Transform item(final int n) throws TransformationException {
        try {
            if (this.transforms == null) {
                this.transforms = XMLUtils.selectDsNodes(super._constructionElement.getFirstChild(), "Transform");
            }
            return new Transform(this.transforms[n], super._baseURI);
        }
        catch (final XMLSecurityException ex) {
            throw new TransformationException("empty", ex);
        }
    }
    
    public String getBaseLocalName() {
        return "Transforms";
    }
    
    static {
        Transforms.log = LogFactory.getLog(Transforms.class.getName());
    }
}
