package com.zoho.clustering.agent.util;

import java.io.OutputStream;
import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.util.UrlUtil;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;

public class ServletUtil
{
    private static String brac(final Object content) {
        return " [" + content + "] ";
    }
    
    public static class Config
    {
        public static String value(final ServletConfig config, final String paramName) {
            final String value = config.getInitParameter(paramName);
            if (value == null || value.length() == 0) {
                throw new IllegalArgumentException("A mandatory servlet parameter" + brac(paramName) + "is missing.");
            }
            return value;
        }
        
        public static String value(final ServletConfig config, final String paramName, final String defaultValue) {
            final String value = config.getInitParameter(paramName);
            return (value != null) ? value : defaultValue;
        }
        
        public static int intValue(final ServletConfig config, final String paramName) {
            final String value = value(config, paramName);
            try {
                return Integer.parseInt(value);
            }
            catch (final NumberFormatException exp) {
                throw new IllegalArgumentException("Thee value" + brac(value) + "specified for the paramater" + brac(paramName) + "is incorrect");
            }
        }
        
        public static int intValue(final HttpServletRequest request, final String paramName, final int defaultValue) {
            final String value = request.getParameter(paramName);
            try {
                return (value != null) ? Integer.parseInt(value) : defaultValue;
            }
            catch (final NumberFormatException ignored) {
                throw new IllegalArgumentException("Thee value" + brac(value) + "specified for the paramater" + brac(paramName) + "is incorrect");
            }
        }
    }
    
    public static class Param
    {
        public static String optionalValue(final HttpServletRequest request, final String paramName) {
            final String value = request.getParameter(paramName);
            return (value == null) ? null : value.trim();
        }
        
        public static String value(final HttpServletRequest request, final String paramName) {
            final String value = optionalValue(request, paramName);
            if (value == null || value.length() == 0) {
                throw new IllegalArgumentException("The parameter" + brac(paramName) + "required for processing this request is missing.");
            }
            return value;
        }
        
        public static String value(final HttpServletRequest request, final String paramName, final String defaultValue) {
            final String value = optionalValue(request, paramName);
            return (value != null) ? value : defaultValue;
        }
        
        public static int intValue(final HttpServletRequest request, final String paramName) {
            final String value = value(request, paramName);
            try {
                return Integer.parseInt(value);
            }
            catch (final NumberFormatException exp) {
                throw new IllegalArgumentException("The value" + brac(value) + "specified for the paramater" + brac(paramName) + "is incorrect");
            }
        }
        
        public static int intValue(final HttpServletRequest request, final String paramName, final int defaultValue) {
            final String value = optionalValue(request, paramName);
            try {
                return (value != null) ? Integer.parseInt(value) : defaultValue;
            }
            catch (final NumberFormatException ignored) {
                throw new IllegalArgumentException("The value" + brac(value) + "specified for the paramater" + brac(paramName) + "is incorrect");
            }
        }
        
        public static boolean boolValue(final HttpServletRequest request, final String paramName) {
            final String value = value(request, paramName);
            return toBoolean(value, paramName);
        }
        
        public static boolean boolValue(final HttpServletRequest request, final String paramName, final boolean defaultValue) {
            final String value = optionalValue(request, paramName);
            return (value != null) ? toBoolean(value, paramName) : defaultValue;
        }
        
        private static boolean toBoolean(String value, final String paramName) {
            value = value.toLowerCase();
            if ("true".equals(value)) {
                return true;
            }
            if ("false".equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("Wrong boolean string " + brac(value) + "specified for the paramater" + brac(paramName));
        }
    }
    
    public static class Write
    {
        public static void text(final HttpServletResponse response, final String message) throws IOException {
            response.setContentType("text/plain");
            response.getWriter().write(message);
        }
        
        public static void text(final HttpServletResponse response, final int sc, final String message) throws IOException {
            response.setContentType("text/plain");
            response.setStatus(sc);
            response.getWriter().write(message);
        }
        
        public static void xml(final HttpServletResponse response, final String xmlMessage) throws IOException {
            response.setContentType("text/xml;charset=utf-8");
            response.getWriter().println(xmlMessage);
        }
        
        public static void xml(final HttpServletResponse response, final int sc, final String xmlMessage) throws IOException {
            response.setContentType("text/xml;charset=utf-8");
            response.setStatus(sc);
            response.getWriter().println(xmlMessage);
        }
        
        public static void file(final HttpServletResponse response, final File file) throws IOException {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + UrlUtil.encode(file.getName()) + "\"");
            try {
                FileUtil.copyFromFile(file, (OutputStream)response.getOutputStream());
            }
            catch (final RuntimeException exp) {
                final Throwable cause = exp.getCause();
                if (cause != null && cause instanceof IOException) {
                    throw (IOException)cause;
                }
            }
        }
    }
}
