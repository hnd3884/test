package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.model.ExternalMetadataReader;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.io.File;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ExternalMetadataFeature extends WebServiceFeature
{
    private static final String ID = "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
    private boolean enabled;
    private List<String> resourceNames;
    private List<File> files;
    private MetadataReader reader;
    
    private ExternalMetadataFeature() {
        this.enabled = true;
    }
    
    public void addResources(final String... resourceNames) {
        if (this.resourceNames == null) {
            this.resourceNames = new ArrayList<String>();
        }
        Collections.addAll(this.resourceNames, resourceNames);
    }
    
    public List<String> getResourceNames() {
        return this.resourceNames;
    }
    
    public void addFiles(final File... files) {
        if (this.files == null) {
            this.files = new ArrayList<File>();
        }
        Collections.addAll(this.files, files);
    }
    
    public List<File> getFiles() {
        return this.files;
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    private void setEnabled(final boolean x) {
        this.enabled = x;
    }
    
    @Override
    public String getID() {
        return "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
    }
    
    public MetadataReader getMetadataReader(final ClassLoader classLoader, final boolean disableXmlSecurity) {
        if (this.reader != null && this.enabled) {
            return this.reader;
        }
        return this.enabled ? new ExternalMetadataReader(this.files, this.resourceNames, classLoader, true, disableXmlSecurity) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ExternalMetadataFeature that = (ExternalMetadataFeature)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        Label_0077: {
            if (this.files != null) {
                if (this.files.equals(that.files)) {
                    break Label_0077;
                }
            }
            else if (that.files == null) {
                break Label_0077;
            }
            return false;
        }
        if (this.resourceNames != null) {
            if (this.resourceNames.equals(that.resourceNames)) {
                return true;
            }
        }
        else if (that.resourceNames == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + ((this.resourceNames != null) ? this.resourceNames.hashCode() : 0);
        result = 31 * result + ((this.files != null) ? this.files.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "[" + this.getID() + ", enabled=" + this.enabled + ", resourceNames=" + this.resourceNames + ", files=" + this.files + ']';
    }
    
    public static Builder builder() {
        return new Builder(new ExternalMetadataFeature());
    }
    
    public static final class Builder
    {
        private final ExternalMetadataFeature o;
        
        Builder(final ExternalMetadataFeature x) {
            this.o = x;
        }
        
        public ExternalMetadataFeature build() {
            return this.o;
        }
        
        public Builder addResources(final String... res) {
            this.o.addResources(res);
            return this;
        }
        
        public Builder addFiles(final File... files) {
            this.o.addFiles(files);
            return this;
        }
        
        public Builder setEnabled(final boolean enabled) {
            this.o.setEnabled(enabled);
            return this;
        }
        
        public Builder setReader(final MetadataReader r) {
            this.o.reader = r;
            return this;
        }
    }
}
