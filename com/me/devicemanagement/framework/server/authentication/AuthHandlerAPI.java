package com.me.devicemanagement.framework.server.authentication;

import com.me.devicemanagement.framework.server.exception.SyMException;
import javax.security.auth.login.LoginException;
import org.json.JSONException;
import org.json.JSONObject;

public interface AuthHandlerAPI
{
    boolean login(final JSONObject p0) throws JSONException, LoginException, SyMException;
}
