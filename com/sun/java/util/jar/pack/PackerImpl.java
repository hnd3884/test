package com.sun.java.util.jar.pack;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedInputStream;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.util.jar.JarInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.SortedMap;
import java.util.jar.Pack200;

public class PackerImpl extends TLGlobals implements Pack200.Packer
{
    @Override
    public SortedMap<String, String> properties() {
        return this.props;
    }
    
    @Override
    public synchronized void pack(final JarFile jarFile, final OutputStream outputStream) throws IOException {
        assert Utils.currentInstance.get() == null;
        final boolean b = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");
        try {
            Utils.currentInstance.set(this);
            if (b) {
                Utils.changeDefaultTimeZoneToUtc();
            }
            if ("0".equals(this.props.getProperty("pack.effort"))) {
                Utils.copyJarFile(jarFile, outputStream);
            }
            else {
                new DoPack().run(jarFile, outputStream);
            }
        }
        finally {
            Utils.currentInstance.set(null);
            if (b) {
                Utils.restoreDefaultTimeZone();
            }
            jarFile.close();
        }
    }
    
    @Override
    public synchronized void pack(final JarInputStream jarInputStream, final OutputStream outputStream) throws IOException {
        assert Utils.currentInstance.get() == null;
        final boolean b = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");
        try {
            Utils.currentInstance.set(this);
            if (b) {
                Utils.changeDefaultTimeZoneToUtc();
            }
            if ("0".equals(this.props.getProperty("pack.effort"))) {
                Utils.copyJarFile(jarInputStream, outputStream);
            }
            else {
                new DoPack().run(jarInputStream, outputStream);
            }
        }
        finally {
            Utils.currentInstance.set(null);
            if (b) {
                Utils.restoreDefaultTimeZone();
            }
            jarInputStream.close();
        }
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.props.addListener(propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.props.removeListener(propertyChangeListener);
    }
    
    private class DoPack
    {
        final int verbose;
        final Package pkg;
        final String unknownAttrCommand;
        final String classFormatCommand;
        final Map<Attribute.Layout, Attribute> attrDefs;
        final Map<Attribute.Layout, String> attrCommands;
        final boolean keepFileOrder;
        final boolean keepClassOrder;
        final boolean keepModtime;
        final boolean latestModtime;
        final boolean keepDeflateHint;
        long totalOutputSize;
        int segmentCount;
        long segmentTotalSize;
        long segmentSize;
        final long segmentLimit;
        final List<String> passFiles;
        private int nread;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        private DoPack() {
            this.verbose = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
            PackerImpl.this.props.setInteger("pack.progress", 0);
            if (this.verbose > 0) {
                Utils.log.info(PackerImpl.this.props.toString());
            }
            this.pkg = new Package(Package.Version.makeVersion(PackerImpl.this.props, "min.class"), Package.Version.makeVersion(PackerImpl.this.props, "max.class"), Package.Version.makeVersion(PackerImpl.this.props, "package"));
            final String property = PackerImpl.this.props.getProperty("pack.unknown.attribute", "pass");
            if (!"strip".equals(property) && !"pass".equals(property) && !"error".equals(property)) {
                throw new RuntimeException("Bad option: pack.unknown.attribute = " + property);
            }
            this.unknownAttrCommand = property.intern();
            final String property2 = PackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass");
            if (!"pass".equals(property2) && !"error".equals(property2)) {
                throw new RuntimeException("Bad option: com.sun.java.util.jar.pack.class.format.error = " + property2);
            }
            this.classFormatCommand = property2.intern();
            final HashMap hashMap = new HashMap();
            final HashMap hashMap2 = new HashMap();
            final String[] array = { "pack.class.attribute.", "pack.field.attribute.", "pack.method.attribute.", "pack.code.attribute." };
            final int[] array2 = { 0, 1, 2, 3 };
            for (int i = 0; i < array2.length; ++i) {
                final String s = array[i];
                for (final String s2 : PackerImpl.this.props.prefixMap(s).keySet()) {
                    assert s2.startsWith(s);
                    final String substring = s2.substring(s.length());
                    final String property3 = PackerImpl.this.props.getProperty(s2);
                    final Attribute.Layout keyForLookup = Attribute.keyForLookup(array2[i], substring);
                    if ("strip".equals(property3) || "pass".equals(property3) || "error".equals(property3)) {
                        hashMap2.put(keyForLookup, property3.intern());
                    }
                    else {
                        Attribute.define(hashMap, array2[i], substring, property3);
                        if (this.verbose > 1) {
                            Utils.log.fine("Added layout for " + Constants.ATTR_CONTEXT_NAME[i] + " attribute " + substring + " = " + property3);
                        }
                        assert hashMap.containsKey(keyForLookup);
                        continue;
                    }
                }
            }
            this.attrDefs = (hashMap.isEmpty() ? null : hashMap);
            this.attrCommands = (hashMap2.isEmpty() ? null : hashMap2);
            this.keepFileOrder = PackerImpl.this.props.getBoolean("pack.keep.file.order");
            this.keepClassOrder = PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.keep.class.order");
            this.keepModtime = "keep".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
            this.latestModtime = "latest".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
            this.keepDeflateHint = "keep".equals(PackerImpl.this.props.getProperty("pack.deflate.hint"));
            if (!this.keepModtime && !this.latestModtime) {
                final int time = PackerImpl.this.props.getTime("pack.modification.time");
                if (time != 0) {
                    this.pkg.default_modtime = time;
                }
            }
            if (!this.keepDeflateHint && PackerImpl.this.props.getBoolean("pack.deflate.hint")) {
                final Package pkg = this.pkg;
                pkg.default_options |= 0x20;
            }
            this.totalOutputSize = 0L;
            this.segmentCount = 0;
            this.segmentTotalSize = 0L;
            this.segmentSize = 0L;
            long long1;
            if (PackerImpl.this.props.getProperty("pack.segment.limit", "").equals("")) {
                long1 = -1L;
            }
            else {
                long1 = PackerImpl.this.props.getLong("pack.segment.limit");
            }
            long max = Math.max(-1L, Math.min(2147483647L, long1));
            if (max == -1L) {
                max = Long.MAX_VALUE;
            }
            this.segmentLimit = max;
            this.passFiles = PackerImpl.this.props.getProperties("pack.pass.file.");
            final ListIterator<String> listIterator = this.passFiles.listIterator();
            while (listIterator.hasNext()) {
                final String s3 = listIterator.next();
                if (s3 == null) {
                    listIterator.remove();
                }
                else {
                    String s4 = Utils.getJarEntryName(s3);
                    if (s4.endsWith("/")) {
                        s4 = s4.substring(0, s4.length() - 1);
                    }
                    listIterator.set(s4);
                }
            }
            if (this.verbose > 0) {
                Utils.log.info("passFiles = " + this.passFiles);
            }
            final int integer = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.archive.options");
            if (integer != 0) {
                final Package pkg2 = this.pkg;
                pkg2.default_options |= integer;
            }
            this.nread = 0;
        }
        
        boolean isClassFile(final String s) {
            if (!s.endsWith(".class")) {
                return false;
            }
            int lastIndex;
            for (String substring = s; !this.passFiles.contains(substring); substring = substring.substring(0, lastIndex)) {
                lastIndex = substring.lastIndexOf(47);
                if (lastIndex < 0) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isMetaInfFile(final String s) {
            return s.startsWith("/META-INF") || s.startsWith("META-INF");
        }
        
        private void makeNextPackage() {
            this.pkg.reset();
        }
        
        private void noteRead(final InFile inFile) {
            ++this.nread;
            if (this.verbose > 2) {
                Utils.log.fine("...read " + inFile.name);
            }
            if (this.verbose > 0 && this.nread % 1000 == 0) {
                Utils.log.info("Have read " + this.nread + " files...");
            }
        }
        
        void run(final JarInputStream jarInputStream, final OutputStream outputStream) throws IOException {
            if (jarInputStream.getManifest() != null) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jarInputStream.getManifest().write(byteArrayOutputStream);
                this.pkg.addFile(this.readFile("META-INF/MANIFEST.MF", new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            }
            JarEntry nextJarEntry;
            while ((nextJarEntry = jarInputStream.getNextJarEntry()) != null) {
                final InFile inFile = new InFile(nextJarEntry);
                final String name = inFile.name;
                final Package.File file = this.readFile(name, jarInputStream);
                Package.File class1 = null;
                final long n = this.isMetaInfFile(name) ? 0L : inFile.getInputLength();
                final long segmentSize = this.segmentSize + n;
                this.segmentSize = segmentSize;
                if (segmentSize > this.segmentLimit) {
                    this.segmentSize -= n;
                    this.flushPartial(outputStream, -1);
                }
                if (this.verbose > 1) {
                    Utils.log.fine("Reading " + name);
                }
                assert nextJarEntry.isDirectory() == name.endsWith("/");
                if (this.isClassFile(name)) {
                    class1 = this.readClass(name, file.getInputStream());
                }
                if (class1 == null) {
                    class1 = file;
                    this.pkg.addFile(class1);
                }
                inFile.copyTo(class1);
                this.noteRead(inFile);
            }
            this.flushAll(outputStream);
        }
        
        void run(final JarFile jarFile, final OutputStream outputStream) throws IOException {
            final List<InFile> scanJar = this.scanJar(jarFile);
            if (this.verbose > 0) {
                Utils.log.info("Reading " + scanJar.size() + " files...");
            }
            int n = 0;
            for (final InFile inFile : scanJar) {
                final String name = inFile.name;
                final long n2 = this.isMetaInfFile(name) ? 0L : inFile.getInputLength();
                final long segmentSize = this.segmentSize + n2;
                this.segmentSize = segmentSize;
                if (segmentSize > this.segmentLimit) {
                    this.segmentSize -= n2;
                    final float n3 = (float)(n + 1);
                    final float n4 = (scanJar.size() - n3) * ((this.segmentCount + 1) / n3);
                    if (this.verbose > 1) {
                        Utils.log.fine("Estimated segments to do: " + n4);
                    }
                    this.flushPartial(outputStream, (int)Math.ceil(n4));
                }
                InputStream inputStream = inFile.getInputStream();
                if (this.verbose > 1) {
                    Utils.log.fine("Reading " + name);
                }
                Package.File file = null;
                if (this.isClassFile(name)) {
                    file = this.readClass(name, inputStream);
                    if (file == null) {
                        inputStream.close();
                        inputStream = inFile.getInputStream();
                    }
                }
                if (file == null) {
                    file = this.readFile(name, inputStream);
                    this.pkg.addFile(file);
                }
                inFile.copyTo(file);
                inputStream.close();
                this.noteRead(inFile);
                ++n;
            }
            this.flushAll(outputStream);
        }
        
        Package.File readClass(final String s, final InputStream inputStream) throws IOException {
            final Package.Class class1 = this.pkg.new Class(s);
            final ClassReader classReader = new ClassReader(class1, new BufferedInputStream(inputStream));
            classReader.setAttrDefs(this.attrDefs);
            classReader.setAttrCommands(this.attrCommands);
            classReader.unknownAttrCommand = this.unknownAttrCommand;
            try {
                classReader.read();
            }
            catch (final IOException ex) {
                final String s2 = "Passing class file uncompressed due to";
                if (ex instanceof Attribute.FormatException) {
                    final Attribute.FormatException ex2 = (Attribute.FormatException)ex;
                    if (ex2.layout.equals("pass")) {
                        Utils.log.info(ex2.toString());
                        Utils.log.warning(s2 + " unrecognized attribute: " + s);
                        return null;
                    }
                }
                else if (ex instanceof ClassReader.ClassFormatException) {
                    final ClassReader.ClassFormatException ex3 = (ClassReader.ClassFormatException)ex;
                    if (this.classFormatCommand.equals("pass")) {
                        Utils.log.info(ex3.toString());
                        Utils.log.warning(s2 + " unknown class format: " + s);
                        return null;
                    }
                }
                throw ex;
            }
            this.pkg.addClass(class1);
            return class1.file;
        }
        
        Package.File readFile(final String s, final InputStream inputStream) throws IOException {
            final Package.File file = this.pkg.new File(s);
            file.readFrom(inputStream);
            if (file.isDirectory() && file.getFileLength() != 0L) {
                throw new IllegalArgumentException("Non-empty directory: " + file.getFileName());
            }
            return file;
        }
        
        void flushPartial(final OutputStream outputStream, final int n) throws IOException {
            if (this.pkg.files.isEmpty() && this.pkg.classes.isEmpty()) {
                return;
            }
            this.flushPackage(outputStream, Math.max(1, n));
            PackerImpl.this.props.setInteger("pack.progress", 25);
            this.makeNextPackage();
            ++this.segmentCount;
            this.segmentTotalSize += this.segmentSize;
            this.segmentSize = 0L;
        }
        
        void flushAll(final OutputStream outputStream) throws IOException {
            PackerImpl.this.props.setInteger("pack.progress", 50);
            this.flushPackage(outputStream, 0);
            outputStream.flush();
            PackerImpl.this.props.setInteger("pack.progress", 100);
            ++this.segmentCount;
            this.segmentTotalSize += this.segmentSize;
            this.segmentSize = 0L;
            if (this.verbose > 0 && this.segmentCount > 1) {
                Utils.log.info("Transmitted " + this.segmentTotalSize + " input bytes in " + this.segmentCount + " segments totaling " + this.totalOutputSize + " bytes");
            }
        }
        
        void flushPackage(final OutputStream outputStream, final int archiveNextCount) throws IOException {
            final int size = this.pkg.files.size();
            if (!this.keepFileOrder) {
                if (this.verbose > 1) {
                    Utils.log.fine("Reordering files.");
                }
                this.pkg.reorderFiles(this.keepClassOrder, true);
            }
            else {
                assert this.pkg.files.containsAll(this.pkg.getClassStubs());
                ArrayList<Package.File> files = this.pkg.files;
                if (DoPack.$assertionsDisabled || !(files = new ArrayList<Package.File>((Collection<? extends E>)this.pkg.files)).retainAll(this.pkg.getClassStubs())) {}
                assert files.equals(this.pkg.getClassStubs());
            }
            this.pkg.trimStubs();
            if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.debug")) {
                this.pkg.stripAttributeKind("Debug");
            }
            if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.compile")) {
                this.pkg.stripAttributeKind("Compile");
            }
            if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.constants")) {
                this.pkg.stripAttributeKind("Constant");
            }
            if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.exceptions")) {
                this.pkg.stripAttributeKind("Exceptions");
            }
            if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.innerclasses")) {
                this.pkg.stripAttributeKind("InnerClasses");
            }
            final PackageWriter packageWriter = new PackageWriter(this.pkg, outputStream);
            packageWriter.archiveNextCount = archiveNextCount;
            packageWriter.write();
            outputStream.flush();
            if (this.verbose > 0) {
                final long n = packageWriter.archiveSize0 + packageWriter.archiveSize1;
                this.totalOutputSize += n;
                Utils.log.info("Transmitted " + size + " files of " + this.segmentSize + " input bytes in a segment of " + n + " bytes");
            }
        }
        
