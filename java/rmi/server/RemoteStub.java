package java.rmi.server;

@Deprecated
public abstract class RemoteStub extends RemoteObject
{
    private static final long serialVersionUID = -1585587260594494182L;
    
    protected RemoteStub() {
    }
    
    protected RemoteStub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    @Deprecated
    protected static void setRef(final RemoteStub remoteStub, final RemoteRef remoteRef) {
        throw new UnsupportedOperationException();
    }
}
