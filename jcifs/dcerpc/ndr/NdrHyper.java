package jcifs.dcerpc.ndr;

public class NdrHyper extends NdrObject
{
    public long value;
    
    public NdrHyper(final long value) {
        this.value = value;
    }
    
    public void encode(final NdrBuffer dst) throws NdrException {
        dst.enc_ndr_hyper(this.value);
    }
    
    public void decode(final NdrBuffer src) throws NdrException {
        this.value = src.dec_ndr_hyper();
    }
}