        List<InFile> scanJar(final JarFile jarFile) throws IOException {
            final ArrayList list = new ArrayList();
            try {
                for (final JarEntry jarEntry : Collections.list(jarFile.entries())) {
                    final InFile inFile = new InFile(jarFile, jarEntry);
                    assert jarEntry.isDirectory() == inFile.name.endsWith("/");
                    list.add(inFile);
                }
            }
            catch (final IllegalStateException ex) {
                throw new IOException(ex.getLocalizedMessage(), ex);
            }
            return list;
        }
        
        final class InFile
        {
            final String name;
            final JarFile jf;
            final JarEntry je;
            final File f;
            int modtime;
            int options;
            
            InFile(final String s) {
                this.modtime = 0;
                this.name = Utils.getJarEntryName(s);
                this.f = new File(s);
                this.jf = null;
                this.je = null;
                final int modtime = this.getModtime(this.f.lastModified());
                if (DoPack.this.keepModtime && modtime != 0) {
                    this.modtime = modtime;
                }
                else if (DoPack.this.latestModtime && modtime > DoPack.this.pkg.default_modtime) {
                    DoPack.this.pkg.default_modtime = modtime;
                }
            }
            
            InFile(final JarFile jf, final JarEntry je) {
                this.modtime = 0;
                this.name = Utils.getJarEntryName(je.getName());
                this.f = null;
                this.jf = jf;
                this.je = je;
                final int modtime = this.getModtime(je.getTime());
                if (DoPack.this.keepModtime && modtime != 0) {
                    this.modtime = modtime;
                }
                else if (DoPack.this.latestModtime && modtime > DoPack.this.pkg.default_modtime) {
                    DoPack.this.pkg.default_modtime = modtime;
                }
                if (DoPack.this.keepDeflateHint && je.getMethod() == 8) {
                    this.options |= 0x1;
                }
            }
            
            InFile(final DoPack doPack, final JarEntry jarEntry) {
                this(doPack, null, jarEntry);
            }
            
            long getInputLength() {
                final long n = (this.je != null) ? this.je.getSize() : this.f.length();
                assert n >= 0L : this + ".len=" + n;
                return Math.max(0L, n) + this.name.length() + 5L;
            }
            
            int getModtime(final long n) {
                final long n2 = (n + 500L) / 1000L;
                if ((int)n2 == n2) {
                    return (int)n2;
                }
                Utils.log.warning("overflow in modtime for " + this.f);
                return 0;
            }
            
            void copyTo(final Package.File file) {
                if (this.modtime != 0) {
                    file.modtime = this.modtime;
                }
                file.options |= this.options;
            }
            
            InputStream getInputStream() throws IOException {
                if (this.jf != null) {
                    return this.jf.getInputStream(this.je);
                }
                return new FileInputStream(this.f);
            }
            
            @Override
            public String toString() {
                return this.name;
            }
        }
    }
}
