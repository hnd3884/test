package com.adventnet.tools.update.installer;

import java.util.Hashtable;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.tools.update.ClassLoaderUtil;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.net.URLClassLoader;
import java.io.FileInputStream;
import java.util.Properties;
import java.nio.file.Paths;
import java.net.URL;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.Iterator;
import com.adventnet.tools.update.CommonUtil;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.File;
import java.io.FileFilter;
import java.util.zip.ZipFile;
import com.adventnet.tools.update.XmlData;

public class PreProcessor
{
    private XmlData xmlData;
    private ZipFile zipFile;
    private String errorMsg;
    private FileFilter jarFileFilter;
    
    public PreProcessor() {
        this.xmlData = null;
        this.zipFile = null;
        this.errorMsg = null;
        this.jarFileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
    }
    
    public PreProcessor(final XmlData xmlData, final String ppmFile) throws Exception {
        this.xmlData = null;
        this.zipFile = null;
        this.errorMsg = null;
        this.jarFileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
        this.cleanup();
        this.xmlData = xmlData;
        if (xmlData.getCustomPatchValidator() != null) {
            this.zipFile = new ZipFile(ppmFile);
            this.loadValidationClassFromPPM();
        }
    }
    
    public void cleanup() {
        if (new File(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp").exists()) {
            CommonUtil.deleteFiles(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp");
        }
    }
    
    private void loadValidationClassFromPPM() throws Exception {
        final CustomPatchValidator cpv = this.xmlData.getCustomPatchValidator();
        if (cpv.getDependentClassesList() != null) {
            for (final String dependentFile : cpv.getDependentClassesList()) {
                this.extract(dependentFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp" + File.separator + dependentFile);
            }
        }
    }
    
    private void extract(final String fileToExtract, final String pathToExtract) throws Exception {
        try {
            final ZipEntry zEntry = this.zipFile.getEntry(fileToExtract);
            if (zEntry == null) {
                this.errorMsg = "File " + fileToExtract + " not found in ppm";
                throw new NullPointerException(this.errorMsg);
            }
            final InputStream xmlUnzipper = this.zipFile.getInputStream(zEntry);
            this.extractFile(xmlUnzipper, pathToExtract);
        }
        catch (final Exception excep) {
            this.errorMsg = "Problem while extracting service pack validator";
            UpdateManagerUtil.setExitStatus(1);
            throw excep;
        }
    }
    
    private void extractFile(InputStream xmlUnzipper, final String filePath) throws Exception {
        CommonUtil.createAllSubDirectories(filePath);
        FileOutputStream xmlFile = new FileOutputStream(filePath);
        int length = 0;
        final byte[] dataRead = new byte[10240];
        while (length != -1) {
            length = xmlUnzipper.read(dataRead);
            if (length == -1) {
                break;
            }
            xmlFile.write(dataRead, 0, length);
        }
        xmlUnzipper.close();
        xmlFile.flush();
        xmlFile.close();
        xmlUnzipper = null;
        xmlFile = null;
    }
    
    public boolean invokeCustomSPValidation(final String baseDir, String className, final ArrayList<String> dependentList, final XmlData xmlData) {
        URLClassLoader urlc = null;
        try {
            className = CommonUtil.convertfilenameToOsFilename(xmlData.getCustomPatchValidator().getValidatorClass());
            className = className.substring(0, className.lastIndexOf("."));
            if (File.separator.equals("/")) {
                className = className.replace('/', '.');
            }
            else {
                className = className.replace('\\', '.');
            }
            final List<URL> classPathUrls = new ArrayList<URL>();
            for (String dependent : dependentList) {
                dependent = CommonUtil.convertfilenameToOsFilename(dependent);
                classPathUrls.add(new File(baseDir + File.separator + dependent).toURI().toURL());
            }
            final Path classPathConf_Path = Paths.get(UpdateManagerUtil.getHomeDirectory(), "conf", "classpath.conf");
            if (classPathConf_Path.toFile().exists()) {
                final Properties pathProperties = new Properties();
                try (final InputStream is = new FileInputStream(classPathConf_Path.toFile())) {
                    pathProperties.load(is);
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
            urlc = new URLClassLoader(urlarr);
            Thread.currentThread().setContextClassLoader(urlc);
            final Class preClass = urlc.loadClass(className);
            final Object preObj = preClass.newInstance();
            try {
                final Method apu = preClass.getMethod("allowPatchUpgrade", xmlData.getClass());
                final Boolean bol = (Boolean)apu.invoke(preObj, xmlData);
                return bol;
            }
            catch (final Throwable thr) {
                Throwable original_cause;
                for (original_cause = thr; original_cause.getCause() != null; original_cause = original_cause.getCause()) {}
                if (UpdateManager.isGUI()) {
                    JOptionPane.showMessageDialog(null, original_cause.getMessage(), "Problem in pre-validation.", 0);
                }
                else {
                    ConsoleOut.print("\n" + original_cause.getMessage() + "\n");
                }
                return false;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (urlc != null) {
                try {
                    urlc.close();
                    ClassLoaderUtil.unloadNativeLibraries();
                    urlc = null;
                }
                catch (final IOException ex) {
                    Logger.getLogger(PreProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        finally {
            if (urlc != null) {
                try {
                    urlc.close();
                    ClassLoaderUtil.unloadNativeLibraries();
                    urlc = null;
                }
                catch (final IOException ex2) {
                    Logger.getLogger(PreProcessor.class.getName()).log(Level.SEVERE, null, ex2);
                }
            }
        }
        return false;
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
    
    public String getErrorMessage() {
        return this.errorMsg;
    }
    
    public void closeZipFile() {
        try {
            this.zipFile.close();
        }
        catch (final IOException ioe) {
            Logger.getLogger(PreProcessor.class.getName()).log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }
}
