package com.me.ems.onpremise.uac.core;

import java.util.Map;
import java.util.HashMap;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.List;
import org.json.JSONObject;

public interface UserManagementAPI
{
    void addUser(final JSONObject p0) throws Exception;
    
    List<String> getMappingDetailsInString(final Long p0) throws Exception;
    
    void updateUser(final JSONObject p0) throws Exception;
    
    void validateUser(final UserDetails p0) throws Exception;
    
    void handleAdminUserDelete(final Long p0, final HashMap p1);
    
    boolean deleteUser(final JSONObject p0, final HashMap p1) throws Exception;
    
    List getMobileUserMappingCustomGroup(final Long p0) throws Exception;
    
    List getComputerUserMappingCustomGroup(final Long p0) throws Exception;
    
    List getComputerUserMappingRemoteOffice(final Long p0) throws Exception;
    
    Map<? extends String, ?> getAddUserDetails() throws Exception;
}
