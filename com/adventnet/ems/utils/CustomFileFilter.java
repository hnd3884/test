package com.adventnet.ems.utils;

import java.util.Enumeration;
import java.io.File;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

public class CustomFileFilter extends FileFilter
{
    private static String TYPE_UNKNOWN;
    private static String HIDDEN_FILE;
    private Hashtable filters;
    private String description;
    private String fullDescription;
    private boolean useExtensionsInDescription;
    
    public CustomFileFilter() {
        this.filters = null;
        this.description = null;
        this.fullDescription = null;
        this.useExtensionsInDescription = true;
        this.filters = new Hashtable();
    }
    
    public CustomFileFilter(final String s) {
        this(s, null);
    }
    
    public CustomFileFilter(final String s, final String description) {
        this();
        if (s != null) {
            this.addExtension(s);
        }
        if (description != null) {
            this.setDescription(description);
        }
    }
    
    public CustomFileFilter(final String[] array) {
        this(array, null);
    }
    
    public CustomFileFilter(final String[] array, final String description) {
        this();
        for (int i = 0; i < array.length; ++i) {
            this.addExtension(array[i]);
        }
        if (description != null) {
            this.setDescription(description);
        }
    }
    
    public boolean accept(final File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return true;
            }
            if (this.getExtension(file) != null && this.filters.get(this.getExtension(file)) != null) {
                return true;
            }
        }
        return false;
    }
    
    public String getExtension(final File file) {
        if (file != null) {
            final String name = file.getName();
            final int lastIndex = name.lastIndexOf(46);
            if (lastIndex > 0 && lastIndex < name.length() - 1) {
                return name.substring(lastIndex + 1).toLowerCase();
            }
        }
        return null;
    }
    
    public void addExtension(final String s) {
        if (this.filters == null) {
            this.filters = new Hashtable(5);
        }
        this.filters.put(s.toLowerCase(), this);
        this.fullDescription = null;
    }
    
    public String getDescription() {
        if (this.fullDescription == null) {
            if (this.description == null || this.isExtensionListInDescription()) {
                this.fullDescription = ((this.description == null) ? "(" : (this.description + " ("));
                final Enumeration keys = this.filters.keys();
                if (keys != null) {
                    this.fullDescription = this.fullDescription + "." + (String)keys.nextElement();
                    while (keys.hasMoreElements()) {
                        this.fullDescription = this.fullDescription + ", " + (String)keys.nextElement();
                    }
                }
                this.fullDescription += ")";
            }
            else {
                this.fullDescription = this.description;
            }
        }
        return this.fullDescription;
    }
    
    public void setDescription(final String description) {
        this.description = description;
        this.fullDescription = null;
    }
    
    public void setExtensionListInDescription(final boolean useExtensionsInDescription) {
        this.useExtensionsInDescription = useExtensionsInDescription;
        this.fullDescription = null;
    }
    
    public boolean isExtensionListInDescription() {
        return this.useExtensionsInDescription;
    }
    
    static {
        CustomFileFilter.TYPE_UNKNOWN = "Type Unknown";
        CustomFileFilter.HIDDEN_FILE = "Hidden File";
    }
}
