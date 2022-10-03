package com.me.devicemanagement.onpremise.server.extensions.processbuilder;

import java.util.regex.Pattern;
import sun.misc.SharedSecrets;
import java.util.concurrent.TimeUnit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.Map;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import sun.misc.JavaIOFileDescriptorAccess;

public class DMProcessImpl extends Process
{
    private static final String DLL_LIBRARY_FILE_NAME = "SyMNative";
    private static final JavaIOFileDescriptorAccess FDACCESS;
    private static final int VERIFICATION_CMD_BAT = 0;
    private static final int VERIFICATION_WIN32 = 1;
    private static final int VERIFICATION_LEGACY = 2;
    private static final char[][] ESCAPE_VERIFICATION;
    private long handle;
    private OutputStream stdin_stream;
    private InputStream stdout_stream;
    private InputStream stderr_stream;
    private static final int STILL_ACTIVE;
    
    private static FileOutputStream newFileOutputStream(final File f, final boolean append) throws IOException {
        if (append) {
            final String path = f.getPath();
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkWrite(path);
            }
            final long handle = openForAtomicAppend(path);
            final FileDescriptor fd = new FileDescriptor();
            DMProcessImpl.FDACCESS.setHandle(fd, handle);
            return AccessController.doPrivileged((PrivilegedAction<FileOutputStream>)new PrivilegedAction<FileOutputStream>() {
                @Override
                public FileOutputStream run() {
                    return new FileOutputStream(fd);
                }
            });
        }
        return new FileOutputStream(f);
    }
    
    static Process start(final String[] cmdarray, final Map<String, String> environment, final String dir, final DMProcessBuilder.Redirect[] redirects, final boolean redirectErrorStream) throws IOException {
        final String envblock = DMProcessEnvironment.toEnvironmentBlock(environment);
        FileInputStream f0 = null;
        FileOutputStream f2 = null;
        FileOutputStream f3 = null;
        try {
            long[] stdHandles;
            if (redirects == null) {
                stdHandles = new long[] { -1L, -1L, -1L };
            }
            else {
                stdHandles = new long[3];
                if (redirects[0] == DMProcessBuilder.Redirect.PIPE) {
                    stdHandles[0] = -1L;
                }
                else if (redirects[0] == DMProcessBuilder.Redirect.INHERIT) {
                    stdHandles[0] = DMProcessImpl.FDACCESS.getHandle(FileDescriptor.in);
                }
                else {
                    f0 = new FileInputStream(redirects[0].file());
                    stdHandles[0] = DMProcessImpl.FDACCESS.getHandle(f0.getFD());
                }
                if (redirects[1] == DMProcessBuilder.Redirect.PIPE) {
                    stdHandles[1] = -1L;
                }
                else if (redirects[1] == DMProcessBuilder.Redirect.INHERIT) {
                    stdHandles[1] = DMProcessImpl.FDACCESS.getHandle(FileDescriptor.out);
                }
                else {
                    f2 = newFileOutputStream(redirects[1].file(), redirects[1].append());
                    stdHandles[1] = DMProcessImpl.FDACCESS.getHandle(f2.getFD());
                }
                if (redirects[2] == DMProcessBuilder.Redirect.PIPE) {
                    stdHandles[2] = -1L;
                }
                else if (redirects[2] == DMProcessBuilder.Redirect.INHERIT) {
                    stdHandles[2] = DMProcessImpl.FDACCESS.getHandle(FileDescriptor.err);
                }
                else {
                    f3 = newFileOutputStream(redirects[2].file(), redirects[2].append());
                    stdHandles[2] = DMProcessImpl.FDACCESS.getHandle(f3.getFD());
                }
            }
            return new DMProcessImpl(cmdarray, envblock, dir, stdHandles, redirectErrorStream);
        }
        finally {
            try {
                if (f0 != null) {
                    f0.close();
                }
            }
            finally {
                try {
                    if (f2 != null) {
                        f2.close();
                    }
                }
                finally {
                    if (f3 != null) {
                        f3.close();
                    }
                }
            }
        }
    }
    
    private static String[] getTokensFromCommand(final String command) {
        final ArrayList<String> matchList = new ArrayList<String>(8);
        final Matcher regexMatcher = LazyPattern.PATTERN.matcher(command);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList.toArray(new String[matchList.size()]);
    }
    
    private static String createCommandLine(final int verificationType, final String executablePath, final String[] cmd) {
        final StringBuilder cmdbuf = new StringBuilder(80);
        cmdbuf.append(executablePath);
        for (int i = 1; i < cmd.length; ++i) {
            cmdbuf.append(' ');
            final String s = cmd[i];
            if (needsEscaping(verificationType, s)) {
                cmdbuf.append('\"').append(s);
                if (verificationType != 0 && s.endsWith("\\")) {
                    cmdbuf.append('\\');
                }
                cmdbuf.append('\"');
            }
            else {
                cmdbuf.append(s);
            }
        }
        return cmdbuf.toString();
    }
    
    private static boolean isQuoted(final boolean noQuotesInside, final String arg, final String errorMessage) {
        final int lastPos = arg.length() - 1;
        if (lastPos >= 1 && arg.charAt(0) == '\"' && arg.charAt(lastPos) == '\"') {
            if (noQuotesInside && arg.indexOf(34, 1) != lastPos) {
                throw new IllegalArgumentException(errorMessage);
            }
            return true;
        }
        else {
            if (noQuotesInside && arg.indexOf(34) >= 0) {
                throw new IllegalArgumentException(errorMessage);
            }
            return false;
        }
    }
    
    private static boolean needsEscaping(final int verificationType, final String arg) {
        final boolean argIsQuoted = isQuoted(verificationType == 0, arg, "Argument has embedded quote, use the explicit CMD.EXE call.");
        if (!argIsQuoted) {
            final char[] testEscape = DMProcessImpl.ESCAPE_VERIFICATION[verificationType];
            for (int i = 0; i < testEscape.length; ++i) {
                if (arg.indexOf(testEscape[i]) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static String getExecutablePath(final String path) throws IOException {
        final boolean pathIsQuoted = isQuoted(true, path, "Executable name has embedded quote, split the arguments");
        final File fileToRun = new File(pathIsQuoted ? path.substring(1, path.length() - 1) : path);
        return fileToRun.getPath();
    }
    
    private boolean isShellFile(final String executablePath) {
        final String upPath = executablePath.toUpperCase();
        return upPath.endsWith(".CMD") || upPath.endsWith(".BAT");
    }
    
    private String quoteString(final String arg) {
        final StringBuilder argbuf = new StringBuilder(arg.length() + 2);
        return argbuf.append('\"').append(arg).append('\"').toString();
    }
    
    private DMProcessImpl(String[] cmd, final String envblock, final String path, final long[] stdHandles, final boolean redirectErrorStream) throws IOException {
        this.handle = 0L;
        final SecurityManager security = System.getSecurityManager();
        boolean allowAmbiguousCommands = false;
        if (security == null) {
            allowAmbiguousCommands = true;
            final String value = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
            if (value != null) {
                allowAmbiguousCommands = !"false".equalsIgnoreCase(value);
            }
        }
        String cmdstr;
        if (allowAmbiguousCommands) {
            String executablePath = new File(cmd[0]).getPath();
            if (needsEscaping(2, executablePath)) {
                executablePath = this.quoteString(executablePath);
            }
            cmdstr = createCommandLine(2, executablePath, cmd);
        }
        else {
            String executablePath;
            try {
                executablePath = getExecutablePath(cmd[0]);
            }
            catch (final IllegalArgumentException e) {
                final StringBuilder join = new StringBuilder();
                for (final String s : cmd) {
                    join.append(s).append(' ');
                }
                cmd = getTokensFromCommand(join.toString());
                executablePath = getExecutablePath(cmd[0]);
                if (security != null) {
                    security.checkExec(executablePath);
                }
            }
            cmdstr = createCommandLine(this.isShellFile(executablePath) ? 0 : 1, this.quoteString(executablePath), cmd);
        }
        this.handle = create(cmdstr, envblock, path, stdHandles, redirectErrorStream);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (stdHandles[0] == -1L) {
                    DMProcessImpl.this.stdin_stream = DMProcessBuilder.NullOutputStream.INSTANCE;
                }
                else {
                    final FileDescriptor stdin_fd = new FileDescriptor();
                    DMProcessImpl.FDACCESS.setHandle(stdin_fd, stdHandles[0]);
                    DMProcessImpl.this.stdin_stream = new BufferedOutputStream(new FileOutputStream(stdin_fd));
                }
                if (stdHandles[1] == -1L) {
                    DMProcessImpl.this.stdout_stream = DMProcessBuilder.NullInputStream.INSTANCE;
                }
                else {
                    final FileDescriptor stdout_fd = new FileDescriptor();
                    DMProcessImpl.FDACCESS.setHandle(stdout_fd, stdHandles[1]);
                    DMProcessImpl.this.stdout_stream = new BufferedInputStream(new FileInputStream(stdout_fd));
                }
                if (stdHandles[2] == -1L) {
                    DMProcessImpl.this.stderr_stream = DMProcessBuilder.NullInputStream.INSTANCE;
                }
                else {
                    final FileDescriptor stderr_fd = new FileDescriptor();
                    DMProcessImpl.FDACCESS.setHandle(stderr_fd, stdHandles[2]);
                    DMProcessImpl.this.stderr_stream = new FileInputStream(stderr_fd);
                }
                return null;
            }
        });
    }
    
    @Override
    public OutputStream getOutputStream() {
        return this.stdin_stream;
    }
    
    @Override
    public InputStream getInputStream() {
        return this.stdout_stream;
    }
    
    @Override
    public InputStream getErrorStream() {
        return this.stderr_stream;
    }
    
    @Override
    protected void finalize() {
        closeHandle(this.handle);
    }
    
    private static native int getStillActive();
    
    @Override
    public int exitValue() {
        final int exitCode = getExitCodeProcess(this.handle);
        if (exitCode == DMProcessImpl.STILL_ACTIVE) {
            throw new IllegalThreadStateException("process has not exited");
        }
        return exitCode;
    }
    
    private static native int getExitCodeProcess(final long p0);
    
    @Override
    public int waitFor() throws InterruptedException {
        waitForInterruptibly(this.handle);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return this.exitValue();
    }
    
    private static native void waitForInterruptibly(final long p0);
    
    @Override
    public boolean waitFor(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (getExitCodeProcess(this.handle) != DMProcessImpl.STILL_ACTIVE) {
            return true;
        }
        if (timeout <= 0L) {
            return false;
        }
        final long msTimeout = unit.toMillis(timeout);
        waitForTimeoutInterruptibly(this.handle, msTimeout);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return getExitCodeProcess(this.handle) != DMProcessImpl.STILL_ACTIVE;
    }
    
    private static native void waitForTimeoutInterruptibly(final long p0, final long p1);
    
    @Override
    public void destroy() {
        terminateProcess(this.handle);
    }
    
    @Override
    public Process destroyForcibly() {
        this.destroy();
        return this;
    }
    
    private static native void terminateProcess(final long p0);
    
    @Override
    public boolean isAlive() {
        return isProcessAlive(this.handle);
    }
    
    private static native boolean isProcessAlive(final long p0);
    
    private static synchronized native long create(final String p0, final String p1, final String p2, final long[] p3, final boolean p4) throws IOException;
    
    private static native long openForAtomicAppend(final String p0) throws IOException;
    
    private static native boolean closeHandle(final long p0);
    
    static {
        System.loadLibrary("SyMNative");
        FDACCESS = SharedSecrets.getJavaIOFileDescriptorAccess();
        ESCAPE_VERIFICATION = new char[][] { { ' ', '\t', '<', '>', '&', '|', '^' }, { ' ', '\t', '<', '>' }, { ' ', '\t' } };
        STILL_ACTIVE = getStillActive();
    }
    
    private static class LazyPattern
    {
        private static final Pattern PATTERN;
        
        static {
            PATTERN = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
        }
    }
}
