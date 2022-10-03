package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.AccessController;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.DigesterOutputStream;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceOctetStreamData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceSubTreeData;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceNodeSetData;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import java.util.Set;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import java.util.HashSet;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.IOException;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.OutputStream;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.algorithms.Algorithm;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceData;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class Reference extends SignatureElementProxy
{
    public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
    public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
    public static final int MAXIMUM_TRANSFORM_COUNT = 5;
    private boolean secureValidation;
    private static boolean useC14N11;
    private static final Logger LOG;
    private Manifest manifest;
    private XMLSignatureInput transformsOutput;
    private Transforms transforms;
    private Element digestMethodElem;
    private Element digestValueElement;
    private ReferenceData referenceData;
    
    protected Reference(final Document document, final String baseURI, final String uri, final Manifest manifest, final Transforms transforms, final String s) throws XMLSignatureException {
        super(document);
        this.addReturnToSelf();
        this.baseURI = baseURI;
        this.manifest = manifest;
        this.setURI(uri);
        if (transforms != null) {
            this.appendSelf(this.transforms = transforms);
            this.addReturnToSelf();
        }
        this.appendSelf(this.digestMethodElem = new Algorithm(this.getDocument(), s) {
            @Override
            public String getBaseNamespace() {
                return "http://www.w3.org/2000/09/xmldsig#";
            }
            
            @Override
            public String getBaseLocalName() {
                return "DigestMethod";
            }
        }.getElement());
        this.addReturnToSelf();
        this.appendSelf(this.digestValueElement = XMLUtils.createElementInSignatureSpace(this.getDocument(), "DigestValue"));
        this.addReturnToSelf();
    }
    
    protected Reference(final Element element, final String s, final Manifest manifest) throws XMLSecurityException {
        this(element, s, manifest, true);
    }
    
    protected Reference(final Element element, final String baseURI, final Manifest manifest, final boolean b) throws XMLSecurityException {
        super(element, baseURI);
        this.secureValidation = b;
        this.baseURI = baseURI;
        Element digestMethodElem = XMLUtils.getNextElement(element.getFirstChild());
        if (digestMethodElem != null && "Transforms".equals(digestMethodElem.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(digestMethodElem.getNamespaceURI())) {
            (this.transforms = new Transforms(digestMethodElem, this.baseURI)).setSecureValidation(b);
            if (b && this.transforms.getLength() > 5) {
                throw new XMLSecurityException("signature.tooManyTransforms", new Object[] { this.transforms.getLength(), 5 });
            }
            digestMethodElem = XMLUtils.getNextElement(digestMethodElem.getNextSibling());
        }
        this.digestMethodElem = digestMethodElem;
        if (this.digestMethodElem == null) {
            throw new XMLSecurityException("signature.Reference.NoDigestMethod");
        }
        this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
        if (this.digestValueElement == null) {
            throw new XMLSecurityException("signature.Reference.NoDigestValue");
        }
        this.manifest = manifest;
    }
    
    public MessageDigestAlgorithm getMessageDigestAlgorithm() throws XMLSignatureException {
        if (this.digestMethodElem == null) {
            return null;
        }
        final String attributeNS = this.digestMethodElem.getAttributeNS(null, "Algorithm");
        if ("".equals(attributeNS)) {
            return null;
        }
        if (this.secureValidation && "http://www.w3.org/2001/04/xmldsig-more#md5".equals(attributeNS)) {
            throw new XMLSignatureException("signature.signatureAlgorithm", new Object[] { attributeNS });
        }
        return MessageDigestAlgorithm.getInstance(this.getDocument(), attributeNS);
    }
    
    public void setURI(final String s) {
        if (s != null) {
            this.setLocalAttribute("URI", s);
        }
    }
    
    public String getURI() {
        return this.getLocalAttribute("URI");
    }
    
    public void setId(final String s) {
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public void setType(final String s) {
        if (s != null) {
            this.setLocalAttribute("Type", s);
        }
    }
    
    public String getType() {
        return this.getLocalAttribute("Type");
    }
    
    public boolean typeIsReferenceToObject() {
        return "http://www.w3.org/2000/09/xmldsig#Object".equals(this.getType());
    }
    
    public boolean typeIsReferenceToManifest() {
        return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(this.getType());
    }
    
    private void setDigestValueElement(final byte[] array) {
        for (Node node = this.digestValueElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.digestValueElement.removeChild(node);
        }
        this.digestValueElement.appendChild(this.createText(XMLUtils.encodeToString(array)));
    }
    
    public void generateDigestValue() throws XMLSignatureException, ReferenceNotInitializedException {
        this.setDigestValueElement(this.calculateDigest(false));
    }
    
    public XMLSignatureInput getContentsBeforeTransformation() throws ReferenceNotInitializedException {
        try {
            final Attr attributeNodeNS = this.getElement().getAttributeNodeNS(null, "URI");
            final ResourceResolver instance = ResourceResolver.getInstance(attributeNodeNS, this.baseURI, this.manifest.getPerManifestResolvers(), this.secureValidation);
            instance.addProperties(this.manifest.getResolverProperties());
            return instance.resolve(attributeNodeNS, this.baseURI, this.secureValidation);
        }
        catch (final ResourceResolverException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
    }
    
    private XMLSignatureInput getContentsAfterTransformation(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws XMLSignatureException {
        try {
            final Transforms transforms = this.getTransforms();
            XMLSignatureInput performTransforms;
            if (transforms != null) {
                performTransforms = transforms.performTransforms(xmlSignatureInput, outputStream);
                this.transformsOutput = performTransforms;
            }
            else {
                performTransforms = xmlSignatureInput;
            }
            return performTransforms;
        }
        catch (final ResourceResolverException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException(ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException(ex3);
        }
        catch (final TransformationException ex4) {
            throw new XMLSignatureException(ex4);
        }
        catch (final XMLSecurityException ex5) {
            throw new XMLSignatureException(ex5);
        }
    }
    
    public XMLSignatureInput getContentsAfterTransformation() throws XMLSignatureException {
        final XMLSignatureInput contentsBeforeTransformation = this.getContentsBeforeTransformation();
        this.cacheDereferencedElement(contentsBeforeTransformation);
        return this.getContentsAfterTransformation(contentsBeforeTransformation, null);
    }
    
    public XMLSignatureInput getNodesetBeforeFirstCanonicalization() throws XMLSignatureException {
        try {
            final XMLSignatureInput contentsBeforeTransformation = this.getContentsBeforeTransformation();
            this.cacheDereferencedElement(contentsBeforeTransformation);
            XMLSignatureInput performTransform = contentsBeforeTransformation;
            final Transforms transforms = this.getTransforms();
            if (transforms != null) {
                for (int i = 0; i < transforms.getLength(); ++i) {
                    final Transform item = transforms.item(i);
                    final String uri = item.getURI();
                    if (uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") || uri.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") || uri.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") || uri.equals("http://www.w3.org/2006/12/xml-c14n11")) {
                        break;
                    }
                    if (uri.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments")) {
                        break;
                    }
                    performTransform = item.performTransform(performTransform, null);
                }
                performTransform.setSourceURI(contentsBeforeTransformation.getSourceURI());
            }
            return performTransform;
        }
        catch (final IOException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final ResourceResolverException ex2) {
            throw new XMLSignatureException(ex2);
        }
        catch (final CanonicalizationException ex3) {
            throw new XMLSignatureException(ex3);
        }
        catch (final InvalidCanonicalizerException ex4) {
            throw new XMLSignatureException(ex4);
        }
        catch (final TransformationException ex5) {
            throw new XMLSignatureException(ex5);
        }
        catch (final XMLSecurityException ex6) {
            throw new XMLSignatureException(ex6);
        }
    }
    
    public String getHTMLRepresentation() throws XMLSignatureException {
        try {
            final XMLSignatureInput nodesetBeforeFirstCanonicalization = this.getNodesetBeforeFirstCanonicalization();
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
            Object prefixStr2Set = new HashSet<String>();
            if (elementProxy != null && elementProxy.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                prefixStr2Set = InclusiveNamespaces.prefixStr2Set(new InclusiveNamespaces(XMLUtils.selectNode(elementProxy.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), this.getBaseURI()).getInclusiveNamespaces());
            }
            return nodesetBeforeFirstCanonicalization.getHTMLRepresentation((Set<String>)prefixStr2Set);
        }
        catch (final TransformationException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final InvalidTransformException ex2) {
            throw new XMLSignatureException(ex2);
        }
        catch (final XMLSecurityException ex3) {
            throw new XMLSignatureException(ex3);
        }
    }
    
    public XMLSignatureInput getTransformsOutput() {
        return this.transformsOutput;
    }
    
    public ReferenceData getReferenceData() {
        return this.referenceData;
    }
    
    protected XMLSignatureInput dereferenceURIandPerformTransforms(final OutputStream outputStream) throws XMLSignatureException {
        try {
            final XMLSignatureInput contentsBeforeTransformation = this.getContentsBeforeTransformation();
            this.cacheDereferencedElement(contentsBeforeTransformation);
            return this.transformsOutput = this.getContentsAfterTransformation(contentsBeforeTransformation, outputStream);
        }
        catch (final XMLSecurityException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
    }
    
    private void cacheDereferencedElement(final XMLSignatureInput xmlSignatureInput) {
        if (xmlSignatureInput.isNodeSet()) {
            try {
                this.referenceData = new ReferenceNodeSetData() {
                    final /* synthetic */ Set val$s = xmlSignatureInput.getNodeSet();
                    
                    @Override
                    public Iterator<Node> iterator() {
                        return new Iterator<Node>() {
                            Iterator<Node> sIterator = ReferenceNodeSetData.this.val$s.iterator();
                            
                            @Override
                            public boolean hasNext() {
                                return this.sIterator.hasNext();
                            }
                            
                            @Override
                            public Node next() {
                                return this.sIterator.next();
                            }
                            
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
            }
            catch (final Exception ex) {
                Reference.LOG.warn("cannot cache dereferenced data: " + ex);
            }
        }
        else if (xmlSignatureInput.isElement()) {
            this.referenceData = new ReferenceSubTreeData(xmlSignatureInput.getSubNode(), xmlSignatureInput.isExcludeComments());
        }
        else {
            if (!xmlSignatureInput.isOctetStream()) {
                if (!xmlSignatureInput.isByteArray()) {
                    return;
                }
            }
            try {
                this.referenceData = new ReferenceOctetStreamData(xmlSignatureInput.getOctetStream(), xmlSignatureInput.getSourceURI(), xmlSignatureInput.getMIMEType());
            }
            catch (final IOException ex2) {
                Reference.LOG.warn("cannot cache dereferenced data: " + ex2);
            }
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
            throw new ReferenceNotInitializedException(ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new ReferenceNotInitializedException(ex2);
        }
    }
    
    private byte[] calculateDigest(final boolean b) throws ReferenceNotInitializedException, XMLSignatureException {
        final XMLSignatureInput contentsBeforeTransformation = this.getContentsBeforeTransformation();
        if (contentsBeforeTransformation.isPreCalculatedDigest()) {
            return this.getPreCalculatedDigest(contentsBeforeTransformation);
        }
        this.cacheDereferencedElement(contentsBeforeTransformation);
        final MessageDigestAlgorithm messageDigestAlgorithm = this.getMessageDigestAlgorithm();
        messageDigestAlgorithm.reset();
        try (final DigesterOutputStream digesterOutputStream = new DigesterOutputStream(messageDigestAlgorithm);
             final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(digesterOutputStream)) {
            final XMLSignatureInput contentsAfterTransformation = this.getContentsAfterTransformation(contentsBeforeTransformation, unsyncBufferedOutputStream);
            this.transformsOutput = contentsAfterTransformation;
            if (Reference.useC14N11 && !b && !contentsAfterTransformation.isOutputStreamSet() && !contentsAfterTransformation.isOctetStream()) {
                if (this.transforms == null) {
                    (this.transforms = new Transforms(this.getDocument())).setSecureValidation(this.secureValidation);
                    this.getElement().insertBefore(this.transforms.getElement(), this.digestMethodElem);
                }
                this.transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
                contentsAfterTransformation.updateOutputStream(unsyncBufferedOutputStream, true);
            }
            else {
                contentsAfterTransformation.updateOutputStream(unsyncBufferedOutputStream);
            }
            unsyncBufferedOutputStream.flush();
            if (contentsAfterTransformation.getOctetStreamReal() != null) {
                contentsAfterTransformation.getOctetStreamReal().close();
            }
            return digesterOutputStream.getDigestValue();
        }
        catch (final XMLSecurityException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
        catch (final IOException ex2) {
            throw new ReferenceNotInitializedException(ex2);
        }
    }
    
    private byte[] getPreCalculatedDigest(final XMLSignatureInput xmlSignatureInput) throws ReferenceNotInitializedException {
        Reference.LOG.debug("Verifying element with pre-calculated digest");
        return XMLUtils.decode(xmlSignatureInput.getPreCalculatedDigest());
    }
    
    public byte[] getDigestValue() throws XMLSecurityException {
        if (this.digestValueElement == null) {
            throw new XMLSecurityException("signature.Verification.NoSignatureElement", new Object[] { "DigestValue", "http://www.w3.org/2000/09/xmldsig#" });
        }
        return XMLUtils.decode(XMLUtils.getFullTextChildrenFromNode(this.digestValueElement));
    }
    
    public boolean verify() throws ReferenceNotInitializedException, XMLSecurityException {
        final byte[] digestValue = this.getDigestValue();
        final byte[] calculateDigest = this.calculateDigest(true);
        final boolean equal = MessageDigestAlgorithm.isEqual(digestValue, calculateDigest);
        if (!equal) {
            Reference.LOG.warn("Verification failed for URI \"" + this.getURI() + "\"");
            Reference.LOG.warn("Expected Digest: " + XMLUtils.encodeToString(digestValue));
            Reference.LOG.warn("Actual Digest: " + XMLUtils.encodeToString(calculateDigest));
        }
        else {
            Reference.LOG.debug("Verification successful for URI \"{}\"", this.getURI());
        }
        return equal;
    }
    
    @Override
    public String getBaseLocalName() {
        return "Reference";
    }
    
    static {
        Reference.useC14N11 = AccessController.doPrivileged(() -> Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
        LOG = LoggerFactory.getLogger(Reference.class);
    }
}
