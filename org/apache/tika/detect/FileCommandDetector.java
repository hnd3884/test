package org.apache.tika.detect;

import org.apache.commons.io.IOUtils;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import org.slf4j.LoggerFactory;
import org.apache.tika.config.Field;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import org.apache.tika.utils.ProcessUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import org.apache.tika.io.BoundedInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import org.apache.tika.parser.external.ExternalParser;
import org.slf4j.Logger;

public class FileCommandDetector implements Detector
{
    private static final Logger LOGGER;
    private static final long DEFAULT_TIMEOUT_MS = 6000L;
    private static final String DEFAULT_FILE_COMMAND_PATH = "file";
    private static boolean HAS_WARNED;
    private Boolean hasFileCommand;
    private String fileCommandPath;
    private int maxBytes;
    private long timeoutMs;
    
    public FileCommandDetector() {
        this.hasFileCommand = null;
        this.fileCommandPath = "file";
        this.maxBytes = 1000000;
        this.timeoutMs = 6000L;
    }
    
    public static boolean checkHasFile() {
        return checkHasFile("file");
    }
    
    public static boolean checkHasFile(final String fileCommandPath) {
        final String[] commandline = { fileCommandPath, "-v" };
        return ExternalParser.check(commandline, new int[0]);
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        if (this.hasFileCommand == null) {
            this.hasFileCommand = checkHasFile(this.fileCommandPath);
        }
        if (!this.hasFileCommand) {
            if (!FileCommandDetector.HAS_WARNED) {
                FileCommandDetector.LOGGER.warn("'file' command isn't working: '" + this.fileCommandPath + "'");
                FileCommandDetector.HAS_WARNED = true;
            }
            return MediaType.OCTET_STREAM;
        }
        final TikaInputStream tis = TikaInputStream.cast(input);
        if (tis != null) {
            return this.detectOnPath(tis.getPath());
        }
        input.mark(this.maxBytes);
        try (final TemporaryResources tmp = new TemporaryResources()) {
            final Path tmpFile = tmp.createTempFile();
            Files.copy(new BoundedInputStream(this.maxBytes, input), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            return this.detectOnPath(tmpFile);
        }
        finally {
            input.reset();
        }
    }
    
    private MediaType detectOnPath(final Path path) throws IOException {
        final String[] args = { ProcessUtils.escapeCommandLine(this.fileCommandPath), "-b", "--mime-type", ProcessUtils.escapeCommandLine(path.toAbsolutePath().toString()) };
        final ProcessBuilder builder = new ProcessBuilder(args);
        final Process process = builder.start();
        final StringStreamGobbler errorGobbler = new StringStreamGobbler(process.getErrorStream());
        final StringStreamGobbler outGobbler = new StringStreamGobbler(process.getInputStream());
        final Thread errorThread = new Thread(errorGobbler);
        final Thread outThread = new Thread(outGobbler);
        errorThread.start();
        outThread.start();
        process.getErrorStream();
        process.getInputStream();
        boolean finished = false;
        try {
            finished = process.waitFor(this.timeoutMs, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException(new TimeoutException("timed out"));
            }
            final int exitValue = process.exitValue();
            if (exitValue != 0) {
                throw new IOException(new RuntimeException("bad exit value"));
            }
            errorThread.join();
            outThread.join();
        }
        catch (final InterruptedException ex) {}
        final MediaType mt = MediaType.parse(outGobbler.toString().trim());
        if (mt == null) {
            return MediaType.OCTET_STREAM;
        }
        return mt;
    }
    
    @Field
    public void setFilePath(final String fileCommandPath) {
        checkHasFile(this.fileCommandPath = fileCommandPath);
    }
    
    @Field
    public void setMaxBytes(final int maxBytes) {
        this.maxBytes = maxBytes;
    }
    
    @Field
    public void setTimeoutMs(final long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FileCommandDetector.class);
        FileCommandDetector.HAS_WARNED = false;
    }
    
    private static class StringStreamGobbler implements Runnable
    {
        private final BufferedReader reader;
        private final StringBuilder sb;
        
        public StringStreamGobbler(final InputStream is) {
            this.sb = new StringBuilder();
            this.reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), StandardCharsets.UTF_8));
        }
        
        @Override
        public void run() {
            String line = null;
            try {
                while ((line = this.reader.readLine()) != null) {
                    this.sb.append(line);
                    this.sb.append("\n");
                }
            }
            catch (final IOException ex) {}
        }
        
        public void stopGobblingAndDie() {
            IOUtils.closeQuietly((Reader)this.reader);
        }
        
        @Override
        public String toString() {
            return this.sb.toString();
        }
    }
}
