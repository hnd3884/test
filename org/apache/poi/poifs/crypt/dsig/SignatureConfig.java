package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.util.POILogFactory;
import java.util.UUID;
import java.text.ParseException;
import java.text.DateFormat;
import org.apache.poi.util.LocaleUtil;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Iterator;
import org.apache.poi.poifs.crypt.dsig.facets.Office2010SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.XAdESSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.KeyInfoSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.OOXMLSignatureFacet;
import org.apache.poi.EncryptedDocumentException;
import java.util.HashMap;
import org.apache.poi.poifs.crypt.dsig.services.TSPTimeStampService;
import java.util.ArrayList;
import java.util.Map;
import org.w3c.dom.events.EventListener;
import org.apache.poi.poifs.crypt.dsig.services.RevocationDataService;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampServiceValidator;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampService;
import javax.xml.crypto.URIDereferencer;
import org.apache.poi.poifs.crypt.dsig.services.SignaturePolicyService;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import java.util.Date;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import java.util.List;
import java.security.Provider;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.POILogger;

public class SignatureConfig
{
    public static final String SIGNATURE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final POILogger LOG;
    private static final String DigestMethod_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    private static final String DigestMethod_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    private ThreadLocal<OPCPackage> opcPackage;
    private ThreadLocal<XMLSignatureFactory> signatureFactory;
    private ThreadLocal<KeyInfoFactory> keyInfoFactory;
    private ThreadLocal<Provider> provider;
    private List<SignatureFacet> signatureFacets;
    private HashAlgorithm digestAlgo;
    private Date executionTime;
    private PrivateKey key;
    private List<X509Certificate> signingCertificateChain;
    private SignaturePolicyService signaturePolicyService;
    private URIDereferencer uriDereferencer;
    private String canonicalizationMethod;
    private boolean includeEntireCertificateChain;
    private boolean includeIssuerSerial;
    private boolean includeKeyValue;
    private TimeStampService tspService;
    private String tspUrl;
    private boolean tspOldProtocol;
    private HashAlgorithm tspDigestAlgo;
    private String tspUser;
    private String tspPass;
    private TimeStampServiceValidator tspValidator;
    private String tspRequestPolicy;
    private String userAgent;
    private String proxyUrl;
    private RevocationDataService revocationDataService;
    private HashAlgorithm xadesDigestAlgo;
    private String xadesRole;
    private String xadesSignatureId;
    private boolean xadesSignaturePolicyImplied;
    private String xadesCanonicalizationMethod;
    private boolean xadesIssuerNameNoReverseOrder;
    private String packageSignatureId;
    private String signatureDescription;
    private EventListener signatureMarshalListener;
    private final Map<String, String> namespacePrefixes;
    private boolean updateConfigOnValidate;
    private boolean allowMultipleSignatures;
    
    public SignatureConfig() {
        this.opcPackage = new ThreadLocal<OPCPackage>();
        this.signatureFactory = new ThreadLocal<XMLSignatureFactory>();
        this.keyInfoFactory = new ThreadLocal<KeyInfoFactory>();
        this.provider = new ThreadLocal<Provider>();
        this.signatureFacets = new ArrayList<SignatureFacet>();
        this.digestAlgo = HashAlgorithm.sha256;
        this.executionTime = new Date();
        this.canonicalizationMethod = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
        this.includeEntireCertificateChain = true;
        this.tspService = new TSPTimeStampService();
        this.tspRequestPolicy = "1.3.6.1.4.1.13762.3";
        this.userAgent = "POI XmlSign Service TSP Client";
        this.xadesSignatureId = "idSignedProperties";
        this.xadesSignaturePolicyImplied = true;
        this.xadesCanonicalizationMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        this.xadesIssuerNameNoReverseOrder = true;
        this.packageSignatureId = "idPackageSignature";
        this.signatureDescription = "Office OpenXML Document";
        this.namespacePrefixes = new HashMap<String, String>();
        this.updateConfigOnValidate = false;
        this.allowMultipleSignatures = false;
    }
    
