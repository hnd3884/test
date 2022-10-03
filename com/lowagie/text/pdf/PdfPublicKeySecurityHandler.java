package com.lowagie.text.pdf;

import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.DEROctetString;
import java.security.Key;
import javax.crypto.Cipher;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.ByteArrayInputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.GeneralSecurityException;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.cert.Certificate;
import org.bouncycastle.asn1.ASN1Encodable;
import java.io.OutputStream;
import org.bouncycastle.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import java.util.ArrayList;

public class PdfPublicKeySecurityHandler
{
    static final int SEED_LENGTH = 20;
    private ArrayList recipients;
    private byte[] seed;
    
    public PdfPublicKeySecurityHandler() {
        this.recipients = null;
        this.seed = new byte[20];
        try {
            final KeyGenerator key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            final SecretKey sk = key.generateKey();
            System.arraycopy(sk.getEncoded(), 0, this.seed, 0, 20);
        }
        catch (final NoSuchAlgorithmException e) {
            this.seed = SecureRandom.getSeed(20);
        }
        this.recipients = new ArrayList();
    }
    
    public void addRecipient(final PdfPublicKeyRecipient recipient) {
        this.recipients.add(recipient);
    }
    
    protected byte[] getSeed() {
        return this.seed.clone();
    }
    
    public int getRecipientsSize() {
        return this.recipients.size();
    }
    
    public byte[] getEncodedRecipient(final int index) throws IOException, GeneralSecurityException {
        final PdfPublicKeyRecipient recipient = this.recipients.get(index);
        byte[] cms = recipient.getCms();
        if (cms != null) {
            return cms;
        }
        final Certificate certificate = recipient.getCertificate();
        int permission = recipient.getPermission();
        final int revision = 3;
        permission |= ((revision == 3) ? -3904 : -64);
        permission &= 0xFFFFFFFC;
        ++permission;
        final byte[] pkcs7input = new byte[24];
        final byte one = (byte)permission;
        final byte two = (byte)(permission >> 8);
        final byte three = (byte)(permission >> 16);
        final byte four = (byte)(permission >> 24);
        System.arraycopy(this.seed, 0, pkcs7input, 0, 20);
        pkcs7input[20] = four;
        pkcs7input[21] = three;
        pkcs7input[22] = two;
        pkcs7input[23] = one;
        final ASN1Primitive obj = this.createDERForRecipient(pkcs7input, (X509Certificate)certificate);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DEROutputStream k = new DEROutputStream((OutputStream)baos);
        k.writeObject((ASN1Encodable)obj);
        cms = baos.toByteArray();
        recipient.setCms(cms);
        return cms;
    }
    
    public PdfArray getEncodedRecipients() throws IOException {
        PdfArray EncodedRecipients = new PdfArray();
        byte[] cms = null;
        for (int i = 0; i < this.recipients.size(); ++i) {
            try {
                cms = this.getEncodedRecipient(i);
                EncodedRecipients.add(new PdfLiteral(PdfContentByte.escapeString(cms)));
            }
            catch (final GeneralSecurityException | IOException e) {
                EncodedRecipients = null;
            }
        }
        return EncodedRecipients;
    }
    
    private ASN1Primitive createDERForRecipient(final byte[] in, final X509Certificate cert) throws IOException, GeneralSecurityException {
        final String s = "1.2.840.113549.3.2";
        final AlgorithmParameterGenerator algorithmparametergenerator = AlgorithmParameterGenerator.getInstance(s);
        final AlgorithmParameters algorithmparameters = algorithmparametergenerator.generateParameters();
        final ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(algorithmparameters.getEncoded("ASN.1"));
        final ASN1InputStream asn1inputstream = new ASN1InputStream((InputStream)bytearrayinputstream);
        final ASN1Primitive derobject = asn1inputstream.readObject();
        final KeyGenerator keygenerator = KeyGenerator.getInstance(s);
        keygenerator.init(128);
        final SecretKey secretkey = keygenerator.generateKey();
        final Cipher cipher = Cipher.getInstance(s);
        cipher.init(1, secretkey, algorithmparameters);
        final byte[] abyte1 = cipher.doFinal(in);
        final DEROctetString deroctetstring = new DEROctetString(abyte1);
        final KeyTransRecipientInfo keytransrecipientinfo = this.computeRecipientInfo(cert, secretkey.getEncoded());
        final DERSet derset = new DERSet((ASN1Encodable)new RecipientInfo(keytransrecipientinfo));
        final AlgorithmIdentifier algorithmidentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(s), (ASN1Encodable)derobject);
        final EncryptedContentInfo encryptedcontentinfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmidentifier, (ASN1OctetString)deroctetstring);
        final ASN1Set set = null;
        final EnvelopedData env = new EnvelopedData((OriginatorInfo)null, (ASN1Set)derset, encryptedcontentinfo, set);
        final ContentInfo contentinfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, (ASN1Encodable)env);
        return contentinfo.toASN1Primitive();
    }
    
    private KeyTransRecipientInfo computeRecipientInfo(final X509Certificate x509certificate, final byte[] abyte0) throws GeneralSecurityException, IOException {
        final ASN1InputStream asn1inputstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(x509certificate.getTBSCertificate()));
        final TBSCertificateStructure tbscertificatestructure = TBSCertificateStructure.getInstance((Object)asn1inputstream.readObject());
        final AlgorithmIdentifier algorithmidentifier = tbscertificatestructure.getSubjectPublicKeyInfo().getAlgorithmId();
        final IssuerAndSerialNumber issuerandserialnumber = new IssuerAndSerialNumber(tbscertificatestructure.getIssuer(), tbscertificatestructure.getSerialNumber().getValue());
        final Cipher cipher = Cipher.getInstance(algorithmidentifier.getAlgorithm().getId());
        cipher.init(1, x509certificate);
        final DEROctetString deroctetstring = new DEROctetString(cipher.doFinal(abyte0));
        final RecipientIdentifier recipId = new RecipientIdentifier(issuerandserialnumber);
        return new KeyTransRecipientInfo(recipId, algorithmidentifier, (ASN1OctetString)deroctetstring);
    }
}
