package com.adventnet.tools.update.installer;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import com.zoho.tools.util.FileUtil;
import java.io.BufferedInputStream;
import java.util.logging.Level;
import com.adventnet.tools.update.CommonUtil;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.io.File;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Logger;

public class UpdateJar
{
    private static final Logger OUT;
    String updateDir;
    String jarPatchDir;
    String dirToZip;
    Manifest manifest;
    final int BUFFER = 10240;
    Set<String> skipJarEntriesSet;
    
    public void updateTheJarFile(final String updateJarName, final String originalJarName, final String dirToUnzip) throws Exception {
        this.dirToZip = dirToUnzip;
        this.updateDir = this.dirToZip + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + originalJarName.substring(0, originalJarName.length() - 4);
        this.jarPatchDir = this.dirToZip + File.separator + "patchtemp" + File.separator + "jarpatch";
        new File(this.updateDir).mkdir();
        this.extractJarFile(this.jarPatchDir + File.separator + updateJarName);
        this.extractJarFile(this.dirToZip + File.separator + originalJarName);
        this.mergeManifestFile(this.jarPatchDir + File.separator + updateJarName);
    }
    
    private void getManifestFile(final String originalJarFileName) {
        try {
            final JarFile jarFile = new JarFile(originalJarFileName);
            this.manifest = jarFile.getManifest();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mergeManifestFile(final String updateJarFileName) throws IOException {
        if (!new File(updateJarFileName).exists()) {
            return;
        }
        final JarFile jarFile = new JarFile(updateJarFileName);
        this.manifest = jarFile.getManifest();
        jarFile.close();
    }
    
    private void writeManifestFile() {
        try {
            final FileOutputStream unjarredFileWriter = new FileOutputStream(new File(this.updateDir + File.separator + "META-INF" + File.separator + "MANIFEST.MF"));
            this.manifest.write(unjarredFileWriter);
            unjarredFileWriter.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void unzipTheRevertJarFile(final String updateJarName, final String originalJarName, final String dirToUnzip) throws Exception {
        this.dirToZip = dirToUnzip;
        this.updateDir = this.dirToZip + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + originalJarName.substring(0, originalJarName.length() - 4);
        new File(this.updateDir).mkdir();
        this.extractJarFile(updateJarName);
        this.extractJarFile(this.dirToZip + File.separator + originalJarName);
    }
    
    public void extractEEARFiles(final String archiveName, final String dirToUnzip) {
        final JarExtractor jarext = new JarExtractor();
        final File f = new File(archiveName);
        final File dest = new File(dirToUnzip + File.separator + archiveName);
        try {
            JarExtractor.extract(f, dest);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured while extracting EEAR file : " + archiveName);
        }
    }
    
    public void revertEEARFiles(final File srcDir, final File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
            final String[] children = srcDir.list();
            for (int i = 0; i < children.length; ++i) {
                this.revertEEARFiles(new File(srcDir, children[i]), new File(dstDir, children[i]));
            }
        }
        else {
            this.copyFile(srcDir, dstDir);
        }
    }
    
    void copyFile(final File src, final File dst) throws IOException {
        final InputStream in = new FileInputStream(src);
        final OutputStream out = new FileOutputStream(dst);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public void compressUpdatedEEARFiles(final String archiveName, final String dir) {
        final JarCompressor jarcom = new JarCompressor();
        final File f = new File(archiveName);
        final File dest = new File(dir);
        try {
            JarCompressor.compress(f, dest);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured while compressing EEAR file : " + archiveName);
        }
    }
    
    private void extractJarFile(final String currentJarFileName) throws Exception {
        if (!new File(currentJarFileName).exists()) {
            return;
        }
        try (final JarFile jarFile = new JarFile(currentJarFileName)) {
            ZipEntry jarFileEntry = null;
            final Enumeration entries = jarFile.entries();
            String jarFileEntryName = null;
            while (entries.hasMoreElements()) {
                jarFileEntry = entries.nextElement();
                jarFileEntryName = jarFileEntry.getName();
                if (!jarFileEntry.isDirectory() && !jarFileEntryName.endsWith("\\")) {
                    if (this.skipJarEntriesSet != null && this.skipJarEntriesSet.contains(jarFileEntryName)) {
                        continue;
                    }
                    jarFileEntryName = (File.separator.equals("/") ? jarFileEntryName.replace('\\', '/') : jarFileEntryName.replace('/', '\\'));
                    final File f = new File(this.updateDir + File.separator + jarFileEntryName);
                    if (!f.getCanonicalPath().startsWith(new File(this.updateDir).getCanonicalPath() + File.separator)) {
                        throw new IOException("Entry is outside of the target dir: " + jarFileEntryName);
                    }
                    if (f.exists()) {
                        continue;
                    }
                    CommonUtil.createAllSubDirectories(this.updateDir + File.separator + jarFileEntryName);
                    if (f.isDirectory()) {
                        continue;
                    }
                    if (!f.exists()) {
                        UpdateJar.OUT.log(Level.FINE, "File doesnot exist, creating new file :: {0}", f.getAbsolutePath());
                        if (f.createNewFile()) {
                            UpdateJar.OUT.log(Level.FINE, "New File Created :: {0}", f.getAbsolutePath());
                        }
                    }
                    try (final InputStream unjarrer = jarFile.getInputStream(jarFileEntry);
                         final FileOutputStream unjarredFileWriter = new FileOutputStream(f);
                         final BufferedInputStream origin = new BufferedInputStream(unjarrer, 10240)) {
                        UpdateJar.OUT.log(Level.FINE, "Inside try-with-resources block check {0}", f.getAbsolutePath());
                        final byte[] data = new byte[10240];
                        int count;
                        while ((count = origin.read(data, 0, 10240)) != -1) {
                            unjarredFileWriter.write(data, 0, count);
                        }
                        UpdateJar.OUT.log(Level.FINE, "End of try-with-resorces-end-block-check");
                    }
                    if (FileUtil.isFileExists(f, f.getName())) {
                        continue;
                    }
                    for (final File existingFile : f.getParentFile().listFiles()) {
                        if (existingFile.isFile() && existingFile.getName().equalsIgnoreCase(f.getName())) {
                            existingFile.renameTo(f);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            UpdateJar.OUT.log(Level.SEVERE, "ERR From extractJarFile : ", ex);
            throw ex;
        }
    }
    
    public void jarUpdatedJar(final String fileName) throws Exception {
        final File f = new File(this.dirToZip + File.separator + "patchtemp" + File.separator + fileName);
        final List<String> filesToBeSkipped = new ArrayList<String>();
        if (!f.getCanonicalPath().startsWith(new File(this.dirToZip + File.separator + "patchtemp" + File.separator).getCanonicalPath())) {
            throw new IOException("Entry is outside of the target dir: " + fileName);
        }
        Files.deleteIfExists(Paths.get(this.dirToZip, "patchtemp", fileName));
        Zipper jarCompresser = new Zipper();
        jarCompresser.intializeZipper(fileName, this.dirToZip);
        jarCompresser.makeFileList(this.updateDir, filesToBeSkipped);
        jarCompresser.startZipping();
        jarCompresser.stopZipping();
        jarCompresser = null;
        try {
            CommonUtil.deleteFiles(this.updateDir);
        }
        catch (final RuntimeException rte) {
            UpdateJar.OUT.log(Level.SEVERE, "Some problem while clearing the temp directory. Will delete it later.", rte);
        }
    }
    
    @Deprecated
    public void setEntriesToBeSkippedWhileFormingJar(final Set<String> entriesToBeSkipped) {
        this.addSkipJarEntries(entriesToBeSkipped);
    }
    
    public void addSkipJarEntries(final Set<String> entriesToBeSkipped) {
        this.skipJarEntriesSet = new TreeSet<String>();
        if (entriesToBeSkipped != null && !entriesToBeSkipped.isEmpty()) {
            for (String entry : entriesToBeSkipped) {
                entry = entry.replace('\\', '/');
                this.skipJarEntriesSet.add(entry);
            }
        }
    }
    
    public void copyUpdatedJarFile(String fileName, final String destFile) throws Exception {
        try {
            if (fileName.endsWith(".jar")) {
                if (File.separator.equals("/")) {
                    final int index = fileName.lastIndexOf(47);
                    fileName = fileName.substring(index + 1);
                }
                else {
                    final int index = fileName.lastIndexOf(92);
                    fileName = fileName.substring(index + 1);
                }
            }
            final FileInputStream f = new FileInputStream(this.dirToZip + File.separator + "patchtemp" + File.separator + fileName);
            if (!new File(destFile).getCanonicalPath().startsWith(new File(this.dirToZip).getCanonicalPath())) {
                throw new IOException("Entry is outside of the target dir: " + new File(destFile).getName());
            }
            final FileOutputStream w = new FileOutputStream(destFile);
            final byte[] data = new byte[10240];
            final BufferedInputStream origin = new BufferedInputStream(f, 10240);
            int count;
            while ((count = origin.read(data, 0, 10240)) != -1) {
                w.write(data, 0, count);
            }
            f.close();
            w.flush();
            w.close();
            origin.close();
        }
        catch (final Exception ex) {
            UpdateJar.OUT.log(Level.SEVERE, "ERR from copyUpdatedJarFile : ", ex);
            throw ex;
        }
    }
    
    static {
        OUT = Logger.getLogger(UpdateJar.class.getName());
    }
}
