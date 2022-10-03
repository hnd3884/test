package com.me.devicemanagement.framework.server.certificate.verifier;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.logging.Level;
import org.bouncycastle.cms.CMSProcessable;
import java.io.InputStream;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationVerifierProvider;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.CollectionStore;
import java.security.cert.Certificate;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.Store;
import java.util.List;
import org.bouncycastle.operator.ContentSigner;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.CMSProcessableByteArray;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Collection;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import java.util.ArrayList;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import java.util.logging.Logger;

public class CRTThumbPrintVerifier
{
    private static Logger logger;
    private static String sourceClass;
    
    public byte[] signContent(final byte[] content, final String certificateFilePath, final String privateKeyFilePath) throws Exception {
        final CMSSignedDataGenerator cmssigned = new CMSSignedDataGenerator();
        final X509Certificate certificate = CertificateUtils.loadX509CertificateFromFile(new File(certificateFilePath));
        final PrivateKey privKey = CertificateUtils.loadPrivateKeyFromApiFactory(new File(privateKeyFilePath));
        final ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
        cmssigned.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, certificate));
        final List certList = new ArrayList();
        certList.add(certificate);
        final Store certs = (Store)new JcaCertStore((Collection)certList);
        cmssigned.addCertificates(certs);
        SyMLogger.info(CRTThumbPrintVerifier.logger, CRTThumbPrintVerifier.sourceClass, "signContent", "The requested content to be signed.");
        final CMSTypedData data = (CMSTypedData)new CMSProcessableByteArray(content);
        final CMSSignedData signeddata = cmssigned.generate(data, true);
        final byte[] signedByteArray = signeddata.getEncoded();
        return signedByteArray;
    }
    
    public boolean verify(final byte[] data, final Certificate certificate) throws Exception {
        return this.verify(data, this.getThumbPrint(certificate));
    }
    
    public boolean verify(final byte[] data, final String thumbPrint) throws Exception {
        final CMSSignedData signedData = new CMSSignedData(data);
        final CollectionStore certificatesStore = (CollectionStore)signedData.getCertificates();
        final Collection collection = certificatesStore.getMatches((Selector)null);
        if (!collection.isEmpty()) {
            final Object obj = collection.iterator().next();
            final X509CertificateHolder holder = (X509CertificateHolder)obj;
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final InputStream inputStream = new ByteArrayInputStream(holder.getEncoded());
            final Certificate certificate = certFactory.generateCertificate(inputStream);
            final String signedThumbPrint = this.getThumbPrint(certificate);
            final X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
            final SignerInformationVerifierProvider vProv = (SignerInformationVerifierProvider)new SignerInformationVerifierProvider() {
                public SignerInformationVerifier get(final SignerId signerId) throws OperatorCreationException {
                    return new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert);
                }
            };
            return signedData.verifySignatures(vProv) && thumbPrint.equals(signedThumbPrint);
        }
        return false;
    }
    
    public byte[] getUNSignedContent(final byte[] signedContent) throws Exception {
        final CMSSignedData signedData = new CMSSignedData(signedContent);
        final CMSProcessable processable = (CMSProcessable)signedData.getSignedContent();
        return this.getUNSignedContent(processable);
    }
    
    public byte[] getUNSignedContent(final CMSProcessable processable) throws Exception {
        if (processable != null) {
            final Object processableContent = processable.getContent();
            if (processableContent != null) {
                if (processableContent instanceof byte[]) {
                    final byte[] processableBytes = (byte[])processableContent;
                    if (processableBytes.length > 0) {
                        return processableBytes;
                    }
                    return null;
                }
                else {
                    CRTThumbPrintVerifier.logger.log(Level.WARNING, "Illegal content of type " + processableContent.getClass().getCanonicalName());
                }
            }
        }
        return null;
    }
    
    public String getThumbPrint(final Certificate cert) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("SHA256");
        final byte[] der = cert.getEncoded();
        md.update(der);
        final byte[] digest = md.digest();
        final String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }
    
    public byte[] verifyAndUNSignFileContentWithMEMetaCrt(final String filePath) {
        try {
            final byte[] signedContent = FileAccessUtil.getFileAsByteArray(filePath);
            if (signedContent != null) {
                if (this.verify(signedContent, "9d4b262c221ea23ad8e66c912b58c43156ec255cd3d2e05e0117883f46d8e40d")) {
                    CRTThumbPrintVerifier.logger.log(Level.INFO, "File verified with MEMeta crt tp");
                    return this.getUNSignedContent(signedContent);
                }
                CRTThumbPrintVerifier.logger.log(Level.INFO, "File verify failed with MEMeta crt tp");
            }
            else {
                CRTThumbPrintVerifier.logger.log(Level.INFO, "File content available to verify with MEMeta crt tp.");
            }
        }
        catch (final Exception e) {
            CRTThumbPrintVerifier.logger.log(Level.WARNING, "Exception occurred while unsigning file with MEMeta crt tp", e);
        }
        return null;
    }
    
    static {
        CRTThumbPrintVerifier.logger = Logger.getLogger(CRTThumbPrintVerifier.class.getName());
        CRTThumbPrintVerifier.sourceClass = "CRTThumbPrintVerifier";
    }
}
