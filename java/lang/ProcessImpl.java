package java.lang;

import java.util.regex.Pattern;
import sun.misc.SharedSecrets;
import java.util.concurrent.TimeUnit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import sun.security.action.GetPropertyAction;
import java.util.Locale;
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

final class ProcessImpl extends Process
{
    private static final JavaIOFileDescriptorAccess fdAccess;
    private static final int VERIFICATION_CMD_BAT = 0;
    private static final int VERIFICATION_WIN32 = 1;
    private static final int VERIFICATION_WIN32_SAFE = 2;
    private static final int VERIFICATION_LEGACY = 3;
    private static final char[][] ESCAPE_VERIFICATION;
    private static final char DOUBLEQUOTE = '\"';
    private static final char BACKSLASH = '\\';
    private long handle;
    private OutputStream stdin_stream;
    private InputStream stdout_stream;
    private InputStream stderr_stream;
    private static final int STILL_ACTIVE;
    
    private static FileOutputStream newFileOutputStream(final File file, final boolean b) throws IOException {
        if (b) {
            final String path = file.getPath();
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkWrite(path);
            }
            final long openForAtomicAppend = openForAtomicAppend(path);
            final FileDescriptor fileDescriptor = new FileDescriptor();
            ProcessImpl.fdAccess.setHandle(fileDescriptor, openForAtomicAppend);
            return AccessController.doPrivileged((PrivilegedAction<FileOutputStream>)new PrivilegedAction<FileOutputStream>() {
                @Override
                public FileOutputStream run() {
                    return new FileOutputStream(fileDescriptor);
                }
            });
        }
        return new FileOutputStream(file);
    }
    
    static Process start(final String[] array, final Map<String, String> map, final String s, final ProcessBuilder.Redirect[] array2, final boolean b) throws IOException {
        final String environmentBlock = ProcessEnvironment.toEnvironmentBlock(map);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileOutputStream fileOutputStream2 = null;
        try {
            long[] array3;
            if (array2 == null) {
                array3 = new long[] { -1L, -1L, -1L };
            }
            else {
                array3 = new long[3];
                if (array2[0] == ProcessBuilder.Redirect.PIPE) {
                    array3[0] = -1L;
                }
                else if (array2[0] == ProcessBuilder.Redirect.INHERIT) {
                    array3[0] = ProcessImpl.fdAccess.getHandle(FileDescriptor.in);
                }
                else {
                    fileInputStream = new FileInputStream(array2[0].file());
                    array3[0] = ProcessImpl.fdAccess.getHandle(fileInputStream.getFD());
                }
                if (array2[1] == ProcessBuilder.Redirect.PIPE) {
                    array3[1] = -1L;
                }
                else if (array2[1] == ProcessBuilder.Redirect.INHERIT) {
                    array3[1] = ProcessImpl.fdAccess.getHandle(FileDescriptor.out);
                }
                else {
                    fileOutputStream = newFileOutputStream(array2[1].file(), array2[1].append());
                    array3[1] = ProcessImpl.fdAccess.getHandle(fileOutputStream.getFD());
                }
                if (array2[2] == ProcessBuilder.Redirect.PIPE) {
                    array3[2] = -1L;
                }
                else if (array2[2] == ProcessBuilder.Redirect.INHERIT) {
                    array3[2] = ProcessImpl.fdAccess.getHandle(FileDescriptor.err);
                }
                else {
                    fileOutputStream2 = newFileOutputStream(array2[2].file(), array2[2].append());
                    array3[2] = ProcessImpl.fdAccess.getHandle(fileOutputStream2.getFD());
                }
            }
            return new ProcessImpl(array, environmentBlock, s, array3, b);
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                finally {
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                }
            }
        }
    }
    
    private static String[] getTokensFromCommand(final String s) {
        final ArrayList list = new ArrayList(8);
        final Matcher matcher = LazyPattern.PATTERN.matcher(s);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list.toArray(new String[list.size()]);
    }
    
    private static String createCommandLine(final int n, final String s, final String[] array) {
        final StringBuilder sb = new StringBuilder(80);
        sb.append(s);
        for (int i = 1; i < array.length; ++i) {
            sb.append(' ');
            final String s2 = array[i];
            if (needsEscaping(n, s2)) {
                sb.append('\"');
                if (n == 2) {
                    for (int length = s2.length(), j = 0; j < length; ++j) {
                        final char char1 = s2.charAt(j);
                        if (char1 == '\"') {
                            int countLeadingBackslash = countLeadingBackslash(n, s2, j);
                            while (countLeadingBackslash-- > 0) {
                                sb.append('\\');
                            }
                            sb.append('\\');
                        }
                        sb.append(char1);
                    }
                }
                else {
                    sb.append(s2);
                }
                int countLeadingBackslash2 = countLeadingBackslash(n, s2, s2.length());
                while (countLeadingBackslash2-- > 0) {
                    sb.append('\\');
                }
                sb.append('\"');
            }
            else {
                sb.append(s2);
            }
        }
        return sb.toString();
    }
    
    private static String unQuote(final String s) {
        final int length = s.length();
        return (length >= 2 && s.charAt(0) == '\"' && s.charAt(length - 1) == '\"') ? s.substring(1, length - 1) : s;
    }
    
    private static boolean needsEscaping(final int n, final String s) {
        final String unQuote = unQuote(s);
        final boolean b = !s.equals(unQuote);
        final boolean b2 = unQuote.indexOf(34) >= 0;
        switch (n) {
            case 0: {
                if (b2) {
                    throw new IllegalArgumentException("Argument has embedded quote, use the explicit CMD.EXE call.");
                }
                break;
            }
            case 2: {
                if (b && b2) {
                    throw new IllegalArgumentException("Malformed argument has embedded quote: " + unQuote);
                }
                break;
            }
        }
        if (!b) {
            final char[] array = ProcessImpl.ESCAPE_VERIFICATION[n];
            for (int i = 0; i < array.length; ++i) {
                if (s.indexOf(array[i]) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static String getExecutablePath(final String s) throws IOException {
        final String unQuote = unQuote(s);
        if (unQuote.indexOf(34) >= 0) {
            throw new IllegalArgumentException("Executable name has embedded quote, split the arguments: " + unQuote);
        }
        return new File(unQuote).getPath();
    }
    
    private boolean isExe(final String s) {
        final String upperCase = new File(s).getName().toUpperCase(Locale.ROOT);
        return upperCase.endsWith(".EXE") || upperCase.indexOf(46) < 0;
    }
    
    private boolean isShellFile(final String s) {
        final String upperCase = s.toUpperCase();
        return upperCase.endsWith(".CMD") || upperCase.endsWith(".BAT");
    }
    
    private String quoteString(final String s) {
        return new StringBuilder(s.length() + 2).append('\"').append(s).append('\"').toString();
    }
    
    private static int countLeadingBackslash(final int n, final CharSequence charSequence, final int n2) {
        if (n == 0) {
            return 0;
        }
        int n3;
        for (n3 = n2 - 1; n3 >= 0 && charSequence.charAt(n3) == '\\'; --n3) {}
        return n2 - 1 - n3;
    }
    
    private ProcessImpl(String[] tokensFromCommand, final String s, final String s2, final long[] array, final boolean b) throws IOException {
        this.handle = 0L;
        final SecurityManager securityManager = System.getSecurityManager();
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("jdk.lang.Process.allowAmbiguousCommands");
        final boolean b2 = !"false".equalsIgnoreCase((privilegedGetProperty != null) ? privilegedGetProperty : ((securityManager == null) ? "true" : "false"));
        String s4;
        if (b2 && securityManager == null) {
            String s3 = new File(tokensFromCommand[0]).getPath();
            if (needsEscaping(3, s3)) {
                s3 = this.quoteString(s3);
            }
            s4 = createCommandLine(3, s3, tokensFromCommand);
        }
        else {
            String s5;
            try {
                s5 = getExecutablePath(tokensFromCommand[0]);
            }
            catch (final IllegalArgumentException ex) {
                final StringBuilder sb = new StringBuilder();
                final String[] array2 = tokensFromCommand;
                for (int length = array2.length, i = 0; i < length; ++i) {
                    sb.append(array2[i]).append(' ');
                }
                tokensFromCommand = getTokensFromCommand(sb.toString());
                s5 = getExecutablePath(tokensFromCommand[0]);
                if (securityManager != null) {
                    securityManager.checkExec(s5);
                }
            }
            s4 = createCommandLine((b2 ? this.isShellFile(s5) : (!this.isExe(s5))) ? 0 : (b2 ? 1 : 2), this.quoteString(s5), tokensFromCommand);
        }
        this.handle = create(s4, s, s2, array, b);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (array[0] == -1L) {
                    ProcessImpl.this.stdin_stream = ProcessBuilder.NullOutputStream.INSTANCE;
                }
                else {
                    final FileDescriptor fileDescriptor = new FileDescriptor();
                    ProcessImpl.fdAccess.setHandle(fileDescriptor, array[0]);
                    ProcessImpl.this.stdin_stream = new BufferedOutputStream(new FileOutputStream(fileDescriptor));
                }
                if (array[1] == -1L) {
                    ProcessImpl.this.stdout_stream = ProcessBuilder.NullInputStream.INSTANCE;
                }
                else {
                    final FileDescriptor fileDescriptor2 = new FileDescriptor();
                    ProcessImpl.fdAccess.setHandle(fileDescriptor2, array[1]);
                    ProcessImpl.this.stdout_stream = new BufferedInputStream(new FileInputStream(fileDescriptor2));
                }
                if (array[2] == -1L) {
                    ProcessImpl.this.stderr_stream = ProcessBuilder.NullInputStream.INSTANCE;
                }
                else {
                    final FileDescriptor fileDescriptor3 = new FileDescriptor();
                    ProcessImpl.fdAccess.setHandle(fileDescriptor3, array[2]);
                    ProcessImpl.this.stderr_stream = new FileInputStream(fileDescriptor3);
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
        final int exitCodeProcess = getExitCodeProcess(this.handle);
        if (exitCodeProcess == ProcessImpl.STILL_ACTIVE) {
            throw new IllegalThreadStateException("process has not exited");
        }
        return exitCodeProcess;
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
    public boolean waitFor(final long n, final TimeUnit timeUnit) throws InterruptedException {
        long nanos = timeUnit.toNanos(n);
        if (getExitCodeProcess(this.handle) != ProcessImpl.STILL_ACTIVE) {
            return true;
        }
        if (n <= 0L) {
            return false;
        }
        final long n2 = System.nanoTime() + nanos;
        do {
            long millis = TimeUnit.NANOSECONDS.toMillis(nanos + 999999L);
            if (millis < 0L) {
                millis = 2147483647L;
            }
            waitForTimeoutInterruptibly(this.handle, millis);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (getExitCodeProcess(this.handle) != ProcessImpl.STILL_ACTIVE) {
                return true;
            }
            nanos = n2 - System.nanoTime();
        } while (nanos > 0L);
        return getExitCodeProcess(this.handle) != ProcessImpl.STILL_ACTIVE;
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
        fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        ESCAPE_VERIFICATION = new char[][] { { ' ', '\t', '<', '>', '&', '|', '^' }, { ' ', '\t', '<', '>' }, { ' ', '\t', '<', '>' }, { ' ', '\t' } };
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
