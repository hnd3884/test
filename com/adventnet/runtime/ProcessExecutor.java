package com.adventnet.runtime;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.logging.Logger;

public class ProcessExecutor
{
    private static final Logger LOGGER;
    private static final String CMD_TIMEOUT = "/usr/bin/timeout";
    private static final int STATUS_ERROR = -1;
    
    private ProcessExecutor() {
    }
    
    private static String[] concatString(final String[] first, final String[] second) {
        final String[] both = new String[first.length + second.length];
        for (int i = 0; i < first.length; ++i) {
            both[i] = first[i];
        }
        for (int j = first.length; j < both.length; ++j) {
            both[j] = second[j - first.length];
        }
        return both;
    }
    
    public static Process execute(final String[] command, final int timeout) throws IOException {
        String[] commandToExecute = command;
        String msg = "";
        Process process = null;
        try {
            if (timeout > 0) {
                final String[] timeoutCommand = { "/usr/bin/timeout", Integer.toString(timeout) };
                commandToExecute = concatString(timeoutCommand, command);
            }
            msg = Arrays.toString(command) + ", Timeout: " + timeout + " seconds";
            ProcessExecutor.LOGGER.info("Exec called with Command: " + msg);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandToExecute);
            process = processBuilder.start();
        }
        catch (final IOException e) {
            ProcessExecutor.LOGGER.log(Level.INFO, "Exception while executing command " + msg, e);
            throw e;
        }
        return process;
    }
    
    public static Process execute(final String command, final int timeout) throws IOException {
        final String[] cmd = { command };
        return execute(cmd, timeout);
    }
    
    private static void closeStream(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException e) {
                ProcessExecutor.LOGGER.log(Level.SEVERE, "Exception occurred while closing stream ", e);
            }
        }
    }
    
    private static String readContentFromStream(final InputStream ipStream) {
        final StringBuilder content = new StringBuilder();
        String line = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(ipStream));
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        }
        catch (final Exception e) {
            ProcessExecutor.LOGGER.log(Level.INFO, "Exception occurred while reading content from Input Stream. ", e);
        }
        finally {
            closeStream(bufferedReader);
        }
        return content.toString();
    }
    
    public static int exec(final String[] command, final int timeout) {
        int exitStatus = -1;
        InputStream errorStream = null;
        InputStream outputStream = null;
        try {
            final Process process = execute(command, timeout);
            if (process != null) {
                errorStream = process.getErrorStream();
                final String error = readContentFromStream(errorStream);
                ProcessExecutor.LOGGER.info("Error : " + error);
                outputStream = process.getInputStream();
                final String output = readContentFromStream(outputStream);
                ProcessExecutor.LOGGER.info("Output : " + output);
                exitStatus = process.waitFor();
            }
        }
        catch (final Exception e) {
            ProcessExecutor.LOGGER.log(Level.INFO, "Exception while executing command ", e);
        }
        finally {
            ProcessExecutor.LOGGER.info("Command Exited with status : " + exitStatus);
            closeStream(errorStream);
            closeStream(outputStream);
        }
        return exitStatus;
    }
    
    public static int exec(final String command, final int timeout) {
        final String[] cmd = { command };
        return exec(cmd, timeout);
    }
    
    static {
        LOGGER = Logger.getLogger(ProcessExecutor.class.getName());
    }
}
