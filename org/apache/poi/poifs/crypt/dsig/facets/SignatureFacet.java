package org.apache.poi.poifs.crypt.dsig.facets;

import org.apache.poi.util.POILogFactory;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import java.security.GeneralSecurityException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.Reference;
import java.util.List;
import org.w3c.dom.Document;
import org.apache.poi.util.POILogger;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;

public abstract class SignatureFacet implements SignatureConfig.SignatureConfigurable
{
    private static final POILogger LOG;
    public static final String XML_NS = "http://www.w3.org/2000/xmlns/";
    public static final String XML_DIGSIG_NS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String OO_DIGSIG_NS = "http://schemas.openxmlformats.org/package/2006/digital-signature";
    public static final String MS_DIGSIG_NS = "http://schemas.microsoft.com/office/2006/digsig";
    public static final String XADES_132_NS = "http://uri.etsi.org/01903/v1.3.2#";
    public static final String XADES_141_NS = "http://uri.etsi.org/01903/v1.4.1#";
    protected SignatureConfig signatureConfig;
    
    @Override
    public void setSignatureConfig(final SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
    
    public void preSign(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
    }
    
    public void postSign(final Document document) throws MarshalException {
    }
    
    protected XMLSignatureFactory getSignatureFactory() {
        return this.signatureConfig.getSignatureFactory();
    }
    
    protected Transform newTransform(final String canonicalizationMethod) throws XMLSignatureException {
        return this.newTransform(canonicalizationMethod, null);
    }
    
    protected Transform newTransform(final String canonicalizationMethod, final TransformParameterSpec paramSpec) throws XMLSignatureException {
        try {
            return this.getSignatureFactory().newTransform(canonicalizationMethod, paramSpec);
        }
        catch (final GeneralSecurityException e) {
            throw new XMLSignatureException("unknown canonicalization method: " + canonicalizationMethod, e);
        }
    }
    
    protected Reference newReference(final String uri, final List<Transform> transforms, final String type, final String id, final byte[] digestValue) throws XMLSignatureException {
        return newReference(uri, transforms, type, id, digestValue, this.signatureConfig);
    }
    
    public static Reference newReference(final String uri, final List<Transform> transforms, final String type, final String id, final byte[] digestValue, final SignatureConfig signatureConfig) throws XMLSignatureException {
        final String digestMethodUri = signatureConfig.getDigestMethodUri();
        final XMLSignatureFactory sigFac = signatureConfig.getSignatureFactory();
        DigestMethod digestMethod;
        try {
            digestMethod = sigFac.newDigestMethod(digestMethodUri, null);
        }
        catch (final GeneralSecurityException e) {
            throw new XMLSignatureException("unknown digest method uri: " + digestMethodUri, e);
        }
        Reference reference;
        if (digestValue == null) {
            reference = sigFac.newReference(uri, digestMethod, transforms, type, id);
        }
        else {
            reference = sigFac.newReference(uri, digestMethod, transforms, type, id, digestValue);
        }
        return reference;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)SignatureFacet.class);
    }
}
