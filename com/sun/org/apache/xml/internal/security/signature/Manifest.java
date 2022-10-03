package com.sun.org.apache.xml.internal.security.signature;

import java.security.AccessController;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.w3c.dom.Node;
import java.io.OutputStream;
import java.util.Collections;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class Manifest extends SignatureElementProxy
{
    public static final int MAXIMUM_REFERENCE_COUNT = 30;
    private static final Logger LOG;
    private static Integer referenceCount;
    private List<Reference> references;
    private Element[] referencesEl;
    private List<VerifiedReference> verificationResults;
    private Map<String, String> resolverProperties;
    private List<ResourceResolver> perManifestResolvers;
    private boolean secureValidation;
    
    public Manifest(final Document document) {
        super(document);
        this.addReturnToSelf();
        this.references = new ArrayList<Reference>();
    }
    
    public Manifest(final Element element, final String s) throws XMLSecurityException {
        this(element, s, true);
    }
    
    public Manifest(final Element element, final String s, final boolean secureValidation) throws XMLSecurityException {
        super(element, s);
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, "Id");
        if (attributeNodeNS != null) {
            element.setIdAttributeNode(attributeNodeNS, true);
        }
        this.secureValidation = secureValidation;
        this.referencesEl = XMLUtils.selectDsNodes(this.getFirstChild(), "Reference");
        final int length = this.referencesEl.length;
        if (length == 0) {
            throw new DOMException((short)4, I18n.translate("xml.WrongContent", new Object[] { "Reference", "Manifest" }));
        }
        if (secureValidation && length > Manifest.referenceCount) {
            throw new XMLSecurityException("signature.tooManyReferences", new Object[] { length, Manifest.referenceCount });
        }
        this.references = new ArrayList<Reference>(length);
        for (int i = 0; i < length; ++i) {
            final Element element2 = this.referencesEl[i];
            final Attr attributeNodeNS2 = element2.getAttributeNodeNS(null, "Id");
            if (attributeNodeNS2 != null) {
                element2.setIdAttributeNode(attributeNodeNS2, true);
            }
            this.references.add(null);
        }
    }
    
    public void addDocument(final String s, final String s2, final Transforms transforms, final String s3, final String id, final String type) throws XMLSignatureException {
        final Reference reference = new Reference(this.getDocument(), s, s2, this, transforms, s3);
        if (id != null) {
            reference.setId(id);
        }
        if (type != null) {
            reference.setType(type);
        }
        this.references.add(reference);
        this.appendSelf(reference);
        this.addReturnToSelf();
    }
    
    public void generateDigestValues() throws XMLSignatureException, ReferenceNotInitializedException {
        for (int i = 0; i < this.getLength(); ++i) {
            this.references.get(i).generateDigestValue();
        }
    }
    
    public int getLength() {
        return this.references.size();
    }
    
    public Reference item(final int n) throws XMLSecurityException {
        if (this.references.get(n) == null) {
            this.references.set(n, new Reference(this.referencesEl[n], this.baseURI, this, this.secureValidation));
        }
        return this.references.get(n);
    }
    
    public void setId(final String s) {
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public boolean verifyReferences() throws MissingResourceFailureException, XMLSecurityException {
        return this.verifyReferences(false);
    }
    
    public boolean verifyReferences(final boolean b) throws MissingResourceFailureException, XMLSecurityException {
        if (this.referencesEl == null) {
            this.referencesEl = XMLUtils.selectDsNodes(this.getFirstChild(), "Reference");
        }
        Manifest.LOG.debug("verify {} References", this.referencesEl.length);
        Manifest.LOG.debug("I am {} requested to follow nested Manifests", b ? "" : "not");
        if (this.referencesEl.length == 0) {
            throw new XMLSecurityException("empty", new Object[] { "References are empty" });
        }
        if (this.secureValidation && this.referencesEl.length > Manifest.referenceCount) {
            throw new XMLSecurityException("signature.tooManyReferences", new Object[] { this.referencesEl.length, Manifest.referenceCount });
        }
        this.verificationResults = new ArrayList<VerifiedReference>(this.referencesEl.length);
        boolean b2 = true;
        for (int i = 0; i < this.referencesEl.length; ++i) {
            final Reference reference = new Reference(this.referencesEl[i], this.baseURI, this, this.secureValidation);
            this.references.set(i, reference);
            try {
                final boolean verify = reference.verify();
                if (!verify) {
                    b2 = false;
                }
                Manifest.LOG.debug("The Reference has Type {}", reference.getType());
                Object o = Collections.emptyList();
                if (b2 && b && reference.typeIsReferenceToManifest()) {
                    Manifest.LOG.debug("We have to follow a nested Manifest");
                    try {
                        final XMLSignatureInput dereferenceURIandPerformTransforms = reference.dereferenceURIandPerformTransforms(null);
                        final Set<Node> nodeSet = dereferenceURIandPerformTransforms.getNodeSet();
                        Manifest manifest = null;
                        for (final Node node : nodeSet) {
                            if (node.getNodeType() == 1 && ((Element)node).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ((Element)node).getLocalName().equals("Manifest")) {
                                try {
                                    manifest = new Manifest((Element)node, dereferenceURIandPerformTransforms.getSourceURI(), this.secureValidation);
                                    break;
                                }
                                catch (final XMLSecurityException ex) {
                                    Manifest.LOG.debug(ex.getMessage(), ex);
                                }
                            }
                        }
                        if (manifest == null) {
                            throw new MissingResourceFailureException(reference, "empty", new Object[] { "No Manifest found" });
                        }
                        manifest.perManifestResolvers = this.perManifestResolvers;
                        manifest.resolverProperties = this.resolverProperties;
                        if (!manifest.verifyReferences(b)) {
                            b2 = false;
                            Manifest.LOG.warn("The nested Manifest was invalid (bad)");
                        }
                        else {
                            Manifest.LOG.debug("The nested Manifest was valid (good)");
                        }
                        o = manifest.getVerificationResults();
                    }
                    catch (final IOException ex2) {
                        throw new ReferenceNotInitializedException(ex2);
                    }
                    catch (final ParserConfigurationException ex3) {
                        throw new ReferenceNotInitializedException(ex3);
                    }
                    catch (final SAXException ex4) {
                        throw new ReferenceNotInitializedException(ex4);
                    }
                }
                this.verificationResults.add(new VerifiedReference(verify, reference.getURI(), (List<VerifiedReference>)o));
            }
            catch (final ReferenceNotInitializedException ex5) {
                throw new MissingResourceFailureException(ex5, reference, "signature.Verification.Reference.NoInput", new Object[] { reference.getURI() });
            }
        }
        return b2;
    }
    
    public boolean getVerificationResult(final int n) throws XMLSecurityException {
        if (n < 0 || n > this.getLength() - 1) {
            throw new XMLSecurityException(new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", new Object[] { Integer.toString(n), Integer.toString(this.getLength()) })));
        }
        if (this.verificationResults == null) {
            try {
                this.verifyReferences();
            }
            catch (final Exception ex) {
                throw new XMLSecurityException(ex);
            }
        }
        return ((ArrayList)this.verificationResults).get(n).isValid();
    }
    
    public List<VerifiedReference> getVerificationResults() {
        if (this.verificationResults == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends VerifiedReference>)this.verificationResults);
    }
    
    public void addResourceResolver(final ResourceResolver resourceResolver) {
        if (resourceResolver == null) {
            return;
        }
        if (this.perManifestResolvers == null) {
            this.perManifestResolvers = new ArrayList<ResourceResolver>();
        }
        this.perManifestResolvers.add(resourceResolver);
    }
    
    public void addResourceResolver(final ResourceResolverSpi resourceResolverSpi) {
        if (resourceResolverSpi == null) {
            return;
        }
        if (this.perManifestResolvers == null) {
            this.perManifestResolvers = new ArrayList<ResourceResolver>();
        }
        this.perManifestResolvers.add(new ResourceResolver(resourceResolverSpi));
    }
    
    public List<ResourceResolver> getPerManifestResolvers() {
        return this.perManifestResolvers;
    }
    
    public Map<String, String> getResolverProperties() {
        return this.resolverProperties;
    }
    
    public void setResolverProperty(final String s, final String s2) {
        if (this.resolverProperties == null) {
            this.resolverProperties = new HashMap<String, String>(10);
        }
        this.resolverProperties.put(s, s2);
    }
    
    public String getResolverProperty(final String s) {
        return this.resolverProperties.get(s);
    }
    
    public byte[] getSignedContentItem(final int n) throws XMLSignatureException {
        try {
            return this.getReferencedContentAfterTransformsItem(n).getBytes();
        }
        catch (final IOException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException(ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException(ex3);
        }
        catch (final XMLSecurityException ex4) {
            throw new XMLSignatureException(ex4);
        }
    }
    
    public XMLSignatureInput getReferencedContentBeforeTransformsItem(final int n) throws XMLSecurityException {
        return this.item(n).getContentsBeforeTransformation();
    }
    
    public XMLSignatureInput getReferencedContentAfterTransformsItem(final int n) throws XMLSecurityException {
        return this.item(n).getContentsAfterTransformation();
    }
    
    public int getSignedContentLength() {
        return this.getLength();
    }
    
    @Override
    public String getBaseLocalName() {
        return "Manifest";
    }
    
    public boolean isSecureValidation() {
        return this.secureValidation;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Manifest.class);
        Manifest.referenceCount = AccessController.doPrivileged(() -> Integer.parseInt(System.getProperty("com.sun.org.apache.xml.internal.security.maxReferences", Integer.toString(30))));
    }
}
