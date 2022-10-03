package com.adventnet.authentication.saml.settings;

import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import com.adventnet.authentication.saml.util.SamlUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.URL;
import java.util.logging.Logger;

public class SamlSettings
{
    private static final Logger LOGGER;
    private String spEntityId;
    private URL spAssertionConsumerServiceUrl;
    private String spAssertionConsumerServiceBinding;
    private URL spSingleLogoutServiceUrl;
    private String spSingleLogoutServiceBinding;
    private X509Certificate spX509cert;
    private PrivateKey spPrivateKey;
    private String spNameIDFormat;
    private String idpEntityId;
    private URL idpSingleSignOnServiceUrl;
    private URL idpSingleLogoutServiceUrl;
    private URL idpSingleLogoutServiceResponseUrl;
    private X509Certificate idpx509cert;
    private Boolean authnRequestsSigned;
    private Boolean logoutRequestSigned;
    private Boolean logoutResponseSigned;
    private String signatureAlgorithm;
    private Boolean allPropertiesLoaded;
    private Properties prop;
    
    public SamlSettings(final Properties props) {
        this.spEntityId = "me.com";
        this.spAssertionConsumerServiceUrl = null;
        this.spAssertionConsumerServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
        this.spSingleLogoutServiceUrl = null;
        this.spSingleLogoutServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
        this.spX509cert = null;
        this.spPrivateKey = null;
        this.spNameIDFormat = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
        this.idpEntityId = "";
        this.idpSingleSignOnServiceUrl = null;
        this.idpSingleLogoutServiceUrl = null;
        this.idpSingleLogoutServiceResponseUrl = null;
        this.idpx509cert = null;
        this.authnRequestsSigned = false;
        this.logoutRequestSigned = false;
        this.logoutResponseSigned = false;
        this.signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        this.allPropertiesLoaded = false;
        this.prop = new Properties();
        this.prop = props;
        this.loadSpSetting();
        this.loadIdpSetting();
        this.finishedLoadingProps(true);
    }
    
    public final String getSpEntityId() {
        return this.spEntityId;
    }
    
    public final URL getSpAssertionConsumerServiceUrl() {
        return this.spAssertionConsumerServiceUrl;
    }
    
    public final String getSpAssertionConsumerServiceBinding() {
        return this.spAssertionConsumerServiceBinding;
    }
    
    public final URL getSpSingleLogoutServiceUrl() {
        return this.spSingleLogoutServiceUrl;
    }
    
    public final String getSpSingleLogoutServiceBinding() {
        return this.spSingleLogoutServiceBinding;
    }
    
    public final String getSpNameIDFormat() {
        return this.spNameIDFormat;
    }
    
    public final X509Certificate getSPcert() {
        return this.spX509cert;
    }
    
    public final PrivateKey getSPkey() {
        return this.spPrivateKey;
    }
    
    public final String getIdpEntityId() {
        return this.idpEntityId;
    }
    
    public final URL getIdpSingleSignOnServiceUrl() {
        return this.idpSingleSignOnServiceUrl;
    }
    
    public final URL getIdpSingleLogoutServiceUrl() {
        return this.idpSingleLogoutServiceUrl;
    }
    
    public final URL getIdpSingleLogoutServiceResponseUrl() {
        if (this.idpSingleLogoutServiceResponseUrl == null) {
            return this.getIdpSingleLogoutServiceUrl();
        }
        return this.idpSingleLogoutServiceResponseUrl;
    }
    
    public final X509Certificate getIdpx509cert() {
        return this.idpx509cert;
    }
    
    public Boolean getAuthnRequestsSigned() {
        return this.authnRequestsSigned;
    }
    
    public Boolean getLogoutRequestSigned() {
        return this.logoutRequestSigned;
    }
    
    public Boolean getLogoutResponseSigned() {
        return this.logoutResponseSigned;
    }
    
