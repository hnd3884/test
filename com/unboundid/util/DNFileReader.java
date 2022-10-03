package com.unboundid.util;

import java.text.ParseException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.DN;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DNFileReader implements Closeable
{
    private final AtomicLong lineNumberCounter;
    private final BufferedReader reader;
    private final File dnFile;
    
    public DNFileReader(final String path) throws IOException {
        this(new File(path));
    }
    
    public DNFileReader(final File dnFile) throws IOException {
        this.dnFile = dnFile;
        this.reader = new BufferedReader(new FileReader(dnFile));
        this.lineNumberCounter = new AtomicLong(0L);
    }
    
    public DN readDN() throws IOException, LDAPException {
        while (true) {
            final String line;
            final long lineNumber;
            synchronized (this) {
                line = this.reader.readLine();
                lineNumber = this.lineNumberCounter.incrementAndGet();
            }
            if (line == null) {
                return null;
            }
            final String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            if (trimmedLine.startsWith("#")) {
                continue;
            }
            String dnString = trimmedLine;
            if (trimmedLine.charAt(2) == ':') {
                final String lowerLine = StaticUtils.toLowerCase(trimmedLine);
                if (lowerLine.startsWith("dn::")) {
                    final String base64String = line.substring(4).trim();
                    try {
                        dnString = Base64.decodeToString(base64String);
                    }
                    catch (final ParseException pe) {
                        Debug.debugException(pe);
                        throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_DN_FILE_READER_CANNOT_BASE64_DECODE.get(base64String, lineNumber, this.dnFile.getAbsolutePath(), pe.getMessage()), pe);
                    }
                }
                else if (lowerLine.startsWith("dn:")) {
                    dnString = line.substring(3).trim();
                }
            }
            try {
                return new DN(dnString);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, UtilityMessages.ERR_DN_FILE_READER_CANNOT_PARSE_DN.get(dnString, lineNumber, this.dnFile.getAbsolutePath(), le.getMessage()), le);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
