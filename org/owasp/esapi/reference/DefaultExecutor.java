package org.owasp.esapi.reference;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Map;
import java.io.IOException;
import org.owasp.esapi.errors.ExecutorException;
import org.owasp.esapi.ExecuteResult;
import java.util.List;
import java.io.File;
import org.owasp.esapi.codecs.UnixCodec;
import org.owasp.esapi.codecs.WindowsCodec;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.Logger;
import org.owasp.esapi.Executor;

public class DefaultExecutor implements Executor
{
    private static volatile Executor singletonInstance;
    private final Logger logger;
    private Codec codec;
    
    public static Executor getInstance() {
        if (DefaultExecutor.singletonInstance == null) {
            synchronized (DefaultExecutor.class) {
                if (DefaultExecutor.singletonInstance == null) {
                    DefaultExecutor.singletonInstance = new DefaultExecutor();
                }
            }
        }
        return DefaultExecutor.singletonInstance;
    }
    
    private DefaultExecutor() {
        this.logger = ESAPI.getLogger("Executor");
        this.codec = null;
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            this.logger.warning(Logger.SECURITY_SUCCESS, "Using WindowsCodec for Executor. If this is not running on Windows this could allow injection");
            this.codec = new WindowsCodec();
        }
        else {
            this.logger.warning(Logger.SECURITY_SUCCESS, "Using UnixCodec for Executor. If this is not running on Unix this could allow injection");
            this.codec = new UnixCodec();
        }
    }
    
    @Override
    public ExecuteResult executeSystemCommand(final File executable, final List params) throws ExecutorException {
        final File workdir = ESAPI.securityConfiguration().getWorkingDirectory();
        final boolean logParams = false;
        final boolean redirectErrorStream = false;
        return this.executeSystemCommand(executable, params, workdir, this.codec, logParams, redirectErrorStream);
    }
    
    @Override
    public ExecuteResult executeSystemCommand(final File executable, final List params, final File workdir, final Codec codec, final boolean logParams, final boolean redirectErrorStream) throws ExecutorException {
        try {
            if (!executable.exists()) {
                throw new ExecutorException("Execution failure", "No such executable: " + executable);
            }
            if (!executable.isAbsolute()) {
                throw new ExecutorException("Execution failure", "Attempt to invoke an executable using a non-absolute path: " + executable);
            }
            if (!executable.getPath().equals(executable.getCanonicalPath())) {
                throw new ExecutorException("Execution failure", "Attempt to invoke an executable using a non-canonical path: " + executable);
            }
            final List approved = ESAPI.securityConfiguration().getAllowedExecutables();
            if (!approved.contains(executable.getPath())) {
                throw new ExecutorException("Execution failure", "Attempt to invoke executable that is not listed as an approved executable in ESAPI configuration: " + executable.getPath() + " not listed in " + approved);
            }
            for (int i = 0; i < params.size(); ++i) {
                final String param = params.get(i);
                params.set(i, ESAPI.encoder().encodeForOS(codec, param));
            }
            if (!workdir.exists()) {
                throw new ExecutorException("Execution failure", "No such working directory for running executable: " + workdir.getPath());
            }
            params.add(0, executable.getCanonicalPath());
            final ProcessBuilder pb = new ProcessBuilder(params);
            final Map env = pb.environment();
            env.clear();
            pb.directory(workdir);
            pb.redirectErrorStream(redirectErrorStream);
            if (logParams) {
                this.logger.debug(Logger.SECURITY_SUCCESS, "Initiating executable: " + executable + " " + params + " in " + workdir);
            }
            else {
                this.logger.debug(Logger.SECURITY_SUCCESS, "Initiating executable: " + executable + " [sensitive parameters obscured] in " + workdir);
            }
            final StringBuilder outputBuffer = new StringBuilder();
            final StringBuilder errorsBuffer = new StringBuilder();
            final Process process = pb.start();
            try {
                ReadThread errorReader;
                if (!redirectErrorStream) {
                    errorReader = new ReadThread(process.getErrorStream(), errorsBuffer);
                    errorReader.start();
                }
                else {
                    errorReader = null;
                }
                readStream(process.getInputStream(), outputBuffer);
                if (errorReader != null) {
                    errorReader.join();
                    if (errorReader.exception != null) {
                        throw errorReader.exception;
                    }
                }
                process.waitFor();
            }
            catch (final Throwable e) {
                process.destroy();
                throw new ExecutorException("Execution failure", "Exception thrown during execution of system command: " + e.getMessage(), e);
            }
            final String output = outputBuffer.toString();
            final String errors = errorsBuffer.toString();
            final int exitValue = process.exitValue();
            if (errors != null && errors.length() > 0) {
                String logErrors = errors;
                final int MAX_LEN = 256;
                if (logErrors.length() > 256) {
                    logErrors = logErrors.substring(0, 256) + "(truncated at " + 256 + " characters)";
                }
                this.logger.warning(Logger.SECURITY_SUCCESS, "Error during system command: " + logErrors);
            }
            if (exitValue != 0) {
                this.logger.warning(Logger.EVENT_FAILURE, "System command exited with non-zero status: " + exitValue);
            }
            this.logger.debug(Logger.SECURITY_SUCCESS, "System command complete");
            return new ExecuteResult(exitValue, output, errors);
        }
        catch (final IOException e2) {
            throw new ExecutorException("Execution failure", "Exception thrown during execution of system command: " + e2.getMessage(), e2);
        }
    }
    
    private static void readStream(final InputStream is, final StringBuilder sb) throws IOException {
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
    }
    
    private static class ReadThread extends Thread
    {
        volatile IOException exception;
        private final InputStream stream;
        private final StringBuilder buffer;
        
        ReadThread(final InputStream stream, final StringBuilder buffer) {
            this.stream = stream;
            this.buffer = buffer;
        }
        
        @Override
        public void run() {
            try {
                readStream(this.stream, this.buffer);
            }
            catch (final IOException e) {
                this.exception = e;
            }
        }
    }
}
