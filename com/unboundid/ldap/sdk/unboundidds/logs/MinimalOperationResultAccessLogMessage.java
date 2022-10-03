package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.List;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface MinimalOperationResultAccessLogMessage
{
    ResultCode getResultCode();
    
    String getDiagnosticMessage();
    
    String getAdditionalInformation();
    
    String getMatchedDN();
    
    List<String> getReferralURLs();
    
    Double getProcessingTimeMillis();
    
    Double getQueueTimeMillis();
}
