package com.adventnet.sym.server.mdm.ios.payload;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMSecurityLogger;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSDictionary;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.FileReader;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.Store;
import java.util.List;
import org.bouncycastle.operator.ContentSigner;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.CMSProcessableByteArray;
import java.util.Collection;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import com.me.mdm.certificate.CertificateHandler;
import java.util.ArrayList;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.logging.Logger;

public class CMSPayloadSigning implements PayloadSigning
{
    private static final Logger LOGGER;
    
    @Override
    public byte[] signPayload(final String sContent) throws Exception {
        this.logUnsignedContent(sContent);
        final String serverCertificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
        final String serverPrivateKeyFilePath = SSLCertificateUtil.getInstance().getServerPrivateKeyFilePath();
        final String intemediateCertificateFilePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
        final String serverCACertificateFilePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
        final CMSSignedDataGenerator cmssigned = new CMSSignedDataGenerator();
        X509Certificate certificate = null;
        final PrivateKey privKey = loadPrivateKeyFromFile(new File(serverPrivateKeyFilePath));
        certificate = CertificateUtils.loadX509CertificateFromFile(new File(serverCertificateFilePath));
        final ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
        cmssigned.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, certificate));
        final List certList = new ArrayList();
        certList.add(certificate);
        if (intemediateCertificateFilePath != null) {
            final X509Certificate interCertificate = CertificateUtils.loadX509CertificateFromFile(new File(intemediateCertificateFilePath));
            certList.add(interCertificate);
        }
        if (serverCACertificateFilePath != null && CertificateHandler.getInstance().isSANOrEnterpriseCACertificate()) {
            final X509Certificate serverCACertificate = CertificateUtils.loadX509CertificateFromFile(new File(serverCACertificateFilePath));
            certList.add(serverCACertificate);
        }
        final Store certs = (Store)new JcaCertStore((Collection)certList);
        cmssigned.addCertificates(certs);
        final byte[] content = sContent.getBytes();
        final CMSTypedData data = (CMSTypedData)new CMSProcessableByteArray(content);
        final CMSSignedData signeddata = cmssigned.generate(data, true);
        final byte[] signedByteArray = signeddata.getEncoded();
        return signedByteArray;
    }
    
    public static PrivateKey loadPrivateKeyFromFile(final File privateKeyFile) throws Exception {
        final FileReader file = new FileReader(privateKeyFile);
        final PemReader reader = new PemReader((Reader)file);
        final PKCS8EncodedKeySpec caKeySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
        final KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
        final PrivateKey caKey = kf.generatePrivate(caKeySpec);
        return caKey;
    }
    
    private void logUnsignedContent(final String content) {
        try {
            final NSDictionary dict = (NSDictionary)DMSecurityUtil.parsePropertyList(content.getBytes("UTF-8"));
            final NSDictionary lightObject = (NSDictionary)PlistWrapper.getInstance().replaceNSDataTypeRecursively((NSObject)dict);
            MDMSecurityLogger.info(CMSPayloadSigning.LOGGER, "CMSPayloadSigning", "signPayload", "The content to be signed : {0}", lightObject.toXMLPropertyList());
        }
        catch (final Exception e) {
            CMSPayloadSigning.LOGGER.log(Level.SEVERE, "CMSPayloadSigning: Exception while logUnsignedContent {0}", e.getMessage());
            CMSPayloadSigning.LOGGER.log(Level.FINE, "CMSPayloadSigning: Exception while logUnsignedContent ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}
