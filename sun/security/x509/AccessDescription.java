package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class AccessDescription
{
    private int myhash;
    private ObjectIdentifier accessMethod;
    private GeneralName accessLocation;
    public static final ObjectIdentifier Ad_OCSP_Id;
    public static final ObjectIdentifier Ad_CAISSUERS_Id;
    public static final ObjectIdentifier Ad_TIMESTAMPING_Id;
    public static final ObjectIdentifier Ad_CAREPOSITORY_Id;
    
    public AccessDescription(final ObjectIdentifier accessMethod, final GeneralName accessLocation) {
        this.myhash = -1;
        this.accessMethod = accessMethod;
        this.accessLocation = accessLocation;
    }
    
    public AccessDescription(final DerValue derValue) throws IOException {
        this.myhash = -1;
        final DerInputStream data = derValue.getData();
        this.accessMethod = data.getOID();
        this.accessLocation = new GeneralName(data.getDerValue());
    }
    
    public ObjectIdentifier getAccessMethod() {
        return this.accessMethod;
    }
    
    public GeneralName getAccessLocation() {
        return this.accessLocation;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOID(this.accessMethod);
        this.accessLocation.encode(derOutputStream2);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public int hashCode() {
        if (this.myhash == -1) {
            this.myhash = this.accessMethod.hashCode() + this.accessLocation.hashCode();
        }
        return this.myhash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof AccessDescription)) {
            return false;
        }
        final AccessDescription accessDescription = (AccessDescription)o;
        return this == accessDescription || (this.accessMethod.equals((Object)accessDescription.getAccessMethod()) && this.accessLocation.equals(accessDescription.getAccessLocation()));
    }
    
    @Override
    public String toString() {
        String string;
        if (this.accessMethod.equals((Object)AccessDescription.Ad_CAISSUERS_Id)) {
            string = "caIssuers";
        }
        else if (this.accessMethod.equals((Object)AccessDescription.Ad_CAREPOSITORY_Id)) {
            string = "caRepository";
        }
        else if (this.accessMethod.equals((Object)AccessDescription.Ad_TIMESTAMPING_Id)) {
            string = "timeStamping";
        }
        else if (this.accessMethod.equals((Object)AccessDescription.Ad_OCSP_Id)) {
            string = "ocsp";
        }
        else {
            string = this.accessMethod.toString();
        }
        return "\n   accessMethod: " + string + "\n   accessLocation: " + this.accessLocation.toString() + "\n";
    }
    
    static {
        Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1 });
        Ad_CAISSUERS_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 2 });
        Ad_TIMESTAMPING_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 3 });
        Ad_CAREPOSITORY_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 5 });
    }
}
