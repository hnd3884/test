package com.unboundid.util;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.BufferedReader;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PasswordReader
{
    private static volatile BufferedReader TEST_READER;
    private static volatile String DEFAULT_ENVIRONMENT_VARIABLE_VALUE;
    private static final String PASSWORD_FILE_ENVIRONMENT_VARIABLE = "LDAP_SDK_PASSWORD_READER_PASSWORD_FILE";
    
    private PasswordReader() {
    }
    
    public static char[] readPasswordChars() throws LDAPException {
        final BufferedReader testReader = PasswordReader.TEST_READER;
        if (testReader != null) {
            try {
                return testReader.readLine().toCharArray();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_PW_READER_FAILURE.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        final String environmentVariableValue = StaticUtils.getEnvironmentVariable("LDAP_SDK_PASSWORD_READER_PASSWORD_FILE", PasswordReader.DEFAULT_ENVIRONMENT_VARIABLE_VALUE);
        if (environmentVariableValue != null) {
            try {
                final File f = new File(environmentVariableValue);
                final PasswordFileReader r = new PasswordFileReader();
                return r.readPassword(f);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_PW_READER_FAILURE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        if (System.console() == null) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_PW_READER_CANNOT_READ_PW_WITH_NO_CONSOLE.get());
        }
        return System.console().readPassword();
    }
    
    public static byte[] readPassword() throws LDAPException {
        final char[] pwChars = readPasswordChars();
        final ByteStringBuffer buffer = new ByteStringBuffer();
        buffer.append(pwChars);
        Arrays.fill(pwChars, '\0');
        final byte[] pwBytes = buffer.toByteArray();
        buffer.clear(true);
        return pwBytes;
    }
    
    @Deprecated
    public void run() {
    }
    
    @InternalUseOnly
    public static void setTestReaderLines(final String... lines) {
        final ByteStringBuffer buffer = new ByteStringBuffer();
        for (final String line : lines) {
            buffer.append((CharSequence)line);
            buffer.append(StaticUtils.EOL_BYTES);
        }
        PasswordReader.TEST_READER = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
    }
    
    @InternalUseOnly
    public static void setTestReader(final BufferedReader reader) {
        PasswordReader.TEST_READER = reader;
    }
    
    @InternalUseOnly
    static void setDefaultEnvironmentVariableValue(final String value) {
        PasswordReader.DEFAULT_ENVIRONMENT_VARIABLE_VALUE = value;
    }
    
    static {
        PasswordReader.TEST_READER = null;
        PasswordReader.DEFAULT_ENVIRONMENT_VARIABLE_VALUE = null;
    }
}
