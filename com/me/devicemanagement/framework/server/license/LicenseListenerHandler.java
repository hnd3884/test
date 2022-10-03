package com.me.devicemanagement.framework.server.license;

import java.util.ArrayList;
import java.util.List;

public class LicenseListenerHandler
{
    private List<LicenseListener> licenseListenerList;
    private static LicenseListenerHandler licenseListenerHandler;
    
    public LicenseListenerHandler() {
        this.licenseListenerList = new ArrayList<LicenseListener>();
    }
    
    public static synchronized LicenseListenerHandler getInstance() {
        if (LicenseListenerHandler.licenseListenerHandler == null) {
            LicenseListenerHandler.licenseListenerHandler = new LicenseListenerHandler();
        }
        return LicenseListenerHandler.licenseListenerHandler;
    }
    
    public void addLicenseListener(final LicenseListener listener) {
        this.licenseListenerList.add(listener);
    }
    
    public void removeLicenseListener(final LicenseListener listener) {
        this.licenseListenerList.remove(listener);
    }
    
    public void invokeLicenseChangedListeners(final LicenseEvent licenseEvent) {
        for (int linstenerInt = 0; linstenerInt < this.licenseListenerList.size(); ++linstenerInt) {
            final LicenseListener listener = this.licenseListenerList.get(linstenerInt);
            listener.licenseChanged(licenseEvent);
        }
    }
    
    static {
        LicenseListenerHandler.licenseListenerHandler = null;
    }
}
