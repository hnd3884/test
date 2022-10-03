package com.me.mdm.server.apps.businessstore;

import org.json.JSONObject;

public interface UserDirectory
{
    JSONObject getUsers(final JSONObject p0);
    
    JSONObject addUser(final JSONObject p0);
    
    JSONObject updateUser(final JSONObject p0);
    
    JSONObject deleteUser(final JSONObject p0);
}
