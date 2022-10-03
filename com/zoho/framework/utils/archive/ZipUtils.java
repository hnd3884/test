package com.zoho.framework.utils.archive;

import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Set;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.util.zip.ZipOutputStream;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import com.zoho.framework.utils.FileNameFilter;
import java.util.zip.ZipInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.List;
import java.util.logging.Logger;

public class ZipUtils
{
    private static final Logger LOGGER;
    private static final int BUFFER = 2048;
    
    public static void unZip(final String zipFile, final String destinationDirectory) throws IOException {
        unZip(zipFile, destinationDirectory, null, null);
    }
    
    public static void unZip(final String zipFile, final String destinationDirectory, final String fileToBeExtracted, final List<Pattern> excludePatterns) throws IOException {
        final File destiDir = new File(destinationDirectory);
        ZipUtils.LOGGER.log(Level.INFO, "Entered unZip :: zipFile :: [{0}], destinationDirectory :: [{1}], fileToBeExtracted :: [{2}]", new Object[] { zipFile, destinationDirectory, fileToBeExtracted });
        final String folderToBeExtracted = (fileToBeExtracted == null) ? null : (fileToBeExtracted.endsWith("/") ? fileToBeExtracted : (fileToBeExtracted + "/"));
        BufferedOutputStream dest = null;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final byte[] data = new byte[2048];
                final String entryName = entry.getName();
                if (excludePatterns != null && FileNameFilter.matches(excludePatterns, entryName)) {
                    continue;
                }
                final File destname = new File(destiDir, entry.getName());
                if (!destname.getCanonicalPath().startsWith(destiDir.getCanonicalPath() + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }
                if (!entry.isDirectory()) {
                    if (fileToBeExtracted != null && !entry.getName().startsWith(folderToBeExtracted) && !entry.getName().equals(fileToBeExtracted)) {
                        continue;
                    }
                    final File parentFolder = destname.getParentFile();
                    if (!parentFolder.exists()) {
                        parentFolder.mkdirs();
                    }
                    try {
                        fos = new FileOutputStream(destname);
                        dest = new BufferedOutputStream(fos, 2048);
                        int count;
                        while ((count = zis.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                    }
                    finally {
                        dest.close();
                    }
                }
                else {
                    if (fileToBeExtracted != null && !entry.getName().startsWith(fileToBeExtracted)) {
                        continue;
                    }
                    destname.mkdirs();
                }
            }
            zis.close();
            ZipUtils.LOGGER.log(Level.INFO, "Unzipping successfully completed ...");
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (dest != null) {
                    dest.close();
                }
                if (zis != null) {
                    zis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                ZipUtils.LOGGER.log(Level.INFO, "{0}", e);
            }
        }
    }
    
    public static boolean isFileExistsInZip(final String zipNameWithFullPath, final String entryNameWithPackage) throws IOException {
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipNameWithFullPath);
            return zip.getEntry(entryNameWithPackage) != null;
        }
        finally {
            if (zip != null) {
                zip.close();
            }
        }
    }
    
    public static File extractFile(final String zipFileName, final String fileNameWithPath, final String destinationFolderWithAbsolutePath) throws IOException {
        ZipUtils.LOGGER.log(Level.INFO, "extractFile :: zipFileName :: [{0}], fileNameWithPath :: [{1}]", new Object[] { zipFileName, fileNameWithPath });
        ZipFile zip = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        File destinationFile = null;
        try {
            zip = new ZipFile(zipFileName);
            final ZipEntry ze = zip.getEntry(fileNameWithPath);
            destinationFile = new File(destinationFolderWithAbsolutePath, fileNameWithPath);
            final File parentFolder = destinationFile.getParentFile();
            parentFolder.mkdirs();
            fos = new FileOutputStream(new File(destinationFolderWithAbsolutePath, fileNameWithPath));
            bis = new BufferedInputStream(zip.getInputStream(ze));
            final byte[] data = new byte[2048];
            int count = 0;
            while ((count = bis.read(data, 0, 2048)) > 0) {
                fos.write(data, 0, count);
            }
            fos.flush();
        }
        finally {
            if (fos != null) {
                fos.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (zip != null) {
                zip.close();
            }
        }
        ZipUtils.LOGGER.log(Level.INFO, "Returning from extractFile method for the zip :: [{0}]", zipFileName);
        return destinationFile;
    }
    
    public static void zip(final File zipFolder, final String zipFileName, final Map<String, List<File>> dirVsFiles) throws IOException {
        ZipUtils.LOGGER.log(Level.INFO, "Entered createBackUpZip(), zipFolder :: [{0}], zipFileName :: [{1}], dirVsFiles :: [{2}]", new Object[] { zipFolder, zipFileName, dirVsFiles });
        if (!zipFolder.exists()) {
            zipFolder.mkdirs();
        }
        final BufferedInputStream buffInputStream = null;
        FileOutputStream dest = null;
        ZipOutputStream zOut = null;
        final FileInputStream fi = null;
        try {
            dest = new FileOutputStream(zipFolder + File.separator + zipFileName, true);
            zOut = new ZipOutputStream(new BufferedOutputStream(dest));
            addFilesToZip(zOut, dirVsFiles);
            ZipUtils.LOGGER.log(Level.INFO, "Finished writing the zip file :: [{0}]", zipFileName);
            zOut.finish();
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        catch (final IOException e2) {
            e2.printStackTrace();
            throw e2;
        }
        finally {
            if (buffInputStream != null) {
                try {
                    buffInputStream.close();
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
            if (zOut != null) {
                try {
                    zOut.close();
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
            if (dest != null) {
                try {
                    dest.close();
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
            if (fi != null) {
                try {
                    fi.close();
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    public static void appendInZip(final String zipFileName, final Map<String, List<File>> dirVsFiles) throws IOException {
        ZipUtils.LOGGER.log(Level.INFO, "Entered appendInBackupZip :: zipFileName :: [{0}], dirVsFiles :: {1}", new Object[] { zipFileName, dirVsFiles });
        final File sourceZipFile = new File(zipFileName);
        if (!sourceZipFile.exists()) {
            throw new FileNotFoundException("ZIP File not found at :: [" + sourceZipFile.getAbsolutePath() + "]");
        }
        final File tempZip = File.createTempFile("temp.zip", null, sourceZipFile.getParentFile());
        if (tempZip.exists()) {
            ZipUtils.LOGGER.log(Level.INFO, "[{0}] already exists hence deleting it ...", tempZip);
            tempZip.delete();
        }
        if (!sourceZipFile.renameTo(tempZip)) {
            throw new IOException("Unable to make the temporary zip file for :: [" + sourceZipFile + "]");
        }
        final byte[] b = new byte[4096];
        ZipInputStream zi = null;
        ZipOutputStream zo = null;
        ZipEntry ze = null;
        try {
            zi = new ZipInputStream(new FileInputStream(tempZip));
            zo = new ZipOutputStream(new FileOutputStream(sourceZipFile));
            while ((ze = zi.getNextEntry()) != null) {
                zo.putNextEntry(new ZipEntry(ze.getName()));
                int len;
                while ((len = zi.read(b)) != -1) {
                    zo.write(b, 0, len);
                }
            }
            zi.close();
            addFilesToZip(zo, dirVsFiles);
            zo.finish();
            ZipUtils.LOGGER.log(Level.INFO, "Finished appending the zip file :: [{0}]", zipFileName);
            tempZip.delete();
        }
        finally {
            if (zi != null) {
                try {
                    zi.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (zo != null) {
                try {
                    zo.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void addFilesToZip(final ZipOutputStream zOut, final Map<String, List<File>> dirVsFiles) throws IOException {
        for (final Map.Entry<String, List<File>> me : dirVsFiles.entrySet()) {
            final String archiveFileDir = me.getKey();
            final List<File> archivedFilesForBackup = me.getValue();
            if (archivedFilesForBackup.isEmpty()) {
                zOut.putNextEntry(new ZipEntry((archiveFileDir != null) ? (archiveFileDir + "/") : ""));
            }
            else {
                writeInZip(zOut, archiveFileDir, archivedFilesForBackup);
            }
        }
    }
    
    private static void writeInZip(final ZipOutputStream zOut, final String parentDir, final List<File> fileNames) {
        final List<String> excludeFiles = new ArrayList<String>();
        excludeFiles.add("postmaster.pid");
        excludeFiles.add("backup_label");
        final int buffer = 4096;
        for (final File file : fileNames) {
            if (file.isFile() && excludeFiles.contains(file.getName())) {
                ZipUtils.LOGGER.log(Level.INFO, "Ignoring the file :: [{0}]", file);
            }
            else {
                final byte[] data = new byte[buffer];
                BufferedInputStream buffInputStream = null;
                FileInputStream fi = null;
                try {
                    if (file.isDirectory()) {
                        zOut.putNextEntry(new ZipEntry(((parentDir != null) ? (parentDir + "/") : "") + file.getName() + "/"));
                        final File[] filesInDir = file.listFiles();
                        if (filesInDir != null) {
                            writeInZip(zOut, ((parentDir != null) ? (parentDir + "/") : "") + file.getName(), Arrays.asList(filesInDir));
                        }
                        else {
                            writeInZip(zOut, ((parentDir != null) ? (parentDir + "/") : "") + file.getName(), new ArrayList<File>());
                        }
                    }
                    fi = new FileInputStream(file);
                    buffInputStream = new BufferedInputStream(fi, buffer);
                    final ZipEntry entry = new ZipEntry(((parentDir != null) ? (parentDir + "/") : "") + file.getName());
                    entry.setMethod(8);
                    zOut.putNextEntry(entry);
                    int count;
                    while ((count = buffInputStream.read(data, 0, buffer)) != -1) {
                        zOut.write(data, 0, count);
                    }
                    zOut.closeEntry();
                }
                catch (final FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
                finally {
                    if (buffInputStream != null) {
                        try {
                            fi.close();
                            buffInputStream.close();
                        }
                        catch (final Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    public static void zip(final String zipFileName, final List<String> directoriesToBeArchived, final boolean zipSubDirs, final Properties prefProps) throws Exception {
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        FileOutputStream dest = null;
        ZipOutputStream output = null;
        try {
            ZipUtils.LOGGER.log(Level.INFO, "Creating zip file started ::: {0}", zipFileName);
            dest = new FileOutputStream(zipFileName, true);
            output = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte[] data = new byte[2048];
            for (final String path : directoriesToBeArchived) {
                final File f = new File(path);
                final boolean ignoreException = getPreferenceToAddFileInZip(path, prefProps);
                ZipUtils.LOGGER.log(Level.INFO, "Adding file [{0}] to zip ::: backup preference ::: {1}", new Object[] { path, ignoreException });
                try {
                    final int serverIndex = f.getCanonicalPath().indexOf(serverHome);
                    final int bacubDirIndex = f.getCanonicalPath().indexOf(new File(zipFileName).getParentFile().getCanonicalPath());
                    if (f.exists() && (serverIndex != -1 || bacubDirIndex != -1)) {
                        String zipEntryPath = zipSubDirs ? f.getCanonicalPath().substring(serverHome.length(), f.getCanonicalPath().length()) : "";
                        if (!zipEntryPath.equals("") && zipEntryPath.startsWith(File.separator)) {
                            zipEntryPath = zipEntryPath.substring(1, zipEntryPath.length());
                        }
                        if (f.isDirectory()) {
                            ZipUtils.LOGGER.log(Level.INFO, "Compressing files in directory ::: {0}", f.getCanonicalPath());
                            final String[] files = f.list();
                            if (files != null) {
                                ZipUtils.LOGGER.log(Level.INFO, "Number of file in the directory ::: {0}", files.length);
                                final File[] f2 = f.listFiles();
                                for (int i = 0; i < files.length; ++i) {
                                    addEntryToZip(f2[i], zipEntryPath, data, output, zipSubDirs);
                                }
                            }
                            else {
                                ZipUtils.LOGGER.log(Level.INFO, "Number of file in the directory ::: 0");
                            }
                        }
                        else {
                            ZipUtils.LOGGER.log(Level.INFO, "Compressing file ::: {0}", f.getCanonicalPath());
                            final String fName = f.getName();
                            zipEntryPath = zipEntryPath.substring(0, zipEntryPath.length() - fName.length() - 1);
                            addEntryToZip(f, zipEntryPath, data, output, zipSubDirs);
                        }
                    }
                    else {
                        if (!ignoreException) {
                            throw new Exception("Unknown File/Directory path [" + path + "] specified. Directory/File should be reside in " + serverHome);
                        }
                        ZipUtils.LOGGER.log(Level.WARNING, "Unknown File/Directory path [{0}] specified. Directory/File should be reside in {1}", new Object[] { path, serverHome });
                        ZipUtils.LOGGER.warning("Error ignored as per backup preference");
                    }
                }
                catch (final Exception e) {
                    if (!ignoreException) {
                        throw e;
                    }
                    ZipUtils.LOGGER.log(Level.WARNING, "The below Exception is ignored for {0}", path);
                    e.printStackTrace();
                }
            }
            output.close();
        }
        catch (final Exception e2) {
            ZipUtils.LOGGER.log(Level.FINE, "Error occurred while zipping:{0}", e2);
            throw e2;
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (dest != null) {
                    dest.close();
                }
            }
            catch (final Exception e3) {
                ZipUtils.LOGGER.log(Level.FINE, "{0}", e3);
            }
        }
    }
    
    private static boolean getPreferenceToAddFileInZip(final String fileName, final Properties props) throws Exception {
        if (props == null) {
            ZipUtils.LOGGER.info("No preference given, hence returning false.");
            return false;
        }
        final String pref = props.getProperty(fileName);
        ZipUtils.LOGGER.log(Level.INFO, "Preference specified in conf file for {0} = {1}", new Object[] { fileName, pref });
        if (pref == null || pref.equals("") || pref.equals("ON_EXCEPTION_FAIL")) {
            return false;
        }
        if (pref.equals("ON_EXCEPTION_IGNORE")) {
            return true;
        }
        throw new Exception("Unknown preference [" + pref + "] specified for file name [" + fileName + "] in backup_files.conf");
    }
    
    private static void addEntryToZip(final File fileToBeArchived, final String zipEntryPath, final byte[] data, final ZipOutputStream output, final boolean zipSubDir) throws Exception {
        FileInputStream fi = null;
        BufferedInputStream origin = null;
        try {
            ZipUtils.LOGGER.log(Level.FINE, "Adding to Zip: {0}", fileToBeArchived.getName());
            String zipEntry = (zipEntryPath == null || zipEntryPath.equals("")) ? fileToBeArchived.getName() : (zipEntryPath + "/" + fileToBeArchived.getName());
            if (fileToBeArchived.isDirectory()) {
                zipEntry += "/";
            }
            final ZipEntry entry = new ZipEntry(zipEntry);
            entry.setMethod(8);
            output.putNextEntry(entry);
            if (!fileToBeArchived.isDirectory()) {
                fi = new FileInputStream(fileToBeArchived);
                origin = new BufferedInputStream(fi, 2048);
                int count;
                while ((count = origin.read(data, 0, 2048)) != -1) {
                    output.write(data, 0, count);
                }
                fi.close();
                origin.close();
            }
            if (fileToBeArchived.isDirectory() && zipSubDir) {
                final File[] filesInDir = fileToBeArchived.listFiles();
                if (filesInDir != null) {
                    for (final File file : filesInDir) {
                        addEntryToZip(file, zipEntryPath + "/" + fileToBeArchived.getName(), data, output, zipSubDir);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw e;
        }
        finally {
            try {
                if (origin != null) {
                    origin.close();
                }
                if (fi != null) {
                    fi.close();
                }
            }
            catch (final Exception e2) {
                ZipUtils.LOGGER.log(Level.FINE, "{0}", e2);
            }
        }
    }
    
    public static Set<String> getAllZipEntries(final String zipNameWithFullPath) throws IOException {
        final Set<String> entries = new TreeSet<String>();
        final ZipFile zip = new ZipFile(zipNameWithFullPath);
        ZipEntry ze = null;
        final Enumeration<? extends ZipEntry> zipEntries = zip.entries();
        while (zipEntries.hasMoreElements()) {
            ze = (ZipEntry)zipEntries.nextElement();
            entries.add(ze.getName());
        }
        zip.close();
        return entries;
    }
    
    @Deprecated
    public static boolean isZipFileEncrypted(final String filename) {
        if (filename == null) {
            return false;
        }
        final File file = new File(filename);
        return file.exists() && filename.endsWith(".ezip");
    }
    
    static {
        LOGGER = Logger.getLogger(ZipUtils.class.getName());
    }
}
