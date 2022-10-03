package com.adventnet.client.util.web;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils
{
    public static String getParam(final HttpServletRequest request, final String parameterName, final String defaultValue) {
        String parameter = request.getParameter(parameterName);
        if (parameter == null) {
            parameter = defaultValue;
        }
        if (parameter == null) {
            throw new IllegalArgumentException(parameterName + " is not present in the request parameter list");
        }
        return parameter;
    }
    
    public static Long getParamAsLong(final HttpServletRequest request, final String parameterName, final Long defaultValue) {
        final String parameter = request.getParameter(parameterName);
        if (parameter == null && defaultValue != null) {
            return defaultValue;
        }
        if (parameter == null) {
            throw new IllegalArgumentException(parameterName + " is not present in the request parameter list");
        }
        try {
            return new Long(parameter);
        }
        catch (final NumberFormatException e) {
            throw new IllegalArgumentException(parameterName + " passed as request parameter is not a proper number");
        }
    }
    
    public static Integer getParamAsInt(final HttpServletRequest request, final String parameterName, final Integer defaultValue) {
        final String parameter = request.getParameter(parameterName);
        if (parameter == null && defaultValue != null) {
            return defaultValue;
        }
        if (parameter == null) {
            throw new IllegalArgumentException(parameterName + " is not present in the request parameter list");
        }
        try {
            return new Integer(parameter);
        }
        catch (final NumberFormatException e) {
            throw new IllegalArgumentException(parameterName + " passed as request parameter is not a proper number");
        }
    }
    
    public static boolean getParamAsBoolean(final HttpServletRequest request, final String parameterName, final boolean defaultValue) {
        final String parameter = request.getParameter(parameterName);
        if (parameter == null) {
            return defaultValue;
        }
        return "true".equals(parameter);
    }
}
