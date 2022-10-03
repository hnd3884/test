package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.Data;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import java.math.BigInteger;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;

public abstract class DVCSRequestBuilder
{
    private final ExtensionsGenerator extGenerator;
    private final CMSSignedDataGenerator signedDataGen;
    protected final DVCSRequestInformationBuilder requestInformationBuilder;
    
    protected DVCSRequestBuilder(final DVCSRequestInformationBuilder requestInformationBuilder) {
        this.extGenerator = new ExtensionsGenerator();
        this.signedDataGen = new CMSSignedDataGenerator();
        this.requestInformationBuilder = requestInformationBuilder;
    }
    
    public void setNonce(final BigInteger nonce) {
        this.requestInformationBuilder.setNonce(nonce);
    }
    
    public void setRequester(final GeneralName requester) {
        this.requestInformationBuilder.setRequester(requester);
    }
    
    public void setDVCS(final GeneralName dvcs) {
        this.requestInformationBuilder.setDVCS(dvcs);
    }
    
    public void setDVCS(final GeneralNames dvcs) {
        this.requestInformationBuilder.setDVCS(dvcs);
    }
    
    public void setDataLocations(final GeneralName dataLocations) {
        this.requestInformationBuilder.setDataLocations(dataLocations);
    }
    
    public void setDataLocations(final GeneralNames dataLocations) {
        this.requestInformationBuilder.setDataLocations(dataLocations);
    }
    
    public void addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws DVCSException {
        try {
            this.extGenerator.addExtension(asn1ObjectIdentifier, b, asn1Encodable);
        }
        catch (final IOException ex) {
            throw new DVCSException("cannot encode extension: " + ex.getMessage(), ex);
        }
    }
    
    protected DVCSRequest createDVCRequest(final Data data) throws DVCSException {
        if (!this.extGenerator.isEmpty()) {
            this.requestInformationBuilder.setExtensions(this.extGenerator.generate());
        }
        return new DVCSRequest(new ContentInfo(DVCSObjectIdentifiers.id_ct_DVCSRequestData, (ASN1Encodable)new org.bouncycastle.asn1.dvcs.DVCSRequest(this.requestInformationBuilder.build(), data)));
    }
}
