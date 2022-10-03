package com.adventnet.tools.update.installer;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.List;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Vector;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

public class Zipper
{
    private static final Logger OUT;
    private JarOutputStream zipEngine;
    private String zipFileName;
    private int lengthCurrentDirectoryPath;
    private int countOfAddFiles;
    private String dirToZip;
    private Vector fileList;
    
    public Zipper() {
        this.countOfAddFiles = 1;
    }
    
    public void intializeZipper(String filename, final String dirName) throws Exception {
        this.fileList = new Vector();
        this.dirToZip = dirName;
        this.lengthCurrentDirectoryPath = (dirName + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + filename.substring(0, filename.length() - 4)).length() + 1;
        try {
            this.zipFileName = filename;
            if (filename.endsWith(".jar")) {
                final int index = filename.lastIndexOf(File.separator);
                filename = filename.substring(index + 1);
            }
            final File f = new File(this.dirToZip + File.separator + "patchtemp" + File.separator + filename);
            if (!f.getCanonicalPath().startsWith(new File(this.dirToZip + File.separator + "patchtemp" + File.separator + filename).getCanonicalPath())) {
                throw new IOException("Entry is outside of the target dir: " + filename);
            }
            this.zipEngine = new JarOutputStream(new FileOutputStream(f));
        }
        catch (final Exception e) {
            Zipper.OUT.log(Level.SEVERE, "ERR From intializeZipper: ", e);
            throw e;
        }
    }
    
    public void startZipping() throws Exception {
        try {
            for (int length = this.fileList.size(), i = 0; i < length; ++i) {
                this.zipTheEntry(this.fileList.elementAt(i));
            }
        }
        catch (final Exception e) {
            Zipper.OUT.log(Level.SEVERE, "ERR from startZipping :", e);
            throw e;
        }
    }
    
    public void makeFileList(final String dirName, final List<String> filesToBeSkipped) {
        final File directoryToList = new File(dirName);
        if (directoryToList.isDirectory()) {
            final String[] list = directoryToList.list();
            if (list.length > 0) {
                if (!dirName.equals(this.dirToZip + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + this.zipFileName.substring(0, this.zipFileName.length() - 4))) {
                    String relativePath = dirName.substring(this.lengthCurrentDirectoryPath, dirName.length());
                    relativePath = relativePath.replace('\\', '/');
                    if (filesToBeSkipped.contains(relativePath)) {
                        Zipper.OUT.info("Skipping entry \"" + relativePath + "\" from getting bundled inside the jar.");
                    }
                    else {
                        this.fileList.addElement(relativePath);
                    }
                }
                for (int i = 0; i < list.length; ++i) {
                    this.makeFileList(dirName + File.separator + list[i], filesToBeSkipped);
                }
            }
        }
        else {
            ++this.countOfAddFiles;
            String relativePath2 = dirName.substring(this.lengthCurrentDirectoryPath, dirName.length());
            if (!dirName.equals(System.getProperty("user.dir") + File.separator + this.zipFileName)) {
                relativePath2 = relativePath2.replace('\\', '/');
                if (filesToBeSkipped.contains(relativePath2)) {
                    Zipper.OUT.info("Skipping entry \"" + relativePath2 + "\" from getting bundled inside the jar.");
                }
                else {
                    this.fileList.addElement(relativePath2);
                }
            }
        }
    }
    
    protected void zipTheEntry(final String files) throws Exception {
        try {
            final int BUFFER = 2048;
            final byte[] data = new byte[BUFFER];
            FileInputStream fi = null;
            BufferedInputStream origin = null;
            ZipEntry entry = null;
            final String zipEntryPath = this.dirToZip + File.separator + "patchtemp" + File.separator + "jarupdate" + File.separator + this.zipFileName.substring(0, this.zipFileName.length() - 4) + File.separator + files;
            if (!new File(zipEntryPath).isDirectory()) {
                entry = new ZipEntry(files);
                this.zipEngine.putNextEntry(entry);
                fi = new FileInputStream(zipEntryPath);
                origin = new BufferedInputStream(fi, BUFFER);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    this.zipEngine.write(data, 0, count);
                }
                this.zipEngine.closeEntry();
                origin.close();
                fi.close();
            }
            else {
                entry = new ZipEntry(files + "/");
                this.zipEngine.putNextEntry(entry);
            }
        }
        catch (final Exception e) {
            Zipper.OUT.log(Level.SEVERE, "ERR From zipTheEntry : ", e);
            throw e;
        }
    }
    
    public void stopZipping() throws IOException {
        try {
            this.zipEngine.close();
            this.zipEngine = null;
        }
        catch (final IOException e) {
            Zipper.OUT.log(Level.SEVERE, "ERR From stopZipping :", e);
            throw e;
        }
    }
    
    static {
        OUT = Logger.getLogger(Zipper.class.getName());
    }
}
