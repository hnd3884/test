package sun.rmi.transport.proxy;

class CGIClientException extends Exception
{
    private static final long serialVersionUID = 8147981687059865216L;
    
    public CGIClientException(final String s) {
        super(s);
    }
    
    public CGIClientException(final String s, final Throwable t) {
        super(s, t);
    }
}
