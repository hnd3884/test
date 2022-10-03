package org.apache.commons.compress.harmony.pack200;

import java.util.jar.JarEntry;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.jar.JarFile;
import java.io.OutputStream;
import java.util.jar.JarInputStream;

public class Archive
{
    private final JarInputStream jarInputStream;
    private final OutputStream outputStream;
    private JarFile jarFile;
    private long currentSegmentSize;
    private final PackingOptions options;
    
    public Archive(final JarInputStream inputStream, OutputStream outputStream, PackingOptions options) throws IOException {
        this.jarInputStream = inputStream;
        if (options == null) {
            options = new PackingOptions();
        }
        this.options = options;
        if (options.isGzip()) {
            outputStream = new GZIPOutputStream(outputStream);
        }
        this.outputStream = new BufferedOutputStream(outputStream);
        PackingUtils.config(options);
    }
    
    public Archive(final JarFile jarFile, OutputStream outputStream, PackingOptions options) throws IOException {
        if (options == null) {
            options = new PackingOptions();
        }
        this.options = options;
        if (options.isGzip()) {
            outputStream = new GZIPOutputStream(outputStream);
        }
        this.outputStream = new BufferedOutputStream(outputStream);
        this.jarFile = jarFile;
        this.jarInputStream = null;
        PackingUtils.config(options);
    }
    
    public void pack() throws Pack200Exception, IOException {
        if (0 == this.options.getEffort()) {
            this.doZeroEffortPack();
        }
        else {
            this.doNormalPack();
        }
    }
    
    private void doZeroEffortPack() throws IOException, Pack200Exception {
        PackingUtils.log("Start to perform a zero-effort packing");
        if (this.jarInputStream != null) {
            PackingUtils.copyThroughJar(this.jarInputStream, this.outputStream);
        }
        else {
            PackingUtils.copyThroughJar(this.jarFile, this.outputStream);
        }
    }
    
    private void doNormalPack() throws IOException, Pack200Exception {
        PackingUtils.log("Start to perform a normal packing");
        List packingFileList;
        if (this.jarInputStream != null) {
            packingFileList = PackingUtils.getPackingFileListFromJar(this.jarInputStream, this.options.isKeepFileOrder());
        }
        else {
            packingFileList = PackingUtils.getPackingFileListFromJar(this.jarFile, this.options.isKeepFileOrder());
        }
        final List segmentUnitList = this.splitIntoSegments(packingFileList);
        int previousByteAmount = 0;
        int packedByteAmount = 0;
        final int segmentSize = segmentUnitList.size();
        for (int index = 0; index < segmentSize; ++index) {
            final SegmentUnit segmentUnit = segmentUnitList.get(index);
            new Segment().pack(segmentUnit, this.outputStream, this.options);
            previousByteAmount += segmentUnit.getByteAmount();
            packedByteAmount += segmentUnit.getPackedByteAmount();
        }
        PackingUtils.log("Total: Packed " + previousByteAmount + " input bytes of " + packingFileList.size() + " files into " + packedByteAmount + " bytes in " + segmentSize + " segments");
        this.outputStream.close();
    }
    
    private List splitIntoSegments(final List packingFileList) throws IOException, Pack200Exception {
        final List segmentUnitList = new ArrayList();
        List classes = new ArrayList();
        List files = new ArrayList();
        final long segmentLimit = this.options.getSegmentLimit();
        for (int size = packingFileList.size(), index = 0; index < size; ++index) {
            final PackingFile packingFile = packingFileList.get(index);
            if (!this.addJarEntry(packingFile, classes, files)) {
                segmentUnitList.add(new SegmentUnit(classes, files));
                classes = new ArrayList();
                files = new ArrayList();
                this.currentSegmentSize = 0L;
                this.addJarEntry(packingFile, classes, files);
                this.currentSegmentSize = 0L;
            }
            else if (segmentLimit == 0L && this.estimateSize(packingFile) > 0L) {
                segmentUnitList.add(new SegmentUnit(classes, files));
                classes = new ArrayList();
                files = new ArrayList();
            }
        }
        if (classes.size() > 0 || files.size() > 0) {
            segmentUnitList.add(new SegmentUnit(classes, files));
        }
        return segmentUnitList;
    }
    
    private boolean addJarEntry(final PackingFile packingFile, final List javaClasses, final List files) throws IOException, Pack200Exception {
        final long segmentLimit = this.options.getSegmentLimit();
        if (segmentLimit != -1L && segmentLimit != 0L) {
            final long packedSize = this.estimateSize(packingFile);
            if (packedSize + this.currentSegmentSize > segmentLimit && this.currentSegmentSize > 0L) {
                return false;
            }
            this.currentSegmentSize += packedSize;
        }
        final String name = packingFile.getName();
        if (name.endsWith(".class") && !this.options.isPassFile(name)) {
            final Pack200ClassReader classParser = new Pack200ClassReader(packingFile.contents);
            classParser.setFileName(name);
            javaClasses.add(classParser);
            packingFile.contents = new byte[0];
        }
        files.add(packingFile);
        return true;
    }
    
    private long estimateSize(final PackingFile packingFile) {
        final String name = packingFile.getName();
        if (name.startsWith("META-INF") || name.startsWith("/META-INF")) {
            return 0L;
        }
        long fileSize = packingFile.contents.length;
        if (fileSize < 0L) {
            fileSize = 0L;
        }
        return name.length() + fileSize + 5L;
    }
    
    static class SegmentUnit
    {
        private final List classList;
        private final List fileList;
        private int byteAmount;
        private int packedByteAmount;
        
        public SegmentUnit(final List classes, final List files) {
            this.byteAmount = 0;
            this.packedByteAmount = 0;
            this.classList = classes;
            this.fileList = files;
            for (final Pack200ClassReader classReader : this.classList) {
                this.byteAmount += classReader.b.length;
            }
            for (final PackingFile file : this.fileList) {
                this.byteAmount += file.contents.length;
            }
        }
        
        public List getClassList() {
            return this.classList;
        }
        
        public int classListSize() {
            return this.classList.size();
        }
        
        public int fileListSize() {
            return this.fileList.size();
        }
        
        public List getFileList() {
            return this.fileList;
        }
        
        public int getByteAmount() {
            return this.byteAmount;
        }
        
        public int getPackedByteAmount() {
            return this.packedByteAmount;
        }
        
        public void addPackedByteAmount(final int amount) {
            this.packedByteAmount += amount;
        }
    }
    
    static class PackingFile
    {
        private final String name;
        private byte[] contents;
        private final long modtime;
        private final boolean deflateHint;
        private final boolean isDirectory;
        
        public PackingFile(final String name, final byte[] contents, final long modtime) {
            this.name = name;
            this.contents = contents;
            this.modtime = modtime;
            this.deflateHint = false;
            this.isDirectory = false;
        }
        
        public PackingFile(final byte[] bytes, final JarEntry jarEntry) {
            this.name = jarEntry.getName();
            this.contents = bytes;
            this.modtime = jarEntry.getTime();
            this.deflateHint = (jarEntry.getMethod() == 8);
            this.isDirectory = jarEntry.isDirectory();
        }
        
        public byte[] getContents() {
            return this.contents;
        }
        
        public String getName() {
            return this.name;
        }
        
        public long getModtime() {
            return this.modtime;
        }
        
        public void setContents(final byte[] contents) {
            this.contents = contents;
        }
        
        public boolean isDefalteHint() {
            return this.deflateHint;
        }
        
        public boolean isDirectory() {
            return this.isDirectory;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
