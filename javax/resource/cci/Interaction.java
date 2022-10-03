package javax.resource.cci;

import javax.resource.ResourceException;

public interface Interaction
{
    void clearWarnings() throws ResourceException;
    
    void close() throws ResourceException;
    
    Record execute(final InteractionSpec p0, final Record p1) throws ResourceException;
    
    boolean execute(final InteractionSpec p0, final Record p1, final Record p2) throws ResourceException;
    
    Connection getConnection() throws ResourceException;
    
    ResourceWarning getWarnings() throws ResourceException;
}
