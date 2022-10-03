package org.apache.tika.pipes;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.apache.tika.utils.ProcessUtils;
import java.util.ArrayList;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.StringUtils;
import org.apache.tika.pipes.emitter.EmitData;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.FutureTask;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import java.io.Closeable;

public class PipesClient implements Closeable
{
    private static final Logger LOG;
    private static final int MAX_BYTES_BEFORE_READY = 20000;
    private static AtomicInteger CLIENT_COUNTER;
    private Process process;
    private final PipesConfigBase pipesConfig;
    private DataOutputStream output;
    private DataInputStream input;
    private final int pipesClientId;
    private final ExecutorService executorService;
    private int filesProcessed;
    
    public PipesClient(final PipesConfigBase pipesConfig) {
        this.executorService = Executors.newFixedThreadPool(1);
        this.filesProcessed = 0;
        this.pipesConfig = pipesConfig;
        this.pipesClientId = PipesClient.CLIENT_COUNTER.getAndIncrement();
    }
    
    public int getFilesProcessed() {
        return this.filesProcessed;
    }
    
    private boolean ping() {
        if (this.process == null || !this.process.isAlive()) {
            return false;
        }
        try {
            this.output.write(PipesServer.STATUS.PING.getByte());
            this.output.flush();
            final int ping = this.input.read();
            if (ping == PipesServer.STATUS.PING.getByte()) {
                return true;
            }
        }
        catch (final IOException e) {
            return false;
        }
        return false;
    }
    
    @Override
    public void close() throws IOException {
        if (this.process != null) {
            this.process.destroyForcibly();
        }
        this.executorService.shutdownNow();
    }
    
    public PipesResult process(final FetchEmitTuple t) throws IOException {
        if (!this.ping()) {
            this.restart();
        }
        if (this.pipesConfig.getMaxFilesProcessedPerProcess() > 0 && this.filesProcessed >= this.pipesConfig.getMaxFilesProcessedPerProcess()) {
            PipesClient.LOG.info("restarting server after hitting max files: " + this.filesProcessed);
            this.restart();
        }
        return this.actuallyProcess(t);
    }
    
