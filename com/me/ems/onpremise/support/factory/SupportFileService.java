package com.me.ems.onpremise.support.factory;

import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;

public interface SupportFileService
{
    Map getSupportFileDetails(final String p0, final String p1) throws APIException;
    
    Map getProcessStatus() throws APIException;
    
    void cancelSupportFileCreation() throws APIException;
    
    Map supportFileCreationForDBLock(final Map p0);
    
    Long validateCustomer(final String p0, final Map p1) throws APIException;
    
    Map supportFileCreation(final Map p0);
    
    Response downloadSupportFile() throws APIException;
    
    void validateSupportFileData(final Map p0) throws APIException;
}
