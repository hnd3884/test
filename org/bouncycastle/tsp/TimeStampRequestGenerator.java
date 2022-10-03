package org.bouncycastle.tsp;

import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import java.math.BigInteger;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class TimeStampRequestGenerator
{
    private ASN1ObjectIdentifier reqPolicy;
    private ASN1Boolean certReq;
    private ExtensionsGenerator extGenerator;
    
    public TimeStampRequestGenerator() {
        this.extGenerator = new ExtensionsGenerator();
    }
    
    @Deprecated
    public void setReqPolicy(final String s) {
        this.reqPolicy = new ASN1ObjectIdentifier(s);
    }
    
    public void setReqPolicy(final ASN1ObjectIdentifier reqPolicy) {
        this.reqPolicy = reqPolicy;
    }
    
    public void setCertReq(final boolean b) {
        this.certReq = ASN1Boolean.getInstance(b);
    }
    
    @Deprecated
    public void addExtension(final String s, final boolean b, final ASN1Encodable asn1Encodable) throws IOException {
        this.addExtension(s, b, asn1Encodable.toASN1Primitive().getEncoded());
    }
    
    @Deprecated
    public void addExtension(final String s, final boolean b, final byte[] array) {
        this.extGenerator.addExtension(new ASN1ObjectIdentifier(s), b, array);
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws TSPIOException {
        TSPUtil.addExtension(this.extGenerator, asn1ObjectIdentifier, b, asn1Encodable);
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) {
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, array);
    }
    
    @Deprecated
    public TimeStampRequest generate(final String s, final byte[] array) {
        return this.generate(s, array, null);
    }
    
    @Deprecated
    public TimeStampRequest generate(final String s, final byte[] array, final BigInteger bigInteger) {
        if (s == null) {
            throw new IllegalArgumentException("No digest algorithm specified");
        }
        final MessageImprint messageImprint = new MessageImprint(new AlgorithmIdentifier(new ASN1ObjectIdentifier(s), (ASN1Encodable)DERNull.INSTANCE), array);
        Extensions generate = null;
        if (!this.extGenerator.isEmpty()) {
            generate = this.extGenerator.generate();
        }
        if (bigInteger != null) {
            return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, new ASN1Integer(bigInteger), this.certReq, generate));
        }
        return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, (ASN1Integer)null, this.certReq, generate));
    }
    
    public TimeStampRequest generate(final ASN1ObjectIdentifier asn1ObjectIdentifier, final byte[] array) {
        return this.generate(asn1ObjectIdentifier.getId(), array);
    }
    
    public TimeStampRequest generate(final ASN1ObjectIdentifier asn1ObjectIdentifier, final byte[] array, final BigInteger bigInteger) {
        return this.generate(asn1ObjectIdentifier.getId(), array, bigInteger);
    }
}
