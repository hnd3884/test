package org.apache.xml.security.signature;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.DigesterOutputStream;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.transforms.InvalidTransformException;
import java.util.Set;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import java.util.HashSet;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.w3c.dom.Attr;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import java.util.Map;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.transforms.Transforms;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class Reference extends SignatureElementProxy
{
    public static final boolean CacheSignedNodes = false;
    static Log log;
    public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
    public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
    Manifest _manifest;
    XMLSignatureInput _transformsOutput;
    private Transforms transforms;
    private Element digestMethodElem;
    private Element digestValueElement;
    
    protected Reference(final Document document, final String baseURI, final String uri, final Manifest manifest, final Transforms transforms, final String s) throws XMLSignatureException {
        super(document);
        this._manifest = null;
        XMLUtils.addReturnToElement(super._constructionElement);
        super._baseURI = baseURI;
        this._manifest = manifest;
        this.setURI(uri);
        if (transforms != null) {
            this.transforms = transforms;
            super._constructionElement.appendChild(transforms.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
        this.digestMethodElem = MessageDigestAlgorithm.getInstance(super._doc, s).getElement();
        super._constructionElement.appendChild(this.digestMethodElem);
        XMLUtils.addReturnToElement(super._constructionElement);
        this.digestValueElement = XMLUtils.createElementInSignatureSpace(super._doc, "DigestValue");
        super._constructionElement.appendChild(this.digestValueElement);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    protected Reference(final Element element, final String baseURI, final Manifest manifest) throws XMLSecurityException {
        super(element, baseURI);
        this._manifest = null;
        super._baseURI = baseURI;
        Element digestMethodElem = XMLUtils.getNextElement(element.getFirstChild());
        if ("Transforms".equals(digestMethodElem.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(digestMethodElem.getNamespaceURI())) {
            this.transforms = new Transforms(digestMethodElem, super._baseURI);
            digestMethodElem = XMLUtils.getNextElement(digestMethodElem.getNextSibling());
        }
        this.digestMethodElem = digestMethodElem;
        this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
        this._manifest = manifest;
    }
    
    public MessageDigestAlgorithm getMessageDigestAlgorithm() throws XMLSignatureException {
        if (this.digestMethodElem == null) {
            return null;
        }
        final String attributeNS = this.digestMethodElem.getAttributeNS(null, "Algorithm");
        if (attributeNS == null) {
            return null;
        }
        return MessageDigestAlgorithm.getInstance(super._doc, attributeNS);
    }
    
    public void setURI(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "URI", s);
        }
    }
    
    public String getURI() {
        return super._constructionElement.getAttributeNS(null, "URI");
    }
    
    public void setId(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Id", s);
            IdResolver.registerElementById(super._constructionElement, s);
        }
    }
    
    public String getId() {
        return super._constructionElement.getAttributeNS(null, "Id");
    }
    
    public void setType(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Type", s);
        }
    }
    
    public String getType() {
        return super._constructionElement.getAttributeNS(null, "Type");
    }
    
    public boolean typeIsReferenceToObject() {
        return "http://www.w3.org/2000/09/xmldsig#Object".equals(this.getType());
    }
    
    public boolean typeIsReferenceToManifest() {
        return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(this.getType());
    }
    
    private void setDigestValueElement(final byte[] array) {
        if (super._state == 0) {
            for (Node node = this.digestValueElement.getFirstChild(); node != null; node = node.getNextSibling()) {
                this.digestValueElement.removeChild(node);
            }
            this.digestValueElement.appendChild(super._doc.createTextNode(Base64.encode(array)));
        }
    }
    
    public void generateDigestValue() throws XMLSignatureException, ReferenceNotInitializedException {
        if (super._state == 0) {
            this.setDigestValueElement(this.calculateDigest());
        }
    }
    
    public XMLSignatureInput getContentsBeforeTransformation() throws ReferenceNotInitializedException {
        try {
            final Attr attributeNodeNS = super._constructionElement.getAttributeNodeNS(null, "URI");
            Object nodeValue;
            if (attributeNodeNS == null) {
                nodeValue = null;
            }
            else {
                nodeValue = attributeNodeNS.getNodeValue();
            }
            final ResourceResolver instance = ResourceResolver.getInstance(attributeNodeNS, super._baseURI, this._manifest._perManifestResolvers);
            if (instance == null) {
                throw new ReferenceNotInitializedException("signature.Verification.Reference.NoInput", new Object[] { nodeValue });
            }
            instance.addProperties(this._manifest._resolverProperties);
            return instance.resolve(attributeNodeNS, super._baseURI);
        }
        catch (final ResourceResolverException ex) {
            throw new ReferenceNotInitializedException("empty", ex);
        }
        catch (final XMLSecurityException ex2) {
            throw new ReferenceNotInitializedException("empty", ex2);
        }
    }
    
    public XMLSignatureInput getTransformsInput() throws ReferenceNotInitializedException {
        final XMLSignatureInput contentsBeforeTransformation = this.getContentsBeforeTransformation();
        XMLSignatureInput xmlSignatureInput;
        try {
            xmlSignatureInput = new XMLSignatureInput(contentsBeforeTransformation.getBytes());
        }
        catch (final CanonicalizationException ex) {
            throw new ReferenceNotInitializedException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new ReferenceNotInitializedException("empty", ex2);
        }
        xmlSignatureInput.setSourceURI(contentsBeforeTransformation.getSourceURI());
        return xmlSignatureInput;
    }
    
    private XMLSignatureInput getContentsAfterTransformation(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws XMLSignatureException {
        try {
            final Transforms transforms = this.getTransforms();
            XMLSignatureInput performTransforms;
            if (transforms != null) {
                performTransforms = transforms.performTransforms(xmlSignatureInput, outputStream);
                this._transformsOutput = performTransforms;
            }
            else {
                performTransforms = xmlSignatureInput;
            }
            return performTransforms;
        }
        catch (final ResourceResolverException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException("empty", ex3);
        }
        catch (final TransformationException ex4) {
            throw new XMLSignatureException("empty", ex4);
        }
        catch (final XMLSecurityException ex5) {
            throw new XMLSignatureException("empty", ex5);
        }
    }
    
    public XMLSignatureInput getContentsAfterTransformation() throws XMLSignatureException {
        return this.getContentsAfterTransformation(this.getContentsBeforeTransformation(), null);
    }
    
    public XMLSignatureInput getNodesetBeforeFirstCanonicalization() throws XMLSignatureException {
        try {
            XMLSignatureInput xmlSignatureInput2;
            final XMLSignatureInput xmlSignatureInput = xmlSignatureInput2 = this.getContentsBeforeTransformation();
            final Transforms transforms = this.getTransforms();
            if (transforms != null) {
                for (int i = 0; i < transforms.getLength(); ++i) {
                    final Transform item = transforms.item(i);
                    final String uri = item.getURI();
                    if (uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") || uri.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) {
                        break;
                    }
                    if (uri.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) {
                        break;
                    }
                    xmlSignatureInput2 = item.performTransform(xmlSignatureInput2, null);
                }
                xmlSignatureInput2.setSourceURI(xmlSignatureInput.getSourceURI());
            }
            return xmlSignatureInput2;
        }
        catch (final IOException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final ResourceResolverException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
        catch (final CanonicalizationException ex3) {
            throw new XMLSignatureException("empty", ex3);
        }
        catch (final InvalidCanonicalizerException ex4) {
            throw new XMLSignatureException("empty", ex4);
        }
        catch (final TransformationException ex5) {
            throw new XMLSignatureException("empty", ex5);
        }
        catch (final XMLSecurityException ex6) {
            throw new XMLSignatureException("empty", ex6);
        }
    }
    
    public String getHTMLRepresentation() throws XMLSignatureException {
        try {
            final XMLSignatureInput nodesetBeforeFirstCanonicalization = this.getNodesetBeforeFirstCanonicalization();
            Object prefixStr2Set = new HashSet();
            final Transforms transforms = this.getTransforms();
            ElementProxy elementProxy = null;
            if (transforms != null) {
                for (int i = 0; i < transforms.getLength(); ++i) {
                    final Transform item = transforms.item(i);
                    final String uri = item.getURI();
                    if (uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
                        elementProxy = item;
                        break;
                    }
                }
            }
            if (elementProxy != null && elementProxy.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                prefixStr2Set = InclusiveNamespaces.prefixStr2Set(new InclusiveNamespaces(XMLUtils.selectNode(elementProxy.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), this.getBaseURI()).getInclusiveNamespaces());
            }
            return nodesetBeforeFirstCanonicalization.getHTMLRepresentation((Set)prefixStr2Set);
        }
        catch (final TransformationException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final InvalidTransformException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
        catch (final XMLSecurityException ex3) {
            throw new XMLSignatureException("empty", ex3);
        }
    }
    
    public XMLSignatureInput getTransformsOutput() {
        return this._transformsOutput;
    }
    
    protected XMLSignatureInput dereferenceURIandPerformTransforms(final OutputStream outputStream) throws XMLSignatureException {
        try {
            return this._transformsOutput = this.getContentsAfterTransformation(this.getContentsBeforeTransformation(), outputStream);
        }
        catch (final XMLSecurityException ex) {
            throw new ReferenceNotInitializedException("empty", ex);
        }
    }
    
    public Transforms getTransforms() throws XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException {
        return this.transforms;
    }
    
    public byte[] getReferencedBytes() throws ReferenceNotInitializedException, XMLSignatureException {
        try {
            return this.dereferenceURIandPerformTransforms(null).getBytes();
        }
        catch (final IOException ex) {
            throw new ReferenceNotInitializedException("empty", ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new ReferenceNotInitializedException("empty", ex2);
        }
    }
    
    private byte[] calculateDigest() throws ReferenceNotInitializedException, XMLSignatureException {
        try {
            final MessageDigestAlgorithm messageDigestAlgorithm = this.getMessageDigestAlgorithm();
            messageDigestAlgorithm.reset();
            final DigesterOutputStream digesterOutputStream = new DigesterOutputStream(messageDigestAlgorithm);
            final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(digesterOutputStream);
            this.dereferenceURIandPerformTransforms(unsyncBufferedOutputStream).updateOutputStream(unsyncBufferedOutputStream);
            unsyncBufferedOutputStream.flush();
            return digesterOutputStream.getDigestValue();
        }
        catch (final XMLSecurityException ex) {
            throw new ReferenceNotInitializedException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new ReferenceNotInitializedException("empty", ex2);
        }
    }
    
    public byte[] getDigestValue() throws Base64DecodingException, XMLSecurityException {
        if (this.digestValueElement == null) {
            throw new XMLSecurityException("signature.Verification.NoSignatureElement", new Object[] { "DigestValue", "http://www.w3.org/2000/09/xmldsig#" });
        }
        return Base64.decode(this.digestValueElement);
    }
    
    public boolean verify() throws ReferenceNotInitializedException, XMLSecurityException {
        final boolean equal = MessageDigestAlgorithm.isEqual(this.getDigestValue(), this.calculateDigest());
        if (!equal) {
            Reference.log.warn((Object)("Verification failed for URI \"" + this.getURI() + "\""));
        }
        else {
            Reference.log.info((Object)("Verification successful for URI \"" + this.getURI() + "\""));
        }
        return equal;
    }
    
    public String getBaseLocalName() {
        return "Reference";
    }
    
    static {
        Reference.log = LogFactory.getLog(Reference.class.getName());
    }
}
