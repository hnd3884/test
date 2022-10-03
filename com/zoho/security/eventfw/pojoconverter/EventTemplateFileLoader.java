package com.zoho.security.eventfw.pojoconverter;

import org.apache.commons.collections.ExtendedProperties;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import org.apache.velocity.exception.ResourceNotFoundException;
import java.io.InputStream;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class EventTemplateFileLoader extends ResourceLoader
{
    public long getLastModified(final Resource arg0) {
        return 0L;
    }
    
    public InputStream getResourceStream(final String templatePath) throws ResourceNotFoundException {
        try {
            return this.findTemplate(templatePath);
        }
        catch (final Exception e) {
            final ResourceNotFoundException re = new ResourceNotFoundException(e.getMessage());
            throw re;
        }
    }
    
    private InputStream findTemplate(final String templatePath) throws FileNotFoundException {
        final File file = new File(templatePath);
        if (file.canRead()) {
            return new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        }
        return null;
    }
    
    public void init(final ExtendedProperties arg0) {
    }
    
    public boolean isSourceModified(final Resource arg0) {
        return false;
    }
}
