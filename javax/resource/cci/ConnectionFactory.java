package javax.resource.cci;

import javax.resource.ResourceException;
import javax.resource.Referenceable;
import java.io.Serializable;

public interface ConnectionFactory extends Serializable, Referenceable
{
    Connection getConnection() throws ResourceException;
    
    Connection getConnection(final ConnectionSpec p0) throws ResourceException;
    
    RecordFactory getRecordFactory() throws ResourceException;
    
    ResourceAdapterMetaData getMetaData() throws ResourceException;
}
