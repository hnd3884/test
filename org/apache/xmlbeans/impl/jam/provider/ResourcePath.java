package org.apache.xmlbeans.impl.jam.provider;

import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.io.File;

public class ResourcePath
{
    private File[] mFiles;
    
    public static ResourcePath forFiles(final File[] files) {
        return new ResourcePath(files);
    }
    
    private ResourcePath(final File[] files) {
        if (files == null) {
            throw new IllegalArgumentException("null files");
        }
        this.mFiles = files;
    }
    
    public URI[] toUriPath() {
        final URI[] out = new URI[this.mFiles.length];
        for (int i = 0; i < this.mFiles.length; ++i) {
            out[i] = this.mFiles[i].toURI();
        }
        return out;
    }
    
    public URL[] toUrlPath() throws MalformedURLException {
        final URL[] out = new URL[this.mFiles.length];
        for (int i = 0; i < this.mFiles.length; ++i) {
            out[i] = this.mFiles[i].toURL();
        }
        return out;
    }
    
    public InputStream findInPath(final String resource) {
        for (int i = 0; i < this.mFiles.length; ++i) {
            final File f = new File(this.mFiles[i], resource);
            try {
                if (f.exists()) {
                    return new FileInputStream(f);
                }
            }
            catch (final FileNotFoundException weird) {
                weird.printStackTrace();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        for (int i = 0; i < this.mFiles.length; ++i) {
            out.write(this.mFiles[i].getAbsolutePath());
            out.write(File.pathSeparatorChar);
        }
        return out.toString();
    }
}
