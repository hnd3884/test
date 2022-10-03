package javax.resource.cci;

import javax.resource.ResourceException;

public interface Connection
{
    void close() throws ResourceException;
    
    Interaction createInteraction() throws ResourceException;
    
    LocalTransaction getLocalTransaction() throws ResourceException;
    
    ConnectionMetaData getMetaData() throws ResourceException;
    
    ResultSetInfo getResultSetInfo() throws ResourceException;
}
