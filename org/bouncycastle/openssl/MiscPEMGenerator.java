package org.bouncycastle.openssl;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.util.io.pem.PemHeader;
import java.util.ArrayList;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.IOException;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class MiscPEMGenerator implements PemObjectGenerator
{
    private static final ASN1ObjectIdentifier[] dsaOids;
    private static final byte[] hexEncodingTable;
    private final Object obj;
    private final PEMEncryptor encryptor;
    
    public MiscPEMGenerator(final Object obj) {
        this.obj = obj;
        this.encryptor = null;
    }
    
    public MiscPEMGenerator(final Object obj, final PEMEncryptor encryptor) {
        this.obj = obj;
        this.encryptor = encryptor;
    }
    
    private PemObject createPemObject(final Object o) throws IOException {
        if (o instanceof PemObject) {
            return (PemObject)o;
        }
        if (o instanceof PemObjectGenerator) {
            return ((PemObjectGenerator)o).generate();
        }
        String s;
        byte[] array;
        if (o instanceof X509CertificateHolder) {
            s = "CERTIFICATE";
            array = ((X509CertificateHolder)o).getEncoded();
        }
        else if (o instanceof X509CRLHolder) {
            s = "X509 CRL";
            array = ((X509CRLHolder)o).getEncoded();
        }
        else if (o instanceof X509TrustedCertificateBlock) {
            s = "TRUSTED CERTIFICATE";
            array = ((X509TrustedCertificateBlock)o).getEncoded();
        }
        else if (o instanceof PrivateKeyInfo) {
            final PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo)o;
            final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
            if (algorithm.equals((Object)PKCSObjectIdentifiers.rsaEncryption)) {
                s = "RSA PRIVATE KEY";
                array = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
            }
            else if (algorithm.equals((Object)MiscPEMGenerator.dsaOids[0]) || algorithm.equals((Object)MiscPEMGenerator.dsaOids[1])) {
                s = "DSA PRIVATE KEY";
                final DSAParameter instance = DSAParameter.getInstance((Object)privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(0L));
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(instance.getP()));
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(instance.getQ()));
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(instance.getG()));
                final BigInteger value = ASN1Integer.getInstance((Object)privateKeyInfo.parsePrivateKey()).getValue();
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(instance.getG().modPow(value, instance.getP())));
                asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(value));
                array = new DERSequence(asn1EncodableVector).getEncoded();
            }
            else {
                if (!algorithm.equals((Object)X9ObjectIdentifiers.id_ecPublicKey)) {
                    throw new IOException("Cannot identify private key");
                }
                s = "EC PRIVATE KEY";
                array = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
            }
        }
        else if (o instanceof SubjectPublicKeyInfo) {
            s = "PUBLIC KEY";
            array = ((SubjectPublicKeyInfo)o).getEncoded();
        }
        else if (o instanceof X509AttributeCertificateHolder) {
            s = "ATTRIBUTE CERTIFICATE";
            array = ((X509AttributeCertificateHolder)o).getEncoded();
        }
        else if (o instanceof PKCS10CertificationRequest) {
            s = "CERTIFICATE REQUEST";
            array = ((PKCS10CertificationRequest)o).getEncoded();
        }
        else if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
            s = "ENCRYPTED PRIVATE KEY";
            array = ((PKCS8EncryptedPrivateKeyInfo)o).getEncoded();
        }
        else {
            if (!(o instanceof ContentInfo)) {
                throw new PemGenerationException("unknown object passed - can't encode.");
            }
            s = "PKCS7";
            array = ((ContentInfo)o).getEncoded();
        }
        if (this.encryptor != null) {
            String upperCase = Strings.toUpperCase(this.encryptor.getAlgorithm());
            if (upperCase.equals("DESEDE")) {
                upperCase = "DES-EDE3-CBC";
            }
            final byte[] iv = this.encryptor.getIV();
            final byte[] encrypt = this.encryptor.encrypt(array);
            final ArrayList list = new ArrayList(2);
            list.add(new PemHeader("Proc-Type", "4,ENCRYPTED"));
            list.add(new PemHeader("DEK-Info", upperCase + "," + this.getHexEncoded(iv)));
            return new PemObject(s, (List)list, encrypt);
        }
        return new PemObject(s, array);
    }
    
    private String getHexEncoded(final byte[] array) throws IOException {
        final char[] array2 = new char[array.length * 2];
        for (int i = 0; i != array.length; ++i) {
            final int n = array[i] & 0xFF;
            array2[2 * i] = (char)MiscPEMGenerator.hexEncodingTable[n >>> 4];
            array2[2 * i + 1] = (char)MiscPEMGenerator.hexEncodingTable[n & 0xF];
        }
        return new String(array2);
    }
    
    public PemObject generate() throws PemGenerationException {
        try {
            return this.createPemObject(this.obj);
        }
        catch (final IOException ex) {
            throw new PemGenerationException("encoding exception: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    static {
        dsaOids = new ASN1ObjectIdentifier[] { X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1 };
        hexEncodingTable = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
    }
}
