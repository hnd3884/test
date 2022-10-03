package jdk.jfr.internal.jfc;

import java.nio.file.NoSuchFileException;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import jdk.jfr.internal.SecuritySupport;
import java.nio.file.Path;
import java.text.ParseException;
import java.io.IOException;
import jdk.jfr.Configuration;
import java.io.Reader;
import java.util.List;

public final class JFC
{
    private static final int BUFFER_SIZE = 8192;
    private static final int MAXIMUM_FILE_SIZE = 1048576;
    private static final int MAX_BUFFER_SIZE = 2147483639;
    private static volatile List<KnownConfiguration> knownConfigurations;
    
    private JFC() {
    }
    
    public static Configuration create(final String s, final Reader reader) throws IOException, ParseException {
        return JFCParser.createConfiguration(s, reader);
    }
    
    private static String nullSafeFileName(final Path path) throws IOException {
        final Path fileName = path.getFileName();
        if (fileName == null) {
            throw new IOException("Path has no file name");
        }
        return fileName.toString();
    }
    
    public static String nameFromPath(final Path path) throws IOException {
        final String nullSafeFileName = nullSafeFileName(path);
        if (nullSafeFileName.endsWith(".jfc")) {
            return nullSafeFileName.substring(0, nullSafeFileName.length() - ".jfc".length());
        }
        return nullSafeFileName;
    }
    
    public static Configuration createKnown(final String s) throws IOException, ParseException {
        for (final KnownConfiguration knownConfiguration : getKnownConfigurations()) {
            if (knownConfiguration.isNamed(s)) {
                return knownConfiguration.getConfigurationFile();
            }
        }
        final SecuritySupport.SafePath jfc_DIRECTORY = SecuritySupport.JFC_DIRECTORY;
        if (jfc_DIRECTORY != null && SecuritySupport.exists(jfc_DIRECTORY)) {
            final Iterator<String> iterator2 = Arrays.asList("", ".jfc").iterator();
            while (iterator2.hasNext()) {
                final SecuritySupport.SafePath safePath = new SecuritySupport.SafePath(jfc_DIRECTORY.toPath().resolveSibling(s + iterator2.next()));
                if (SecuritySupport.exists(safePath) && !SecuritySupport.isDirectory(safePath)) {
                    try (final Reader fileReader = SecuritySupport.newFileReader(safePath)) {
                        return JFCParser.createConfiguration(nameFromPath(safePath.toPath()), fileReader);
                    }
                }
            }
        }
        final Path value = Paths.get(s, new String[0]);
        final String nameFromPath = nameFromPath(value);
        try (final BufferedReader bufferedReader = Files.newBufferedReader(value)) {
            return JFCParser.createConfiguration(nameFromPath, bufferedReader);
        }
    }
    
    private static String readContent(final InputStream inputStream) throws IOException {
        return new String(read(inputStream, 8192), StandardCharsets.UTF_8);
    }
    
    private static byte[] read(final InputStream inputStream, final int n) throws IOException {
        int max = n;
        byte[] copy = new byte[max];
        int n2 = 0;
        while (true) {
            final int read;
            if ((read = inputStream.read(copy, n2, max - n2)) > 0) {
                n2 += read;
            }
            else {
                final int read2;
                if (read < 0 || (read2 = inputStream.read()) < 0) {
                    return (max == n2) ? copy : Arrays.copyOf(copy, n2);
                }
                if (max <= 2147483639 - max) {
                    max = Math.max(max << 1, 8192);
                }
                else {
                    if (max == 2147483639) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    max = 2147483639;
                }
                copy = Arrays.copyOf(copy, max);
                copy[n2++] = (byte)read2;
            }
        }
    }
    
    public static List<Configuration> getConfigurations() {
        final ArrayList list = new ArrayList();
        for (final KnownConfiguration knownConfiguration : getKnownConfigurations()) {
            try {
                list.add(knownConfiguration.getConfigurationFile());
            }
            catch (final IOException ex) {
                Logger.log(LogTag.JFR, LogLevel.WARN, "Could not load configuration " + knownConfiguration.getName() + ". " + ex.getMessage());
            }
            catch (final ParseException ex2) {
                Logger.log(LogTag.JFR, LogLevel.WARN, "Could not parse configuration " + knownConfiguration.getName() + ". " + ex2.getMessage());
            }
        }
        return list;
    }
    
    private static List<KnownConfiguration> getKnownConfigurations() {
        if (JFC.knownConfigurations == null) {
            final ArrayList knownConfigurations = new ArrayList();
            for (final SecuritySupport.SafePath safePath : SecuritySupport.getPredefinedJFCFiles()) {
                try {
                    knownConfigurations.add(new KnownConfiguration(safePath));
                }
                catch (final IOException ex) {}
            }
            JFC.knownConfigurations = knownConfigurations;
        }
        return JFC.knownConfigurations;
    }
    
    public static Configuration getPredefined(final String s) throws IOException, ParseException {
        for (final KnownConfiguration knownConfiguration : getKnownConfigurations()) {
            if (knownConfiguration.getName().equals(s)) {
                return knownConfiguration.getConfigurationFile();
            }
        }
        throw new NoSuchFileException("Could not locate configuration with name " + s);
    }
    
    private static final class KnownConfiguration
    {
        private final String content;
        private final String filename;
        private final String name;
        private Configuration configuration;
        
        public KnownConfiguration(final SecuritySupport.SafePath safePath) throws IOException {
            this.content = readContent(safePath);
            this.name = JFC.nameFromPath(safePath.toPath());
            this.filename = nullSafeFileName(safePath.toPath());
        }
        
        public boolean isNamed(final String s) {
            return this.filename.equals(s) || this.name.equals(s);
        }
        
        public Configuration getConfigurationFile() throws IOException, ParseException {
            if (this.configuration == null) {
                this.configuration = JFCParser.createConfiguration(this.name, this.content);
            }
            return this.configuration;
        }
        
        public String getName() {
            return this.name;
        }
        
        private static String readContent(final SecuritySupport.SafePath safePath) throws IOException {
            if (SecuritySupport.getFileSize(safePath) > 1048576L) {
                throw new IOException("Configuration with more than 1048576 characters can't be read.");
            }
            try (final InputStream fileInputStream = SecuritySupport.newFileInputStream(safePath)) {
                return readContent(fileInputStream);
            }
        }
    }
}
