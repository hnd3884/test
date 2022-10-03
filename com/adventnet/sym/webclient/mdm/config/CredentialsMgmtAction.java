package com.adventnet.sym.webclient.mdm.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import java.security.cert.Certificate;
import java.util.StringTokenizer;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import org.json.JSONObject;
import java.util.Enumeration;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.InputStream;
import java.util.logging.Level;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class CredentialsMgmtAction
{
    public static Logger logger;
    public static final Long CERTIFICATE_PASSWORD_ERROR;
    public static final Long CERTIFICATE_PARSING_ERROR_CODE;
    public static final Long CERTIFICATE_EXPIRED_ERROR_CODE;
    public static final Long CERTIFICATE_ALREADY_EXIST_ERROR_CODE;
    public static final Long CERTIFICATE_ADDED_ERROR_CODE;
    public static final Long RA_CERTIFICATE_MATCH_NOT_FOUND;
    public static final Long DIGICERT_DEPENDENCIES_MISSING;
    
    public static X509Certificate readX509Certificate(final String apnsCertificateFilePath) {
        X509Certificate apnsCertificate = null;
        InputStream stream = null;
        try {
            final byte[] certificateBytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(apnsCertificateFilePath);
            stream = new ByteArrayInputStream(certificateBytes);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            apnsCertificate = (X509Certificate)certFactory.generateCertificate(stream);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex) {
                CredentialsMgmtAction.logger.log(Level.WARNING, "Exception closing InputStream stream", ex.getMessage());
            }
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex2) {
                CredentialsMgmtAction.logger.log(Level.WARNING, "Exception closing InputStream stream", ex2.getMessage());
            }
        }
        return apnsCertificate;
    }
    
    public static X509Certificate readCertificateFromPKCS12(final String pkcs12File, final String password) throws Exception {
        X509Certificate certificate = null;
        InputStream stream = null;
        try {
            final byte[] certificateBytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(pkcs12File);
            stream = new ByteArrayInputStream(certificateBytes);
            Security.addProvider((Provider)new BouncyCastleProvider());
            final KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
            ks.load(stream, password.toCharArray());
            final Enumeration enume = ks.aliases();
            while (enume.hasMoreElements()) {
                final String alias = enume.nextElement();
                if (ks.isKeyEntry(alias)) {
                    certificate = (X509Certificate)ks.getCertificate(alias);
                }
            }
        }
        catch (final Exception exp) {
            CredentialsMgmtAction.logger.log(Level.SEVERE, "Exception in reading PKCS12 Certificate", exp);
            throw exp;
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex) {
                CredentialsMgmtAction.logger.log(Level.WARNING, "Exception closing InputStream stream", ex.getMessage());
            }
        }
        return certificate;
    }
    
    public static JSONObject extractCertificateDetails(final String certificateFile, final String password) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (certificateFile != null) {
                X509Certificate certificate = null;
                if (certificateFile.toLowerCase().endsWith(".p12") || certificateFile.toLowerCase().endsWith(".pfx")) {
                    certificate = readCertificateFromPKCS12(certificateFile, password);
                }
                else if (certificateFile.toLowerCase().endsWith(".p7b")) {
                    final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(certificateFile);
                    final Certificate[] certificates = CertificateUtil.convertInputStreamToX509CertificateChain(inputStream);
                    certificate = (X509Certificate)certificates[0];
                }
                else {
                    certificate = readX509Certificate(certificateFile);
                }
                if (certificate != null) {
                    final X500Principal issuerPrincipal = certificate.getIssuerX500Principal();
                    final String issuerDistinguishedName = issuerPrincipal.getName();
                    if (issuerDistinguishedName != null) {
                        final String[] strIssuerNameArray = issuerDistinguishedName.split(",");
                        for (int issuerNameIndex = 0; issuerNameIndex < strIssuerNameArray.length; ++issuerNameIndex) {
                            final String issuerName = strIssuerNameArray[issuerNameIndex];
                            final String[] strArray = issuerName.split("=");
                            jsonObject.put("CERTIFICATE_ISSUER_DN", (Object)issuerDistinguishedName);
                            if (issuerName.startsWith("CN=")) {
                                jsonObject.put("IssuerName", (Object)strArray[1]);
                            }
                            else if (issuerName.startsWith("OU=")) {
                                jsonObject.put("IssuerOrganizationalUnitName", (Object)strArray[1]);
                            }
                            else if (issuerName.startsWith("O=")) {
                                jsonObject.put("IssuerOrganizationName", (Object)strArray[1]);
                            }
                        }
                    }
                    final Date notAfter = certificate.getNotAfter();
                    final Date notBefore = certificate.getNotBefore();
                    final BigInteger serialNumber = certificate.getSerialNumber();
                    final String fingerPrint = CertificateUtils.getCertificateFingerPrint(certificate);
                    jsonObject.put("CERTIFICATE_THUMB_PRINT", (Object)fingerPrint);
                    jsonObject.put("CERTIFICATE_SERIAL_NUMBER", (Object)serialNumber);
                    jsonObject.put("NotAfter", notAfter.getTime());
                    jsonObject.put("NotBefore", notBefore.getTime());
                    jsonObject.put("serialNumber", (Object)serialNumber);
                    final String subjectName = certificate.getSubjectDN().getName();
                    jsonObject.put("CERTIFICATE_SUBJECT_DN", (Object)subjectName);
                    if (subjectName != null) {
                        final StringTokenizer tokenizer = new StringTokenizer(subjectName, ",");
                        while (tokenizer.hasMoreElements()) {
                            final String token = (String)tokenizer.nextElement();
                            final String[] strArray2 = token.split("=");
                            if (token.trim().startsWith("CN=")) {
                                jsonObject.put("CommonName", (Object)strArray2[1]);
                            }
                            else if (token.trim().startsWith("OU=")) {
                                jsonObject.put("SubjectOrganizationalUnitName", (Object)strArray2[1]);
                            }
                            else {
                                if (!token.trim().startsWith("O=")) {
                                    continue;
                                }
                                jsonObject.put("SubjectOrganizationName", (Object)strArray2[1]);
                            }
                        }
                    }
                    if (!jsonObject.has("CommonName")) {
                        final String sanAltName = getSingleSubjectAlterNativeName(certificate);
                        if (jsonObject.has("SubjectOrganizationName")) {
                            jsonObject.put("CommonName", jsonObject.get("SubjectOrganizationName"));
                        }
                        else if (jsonObject.has("SubjectOrganizationalUnitName")) {
                            jsonObject.put("CommonName", jsonObject.get("SubjectOrganizationalUnitName"));
                        }
                        else if (sanAltName != null) {
                            jsonObject.put("CommonName", (Object)sanAltName);
                        }
                        else if (jsonObject.has("IssuerName")) {
                            jsonObject.put("CommonName", jsonObject.get("IssuerName"));
                        }
                        else if (jsonObject.has("IssuerOrganizationName")) {
                            jsonObject.put("CommonName", jsonObject.get("IssuerOrganizationName"));
                        }
                        else if (jsonObject.has("IssuerOrganizationalUnitName")) {
                            jsonObject.put("CommonName", jsonObject.get("IssuerOrganizationalUnitName"));
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(CredentialsMgmtAction.class.getName()).log(Level.SEVERE, "extractCertificateDetails:Exception while Extracting certificate Details", ex);
            throw ex;
        }
        return jsonObject;
    }
    
    public static String getSingleSubjectAlterNativeName(final X509Certificate certificate) {
        String sanField = null;
        try {
            final Collection sanNamesColln = certificate.getSubjectAlternativeNames();
            final Map<Integer, String> map = new HashMap<Integer, String>();
            if (sanNamesColln != null && !sanNamesColln.isEmpty()) {
                for (final List oidParList : sanNamesColln) {
                    map.put(oidParList.get(0), oidParList.get(1).toString());
                }
            }
            if (!map.isEmpty()) {
                if (map.containsKey(1)) {
                    sanField = map.get(1);
                }
                else if (map.containsKey(2)) {
                    sanField = map.get(2);
                }
                else if (map.containsKey(3)) {
                    sanField = map.get(3);
                }
                else if (map.containsKey(4)) {
                    sanField = map.get(4);
                }
                else if (map.containsKey(0)) {
                    sanField = map.get(0);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(CredentialsMgmtAction.class.getName()).log(Level.SEVERE, "Exception in getSingleSubjectAlterNativeName", ex);
        }
        return sanField;
    }
    
    static {
        CredentialsMgmtAction.logger = Logger.getLogger(CredentialsMgmtAction.class.getName());
        CERTIFICATE_PASSWORD_ERROR = -4L;
        CERTIFICATE_PARSING_ERROR_CODE = -3L;
        CERTIFICATE_EXPIRED_ERROR_CODE = -2L;
        CERTIFICATE_ALREADY_EXIST_ERROR_CODE = -1L;
        CERTIFICATE_ADDED_ERROR_CODE = 1L;
        RA_CERTIFICATE_MATCH_NOT_FOUND = -5L;
        DIGICERT_DEPENDENCIES_MISSING = -6L;
        if (Security.getProvider("BC") == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
        }
    }
}
