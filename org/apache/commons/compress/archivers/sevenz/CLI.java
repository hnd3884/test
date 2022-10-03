package org.apache.commons.compress.archivers.sevenz;

import java.util.Iterator;
import java.io.IOException;
import java.io.File;

public class CLI
{
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        final Mode mode = grabMode(args);
        System.out.println(mode.getMessage() + " " + args[0]);
        final File f = new File(args[0]);
        if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
        }
        try (final SevenZFile archive = new SevenZFile(f)) {
            SevenZArchiveEntry ae;
            while ((ae = archive.getNextEntry()) != null) {
                mode.takeAction(archive, ae);
            }
        }
    }
    
    private static void usage() {
        System.out.println("Parameters: archive-name [list]");
    }
    
    private static Mode grabMode(final String[] args) {
        if (args.length < 2) {
            return Mode.LIST;
        }
        return Enum.valueOf(Mode.class, args[1].toUpperCase());
    }
    
    private enum Mode
    {
        LIST("Analysing") {
            @Override
            public void takeAction(final SevenZFile archive, final SevenZArchiveEntry entry) {
                System.out.print(entry.getName());
                if (entry.isDirectory()) {
                    System.out.print(" dir");
                }
                else {
                    System.out.print(" " + entry.getCompressedSize() + "/" + entry.getSize());
                }
                if (entry.getHasLastModifiedDate()) {
                    System.out.print(" " + entry.getLastModifiedDate());
                }
                else {
                    System.out.print(" no last modified date");
                }
                if (!entry.isDirectory()) {
                    System.out.println(" " + this.getContentMethods(entry));
                }
                else {
                    System.out.println();
                }
            }
            
            private String getContentMethods(final SevenZArchiveEntry entry) {
                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (final SevenZMethodConfiguration m : entry.getContentMethods()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    first = false;
                    sb.append(m.getMethod());
                    if (m.getOptions() != null) {
                        sb.append("(").append(m.getOptions()).append(")");
                    }
                }
                return sb.toString();
            }
        };
        
        private final String message;
        
        private Mode(final String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public abstract void takeAction(final SevenZFile p0, final SevenZArchiveEntry p1) throws IOException;
    }
}
