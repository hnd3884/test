package com.adventnet.tools.update.installer;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import com.zoho.tools.util.FileUtil;
import com.adventnet.tools.update.UpdateManagerUtil;
import com.adventnet.tools.update.UpdateData;
import com.adventnet.tools.update.XmlParser;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Iterator;
import java.io.InputStream;
import com.adventnet.tools.update.ClassLoaderUtil;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.tools.update.FileGroup;
import java.util.Collection;
import com.zoho.tools.util.UpgradeUtil;
import com.adventnet.tools.update.CommonUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.adventnet.tools.update.ZipFileGroup;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import com.adventnet.tools.update.XmlData;
import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class RevertPatch
{
    private static Logger out;
    private Vector fileGrpVector;
    private ArrayList zipfg;
    private Set<String> deletedFiles;
    private Hashtable<String, Set<String>> deletedJarEntries;
    private String versionToRevert;
    private String previousVersion;
    private String contextDir;
    private Object[] preInstallArray;
    private Object[] postInstallArray;
    private Common common;
    private boolean GUI;
    private LoggingUtil logg;
    private boolean fromFailure;
    UpdateJar jarUpdater;
    String installDir;
    String dirName;
    private XmlData infXmlData;
    private Path patchFilePath;
    private FileFilter jarFileFilter;
    
    public RevertPatch(final Common common, final boolean GUI, final boolean fromFailure) {
        this.fileGrpVector = null;
        this.zipfg = null;
        this.deletedFiles = null;
        this.deletedJarEntries = null;
        this.versionToRevert = null;
        this.previousVersion = null;
        this.contextDir = null;
        this.preInstallArray = null;
        this.postInstallArray = null;
        this.common = null;
        this.GUI = false;
        this.logg = null;
        this.fromFailure = false;
        this.jarUpdater = new UpdateJar();
        this.installDir = null;
        this.dirName = null;
        this.jarFileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.getName().endsWith(".jar") && !f.getName().equals("AdventNetUpdateManagerInstaller.jar")) || f.getName().endsWith(".zip");
            }
        };
        this.common = common;
        this.GUI = GUI;
        this.fromFailure = fromFailure;
        this.installDir = common.getInstallationDirectory();
    }
    
    public void extractEEARForReverting() {
        this.dirName = this.installDir + File.separator + "Patch" + File.separator + this.versionToRevert;
        if (this.zipfg.size() != 0) {
            for (int s = this.zipfg.size(), a = 0; a < s; ++a) {
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                if (new File(this.installDir + File.separator + zip.getZipName()).isDirectory()) {
                    new File(this.installDir + File.separator + "eeartemp" + File.separator + zip.getZipName()).mkdirs();
                }
                if (!new File(zip.getZipName()).isDirectory()) {
                    this.jarUpdater.extractEEARFiles(zip.getZipName(), this.installDir + File.separator + "eeartemp");
                }
                else {
                    try {
                        this.jarUpdater.revertEEARFiles(new File(zip.getZipName()), new File(this.installDir + File.separator + "eeartemp"));
                    }
                    catch (final Exception e) {
                        ConsoleOut.println("Exception occured while reverting EEAR file : " + zip.getZipName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void revertEEARFiles() {
        this.dirName = this.installDir + File.separator + "Patch" + File.separator + this.versionToRevert;
        if (this.zipfg.size() != 0) {
            for (int s = this.zipfg.size(), a = 0; a < s; ++a) {
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                try {
                    this.jarUpdater.revertEEARFiles(new File(this.dirName + File.separator + this.contextDir + File.separator + zip.getZipName()), new File(this.installDir + File.separator + "eeartemp" + File.separator + zip.getZipName()));
                }
                catch (final Exception ex) {}
            }
        }
        File inputFile = null;
        inputFile = new File(this.dirName + File.separator + this.contextDir + File.separator + "newFiles");
        if (inputFile.exists()) {
            try {
                final BufferedReader in = new BufferedReader(new FileReader(inputFile.toString()));
                String st;
                while ((st = in.readLine()) != null) {
                    CommonUtil.deleteFiles(this.installDir + File.separator + st);
                }
                in.close();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void startReverting() {
        this.cleanUpTempDirectory();
        this.revertEEARFiles();
        this.dirName = this.installDir + File.separator + "Patch" + File.separator + this.versionToRevert;
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.FILE_RESTORE_STARTED);
        final List<String> deletedFilesList = new ArrayList<String>(this.deletedFiles);
        final int deletedFilesCount = this.deletedFiles.size();
        for (int l = deletedFilesCount - 1; l >= 0; --l) {
            String deletedFileName = deletedFilesList.get(l);
            if (!this.contextDir.equals("NoContext") && deletedFileName.startsWith(this.contextDir)) {
                deletedFileName = deletedFileName.substring(this.contextDir.length() + 1);
            }
            deletedFileName = CommonUtil.convertfilenameToOsFilename(deletedFileName);
            this.showStatus("Restoring", deletedFileName, 45 + 5 * (deletedFilesCount - l) / deletedFilesCount);
            RevertPatch.out.info("Restoring : " + deletedFileName);
            this.logg.log("Restoring : " + deletedFileName);
            this.overWriteFiles(this.dirName, deletedFileName);
        }
        final List<String> jarNames = new ArrayList<String>(this.deletedJarEntries.keySet());
        for (int k = jarNames.size() - 1; k >= 0; --k) {
            String jarName = jarNames.get(k);
            jarName = CommonUtil.convertfilenameToOsFilename(jarName);
            RevertPatch.out.info("Restoring deleted entries in jar : " + jarName);
            this.logg.log("Restoring deleted entries in jar : " + jarName);
            String backupJar = null;
            if (this.contextDir.equals("NoContext")) {
                backupJar = jarName;
            }
            else {
                backupJar = this.contextDir + File.separator + jarName;
            }
            final String inputJar = this.dirName + File.separator + backupJar;
            this.revertTheBackedJarFile(inputJar, jarName, this.installDir, new Vector());
        }
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.FILE_RESTORE_COMPLETED);
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.FILE_REVERT_STARTED);
        final int size = this.fileGrpVector.size();
        for (int i = size - 1; i >= 0; --i) {
            final FileGroup filgrp = this.fileGrpVector.elementAt(i);
            final Vector fgJarNames = filgrp.getJarNameVector();
            final Vector fgFileNames = filgrp.getFileNameVector();
            if (fgJarNames.size() == 0) {
                for (int j = fgFileNames.size() - 1; j >= 0; --j) {
                    String fileName = fgFileNames.elementAt(j);
                    if (!this.contextDir.equals("NoContext") && fileName.startsWith(this.contextDir)) {
                        fileName = fileName.substring(this.contextDir.length() + 1);
                    }
                    fileName = CommonUtil.convertfilenameToOsFilename(fileName);
                    this.logg.log(fileName);
                    RevertPatch.out.info(fileName);
                    final int prg = 50 + 12 * (fgFileNames.size() - j) / fgFileNames.size();
                    if (this.fromFailure) {
                        this.updateFailureProgress(fileName, prg, "Reverting File Changes");
                    }
                    else {
                        this.showStatus("Reverting File Changes", fileName, prg);
                    }
                    this.overWriteFiles(this.dirName, fileName);
                }
            }
            else {
                for (int a = fgJarNames.size() - 1; a >= 0; --a) {
                    String jarFileName = fgFileNames.elementAt(0);
                    if (jarFileName.endsWith(".ujar")) {
                        if (!this.contextDir.equals("NoContext") && jarFileName.startsWith(this.contextDir)) {
                            jarFileName = jarFileName.substring(this.contextDir.length() + 1);
                        }
                        final int index = jarFileName.lastIndexOf(".ujar");
                        String jar = jarFileName.substring(0, index) + ".jar";
                        jar = CommonUtil.convertfilenameToOsFilename(jar);
                        final int prg2 = 62 + 8 * (fgJarNames.size() - a) / fgJarNames.size();
                        if (this.fromFailure) {
                            this.updateFailureProgress(jar, prg2, "Reverting File Changes");
                        }
                        else {
                            this.showStatus("Reverting File Changes", jar, prg2);
                        }
                        String backupJar2 = null;
                        if (this.contextDir.equals("NoContext")) {
                            backupJar2 = jar;
                        }
                        else {
                            backupJar2 = this.contextDir + File.separator + jar;
                        }
                        final String inputJar2 = this.dirName + File.separator + backupJar2;
                        this.revertTheBackedJarFile(inputJar2, jar, this.installDir, fgFileNames);
                    }
                }
            }
        }
        this.cleanUpTempDirectory();
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.FILE_REVERT_COMPLETED);
    }
    
    public void compressEEARAfterReverting() {
        if (this.zipfg.size() != 0) {
            for (int s = this.zipfg.size(), a = 0; a < s; ++a) {
                ZipFileGroup zip = null;
                zip = this.zipfg.get(a);
                if (new File(this.installDir + File.separator + zip.getZipName()).isDirectory()) {
                    new File(this.installDir + File.separator + "eeartemp" + File.separator + zip.getZipName()).mkdirs();
                }
                if (!new File(this.installDir + File.separator + zip.getZipName()).isDirectory()) {
                    this.jarUpdater.compressUpdatedEEARFiles(this.installDir + File.separator + "eeartemp" + File.separator + zip.getZipName(), this.installDir + File.separator + zip.getZipName());
                }
                else {
                    try {
                        this.jarUpdater.revertEEARFiles(new File(this.installDir + File.separator + "eeartemp" + File.separator + zip.getZipName()), new File(this.installDir + File.separator + zip.getZipName()));
                    }
                    catch (final Exception e) {
                        ConsoleOut.println("Exception occured while reverting EEAR file : " + zip.getZipName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void revertTheBackedJarFile(final String backupJar, final String jarFileName, final String dir, final Vector fgFileNames) {
        final String installDir = dir;
        try {
            if (new File(backupJar).exists()) {
                final String message = "Going to revert the " + jarFileName + " contents.";
                this.logg.log(message);
                RevertPatch.out.info(message);
                final String path = installDir + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + jarFileName.substring(0, jarFileName.length() - 4);
                final Set<String> newlyAddedEntriesInJar = this.getNewlyAddedEntriesInJar(path, fgFileNames, backupJar);
                this.jarUpdater.addSkipJarEntries(newlyAddedEntriesInJar);
                this.jarUpdater.unzipTheRevertJarFile(backupJar, jarFileName, installDir);
                this.jarUpdater.jarUpdatedJar(jarFileName);
                this.jarUpdater.copyUpdatedJarFile(jarFileName, installDir + File.separator + jarFileName);
            }
            else {
                final String errMsg = CommonUtil.getString("The Jar File to be updated is not present") + jarFileName;
                this.logg.log(errMsg);
                RevertPatch.out.severe("ERR:" + errMsg);
                final File deleteJarFile = new File(installDir + File.separator + jarFileName);
                if (deleteJarFile.exists()) {
                    deleteJarFile.delete();
                }
            }
        }
        catch (final Exception ex) {
            RevertPatch.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while reverting the jar ") + jarFileName, ex);
            throw new IllegalArgumentException(ex);
        }
    }
    
    private Set<String> getNewlyAddedEntriesInJar(final String path, final Vector addedAndModifiedEntries, final String backupJarFilePath) throws IOException {
        final Set<String> newEntries = new TreeSet<String>();
        final ArrayList modifiedEntries = new ArrayList();
        final JarFile backupJarFile = new JarFile(backupJarFilePath);
        final Enumeration entries = backupJarFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry jarFileEntry = entries.nextElement();
            final String jarFileEntryName = jarFileEntry.getName();
            if (addedAndModifiedEntries.contains(jarFileEntryName)) {
                modifiedEntries.add(jarFileEntryName);
            }
        }
        for (int size = addedAndModifiedEntries.size(), i = 1; i < size; ++i) {
            final String fName = addedAndModifiedEntries.elementAt(i);
            if (!modifiedEntries.contains(fName)) {
                newEntries.add(fName);
            }
        }
        return newEntries;
    }
    
    public void deleteBackupDir() {
        final String installDir = this.common.getInstallationDirectory();
        final String dirName = installDir + File.separator + "Patch" + File.separator + this.versionToRevert;
        File versionDir = null;
        if (this.contextDir.equals("NoContext")) {
            versionDir = new File(dirName);
        }
        else {
            versionDir = new File(dirName + File.separator + this.contextDir);
        }
        this.showStatus("Deleting backup directory", "", 98);
        this.deleteDir(versionDir);
        this.cleanUpTempDirectory();
        this.showStatus("Cleaned up backup contents", "", 100);
    }
    
    private void addAllJarFilesOfFolderToList(final File folder, final ArrayList urls) throws MalformedURLException {
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
    
    private boolean contextPrePostUnInstallation(String classNameWithPackage, final String backUpDir, final Properties prop, final ArrayList dependentClassesList, final ArrayList classPathList, final int classIndex, final boolean isPreRevertClass) {
        classNameWithPackage = CommonUtil.convertfilenameToOsFilename(classNameWithPackage);
        final String className = classNameWithPackage.substring(classNameWithPackage.lastIndexOf(File.separator) + 1, classNameWithPackage.indexOf(".class"));
        try {
            final String dirToUnzip = this.common.getInstallationDirectory();
            final ArrayList classPathUrls = new ArrayList();
            if (dependentClassesList != null) {
                for (int i = 0; i < dependentClassesList.size(); ++i) {
                    final String dependentFile = dependentClassesList.get(i);
                    final File destFile = new File(backUpDir + dependentFile);
                    classPathUrls.add(destFile.toURI().toURL());
                }
            }
            boolean isJarsLoadedFromClasspathEntries = false;
            final Path classPathConf_Path = Paths.get(dirToUnzip, "conf", "classpath.conf");
            if (classPathConf_Path.toFile().exists()) {
                final Properties pathProperties = new Properties();
                try (final InputStream is = new FileInputStream(classPathConf_Path.toFile())) {
                    pathProperties.load(is);
                }
                for (final Object key : ((Hashtable<Object, V>)pathProperties).keySet()) {
                    final String dirName = pathProperties.getProperty((String)key);
                    final Path folderPath = Paths.get(dirToUnzip, dirName);
                    this.addAllJarFilesOfFolderToList(folderPath.toFile(), classPathUrls);
                }
            }
            else if (classPathList != null && !classPathList.isEmpty()) {
                isJarsLoadedFromClasspathEntries = true;
                for (final Object classPathEntry : classPathList) {
                    classPathUrls.add(Paths.get(dirToUnzip, (String)classPathEntry).toUri().toURL());
                }
            }
            if (!isJarsLoadedFromClasspathEntries) {
                final Path binFolderPath = Paths.get(dirToUnzip, "bin");
                this.addAllJarFilesOfFolderToList(binFolderPath.toFile(), classPathUrls);
            }
            final URL[] urlAry = new URL[classPathUrls.size()];
            for (int k = 0; k < classPathUrls.size(); ++k) {
                final URL url = classPathUrls.get(k);
                urlAry[k] = url;
                final URLConnection urlCon = url.openConnection();
                urlCon.setDefaultUseCaches(false);
            }
            URLClassLoader urlclsldr = null;
            String errorMessage = null;
            Object[] listOfFilesToModify = null;
            int type;
            boolean isFilesToBeBackedUp;
            try {
                urlclsldr = new URLClassLoader(urlAry);
                Thread.currentThread().setContextClassLoader(urlclsldr);
                classNameWithPackage = classNameWithPackage.replace(File.separator, ".");
                final Class prePostClass = urlclsldr.loadClass(classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf(".class")));
                final Object prePostObj = prePostClass.newInstance();
                Properties p = null;
                if (prop == null) {
                    p = new Properties();
                }
                else {
                    p = prop;
                }
                final String confProductName = this.common.getConfProductName();
                final String confProductVersion = this.common.getConfProductVersion();
                ((Hashtable<String, String>)p).put("product", confProductName);
                ((Hashtable<String, String>)p).put("version", confProductVersion);
                ((Hashtable<String, String>)p).put("home", this.installDir);
                ((Hashtable<String, String>)p).put("mode", String.valueOf(this.GUI));
                ((Hashtable<String, String>)p).put("patchversion", this.versionToRevert);
                ((Hashtable<String, String>)p).put("context", this.contextDir);
                if (this.previousVersion != null) {
                    ((Hashtable<String, String>)p).put("previousversion", this.previousVersion);
                }
                final Class[] constArgs = { p.getClass() };
                final Method init = prePostClass.getMethod("revert", (Class[])constArgs);
                final Object[] constArgsVal = { p };
                final Integer returnValue = (Integer)init.invoke(prePostObj, constArgsVal);
                type = returnValue;
                final Method gftm = prePostClass.getMethod("getFilesToModify", (Class[])null);
                listOfFilesToModify = (Object[])gftm.invoke(prePostObj, (Object[])null);
                final Method gem = prePostClass.getMethod("getErrorMsg", (Class[])null);
                errorMessage = (String)gem.invoke(prePostObj, (Object[])null);
                final Method isfbp = prePostClass.getMethod("isFilesToBeBackedUp", (Class[])null);
                final Boolean bol = (Boolean)isfbp.invoke(prePostObj, (Object[])null);
                isFilesToBeBackedUp = bol;
            }
            finally {
                if (urlclsldr != null) {
                    urlclsldr.close();
                    ClassLoaderUtil.unloadNativeLibraries();
                }
            }
            if (type == 1) {
                UpdateManager.getUpdateState().setCurrentClassStats(true, System.currentTimeMillis());
                this.revertBackupFiles(listOfFilesToModify, backUpDir + className + ".class", isFilesToBeBackedUp);
                return true;
            }
            if (type == 2) {
                UpdateManager.getUpdateState().setCurrentClassStats(false, System.currentTimeMillis());
                this.revertBackupFiles(listOfFilesToModify, backUpDir + className + ".class", isFilesToBeBackedUp);
                this.displayError(errorMessage);
                return false;
            }
            if (type == 3) {
                UpdateManager.getUpdateState().setCurrentClassStats(true, System.currentTimeMillis());
                this.revertBackupFiles(listOfFilesToModify, backUpDir + className + ".class", isFilesToBeBackedUp);
                return true;
            }
            if (type == 4) {
                UpdateManager.getUpdateState().setCurrentClassStats(true, System.currentTimeMillis());
                this.revertAllBackupFiles(listOfFilesToModify, this.postInstallArray, backUpDir, classIndex, isFilesToBeBackedUp);
                return true;
            }
            if (errorMessage != null) {
                this.displayError(errorMessage);
            }
            UpdateManager.getUpdateState().setCurrentClassStats(false, System.currentTimeMillis());
            return false;
        }
        catch (final Exception e) {
            e.printStackTrace();
            RevertPatch.out.log(Level.SEVERE, "ERR: " + CommonUtil.getString("Unexpected Error"));
            this.logg.fail(CommonUtil.getString("Unexpected Error"), e);
            this.displayError("Unexpected Error");
            return false;
        }
    }
    
    public boolean revertPostInvocationClasses(final int indx) {
        final String header = "Uninstalling post invocation classes";
        RevertPatch.out.info(CommonUtil.getString(header));
        final int size = indx;
        if (size == 0) {
            RevertPatch.out.info(CommonUtil.getString("No post invocation class files are present for uninstalling."));
            return true;
        }
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.POST_PROCESSOR_REVERT_EXECUTION_STARTED);
        int prg = 0;
        for (int i = size; i > 0; i -= 4) {
            this.showStatus(header, "", prg);
            try {
                final String installDir = this.common.getInstallationDirectory();
                String fileName = (String)this.postInstallArray[i - 4];
                UpdateManager.getUpdateState().setCurrentPrePostClassInProgress(fileName, System.currentTimeMillis());
                if (fileName == null || fileName.equals("")) {
                    UpdateManager.getUpdateState().setCurrentClassStats(false, System.currentTimeMillis());
                    return false;
                }
                RevertPatch.out.info(CommonUtil.getString("Executing class:") + fileName);
                final ArrayList depenList = (ArrayList)this.postInstallArray[i - 3];
                final ArrayList depenClassPath = (ArrayList)this.postInstallArray[i - 2];
                final Properties prop = (Properties)this.postInstallArray[i - 1];
                if (!this.contextDir.equals("NoContext") && fileName.startsWith(this.contextDir)) {
                    fileName = fileName.substring(this.contextDir.length() + 1);
                }
                String dir = installDir + File.separator + "Patch" + File.separator + this.versionToRevert + File.separator;
                if (!this.contextDir.equals("NoContext")) {
                    dir = dir + this.contextDir + File.separator;
                }
                dir = dir + "PostInstall" + File.separator;
                final boolean postSuccess = this.contextPrePostUnInstallation(fileName, dir, prop, depenList, depenClassPath, i - 4, false);
                if (!postSuccess) {
                    UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.POST_PROCESSOR_REVERT_EXECUTION_FAILED);
                    return false;
                }
            }
            catch (final Exception e) {
                RevertPatch.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception in post invocation."), e);
                return false;
            }
            prg = 45 * (size - i + 4) / size;
            this.showStatus(header, "", prg);
        }
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.POST_PROCESSOR_REVERT_EXECUTION_COMPLETED);
        return true;
    }
    
    public boolean revertPostInvocationClasses() {
        return this.revertPostInvocationClasses(this.postInstallArray.length);
    }
    
    public boolean revertPreInvocationClasses(final int indx) {
        final String header = "Uninstalling pre invocation classes";
        RevertPatch.out.info(CommonUtil.getString(header));
        final int size = indx;
        if (size == 0) {
            RevertPatch.out.info(CommonUtil.getString("No pre invocation class files are present for uninstalling."));
            return true;
        }
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.PRE_PROCESSOR_REVERT_EXECUTION_STARTED);
        int prg = 71;
        for (int i = size; i > 0; i -= 4) {
            this.showStatus(header, "", prg);
            try {
                final String installDir = this.common.getInstallationDirectory();
                String fileName = (String)this.preInstallArray[i - 4];
                UpdateManager.getUpdateState().setCurrentPrePostClassInProgress(fileName, System.currentTimeMillis());
                RevertPatch.out.info(CommonUtil.getString("Executing class:") + fileName);
                final ArrayList depenList = (ArrayList)this.preInstallArray[i - 3];
                final ArrayList depenClassPath = (ArrayList)this.preInstallArray[i - 2];
                final Properties prop = (Properties)this.preInstallArray[i - 1];
                if (!this.contextDir.equals("NoContext") && fileName.startsWith(this.contextDir)) {
                    fileName = fileName.substring(this.contextDir.length() + 1);
                }
                String dir = installDir + File.separator + "Patch" + File.separator + this.versionToRevert + File.separator;
                if (!this.contextDir.equals("NoContext")) {
                    dir = dir + this.contextDir + File.separator;
                }
                dir = dir + "PreInstall" + File.separator;
                final boolean preSuccess = this.contextPrePostUnInstallation(fileName, dir, prop, depenList, depenClassPath, i - 4, true);
                if (!preSuccess) {
                    UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.PRE_PROCESSOR_REVERT_EXECUTION_FAILED);
                    return false;
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                this.logg.fail(CommonUtil.getString("Unexpected Error"), e);
                RevertPatch.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Unexpected Error"));
                this.displayError("Unexpected Error");
                return false;
            }
            prg = 70 + 20 * (size - i + 4) / size;
            this.showStatus(header, "", prg);
        }
        UpgradeUtil.notifyStatus(this.patchFilePath, this.infXmlData, PatchInstallationState.PRE_PROCESSOR_REVERT_EXECUTION_COMPLETED);
        return true;
    }
    
    public boolean revertPreInvocationClasses() {
        return this.revertPreInvocationClasses(this.preInstallArray.length);
    }
    
    private void revertAllBackupFiles(final Object[] listOfFilesToModify, final Object[] prepostArray, final String dir, final int classIndex, final boolean isFilesToBeBackedUp) {
        final Object[] obj = prepostArray;
        final int size = obj.length;
        int i;
        for (int len = i = prepostArray.length; i > classIndex; i -= 4) {
            try {
                String fileName = null;
                fileName = (String)obj[i - 4];
                if (File.separator.equals("/")) {
                    fileName = fileName.replace('\\', '/');
                }
                else {
                    fileName = fileName.replace('/', '\\');
                }
                final int lastIndex = fileName.lastIndexOf(File.separator);
                final String backupDirName = fileName.substring(lastIndex + 1);
                this.revertBackupFiles(listOfFilesToModify, dir + backupDirName, isFilesToBeBackedUp);
            }
            catch (final Exception exp) {
                final String errMsg = CommonUtil.getString("Exception while reverting back up files.");
                RevertPatch.out.log(Level.SEVERE, "ERR:" + errMsg, exp);
                this.logg.fail(errMsg, exp);
                return;
            }
        }
    }
    
    private void revertBackupFiles(final Object[] listOfFilesToModify, final String backupDir, final boolean isFilesToBeBackedUp) {
        if (!isFilesToBeBackedUp) {
            return;
        }
        if (listOfFilesToModify != null) {
            for (int i = 0; i < listOfFilesToModify.length; ++i) {
                try {
                    final String fileName = (String)listOfFilesToModify[i];
                    final String installDir = this.common.getInstallationDirectory();
                    final File file = new File(backupDir + File.separator + fileName);
                    if (file.exists()) {
                        final FileInputStream input = new FileInputStream(file);
                        this.writeFile(input, installDir + File.separator + fileName);
                    }
                }
                catch (final Exception e) {
                    final String errMsg = CommonUtil.getString("Exception while taking backup for pre invokation classes");
                    this.logg.fail(errMsg, e);
                    RevertPatch.out.log(Level.SEVERE, "ERR:" + errMsg, e);
                }
            }
            if (new File(backupDir).exists()) {
                CommonUtil.deleteFiles(backupDir);
            }
        }
    }
    
    public boolean readInfoFile(final String version, final String contextVersion, final LoggingUtil logg) {
        this.logg = logg;
        this.versionToRevert = version;
        final String installDir = this.common.getInstallationDirectory();
        final String dirName = installDir + File.separator + "Patch" + File.separator + this.versionToRevert;
        this.contextDir = contextVersion;
        final String specsPath = installDir + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        if (new File(specsPath).exists() && vProfile.getTheVersions() != null) {
            vProfile.readDocument(specsPath, false, false);
            String patchType = vProfile.getTheAdditionalDetail(this.versionToRevert, "Type");
            if (patchType == null || patchType.trim().equals("")) {
                patchType = "SP";
            }
            if (patchType.equals("SP")) {
                final String[] versionArray = vProfile.getTheVersions();
                final int len = versionArray.length;
                if (len == 1) {
                    this.previousVersion = "BaseVersion";
                }
                else {
                    this.previousVersion = versionArray[len - 2];
                }
            }
            final String patchName = vProfile.getTheAdditionalDetail(this.versionToRevert, "PatchName");
            if (patchName != null) {
                this.patchFilePath = Paths.get(installDir, "Patch", patchName);
            }
        }
        if (this.patchFilePath == null) {
            this.patchFilePath = Paths.get(this.common.getPatchFileNamePath(), new String[0]);
        }
        try {
            final String temp = dirName + File.separator + "inf.xml";
            if (!new File(temp).exists()) {
                return false;
            }
            final XmlParser xmlParser = new XmlParser(temp);
            this.infXmlData = xmlParser.getXmlData();
            final UpdateData updateData = this.infXmlData.getContextTable().get(this.contextDir);
            this.fileGrpVector = updateData.getContextVector();
            this.zipfg = updateData.getZipFileGroup();
            this.preInstallArray = updateData.getPreInstallArray();
            this.postInstallArray = updateData.getPostInstallArray();
            this.deletedFiles = updateData.getFilesMarkedForDelete();
            this.deletedJarEntries = updateData.getAllJarEntriesMarkedForDelete();
        }
        catch (final Exception ex) {
            final String errMsg = CommonUtil.getString("Exception in reading inf file");
            logg.fail(errMsg, ex);
            RevertPatch.out.log(Level.SEVERE, "ERR:" + errMsg, ex);
            UpdateManagerUtil.setExitStatus(1);
            return false;
        }
        return true;
    }
    
    private void overWriteFiles(final String source, final String dest) {
        final String installDir = this.common.getInstallationDirectory();
        FileInputStream readFile = null;
        try {
            String patchFilePath = null;
            String destFilePath = null;
            String fileName = null;
            if (this.contextDir.equals("NoContext")) {
                fileName = dest;
            }
            else {
                fileName = this.contextDir + File.separator + dest;
            }
            patchFilePath = source + File.separator + fileName;
            destFilePath = installDir + File.separator + dest;
            final File backupFile = new File(patchFilePath);
            if (FileUtil.isFileExists(backupFile, backupFile.getName())) {
                if (backupFile.isDirectory()) {
                    this.copyFolder(Paths.get(source, fileName).toFile(), Paths.get(installDir, dest).toFile());
                }
                else {
                    readFile = new FileInputStream(patchFilePath);
                    this.writeFile(readFile, destFilePath);
                }
            }
            else {
                final File fileToDelete = new File(installDir + File.separator + dest);
                if (FileUtil.isFileExists(fileToDelete, fileToDelete.getName())) {
                    Files.delete(fileToDelete.toPath());
                    final String msg = "Deleted newly added file : " + fileToDelete.getName();
                    RevertPatch.out.info(msg);
                    this.deleteEmptyDirs(fileToDelete.getParentFile());
                }
                else {
                    RevertPatch.out.severe("File not exists in backupdir as well as in product's home directory.");
                }
            }
        }
        catch (final Exception exce) {
            RevertPatch.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while overwriting files."), exce);
            if (readFile != null) {
                try {
                    readFile.close();
                }
                catch (final IOException ex) {
                    RevertPatch.out.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        finally {
            if (readFile != null) {
                try {
                    readFile.close();
                }
                catch (final IOException ex2) {
                    RevertPatch.out.log(Level.SEVERE, ex2.getMessage(), ex2);
                }
            }
        }
    }
    
    public void deleteEmptyDirs(final File dirToDel) {
        final int len = dirToDel.listFiles().length;
        if (len == 0) {
            dirToDel.delete();
            final String parent = dirToDel.getParent();
            final File parentFile = new File(parent);
            this.deleteEmptyDirs(parentFile);
        }
    }
    
    public void deleteDir(final File dirToDel) {
        if (dirToDel.isDirectory()) {
            for (final File file : dirToDel.listFiles()) {
                if (file.isDirectory()) {
                    this.deleteDir(file);
                }
                else if (!file.delete()) {
                    file.deleteOnExit();
                }
            }
        }
        if (!dirToDel.delete()) {
            dirToDel.deleteOnExit();
        }
    }
    
    private void copyFolder(final File sourceFolder, final File destinationFolder) throws IOException {
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        final String[] arr$;
        final String[] sourceFiles = arr$ = sourceFolder.list();
        for (final String sourceFile : arr$) {
            final File scrFile = new File(sourceFolder, sourceFile);
            final File destFile = new File(destinationFolder, sourceFile);
            if (scrFile.isDirectory()) {
                this.copyFolder(scrFile, destFile);
            }
            else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(scrFile);
                    this.writeFile(fis, destFile.getCanonicalPath());
                }
                finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
        }
    }
    
    private void writeFile(final InputStream in, final String path) {
        try {
            final int BUFFER = 10240;
            final byte[] data = new byte[10240];
            final File f = new File(path);
            if (!f.exists()) {
                CommonUtil.createAllSubDirectories(path);
            }
            try (final FileOutputStream fos = new FileOutputStream(f);
                 final BufferedInputStream bis = new BufferedInputStream(in, 10240)) {
                int count;
                while ((count = bis.read(data, 0, 10240)) != -1) {
                    fos.write(data, 0, count);
                }
            }
            if (!FileUtil.isFileExists(f, f.getName())) {
                for (final File existingFile : f.getParentFile().listFiles()) {
                    if (existingFile.isFile() && existingFile.getName().equalsIgnoreCase(f.getName())) {
                        existingFile.renameTo(f);
                    }
                }
            }
        }
        catch (final Exception ex) {
            RevertPatch.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while writing file."), ex);
        }
    }
    
    private void showStatus(final String status, final String msg, final int percentage) {
        if (this.GUI) {
            UpdateManagerUtil.updateRevertUIProgress(percentage, msg, status);
        }
        else {
            UpdateManagerUtil.updateProgress(percentage, msg, status);
        }
    }
    
    private void updateFailureProgress(final String fileName, final long fileSize, final String status) {
        if (!this.GUI) {
            UpdateManagerUtil.updateFailureProgress(fileName, status);
        }
    }
    
    private void displayError(final String errorMessage) {
        UpdateManagerUtil.setExitStatus(1);
        if (this.GUI) {
            UpdateManagerUtil.setRevertState(2);
            final String newerrMsg = UpdateManagerUtil.getNewText(CommonUtil.getString(errorMessage), 4, "red");
            UpdateManagerUtil.setRevertCorruptMainLabelMessage(newerrMsg, " ");
        }
        else {
            ConsoleOut.println("\n" + CommonUtil.getString(errorMessage));
        }
    }
    
    public void setFileGrpVector(final Vector vec) {
        this.fileGrpVector = vec;
    }
    
    protected void setDeletedFiles(final Set<String> deletedFiles) {
        this.deletedFiles = deletedFiles;
    }
    
    private void cleanUpTempDirectory() {
        try {
            FileUtil.deleteFiles(Paths.get(this.installDir, "patchtemp").toString(), Paths.get(this.installDir, "eeartemp").toString());
        }
        catch (final RuntimeException | IOException rte) {
            RevertPatch.out.log(Level.SEVERE, "Ignoring the problem, while deleting the patchtemp/eeartemp directory.", rte);
        }
    }
    
    static {
        RevertPatch.out = Logger.getLogger(RevertPatch.class.getName());
    }
}
