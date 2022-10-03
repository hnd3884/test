package org.apache.tika.pipes;

import org.slf4j.LoggerFactory;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.parser.AutoDetectParser;
import java.io.ObjectInputStream;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.ContentHandlerFactory;
import java.util.Collections;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BasicContentHandlerFactory;
import java.util.Iterator;
import org.apache.tika.pipes.fetcher.Fetcher;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.utils.StringUtils;
import org.apache.tika.pipes.emitter.TikaEmitterException;
import java.nio.charset.StandardCharsets;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.Metadata;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import org.apache.tika.pipes.emitter.EmitterManager;
import org.apache.tika.pipes.fetcher.FetcherManager;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.parser.Parser;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.file.Path;
import org.slf4j.Logger;

public class PipesServer implements Runnable
{
    private static final Logger LOG;
    public static final int TIMEOUT_EXIT_CODE = 17;
    private final Object[] lock;
    private long checkForTimeoutMs;
    private final Path tikaConfigPath;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final long maxForEmitBatchBytes;
    private final long serverParseTimeoutMillis;
    private final long serverWaitTimeoutMillis;
    private Parser autoDetectParser;
    private Parser rMetaParser;
    private TikaConfig tikaConfig;
    private FetcherManager fetcherManager;
    private EmitterManager emitterManager;
    private volatile boolean parsing;
    private volatile long since;
    
