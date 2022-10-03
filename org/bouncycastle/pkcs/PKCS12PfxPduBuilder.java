package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSEncryptedDataGenerator;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.operator.OutputEncryptor;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1EncodableVector;

public class PKCS12PfxPduBuilder
{
    private ASN1EncodableVector dataVector;
    
    public PKCS12PfxPduBuilder() {
        this.dataVector = new ASN1EncodableVector();
    }
    
    public PKCS12PfxPduBuilder addData(final PKCS12SafeBag pkcs12SafeBag) throws IOException {
        this.dataVector.add((ASN1Encodable)new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString(new DLSequence((ASN1Encodable)pkcs12SafeBag.toASN1Structure()).getEncoded())));
        return this;
    }
    
    public PKCS12PfxPduBuilder addEncryptedData(final OutputEncryptor outputEncryptor, final PKCS12SafeBag pkcs12SafeBag) throws IOException {
        return this.addEncryptedData(outputEncryptor, (ASN1Sequence)new DERSequence((ASN1Encodable)pkcs12SafeBag.toASN1Structure()));
    }
    
    public PKCS12PfxPduBuilder addEncryptedData(final OutputEncryptor outputEncryptor, final PKCS12SafeBag[] array) throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add((ASN1Encodable)array[i].toASN1Structure());
        }
        return this.addEncryptedData(outputEncryptor, (ASN1Sequence)new DLSequence(asn1EncodableVector));
    }
    
    private PKCS12PfxPduBuilder addEncryptedData(final OutputEncryptor outputEncryptor, final ASN1Sequence asn1Sequence) throws IOException {
        final CMSEncryptedDataGenerator cmsEncryptedDataGenerator = new CMSEncryptedDataGenerator();
        try {
            this.dataVector.add((ASN1Encodable)cmsEncryptedDataGenerator.generate(new CMSProcessableByteArray(asn1Sequence.getEncoded()), outputEncryptor).toASN1Structure());
        }
        catch (final CMSException ex) {
            throw new PKCSIOException(ex.getMessage(), ex.getCause());
        }
        return this;
    }
    
    public PKCS12PfxPdu build(final PKCS12MacCalculatorBuilder pkcs12MacCalculatorBuilder, final char[] array) throws PKCSException {
        final AuthenticatedSafe instance = AuthenticatedSafe.getInstance((Object)new DLSequence(this.dataVector));
        byte[] encoded;
        try {
            encoded = instance.getEncoded();
        }
        catch (final IOException ex) {
            throw new PKCSException("unable to encode AuthenticatedSafe: " + ex.getMessage(), ex);
        }
        final ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString(encoded));
        MacData build = null;
        if (pkcs12MacCalculatorBuilder != null) {
            build = new MacDataGenerator(pkcs12MacCalculatorBuilder).build(array, encoded);
        }
        return new PKCS12PfxPdu(new Pfx(contentInfo, build));
    }
}
