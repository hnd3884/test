package com.adventnet.tools.update.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.Collections;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.ZipFile;
import java.util.zip.CRC32;
import java.util.List;
import java.util.ArrayList;

public class ZipDiffUtil
{
    private final int UNDETERMINED = -1001;
    private final String fileSep;
    private String productHome;
    private ArrayList newFiles;
    private ArrayList modFiles;
    private ArrayList unModFiles;
    private ArrayList delFiles;
    private List<String> jarsWithAddedEntries;
    private EnhancedFileFilter filterForCRC;
    private int BUFFER;
    private byte[] data;
    private CRC32 crc;
    
    public ZipDiffUtil(final ZipFile oldVersionZip, final ZipFile newVersionZip, final Properties props, final boolean isAddJarEntries) {
        this.fileSep = System.getProperty("file.separator");
        this.productHome = ".";
        this.newFiles = new ArrayList();
        this.modFiles = new ArrayList();
        this.unModFiles = new ArrayList();
        this.delFiles = new ArrayList();
        this.jarsWithAddedEntries = new ArrayList<String>();
        this.filterForCRC = null;
        this.BUFFER = 40960;
        this.data = new byte[this.BUFFER];
        this.crc = new CRC32();
        this.productHome = props.getProperty("ProductHome", ".");
        final String localHome = "";
        final EnhancedFileFilter diffFilter = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForDiff");
        final Hashtable oldFileDetails = this.getFileDetailsFromZip(localHome, oldVersionZip, diffFilter);
        final Hashtable newFileDetails = this.getFileDetailsFromZip(localHome, newVersionZip, diffFilter);
        this.filterForCRC = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForCRC");
        if (isAddJarEntries) {
            final Set oldKeySet = oldFileDetails.keySet();
            final Set newKeySet = newFileDetails.keySet();
            for (final Object newKey : newKeySet) {
                final String key = (String)newKey;
                if (!oldKeySet.contains(key)) {
                    this.jarsWithAddedEntries.add(oldVersionZip.getName());
                    break;
                }
            }
        }
        this.processFiles(oldFileDetails, newFileDetails);
    }
    
    public ZipDiffUtil(final ZipFile oldVersionZip, final ZipFile newVersionZip, final Properties props) {
        this(oldVersionZip, newVersionZip, props, false);
    }
    
    public ZipDiffUtil(final ZipFile oldVersionZip, final File newDirectory, final Properties props) {
        this.fileSep = System.getProperty("file.separator");
        this.productHome = ".";
        this.newFiles = new ArrayList();
        this.modFiles = new ArrayList();
        this.unModFiles = new ArrayList();
        this.delFiles = new ArrayList();
        this.jarsWithAddedEntries = new ArrayList<String>();
        this.filterForCRC = null;
        this.BUFFER = 40960;
        this.data = new byte[this.BUFFER];
        this.crc = new CRC32();
        this.productHome = props.getProperty("ProductHome", ".");
        final String localHome = Utils.getUnixFileName(newDirectory.getPath() + this.fileSep);
        final EnhancedFileFilter diffFilter = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForDiff");
        final Hashtable oldFileDetails = this.getFileDetailsFromZip(localHome, oldVersionZip, diffFilter);
        final Hashtable newFileDetails = this.getFileDetailsFromDir(localHome, newDirectory, diffFilter);
        this.filterForCRC = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForCRC");
        this.processFiles(oldFileDetails, newFileDetails);
    }
    
    public ZipDiffUtil(final File oldDirectory, final File newDirectory, final Properties props) {
        this.fileSep = System.getProperty("file.separator");
        this.productHome = ".";
        this.newFiles = new ArrayList();
        this.modFiles = new ArrayList();
        this.unModFiles = new ArrayList();
        this.delFiles = new ArrayList();
        this.jarsWithAddedEntries = new ArrayList<String>();
        this.filterForCRC = null;
        this.BUFFER = 40960;
        this.data = new byte[this.BUFFER];
        this.crc = new CRC32();
        this.productHome = props.getProperty("ProductHome", ".");
        String localHome = Utils.getUnixFileName(oldDirectory.getPath() + this.fileSep);
        final EnhancedFileFilter diffFilter = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForDiff");
        final Hashtable oldFileDetails = this.getFileDetailsFromDir(localHome, oldDirectory, diffFilter);
        localHome = Utils.getUnixFileName(newDirectory.getPath() + this.fileSep);
        final Hashtable newFileDetails = this.getFileDetailsFromDir(localHome, newDirectory, diffFilter);
        this.filterForCRC = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForCRC");
        this.processFiles(oldFileDetails, newFileDetails);
    }
    
