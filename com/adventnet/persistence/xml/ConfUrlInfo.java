package com.adventnet.persistence.xml;

import com.zoho.conf.Configuration;
import com.zoho.framework.utils.OSCheckUtil;
import java.io.File;
import java.net.URL;

public class ConfUrlInfo
{
    URL url;
    String moduleName;
    Long moduleId;
    private static String server_home;
    
    public ConfUrlInfo(final String moduleName, final Long moduleId, final URL serverHome) throws Exception {
        this.url = serverHome;
        this.moduleName = moduleName;
        this.moduleId = moduleId;
    }
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public Long getModuleId() {
        return this.moduleId;
    }
    
    public String getURL() {
        return this.url.toExternalForm();
    }
    
    public URL getResource(final String fileName) {
        String urlstr = this.url.toString();
        if (!urlstr.endsWith(File.separator)) {
            urlstr += File.separator;
        }
        urlstr = urlstr + "conf" + File.separator + this.moduleName + File.separator + fileName;
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
    
    public static String getRelativePath(String urlString) {
        try {
            final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
            final StringBuilder relativePath = new StringBuilder("file:");
            if (isWindows) {
                relativePath.append("/");
            }
            relativePath.append("${server.home}");
            if (isWindows) {
                relativePath.append("/");
            }
            urlString = urlString.replaceAll("\\\\", "/");
            relativePath.append(urlString.substring(urlString.lastIndexOf("/conf/")));
            return relativePath.toString();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getExactPath(final String relativePathString) {
        try {
            String serverHome = new File(ConfUrlInfo.server_home).toURL().toExternalForm();
            serverHome = serverHome.substring(5, serverHome.length() - 10);
            final String populationURL = relativePathString.replaceAll("\\$\\{server.home\\}", serverHome);
            return populationURL;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "ConfFile :: ModuleName --> " + this.moduleName + "   URL --> " + this.url;
    }
    
    static {
        ConfUrlInfo.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
