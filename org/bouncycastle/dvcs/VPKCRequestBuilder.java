package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import java.util.Date;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.dvcs.CertEtcToken;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.ArrayList;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.ServiceType;
import java.util.List;

public class VPKCRequestBuilder extends DVCSRequestBuilder
{
    private List chains;
    
    public VPKCRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VPKC));
        this.chains = new ArrayList();
    }
    
    public void addTargetChain(final X509CertificateHolder x509CertificateHolder) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(0, (ASN1Encodable)x509CertificateHolder.toASN1Structure())));
    }
    
    public void addTargetChain(final Extension extension) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(extension)));
    }
    
    public void addTargetChain(final TargetChain targetChain) {
        this.chains.add(targetChain.toASN1Structure());
    }
    
    public void setRequestTime(final Date date) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(date));
    }
    
    public DVCSRequest build() throws DVCSException {
        return this.createDVCRequest(new Data((TargetEtcChain[])this.chains.toArray(new TargetEtcChain[this.chains.size()])));
    }
}
