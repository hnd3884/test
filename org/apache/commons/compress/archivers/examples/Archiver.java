package org.apache.commons.compress.archivers.examples;

import org.apache.commons.compress.utils.IOUtils;
import java.nio.file.SimpleFileVisitor;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.nio.file.OpenOption;
import java.util.Objects;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import java.nio.file.FileVisitor;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.ArchiveException;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.io.File;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import java.nio.file.FileVisitOption;
import java.util.EnumSet;

public class Archiver
{
    public static final EnumSet<FileVisitOption> EMPTY_FileVisitOption;
    
    public void create(final ArchiveOutputStream target, final File directory) throws IOException, ArchiveException {
        this.create(target, directory.toPath(), Archiver.EMPTY_FileVisitOption, new LinkOption[0]);
    }
    
    public void create(final ArchiveOutputStream target, final Path directory, final EnumSet<FileVisitOption> fileVisitOptions, final LinkOption... linkOptions) throws IOException {
        Files.walkFileTree(directory, fileVisitOptions, Integer.MAX_VALUE, new ArchiverFileVisitor(target, directory, linkOptions));
        target.finish();
    }
    
    public void create(final ArchiveOutputStream target, final Path directory) throws IOException {
        this.create(target, directory, Archiver.EMPTY_FileVisitOption, new LinkOption[0]);
    }
    
    public void create(final SevenZOutputFile target, final File directory) throws IOException {
        this.create(target, directory.toPath());
    }
    
    public void create(final SevenZOutputFile target, final Path directory) throws IOException {
        Files.walkFileTree(directory, new ArchiverFileVisitor(null, directory, new LinkOption[0]) {
            @Override
            protected FileVisitResult visit(final Path path, final BasicFileAttributes attrs, final boolean isFile) throws IOException {
                Objects.requireNonNull(path);
                Objects.requireNonNull(attrs);
                final String name = directory.relativize(path).toString().replace('\\', '/');
                if (!name.isEmpty()) {
                    final ArchiveEntry archiveEntry = target.createArchiveEntry(path, (isFile || name.endsWith("/")) ? name : (name + "/"), new LinkOption[0]);
                    target.putArchiveEntry(archiveEntry);
                    if (isFile) {
                        target.write(path, new OpenOption[0]);
                    }
                    target.closeArchiveEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        target.finish();
    }
    
    public void create(final String format, final File target, final File directory) throws IOException, ArchiveException {
        this.create(format, target.toPath(), directory.toPath());
    }
    
    @Deprecated
    public void create(final String format, final OutputStream target, final File directory) throws IOException, ArchiveException {
        this.create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
    }
    
    public void create(final String format, final OutputStream target, final File directory, final CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
        try (final CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
            this.create(c.track(ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, target)), directory);
        }
    }
    
    public void create(final String format, final Path target, final Path directory) throws IOException, ArchiveException {
        if (this.prefersSeekableByteChannel(format)) {
            try (final SeekableByteChannel channel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                this.create(format, channel, directory);
                return;
            }
        }
        try (final ArchiveOutputStream outputStream = ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, Files.newOutputStream(target, new OpenOption[0]))) {
            this.create(outputStream, directory, Archiver.EMPTY_FileVisitOption, new LinkOption[0]);
        }
    }
    
    @Deprecated
    public void create(final String format, final SeekableByteChannel target, final File directory) throws IOException, ArchiveException {
        this.create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
    }
    
    public void create(final String format, final SeekableByteChannel target, final File directory, final CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
        try (final CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
            if (!this.prefersSeekableByteChannel(format)) {
                this.create(format, c.track(Channels.newOutputStream(target)), directory);
            }
            else if ("zip".equalsIgnoreCase(format)) {
                this.create(c.track(new ZipArchiveOutputStream(target)), directory);
            }
            else {
                if (!"7z".equalsIgnoreCase(format)) {
                    throw new ArchiveException("Don't know how to handle format " + format);
                }
                this.create(c.track(new SevenZOutputFile(target)), directory);
            }
        }
    }
    
    public void create(final String format, final SeekableByteChannel target, final Path directory) throws IOException {
        if ("7z".equalsIgnoreCase(format)) {
            try (final SevenZOutputFile sevenZFile = new SevenZOutputFile(target)) {
                this.create(sevenZFile, directory);
            }
        }
        else {
            if (!"zip".equalsIgnoreCase(format)) {
                throw new IllegalStateException(format);
            }
            try (final ArchiveOutputStream archiveOutputStream = new ZipArchiveOutputStream(target)) {
                this.create(archiveOutputStream, directory, Archiver.EMPTY_FileVisitOption, new LinkOption[0]);
            }
        }
    }
    
    private boolean prefersSeekableByteChannel(final String format) {
        return "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
    }
    
    static {
        EMPTY_FileVisitOption = EnumSet.noneOf(FileVisitOption.class);
    }
    
    private static class ArchiverFileVisitor extends SimpleFileVisitor<Path>
    {
        private final ArchiveOutputStream target;
        private final Path directory;
        private final LinkOption[] linkOptions;
        
        private ArchiverFileVisitor(final ArchiveOutputStream target, final Path directory, final LinkOption... linkOptions) {
            this.target = target;
            this.directory = directory;
            this.linkOptions = ((linkOptions == null) ? IOUtils.EMPTY_LINK_OPTIONS : linkOptions.clone());
        }
        
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return this.visit(dir, attrs, false);
        }
        
        protected FileVisitResult visit(final Path path, final BasicFileAttributes attrs, final boolean isFile) throws IOException {
            Objects.requireNonNull(path);
            Objects.requireNonNull(attrs);
            final String name = this.directory.relativize(path).toString().replace('\\', '/');
            if (!name.isEmpty()) {
                final ArchiveEntry archiveEntry = this.target.createArchiveEntry(path, (isFile || name.endsWith("/")) ? name : (name + "/"), this.linkOptions);
                this.target.putArchiveEntry(archiveEntry);
                if (isFile) {
                    Files.copy(path, this.target);
                }
                this.target.closeArchiveEntry();
            }
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            return this.visit(file, attrs, true);
        }
    }
}
