package com.me.ems.onpremise.uac.factory;

import java.util.Map;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;

public interface UserAccountValidator
{
    void validateAddition(final UserDetails p0) throws APIException;
    
    void validateUpdate(final UserDetails p0) throws APIException;
    
    void validateDelete(final Long p0, final Map<String, Object> p1) throws APIException;
}
