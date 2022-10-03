package sun.misc;

import java.net.MalformedURLException;
import sun.net.www.ParseUtil;
import java.net.URL;
import java.io.FilenameFilter;
import sun.security.action.GetPropertyAction;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.jar.Manifest;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.Vector;

public class ExtensionDependency
{
    private static Vector<ExtensionInstallationProvider> providers;
    static final boolean DEBUG = false;
    
    public static synchronized void addExtensionInstallationProvider(final ExtensionInstallationProvider extensionInstallationProvider) {
        if (ExtensionDependency.providers == null) {
            ExtensionDependency.providers = new Vector<ExtensionInstallationProvider>();
        }
        ExtensionDependency.providers.add(extensionInstallationProvider);
    }
    
    public static synchronized void removeExtensionInstallationProvider(final ExtensionInstallationProvider extensionInstallationProvider) {
        ExtensionDependency.providers.remove(extensionInstallationProvider);
    }
    
    public static boolean checkExtensionsDependencies(final JarFile jarFile) {
        if (ExtensionDependency.providers == null) {
            return true;
        }
        try {
            return new ExtensionDependency().checkExtensions(jarFile);
        }
        catch (final ExtensionInstallationException ex) {
            debug(ex.getMessage());
            return false;
        }
    }
    
    protected boolean checkExtensions(final JarFile jarFile) throws ExtensionInstallationException {
        Manifest manifest;
        try {
            manifest = jarFile.getManifest();
        }
        catch (final IOException ex) {
            return false;
        }
        if (manifest == null) {
            return true;
        }
        boolean b = true;
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
            final String value = mainAttributes.getValue(Attributes.Name.EXTENSION_LIST);
            if (value != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(value);
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    debug("The file " + jarFile.getName() + " appears to depend on " + nextToken);
                    final String string = nextToken + "-" + Attributes.Name.EXTENSION_NAME.toString();
                    if (mainAttributes.getValue(string) == null) {
                        debug("The jar file " + jarFile.getName() + " appers to depend on " + nextToken + " but does not define the " + string + " attribute in its manifest ");
                    }
                    else {
                        if (this.checkExtension(nextToken, mainAttributes)) {
                            continue;
                        }
                        debug("Failed installing " + nextToken);
                        b = false;
                    }
                }
            }
            else {
                debug("No dependencies for " + jarFile.getName());
            }
        }
        return b;
    }
    
    protected synchronized boolean checkExtension(final String s, final Attributes attributes) throws ExtensionInstallationException {
        debug("Checking extension " + s);
        if (this.checkExtensionAgainstInstalled(s, attributes)) {
            return true;
        }
        debug("Extension not currently installed ");
        return this.installExtension(new ExtensionInfo(s, attributes), null);
    }
    
    boolean checkExtensionAgainstInstalled(final String s, final Attributes attributes) throws ExtensionInstallationException {
        final File checkExtensionExists = this.checkExtensionExists(s);
        if (checkExtensionExists != null) {
            try {
                if (this.checkExtensionAgainst(s, attributes, checkExtensionExists)) {
                    return true;
                }
            }
            catch (final FileNotFoundException ex) {
                this.debugException(ex);
            }
            catch (final IOException ex2) {
                this.debugException(ex2);
            }
            return false;
        }
        File[] installedExtensions;
        try {
            installedExtensions = this.getInstalledExtensions();
        }
        catch (final IOException ex3) {
            this.debugException(ex3);
            return false;
        }
        for (int i = 0; i < installedExtensions.length; ++i) {
            try {
                if (this.checkExtensionAgainst(s, attributes, installedExtensions[i])) {
                    return true;
                }
            }
            catch (final FileNotFoundException ex4) {
                this.debugException(ex4);
            }
            catch (final IOException ex5) {
                this.debugException(ex5);
            }
        }
        return false;
    }
    
    protected boolean checkExtensionAgainst(final String s, final Attributes attributes, final File file) throws IOException, FileNotFoundException, ExtensionInstallationException {
        debug("Checking extension " + s + " against " + file.getName());
        Manifest manifest;
        try {
            manifest = AccessController.doPrivileged((PrivilegedExceptionAction<Manifest>)new PrivilegedExceptionAction<Manifest>() {
                @Override
                public Manifest run() throws IOException, FileNotFoundException {
                    if (!file.exists()) {
                        throw new FileNotFoundException(file.getName());
                    }
                    return new JarFile(file).getManifest();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            if (ex.getException() instanceof FileNotFoundException) {
                throw (FileNotFoundException)ex.getException();
            }
            throw (IOException)ex.getException();
        }
        final ExtensionInfo extensionInfo = new ExtensionInfo(s, attributes);
        debug("Requested Extension : " + extensionInfo);
        if (manifest != null) {
            final Attributes mainAttributes = manifest.getMainAttributes();
            if (mainAttributes != null) {
                final ExtensionInfo extensionInfo2 = new ExtensionInfo(null, mainAttributes);
                debug("Extension Installed " + extensionInfo2);
                switch (extensionInfo2.isCompatibleWith(extensionInfo)) {
                    case 0: {
                        debug("Extensions are compatible");
                        return true;
                    }
                    case 4: {
                        debug("Extensions are incompatible");
                        return false;
                    }
                    default: {
                        debug("Extensions require an upgrade or vendor switch");
                        return this.installExtension(extensionInfo, extensionInfo2);
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean installExtension(final ExtensionInfo extensionInfo, final ExtensionInfo extensionInfo2) throws ExtensionInstallationException {
        final Vector vector;
        synchronized (ExtensionDependency.providers) {
            vector = (Vector)ExtensionDependency.providers.clone();
        }
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            final ExtensionInstallationProvider extensionInstallationProvider = (ExtensionInstallationProvider)elements.nextElement();
            if (extensionInstallationProvider != null && extensionInstallationProvider.installExtension(extensionInfo, extensionInfo2)) {
                debug(extensionInfo.name + " installation successful");
                this.addNewExtensionsToClassLoader((Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent());
                return true;
            }
        }
        debug(extensionInfo.name + " installation failed");
        return false;
    }
    
    private File checkExtensionExists(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<File>)new PrivilegedAction<File>() {
            final /* synthetic */ String[] val$fileExt = { ".jar", ".zip" };
            
            @Override
            public File run() {
                try {
                    final File[] access$000 = getExtDirs();
                    for (int i = 0; i < access$000.length; ++i) {
                        for (int j = 0; j < this.val$fileExt.length; ++j) {
                            File file;
                            if (s.toLowerCase().endsWith(this.val$fileExt[j])) {
                                file = new File(access$000[i], s);
                            }
                            else {
                                file = new File(access$000[i], s + this.val$fileExt[j]);
                            }
                            debug("checkExtensionExists:fileName " + file.getName());
                            if (file.exists()) {
                                return file;
                            }
                        }
                    }
                    return null;
                }
                catch (final Exception ex) {
                    ExtensionDependency.this.debugException(ex);
                    return null;
                }
            }
        });
    }
    
    private static File[] getExtDirs() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.ext.dirs"));
        File[] array;
        if (s != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, File.pathSeparator);
            final int countTokens = stringTokenizer.countTokens();
            debug("getExtDirs count " + countTokens);
            array = new File[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array[i] = new File(stringTokenizer.nextToken());
                debug("getExtDirs dirs[" + i + "] " + array[i]);
            }
        }
        else {
            array = new File[0];
            debug("getExtDirs dirs " + array);
        }
        debug("getExtDirs dirs.length " + array.length);
        return array;
    }
    
    private static File[] getExtFiles(final File[] array) throws IOException {
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            final String[] list = array[i].list(new JarFilter());
            if (list != null) {
                debug("getExtFiles files.length " + list.length);
                for (int j = 0; j < list.length; ++j) {
                    final File file = new File(array[i], list[j]);
                    vector.add(file);
                    debug("getExtFiles f[" + j + "] " + file);
                }
            }
        }
        final File[] array2 = new File[vector.size()];
        vector.copyInto(array2);
        debug("getExtFiles ua.length " + array2.length);
        return array2;
    }
    
    private File[] getInstalledExtensions() throws IOException {
        return AccessController.doPrivileged((PrivilegedAction<File[]>)new PrivilegedAction<File[]>() {
            @Override
            public File[] run() {
                try {
                    return getExtFiles(getExtDirs());
                }
                catch (final IOException ex) {
                    debug("Cannot get list of installed extensions");
                    ExtensionDependency.this.debugException(ex);
                    return new File[0];
                }
            }
        });
    }
    
    private Boolean addNewExtensionsToClassLoader(final Launcher.ExtClassLoader extClassLoader) {
        try {
            final File[] installedExtensions = this.getInstalledExtensions();
            for (int i = 0; i < installedExtensions.length; ++i) {
                final URL url = AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction<URL>() {
                    final /* synthetic */ File val$instFile = installedExtensions[i];
                    
                    @Override
                    public URL run() {
                        try {
                            return ParseUtil.fileToEncodedURL(this.val$instFile);
                        }
                        catch (final MalformedURLException ex) {
                            ExtensionDependency.this.debugException(ex);
                            return null;
                        }
                    }
                });
                if (url != null) {
                    final URL[] urLs = extClassLoader.getURLs();
                    boolean b = false;
                    for (int j = 0; j < urLs.length; ++j) {
                        debug("URL[" + j + "] is " + urLs[j] + " looking for " + url);
                        if (urLs[j].toString().compareToIgnoreCase(url.toString()) == 0) {
                            b = true;
                            debug("Found !");
                        }
                    }
                    if (!b) {
                        debug("Not Found ! adding to the classloader " + url);
                        extClassLoader.addExtURL(url);
                    }
                }
            }
        }
        catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }
        catch (final IOException ex2) {
            ex2.printStackTrace();
        }
        return Boolean.TRUE;
    }
    
    private static void debug(final String s) {
    }
    
    private void debugException(final Throwable t) {
    }
}
