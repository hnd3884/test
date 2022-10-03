package org.apache.xmlbeans.impl.schema;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import org.apache.xmlbeans.ResourceLoader;

public class PathResourceLoader implements ResourceLoader
{
    private ResourceLoader[] _path;
    
    public PathResourceLoader(final ResourceLoader[] loaderpath) throws IOException {
        System.arraycopy(loaderpath, 0, this._path = new ResourceLoader[loaderpath.length], 0, this._path.length);
    }
    
    public PathResourceLoader(final File[] filepath) {
        final List pathlist = new ArrayList();
        for (int i = 0; i < filepath.length; ++i) {
            try {
                final ResourceLoader path = new FileResourceLoader(filepath[i]);
                pathlist.add(path);
            }
            catch (final IOException e) {}
        }
        this._path = pathlist.toArray(new ResourceLoader[pathlist.size()]);
    }
    
    @Override
    public InputStream getResourceAsStream(final String resourceName) {
        for (int i = 0; i < this._path.length; ++i) {
            final InputStream result = this._path[i].getResourceAsStream(resourceName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    @Override
    public void close() {
        for (int i = 0; i < this._path.length; ++i) {
            try {
                this._path[i].close();
            }
            catch (final Exception ex) {}
        }
    }
}
