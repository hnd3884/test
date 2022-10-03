package com.maverick.ssh;

public class SshException extends Exception
{
    public static final int UNEXPECTED_TERMINATION = 1;
    public static final int REMOTE_HOST_DISCONNECTED = 2;
    public static final int PROTOCOL_VIOLATION = 3;
    public static final int BAD_API_USAGE = 4;
    public static final int INTERNAL_ERROR = 5;
    public static final int CHANNEL_FAILURE = 6;
    public static final int UNSUPPORTED_ALGORITHM = 7;
    public static final int CANCELLED_CONNECTION = 8;
    public static final int KEY_EXCHANGE_FAILED = 9;
    public static final int CONNECT_FAILED = 10;
    public static final int LICENSE_ERROR = 11;
    public static final int CONNECTION_CLOSED = 12;
    public static final int AGENT_ERROR = 13;
    public static final int FORWARDING_ERROR = 14;
    public static final int PSEUDO_TTY_ERROR = 15;
    public static final int SHELL_ERROR = 15;
    public static final int SESSION_STREAM_ERROR = 15;
    public static final int JCE_ERROR = 16;
    public static final int POSSIBLE_CORRUPT_FILE = 17;
    public static final int SCP_TRANSFER_CANCELLED = 18;
    public static final int SOCKET_TIMEOUT = 19;
    public static final int PROMPT_TIMEOUT = 20;
    public static final int MESSAGE_TIMEOUT = 21;
    int c;
    Throwable b;
    
    public SshException(final String s, final int n) {
        this(s, n, null);
    }
    
    public SshException(final int n, final Throwable t) {
        this(null, n, t);
    }
    
    public SshException(final Throwable t, final int n) {
        this(null, n, t);
    }
    
    public SshException(final String s, final Throwable t) {
        this(s, 5, t);
    }
    
    public SshException(final Throwable t) {
        this("An unexpected exception was caught: " + t.getMessage(), t);
    }
    
    public SshException(final String s, final int c, final Throwable b) {
        super((s == null) ? ((b == null) ? "Unknown cause" : b.getClass().getName()) : (s + " [" + ((b == null) ? "Unknown cause" : b.getClass().getName()) + "]"));
        this.b = b;
        this.c = c;
    }
    
    public int getReason() {
        return this.c;
    }
    
    public Throwable getCause() {
        return this.b;
    }
}
