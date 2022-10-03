package jcifs.smb;

public class SmbAuthException extends SmbException
{
    SmbAuthException(final int errcode) {
        super(errcode, null);
    }
}
