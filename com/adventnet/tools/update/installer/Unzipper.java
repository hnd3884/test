package com.adventnet.tools.update.installer;

import java.util.Enumeration;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.adventnet.tools.update.ZipFileGroup;
import java.nio.file.Files;
import java.util.TreeSet;
import java.io.FileNotFoundException;
import com.zoho.tools.util.FileUtil;
import java.io.IOException;
import com.adventnet.tools.update.FileGroup;
import java.io.FileOutputStream;
import java.awt.Toolkit;
import javax.swing.JDialog;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Iterator;
import java.nio.file.Path;
import java.io.InputStream;
import com.adventnet.tools.update.ClassLoaderUtil;
import java.net.URLClassLoader;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import com.adventnet.tools.update.CommonUtil;
import java.util.logging.Level;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.logging.Logger;

public class Unzipper
{
    private static Logger out;
    private ZipFile zipFile;
    private ZipEntry zipFileEntry;
    private String dirToUnzip;
    private String contextDir;
    private Common installDetail;
    private int intForPost;
    private int intForPre;
    private UpdateJar jarUpdater;
    private Vector fileGrpVector;
    private ArrayList zipfg;
    private Object[] preInstallArray;
    private Object[] postInstallArray;
    private Hashtable<String, Set<String>> jarEntriesToBeDeleted;
    private Set<String> filesToBeDeleted;
    private String currentVersion;
    private String confProductName;
    private String confProductVersion;
    private boolean GUI;
    private LoggingUtil logg;
    private Backup backupFiles;
    private String corruptMessage;
    private String unexpectedError;
    private List<String> alreadyCompletedClasses;
    private FileFilter jarFileFilter;
    
