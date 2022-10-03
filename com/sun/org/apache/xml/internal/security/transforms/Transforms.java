package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class Transforms extends SignatureElementProxy
{
    public static final String TRANSFORM_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String TRANSFORM_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String TRANSFORM_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String TRANSFORM_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
    public static final String TRANSFORM_BASE64_DECODE = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    public static final String TRANSFORM_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
    public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
    private static final Logger LOG;
    private Element[] transforms;
    private boolean secureValidation;
    
    protected Transforms() {
    }
    
    public Transforms(final Document document) {
        super(document);
        this.addReturnToSelf();
    }
    
    public Transforms(final Element element, final String s) throws DOMException, XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, s);
        if (this.getLength() == 0) {
            throw new TransformationException("xml.WrongContent", new Object[] { "Transform", "Transforms" });
        }
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
    
    public void addTransform(final String s) throws TransformationException {
        try {
            Transforms.LOG.debug("Transforms.addTransform({})", s);
            this.addTransform(new Transform(this.getDocument(), s));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException(ex);
        }
    }
    
    public void addTransform(final String s, final Element element) throws TransformationException {
        try {
            Transforms.LOG.debug("Transforms.addTransform({})", s);
            this.addTransform(new Transform(this.getDocument(), s, element));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException(ex);
        }
    }
    
    public void addTransform(final String s, final NodeList list) throws TransformationException {
        try {
            this.addTransform(new Transform(this.getDocument(), s, list));
        }
        catch (final InvalidTransformException ex) {
            throw new TransformationException(ex);
        }
    }
    
    private void addTransform(final Transform transform) {
        Transforms.LOG.debug("Transforms.addTransform({})", transform.getURI());
        this.appendSelf(transform.getElement());
        this.addReturnToSelf();
    }
    
    public XMLSignatureInput performTransforms(final XMLSignatureInput xmlSignatureInput) throws TransformationException {
        return this.performTransforms(xmlSignatureInput, null);
    }
    
    public XMLSignatureInput performTransforms(XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws TransformationException {
        try {
            final int n = this.getLength() - 1;
            for (int i = 0; i < n; ++i) {
                final Transform item = this.item(i);
                Transforms.LOG.debug("Perform the ({})th {} transform", i, item.getURI());
                this.checkSecureValidation(item);
                xmlSignatureInput = item.performTransform(xmlSignatureInput);
            }
            if (n >= 0) {
                final Transform item2 = this.item(n);
                Transforms.LOG.debug("Perform the ({})th {} transform", n, item2.getURI());
                this.checkSecureValidation(item2);
                xmlSignatureInput = item2.performTransform(xmlSignatureInput, outputStream);
            }
            return xmlSignatureInput;
        }
        catch (final IOException ex) {
            throw new TransformationException(ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new TransformationException(ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new TransformationException(ex3);
        }
    }
    
    private void checkSecureValidation(final Transform transform) throws TransformationException {
        final String uri = transform.getURI();
        if (this.secureValidation && "http://www.w3.org/TR/1999/REC-xslt-19991116".equals(uri)) {
            throw new TransformationException("signature.Transform.ForbiddenTransform", new Object[] { uri });
        }
        transform.setSecureValidation(this.secureValidation);
    }
    
    public int getLength() {
        this.initTransforms();
        return this.transforms.length;
    }
    
    public Transform item(final int n) throws TransformationException {
        try {
            this.initTransforms();
            return new Transform(this.transforms[n], this.baseURI);
        }
        catch (final XMLSecurityException ex) {
            throw new TransformationException(ex);
        }
    }
    
    private void initTransforms() {
        if (this.transforms == null) {
            this.transforms = XMLUtils.selectDsNodes(this.getFirstChild(), "Transform");
        }
    }
    
    @Override
    public String getBaseLocalName() {
        return "Transforms";
    }
    
    static {
        LOG = LoggerFactory.getLogger(Transforms.class);
    }
}
