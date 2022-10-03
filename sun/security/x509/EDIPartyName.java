package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class EDIPartyName implements GeneralNameInterface
{
    private static final byte TAG_ASSIGNER = 0;
    private static final byte TAG_PARTYNAME = 1;
    private String assigner;
    private String party;
    private int myhash;
    
    public EDIPartyName(final String assigner, final String party) {
        this.assigner = null;
        this.party = null;
        this.myhash = -1;
        this.assigner = assigner;
        this.party = party;
    }
    
    public EDIPartyName(final String party) {
        this.assigner = null;
        this.party = null;
        this.myhash = -1;
        this.party = party;
    }
    
    public EDIPartyName(final DerValue derValue) throws IOException {
        this.assigner = null;
        this.party = null;
        this.myhash = -1;
        final DerValue[] sequence = new DerInputStream(derValue.toByteArray()).getSequence(2);
        final int length = sequence.length;
        if (length < 1 || length > 2) {
            throw new IOException("Invalid encoding of EDIPartyName");
        }
        for (DerValue derValue2 : sequence) {
            if (derValue2.isContextSpecific((byte)0) && !derValue2.isConstructed()) {
                if (this.assigner != null) {
                    throw new IOException("Duplicate nameAssigner found in EDIPartyName");
                }
                derValue2 = derValue2.data.getDerValue();
                this.assigner = derValue2.getAsString();
            }
            if (derValue2.isContextSpecific((byte)1) && !derValue2.isConstructed()) {
                if (this.party != null) {
                    throw new IOException("Duplicate partyName found in EDIPartyName");
                }
                this.party = derValue2.data.getDerValue().getAsString();
            }
        }
    }
    
    @Override
    public int getType() {
        return 5;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        if (this.assigner != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putPrintableString(this.assigner);
            derOutputStream2.write(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream4);
        }
        if (this.party == null) {
            throw new IOException("Cannot have null partyName");
        }
        derOutputStream3.putPrintableString(this.party);
        derOutputStream2.write(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream3);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    public String getAssignerName() {
        return this.assigner;
    }
    
    public String getPartyName() {
        return this.party;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof EDIPartyName)) {
            return false;
        }
        final String assigner = ((EDIPartyName)o).assigner;
        if (this.assigner == null) {
            if (assigner != null) {
                return false;
            }
        }
        else if (!this.assigner.equals(assigner)) {
            return false;
        }
        final String party = ((EDIPartyName)o).party;
        if (this.party == null) {
            if (party != null) {
                return false;
            }
        }
        else if (!this.party.equals(party)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.myhash == -1) {
            this.myhash = 37 + ((this.party == null) ? 1 : this.party.hashCode());
            if (this.assigner != null) {
                this.myhash = 37 * this.myhash + this.assigner.hashCode();
            }
        }
        return this.myhash;
    }
    
    @Override
    public String toString() {
        return "EDIPartyName: " + ((this.assigner == null) ? "" : ("  nameAssigner = " + this.assigner + ",")) + "  partyName = " + this.party;
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else {
            if (generalNameInterface.getType() == 5) {
                throw new UnsupportedOperationException("Narrowing, widening, and matching of names not supported for EDIPartyName");
            }
            n = -1;
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("subtreeDepth() not supported for EDIPartyName");
    }
}
