package org.apache.tika.parser;

import org.xml.sax.ContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.exception.ZeroByteFileException;
import java.io.PipedWriter;
import java.io.BufferedReader;
import java.io.PipedReader;
import java.util.concurrent.Executor;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.io.Writer;
import java.io.Reader;

public class ParsingReader extends Reader
{
    private final Parser parser;
    private final Reader reader;
    private final Writer writer;
    private final InputStream stream;
    private final Metadata metadata;
    private final ParseContext context;
    private transient Throwable throwable;
    
    public ParsingReader(final InputStream stream) throws IOException {
        this(new AutoDetectParser(), stream, new Metadata(), new ParseContext());
        this.context.set(Parser.class, this.parser);
    }
    
    public ParsingReader(final InputStream stream, final String name) throws IOException {
        this(new AutoDetectParser(), stream, getMetadata(name), new ParseContext());
        this.context.set(Parser.class, this.parser);
    }
    
    public ParsingReader(final Path path) throws IOException {
        this(Files.newInputStream(path, new OpenOption[0]), path.getFileName().toString());
    }
    
    public ParsingReader(final File file) throws FileNotFoundException, IOException {
        this(new FileInputStream(file), file.getName());
    }
    
    public ParsingReader(final Parser parser, final InputStream stream, final Metadata metadata, final ParseContext context) throws IOException {
        this(parser, stream, metadata, context, command -> {
            final String name = metadata.get("resourceName");
            String name2;
            if (name != null) {
                name2 = "Apache Tika: " + name;
            }
            else {
                name2 = "Apache Tika";
            }
            final Thread thread = new Thread(command, name2);
            thread.setDaemon(true);
            thread.start();
        });
    }
    
    public ParsingReader(final Parser parser, final InputStream stream, final Metadata metadata, final ParseContext context, final Executor executor) throws IOException {
        this.parser = parser;
        final PipedReader pipedReader = new PipedReader();
        this.reader = new BufferedReader(pipedReader);
        try {
            this.writer = new PipedWriter(pipedReader);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        this.stream = stream;
        this.metadata = metadata;
        this.context = context;
        executor.execute(new ParsingTask());
        this.reader.mark(1);
        this.reader.read();
        this.reader.reset();
    }
    
    private static Metadata getMetadata(final String name) {
        final Metadata metadata = new Metadata();
        if (name != null && name.length() > 0) {
            metadata.set("resourceName", name);
        }
        return metadata;
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.throwable instanceof ZeroByteFileException) {
            return -1;
        }
        if (this.throwable instanceof IOException) {
            throw (IOException)this.throwable;
        }
        if (this.throwable != null) {
            throw new IOException("", this.throwable);
        }
        return this.reader.read(cbuf, off, len);
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
    
    private class ParsingTask implements Runnable
    {
        @Override
        public void run() {
            try {
                final ContentHandler handler = new BodyContentHandler(ParsingReader.this.writer);
                ParsingReader.this.parser.parse(ParsingReader.this.stream, handler, ParsingReader.this.metadata, ParsingReader.this.context);
            }
            catch (final Throwable t) {
                ParsingReader.this.throwable = t;
            }
            try {
                ParsingReader.this.stream.close();
            }
            catch (final Throwable t) {
                if (ParsingReader.this.throwable == null) {
                    ParsingReader.this.throwable = t;
                }
            }
            try {
                ParsingReader.this.writer.close();
            }
            catch (final Throwable t) {
                if (ParsingReader.this.throwable == null) {
                    ParsingReader.this.throwable = t;
                }
            }
        }
    }
}
