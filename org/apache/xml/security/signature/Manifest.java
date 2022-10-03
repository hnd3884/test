package org.apache.xml.security.signature;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Node;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.DOMException;
import org.apache.xml.security.utils.I18n;
import java.util.ArrayList;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class Manifest extends SignatureElementProxy
{
    static Log log;
    List _references;
    Element[] _referencesEl;
    private boolean[] verificationResults;
    HashMap _resolverProperties;
    List _perManifestResolvers;
    
    public Manifest(final Document document) {
        super(document);
        this.verificationResults = null;
        this._resolverProperties = null;
        this._perManifestResolvers = null;
        XMLUtils.addReturnToElement(super._constructionElement);
        this._references = new ArrayList();
    }
    
    public Manifest(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        this.verificationResults = null;
        this._resolverProperties = null;
        this._perManifestResolvers = null;
        this._referencesEl = XMLUtils.selectDsNodes(super._constructionElement.getFirstChild(), "Reference");
        final int length = this._referencesEl.length;
        if (length == 0) {
            throw new DOMException((short)4, I18n.translate("xml.WrongContent", new Object[] { "Reference", "Manifest" }));
        }
        this._references = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            this._references.add(null);
        }
    }
    
    public void addDocument(final String s, final String s2, final Transforms transforms, final String s3, final String id, final String type) throws XMLSignatureException {
        if (super._state == 0) {
            final Reference reference = new Reference(super._doc, s, s2, this, transforms, s3);
            if (id != null) {
                reference.setId(id);
            }
            if (type != null) {
                reference.setType(type);
            }
            this._references.add(reference);
            super._constructionElement.appendChild(reference.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void generateDigestValues() throws XMLSignatureException, ReferenceNotInitializedException {
        if (super._state == 0) {
            for (int i = 0; i < this.getLength(); ++i) {
                ((Reference)this._references.get(i)).generateDigestValue();
            }
        }
    }
    
    public int getLength() {
        return this._references.size();
    }
    
    public Reference item(final int n) throws XMLSecurityException {
        if (super._state == 0) {
            return this._references.get(n);
        }
        if (this._references.get(n) == null) {
            this._references.set(n, new Reference(this._referencesEl[n], super._baseURI, this));
        }
        return this._references.get(n);
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
    
    public boolean verifyReferences() throws MissingResourceFailureException, XMLSecurityException {
        return this.verifyReferences(false);
    }
    
    public boolean verifyReferences(final boolean b) throws MissingResourceFailureException, XMLSecurityException {
        if (this._referencesEl == null) {
            this._referencesEl = XMLUtils.selectDsNodes(super._constructionElement.getFirstChild(), "Reference");
        }
        if (Manifest.log.isDebugEnabled()) {
            Manifest.log.debug((Object)("verify " + this._referencesEl.length + " References"));
            Manifest.log.debug((Object)("I am " + (b ? "" : "not") + " requested to follow nested Manifests"));
        }
        boolean b2 = true;
        if (this._referencesEl.length == 0) {
            throw new XMLSecurityException("empty");
        }
        this.verificationResults = new boolean[this._referencesEl.length];
        for (int i = 0; i < this._referencesEl.length; ++i) {
            final Reference reference = new Reference(this._referencesEl[i], super._baseURI, this);
            this._references.set(i, reference);
            try {
                final boolean verify = reference.verify();
                this.setVerificationResult(i, verify);
                if (!verify) {
                    b2 = false;
                }
                if (Manifest.log.isDebugEnabled()) {
                    Manifest.log.debug((Object)("The Reference has Type " + reference.getType()));
                }
                if (b2 && b && reference.typeIsReferenceToManifest()) {
                    Manifest.log.debug((Object)"We have to follow a nested Manifest");
                    try {
                        final XMLSignatureInput dereferenceURIandPerformTransforms = reference.dereferenceURIandPerformTransforms(null);
                        final Set nodeSet = dereferenceURIandPerformTransforms.getNodeSet();
                        Manifest manifest = null;
                        final Iterator iterator = nodeSet.iterator();
                        while (iterator.hasNext()) {
                            final Node node = (Node)iterator.next();
                            if (node.getNodeType() == 1 && ((Element)node).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ((Element)node).getLocalName().equals("Manifest")) {
                                try {
                                    manifest = new Manifest((Element)node, dereferenceURIandPerformTransforms.getSourceURI());
                                    break;
                                }
                                catch (final XMLSecurityException ex) {}
                            }
                        }
                        if (manifest == null) {
                            throw new MissingResourceFailureException("empty", reference);
                        }
                        manifest._perManifestResolvers = this._perManifestResolvers;
                        manifest._resolverProperties = this._resolverProperties;
                        if (!manifest.verifyReferences(b)) {
                            b2 = false;
                            Manifest.log.warn((Object)"The nested Manifest was invalid (bad)");
                        }
                        else {
                            Manifest.log.debug((Object)"The nested Manifest was valid (good)");
                        }
                    }
                    catch (final IOException ex2) {
                        throw new ReferenceNotInitializedException("empty", ex2);
                    }
                    catch (final ParserConfigurationException ex3) {
                        throw new ReferenceNotInitializedException("empty", ex3);
                    }
                    catch (final SAXException ex4) {
                        throw new ReferenceNotInitializedException("empty", ex4);
                    }
                }
            }
            catch (final ReferenceNotInitializedException ex5) {
                throw new MissingResourceFailureException("signature.Verification.Reference.NoInput", new Object[] { reference.getURI() }, ex5, reference);
            }
        }
        return b2;
    }
    
    private void setVerificationResult(final int n, final boolean b) {
        if (this.verificationResults == null) {
            this.verificationResults = new boolean[this.getLength()];
        }
        this.verificationResults[n] = b;
    }
    
    public boolean getVerificationResult(final int n) throws XMLSecurityException {
        if (n < 0 || n > this.getLength() - 1) {
            throw new XMLSecurityException("generic.EmptyMessage", new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", new Object[] { Integer.toString(n), Integer.toString(this.getLength()) })));
        }
        if (this.verificationResults == null) {
            try {
                this.verifyReferences();
            }
            catch (final Exception ex) {
                throw new XMLSecurityException("generic.EmptyMessage", ex);
            }
        }
        return this.verificationResults[n];
    }
    
    public void addResourceResolver(final ResourceResolver resourceResolver) {
        if (resourceResolver == null) {
            return;
        }
        if (this._perManifestResolvers == null) {
            this._perManifestResolvers = new ArrayList();
        }
        this._perManifestResolvers.add(resourceResolver);
    }
    
    public void addResourceResolver(final ResourceResolverSpi resourceResolverSpi) {
        if (resourceResolverSpi == null) {
            return;
        }
        if (this._perManifestResolvers == null) {
            this._perManifestResolvers = new ArrayList();
        }
        this._perManifestResolvers.add(new ResourceResolver(resourceResolverSpi));
    }
    
    public void setResolverProperty(final String s, final String s2) {
        if (this._resolverProperties == null) {
            this._resolverProperties = new HashMap(10);
        }
        this._resolverProperties.put(s, s2);
    }
    
    public String getResolverProperty(final String s) {
        return this._resolverProperties.get(s);
    }
    
    public byte[] getSignedContentItem(final int n) throws XMLSignatureException {
        try {
            return this.getReferencedContentAfterTransformsItem(n).getBytes();
        }
        catch (final IOException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException("empty", ex3);
        }
        catch (final XMLSecurityException ex4) {
            throw new XMLSignatureException("empty", ex4);
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
    
    public String getBaseLocalName() {
        return "Manifest";
    }
    
    static {
        Manifest.log = LogFactory.getLog(Manifest.class.getName());
    }
}
