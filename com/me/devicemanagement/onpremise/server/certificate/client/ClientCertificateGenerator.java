package com.me.devicemanagement.onpremise.server.certificate.client;

import java.security.GeneralSecurityException;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.SignatureException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import java.security.InvalidKeyException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import java.util.Date;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.Writer;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import java.security.PrivateKey;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import java.security.PublicKey;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.HashMap;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.logging.Logger;

public class ClientCertificateGenerator
{
    private static ClientCertificateGenerator clientCertificateGenerator;
    private static final Logger LOGGER;
    
    private ClientCertificateGenerator() {
    }
    
    public static ClientCertificateGenerator getInstance() {
        if (ClientCertificateGenerator.clientCertificateGenerator == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
            ClientCertificateGenerator.clientCertificateGenerator = new ClientCertificateGenerator();
        }
        return ClientCertificateGenerator.clientCertificateGenerator;
    }
    
    public Boolean generateClientRootCACertificateAndKey() throws Exception {
        ClientCertificateGenerator.LOGGER.info("Going to create Client Root CA crt and key.");
        if (ClientCertAuthBean.getInstance().getClientCertAuthConfig() == null) {
            ClientCertificateUtil.getInstance().loadClientCertAuthConfig();
        }
        final Path clientRootCACertPath = ClientCertificateUtil.getInstance().getClientRootCACertificatePath();
        final Path clientRootCAKeyPath = ClientCertificateUtil.getInstance().getClientRootCAPrivateKeyPath();
        ClientCertificateGenerator.LOGGER.info("Client Cert Path " + clientRootCACertPath);
        ClientCertificateGenerator.LOGGER.info("Client Cert key Path " + clientRootCAKeyPath);
        if (!clientRootCACertPath.equals(Paths.get("", new String[0])) && !clientRootCAKeyPath.equals(Paths.get("", new String[0]))) {
            final Map clientRootCAProps = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("clientCertRootCA");
            if (Files.notExists(clientRootCACertPath.getParent(), new LinkOption[0])) {
                Files.createDirectories(clientRootCACertPath.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            }
            final String pemEncodedClientRootCACertificate = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE");
            final String pemEncodedClientRootPrivateKey = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY");
            final String pemEncodedOldClientRootCACertificate = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE_OLD");
            Boolean writeClientCertificateFromDatabase = Boolean.FALSE;
            Boolean isOldRootCertificateNeedToBeAppended = Boolean.FALSE;
            if (pemEncodedClientRootCACertificate == null || pemEncodedClientRootPrivateKey == null) {
                this.generateClientRootCAKeyPair(clientRootCACertPath, clientRootCAKeyPath, clientRootCAProps);
                ClientCertificateGenerator.LOGGER.info("Client Root CA crt and key has been created successfully.");
                return Boolean.TRUE;
            }
            final Boolean isClientRootCertificateGoingToExpire = ClientCertificateUtil.getInstance().isCertificateGoingToExpire(ClientCertificateUtil.getInstance().getClientRootCACertificate(), 1, 1);
            if (isClientRootCertificateGoingToExpire) {
                ClientCertificateGenerator.LOGGER.info("Client Root CA crt and key is going to expire in next 30 days.So going to regenerate.");
                this.generateClientRootCAKeyPair(clientRootCACertPath, clientRootCAKeyPath, clientRootCAProps);
                ClientCertificateGenerator.LOGGER.info("Client Root CA crt and key has been created successfully.");
                return Boolean.TRUE;
            }
            if ((Files.notExists(clientRootCACertPath, new LinkOption[0]) || Files.notExists(clientRootCAKeyPath, new LinkOption[0])) && pemEncodedClientRootCACertificate != null && pemEncodedClientRootPrivateKey != null) {
                writeClientCertificateFromDatabase = Boolean.TRUE;
            }
            if (pemEncodedOldClientRootCACertificate != null) {
                isOldRootCertificateNeedToBeAppended = ClientCertificateUtil.getInstance().isCertificateGoingToExpire(pemEncodedOldClientRootCACertificate, 5, 0);
                if (!isOldRootCertificateNeedToBeAppended) {
                    ClientCertificateGenerator.LOGGER.log(Level.INFO, "The existing client root certificate has been expired so it not trusted.");
                    SecurityUtil.deleteAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE_OLD");
                    SecurityUtil.deleteAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY_OLD");
                }
            }
            if (!writeClientCertificateFromDatabase) {
                if (!isOldRootCertificateNeedToBeAppended) {
                    return Boolean.FALSE;
                }
            }
            try {
                Files.write(clientRootCACertPath, pemEncodedClientRootCACertificate.getBytes(), new OpenOption[0]);
                if (isOldRootCertificateNeedToBeAppended) {
                    Files.write(clientRootCACertPath, pemEncodedOldClientRootCACertificate.getBytes(), StandardOpenOption.APPEND);
                }
                Files.write(clientRootCAKeyPath, pemEncodedClientRootPrivateKey.getBytes(), new OpenOption[0]);
            }
            catch (final Exception e) {
                ClientCertificateGenerator.LOGGER.log(Level.SEVERE, "Exception while writing client root certificate file.", e);
            }
        }
        return Boolean.FALSE;
    }
    
    public void generateClientRootCAKeyPair(final Path clientRootCACertPath, final Path clientRootCAPrivateKeyPath, final Map clientRootCAProps) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, CertificateException, OperatorCreationException {
        final HashMap<String, String> subject = clientRootCAProps.get("subject");
        final String clientRootRegenCount = SecurityUtil.getSecurityParameter("CLIENT_ROOT_REGENERATION_COUNT");
        String rootCertificateCommonName = subject.get("commonName");
        int regenCount = 0;
        final String regenCountString = Integer.toString(regenCount);
        if (clientRootRegenCount != null) {
            regenCount = Integer.parseInt(clientRootRegenCount);
            ++regenCount;
        }
        if (regenCount != 0) {
            rootCertificateCommonName = rootCertificateCommonName + " - " + regenCount;
            ClientCertificateUtil.updateComponentSettings("CLIENT_ROOT_REGENERATION_COUNT", Integer.toString(regenCount));
        }
        SecurityUtil.updateSecurityParameter("CLIENT_ROOT_REGENERATION_COUNT", regenCountString);
        ClientCertificateGenerator.LOGGER.info("Certificate Going to regenerate with commonName " + rootCertificateCommonName);
        Security.addProvider((Provider)new BouncyCastleProvider());
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, Integer.parseInt(clientRootCAProps.get("validNotAfter").toString()));
        final Date notAfter = cal.getTime();
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        final ECGenParameterSpec ecsp = new ECGenParameterSpec(clientRootCAProps.get("eccCurve").toString());
        keyPairGenerator.initialize(ecsp, new SecureRandom());
        final KeyPair keyPair = keyPairGenerator.genKeyPair();
        final ECPrivateKey ecPrivateKey = (ECPrivateKey)keyPair.getPrivate();
        final ECPublicKey ecPublicKey = (ECPublicKey)keyPair.getPublic();
        final X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        x500NameBuilder.addRDN(BCStyle.C, (String)subject.get("countryCode"));
        x500NameBuilder.addRDN(BCStyle.ST, (String)subject.get("stateOrProvince"));
        x500NameBuilder.addRDN(BCStyle.O, (String)subject.get("organization"));
        x500NameBuilder.addRDN(BCStyle.OU, (String)subject.get("organizationalUnit"));
        x500NameBuilder.addRDN(BCStyle.CN, rootCertificateCommonName);
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(x500NameBuilder.build(), serialNumber, notBefore, notAfter, x500NameBuilder.build(), (PublicKey)ecPublicKey);
        final ContentSigner contentSigner = new JcaContentSignerBuilder(clientRootCAProps.get("signatureAlgorithm").toString()).setProvider("BC").build((PrivateKey)ecPrivateKey);
        builder.addExtension(Extension.basicConstraints, true, (ASN1Encodable)new BasicConstraints(true));
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier((PublicKey)ecPublicKey);
        builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier((PublicKey)ecPublicKey);
        builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
        final KeyUsage keyUsage = new KeyUsage(166);
        builder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
        final X509CertificateHolder holder = builder.build(contentSigner);
        final X509Certificate rootCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        final StringWriter clientRootCACertificateStringWriter = new StringWriter();
        final JcaPEMWriter certWriter = new JcaPEMWriter((Writer)clientRootCACertificateStringWriter);
        certWriter.writeObject((Object)rootCA);
        certWriter.flush();
        certWriter.close();
        final StringWriter clientRootPrivateKeyStringWriter = new StringWriter();
        final JcaPEMWriter keyWriter = new JcaPEMWriter((Writer)clientRootPrivateKeyStringWriter);
        keyWriter.writeObject((Object)ecPrivateKey);
        keyWriter.flush();
        keyWriter.close();
        final String existingClientRootCACertificate = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE");
        final String existingClientRootCAPrivateKey = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY");
        if (existingClientRootCACertificate != null) {
            SecurityUtil.updateAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE_OLD", existingClientRootCACertificate);
        }
        if (existingClientRootCAPrivateKey != null) {
            SecurityUtil.updateAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY_OLD", existingClientRootCAPrivateKey);
        }
        SecurityUtil.updateAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE", clientRootCACertificateStringWriter.toString());
        SecurityUtil.updateAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY", clientRootPrivateKeyStringWriter.toString());
        String base64EncodedCertificate;
        if ((base64EncodedCertificate = existingClientRootCACertificate) != null) {
            try {
                base64EncodedCertificate = base64EncodedCertificate.replace("-----BEGIN CERTIFICATE-----", "");
                base64EncodedCertificate = base64EncodedCertificate.replace("-----END CERTIFICATE-----", "");
                CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCertificate).checkValidity();
                clientRootCACertificateStringWriter.append(existingClientRootCACertificate);
            }
            catch (final CertificateExpiredException | CertificateNotYetValidException exception) {
                ClientCertificateGenerator.LOGGER.log(Level.INFO, "The existing client root certificate has been expired so it not trusted.", exception);
                SecurityUtil.deleteAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE_OLD");
                SecurityUtil.deleteAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY_OLD");
            }
        }
        Files.write(clientRootCACertPath, clientRootCACertificateStringWriter.toString().getBytes(), new OpenOption[0]);
        Files.write(clientRootCAPrivateKeyPath, clientRootPrivateKeyStringWriter.toString().getBytes(), new OpenOption[0]);
    }
    
    public X509Certificate signClientCSR(final PKCS10CertificationRequest csr) throws InvalidKeyException, OperatorCreationException, NoSuchAlgorithmException, CertificateException {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final Map clientCertProps = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("agentClientCertificate");
        try {
            final X509Certificate rootCACert = ClientCertificateUtil.getInstance().getClientRootCACertificate();
            final PrivateKey rootCAKey = ClientCertificateUtil.getInstance().getClientRootPrivateKey();
            final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
            final Calendar cal = Calendar.getInstance();
            final Date notBefore = cal.getTime();
            cal.add(5, Integer.parseInt(clientCertProps.get("validNotAfter").toString()));
            final Date notAfter = cal.getTime();
            final JcaPKCS10CertificationRequest jcaRequest = new JcaPKCS10CertificationRequest(csr);
            final X509v3CertificateBuilder certificateBuilder = (X509v3CertificateBuilder)new JcaX509v3CertificateBuilder(rootCACert, serialNumber, notBefore, notAfter, jcaRequest.getSubject(), jcaRequest.getPublicKey());
            final ContentSigner signer = new JcaContentSignerBuilder(clientCertProps.get("signatureAlgorithm").toString()).setProvider("BC").build(rootCAKey);
            final X509Certificate clientCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateBuilder.build(signer));
            return clientCertificate;
        }
        catch (final CertificateException | InvalidKeyException | NoSuchAlgorithmException | OperatorCreationException e) {
            ClientCertificateGenerator.LOGGER.log(Level.INFO, "exception in generateClientCertificate", e);
            throw e;
        }
    }
    
    public Map<String, String> getClientCertificateAndKeyForSecureGatewayServer() {
        final Map<String, String> certificateAndPrivateKey = new HashMap<String, String>();
        final Map sgsClientCertificateProps = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("sgsClientCertificate");
        try {
            final String pemEncodedSGSClientCert = SecurityUtil.getAdvancedSecurityDetail("SGS_CLIENT_CERTIFICATE");
            final String pemEncodedSGSPrivateKey = SecurityUtil.getAdvancedSecurityDetail("SGS_CLIENT_PRIVATE_KEY");
            String base64EncodedCertificate = pemEncodedSGSClientCert;
            if (base64EncodedCertificate != null && pemEncodedSGSPrivateKey != null) {
                base64EncodedCertificate = base64EncodedCertificate.replace("-----BEGIN CERTIFICATE-----", "");
                base64EncodedCertificate = base64EncodedCertificate.replace("-----END CERTIFICATE-----", "");
                final X509Certificate sgsClientX509Certificate = CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCertificate);
                final PrivateKey sgsClientPrivateKey = ClientCertificateUtil.getInstance().loadECPrivateKey(pemEncodedSGSPrivateKey);
                final X509Certificate rootCACertificate = ClientCertificateUtil.getInstance().getClientRootCACertificate();
                sgsClientX509Certificate.verify(rootCACertificate.getPublicKey());
                if (!ClientCertificateUtil.getInstance().isCertificateGoingToExpire(sgsClientX509Certificate, 5, 30)) {
                    certificateAndPrivateKey.put("client.crt", pemEncodedSGSClientCert);
                    certificateAndPrivateKey.put("client.key", pemEncodedSGSPrivateKey);
                    return certificateAndPrivateKey;
                }
                ClientCertificateGenerator.LOGGER.info("SGS Client certificate is gong to expire so generating new keypair.");
            }
        }
        catch (final SignatureException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException verifyException) {
            ClientCertificateGenerator.LOGGER.log(Level.SEVERE, "Secure Gateway Server Client SignatureException  ", verifyException);
            ClientCertificateGenerator.LOGGER.warning("The client root certificate has been regenerated to going to regenerate sgs certificate.");
        }
        catch (final Exception e) {
            ClientCertificateGenerator.LOGGER.log(Level.SEVERE, "Exception occured while parsing pem encoded sgs client certificate. So regenerating new certificate.", e);
        }
        try {
            Security.addProvider((Provider)new BouncyCastleProvider());
            final X509Certificate rootCACert = ClientCertificateUtil.getInstance().getClientRootCACertificate();
            final PrivateKey rootCAKey = ClientCertificateUtil.getInstance().getClientRootPrivateKey();
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
            final ECGenParameterSpec ecsp = new ECGenParameterSpec(sgsClientCertificateProps.get("eccCurve").toString());
            keyPairGenerator.initialize(ecsp, new SecureRandom());
            final KeyPair keyPair = keyPairGenerator.genKeyPair();
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)keyPair.getPrivate();
            final ECPublicKey ecPublicKey = (ECPublicKey)keyPair.getPublic();
            final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
            final Calendar cal = Calendar.getInstance();
            final Date notBefore = cal.getTime();
            cal.add(5, Integer.parseInt(sgsClientCertificateProps.get("validNotAfter").toString()));
            final Date notAfter = cal.getTime();
            final HashMap<String, String> subject = sgsClientCertificateProps.get("subject");
            final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
            builderex.addRDN(BCStyle.C, (String)subject.get("countryCode"));
            builderex.addRDN(BCStyle.ST, (String)subject.get("stateOrProvince"));
            builderex.addRDN(BCStyle.OU, (String)subject.get("organizationalUnit"));
            builderex.addRDN(BCStyle.O, (String)subject.get("organization"));
            builderex.addRDN(BCStyle.CN, (String)subject.get("commonName"));
            final JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new JcaX509CertificateHolder(rootCACert).getSubject(), serialNumber, notBefore, notAfter, builderex.build(), (PublicKey)ecPublicKey);
            final ContentSigner contentSigner = new JcaContentSignerBuilder(sgsClientCertificateProps.get("signatureAlgorithm").toString()).setProvider("BC").build(rootCAKey);
            builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(false));
            final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier((PublicKey)ecPublicKey);
            builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
            final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(rootCACert.getPublicKey());
            builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
            final KeyUsage keyUsage = new KeyUsage(160);
            builder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
            final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_clientAuth };
            builder.addExtension(Extension.extendedKeyUsage, true, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
            final X509CertificateHolder holder = builder.build(contentSigner);
            final X509Certificate clientCertificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
            final StringWriter clientCrtWriter = new StringWriter();
            final JcaPEMWriter clientCertificatewriter = new JcaPEMWriter((Writer)clientCrtWriter);
            clientCertificatewriter.writeObject((Object)clientCertificate);
            clientCertificatewriter.flush();
            clientCertificatewriter.close();
            final StringWriter clientPrivateKeyWriter = new StringWriter();
            final JcaPEMWriter writer = new JcaPEMWriter((Writer)clientPrivateKeyWriter);
            writer.writeObject((Object)ecPrivateKey);
            writer.flush();
            writer.close();
            final String pemEncodedSGSClientCert = clientCrtWriter.toString();
            final String pemEncodedSGSPrivateKey = clientPrivateKeyWriter.toString();
            SecurityUtil.updateAdvancedSecurityDetail("SGS_CLIENT_CERTIFICATE", pemEncodedSGSClientCert);
            SecurityUtil.updateAdvancedSecurityDetail("SGS_CLIENT_PRIVATE_KEY", pemEncodedSGSPrivateKey);
            certificateAndPrivateKey.put("client.crt", pemEncodedSGSClientCert);
            certificateAndPrivateKey.put("client.key", pemEncodedSGSPrivateKey);
        }
        catch (final Exception ex) {
            ClientCertificateGenerator.LOGGER.log(Level.SEVERE, "Error in generating Client certificate for Secure Gateway Server.", ex);
        }
        return certificateAndPrivateKey;
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        ClientCertificateGenerator.clientCertificateGenerator = null;
        LOGGER = Logger.getLogger("AgentServerAuthLogger");
    }
}
