package java.rmi.server;

import java.rmi.Remote;

@Deprecated
public interface Skeleton
{
    @Deprecated
    void dispatch(final Remote p0, final RemoteCall p1, final int p2, final long p3) throws Exception;
    
    @Deprecated
    Operation[] getOperations();
}
