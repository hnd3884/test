package com.sun.nio.zipfs;

import java.nio.file.FileSystem;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URI;

public class JarFileSystemProvider extends ZipFileSystemProvider
{
    @Override
    public String getScheme() {
        return "jar";
    }
    
    @Override
    protected Path uriToPath(URI uri) {
        final String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase(this.getScheme())) {
            throw new IllegalArgumentException("URI scheme is not '" + this.getScheme() + "'");
        }
        try {
            final String string = uri.toString();
            final int index = string.indexOf("!/");
            uri = new URI(string.substring(4, (index == -1) ? string.length() : index));
            return Paths.get(new URI("file", uri.getHost(), uri.getPath(), null)).toAbsolutePath();
        }
        catch (final URISyntaxException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    @Override
    public Path getPath(final URI uri) {
        final FileSystem fileSystem = this.getFileSystem(uri);
        String s = uri.getFragment();
        if (s == null) {
            final String string = uri.toString();
            final int index = string.indexOf("!/");
            if (index != -1) {
                s = string.substring(index + 2);
            }
        }
        if (s != null) {
            return fileSystem.getPath(s, new String[0]);
        }
        throw new IllegalArgumentException("URI: " + uri + " does not contain path fragment ex. jar:///c:/foo.zip!/BAR");
    }
}
