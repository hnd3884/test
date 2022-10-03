package com.adventnet.tools.update.installer;

import java.io.File;
import java.applet.Applet;
import javax.swing.filechooser.FileFilter;

public class CertificateFilter extends FileFilter
{
    private final String[] extension;
    static BuilderResourceBundle resourceBundle;
    private String localePropertiesFileName;
    private ParameterObject po;
    private Applet applet;
    
    public CertificateFilter() {
        this.extension = new String[] { "cer", "crt" };
        this.localePropertiesFileName = "UpdateManagerResources";
        this.po = null;
        this.applet = null;
    }
    
    @Override
    public boolean accept(final File f) {
        return f.isDirectory() || this.isExtensionPatch(f);
    }
    
    @Override
    public String getDescription() {
        if (this.getParameter("RESOURCE_PROPERTIES") != null) {
            this.localePropertiesFileName = this.getParameter("RESOURCE_PROPERTIES");
        }
        CertificateFilter.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        return CertificateFilter.resourceBundle.getString("Certificate Files(*." + this.extension[0] + ", *." + this.extension[1] + ")");
    }
    
    private boolean isExtensionPatch(final File file) {
        final String fileName = file.getName();
        return fileName.endsWith("." + this.extension[0]) || fileName.endsWith("." + this.extension[1]);
    }
    
    public String getParameter(final String input) {
        if (this.po != null && this.po.getParameter(input) != null) {
            return (String)this.po.getParameter(input);
        }
        String value;
        if (this.applet != null) {
            value = this.applet.getParameter(input);
        }
        else {
            value = (String)Utility.getParameter(input);
        }
        if (value == null) {
            if (input.equals("RESOURCE_LOCALE")) {
                value = "en_US";
            }
            if (input.equals("RESOURCE_PROPERTIES")) {
                value = "UpdateManagerResources";
            }
        }
        return value;
    }
    
    static {
        CertificateFilter.resourceBundle = null;
    }
}
