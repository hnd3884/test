package org.apache.tika.parser.external;

import java.lang.invoke.SerializedLambda;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileInputStream;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.util.Collection;
import java.util.HashSet;
import org.apache.tika.parser.ParseContext;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.Map;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.parser.AbstractParser;

public class ExternalParser extends AbstractParser
{
    public static final String INPUT_FILE_TOKEN = "${INPUT}";
    public static final String OUTPUT_FILE_TOKEN = "${OUTPUT}";
    private static final long serialVersionUID = -1079128990650687037L;
    private final long timeoutMs = 60000L;
    private Set<MediaType> supportedTypes;
    private Map<Pattern, String> metadataPatterns;
    private String[] command;
    private LineConsumer ignoredLineConsumer;
    
    public ExternalParser() {
        this.supportedTypes = Collections.emptySet();
        this.metadataPatterns = null;
        this.command = new String[] { "cat" };
        this.ignoredLineConsumer = LineConsumer.NULL;
    }
    
    private static void ignoreStream(final InputStream stream) {
        ignoreStream(stream, true);
    }
    
    private static Thread ignoreStream(final InputStream stream, final boolean waitForDeath) {
        final Thread t = new Thread(() -> {
            try {
                IOUtils.copy(stream, (OutputStream)NullOutputStream.NULL_OUTPUT_STREAM);
            }
            catch (final IOException ex2) {}
            finally {
                IOUtils.closeQuietly(stream);
            }
            return;
        });
        t.start();
        if (waitForDeath) {
            try {
                t.join();
            }
            catch (final InterruptedException ex) {}
        }
        return t;
    }
    
    public static boolean check(final String checkCmd, final int... errorValue) {
        return check(new String[] { checkCmd }, errorValue);
    }
    
    public static boolean check(final String[] checkCmd, int... errorValue) {
        if (errorValue.length == 0) {
            errorValue = new int[] { 127 };
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(checkCmd);
            final Thread stdErrSuckerThread = ignoreStream(process.getErrorStream(), false);
            final Thread stdOutSuckerThread = ignoreStream(process.getInputStream(), false);
            stdErrSuckerThread.join();
            stdOutSuckerThread.join();
            final boolean finished = process.waitFor(60000L, TimeUnit.MILLISECONDS);
            if (!finished) {
                throw new TimeoutException();
            }
            final int result = process.exitValue();
            for (final int err : errorValue) {
                if (result == err) {
                    return false;
                }
            }
            return true;
        }
        catch (final IOException | InterruptedException | TimeoutException e) {
            return false;
        }
        catch (final SecurityException se) {
            throw se;
        }
        catch (final Error err2) {
            if (err2.getMessage() != null && (err2.getMessage().contains("posix_spawn") || err2.getMessage().contains("UNIXProcess"))) {
                return false;
            }
            throw err2;
        }
        finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.getSupportedTypes();
    }
    
    public Set<MediaType> getSupportedTypes() {
        return this.supportedTypes;
    }
    
    public void setSupportedTypes(final Set<MediaType> supportedTypes) {
        this.supportedTypes = Collections.unmodifiableSet((Set<? extends MediaType>)new HashSet<MediaType>(supportedTypes));
    }
    
    public String[] getCommand() {
        return this.command;
    }
    
    public void setCommand(final String... command) {
        this.command = command;
    }
    
    public LineConsumer getIgnoredLineConsumer() {
        return this.ignoredLineConsumer;
    }
    
    public void setIgnoredLineConsumer(final LineConsumer ignoredLineConsumer) {
        this.ignoredLineConsumer = ignoredLineConsumer;
    }
    
    public Map<Pattern, String> getMetadataExtractionPatterns() {
        return this.metadataPatterns;
    }
    
    public void setMetadataExtractionPatterns(final Map<Pattern, String> patterns) {
        this.metadataPatterns = patterns;
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        final TemporaryResources tmp = new TemporaryResources();
        try {
            this.parse(TikaInputStream.get(stream, tmp), xhtml, metadata, tmp);
        }
        finally {
            tmp.dispose();
        }
    }
    
