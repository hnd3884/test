package org.apache.commons.compress.archivers;

import java.util.Iterator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;

public final class Lister
{
    private static final ArchiveStreamFactory FACTORY;
    
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        System.out.println("Analysing " + args[0]);
        final File f = new File(args[0]);
        if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
        }
        final String format = (args.length > 1) ? args[1] : detectFormat(f);
        if ("7z".equalsIgnoreCase(format)) {
            list7z(f);
        }
        else if ("zipfile".equals(format)) {
            listZipUsingZipFile(f);
        }
        else if ("tarfile".equals(format)) {
            listZipUsingTarFile(f);
        }
        else {
            listStream(f, args);
        }
    }
    
    private static void listStream(final File f, final String[] args) throws ArchiveException, IOException {
        try (final InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new OpenOption[0]));
             final ArchiveInputStream ais = createArchiveInputStream(args, fis)) {
            System.out.println("Created " + ais.toString());
            ArchiveEntry ae;
            while ((ae = ais.getNextEntry()) != null) {
                System.out.println(ae.getName());
            }
        }
    }
    
    private static ArchiveInputStream createArchiveInputStream(final String[] args, final InputStream fis) throws ArchiveException {
        if (args.length > 1) {
            return Lister.FACTORY.createArchiveInputStream(args[1], fis);
        }
        return Lister.FACTORY.createArchiveInputStream(fis);
    }
    
    private static String detectFormat(final File f) throws ArchiveException, IOException {
        try (final InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new OpenOption[0]))) {
            return ArchiveStreamFactory.detect(fis);
        }
    }
    
    private static void list7z(final File f) throws ArchiveException, IOException {
        try (final SevenZFile z = new SevenZFile(f)) {
            System.out.println("Created " + z.toString());
            ArchiveEntry ae;
            while ((ae = z.getNextEntry()) != null) {
                final String name = (ae.getName() == null) ? (z.getDefaultName() + " (entry name was null)") : ae.getName();
                System.out.println(name);
            }
        }
    }
    
    private static void listZipUsingZipFile(final File f) throws ArchiveException, IOException {
        try (final ZipFile z = new ZipFile(f)) {
            System.out.println("Created " + z.toString());
            final Enumeration<ZipArchiveEntry> en = z.getEntries();
            while (en.hasMoreElements()) {
                System.out.println(en.nextElement().getName());
            }
        }
    }
    
    private static void listZipUsingTarFile(final File f) throws ArchiveException, IOException {
        try (final TarFile t = new TarFile(f)) {
            System.out.println("Created " + t.toString());
            for (final TarArchiveEntry en : t.getEntries()) {
                System.out.println(en.getName());
            }
        }
    }
    
    private static void usage() {
        System.out.println("Parameters: archive-name [archive-type]\n");
        System.out.println("the magic archive-type 'zipfile' prefers ZipFile over ZipArchiveInputStream");
        System.out.println("the magic archive-type 'tarfile' prefers TarFile over TarArchiveInputStream");
    }
    
    static {
        FACTORY = ArchiveStreamFactory.DEFAULT;
    }
}
