package org.apache.poi.poifs.crypt.dsig.facets;

import org.apache.poi.util.POILogFactory;
import org.apache.xmlbeans.XmlCursor;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import java.security.cert.CertificateEncodingException;
import java.security.MessageDigest;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.xml.crypto.dsig.Transform;
import java.util.Collections;
import org.w3c.dom.Node;
import java.util.Arrays;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.XMLStructure;
import org.etsi.uri.x01903.v13.DataObjectFormatType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.apache.poi.poifs.crypt.dsig.services.SignaturePolicyService;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.etsi.uri.x01903.v13.SignerRoleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.etsi.uri.x01903.v13.CertIDType;
import org.etsi.uri.x01903.v13.CertIDListType;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.Reference;
import java.util.List;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.POILogger;

public class XAdESSignatureFacet extends SignatureFacet
{
    private static final POILogger LOG;
    private static final String XADES_TYPE = "http://uri.etsi.org/01903#SignedProperties";
    private final Map<String, String> dataObjectFormatMimeTypes;
    
    public XAdESSignatureFacet() {
        this.dataObjectFormatMimeTypes = new HashMap<String, String>();
    }
    
    @Override
    public void preSign(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
        XAdESSignatureFacet.LOG.log(1, new Object[] { "preSign" });
        final QualifyingPropertiesDocument qualDoc = QualifyingPropertiesDocument.Factory.newInstance();
        final QualifyingPropertiesType qualifyingProperties = qualDoc.addNewQualifyingProperties();
        qualifyingProperties.setTarget("#" + this.signatureConfig.getPackageSignatureId());
        final SignedPropertiesType signedProperties = qualifyingProperties.addNewSignedProperties();
        signedProperties.setId(this.signatureConfig.getXadesSignatureId());
        final SignedSignaturePropertiesType signedSignatureProperties = signedProperties.addNewSignedSignatureProperties();
        this.addSigningTime(signedSignatureProperties);
        this.addCertificate(signedSignatureProperties);
        this.addXadesRole(signedSignatureProperties);
        this.addPolicy(signedSignatureProperties);
        this.addMimeTypes(signedProperties);
        objects.add(this.addXadesObject(document, qualifyingProperties));
        references.add(this.addXadesReference());
    }
    
    private void addSigningTime(final SignedSignaturePropertiesType signedSignatureProperties) {
        final Calendar xmlGregorianCalendar = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
        xmlGregorianCalendar.setTime(this.signatureConfig.getExecutionTime());
        xmlGregorianCalendar.clear(14);
        signedSignatureProperties.setSigningTime(xmlGregorianCalendar);
    }
    
    private void addCertificate(final SignedSignaturePropertiesType signedSignatureProperties) {
        final List<X509Certificate> chain = this.signatureConfig.getSigningCertificateChain();
        if (chain == null || chain.isEmpty()) {
            throw new RuntimeException("no signing certificate chain available");
        }
        final CertIDListType signingCertificates = signedSignatureProperties.addNewSigningCertificate();
        final CertIDType certId = signingCertificates.addNewCert();
        setCertID(certId, this.signatureConfig, this.signatureConfig.isXadesIssuerNameNoReverseOrder(), chain.get(0));
    }
    
    private void addXadesRole(final SignedSignaturePropertiesType signedSignatureProperties) {
        final String role = this.signatureConfig.getXadesRole();
        if (role == null || role.isEmpty()) {
            return;
        }
        final SignerRoleType signerRole = signedSignatureProperties.addNewSignerRole();
        signedSignatureProperties.setSignerRole(signerRole);
        final ClaimedRolesListType claimedRolesList = signerRole.addNewClaimedRoles();
        final AnyType claimedRole = claimedRolesList.addNewClaimedRole();
        final XmlString roleString = XmlString.Factory.newInstance();
        roleString.setStringValue(role);
        insertXChild((XmlObject)claimedRole, (XmlObject)roleString);
    }
    
