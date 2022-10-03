package jcifs.smb;

public interface SmbFilenameFilter
{
    boolean accept(final SmbFile p0, final String p1) throws SmbException;
}
