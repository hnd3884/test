package com.me.devicemanagement.framework.server.authentication;

import java.util.Hashtable;

public interface DCUserHandlerAPI
{
    Hashtable<String, String> userLastLogonDetails() throws Exception;
}
