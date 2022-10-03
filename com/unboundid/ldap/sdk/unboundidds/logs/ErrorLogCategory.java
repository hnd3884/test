package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ErrorLogCategory
{
    ACCESS_CONTROL, 
    ADMIN, 
    ADMIN_TOOL, 
    BACKEND, 
    CONFIG, 
    CORE, 
    DSCONFIG, 
    EXTENSIONS, 
    JEB, 
    LOG, 
    PLUGIN, 
    PROTOCOL, 
    PROXY, 
    QUICKSETUP, 
    REPLICATION, 
    RUNTIME_INFORMATION, 
    SCHEMA, 
    TASK, 
    THIRD_PARTY, 
    TOOLS, 
    UTIL, 
    VERSION;
    
    public static ErrorLogCategory forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "accesscontrol":
            case "access-control":
            case "access_control": {
                return ErrorLogCategory.ACCESS_CONTROL;
            }
            case "admin": {
                return ErrorLogCategory.ADMIN;
            }
            case "admintool":
            case "admin-tool":
            case "admin_tool": {
                return ErrorLogCategory.ADMIN_TOOL;
            }
            case "backend": {
                return ErrorLogCategory.BACKEND;
            }
            case "config": {
                return ErrorLogCategory.CONFIG;
            }
            case "core": {
                return ErrorLogCategory.CORE;
            }
            case "dsconfig": {
                return ErrorLogCategory.DSCONFIG;
            }
            case "extensions": {
                return ErrorLogCategory.EXTENSIONS;
            }
            case "jeb": {
                return ErrorLogCategory.JEB;
            }
            case "log": {
                return ErrorLogCategory.LOG;
            }
            case "plugin": {
                return ErrorLogCategory.PLUGIN;
            }
            case "protocol": {
                return ErrorLogCategory.PROTOCOL;
            }
            case "proxy": {
                return ErrorLogCategory.PROXY;
            }
            case "quicksetup": {
                return ErrorLogCategory.QUICKSETUP;
            }
            case "replication": {
                return ErrorLogCategory.REPLICATION;
            }
            case "runtimeinformation":
            case "runtime-information":
            case "runtime_information": {
                return ErrorLogCategory.RUNTIME_INFORMATION;
            }
            case "schema": {
                return ErrorLogCategory.SCHEMA;
            }
            case "task": {
                return ErrorLogCategory.TASK;
            }
            case "thirdparty":
            case "third-party":
            case "third_party": {
                return ErrorLogCategory.THIRD_PARTY;
            }
            case "tools": {
                return ErrorLogCategory.TOOLS;
            }
            case "util": {
                return ErrorLogCategory.UTIL;
            }
            case "version": {
                return ErrorLogCategory.VERSION;
            }
            default: {
                return null;
            }
        }
    }
}
