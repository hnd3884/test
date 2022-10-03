package com.unboundid.ldif;

import com.unboundid.util.Base64;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.parallel.Result;
import java.util.List;
import com.unboundid.util.Debug;
import java.util.concurrent.ThreadFactory;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.parallel.Processor;
import com.unboundid.util.LDAPSDKThreadFactory;
import com.unboundid.util.Validator;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.unboundid.util.parallel.ParallelProcessor;
import com.unboundid.util.ByteStringBuffer;
import java.io.BufferedOutputStream;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDIFWriter implements Closeable
{
    private static volatile boolean commentAboutBase64EncodedValues;
    private static final byte[] VERSION_1_HEADER_BYTES;
    private static final int DEFAULT_BUFFER_SIZE = 131072;
    private final BufferedOutputStream writer;
    private final ByteStringBuffer buffer;
    private final LDIFWriterChangeRecordTranslator changeRecordTranslator;
    private final LDIFWriterEntryTranslator entryTranslator;
    private int wrapColumn;
    private int wrapColumnMinusTwo;
    private final ParallelProcessor<LDIFRecord, ByteStringBuffer> toLdifBytesInvoker;
    
    public LDIFWriter(final String path) throws IOException {
        this(new FileOutputStream(path));
    }
    
    public LDIFWriter(final File file) throws IOException {
        this(new FileOutputStream(file));
    }
    
    public LDIFWriter(final OutputStream outputStream) {
        this(outputStream, 0);
    }
    
    public LDIFWriter(final OutputStream outputStream, final int parallelThreads) {
        this(outputStream, parallelThreads, null);
    }
    
    public LDIFWriter(final OutputStream outputStream, final int parallelThreads, final LDIFWriterEntryTranslator entryTranslator) {
        this(outputStream, parallelThreads, entryTranslator, null);
    }
    
    public LDIFWriter(final OutputStream outputStream, final int parallelThreads, final LDIFWriterEntryTranslator entryTranslator, final LDIFWriterChangeRecordTranslator changeRecordTranslator) {
        this.wrapColumn = 0;
        this.wrapColumnMinusTwo = -2;
        Validator.ensureNotNull(outputStream);
        Validator.ensureTrue(parallelThreads >= 0, "LDIFWriter.parallelThreads must not be negative.");
        this.entryTranslator = entryTranslator;
        this.changeRecordTranslator = changeRecordTranslator;
        this.buffer = new ByteStringBuffer();
        if (outputStream instanceof BufferedOutputStream) {
            this.writer = (BufferedOutputStream)outputStream;
        }
        else {
            this.writer = new BufferedOutputStream(outputStream, 131072);
        }
        if (parallelThreads == 0) {
            this.toLdifBytesInvoker = null;
        }
        else {
            final LDAPSDKThreadFactory threadFactory = new LDAPSDKThreadFactory("LDIFWriter Worker", true, null);
            this.toLdifBytesInvoker = new ParallelProcessor<LDIFRecord, ByteStringBuffer>(new Processor<LDIFRecord, ByteStringBuffer>() {
                @Override
                public ByteStringBuffer process(final LDIFRecord input) throws IOException {
                    LDIFRecord r;
                    if (entryTranslator != null && input instanceof Entry) {
                        r = entryTranslator.translateEntryToWrite((Entry)input);
                        if (r == null) {
                            return null;
                        }
                    }
                    else if (changeRecordTranslator != null && input instanceof LDIFChangeRecord) {
                        r = changeRecordTranslator.translateChangeRecordToWrite((LDIFChangeRecord)input);
                        if (r == null) {
                            return null;
                        }
                    }
                    else {
                        r = input;
                    }
                    final ByteStringBuffer b = new ByteStringBuffer(200);
                    r.toLDIF(b, LDIFWriter.this.wrapColumn);
                    return b;
                }
            }, threadFactory, parallelThreads, 5);
        }
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.toLdifBytesInvoker != null) {
                try {
                    this.toLdifBytesInvoker.shutdown();
                }
                catch (final InterruptedException e) {
                    Debug.debugException(e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        finally {
            this.writer.close();
        }
    }
    
    public int getWrapColumn() {
        return this.wrapColumn;
    }
    
    public void setWrapColumn(final int wrapColumn) {
        this.wrapColumn = wrapColumn;
        this.wrapColumnMinusTwo = wrapColumn - 2;
    }
    
    public static boolean commentAboutBase64EncodedValues() {
        return LDIFWriter.commentAboutBase64EncodedValues;
    }
    
    public static void setCommentAboutBase64EncodedValues(final boolean commentAboutBase64EncodedValues) {
        LDIFWriter.commentAboutBase64EncodedValues = commentAboutBase64EncodedValues;
    }
    
    public void writeVersionHeader() throws IOException {
        this.writer.write(LDIFWriter.VERSION_1_HEADER_BYTES);
    }
    
    public void writeEntry(final Entry entry) throws IOException {
        this.writeEntry(entry, null);
    }
    
    public void writeEntry(final Entry entry, final String comment) throws IOException {
        Validator.ensureNotNull(entry);
        Entry e;
        if (this.entryTranslator == null) {
            e = entry;
        }
        else {
            e = this.entryTranslator.translateEntryToWrite(entry);
            if (e == null) {
                return;
            }
        }
        if (comment != null) {
            this.writeComment(comment, false, false);
        }
        Debug.debugLDIFWrite(e);
        this.writeLDIF(e);
    }
    
    public void writeChangeRecord(final LDIFChangeRecord changeRecord) throws IOException {
        this.writeChangeRecord(changeRecord, null);
    }
    
    public void writeChangeRecord(final LDIFChangeRecord changeRecord, final String comment) throws IOException {
        Validator.ensureNotNull(changeRecord);
        LDIFChangeRecord r;
        if (this.changeRecordTranslator == null) {
            r = changeRecord;
        }
        else {
            r = this.changeRecordTranslator.translateChangeRecordToWrite(changeRecord);
            if (r == null) {
                return;
            }
        }
        if (comment != null) {
            this.writeComment(comment, false, false);
        }
        Debug.debugLDIFWrite(r);
        this.writeLDIF(r);
    }
    
    public void writeLDIFRecord(final LDIFRecord record) throws IOException {
        this.writeLDIFRecord(record, null);
    }
    
    public void writeLDIFRecord(final LDIFRecord record, final String comment) throws IOException {
        Validator.ensureNotNull(record);
        LDIFRecord r;
        if (this.entryTranslator != null && record instanceof Entry) {
            r = this.entryTranslator.translateEntryToWrite((Entry)record);
            if (r == null) {
                return;
            }
        }
        else if (this.changeRecordTranslator != null && record instanceof LDIFChangeRecord) {
            r = this.changeRecordTranslator.translateChangeRecordToWrite((LDIFChangeRecord)record);
            if (r == null) {
                return;
            }
        }
        else {
            r = record;
        }
        Debug.debugLDIFWrite(r);
        if (comment != null) {
            this.writeComment(comment, false, false);
        }
        this.writeLDIF(r);
    }
    
    public void writeLDIFRecords(final List<? extends LDIFRecord> ldifRecords) throws IOException, InterruptedException {
        if (this.toLdifBytesInvoker == null) {
            for (final LDIFRecord ldifRecord : ldifRecords) {
                this.writeLDIFRecord(ldifRecord);
            }
        }
        else {
            final List<Result<LDIFRecord, ByteStringBuffer>> results = this.toLdifBytesInvoker.processAll(ldifRecords);
            for (final Result<LDIFRecord, ByteStringBuffer> result : results) {
                rethrow(result.getFailureCause());
                final ByteStringBuffer encodedBytes = result.getOutput();
                if (encodedBytes != null) {
                    encodedBytes.write(this.writer);
                    this.writer.write(StaticUtils.EOL_BYTES);
                }
            }
        }
    }
    
    public void writeComment(final String comment, final boolean spaceBefore, final boolean spaceAfter) throws IOException {
        Validator.ensureNotNull(comment);
        if (spaceBefore) {
            this.writer.write(StaticUtils.EOL_BYTES);
        }
        if (comment.indexOf(10) < 0) {
            this.writeSingleLineComment(comment);
        }
        else {
            final String[] arr$;
            final String[] lines = arr$ = comment.split("\\r?\\n");
            for (final String line : arr$) {
                this.writeSingleLineComment(line);
            }
        }
        if (spaceAfter) {
            this.writer.write(StaticUtils.EOL_BYTES);
        }
    }
    
    private void writeSingleLineComment(final String comment) throws IOException {
        int commentWrapMinusTwo;
        if (this.wrapColumn <= 0) {
            commentWrapMinusTwo = StaticUtils.TERMINAL_WIDTH_COLUMNS - 3;
        }
        else {
            commentWrapMinusTwo = this.wrapColumnMinusTwo;
        }
        this.buffer.clear();
        final int length = comment.length();
        if (length <= commentWrapMinusTwo) {
            this.buffer.append((CharSequence)"# ");
            this.buffer.append((CharSequence)comment);
            this.buffer.append(StaticUtils.EOL_BYTES);
        }
        else {
            int minPos = 0;
            while (minPos < length) {
                if (length - minPos <= commentWrapMinusTwo) {
                    this.buffer.append((CharSequence)"# ");
                    this.buffer.append((CharSequence)comment.substring(minPos));
                    this.buffer.append(StaticUtils.EOL_BYTES);
                    break;
                }
                boolean spaceFound = false;
                int spacePos;
                int pos;
                for (pos = (spacePos = minPos + commentWrapMinusTwo); spacePos > minPos; --spacePos) {
                    if (comment.charAt(spacePos) == ' ') {
                        spaceFound = true;
                        break;
                    }
                }
                if (!spaceFound) {
                    for (spacePos = pos + 1; spacePos < length; ++spacePos) {
                        if (comment.charAt(spacePos) == ' ') {
                            spaceFound = true;
                            break;
                        }
                    }
                    if (!spaceFound) {
                        this.buffer.append((CharSequence)"# ");
                        this.buffer.append((CharSequence)comment.substring(minPos));
                        this.buffer.append(StaticUtils.EOL_BYTES);
                        break;
                    }
                }
                this.buffer.append((CharSequence)"# ");
                this.buffer.append((CharSequence)comment.substring(minPos, spacePos));
                this.buffer.append(StaticUtils.EOL_BYTES);
                for (minPos = spacePos + 1; minPos < length && comment.charAt(minPos) == ' '; ++minPos) {}
            }
        }
        this.buffer.write(this.writer);
    }
    
    private void writeLDIF(final LDIFRecord record) throws IOException {
        this.buffer.clear();
        record.toLDIF(this.buffer, this.wrapColumn);
        this.buffer.append(StaticUtils.EOL_BYTES);
        this.buffer.write(this.writer);
    }
    
    public static List<String> wrapLines(final int wrapColumn, final String... ldifLines) {
        return wrapLines(wrapColumn, Arrays.asList(ldifLines));
    }
    
    public static List<String> wrapLines(final int wrapColumn, final List<String> ldifLines) {
        if (wrapColumn <= 2) {
            return new ArrayList<String>(ldifLines);
        }
        final ArrayList<String> newLines = new ArrayList<String>(ldifLines.size());
        for (final String s : ldifLines) {
            final int length = s.length();
            if (length <= wrapColumn) {
                newLines.add(s);
            }
            else {
                newLines.add(s.substring(0, wrapColumn));
                for (int pos = wrapColumn; pos < length; pos += wrapColumn - 1) {
                    if (length - pos + 1 <= wrapColumn) {
                        newLines.add(' ' + s.substring(pos));
                        break;
                    }
                    newLines.add(' ' + s.substring(pos, pos + wrapColumn - 1));
                }
            }
        }
        return newLines;
    }
    
    public static String encodeNameAndValue(final String name, final ASN1OctetString value) {
        final StringBuilder buffer = new StringBuilder();
        encodeNameAndValue(name, value, buffer);
        return buffer.toString();
    }
    
    public static void encodeNameAndValue(final String name, final ASN1OctetString value, final StringBuilder buffer) {
        encodeNameAndValue(name, value, buffer, 0);
    }
    
    public static void encodeNameAndValue(final String name, final ASN1OctetString value, final StringBuilder buffer, final int wrapColumn) {
        final int bufferStartPos = buffer.length();
        final byte[] valueBytes = value.getValue();
        boolean base64Encoded = false;
        try {
            buffer.append(name);
            buffer.append(':');
            final int length = valueBytes.length;
            if (length == 0) {
                buffer.append(' ');
                return;
            }
            switch (valueBytes[0]) {
                case 32:
                case 58:
                case 60: {
                    buffer.append(": ");
                    Base64.encode(valueBytes, buffer);
                    base64Encoded = true;
                    return;
                }
                default: {
                    if (valueBytes[length - 1] == 32) {
                        buffer.append(": ");
                        Base64.encode(valueBytes, buffer);
                        base64Encoded = true;
                        return;
                    }
                    int i = 0;
                    while (i < length) {
                        if ((valueBytes[i] & 0x7F) != (valueBytes[i] & 0xFF)) {
                            buffer.append(": ");
                            Base64.encode(valueBytes, buffer);
                            base64Encoded = true;
                            return;
                        }
                        switch (valueBytes[i]) {
                            case 0:
                            case 10:
                            case 13: {
                                buffer.append(": ");
                                Base64.encode(valueBytes, buffer);
                                base64Encoded = true;
                                return;
                            }
                            default: {
                                ++i;
                                continue;
                            }
                        }
                    }
                    buffer.append(' ');
                    buffer.append(value.stringValue());
                    break;
                }
            }
        }
        finally {
            if (wrapColumn > 2) {
                final int length2 = buffer.length() - bufferStartPos;
                if (length2 > wrapColumn) {
                    final String EOL_PLUS_SPACE = StaticUtils.EOL + ' ';
                    buffer.insert(bufferStartPos + wrapColumn, EOL_PLUS_SPACE);
                    for (int pos = bufferStartPos + 2 * wrapColumn + EOL_PLUS_SPACE.length() - 1; pos < buffer.length(); pos += wrapColumn - 1 + EOL_PLUS_SPACE.length()) {
                        buffer.insert(pos, EOL_PLUS_SPACE);
                    }
                }
            }
            if (base64Encoded && LDIFWriter.commentAboutBase64EncodedValues) {
                writeBase64DecodedValueComment(valueBytes, buffer, wrapColumn);
            }
        }
    }
    
    private static void writeBase64DecodedValueComment(final byte[] valueBytes, final StringBuilder buffer, final int wrapColumn) {
        if (LDIFWriter.commentAboutBase64EncodedValues) {
            int wrapColumnMinusTwo;
            if (wrapColumn <= 5) {
                wrapColumnMinusTwo = StaticUtils.TERMINAL_WIDTH_COLUMNS - 3;
            }
            else {
                wrapColumnMinusTwo = wrapColumn - 2;
            }
            final int wrapColumnMinusThree = wrapColumnMinusTwo - 1;
            boolean first = true;
            final String comment = "Non-base64-encoded representation of the above value: " + getEscapedValue(valueBytes);
            for (final String s : StaticUtils.wrapLine(comment, wrapColumnMinusTwo, wrapColumnMinusThree)) {
                buffer.append(StaticUtils.EOL);
                buffer.append("# ");
                if (first) {
                    first = false;
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(s);
            }
        }
    }
    
    public static void encodeNameAndValue(final String name, final ASN1OctetString value, final ByteStringBuffer buffer, final int wrapColumn) {
        final int bufferStartPos = buffer.length();
        boolean base64Encoded = false;
        try {
            buffer.append((CharSequence)name);
            base64Encoded = encodeValue(value, buffer);
        }
        finally {
            if (wrapColumn > 2) {
                final int length = buffer.length() - bufferStartPos;
                if (length > wrapColumn) {
                    final byte[] EOL_BYTES_PLUS_SPACE = new byte[StaticUtils.EOL_BYTES.length + 1];
                    System.arraycopy(StaticUtils.EOL_BYTES, 0, EOL_BYTES_PLUS_SPACE, 0, StaticUtils.EOL_BYTES.length);
                    EOL_BYTES_PLUS_SPACE[StaticUtils.EOL_BYTES.length] = 32;
                    buffer.insert(bufferStartPos + wrapColumn, EOL_BYTES_PLUS_SPACE);
                    for (int pos = bufferStartPos + 2 * wrapColumn + EOL_BYTES_PLUS_SPACE.length - 1; pos < buffer.length(); pos += wrapColumn - 1 + EOL_BYTES_PLUS_SPACE.length) {
                        buffer.insert(pos, EOL_BYTES_PLUS_SPACE);
                    }
                }
            }
            if (base64Encoded && LDIFWriter.commentAboutBase64EncodedValues) {
                writeBase64DecodedValueComment(value.getValue(), buffer, wrapColumn);
            }
        }
    }
    
    static boolean encodeValue(final ASN1OctetString value, final ByteStringBuffer buffer) {
        buffer.append(':');
        final byte[] valueBytes = value.getValue();
        final int length = valueBytes.length;
        if (length == 0) {
            buffer.append(' ');
            return false;
        }
        switch (valueBytes[0]) {
            case 32:
            case 58:
            case 60: {
                buffer.append(':');
                buffer.append(' ');
                Base64.encode(valueBytes, buffer);
                return true;
            }
            default: {
                if (valueBytes[length - 1] == 32) {
                    buffer.append(':');
                    buffer.append(' ');
                    Base64.encode(valueBytes, buffer);
                    return true;
                }
                int i = 0;
                while (i < length) {
                    if ((valueBytes[i] & 0x7F) != (valueBytes[i] & 0xFF)) {
                        buffer.append(':');
                        buffer.append(' ');
                        Base64.encode(valueBytes, buffer);
                        return true;
                    }
                    switch (valueBytes[i]) {
                        case 0:
                        case 10:
                        case 13: {
                            buffer.append(':');
                            buffer.append(' ');
                            Base64.encode(valueBytes, buffer);
                            return true;
                        }
                        default: {
                            ++i;
                            continue;
                        }
                    }
                }
                buffer.append(' ');
                buffer.append(valueBytes);
                return false;
            }
        }
    }
    
    private static void writeBase64DecodedValueComment(final byte[] valueBytes, final ByteStringBuffer buffer, final int wrapColumn) {
        if (LDIFWriter.commentAboutBase64EncodedValues) {
            int wrapColumnMinusTwo;
            if (wrapColumn <= 5) {
                wrapColumnMinusTwo = StaticUtils.TERMINAL_WIDTH_COLUMNS - 3;
            }
            else {
                wrapColumnMinusTwo = wrapColumn - 2;
            }
            final int wrapColumnMinusThree = wrapColumnMinusTwo - 1;
            boolean first = true;
            final String comment = "Non-base64-encoded representation of the above value: " + getEscapedValue(valueBytes);
            for (final String s : StaticUtils.wrapLine(comment, wrapColumnMinusTwo, wrapColumnMinusThree)) {
                buffer.append((CharSequence)StaticUtils.EOL);
                buffer.append((CharSequence)"# ");
                if (first) {
                    first = false;
                }
                else {
                    buffer.append(' ');
                }
                buffer.append((CharSequence)s);
            }
        }
    }
    
    private static String getEscapedValue(final byte[] valueBytes) {
        final StringBuilder buffer = new StringBuilder(valueBytes.length * 2);
        for (int i = 0; i < valueBytes.length; ++i) {
            final byte b = valueBytes[i];
            switch (b) {
                case 32: {
                    if (i == 0 || i == valueBytes.length - 1) {
                        buffer.append("\\20");
                        break;
                    }
                    buffer.append(' ');
                    break;
                }
                case 40: {
                    buffer.append("\\28");
                    break;
                }
                case 41: {
                    buffer.append("\\29");
                    break;
                }
                case 42: {
                    buffer.append("\\2a");
                    break;
                }
                case 58: {
                    if (i == 0) {
                        buffer.append("\\3a");
                        break;
                    }
                    buffer.append(':');
                    break;
                }
                case 60: {
                    if (i == 0) {
                        buffer.append("\\3c");
                        break;
                    }
                    buffer.append('<');
                    break;
                }
                case 92: {
                    buffer.append("\\5c");
                    break;
                }
                default: {
                    if (b >= 33 && b <= 126) {
                        buffer.append((char)b);
                        break;
                    }
                    buffer.append("\\");
                    StaticUtils.toHex(b, buffer);
                    break;
                }
            }
        }
        return buffer.toString();
    }
    
    static void rethrow(final Throwable t) throws IOException {
        if (t == null) {
            return;
        }
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new IOException(t);
    }
    
    static {
        LDIFWriter.commentAboutBase64EncodedValues = false;
        VERSION_1_HEADER_BYTES = StaticUtils.getBytes("version: 1" + StaticUtils.EOL);
    }
}
