package com.adventnet.authentication.lm;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

public class ADHandler
{
    private static ADHandler adHandler;
    private static Logger nativeLogger;
    
    private static synchronized native Properties GetUserObjectForNTFormat(final Properties p0, final String p1, final ArrayList p2, final boolean p3);
    
    public Properties getADUserProperty(final String ntLogin, final String domainName, final String serverName, final String userName, final String passWord, final boolean isSSL) {
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("DOMAIN_NAME", domainName);
        ((Hashtable<String, String>)prop).put("DOMAIN_CONTROLLER_NAME", serverName);
        ((Hashtable<String, String>)prop).put("USER_NAME", userName);
        ((Hashtable<String, String>)prop).put("PASSWORD", passWord);
        ((Hashtable<String, String>)prop).put("IS_LOCAL_DOMAIN", "false");
        ((Hashtable<String, String>)prop).put("IS_AUTHENTICATION_REQUIRED", "true");
        ((Hashtable<String, String>)prop).put("ROOT_DOMAIN_NAMING_CONTEXT", "");
        ((Hashtable<String, String>)prop).put("SCHEMA_NAMING_CONTEXT", "");
        ((Hashtable<String, String>)prop).put("CONFIGURATION_NAMING_CONTEXT", "");
        final ArrayList list = new ArrayList();
        list.add("General");
        list.add("Address");
        list.add("Account");
        list.add("Profile");
        list.add("Telephones");
        list.add("Organization");
        list.add("Groups");
        final Properties pr = GetUserObjectForNTFormat(prop, domainName + "\\" + ntLogin, list, isSSL);
        return pr;
    }
    
    static {
        ADHandler.adHandler = null;
        ADHandler.nativeLogger = Logger.getLogger("ADSMLogger");
        System.loadLibrary("ADHandler");
    }
}
