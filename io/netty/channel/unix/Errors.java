package io.netty.channel.unix;

import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ClosedChannelException;
import io.netty.util.internal.EmptyArrays;
import java.net.ConnectException;
import java.io.FileNotFoundException;
import java.nio.channels.AlreadyConnectedException;
import java.net.NoRouteToHostException;
import java.nio.channels.ConnectionPendingException;
import java.io.IOException;

public final class Errors
{
    public static final int ERRNO_ENOENT_NEGATIVE;
    public static final int ERRNO_ENOTCONN_NEGATIVE;
    public static final int ERRNO_EBADF_NEGATIVE;
    public static final int ERRNO_EPIPE_NEGATIVE;
    public static final int ERRNO_ECONNRESET_NEGATIVE;
    public static final int ERRNO_EAGAIN_NEGATIVE;
    public static final int ERRNO_EWOULDBLOCK_NEGATIVE;
    public static final int ERRNO_EINPROGRESS_NEGATIVE;
    public static final int ERROR_ECONNREFUSED_NEGATIVE;
    public static final int ERROR_EISCONN_NEGATIVE;
    public static final int ERROR_EALREADY_NEGATIVE;
    public static final int ERROR_ENETUNREACH_NEGATIVE;
    private static final String[] ERRORS;
    
    static boolean handleConnectErrno(final String method, final int err) throws IOException {
        if (err == Errors.ERRNO_EINPROGRESS_NEGATIVE || err == Errors.ERROR_EALREADY_NEGATIVE) {
            return false;
        }
        throw newConnectException0(method, err);
    }
    
    @Deprecated
    public static void throwConnectException(final String method, final int err) throws IOException {
        if (err == Errors.ERROR_EALREADY_NEGATIVE) {
            throw new ConnectionPendingException();
        }
        throw newConnectException0(method, err);
    }
    
    private static IOException newConnectException0(final String method, final int err) {
        if (err == Errors.ERROR_ENETUNREACH_NEGATIVE) {
            return new NoRouteToHostException();
        }
        if (err == Errors.ERROR_EISCONN_NEGATIVE) {
            throw new AlreadyConnectedException();
        }
        if (err == Errors.ERRNO_ENOENT_NEGATIVE) {
            return new FileNotFoundException();
        }
        return new ConnectException(method + "(..) failed: " + Errors.ERRORS[-err]);
    }
    
    public static NativeIoException newConnectionResetException(final String method, final int errnoNegative) {
        final NativeIoException exception = new NativeIoException(method, errnoNegative, false);
        exception.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        return exception;
    }
    
    public static NativeIoException newIOException(final String method, final int err) {
        return new NativeIoException(method, err);
    }
    
    @Deprecated
    public static int ioResult(final String method, final int err, final NativeIoException resetCause, final ClosedChannelException closedCause) throws IOException {
        if (err == Errors.ERRNO_EAGAIN_NEGATIVE || err == Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == resetCause.expectedErr()) {
            throw resetCause;
        }
        if (err == Errors.ERRNO_EBADF_NEGATIVE) {
            throw closedCause;
        }
        if (err == Errors.ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        }
        if (err == Errors.ERRNO_ENOENT_NEGATIVE) {
            throw new FileNotFoundException();
        }
        throw newIOException(method, err);
    }
    
    public static int ioResult(final String method, final int err) throws IOException {
        if (err == Errors.ERRNO_EAGAIN_NEGATIVE || err == Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == Errors.ERRNO_EBADF_NEGATIVE) {
            throw new ClosedChannelException();
        }
        if (err == Errors.ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        }
        if (err == Errors.ERRNO_ENOENT_NEGATIVE) {
            throw new FileNotFoundException();
        }
        throw new NativeIoException(method, err, false);
    }
    
    private Errors() {
    }
    
    static {
        ERRNO_ENOENT_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOENT();
        ERRNO_ENOTCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOTCONN();
        ERRNO_EBADF_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEBADF();
        ERRNO_EPIPE_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEPIPE();
        ERRNO_ECONNRESET_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoECONNRESET();
        ERRNO_EAGAIN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEAGAIN();
        ERRNO_EWOULDBLOCK_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK();
        ERRNO_EINPROGRESS_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS();
        ERROR_ECONNREFUSED_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED();
        ERROR_EISCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEISCONN();
        ERROR_EALREADY_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEALREADY();
        ERROR_ENETUNREACH_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorENETUNREACH();
        ERRORS = new String[512];
        for (int i = 0; i < Errors.ERRORS.length; ++i) {
            Errors.ERRORS[i] = ErrorsStaticallyReferencedJniMethods.strError(i);
        }
    }
    
    public static final class NativeIoException extends IOException
    {
        private static final long serialVersionUID = 8222160204268655526L;
        private final int expectedErr;
        private final boolean fillInStackTrace;
        
        public NativeIoException(final String method, final int expectedErr) {
            this(method, expectedErr, true);
        }
        
        public NativeIoException(final String method, final int expectedErr, final boolean fillInStackTrace) {
            super(method + "(..) failed: " + Errors.ERRORS[-expectedErr]);
            this.expectedErr = expectedErr;
            this.fillInStackTrace = fillInStackTrace;
        }
        
        public int expectedErr() {
            return this.expectedErr;
        }
        
        @Override
        public synchronized Throwable fillInStackTrace() {
            if (this.fillInStackTrace) {
                return super.fillInStackTrace();
            }
            return this;
        }
    }
    
    static final class NativeConnectException extends ConnectException
    {
        private static final long serialVersionUID = -5532328671712318161L;
        private final int expectedErr;
        
        NativeConnectException(final String method, final int expectedErr) {
            super(method + "(..) failed: " + Errors.ERRORS[-expectedErr]);
            this.expectedErr = expectedErr;
        }
        
        int expectedErr() {
            return this.expectedErr;
        }
    }
}
