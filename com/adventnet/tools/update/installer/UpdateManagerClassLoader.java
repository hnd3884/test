package com.adventnet.tools.update.installer;

import java.util.Hashtable;
import java.net.MalformedURLException;
import java.io.File;
import com.adventnet.tools.update.ClassLoaderUtil;
import java.net.URLConnection;
import java.util.Iterator;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.util.Properties;
import java.nio.file.Paths;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.ArrayList;
import java.io.FileFilter;
import java.net.URL;
import java.util.logging.Logger;
import java.net.URLClassLoader;

public class UpdateManagerClassLoader extends URLClassLoader
{
    private static final Logger LOGGER;
    private static URL[] urlArray;
    private static final FileFilter JAR_FILE_FILTER;
    
    public static void init() {
        try {
            final List<URL> classPathUrls = new ArrayList<URL>();
            final Path classPathConf_Path = Paths.get(UpdateManagerUtil.getHomeDirectory(), "conf", "classpath.conf");
            final Properties pathProperties = new Properties();
            try (final InputStream is = new FileInputStream(classPathConf_Path.toFile())) {
                pathProperties.load(is);
            }
            for (final Object key : ((Hashtable<Object, V>)pathProperties).keySet()) {
                final String dirName = pathProperties.getProperty((String)key);
                final Path folderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), dirName);
                addAllJarFilesOfFolderToList(folderPath.toFile(), classPathUrls);
            }
            final Path binFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "bin");
            addAllJarFilesOfFolderToList(binFolderPath.toFile(), classPathUrls);
            UpdateManagerClassLoader.urlArray = new URL[classPathUrls.size()];
            for (int i = 0; i < classPathUrls.size(); ++i) {
                UpdateManagerClassLoader.urlArray[i] = classPathUrls.get(i);
                final URLConnection urlConn = UpdateManagerClassLoader.urlArray[i].openConnection();
                urlConn.setDefaultUseCaches(false);
            }
        }
        catch (final IOException e) {
            UpdateManagerClassLoader.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    UpdateManagerClassLoader() {
        super(UpdateManagerClassLoader.urlArray);
    }
    
    public static UpdateManagerClassLoader getInstance() {
        init();
        return new UpdateManagerClassLoader();
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        ClassLoaderUtil.unloadNativeLibraries();
    }
    
    private static void addAllJarFilesOfFolderToList(final File folder, final List<URL> urls) throws MalformedURLException {
        if (folder.exists()) {
            final File[] files = folder.listFiles(UpdateManagerClassLoader.JAR_FILE_FILTER);
            if (files != null) {
                for (final File f : files) {
                    if (f.isDirectory()) {
                        urls.add(new File(f.getPath() + '/').toURI().toURL());
                    }
                    else {
                        urls.add(f.toURI().toURL());
                    }
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(UpdateManagerClassLoader.class.getName());
        init();
        JAR_FILE_FILTER = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
    }
}
