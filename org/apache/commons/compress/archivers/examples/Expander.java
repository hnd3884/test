package org.apache.commons.compress.archivers.examples;

import java.io.OutputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.Iterator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.tar.TarFile;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import org.apache.commons.compress.archivers.ArchiveException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;

public class Expander
{
    public void expand(final File archive, final File targetDirectory) throws IOException, ArchiveException {
        String format = null;
        try (final InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
            format = ArchiveStreamFactory.detect(i);
        }
        this.expand(format, archive, targetDirectory);
    }
    
    public void expand(final String format, final File archive, final File targetDirectory) throws IOException, ArchiveException {
        if (this.prefersSeekableByteChannel(format)) {
            try (final SeekableByteChannel c = FileChannel.open(archive.toPath(), StandardOpenOption.READ)) {
                this.expand(format, c, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
            }
            return;
        }
        try (final InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
            this.expand(format, i, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
        }
    }
    
    @Deprecated
    public void expand(final InputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }
    
    public void expand(final InputStream archive, final File targetDirectory, final CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
        try (final CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
            this.expand(c.track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(archive)), targetDirectory);
        }
    }
    
    @Deprecated
    public void expand(final String format, final InputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }
    
    public void expand(final String format, final InputStream archive, final File targetDirectory, final CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
        try (final CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
            this.expand(c.track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(format, archive)), targetDirectory);
        }
    }
    
    @Deprecated
    public void expand(final String format, final SeekableByteChannel archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }
    
    public void expand(final String format, final SeekableByteChannel archive, final File targetDirectory, final CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
        try (final CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
            if (!this.prefersSeekableByteChannel(format)) {
                this.expand(format, c.track(Channels.newInputStream(archive)), targetDirectory);
            }
            else if ("tar".equalsIgnoreCase(format)) {
                this.expand(c.track(new TarFile(archive)), targetDirectory);
            }
            else if ("zip".equalsIgnoreCase(format)) {
                this.expand(c.track(new ZipFile(archive)), targetDirectory);
            }
            else {
                if (!"7z".equalsIgnoreCase(format)) {
                    throw new ArchiveException("Don't know how to handle format " + format);
                }
                this.expand(c.track(new SevenZFile(archive)), targetDirectory);
            }
        }
    }
    
    public void expand(final ArchiveInputStream archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(() -> {
            ArchiveEntry next;
            for (next = archive.getNextEntry(); next != null && !archive.canReadEntryData(next); next = archive.getNextEntry()) {}
            return next;
        }, (entry, out) -> IOUtils.copy(archive, out), targetDirectory);
    }
    
    public void expand(final TarFile archive, final File targetDirectory) throws IOException, ArchiveException {
        final Iterator<TarArchiveEntry> entryIterator = archive.getEntries().iterator();
        this.expand(() -> entryIterator.hasNext() ? entryIterator.next() : null, (entry, out) -> {
            final InputStream in = archive.getInputStream((TarArchiveEntry)entry);
            try {
                IOUtils.copy(in, out);
            }
            catch (final Throwable t) {
                throw t;
            }
            finally {
                if (in != null) {
                    final Throwable t2;
                    if (t2 != null) {
                        try {
                            in.close();
                        }
                        catch (final Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        in.close();
                    }
                }
            }
        }, targetDirectory);
    }
    
    public void expand(final ZipFile archive, final File targetDirectory) throws IOException, ArchiveException {
        final Enumeration<ZipArchiveEntry> entries = archive.getEntries();
        this.expand(() -> {
            ZipArchiveEntry next;
            for (next = (entries.hasMoreElements() ? entries.nextElement() : null); next != null && !archive.canReadEntryData(next); next = (entries.hasMoreElements() ? entries.nextElement() : null)) {}
            return next;
        }, (entry, out) -> {
            final InputStream in = archive.getInputStream((ZipArchiveEntry)entry);
            try {
                IOUtils.copy(in, out);
            }
            catch (final Throwable t) {
                throw t;
            }
            finally {
                if (in != null) {
                    final Throwable t2;
                    if (t2 != null) {
                        try {
                            in.close();
                        }
                        catch (final Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        in.close();
                    }
                }
            }
        }, targetDirectory);
    }
    
    public void expand(final SevenZFile archive, final File targetDirectory) throws IOException, ArchiveException {
        this.expand(archive::getNextEntry, (entry, out) -> {
            final byte[] buffer = new byte[8192];
            while (true) {
                final int n = archive.read(buffer);
                final Object o;
                final Object o2;
                if (o != o2) {
                    out.write(buffer, 0, n);
                }
                else {
                    break;
                }
            }
        }, targetDirectory);
    }
    
    private boolean prefersSeekableByteChannel(final String format) {
        return "tar".equalsIgnoreCase(format) || "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
    }
    
    private void expand(final ArchiveEntrySupplier supplier, final EntryWriter writer, final File targetDirectory) throws IOException {
        String targetDirPath = targetDirectory.getCanonicalPath();
        if (!targetDirPath.endsWith(File.separator)) {
            targetDirPath += File.separator;
        }
        for (ArchiveEntry nextEntry = supplier.getNextReadableEntry(); nextEntry != null; nextEntry = supplier.getNextReadableEntry()) {
            final File f = new File(targetDirectory, nextEntry.getName());
            if (!f.getCanonicalPath().startsWith(targetDirPath)) {
                throw new IOException("Expanding " + nextEntry.getName() + " would create file outside of " + targetDirectory);
            }
            if (nextEntry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("Failed to create directory " + f);
                }
            }
            else {
                final File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                try (final OutputStream o = Files.newOutputStream(f.toPath(), new OpenOption[0])) {
                    writer.writeEntryDataTo(nextEntry, o);
                }
            }
        }
    }
    
    private interface EntryWriter
    {
        void writeEntryDataTo(final ArchiveEntry p0, final OutputStream p1) throws IOException;
    }
    
    private interface ArchiveEntrySupplier
    {
        ArchiveEntry getNextReadableEntry() throws IOException;
    }
}
