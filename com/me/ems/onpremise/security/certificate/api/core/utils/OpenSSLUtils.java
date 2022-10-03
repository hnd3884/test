package com.me.ems.onpremise.security.certificate.api.core.utils;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.Iterator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import java.io.Reader;
import org.bouncycastle.openssl.PEMParser;
import java.io.FileReader;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.x509.GeneralNames;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.io.Writer;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import java.io.FileWriter;
import java.io.File;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.util.logging.Level;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.certificate.CSRAttributes;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import java.util.Scanner;
import java.util.logging.Logger;

public class OpenSSLUtils
{
    private static Logger consoleLogger;
    
    public static void main(final String[] args) {
        readCSRInfo();
    }
    
    public static void getPrivateKeyDetails() throws Exception {
        final Scanner scanner = new Scanner(System.in);
        DCConsoleOut.print("Encrypted Key File Name[]:");
        final String encryptedKeyPath = scanner.nextLine();
        DCConsoleOut.print("PEM Passphrase[]:");
        final char[] password = System.console().readPassword();
        try {
            decryptPrivateKey(encryptedKeyPath, password);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new Exception("Exception in decryting private key file", ex);
        }
    }
    
    public static void readCSRInfo() {
        final Scanner scanner = new Scanner(System.in);
        final CSRAttributes csrAttributes = new CSRAttributes();
        final ArrayList<String> sanNames = new ArrayList<String>();
        DCConsoleOut.print("Country Name (2 letter code) [AU]:");
        csrAttributes.setCountry(scanner.nextLine());
        DCConsoleOut.print("State or Province Name (full name) [Some-State]:");
        csrAttributes.setState(scanner.nextLine());
        DCConsoleOut.print("Locality Name (eg, city) []:");
        csrAttributes.setLocality(scanner.nextLine());
        DCConsoleOut.print("Organization Name (eg, company) [Internet Widgits Pty Ltd]:");
        csrAttributes.setOrganizationName(scanner.nextLine());
        DCConsoleOut.print("Organizational Unit Name (eg, section) []:");
        csrAttributes.setOrganizationalUnit(scanner.nextLine());
        DCConsoleOut.print("Common Name (e.g. server FQDN or YOUR name) []:");
        csrAttributes.setCommonName(scanner.nextLine());
        DCConsoleOut.print("Subject Alternative Names (e.g. *.zoho.com,manageengine.com) (Press Enter with empty name to end. )\n");
        while (true) {
            final String sanName = scanner.nextLine();
            if (sanName.equalsIgnoreCase("")) {
                break;
            }
            sanNames.add(sanName);
        }
        csrAttributes.setSanNames(sanNames);
        try {
            generateCSR(csrAttributes);
            DCConsoleOut.println("CSR and Key Files Generated successfully . File Names are server.csr and server.key inside bin folder");
        }
        catch (final Exception ex) {
            OpenSSLUtils.consoleLogger.log(Level.INFO, "Exception in generating CSR and key file " + ex);
            DCConsoleOut.println("Error while generating CSR and Key. Contact support with server logs");
        }
    }
    
    public static void generateCSR(final CSRAttributes attributes) throws Exception {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048, new SecureRandom());
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        final PrivateKey privateKey = keyPair.getPrivate();
        final PublicKey publicKey = keyPair.getPublic();
        String subjectPrincipal = "";
        final String commaSpace = ", ";
        final GeneralNames subjectAlternateNames = toGeneralName(attributes.getSanNames());
        final ExtensionsGenerator extGen = new ExtensionsGenerator();
        extGen.addExtension(Extension.subjectAlternativeName, false, (ASN1Encodable)subjectAlternateNames.toASN1Primitive());
        subjectPrincipal = subjectPrincipal + "C=" + attributes.getCountry() + commaSpace + "ST=" + attributes.getState() + commaSpace + "L=" + attributes.getLocality() + commaSpace + "O=" + attributes.getOrganizationName() + commaSpace + "OU=" + attributes.getOrganizationalUnit() + commaSpace + "CN=" + attributes.getCommonName();
        final X500Principal subject = new X500Principal(subjectPrincipal);
        final String csrAlgorithm = WebServerUtil.getWebServerSettings().getProperty("ssl.csr.algorithm");
        final ContentSigner contentSigner = new JcaContentSignerBuilder(csrAlgorithm).build(privateKey);
        final PKCS10CertificationRequestBuilder builder = (PKCS10CertificationRequestBuilder)new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        if (attributes.getSanNames().size() != 0) {
            builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, (ASN1Encodable)extGen.generate());
        }
        final PKCS10CertificationRequest csr = builder.build(contentSigner);
        final String serverHome = new File(System.getProperty("user.dir")).getParent();
        final File serverCSRFile = new File(serverHome + File.separator + "bin" + File.separator + "server.csr");
        final File serverKeyFile = new File(serverHome + File.separator + "bin" + File.separator + "server.key");
        final JcaPEMWriter csrWriter = new JcaPEMWriter((Writer)new FileWriter(serverCSRFile));
        csrWriter.writeObject((Object)csr);
        csrWriter.flush();
        csrWriter.close();
        final JcaPEMWriter keyWriter = new JcaPEMWriter((Writer)new FileWriter(serverKeyFile));
        keyWriter.writeObject((Object)privateKey);
        keyWriter.flush();
        keyWriter.close();
    }
    
    public static void decryptPrivateKey(final String encryptedKeyFile, final char[] passPhrase) throws Exception {
        final PEMParser pemParser = new PEMParser((Reader)new FileReader(encryptedKeyFile));
        final Object object = pemParser.readObject();
        final PEMDecryptorProvider decryptorProvider = new JcePEMDecryptorProviderBuilder().build(passPhrase);
        final JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter().setProvider("BC");
        KeyPair keyPair = null;
        if (object instanceof PEMEncryptedKeyPair) {
            keyPair = pemKeyConverter.getKeyPair(((PEMEncryptedKeyPair)object).decryptKeyPair(decryptorProvider));
        }
        final PrivateKey privateKey = keyPair.getPrivate();
        final byte[] bytes = privateKey.getEncoded();
        ApiFactoryProvider.getFileAccessAPI().writeFile("decrypted_key.key", bytes);
    }
    
    private static GeneralNames toGeneralName(final ArrayList<String> dnsNames) {
        final ArrayList<GeneralName> generalNames = new ArrayList<GeneralName>();
        for (final String dnsName : dnsNames) {
            generalNames.add(new GeneralName(2, dnsName));
        }
        return new GeneralNames((GeneralName[])generalNames.toArray(new GeneralName[generalNames.size()]));
    }
    
    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
        OpenSSLUtils.consoleLogger = Logger.getLogger("CSRGeneration");
    }
}