    public ZipDiffUtil(final File oldDirectory, final ZipFile newVersionZip, final Properties props) {
        this.fileSep = System.getProperty("file.separator");
        this.productHome = ".";
        this.newFiles = new ArrayList();
        this.modFiles = new ArrayList();
        this.unModFiles = new ArrayList();
        this.delFiles = new ArrayList();
        this.jarsWithAddedEntries = new ArrayList<String>();
        this.filterForCRC = null;
        this.BUFFER = 40960;
        this.data = new byte[this.BUFFER];
        this.crc = new CRC32();
        this.productHome = props.getProperty("ProductHome", ".");
        final String localHome = Utils.getUnixFileName(oldDirectory.getPath() + this.fileSep);
        final EnhancedFileFilter diffFilter = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForDiff");
        final Hashtable oldFileDetails = this.getFileDetailsFromDir(localHome, oldDirectory, diffFilter);
        final Hashtable newFileDetails = this.getFileDetailsFromZip(localHome, newVersionZip, diffFilter);
        this.filterForCRC = ((Hashtable<K, EnhancedFileFilter>)props).get("ExcludeForCRC");
        this.processFiles(oldFileDetails, newFileDetails);
    }
    
    public ArrayList getNewFiles(final EnhancedFileFilter filter) {
        return this.filterAndGetFiles(this.newFiles, filter);
    }
    
    public ArrayList getModifiedFiles(final EnhancedFileFilter filter) {
        return this.filterAndGetFiles(this.modFiles, filter);
    }
    
    public ArrayList getDeletedFiles(final EnhancedFileFilter filter) {
        return this.filterAndGetFiles(this.delFiles, filter);
    }
    
    public ArrayList getUnModifiedFiles(final EnhancedFileFilter filter) {
        return this.filterAndGetFiles(this.unModFiles, filter);
    }
    
    public List<String> getJarFilesWithAddedEntries() {
        return Collections.unmodifiableList((List<? extends String>)this.jarsWithAddedEntries);
    }
    
    private ArrayList filterAndGetFiles(final ArrayList filesToFilter, final EnhancedFileFilter filter) {
        if (filter == null) {
            return (ArrayList)filesToFilter.clone();
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i < filesToFilter.size(); ++i) {
            final String fileName = filesToFilter.get(i);
            if (filter.accept(fileName)) {
                list.add(fileName);
            }
        }
        return list;
    }
    
    private Hashtable getFileDetailsFromDir(final String localHome, File directory, EnhancedFileFilter filter) {
        final Hashtable hashToReturn = new Hashtable();
        final ArrayList dirList = new ArrayList();
        if (filter == null) {
            filter = new EnhancedFileFilter();
        }
        dirList.add(directory);
        while (dirList.size() > 0) {
            directory = dirList.remove(0);
            if (directory.isDirectory()) {
                for (final File tempFile : directory.listFiles()) {
                    final String absPath = tempFile.getPath();
                    final String relPath = Utils.getRelativeFileName(localHome, absPath);
                    if (filter.accept(relPath)) {
                        if (tempFile.isDirectory()) {
                            dirList.add(tempFile);
                        }
                        else {
                            final ThinFileProps tfp = new ThinFileProps(absPath, relPath);
                            tfp.setLength(tempFile.length());
                            hashToReturn.put(relPath, tfp);
                        }
                    }
                }
            }
            else {
                final String absPath2 = directory.getPath();
                final String relPath2 = Utils.getRelativeFileName(localHome, absPath2);
                if (!filter.accept(relPath2)) {
                    continue;
                }
                final ThinFileProps tfp2 = new ThinFileProps(absPath2, relPath2);
                tfp2.setLength(directory.length());
                hashToReturn.put(relPath2, tfp2);
            }
        }
        return hashToReturn;
    }
    
    private Hashtable getFileDetailsFromZip(final String localHome, final ZipFile zipFile, EnhancedFileFilter filter) {
        final Hashtable hashToReturn = new Hashtable();
        String relPath = null;
        String absPath = null;
        if (filter == null) {
            filter = new EnhancedFileFilter();
        }
        final Enumeration en = zipFile.entries();
        while (en.hasMoreElements()) {
            final ZipEntry zEntry = en.nextElement();
            if (zEntry.isDirectory()) {
                continue;
            }
            relPath = Utils.getUnixFileName(zEntry.toString());
            if (!filter.accept(relPath)) {
                continue;
            }
            absPath = localHome + relPath;
            final ThinFileProps tfp = new ThinFileProps(absPath, relPath);
            tfp.setLength(zEntry.getSize());
            tfp.setCRC(zEntry.getCrc());
            hashToReturn.put(relPath, tfp);
        }
        return hashToReturn;
    }
    
