package jcifs.dcerpc.ndr;

public class NdrLong extends NdrObject
{
    public int value;
    
    public NdrLong(final int value) {
        this.value = value;
    }
    
    public void encode(final NdrBuffer dst) throws NdrException {
        dst.enc_ndr_long(this.value);
    }
    
    public void decode(final NdrBuffer src) throws NdrException {
        this.value = src.dec_ndr_long();
    }
}
