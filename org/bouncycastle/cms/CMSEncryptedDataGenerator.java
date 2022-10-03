package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.BERSet;
import java.util.Map;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.BEROctetString;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEncryptedDataGenerator extends CMSEncryptedGenerator
{
    private CMSEncryptedData doGenerate(final CMSTypedData cmsTypedData, final OutputEncryptor outputEncryptor) throws CMSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final OutputStream outputStream = outputEncryptor.getOutputStream(byteArrayOutputStream);
            cmsTypedData.write(outputStream);
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CMSException("");
        }
        final EncryptedContentInfo encryptedContentInfo = new EncryptedContentInfo(cmsTypedData.getContentType(), outputEncryptor.getAlgorithmIdentifier(), (ASN1OctetString)new BEROctetString(byteArrayOutputStream.toByteArray()));
        Object o = null;
        if (this.unprotectedAttributeGenerator != null) {
            o = new BERSet(this.unprotectedAttributeGenerator.getAttributes(new HashMap()).toASN1EncodableVector());
        }
        return new CMSEncryptedData(new ContentInfo(CMSObjectIdentifiers.encryptedData, (ASN1Encodable)new EncryptedData(encryptedContentInfo, (ASN1Set)o)));
    }
    
    public CMSEncryptedData generate(final CMSTypedData cmsTypedData, final OutputEncryptor outputEncryptor) throws CMSException {
        return this.doGenerate(cmsTypedData, outputEncryptor);
    }
}
