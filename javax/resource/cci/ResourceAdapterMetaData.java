package javax.resource.cci;

import javax.resource.ResourceException;

public interface ResourceAdapterMetaData
{
    String getAdapterName() throws ResourceException;
    
    String getAdapterShortDescription() throws ResourceException;
    
    String getAdapterVendorName() throws ResourceException;
    
    String getAdapterVersion() throws ResourceException;
    
    String[] getInteractionSpecsSupported() throws ResourceException;
    
    String getSpecVersion() throws ResourceException;
    
    boolean supportsExecuteWithInputAndOutputRecord() throws ResourceException;
    
    boolean supportsExecuteWithInputRecordOnly() throws ResourceException;
    
    boolean supportsLocalTransactionDemarcation() throws ResourceException;
}
