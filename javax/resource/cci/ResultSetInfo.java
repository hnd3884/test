package javax.resource.cci;

import javax.resource.ResourceException;

public interface ResultSetInfo
{
    boolean deletesAreDetected(final int p0) throws ResourceException;
    
    boolean insertsAreDetected(final int p0) throws ResourceException;
    
    boolean othersDeletesAreVisible(final int p0) throws ResourceException;
    
    boolean othersInsertsAreVisible(final int p0) throws ResourceException;
    
    boolean othersUpdatesAreVisible(final int p0) throws ResourceException;
    
    boolean ownDeletesAreVisible(final int p0) throws ResourceException;
    
    boolean ownInsertsAreVisible(final int p0) throws ResourceException;
    
    boolean ownUpdatesAreVisible(final int p0) throws ResourceException;
    
    boolean supportsResultSetType(final int p0) throws ResourceException;
    
    boolean supportsResultTypeConcurrency(final int p0, final int p1) throws ResourceException;
    
    boolean updatesAreDetected(final int p0) throws ResourceException;
}
