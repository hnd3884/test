package com.me.devicemanagement.framework.webclient.factory;

import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import com.me.devicemanagement.framework.webclient.file.FormFileAPI;
import com.me.devicemanagement.framework.webclient.authentication.RBCAQueryAPI;
import com.me.devicemanagement.framework.webclient.alert.AlertsAPI;
import com.me.devicemanagement.framework.webclient.search.SearchCriteriaAPI;

public class WebclientAPIFactoryProvider
{
    private static SearchCriteriaAPI searchAPI;
    private static AlertsAPI alertsAPI;
    private static RBCAQueryAPI rbcaAPI;
    private static FormFileAPI formfileAPI;
    private static SessionAPI sessionAPI;
    private static final Logger LOGGER;
    
    public static SearchCriteriaAPI getSearchCriteria(final String className) {
        try {
            WebclientAPIFactoryProvider.searchAPI = (SearchCriteriaAPI)Class.forName(className).newInstance();
        }
        catch (final ClassNotFoundException ce) {
            WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for SearchCriteriaAPI... ", ce);
        }
        catch (final InstantiationException ie) {
            WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for SearchCriteriaAPI...", ie);
        }
        catch (final IllegalAccessException ie2) {
            WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for SearchCriteriaAPI...", ie2);
        }
        catch (final Exception ex) {
            WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for SearchCriteriaAPI...", ex);
        }
        return WebclientAPIFactoryProvider.searchAPI;
    }
    
    public static AlertsAPI getAlertAPI() {
        if (WebclientAPIFactoryProvider.alertsAPI == null) {
            try {
                WebclientAPIFactoryProvider.alertsAPI = (AlertsAPI)Class.forName("com.me.mdm.webclient.alert.MDMAlertsImpl").newInstance();
            }
            catch (final ClassNotFoundException ce) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for SearchCriteriaAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for SearchCriteriaAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for SearchCriteriaAPI...", ie2);
            }
            catch (final Exception ex) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for SearchCriteriaAPI...", ex);
            }
        }
        return WebclientAPIFactoryProvider.alertsAPI;
    }
    
    public static RBCAQueryAPI getRBCAQuery() {
        if (WebclientAPIFactoryProvider.rbcaAPI == null) {
            try {
                WebclientAPIFactoryProvider.rbcaAPI = (RBCAQueryAPI)Class.forName("com.me.dc.server.authentication.RBCAQueryImpl").newInstance();
            }
            catch (final ClassNotFoundException ce) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for RBCAQueryAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for RBCAQueryAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for RBCAQueryAPI...", ie2);
            }
            catch (final Exception ex) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for RBCAQueryAPI...", ex);
            }
        }
        return WebclientAPIFactoryProvider.rbcaAPI;
    }
    
    public static FormFileAPI getFormFileAPI() {
        if (WebclientAPIFactoryProvider.formfileAPI == null) {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                try {
                    WebclientAPIFactoryProvider.formfileAPI = (FormFileAPI)Class.forName("com.me.devicemanagement.onpremise.webclient.file.FormFileImpl").newInstance();
                }
                catch (final Exception ex) {
                    WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in ApiFactory.getFileAccessAPI in sas mode: " + ex);
                }
            }
            else {
                try {
                    WebclientAPIFactoryProvider.formfileAPI = (FormFileAPI)Class.forName("com.me.devicemanagement.cloud.webclient.file.FormFileImpl").newInstance();
                }
                catch (final Exception ex) {
                    WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in ApiFactory.getFileAccessAPI : " + ex);
                }
            }
        }
        return WebclientAPIFactoryProvider.formfileAPI;
    }
    
    public static SessionAPI getSessionAPI() {
        if (WebclientAPIFactoryProvider.sessionAPI == null) {
            String classname = null;
            try {
                classname = ProductClassLoader.getSingleImplProductClass("DM_SESSION_IMPL_CLASS");
                if (classname != null && classname.trim().length() != 0) {
                    WebclientAPIFactoryProvider.sessionAPI = (SessionAPI)Class.forName(classname).newInstance();
                }
            }
            catch (final Exception ex) {
                WebclientAPIFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in getSessionAPI" + classname, ex);
            }
        }
        return WebclientAPIFactoryProvider.sessionAPI;
    }
    
    static {
        WebclientAPIFactoryProvider.searchAPI = null;
        WebclientAPIFactoryProvider.alertsAPI = null;
        WebclientAPIFactoryProvider.rbcaAPI = null;
        WebclientAPIFactoryProvider.formfileAPI = null;
        WebclientAPIFactoryProvider.sessionAPI = null;
        LOGGER = Logger.getLogger(WebclientAPIFactoryProvider.class.getName());
    }
}
