package org.bouncycastle.cms;

import java.util.Iterator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import java.util.Map;
import java.util.Collections;
import java.io.IOException;
import org.bouncycastle.asn1.BEROctetString;
import java.io.OutputStream;
import org.bouncycastle.util.io.TeeOutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.MacCalculator;

public class CMSAuthenticatedDataGenerator extends CMSAuthenticatedGenerator
{
    public CMSAuthenticatedData generate(final CMSTypedData cmsTypedData, final MacCalculator macCalculator) throws CMSException {
        return this.generate(cmsTypedData, macCalculator, null);
    }
    
    public CMSAuthenticatedData generate(final CMSTypedData cmsTypedData, final MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator iterator = this.recipientInfoGenerators.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)((RecipientInfoGenerator)iterator.next()).generate(macCalculator.getKey()));
        }
        AuthenticatedData authenticatedData;
        if (digestCalculator != null) {
            BEROctetString berOctetString;
            try {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final TeeOutputStream teeOutputStream = new TeeOutputStream(digestCalculator.getOutputStream(), (OutputStream)byteArrayOutputStream);
                cmsTypedData.write((OutputStream)teeOutputStream);
                ((OutputStream)teeOutputStream).close();
                berOctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
            }
            catch (final IOException ex) {
                throw new CMSException("unable to perform digest calculation: " + ex.getMessage(), ex);
            }
            final Map baseParameters = this.getBaseParameters(cmsTypedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), macCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest());
            if (this.authGen == null) {
                this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
            }
            final DERSet set = new DERSet(this.authGen.getAttributes(Collections.unmodifiableMap((Map<?, ?>)baseParameters)).toASN1EncodableVector());
            DEROctetString derOctetString;
            try {
                final OutputStream outputStream = macCalculator.getOutputStream();
                outputStream.write(((ASN1Set)set).getEncoded("DER"));
                outputStream.close();
                derOctetString = new DEROctetString(macCalculator.getMac());
            }
            catch (final IOException ex2) {
                throw new CMSException("exception decoding algorithm parameters.", ex2);
            }
            authenticatedData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(asn1EncodableVector), macCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), new ContentInfo(CMSObjectIdentifiers.data, (ASN1Encodable)berOctetString), (ASN1Set)set, (ASN1OctetString)derOctetString, (ASN1Set)((this.unauthGen != null) ? new BERSet(this.unauthGen.getAttributes(Collections.unmodifiableMap((Map<?, ?>)baseParameters)).toASN1EncodableVector()) : null));
        }
        else {
            BEROctetString berOctetString2;
            DEROctetString derOctetString2;
            try {
                final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                final TeeOutputStream teeOutputStream2 = new TeeOutputStream((OutputStream)byteArrayOutputStream2, macCalculator.getOutputStream());
                cmsTypedData.write((OutputStream)teeOutputStream2);
                ((OutputStream)teeOutputStream2).close();
                berOctetString2 = new BEROctetString(byteArrayOutputStream2.toByteArray());
                derOctetString2 = new DEROctetString(macCalculator.getMac());
            }
            catch (final IOException ex3) {
                throw new CMSException("exception decoding algorithm parameters.", ex3);
            }
            authenticatedData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(asn1EncodableVector), macCalculator.getAlgorithmIdentifier(), (AlgorithmIdentifier)null, new ContentInfo(CMSObjectIdentifiers.data, (ASN1Encodable)berOctetString2), (ASN1Set)null, (ASN1OctetString)derOctetString2, (ASN1Set)((this.unauthGen != null) ? new BERSet(this.unauthGen.getAttributes(new HashMap()).toASN1EncodableVector()) : null));
        }
        return new CMSAuthenticatedData(new ContentInfo(CMSObjectIdentifiers.authenticatedData, (ASN1Encodable)authenticatedData), new DigestCalculatorProvider() {
            public DigestCalculator get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                return digestCalculator;
            }
        });
    }
}
