package com.adventnet.tools.update.installer;

import java.io.File;
import java.applet.Applet;
import javax.swing.filechooser.FileFilter;

public class PatchFilter extends FileFilter
{
    private String exten;
    static BuilderResourceBundle resourceBundle;
    private String localePropertiesFileName;
    private ParameterObject po;
    private Applet applet;
    
    public PatchFilter(final String text) {
        this.localePropertiesFileName = "UpdateManagerResources";
        this.po = null;
        this.applet = null;
        this.exten = text;
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
        PatchFilter.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        return PatchFilter.resourceBundle.getString("Patch Files(*.ppm)");
    }
    
    private boolean isExtensionPatch(final File file) {
        final String fileName = file.getName();
        final int index = fileName.lastIndexOf(".ppm");
        final String extension = fileName.substring(index + 1, fileName.length());
        return extension.equals(this.exten);
    }
    
    public String getParameter(final String input) {
        if (this.po != null && this.po.getParameter(input) != null) {
            return (String)this.po.getParameter(input);
        }
        String value = null;
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
        PatchFilter.resourceBundle = null;
    }
}
