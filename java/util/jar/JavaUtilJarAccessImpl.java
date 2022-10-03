package java.util.jar;

import java.util.List;
import java.util.Enumeration;
import java.security.CodeSource;
import java.net.URL;
import java.io.IOException;
import sun.misc.JavaUtilJarAccess;

class JavaUtilJarAccessImpl implements JavaUtilJarAccess
{
    @Override
    public boolean jarFileHasClassPathAttribute(final JarFile jarFile) throws IOException {
        return jarFile.hasClassPathAttribute();
    }
    
    @Override
    public CodeSource[] getCodeSources(final JarFile jarFile, final URL url) {
        return jarFile.getCodeSources(url);
    }
    
    @Override
    public CodeSource getCodeSource(final JarFile jarFile, final URL url, final String s) {
        return jarFile.getCodeSource(url, s);
    }
    
    @Override
    public Enumeration<String> entryNames(final JarFile jarFile, final CodeSource[] array) {
        return jarFile.entryNames(array);
    }
    
    @Override
    public Enumeration<JarEntry> entries2(final JarFile jarFile) {
        return jarFile.entries2();
    }
    
    @Override
    public void setEagerValidation(final JarFile jarFile, final boolean eagerValidation) {
        jarFile.setEagerValidation(eagerValidation);
    }
    
    @Override
    public List<Object> getManifestDigests(final JarFile jarFile) {
        return jarFile.getManifestDigests();
    }
    
    @Override
    public Attributes getTrustedAttributes(final Manifest manifest, final String s) {
        return manifest.getTrustedAttributes(s);
    }
    
    @Override
    public void ensureInitialization(final JarFile jarFile) {
        jarFile.ensureInitialization();
    }
    
    @Override
    public boolean isInitializing() {
        return JarFile.isInitializing();
    }
}
