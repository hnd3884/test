package javax.xml.crypto;

public interface URIDereferencer
{
    Data dereference(final URIReference p0, final XMLCryptoContext p1) throws URIReferenceException;
}
