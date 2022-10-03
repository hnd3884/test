package org.bouncycastle.est;

import java.io.IOException;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.est.AttrOrOID;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashMap;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.util.Encodable;

public class CSRAttributesResponse implements Encodable
{
    private final CsrAttrs csrAttrs;
    private final HashMap<ASN1ObjectIdentifier, AttrOrOID> index;
    
    public CSRAttributesResponse(final byte[] array) throws ESTException {
        this(parseBytes(array));
    }
    
    public CSRAttributesResponse(final CsrAttrs csrAttrs) throws ESTException {
        this.csrAttrs = csrAttrs;
        this.index = new HashMap<ASN1ObjectIdentifier, AttrOrOID>(csrAttrs.size());
        final AttrOrOID[] attrOrOIDs = csrAttrs.getAttrOrOIDs();
        for (int i = 0; i != attrOrOIDs.length; ++i) {
            final AttrOrOID attrOrOID = attrOrOIDs[i];
            if (attrOrOID.isOid()) {
                this.index.put(attrOrOID.getOid(), attrOrOID);
            }
            else {
                this.index.put(attrOrOID.getAttribute().getAttrType(), attrOrOID);
            }
        }
    }
    
    private static CsrAttrs parseBytes(final byte[] array) throws ESTException {
        try {
            return CsrAttrs.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final Exception ex) {
            throw new ESTException("malformed data: " + ex.getMessage(), ex);
        }
    }
    
    public boolean hasRequirement(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.index.containsKey(asn1ObjectIdentifier);
    }
    
    public boolean isAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.index.containsKey(asn1ObjectIdentifier) && !this.index.get(asn1ObjectIdentifier).isOid();
    }
    
    public boolean isEmpty() {
        return this.csrAttrs.size() == 0;
    }
    
    public Collection<ASN1ObjectIdentifier> getRequirements() {
        return this.index.keySet();
    }
    
    public byte[] getEncoded() throws IOException {
        return this.csrAttrs.getEncoded();
    }
}
