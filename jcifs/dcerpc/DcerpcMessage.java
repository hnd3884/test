package jcifs.dcerpc;

import jcifs.dcerpc.ndr.NdrException;
import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrObject;

public abstract class DcerpcMessage extends NdrObject implements DcerpcConstants
{
    protected int ptype;
    protected int flags;
    protected int length;
    protected int call_id;
    protected int alloc_hint;
    protected int result;
    
    public DcerpcMessage() {
        this.ptype = -1;
        this.flags = 0;
        this.length = 0;
        this.call_id = 0;
        this.alloc_hint = 0;
        this.result = 0;
    }
    
    public boolean isFlagSet(final int flag) {
        return (this.flags & flag) == flag;
    }
    
    public void unsetFlag(final int flag) {
        this.flags |= flag;
    }
    
    public void setFlag(final int flag) {
        this.flags |= flag;
    }
    
    public DcerpcException getResult() {
        if (this.result != 0) {
            return new DcerpcException(this.result);
        }
        return null;
    }
    
    void encode_header(final NdrBuffer buf) {
        buf.enc_ndr_small(5);
        buf.enc_ndr_small(0);
        buf.enc_ndr_small(this.ptype);
        buf.enc_ndr_small(this.flags);
        buf.enc_ndr_long(16);
        buf.enc_ndr_short(this.length);
        buf.enc_ndr_short(0);
        buf.enc_ndr_long(this.call_id);
    }
    
    void decode_header(final NdrBuffer buf) throws NdrException {
        buf.dec_ndr_small();
        buf.dec_ndr_small();
        this.ptype = buf.dec_ndr_small();
        this.flags = buf.dec_ndr_small();
        if (buf.dec_ndr_long() != 16) {
            throw new NdrException("Data representation not supported");
        }
        this.length = buf.dec_ndr_short();
        if (buf.dec_ndr_short() != 0) {
            throw new NdrException("DCERPC authentication not supported");
        }
        this.call_id = buf.dec_ndr_long();
    }
    
    public void encode(final NdrBuffer buf) throws NdrException {
        final int start = buf.getIndex();
        int alloc_hint_index = 0;
        buf.advance(16);
        if (this.ptype == 0) {
            alloc_hint_index = buf.getIndex();
            buf.enc_ndr_long(0);
            buf.enc_ndr_short(0);
            buf.enc_ndr_short(this.getOpnum());
        }
        this.encode_in(buf);
        this.length = buf.getIndex() - start;
        if (this.ptype == 0) {
            buf.setIndex(alloc_hint_index);
            buf.enc_ndr_long(this.alloc_hint = this.length - alloc_hint_index);
        }
        buf.setIndex(start);
        this.encode_header(buf);
        buf.setIndex(start + this.length);
    }
    
    public void decode(final NdrBuffer buf) throws NdrException {
        this.decode_header(buf);
        if (this.ptype != 12 && this.ptype != 2 && this.ptype != 3) {
            throw new NdrException("Unexpected ptype: " + this.ptype);
        }
        if (this.ptype == 2 || this.ptype == 3) {
            this.alloc_hint = buf.dec_ndr_long();
            buf.dec_ndr_short();
            buf.dec_ndr_short();
        }
        if (this.ptype == 3) {
            this.result = buf.dec_ndr_long();
        }
        else {
            this.decode_out(buf);
        }
    }
    
    public abstract int getOpnum();
    
    public abstract void encode_in(final NdrBuffer p0) throws NdrException;
    
    public abstract void decode_out(final NdrBuffer p0) throws NdrException;
}