    public Unzipper(final Common common, final String contextDirectory, final boolean GUI, final LoggingUtil logg) {
        this.zipFile = null;
        this.zipFileEntry = null;
        this.dirToUnzip = null;
        this.contextDir = null;
        this.installDetail = null;
        this.intForPost = 0;
        this.intForPre = 0;
        this.jarUpdater = null;
        this.fileGrpVector = null;
        this.zipfg = null;
        this.preInstallArray = null;
        this.postInstallArray = null;
        this.jarEntriesToBeDeleted = null;
        this.filesToBeDeleted = null;
        this.currentVersion = null;
        this.confProductName = null;
        this.confProductVersion = null;
        this.GUI = false;
        this.logg = null;
        this.backupFiles = null;
        this.corruptMessage = "The file may be corrupted.Download again.";
        this.unexpectedError = "Unexpected Error. Please click View Log>> for more details";
        this.alreadyCompletedClasses = new ArrayList<String>();
        this.jarFileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
        try {
            this.installDetail = common;
            final File tempFile = new File(this.installDetail.getPatchFileNamePath());
            this.zipFile = new ZipFile(tempFile);
            this.jarUpdater = new UpdateJar();
            if (this.installDetail.getInstallationDirectory().equals("")) {
                this.dirToUnzip = System.getProperty("user.dir");
            }
            else {
                this.dirToUnzip = this.installDetail.getInstallationDirectory();
            }
            this.confProductName = this.installDetail.getConfProductName();
            this.confProductVersion = this.installDetail.getConfProductVersion();
            this.contextDir = contextDirectory;
            this.GUI = GUI;
            this.logg = logg;
        }
        catch (final Exception e) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while creating unzipper instance."), e);
        }
    }
    
    public boolean invokePreInstallationClasses() {
        final String header = "Pre Invocation in progress";
        Unzipper.out.info(CommonUtil.getString(header));
        final int size = this.preInstallArray.length;
        if (size == 0) {
            Unzipper.out.info(CommonUtil.getString("No class files are present for pre invocation."));
            return true;
        }
        int prg = 0;
        this.updateMainLabelProgress(header, "");
        for (int i = 0; i < size; i += 4) {
            this.showStatus(header, "", prg);
            this.intForPre += 4;
            try {
                String zipFileName = "";
                String fileName = (String)this.preInstallArray[i];
                UpdateManager.getUpdateState().setCurrentPrePostClassInProgress(fileName, System.currentTimeMillis());
                final ArrayList depenList = (ArrayList)this.preInstallArray[i + 1];
                final ArrayList depenClassPath = (ArrayList)this.preInstallArray[i + 2];
                final Properties prop = (Properties)this.preInstallArray[i + 3];
                zipFileName = fileName;
                if (!zipFileName.equals("inf.xml") && !this.alreadyCompletedClasses.contains(fileName)) {
                    Unzipper.out.info(CommonUtil.getString("Executing class:") + fileName);
                    final String preInstall = "PreInstall";
                    String dir = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator;
                    if (!this.contextDir.equals("NoContext")) {
                        dir = dir + this.contextDir + File.separator;
                        if (fileName.startsWith(this.contextDir)) {
                            fileName = fileName.substring(this.contextDir.length() + 1);
                        }
                    }
                    dir = dir + preInstall + File.separator;
                    new File(dir).mkdir();
                    final boolean preSuccess = this.contextPreInstallation(preInstall, fileName, dir, prop, depenList, depenClassPath, i + 4);
                    if (!preSuccess) {
                        UpdateManager.getUpdateState().setErrorCode(100);
                        Unzipper.out.info(CommonUtil.getString("ERROR CODE") + "     : " + 100);
                        Unzipper.out.info(CommonUtil.getString("ERROR IN") + "       : " + CommonUtil.getString("Pre install"));
                        Unzipper.out.info(CommonUtil.getString("ERROR IN CLASS") + " : " + fileName);
                        return false;
                    }
                    UpdateManager.getUpdateState().setCurrentClassStats(preSuccess, System.currentTimeMillis());
                }
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error in pre invocation"), e);
                return false;
            }
            prg = 20 * this.intForPre / size;
            this.showStatus(header, "", prg);
        }
        return true;
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
    
    public boolean contextPreInstallation(final String prePostDir, String zipFileName, final String dir, final Properties prop, final ArrayList dependentClassesList, final ArrayList classPathList, final int classIndex) {
        final String backupDir = dir;
        String className = null;
        String depenFile = null;
        try {
            className = CommonUtil.convertfilenameToOsFilename(zipFileName);
            CommonUtil.createAllSubDirectories(backupDir + className);
            this.zipFileEntry = this.zipFile.getEntry(zipFileName);
            InputStream zipFileInputStream = null;
            if (this.zipFileEntry != null) {
                zipFileInputStream = this.zipFile.getInputStream(this.zipFileEntry);
                this.writeFile(zipFileInputStream, backupDir + className);
            }
            if (dependentClassesList != null) {
                for (int size = dependentClassesList.size(), i = 0; i < size; ++i) {
                    depenFile = dependentClassesList.get(i);
                    className = CommonUtil.convertfilenameToOsFilename(depenFile);
                    CommonUtil.createAllSubDirectories(backupDir + className);
                    this.zipFileEntry = this.zipFile.getEntry(depenFile);
                    zipFileInputStream = this.zipFile.getInputStream(this.zipFileEntry);
                    this.writeFile(zipFileInputStream, backupDir + className);
                }
            }
            zipFileInputStream.close();
        }
        catch (final NullPointerException npe) {
            final String depenError = CommonUtil.getString("Dependent File") + " " + depenFile + " " + CommonUtil.getString("Not found in pre invocation") + " " + zipFileName;
            this.displayError(depenError);
            this.logg.fail(depenError, npe);
            Unzipper.out.log(Level.SEVERE, "ERR:" + depenError + " ", npe);
            return false;
        }
        catch (final Exception expp) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error in pre invocation:") + zipFileName, expp);
            return false;
        }
        if (zipFileName.endsWith(".class")) {
            try {
                zipFileName = CommonUtil.convertfilenameToOsFilename(zipFileName);
                final int index = zipFileName.lastIndexOf(".class");
                final int lastIndex = zipFileName.lastIndexOf(File.separator);
                String backupDirName = zipFileName.substring(lastIndex + 1);
                zipFileName = zipFileName.substring(0, index);
                backupDirName = CommonUtil.convertfilenameToOsFilename(backupDirName);
                if (File.separator.equals("/")) {
                    zipFileName = zipFileName.replace('/', '.');
                }
                else {
                    zipFileName = zipFileName.replace('\\', '.');
                }
                final List<URL> classPathUrls = new ArrayList<URL>();
                for (int s = dependentClassesList.size(), j = 0; j < s; ++j) {
                    String cp = dependentClassesList.get(j);
                    cp = CommonUtil.convertfilenameToOsFilename(cp);
                    if (new File(backupDir + File.separator + cp).isDirectory()) {
                        this.addAllJarFilesOfFolderToList(new File(backupDir + File.separator + cp), classPathUrls);
                    }
                    classPathUrls.add(new File(backupDir + File.separator + cp).toURI().toURL());
                }
                boolean isJarsLoadedFromClasspathEntries = false;
                final Path classPathConf_Path = Paths.get(this.dirToUnzip, "conf", "classpath.conf");
                if (classPathConf_Path.toFile().exists()) {
                    final Properties pathProperties = new Properties();
                    try (final InputStream is = new FileInputStream(classPathConf_Path.toFile())) {
                        pathProperties.load(is);
                    }
                    for (final Object key : ((Hashtable<Object, V>)pathProperties).keySet()) {
                        final String dirName = pathProperties.getProperty((String)key);
                        final Path folderPath = Paths.get(this.dirToUnzip, dirName);
                        this.addAllJarFilesOfFolderToList(folderPath.toFile(), classPathUrls);
                    }
                }
                else if (classPathList != null && !classPathList.isEmpty()) {
                    isJarsLoadedFromClasspathEntries = true;
                    for (final Object classPathEntry : classPathList) {
                        classPathUrls.add(Paths.get(this.dirToUnzip, (String)classPathEntry).toUri().toURL());
                    }
                }
                else {
                    Path folderPath2 = Paths.get(UpdateManagerUtil.getHomeDirectory(), "lib");
                    this.addAllJarFilesOfFolderToList(folderPath2.toFile(), classPathUrls);
                    folderPath2 = Paths.get(UpdateManagerUtil.getHomeDirectory(), "server", "default", "lib");
                    this.addAllJarFilesOfFolderToList(folderPath2.toFile(), classPathUrls);
                }
                if (!isJarsLoadedFromClasspathEntries) {
                    final Path binFolderPath = Paths.get(this.dirToUnzip, "bin");
                    this.addAllJarFilesOfFolderToList(binFolderPath.toFile(), classPathUrls);
                }
                final File f = new File(backupDir);
                classPathUrls.add(f.toURI().toURL());
                final URL[] urlarr = new URL[classPathUrls.size()];
                for (int k = 0; k < classPathUrls.size(); ++k) {
                    urlarr[k] = classPathUrls.get(k);
                    final URLConnection urlConn = urlarr[k].openConnection();
                    urlConn.setDefaultUseCaches(false);
                }
                URLClassLoader urlclsldr = null;
                Object[] filesToModify = null;
                int type;
                String errorMessage;
                try {
                    urlclsldr = new URLClassLoader(urlarr);
                    Thread.currentThread().setContextClassLoader(urlclsldr);
                    final Class preClass = urlclsldr.loadClass(zipFileName);
                    final Object preObj = preClass.newInstance();
                    final Method isfbp = preClass.getMethod("isFilesToBeBackedUp", (Class[])null);
                    final Boolean bol = (Boolean)isfbp.invoke(preObj, (Object[])null);
                    final boolean isFilesToBeBackedUp = bol;
                    if (isFilesToBeBackedUp) {
                        final Method gftm = preClass.getMethod("getFilesToModify", (Class[])null);
                        filesToModify = (Object[])gftm.invoke(preObj, (Object[])null);
                        this.moveBackupFiles(filesToModify, backupDir + backupDirName);
                    }
                    Properties p = null;
                    if (prop == null) {
                        p = new Properties();
                    }
                    else {
                        p = prop;
                    }
                    ((Hashtable<String, String>)p).put("product", this.confProductName);
                    ((Hashtable<String, String>)p).put("version", this.confProductVersion);
                    ((Hashtable<String, String>)p).put("home", this.dirToUnzip);
                    ((Hashtable<String, String>)p).put("mode", String.valueOf(this.GUI));
                    ((Hashtable<String, String>)p).put("patchversion", this.installDetail.getPatchVersion());
                    ((Hashtable<String, String>)p).put("context", this.contextDir);
                    JDialog jd = null;
                    if ((jd = UpdateManagerUtil.getParent()) != null) {
                        ((Hashtable<String, JDialog>)p).put("parentDialog", jd);
                    }
                    final Class[] constArgs = { p.getClass() };
                    final Method init = preClass.getMethod("install", (Class[])constArgs);
                    final Object[] constArgsVal = { p };
                    final Integer returnValue = (Integer)init.invoke(preObj, constArgsVal);
                    type = returnValue;
                    final Method gem = preClass.getMethod("getErrorMsg", (Class[])null);
                    errorMessage = (String)gem.invoke(preObj, (Object[])null);
                }
                finally {
                    if (urlclsldr != null) {
                        urlclsldr.close();
                        ClassLoaderUtil.unloadNativeLibraries();
                    }
                }
                if (type == 1) {
                    return true;
                }
                if (type == 2) {
                    return this.failureRevertAll(this.preInstallArray, prePostDir, errorMessage, classIndex, filesToModify);
                }
                if (type == 3) {
                    return this.failureRevertContinue(prePostDir, filesToModify, backupDirName);
                }
                if (type == 4) {
                    return this.failureRevertAllContinue(this.preInstallArray, prePostDir, classIndex, filesToModify);
                }
                if (type == 9) {
                    return this.failureRevertAbsolute(this.preInstallArray, prePostDir, errorMessage, classIndex);
                }
                errorMessage = "The pre invocation class returns a constant which is not supported";
                this.displayError(errorMessage);
                return false;
            }
            catch (final ClassNotFoundException ce) {
                Unzipper.out.log(Level.SEVERE, "ERR" + this.unexpectedError, ce);
                this.logg.fail(CommonUtil.getString(this.unexpectedError), ce);
                this.displayError(this.unexpectedError);
                return false;
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR" + this.unexpectedError, e);
                this.logg.fail(CommonUtil.getString(this.unexpectedError), e);
                this.displayError(this.unexpectedError);
                return false;
            }
        }
        return true;
    }
    
    private boolean failureRevertAll(final Object[] array, final String dir, final String error, final int index, final Object[] filesToModify) {
        System.setProperty("FAILURE_REVERT_ABSOLUTE", "false");
        final boolean bool = this.revert(array, dir, index, filesToModify);
        Toolkit.getDefaultToolkit().beep();
        this.displayError(error);
        return false;
    }
    
    private boolean failureRevertContinue(final String dir, final Object[] filesToModify, final String backupDirName) {
        System.setProperty("FAILURE_REVERT_ABSOLUTE", "false");
        return this.revertPreInstallClasses(dir, filesToModify, backupDirName);
    }
    
    private boolean failureRevertAllContinue(final Object[] array, final String dir, final int index, final Object[] filesToModify) {
        System.setProperty("FAILURE_REVERT_ABSOLUTE", "false");
        return this.revert(array, dir, index, filesToModify);
    }
    
    private boolean failureRevertComplete(final Object[] array, final String dir, final String error, final int index, final Object[] filesToModify) {
        System.setProperty("FAILURE_REVERT_ABSOLUTE", "false");
        final boolean bool = this.revert(array, dir, index, filesToModify);
        if (this.GUI) {
            UpdateManagerUtil.setErrorMessage(error);
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startFailureCompletion();
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_IN_PROGRESS);
            UpdateManagerUtil.startInstallFailureAnimation();
        }
        else {
            ConsoleOut.println("\n" + error);
        }
        final RevertPatch revertPatch = new RevertPatch(this.installDetail, this.GUI, true);
        final String rversion = this.currentVersion;
        String name = null;
        final LoggingUtil logg = new LoggingUtil();
        final String dirName = this.dirToUnzip + File.separator + "Patch" + File.separator + "logs";
        final File t = new File(dirName);
        if (!t.isDirectory()) {
            t.mkdir();
        }
        if (this.contextDir.equals("NoContext")) {
            name = rversion + "Rlog.txt";
        }
        else {
            name = rversion + this.contextDir + "Rlog.txt";
        }
        logg.init(dirName + File.separator + name);
        revertPatch.readInfoFile(rversion, this.contextDir, logg);
        UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
        revertPatch.startReverting();
        UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
        revertPatch.compressEEARAfterReverting();
        revertPatch.deleteBackupDir();
        final String specsFile = this.dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File specsInf = new File(specsFile);
        if (specsInf.isFile()) {
            final VersionProfile vprofile = VersionProfile.getInstance();
            vprofile.readDocument(specsFile, false, false);
            final String[] contextList = vprofile.getTheContext(rversion);
            if (contextList != null) {
                int j;
                for (int conLength = j = contextList.length; j > 0; --j) {
                    final String rcontext = contextList[j - 1];
                    try {
                        String logName = null;
                        if (rcontext.equals("NoContext")) {
                            logName = rversion + "Rlog.txt";
                        }
                        else {
                            logName = rversion + rcontext + "Rlog.txt";
                        }
                        logg.init(dirName + File.separator + logName);
                    }
                    catch (final Exception e) {
                        Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error while reverting"), e);
                    }
                    revertPatch.readInfoFile(rversion, rcontext, logg);
                    UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
                    revertPatch.startReverting();
                    UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
                    revertPatch.compressEEARAfterReverting();
                    revertPatch.deleteBackupDir();
                }
            }
            String patchType = vprofile.getTheAdditionalDetail(rversion, "Type");
            if (patchType == null) {
                patchType = "SP";
            }
            vprofile.removeVersion(rversion, specsFile, patchType);
        }
        String patchFileName = this.installDetail.getPatchFileNamePath();
        patchFileName = patchFileName.substring(patchFileName.lastIndexOf(File.separator) + 1);
        final File patchFile = new File(this.dirToUnzip + File.separator + "Patch" + File.separator + patchFileName);
        if (patchFile.exists()) {
            patchFile.delete();
        }
        if (this.GUI) {
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startInstallFailureAnimationCompletion();
            UpdateManagerUtil.updateTheInstallUI();
        }
        else {
            UpdateManagerUtil.updateTheFailureInCMD();
        }
        return false;
    }
    
    private boolean failureRevertAbsolute(final Object[] array, final String dir, final String error, final int index) {
        System.setProperty("FAILURE_REVERT_ABSOLUTE", "true");
        if (this.GUI) {
            UpdateManagerUtil.setErrorMessage(error);
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startFailureCompletion();
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_IN_PROGRESS);
            UpdateManagerUtil.startInstallFailureAnimation();
        }
        else {
            ConsoleOut.println("\n" + error);
        }
        final RevertPatch revertPatch = new RevertPatch(this.installDetail, this.GUI, true);
        final String rversion = this.currentVersion;
        String name = null;
        final LoggingUtil logg = new LoggingUtil();
        final String dirName = this.dirToUnzip + File.separator + "Patch" + File.separator + "logs";
        final File t = new File(dirName);
        if (!t.isDirectory()) {
            t.mkdir();
        }
        if (this.contextDir.equals("NoContext")) {
            name = rversion + "Rlog.txt";
        }
        else {
            name = rversion + this.contextDir + "Rlog.txt";
        }
        logg.init(dirName + File.separator + name);
        revertPatch.readInfoFile(rversion, this.contextDir, logg);
        UpdateManager.getUpdateState().setCurrentState(14, System.currentTimeMillis());
        boolean isPostInvocationSuccess = revertPatch.revertPostInvocationClasses(this.intForPost);
        if (!isPostInvocationSuccess) {
            return false;
        }
        if (!dir.equals("PreInstall")) {
            UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
            revertPatch.startReverting();
        }
        UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
        revertPatch.compressEEARAfterReverting();
        UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
        boolean isPreInvocationSuccess = revertPatch.revertPreInvocationClasses(this.intForPre);
        if (!isPreInvocationSuccess) {
            return false;
        }
        revertPatch.deleteBackupDir();
        final String specsFile = this.dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File specsInf = new File(specsFile);
        if (specsInf.isFile()) {
            final VersionProfile vprofile = VersionProfile.getInstance();
            vprofile.readDocument(specsFile, false, false);
            final String[] contextList = vprofile.getTheContext(rversion);
            if (contextList != null) {
                int j;
                for (int conLength = j = contextList.length; j > 0; --j) {
                    final String rcontext = contextList[j - 1];
                    try {
                        String logName = null;
                        if (rcontext.equals("NoContext")) {
                            logName = rversion + "Rlog.txt";
                        }
                        else {
                            logName = rversion + rcontext + "Rlog.txt";
                        }
                        logg.init(dirName + File.separator + logName);
                    }
                    catch (final Exception e) {
                        Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error while reverting"), e);
                    }
                    revertPatch.readInfoFile(rversion, rcontext, logg);
                    UpdateManager.getUpdateState().setCurrentState(14, System.currentTimeMillis());
                    isPostInvocationSuccess = revertPatch.revertPostInvocationClasses(this.intForPost);
                    if (!isPostInvocationSuccess) {
                        return false;
                    }
                    if (!dir.equals("PreInstall")) {
                        UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
                        revertPatch.startReverting();
                    }
                    UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
                    revertPatch.compressEEARAfterReverting();
                    UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
                    isPreInvocationSuccess = revertPatch.revertPreInvocationClasses(this.intForPre);
                    if (!isPreInvocationSuccess) {
                        return false;
                    }
                    revertPatch.deleteBackupDir();
                }
            }
            String patchType = vprofile.getTheAdditionalDetail(rversion, "Type");
            if (patchType == null) {
                patchType = "SP";
            }
            vprofile.removeVersion(rversion, specsFile, patchType);
        }
        String patchFileName = this.installDetail.getPatchFileNamePath();
        patchFileName = patchFileName.substring(patchFileName.lastIndexOf(File.separator) + 1);
        final File patchFile = new File(this.dirToUnzip + File.separator + "Patch" + File.separator + patchFileName);
        if (patchFile.exists()) {
            patchFile.delete();
        }
        if (this.GUI) {
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startInstallFailureAnimationCompletion();
            UpdateManagerUtil.updateTheInstallUI();
        }
        else {
            UpdateManagerUtil.updateTheFailureInCMD();
        }
        return false;
    }
    
    private void moveBackupFiles(final Object[] aList, final String backupDir) {
        if (aList != null) {
            for (int i = 0; i < aList.length; ++i) {
                try {
                    String fileName = (String)aList[i];
                    fileName = CommonUtil.convertfilenameToOsFilename(fileName);
                    File file = null;
                    file = new File(this.dirToUnzip + File.separator + fileName);
                    int dataRead = 1;
                    final byte[] data = new byte[10240];
                    if (file.exists()) {
                        FileInputStream input = new FileInputStream(file);
                        CommonUtil.createAllSubDirectories(backupDir + File.separator + fileName);
                        FileOutputStream output = new FileOutputStream(backupDir + File.separator + fileName);
                        while (dataRead != -1) {
                            dataRead = input.read(data);
                            if (dataRead == -1) {
                                break;
                            }
                            output.write(data, 0, dataRead);
                        }
                        input.close();
                        output.flush();
                        output.close();
                        input = null;
                        output = null;
                    }
                }
                catch (final Exception e) {
                    Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while taking backup for pre invokation classes"), e);
                }
            }
        }
    }
    
    private boolean revert(final Object[] array, final String dir, final int classIndex, final Object[] filesToModify) {
        final Object[] obj = array;
        final int size = obj.length;
        for (int i = 0; i < classIndex; i += 4) {
            try {
                String preFileName = null;
                preFileName = (String)obj[i];
                if (File.separator.equals("/")) {
                    preFileName = preFileName.replace('\\', '/');
                }
                else {
                    preFileName = preFileName.replace('/', '\\');
                }
                final int lastIndex = preFileName.lastIndexOf(File.separator);
                final String backupDirName = preFileName.substring(lastIndex + 1);
                this.revertPreInstallClasses(dir, filesToModify, backupDirName);
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while reverting pre/post invokation classes"), e);
                return false;
            }
        }
        return true;
    }
    
    public boolean revertPreInstallClasses(final String dir, final Object[] filesToModify, final String backupDirName) {
        if (filesToModify == null) {
            return true;
        }
        String dirPath = null;
        final String prePostDir = dir;
        for (int i = 0; i < filesToModify.length; ++i) {
            try {
                if (this.contextDir.equals("NoContext")) {
                    dirPath = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator + prePostDir + File.separator;
                }
                else {
                    dirPath = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator + this.contextDir + File.separator + prePostDir + File.separator;
                }
                final String fileName = (String)filesToModify[i];
                File file = null;
                file = new File(dirPath + File.separator + backupDirName + File.separator + fileName);
                int dataRead = 1;
                final byte[] data = new byte[10240];
                if (file.exists()) {
                    FileInputStream input = null;
                    FileOutputStream output = null;
                    try {
                        input = new FileInputStream(file);
                        output = new FileOutputStream(this.dirToUnzip + File.separator + fileName);
                        while (dataRead != -1) {
                            dataRead = input.read(data);
                            if (dataRead == -1) {
                                break;
                            }
                            output.write(data, 0, dataRead);
                        }
                    }
                    finally {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.flush();
                            output.close();
                        }
                    }
                }
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while reverting backup for pre invokation classes"), e);
                return false;
            }
        }
        if (Paths.get(dirPath, backupDirName).toFile().exists()) {
            CommonUtil.deleteFiles(dirPath + File.separator + backupDirName);
        }
        return true;
    }
    
    public boolean startUnzipping() {
        this.installEEARFiles();
        final int size = this.fileGrpVector.size();
        if (this.GUI) {}
        for (int i = 0; i < size; ++i) {
            final FileGroup filgrp = this.fileGrpVector.elementAt(i);
            final Vector fgFileNames = filgrp.getFileNameVector();
            final Vector jarFileNames = filgrp.getJarNameVector();
            try {
                if (jarFileNames.size() == 0) {
                    String zipFileName = "";
                    for (int q = 0; q < fgFileNames.size(); ++q) {
                        try {
                            final String fileName = fgFileNames.elementAt(q);
                            this.zipFileEntry = this.zipFile.getEntry(fileName);
                            if (!this.zipFileEntry.isDirectory()) {
                                zipFileName = this.zipFileEntry.getName();
                                if (!zipFileName.equals("inf.xml")) {
                                    zipFileName = CommonUtil.convertfilenameToOsFilename(zipFileName);
                                    final InputStream unzipper = this.zipFile.getInputStream(this.zipFileEntry);
                                    if (!this.contextDir.equals("NoContext") && zipFileName.startsWith(this.contextDir)) {
                                        zipFileName = zipFileName.substring(this.contextDir.length() + 1);
                                    }
                                    Unzipper.out.info(zipFileName);
                                    this.logg.log(zipFileName);
                                    final String progressFileName = zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1);
                                    final int prg = 38 + 12 * (i + 1) / size * (q + 1) / fgFileNames.size();
                                    this.showStatus("Applying File Changes", progressFileName, prg);
                                    final File f = Paths.get(this.dirToUnzip, zipFileName).toFile();
                                    if (!f.getCanonicalPath().startsWith(new File(this.dirToUnzip).getCanonicalPath())) {
                                        throw new IOException("Entry is outside of the target dir: " + f.getName());
                                    }
                                    CommonUtil.createAllSubDirectories(this.dirToUnzip + File.separator + zipFileName);
                                    final boolean res = this.writeFile(unzipper, Paths.get(this.dirToUnzip, zipFileName).toFile().getCanonicalPath());
                                    if (!res) {
                                        return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, false, this.zipFileEntry.getName(), null);
                                    }
                                    if (!FileUtil.isFileExists(f, f.getName())) {
                                        for (final File existingFile : f.getParentFile().listFiles()) {
                                            if (existingFile.isFile() && existingFile.getName().equalsIgnoreCase(f.getName())) {
                                                existingFile.renameTo(f);
                                            }
                                        }
                                    }
                                    if ((System.getProperty("os.name").startsWith("Linux") || System.getProperty("os.name").startsWith("Sun")) && (zipFileName.endsWith(".sh") || zipFileName.endsWith(".bin"))) {
                                        UpdateManagerUtil.setExecutablePermission(this.dirToUnzip + File.separator + zipFileName);
                                    }
                                }
                            }
                        }
                        catch (final Exception ex) {
                            Unzipper.out.log(Level.SEVERE, "ERR:" + this.corruptMessage, ex);
                            this.logg.fail(CommonUtil.getString(this.corruptMessage), ex);
                            if (this.GUI) {
                                UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                                UpdateManagerUtil.setInstallCorruptMainLabelMessage(CommonUtil.getString(this.corruptMessage), " ");
                                UpdateManagerUtil.setDefaultCursor();
                            }
                            else {
                                ConsoleOut.println(CommonUtil.getString(this.corruptMessage));
                            }
                            return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, false, zipFileName, null);
                        }
                    }
                }
                else {
                    String jarFileName = null;
                    try {
                        for (int a = 0; a < jarFileNames.size(); ++a) {
                            jarFileName = jarFileNames.elementAt(a);
                            if (!this.contextDir.equals("NoContext") && jarFileName.startsWith(this.contextDir)) {
                                jarFileName = jarFileName.substring(this.contextDir.length() + 1);
                            }
                            jarFileName = CommonUtil.convertfilenameToOsFilename(jarFileName);
                            if (new File(this.dirToUnzip + File.separator + jarFileName).exists()) {
                                final String message = "Going to update the " + jarFileName + " contents";
                                Unzipper.out.info(message);
                                this.logg.log(message);
                                final String progressFileName2 = jarFileName.substring(jarFileName.lastIndexOf(File.separator) + 1);
                                final int prg2 = 30 + 8 * (i + 1) / size * (a + 1) / jarFileNames.size();
                                this.showStatus("Applying File Changes", progressFileName2, prg2);
                                final String fgFileName = fgFileNames.elementAt(0);
                                if (fgFileName.endsWith(".ujar")) {
                                    this.zipFileEntry = this.zipFile.getEntry(fgFileName);
                                    try (final InputStream unzipper2 = this.zipFile.getInputStream(this.zipFileEntry)) {
                                        final Path filePath = Paths.get(this.dirToUnzip, "patchtemp", "jarpatch", fgFileName);
                                        if (!filePath.toFile().getCanonicalPath().startsWith(Paths.get(this.dirToUnzip, "patchtemp", "jarpatch").toFile().getCanonicalPath())) {
                                            throw new IOException("Entry is outside of the target dir: " + filePath.toFile().getName());
                                        }
                                        CommonUtil.createAllSubDirectories(filePath.toString());
                                        final boolean res2 = this.writeFile(unzipper2, filePath.toString());
                                        if (!res2) {
                                            return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, true, jarFileName, null);
                                        }
                                        this.jarUpdater.addSkipJarEntries(filgrp.getDeletedFiles());
                                        this.jarUpdater.updateTheJarFile(fgFileName, jarFileName, this.dirToUnzip);
                                    }
                                }
                                this.jarUpdater.jarUpdatedJar(jarFileName);
                                this.jarUpdater.copyUpdatedJarFile(jarFileName, this.dirToUnzip + File.separator + jarFileName);
                            }
                            else {
                                final String fgFileName2 = fgFileNames.elementAt(0);
                                if (fgFileName2.endsWith(".ujar")) {
                                    final FileNotFoundException fnfe = new FileNotFoundException(jarFileName);
                                    final String errMsg = CommonUtil.getString("ERR: File not found in the product ") + "\n";
                                    Unzipper.out.log(Level.SEVERE, errMsg, fnfe);
                                    return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, true, jarFileName, null);
                                }
                            }
                        }
                    }
                    catch (final Exception ex2) {
                        Unzipper.out.log(Level.SEVERE, "ERR:" + this.corruptMessage, ex2);
                        this.logg.fail(CommonUtil.getString(this.corruptMessage), ex2);
                        if (this.GUI) {
                            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                            UpdateManagerUtil.setInstallCorruptMainLabelMessage(CommonUtil.getString(this.corruptMessage), " ");
                            UpdateManagerUtil.setDefaultCursor();
                        }
                        else {
                            ConsoleOut.println(CommonUtil.getString(this.corruptMessage));
                        }
                        return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, true, jarFileName, null);
                    }
                }
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + this.corruptMessage, e);
                this.logg.fail(CommonUtil.getString(this.corruptMessage), e);
                if (this.GUI) {
                    UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                    UpdateManagerUtil.setInstallCorruptMainLabelMessage(CommonUtil.getString(this.corruptMessage), " ");
                    UpdateManagerUtil.setDefaultCursor();
                }
                else {
                    ConsoleOut.println(CommonUtil.getString(this.corruptMessage));
                }
                UpdateManagerUtil.setExitStatus(1);
                return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), i, false, null, null);
            }
        }
        final Set<String> deletedFiles = new TreeSet<String>();
        final Set<String> jarNamesContainingDeleteEntries = this.jarEntriesToBeDeleted.keySet();
        for (final String jarName : jarNamesContainingDeleteEntries) {
            try {
                final String osSpecificjarName = CommonUtil.convertfilenameToOsFilename(jarName);
                final String msg = "Deleting entries from jar file, " + jarName;
                Unzipper.out.info(msg);
                this.logg.log(msg);
                final Path filePath2 = Paths.get(this.dirToUnzip, "patchtemp", "jarpatch", jarName);
                if (!filePath2.toFile().getCanonicalPath().startsWith(Paths.get(this.dirToUnzip, "patchtemp", "jarpatch").toFile().getCanonicalPath())) {
                    throw new IOException("Entry is outside of the target dir: " + filePath2.toFile().getName());
                }
                CommonUtil.createAllSubDirectories(this.dirToUnzip + File.separator + "patchtemp" + File.separator + "jarpatch" + File.separator + jarName);
                this.jarUpdater.addSkipJarEntries(this.jarEntriesToBeDeleted.get(jarName));
                this.jarUpdater.updateTheJarFile(jarName, osSpecificjarName, this.dirToUnzip);
                this.jarUpdater.jarUpdatedJar(osSpecificjarName);
                this.jarUpdater.copyUpdatedJarFile(osSpecificjarName, this.dirToUnzip + File.separator + osSpecificjarName);
            }
            catch (final Exception ex3) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + ex3.getMessage(), ex3);
                this.logg.fail(CommonUtil.getString(ex3.getMessage()), ex3);
                if (this.GUI) {
                    UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                    UpdateManagerUtil.setInstallCorruptMainLabelMessage(CommonUtil.getString(ex3.getMessage()), " ");
                    UpdateManagerUtil.setDefaultCursor();
                }
                else {
                    ConsoleOut.println(CommonUtil.getString(ex3.getMessage()));
                }
                UpdateManagerUtil.setExitStatus(1);
                return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), -1, false, jarName, deletedFiles);
            }
        }
        int fileIndex = 0;
        for (final String deletedFileName : this.filesToBeDeleted) {
            final File fileToBeDeleted = new File(this.dirToUnzip + File.separator + deletedFileName);
            if (FileUtil.isFileExists(fileToBeDeleted, fileToBeDeleted.getName())) {
                try {
                    this.showStatus("Removing", deletedFileName, 50 + 5 * (fileIndex + 1) / this.filesToBeDeleted.size());
                    if (fileToBeDeleted.isDirectory()) {
                        this.deleteDir(fileToBeDeleted);
                        this.deleteEmptyDir(fileToBeDeleted.getParentFile());
                    }
                    else {
                        Files.deleteIfExists(Paths.get(this.dirToUnzip, deletedFileName));
                        Unzipper.out.info("Removed : " + deletedFileName);
                        this.logg.log("Removed : " + deletedFileName);
                        this.deleteEmptyDir(Paths.get(this.dirToUnzip, deletedFileName).getParent().toFile());
                    }
                }
                catch (final IOException ioe) {
                    Unzipper.out.log(Level.SEVERE, "ERR:" + ioe.getMessage(), ioe);
                    this.logg.fail(CommonUtil.getString(ioe.getMessage()), ioe);
                    if (this.GUI) {
                        UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                        UpdateManagerUtil.setInstallCorruptMainLabelMessage(CommonUtil.getString(ioe.getMessage()), " ");
                        UpdateManagerUtil.setDefaultCursor();
                    }
                    else {
                        ConsoleOut.println(CommonUtil.getString(ioe.getMessage()));
                    }
                    UpdateManagerUtil.setExitStatus(1);
                    return this.fileUpdateFailureHandler((Vector)this.fileGrpVector.clone(), -1, false, deletedFileName, deletedFiles);
                }
                deletedFiles.add(deletedFileName);
            }
            ++fileIndex;
        }
        Unzipper.out.info("All file level changes applied sucessfully. Going to clean up temp directory.");
        try {
            FileUtil.deleteFiles(Paths.get(this.dirToUnzip, "patchtemp").toString(), Paths.get(this.dirToUnzip, "eeartemp").toString());
        }
        catch (final RuntimeException | IOException ex4) {}
        return true;
    }
    
    private void deleteDir(final File dir) {
        if (!dir.exists()) {
            return;
        }
        if (dir.isDirectory()) {
            for (final File f : dir.listFiles()) {
                this.deleteDir(f);
            }
        }
        String name = dir.getName();
        try {
            name = dir.getCanonicalPath().substring(new File(this.dirToUnzip).getCanonicalPath().length() + 1);
            Files.delete(dir.toPath());
            Unzipper.out.info("Removed : " + name);
            this.logg.log("Removed : " + name);
        }
        catch (final IOException ioe) {
            Unzipper.out.info("Removing : " + name + "[FAILED]");
            this.logg.log("Removing : " + name + "[FAILED]");
            throw new IllegalArgumentException(ioe);
        }
    }
    
    private void deleteEmptyDir(final File dirToDel) {
        if (!dirToDel.exists()) {
            return;
        }
        final int len = dirToDel.listFiles().length;
        if (len == 0) {
            String name = dirToDel.getName();
            try {
                name = dirToDel.getCanonicalPath().substring(new File(this.dirToUnzip).getCanonicalPath().length() + 1);
                Files.delete(dirToDel.toPath());
                Unzipper.out.info("Removed : " + name);
                this.logg.log("Removed : " + name);
                this.deleteEmptyDir(dirToDel.getParentFile());
            }
            catch (final IOException ioe) {
                Unzipper.out.info("Removing : " + name + "[FAILED]");
                this.logg.log("Removing : " + name + "[FAILED]");
                throw new IllegalArgumentException(ioe);
            }
        }
    }
    
    public void extractBaseEEARFiles() {
        final JarExtractor jarext = new JarExtractor();
        if (this.zipfg.size() != 0) {
            final int s = this.zipfg.size();
            String archiveName = "";
            for (int a = 0; a < s; ++a) {
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                archiveName = zip.getZipName();
                final File f = new File(this.dirToUnzip + File.separator + archiveName);
                final File dest = new File(this.dirToUnzip + File.separator + "eeartemp" + File.separator + archiveName);
                if (f.isDirectory()) {
                    dest.mkdirs();
                }
                try {
                    if (!f.isDirectory()) {
                        JarExtractor.extract(f, dest);
                    }
                    else {
                        this.jarUpdater.revertEEARFiles(f, dest);
                    }
                }
                catch (final Exception e) {
                    Unzipper.out.log(Level.SEVERE, "Exception occured while extracting EEAR file, \"" + archiveName + "\"", e);
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
    
    public void compressUpdatedEEARFiles() {
        if (this.zipfg.size() != 0) {
            final int s = this.zipfg.size();
            String archiveName = "";
            for (int a = 0; a < s; ++a) {
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                archiveName = zip.getZipName();
                final JarCompressor jarcom = new JarCompressor();
                final File f = new File(this.dirToUnzip + File.separator + "eeartemp" + File.separator + archiveName);
                final File dest = new File(this.dirToUnzip + File.separator + archiveName);
                if (f.isDirectory()) {
                    dest.mkdirs();
                }
                try {
                    if (!dest.isDirectory()) {
                        JarCompressor.compress(f, dest);
                    }
                    else {
                        this.jarUpdater.revertEEARFiles(f, dest);
                    }
                }
                catch (final Exception e) {
                    ConsoleOut.println("Exception occured while compressing EEAR file : " + archiveName);
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void installEEARFiles() {
        if (this.zipfg.size() != 0) {
            for (int s = this.zipfg.size(), a = 0; a < s; ++a) {
                String zipName = "";
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                final String archiveName = zip.getZipName();
                final String dir = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator + this.contextDir;
                File f = null;
                for (int b = 0; b < zip.getFilesList().size(); ++b) {
                    this.zipFileEntry = this.zipFile.getEntry(zip.getFilesList().get(b));
                    if (!this.zipFileEntry.isDirectory()) {
                        zipName = this.zipFileEntry.getName();
                        final long fileSize = this.zipFileEntry.getSize();
                        if (!zipName.equals("inf.xml")) {
                            zipName = CommonUtil.convertfilenameToOsFilename(zipName);
                            try {
                                final InputStream unzipper = this.zipFile.getInputStream(this.zipFileEntry);
                                if (!this.contextDir.equals("NoContext") && zipName.startsWith(this.contextDir)) {
                                    zipName = zipName.substring(this.contextDir.length() + 1);
                                }
                                Unzipper.out.info(zipName);
                                this.logg.log(zipName);
                                f = new File(this.dirToUnzip + File.separator + "eeartemp" + File.separator + zipName);
                                if (!f.getCanonicalPath().startsWith(new File(this.dirToUnzip).getCanonicalPath() + File.separator + "eeartemp")) {
                                    throw new IOException("Entry is outside of the target dir: " + zipName);
                                }
                                if (!f.exists()) {
                                    final BufferedWriter out = new BufferedWriter(new FileWriter(dir + File.separator + "newFiles", true));
                                    out.write(f.getCanonicalPath().substring(f.getCanonicalPath().indexOf("eeartemp")) + "\n");
                                    out.close();
                                }
                                CommonUtil.createAllSubDirectories(this.dirToUnzip + File.separator + "eeartemp" + File.separator + zipName);
                                this.writeFile(unzipper, Paths.get(this.dirToUnzip, "eeartemp", zipName).toFile().getCanonicalPath());
                            }
                            catch (final Exception e) {
                                ConsoleOut.println("Exception occured while updating EEAR file : " + archiveName);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean invokePostInstallationClasses() {
        final String header = "Post invocation in progress";
        this.moveTempFiles();
        Unzipper.out.info(CommonUtil.getString(header));
        this.updateMainLabelProgress(CommonUtil.getString(header), " ");
        final int size = this.postInstallArray.length;
        if (size == 0) {
            Unzipper.out.info(CommonUtil.getString("No post invocation classes are present."));
            Unzipper.out.info(CommonUtil.getString("Copying the ppm file under Patch directory."));
            this.defaultPostInvocation();
            return true;
        }
        int prg = 56;
        for (int i = 0; i < size; i += 4) {
            this.showStatus(header, "", prg);
            this.intForPost += 4;
            try {
                String zipFileName = "";
                String fileName = (String)this.postInstallArray[i];
                UpdateManager.getUpdateState().setCurrentPrePostClassInProgress(fileName, System.currentTimeMillis());
                final ArrayList depenList = (ArrayList)this.postInstallArray[i + 1];
                final ArrayList depenClassPath = (ArrayList)this.postInstallArray[i + 2];
                final Properties prop = (Properties)this.postInstallArray[i + 3];
                zipFileName = fileName;
                if (!zipFileName.equals("inf.xml") && !this.alreadyCompletedClasses.contains(fileName)) {
                    Unzipper.out.info(CommonUtil.getString("Executing class:") + fileName);
                    final String postInstall = "PostInstall";
                    String dir = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator;
                    if (!this.contextDir.equals("NoContext")) {
                        dir = dir + this.contextDir + File.separator;
                        if (fileName.startsWith(this.contextDir)) {
                            fileName = fileName.substring(this.contextDir.length() + 1);
                        }
                    }
                    dir = dir + postInstall + File.separator;
                    new File(dir).mkdir();
                    final boolean postSuccess = this.contextPostInstallation(postInstall, fileName, dir, prop, depenList, depenClassPath, i + 4);
                    if (!postSuccess) {
                        UpdateManager.getUpdateState().setErrorCode(200);
                        Unzipper.out.info(CommonUtil.getString("ERROR CODE") + "     : " + 200);
                        Unzipper.out.info(CommonUtil.getString("ERROR IN") + "       : " + CommonUtil.getString("Post install"));
                        Unzipper.out.info(CommonUtil.getString("ERROR IN CLASS") + " : " + fileName);
                        return false;
                    }
                    UpdateManager.getUpdateState().setCurrentClassStats(postSuccess, System.currentTimeMillis());
                }
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error in post invocation"), e);
                return false;
            }
            prg = 55 + 45 * this.intForPost / size;
            this.showStatus(header, "", prg);
        }
        this.defaultPostInvocation();
        return true;
    }
    
    public void closeZipFile() {
        try {
            this.zipFile.close();
        }
        catch (final Exception zipex) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception in Zip closing."), zipex);
        }
    }
    
    public boolean contextPostInstallation(final String postInstall, String zipFileName, final String dir, final Properties prop, final ArrayList dependentClassesList, final ArrayList classPathList, final int classIndex) {
        final String backupDir = dir;
        String className = null;
        String depenFile = null;
        try {
            className = CommonUtil.convertfilenameToOsFilename(zipFileName);
            CommonUtil.createAllSubDirectories(backupDir + className);
            this.zipFileEntry = this.zipFile.getEntry(zipFileName);
            InputStream unzipper = null;
            if (this.zipFileEntry != null) {
                unzipper = this.zipFile.getInputStream(this.zipFileEntry);
                this.writeFile(unzipper, backupDir + className);
            }
            if (dependentClassesList != null) {
                for (int size = dependentClassesList.size(), i = 0; i < size; ++i) {
                    depenFile = dependentClassesList.get(i);
                    className = CommonUtil.convertfilenameToOsFilename(depenFile);
                    CommonUtil.createAllSubDirectories(backupDir + className);
                    this.zipFileEntry = this.zipFile.getEntry(depenFile);
                    unzipper = this.zipFile.getInputStream(this.zipFileEntry);
                    this.writeFile(unzipper, backupDir + className);
                }
            }
            unzipper.close();
        }
        catch (final NullPointerException npe) {
            final String depenError = CommonUtil.getString("Dependent File") + " " + depenFile + " " + CommonUtil.getString("Not found in post invocation") + " " + zipFileName;
            this.displayError(depenError);
            this.logg.fail(depenError, npe);
            Unzipper.out.log(Level.SEVERE, "ERR:" + depenError + " ", npe);
        }
        catch (final Exception expp) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception in post installation."), expp);
        }
        if (zipFileName.endsWith(".class")) {
            try {
                zipFileName = CommonUtil.convertfilenameToOsFilename(zipFileName);
                final int index = zipFileName.lastIndexOf(".class");
                final int lastIndex = zipFileName.lastIndexOf(File.separator);
                final String backupDirName = zipFileName.substring(lastIndex + 1);
                zipFileName = zipFileName.substring(0, index);
                if (File.separator.equals("/")) {
                    zipFileName = zipFileName.replace('/', '.');
                }
                else {
                    zipFileName = zipFileName.replace('\\', '.');
                }
                final List<URL> classPathUrls = new ArrayList<URL>();
                for (int s = dependentClassesList.size(), j = 0; j < s; ++j) {
                    String cp = dependentClassesList.get(j);
                    cp = CommonUtil.convertfilenameToOsFilename(cp);
                    classPathUrls.add(new File(backupDir + File.separator + cp).toURI().toURL());
                }
                boolean isJarsLoadedFromClasspathEntries = false;
                final Path classPathConf_Path = Paths.get(this.dirToUnzip, "conf", "classpath.conf");
                if (classPathConf_Path.toFile().exists()) {
                    final Properties pathProperties = new Properties();
                    try (final InputStream is = new FileInputStream(classPathConf_Path.toFile())) {
                        pathProperties.load(is);
                    }
                    for (final Object key : ((Hashtable<Object, V>)pathProperties).keySet()) {
                        final String dirName = pathProperties.getProperty((String)key);
                        final Path folderPath = Paths.get(this.dirToUnzip, dirName);
                        this.addAllJarFilesOfFolderToList(folderPath.toFile(), classPathUrls);
                    }
                }
                else if (classPathList != null && !classPathList.isEmpty()) {
                    isJarsLoadedFromClasspathEntries = true;
                    for (final Object classPathEntry : classPathList) {
                        classPathUrls.add(Paths.get(this.dirToUnzip, (String)classPathEntry).toUri().toURL());
                    }
                }
                if (!isJarsLoadedFromClasspathEntries) {
                    final Path binFolderPath = Paths.get(this.dirToUnzip, "bin");
                    this.addAllJarFilesOfFolderToList(binFolderPath.toFile(), classPathUrls);
                }
                final File f = new File(backupDir);
                classPathUrls.add(f.toURI().toURL());
                final URL[] urlarr = new URL[classPathUrls.size()];
                for (int k = 0; k < classPathUrls.size(); ++k) {
                    urlarr[k] = classPathUrls.get(k);
                    final URLConnection urlConn = urlarr[k].openConnection();
                    urlConn.setDefaultUseCaches(false);
                }
                URLClassLoader urlclsldr = null;
                Object[] filesToModify = null;
                int type;
                String errorMessage;
                try {
                    urlclsldr = new URLClassLoader(urlarr);
                    Thread.currentThread().setContextClassLoader(urlclsldr);
                    final Class postClass = urlclsldr.loadClass(zipFileName);
                    final Object preObj = postClass.newInstance();
                    final Method isfbp = postClass.getMethod("isFilesToBeBackedUp", (Class[])null);
                    final Boolean bol = (Boolean)isfbp.invoke(preObj, (Object[])null);
                    final boolean isFilesToBeBackedUp = bol;
                    if (isFilesToBeBackedUp) {
                        final Method gftm = postClass.getMethod("getFilesToModify", (Class[])null);
                        filesToModify = (Object[])gftm.invoke(preObj, (Object[])null);
                        this.moveBackupFiles(filesToModify, backupDir + backupDirName);
                    }
                    Properties p = null;
                    if (prop == null) {
                        p = new Properties();
                    }
                    else {
                        p = prop;
                    }
                    ((Hashtable<String, String>)p).put("product", this.confProductName);
                    ((Hashtable<String, String>)p).put("version", this.confProductVersion);
                    ((Hashtable<String, String>)p).put("home", this.dirToUnzip);
                    ((Hashtable<String, String>)p).put("mode", String.valueOf(this.GUI));
                    ((Hashtable<String, String>)p).put("patchversion", this.installDetail.getPatchVersion());
                    ((Hashtable<String, String>)p).put("context", this.contextDir);
                    JDialog jd = null;
                    if ((jd = UpdateManagerUtil.getParent()) != null) {
                        ((Hashtable<String, JDialog>)p).put("parentDialog", jd);
                    }
                    final Class[] constArgs = { p.getClass() };
                    final Method init = postClass.getMethod("install", (Class[])constArgs);
                    final Object[] constArgsVal = { p };
                    final Integer returnValue = (Integer)init.invoke(preObj, constArgsVal);
                    type = returnValue;
                    final Method gem = postClass.getMethod("getErrorMsg", (Class[])null);
                    errorMessage = (String)gem.invoke(preObj, (Object[])null);
                }
                finally {
                    if (urlclsldr != null) {
                        urlclsldr.close();
                        ClassLoaderUtil.unloadNativeLibraries();
                    }
                }
                if (type == 1) {
                    return true;
                }
                if (type == 2) {
                    return this.failureRevertAll(this.postInstallArray, postInstall, errorMessage, classIndex, filesToModify);
                }
                if (type == 3) {
                    return this.failureRevertContinue(postInstall, filesToModify, backupDirName);
                }
                if (type == 4) {
                    return this.failureRevertAllContinue(this.postInstallArray, postInstall, classIndex, filesToModify);
                }
                if (type == 5) {
                    return this.failureRevertComplete(this.postInstallArray, postInstall, errorMessage, classIndex, filesToModify);
                }
                if (type == 9) {
                    return this.failureRevertAbsolute(this.postInstallArray, postInstall, errorMessage, classIndex);
                }
                errorMessage = "The post invocation class returns a constant which is not supported";
                this.displayError(errorMessage);
                return false;
            }
            catch (final ClassNotFoundException ce) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + this.unexpectedError, ce);
                this.logg.fail(CommonUtil.getString(this.unexpectedError), ce);
                this.displayError(this.unexpectedError);
                return false;
            }
            catch (final Exception e) {
                Unzipper.out.log(Level.SEVERE, "ERR:" + this.unexpectedError, e);
                this.logg.fail(CommonUtil.getString(this.unexpectedError), e);
                this.displayError(this.unexpectedError);
                return false;
            }
        }
        return true;
    }
    
    private boolean writeFile(final InputStream unzipper, final String path) {
        FileOutputStream outs = null;
        BufferedInputStream origin = null;
        try {
            final int BUFFER = 10240;
            final byte[] data = new byte[10240];
            if (!new File(path).getCanonicalPath().startsWith(new File(this.dirToUnzip).getCanonicalPath())) {
                throw new IOException("Entry is outside of the target dir: " + new File(path).getName());
            }
            outs = new FileOutputStream(new File(path).getCanonicalPath());
            origin = new BufferedInputStream(unzipper, 10240);
            int count;
            while ((count = origin.read(data, 0, 10240)) != -1) {
                outs.write(data, 0, count);
            }
            outs.flush();
        }
        catch (final Exception ex) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while writing file."), ex);
            return false;
        }
        finally {
            try {
                if (origin != null) {
                    origin.close();
                }
                if (outs != null) {
                    outs.close();
                }
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return true;
    }
    
    public boolean initializeUnzipper() {
        try {
            (this.backupFiles = new Backup(this.dirToUnzip, this.contextDir, this.GUI, this.logg)).readInfoInPatchFile();
            this.backupFiles.createInfoForLastVersion();
            this.fileGrpVector = this.backupFiles.getFileGroupVector();
            this.zipfg = this.backupFiles.getZipGroupVector();
            this.preInstallArray = this.backupFiles.getPreInstallArray();
            this.postInstallArray = this.backupFiles.getPostInstallArray();
            this.currentVersion = this.backupFiles.getCurrentVersion();
            this.filesToBeDeleted = this.backupFiles.getFilesToBeDeleted();
            this.jarEntriesToBeDeleted = this.backupFiles.getJarEntriesToBeDeleted();
            this.initializeAlreadyCompletedClassesList();
            FileUtil.deleteFiles(Paths.get(this.dirToUnzip, "patchtemp").toString(), Paths.get(this.dirToUnzip, "eeartemp").toString());
        }
        catch (final Exception e) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while initializing unzipper."), e);
            return false;
        }
        return true;
    }
    
    private void initializeAlreadyCompletedClassesList() {
        final String alreadyCompletedClassName = UpdateManager.getAlreadyCompletedPrePostClassName();
        if (alreadyCompletedClassName != null) {
            if (this.preInstallArray.length != 0) {
                for (int i = 0; i < this.preInstallArray.length; i += 4) {
                    final String className = (String)this.preInstallArray[i];
                    this.alreadyCompletedClasses.add(className);
                    if (className.equals(alreadyCompletedClassName)) {
                        return;
                    }
                }
            }
            if (this.postInstallArray.length != 0) {
                for (int j = 0; j < this.postInstallArray.length; j += 4) {
                    final String className = (String)this.postInstallArray[j];
                    this.alreadyCompletedClasses.add(className);
                    if (className.equals(alreadyCompletedClassName)) {
                        return;
                    }
                }
            }
        }
    }
    
    public boolean backupFiles() {
        try {
            this.backupFiles.moveFilesToDelete();
            final String patchReadme = this.backupFiles.getPatchReadme();
            if (patchReadme != null) {
                this.moveReadme(patchReadme, this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion, patchReadme);
            }
            final String contextReadme = this.backupFiles.getContextReadme();
            if (!this.contextDir.equals("NoContext")) {
                String readme = null;
                if (contextReadme.lastIndexOf("/") != -1 || contextReadme.lastIndexOf("\\") != -1) {
                    if (contextReadme.startsWith(this.contextDir)) {
                        readme = contextReadme.substring(this.contextDir.length() + 1);
                    }
                }
                else {
                    readme = contextReadme;
                }
                this.moveReadme(readme, this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator + this.contextDir, contextReadme);
            }
            return true;
        }
        catch (final Exception e) {
            return this.fileUpdateFailureHandler(null, 0, false, "while taking backup", null);
        }
    }
    
    private void moveTempFiles() {
        final int BUFFER = 2048;
        try {
            BufferedOutputStream dest = null;
            BufferedInputStream is = null;
            final Enumeration e = this.zipFile.entries();
            while (e.hasMoreElements()) {
                final ZipEntry entry = e.nextElement();
                is = new BufferedInputStream(this.zipFile.getInputStream(entry));
                final byte[] data = new byte[2048];
                if (entry.getName().startsWith("tempDir")) {
                    final File destFile = new File(this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion, entry.getName());
                    if (!destFile.getCanonicalPath().startsWith(new File(this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion).getCanonicalPath())) {
                        throw new IOException("Entry is outside of the target dir: " + entry.getName());
                    }
                    final File destinationParent = destFile.getParentFile();
                    destinationParent.mkdirs();
                    if (entry.isDirectory()) {
                        continue;
                    }
                    FileOutputStream fos = null;
                    try {
                        is = new BufferedInputStream(this.zipFile.getInputStream(entry));
                        fos = new FileOutputStream(destFile.getCanonicalPath());
                        dest = new BufferedOutputStream(fos, 2048);
                        int currentByte;
                        while ((currentByte = is.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    }
                    finally {
                        if (dest != null) {
                            dest.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while moving temp files."), e2);
        }
    }
    
    private void moveReadme(final String patchReadme, final String dir, final String entry) {
        try {
            final String fileName = entry;
            this.zipFileEntry = this.zipFile.getEntry(fileName);
            if (this.zipFileEntry == null) {
                return;
            }
            final InputStream readmezipper = this.zipFile.getInputStream(this.zipFileEntry);
            CommonUtil.createAllSubDirectories(dir + File.separator + patchReadme);
            this.writeFile(readmezipper, dir + File.separator + patchReadme);
        }
        catch (final Exception e) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while moving readme file."), e);
        }
    }
    
    public boolean defaultPostInvocation() {
        return this.copyPPMFile();
    }
    
    public boolean copyPPMFile() {
        final String ppmPath = this.installDetail.getPatchFileNamePath();
        final FileInputStream inputStream = null;
        try {
            final File f = new File(ppmPath);
            if (f.exists() && ppmPath.endsWith(".ppm")) {
                final Path destFilePath = Paths.get(this.dirToUnzip, "Patch", f.getName());
                if (destFilePath.toFile().exists()) {
                    return true;
                }
                final FileInputStream input = new FileInputStream(ppmPath);
                this.writeFile(input, destFilePath.toString());
                return true;
            }
        }
        catch (final Exception e) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while copying ppm file."), e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex) {
                    Logger.getLogger(Unzipper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex2) {
                    Logger.getLogger(Unzipper.class.getName()).log(Level.SEVERE, null, ex2);
                }
            }
        }
        return false;
    }
    
    private void showStatus(final String status, final String msg, final int percentage) {
        if (this.GUI) {
            UpdateManagerUtil.updateInstallUIProgress(percentage, msg, status);
        }
        else {
            UpdateManagerUtil.updateProgress(percentage, msg, status);
        }
    }
    
    private void updateMainLabelProgress(final String mainName, final String subName) {
        if (this.GUI) {
            UpdateManagerUtil.setInstallMainLabelMessage(mainName, subName);
        }
    }
    
    private void displayError(final String errorMessage) {
        UpdateManagerUtil.setExitStatus(1);
        if (this.GUI) {
            final String newerrMsg = UpdateManagerUtil.getNewText(CommonUtil.getString(errorMessage), 4, "red");
            UpdateManagerUtil.showErrorMessage(newerrMsg, true);
        }
        else {
            ConsoleOut.println("\n\n" + CommonUtil.getString(errorMessage) + "\n\n");
        }
    }
    
    public boolean fileUpdateFailureHandler(final Vector completeVec, final int exitIndex, final boolean isUjar, final String exitFileName, final Set<String> alreadyDeletedFiles) {
        try {
            final String errMsg = CommonUtil.getString("Error while updating files");
            if (exitFileName != null) {
                Unzipper.out.log(Level.INFO, CommonUtil.getString("Error while updating the file ") + exitFileName);
            }
            Vector revertVetor;
            if (alreadyDeletedFiles != null) {
                revertVetor = completeVec;
            }
            else if (exitIndex > 0) {
                revertVetor = this.abnormalRevert(completeVec, exitIndex, isUjar, exitFileName);
            }
            else {
                revertVetor = null;
            }
            this.failureRevertAbnormal(errMsg, revertVetor, alreadyDeletedFiles);
        }
        catch (final Exception e) {
            Unzipper.out.log(Level.SEVERE, "ERR:" + this.corruptMessage, e);
        }
        finally {
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.setDefaultCursor();
        }
        return false;
    }
    
    public Vector abnormalRevert(final Vector completeVec, final int exitIndex, final boolean isUjar, final String fileName) {
        final Vector revertVetor = new Vector();
        for (int i = 0; i < exitIndex; ++i) {
            revertVetor.addElement(completeVec.elementAt(i));
        }
        if (!isUjar) {
            final FileGroup fg = completeVec.elementAt(exitIndex);
            final int size = fg.getFileNameVector().size();
            for (int index = fg.getFileNameVector().lastIndexOf(fileName), j = size - 1; j >= index; --j) {
                fg.getFileNameVector().remove(j);
            }
            revertVetor.addElement(fg);
        }
        return revertVetor;
    }
    
    private boolean failureRevertAbnormal(final String error, final Vector vec, final Set<String> alreadyDeletedFiles) {
        Unzipper.out.log(Level.INFO, CommonUtil.getString("Error while updating files start to revert from the error point"));
        final String dir = "PostInstall";
        if (this.GUI) {
            UpdateManagerUtil.setErrorMessage(error);
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startFailureCompletion();
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_IN_PROGRESS);
            UpdateManagerUtil.startInstallFailureAnimation();
        }
        else {
            ConsoleOut.println("\n" + error);
        }
        final RevertPatch revertPatch = new RevertPatch(this.installDetail, this.GUI, true);
        final String rversion = this.currentVersion;
        String name = null;
        final LoggingUtil logg = new LoggingUtil();
        final String dirName = this.dirToUnzip + File.separator + "Patch" + File.separator + "logs";
        final File t = new File(dirName);
        if (!t.isDirectory()) {
            t.mkdir();
        }
        if (this.contextDir.equals("NoContext")) {
            name = rversion + "Rlog.txt";
        }
        else {
            name = rversion + this.contextDir + "Rlog.txt";
        }
        logg.init(dirName + File.separator + name);
        revertPatch.readInfoFile(rversion, this.contextDir, logg);
        if (vec != null) {
            revertPatch.setFileGrpVector(vec);
            if (alreadyDeletedFiles == null) {
                revertPatch.setDeletedFiles(new TreeSet<String>());
            }
            else {
                revertPatch.setDeletedFiles(alreadyDeletedFiles);
            }
            UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
            revertPatch.startReverting();
        }
        UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
        revertPatch.compressEEARAfterReverting();
        UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
        revertPatch.revertPreInvocationClasses(this.preInstallArray.length);
        revertPatch.deleteBackupDir();
        final String specsFile = this.dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File specsInf = new File(specsFile);
        if (specsInf.isFile()) {
            final VersionProfile vprofile = VersionProfile.getInstance();
            vprofile.readDocument(specsFile, false, false);
            final String[] contextList = vprofile.getTheContext(rversion);
            if (contextList != null) {
                int j;
                for (int conLength = j = contextList.length; j > 0; --j) {
                    final String rcontext = contextList[j - 1];
                    try {
                        String logName = null;
                        if (rcontext.equals("NoContext")) {
                            logName = rversion + "Rlog.txt";
                        }
                        else {
                            logName = rversion + rcontext + "Rlog.txt";
                        }
                        logg.init(dirName + File.separator + logName);
                    }
                    catch (final Exception e) {
                        Unzipper.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Error while reverting"), e);
                    }
                    revertPatch.readInfoFile(rversion, rcontext, logg);
                    UpdateManager.getUpdateState().setCurrentState(14, System.currentTimeMillis());
                    revertPatch.revertPostInvocationClasses(this.intForPost);
                    if (!dir.equals("PreInstall")) {
                        UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
                        revertPatch.startReverting();
                    }
                    UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
                    revertPatch.compressEEARAfterReverting();
                    UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
                    revertPatch.revertPreInvocationClasses(this.intForPre);
                    revertPatch.deleteBackupDir();
                }
            }
            String patchType = vprofile.getTheAdditionalDetail(rversion, "Type");
            if (patchType == null) {
                patchType = "SP";
            }
            vprofile.removeVersion(rversion, specsFile, patchType);
        }
        String patchFileName = this.installDetail.getPatchFileNamePath();
        patchFileName = patchFileName.substring(patchFileName.lastIndexOf(File.separator) + 1);
        final File patchFile = new File(this.dirToUnzip + File.separator + "Patch" + File.separator + patchFileName);
        if (patchFile.exists()) {
            patchFile.delete();
        }
        if (this.GUI) {
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startInstallFailureAnimationCompletion();
            UpdateManagerUtil.updateTheInstallUI();
        }
        else {
            UpdateManagerUtil.updateTheFailureInCMD();
        }
        return false;
    }
    
    @Deprecated
    public void cleanUpTempDirectory() {
        if (new File(this.dirToUnzip + File.separator + "patchtemp").exists()) {
            CommonUtil.deleteFiles(this.dirToUnzip + File.separator + "patchtemp");
        }
    }
    
    public static void extractPatchesFromPatch(final String patchFilePathInString, final String homeDir) throws Exception {
        final Path patchFilePath = Paths.get(patchFilePathInString, new String[0]);
        final Path patchesFolderPath = Paths.get(homeDir, "patches");
        Unzipper.out.log(Level.INFO, "Going to extract patch file(s) from container of patches patch file : {0}", patchFilePathInString);
        try (final ZipFile ppmZipFile = new ZipFile(new File(patchFilePathInString))) {
            final Enumeration<? extends ZipEntry> zipEntries = ppmZipFile.entries();
            while (zipEntries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry)zipEntries.nextElement();
                final String fileName = entry.getName();
                if (fileName.endsWith(".ppm") || fileName.equals("patches.cp")) {
                    final Path desinationPath = Paths.get(patchesFolderPath.toString(), fileName);
                    if (!desinationPath.toFile().getCanonicalPath().startsWith(Paths.get(homeDir, new String[0]).toFile().getCanonicalPath())) {
                        throw new Exception("Entry is outside of the target dir: " + fileName);
                    }
                    final File desinationFile = desinationPath.toFile();
                    if (!desinationFile.getParentFile().equals(patchesFolderPath.toFile())) {
                        continue;
                    }
                    try (final InputStream inputStream = ppmZipFile.getInputStream(entry)) {
                        extractPatches(inputStream, desinationPath.toString());
                        Unzipper.out.log(Level.INFO, "Extracted \"{0}\" from container of patches.", desinationFile.getName());
                    }
                }
            }
        }
    }
    
    public static void extractPatches(final InputStream is, final String filePath) throws IOException {
        new File(filePath).getParentFile().mkdirs();
        if (!new File(filePath).getCanonicalPath().startsWith(Paths.get(UpdateManagerUtil.getHomeDirectory(), new String[0]).toFile().getCanonicalPath())) {
            throw new RuntimeException("Entry is outside of the product home directory: " + filePath);
        }
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
    
    static {
        Unzipper.out = Logger.getLogger(Unzipper.class.getName());
    }
}
