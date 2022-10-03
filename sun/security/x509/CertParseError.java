package sun.security.x509;

@Deprecated
class CertParseError extends CertException
{
    private static final long serialVersionUID = -4559645519017017804L;
    
    CertParseError(final String s) {
        super(7, s);
    }
}
