package sun.misc;

import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.security.CodeSource;
import java.net.URL;
import java.io.IOException;
import java.util.jar.JarFile;

public interface JavaUtilJarAccess
{
    boolean jarFileHasClassPathAttribute(final JarFile p0) throws IOException;
    
    CodeSource[] getCodeSources(final JarFile p0, final URL p1);
    
    CodeSource getCodeSource(final JarFile p0, final URL p1, final String p2);
    
    Enumeration<String> entryNames(final JarFile p0, final CodeSource[] p1);
    
    Enumeration<JarEntry> entries2(final JarFile p0);
    
    void setEagerValidation(final JarFile p0, final boolean p1);
    
    List<Object> getManifestDigests(final JarFile p0);
    
    Attributes getTrustedAttributes(final Manifest p0, final String p1);
    
    void ensureInitialization(final JarFile p0);
    
    boolean isInitializing();
}
