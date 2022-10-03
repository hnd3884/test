package com.adventnet.mfw;

import com.zoho.conf.Configuration;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.io.FileOutputStream;
import java.util.Iterator;
import com.zoho.framework.utils.archive.ZipUtils;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.zoho.framework.utils.FileUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.util.List;

class PatchUpdater
{
    private static List<File> jarList;
    private static final Logger OUT;
    private static String server_home;
    
    private static void getJars(final File src, final String extension, final String contextName, final boolean areLogsPrinted) {
        if (!areLogsPrinted) {
            for (final File file : src.listFiles()) {
                final String fileName = file.getName();
                if (file.isDirectory()) {
                    PatchUpdater.OUT.log(Level.SEVERE, "The sub-folders of fixes cannot be parsed by PatchUpdater. Hence the directory \"" + fileName + "\" is SKIPPED");
                }
                else if (!fileName.endsWith(".jar") && !fileName.endsWith(".fjar") && !fileName.endsWith(".wjar")) {
                    PatchUpdater.OUT.log(Level.SEVERE, "Except *.fjar, *.wjar, *.jar(classpath jars) should not be present inside fixes folder. Hence the file \"" + fileName + "\" is SKIPPED");
                }
                else if (fileName.endsWith(".jar")) {
                    if (!System.getProperty("java.class.path").contains("fixes" + File.separator + fileName)) {
                        PatchUpdater.OUT.log(Level.SEVERE, "The jar file \"" + fileName + "\" is not present in the classpath. Hence it is skipped");
                    }
                    else {
                        PatchUpdater.OUT.log(Level.SEVERE, "The file \"" + fileName + "\" is loaded in classpath");
                    }
                }
            }
        }
        final File[] fileList = FileUtils.listFiles(src, contextName, extension);
        String fileName2 = "";
        boolean isAdded = false;
        for (final File file2 : fileList) {
            fileName2 = file2.getName();
            isAdded = false;
            if (contextName != null) {
                if (fileName2.startsWith(contextName + "_")) {
                    isAdded = true;
                }
                else {
                    PatchUpdater.OUT.log(Level.WARNING, "The filename \"" + fileName2 + "\" is invalid. Hence It is SKIPPED");
                }
            }
            else {
                isAdded = true;
            }
            if (isAdded) {
                PatchUpdater.jarList.add(file2);
                PatchUpdater.OUT.log(Level.INFO, file2.getName() + " is SELECTED.");
            }
        }
    }
    
    public static void createPatchToLoad(final File src, File patch, final List<String> contextList) throws Exception {
        for (final String cntxt : contextList) {
            if (cntxt.equals("lib_fix")) {
                getJars(src, ".fjar", null, false);
                PatchUpdater.OUT.log(Level.INFO, "Number of .fjar(s) received from " + src.getPath() + " : " + PatchUpdater.jarList.size());
            }
            else {
                final File patchDir = new File(PatchUpdater.server_home + File.separator + "webapps" + File.separator + cntxt + File.separator + "WEB-INF" + File.separator + "lib");
                if (patchDir.exists()) {
                    getJars(src, ".wjar", cntxt, true);
                    PatchUpdater.OUT.log(Level.INFO, "Number of .wjar(s) received from " + src.getPath() + " : " + PatchUpdater.jarList.size());
                    patch = new File(patchDir.getPath() + File.separator + "tempPatch.jar");
                    if (patch.exists()) {
                        patch.delete();
                    }
                }
                else {
                    PatchUpdater.OUT.log(Level.SEVERE, "The Given Context " + cntxt + "/WEB-INF/lib is not available. Hence PatchUpdater skipped those archives which are starts with " + cntxt);
                }
            }
            if (PatchUpdater.jarList.size() > 0) {
                selectJarEntriesToCreatePatch(patch);
                if (patch.getName().equals("tempPatch.jar") && patch.exists()) {
                    final File destPath = new File(PatchUpdater.server_home + File.separator + "webapps" + File.separator + cntxt + File.separator + "WEB-INF" + File.separator + "classes");
                    if (!destPath.exists()) {
                        PatchUpdater.OUT.log(Level.INFO, "is classes directory created in " + cntxt + " :: " + destPath.mkdir());
                    }
                    final List<Pattern> excludePattern = new ArrayList<Pattern>();
                    excludePattern.add(Pattern.compile("META-INF/MANIFEST.MF"));
                    ZipUtils.unZip(patch.getAbsolutePath(), destPath.getAbsolutePath(), (String)null, (List)excludePattern);
                    if (patch.exists()) {
                        patch.delete();
                    }
                }
                PatchUpdater.jarList.clear();
            }
        }
    }
    
