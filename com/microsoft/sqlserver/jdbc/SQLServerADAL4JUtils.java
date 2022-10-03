package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.logging.Level;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import com.microsoft.aad.adal4j.AuthenticationException;
import java.text.MessageFormat;
import java.net.MalformedURLException;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationContext;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

class SQLServerADAL4JUtils
{
    private static final Logger adal4jLogger;
    
    static SqlFedAuthToken getSqlFedAuthToken(final SQLServerConnection.SqlFedAuthInfo fedAuthInfo, final String user, final String password, final String authenticationString) throws SQLServerException {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            final AuthenticationContext context = new AuthenticationContext(fedAuthInfo.stsurl, false, executorService);
            final Future<AuthenticationResult> future = context.acquireToken(fedAuthInfo.spn, "7f98cb04-cd1e-40df-9140-3bf7e2cea4db", user, password, (AuthenticationCallback)null);
            final AuthenticationResult authenticationResult = future.get();
            return new SqlFedAuthToken(authenticationResult.getAccessToken(), authenticationResult.getExpiresOnDate());
        }
        catch (final MalformedURLException | InterruptedException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (final ExecutionException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ADALExecution"));
            final Object[] msgArgs = { user, authenticationString };
            final String correctedErrorMessage = e2.getCause().getMessage().replaceAll("\\\\r\\\\n", "\r\n");
            final AuthenticationException correctedAuthenticationException = new AuthenticationException(correctedErrorMessage);
            final ExecutionException correctedExecutionException = new ExecutionException((Throwable)correctedAuthenticationException);
            throw new SQLServerException(form.format(msgArgs), null, 0, correctedExecutionException);
        }
        finally {
            executorService.shutdown();
        }
    }
    
    static SqlFedAuthToken getSqlFedAuthTokenIntegrated(final SQLServerConnection.SqlFedAuthInfo fedAuthInfo, final String authenticationString) throws SQLServerException {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            final KerberosPrincipal kerberosPrincipal = new KerberosPrincipal("username");
            final String username = kerberosPrincipal.getName();
            if (SQLServerADAL4JUtils.adal4jLogger.isLoggable(Level.FINE)) {
                SQLServerADAL4JUtils.adal4jLogger.fine(SQLServerADAL4JUtils.adal4jLogger.toString() + " realm name is:" + kerberosPrincipal.getRealm());
            }
            final AuthenticationContext context = new AuthenticationContext(fedAuthInfo.stsurl, false, executorService);
            final Future<AuthenticationResult> future = context.acquireToken(fedAuthInfo.spn, "7f98cb04-cd1e-40df-9140-3bf7e2cea4db", username, (String)null, (AuthenticationCallback)null);
            final AuthenticationResult authenticationResult = future.get();
            return new SqlFedAuthToken(authenticationResult.getAccessToken(), authenticationResult.getExpiresOnDate());
        }
        catch (final InterruptedException | IOException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (final ExecutionException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ADALExecution"));
            final Object[] msgArgs = { "", authenticationString };
            if (null == e2.getCause() || null == e2.getCause().getMessage()) {
                throw new SQLServerException(form.format(msgArgs), (Throwable)null);
            }
            final String correctedErrorMessage = e2.getCause().getMessage().replaceAll("\\\\r\\\\n", "\r\n");
            final AuthenticationException correctedAuthenticationException = new AuthenticationException(correctedErrorMessage);
            final ExecutionException correctedExecutionException = new ExecutionException((Throwable)correctedAuthenticationException);
            throw new SQLServerException(form.format(msgArgs), null, 0, correctedExecutionException);
        }
        finally {
            executorService.shutdown();
        }
    }
    
    static {
        adal4jLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerADAL4JUtils");
    }
}
