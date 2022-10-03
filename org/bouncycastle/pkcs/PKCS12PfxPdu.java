package org.bouncycastle.pkcs;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.Pfx;

public class PKCS12PfxPdu
{
    private Pfx pfx;
    
    private static Pfx parseBytes(final byte[] array) throws IOException {
        try {
            return Pfx.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final ClassCastException ex) {
            throw new PKCSIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new PKCSIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public PKCS12PfxPdu(final Pfx pfx) {
        this.pfx = pfx;
    }
    
    public PKCS12PfxPdu(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public ContentInfo[] getContentInfos() {
        final ASN1Sequence instance = ASN1Sequence.getInstance((Object)ASN1OctetString.getInstance((Object)this.pfx.getAuthSafe().getContent()).getOctets());
        final ContentInfo[] array = new ContentInfo[instance.size()];
        for (int i = 0; i != instance.size(); ++i) {
            array[i] = ContentInfo.getInstance((Object)instance.getObjectAt(i));
        }
        return array;
    }
    
    public boolean hasMac() {
        return this.pfx.getMacData() != null;
    }
    
    public AlgorithmIdentifier getMacAlgorithmID() {
        final MacData macData = this.pfx.getMacData();
        if (macData != null) {
            return macData.getMac().getAlgorithmId();
        }
        return null;
    }
    
    public boolean isMacValid(final PKCS12MacCalculatorBuilderProvider pkcs12MacCalculatorBuilderProvider, final char[] array) throws PKCSException {
        if (this.hasMac()) {
            final MacData macData = this.pfx.getMacData();
            final MacDataGenerator macDataGenerator = new MacDataGenerator(pkcs12MacCalculatorBuilderProvider.get(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), (ASN1Encodable)new PKCS12PBEParams(macData.getSalt(), macData.getIterationCount().intValue()))));
            try {
                return Arrays.constantTimeAreEqual(macDataGenerator.build(array, ASN1OctetString.getInstance((Object)this.pfx.getAuthSafe().getContent()).getOctets()).getEncoded(), this.pfx.getMacData().getEncoded());
            }
            catch (final IOException ex) {
                throw new PKCSException("unable to process AuthSafe: " + ex.getMessage());
            }
        }
        throw new IllegalStateException("no MAC present on PFX");
    }
    
    public Pfx toASN1Structure() {
        return this.pfx;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.toASN1Structure().getEncoded();
    }
    
    public byte[] getEncoded(final String s) throws IOException {
        return this.toASN1Structure().getEncoded(s);
    }
}
