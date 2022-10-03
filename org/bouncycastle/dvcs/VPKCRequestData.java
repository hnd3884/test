package org.bouncycastle.dvcs;

import java.util.Collections;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import java.util.ArrayList;
import org.bouncycastle.asn1.dvcs.Data;
import java.util.List;

public class VPKCRequestData extends DVCSRequestData
{
    private List chains;
    
    VPKCRequestData(final Data data) throws DVCSConstructionException {
        super(data);
        final TargetEtcChain[] certs = data.getCerts();
        if (certs == null) {
            throw new DVCSConstructionException("DVCSRequest.data.certs should be specified for VPKC service");
        }
        this.chains = new ArrayList(certs.length);
        for (int i = 0; i != certs.length; ++i) {
            this.chains.add(new TargetChain(certs[i]));
        }
    }
    
    public List getCerts() {
        return Collections.unmodifiableList((List<?>)this.chains);
    }
}
