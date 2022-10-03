package com.sun.java.util.jar.pack;

import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.Checksum;
import java.util.zip.CheckedOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.CRC32;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.io.BufferedInputStream;
import java.util.jar.JarOutputStream;
import java.io.InputStream;
import java.util.SortedMap;
import java.beans.PropertyChangeListener;
import java.util.jar.Pack200;

public class UnpackerImpl extends TLGlobals implements Pack200.Unpacker
{
    Object _nunp;
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.props.addListener(propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.props.removeListener(propertyChangeListener);
    }
    
    @Override
    public SortedMap<String, String> properties() {
        return this.props;
    }
    
    @Override
    public String toString() {
        return Utils.getVersionString();
    }
    
    @Override
    public synchronized void unpack(final InputStream inputStream, final JarOutputStream jarOutputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("null input");
        }
        if (jarOutputStream == null) {
            throw new NullPointerException("null output");
        }
        assert Utils.currentInstance.get() == null;
        final boolean b = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");
        try {
            Utils.currentInstance.set(this);
            if (b) {
                Utils.changeDefaultTimeZoneToUtc();
            }
            final int integer = this.props.getInteger("com.sun.java.util.jar.pack.verbose");
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            if (Utils.isJarMagic(Utils.readMagic(bufferedInputStream))) {
                if (integer > 0) {
                    Utils.log.info("Copying unpacked JAR file...");
                }
                Utils.copyJarFile(new JarInputStream(bufferedInputStream), jarOutputStream);
            }
            else if (this.props.getBoolean("com.sun.java.util.jar.pack.disable.native")) {
                new DoUnpack().run(bufferedInputStream, jarOutputStream);
                bufferedInputStream.close();
                Utils.markJarFile(jarOutputStream);
            }
            else {
                try {
                    new NativeUnpack(this).run(bufferedInputStream, jarOutputStream);
                }
                catch (final UnsatisfiedLinkError | NoClassDefFoundError unsatisfiedLinkError | NoClassDefFoundError) {
                    new DoUnpack().run(bufferedInputStream, jarOutputStream);
                }
                bufferedInputStream.close();
                Utils.markJarFile(jarOutputStream);
            }
        }
        finally {
            this._nunp = null;
            Utils.currentInstance.set(null);
            if (b) {
                Utils.restoreDefaultTimeZone();
            }
        }
    }
    
    @Override
    public synchronized void unpack(final File file, final JarOutputStream jarOutputStream) throws IOException {
        if (file == null) {
            throw new NullPointerException("null input");
        }
        if (jarOutputStream == null) {
            throw new NullPointerException("null output");
        }
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            this.unpack(fileInputStream, jarOutputStream);
        }
        if (this.props.getBoolean("com.sun.java.util.jar.pack.unpack.remove.packfile")) {
            file.delete();
        }
    }
    
    private class DoUnpack
    {
        final int verbose;
        final Package pkg;
        final boolean keepModtime;
        final boolean keepDeflateHint;
        final int modtime;
        final boolean deflateHint;
        final CRC32 crc;
        final ByteArrayOutputStream bufOut;
        final OutputStream crcOut;
        
        private DoUnpack() {
            this.verbose = UnpackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
            UnpackerImpl.this.props.setInteger("unpack.progress", 0);
            this.pkg = new Package();
            this.keepModtime = "keep".equals(UnpackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "keep"));
            this.keepDeflateHint = "keep".equals(UnpackerImpl.this.props.getProperty("unpack.deflate.hint", "keep"));
            if (!this.keepModtime) {
                this.modtime = UnpackerImpl.this.props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
            }
            else {
                this.modtime = this.pkg.default_modtime;
            }
            this.deflateHint = (!this.keepDeflateHint && UnpackerImpl.this.props.getBoolean("unpack.deflate.hint"));
            this.crc = new CRC32();
            this.bufOut = new ByteArrayOutputStream();
            this.crcOut = new CheckedOutputStream(this.bufOut, this.crc);
        }
        
        public void run(final BufferedInputStream bufferedInputStream, final JarOutputStream jarOutputStream) throws IOException {
            if (this.verbose > 0) {
                UnpackerImpl.this.props.list(System.out);
            }
            int n = 1;
            while (true) {
                this.unpackSegment(bufferedInputStream, jarOutputStream);
                if (!Utils.isPackMagic(Utils.readMagic(bufferedInputStream))) {
                    break;
                }
                if (this.verbose > 0) {
                    Utils.log.info("Finished segment #" + n);
                }
                ++n;
            }
        }
        
        private void unpackSegment(final InputStream inputStream, final JarOutputStream jarOutputStream) throws IOException {
            UnpackerImpl.this.props.setProperty("unpack.progress", "0");
            new PackageReader(this.pkg, inputStream).read();
            if (UnpackerImpl.this.props.getBoolean("unpack.strip.debug")) {
                this.pkg.stripAttributeKind("Debug");
            }
            if (UnpackerImpl.this.props.getBoolean("unpack.strip.compile")) {
                this.pkg.stripAttributeKind("Compile");
            }
            UnpackerImpl.this.props.setProperty("unpack.progress", "50");
            this.pkg.ensureAllClassFiles();
            final HashSet set = new HashSet((Collection<? extends E>)this.pkg.getClasses());
            for (final Package.File file : this.pkg.getFiles()) {
                final JarEntry jarEntry = new JarEntry(Utils.getJarEntryName(file.nameString));
                final boolean b = this.keepDeflateHint ? ((file.options & 0x1) != 0x0 || (this.pkg.default_options & 0x20) != 0x0) : this.deflateHint;
                final boolean b2 = !b;
                if (b2) {
                    this.crc.reset();
                }
                this.bufOut.reset();
                if (file.isClassStub()) {
                    final Package.Class stubClass = file.getStubClass();
                    assert stubClass != null;
                    new ClassWriter(stubClass, b2 ? this.crcOut : this.bufOut).write();
                    set.remove(stubClass);
                }
                else {
                    file.writeTo(b2 ? this.crcOut : this.bufOut);
                }
                jarEntry.setMethod(b ? 8 : 0);
                if (b2) {
                    if (this.verbose > 0) {
                        Utils.log.info("stored size=" + this.bufOut.size() + " and crc=" + this.crc.getValue());
                    }
                    jarEntry.setMethod(0);
                    jarEntry.setSize(this.bufOut.size());
                    jarEntry.setCrc(this.crc.getValue());
                }
                if (this.keepModtime) {
                    jarEntry.setTime(file.modtime);
                    jarEntry.setTime(file.modtime * 1000L);
                }
                else {
                    jarEntry.setTime(this.modtime * 1000L);
                }
                jarOutputStream.putNextEntry(jarEntry);
                this.bufOut.writeTo(jarOutputStream);
                jarOutputStream.closeEntry();
                if (this.verbose > 0) {
                    Utils.log.info("Writing " + Utils.zeString(jarEntry));
                }
            }
            assert set.isEmpty();
            UnpackerImpl.this.props.setProperty("unpack.progress", "100");
            this.pkg.reset();
        }
    }
}
