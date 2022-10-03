package com.me.ems.onpremise.uac.core;

import java.util.Map;
import org.json.JSONObject;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;

public interface UserOperationsInterface
{
    default void validateAddition(final UserDetails userDetails) throws APIException {
    }
    
    default void validateModification(final UserDetails userDetails) throws APIException {
    }
    
    default void validateDelete(final Long loginID) throws APIException {
    }
    
    default void doAddUserTableOperations(final JSONObject addUserJObj) throws APIException {
    }
    
    default void doModifyUserTableOperations(final JSONObject modifyUserJObj) throws APIException {
    }
    
    default void doDeleteUserTableOperations(final Long loginId) throws APIException {
    }
    
    default void getUserDetails(final Map<String, Object> userDetailsMap) throws APIException {
    }
}
