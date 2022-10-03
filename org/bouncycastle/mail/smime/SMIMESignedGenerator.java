package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import javax.mail.internet.ContentType;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.OutputStream;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import java.util.Collections;
import org.bouncycastle.cms.CMSAlgorithm;
import java.security.AccessController;
import javax.activation.CommandMap;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.BodyPart;
import java.util.Collection;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.SignerInformation;
import java.util.TreeSet;
import org.bouncycastle.util.Store;
import org.bouncycastle.cms.SignerInfoGenerator;
import java.util.Iterator;
import org.bouncycastle.cms.SignerInformationStore;
import java.util.HashMap;
import java.util.ArrayList;
import javax.activation.MailcapCommandMap;
import java.util.List;
import java.util.Map;

public class SMIMESignedGenerator extends SMIMEGenerator
{
    public static final String DIGEST_SHA1;
    public static final String DIGEST_MD5;
    public static final String DIGEST_SHA224;
    public static final String DIGEST_SHA256;
    public static final String DIGEST_SHA384;
    public static final String DIGEST_SHA512;
    public static final String DIGEST_GOST3411;
    public static final String DIGEST_RIPEMD128;
    public static final String DIGEST_RIPEMD160;
    public static final String DIGEST_RIPEMD256;
    public static final String ENCRYPTION_RSA;
    public static final String ENCRYPTION_DSA;
    public static final String ENCRYPTION_ECDSA;
    public static final String ENCRYPTION_RSA_PSS;
    public static final String ENCRYPTION_GOST3410;
    public static final String ENCRYPTION_ECGOST3410;
    public static final String ENCRYPTION_ECGOST3410_2012_256;
    public static final String ENCRYPTION_ECGOST3410_2012_512;
    private static final String CERTIFICATE_MANAGEMENT_CONTENT = "application/pkcs7-mime; name=smime.p7c; smime-type=certs-only";
    private static final String DETACHED_SIGNATURE_TYPE = "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data";
    private static final String ENCAPSULATED_SIGNED_CONTENT_TYPE = "application/pkcs7-mime; name=smime.p7m; smime-type=signed-data";
    public static final Map RFC3851_MICALGS;
    public static final Map RFC5751_MICALGS;
    public static final Map STANDARD_MICALGS;
    private final String defaultContentTransferEncoding;
    private final Map micAlgs;
    private List _certStores;
    private List certStores;
    private List crlStores;
    private List attrCertStores;
    private List signerInfoGens;
    private List _signers;
    private List _oldSigners;
    private List _attributeCerts;
    private Map _digests;
    
    private static MailcapCommandMap addCommands(final MailcapCommandMap mailcapCommandMap) {
        mailcapCommandMap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcapCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcapCommandMap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcapCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcapCommandMap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
        return mailcapCommandMap;
    }
    
    public SMIMESignedGenerator() {
        this("7bit", SMIMESignedGenerator.STANDARD_MICALGS);
    }
    
    public SMIMESignedGenerator(final String s) {
        this(s, SMIMESignedGenerator.STANDARD_MICALGS);
    }
    
    public SMIMESignedGenerator(final Map map) {
        this("7bit", map);
    }
    
    public SMIMESignedGenerator(final String defaultContentTransferEncoding, final Map micAlgs) {
        this._certStores = new ArrayList();
        this.certStores = new ArrayList();
        this.crlStores = new ArrayList();
        this.attrCertStores = new ArrayList();
        this.signerInfoGens = new ArrayList();
        this._signers = new ArrayList();
        this._oldSigners = new ArrayList();
        this._attributeCerts = new ArrayList();
        this._digests = new HashMap();
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
        this.micAlgs = micAlgs;
    }
    
    public void addSigners(final SignerInformationStore signerInformationStore) {
        final Iterator iterator = signerInformationStore.getSigners().iterator();
        while (iterator.hasNext()) {
            this._oldSigners.add(iterator.next());
        }
    }
    
    public void addSignerInfoGenerator(final SignerInfoGenerator signerInfoGenerator) {
        this.signerInfoGens.add(signerInfoGenerator);
    }
    
    public void addCertificates(final Store store) {
        this.certStores.add(store);
    }
    
    public void addCRLs(final Store store) {
        this.crlStores.add(store);
    }
    
    public void addAttributeCertificates(final Store store) {
        this.attrCertStores.add(store);
    }
    