    public static void main(final String[] args) throws Exception {
        final Properties props = new Properties();
        final ArrayList list = new ArrayList();
        list.add("*");
        ((Hashtable<String, EnhancedFileFilter>)props).put("ExcludeForCRC", new EnhancedFileFilter());
        ZipFile zf1 = null;
        ZipFile zf2 = null;
        try {
            zf1 = new ZipFile(args[0]);
            zf2 = new ZipFile(args[1]);
            final ZipDiffUtil du = new ZipDiffUtil(zf1, zf2, props);
            System.out.println("================================================================================");
            System.out.println("                    NEW FILE");
            System.out.println(du.getNewFiles(null));
            System.out.println("================================================================================");
            System.out.println("================================================================================");
            System.out.println("                    NEW FILE");
            System.out.println(du.getNewFiles(null));
            System.out.println("================================================================================");
            System.out.println("================================================================================");
            System.out.println("                    NEW FILE");
            System.out.println(du.getNewFiles(null));
            System.out.println("================================================================================");
            System.out.println("================================================================================");
            System.out.println("                    NEW FILE");
            System.out.println(du.getNewFiles(null));
            System.out.println("================================================================================");
        }
        finally {
            if (zf2 != null) {
                zf2.close();
            }
            if (zf1 != null) {
                zf1.close();
            }
        }
    }
    
    private void processFiles(final Hashtable oldFileDetails, final Hashtable newFileDetails) {
        final Enumeration en = oldFileDetails.keys();
        while (en.hasMoreElements()) {
            final String key = en.nextElement();
            final ThinFileProps oldFileProps = oldFileDetails.get(key);
            final ThinFileProps newFileProps = newFileDetails.get(key);
            if (newFileProps == null) {
                this.delFiles.add(key);
            }
            else if (oldFileProps.getLength() != newFileProps.getLength()) {
                this.modFiles.add(key);
            }
            else {
                final long oldCrc = oldFileProps.getCRC();
                final long newCrc = newFileProps.getCRC();
                if (oldCrc == newCrc || oldCrc == -1001L || newCrc == -1001L) {
                    this.unModFiles.add(key);
                }
                else {
                    this.modFiles.add(key);
                }
            }
            newFileDetails.remove(key);
        }
        this.newFiles.addAll(newFileDetails.keySet());
    }
    
    private long getCrcForFile(final File file) {
        this.crc.reset();
        FileInputStream fiStream = null;
        BufferedInputStream biStream = null;
        try {
            fiStream = new FileInputStream(file);
            biStream = new BufferedInputStream(fiStream);
            int count = 0;
            while ((count = biStream.read(this.data, 0, this.BUFFER)) != -1) {
                this.crc.update(this.data, 0, count);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fiStream.close();
            }
            catch (final Exception ex) {}
            try {
                fiStream.close();
            }
            catch (final Exception ex2) {}
        }
        return this.crc.getValue();
    }
    
    private class ThinFileProps
    {
        private long fileLength;
        private long crc;
        private String absPath;
        private String relPath;
        
        public ThinFileProps(final String absPath, final String relPath) {
            this.fileLength = -1001L;
            this.crc = -1001L;
            this.absPath = null;
            this.relPath = null;
            this.absPath = absPath;
            this.relPath = relPath;
        }
        
        public String getAbsPath() {
            return this.absPath;
        }
        
        public String getRelPath() {
            return this.relPath;
        }
        
        public void setLength(final long fileLength) {
            this.fileLength = fileLength;
        }
        
        public void setCRC(final long crc) {
            this.crc = crc;
        }
        
        public long getLength() {
            if (this.fileLength == -1001L) {
                final File tempFile = new File(this.absPath);
                this.fileLength = tempFile.length();
            }
            return this.fileLength;
        }
        
        public long getCRC() {
            if (this.crc == -1001L && (ZipDiffUtil.this.filterForCRC == null || ZipDiffUtil.this.filterForCRC.accept(this.relPath))) {
                this.crc = ZipDiffUtil.this.getCrcForFile(new File(this.absPath));
            }
            return this.crc;
        }
        
        @Override
        public String toString() {
            return "\nAbs File Name : " + this.absPath + "\nRel File Name : " + this.relPath + "\n  CRC     : " + this.crc + "\n  Length  : " + this.fileLength + "\n";
        }
    }
}
