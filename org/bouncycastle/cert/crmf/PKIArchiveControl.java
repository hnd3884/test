package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKIArchiveControl implements Control
{
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private static final ASN1ObjectIdentifier type;
    private final PKIArchiveOptions pkiArchiveOptions;
    
    public PKIArchiveControl(final PKIArchiveOptions pkiArchiveOptions) {
        this.pkiArchiveOptions = pkiArchiveOptions;
    }
    
    public ASN1ObjectIdentifier getType() {
        return PKIArchiveControl.type;
    }
    
    public ASN1Encodable getValue() {
        return (ASN1Encodable)this.pkiArchiveOptions;
    }
    
    public int getArchiveType() {
        return this.pkiArchiveOptions.getType();
    }
    
    public boolean isEnvelopedData() {
        return !EncryptedKey.getInstance((Object)this.pkiArchiveOptions.getValue()).isEncryptedValue();
    }
    
    public CMSEnvelopedData getEnvelopedData() throws CRMFException {
        try {
            return new CMSEnvelopedData(new ContentInfo(CMSObjectIdentifiers.envelopedData, (ASN1Encodable)EnvelopedData.getInstance((Object)EncryptedKey.getInstance((Object)this.pkiArchiveOptions.getValue()).getValue())));
        }
        catch (final CMSException ex) {
            throw new CRMFException("CMS parsing error: " + ex.getMessage(), ex.getCause());
        }
        catch (final Exception ex2) {
            throw new CRMFException("CRMF parsing error: " + ex2.getMessage(), ex2);
        }
    }
    
    static {
        type = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
    }
}