    private void parse(final TikaInputStream stream, final XHTMLContentHandler xhtml, final Metadata metadata, final TemporaryResources tmp) throws IOException, SAXException, TikaException {
        boolean inputToStdIn = true;
        boolean outputFromStdOut = true;
        final boolean hasPatterns = this.metadataPatterns != null && !this.metadataPatterns.isEmpty();
        File output = null;
        String[] cmd;
        if (this.command.length == 1) {
            cmd = this.command[0].split(" ");
        }
        else {
            cmd = new String[this.command.length];
            System.arraycopy(this.command, 0, cmd, 0, this.command.length);
        }
        for (int i = 0; i < cmd.length; ++i) {
            if (cmd[i].contains("${INPUT}")) {
                cmd[i] = cmd[i].replace("${INPUT}", stream.getFile().getPath());
                inputToStdIn = false;
            }
            if (cmd[i].contains("${OUTPUT}")) {
                output = tmp.createTemporaryFile();
                outputFromStdOut = false;
                cmd[i] = cmd[i].replace("${OUTPUT}", output.getPath());
            }
        }
        Process process = null;
        try {
            if (cmd.length == 1) {
                process = Runtime.getRuntime().exec(cmd[0]);
            }
            else {
                process = Runtime.getRuntime().exec(cmd);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        try {
            if (inputToStdIn) {
                this.sendInput(process, (InputStream)stream);
            }
            else {
                process.getOutputStream().close();
            }
            final InputStream out = process.getInputStream();
            final InputStream err = process.getErrorStream();
            if (hasPatterns) {
                this.extractMetadata(err, metadata);
                if (outputFromStdOut) {
                    this.extractOutput(out, xhtml);
                }
                else {
                    this.extractMetadata(out, metadata);
                }
            }
            else {
                ignoreStream(err);
                if (outputFromStdOut) {
                    this.extractOutput(out, xhtml);
                }
                else {
                    ignoreStream(out);
                }
            }
        }
        finally {
            try {
                process.waitFor();
            }
            catch (final InterruptedException ex) {}
        }
        if (!outputFromStdOut) {
            this.extractOutput(new FileInputStream(output), xhtml);
        }
    }
    
    private void extractOutput(final InputStream stream, final XHTMLContentHandler xhtml) throws SAXException, IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            xhtml.startDocument();
            xhtml.startElement("p");
            final char[] buffer = new char[1024];
            for (int n = reader.read(buffer); n != -1; n = reader.read(buffer)) {
                xhtml.characters(buffer, 0, n);
            }
            xhtml.endElement("p");
            xhtml.endDocument();
        }
    }
    
    private void sendInput(final Process process, final InputStream stream) {
        final Thread t = new Thread(() -> {
            final OutputStream stdin = process.getOutputStream();
            try {
                IOUtils.copy(stream, stdin);
            }
            catch (final IOException ex2) {}
            return;
        });
        t.start();
        try {
            t.join();
        }
        catch (final InterruptedException ex) {}
    }
    
    private void extractMetadata(final InputStream stream, final Metadata metadata) {
        final Thread t = new Thread(() -> {
            new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            final BufferedReader bufferedReader;
            final BufferedReader reader = bufferedReader;
            try {
                while (true) {
                    final String line = reader.readLine();
                    final Object o;
                    if (o != null) {
                        boolean consumed = false;
                        this.metadataPatterns.keySet().iterator();
                        final Iterator iterator;
                        while (iterator.hasNext()) {
                            final Pattern p = iterator.next();
                            final Matcher m = p.matcher(line);
                            if (m.find()) {
                                consumed = true;
                                if (this.metadataPatterns.get(p) != null && !this.metadataPatterns.get(p).equals("")) {
                                    metadata.add(this.metadataPatterns.get(p), m.group(1));
                                }
                                else {
                                    metadata.add(m.group(1), m.group(2));
                                }
                            }
                        }
                        if (!consumed) {
                            this.ignoredLineConsumer.consume(line);
                        }
                        else {
                            continue;
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            catch (final IOException ex2) {}
            finally {
                IOUtils.closeQuietly((Reader)reader);
                IOUtils.closeQuietly(stream);
            }
            return;
        });
        t.start();
        try {
            t.join();
        }
        catch (final InterruptedException ex) {}
    }
    
    public interface LineConsumer extends Serializable
    {
        public static final LineConsumer NULL = line -> {};
        
        void consume(final String p0);
    }
}