    private void addPolicy(final SignedSignaturePropertiesType signedSignatureProperties) {
        final SignaturePolicyService policyService = this.signatureConfig.getSignaturePolicyService();
        if (policyService == null) {
            if (this.signatureConfig.isXadesSignaturePolicyImplied()) {
                signedSignatureProperties.addNewSignaturePolicyIdentifier().addNewSignaturePolicyImplied();
            }
            return;
        }
        final SignaturePolicyIdentifierType policyId = signedSignatureProperties.addNewSignaturePolicyIdentifier();
        final SignaturePolicyIdType signaturePolicyId = policyId.addNewSignaturePolicyId();
        final ObjectIdentifierType oit = signaturePolicyId.addNewSigPolicyId();
        oit.setDescription(policyService.getSignaturePolicyDescription());
        oit.addNewIdentifier().setStringValue(policyService.getSignaturePolicyIdentifier());
        final byte[] signaturePolicyDocumentData = policyService.getSignaturePolicyDocument();
        final DigestAlgAndValueType sigPolicyHash = signaturePolicyId.addNewSigPolicyHash();
        setDigestAlgAndValue(sigPolicyHash, signaturePolicyDocumentData, this.signatureConfig.getDigestAlgo());
        final String signaturePolicyDownloadUrl = policyService.getSignaturePolicyDownloadUrl();
        if (signaturePolicyDownloadUrl == null) {
            return;
        }
        final AnyType sigPolicyQualifier = signaturePolicyId.addNewSigPolicyQualifiers().addNewSigPolicyQualifier();
        final XmlString spUriElement = XmlString.Factory.newInstance();
        spUriElement.setStringValue(signaturePolicyDownloadUrl);
        insertXChild((XmlObject)sigPolicyQualifier, (XmlObject)spUriElement);
    }
    
    private void addMimeTypes(final SignedPropertiesType signedProperties) {
        if (this.dataObjectFormatMimeTypes.isEmpty()) {
            return;
        }
        final List<DataObjectFormatType> dataObjectFormats = signedProperties.addNewSignedDataObjectProperties().getDataObjectFormatList();
        this.dataObjectFormatMimeTypes.forEach((key, value) -> {
            final DataObjectFormatType dof = DataObjectFormatType.Factory.newInstance();
            dof.setObjectReference("#" + key);
            dof.setMimeType(value);
            dataObjectFormats.add(dof);
        });
    }
    
    private XMLObject addXadesObject(final Document document, final QualifyingPropertiesType qualifyingProperties) {
        final Node qualDocElSrc = qualifyingProperties.getDomNode();
        final Node qualDocEl = document.importNode(qualDocElSrc, true);
        final List<XMLStructure> xadesObjectContent = Arrays.asList(new DOMStructure(qualDocEl));
        return this.getSignatureFactory().newXMLObject(xadesObjectContent, null, null, null);
    }
    
    private Reference addXadesReference() throws XMLSignatureException {
        final List<Transform> transforms = Collections.singletonList(this.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
        return this.newReference("#" + this.signatureConfig.getXadesSignatureId(), transforms, "http://uri.etsi.org/01903#SignedProperties", null, null);
    }
    
    protected static void setDigestAlgAndValue(final DigestAlgAndValueType digestAlgAndValue, final byte[] data, final HashAlgorithm digestAlgo) {
        final DigestMethodType digestMethod = digestAlgAndValue.addNewDigestMethod();
        digestMethod.setAlgorithm(SignatureConfig.getDigestMethodUri(digestAlgo));
        final MessageDigest messageDigest = CryptoFunctions.getMessageDigest(digestAlgo);
        final byte[] digestValue = messageDigest.digest(data);
        digestAlgAndValue.setDigestValue(digestValue);
    }
    
    protected static void setCertID(final CertIDType certId, final SignatureConfig signatureConfig, final boolean issuerNameNoReverseOrder, final X509Certificate certificate) {
        final X509IssuerSerialType issuerSerial = certId.addNewIssuerSerial();
        String issuerName;
        if (issuerNameNoReverseOrder) {
            issuerName = certificate.getIssuerDN().getName().replace(",", ", ");
        }
        else {
            issuerName = certificate.getIssuerX500Principal().toString();
        }
        issuerSerial.setX509IssuerName(issuerName);
        issuerSerial.setX509SerialNumber(certificate.getSerialNumber());
        byte[] encodedCertificate;
        try {
            encodedCertificate = certificate.getEncoded();
        }
        catch (final CertificateEncodingException e) {
            throw new RuntimeException("certificate encoding error: " + e.getMessage(), e);
        }
        final DigestAlgAndValueType certDigest = certId.addNewCertDigest();
        setDigestAlgAndValue(certDigest, encodedCertificate, signatureConfig.getXadesDigestAlgo());
    }
    
    public void addMimeType(final String dsReferenceUri, final String mimetype) {
        this.dataObjectFormatMimeTypes.put(dsReferenceUri, mimetype);
    }
    
    protected static void insertXChild(final XmlObject root, final XmlObject child) {
        final XmlCursor rootCursor = root.newCursor();
        rootCursor.toEndToken();
        final XmlCursor childCursor = child.newCursor();
        childCursor.toNextToken();
        childCursor.moveXml(rootCursor);
        childCursor.dispose();
        rootCursor.dispose();
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XAdESSignatureFacet.class);
    }
}
