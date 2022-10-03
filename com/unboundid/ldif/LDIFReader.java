package com.unboundid.ldif;

import java.util.Set;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.HashSet;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import java.util.LinkedHashMap;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.Control;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collection;
import java.text.ParseException;
import com.unboundid.util.Base64;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import com.unboundid.util.parallel.Processor;
import com.unboundid.util.parallel.ParallelProcessor;
import com.unboundid.util.LDAPSDKThreadFactory;
import com.unboundid.util.Validator;
import java.nio.charset.Charset;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.unboundid.util.AggregateInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.unboundid.util.parallel.Result;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.parallel.AsynchronousParallelProcessor;
import com.unboundid.ldap.sdk.schema.Schema;
import java.io.BufferedReader;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDIFReader implements Closeable
{
    public static final int DEFAULT_BUFFER_SIZE = 131072;
    private static final int ASYNC_MIN_PER_PARSING_THREAD = 3;
    private static final int ASYNC_QUEUE_SIZE = 500;
    private static final Entry SKIP_ENTRY;
    private static final String DEFAULT_RELATIVE_BASE_PATH;
    private final BufferedReader reader;
    private volatile DuplicateValueBehavior duplicateValueBehavior;
    private long lineNumberCounter;
    private final LDIFReaderChangeRecordTranslator changeRecordTranslator;
    private final LDIFReaderEntryTranslator entryTranslator;
    private Schema schema;
    private volatile String relativeBasePath;
    private volatile TrailingSpaceBehavior trailingSpaceBehavior;
    private final boolean isAsync;
    private final AsynchronousParallelProcessor<UnparsedLDIFRecord, LDIFRecord> asyncParser;
    private final AtomicBoolean asyncParsingComplete;
    private final BlockingQueue<Result<UnparsedLDIFRecord, LDIFRecord>> asyncParsedRecords;
    
    public LDIFReader(final String path) throws IOException {
        this(new FileInputStream(path));
    }
    
    public LDIFReader(final String path, final int numParseThreads) throws IOException {
        this(new FileInputStream(path), numParseThreads);
    }
    
    public LDIFReader(final File file) throws IOException {
        this(new FileInputStream(file));
    }
    
    public LDIFReader(final File file, final int numParseThreads) throws IOException {
        this(new FileInputStream(file), numParseThreads);
    }
    
    public LDIFReader(final File[] files, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator) throws IOException {
        this(files, numParseThreads, entryTranslator, null);
    }
    
    public LDIFReader(final File[] files, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator, final LDIFReaderChangeRecordTranslator changeRecordTranslator) throws IOException {
        this(files, numParseThreads, entryTranslator, changeRecordTranslator, "UTF-8");
    }
    
    public LDIFReader(final File[] files, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator, final LDIFReaderChangeRecordTranslator changeRecordTranslator, final String characterSet) throws IOException {
        this(createAggregateInputStream(files), numParseThreads, entryTranslator, changeRecordTranslator, characterSet);
    }
    
    private static InputStream createAggregateInputStream(final File... files) throws IOException {
        if (files.length == 0) {
            throw new IOException(LDIFMessages.ERR_READ_NO_LDIF_FILES.get());
        }
        return new AggregateInputStream(true, files);
    }
    
    public LDIFReader(final InputStream inputStream) {
        this(inputStream, 0);
    }
    
    public LDIFReader(final InputStream inputStream, final int numParseThreads) {
        this(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 131072), numParseThreads);
    }
    
    public LDIFReader(final InputStream inputStream, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator) {
        this(inputStream, numParseThreads, entryTranslator, null);
    }
    
    public LDIFReader(final InputStream inputStream, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator, final LDIFReaderChangeRecordTranslator changeRecordTranslator) {
        this(inputStream, numParseThreads, entryTranslator, changeRecordTranslator, "UTF-8");
    }
    
    public LDIFReader(final InputStream inputStream, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator, final LDIFReaderChangeRecordTranslator changeRecordTranslator, final String characterSet) {
        this(new BufferedReader(new InputStreamReader(inputStream, Charset.forName(characterSet)), 131072), numParseThreads, entryTranslator, changeRecordTranslator);
    }
    
    public LDIFReader(final BufferedReader reader) {
        this(reader, 0);
    }
    
    public LDIFReader(final BufferedReader reader, final int numParseThreads) {
        this(reader, numParseThreads, null);
    }
    
    public LDIFReader(final BufferedReader reader, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator) {
        this(reader, numParseThreads, entryTranslator, null);
    }
    
    public LDIFReader(final BufferedReader reader, final int numParseThreads, final LDIFReaderEntryTranslator entryTranslator, final LDIFReaderChangeRecordTranslator changeRecordTranslator) {
        this.lineNumberCounter = 0L;
        Validator.ensureNotNull(reader);
        Validator.ensureTrue(numParseThreads >= 0, "LDIFReader.numParseThreads must not be negative.");
        this.reader = reader;
        this.entryTranslator = entryTranslator;
        this.changeRecordTranslator = changeRecordTranslator;
        this.duplicateValueBehavior = DuplicateValueBehavior.STRIP;
        this.trailingSpaceBehavior = TrailingSpaceBehavior.REJECT;
        this.relativeBasePath = LDIFReader.DEFAULT_RELATIVE_BASE_PATH;
        if (numParseThreads == 0) {
            this.isAsync = false;
            this.asyncParser = null;
            this.asyncParsingComplete = null;
            this.asyncParsedRecords = null;
        }
        else {
            this.isAsync = true;
            this.asyncParsingComplete = new AtomicBoolean(false);
            final LDAPSDKThreadFactory threadFactory = new LDAPSDKThreadFactory("LDIFReader Worker", true, null);
            final ParallelProcessor<UnparsedLDIFRecord, LDIFRecord> parallelParser = new ParallelProcessor<UnparsedLDIFRecord, LDIFRecord>(new RecordParser(), threadFactory, numParseThreads, 3);
            final BlockingQueue<UnparsedLDIFRecord> pendingQueue = new ArrayBlockingQueue<UnparsedLDIFRecord>(500);
            this.asyncParsedRecords = new ArrayBlockingQueue<Result<UnparsedLDIFRecord, LDIFRecord>>(1100);
            this.asyncParser = new AsynchronousParallelProcessor<UnparsedLDIFRecord, LDIFRecord>(pendingQueue, parallelParser, this.asyncParsedRecords);
            final LineReaderThread lineReaderThread = new LineReaderThread();
            lineReaderThread.start();
        }
    }
    
    public static List<Entry> readEntries(final String path) throws IOException, LDIFException {
        return readEntries(new LDIFReader(path));
    }
    
    public static List<Entry> readEntries(final File file) throws IOException, LDIFException {
        return readEntries(new LDIFReader(file));
    }
    
    public static List<Entry> readEntries(final InputStream inputStream) throws IOException, LDIFException {
        return readEntries(new LDIFReader(inputStream));
    }
    
    private static List<Entry> readEntries(final LDIFReader reader) throws IOException, LDIFException {
        try {
            final ArrayList<Entry> entries = new ArrayList<Entry>(10);
            while (true) {
                final Entry e = reader.readEntry();
                if (e == null) {
                    break;
                }
                entries.add(e);
            }
            return entries;
        }
        finally {
            reader.close();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
        if (this.isAsync()) {
            this.asyncParsedRecords.clear();
        }
    }
    
    @Deprecated
    public boolean ignoreDuplicateValues() {
        return this.duplicateValueBehavior == DuplicateValueBehavior.STRIP;
    }
    
    @Deprecated
    public void setIgnoreDuplicateValues(final boolean ignoreDuplicateValues) {
        if (ignoreDuplicateValues) {
            this.duplicateValueBehavior = DuplicateValueBehavior.STRIP;
        }
        else {
            this.duplicateValueBehavior = DuplicateValueBehavior.REJECT;
        }
    }
    
    public DuplicateValueBehavior getDuplicateValueBehavior() {
        return this.duplicateValueBehavior;
    }
    
    public void setDuplicateValueBehavior(final DuplicateValueBehavior duplicateValueBehavior) {
        this.duplicateValueBehavior = duplicateValueBehavior;
    }
    
    @Deprecated
    public boolean stripTrailingSpaces() {
        return this.trailingSpaceBehavior == TrailingSpaceBehavior.STRIP;
    }
    
    @Deprecated
    public void setStripTrailingSpaces(final boolean stripTrailingSpaces) {
        this.trailingSpaceBehavior = (stripTrailingSpaces ? TrailingSpaceBehavior.STRIP : TrailingSpaceBehavior.REJECT);
    }
    
    public TrailingSpaceBehavior getTrailingSpaceBehavior() {
        return this.trailingSpaceBehavior;
    }
    
    public void setTrailingSpaceBehavior(final TrailingSpaceBehavior trailingSpaceBehavior) {
        this.trailingSpaceBehavior = trailingSpaceBehavior;
    }
    
    public String getRelativeBasePath() {
        return this.relativeBasePath;
    }
    
    public void setRelativeBasePath(final String relativeBasePath) {
        this.setRelativeBasePath(new File(relativeBasePath));
    }
    
    public void setRelativeBasePath(final File relativeBasePath) {
        final String path = relativeBasePath.getAbsolutePath();
        if (path.endsWith(File.separator)) {
            this.relativeBasePath = path;
        }
        else {
            this.relativeBasePath = path + File.separator;
        }
    }
    
    public Schema getSchema() {
        return this.schema;
    }
    
    public void setSchema(final Schema schema) {
        this.schema = schema;
    }
    
    public LDIFRecord readLDIFRecord() throws IOException, LDIFException {
        if (this.isAsync()) {
            return this.readLDIFRecordAsync();
        }
        return this.readLDIFRecordInternal();
    }
    
    public Entry readEntry() throws IOException, LDIFException {
        if (this.isAsync()) {
            return this.readEntryAsync();
        }
        return this.readEntryInternal();
    }
    
    public LDIFChangeRecord readChangeRecord() throws IOException, LDIFException {
        return this.readChangeRecord(false);
    }
    
    public LDIFChangeRecord readChangeRecord(final boolean defaultAdd) throws IOException, LDIFException {
        if (this.isAsync()) {
            return this.readChangeRecordAsync(defaultAdd);
        }
        return this.readChangeRecordInternal(defaultAdd);
    }
    
    private LDIFRecord readLDIFRecordAsync() throws IOException, LDIFException {
        LDIFRecord record = null;
        while (record == null) {
            final Result<UnparsedLDIFRecord, LDIFRecord> result = this.readLDIFRecordResultAsync();
            if (result == null) {
                return null;
            }
            record = result.getOutput();
            if (record != LDIFReader.SKIP_ENTRY) {
                continue;
            }
            record = null;
        }
        return record;
    }
    
    private Entry readEntryAsync() throws IOException, LDIFException {
        Result<UnparsedLDIFRecord, LDIFRecord> result = null;
        LDIFRecord record = null;
        while (record == null) {
            result = this.readLDIFRecordResultAsync();
            if (result == null) {
                return null;
            }
            record = result.getOutput();
            if (record != LDIFReader.SKIP_ENTRY) {
                continue;
            }
            record = null;
        }
        if (record instanceof Entry) {
            return (Entry)record;
        }
        if (record instanceof LDIFChangeRecord) {
            try {
                return ((LDIFChangeRecord)record).toEntry();
            }
            catch (final LDIFException e) {
                Debug.debugException(e);
                final long firstLineNumber = result.getInput().getFirstLineNumber();
                throw new LDIFException(e.getExceptionMessage(), firstLineNumber, true, e);
            }
        }
        throw new AssertionError((Object)"LDIFRecords must either be an Entry or an LDIFChangeRecord");
    }
    
    private LDIFChangeRecord readChangeRecordAsync(final boolean defaultAdd) throws IOException, LDIFException {
        Result<UnparsedLDIFRecord, LDIFRecord> result = null;
        LDIFRecord record = null;
        while (record == null) {
            result = this.readLDIFRecordResultAsync();
            if (result == null) {
                return null;
            }
            record = result.getOutput();
            if (record != LDIFReader.SKIP_ENTRY) {
                continue;
            }
            record = null;
        }
        if (record instanceof LDIFChangeRecord) {
            return (LDIFChangeRecord)record;
        }
        if (!(record instanceof Entry)) {
            throw new AssertionError((Object)"LDIFRecords must either be an Entry or an LDIFChangeRecord");
        }
        if (defaultAdd) {
            return new LDIFAddChangeRecord((Entry)record);
        }
        final long firstLineNumber = result.getInput().getFirstLineNumber();
        throw new LDIFException(LDIFMessages.ERR_READ_NOT_CHANGE_RECORD.get(firstLineNumber), firstLineNumber, true);
    }
    
    private Result<UnparsedLDIFRecord, LDIFRecord> readLDIFRecordResultAsync() throws IOException, LDIFException {
        Result<UnparsedLDIFRecord, LDIFRecord> result = null;
        if (this.asyncParsingComplete.get()) {
            result = this.asyncParsedRecords.poll();
        }
        else {
            try {
                while (result == null && !this.asyncParsingComplete.get()) {
                    result = this.asyncParsedRecords.poll(1L, TimeUnit.SECONDS);
                }
                if (result == null) {
                    result = this.asyncParsedRecords.poll();
                }
            }
            catch (final InterruptedException e) {
                Debug.debugException(e);
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
        }
        if (result == null) {
            return null;
        }
        rethrow(result.getFailureCause());
        final UnparsedLDIFRecord unparsedRecord = result.getInput();
        if (unparsedRecord.isEOF()) {
            this.asyncParsingComplete.set(true);
            try {
                this.asyncParsedRecords.put(result);
            }
            catch (final InterruptedException e2) {
                Debug.debugException(e2);
                Thread.currentThread().interrupt();
            }
            return null;
        }
        return result;
    }
    
    private boolean isAsync() {
        return this.isAsync;
    }
    
    static void rethrow(final Throwable t) throws IOException, LDIFException {
        if (t == null) {
            return;
        }
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof LDIFException) {
            throw (LDIFException)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new IOException(t);
    }
    
    private LDIFRecord readLDIFRecordInternal() throws IOException, LDIFException {
        final UnparsedLDIFRecord unparsedRecord = this.readUnparsedRecord();
        return decodeRecord(unparsedRecord, this.relativeBasePath, this.schema);
    }
    
    private Entry readEntryInternal() throws IOException, LDIFException {
        Entry e = null;
        while (e == null) {
            final UnparsedLDIFRecord unparsedRecord = this.readUnparsedRecord();
            if (unparsedRecord.isEOF()) {
                return null;
            }
            e = decodeEntry(unparsedRecord, this.relativeBasePath);
            Debug.debugLDIFRead(e);
            if (this.entryTranslator == null) {
                continue;
            }
            e = this.entryTranslator.translate(e, unparsedRecord.getFirstLineNumber());
        }
        return e;
    }
    
    private LDIFChangeRecord readChangeRecordInternal(final boolean defaultAdd) throws IOException, LDIFException {
        LDIFChangeRecord r = null;
        while (r == null) {
            final UnparsedLDIFRecord unparsedRecord = this.readUnparsedRecord();
            if (unparsedRecord.isEOF()) {
                return null;
            }
            r = decodeChangeRecord(unparsedRecord, this.relativeBasePath, defaultAdd, this.schema);
            Debug.debugLDIFRead(r);
            if (this.changeRecordTranslator == null) {
                continue;
            }
            r = this.changeRecordTranslator.translate(r, unparsedRecord.getFirstLineNumber());
        }
        return r;
    }
    
    private UnparsedLDIFRecord readUnparsedRecord() throws IOException, LDIFException {
        final ArrayList<StringBuilder> lineList = new ArrayList<StringBuilder>(20);
        boolean lastWasComment = false;
        long firstLineNumber = this.lineNumberCounter + 1L;
        while (true) {
            final String line = this.reader.readLine();
            ++this.lineNumberCounter;
            if (line == null) {
                if (lineList.isEmpty()) {
                    return new UnparsedLDIFRecord(new ArrayList(0), this.duplicateValueBehavior, this.trailingSpaceBehavior, this.schema, -1L);
                }
                break;
            }
            else if (line.isEmpty()) {
                lastWasComment = false;
                if (!lineList.isEmpty()) {
                    break;
                }
                ++firstLineNumber;
            }
            else if (line.charAt(0) == ' ') {
                if (lastWasComment) {
                    continue;
                }
                if (lineList.isEmpty()) {
                    throw new LDIFException(LDIFMessages.ERR_READ_UNEXPECTED_FIRST_SPACE.get(this.lineNumberCounter), this.lineNumberCounter, false);
                }
                lineList.get(lineList.size() - 1).append(line.substring(1));
                lastWasComment = false;
            }
            else if (line.charAt(0) == '#') {
                lastWasComment = true;
            }
            else if (lineList.isEmpty() && line.startsWith("version:")) {
                lastWasComment = true;
            }
            else {
                lineList.add(new StringBuilder(line));
                lastWasComment = false;
            }
        }
        return new UnparsedLDIFRecord((ArrayList)lineList, this.duplicateValueBehavior, this.trailingSpaceBehavior, this.schema, firstLineNumber);
    }
    
    public static Entry decodeEntry(final String... ldifLines) throws LDIFException {
        final Entry e = decodeEntry(prepareRecord(DuplicateValueBehavior.STRIP, TrailingSpaceBehavior.REJECT, null, ldifLines), LDIFReader.DEFAULT_RELATIVE_BASE_PATH);
        Debug.debugLDIFRead(e);
        return e;
    }
    
    public static Entry decodeEntry(final boolean ignoreDuplicateValues, final Schema schema, final String... ldifLines) throws LDIFException {
        return decodeEntry(ignoreDuplicateValues, TrailingSpaceBehavior.REJECT, schema, ldifLines);
    }
    
    public static Entry decodeEntry(final boolean ignoreDuplicateValues, final TrailingSpaceBehavior trailingSpaceBehavior, final Schema schema, final String... ldifLines) throws LDIFException {
        final Entry e = decodeEntry(prepareRecord(ignoreDuplicateValues ? DuplicateValueBehavior.STRIP : DuplicateValueBehavior.REJECT, trailingSpaceBehavior, schema, ldifLines), LDIFReader.DEFAULT_RELATIVE_BASE_PATH);
        Debug.debugLDIFRead(e);
        return e;
    }
    
    public static LDIFChangeRecord decodeChangeRecord(final String... ldifLines) throws LDIFException {
        return decodeChangeRecord(false, ldifLines);
    }
    
    public static LDIFChangeRecord decodeChangeRecord(final boolean defaultAdd, final String... ldifLines) throws LDIFException {
        final LDIFChangeRecord r = decodeChangeRecord(prepareRecord(DuplicateValueBehavior.STRIP, TrailingSpaceBehavior.REJECT, null, ldifLines), LDIFReader.DEFAULT_RELATIVE_BASE_PATH, defaultAdd, null);
        Debug.debugLDIFRead(r);
        return r;
    }
    
    public static LDIFChangeRecord decodeChangeRecord(final boolean ignoreDuplicateValues, final Schema schema, final boolean defaultAdd, final String... ldifLines) throws LDIFException {
        return decodeChangeRecord(ignoreDuplicateValues, TrailingSpaceBehavior.REJECT, schema, defaultAdd, ldifLines);
    }
    
    public static LDIFChangeRecord decodeChangeRecord(final boolean ignoreDuplicateValues, final TrailingSpaceBehavior trailingSpaceBehavior, final Schema schema, final boolean defaultAdd, final String... ldifLines) throws LDIFException {
        final LDIFChangeRecord r = decodeChangeRecord(prepareRecord(ignoreDuplicateValues ? DuplicateValueBehavior.STRIP : DuplicateValueBehavior.REJECT, trailingSpaceBehavior, schema, ldifLines), LDIFReader.DEFAULT_RELATIVE_BASE_PATH, defaultAdd, null);
        Debug.debugLDIFRead(r);
        return r;
    }
    
    private static UnparsedLDIFRecord prepareRecord(final DuplicateValueBehavior duplicateValueBehavior, final TrailingSpaceBehavior trailingSpaceBehavior, final Schema schema, final String... ldifLines) throws LDIFException {
        Validator.ensureNotNull(ldifLines);
        Validator.ensureFalse(ldifLines.length == 0, "LDIFReader.prepareRecord.ldifLines must not be empty.");
        boolean lastWasComment = false;
        final ArrayList<StringBuilder> lineList = new ArrayList<StringBuilder>(ldifLines.length);
        for (int i = 0; i < ldifLines.length; ++i) {
            final String line = ldifLines[i];
            if (line.isEmpty()) {
                final int j = i + 1;
                if (j < ldifLines.length) {
                    if (!ldifLines[j].isEmpty()) {
                        throw new LDIFException(LDIFMessages.ERR_READ_UNEXPECTED_BLANK.get(i), i, true, ldifLines, null);
                    }
                    if (lineList.isEmpty()) {
                        throw new LDIFException(LDIFMessages.ERR_READ_ONLY_BLANKS.get(), 0L, true, ldifLines, null);
                    }
                    return new UnparsedLDIFRecord((ArrayList)lineList, duplicateValueBehavior, trailingSpaceBehavior, schema, 0L);
                }
            }
            if (line.charAt(0) == ' ') {
                if (i <= 0) {
                    throw new LDIFException(LDIFMessages.ERR_READ_UNEXPECTED_FIRST_SPACE_NO_NUMBER.get(), 0L, true, ldifLines, null);
                }
                if (!lastWasComment) {
                    lineList.get(lineList.size() - 1).append(line.substring(1));
                }
            }
            else if (line.charAt(0) == '#') {
                lastWasComment = true;
            }
            else {
                lineList.add(new StringBuilder(line));
                lastWasComment = false;
            }
        }
        if (lineList.isEmpty()) {
            throw new LDIFException(LDIFMessages.ERR_READ_NO_DATA.get(), 0L, true, ldifLines, null);
        }
        return new UnparsedLDIFRecord((ArrayList)lineList, duplicateValueBehavior, trailingSpaceBehavior, schema, 0L);
    }
    
    private static LDIFRecord decodeRecord(final UnparsedLDIFRecord unparsedRecord, final String relativeBasePath, final Schema schema) throws LDIFException {
        final Exception readError = unparsedRecord.getFailureCause();
        if (readError != null) {
            if (readError instanceof LDIFException) {
                final LDIFException ldifEx = (LDIFException)readError;
                throw new LDIFException(ldifEx.getMessage(), ldifEx.getLineNumber(), ldifEx.mayContinueReading(), ldifEx.getDataLines(), ldifEx.getCause());
            }
            throw new LDIFException(StaticUtils.getExceptionMessage(readError), -1L, true, readError);
        }
        else {
            if (unparsedRecord.isEOF()) {
                return null;
            }
            final ArrayList<StringBuilder> lineList = unparsedRecord.getLineList();
            if (unparsedRecord.getLineList() == null) {
                return null;
            }
            LDIFRecord r;
            if (lineList.size() == 1) {
                r = decodeEntry(unparsedRecord, relativeBasePath);
            }
            else {
                final String lowerSecondLine = StaticUtils.toLowerCase(lineList.get(1).toString());
                if (lowerSecondLine.startsWith("control:") || lowerSecondLine.startsWith("changetype:")) {
                    r = decodeChangeRecord(unparsedRecord, relativeBasePath, true, schema);
                }
                else {
                    r = decodeEntry(unparsedRecord, relativeBasePath);
                }
            }
            Debug.debugLDIFRead(r);
            return r;
        }
    }
    
    private static Entry decodeEntry(final UnparsedLDIFRecord unparsedRecord, final String relativeBasePath) throws LDIFException {
        final ArrayList<StringBuilder> ldifLines = unparsedRecord.getLineList();
        final long firstLineNumber = unparsedRecord.getFirstLineNumber();
        final Iterator<StringBuilder> iterator = ldifLines.iterator();
        StringBuilder line = iterator.next();
        handleTrailingSpaces(line, null, firstLineNumber, unparsedRecord.getTrailingSpaceBehavior());
        int colonPos = line.indexOf(":");
        if (colonPos > 0 && line.substring(0, colonPos).equalsIgnoreCase("version")) {
            line = iterator.next();
            handleTrailingSpaces(line, null, firstLineNumber, unparsedRecord.getTrailingSpaceBehavior());
        }
        colonPos = line.indexOf(":");
        if (colonPos < 0 || !line.substring(0, colonPos).equalsIgnoreCase("dn")) {
            throw new LDIFException(LDIFMessages.ERR_READ_DN_LINE_DOESNT_START_WITH_DN.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        final int length = line.length();
        String dn;
        if (length == colonPos + 1) {
            dn = "";
        }
        else if (line.charAt(colonPos + 1) == ':') {
            int pos;
            for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
            try {
                final byte[] dnBytes = Base64.decode(line.substring(pos));
                dn = StaticUtils.toUTF8String(dnBytes);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_DN.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_DN.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
            }
        }
        else {
            int pos;
            for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
            dn = line.substring(pos);
        }
        if (!iterator.hasNext()) {
            return new Entry(dn, unparsedRecord.getSchema());
        }
        return new Entry(dn, unparsedRecord.getSchema(), parseAttributes(dn, unparsedRecord.getDuplicateValueBehavior(), unparsedRecord.getTrailingSpaceBehavior(), unparsedRecord.getSchema(), ldifLines, iterator, relativeBasePath, firstLineNumber));
    }
    
    private static LDIFChangeRecord decodeChangeRecord(final UnparsedLDIFRecord unparsedRecord, final String relativeBasePath, final boolean defaultAdd, final Schema schema) throws LDIFException {
        final ArrayList<StringBuilder> ldifLines = unparsedRecord.getLineList();
        final long firstLineNumber = unparsedRecord.getFirstLineNumber();
        Iterator<StringBuilder> iterator = ldifLines.iterator();
        StringBuilder line = iterator.next();
        handleTrailingSpaces(line, null, firstLineNumber, unparsedRecord.getTrailingSpaceBehavior());
        int colonPos = line.indexOf(":");
        int linesRead = 1;
        if (colonPos > 0 && line.substring(0, colonPos).equalsIgnoreCase("version")) {
            line = iterator.next();
            ++linesRead;
            handleTrailingSpaces(line, null, firstLineNumber, unparsedRecord.getTrailingSpaceBehavior());
        }
        colonPos = line.indexOf(":");
        if (colonPos < 0 || !line.substring(0, colonPos).equalsIgnoreCase("dn")) {
            throw new LDIFException(LDIFMessages.ERR_READ_DN_LINE_DOESNT_START_WITH_DN.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        final int length = line.length();
        String dn;
        if (length == colonPos + 1) {
            dn = "";
        }
        else if (line.charAt(colonPos + 1) == ':') {
            int pos;
            for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
            try {
                final byte[] dnBytes = Base64.decode(line.substring(pos));
                dn = StaticUtils.toUTF8String(dnBytes);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDIFException(LDIFMessages.ERR_READ_CR_CANNOT_BASE64_DECODE_DN.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDIFException(LDIFMessages.ERR_READ_CR_CANNOT_BASE64_DECODE_DN.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
            }
        }
        else {
            int pos;
            for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
            dn = line.substring(pos);
        }
        if (!iterator.hasNext()) {
            throw new LDIFException(LDIFMessages.ERR_READ_CR_TOO_SHORT.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        ArrayList<Control> controls = null;
        while (true) {
            line = iterator.next();
            handleTrailingSpaces(line, dn, firstLineNumber, unparsedRecord.getTrailingSpaceBehavior());
            colonPos = line.indexOf(":");
            if (colonPos < 0) {
                throw new LDIFException(LDIFMessages.ERR_READ_CR_SECOND_LINE_MISSING_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            final String token = StaticUtils.toLowerCase(line.substring(0, colonPos));
            if (token.equals("control")) {
                if (controls == null) {
                    controls = new ArrayList<Control>(5);
                }
                controls.add(decodeControl(line, colonPos, firstLineNumber, ldifLines, relativeBasePath));
                ++linesRead;
            }
            else {
                String changeType;
                if (token.equals("changetype")) {
                    changeType = decodeChangeType(line, colonPos, firstLineNumber, ldifLines);
                }
                else {
                    if (!defaultAdd) {
                        throw new LDIFException(LDIFMessages.ERR_READ_CR_CT_LINE_DOESNT_START_WITH_CONTROL_OR_CT.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                    }
                    changeType = "add";
                    iterator = ldifLines.iterator();
                    for (int i = 0; i < linesRead; ++i) {
                        iterator.next();
                    }
                }
                final String lowerChangeType = StaticUtils.toLowerCase(changeType);
                if (lowerChangeType.equals("add")) {
                    if (iterator.hasNext()) {
                        final Collection<Attribute> attrs = parseAttributes(dn, unparsedRecord.getDuplicateValueBehavior(), unparsedRecord.getTrailingSpaceBehavior(), unparsedRecord.getSchema(), ldifLines, iterator, relativeBasePath, firstLineNumber);
                        final Attribute[] attributes = new Attribute[attrs.size()];
                        final Iterator<Attribute> attrIterator = attrs.iterator();
                        for (int j = 0; j < attributes.length; ++j) {
                            attributes[j] = attrIterator.next();
                        }
                        return new LDIFAddChangeRecord(dn, attributes, controls);
                    }
                    throw new LDIFException(LDIFMessages.ERR_READ_CR_NO_ATTRIBUTES.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
                else if (lowerChangeType.equals("delete")) {
                    if (iterator.hasNext()) {
                        throw new LDIFException(LDIFMessages.ERR_READ_CR_EXTRA_DELETE_DATA.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                    }
                    return new LDIFDeleteChangeRecord(dn, controls);
                }
                else if (lowerChangeType.equals("modify")) {
                    if (iterator.hasNext()) {
                        final Modification[] mods = parseModifications(dn, unparsedRecord.getTrailingSpaceBehavior(), ldifLines, iterator, firstLineNumber, schema);
                        return new LDIFModifyChangeRecord(dn, mods, controls);
                    }
                    throw new LDIFException(LDIFMessages.ERR_READ_CR_NO_MODS.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
                else {
                    if (!lowerChangeType.equals("moddn") && !lowerChangeType.equals("modrdn")) {
                        throw new LDIFException(LDIFMessages.ERR_READ_CR_INVALID_CT.get(changeType, firstLineNumber), firstLineNumber, true, ldifLines, null);
                    }
                    if (iterator.hasNext()) {
                        return parseModifyDNChangeRecord(ldifLines, iterator, dn, controls, unparsedRecord.getTrailingSpaceBehavior(), firstLineNumber);
                    }
                    throw new LDIFException(LDIFMessages.ERR_READ_CR_NO_NEWRDN.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
            }
        }
    }
    
    private static Control decodeControl(final StringBuilder line, final int colonPos, final long firstLineNumber, final ArrayList<StringBuilder> ldifLines, final String relativeBasePath) throws LDIFException {
        int length = line.length();
        if (length == colonPos + 1) {
            throw new LDIFException(LDIFMessages.ERR_READ_CONTROL_LINE_NO_CONTROL_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        String controlString;
        if (line.charAt(colonPos + 1) == ':') {
            int pos;
            for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
            try {
                final byte[] controlBytes = Base64.decode(line.substring(pos));
                controlString = StaticUtils.toUTF8String(controlBytes);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_CONTROL.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_CONTROL.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
            }
        }
        else {
            int pos;
            for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
            controlString = line.substring(pos);
        }
        if (controlString.isEmpty()) {
            throw new LDIFException(LDIFMessages.ERR_READ_CONTROL_LINE_NO_CONTROL_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        String oid = null;
        boolean hasCriticality = false;
        boolean hasValue = false;
        int pos2;
        for (pos2 = 0, length = controlString.length(); pos2 < length; ++pos2) {
            final char c = controlString.charAt(pos2);
            if (c == ':') {
                oid = controlString.substring(0, pos2++);
                hasValue = true;
                break;
            }
            if (c == ' ') {
                oid = controlString.substring(0, pos2++);
                hasCriticality = true;
                break;
            }
        }
        if (oid == null) {
            return new Control(controlString, false);
        }
        boolean isCritical;
        if (hasCriticality) {
            while (controlString.charAt(pos2) == ' ') {
                ++pos2;
            }
            final int criticalityStartPos = pos2;
            while (pos2 < length) {
                final char c2 = controlString.charAt(pos2);
                if (c2 == ':') {
                    hasValue = true;
                    break;
                }
                ++pos2;
            }
            final String criticalityString = StaticUtils.toLowerCase(controlString.substring(criticalityStartPos, pos2));
            if (criticalityString.equals("true")) {
                isCritical = true;
            }
            else {
                if (!criticalityString.equals("false")) {
                    throw new LDIFException(LDIFMessages.ERR_READ_CONTROL_LINE_INVALID_CRITICALITY.get(criticalityString, firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
                isCritical = false;
            }
            if (hasValue) {
                ++pos2;
            }
        }
        else {
            isCritical = false;
        }
        ASN1OctetString value = null;
        if (hasValue) {
            switch (controlString.charAt(pos2)) {
                case ':': {
                    try {
                        if (controlString.length() == pos2 + 1) {
                            value = new ASN1OctetString();
                        }
                        else if (controlString.charAt(pos2 + 1) == ' ') {
                            value = new ASN1OctetString(Base64.decode(controlString.substring(pos2 + 2)));
                        }
                        else {
                            value = new ASN1OctetString(Base64.decode(controlString.substring(pos2 + 1)));
                        }
                        break;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new LDIFException(LDIFMessages.ERR_READ_CONTROL_LINE_CANNOT_BASE64_DECODE_VALUE.get(firstLineNumber, StaticUtils.getExceptionMessage(e2)), firstLineNumber, true, ldifLines, e2);
                    }
                }
                case '<': {
                    try {
                        String urlString;
                        if (controlString.charAt(pos2 + 1) == ' ') {
                            urlString = controlString.substring(pos2 + 2);
                        }
                        else {
                            urlString = controlString.substring(pos2 + 1);
                        }
                        value = new ASN1OctetString(retrieveURLBytes(urlString, relativeBasePath, firstLineNumber));
                        break;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new LDIFException(LDIFMessages.ERR_READ_CONTROL_LINE_CANNOT_RETRIEVE_VALUE_FROM_URL.get(firstLineNumber, StaticUtils.getExceptionMessage(e2)), firstLineNumber, true, ldifLines, e2);
                    }
                }
                case ' ': {
                    value = new ASN1OctetString(controlString.substring(pos2 + 1));
                    break;
                }
                default: {
                    value = new ASN1OctetString(controlString.substring(pos2));
                    break;
                }
            }
        }
        else {
            value = null;
        }
        return new Control(oid, isCritical, value);
    }
    
    private static String decodeChangeType(final StringBuilder line, final int colonPos, final long firstLineNumber, final ArrayList<StringBuilder> ldifLines) throws LDIFException {
        final int length = line.length();
        if (length == colonPos + 1) {
            throw new LDIFException(LDIFMessages.ERR_READ_CT_LINE_NO_CT_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        if (line.charAt(colonPos + 1) == ':') {
            int pos;
            for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
            try {
                final byte[] changeTypeBytes = Base64.decode(line.substring(pos));
                return StaticUtils.toUTF8String(changeTypeBytes);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_CT.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_CT.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
            }
        }
        int pos;
        for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
        return line.substring(pos);
    }
    
    private static ArrayList<Attribute> parseAttributes(final String dn, final DuplicateValueBehavior duplicateValueBehavior, final TrailingSpaceBehavior trailingSpaceBehavior, final Schema schema, final ArrayList<StringBuilder> ldifLines, final Iterator<StringBuilder> iterator, final String relativeBasePath, final long firstLineNumber) throws LDIFException {
        final LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>(StaticUtils.computeMapCapacity(ldifLines.size()));
        while (iterator.hasNext()) {
            final StringBuilder line = iterator.next();
            handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
            final int colonPos = line.indexOf(":");
            if (colonPos <= 0) {
                throw new LDIFException(LDIFMessages.ERR_READ_NO_ATTR_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            final String attributeName = line.substring(0, colonPos);
            final String lowerName = StaticUtils.toLowerCase(attributeName);
            MatchingRule matchingRule;
            if (schema == null) {
                matchingRule = CaseIgnoreStringMatchingRule.getInstance();
            }
            else {
                matchingRule = MatchingRule.selectEqualityMatchingRule(attributeName, schema);
            }
            final Object attrObject = attributes.get(lowerName);
            LDIFAttribute ldifAttr;
            if (attrObject == null) {
                final Attribute attr = null;
                ldifAttr = null;
            }
            else if (attrObject instanceof Attribute) {
                final Attribute attr = (Attribute)attrObject;
                ldifAttr = new LDIFAttribute(attr.getName(), matchingRule, attr.getRawValues()[0]);
                attributes.put(lowerName, ldifAttr);
            }
            else {
                final Attribute attr = null;
                ldifAttr = (LDIFAttribute)attrObject;
            }
            final int length = line.length();
            if (length == colonPos + 1) {
                if (attrObject == null) {
                    final Attribute attr = new Attribute(attributeName, matchingRule, "");
                    attributes.put(lowerName, attr);
                    continue;
                }
                try {
                    if (!ldifAttr.addValue(new ASN1OctetString(), duplicateValueBehavior) && duplicateValueBehavior != DuplicateValueBehavior.STRIP) {
                        throw new LDIFException(LDIFMessages.ERR_READ_DUPLICATE_VALUE.get(dn, firstLineNumber, attributeName), firstLineNumber, true, ldifLines, null);
                    }
                    continue;
                }
                catch (final LDAPException le) {
                    throw new LDIFException(LDIFMessages.ERR_READ_VALUE_SYNTAX_VIOLATION.get(dn, firstLineNumber, attributeName, StaticUtils.getExceptionMessage(le)), firstLineNumber, true, ldifLines, le);
                }
            }
            if (line.charAt(colonPos + 1) == ':') {
                int pos;
                for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
                try {
                    final byte[] valueBytes = Base64.decode(line.substring(pos));
                    if (attrObject == null) {
                        final Attribute attr = new Attribute(attributeName, matchingRule, valueBytes);
                        attributes.put(lowerName, attr);
                    }
                    else {
                        try {
                            if (!ldifAttr.addValue(new ASN1OctetString(valueBytes), duplicateValueBehavior) && duplicateValueBehavior != DuplicateValueBehavior.STRIP) {
                                throw new LDIFException(LDIFMessages.ERR_READ_DUPLICATE_VALUE.get(dn, firstLineNumber, attributeName), firstLineNumber, true, ldifLines, null);
                            }
                            continue;
                        }
                        catch (final LDAPException le2) {
                            throw new LDIFException(LDIFMessages.ERR_READ_VALUE_SYNTAX_VIOLATION.get(dn, firstLineNumber, attributeName, StaticUtils.getExceptionMessage(le2)), firstLineNumber, true, ldifLines, le2);
                        }
                    }
                }
                catch (final ParseException pe) {
                    Debug.debugException(pe);
                    throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_ATTR.get(attributeName, firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
                }
            }
            else if (line.charAt(colonPos + 1) == '<') {
                int pos;
                for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
                final String urlString = line.substring(pos);
                byte[] urlBytes;
                try {
                    urlBytes = retrieveURLBytes(urlString, relativeBasePath, firstLineNumber);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDIFException(LDIFMessages.ERR_READ_URL_EXCEPTION.get(attributeName, urlString, firstLineNumber, e), firstLineNumber, true, ldifLines, e);
                }
                if (attrObject == null) {
                    final Attribute attr = new Attribute(attributeName, matchingRule, urlBytes);
                    attributes.put(lowerName, attr);
                }
                else {
                    try {
                        if (!ldifAttr.addValue(new ASN1OctetString(urlBytes), duplicateValueBehavior) && duplicateValueBehavior != DuplicateValueBehavior.STRIP) {
                            throw new LDIFException(LDIFMessages.ERR_READ_DUPLICATE_VALUE.get(dn, firstLineNumber, attributeName), firstLineNumber, true, ldifLines, null);
                        }
                        continue;
                    }
                    catch (final LDIFException le3) {
                        Debug.debugException(le3);
                        throw le3;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDIFException(LDIFMessages.ERR_READ_URL_EXCEPTION.get(attributeName, urlString, firstLineNumber, e), firstLineNumber, true, ldifLines, e);
                    }
                }
            }
            else {
                int pos;
                for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
                final String valueString = line.substring(pos);
                if (attrObject == null) {
                    final Attribute attr = new Attribute(attributeName, matchingRule, valueString);
                    attributes.put(lowerName, attr);
                }
                else {
                    try {
                        if (!ldifAttr.addValue(new ASN1OctetString(valueString), duplicateValueBehavior) && duplicateValueBehavior != DuplicateValueBehavior.STRIP) {
                            throw new LDIFException(LDIFMessages.ERR_READ_DUPLICATE_VALUE.get(dn, firstLineNumber, attributeName), firstLineNumber, true, ldifLines, null);
                        }
                        continue;
                    }
                    catch (final LDAPException le2) {
                        throw new LDIFException(LDIFMessages.ERR_READ_VALUE_SYNTAX_VIOLATION.get(dn, firstLineNumber, attributeName, StaticUtils.getExceptionMessage(le2)), firstLineNumber, true, ldifLines, le2);
                    }
                }
            }
        }
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(attributes.size());
        for (final Object o : attributes.values()) {
            if (o instanceof Attribute) {
                attrList.add((Attribute)o);
            }
            else {
                attrList.add(((LDIFAttribute)o).toAttribute());
            }
        }
        return attrList;
    }
    
    private static byte[] retrieveURLBytes(final String urlString, final String relativeBasePath, final long firstLineNumber) throws LDIFException, IOException {
        final String lowerURLString = StaticUtils.toLowerCase(urlString);
        String path;
        if (lowerURLString.startsWith("file:/")) {
            int pos;
            for (pos = 6; pos < urlString.length() && urlString.charAt(pos) == '/'; ++pos) {}
            path = urlString.substring(pos - 1);
        }
        else {
            if (!lowerURLString.startsWith("file:")) {
                throw new LDIFException(LDIFMessages.ERR_READ_URL_INVALID_SCHEME.get(urlString), firstLineNumber, true);
            }
            path = relativeBasePath + urlString.substring(5);
        }
        final File f = new File(path);
        if (!f.exists()) {
            throw new LDIFException(LDIFMessages.ERR_READ_URL_NO_SUCH_FILE.get(urlString, f.getAbsolutePath()), firstLineNumber, true);
        }
        final long fileSize = f.length();
        if (fileSize > 10485760L) {
            throw new LDIFException(LDIFMessages.ERR_READ_URL_FILE_TOO_LARGE.get(urlString, f.getAbsolutePath(), 10485760), firstLineNumber, true);
        }
        int fileBytesRemaining = (int)fileSize;
        final byte[] fileData = new byte[(int)fileSize];
        final FileInputStream fis = new FileInputStream(f);
        try {
            int bytesRead;
            for (int fileBytesRead = 0; fileBytesRead < fileSize; fileBytesRead += bytesRead, fileBytesRemaining -= bytesRead) {
                bytesRead = fis.read(fileData, fileBytesRead, fileBytesRemaining);
                if (bytesRead < 0) {
                    throw new LDIFException(LDIFMessages.ERR_READ_URL_FILE_SIZE_CHANGED.get(urlString, f.getAbsolutePath()), firstLineNumber, true);
                }
            }
            if (fis.read() != -1) {
                throw new LDIFException(LDIFMessages.ERR_READ_URL_FILE_SIZE_CHANGED.get(urlString, f.getAbsolutePath()), firstLineNumber, true);
            }
        }
        finally {
            fis.close();
        }
        return fileData;
    }
    
    private static Modification[] parseModifications(final String dn, final TrailingSpaceBehavior trailingSpaceBehavior, final ArrayList<StringBuilder> ldifLines, final Iterator<StringBuilder> iterator, final long firstLineNumber, final Schema schema) throws LDIFException {
        final ArrayList<Modification> modList = new ArrayList<Modification>(ldifLines.size());
        while (iterator.hasNext()) {
            StringBuilder line = iterator.next();
            handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
            int colonPos = line.indexOf(":");
            if (colonPos < 0) {
                throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_NO_MODTYPE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            final String modTypeStr = StaticUtils.toLowerCase(line.substring(0, colonPos));
            ModificationType modType;
            if (modTypeStr.equals("add")) {
                modType = ModificationType.ADD;
            }
            else if (modTypeStr.equals("delete")) {
                modType = ModificationType.DELETE;
            }
            else if (modTypeStr.equals("replace")) {
                modType = ModificationType.REPLACE;
            }
            else {
                if (!modTypeStr.equals("increment")) {
                    throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_INVALID_MODTYPE.get(modTypeStr, firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
                modType = ModificationType.INCREMENT;
            }
            int length = line.length();
            if (length == colonPos + 1) {
                throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_MODTYPE_NO_ATTR.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            String attributeName;
            if (line.charAt(colonPos + 1) == ':') {
                int pos;
                for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
                try {
                    final byte[] dnBytes = Base64.decode(line.substring(pos));
                    attributeName = StaticUtils.toUTF8String(dnBytes);
                }
                catch (final ParseException pe) {
                    Debug.debugException(pe);
                    throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_MODTYPE_CANNOT_BASE64_DECODE_ATTR.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_MODTYPE_CANNOT_BASE64_DECODE_ATTR.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
                }
            }
            else {
                int pos;
                for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
                attributeName = line.substring(pos);
            }
            if (attributeName.isEmpty()) {
                throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_MODTYPE_NO_ATTR.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(ldifLines.size());
            while (iterator.hasNext()) {
                line = iterator.next();
                handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
                if (line.toString().equals("-")) {
                    break;
                }
                colonPos = line.indexOf(":");
                if (colonPos < 0) {
                    throw new LDIFException(LDIFMessages.ERR_READ_NO_ATTR_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
                }
                if (!line.substring(0, colonPos).equalsIgnoreCase(attributeName)) {
                    final String alternateName = line.substring(0, colonPos);
                    boolean baseNameEquivalent = false;
                    final String expectedBaseName = Attribute.getBaseName(attributeName);
                    final String alternateBaseName = Attribute.getBaseName(alternateName);
                    if (alternateBaseName.equalsIgnoreCase(expectedBaseName)) {
                        baseNameEquivalent = true;
                    }
                    else if (schema != null) {
                        final AttributeTypeDefinition expectedAT = schema.getAttributeType(expectedBaseName);
                        final AttributeTypeDefinition alternateAT = schema.getAttributeType(alternateBaseName);
                        if (expectedAT != null && alternateAT != null && expectedAT.equals(alternateAT)) {
                            baseNameEquivalent = true;
                        }
                    }
                    final Set<String> expectedOptions = Attribute.getOptions(attributeName);
                    final Set<String> lowerExpectedOptions = new HashSet<String>(StaticUtils.computeMapCapacity(expectedOptions.size()));
                    for (final String s : expectedOptions) {
                        lowerExpectedOptions.add(StaticUtils.toLowerCase(s));
                    }
                    final Set<String> alternateOptions = Attribute.getOptions(alternateName);
                    final Set<String> lowerAlternateOptions = new HashSet<String>(StaticUtils.computeMapCapacity(alternateOptions.size()));
                    for (final String s2 : alternateOptions) {
                        lowerAlternateOptions.add(StaticUtils.toLowerCase(s2));
                    }
                    final boolean optionsEquivalent = lowerAlternateOptions.equals(lowerExpectedOptions);
                    if (!baseNameEquivalent || !optionsEquivalent) {
                        if (!valueList.isEmpty() || !baseNameEquivalent || !lowerAlternateOptions.remove("binary") || !lowerAlternateOptions.equals(lowerExpectedOptions)) {
                            throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_ATTR_MISMATCH.get(firstLineNumber, line.substring(0, colonPos), attributeName), firstLineNumber, true, ldifLines, null);
                        }
                        attributeName = alternateName;
                    }
                }
                length = line.length();
                ASN1OctetString value;
                if (length == colonPos + 1) {
                    value = new ASN1OctetString();
                }
                else if (line.charAt(colonPos + 1) == ':') {
                    int pos2;
                    for (pos2 = colonPos + 2; pos2 < length && line.charAt(pos2) == ' '; ++pos2) {}
                    try {
                        value = new ASN1OctetString(Base64.decode(line.substring(pos2)));
                    }
                    catch (final ParseException pe2) {
                        Debug.debugException(pe2);
                        throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_ATTR.get(attributeName, firstLineNumber, pe2.getMessage()), firstLineNumber, true, ldifLines, pe2);
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new LDIFException(LDIFMessages.ERR_READ_CANNOT_BASE64_DECODE_ATTR.get(firstLineNumber, e2), firstLineNumber, true, ldifLines, e2);
                    }
                }
                else {
                    int pos2;
                    for (pos2 = colonPos + 1; pos2 < length && line.charAt(pos2) == ' '; ++pos2) {}
                    value = new ASN1OctetString(line.substring(pos2));
                }
                valueList.add(value);
            }
            final ASN1OctetString[] values = new ASN1OctetString[valueList.size()];
            valueList.toArray(values);
            if (modType.intValue() == ModificationType.ADD.intValue() && values.length == 0) {
                throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_NO_ADD_VALUES.get(attributeName, firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            if (modType.intValue() == ModificationType.INCREMENT.intValue() && values.length != 1) {
                throw new LDIFException(LDIFMessages.ERR_READ_MOD_CR_INVALID_INCR_VALUE_COUNT.get(firstLineNumber, attributeName), firstLineNumber, true, ldifLines, null);
            }
            modList.add(new Modification(modType, attributeName, values));
        }
        final Modification[] mods = new Modification[modList.size()];
        modList.toArray(mods);
        return mods;
    }
    
    private static LDIFModifyDNChangeRecord parseModifyDNChangeRecord(final ArrayList<StringBuilder> ldifLines, final Iterator<StringBuilder> iterator, final String dn, final List<Control> controls, final TrailingSpaceBehavior trailingSpaceBehavior, final long firstLineNumber) throws LDIFException {
        StringBuilder line = iterator.next();
        handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
        int colonPos = line.indexOf(":");
        if (colonPos < 0 || !line.substring(0, colonPos).equalsIgnoreCase("newrdn")) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_NEWRDN_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        int length = line.length();
        if (length == colonPos + 1) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_NEWRDN_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        String newRDN;
        if (line.charAt(colonPos + 1) == ':') {
            int pos;
            for (pos = colonPos + 2; pos < length && line.charAt(pos) == ' '; ++pos) {}
            try {
                final byte[] dnBytes = Base64.decode(line.substring(pos));
                newRDN = StaticUtils.toUTF8String(dnBytes);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWRDN.get(firstLineNumber, pe.getMessage()), firstLineNumber, true, ldifLines, pe);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWRDN.get(firstLineNumber, e), firstLineNumber, true, ldifLines, e);
            }
        }
        else {
            int pos;
            for (pos = colonPos + 1; pos < length && line.charAt(pos) == ' '; ++pos) {}
            newRDN = line.substring(pos);
        }
        if (newRDN.isEmpty()) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_NEWRDN_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        if (!iterator.hasNext()) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_DELOLDRDN_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        line = iterator.next();
        handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
        colonPos = line.indexOf(":");
        if (colonPos < 0 || !line.substring(0, colonPos).equalsIgnoreCase("deleteoldrdn")) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_DELOLDRDN_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        length = line.length();
        if (length == colonPos + 1) {
            throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_DELOLDRDN_VALUE.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        String deleteOldRDNStr;
        if (line.charAt(colonPos + 1) == ':') {
            int pos2;
            for (pos2 = colonPos + 2; pos2 < length && line.charAt(pos2) == ' '; ++pos2) {}
            try {
                final byte[] changeTypeBytes = Base64.decode(line.substring(pos2));
                deleteOldRDNStr = StaticUtils.toUTF8String(changeTypeBytes);
            }
            catch (final ParseException pe2) {
                Debug.debugException(pe2);
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_DELOLDRDN.get(firstLineNumber, pe2.getMessage()), firstLineNumber, true, ldifLines, pe2);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_DELOLDRDN.get(firstLineNumber, e2), firstLineNumber, true, ldifLines, e2);
            }
        }
        else {
            int pos2;
            for (pos2 = colonPos + 1; pos2 < length && line.charAt(pos2) == ' '; ++pos2) {}
            deleteOldRDNStr = line.substring(pos2);
        }
        boolean deleteOldRDN;
        if (deleteOldRDNStr.equals("0")) {
            deleteOldRDN = false;
        }
        else if (deleteOldRDNStr.equals("1")) {
            deleteOldRDN = true;
        }
        else if (deleteOldRDNStr.equalsIgnoreCase("false") || deleteOldRDNStr.equalsIgnoreCase("no")) {
            deleteOldRDN = false;
        }
        else {
            if (!deleteOldRDNStr.equalsIgnoreCase("true") && !deleteOldRDNStr.equalsIgnoreCase("yes")) {
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_INVALID_DELOLDRDN.get(deleteOldRDNStr, firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            deleteOldRDN = false;
        }
        String newSuperiorDN;
        if (iterator.hasNext()) {
            line = iterator.next();
            handleTrailingSpaces(line, dn, firstLineNumber, trailingSpaceBehavior);
            colonPos = line.indexOf(":");
            if (colonPos < 0 || !line.substring(0, colonPos).equalsIgnoreCase("newsuperior")) {
                throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_NO_NEWSUPERIOR_COLON.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
            }
            length = line.length();
            if (length == colonPos + 1) {
                newSuperiorDN = "";
            }
            else if (line.charAt(colonPos + 1) == ':') {
                int pos3;
                for (pos3 = colonPos + 2; pos3 < length && line.charAt(pos3) == ' '; ++pos3) {}
                try {
                    final byte[] dnBytes2 = Base64.decode(line.substring(pos3));
                    newSuperiorDN = StaticUtils.toUTF8String(dnBytes2);
                }
                catch (final ParseException pe3) {
                    Debug.debugException(pe3);
                    throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWSUPERIOR.get(firstLineNumber, pe3.getMessage()), firstLineNumber, true, ldifLines, pe3);
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    throw new LDIFException(LDIFMessages.ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWSUPERIOR.get(firstLineNumber, e3), firstLineNumber, true, ldifLines, e3);
                }
            }
            else {
                int pos3;
                for (pos3 = colonPos + 1; pos3 < length && line.charAt(pos3) == ' '; ++pos3) {}
                newSuperiorDN = line.substring(pos3);
            }
        }
        else {
            newSuperiorDN = null;
        }
        if (iterator.hasNext()) {
            throw new LDIFException(LDIFMessages.ERR_READ_CR_EXTRA_MODDN_DATA.get(firstLineNumber), firstLineNumber, true, ldifLines, null);
        }
        return new LDIFModifyDNChangeRecord(dn, newRDN, deleteOldRDN, newSuperiorDN, controls);
    }
    
    private static void handleTrailingSpaces(final StringBuilder buffer, final String dn, final long firstLineNumber, final TrailingSpaceBehavior trailingSpaceBehavior) throws LDIFException {
        int pos = buffer.length() - 1;
        boolean trailingFound = false;
        while (pos >= 0 && buffer.charAt(pos) == ' ') {
            trailingFound = true;
            --pos;
        }
        if (trailingFound && buffer.charAt(pos) != ':') {
            switch (trailingSpaceBehavior) {
                case STRIP: {
                    buffer.setLength(pos + 1);
                    break;
                }
                case REJECT: {
                    if (dn == null) {
                        throw new LDIFException(LDIFMessages.ERR_READ_ILLEGAL_TRAILING_SPACE_WITHOUT_DN.get(firstLineNumber, buffer.toString()), firstLineNumber, true);
                    }
                    throw new LDIFException(LDIFMessages.ERR_READ_ILLEGAL_TRAILING_SPACE_WITH_DN.get(dn, firstLineNumber, buffer.toString()), firstLineNumber, true);
                }
            }
        }
    }
    
    static {
        SKIP_ENTRY = new Entry("cn=skipped");
        final String currentDirString = StaticUtils.getSystemProperty("user.dir");
        File currentDir;
        if (currentDirString == null) {
            currentDir = new File(".");
        }
        else {
            currentDir = new File(currentDirString);
        }
        final String currentDirAbsolutePath = currentDir.getAbsolutePath();
        if (currentDirAbsolutePath.endsWith(File.separator)) {
            DEFAULT_RELATIVE_BASE_PATH = currentDirAbsolutePath;
        }
        else {
            DEFAULT_RELATIVE_BASE_PATH = currentDirAbsolutePath + File.separator;
        }
    }
    
    private static final class UnparsedLDIFRecord
    {
        private final ArrayList<StringBuilder> lineList;
        private final long firstLineNumber;
        private final Exception failureCause;
        private final boolean isEOF;
        private final DuplicateValueBehavior duplicateValueBehavior;
        private final Schema schema;
        private final TrailingSpaceBehavior trailingSpaceBehavior;
        
        private UnparsedLDIFRecord(final ArrayList<StringBuilder> lineList, final DuplicateValueBehavior duplicateValueBehavior, final TrailingSpaceBehavior trailingSpaceBehavior, final Schema schema, final long firstLineNumber) {
            this.lineList = lineList;
            this.firstLineNumber = firstLineNumber;
            this.duplicateValueBehavior = duplicateValueBehavior;
            this.trailingSpaceBehavior = trailingSpaceBehavior;
            this.schema = schema;
            this.failureCause = null;
            this.isEOF = (firstLineNumber < 0L || (lineList != null && lineList.isEmpty()));
        }
        
        private UnparsedLDIFRecord(final Exception failureCause) {
            this.failureCause = failureCause;
            this.lineList = null;
            this.firstLineNumber = 0L;
            this.duplicateValueBehavior = DuplicateValueBehavior.REJECT;
            this.trailingSpaceBehavior = TrailingSpaceBehavior.REJECT;
            this.schema = null;
            this.isEOF = false;
        }
        
        private ArrayList<StringBuilder> getLineList() {
            return this.lineList;
        }
        
        private DuplicateValueBehavior getDuplicateValueBehavior() {
            return this.duplicateValueBehavior;
        }
        
        private TrailingSpaceBehavior getTrailingSpaceBehavior() {
            return this.trailingSpaceBehavior;
        }
        
        private Schema getSchema() {
            return this.schema;
        }
        
        private long getFirstLineNumber() {
            return this.firstLineNumber;
        }
        
        private boolean isEOF() {
            return this.isEOF;
        }
        
        private Exception getFailureCause() {
            return this.failureCause;
        }
    }
    
    private final class LineReaderThread extends Thread
    {
        private LineReaderThread() {
            super("Asynchronous LDIF line reader");
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            try {
                for (boolean stopProcessing = false; !stopProcessing; stopProcessing = true) {
                    UnparsedLDIFRecord unparsedRecord;
                    try {
                        unparsedRecord = LDIFReader.this.readUnparsedRecord();
                    }
                    catch (final IOException e) {
                        Debug.debugException(e);
                        unparsedRecord = new UnparsedLDIFRecord((Exception)e);
                        stopProcessing = true;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        unparsedRecord = new UnparsedLDIFRecord(e2);
                    }
                    try {
                        LDIFReader.this.asyncParser.submit(unparsedRecord);
                    }
                    catch (final InterruptedException e3) {
                        Debug.debugException(e3);
                        Thread.currentThread().interrupt();
                        stopProcessing = true;
                    }
                    if (unparsedRecord == null || unparsedRecord.isEOF()) {}
                }
            }
            finally {
                try {
                    LDIFReader.this.asyncParser.shutdown();
                }
                catch (final InterruptedException e4) {
                    Debug.debugException(e4);
                    Thread.currentThread().interrupt();
                    LDIFReader.this.asyncParsingComplete.set(true);
                }
                finally {
                    LDIFReader.this.asyncParsingComplete.set(true);
                }
            }
        }
    }
    
    private final class RecordParser implements Processor<UnparsedLDIFRecord, LDIFRecord>
    {
        @Override
        public LDIFRecord process(final UnparsedLDIFRecord input) throws LDIFException {
            LDIFRecord record = decodeRecord(input, LDIFReader.this.relativeBasePath, LDIFReader.this.schema);
            if (record instanceof Entry && LDIFReader.this.entryTranslator != null) {
                record = LDIFReader.this.entryTranslator.translate((Entry)record, input.getFirstLineNumber());
                if (record == null) {
                    record = LDIFReader.SKIP_ENTRY;
                }
            }
            if (record instanceof LDIFChangeRecord && LDIFReader.this.changeRecordTranslator != null) {
                record = LDIFReader.this.changeRecordTranslator.translate((LDIFChangeRecord)record, input.getFirstLineNumber());
                if (record == null) {
                    record = LDIFReader.SKIP_ENTRY;
                }
            }
            return record;
        }
    }
}
