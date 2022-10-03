package com.adventnet.sym.server.mdm.apps;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AppsLicensesHandler
{
    public static final int APP_LICENSE_DIRECT_REMOVAL = 1;
    public static final int APP_LICENSE_TO_BE_REASSIGNED_REMOVAL = 2;
    Logger logger;
    private static AppsLicensesHandler appsLicensesHandler;
    private List<AppsLicensesHandlingListener> licenseHandlingListeners;
    
    public AppsLicensesHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
        this.licenseHandlingListeners = new ArrayList<AppsLicensesHandlingListener>();
    }
    
    public static AppsLicensesHandler getInstance() {
        if (AppsLicensesHandler.appsLicensesHandler == null) {
            AppsLicensesHandler.appsLicensesHandler = new AppsLicensesHandler();
        }
        return AppsLicensesHandler.appsLicensesHandler;
    }
    
    public void addAppLicenseHandlingListeners(final AppsLicensesHandlingListener appsLicensesHandlingListener) {
        this.licenseHandlingListeners.add(appsLicensesHandlingListener);
    }
    
    public void invokeLicenseHandlingListener(final AppsLicensesHandlerEvent appsLicensesHandlerEvent, final int operation) {
        this.logger.log(Level.INFO, "Invoking AppsLicensesHandlingListener with AppsLicensesHandlerEvent: {0} and operation: {1}", new Object[] { appsLicensesHandlerEvent, operation });
        for (int l = this.licenseHandlingListeners.size(), i = 0; i < l; ++i) {
            final AppsLicensesHandlingListener appsLicensesHandlingListener = this.licenseHandlingListeners.get(i);
            if (operation == 1) {
                appsLicensesHandlingListener.appLicenseDirectRemoval(appsLicensesHandlerEvent);
            }
            else if (operation == 2) {
                appsLicensesHandlingListener.appReassignedLicenseRemoval(appsLicensesHandlerEvent);
            }
        }
    }
    
    static {
        AppsLicensesHandler.appsLicensesHandler = null;
    }
}
