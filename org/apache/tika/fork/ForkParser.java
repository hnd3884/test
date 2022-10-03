package org.apache.tika.fork;

import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.parser.ParseContext;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.tika.parser.AutoDetectParser;
import java.util.Arrays;
import java.util.LinkedList;
import org.apache.tika.config.Field;
import java.util.List;
import java.util.Queue;
import java.nio.file.Path;
import org.apache.tika.parser.Parser;
import java.io.Closeable;
import org.apache.tika.parser.AbstractParser;

public class ForkParser extends AbstractParser implements Closeable
{
    private static final long serialVersionUID = -4962742892274663950L;
    private final ClassLoader loader;
    private final Parser parser;
    private final Path tikaBin;
    private final ParserFactoryFactory parserFactoryFactory;
    private final Queue<ForkClient> pool;
    private List<String> java;
    @Field
    private int poolSize;
    private int currentlyInUse;
    @Field
    private long serverPulseMillis;
    @Field
    private long serverParseTimeoutMillis;
    @Field
    private long serverWaitTimeoutMillis;
    @Field
    private int maxFilesProcessedPerClient;
    
    public ForkParser(final Path tikaBin, final ParserFactoryFactory factoryFactory) {
        this.pool = new LinkedList<ForkClient>();
        this.java = Arrays.asList("java", "-Xmx32m", "-Djava.awt.headless=true");
        this.poolSize = 5;
        this.currentlyInUse = 0;
        this.serverPulseMillis = 1000L;
        this.serverParseTimeoutMillis = 60000L;
        this.serverWaitTimeoutMillis = 60000L;
        this.maxFilesProcessedPerClient = -1;
        this.loader = null;
        this.parser = null;
        this.tikaBin = tikaBin;
        this.parserFactoryFactory = factoryFactory;
    }
    
    public ForkParser(final Path tikaBin, final ParserFactoryFactory parserFactoryFactory, final ClassLoader classLoader) {
        this.pool = new LinkedList<ForkClient>();
        this.java = Arrays.asList("java", "-Xmx32m", "-Djava.awt.headless=true");
        this.poolSize = 5;
        this.currentlyInUse = 0;
        this.serverPulseMillis = 1000L;
        this.serverParseTimeoutMillis = 60000L;
        this.serverWaitTimeoutMillis = 60000L;
        this.maxFilesProcessedPerClient = -1;
        this.parser = null;
        this.loader = classLoader;
        this.tikaBin = tikaBin;
        this.parserFactoryFactory = parserFactoryFactory;
    }
    
    public ForkParser(final ClassLoader loader, final Parser parser) {
        this.pool = new LinkedList<ForkClient>();
        this.java = Arrays.asList("java", "-Xmx32m", "-Djava.awt.headless=true");
        this.poolSize = 5;
        this.currentlyInUse = 0;
        this.serverPulseMillis = 1000L;
        this.serverParseTimeoutMillis = 60000L;
        this.serverWaitTimeoutMillis = 60000L;
        this.maxFilesProcessedPerClient = -1;
        if (parser instanceof ForkParser) {
            throw new IllegalArgumentException("The underlying parser of a ForkParser should not be a ForkParser, but a specific implementation.");
        }
        this.tikaBin = null;
        this.parserFactoryFactory = null;
        this.loader = loader;
        this.parser = parser;
    }
    
    public ForkParser(final ClassLoader loader) {
        this(loader, new AutoDetectParser());
    }
    
    public ForkParser() {
        this(ForkParser.class.getClassLoader());
    }
    
    public synchronized int getPoolSize() {
        return this.poolSize;
    }
    
    public synchronized void setPoolSize(final int poolSize) {
        this.poolSize = poolSize;
    }
    
