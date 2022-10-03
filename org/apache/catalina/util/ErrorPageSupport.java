package org.apache.catalina.util;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import java.util.concurrent.ConcurrentMap;

public class ErrorPageSupport
{
    private ConcurrentMap<String, ErrorPage> exceptionPages;
    private ConcurrentMap<Integer, ErrorPage> statusPages;
    
    public ErrorPageSupport() {
        this.exceptionPages = new ConcurrentHashMap<String, ErrorPage>();
        this.statusPages = new ConcurrentHashMap<Integer, ErrorPage>();
    }
    
    public void add(final ErrorPage errorPage) {
        final String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.put(errorPage.getErrorCode(), errorPage);
        }
        else {
            this.exceptionPages.put(exceptionType, errorPage);
        }
    }
    
    public void remove(final ErrorPage errorPage) {
        final String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            this.statusPages.remove(errorPage.getErrorCode(), errorPage);
        }
        else {
            this.exceptionPages.remove(exceptionType, errorPage);
        }
    }
    
    public ErrorPage find(final int statusCode) {
        return this.statusPages.get(statusCode);
    }
    
    @Deprecated
    public ErrorPage find(final String exceptionType) {
        return this.exceptionPages.get(exceptionType);
    }
    
    public ErrorPage find(final Throwable exceptionType) {
        if (exceptionType == null) {
            return null;
        }
        Class<?> clazz = exceptionType.getClass();
        String name = clazz.getName();
        while (!Object.class.equals(clazz)) {
            final ErrorPage errorPage = this.exceptionPages.get(name);
            if (errorPage != null) {
                return errorPage;
            }
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                break;
            }
            name = clazz.getName();
        }
        return null;
    }
    
    public ErrorPage[] findAll() {
        final Set<ErrorPage> errorPages = new HashSet<ErrorPage>();
        errorPages.addAll(this.exceptionPages.values());
        errorPages.addAll(this.statusPages.values());
        return errorPages.toArray(new ErrorPage[0]);
    }
}
