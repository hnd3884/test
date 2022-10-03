package com.adventnet.tools.update.installer;

import java.util.Hashtable;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.net.MalformedURLException;
import com.adventnet.tools.update.ClassLoaderUtil;
import java.io.IOException;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.Iterator;
import com.adventnet.tools.update.CommonUtil;
import java.io.FileInputStream;
import java.nio.file.Paths;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.zip.ZipFile;
import java.net.URL;
import java.nio.file.Path;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.Node;
import java.io.FileFilter;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.List;

public class CustomPatchStateTracker
{
    private String patchInstallationStateTracker_implementationClassName;
    private List<String> dependentClassesList;
    private Properties props;
    private URLClassLoader urcl;
    private FileFilter jarFileFilter;
    
    public CustomPatchStateTracker(final Node trackerEntryNode) {
        this.jarFileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
        this.process(trackerEntryNode);
    }
    
    private void process(final Node trackerEntryNode) {
        this.patchInstallationStateTracker_implementationClassName = trackerEntryNode.getAttributes().getNamedItem("name").getNodeValue();
        final NodeList dependentClassPathNodes = ((Element)trackerEntryNode).getElementsByTagName("dependentClassPath");
        if (dependentClassPathNodes != null) {
            this.dependentClassesList = new ArrayList<String>();
            for (int i = 0; i < dependentClassPathNodes.getLength(); ++i) {
                this.dependentClassesList.add(dependentClassPathNodes.item(i).getFirstChild().getNodeValue());
            }
        }
        final NodeList propertyNodes = ((Element)trackerEntryNode).getElementsByTagName("property");
        if (propertyNodes != null) {
            this.props = new Properties();
            for (int j = 0; j < propertyNodes.getLength(); ++j) {
                final Element el = (Element)propertyNodes.item(j);
                this.props.setProperty(el.getAttribute("name"), "value");
            }
        }
    }
    
    public String getTrackerImplementationClassName() {
        return this.patchInstallationStateTracker_implementationClassName;
    }
    
    public List<String> getDependentClassesList() {
        return this.dependentClassesList;
    }
    
    public Properties getProperties() {
        return this.props;
    }
    
    public PatchInstallationStateTracker getTrackerInstance(final Path patchFilePath) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        final List<URL> classPathUrls = new ArrayList<URL>();
        if (this.dependentClassesList != null) {
            try (final ZipFile patchZipFile = new ZipFile(patchFilePath.toFile())) {
                for (final String dependentClass : this.dependentClassesList) {
                    final Path pathToExtract = Paths.get(UpdateManagerUtil.getHomeDirectory(), "Patch", "tmp", dependentClass);
                    this.extract(patchZipFile, dependentClass, pathToExtract.toString());
                    classPathUrls.add(pathToExtract.toUri().toURL());
                }
            }
        }
        final Path classPathConf_Path = Paths.get(UpdateManagerUtil.getHomeDirectory(), "conf", "classpath.conf");
        if (classPathConf_Path.toFile().exists()) {
            final Properties pathProperties = new Properties();
            InputStream is = null;
            try {
                is = new FileInputStream(classPathConf_Path.toFile());
                pathProperties.load(is);
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
            for (final Object key : ((Hashtable<Object, V>)pathProperties).keySet()) {
                final String dirName = pathProperties.getProperty((String)key);
                final Path folderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), dirName);
                this.addAllJarFilesOfFolderToList(folderPath.toFile(), classPathUrls);
            }
        }
        else {
            Path folderPath2 = Paths.get(UpdateManagerUtil.getHomeDirectory(), "lib");
            this.addAllJarFilesOfFolderToList(folderPath2.toFile(), classPathUrls);
            folderPath2 = Paths.get(UpdateManagerUtil.getHomeDirectory(), "server", "default", "lib");
            this.addAllJarFilesOfFolderToList(folderPath2.toFile(), classPathUrls);
        }
        final Path binFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "bin");
        this.addAllJarFilesOfFolderToList(binFolderPath.toFile(), classPathUrls);
        final URL[] urlarr = new URL[classPathUrls.size()];
        for (int i = 0; i < classPathUrls.size(); ++i) {
            urlarr[i] = classPathUrls.get(i);
            final URLConnection urlConn = urlarr[i].openConnection();
            urlConn.setDefaultUseCaches(false);
        }
        this.urcl = new URLClassLoader(urlarr);
        Thread.currentThread().setContextClassLoader(this.urcl);
        String className = this.patchInstallationStateTracker_implementationClassName;
        className = CommonUtil.convertfilenameToOsFilename(className);
        className = className.substring(0, className.lastIndexOf("."));
        if (File.separator.equals("/")) {
            className = className.replace('/', '.');
        }
        else {
            className = className.replace('\\', '.');
        }
        return (PatchInstallationStateTracker)this.urcl.loadClass(className).newInstance();
    }
    
    public void cleanup() throws IOException {
        if (this.urcl != null) {
            this.urcl.close();
            ClassLoaderUtil.unloadNativeLibraries();
        }
        if (new File(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp").exists()) {
            CommonUtil.deleteFiles(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp");
        }
    }
    
    private void addAllJarFilesOfFolderToList(final File folder, final List<URL> urls) throws MalformedURLException {
        if (folder.exists()) {
            for (final File f : folder.listFiles(this.jarFileFilter)) {
                if (f.isDirectory()) {
                    urls.add(new File(f.getPath() + '/').toURI().toURL());
                }
                else {
                    urls.add(f.toURI().toURL());
                }
            }
        }
    }
    
    private void extract(final ZipFile patchZipFile, final String fileToExtract, final String pathToExtract) throws IOException {
        final ZipEntry zEntry = patchZipFile.getEntry(fileToExtract);
        if (zEntry == null) {
            throw new NullPointerException("File " + fileToExtract + " not found in ppm");
        }
        try (final InputStream is = patchZipFile.getInputStream(zEntry)) {
            this.extractFile(is, pathToExtract);
        }
    }
    
    private void extractFile(final InputStream is, final String filePath) throws IOException {
        CommonUtil.createAllSubDirectories(filePath);
        try (final FileOutputStream fos = new FileOutputStream(filePath)) {
            int length = 0;
            final byte[] dataRead = new byte[10240];
            while (length != -1) {
                length = is.read(dataRead);
                if (length == -1) {
                    break;
                }
                fos.write(dataRead, 0, length);
            }
        }
    }
}
