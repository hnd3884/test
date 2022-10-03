package org.apache.catalina.mbeans;

import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import javax.management.MBeanException;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.catalina.Context;

public class ContextMBean extends BaseCatalinaMBean<Context>
{
    public String[] findApplicationParameters() throws MBeanException {
        final Context context = this.doGetManagedResource();
        final ApplicationParameter[] params = context.findApplicationParameters();
        final String[] stringParams = new String[params.length];
        for (int counter = 0; counter < params.length; ++counter) {
            stringParams[counter] = params[counter].toString();
        }
        return stringParams;
    }
    
    public String[] findConstraints() throws MBeanException {
        final Context context = this.doGetManagedResource();
        final SecurityConstraint[] constraints = context.findConstraints();
        final String[] stringConstraints = new String[constraints.length];
        for (int counter = 0; counter < constraints.length; ++counter) {
            stringConstraints[counter] = constraints[counter].toString();
        }
        return stringConstraints;
    }
    
    public String findErrorPage(final int errorCode) throws MBeanException {
        final Context context = this.doGetManagedResource();
        return context.findErrorPage(errorCode).toString();
    }
    
    @Deprecated
    public String findErrorPage(final String exceptionType) throws MBeanException {
        final Context context = this.doGetManagedResource();
        return context.findErrorPage(exceptionType).toString();
    }
    
    public String findErrorPage(final Throwable exceptionType) throws MBeanException {
        final Context context = this.doGetManagedResource();
        return context.findErrorPage(exceptionType).toString();
    }
    
    public String[] findErrorPages() throws MBeanException {
        final Context context = this.doGetManagedResource();
        final ErrorPage[] pages = context.findErrorPages();
        final String[] stringPages = new String[pages.length];
        for (int counter = 0; counter < pages.length; ++counter) {
            stringPages[counter] = pages[counter].toString();
        }
        return stringPages;
    }
    
    public String findFilterDef(final String name) throws MBeanException {
        final Context context = this.doGetManagedResource();
        final FilterDef filterDef = context.findFilterDef(name);
        return filterDef.toString();
    }
    
    public String[] findFilterDefs() throws MBeanException {
        final Context context = this.doGetManagedResource();
        final FilterDef[] filterDefs = context.findFilterDefs();
        final String[] stringFilters = new String[filterDefs.length];
        for (int counter = 0; counter < filterDefs.length; ++counter) {
            stringFilters[counter] = filterDefs[counter].toString();
        }
        return stringFilters;
    }
    
    public String[] findFilterMaps() throws MBeanException {
        final Context context = this.doGetManagedResource();
        final FilterMap[] maps = context.findFilterMaps();
        final String[] stringMaps = new String[maps.length];
        for (int counter = 0; counter < maps.length; ++counter) {
            stringMaps[counter] = maps[counter].toString();
        }
        return stringMaps;
    }
}