    public static void deleteAllClassFiles(final File fileToDelete) {
        final File[] listFiles;
        final File[] files = listFiles = fileToDelete.listFiles();
        for (final File file : listFiles) {
            if (file.isDirectory()) {
                deleteAllClassFiles(file);
            }
            if (file.getName().endsWith(".class")) {
                file.delete();
            }
        }
        if (fileToDelete.listFiles().length == 0 && !fileToDelete.getName().equals("classes")) {
            fileToDelete.delete();
        }
    }
    
    private static void selectJarEntriesToCreatePatch(final File patch) throws Exception {
        final List<String> selectedJarEntries = new ArrayList<String>();
        JarOutputStream target = null;
        JarFile primaryJar = null;
        File checkFileCorrupted = null;
        try {
            target = new JarOutputStream(new FileOutputStream(patch, true), new Manifest());
            for (int i = 0; i < PatchUpdater.jarList.size(); ++i) {
                checkFileCorrupted = PatchUpdater.jarList.get(i);
                primaryJar = new JarFile(checkFileCorrupted);
                JarEntry selectedEntry = null;
                final Enumeration<JarEntry> jarEntryList = primaryJar.entries();
                while (jarEntryList.hasMoreElements()) {
                    JarEntry entryOfPrimaryJar = jarEntryList.nextElement();
                    long lastCompiledTimeForJar1 = entryOfPrimaryJar.getTime();
                    entryOfPrimaryJar = new JarEntry(entryOfPrimaryJar.getName());
                    primaryJar = new JarFile(PatchUpdater.jarList.get(i));
                    selectedEntry = entryOfPrimaryJar;
                    if (!selectedEntry.toString().contains("META-INF") && !selectedJarEntries.contains(selectedEntry.toString())) {
                        for (int j = i; j < PatchUpdater.jarList.size(); ++j) {
                            final JarFile secondaryJar = new JarFile(PatchUpdater.jarList.get(j));
                            final JarEntry entryOfSecondaryJar;
                            if (!entryOfPrimaryJar.isDirectory() && !secondaryJar.getName().equals(primaryJar.getName()) && (entryOfSecondaryJar = (JarEntry)secondaryJar.getEntry(entryOfPrimaryJar.toString())) != null && !entryOfSecondaryJar.toString().contains("META-INF")) {
                                final long lastCompiledTimeForJar2 = entryOfSecondaryJar.getTime();
                                if (lastCompiledTimeForJar1 < lastCompiledTimeForJar2) {
                                    selectedEntry = new JarEntry(entryOfSecondaryJar.getName());
                                    primaryJar = secondaryJar;
                                    lastCompiledTimeForJar1 = lastCompiledTimeForJar2;
                                }
                            }
                        }
                        selectedJarEntries.add(selectedEntry.toString());
                        if (!selectedEntry.isDirectory()) {
                            PatchUpdater.OUT.log(Level.SEVERE, selectedEntry.getName() + " is selected from " + primaryJar.getName());
                        }
                        try {
                            putJarEntriesIntoNewJar(selectedEntry, primaryJar, target);
                        }
                        catch (final Exception e) {
                            patch.delete();
                        }
                    }
                }
            }
        }
        catch (final ZipException ze) {
            if (checkFileCorrupted != null) {
                PatchUpdater.OUT.log(Level.SEVERE, "" + ze);
                PatchUpdater.OUT.log(Level.SEVERE, "REMOVE " + checkFileCorrupted.getName() + " from fixes folder");
            }
        }
        catch (final Exception e2) {
            PatchUpdater.OUT.log(Level.SEVERE, "" + e2);
            PatchUpdater.OUT.log(Level.SEVERE, "Exception Occurred. Is created patch deleted : " + patch.delete());
            throw e2;
        }
        finally {
            if (target != null) {
                try {
                    target.close();
                }
                catch (final IOException ioe) {
                    PatchUpdater.OUT.log(Level.SEVERE, "Exception Occurred while closing OUTPUTSTREAM. Is created patch deleted : " + patch.delete());
                    ioe.printStackTrace();
                    throw ioe;
                }
            }
        }
    }
    
    private static void putJarEntriesIntoNewJar(final JarEntry jarEntry, final JarFile selectedJar, final JarOutputStream target) throws Exception {
        target.putNextEntry(jarEntry);
        InputStream is = null;
        try {
            is = selectedJar.getInputStream(jarEntry);
            final byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                target.write(buffer, 0, len);
            }
            target.flush();
        }
        catch (final IOException ioe) {
            PatchUpdater.OUT.log(Level.SEVERE, "" + ioe);
            throw ioe;
        }
        finally {
            try {
                target.closeEntry();
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }
    
    static {
        PatchUpdater.jarList = new ArrayList<File>();
        OUT = Logger.getLogger(PatchUpdater.class.getName());
        PatchUpdater.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
