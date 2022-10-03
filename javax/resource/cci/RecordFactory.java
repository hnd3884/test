package javax.resource.cci;

import javax.resource.ResourceException;

public interface RecordFactory
{
    MappedRecord createMappedRecord(final String p0) throws ResourceException;
    
    IndexedRecord createIndexedRecord(final String p0) throws ResourceException;
}