    @Deprecated
    public String getJavaCommand() {
        final StringBuilder sb = new StringBuilder();
        for (final String part : this.getJavaCommandAsList()) {
            sb.append(part).append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public void setJavaCommand(final List<String> java) {
        this.java = new ArrayList<String>(java);
    }
    
    @Deprecated
    public void setJavaCommand(final String java) {
        this.setJavaCommand(Arrays.asList(java.split(" ")));
    }
    
    public List<String> getJavaCommandAsList() {
        return Collections.unmodifiableList((List<? extends String>)this.java);
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.parser.getSupportedTypes(context);
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        if (stream == null) {
            throw new NullPointerException("null stream");
        }
        boolean alive = false;
        final ForkClient client = this.acquireClient();
        Throwable t;
        try {
            ContentHandler contentHandler;
            if (handler instanceof AbstractRecursiveParserWrapperHandler) {
                contentHandler = handler;
            }
            else {
                final ContentHandler[] handlers;
                contentHandler = new TeeContentHandler(handlers);
                handlers = new ContentHandler[] { handler, new MetadataContentHandler(metadata) };
            }
            final ContentHandler tee = contentHandler;
            t = client.call("parse", stream, tee, metadata, context);
            alive = true;
        }
        catch (final TikaException te) {
            alive = true;
            throw te;
        }
        catch (final IOException e) {
            throw new TikaException("Failed to communicate with a forked parser process. The process has most likely crashed due to some error like running out of memory. A new process will be started for the next parsing request.", e);
        }
        finally {
            this.releaseClient(client, alive);
        }
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof SAXException) {
            throw (SAXException)t;
        }
        if (t instanceof TikaException) {
            throw (TikaException)t;
        }
        if (t != null) {
            throw new TikaException("Unexpected error in forked server process", t);
        }
    }
    
    @Override
    public synchronized void close() {
        for (final ForkClient client : this.pool) {
            client.close();
        }
        this.pool.clear();
        this.poolSize = 0;
    }
    
    private synchronized ForkClient acquireClient() throws IOException, TikaException {
        ForkClient client;
        while (true) {
            client = this.pool.poll();
            if (client == null && this.currentlyInUse < this.poolSize) {
                client = this.newClient();
            }
            if (client != null && !client.ping()) {
                client.close();
                client = null;
            }
            if (client != null) {
                break;
            }
            if (this.currentlyInUse < this.poolSize) {
                continue;
            }
            try {
                this.wait();
            }
            catch (final InterruptedException e) {
                throw new TikaException("Interrupted while waiting for a fork parser", e);
            }
        }
        ++this.currentlyInUse;
        return client;
    }
    
    private ForkClient newClient() throws IOException, TikaException {
        final TimeoutLimits timeoutLimits = new TimeoutLimits(this.serverPulseMillis, this.serverParseTimeoutMillis, this.serverWaitTimeoutMillis);
        if (this.loader == null && this.parser == null && this.tikaBin != null && this.parserFactoryFactory != null) {
            return new ForkClient(this.tikaBin, this.parserFactoryFactory, this.java, timeoutLimits);
        }
        if (this.loader != null && this.parser != null && this.tikaBin == null && this.parserFactoryFactory == null) {
            return new ForkClient(this.loader, this.parser, this.java, timeoutLimits);
        }
        if (this.loader != null && this.parser == null && this.tikaBin != null && this.parserFactoryFactory != null) {
            return new ForkClient(this.tikaBin, this.parserFactoryFactory, this.loader, this.java, timeoutLimits);
        }
        throw new IllegalStateException("Unexpected combination of state items");
    }
    
    private synchronized void releaseClient(final ForkClient client, final boolean alive) {
        --this.currentlyInUse;
        if (this.currentlyInUse + this.pool.size() < this.poolSize && alive) {
            if (this.maxFilesProcessedPerClient > 0 && client.getFilesProcessed() >= this.maxFilesProcessedPerClient) {
                client.close();
            }
            else {
                this.pool.offer(client);
            }
            this.notifyAll();
        }
        else {
            client.close();
        }
    }
    
    public void setServerPulseMillis(final long serverPulseMillis) {
        this.serverPulseMillis = serverPulseMillis;
    }
    
    public void setServerParseTimeoutMillis(final long serverParseTimeoutMillis) {
        this.serverParseTimeoutMillis = serverParseTimeoutMillis;
    }
    
    public void setServerWaitTimeoutMillis(final long serverWaitTimeoutMillis) {
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
    }
    
    public void setMaxFilesProcessedPerServer(final int maxFilesProcessedPerClient) {
        this.maxFilesProcessedPerClient = maxFilesProcessedPerClient;
    }
}