    private PipesResult actuallyProcess(final FetchEmitTuple t) {
        final long start = System.currentTimeMillis();
        final FutureTask<PipesResult> futureTask = new FutureTask<PipesResult>(() -> {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
            try {
                objectOutputStream.writeObject(t);
            }
            catch (final Throwable t2) {
                throw t2;
            }
            finally {
                if (objectOutputStream != null) {
                    final Throwable t3;
                    if (t3 != null) {
                        try {
                            objectOutputStream.close();
                        }
                        catch (final Throwable t4) {
                            t3.addSuppressed(t4);
                        }
                    }
                    else {
                        objectOutputStream.close();
                    }
                }
            }
            final byte[] bytes = bos.toByteArray();
            this.output.write(PipesServer.STATUS.CALL.getByte());
            this.output.writeInt(bytes.length);
            this.output.write(bytes);
            this.output.flush();
            if (PipesClient.LOG.isTraceEnabled()) {
                PipesClient.LOG.trace("timer -- write tuple: {} ms", (Object)(System.currentTimeMillis() - start));
            }
            final long readStart = System.currentTimeMillis();
            final PipesResult result = this.readResults(t, start);
            if (PipesClient.LOG.isTraceEnabled()) {
                PipesClient.LOG.trace("timer -- read result: {} ms", (Object)(System.currentTimeMillis() - readStart));
            }
            return result;
        });
        try {
            this.executorService.execute(futureTask);
            return futureTask.get(this.pipesConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        catch (final InterruptedException e) {
            this.process.destroyForcibly();
            return PipesResult.INTERRUPTED_EXCEPTION;
        }
        catch (final ExecutionException e2) {
            PipesClient.LOG.error("pipesClientId=" + this.pipesClientId + " execution exception", (Throwable)e2);
            final long elapsed = System.currentTimeMillis() - start;
            this.destroyWithPause();
            if (!this.process.isAlive() && 17 == this.process.exitValue()) {
                PipesClient.LOG.warn("pipesClientId={} server timeout: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), elapsed });
                return PipesResult.TIMEOUT;
            }
            try {
                this.process.waitFor(500L, TimeUnit.MILLISECONDS);
                if (this.process.isAlive()) {
                    PipesClient.LOG.warn("pipesClientId={} crash: {} in {} ms with no exit code available", new Object[] { this.pipesClientId, t.getId(), elapsed });
                }
                else {
                    PipesClient.LOG.warn("pipesClientId={} crash: {} in {} ms with exit code {}", new Object[] { this.pipesClientId, t.getId(), elapsed, this.process.exitValue() });
                }
            }
            catch (final InterruptedException ex) {}
            return PipesResult.UNSPECIFIED_CRASH;
        }
        catch (final TimeoutException e3) {
            final long elapsed = System.currentTimeMillis() - start;
            this.process.destroyForcibly();
            PipesClient.LOG.warn("pipesClientId={} client timeout: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), elapsed });
            return PipesResult.TIMEOUT;
        }
        finally {
            futureTask.cancel(true);
        }
    }
    
    private void destroyWithPause() {
        try {
            this.process.waitFor(200L, TimeUnit.MILLISECONDS);
        }
        catch (final InterruptedException ex) {}
        finally {
            this.process.destroyForcibly();
        }
    }
    
    private PipesResult readResults(final FetchEmitTuple t, final long start) throws IOException {
        final int statusByte = this.input.read();
        final long millis = System.currentTimeMillis() - start;
        PipesServer.STATUS status = null;
        try {
            status = PipesServer.STATUS.lookup(statusByte);
        }
        catch (final IllegalArgumentException e) {
            throw new IOException("problem reading response from server " + status);
        }
        switch (status) {
            case OOM: {
                PipesClient.LOG.warn("pipesClientId={} oom: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return PipesResult.OOM;
            }
            case TIMEOUT: {
                PipesClient.LOG.warn("pipesClientId={} server response timeout: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return PipesResult.TIMEOUT;
            }
            case EMIT_EXCEPTION: {
                PipesClient.LOG.warn("pipesClientId={} emit exception: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.readMessage(PipesResult.STATUS.EMIT_EXCEPTION);
            }
            case EMITTER_NOT_FOUND: {
                PipesClient.LOG.warn("pipesClientId={} emitter not found: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.readMessage(PipesResult.STATUS.NO_EMITTER_FOUND);
            }
            case FETCHER_NOT_FOUND: {
                PipesClient.LOG.warn("pipesClientId={} fetcher not found: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.readMessage(PipesResult.STATUS.NO_FETCHER_FOUND);
            }
            case FETCHER_INITIALIZATION_EXCEPTION: {
                PipesClient.LOG.warn("pipesClientId={} fetcher initialization exception: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.readMessage(PipesResult.STATUS.FETCHER_INITIALIZATION_EXCEPTION);
            }
            case FETCH_EXCEPTION: {
                PipesClient.LOG.warn("pipesClientId={} fetch exception: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.readMessage(PipesResult.STATUS.FETCH_EXCEPTION);
            }
            case PARSE_SUCCESS: {
                PipesClient.LOG.info("pipesClientId={} parse success: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return this.deserializeEmitData();
            }
            case PARSE_EXCEPTION_NO_EMIT: {
                return this.readMessage(PipesResult.STATUS.PARSE_EXCEPTION_NO_EMIT);
            }
            case EMIT_SUCCESS: {
                PipesClient.LOG.info("pipesClientId={} emit success: {} in {} ms", new Object[] { this.pipesClientId, t.getId(), millis });
                return PipesResult.EMIT_SUCCESS;
            }
            case EMIT_SUCCESS_PARSE_EXCEPTION: {
                return this.readMessage(PipesResult.STATUS.EMIT_SUCCESS_PARSE_EXCEPTION);
            }
            case EMPTY_OUTPUT: {
                return PipesResult.EMPTY_OUTPUT;
            }
            case READY:
            case CALL:
            case PING:
            case FAILED_TO_START: {
                throw new IOException("Not expecting this status: " + status);
            }
            default: {
                throw new IOException("Need to handle procesing for: " + status);
            }
        }
    }
    
    private PipesResult readMessage(final PipesResult.STATUS status) throws IOException {
        final int length = this.input.readInt();
        final byte[] bytes = new byte[length];
        this.input.readFully(bytes);
        final String msg = new String(bytes, StandardCharsets.UTF_8);
        return new PipesResult(status, msg);
    }
    
    private PipesResult deserializeEmitData() throws IOException {
        final int length = this.input.readInt();
        final byte[] bytes = new byte[length];
        this.input.readFully(bytes);
        try (final ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            final EmitData emitData = (EmitData)objectInputStream.readObject();
            final String stack = this.getStack(emitData);
            if (StringUtils.isBlank(stack)) {
                return new PipesResult(emitData);
            }
            return new PipesResult(emitData, stack);
        }
        catch (final ClassNotFoundException e) {
            PipesClient.LOG.error("class not found exception deserializing data", (Throwable)e);
            throw new RuntimeException(e);
        }
    }
    
    private String getStack(final EmitData emitData) {
        if (emitData.getMetadataList() == null || emitData.getMetadataList().size() < 1) {
            return "";
        }
        return emitData.getMetadataList().get(0).get(TikaCoreProperties.CONTAINER_EXCEPTION);
    }
    
    private void restart() throws IOException {
        if (this.process != null) {
            this.process.destroyForcibly();
            PipesClient.LOG.info("restarting process");
        }
        else {
            PipesClient.LOG.info("starting process");
        }
        final ProcessBuilder pb = new ProcessBuilder(this.getCommandline());
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        this.process = pb.start();
        this.input = new DataInputStream(this.process.getInputStream());
        this.output = new DataOutputStream(this.process.getOutputStream());
        final FutureTask<Integer> futureTask = new FutureTask<Integer>(() -> {
            int b = this.input.read();
            int read = 1;
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (read < 20000 && b != PipesServer.STATUS.READY.getByte()) {
                if (b == -1) {
                    throw new RuntimeException("Couldn't start server: read EOF before 'ready' byte.\n Make absolutely certain that your logger is not writing to stdout.");
                }
                else {
                    bos.write(b);
                    b = this.input.read();
                    ++read;
                }
            }
            if (read >= 20000) {
                new RuntimeException("Couldn't start server: read too many bytes before 'ready' byte.\n Make absolutely certain that your logger is not writing to stdout.\n Message read: " + new String(bos.toByteArray(), StandardCharsets.ISO_8859_1));
                throw;
            }
            else {
                if (bos.size() > 0) {
                    PipesClient.LOG.warn("From forked process before start byte: {}", (Object)new String(bos.toByteArray(), StandardCharsets.ISO_8859_1));
                }
                return Integer.valueOf(1);
            }
        });
        this.executorService.submit(futureTask);
        try {
            futureTask.get(this.pipesConfig.getStartupTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        catch (final InterruptedException e) {
            this.process.destroyForcibly();
        }
        catch (final ExecutionException e2) {
            PipesClient.LOG.error("couldn't start server", (Throwable)e2);
            this.process.destroyForcibly();
            throw new RuntimeException(e2);
        }
        catch (final TimeoutException e3) {
            PipesClient.LOG.error("couldn't start server in time", (Throwable)e3);
            this.process.destroyForcibly();
            throw new RuntimeException(e3);
        }
        finally {
            futureTask.cancel(true);
        }
    }
    
    private String[] getCommandline() {
        final List<String> configArgs = this.pipesConfig.getForkedJvmArgs();
        boolean hasClassPath = false;
        boolean hasHeadless = false;
        boolean hasExitOnOOM = false;
        boolean hasLog4j = false;
        String origGCString = null;
        String newGCLogString = null;
        for (final String arg : configArgs) {
            if (arg.startsWith("-Djava.awt.headless")) {
                hasHeadless = true;
            }
            if (arg.equals("-cp") || arg.equals("--classpath")) {
                hasClassPath = true;
            }
            if (arg.equals("-XX:+ExitOnOutOfMemoryError") || arg.equals("-XX:+CrashOnOutOfMemoryError")) {
                hasExitOnOOM = true;
            }
            if (arg.startsWith("-Dlog4j.configuration")) {
                hasLog4j = true;
            }
            if (arg.startsWith("-Xloggc:")) {
                origGCString = arg;
                newGCLogString = arg.replace("${pipesClientId}", "id-" + this.pipesClientId);
            }
        }
        if (origGCString != null && newGCLogString != null) {
            configArgs.remove(origGCString);
            configArgs.add(newGCLogString);
        }
        final List<String> commandLine = new ArrayList<String>();
        final String javaPath = this.pipesConfig.getJavaPath();
        commandLine.add(ProcessUtils.escapeCommandLine(javaPath));
        if (!hasClassPath) {
            commandLine.add("-cp");
            commandLine.add(System.getProperty("java.class.path"));
        }
        if (!hasHeadless) {
            commandLine.add("-Djava.awt.headless=true");
        }
        if (hasExitOnOOM) {
            PipesClient.LOG.warn("I notice that you have an exit/crash on OOM. If you run heavy external processes like tesseract, this setting may result in orphaned processes which could be disastrous for performance.");
        }
        if (!hasLog4j) {
            commandLine.add("-Dlog4j.configurationFile=classpath:pipes-fork-server-default-log4j2.xml");
        }
        commandLine.add("-DpipesClientId=" + this.pipesClientId);
        commandLine.addAll(configArgs);
        commandLine.add("org.apache.tika.pipes.PipesServer");
        commandLine.add(ProcessUtils.escapeCommandLine(this.pipesConfig.getTikaConfig().toAbsolutePath().toString()));
        commandLine.add(Long.toString(this.pipesConfig.getMaxForEmitBatchBytes()));
        commandLine.add(Long.toString(this.pipesConfig.getTimeoutMillis()));
        commandLine.add(Long.toString(this.pipesConfig.getShutdownClientAfterMillis()));
        PipesClient.LOG.debug("commandline: {}", (Object)commandLine);
        return commandLine.toArray(new String[0]);
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)PipesClient.class);
        PipesClient.CLIENT_COUNTER = new AtomicInteger(0);
    }
}
