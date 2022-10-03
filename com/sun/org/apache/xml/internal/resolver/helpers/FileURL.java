package com.sun.org.apache.xml.internal.resolver.helpers;

import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;

public abstract class FileURL
{
    protected FileURL() {
    }
    
    public static URL makeURL(final String pathname) throws MalformedURLException {
        final File file = new File(pathname);
        return file.toURI().toURL();
    }
}
