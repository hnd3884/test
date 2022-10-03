package jcifs.smb;

public interface SmbFileFilter
{
    boolean accept(final SmbFile p0) throws SmbException;
}
