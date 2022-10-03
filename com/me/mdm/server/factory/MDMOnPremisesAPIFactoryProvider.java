package com.me.mdm.server.factory;

import com.me.mdm.onpremise.remotesession.auth_token.AssistAuthApiHandler;
import javax.resource.NotSupportedException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Logger;
import com.me.mdm.onpremise.remotesession.AssistApiHandlerImpl;
import com.me.mdm.server.util.MDMServerDetailsUtil;

public class MDMOnPremisesAPIFactoryProvider
{
    private static MDMServerDetailsUtil mdmServerDetailsUtil;
    private static AssistApiHandlerImpl assistApiHandlerImpl;
    private static Logger logger;
    
    public static Object getImplClassInstance(final String key) {
        String classname = null;
        try {
            classname = ProductClassLoader.getSingleImplProductClass(key);
            if (classname != null && classname.trim().length() != 0) {
                return Class.forName(classname).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            MDMOnPremisesAPIFactoryProvider.logger.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + classname, ce);
        }
        catch (final InstantiationException ie) {
            MDMOnPremisesAPIFactoryProvider.logger.log(Level.SEVERE, "InstantiationException During Instantiation  for" + classname, ie);
        }
        catch (final IllegalAccessException ie2) {
            MDMOnPremisesAPIFactoryProvider.logger.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + classname, ie2);
        }
        catch (final NotSupportedException e) {
            MDMOnPremisesAPIFactoryProvider.logger.log(Level.SEVERE, "Exception while trying to get class value for" + key, (Throwable)e);
        }
        catch (final Exception ex) {
            MDMOnPremisesAPIFactoryProvider.logger.log(Level.SEVERE, "Exception During Instantiation  for" + classname, ex);
        }
        return null;
    }
    
    public static MDMServerDetailsUtil getMdmServerDetailsUtil() {
        if (MDMOnPremisesAPIFactoryProvider.mdmServerDetailsUtil == null) {
            MDMOnPremisesAPIFactoryProvider.mdmServerDetailsUtil = (MDMServerDetailsUtil)getImplClassInstance("MDM_SERVER_DETAILS_UTIL");
        }
        return MDMOnPremisesAPIFactoryProvider.mdmServerDetailsUtil;
    }
    
    public static AssistApiHandlerImpl getAssistApiHandlerImpl(final int param) {
        switch (param) {
            case 0: {
                if (MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl == null) {
                    MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl = (AssistApiHandlerImpl)getImplClassInstance("ASSIST_AUTHAPI_HANDLER_IMPL");
                }
                return MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl;
            }
            case 1: {
                if (MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl == null || MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl instanceof AssistAuthApiHandler) {
                    MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl = (AssistApiHandlerImpl)getImplClassInstance("ASSIST_OAUTH_API_HANDLER_IMPL");
                }
                return MDMOnPremisesAPIFactoryProvider.assistApiHandlerImpl;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        MDMOnPremisesAPIFactoryProvider.logger = Logger.getLogger(MDMOnPremisesAPIFactoryProvider.class.getName());
    }
}
