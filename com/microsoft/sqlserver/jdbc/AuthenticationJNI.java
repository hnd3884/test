package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;

final class AuthenticationJNI extends SSPIAuthentication
{
    private static final int maximumpointersize = 128;
    private static boolean enabled;
    private static Logger authLogger;
    private static int sspiBlobMaxlen;
    private byte[] sniSec;
    private int[] sniSecLen;
    private final String dnsName;
    private final int port;
    private SQLServerConnection con;
    private static final UnsatisfiedLinkError linkError;
    
    static int getMaxSSPIBlobSize() {
        return AuthenticationJNI.sspiBlobMaxlen;
    }
    
    static boolean isDllLoaded() {
        return AuthenticationJNI.enabled;
    }
    
    AuthenticationJNI(final SQLServerConnection con, final String address, final int serverport) throws SQLServerException {
        this.sniSec = new byte[128];
        this.sniSecLen = new int[] { 0 };
        if (!AuthenticationJNI.enabled) {
            con.terminate(0, SQLServerException.getErrString("R_notConfiguredForIntegrated"), AuthenticationJNI.linkError);
        }
        this.con = con;
        this.dnsName = initDNSArray(address);
        this.port = serverport;
    }
    
    static FedAuthDllInfo getAccessTokenForWindowsIntegrated(final String stsURL, final String servicePrincipalName, final String clientConnectionId, final String clientId, final long expirationFileTime) throws DLLException {
        final FedAuthDllInfo dllInfo = ADALGetAccessTokenForWindowsIntegrated(stsURL, servicePrincipalName, clientConnectionId, clientId, expirationFileTime, AuthenticationJNI.authLogger);
        return dllInfo;
    }
    
    @Override
    byte[] generateClientContext(final byte[] pin, final boolean[] done) throws SQLServerException {
        final int[] outsize = { getMaxSSPIBlobSize() };
        final byte[] pOut = new byte[outsize[0]];
        assert this.dnsName != null;
        final int failure = SNISecGenClientContext(this.sniSec, this.sniSecLen, pin, pin.length, pOut, outsize, done, this.dnsName, this.port, null, null, AuthenticationJNI.authLogger);
        if (failure != 0) {
            if (AuthenticationJNI.authLogger.isLoggable(Level.WARNING)) {
                AuthenticationJNI.authLogger.warning(this.toString() + " Authentication failed code : " + failure);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), AuthenticationJNI.linkError);
        }
        final byte[] output = new byte[outsize[0]];
        System.arraycopy(pOut, 0, output, 0, outsize[0]);
        return output;
    }
    
    @Override
    void releaseClientContext() {
        int success = 0;
        if (this.sniSecLen[0] > 0) {
            success = SNISecReleaseClientContext(this.sniSec, this.sniSecLen[0], AuthenticationJNI.authLogger);
            this.sniSecLen[0] = 0;
        }
        if (AuthenticationJNI.authLogger.isLoggable(Level.FINER)) {
            AuthenticationJNI.authLogger.finer(this.toString() + " Release client context status : " + success);
        }
    }
    
    private static String initDNSArray(final String address) {
        final String[] dns = { null };
        if (GetDNSName(address, dns, AuthenticationJNI.authLogger) != 0) {
            dns[0] = address;
        }
        return dns[0];
    }
    
    private static native int SNISecGenClientContext(final byte[] p0, final int[] p1, final byte[] p2, final int p3, final byte[] p4, final int[] p5, final boolean[] p6, final String p7, final int p8, final String p9, final String p10, final Logger p11);
    
    private static native int SNISecReleaseClientContext(final byte[] p0, final int p1, final Logger p2);
    
    private static native int SNISecInitPackage(final int[] p0, final Logger p1);
    
    private static native int SNISecTerminatePackage(final Logger p0);
    
    private static native int SNIGetSID(final byte[] p0, final Logger p1);
    
    private static native boolean SNIIsEqualToCurrentSID(final byte[] p0, final Logger p1);
    
    private static native int GetDNSName(final String p0, final String[] p1, final Logger p2);
    
    private static native FedAuthDllInfo ADALGetAccessTokenForWindowsIntegrated(final String p0, final String p1, final String p2, final String p3, final long p4, final Logger p5);
    
    static synchronized native byte[] DecryptColumnEncryptionKey(final String p0, final String p1, final byte[] p2) throws DLLException;
    
    static synchronized native boolean VerifyColumnMasterKeyMetadata(final String p0, final boolean p1, final byte[] p2) throws DLLException;
    
    static {
        AuthenticationJNI.enabled = false;
        AuthenticationJNI.authLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.AuthenticationJNI");
        AuthenticationJNI.sspiBlobMaxlen = 0;
        UnsatisfiedLinkError temp = null;
        try {
            System.loadLibrary(SQLServerDriver.AUTH_DLL_NAME);
            final int[] pkg = { 0 };
            if (0 != SNISecInitPackage(pkg, AuthenticationJNI.authLogger)) {
                throw new UnsatisfiedLinkError();
            }
            AuthenticationJNI.sspiBlobMaxlen = pkg[0];
            AuthenticationJNI.enabled = true;
        }
        catch (final UnsatisfiedLinkError e) {
            temp = e;
        }
        finally {
            linkError = temp;
        }
    }
}
