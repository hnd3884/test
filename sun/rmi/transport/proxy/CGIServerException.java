package sun.rmi.transport.proxy;

class CGIServerException extends Exception
{
    private static final long serialVersionUID = 6928425456704527017L;
    
    public CGIServerException(final String s) {
        super(s);
    }
    
    public CGIServerException(final String s, final Throwable t) {
        super(s, t);
    }
}
