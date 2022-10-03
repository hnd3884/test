package com.adventnet.tools.update.installer;

import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.util.jar.JarFile;
import java.nio.file.Paths;
import com.adventnet.tools.update.FileGroup;
import com.adventnet.tools.update.ZipFileGroup;
import com.adventnet.tools.update.UpdateData;
import com.adventnet.tools.update.XmlParser;
import java.util.logging.Level;
import com.adventnet.tools.update.CommonUtil;
import java.io.File;
import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class Backup
{
    private static Logger logOut;
    private String patchVersion;
    private String dirToUnzip;
    private String patchDir;
    private String currentVersion;
    private String patchReadme;
    private String contextReadme;
    private Vector fileGrpVector;
    private ArrayList zipfg;
    private Object[] preInstallArray;
    private Object[] postInstallArray;
    private Hashtable<String, Set<String>> jarEntriesToBeDeleted;
    private Set<String> filesToBeDeleted;
    private String contextDir;
    private boolean GUI;
    private LoggingUtil logg;
    
    public Backup(final String dirName, final String contextName, final boolean GUI, final LoggingUtil logg) {
        this.patchVersion = null;
        this.dirToUnzip = null;
        this.patchDir = null;
        this.currentVersion = null;
        this.patchReadme = null;
        this.contextReadme = null;
        this.fileGrpVector = null;
        this.zipfg = null;
        this.preInstallArray = null;
        this.postInstallArray = null;
        this.jarEntriesToBeDeleted = null;
        this.filesToBeDeleted = null;
        this.contextDir = null;
        this.logg = null;
        if (dirName.equals("")) {
            this.dirToUnzip = System.getProperty("user.dir");
        }
        else {
            this.dirToUnzip = dirName;
        }
        this.GUI = GUI;
        this.logg = logg;
        this.contextDir = contextName;
    }
    
    public void createInfoForLastVersion() {
        try {
            final String patchDir = this.dirToUnzip + File.separator + "Patch" + File.separator + this.currentVersion + File.separator;
            new File(patchDir).mkdir();
            final String temp = this.dirToUnzip + File.separator + "Patch" + File.separator + "inf.xml";
            this.copyFile(temp, patchDir + "inf.xml");
        }
        catch (final Exception e) {
            final String errMsg = CommonUtil.getString("Exception while copying inf.xml file.");
            Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg, e);
        }
    }
    
    public void readInfoInPatchFile() {
        try {
            final String temp = this.dirToUnzip + File.separator + "Patch" + File.separator + "inf.xml";
            final XmlParser xmlParser = new XmlParser(temp);
            this.patchVersion = xmlParser.getXmlData().getPatchVersion();
            this.patchReadme = xmlParser.getXmlData().getPatchReadme();
            final UpdateData updateData = xmlParser.getXmlData().getContextTable().get(this.contextDir);
            this.fileGrpVector = updateData.getContextVector();
            this.zipfg = updateData.getZipFileGroup();
            this.preInstallArray = updateData.getPreInstallArray();
            this.postInstallArray = updateData.getPostInstallArray();
            this.contextReadme = updateData.getContextReadme();
            this.filesToBeDeleted = updateData.getFilesMarkedForDelete();
            this.jarEntriesToBeDeleted = updateData.getAllJarEntriesMarkedForDelete();
            this.currentVersion = xmlParser.getXmlData().getPatchVersion();
        }
        catch (final Exception execp) {
            final String errMsg = CommonUtil.getString("Exception occured in reading the xml file during backing up:");
            Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg, execp);
        }
    }
    
    public void moveFilesToDelete() throws Exception {
        try {
            final StringBuffer dir = new StringBuffer();
            dir.append(this.dirToUnzip);
            dir.append(File.separator);
            dir.append("Patch");
            dir.append(File.separator);
            dir.append(this.currentVersion);
            dir.append(File.separator);
            this.patchDir = dir.toString();
            String contextPatchDir = null;
            if (this.contextDir.equals("NoContext")) {
                contextPatchDir = this.patchDir;
            }
            else {
                contextPatchDir = this.patchDir + this.contextDir + File.separator;
            }
            new File(contextPatchDir).mkdir();
            this.remove(this.fileGrpVector, contextPatchDir, this.zipfg);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private void remove(final Vector fileGrpVector, final String dirName, final ArrayList ZipFileGrp) throws Exception {
        final File destDir = new File(dirName);
        if (ZipFileGrp.size() != 0) {
            for (int s = ZipFileGrp.size(), a = 0; a < s; ++a) {
                ZipFileGroup zip = new ZipFileGroup();
                zip = this.zipfg.get(a);
                for (int c = 0; c < zip.getFilesList().size(); ++c) {
                    try {
                        String archiveName = zip.getFilesList().get(c);
                        archiveName = CommonUtil.convertfilenameToOsFilename(archiveName);
                        if (!this.contextDir.equals("NoContext") && archiveName.startsWith(this.contextDir)) {
                            archiveName = archiveName.substring(this.contextDir.length() + 1);
                        }
                        final StringBuffer srcBuffer = new StringBuffer();
                        srcBuffer.append(this.dirToUnzip);
                        srcBuffer.append(File.separator);
                        srcBuffer.append("eeartemp");
                        srcBuffer.append(File.separator);
                        srcBuffer.append(archiveName);
                        final StringBuffer destBuffer = new StringBuffer();
                        destBuffer.append(destDir);
                        destBuffer.append(File.separator);
                        destBuffer.append(archiveName);
                        this.copyFile(srcBuffer.toString(), destBuffer.toString());
                    }
                    catch (final Exception e) {
                        final String errMsg = CommonUtil.getString("Exception while taking bakup of jar files");
                        Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg, e);
                        this.logg.fail(errMsg, e);
                        throw e;
                    }
                }
            }
        }
        for (int size = fileGrpVector.size(), i = 0; i < size; ++i) {
            final FileGroup filgrp = fileGrpVector.elementAt(i);
            final Vector fgJarNames = filgrp.getJarNameVector();
            final Vector fgFileNames = filgrp.getFileNameVector();
            final int fgJarSize = fgJarNames.size();
            if (fgJarSize == 0) {
                for (int fgFileSize = fgFileNames.size(), j = 0; j < fgFileSize; ++j) {
                    try {
                        String fileName = fgFileNames.elementAt(j);
                        final String displayFileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                        final int prg = 24 + 4 * (i + 1) / size * (j + 1) / fgFileSize;
                        this.showStatus("Backing up the files to get modified", displayFileName, prg);
                        fileName = CommonUtil.convertfilenameToOsFilename(fileName);
                        if (!this.contextDir.equals("NoContext") && fileName.startsWith(this.contextDir)) {
                            fileName = fileName.substring(this.contextDir.length() + 1);
                        }
                        final File srcFile = Paths.get(this.dirToUnzip, fileName).toFile();
                        if (this.fileExistsCaseSensative(srcFile, srcFile.getName())) {
                            final StringBuffer srcBuffer2 = new StringBuffer();
                            srcBuffer2.append(this.dirToUnzip);
                            srcBuffer2.append(File.separator);
                            srcBuffer2.append(fileName);
                            final StringBuffer destBuffer2 = new StringBuffer();
                            destBuffer2.append(destDir);
                            destBuffer2.append(File.separator);
                            destBuffer2.append(fileName);
                            this.copyFile(srcBuffer2.toString(), destBuffer2.toString());
                        }
                    }
                    catch (final Exception e2) {
                        final String errMsg2 = CommonUtil.getString("Exception occured while moving the file:");
                        Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg2, e2);
                        throw e2;
                    }
                }
            }
            else {
                for (int jarSize = fgJarNames.size(), k = 0; k < jarSize; ++k) {
                    try {
                        String jarName = fgJarNames.elementAt(k);
                        final String displayJarName = jarName.substring(jarName.lastIndexOf(File.separator) + 1);
                        final int prg = 20 + 4 * (i + 1) / size * (k + 1) / jarSize;
                        this.showStatus("Backing up the files to get modified", displayJarName, prg);
                        if (!this.contextDir.equals("NoContext") && jarName.startsWith(this.contextDir)) {
                            jarName = jarName.substring(this.contextDir.length() + 1);
                        }
                        jarName = CommonUtil.convertfilenameToOsFilename(jarName);
                        final File srcJarFile = Paths.get(this.dirToUnzip, jarName).toFile();
                        if (this.fileExistsCaseSensative(srcJarFile, srcJarFile.getName())) {
                            final JarFile jarZip = new JarFile(this.dirToUnzip + File.separator + jarName);
                            final String destination = destDir + File.separator + jarName;
                            CommonUtil.createAllSubDirectories(destination);
                            final JarOutputStream out = new JarOutputStream(new FileOutputStream(destination));
                            ZipEntry jarEntry = null;
                            for (int fsize = fgFileNames.size(), l = 0; l < fsize; ++l) {
                                String ujarFileName = fgFileNames.elementAt(l);
                                if (!this.contextDir.equals("NoContext") && ujarFileName.startsWith(this.contextDir)) {
                                    ujarFileName = ujarFileName.substring(this.contextDir.length() + 1);
                                }
                                final String name = ujarFileName.replace('\\', '/');
                                jarEntry = jarZip.getEntry(name);
                                if (jarEntry != null) {
                                    final InputStream jarInputStream = jarZip.getInputStream(jarEntry);
                                    final int BUFFER = 10240;
                                    final byte[] data = new byte[10240];
                                    final BufferedInputStream origin = new BufferedInputStream(jarInputStream, 10240);
                                    final ZipEntry entry = new ZipEntry(name);
                                    out.putNextEntry(entry);
                                    int count;
                                    while ((count = origin.read(data, 0, 10240)) != -1) {
                                        out.write(data, 0, count);
                                    }
                                    origin.close();
                                    jarInputStream.close();
                                }
                            }
                            final Set<String> deletedFiles = filgrp.getDeletedFiles();
                            for (final String fileName2 : deletedFiles) {
                                final String name2 = fileName2.replace("\\", "/");
                                jarEntry = jarZip.getEntry(name2);
                                if (jarEntry == null) {
                                    continue;
                                }
                                InputStream jarInputStream2 = null;
                                BufferedInputStream origin2 = null;
                                try {
                                    jarInputStream2 = jarZip.getInputStream(jarEntry);
                                    final int BUFFER2 = 10240;
                                    final byte[] data2 = new byte[10240];
                                    origin2 = new BufferedInputStream(jarInputStream2, 10240);
                                    final ZipEntry entry2 = new ZipEntry(name2);
                                    out.putNextEntry(entry2);
                                    int count2;
                                    while ((count2 = origin2.read(data2, 0, 10240)) != -1) {
                                        out.write(data2, 0, count2);
                                    }
                                }
                                finally {
                                    if (origin2 != null) {
                                        origin2.close();
                                    }
                                    if (jarInputStream2 != null) {
                                        jarInputStream2.close();
                                    }
                                }
                            }
                            out.close();
                        }
                        else {
                            final String errMsg3 = CommonUtil.getString("The Jar File to be updated is not present");
                            Backup.logOut.severe("ERR:" + errMsg3 + jarName);
                            this.logg.log(errMsg3 + jarName);
                        }
                    }
                    catch (final Exception e2) {
                        final String errMsg2 = CommonUtil.getString("Exception while taking bakup of jar files");
                        Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg2, e2);
                        this.logg.fail(errMsg2, e2);
                        throw e2;
                    }
                }
            }
        }
        final Set<String> jarNamesContainingDeleteEntries = this.jarEntriesToBeDeleted.keySet();
        for (final String jarName2 : jarNamesContainingDeleteEntries) {
            final File srcJarFile2 = Paths.get(this.dirToUnzip, jarName2).toFile();
            if (this.fileExistsCaseSensative(srcJarFile2, srcJarFile2.getName())) {
                final JarFile jarZip2 = new JarFile(this.dirToUnzip + File.separator + jarName2);
                final String destination2 = destDir + File.separator + jarName2;
                CommonUtil.createAllSubDirectories(destination2);
                final JarOutputStream out2 = new JarOutputStream(new FileOutputStream(destination2));
                ZipEntry jarEntry2 = null;
                final Set<String> deletedFiles2 = this.jarEntriesToBeDeleted.get(jarName2);
                for (final String fileName3 : deletedFiles2) {
                    final String name3 = fileName3.replace("\\", "/");
                    jarEntry2 = jarZip2.getEntry(name3);
                    if (jarEntry2 == null) {
                        continue;
                    }
                    InputStream jarInputStream3 = null;
                    BufferedInputStream origin3 = null;
                    try {
                        jarInputStream3 = jarZip2.getInputStream(jarEntry2);
                        final int BUFFER3 = 10240;
                        final byte[] data3 = new byte[10240];
                        origin3 = new BufferedInputStream(jarInputStream3, 10240);
                        final ZipEntry entry3 = new ZipEntry(name3);
                        out2.putNextEntry(entry3);
                        int count3;
                        while ((count3 = origin3.read(data3, 0, 10240)) != -1) {
                            out2.write(data3, 0, count3);
                        }
                    }
                    finally {
                        if (origin3 != null) {
                            origin3.close();
                        }
                        if (jarInputStream3 != null) {
                            jarInputStream3.close();
                        }
                    }
                }
                out2.close();
            }
        }
        int fileIndex = 0;
        for (String fileName4 : this.filesToBeDeleted) {
            try {
                fileName4 = CommonUtil.convertfilenameToOsFilename(fileName4);
                if (!this.contextDir.equals("NoContext") && fileName4.startsWith(this.contextDir)) {
                    fileName4 = fileName4.substring(this.contextDir.length() + 1);
                }
                final File srcFile2 = Paths.get(this.dirToUnzip, fileName4).toFile();
                if (!this.fileExistsCaseSensative(srcFile2, srcFile2.getName())) {
                    continue;
                }
                final int prg2 = 28 + 2 * (fileIndex + 1) / this.filesToBeDeleted.size();
                if (srcFile2.isDirectory()) {
                    this.showStatus("Backing up the folder to be removed", fileName4, prg2);
                    final File sourceFolder = Paths.get(this.dirToUnzip, fileName4).toFile();
                    final File destinationFolder = Paths.get(destDir.getPath(), fileName4).toFile();
                    this.copyFolder(sourceFolder, destinationFolder);
                }
                else {
                    final String displayFileName2 = fileName4.substring(fileName4.lastIndexOf(File.separator) + 1);
                    this.showStatus("Backing up the files to be removed", displayFileName2, prg2);
                    final StringBuilder srcBuffer3 = new StringBuilder();
                    srcBuffer3.append(this.dirToUnzip);
                    srcBuffer3.append(File.separator);
                    srcBuffer3.append(fileName4);
                    final StringBuilder destBuffer3 = new StringBuilder();
                    destBuffer3.append(destDir);
                    destBuffer3.append(File.separator);
                    destBuffer3.append(fileName4);
                    this.copyFile(srcBuffer3.toString(), destBuffer3.toString());
                }
            }
            catch (final Exception e3) {
                final String errMsg4 = CommonUtil.getString("Exception occured while moving the file:");
                Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg4, e3);
                throw e3;
            }
            ++fileIndex;
        }
    }
    
    private boolean fileExistsCaseSensative(final File f, final String fileNameInCaseSensitive) {
        if (f.exists()) {
            for (final File f2 : f.getParentFile().listFiles()) {
                if (f2.getName().equals(fileNameInCaseSensitive)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getCurrentVersion() {
        return this.currentVersion;
    }
    
    public String getPatchVersion() {
        return this.patchVersion;
    }
    
    public String getPatchReadme() {
        return this.patchReadme;
    }
    
    public String getContextReadme() {
        return this.contextReadme;
    }
    
    public Vector getFileGroupVector() {
        return this.fileGrpVector;
    }
    
    public ArrayList getZipGroupVector() {
        return this.zipfg;
    }
    
    public Object[] getPreInstallArray() {
        return this.preInstallArray;
    }
    
    public Object[] getPostInstallArray() {
        return this.postInstallArray;
    }
    
    public Set<String> getFilesToBeDeleted() {
        return this.filesToBeDeleted;
    }
    
    public Hashtable<String, Set<String>> getJarEntriesToBeDeleted() {
        return this.jarEntriesToBeDeleted;
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
                this.copyFile(scrFile.getCanonicalPath(), destFile.getCanonicalPath());
            }
        }
    }
    
    private void copyFile(final String in, final String out) throws IOException {
        try {
            final int BUFFER = 10240;
            final byte[] data = new byte[10240];
            final File file = new File(in);
            if (file.exists()) {
                CommonUtil.createAllSubDirectories(out);
                try (final FileInputStream input = new FileInputStream(in);
                     final FileOutputStream output = new FileOutputStream(out);
                     final BufferedInputStream binput = new BufferedInputStream(input, 10240)) {
                    int count;
                    while ((count = binput.read(data, 0, 10240)) != -1) {
                        output.write(data, 0, count);
                    }
                }
            }
        }
        catch (final IOException ex) {
            final String errMsg = CommonUtil.getString("Exception while taking backup of \"" + in + "\" file.");
            Backup.logOut.log(Level.SEVERE, "ERR:" + errMsg, ex);
            throw ex;
        }
    }
    
    private void showStatus(final String status, final String msg, final int percentage) {
        if (this.GUI) {
            UpdateManagerUtil.updateInstallUIProgress(percentage, msg, status);
        }
        else {
            UpdateManagerUtil.updateProgress(percentage, msg, status);
        }
    }
    
    static {
        Backup.logOut = Logger.getLogger(Backup.class.getName());
    }
}
