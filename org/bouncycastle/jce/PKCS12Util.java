package org.bouncycastle.jce;

import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.Mac;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.Pfx;
import java.io.OutputStream;
import org.bouncycastle.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;

public class PKCS12Util
{
    public static byte[] convertToDefiniteLength(final byte[] array) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
        final Pfx instance = Pfx.getInstance(array);
        byteArrayOutputStream.reset();
        derOutputStream.writeObject(instance);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] convertToDefiniteLength(final byte[] array, final char[] array2, final String s) throws IOException {
        final Pfx instance = Pfx.getInstance(array);
        final ContentInfo authSafe = instance.getAuthSafe();
        final ASN1OctetString instance2 = ASN1OctetString.getInstance(authSafe.getContent());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
        derOutputStream.writeObject(new ASN1InputStream(instance2.getOctets()).readObject());
        final ContentInfo contentInfo = new ContentInfo(authSafe.getContentType(), new DEROctetString(byteArrayOutputStream.toByteArray()));
        final MacData macData = instance.getMacData();
        MacData macData2;
        try {
            final int intValue = macData.getIterationCount().intValue();
            macData2 = new MacData(new DigestInfo(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE), calculatePbeMac(macData.getMac().getAlgorithmId().getAlgorithm(), macData.getSalt(), intValue, array2, ASN1OctetString.getInstance(contentInfo.getContent()).getOctets(), s)), macData.getSalt(), intValue);
        }
        catch (final Exception ex) {
            throw new IOException("error constructing MAC: " + ex.toString());
        }
        final Pfx pfx = new Pfx(contentInfo, macData2);
        byteArrayOutputStream.reset();
        derOutputStream.writeObject(pfx);
        return byteArrayOutputStream.toByteArray();
    }
    
    private static byte[] calculatePbeMac(final ASN1ObjectIdentifier asn1ObjectIdentifier, final byte[] array, final int n, final char[] array2, final byte[] array3, final String s) throws Exception {
        final SecretKeyFactory instance = SecretKeyFactory.getInstance(asn1ObjectIdentifier.getId(), s);
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array, n);
        final SecretKey generateSecret = instance.generateSecret(new PBEKeySpec(array2));
        final Mac instance2 = Mac.getInstance(asn1ObjectIdentifier.getId(), s);
        instance2.init(generateSecret, pbeParameterSpec);
        instance2.update(array3);
        return instance2.doFinal();
    }
}
