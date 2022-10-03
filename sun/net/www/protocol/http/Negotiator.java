package sun.net.www.protocol.http;

import sun.util.logging.PlatformLogger;
import java.io.IOException;
import java.lang.reflect.Constructor;

public abstract class Negotiator
{
    static Negotiator getNegotiator(final HttpCallerInfo httpCallerInfo) {
        Constructor<?> constructor;
        try {
            constructor = Class.forName("sun.net.www.protocol.http.spnego.NegotiatorImpl", true, null).getConstructor(HttpCallerInfo.class);
        }
        catch (final ClassNotFoundException ex) {
            finest(ex);
            return null;
        }
        catch (final ReflectiveOperationException ex2) {
            throw new AssertionError((Object)ex2);
        }
        try {
            return (Negotiator)constructor.newInstance(httpCallerInfo);
        }
        catch (final ReflectiveOperationException ex3) {
            finest(ex3);
            final Throwable cause = ex3.getCause();
            if (cause != null && cause instanceof Exception) {
                finest((Exception)cause);
            }
            return null;
        }
    }
    
    public abstract byte[] firstToken() throws IOException;
    
    public abstract byte[] nextToken(final byte[] p0) throws IOException;
    
    private static void finest(final Exception ex) {
        final PlatformLogger httpLogger = HttpURLConnection.getHttpLogger();
        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
            httpLogger.finest("NegotiateAuthentication: " + ex);
        }
    }
}