    public PipesServer(final Path tikaConfigPath, final InputStream in, final PrintStream out, final long maxForEmitBatchBytes, final long serverParseTimeoutMillis, final long serverWaitTimeoutMillis) throws IOException, TikaException, SAXException {
        this.lock = new Object[0];
        this.checkForTimeoutMs = 1000L;
        this.tikaConfigPath = tikaConfigPath;
        this.input = new DataInputStream(in);
        this.output = new DataOutputStream(out);
        this.maxForEmitBatchBytes = maxForEmitBatchBytes;
        this.serverParseTimeoutMillis = serverParseTimeoutMillis;
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
        this.parsing = false;
        this.since = System.currentTimeMillis();
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final Path tikaConfig = Paths.get(args[0], new String[0]);
            final long maxForEmitBatchBytes = Long.parseLong(args[1]);
            final long serverParseTimeoutMillis = Long.parseLong(args[2]);
            final long serverWaitTimeoutMillis = Long.parseLong(args[3]);
            final PipesServer server = new PipesServer(tikaConfig, System.in, System.out, maxForEmitBatchBytes, serverParseTimeoutMillis, serverWaitTimeoutMillis);
            System.setIn(new ByteArrayInputStream(new byte[0]));
            System.setOut(System.err);
            final Thread watchdog = new Thread(server, "Tika Watchdog");
            watchdog.setDaemon(true);
            watchdog.start();
            server.processRequests();
        }
        finally {
            PipesServer.LOG.info("server shutting down");
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this.lock) {
                    final long elapsed = System.currentTimeMillis() - this.since;
                    if (this.parsing && elapsed > this.serverParseTimeoutMillis) {
                        PipesServer.LOG.warn("timeout server; elapsed {}  with {}", (Object)elapsed, (Object)this.serverParseTimeoutMillis);
                        this.exit(17);
                    }
                    else if (!this.parsing && this.serverWaitTimeoutMillis > 0L && elapsed > this.serverWaitTimeoutMillis) {
                        PipesServer.LOG.info("closing down from inactivity");
                        this.exit(0);
                    }
                }
                Thread.sleep(this.checkForTimeoutMs);
            }
        }
        catch (final InterruptedException ex) {}
    }
    
    public void processRequests() {
        try {
            final long start = System.currentTimeMillis();
            this.initializeParser();
            if (PipesServer.LOG.isTraceEnabled()) {
                PipesServer.LOG.trace("timer -- initialize parser: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
            PipesServer.LOG.error("couldn't initialize parser", t);
            try {
                this.output.writeByte(STATUS.FAILED_TO_START.getByte());
                this.output.flush();
            }
            catch (final IOException e) {
                PipesServer.LOG.warn("couldn't notify of failure to start", (Throwable)e);
            }
            return;
        }
        try {
            this.write(STATUS.READY);
            long start = System.currentTimeMillis();
            while (true) {
                final int request = this.input.read();
                if (request == -1) {
                    this.exit(1);
                }
                else if (request == STATUS.PING.getByte()) {
                    if (PipesServer.LOG.isTraceEnabled()) {
                        PipesServer.LOG.trace("timer -- ping: {} ms", (Object)(System.currentTimeMillis() - start));
                    }
                    this.write(STATUS.PING);
                    start = System.currentTimeMillis();
                }
                else {
                    if (request != STATUS.CALL.getByte()) {
                        break;
                    }
                    this.parseOne();
                    if (PipesServer.LOG.isTraceEnabled()) {
                        PipesServer.LOG.trace("timer -- parse one: {} ms", (Object)(System.currentTimeMillis() - start));
                    }
                    start = System.currentTimeMillis();
                }
                this.output.flush();
            }
            throw new IllegalStateException("Unexpected request");
        }
        catch (final Throwable t) {
            PipesServer.LOG.error("main loop error (did the forking process shut down?)", t);
            this.exit(1);
            System.err.flush();
        }
    }
    
    private boolean metadataIsEmpty(final List<Metadata> metadataList) {
        return metadataList == null || metadataList.size() == 0;
    }
    
    private String getContainerStacktrace(final FetchEmitTuple t, final List<Metadata> metadataList) {
        if (metadataList == null || metadataList.size() < 1) {
            return "";
        }
        final String stack = metadataList.get(0).get(TikaCoreProperties.CONTAINER_EXCEPTION);
        return (stack != null) ? stack : "";
    }
    
    private void emit(final String taskId, final EmitData emitData, final String parseExceptionStack) {
        Emitter emitter = null;
        try {
            emitter = this.emitterManager.getEmitter(emitData.getEmitKey().getEmitterName());
        }
        catch (final IllegalArgumentException e) {
            final String noEmitterMsg = this.getNoEmitterMsg(taskId);
            PipesServer.LOG.warn(noEmitterMsg);
            this.write(STATUS.EMITTER_NOT_FOUND, noEmitterMsg);
            return;
        }
        try {
            emitter.emit(emitData.getEmitKey().getEmitKey(), emitData.getMetadataList());
        }
        catch (final IOException | TikaEmitterException e2) {
            PipesServer.LOG.warn("emit exception", (Throwable)e2);
            final String msg = ExceptionUtils.getStackTrace(e2);
            final byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            this.write(STATUS.EMIT_EXCEPTION, bytes);
            return;
        }
        if (StringUtils.isBlank(parseExceptionStack)) {
            this.write(STATUS.EMIT_SUCCESS);
        }
        else {
            this.write(STATUS.EMIT_SUCCESS_PARSE_EXCEPTION, parseExceptionStack.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    private void parseOne() {
        synchronized (this.lock) {
            this.parsing = true;
            this.since = System.currentTimeMillis();
        }
        FetchEmitTuple t = null;
        try {
            long start = System.currentTimeMillis();
            t = this.readFetchEmitTuple();
            if (PipesServer.LOG.isTraceEnabled()) {
                PipesServer.LOG.trace("timer -- read fetchEmitTuple: {} ms", (Object)(System.currentTimeMillis() - start));
            }
            start = System.currentTimeMillis();
            this.actuallyParse(t);
            if (PipesServer.LOG.isTraceEnabled()) {
                PipesServer.LOG.trace("timer -- actually parsed: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        catch (final OutOfMemoryError e) {
            this.handleOOM(t.getId(), e);
            synchronized (this.lock) {
                this.parsing = false;
                this.since = System.currentTimeMillis();
            }
        }
        finally {
            synchronized (this.lock) {
                this.parsing = false;
                this.since = System.currentTimeMillis();
            }
        }
    }
    
    private void actuallyParse(final FetchEmitTuple t) {
        List<Metadata> metadataList = null;
        long start = System.currentTimeMillis();
        Fetcher fetcher = null;
        try {
            fetcher = this.fetcherManager.getFetcher(t.getFetchKey().getFetcherName());
        }
        catch (final IllegalArgumentException e) {
            final String noFetcherMsg = this.getNoFetcherMsg(t.getFetchKey().getFetcherName());
            PipesServer.LOG.warn(noFetcherMsg);
            this.write(STATUS.FETCHER_NOT_FOUND, noFetcherMsg);
            return;
        }
        catch (final IOException | TikaException e2) {
            PipesServer.LOG.warn("Couldn't initialize fetcher for fetch id '" + t.getId() + "'", (Throwable)e2);
            this.write(STATUS.FETCHER_INITIALIZATION_EXCEPTION, ExceptionUtils.getStackTrace(e2));
            return;
        }
        if (PipesServer.LOG.isTraceEnabled()) {
            final long elapsed = System.currentTimeMillis() - start;
            PipesServer.LOG.trace("timer -- got fetcher: {}ms", (Object)elapsed);
        }
        start = System.currentTimeMillis();
        final Metadata metadata = new Metadata();
        try (final InputStream stream = fetcher.fetch(t.getFetchKey().getFetchKey(), metadata)) {
            metadataList = this.parse(t, stream, metadata);
        }
        catch (final SecurityException e3) {
            PipesServer.LOG.error("security exception " + t.getId(), (Throwable)e3);
            throw e3;
        }
        catch (final TikaException | IOException e4) {
            PipesServer.LOG.warn("fetch exception " + t.getId(), (Throwable)e4);
            this.write(STATUS.FETCH_EXCEPTION, ExceptionUtils.getStackTrace(e4));
        }
        if (PipesServer.LOG.isTraceEnabled()) {
            PipesServer.LOG.trace("timer -- to parse: {} ms", (Object)(System.currentTimeMillis() - start));
        }
        if (this.metadataIsEmpty(metadataList)) {
            this.write(STATUS.EMPTY_OUTPUT);
            return;
        }
        start = System.currentTimeMillis();
        final String stack = this.getContainerStacktrace(t, metadataList);
        if (StringUtils.isBlank(stack) || t.getOnParseException() == FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT) {
            this.injectUserMetadata(t.getMetadata(), metadataList);
            EmitKey emitKey = t.getEmitKey();
            if (StringUtils.isBlank(emitKey.getEmitKey())) {
                emitKey = new EmitKey(emitKey.getEmitterName(), t.getFetchKey().getFetchKey());
                t.setEmitKey(emitKey);
            }
            final EmitData emitData = new EmitData(t.getEmitKey(), metadataList);
            if (this.maxForEmitBatchBytes >= 0L && emitData.getEstimatedSizeBytes() >= this.maxForEmitBatchBytes) {
                this.emit(t.getId(), emitData, stack);
                if (PipesServer.LOG.isTraceEnabled()) {
                    PipesServer.LOG.trace("timer -- emitted: {} ms", (Object)(System.currentTimeMillis() - start));
                }
            }
            else {
                this.write(emitData);
                if (PipesServer.LOG.isTraceEnabled()) {
                    PipesServer.LOG.trace("timer -- to write data: {} ms", (Object)(System.currentTimeMillis() - start));
                }
            }
        }
        else {
            this.write(STATUS.PARSE_EXCEPTION_NO_EMIT, stack);
        }
    }
    
    private String getNoFetcherMsg(final String fetcherName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Fetcher '").append(fetcherName).append("'");
        sb.append(" not found.");
        sb.append("\nThe configured FetcherManager supports:");
        int i = 0;
        for (final String f : this.fetcherManager.getSupported()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(f);
        }
        return sb.toString();
    }
    
    private String getNoEmitterMsg(final String emitterName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Emitter '").append(emitterName).append("'");
        sb.append(" not found.");
        sb.append("\nThe configured emitterManager supports:");
        int i = 0;
        for (final String e : this.emitterManager.getSupported()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(e);
        }
        return sb.toString();
    }
    
    private void handleOOM(final String taskId, final OutOfMemoryError oom) {
        this.write(STATUS.OOM);
        PipesServer.LOG.error("oom: " + taskId, (Throwable)oom);
        this.exit(1);
    }
    
    private List<Metadata> parse(final FetchEmitTuple fetchEmitTuple, final InputStream stream, final Metadata metadata) {
        final HandlerConfig handlerConfig = fetchEmitTuple.getHandlerConfig();
        if (handlerConfig.getParseMode() == HandlerConfig.PARSE_MODE.RMETA) {
            return this.parseRecursive(fetchEmitTuple, handlerConfig, stream, metadata);
        }
        return this.parseConcatenated(fetchEmitTuple, handlerConfig, stream, metadata);
    }
    
    private List<Metadata> parseConcatenated(final FetchEmitTuple fetchEmitTuple, final HandlerConfig handlerConfig, final InputStream stream, final Metadata metadata) {
        final ContentHandlerFactory contentHandlerFactory = new BasicContentHandlerFactory(handlerConfig.getType(), handlerConfig.getWriteLimit());
        final ContentHandler handler = contentHandlerFactory.getNewContentHandler();
        final ParseContext parseContext = new ParseContext();
        parseContext.set((Class<PipesServer$1>)DocumentSelector.class, new DocumentSelector() {
            final int maxEmbedded = handlerConfig.maxEmbeddedResources;
            int embedded = 0;
            
            @Override
            public boolean select(final Metadata metadata) {
                return this.maxEmbedded < 0 || this.embedded++ > this.maxEmbedded;
            }
        });
        String containerException = null;
        final long start = System.currentTimeMillis();
        try {
            this.autoDetectParser.parse(stream, handler, metadata, parseContext);
        }
        catch (final SAXException e) {
            containerException = ExceptionUtils.getStackTrace(e);
            PipesServer.LOG.warn("sax problem:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (final EncryptedDocumentException e2) {
            containerException = ExceptionUtils.getStackTrace(e2);
            PipesServer.LOG.warn("encrypted document:" + fetchEmitTuple.getId(), (Throwable)e2);
        }
        catch (final SecurityException e3) {
            PipesServer.LOG.warn("security exception:" + fetchEmitTuple.getId(), (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            containerException = ExceptionUtils.getStackTrace(e4);
            PipesServer.LOG.warn("parse exception: " + fetchEmitTuple.getId(), (Throwable)e4);
        }
        finally {
            metadata.add(TikaCoreProperties.TIKA_CONTENT, handler.toString());
            if (containerException != null) {
                metadata.add(TikaCoreProperties.CONTAINER_EXCEPTION, containerException);
            }
            try {
                this.tikaConfig.getMetadataFilter().filter(metadata);
            }
            catch (final TikaException e5) {
                PipesServer.LOG.warn("exception mapping metadata", (Throwable)e5);
            }
            if (PipesServer.LOG.isTraceEnabled()) {
                PipesServer.LOG.trace("timer -- parse only time: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        return Collections.singletonList(metadata);
    }
    
    private List<Metadata> parseRecursive(final FetchEmitTuple fetchEmitTuple, final HandlerConfig handlerConfig, final InputStream stream, final Metadata metadata) {
        final RecursiveParserWrapperHandler handler = new RecursiveParserWrapperHandler(new BasicContentHandlerFactory(handlerConfig.getType(), handlerConfig.getWriteLimit()), handlerConfig.getMaxEmbeddedResources(), this.tikaConfig.getMetadataFilter());
        final ParseContext parseContext = new ParseContext();
        final long start = System.currentTimeMillis();
        try {
            this.rMetaParser.parse(stream, handler, metadata, parseContext);
        }
        catch (final SAXException e) {
            PipesServer.LOG.warn("sax problem:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (final EncryptedDocumentException e2) {
            PipesServer.LOG.warn("encrypted document:" + fetchEmitTuple.getId(), (Throwable)e2);
        }
        catch (final SecurityException e3) {
            PipesServer.LOG.warn("security exception:" + fetchEmitTuple.getId(), (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            PipesServer.LOG.warn("parse exception: " + fetchEmitTuple.getId(), (Throwable)e4);
        }
        finally {
            if (PipesServer.LOG.isTraceEnabled()) {
                PipesServer.LOG.trace("timer -- parse only time: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        return handler.getMetadataList();
    }
    
    private void injectUserMetadata(final Metadata userMetadata, final List<Metadata> metadataList) {
        for (final String n : userMetadata.names()) {
            metadataList.get(0).set(n, null);
            for (final String val : userMetadata.getValues(n)) {
                metadataList.get(0).add(n, val);
            }
        }
    }
    
    private void exit(final int exitCode) {
        if (exitCode != 0) {
            PipesServer.LOG.error("exiting: {}", (Object)exitCode);
        }
        else {
            PipesServer.LOG.info("exiting: {}", (Object)exitCode);
        }
        System.exit(exitCode);
    }
    
    private FetchEmitTuple readFetchEmitTuple() {
        try {
            final int length = this.input.readInt();
            final byte[] bytes = new byte[length];
            this.input.readFully(bytes);
            try (final ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (FetchEmitTuple)objectInputStream.readObject();
            }
        }
        catch (final IOException e) {
            PipesServer.LOG.error("problem reading tuple", (Throwable)e);
            this.exit(1);
        }
        catch (final ClassNotFoundException e2) {
            PipesServer.LOG.error("can't find class?!", (Throwable)e2);
            this.exit(1);
        }
        return null;
    }
    
    private void initializeParser() throws TikaException, IOException, SAXException {
        this.tikaConfig = new TikaConfig(this.tikaConfigPath);
        this.fetcherManager = FetcherManager.load(this.tikaConfigPath);
        this.emitterManager = EmitterManager.load(this.tikaConfigPath);
        this.autoDetectParser = new AutoDetectParser(this.tikaConfig);
        this.rMetaParser = new RecursiveParserWrapper(this.autoDetectParser);
    }
    
    private void write(final EmitData emitData) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (final ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos)) {
                objectOutputStream.writeObject(emitData);
            }
            this.write(STATUS.PARSE_SUCCESS, bos.toByteArray());
        }
        catch (final IOException e) {
            PipesServer.LOG.error("problem writing emit data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }
    
    private void write(final STATUS status, final String msg) {
        final byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        this.write(status, bytes);
    }
    
    private void write(final STATUS status, final byte[] bytes) {
        try {
            final int len = bytes.length;
            this.output.write(status.getByte());
            this.output.writeInt(len);
            this.output.write(bytes);
            this.output.flush();
        }
        catch (final IOException e) {
            PipesServer.LOG.error("problem writing data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }
    
    private void write(final STATUS status) {
        try {
            this.output.write(status.getByte());
            this.output.flush();
        }
        catch (final IOException e) {
            PipesServer.LOG.error("problem writing data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)PipesServer.class);
    }
    
    public enum STATUS
    {
        READY, 
        CALL, 
        PING, 
        FAILED_TO_START, 
        FETCHER_NOT_FOUND, 
        EMITTER_NOT_FOUND, 
        FETCHER_INITIALIZATION_EXCEPTION, 
        FETCH_EXCEPTION, 
        PARSE_SUCCESS, 
        PARSE_EXCEPTION_NO_EMIT, 
        EMIT_SUCCESS, 
        EMIT_SUCCESS_PARSE_EXCEPTION, 
        EMIT_EXCEPTION, 
        OOM, 
        TIMEOUT, 
        EMPTY_OUTPUT;
        
        byte getByte() {
            return (byte)(this.ordinal() + 1);
        }
        
        public static STATUS lookup(final int val) {
            final int i = val - 1;
            if (i < 0) {
                throw new IllegalArgumentException("byte must be > 0");
            }
            final STATUS[] statuses = values();
            if (i >= statuses.length) {
                throw new IllegalArgumentException("byte with index " + i + " must be < " + statuses.length);
            }
            return statuses[i];
        }
    }
}
