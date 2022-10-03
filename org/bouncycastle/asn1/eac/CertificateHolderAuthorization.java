package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.util.Integers;
import java.util.Hashtable;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CertificateHolderAuthorization extends ASN1Object
{
    ASN1ObjectIdentifier oid;
    DERApplicationSpecific accessRights;
    public static final ASN1ObjectIdentifier id_role_EAC;
    public static final int CVCA = 192;
    public static final int DV_DOMESTIC = 128;
    public static final int DV_FOREIGN = 64;
    public static final int IS = 0;
    public static final int RADG4 = 2;
    public static final int RADG3 = 1;
    static Hashtable RightsDecodeMap;
    static BidirectionalMap AuthorizationRole;
    static Hashtable ReverseMap;
    
    public static String getRoleDescription(final int n) {
        return CertificateHolderAuthorization.AuthorizationRole.get(Integers.valueOf(n));
    }
    
    public static int getFlag(final String s) {
        final Integer n = (Integer)CertificateHolderAuthorization.AuthorizationRole.getReverse(s);
        if (n == null) {
            throw new IllegalArgumentException("Unknown value " + s);
        }
        return n;
    }
    
    private void setPrivateData(final ASN1InputStream asn1InputStream) throws IOException {
        final ASN1Primitive object = asn1InputStream.readObject();
        if (!(object instanceof ASN1ObjectIdentifier)) {
            throw new IllegalArgumentException("no Oid in CerticateHolderAuthorization");
        }
        this.oid = (ASN1ObjectIdentifier)object;
        final ASN1Primitive object2 = asn1InputStream.readObject();
        if (object2 instanceof DERApplicationSpecific) {
            this.accessRights = (DERApplicationSpecific)object2;
            return;
        }
        throw new IllegalArgumentException("No access rights in CerticateHolderAuthorization");
    }
    
    public CertificateHolderAuthorization(final ASN1ObjectIdentifier oid, final int n) throws IOException {
        this.setOid(oid);
        this.setAccessRights((byte)n);
    }
    
    public CertificateHolderAuthorization(final DERApplicationSpecific derApplicationSpecific) throws IOException {
        if (derApplicationSpecific.getApplicationTag() == 76) {
            this.setPrivateData(new ASN1InputStream(derApplicationSpecific.getContents()));
        }
    }
    
    public int getAccessRights() {
        return this.accessRights.getContents()[0] & 0xFF;
    }
    
    private void setAccessRights(final byte b) {
        this.accessRights = new DERApplicationSpecific(19, new byte[] { b });
    }
    
    public ASN1ObjectIdentifier getOid() {
        return this.oid;
    }
    
    private void setOid(final ASN1ObjectIdentifier oid) {
        this.oid = oid;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.oid);
        asn1EncodableVector.add(this.accessRights);
        return new DERApplicationSpecific(76, asn1EncodableVector);
    }
    
    static {
        id_role_EAC = EACObjectIdentifiers.bsi_de.branch("3.1.2.1");
        CertificateHolderAuthorization.RightsDecodeMap = new Hashtable();
        CertificateHolderAuthorization.AuthorizationRole = new BidirectionalMap();
        CertificateHolderAuthorization.ReverseMap = new Hashtable();
        CertificateHolderAuthorization.RightsDecodeMap.put(Integers.valueOf(2), "RADG4");
        CertificateHolderAuthorization.RightsDecodeMap.put(Integers.valueOf(1), "RADG3");
        CertificateHolderAuthorization.AuthorizationRole.put(Integers.valueOf(192), "CVCA");
        CertificateHolderAuthorization.AuthorizationRole.put(Integers.valueOf(128), "DV_DOMESTIC");
        CertificateHolderAuthorization.AuthorizationRole.put(Integers.valueOf(64), "DV_FOREIGN");
        CertificateHolderAuthorization.AuthorizationRole.put(Integers.valueOf(0), "IS");
    }
}
