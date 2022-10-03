package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldif.LDIFChangeRecord;
import java.util.List;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.util.Debug;
import com.unboundid.ldif.LDIFReader;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Closeable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuditLogReader implements Closeable
{
    private final BufferedReader reader;
    
    public AuditLogReader(final String path) throws IOException {
        this.reader = new BufferedReader(new FileReader(path));
    }
    
    public AuditLogReader(final File file) throws IOException {
        this.reader = new BufferedReader(new FileReader(file));
    }
    
    public AuditLogReader(final Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader)reader;
        }
        else {
            this.reader = new BufferedReader(reader);
        }
    }
    
    public AuditLogReader(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }
    
    public AuditLogMessage read() throws IOException, AuditLogException {
        final List<String> fullMessageLines = new ArrayList<String>(20);
        final List<String> nonCommentLines = new ArrayList<String>(20);
        while (true) {
            final String line = this.reader.readLine();
            if (line == null) {
                break;
            }
            if (line.isEmpty()) {
                if (!nonCommentLines.isEmpty()) {
                    break;
                }
                fullMessageLines.clear();
            }
            else {
                fullMessageLines.add(line);
                if (line.startsWith("#")) {
                    continue;
                }
                nonCommentLines.add(line);
            }
        }
        if (nonCommentLines.isEmpty()) {
            return null;
        }
        LDIFChangeRecord changeRecord;
        try {
            final String[] ldifLines = StaticUtils.toArray(nonCommentLines, String.class);
            changeRecord = LDIFReader.decodeChangeRecord(ldifLines);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final String concatenatedLogLines = StaticUtils.concatenateStrings("[ ", "\"", ", ", "\"", " ]", fullMessageLines);
            throw new AuditLogException(fullMessageLines, LogMessages.ERR_AUDIT_LOG_READER_CANNOT_PARSE_CHANGE_RECORD.get(concatenatedLogLines, StaticUtils.getExceptionMessage(e)), e);
        }
        if (changeRecord instanceof LDIFAddChangeRecord) {
            return new AddAuditLogMessage(fullMessageLines, (LDIFAddChangeRecord)changeRecord);
        }
        if (changeRecord instanceof LDIFDeleteChangeRecord) {
            return new DeleteAuditLogMessage(fullMessageLines, (LDIFDeleteChangeRecord)changeRecord);
        }
        if (changeRecord instanceof LDIFModifyChangeRecord) {
            return new ModifyAuditLogMessage(fullMessageLines, (LDIFModifyChangeRecord)changeRecord);
        }
        if (changeRecord instanceof LDIFModifyDNChangeRecord) {
            return new ModifyDNAuditLogMessage(fullMessageLines, (LDIFModifyDNChangeRecord)changeRecord);
        }
        final String concatenatedLogLines2 = StaticUtils.concatenateStrings("[ ", "\"", ", ", "\"", " ]", fullMessageLines);
        throw new AuditLogException(fullMessageLines, LogMessages.ERR_AUDIT_LOG_READER_UNSUPPORTED_CHANGE_RECORD.get(concatenatedLogLines2, changeRecord.getChangeType().getName()));
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
