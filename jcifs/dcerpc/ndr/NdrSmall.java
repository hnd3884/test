package jcifs.dcerpc.ndr;

public class NdrSmall extends NdrObject
{
    public int value;
    
    public NdrSmall(final int value) {
        this.value = (value & 0xFF);
    }
    
    public void encode(final NdrBuffer dst) throws NdrException {
        dst.enc_ndr_small(this.value);
    }
    
    public void decode(final NdrBuffer src) throws NdrException {
        this.value = src.dec_ndr_small();
    }
}
