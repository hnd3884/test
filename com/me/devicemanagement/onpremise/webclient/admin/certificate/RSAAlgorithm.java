package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.OutputStream;
import java.io.File;
import java.security.cert.Certificate;
import java.security.Key;
import java.security.KeyStore;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.NoSuchProviderException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.asn1.ASN1Sequence;
import org.apache.commons.codec.binary.Base64;
import java.security.interfaces.RSAPrivateKey;
import java.io.IOException;
import java.util.logging.Level;
import java.security.interfaces.RSAPublicKey;
import java.nio.charset.Charset;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.logging.Logger;

public class RSAAlgorithm implements CertificateAlgorithm
{
    private static Logger logger;
    private static String apacheLocation;
    private String sourceClass;
    private static FileAccessAPI fileAccessAPI;
    
    public RSAAlgorithm() {
        this.sourceClass = "RSAAlgorithm";
    }
    
    @Override
    public boolean verifySignature(final String serverCertificateFile, final String serverCertificateKey) {
        final String sourceMethod = "verifySignature";
        try {
            RSAAlgorithm.logger.info("Verifying if the server certificate and key file matches..");
            final byte[] keyBytes = RSAAlgorithm.fileAccessAPI.readFileContentAsArray(serverCertificateKey);
            String keyContent = new String(keyBytes, Charset.forName("US-ASCII"));
            keyContent = keyContent.replaceAll("(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "");
            keyContent = keyContent.replaceAll("\n", "");
            final String certificateContent = CertificateUtil.trimCertificate(serverCertificateFile);
            if (certificateContent == null) {
                RSAAlgorithm.logger.severe("Certificate file not read properly..Not of the expected format.. BEGIN certificate label not found");
                return false;
            }
            final RSAPublicKey publicKey = (RSAPublicKey)this.loadPublicKeyFromFile(serverCertificateFile);
            if (publicKey == null) {
                return false;
            }
            final RSAPrivateKey privKey = this.loadPrivateKeyFromFileContent(keyContent);
            if (publicKey != null && privKey != null) {
                return publicKey.getModulus().compareTo(privKey.getModulus()) == 0;
            }
            if (publicKey != null && privKey == null) {
                RSAAlgorithm.logger.info("Previous attempt failed.. Trying out the new one");
                return this.validateCertificateAndKey(serverCertificateFile, serverCertificateKey);
            }
            RSAAlgorithm.logger.severe("Server Certificate and key file's format is not supported by this tool");
            return false;
        }
        catch (final IOException ex) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, "Signature verification failed.. This could be because the file is in use", ex);
        }
        catch (final Exception ex2) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, "Interrupted while reading the certificate..Signature verificate failed..", ex2);
        }
        RSAAlgorithm.logger.severe("Server Certificate and key file's format is not supported by this tool");
        return false;
    }
    
    public RSAPrivateKey loadPrivateKeyFromFileContent(final String privateKeyString) {
        final String sourceMethod = "loadPrivateKeyFromFileContent";
        try {
            final byte[] asn1PrivateKeyBytes = Base64.decodeBase64(privateKeyString.getBytes("US-ASCII"));
            final RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence)ASN1Sequence.fromByteArray(asn1PrivateKeyBytes));
            final RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            final RSAPrivateKey privKey = (RSAPrivateKey)kf.generatePrivate(rsaPrivKeySpec);
            return privKey;
        }
        catch (final IOException ex) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, " key file may be in use/not available", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, "Tool doesn't support this algorithm for now", ex2);
        }
        catch (final InvalidKeySpecException ex3) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, " Failed to handle the key spec.. Tool may not support this algorithm", ex3);
        }
        return null;
    }
    
    @Override
    public PublicKey loadPublicKeyFromFile(final String publicKeyFile) {
        final X509Certificate cert = CertificateUtil.getInstance().generateCertificateFromFile(publicKeyFile);
        if (cert == null) {
            return null;
        }
        if (cert.getSigAlgName().contains("RSA")) {
            return cert.getPublicKey();
        }
        RSAAlgorithm.logger.severe("Signature algorithm not supported..");
        return null;
    }
    
    @Override
    public PrivateKey loadPrivateKeyFromFile(final String privateKeyFile) {
        final String sourceMethod = "loadPrivateKeyFromFile";
        PemReader reader = null;
        PrivateKey caKey = null;
        InputStreamReader privateKeyStream = null;
        try {
            privateKeyStream = new InputStreamReader(RSAAlgorithm.fileAccessAPI.readFile(privateKeyFile));
            reader = new PemReader((Reader)privateKeyStream);
            final PKCS8EncodedKeySpec caKeySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
            final KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            caKey = kf.generatePrivate(caKeySpec);
        }
        catch (final IOException ex) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", "Private key file may have been in use/not available..", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", " This tool supports only RSA Algorithm.. Send the logs to desktopcentral support", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", " Provider of the algorithm is not supported.. Send the logs to desktopcentral support", ex3);
        }
        catch (final InvalidKeySpecException ex4) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", " The Private key may have been in different algorithm..Send the logs to desktopcentral support", ex4);
        }
        catch (final Exception ex5) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", "Reading private key file failed..", ex5);
        }
        finally {
            try {
                if (privateKeyStream != null) {
                    privateKeyStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException ex6) {
                RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, "loadPrivateKeyFromFile", "IO Exception in closing the reader stream", ex6);
            }
        }
        return caKey;
    }
    
    public boolean validateCertificateAndKey(final String certFile, final String keyFile) {
        final String sourceMethod = "validateCertificateAndKey";
        OutputStream fout = null;
        PemReader pemreader = null;
        try {
            Security.addProvider((Provider)new BouncyCastleProvider());
            pemreader = new PemReader((Reader)new InputStreamReader(RSAAlgorithm.fileAccessAPI.readFile(certFile)));
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
            final X509Certificate signedCertificate = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(pemreader.readPemObject().getContent()));
            final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(null, null);
            final X509Certificate[] chain = { signedCertificate };
            final PrivateKey privateKey = this.loadPrivateKeyFromFile(keyFile);
            store.setKeyEntry("APNsCertificate", privateKey, "keystore".toCharArray(), chain);
            fout = RSAAlgorithm.fileAccessAPI.writeFile(RSAAlgorithm.apacheLocation + File.separator + "keystore.key");
            store.store(fout, "surendhar".toCharArray());
            return true;
        }
        catch (final Exception ex) {
            RSAAlgorithm.logger.logp(Level.SEVERE, this.sourceClass, sourceMethod, "validation of server certificate and key failed..", ex);
            try {
                if (fout != null) {
                    fout.close();
                }
                if (pemreader != null) {
                    pemreader.close();
                }
            }
            catch (final Exception exp) {
                RSAAlgorithm.logger.info("you can ignore this..exception in closing the stream..");
            }
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (pemreader != null) {
                    pemreader.close();
                }
            }
            catch (final Exception exp2) {
                RSAAlgorithm.logger.info("you can ignore this..exception in closing the stream..");
            }
        }
        return false;
    }
    
    static {
        RSAAlgorithm.logger = Logger.getLogger("ImportCertificateLogger");
        RSAAlgorithm.apacheLocation = System.getProperty("server.home") + File.separator + "apache";
        RSAAlgorithm.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
    }
}
