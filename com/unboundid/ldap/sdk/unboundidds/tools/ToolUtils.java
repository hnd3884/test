package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.util.PassphraseEncryptedInputStream;
import java.util.logging.Level;
import com.unboundid.util.PassphraseEncryptedStreamHeader;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.util.Collection;
import com.unboundid.util.AggregateInputStream;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import com.unboundid.util.ByteStringBuffer;
import java.util.ArrayList;
import java.io.InputStream;
import com.unboundid.util.ObjectPair;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import com.unboundid.util.PasswordReader;
import java.io.PrintStream;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import java.io.File;
import java.lang.reflect.Method;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ToolUtils
{
    private static final int WRAP_COLUMN;
    private static final Method GET_PASSPHRASE_FOR_ENCRYPTION_SETTINGS_ID_METHOD;
    
    private ToolUtils() {
    }
    
    public static String readEncryptionPassphraseFromFile(final File f) throws LDAPException {
        Validator.ensureTrue(f != null, "ToolUtils.readEncryptionPassphraseFromFile.f must not be null.");
        if (!f.exists()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_MISSING.get(f.getAbsolutePath()));
        }
        if (!f.isFile()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_NOT_FILE.get(f.getAbsolutePath()));
        }
        try (final FileReader fileReader = new FileReader(f);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final String encryptionPassphrase = bufferedReader.readLine();
            if (encryptionPassphrase == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_EMPTY.get(f.getAbsolutePath()));
            }
            if (bufferedReader.readLine() != null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_MULTIPLE_LINES.get(f.getAbsolutePath()));
            }
            if (encryptionPassphrase.isEmpty()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_EMPTY.get(f.getAbsolutePath()));
            }
            return encryptionPassphrase;
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_FILE_READ_ERROR.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
        }
    }
    
    public static String promptForEncryptionPassphrase(final boolean allowEmpty, final boolean confirm, final PrintStream out, final PrintStream err) throws LDAPException {
        return promptForEncryptionPassphrase(allowEmpty, confirm, ToolMessages.INFO_TOOL_UTILS_ENCRYPTION_PW_PROMPT.get(), ToolMessages.INFO_TOOL_UTILS_ENCRYPTION_PW_CONFIRM.get(), out, err);
    }
    
    public static String promptForEncryptionPassphrase(final boolean allowEmpty, final boolean confirm, final CharSequence initialPrompt, final CharSequence confirmPrompt, final PrintStream out, final PrintStream err) throws LDAPException {
        Validator.ensureTrue(initialPrompt != null && initialPrompt.length() > 0, "TestUtils.promptForEncryptionPassphrase.initialPrompt must not be null or empty.");
        Validator.ensureTrue(!confirm || (confirmPrompt != null && confirmPrompt.length() > 0), "TestUtils.promptForEncryptionPassphrase.confirmPrompt must not be null or empty when confirm is true.");
        Validator.ensureTrue(out != null, "ToolUtils.promptForEncryptionPassphrase.out must not be null");
        Validator.ensureTrue(err != null, "ToolUtils.promptForEncryptionPassphrase.err must not be null");
        while (true) {
            char[] passphraseChars = null;
            char[] confirmChars = null;
            try {
                wrapPrompt(initialPrompt, true, out);
                passphraseChars = PasswordReader.readPasswordChars();
                if (passphraseChars == null || passphraseChars.length == 0) {
                    if (!allowEmpty) {
                        wrap(ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_EMPTY.get(), err);
                        err.println();
                        continue;
                    }
                    passphraseChars = StaticUtils.NO_CHARS;
                }
                if (confirm) {
                    wrapPrompt(confirmPrompt, true, out);
                    confirmChars = PasswordReader.readPasswordChars();
                    if (confirmChars == null || !Arrays.equals(passphraseChars, confirmChars)) {
                        wrap(ToolMessages.ERR_TOOL_UTILS_ENCRYPTION_PW_MISMATCH.get(), err);
                        err.println();
                        continue;
                    }
                }
                return new String(passphraseChars);
            }
            finally {
                if (passphraseChars != null) {
                    Arrays.fill(passphraseChars, '\0');
                }
                if (confirmChars != null) {
                    Arrays.fill(confirmChars, '\0');
                }
            }
        }
    }
    
    public static void wrap(final CharSequence message, final PrintStream out) {
        Validator.ensureTrue(out != null, "ToolUtils.wrap.out must not be null.");
        if (message == null || message.length() == 0) {
            out.println();
            return;
        }
        for (final String line : StaticUtils.wrapLine(message.toString(), ToolUtils.WRAP_COLUMN)) {
            out.println(line);
        }
    }
    
    public static void wrapPrompt(final CharSequence prompt, final boolean ensureTrailingSpace, final PrintStream out) {
        Validator.ensureTrue(prompt != null && prompt.length() > 0, "ToolUtils.wrapPrompt.prompt must not be null or empty.");
        Validator.ensureTrue(out != null, "ToolUtils.wrapPrompt.out must not be null.");
        String promptString = prompt.toString();
        if (ensureTrailingSpace && !promptString.endsWith(" ")) {
            promptString += ' ';
        }
        final List<String> lines = StaticUtils.wrapLine(promptString, ToolUtils.WRAP_COLUMN);
        final Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            final String line = iterator.next();
            if (iterator.hasNext()) {
                out.println(line);
            }
            else {
                out.print(line);
            }
        }
    }
    
    public static ObjectPair<InputStream, String> getInputStreamForLDIFFiles(final List<File> ldifFiles, final String encryptionPassphrase, final PrintStream out, final PrintStream err) throws IOException {
        Validator.ensureTrue(ldifFiles != null && !ldifFiles.isEmpty(), "ToolUtils.getInputStreamForLDIFFiles.ldifFiles must not be null or empty.");
        Validator.ensureTrue(out != null, "ToolUtils.getInputStreamForLDIFFiles.out must not be null");
        Validator.ensureTrue(err != null, "ToolUtils.getInputStreamForLDIFFiles.err must not be null");
        boolean createdSuccessfully = false;
        final ArrayList<InputStream> inputStreams = new ArrayList<InputStream>(ldifFiles.size() * 2);
        try {
            byte[] twoEOLs = null;
            String passphrase = encryptionPassphrase;
            for (final File f : ldifFiles) {
                if (!inputStreams.isEmpty()) {
                    if (twoEOLs == null) {
                        final ByteStringBuffer buffer = new ByteStringBuffer(4);
                        buffer.append(StaticUtils.EOL_BYTES);
                        buffer.append(StaticUtils.EOL_BYTES);
                        twoEOLs = buffer.toByteArray();
                    }
                    inputStreams.add(new ByteArrayInputStream(twoEOLs));
                }
                InputStream inputStream = new FileInputStream(f);
                try {
                    final ObjectPair<InputStream, String> p = getPossiblyPassphraseEncryptedInputStream(inputStream, passphrase, encryptionPassphrase == null, ToolMessages.INFO_TOOL_UTILS_ENCRYPTED_LDIF_FILE_PW_PROMPT.get(f.getPath()), ToolMessages.ERR_TOOL_UTILS_ENCRYPTED_LDIF_FILE_WRONG_PW.get(), out, err);
                    inputStream = p.getFirst();
                    if (p.getSecond() != null && passphrase == null) {
                        passphrase = p.getSecond();
                    }
                }
                catch (final GeneralSecurityException e) {
                    Debug.debugException(e);
                    inputStream.close();
                    throw new IOException(ToolMessages.ERR_TOOL_UTILS_ENCRYPTED_LDIF_FILE_CANNOT_DECRYPT.get(f.getPath(), StaticUtils.getExceptionMessage(e)), e);
                }
                inputStream = getPossiblyGZIPCompressedInputStream(inputStream);
                inputStreams.add(inputStream);
            }
            createdSuccessfully = true;
            if (inputStreams.size() == 1) {
                return new ObjectPair<InputStream, String>(inputStreams.get(0), passphrase);
            }
            return new ObjectPair<InputStream, String>(new AggregateInputStream(inputStreams), passphrase);
        }
        finally {
            if (!createdSuccessfully) {
                for (final InputStream inputStream2 : inputStreams) {
                    try {
                        inputStream2.close();
                    }
                    catch (final IOException e2) {
                        Debug.debugException(e2);
                    }
                }
            }
        }
    }
    
    public static InputStream getPossiblyGZIPCompressedInputStream(final InputStream inputStream) throws IOException {
        Validator.ensureTrue(inputStream != null, "StaticUtils.getPossiblyGZIPCompressedInputStream.inputStream must not be null.");
        InputStream markableInputStream;
        if (inputStream.markSupported()) {
            markableInputStream = inputStream;
        }
        else {
            markableInputStream = new BufferedInputStream(inputStream);
        }
        markableInputStream.mark(2);
        boolean isCompressed;
        try {
            isCompressed = (markableInputStream.read() == 31 && markableInputStream.read() == 139);
        }
        finally {
            markableInputStream.reset();
        }
        if (isCompressed) {
            return new GZIPInputStream(markableInputStream);
        }
        return markableInputStream;
    }
    
    public static ObjectPair<InputStream, String> getPossiblyPassphraseEncryptedInputStream(final InputStream inputStream, final String potentialPassphrase, final boolean promptOnIncorrectPassphrase, final CharSequence passphrasePrompt, final CharSequence incorrectPassphraseError, final PrintStream standardOutput, final PrintStream standardError) throws IOException, InvalidKeyException, GeneralSecurityException {
        Collection<char[]> potentialPassphrases;
        if (potentialPassphrase == null) {
            potentialPassphrases = (Collection<char[]>)Collections.emptySet();
        }
        else {
            potentialPassphrases = Collections.singleton(potentialPassphrase.toCharArray());
        }
        final ObjectPair<InputStream, char[]> p = getPossiblyPassphraseEncryptedInputStream(inputStream, potentialPassphrases, promptOnIncorrectPassphrase, passphrasePrompt, incorrectPassphraseError, standardOutput, standardError);
        if (p.getSecond() == null) {
            return new ObjectPair<InputStream, String>(p.getFirst(), null);
        }
        return new ObjectPair<InputStream, String>(p.getFirst(), new String(p.getSecond()));
    }
    
    public static ObjectPair<InputStream, char[]> getPossiblyPassphraseEncryptedInputStream(final InputStream inputStream, final char[] potentialPassphrase, final boolean promptOnIncorrectPassphrase, final CharSequence passphrasePrompt, final CharSequence incorrectPassphraseError, final PrintStream standardOutput, final PrintStream standardError) throws IOException, InvalidKeyException, GeneralSecurityException {
        Collection<char[]> potentialPassphrases;
        if (potentialPassphrase == null) {
            potentialPassphrases = (Collection<char[]>)Collections.emptySet();
        }
        else {
            potentialPassphrases = Collections.singleton(potentialPassphrase);
        }
        final ObjectPair<InputStream, char[]> p = getPossiblyPassphraseEncryptedInputStream(inputStream, potentialPassphrases, promptOnIncorrectPassphrase, passphrasePrompt, incorrectPassphraseError, standardOutput, standardError);
        if (p.getSecond() == null) {
            return new ObjectPair<InputStream, char[]>(p.getFirst(), null);
        }
        return new ObjectPair<InputStream, char[]>(p.getFirst(), p.getSecond());
    }
    
    public static ObjectPair<InputStream, char[]> getPossiblyPassphraseEncryptedInputStream(final InputStream inputStream, final Collection<char[]> potentialPassphrases, final boolean promptOnIncorrectPassphrase, final CharSequence passphrasePrompt, final CharSequence incorrectPassphraseError, final PrintStream standardOutput, final PrintStream standardError) throws IOException, InvalidKeyException, GeneralSecurityException {
        Validator.ensureTrue(inputStream != null, "StaticUtils.getPossiblyPassphraseEncryptedInputStream.inputStream must not be null.");
        Validator.ensureTrue(passphrasePrompt != null && passphrasePrompt.length() > 0, "StaticUtils.getPossiblyPassphraseEncryptedInputStream.passphrasePrompt must not be null or empty.");
        Validator.ensureTrue(incorrectPassphraseError != null && incorrectPassphraseError.length() > 0, "StaticUtils.getPossiblyPassphraseEncryptedInputStream.incorrectPassphraseError must not be null or empty.");
        Validator.ensureTrue(standardOutput != null, "StaticUtils.getPossiblyPassphraseEncryptedInputStream.standardOutput must not be null.");
        Validator.ensureTrue(standardError != null, "StaticUtils.getPossiblyPassphraseEncryptedInputStream.standardError must not be null.");
        InputStream markableInputStream;
        if (inputStream.markSupported()) {
            markableInputStream = inputStream;
        }
        else {
            markableInputStream = new BufferedInputStream(inputStream);
        }
        markableInputStream.mark(1024);
        PassphraseEncryptedStreamHeader streamHeaderShell;
        try {
            streamHeaderShell = PassphraseEncryptedStreamHeader.readFrom(markableInputStream, null);
        }
        catch (final LDAPException e) {
            Debug.debugException(Level.FINEST, e);
            markableInputStream.reset();
            return new ObjectPair<InputStream, char[]>(markableInputStream, null);
        }
        if (streamHeaderShell.getKeyIdentifier() != null && ToolUtils.GET_PASSPHRASE_FOR_ENCRYPTION_SETTINGS_ID_METHOD != null) {
            try {
                final Object passphraseObject = ToolUtils.GET_PASSPHRASE_FOR_ENCRYPTION_SETTINGS_ID_METHOD.invoke(null, streamHeaderShell.getKeyIdentifier(), standardOutput, standardError);
                if (passphraseObject != null && passphraseObject instanceof String) {
                    final char[] passphraseChars = ((String)passphraseObject).toCharArray();
                    final PassphraseEncryptedStreamHeader validStreamHeader = PassphraseEncryptedStreamHeader.decode(streamHeaderShell.getEncodedHeader(), passphraseChars);
                    return new ObjectPair<InputStream, char[]>(new PassphraseEncryptedInputStream(markableInputStream, validStreamHeader), passphraseChars);
                }
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
        if (potentialPassphrases != null) {
            final Iterator<char[]> passphraseIterator = potentialPassphrases.iterator();
            while (passphraseIterator.hasNext()) {
                try {
                    final char[] passphraseChars = passphraseIterator.next();
                    final PassphraseEncryptedStreamHeader validStreamHeader = PassphraseEncryptedStreamHeader.decode(streamHeaderShell.getEncodedHeader(), passphraseChars);
                    return new ObjectPair<InputStream, char[]>(new PassphraseEncryptedInputStream(markableInputStream, validStreamHeader), passphraseChars);
                }
                catch (final InvalidKeyException e3) {
                    Debug.debugException(e3);
                    if (!promptOnIncorrectPassphrase && !passphraseIterator.hasNext()) {
                        throw e3;
                    }
                    continue;
                }
                catch (final GeneralSecurityException e4) {
                    Debug.debugException(e4);
                    if (!passphraseIterator.hasNext()) {
                        throw e4;
                    }
                    continue;
                }
                catch (final LDAPException e5) {
                    Debug.debugException(e5);
                    if (!passphraseIterator.hasNext()) {
                        throw new GeneralSecurityException(e5.getMessage(), e5);
                    }
                    continue;
                }
                break;
            }
        }
        while (true) {
            String promptedPassphrase;
            try {
                promptedPassphrase = promptForEncryptionPassphrase(false, false, passphrasePrompt, null, standardOutput, standardError);
            }
            catch (final LDAPException e5) {
                Debug.debugException(e5);
                throw new IOException(e5.getMessage(), e5);
            }
            try {
                final char[] passphraseChars = promptedPassphrase.toCharArray();
                final PassphraseEncryptedStreamHeader validStreamHeader = PassphraseEncryptedStreamHeader.decode(streamHeaderShell.getEncodedHeader(), passphraseChars);
                return new ObjectPair<InputStream, char[]>(new PassphraseEncryptedInputStream(markableInputStream, validStreamHeader), passphraseChars);
            }
            catch (final InvalidKeyException e3) {
                Debug.debugException(e3);
                wrap(incorrectPassphraseError, standardError);
                standardError.println();
            }
            catch (final GeneralSecurityException e4) {
                Debug.debugException(e4);
                throw e4;
            }
            catch (final LDAPException e5) {
                Debug.debugException(e5);
                throw new GeneralSecurityException(e5.getMessage(), e5);
            }
        }
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
        Method m = null;
        try {
            final Class<?> serverStaticUtilsClass = Class.forName("com.unboundid.directory.server.util.StaticUtils");
            m = serverStaticUtilsClass.getMethod("getPassphraseForEncryptionSettingsID", String.class, PrintStream.class, PrintStream.class);
        }
        catch (final Exception e) {
            Debug.debugException(Level.FINEST, e);
        }
        GET_PASSPHRASE_FOR_ENCRYPTION_SETTINGS_ID_METHOD = m;
    }
}
