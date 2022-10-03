package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface OperationResultAccessLogMessage extends MinimalOperationResultAccessLogMessage
{
    Long getIntermediateResponsesReturned();
    
    List<String> getResponseControlOIDs();
    
    List<String> getServersAccessed();
    
    String getIntermediateClientResult();
}
