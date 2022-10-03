package com.unboundid.util;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.io.InputStream;
import java.util.Collection;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolUtils;
import java.io.FileInputStream;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PasswordFileReader
{
    private final CopyOnWriteArrayList<char[]> encryptionPasswordCache;
    private final PrintStream standardError;
    private final PrintStream standardOutput;
    
    public PasswordFileReader() {
        this(System.out, System.err);
    }
    
    public PasswordFileReader(final PrintStream standardOutput, final PrintStream standardError) {
        Validator.ensureNotNullWithMessage(standardOutput, "PasswordFileReader.standardOutput must not be null.");
        Validator.ensureNotNullWithMessage(standardError, "PasswordFileReader.standardError must not be null.");
        this.standardOutput = standardOutput;
        this.standardError = standardError;
        this.encryptionPasswordCache = new CopyOnWriteArrayList<char[]>();
    }
    
    public char[] readPassword(final String path) throws IOException, LDAPException {
        return this.readPassword(new File(path));
    }
    
    public char[] readPassword(final File file) throws IOException, LDAPException {
        if (!file.exists()) {
            throw new IOException(UtilityMessages.ERR_PW_FILE_READER_FILE_MISSING.get(file.getAbsolutePath()));
        }
        if (!file.isFile()) {
            throw new IOException(UtilityMessages.ERR_PW_FILE_READER_FILE_NOT_FILE.get(file.getAbsolutePath()));
        }
        InputStream inputStream = new FileInputStream(file);
        try {
            try {
                final ObjectPair<InputStream, char[]> encryptedFileData = ToolUtils.getPossiblyPassphraseEncryptedInputStream(inputStream, this.encryptionPasswordCache, true, UtilityMessages.INFO_PW_FILE_READER_ENTER_PW_PROMPT.get(file.getAbsolutePath()), UtilityMessages.ERR_PW_FILE_READER_WRONG_PW.get(file.getAbsolutePath()), this.standardOutput, this.standardError);
                inputStream = encryptedFileData.getFirst();
                final char[] encryptionPassword = encryptedFileData.getSecond();
                if (encryptionPassword != null) {
                    synchronized (this.encryptionPasswordCache) {
                        boolean passwordIsAlreadyCached = false;
                        for (final char[] cachedPassword : this.encryptionPasswordCache) {
                            if (Arrays.equals(encryptionPassword, cachedPassword)) {
                                passwordIsAlreadyCached = true;
                                break;
                            }
                        }
                        if (!passwordIsAlreadyCached) {
                            this.encryptionPasswordCache.add(encryptionPassword);
                        }
                    }
                }
            }
            catch (final GeneralSecurityException e) {
                Debug.debugException(e);
                throw new IOException(e);
            }
            inputStream = ToolUtils.getPossiblyGZIPCompressedInputStream(inputStream);
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                final String passwordLine = reader.readLine();
                if (passwordLine == null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_PW_FILE_READER_FILE_EMPTY.get(file.getAbsolutePath()));
                }
                final String secondLine = reader.readLine();
                if (secondLine != null) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_PW_FILE_READER_FILE_HAS_MULTIPLE_LINES.get(file.getAbsolutePath()));
                }
                if (passwordLine.isEmpty()) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UtilityMessages.ERR_PW_FILE_READER_FILE_HAS_EMPTY_LINE.get(file.getAbsolutePath()));
                }
                return passwordLine.toCharArray();
            }
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
    }
    
    public List<char[]> getCachedEncryptionPasswords() {
        final ArrayList<char[]> cacheCopy;
        synchronized (this.encryptionPasswordCache) {
            cacheCopy = new ArrayList<char[]>(this.encryptionPasswordCache.size());
            for (final char[] cachedPassword : this.encryptionPasswordCache) {
                cacheCopy.add(Arrays.copyOf(cachedPassword, cachedPassword.length));
            }
        }
        return Collections.unmodifiableList((List<? extends char[]>)cacheCopy);
    }
    
    public void addToEncryptionPasswordCache(final String encryptionPassword) {
        this.addToEncryptionPasswordCache(encryptionPassword.toCharArray());
    }
    
    public void addToEncryptionPasswordCache(final char[] encryptionPassword) {
        Validator.ensureNotNullWithMessage(encryptionPassword, "PasswordFileReader.addToEncryptionPasswordCache.encryptionPassword must not be null or empty.");
        Validator.ensureTrue(encryptionPassword.length > 0, "PasswordFileReader.addToEncryptionPasswordCache.encryptionPassword must not be null or empty.");
        synchronized (this.encryptionPasswordCache) {
            for (final char[] cachedPassword : this.encryptionPasswordCache) {
                if (Arrays.equals(cachedPassword, encryptionPassword)) {
                    return;
                }
            }
            this.encryptionPasswordCache.add(encryptionPassword);
        }
    }
    
    public void clearEncryptionPasswordCache(final boolean zeroArrays) {
        synchronized (this.encryptionPasswordCache) {
            if (zeroArrays) {
                for (final char[] cachedPassword : this.encryptionPasswordCache) {
                    Arrays.fill(cachedPassword, '\0');
                }
            }
            this.encryptionPasswordCache.clear();
        }
    }
}
