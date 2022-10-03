package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.util.POILogFactory;
import org.w3c.dom.NodeList;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.util.Map;
import java.util.HashMap;
import org.apache.xmlbeans.XmlOptions;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.URIDereferencer;
import org.w3c.dom.events.EventListener;
import org.apache.jcp.xml.dsig.internal.dom.DOMReference;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.util.List;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.Reference;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import org.w3c.dom.events.EventTarget;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.apache.xml.security.Init;
import java.util.NoSuchElementException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.w3c.dom.Element;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import java.security.PrivateKey;
import javax.xml.crypto.dsig.TransformException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.io.OutputStream;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.Data;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.poi.EncryptedDocumentException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import java.security.Key;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import org.w3c.dom.Document;
import org.apache.poi.ooxml.util.DocumentHelper;
import java.util.Iterator;
import org.apache.poi.util.POILogger;

public class SignatureInfo implements SignatureConfig.SignatureConfigurable
{
    private static final POILogger LOG;
    private static boolean isInitialized;
    private SignatureConfig signatureConfig;
    
    public SignatureInfo() {
        initXmlProvider();
    }
    
    public SignatureConfig getSignatureConfig() {
        return this.signatureConfig;
    }
    
    @Override
    public void setSignatureConfig(final SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
    
    public boolean verifySignature() {
        final Iterator<SignaturePart> iterator = this.getSignatureParts().iterator();
        if (iterator.hasNext()) {
            final SignaturePart sp = iterator.next();
            return sp.validate();
        }
        return false;
    }
    
    public void confirmSignature() throws XMLSignatureException, MarshalException {
        final Document document = DocumentHelper.createDocument();
        final DOMSignContext xmlSignContext = this.createXMLSignContext(document);
        final DOMSignedInfo signedInfo = this.preSign(xmlSignContext);
        final String signatureValue = this.signDigest(xmlSignContext, signedInfo);
        this.postSign(xmlSignContext, signatureValue);
    }
    
    public DOMSignContext createXMLSignContext(final Document document) {
        return new DOMSignContext(this.signatureConfig.getKey(), document);
    }
    
    public String signDigest(final DOMSignContext xmlSignContext, final DOMSignedInfo signedInfo) {
        final PrivateKey key = this.signatureConfig.getKey();
        final HashAlgorithm algo = this.signatureConfig.getDigestAlgo();
        final int BASE64DEFAULTLENGTH = 76;
        if (algo.hashSize * 4 / 3 > 76 && !XMLUtils.ignoreLineBreaks()) {
            throw new EncryptedDocumentException("The hash size of the choosen hash algorithm (" + algo + " = " + algo.hashSize + " bytes), will motivate XmlSec to add linebreaks to the generated digest, which results in an invalid signature (... at least for Office) - please persuade it otherwise by adding '-Dorg.apache.xml.security.ignoreLineBreaks=true' to the JVM system properties.");
        }
        try (final DigestOutputStream dos = getDigestStream(algo, key)) {
            dos.init();
            final Document document = (Document)xmlSignContext.getParent();
            final Element el = this.getDsigElement(document, "SignedInfo");
            final DOMSubTreeData subTree = new DOMSubTreeData((Node)el, true);
            signedInfo.getCanonicalizationMethod().transform((Data)subTree, xmlSignContext, dos);
            return Base64.getEncoder().encodeToString(dos.sign());
        }
        catch (final GeneralSecurityException | IOException | TransformException e) {
            throw new EncryptedDocumentException((Throwable)e);
        }
    }
    
    private static DigestOutputStream getDigestStream(final HashAlgorithm algo, final PrivateKey key) {
        switch (algo) {
            case md2:
            case md5:
            case sha1:
            case sha256:
            case sha384:
            case sha512: {
                return new SignatureOutputStream(algo, key);
            }
            default: {
                return new DigestOutputStream(algo, key);
            }
        }
    }
    
    public Iterable<SignaturePart> getSignatureParts() {
        this.signatureConfig.init(true);
        return new Iterable<SignaturePart>() {
            @Override
            public Iterator<SignaturePart> iterator() {
                return new Iterator<SignaturePart>() {
                    OPCPackage pkg = SignatureInfo.this.signatureConfig.getOpcPackage();
                    Iterator<PackageRelationship> sigOrigRels = this.pkg.getRelationshipsByType("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin").iterator();
                    Iterator<PackageRelationship> sigRels;
                    PackagePart sigPart;
                    
                    @Override
                    public boolean hasNext() {
                        while (this.sigRels == null || !this.sigRels.hasNext()) {
                            if (!this.sigOrigRels.hasNext()) {
                                return false;
                            }
                            this.sigPart = this.pkg.getPart(this.sigOrigRels.next());
                            SignatureInfo.LOG.log(1, new Object[] { "Digital Signature Origin part", this.sigPart });
                            try {
                                this.sigRels = this.sigPart.getRelationshipsByType("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature").iterator();
                            }
                            catch (final InvalidFormatException e) {
                                SignatureInfo.LOG.log(5, new Object[] { "Reference to signature is invalid.", e });
                            }
                        }
                        return true;
                    }
                    
                    @Override
                    public SignaturePart next() {
                        PackagePart sigRelPart = null;
                        do {
                            try {
                                if (!this.hasNext()) {
                                    throw new NoSuchElementException();
                                }
                                sigRelPart = this.sigPart.getRelatedPart(this.sigRels.next());
                                SignatureInfo.LOG.log(1, new Object[] { "XML Signature part", sigRelPart });
                            }
                            catch (final InvalidFormatException e) {
                                SignatureInfo.LOG.log(5, new Object[] { "Reference to signature is invalid.", e });
                            }
                        } while (sigRelPart == null);
                        return new SignaturePart(sigRelPart, SignatureInfo.this.signatureConfig);
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    protected static synchronized void initXmlProvider() {
        if (SignatureInfo.isInitialized) {
            return;
        }
        SignatureInfo.isInitialized = true;
        try {
            Init.init();
            RelationshipTransformService.registerDsigProvider();
            CryptoFunctions.registerBouncyCastle();
        }
        catch (final Exception e) {
            throw new RuntimeException("Xml & BouncyCastle-Provider initialization failed", e);
        }
    }
    
    public DOMSignedInfo preSign(final DOMSignContext xmlSignContext) throws XMLSignatureException, MarshalException {
        this.signatureConfig.init(false);
        final Document document = (Document)xmlSignContext.getParent();
        final EventTarget target = (EventTarget)document;
        final EventListener creationListener = this.signatureConfig.getSignatureMarshalListener();
        if (creationListener != null) {
            if (creationListener instanceof SignatureMarshalListener) {
                ((SignatureMarshalListener)creationListener).setEventTarget(target);
            }
            SignatureMarshalListener.setListener(target, creationListener, true);
        }
        final URIDereferencer uriDereferencer = this.signatureConfig.getUriDereferencer();
        if (null != uriDereferencer) {
            xmlSignContext.setURIDereferencer(uriDereferencer);
        }
        this.signatureConfig.getNamespacePrefixes().forEach(xmlSignContext::putNamespacePrefix);
        xmlSignContext.setDefaultNamespacePrefix("");
        final XMLSignatureFactory signatureFactory = this.signatureConfig.getSignatureFactory();
        final List<Reference> references = new ArrayList<Reference>();
        final List<XMLObject> objects = new ArrayList<XMLObject>();
        for (final SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            SignatureInfo.LOG.log(1, new Object[] { "invoking signature facet: " + signatureFacet.getClass().getSimpleName() });
            signatureFacet.preSign(document, references, objects);
        }
        SignedInfo signedInfo;
        try {
            final SignatureMethod signatureMethod = signatureFactory.newSignatureMethod(this.signatureConfig.getSignatureMethodUri(), null);
            final CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(this.signatureConfig.getCanonicalizationMethod(), (C14NMethodParameterSpec)null);
            signedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);
        }
        catch (final GeneralSecurityException e) {
            throw new XMLSignatureException(e);
        }
        final String signatureValueId = this.signatureConfig.getPackageSignatureId() + "-signature-value";
        final XMLSignature xmlSignature = signatureFactory.newXMLSignature(signedInfo, null, objects, this.signatureConfig.getPackageSignatureId(), signatureValueId);
        xmlSignature.sign(xmlSignContext);
        for (final XMLObject object : objects) {
            SignatureInfo.LOG.log(1, new Object[] { "object java type: " + object.getClass().getName() });
            final List<XMLStructure> objectContentList = object.getContent();
            for (final XMLStructure objectContent : objectContentList) {
                SignatureInfo.LOG.log(1, new Object[] { "object content java type: " + objectContent.getClass().getName() });
                if (!(objectContent instanceof Manifest)) {
                    continue;
                }
                final Manifest manifest = (Manifest)objectContent;
                final List<Reference> manifestReferences = manifest.getReferences();
                for (final Reference manifestReference : manifestReferences) {
                    if (manifestReference.getDigestValue() != null) {
                        continue;
                    }
                    final DOMReference manifestDOMReference = (DOMReference)manifestReference;
                    manifestDOMReference.digest((XMLSignContext)xmlSignContext);
                }
            }
        }
        final List<Reference> signedInfoReferences = signedInfo.getReferences();
        for (final Reference signedInfoReference : signedInfoReferences) {
            final DOMReference domReference = (DOMReference)signedInfoReference;
            if (domReference.getDigestValue() != null) {
                continue;
            }
            domReference.digest((XMLSignContext)xmlSignContext);
        }
        return (DOMSignedInfo)signedInfo;
    }
    
    public void postSign(final DOMSignContext xmlSignContext, final String signatureValue) throws MarshalException {
        SignatureInfo.LOG.log(1, new Object[] { "postSign" });
        final Document document = (Document)xmlSignContext.getParent();
        final String signatureId = this.signatureConfig.getPackageSignatureId();
        if (!signatureId.equals(document.getDocumentElement().getAttribute("Id"))) {
            throw new RuntimeException("ds:Signature not found for @Id: " + signatureId);
        }
        final Element signatureNode = this.getDsigElement(document, "SignatureValue");
        if (signatureNode == null) {
            throw new RuntimeException("preSign has to be called before postSign");
        }
        signatureNode.setTextContent(signatureValue);
        for (final SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            signatureFacet.postSign(document);
        }
        this.writeDocument(document);
    }
    
    protected void writeDocument(final Document document) throws MarshalException {
        final XmlOptions xo = new XmlOptions();
        final Map<String, String> namespaceMap = new HashMap<String, String>();
        this.signatureConfig.getNamespacePrefixes().forEach((k, v) -> {
            final String s = namespaceMap.put(v, k);
            return;
        });
        xo.setSaveSuggestedPrefixes((Map)namespaceMap);
        xo.setUseDefaultNamespace();
        SignatureInfo.LOG.log(1, new Object[] { "output signed Office OpenXML document" });
        final OPCPackage pkg = this.signatureConfig.getOpcPackage();
        try {
            final DSigRelation originDesc = DSigRelation.ORIGIN_SIGS;
            final PackagePartName originPartName = PackagingURIHelper.createPartName(originDesc.getFileName(0));
            PackagePart originPart = pkg.getPart(originPartName);
            if (originPart == null) {
                originPart = pkg.createPart(originPartName, originDesc.getContentType());
                pkg.addRelationship(originPartName, TargetMode.INTERNAL, originDesc.getRelation());
            }
            final DSigRelation sigDesc = DSigRelation.SIG;
            int nextSigIdx = pkg.getUnusedPartIndex(sigDesc.getDefaultFileName());
            if (!this.signatureConfig.isAllowMultipleSignatures()) {
                final PackageRelationshipCollection prc = originPart.getRelationshipsByType(sigDesc.getRelation());
                for (int i = 2; i < nextSigIdx; ++i) {
                    final PackagePartName pn = PackagingURIHelper.createPartName(sigDesc.getFileName(i));
                    for (final PackageRelationship rel : prc) {
                        final PackagePart pp = originPart.getRelatedPart(rel);
                        if (pp.getPartName().equals(pn)) {
                            originPart.removeRelationship(rel.getId());
                            prc.removeRelationship(rel.getId());
                            break;
                        }
                    }
                    pkg.removePart(pkg.getPart(pn));
                }
                nextSigIdx = 1;
            }
            final PackagePartName sigPartName = PackagingURIHelper.createPartName(sigDesc.getFileName(nextSigIdx));
            PackagePart sigPart = pkg.getPart(sigPartName);
            if (sigPart == null) {
                sigPart = pkg.createPart(sigPartName, sigDesc.getContentType());
                originPart.addRelationship(sigPartName, TargetMode.INTERNAL, sigDesc.getRelation());
            }
            else {
                sigPart.clear();
            }
            try (final OutputStream os = sigPart.getOutputStream()) {
                final SignatureDocument sigDoc = SignatureDocument.Factory.parse((Node)document, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                sigDoc.save(os, xo);
            }
        }
        catch (final Exception e) {
            throw new MarshalException("Unable to write signature document", e);
        }
    }
    
    private Element getDsigElement(final Document document, final String localName) {
        final NodeList sigValNl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", localName);
        if (sigValNl.getLength() == 1) {
            return (Element)sigValNl.item(0);
        }
        SignatureInfo.LOG.log(5, new Object[] { "Signature element '" + localName + "' was " + ((sigValNl.getLength() == 0) ? "not found" : "multiple times") });
        return null;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)SignatureInfo.class);
    }
}
