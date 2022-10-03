package jcifs.dcerpc.ndr;

public class NdrShort extends NdrObject
{
    public int value;
    
    public NdrShort(final int value) {
        this.value = (value & 0xFF);
    }
    
    public void encode(final NdrBuffer dst) throws NdrException {
        dst.enc_ndr_short(this.value);
    }
    
    public void decode(final NdrBuffer src) throws NdrException {
        this.value = src.dec_ndr_short();
    }
}
