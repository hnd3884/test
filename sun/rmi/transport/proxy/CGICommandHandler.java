package sun.rmi.transport.proxy;

interface CGICommandHandler
{
    String getName();
    
    void execute(final String p0) throws CGIClientException, CGIServerException;
}
