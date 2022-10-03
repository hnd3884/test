package jcifs.smb;

public interface SmbExtendedAuthenticator
{
    void sessionSetup(final SmbSession p0, final ServerMessageBlock p1, final ServerMessageBlock p2) throws SmbException;
}
