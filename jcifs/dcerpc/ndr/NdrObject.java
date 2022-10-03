package jcifs.dcerpc.ndr;

public abstract class NdrObject
{
    public abstract void encode(final NdrBuffer p0) throws NdrException;
    
    public abstract void decode(final NdrBuffer p0) throws NdrException;
}