    public String getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }
    
    public Boolean arePropsLoaded() {
        return this.allPropertiesLoaded;
    }
    
    public final void setIdpEntityId(final String idpEntityId) {
        this.idpEntityId = idpEntityId;
    }
    
    public void setAuthnRequestsSigned(final Boolean authnRequestsSigned) {
        this.authnRequestsSigned = authnRequestsSigned;
    }
    
    private void setSignatureAlgorithm(final String signatureAlgorithm) {
        switch (signatureAlgorithm) {
            case "DSA_SHA1": {
                this.signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
                break;
            }
            case "RSA_SHA1": {
                this.signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                break;
            }
            case "RSA_SHA256": {
                this.signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
                break;
            }
            case "RSA_SHA384": {
                this.signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
                break;
            }
            case "RSA_SHA512": {
                this.signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
                break;
            }
        }
        if (this.signatureAlgorithm != null && this.signatureAlgorithm.equals("")) {
            this.signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
    }
    
    void finishedLoadingProps(final boolean allPropertiesLoaded) {
        this.allPropertiesLoaded = allPropertiesLoaded;
    }
    
    public List<String> checkSettings() {
        final List<String> errors = new ArrayList<String>(this.checkSPSettings());
        errors.addAll(this.checkIdPSettings());
        return errors;
    }
    
    private List<String> checkIdPSettings() {
        final List<String> errors = new ArrayList<String>();
        if (!this.checkRequired(this.getIdpSingleSignOnServiceUrl())) {
            final String errorMsg = "idp_sso_url_invalid";
            errors.add(errorMsg);
            SamlSettings.LOGGER.severe(errorMsg);
        }
        if (this.getIdpx509cert() == null) {
            final String errorMsg = "idp_cert_not_found_and_required";
            errors.add(errorMsg);
            SamlSettings.LOGGER.severe(errorMsg);
        }
        return errors;
    }
    
    private List<String> checkSPSettings() {
        final List<String> errors = new ArrayList<String>();
        if (!this.checkRequired(this.getSpEntityId())) {
            final String errorMsg = "sp_entityId_not_found";
            errors.add(errorMsg);
            SamlSettings.LOGGER.severe(errorMsg);
        }
        if (!this.checkRequired(this.getSpAssertionConsumerServiceUrl())) {
            final String errorMsg = "sp_acs_url_not_found";
            errors.add(errorMsg);
            SamlSettings.LOGGER.severe(errorMsg);
        }
        if ((this.getAuthnRequestsSigned() || this.getLogoutRequestSigned() || this.getLogoutResponseSigned()) && !this.checkSPCerts()) {
            final String errorMsg = "sp_cert_not_found_and_required";
            errors.add(errorMsg);
            SamlSettings.LOGGER.severe(errorMsg);
        }
        return errors;
    }
    
    private boolean checkSPCerts() {
        final X509Certificate cert = this.getSPcert();
        final PrivateKey pKey = this.getSPkey();
        return cert != null && pKey != null;
    }
    
    private boolean checkRequired(final Object value) {
        return value != null && (!(value instanceof String) || !((String)value).isEmpty());
    }
    
    private Boolean loadBooleanProperty(final String propertyKey) {
        final String booleanPropValue = this.prop.getProperty(propertyKey);
        if (booleanPropValue != null) {
            return Boolean.parseBoolean(booleanPropValue.trim());
        }
        return null;
    }
    
    private void loadIdpSetting() {
        final URL idpSingleSignOnServiceUrl = this.loadURLProperty("saml.idp.single_sign_on_service.url");
        if (idpSingleSignOnServiceUrl != null) {
            this.idpSingleSignOnServiceUrl = idpSingleSignOnServiceUrl;
        }
        final URL idpSingleLogoutServiceUrl = this.loadURLProperty("saml.idp.single_logout_service.url");
        if (idpSingleLogoutServiceUrl != null) {
            this.idpSingleLogoutServiceUrl = idpSingleLogoutServiceUrl;
        }
        final URL idpSingleLogoutServiceResponseUrl = this.loadURLProperty("saml.idp.single_logout_service.response.url");
        if (idpSingleLogoutServiceResponseUrl != null) {
            this.idpSingleLogoutServiceResponseUrl = idpSingleLogoutServiceResponseUrl;
        }
        final X509Certificate idpX509cert = this.loadCertificateFromProp("saml.idp.x509cert");
        if (idpX509cert != null) {
            this.idpx509cert = idpX509cert;
        }
        final String signatureAlgorithm = this.loadStringProperty("saml.security.signature_algorithm");
        if (signatureAlgorithm != null && !signatureAlgorithm.isEmpty()) {
            this.setSignatureAlgorithm(signatureAlgorithm);
        }
        final Boolean logoutRequestSigned = this.loadBooleanProperty("saml.security.logoutrequest_signed");
        if (logoutRequestSigned != null) {
            this.logoutRequestSigned = logoutRequestSigned;
        }
        final Boolean logoutResponseSigned = this.loadBooleanProperty("saml.security.logoutresponse_signed");
        if (logoutResponseSigned != null) {
            this.logoutResponseSigned = logoutResponseSigned;
        }
        final String idpEntityId = this.loadStringProperty("saml.idp.entityid");
        if (idpEntityId != null && !idpEntityId.isEmpty()) {
            this.idpEntityId = idpEntityId;
        }
    }
    
    private void loadSpSetting() {
        final String spEntityID = this.loadStringProperty("saml.sp.entityid");
        if (spEntityID != null) {
            this.spEntityId = spEntityID;
        }
        final URL assertionConsumerServiceUrl = this.loadURLProperty("saml.sp.assertion_consumer_service.url");
        if (assertionConsumerServiceUrl != null) {
            this.spAssertionConsumerServiceUrl = assertionConsumerServiceUrl;
        }
        final String spAssertionConsumerServiceBinding = this.loadStringProperty("saml.sp.assertion_consumer_service.binding");
        if (spAssertionConsumerServiceBinding != null) {
            this.spAssertionConsumerServiceBinding = spAssertionConsumerServiceBinding;
        }
        final URL spSingleLogoutServiceUrl = this.loadURLProperty("saml.sp.single_logout_service.url");
        if (spSingleLogoutServiceUrl != null) {
            this.spSingleLogoutServiceUrl = spSingleLogoutServiceUrl;
        }
        final String spSingleLogoutServiceBinding = this.loadStringProperty("saml.sp.single_logout_service.binding");
        if (spSingleLogoutServiceBinding != null) {
            this.spSingleLogoutServiceBinding = spSingleLogoutServiceBinding;
        }
        final String spNameIDFormat = this.loadStringProperty("saml.sp.nameidformat");
        if (spNameIDFormat != null && !spNameIDFormat.isEmpty()) {
            this.spNameIDFormat = spNameIDFormat;
        }
        final X509Certificate spX509cert = this.loadCertificateFromProp("saml.sp.x509cert");
        if (spX509cert != null) {
            this.spX509cert = spX509cert;
        }
        final PrivateKey spPrivateKey = this.loadPrivateKeyFromProp("saml.sp.privatekey");
        if (spPrivateKey != null) {
            this.spPrivateKey = spPrivateKey;
        }
        final Boolean authnRequestsSigned = this.loadBooleanProperty("saml.security.authnrequest_signed");
        if (authnRequestsSigned != null) {
            this.authnRequestsSigned = authnRequestsSigned;
        }
    }
    
    private X509Certificate loadCertificateFromProp(final String propertyKey) {
        final String certString = this.prop.getProperty(propertyKey);
        if (certString == null || certString.isEmpty()) {
            return null;
        }
        try {
            return SamlUtil.loadCert(certString);
        }
        catch (final CertificateException e) {
            SamlSettings.LOGGER.log(Level.SEVERE, "Error loading certificate from properties.", e);
            return null;
        }
    }
    
    private PrivateKey loadPrivateKeyFromProp(final String propertyKey) {
        final String keyString = this.prop.getProperty(propertyKey);
        if (keyString == null || keyString.isEmpty()) {
            return null;
        }
        try {
            return SamlUtil.loadPrivateKey(keyString);
        }
        catch (final Exception e) {
            SamlSettings.LOGGER.log(Level.SEVERE, "Error loading privatekey from properties.", e);
            return null;
        }
    }
    
    private URL loadURLProperty(final String propertyKey) {
        final String urlPropValue = this.prop.getProperty(propertyKey);
        if (urlPropValue == null || urlPropValue.isEmpty()) {
            return null;
        }
        try {
            return new URL(urlPropValue.trim());
        }
        catch (final MalformedURLException e) {
            SamlSettings.LOGGER.log(Level.SEVERE, "'" + propertyKey + "' contains malformed url.", e);
            return null;
        }
    }
    
    private String loadStringProperty(final String propertyKey) {
        String propValue = this.prop.getProperty(propertyKey);
        if (propValue != null) {
            propValue = propValue.trim();
        }
        return propValue;
    }
    
    static {
        LOGGER = Logger.getLogger(SamlSettings.class.getName());
    }
}