    protected void init(final boolean onlyValidation) {
        if (this.opcPackage == null) {
            throw new EncryptedDocumentException("opcPackage is null");
        }
        if (this.uriDereferencer == null) {
            this.uriDereferencer = new OOXMLURIDereferencer();
        }
        if (this.uriDereferencer instanceof SignatureConfigurable) {
            ((SignatureConfigurable)this.uriDereferencer).setSignatureConfig(this);
        }
        if (this.namespacePrefixes.isEmpty()) {
            this.namespacePrefixes.put("http://schemas.openxmlformats.org/package/2006/digital-signature", "mdssi");
            this.namespacePrefixes.put("http://uri.etsi.org/01903/v1.3.2#", "xd");
        }
        if (onlyValidation) {
            return;
        }
        if (this.signatureMarshalListener == null) {
            this.signatureMarshalListener = new SignatureMarshalListener();
        }
        if (this.signatureMarshalListener instanceof SignatureConfigurable) {
            ((SignatureConfigurable)this.signatureMarshalListener).setSignatureConfig(this);
        }
        if (this.tspService != null) {
            this.tspService.setSignatureConfig(this);
        }
        if (this.signatureFacets.isEmpty()) {
            this.addSignatureFacet(new OOXMLSignatureFacet());
            this.addSignatureFacet(new KeyInfoSignatureFacet());
            this.addSignatureFacet(new XAdESSignatureFacet());
            this.addSignatureFacet(new Office2010SignatureFacet());
        }
        for (final SignatureFacet sf : this.signatureFacets) {
            sf.setSignatureConfig(this);
        }
    }
    
    public void addSignatureFacet(final SignatureFacet signatureFacet) {
        this.signatureFacets.add(signatureFacet);
    }
    
    public List<SignatureFacet> getSignatureFacets() {
        return this.signatureFacets;
    }
    
    public void setSignatureFacets(final List<SignatureFacet> signatureFacets) {
        this.signatureFacets = signatureFacets;
    }
    
    public HashAlgorithm getDigestAlgo() {
        return this.digestAlgo;
    }
    
    public void setDigestAlgo(final HashAlgorithm digestAlgo) {
        this.digestAlgo = digestAlgo;
    }
    
    public OPCPackage getOpcPackage() {
        return this.opcPackage.get();
    }
    
    public void setOpcPackage(final OPCPackage opcPackage) {
        this.opcPackage.set(opcPackage);
    }
    
    public PrivateKey getKey() {
        return this.key;
    }
    
    public void setKey(final PrivateKey key) {
        this.key = key;
    }
    
    public List<X509Certificate> getSigningCertificateChain() {
        return this.signingCertificateChain;
    }
    
    public void setSigningCertificateChain(final List<X509Certificate> signingCertificateChain) {
        this.signingCertificateChain = signingCertificateChain;
    }
    
    public Date getExecutionTime() {
        return this.executionTime;
    }
    
    public void setExecutionTime(final Date executionTime) {
        this.executionTime = executionTime;
    }
    
    public String formatExecutionTime() {
        final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
        fmt.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return fmt.format(this.getExecutionTime());
    }
    
    public void setExecutionTime(final String executionTime) {
        if (executionTime != null && !"".equals(executionTime)) {
            final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
            fmt.setTimeZone(LocaleUtil.TIMEZONE_UTC);
            try {
                this.executionTime = fmt.parse(executionTime);
            }
            catch (final ParseException e) {
                SignatureConfig.LOG.log(5, new Object[] { "Illegal execution time: " + executionTime });
            }
        }
    }
    
    public SignaturePolicyService getSignaturePolicyService() {
        return this.signaturePolicyService;
    }
    
    public void setSignaturePolicyService(final SignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }
    
    public URIDereferencer getUriDereferencer() {
        return this.uriDereferencer;
    }
    
    public void setUriDereferencer(final URIDereferencer uriDereferencer) {
        this.uriDereferencer = uriDereferencer;
    }
    
    public String getSignatureDescription() {
        return this.signatureDescription;
    }
    
    public void setSignatureDescription(final String signatureDescription) {
        this.signatureDescription = signatureDescription;
    }
    
    public String getCanonicalizationMethod() {
        return this.canonicalizationMethod;
    }
    
