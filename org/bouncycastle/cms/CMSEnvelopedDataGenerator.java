package org.bouncycastle.cms;

import java.util.Iterator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.BERSet;
import java.util.Map;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.BEROctetString;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataGenerator extends CMSEnvelopedGenerator
{
    private CMSEnvelopedData doGenerate(final CMSTypedData cmsTypedData, final OutputEncryptor outputEncryptor) throws CMSException {
        if (!this.oldRecipientInfoGenerators.isEmpty()) {
            throw new IllegalStateException("can only use addRecipientGenerator() with this method");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final OutputStream outputStream = outputEncryptor.getOutputStream(byteArrayOutputStream);
            cmsTypedData.write(outputStream);
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CMSException("");
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        final AlgorithmIdentifier algorithmIdentifier = outputEncryptor.getAlgorithmIdentifier();
        final BEROctetString berOctetString = new BEROctetString(byteArray);
        final GenericKey key = outputEncryptor.getKey();
        final Iterator iterator = this.recipientInfoGenerators.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)((RecipientInfoGenerator)iterator.next()).generate(key));
        }
        final EncryptedContentInfo encryptedContentInfo = new EncryptedContentInfo(cmsTypedData.getContentType(), algorithmIdentifier, (ASN1OctetString)berOctetString);
        Object o = null;
        if (this.unprotectedAttributeGenerator != null) {
            o = new BERSet(this.unprotectedAttributeGenerator.getAttributes(new HashMap()).toASN1EncodableVector());
        }
        return new CMSEnvelopedData(new ContentInfo(CMSObjectIdentifiers.envelopedData, (ASN1Encodable)new EnvelopedData(this.originatorInfo, (ASN1Set)new DERSet(asn1EncodableVector), encryptedContentInfo, (ASN1Set)o)));
    }
    
    public CMSEnvelopedData generate(final CMSTypedData cmsTypedData, final OutputEncryptor outputEncryptor) throws CMSException {
        return this.doGenerate(cmsTypedData, outputEncryptor);
    }
}
