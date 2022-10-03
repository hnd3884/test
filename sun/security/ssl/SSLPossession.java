package sun.security.ssl;

interface SSLPossession
{
    default byte[] encode() {
        return new byte[0];
    }
}