    private void addHashHeader(final StringBuffer sb, final List list) {
        int n = 0;
        final Iterator iterator = list.iterator();
        final TreeSet set = new TreeSet();
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            ASN1ObjectIdentifier asn1ObjectIdentifier;
            if (next instanceof SignerInformation) {
                asn1ObjectIdentifier = ((SignerInformation)next).getDigestAlgorithmID().getAlgorithm();
            }
            else {
                asn1ObjectIdentifier = ((SignerInfoGenerator)next).getDigestAlgorithm().getAlgorithm();
            }
            final String s = this.micAlgs.get(asn1ObjectIdentifier);
            if (s == null) {
                set.add("unknown");
            }
            else {
                set.add(s);
            }
        }
        for (final String s2 : set) {
            if (n == 0) {
                if (set.size() != 1) {
                    sb.append("; micalg=\"");
                }
                else {
                    sb.append("; micalg=");
                }
            }
            else {
                sb.append(',');
            }
            sb.append(s2);
            ++n;
        }
        if (n != 0 && set.size() != 1) {
            sb.append('\"');
        }
    }
    
    private MimeMultipart make(final MimeBodyPart mimeBodyPart) throws SMIMEException {
        try {
            final MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
            mimeBodyPart2.setContent((Object)new ContentSigner(mimeBodyPart, false), "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data");
            mimeBodyPart2.addHeader("Content-Type", "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data");
            mimeBodyPart2.addHeader("Content-Disposition", "attachment; filename=\"smime.p7s\"");
            mimeBodyPart2.addHeader("Content-Description", "S/MIME Cryptographic Signature");
            mimeBodyPart2.addHeader("Content-Transfer-Encoding", this.encoding);
            final StringBuffer sb = new StringBuffer("signed; protocol=\"application/pkcs7-signature\"");
            final ArrayList list = new ArrayList(this._signers);
            list.addAll(this._oldSigners);
            list.addAll(this.signerInfoGens);
            this.addHashHeader(sb, list);
            final MimeMultipart mimeMultipart = new MimeMultipart(sb.toString());
            mimeMultipart.addBodyPart((BodyPart)mimeBodyPart);
            mimeMultipart.addBodyPart((BodyPart)mimeBodyPart2);
            return mimeMultipart;
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)ex);
        }
    }
    
    private MimeBodyPart makeEncapsulated(final MimeBodyPart mimeBodyPart) throws SMIMEException {
        try {
            final MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
            mimeBodyPart2.setContent((Object)new ContentSigner(mimeBodyPart, true), "application/pkcs7-mime; name=smime.p7m; smime-type=signed-data");
            mimeBodyPart2.addHeader("Content-Type", "application/pkcs7-mime; name=smime.p7m; smime-type=signed-data");
            mimeBodyPart2.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
            mimeBodyPart2.addHeader("Content-Description", "S/MIME Cryptographic Signed Data");
            mimeBodyPart2.addHeader("Content-Transfer-Encoding", this.encoding);
            return mimeBodyPart2;
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception putting body part together.", (Exception)ex);
        }
    }
    
    public Map getGeneratedDigests() {
        return new HashMap(this._digests);
    }
    
    public MimeMultipart generate(final MimeBodyPart mimeBodyPart) throws SMIMEException {
        return this.make(this.makeContentBodyPart(mimeBodyPart));
    }
    
    public MimeMultipart generate(final MimeMessage mimeMessage) throws SMIMEException {
        try {
            mimeMessage.saveChanges();
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("unable to save message", (Exception)ex);
        }
        return this.make(this.makeContentBodyPart(mimeMessage));
    }
    
    public MimeBodyPart generateEncapsulated(final MimeBodyPart mimeBodyPart) throws SMIMEException {
        return this.makeEncapsulated(this.makeContentBodyPart(mimeBodyPart));
    }
    
    public MimeBodyPart generateEncapsulated(final MimeMessage mimeMessage) throws SMIMEException {
        try {
            mimeMessage.saveChanges();
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("unable to save message", (Exception)ex);
        }
        return this.makeEncapsulated(this.makeContentBodyPart(mimeMessage));
    }
    
    public MimeBodyPart generateCertificateManagement() throws SMIMEException {
        try {
            final MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent((Object)new ContentSigner(null, true), "application/pkcs7-mime; name=smime.p7c; smime-type=certs-only");
            mimeBodyPart.addHeader("Content-Type", "application/pkcs7-mime; name=smime.p7c; smime-type=certs-only");
            mimeBodyPart.addHeader("Content-Disposition", "attachment; filename=\"smime.p7c\"");
            mimeBodyPart.addHeader("Content-Description", "S/MIME Certificate Management Message");
            mimeBodyPart.addHeader("Content-Transfer-Encoding", this.encoding);
            return mimeBodyPart;
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception putting body part together.", (Exception)ex);
        }
    }
    
    static {
        DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
        DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
        DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
        DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
        DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
        DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
        DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
        DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
        DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
        DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();
        ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
        ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
        ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
        ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
        ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
        ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();
        ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.getId();
        ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.getId();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
                if (defaultCommandMap instanceof MailcapCommandMap) {
                    CommandMap.setDefaultCommandMap(addCommands((MailcapCommandMap)defaultCommandMap));
                }
                return null;
            }
        });
        final HashMap hashMap = new HashMap();
        hashMap.put(CMSAlgorithm.MD5, "md5");
        hashMap.put(CMSAlgorithm.SHA1, "sha-1");
        hashMap.put(CMSAlgorithm.SHA224, "sha-224");
        hashMap.put(CMSAlgorithm.SHA256, "sha-256");
        hashMap.put(CMSAlgorithm.SHA384, "sha-384");
        hashMap.put(CMSAlgorithm.SHA512, "sha-512");
        hashMap.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        hashMap.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        hashMap.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC5751_MICALGS = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        final HashMap hashMap2 = new HashMap();
        hashMap2.put(CMSAlgorithm.MD5, "md5");
        hashMap2.put(CMSAlgorithm.SHA1, "sha1");
        hashMap2.put(CMSAlgorithm.SHA224, "sha224");
        hashMap2.put(CMSAlgorithm.SHA256, "sha256");
        hashMap2.put(CMSAlgorithm.SHA384, "sha384");
        hashMap2.put(CMSAlgorithm.SHA512, "sha512");
        hashMap2.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        hashMap2.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        hashMap2.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC3851_MICALGS = Collections.unmodifiableMap((Map<?, ?>)hashMap2);
        STANDARD_MICALGS = SMIMESignedGenerator.RFC5751_MICALGS;
    }
    
    private class ContentSigner implements SMIMEStreamingProcessor
    {
        private final MimeBodyPart content;
        private final boolean encapsulate;
        private final boolean noProvider;
        
        ContentSigner(final MimeBodyPart content, final boolean encapsulate) {
            this.content = content;
            this.encapsulate = encapsulate;
            this.noProvider = true;
        }
        
        protected CMSSignedDataStreamGenerator getGenerator() throws CMSException {
            final CMSSignedDataStreamGenerator cmsSignedDataStreamGenerator = new CMSSignedDataStreamGenerator();
            final Iterator iterator = SMIMESignedGenerator.this.certStores.iterator();
            while (iterator.hasNext()) {
                cmsSignedDataStreamGenerator.addCertificates((Store)iterator.next());
            }
            final Iterator iterator2 = SMIMESignedGenerator.this.crlStores.iterator();
            while (iterator2.hasNext()) {
                cmsSignedDataStreamGenerator.addCRLs((Store)iterator2.next());
            }
            final Iterator iterator3 = SMIMESignedGenerator.this.attrCertStores.iterator();
            while (iterator3.hasNext()) {
                cmsSignedDataStreamGenerator.addAttributeCertificates((Store)iterator3.next());
            }
            final Iterator iterator4 = SMIMESignedGenerator.this.signerInfoGens.iterator();
            while (iterator4.hasNext()) {
                cmsSignedDataStreamGenerator.addSignerInfoGenerator((SignerInfoGenerator)iterator4.next());
            }
            cmsSignedDataStreamGenerator.addSigners(new SignerInformationStore((Collection)SMIMESignedGenerator.this._oldSigners));
            return cmsSignedDataStreamGenerator;
        }
        
        private void writeBodyPart(OutputStream outputStream, final MimeBodyPart mimeBodyPart) throws IOException, MessagingException {
            if (SMIMEUtil.isMultipartContent((Part)mimeBodyPart)) {
                final Object content = mimeBodyPart.getContent();
                Object o;
                if (content instanceof Multipart) {
                    o = content;
                }
                else {
                    o = new MimeMultipart(mimeBodyPart.getDataHandler().getDataSource());
                }
                final String string = "--" + new ContentType(((Multipart)o).getContentType()).getParameter("boundary");
                final SMIMEUtil.LineOutputStream lineOutputStream = new SMIMEUtil.LineOutputStream(outputStream);
                final Enumeration allHeaderLines = mimeBodyPart.getAllHeaderLines();
                while (allHeaderLines.hasMoreElements()) {
                    lineOutputStream.writeln((String)allHeaderLines.nextElement());
                }
                lineOutputStream.writeln();
                SMIMEUtil.outputPreamble(lineOutputStream, mimeBodyPart, string);
                for (int i = 0; i < ((Multipart)o).getCount(); ++i) {
                    lineOutputStream.writeln(string);
                    this.writeBodyPart(outputStream, (MimeBodyPart)((Multipart)o).getBodyPart(i));
                    lineOutputStream.writeln();
                }
                lineOutputStream.writeln(string + "--");
            }
            else {
                if (SMIMEUtil.isCanonicalisationRequired(mimeBodyPart, SMIMESignedGenerator.this.defaultContentTransferEncoding)) {
                    outputStream = new CRLFOutputStream(outputStream);
                }
                mimeBodyPart.writeTo(outputStream);
            }
        }
        
        public void write(final OutputStream outputStream) throws IOException {
            try {
                final CMSSignedDataStreamGenerator generator = this.getGenerator();
                final OutputStream open = generator.open(outputStream, this.encapsulate);
                if (this.content != null) {
                    if (!this.encapsulate) {
                        this.writeBodyPart(open, this.content);
                    }
                    else {
                        final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
                        if (defaultCommandMap instanceof MailcapCommandMap) {
                            this.content.getDataHandler().setCommandMap(addCommands((MailcapCommandMap)defaultCommandMap));
                        }
                        this.content.writeTo(open);
                    }
                }
                open.close();
                SMIMESignedGenerator.this._digests = generator.getGeneratedDigests();
            }
            catch (final MessagingException ex) {
                throw new IOException(ex.toString());
            }
            catch (final CMSException ex2) {
                throw new IOException(ex2.toString());
            }
        }
    }
}