    public void setCanonicalizationMethod(final String canonicalizationMethod) {
        this.canonicalizationMethod = verifyCanonicalizationMethod(canonicalizationMethod, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }
    
    private static String verifyCanonicalizationMethod(final String canonicalizationMethod, final String defaultMethod) {
        if (canonicalizationMethod == null || canonicalizationMethod.isEmpty()) {
            return defaultMethod;
        }
        switch (canonicalizationMethod) {
            case "http://www.w3.org/TR/2001/REC-xml-c14n-20010315":
            case "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments":
            case "http://www.w3.org/2000/09/xmldsig#enveloped-signature":
            case "http://www.w3.org/2001/10/xml-exc-c14n#":
            case "http://www.w3.org/2001/10/xml-exc-c14n#WithComments": {
                return canonicalizationMethod;
            }
            default: {
                throw new EncryptedDocumentException("Unknown CanonicalizationMethod: " + canonicalizationMethod);
            }
        }
    }
    
    public String getPackageSignatureId() {
        return this.packageSignatureId;
    }
    
    public void setPackageSignatureId(final String packageSignatureId) {
        this.packageSignatureId = nvl(packageSignatureId, "xmldsig-" + UUID.randomUUID());
    }
    
    public String getTspUrl() {
        return this.tspUrl;
    }
    
    public void setTspUrl(final String tspUrl) {
        this.tspUrl = tspUrl;
    }
    
    public boolean isTspOldProtocol() {
        return this.tspOldProtocol;
    }
    
    public void setTspOldProtocol(final boolean tspOldProtocol) {
        this.tspOldProtocol = tspOldProtocol;
    }
    
    public HashAlgorithm getTspDigestAlgo() {
        return nvl(this.tspDigestAlgo, this.digestAlgo);
    }
    
    public void setTspDigestAlgo(final HashAlgorithm tspDigestAlgo) {
        this.tspDigestAlgo = tspDigestAlgo;
    }
    
    public String getProxyUrl() {
        return this.proxyUrl;
    }
    
    public void setProxyUrl(final String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
    
    public TimeStampService getTspService() {
        return this.tspService;
    }
    
    public void setTspService(final TimeStampService tspService) {
        this.tspService = tspService;
    }
    
    public String getTspUser() {
        return this.tspUser;
    }
    
    public void setTspUser(final String tspUser) {
        this.tspUser = tspUser;
    }
    
    public String getTspPass() {
        return this.tspPass;
    }
    
    public void setTspPass(final String tspPass) {
        this.tspPass = tspPass;
    }
    
    public TimeStampServiceValidator getTspValidator() {
        return this.tspValidator;
    }
    
    public void setTspValidator(final TimeStampServiceValidator tspValidator) {
        this.tspValidator = tspValidator;
    }
    
    public RevocationDataService getRevocationDataService() {
        return this.revocationDataService;
    }
    
    public void setRevocationDataService(final RevocationDataService revocationDataService) {
        this.revocationDataService = revocationDataService;
    }
    
    public HashAlgorithm getXadesDigestAlgo() {
        return nvl(this.xadesDigestAlgo, this.digestAlgo);
    }
    
    public void setXadesDigestAlgo(final HashAlgorithm xadesDigestAlgo) {
        this.xadesDigestAlgo = xadesDigestAlgo;
    }
    
    public void setXadesDigestAlgo(final String xadesDigestAlgo) {
        this.xadesDigestAlgo = getDigestMethodAlgo(xadesDigestAlgo);
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getTspRequestPolicy() {
        return this.tspRequestPolicy;
    }
    
    public void setTspRequestPolicy(final String tspRequestPolicy) {
        this.tspRequestPolicy = tspRequestPolicy;
    }
    
    public boolean isIncludeEntireCertificateChain() {
        return this.includeEntireCertificateChain;
    }
    
    public void setIncludeEntireCertificateChain(final boolean includeEntireCertificateChain) {
        this.includeEntireCertificateChain = includeEntireCertificateChain;
    }
    
    public boolean isIncludeIssuerSerial() {
        return this.includeIssuerSerial;
    }
    
    public void setIncludeIssuerSerial(final boolean includeIssuerSerial) {
        this.includeIssuerSerial = includeIssuerSerial;
    }
    
    public boolean isIncludeKeyValue() {
        return this.includeKeyValue;
    }
    
    public void setIncludeKeyValue(final boolean includeKeyValue) {
        this.includeKeyValue = includeKeyValue;
    }
    
    public String getXadesRole() {
        return this.xadesRole;
    }
    
    public void setXadesRole(final String xadesRole) {
        this.xadesRole = xadesRole;
    }
    
    public String getXadesSignatureId() {
        return nvl(this.xadesSignatureId, "idSignedProperties");
    }
    
    public void setXadesSignatureId(final String xadesSignatureId) {
        this.xadesSignatureId = xadesSignatureId;
    }
    
    public boolean isXadesSignaturePolicyImplied() {
        return this.xadesSignaturePolicyImplied;
    }
    
    public void setXadesSignaturePolicyImplied(final boolean xadesSignaturePolicyImplied) {
        this.xadesSignaturePolicyImplied = xadesSignaturePolicyImplied;
    }
    
    public boolean isXadesIssuerNameNoReverseOrder() {
        return this.xadesIssuerNameNoReverseOrder;
    }
    
    public void setXadesIssuerNameNoReverseOrder(final boolean xadesIssuerNameNoReverseOrder) {
        this.xadesIssuerNameNoReverseOrder = xadesIssuerNameNoReverseOrder;
    }
    
    public EventListener getSignatureMarshalListener() {
        return this.signatureMarshalListener;
    }
    
    public void setSignatureMarshalListener(final EventListener signatureMarshalListener) {
        this.signatureMarshalListener = signatureMarshalListener;
    }
    
    public Map<String, String> getNamespacePrefixes() {
        return this.namespacePrefixes;
    }
    
    public void setNamespacePrefixes(final Map<String, String> namespacePrefixes) {
        this.namespacePrefixes.clear();
        this.namespacePrefixes.putAll(namespacePrefixes);
    }
    
    private static <T> T nvl(final T value, final T defaultValue) {
        return (value == null) ? defaultValue : value;
    }
    
    public String getSignatureMethodUri() {
        switch (this.getDigestAlgo()) {
            case sha1: {
                return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
            }
            case sha224: {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
            }
            case sha256: {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
            }
            case sha384: {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
            }
            case sha512: {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
            }
            case ripemd160: {
                return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
            }
            default: {
                throw new EncryptedDocumentException("Hash algorithm " + this.getDigestAlgo() + " not supported for signing.");
            }
        }
    }
    
    public String getDigestMethodUri() {
        return getDigestMethodUri(this.getDigestAlgo());
    }
    
    public static String getDigestMethodUri(final HashAlgorithm digestAlgo) {
        switch (digestAlgo) {
            case sha1: {
                return "http://www.w3.org/2000/09/xmldsig#sha1";
            }
            case sha224: {
                return "http://www.w3.org/2001/04/xmldsig-more#sha224";
            }
            case sha256: {
                return "http://www.w3.org/2001/04/xmlenc#sha256";
            }
            case sha384: {
                return "http://www.w3.org/2001/04/xmldsig-more#sha384";
            }
            case sha512: {
                return "http://www.w3.org/2001/04/xmlenc#sha512";
            }
            case ripemd160: {
                return "http://www.w3.org/2001/04/xmlenc#ripemd160";
            }
            default: {
                throw new EncryptedDocumentException("Hash algorithm " + digestAlgo + " not supported for signing.");
            }
        }
    }
    
    private static HashAlgorithm getDigestMethodAlgo(final String digestMethodUri) {
        if (digestMethodUri == null || digestMethodUri.isEmpty()) {
            return null;
        }
        switch (digestMethodUri) {
            case "http://www.w3.org/2000/09/xmldsig#sha1": {
                return HashAlgorithm.sha1;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#sha224": {
                return HashAlgorithm.sha224;
            }
            case "http://www.w3.org/2001/04/xmlenc#sha256": {
                return HashAlgorithm.sha256;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#sha384": {
                return HashAlgorithm.sha384;
            }
            case "http://www.w3.org/2001/04/xmlenc#sha512": {
                return HashAlgorithm.sha512;
            }
            case "http://www.w3.org/2001/04/xmlenc#ripemd160": {
                return HashAlgorithm.ripemd160;
            }
            default: {
                throw new EncryptedDocumentException("Hash algorithm " + digestMethodUri + " not supported for signing.");
            }
        }
    }
    
    public void setSignatureMethodFromUri(final String signatureMethodUri) {
        switch (signatureMethodUri) {
            case "http://www.w3.org/2000/09/xmldsig#rsa-sha1": {
                this.setDigestAlgo(HashAlgorithm.sha1);
                break;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224": {
                this.setDigestAlgo(HashAlgorithm.sha224);
                break;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256": {
                this.setDigestAlgo(HashAlgorithm.sha256);
                break;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384": {
                this.setDigestAlgo(HashAlgorithm.sha384);
                break;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512": {
                this.setDigestAlgo(HashAlgorithm.sha512);
                break;
            }
            case "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160": {
                this.setDigestAlgo(HashAlgorithm.ripemd160);
                break;
            }
            default: {
                throw new EncryptedDocumentException("Hash algorithm " + signatureMethodUri + " not supported.");
            }
        }
    }
    
    public void setSignatureFactory(final XMLSignatureFactory signatureFactory) {
        this.signatureFactory.set(signatureFactory);
    }
    
    public XMLSignatureFactory getSignatureFactory() {
        XMLSignatureFactory sigFac = this.signatureFactory.get();
        if (sigFac == null) {
            sigFac = XMLSignatureFactory.getInstance("DOM", this.getProvider());
            this.setSignatureFactory(sigFac);
        }
        return sigFac;
    }
    
    public void setKeyInfoFactory(final KeyInfoFactory keyInfoFactory) {
        this.keyInfoFactory.set(keyInfoFactory);
    }
    
    public KeyInfoFactory getKeyInfoFactory() {
        KeyInfoFactory keyFac = this.keyInfoFactory.get();
        if (keyFac == null) {
            keyFac = KeyInfoFactory.getInstance("DOM", this.getProvider());
            this.setKeyInfoFactory(keyFac);
        }
        return keyFac;
    }
    
    public Provider getProvider() {
        Provider prov = this.provider.get();
        if (prov == null) {
            final String[] array;
            final String[] dsigProviderNames = array = new String[] { System.getProperty("jsr105Provider"), "org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI", "org.jcp.xml.dsig.internal.dom.XMLDSigRI" };
            for (final String pn : array) {
                if (pn != null) {
                    try {
                        prov = (Provider)Class.forName(pn).newInstance();
                        break;
                    }
                    catch (final Exception e) {
                        SignatureConfig.LOG.log(1, new Object[] { "XMLDsig-Provider '" + pn + "' can't be found - trying next." });
                    }
                }
            }
        }
        if (prov == null) {
            throw new RuntimeException("JRE doesn't support default xml signature provider - set jsr105Provider system property!");
        }
        return prov;
    }
    
    public String getXadesCanonicalizationMethod() {
        return this.xadesCanonicalizationMethod;
    }
    
    public void setXadesCanonicalizationMethod(final String xadesCanonicalizationMethod) {
        this.xadesCanonicalizationMethod = verifyCanonicalizationMethod(xadesCanonicalizationMethod, "http://www.w3.org/2001/10/xml-exc-c14n#");
    }
    
    public boolean isUpdateConfigOnValidate() {
        return this.updateConfigOnValidate;
    }
    
    public void setUpdateConfigOnValidate(final boolean updateConfigOnValidate) {
        this.updateConfigOnValidate = updateConfigOnValidate;
    }
    
    public boolean isAllowMultipleSignatures() {
        return this.allowMultipleSignatures;
    }
    
    public void setAllowMultipleSignatures(final boolean allowMultipleSignatures) {
        this.allowMultipleSignatures = allowMultipleSignatures;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)SignatureConfig.class);
    }
    
    public interface SignatureConfigurable
    {
        void setSignatureConfig(final SignatureConfig p0);
    }
}
