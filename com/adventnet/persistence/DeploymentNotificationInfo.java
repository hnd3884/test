package com.adventnet.persistence;

import java.io.File;
import com.zoho.conf.AppResources;
import java.net.URL;
import java.io.Serializable;

public class DeploymentNotificationInfo implements Serializable
{
    private static final long serialVersionUID = -171400911113420063L;
    public static final int PRECREATE = 1;
    public static final int POSTCREATE = 2;
    public static final int POSTSTART = 3;
    public static final int PRESTOP = 4;
    public static final int PREDESTROY = 5;
    public static final int POSTDESTROY = 6;
    public static final int SERVER_STARTUP = 7;
    public static final int SERVER_SHUTDOWN = 8;
    public static final int INSTALL = 9;
    public static final int DEPLOY = 10;
    public static final int UNINSTALL = 11;
    public static final int UNDEPLOY = 12;
    public static final int POPULATE = 13;
    public static final int PRE_APP_STARTUP = 14;
    public static final int POST_APP_STARTUP = 15;
    public static final int POPULATE_APPLICATION_DATA = 16;
    private String applicationName;
    private String moduleName;
    private String tierId;
    private String tierType;
    private URL url;
    private boolean isFailure;
    private boolean fullPath;
    
    public DeploymentNotificationInfo(final String applicationName, final String moduleName, final URL url, final boolean isFailure) {
        this.tierType = "BE";
        this.applicationName = applicationName;
        this.moduleName = moduleName;
        this.tierId = AppResources.getString("tier-id");
        this.url = url;
        this.isFailure = isFailure;
    }
    
    public DeploymentNotificationInfo(final String applicationName, final String moduleName, final URL url, final boolean isFailure, final boolean fullPath) {
        this.tierType = "BE";
        this.applicationName = applicationName;
        this.moduleName = moduleName;
        this.tierId = AppResources.getString("tier-id");
        this.url = url;
        this.isFailure = isFailure;
        this.fullPath = fullPath;
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public String getTierId() {
        return this.tierId;
    }
    
    public String getTierType() {
        return this.tierType;
    }
    
    public URL getResource(final String fileName) {
        if (this.url == null) {
            return null;
        }
        if (this.fullPath) {
            return this.url;
        }
        if (this.moduleName != null && this.moduleName.equals("DeploymentManager")) {
            return this.getClass().getClassLoader().getResource(fileName);
        }
        final File f = new File(this.url.getFile());
        if (f.exists() && f.isFile()) {
            final String urlPrefix = "jar:" + this.url + "!/";
            try {
                final URL resourceURL = new URL(urlPrefix + fileName);
                resourceURL.openStream().close();
                return resourceURL;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        if (f.isDirectory()) {
            String urlstr = this.url.toString();
            if (!urlstr.endsWith("/")) {
                urlstr += "/";
            }
            urlstr += fileName;
            try {
                final URL resourceURL = new URL(urlstr);
                final String fileString = resourceURL.getFile();
                final File resourceFile = new File(fileString);
                if (resourceFile.exists()) {
                    return resourceURL;
                }
                return null;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        return null;
    }
    
    public boolean isFailureNotification() {
        return this.isFailure;
    }
    
    @Override
    public String toString() {
        return "Notification: application=" + this.applicationName + " moduleName=" + this.moduleName + " tierId=" + this.tierId + " tierType=" + this.tierType + " isFailure=" + this.isFailure + " URL=" + this.url;
    }
}
