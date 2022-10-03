package org.bouncycastle.cms;

import org.bouncycastle.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSAuthenticatedGenerator extends CMSEnvelopedGenerator
{
    protected CMSAttributeTableGenerator authGen;
    protected CMSAttributeTableGenerator unauthGen;
    
    public void setAuthenticatedAttributeGenerator(final CMSAttributeTableGenerator authGen) {
        this.authGen = authGen;
    }
    
    public void setUnauthenticatedAttributeGenerator(final CMSAttributeTableGenerator unauthGen) {
        this.unauthGen = unauthGen;
    }
    
    protected Map getBaseParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) {
        final HashMap hashMap = new HashMap();
        hashMap.put("contentType", asn1ObjectIdentifier);
        hashMap.put("digestAlgID", algorithmIdentifier);
        hashMap.put("digest", Arrays.clone(array));
        hashMap.put("macAlgID", algorithmIdentifier2);
        return hashMap;
    }
}
